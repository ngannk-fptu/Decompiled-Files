/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.support;

import java.security.AccessControlContext;
import java.security.AccessController;
import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.lang.Nullable;

public class SimpleSecurityContextProvider
implements SecurityContextProvider {
    @Nullable
    private final AccessControlContext acc;

    public SimpleSecurityContextProvider() {
        this(null);
    }

    public SimpleSecurityContextProvider(@Nullable AccessControlContext acc) {
        this.acc = acc;
    }

    @Override
    public AccessControlContext getAccessControlContext() {
        return this.acc != null ? this.acc : AccessController.getContext();
    }
}

