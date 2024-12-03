/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi;

import com.sun.jersey.spi.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface StringReaderWorkers {
    public <T> StringReader<T> getStringReader(Class<T> var1, Type var2, Annotation[] var3);
}

