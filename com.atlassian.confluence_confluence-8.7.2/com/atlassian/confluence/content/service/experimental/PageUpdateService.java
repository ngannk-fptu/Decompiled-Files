/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content.service.experimental;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.service.experimental.PreparedAbstractPage;
import com.atlassian.confluence.core.Modification;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.AbstractPage;
import org.checkerframework.checker.nullness.qual.Nullable;

@ExperimentalApi
public interface PageUpdateService {
    public PreparedAbstractPage prepare(long var1, Modification<AbstractPage> var3, @Nullable SaveContext var4) throws ServiceException;

    public void update(PreparedAbstractPage var1) throws ServiceException;
}

