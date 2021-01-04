/*
 * Copyright (c) 2017-2018 m2049r et al.
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

package com.m2049r.xmrwallet.service.exchange.coingecko;

import com.m2049r.xmrwallet.service.exchange.api.ExchangeException;
import com.m2049r.xmrwallet.service.exchange.api.BaseExchangeRate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.NoSuchElementException;

import androidx.annotation.NonNull;

class ExchangeRateImpl extends BaseExchangeRate {


    @Override
    public String getServiceName() {
        return "coingecko.com";
    }

    ExchangeRateImpl(@NonNull final String baseCurrency, @NonNull final String quoteCurrency, double rate) {
        super(baseCurrency, quoteCurrency, rate);
    }

    ExchangeRateImpl(@NonNull final String baseCurrency, @NonNull final String quoteCurrency, double rate, final boolean inverse) {
        double price = inverse ? (1d / rate) : rate;
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.rate = price;
    }

    ExchangeRateImpl(final JSONObject jsonObject, final boolean swapAssets) throws JSONException, ExchangeException {
        try {
            final String baseC = jsonObject.getString("symbol");
            final JSONObject quotes = jsonObject.getJSONObject("quotes");
            final Iterator<String> keys = quotes.keys();
            String key = null;
            // get key which is not USD unless it is the only one
            while (keys.hasNext()) {
                key = keys.next();
                if (!key.equals("USD")) break;
            }
            final String quoteC = key;
            baseCurrency = swapAssets ? quoteC : baseC;
            quoteCurrency = swapAssets ? baseC : quoteC;
            JSONObject quote = quotes.getJSONObject(key);
            double price = quote.getDouble("price");
            this.rate = swapAssets ? (1d / price) : price;
        } catch (NoSuchElementException ex) {
            throw new ExchangeException(ex.getLocalizedMessage());
        }
    }
}
