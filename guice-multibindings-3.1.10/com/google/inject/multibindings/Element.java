/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.BindingAnnotation
 */
package com.google.inject.multibindings;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
@BindingAnnotation
@interface Element {
    public String setName();

    public int uniqueId();

    public Type type();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Type {
        MAPBINDER,
        MULTIBINDER;

    }
}

