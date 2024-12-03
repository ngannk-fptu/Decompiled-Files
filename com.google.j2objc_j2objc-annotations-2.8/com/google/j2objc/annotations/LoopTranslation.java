/*
 * Decompiled with CFR 0.152.
 */
package com.google.j2objc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.LOCAL_VARIABLE})
@Retention(value=RetentionPolicy.SOURCE)
public @interface LoopTranslation {
    public LoopStyle value();

    public static enum LoopStyle {
        JAVA_ITERATOR,
        FAST_ENUMERATION;

    }
}

