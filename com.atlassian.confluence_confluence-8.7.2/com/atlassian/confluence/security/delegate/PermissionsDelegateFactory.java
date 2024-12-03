/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.delegate.SharedAccessInterceptor;
import com.atlassian.confluence.security.delegate.TargetToLatestVersionDecorator;

@Deprecated
public class PermissionsDelegateFactory {
    public PermissionDelegate getDelegate(PermissionDelegate delegate) {
        return new SharedAccessInterceptor(new TargetToLatestVersionDecorator(delegate));
    }
}

