/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics.jmx;

import javax.management.ObjectName;

public interface ObjectNameFactory {
    public ObjectName createName(String var1, String var2, String var3);
}

