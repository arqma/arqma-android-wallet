/*
 * Copyright (c) 2017 m2049r
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

// all ranges go back 5 days

public class RestoreHeightTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void pre2014() {
        assertTrue(getHeight("2013-12-01") == 1);
        assertTrue(getHeight("1958-12-01") == 1);
    }

    @Test
    public void zero() {
        assertTrue(getHeight("2014-04-27") == 1);
    }

    @Test
    public void notZero() {
        assertTrue(getHeight("2018-06-05") > 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notDateA() {
        getHeight("2013-13-04");
    }

    @Test(expected = IllegalArgumentException.class)
    public void notDateB() {
        getHeight("2013-13-01-");
    }

    @Test(expected = IllegalArgumentException.class)
    public void notDateC() {
        getHeight("x013-13-01");
    }

    @Test(expected = IllegalArgumentException.class)
    public void notDateD() {
        getHeight("2013-12-41");
    }

    @Test
    public void post201802() {
        assertTrue(isInRange(getHeight("2018-02-19"), 1, 21165));
    }

    @Test
    public void postFuture() {
        long b_20180605 = 21165;
        long b_20180905 = b_20180605 + 720 * (30 + 31 + 31);
        assertTrue(isInRange(getHeight("2018-09-05"), b_20180905 - 720 * 5, b_20180905));
    }


    private boolean isInRange(long n, long min, long max) {
        if (n > max) return false;
        if (n < min) return false;
        return true;
    }

    private long getHeight(String date) {
        return RestoreHeight.getInstance().getHeight(date);
    }
}
