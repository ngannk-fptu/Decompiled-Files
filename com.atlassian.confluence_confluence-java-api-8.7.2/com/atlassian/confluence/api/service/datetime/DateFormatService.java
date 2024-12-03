/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.joda.time.LocalDateTime
 */
package com.atlassian.confluence.api.service.datetime;

import com.atlassian.annotations.ExperimentalApi;
import java.time.LocalDate;
import org.joda.time.LocalDateTime;

@ExperimentalApi
public interface DateFormatService {
    public String getFormattedDateByUserLocale(LocalDate var1);

    @Deprecated
    public String getFormattedDateByUserLocale(LocalDateTime var1);

    public String getDateFormatPatternForUser();
}

