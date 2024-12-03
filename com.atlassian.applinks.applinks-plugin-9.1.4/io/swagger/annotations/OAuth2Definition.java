/*
 * Decompiled with CFR 0.152.
 */
package io.swagger.annotations;

import io.swagger.annotations.Scope;

public @interface OAuth2Definition {
    public String key();

    public String description() default "";

    public Flow flow();

    public String authorizationUrl() default "";

    public String tokenUrl() default "";

    public Scope[] scopes() default {};

    public static enum Flow {
        IMPLICIT,
        ACCESS_CODE,
        PASSWORD,
        APPLICATION;

    }
}

