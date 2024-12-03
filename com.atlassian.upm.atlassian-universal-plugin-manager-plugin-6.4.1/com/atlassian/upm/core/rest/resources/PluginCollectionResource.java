/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.plugins.rest.common.multipart.FilePart
 *  com.atlassian.plugins.rest.common.multipart.MultipartHandler
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.HEAD
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.resources;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartHandler;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.SelfUpdateController;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentationFactory;
import com.atlassian.upm.core.rest.representations.BasePluginRepresentationFactory;
import com.atlassian.upm.core.rest.resources.RequestContext;
import com.atlassian.upm.core.rest.resources.UpmResources;
import com.atlassian.upm.core.rest.resources.install.InstallFromFileTask;
import com.atlassian.upm.core.rest.resources.install.InstallFromUriTask;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.core.token.TokenManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/")
public class PluginCollectionResource {
    private final BasePluginRepresentationFactory representationFactory;
    private final PluginDownloadService pluginDownloadService;
    private final PluginInstallationService pluginInstaller;
    private final SelfUpdateController selfUpdateController;
    private final AsynchronousTaskManager taskManager;
    private final PermissionEnforcer permissionEnforcer;
    private final AuditLogService auditLogger;
    private final TokenManager tokenManager;
    private final UserManager userManager;
    private final BaseUriBuilder uriBuilder;
    private final PluginRetriever pluginRetriever;
    private final LocaleResolver localeResolver;
    private final I18nResolver i18nResolver;
    private final AsyncTaskRepresentationFactory taskRepresentationFactory;
    private final SysPersisted sysPersisted;
    private final ApplicationPluginsManager applicationPluginsManager;

