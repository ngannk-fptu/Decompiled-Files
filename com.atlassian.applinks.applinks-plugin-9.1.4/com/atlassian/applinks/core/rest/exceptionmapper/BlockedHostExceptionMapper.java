/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.applinks.core.rest.exceptionmapper;

import com.atlassian.applinks.core.rest.util.BlockedHostException;
import com.atlassian.applinks.core.rest.util.RestUtil;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BlockedHostExceptionMapper
implements ExceptionMapper<BlockedHostException> {
    public Response toResponse(BlockedHostException exception) {
        return RestUtil.forbidden(exception.getMessage());
    }
}

