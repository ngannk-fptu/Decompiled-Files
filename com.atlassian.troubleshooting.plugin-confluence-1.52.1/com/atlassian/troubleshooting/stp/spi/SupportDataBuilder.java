/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.troubleshooting.stp.spi.SupportDataBuilderContext;

public interface SupportDataBuilder {
    public SupportDataBuilderContext getBuilderContext();

    public SupportDataBuilder addValue(String var1, String var2);

    public SupportDataBuilder addCategory(String var1);

    public <T> SupportDataBuilder addContext(T var1);
}