    public PluginCollectionResource(BasePluginRepresentationFactory representationFactory, PluginDownloadService pluginDownloadService, PluginInstallationService pluginInstaller, SelfUpdateController selfUpdateController, AsynchronousTaskManager taskManager, PermissionEnforcer permissionEnforcer, AuditLogService auditLogger, TokenManager tokenManager, UserManager userManager, BaseUriBuilder uriBuilder, PluginRetriever pluginRetriever, LocaleResolver localeResolver, I18nResolver i18nResolver, AsyncTaskRepresentationFactory taskRepresentationFactory, SysPersisted sysPersisted, ApplicationPluginsManager applicationPluginsManager) {
        this.sysPersisted = sysPersisted;
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.pluginDownloadService = Objects.requireNonNull(pluginDownloadService, "pluginDownloadService");
        this.pluginInstaller = Objects.requireNonNull(pluginInstaller, "pluginInstaller");
        this.selfUpdateController = Objects.requireNonNull(selfUpdateController, "selfUpdateController");
        this.taskManager = Objects.requireNonNull(taskManager, "taskManager");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
        this.tokenManager = Objects.requireNonNull(tokenManager, "tokenManager");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.localeResolver = Objects.requireNonNull(localeResolver, "localeResolver");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.taskRepresentationFactory = Objects.requireNonNull(taskRepresentationFactory, "taskRepresentationFactory");
        this.applicationPluginsManager = Objects.requireNonNull(applicationPluginsManager, "applicationPluginsManager");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.installed+json"})
    public Response get(@Context HttpServletRequest request) {
        this.permissionEnforcer.enforcePermission(Permission.GET_INSTALLED_PLUGINS);
        List<Plugin> plugins = Iterables.toList(this.pluginRetriever.getPlugins());
        Map<String, UpmAppManager.ApplicationDescriptorModuleInfo> appPlugins = this.applicationPluginsManager.getApplicationRelatedPlugins(StreamSupport.stream(plugins.spliterator(), false).map(Plugins.toPlugPlugin).collect(Collectors.toList()));
        return Response.ok((Object)this.representationFactory.createInstalledPluginCollectionRepresentation(this.localeResolver.getLocale(request), plugins, appPlugins, new RequestContext(request))).header("upm-token", (Object)this.tokenManager.getTokenForUser(this.userManager.getRemoteUserKey())).build();
    }

    @HEAD
    @Produces(value={"application/vnd.atl.plugins.installed+json"})
    @WebSudoNotRequired
    public Response head() {
        this.permissionEnforcer.enforceAdmin();
        return Response.ok().header("upm-token", (Object)this.tokenManager.getTokenForUser(this.userManager.getRemoteUserKey())).build();
    }

    @POST
    @Consumes(value={"application/vnd.atl.plugins.install.uri+json"})
    public Response installFromUri(InstallPluginUri installPluginUri, @QueryParam(value="token") String token) {
        try {
            URI uri = new URI(installPluginUri.getPluginUri());
            this.permissionEnforcer.enforceAdmin();
            UpmResources.validateToken(token, this.userManager.getRemoteUserKey(), "application/json", this.tokenManager, this.representationFactory);
            String name = installPluginUri.getPluginName();
            if (!uri.isAbsolute()) {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.pluginInstall.error.invalid.relative.uri")).type("application/vnd.atl.plugins.task.error+json").build();
            }
            InstallFromUriTask task = new InstallFromUriTask(uri, Option.option(name), this.pluginDownloadService, this.auditLogger, this.pluginInstaller, this.selfUpdateController, this.uriBuilder, this.applicationPluginsManager, this.i18nResolver, this.permissionEnforcer, this.sysPersisted);
            AsyncTaskInfo taskInfo = this.taskManager.executeAsynchronousTask(task);
            return this.taskRepresentationFactory.createLegacyAsyncTaskRepresentation(taskInfo).toNewlyCreatedResponse(this.uriBuilder);
        }
        catch (URISyntaxException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.pluginInstall.error.invalid.uri.syntax")).type("application/vnd.atl.plugins.task.error+json").build();
        }
    }

    @POST
    @Consumes(value={"multipart/form-data", "multipart/mixed"})
    @XsrfProtectionExcluded
    public Response installFromFileSystem(@Context MultipartHandler multipartHandler, @Context HttpServletRequest request, @DefaultValue(value="jar") @QueryParam(value="type") String type, @QueryParam(value="token") String token) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_FILE);
        UpmResources.validateToken(token, this.userManager.getRemoteUserKey(), "text/html", this.tokenManager, this.representationFactory);
        try {
            FilePart filePart = multipartHandler.getFilePart(request, "plugin");
            File plugin = PluginCollectionResource.copyFilePartToTemporaryFile(filePart, type);
            InstallFromFileTask task = new InstallFromFileTask(Option.option(filePart.getName()), plugin, this.pluginInstaller, this.selfUpdateController, this.uriBuilder, this.applicationPluginsManager, this.i18nResolver);
            AsyncTaskInfo taskInfo = this.taskManager.executeAsynchronousTask(task);
            Response response = this.taskRepresentationFactory.createLegacyAsyncTaskRepresentation(taskInfo).toNewlyCreatedResponse(this.uriBuilder);
            String acceptHeader = request.getHeader("Accept");
            if (acceptHeader != null && (acceptHeader.contains("text/html") || acceptHeader.contains("*"))) {
                return Response.fromResponse((Response)response).type("text/html").build();
            }
            return response;
        }
        catch (IOException e) {
            return Response.serverError().entity((Object)this.representationFactory.createErrorRepresentation(e.getMessage())).type("application/vnd.atl.plugins.error+json").build();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static File copyFilePartToTemporaryFile(FilePart filePart, String type) throws IOException {
        File plugin = File.createTempFile("plugin_", PluginCollectionResource.getTempFileSuffix(filePart, type));
        InputStream in = filePart.getInputStream();
        FileOutputStream out = FileUtils.openOutputStream((File)plugin);
        try {
            IOUtils.copy((InputStream)in, (OutputStream)out);
        }
        finally {
            IOUtils.closeQuietly((InputStream)in);
            IOUtils.closeQuietly((OutputStream)out);
        }
        return plugin;
    }

    protected static String getTempFileSuffix(FilePart filePart, String type) {
        if (filePart.getName() == null) {
            return "." + type;
        }
        String[] paths = filePart.getName().split("[\\\\|/]");
        return "_" + paths[paths.length - 1];
    }

    public static class InstallPluginUri {
        @JsonProperty
        private String pluginUri;
        @JsonProperty
        private String pluginName;

        @JsonCreator
        public InstallPluginUri(@JsonProperty(value="pluginUri") String pluginUri, @JsonProperty(value="pluginName") String pluginName) {
            this.pluginUri = pluginUri;
            this.pluginName = pluginName;
        }

        public String getPluginUri() {
            return this.pluginUri;
        }

        public String getPluginName() {
            return this.pluginName;
        }
    }
}

