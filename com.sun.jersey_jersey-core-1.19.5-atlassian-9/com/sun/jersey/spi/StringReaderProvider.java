/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi;

import com.sun.jersey.spi.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface StringReaderProvider<T> {
    public StringReader<T> getStringReader(Class<?> var1, Type var2, Annotation[] var3);
}

