/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.host.dao.ao;

import java.util.Calendar;
import java.util.Date;

public final class DateUtil {
    public static Date getNoDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -days);
        Date weekAgo = calendar.getTime();
        return weekAgo;
    }
}

