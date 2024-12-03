/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.capabilities;

import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import com.atlassian.applinks.internal.common.permission.Unrestricted;
import java.util.Set;
import javax.annotation.Nonnull;

@Unrestricted(value="No sensitive information exposed. Capabilities need to be anonymously accessible by linked applications")
public interface ApplinksCapabilitiesService {
    @Nonnull
    public Set<ApplinksCapabilities> getCapabilities();
}

