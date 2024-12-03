/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.security;

import javax.jcr.security.AccessControlPolicy;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface JackrabbitAccessControlPolicy
extends AccessControlPolicy {
    @Nullable
    public String getPath();
}

