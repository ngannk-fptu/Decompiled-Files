/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 */
package com.atlassian.confluence.compat.api.service.accessmode;

import com.atlassian.confluence.api.service.exceptions.ServiceException;
import java.util.concurrent.Callable;

public interface AccessModeCompatService {
    public boolean isReadOnlyAccessModeEnabled();

    public <T> T withReadOnlyAccessExemption(Callable<T> var1) throws ServiceException;
}

