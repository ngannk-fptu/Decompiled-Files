/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof;

import java.lang.reflect.Field;

public interface Filter {
    public void ignoreInstancesOf(Class var1, boolean var2);

    public void ignoreField(Field var1);
}

