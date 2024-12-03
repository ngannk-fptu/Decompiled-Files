/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
interface ObjectConstructor {
    public <T> T construct(Type var1);

    public Object constructArray(Type var1, int var2);
}

