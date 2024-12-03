/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 */
package com.atlassian.confluence.api.service.accessmode;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import java.util.concurrent.Callable;

@ExperimentalApi
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface AccessModeService {
    public AccessMode getAccessMode();

    public void updateAccessMode(AccessMode var1) throws ServiceException;

    public boolean isReadOnlyAccessModeEnabled();

    public boolean shouldEnforceReadOnlyAccess();

    public <T> T withReadOnlyAccessExemption(Callable<T> var1) throws ServiceException;
}

