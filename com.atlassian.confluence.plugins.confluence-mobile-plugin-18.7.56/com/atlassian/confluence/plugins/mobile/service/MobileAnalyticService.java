/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.plugins.mobile.dto.MobileAnalyticEventDto;
import java.util.List;
import javax.annotation.Nonnull;

public interface MobileAnalyticService {
    public void publish(@Nonnull List<MobileAnalyticEventDto> var1);
}

