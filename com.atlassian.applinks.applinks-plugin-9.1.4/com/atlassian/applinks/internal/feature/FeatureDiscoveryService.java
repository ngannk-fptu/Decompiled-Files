/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.feature;

import com.atlassian.applinks.internal.common.exception.InvalidFeatureKeyException;
import com.atlassian.applinks.internal.common.exception.NotAuthenticatedException;
import com.atlassian.applinks.internal.common.permission.PermissionLevel;
import com.atlassian.applinks.internal.common.permission.Restricted;
import java.util.Set;
import javax.annotation.Nonnull;

@Restricted(value={PermissionLevel.USER})
public interface FeatureDiscoveryService {
    public boolean isDiscovered(@Nonnull String var1) throws NotAuthenticatedException, InvalidFeatureKeyException;

    public Set<String> getAllDiscoveredFeatureKeys() throws NotAuthenticatedException;

    public void discover(@Nonnull String var1) throws NotAuthenticatedException, InvalidFeatureKeyException;
}

