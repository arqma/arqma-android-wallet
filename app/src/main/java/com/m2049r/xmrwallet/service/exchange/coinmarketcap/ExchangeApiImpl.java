/*
 * Copyright (c) 2017 m2049r et al.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.m2049r.xmrwallet.service.exchange.coinmarketcap;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeApi;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeCallback;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeException;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeRate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExchangeApiImpl implements ExchangeApi {

    private static final String ENDPOINT_BASE_PART = "https://api.coinmarketcap.com/v2/ticker/";
    private static final String RESPONSE_DATA_KEY = "data";
    private static final String REQUEST_CONVERT_PARAM = "convert";
    private static final String REQUEST_METADATA_PARAM = "metadata";
    private static final String REQUEST_METADATA_ERROR_PARAM = "error";

    private static final int COINMARKET_ARQ_ID = 0;

    @NonNull
    private final OkHttpClient okHttpClient;

    private final HttpUrl baseUrl;

    //so we can inject the mockserver url
    @VisibleForTesting
    ExchangeApiImpl(@NonNull final OkHttpClient okHttpClient, final HttpUrl baseUrl) {

        this.okHttpClient = okHttpClient;
        this.baseUrl = baseUrl;
    }

    public ExchangeApiImpl(@NonNull final OkHttpClient okHttpClient) {
        this(okHttpClient, HttpUrl.parse(ENDPOINT_BASE_PART + COINMARKET_ARQ_ID));
    }

    @Override
    public void queryExchangeRate(@NonNull final String baseCurrency, @NonNull final String quoteCurrency,
                                  @NonNull final ExchangeCallback callback) {

        if (baseCurrency.equals(quoteCurrency)) {
            callback.onSuccess(new ExchangeRateImpl(baseCurrency, quoteCurrency, 1.0));
            return;
        }

        boolean inverse = false;
        String fiat = null;

        if (baseCurrency.equals(Wallet.ARQ_SYMBOL)) {
            fiat = quoteCurrency;
            inverse = false;
        }

        if (quoteCurrency.equals(Wallet.ARQ_SYMBOL)) {
            fiat = baseCurrency;
            inverse = true;
        }

        if (fiat == null) {
            callback.onError(new IllegalArgumentException("no fiat specified"));
            return;
        }

        final boolean swapAssets = inverse;

        final HttpUrl url = baseUrl.newBuilder()
                .addQueryParameter(REQUEST_CONVERT_PARAM, fiat)
                .build();

        final Request httpRequest = createHttpRequest(url);

        okHttpClient.newCall(httpRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(final Call call, final IOException ex) {
                callback.onError(ex);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError(new ExchangeException(response.code(), response.message()));
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    final JSONObject metadata = json.getJSONObject(REQUEST_METADATA_PARAM);
                    if (!metadata.isNull(REQUEST_METADATA_ERROR_PARAM)) {
                        callback.onError(new ExchangeException(response.code(),
                                metadata.getString(REQUEST_METADATA_ERROR_PARAM)));
                    } else {
                        callback.onSuccess(new ExchangeRateImpl(json.getJSONObject(RESPONSE_DATA_KEY), swapAssets));
                    }
                } catch (JSONException ex) {
                    callback.onError(new ExchangeException(ex.getLocalizedMessage()));
                } catch (ExchangeException ex) {
                    callback.onError(ex);
                }
            }
        });
    }

    private Request createHttpRequest(final HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }
}
