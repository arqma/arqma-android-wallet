/*
 * Copyright (c) 2018 m2049r
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

package com.m2049r.xmrwallet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

public class RestoreHeight {
    static private RestoreHeight Singleton = null;

    static public RestoreHeight getInstance() {
        if (Singleton == null) {
            synchronized (RestoreHeight.class) {
                if (Singleton == null) {
                    Singleton = new RestoreHeight();
                }
            }
        }
        return Singleton;
    }

    private Map<String, Long> blockheight = new HashMap<>();

    RestoreHeight() {
      blockheight.put("2018-07", 6000L);
      blockheight.put("2018-08", 17000L);
      blockheight.put("2018-09", 28000L);
      blockheight.put("2018-10", 39000L);
      blockheight.put("2018-11", 49000L);
      blockheight.put("2018-12", 60000L);
      blockheight.put("2019-02", 109000L);
      blockheight.put("2019-03", 130000L);
      blockheight.put("2019-04", 145000L);
      blockheight.put("2019-05", 170000L);
    }

    long latestHeight = 170000L;

    public long getHeight(final Date date) {
        Timber.d("Restore Height date %s", date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Timber.d("Restore Height cal %s", cal);

        if (cal.get(Calendar.YEAR) < 2018)
            return 0;
        if ((cal.get(Calendar.YEAR) == 2018) && ((cal.get(Calendar.MONTH) + 1) <= 9))
            // before September 2014
            return 0;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");

        String queryDate = formatter.format(date);
        Timber.d("String query date %s", queryDate);

        long height = 0;
        if (blockheight.get(queryDate) == null) {
            height = latestHeight;
        } else {
            height = blockheight.get(queryDate);
        }

        return height;
    }

}
