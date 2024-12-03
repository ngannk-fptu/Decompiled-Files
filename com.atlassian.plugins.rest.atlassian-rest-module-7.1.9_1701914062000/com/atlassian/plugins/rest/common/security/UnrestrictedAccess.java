/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface UnrestrictedAccess {
}

