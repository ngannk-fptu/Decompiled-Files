/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api.auth;

import com.atlassian.applinks.api.auth.AuthenticationProvider;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface DependsOn {
    public Class<? extends AuthenticationProvider>[] value();
}

