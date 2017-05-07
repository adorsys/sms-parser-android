package de.adorsys.android.smsparser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Drilon Reçica
 */

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
