/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.delegate.SharedAccessInterceptor;
import com.atlassian.confluence.security.delegate.TargetToLatestVersionDecorator;
import java.util.function.UnaryOperator;

final class PermissionDelegateDecorators {
    PermissionDelegateDecorators() {
    }

    public static <T> UnaryOperator<PermissionDelegate<T>> sharedAccessCheck() {
        return delegate -> new SharedAccessInterceptor(new TargetToLatestVersionDecorator((PermissionDelegate)delegate));
    }

    public static <T> UnaryOperator<PermissionDelegate<T>> none() {
        return delegate -> delegate;
    }
}

