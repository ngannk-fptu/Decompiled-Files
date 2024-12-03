/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import java.security.AccessControlContext;

public interface SecurityContextProvider {
    public AccessControlContext getAccessControlContext();
}

