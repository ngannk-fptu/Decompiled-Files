/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;

public interface SupportDataAppender<T> {
    public void addSupportData(SupportDataBuilder var1, T var2);
}

