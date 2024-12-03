/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components.date;

import java.time.temporal.TemporalAccessor;

public interface DateFormatter {
    public String format(TemporalAccessor var1, String var2);
}

