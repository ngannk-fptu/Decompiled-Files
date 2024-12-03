/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.rest;

import com.atlassian.fugue.Either;
import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.HandleErrorFunction;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.Server;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerConnectionException;
import com.atlassian.plugin.notifications.api.medium.ServerFactory;
import com.atlassian.plugin.notifications.api.notification.NotificationStatusRepresentation;
import com.atlassian.plugin.notifications.config.ServerConfigurationManager;
import com.atlassian.plugin.notifications.module.NotificationMediumManager;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;

@Path(value="server")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@WebSudoRequired
public class ServerResource {
    private static final String SERVER_NAME = "server-name";
    private static final String CUSTOM_TEMPLATE_PATH = "customTemplatePath";
    private static final String NOTIFICATION_MEDIUM = "notification-medium";
    private static final String SERVER_GROUP_ACCESS = "server-group-access";
    private static final String ENABLED_FOR_ALL = "enabled-for-all";
    private final NotificationMediumManager notificationMediumManager;
    private final ServerConfigurationManager serverConfigurationManager;
    private final ServerFactory serverFactory;
    private final UserManager userManager;

    public ServerResource(UserManager userManager, NotificationMediumManager notificationMediumManager, ServerConfigurationManager serverConfigurationManager, ServerFactory serverFactory) {
        this.userManager = userManager;
        this.notificationMediumManager = notificationMediumManager;
        this.serverConfigurationManager = serverConfigurationManager;
        this.serverFactory = serverFactory;
    }

    @PUT
    @Path(value="status")
    public Response setNotificationStatus(NotificationStatusRepresentation status) {
        ErrorCollection errorCollection = this.serverConfigurationManager.validateToggleNotifications(this.userManager.getRemoteUsername(), status.isEnabled());
        if (errorCollection.hasAnyErrors()) {
            return new HandleErrorFunction().apply(errorCollection);
        }
        this.serverConfigurationManager.toggleNotifications(status.isEnabled());
        return Response.ok().cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    @GET
    @Path(value="{id}/group")
    public Response getGroups(@PathParam(value="id") int id, @QueryParam(value="query") String query) {
        String remoteUsername = this.userManager.getRemoteUsername();
        if (!this.userManager.isSystemAdmin(remoteUsername)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        ServerConfiguration config = this.serverConfigurationManager.getServer(id);
        if (config == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        Server server = this.serverFactory.getServer(config);
        try {
            return Response.ok(server.getAvailableGroups(query)).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        catch (ServerConnectionException e) {
            ErrorCollection errors = new ErrorCollection();
            errors.addErrorMessage(e.getMessage());
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)errors).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
    }

    @PUT
    @Path(value="{id}")
    public Response editServer(@PathParam(value="id") int id, Data data) {
        String remoteUsername = this.userManager.getRemoteUsername();
        if (!this.userManager.isSystemAdmin(remoteUsername)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        ServerConfiguration server = this.serverConfigurationManager.getServer(id);
        if (server == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        Map<String, String> params = data.toMap();
        ServerConfigurationManager.CommonServerConfig commonConfig = new ServerConfigurationManager.CommonServerConfig(id, params.get(SERVER_NAME), null, params.get(CUSTOM_TEMPLATE_PATH), params.containsKey(ENABLED_FOR_ALL), data.getParamValues(SERVER_GROUP_ACCESS));
        this.stripCommonParams(params);
        Either<ErrorCollection, ServerConfigurationManager.ServerValidationResult> result = this.serverConfigurationManager.validateUpdateServer(commonConfig, params);
        return (Response)result.fold((Function)new HandleErrorFunction(), (Function)new Function<ServerConfigurationManager.ServerValidationResult, Response>(){

            public Response apply(@Nullable ServerConfigurationManager.ServerValidationResult input) {
                ServerResource.this.serverConfigurationManager.updateServer(input);
                return Response.ok().cacheControl(HandleErrorFunction.NO_CACHE).build();
            }
        });
    }

    @POST
    public Response addServer(Data data) {
        String remoteUsername = this.userManager.getRemoteUsername();
        if (!this.userManager.isSystemAdmin(remoteUsername)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        Map<String, String> params = data.toMap();
        String mediumTypeParam = params.get(NOTIFICATION_MEDIUM);
        NotificationMedium notificationMedium = this.notificationMediumManager.getNotificationMedium(mediumTypeParam);
        if (notificationMedium == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        ServerConfigurationManager.CommonServerConfig commonConfig = new ServerConfigurationManager.CommonServerConfig(0, params.get(SERVER_NAME), mediumTypeParam, params.get(CUSTOM_TEMPLATE_PATH), params.containsKey(ENABLED_FOR_ALL), data.getParamValues(SERVER_GROUP_ACCESS));
        this.stripCommonParams(params);
        Either<ErrorCollection, ServerConfigurationManager.ServerValidationResult> result = this.serverConfigurationManager.validateAddServer(commonConfig, params);
        return (Response)result.fold((Function)new HandleErrorFunction(), (Function)new Function<ServerConfigurationManager.ServerValidationResult, Response>(){

            public Response apply(@Nullable ServerConfigurationManager.ServerValidationResult input) {
                ServerConfiguration config = ServerResource.this.serverConfigurationManager.addServer(input);
                return Response.ok((Object)config).cacheControl(HandleErrorFunction.NO_CACHE).build();
            }
        });
    }

    @DELETE
    @Path(value="{id}")
    public Response removeServer(@PathParam(value="id") int id) {
        String remoteUsername = this.userManager.getRemoteUsername();
        if (!this.userManager.isSystemAdmin(remoteUsername)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        ServerConfiguration server = this.serverConfigurationManager.getServer(id);
        if (server == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        this.serverConfigurationManager.removeServer(id);
        return Response.ok().cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    private void stripCommonParams(Map<String, String> params) {
        params.remove(SERVER_NAME);
        params.remove(SERVER_GROUP_ACCESS);
        params.remove(CUSTOM_TEMPLATE_PATH);
        params.remove(NOTIFICATION_MEDIUM);
        params.remove(ENABLED_FOR_ALL);
    }

    @XmlRootElement
    public static class Parameter {
        @XmlElement
        private String name;
        @XmlElement
        private String value;

        private Parameter() {
        }

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }
    }

    @XmlRootElement
    public static class Data {
        @XmlElement
        private List<Parameter> config;

        private Data() {
        }

        public Data(List<Parameter> config) {
            this.config = config;
        }

        public List<Parameter> getParams() {
            return this.config;
        }

        public Iterable<String> getParamValues(String name) {
            ArrayList ret = Lists.newArrayList();
            for (Parameter parameter : this.config) {
                if (!StringUtils.equals((CharSequence)name, (CharSequence)parameter.getName())) continue;
                ret.add(parameter.getValue());
            }
            return ret;
        }

        public Map<String, String> toMap() {
            HashMap ret = Maps.newHashMap();
            for (Parameter parameter : this.config) {
                ret.put(parameter.getName(), parameter.getValue());
            }
            return ret;
        }
    }
}

