/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.google.common.collect.Lists
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.model.rest.GeneralResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.MigrationResourceEntity;
import com.atlassian.confluence.extra.calendar3.upgrade.task.CalendarContentTypePermissionSyncUpgradeTask;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaToActiveObjectMigrationManager;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.CalendarContentTypeMigrationManager;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.CalendarsToSpaceMigrator;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.RequireSecurityToken;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="migration")
@ReadOnlyAccessAllowed
@WebSudoRequired
@Consumes(value={"application/json"})
@InterceptorChain(value={TransactionInterceptor.class})
public class MigrationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationResource.class);
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private final BandanaToActiveObjectMigrationManager bandanaToActiveObjectMigrationManager;
    private final CalendarContentTypeMigrationManager customContentTypeTCMigrationManager;
    private ActiveObjectsServiceWrapper activeObjectsWrapper;
    private PermissionManager permissionManager;
    private final CalendarContentTypePermissionSyncUpgradeTask permissionSyncUpgradeTask;
    private final List<Function<ActiveObjectsServiceWrapper, Boolean>> migrationCommamnds;
    List<CalendarsToSpaceMigrator> calendarsToSpaceMigrators;

    public MigrationResource(BandanaToActiveObjectMigrationManager bandanaToActiveObjectMigrationManager, ActiveObjectsServiceWrapper activeObjectsWrapper, PermissionManager permissionManager, CalendarContentTypeMigrationManager customContentTypeTCMigrationManager, List<CalendarsToSpaceMigrator> calendarsToSpaceMigrators, @Qualifier(value="calendarContentTypePermissionSyncUpgradeTask") CalendarContentTypePermissionSyncUpgradeTask permissionSyncUpgradeTask) {
        this.bandanaToActiveObjectMigrationManager = bandanaToActiveObjectMigrationManager;
        this.activeObjectsWrapper = activeObjectsWrapper;
        this.permissionManager = permissionManager;
        this.customContentTypeTCMigrationManager = customContentTypeTCMigrationManager;
        this.calendarsToSpaceMigrators = calendarsToSpaceMigrators;
        this.permissionSyncUpgradeTask = permissionSyncUpgradeTask;
        this.migrationCommamnds = this.buildMigrationCommands();
    }

    private List<Function<ActiveObjectsServiceWrapper, Boolean>> buildMigrationCommands() {
        ArrayList migrationCommands = Lists.newArrayList();
        migrationCommands.add(this.createSafeCommand(activeObjectsServiceWrapper -> this.bandanaToActiveObjectMigrationManager.doMigrate(this.activeObjectsWrapper, true)));
        migrationCommands.add(this.createSafeCommand(activeObjectsServiceWrapper -> this.customContentTypeTCMigrationManager.doMigrate(this.activeObjectsWrapper)));
        migrationCommands.add(this.createSafeCommand(activeObjectsServiceWrapper -> {
            this.permissionSyncUpgradeTask.upgrade(null, this.activeObjectsWrapper.getActiveObjects());
            return true;
        }));
        return migrationCommands;
    }

    private Function<ActiveObjectsServiceWrapper, Boolean> createSafeCommand(Function<ActiveObjectsServiceWrapper, Boolean> target) {
        return activeObjectsServiceWrapper -> {
            boolean result = false;
            try {
                result = (Boolean)target.apply((ActiveObjectsServiceWrapper)activeObjectsServiceWrapper);
            }
            catch (Exception ex) {
                LOGGER.error("Exception happen when running AO migration manually", (Throwable)ex);
            }
            return result;
        };
    }

    @Path(value="ao/status")
    @GET
    @Produces(value={"application/json"})
    public Response getMigrationStatus() {
        return Response.status((Response.Status)Response.Status.OK).header(CONTENT_TYPE_HEADER, (Object)"application/json").entity((Object)new MigrationResourceEntity(this.bandanaToActiveObjectMigrationManager.getStatus(), this.bandanaToActiveObjectMigrationManager.getMigrationEvents()).toJson().toString()).build();
    }

    @Path(value="ao/run")
    @POST
    @Produces(value={"application/json"})
    @RequireSecurityToken(value=true)
    public Response runMigration() {
        GeneralResponseEntity responseEntity = new GeneralResponseEntity();
        try {
            boolean result = true;
            for (Function<ActiveObjectsServiceWrapper, Boolean> command : this.migrationCommamnds) {
                if (command.apply(this.activeObjectsWrapper).booleanValue()) continue;
                result = false;
                break;
            }
            responseEntity.setSuccess(result);
        }
        catch (Exception ex) {
            responseEntity.setSuccess(false);
            LOGGER.error("Exception happen when running AO migration manually", (Throwable)ex);
        }
        return Response.status((Response.Status)Response.Status.OK).header(CONTENT_TYPE_HEADER, (Object)"application/json").entity((Object)responseEntity.toJson().toString()).build();
    }

    @Path(value="ao/force")
    @DELETE
    @Produces(value={"application/json"})
    @RequireSecurityToken(value=true)
    public Response forceData() {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        GeneralResponseEntity responseEntity = new GeneralResponseEntity();
        try {
            boolean result = this.bandanaToActiveObjectMigrationManager.forceDeleteAllData(this.activeObjectsWrapper);
            responseEntity.setSuccess(result && this.customContentTypeTCMigrationManager.deleteAllCalendarContentTypes());
        }
        catch (Exception ex) {
            responseEntity.setSuccess(false);
            LOGGER.error("Exception while forcing delete of AO calendar data ", (Throwable)ex);
        }
        return Response.status((Response.Status)Response.Status.OK).header(CONTENT_TYPE_HEADER, (Object)"application/json").entity((Object)responseEntity.toJson().toString()).build();
    }

    @Path(value="spacecalendars")
    @POST
    @Produces(value={"application/json"})
    @RequireSecurityToken(value=true)
    public Response migrateSpaceCalendars() {
        GeneralResponseEntity responseEntity = new GeneralResponseEntity();
        try {
            for (CalendarsToSpaceMigrator migrator : this.calendarsToSpaceMigrators) {
                migrator.doMigrate(this.activeObjectsWrapper);
            }
            responseEntity.setSuccess(true);
        }
        catch (Exception ex) {
            responseEntity.setSuccess(false);
            LOGGER.error("Exception happen when running AO migration manually", (Throwable)ex);
        }
        return Response.status((Response.Status)Response.Status.OK).header(CONTENT_TYPE_HEADER, (Object)"application/json").entity((Object)responseEntity.toJson().toString()).build();
    }

    public static interface Function<F, T> {
        public T apply(@Nullable F var1) throws Exception;

        public boolean equals(@Nullable Object var1);
    }
}

