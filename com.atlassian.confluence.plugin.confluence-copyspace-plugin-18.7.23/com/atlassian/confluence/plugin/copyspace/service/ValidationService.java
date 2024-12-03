/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;

public interface ValidationService {
    public void validate(CopySpaceRequest var1) throws ServiceException;
}

