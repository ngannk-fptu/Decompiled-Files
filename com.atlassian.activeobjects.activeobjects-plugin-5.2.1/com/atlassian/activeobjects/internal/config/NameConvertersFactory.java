/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.internal.config;

import com.atlassian.activeobjects.internal.Prefix;
import net.java.ao.schema.NameConverters;

public interface NameConvertersFactory {
    public NameConverters getNameConverters(Prefix var1);
}

