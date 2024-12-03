/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.websudo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PACKAGE, ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface WebSudoNotRequired {
}

