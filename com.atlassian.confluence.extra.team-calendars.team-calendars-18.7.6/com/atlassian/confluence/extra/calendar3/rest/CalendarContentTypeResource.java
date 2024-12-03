/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.RequireSecurityToken
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.model.rest.GeneralResponseEntity;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.CalendarContentTypeMigrationManager;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.RequireSecurityToken;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="calendarcontenttype")
@ReadOnlyAccessAllowed
@WebSudoRequired
@Consumes(value={"application/json"})
@InterceptorChain(value={TransactionInterceptor.class})
public class CalendarContentTypeResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarContentTypeResource.class);
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private CalendarContentTypeMigrationManager calendarContentTypeMigrationManager;
    private ActiveObjectsServiceWrapper activeObjectsWrapper;

    public CalendarContentTypeResource(CalendarContentTypeMigrationManager calendarContentTypeMigrationManager, ActiveObjectsServiceWrapper activeObjectsWrapper) {
        this.calendarContentTypeMigrationManager = calendarContentTypeMigrationManager;
        this.activeObjectsWrapper = activeObjectsWrapper;
    }

    @Path(value="reindex")
    @POST
    @Produces(value={"application/json"})
    @RequireSecurityToken(value=true)
    public Response forceData() {
        GeneralResponseEntity responseEntity = new GeneralResponseEntity();
        try {
            boolean result = this.calendarContentTypeMigrationManager.deleteAllCalendarContentTypes() && this.calendarContentTypeMigrationManager.doMigrate(this.activeObjectsWrapper);
            responseEntity.setSuccess(result);
        }
        catch (Exception ex) {
            responseEntity.setSuccess(false);
            LOGGER.error("Exception happen when Reindex Calendar CustomContentTypes manually", (Throwable)ex);
        }
        return Response.status((Response.Status)Response.Status.OK).header(CONTENT_TYPE_HEADER, (Object)"application/json").entity((Object)responseEntity.toJson().toString()).build();
    }
}

