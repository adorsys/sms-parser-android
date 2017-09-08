/*
 * Copyright (C) 2017 adorsys GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.adorsys.android.smsparser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public enum SmsConfig {
    INSTANCE;

    private String beginIndex;
    private String endIndex;
    private String[] smsSenderNumbers;

    public void initializeSmsConfig(@Nullable String beginIndex, @Nullable String endIndex, @NonNull String... smsSenderNumbers) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.smsSenderNumbers = smsSenderNumbers;
    }

    @Nullable
    public String getBeginIndex() {
        return beginIndex;
    }

    @Nullable
    public String getEndIndex() {
        return endIndex;
    }

    @Nullable
    public String[] getSmsSenderNumbers() {
        return smsSenderNumbers;
    }
}