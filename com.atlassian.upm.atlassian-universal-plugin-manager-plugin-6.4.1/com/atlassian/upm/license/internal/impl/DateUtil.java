/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.upm.api.util.Option;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.joda.time.DateTime;

public class DateUtil {
    private DateUtil() {
    }

    public static ZonedDateTime toZonedDate(DateTime dt) {
        if (dt == null) {
            return null;
        }
        return dt.toGregorianCalendar().toZonedDateTime();
    }

    public static Optional<ZonedDateTime> toOptionalZonedDate(Option<DateTime> jodaDate) {
        return jodaDate.fold(Optional::empty, d -> Optional.of(DateUtil.toZonedDate(d)));
    }
}

