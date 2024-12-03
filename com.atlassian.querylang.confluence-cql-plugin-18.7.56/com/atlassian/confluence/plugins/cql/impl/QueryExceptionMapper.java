/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.querylang.exceptions.QueryException
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.querylang.exceptions.QueryException;

public class QueryExceptionMapper {
    public static ServiceException mapToServiceException(QueryException e) {
        return new BadRequestException(e.getMessage(), (Throwable)e);
    }
}

