/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTimeZone
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.util.RecurrenceIdJodaTimeHelper;
import java.util.Objects;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecurrenceIdPredicate
implements Predicate<String> {
    private static final Logger LOG = LoggerFactory.getLogger(RecurrenceIdPredicate.class);
    private final String sourceRecurrenceIdStr;

    public RecurrenceIdPredicate(String sourceRecurrenceIdStr) {
        Objects.requireNonNull(sourceRecurrenceIdStr);
        this.sourceRecurrenceIdStr = sourceRecurrenceIdStr;
    }

    @Override
    public boolean test(String targetRecurrenceIdStr) {
        if (StringUtils.isEmpty((CharSequence)targetRecurrenceIdStr)) {
            return false;
        }
        try {
            return RecurrenceIdJodaTimeHelper.compareRecurrenceIds(this.sourceRecurrenceIdStr.substring(0, 8), targetRecurrenceIdStr.substring(0, 8), DateTimeZone.UTC) == 0;
        }
        catch (Exception e) {
            LOG.error("Could not parse recurrence str to compare. Source is [{}] and target is [{}]", (Object)this.sourceRecurrenceIdStr, (Object)targetRecurrenceIdStr);
            return false;
        }
    }
}

