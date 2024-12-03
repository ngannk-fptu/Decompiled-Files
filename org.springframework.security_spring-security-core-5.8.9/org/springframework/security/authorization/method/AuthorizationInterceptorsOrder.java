/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authorization.method;

public enum AuthorizationInterceptorsOrder {
    FIRST(Integer.MIN_VALUE),
    PRE_FILTER,
    PRE_AUTHORIZE,
    SECURED,
    JSR250,
    POST_AUTHORIZE,
    POST_FILTER,
    LAST(Integer.MAX_VALUE);

    private static final int INTERVAL = 100;
    private final int order;

    private AuthorizationInterceptorsOrder() {
        this.order = this.ordinal() * 100;
    }

    private AuthorizationInterceptorsOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }
}

