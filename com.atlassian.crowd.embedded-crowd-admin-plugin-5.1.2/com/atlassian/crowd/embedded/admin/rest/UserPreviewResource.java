/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.crowd.embedded.admin.rest;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.crowd.embedded.admin.DirectoryMapper;
import com.atlassian.crowd.embedded.admin.crowd.CrowdDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.dto.CrowdUserSyncPreviewRequest;
import com.atlassian.crowd.embedded.admin.dto.LdapUserSyncPreviewRequest;
import com.atlassian.crowd.embedded.admin.dto.UserSyncPreviewResult;
import com.atlassian.crowd.embedded.admin.ldap.LdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.service.UserSyncPreviewService;
import com.atlassian.crowd.embedded.admin.util.PasswordRestoreUtil;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;

@Path(value="/preview-users")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class UserPreviewResource {
    private final UserSyncPreviewService userSyncPreviewService;
    private final DirectoryMapper directoryMapper;
    private final PasswordRestoreUtil passwordRestoreUtil;
    private ObjectMapper objectMapper = new ObjectMapper();

    public UserPreviewResource(UserSyncPreviewService userSyncPreviewService, DirectoryMapper directoryMapper, PasswordRestoreUtil passwordRestoreUtil) {
        this.userSyncPreviewService = userSyncPreviewService;
        this.directoryMapper = directoryMapper;
        this.passwordRestoreUtil = passwordRestoreUtil;
    }

    @POST
    @Path(value="/ldap")
    public Response getLdapUserPreviewResult(InputStream requestBody) throws Exception {
        LdapUserSyncPreviewRequest request = (LdapUserSyncPreviewRequest)this.objectMapper.readValue(requestBody, LdapUserSyncPreviewRequest.class);
        LdapDirectoryConfiguration configuration = request.getDirectoryConfiguration();
        Directory directory = this.directoryMapper.buildLdapDirectory(configuration);
        Directory directoryWithPassword = this.passwordRestoreUtil.restoreOldPasswordIfNewIsEmpty(configuration, directory);
        UserSyncPreviewResult userPreviewResult = this.userSyncPreviewService.getUserPreviewResult(directoryWithPassword, request);
        return Response.ok((Object)userPreviewResult).build();
    }

    @POST
    @Path(value="/crowd")
    public Response getCrowdUserPreviewResult(InputStream requestBody) throws Exception {
        CrowdUserSyncPreviewRequest request = (CrowdUserSyncPreviewRequest)this.objectMapper.readValue(requestBody, CrowdUserSyncPreviewRequest.class);
        CrowdDirectoryConfiguration configuration = request.getDirectoryConfiguration();
        Directory directory = this.directoryMapper.buildCrowdDirectory(configuration);
        Directory directoryWithPassword = this.passwordRestoreUtil.restoreOldPasswordIfNewIsEmpty(configuration, directory);
        UserSyncPreviewResult userPreviewResult = this.userSyncPreviewService.getUserPreviewResult(directoryWithPassword, request);
        return Response.ok((Object)userPreviewResult).build();
    }

    @VisibleForTesting
    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}

