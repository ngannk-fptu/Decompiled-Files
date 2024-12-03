/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.plugins.mobile.dto.LocationDto;
import com.atlassian.confluence.plugins.mobile.model.Context;
import javax.annotation.Nonnull;

public interface LocationService {
    @Nonnull
    public LocationDto getPageCreateLocation(@Nonnull Context var1);
}

