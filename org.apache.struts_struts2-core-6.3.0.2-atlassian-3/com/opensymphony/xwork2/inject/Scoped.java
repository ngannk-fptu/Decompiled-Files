/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.Scope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Scoped {
    public Scope value();
}

