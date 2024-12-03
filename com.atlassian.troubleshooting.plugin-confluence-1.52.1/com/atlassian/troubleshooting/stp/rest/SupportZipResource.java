/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.sun.jersey.api.NotFoundException
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.HeaderParam
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.rest;

import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.audit.Auditor;
import com.atlassian.troubleshooting.stp.persistence.ZipConfiguration;
import com.atlassian.troubleshooting.stp.persistence.ZipConfigurationRepository;
import com.atlassian.troubleshooting.stp.rest.CacheControlUtils;
import com.atlassian.troubleshooting.stp.rest.NodeLocalSupportZipTaskInfoDtoFactory;
import com.atlassian.troubleshooting.stp.rest.dto.ClusterNodeDto;
import com.atlassian.troubleshooting.stp.rest.dto.ClusteredZipTaskInfoDto;
import com.atlassian.troubleshooting.stp.rest.dto.LocalSupportZipTaskInfoDto;
import com.atlassian.troubleshooting.stp.rest.dto.NodeLocalSupportZipTaskInfoDto;
import com.atlassian.troubleshooting.stp.rest.dto.SupportZipInfoDto;
import com.atlassian.troubleshooting.stp.rest.dto.SupportZipItemDto;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.security.AuthorisationException;
import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import com.atlassian.troubleshooting.stp.util.ObjectMapperFactory;
import com.atlassian.troubleshooting.stp.zip.ClusterMessagingException;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import com.atlassian.troubleshooting.stp.zip.NotClusteredException;
import com.atlassian.troubleshooting.stp.zip.SupportZipRequest;
import com.atlassian.troubleshooting.stp.zip.SupportZipService;
import com.atlassian.troubleshooting.stp.zip.TaskNotFoundException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.spi.resource.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="support-zip")
@Produces(value={"application/json"})
@Singleton
@WebSudoRequired
public class SupportZipResource {
    @VisibleForTesting
    static final String INSTANCE_TITLE_I18N_KEY = "stp.instance.title";
    static final List<Integer> FILE_CONSTRAINT_SIZE_OPTIONS = Lists.newArrayList((Object[])new Integer[]{-1, 25, 100, 500});
    static final List<Integer> FILE_CONSTRAINT_LASTMODIFIED_OPTIONS = Lists.newArrayList((Object[])new Integer[]{-1, 0, 3, 5, 10});
    private static final Comparator<LocalSupportZipTaskInfoDto> FILE_NAME_COMPARATOR = Comparator.comparing(task -> StringUtils.trimToEmpty((String)task.getFileName()));
    @VisibleForTesting
    static final Comparator<LocalSupportZipTaskInfoDto> SUPPORT_ZIP_STATUS_SORTER = Comparator.comparingInt(LocalSupportZipTaskInfoDto::getProgressPercentage).thenComparing(FILE_NAME_COMPARATOR.reversed());
    private static final Supplier<NotFoundException> TASK_NOT_FOUND = () -> new NotFoundException("No such task found");
    private static final String NOT_CLUSTERED_MESSAGE = "Not clustered";
    private static final String REFERRER = "Referer";
    private final ClusterService clusterService;
    private final NodeLocalSupportZipTaskInfoDtoFactory nodeLocalSupportZipTaskInfoDtoFactory;
    private final PermissionValidationService permissionValidationService;
    private final SupportApplicationInfo applicationInfo;
    private final SupportZipService supportZipService;
    private final Auditor auditor;
    private final ZipConfigurationRepository zipConfigurationRepository;

    @Autowired
    public SupportZipResource(PermissionValidationService permissionValidationService, SupportApplicationInfo applicationInfo, SupportZipService supportZipService, ClusterService clusterService, NodeLocalSupportZipTaskInfoDtoFactory nodeLocalSupportZipTaskInfoDtoFactory, Auditor auditor, ZipConfigurationRepository zipConfigurationRepository) {
        this.permissionValidationService = Objects.requireNonNull(permissionValidationService);
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.supportZipService = Objects.requireNonNull(supportZipService);
        this.clusterService = Objects.requireNonNull(clusterService);
        this.nodeLocalSupportZipTaskInfoDtoFactory = nodeLocalSupportZipTaskInfoDtoFactory;
        this.auditor = Objects.requireNonNull(auditor);
        this.zipConfigurationRepository = zipConfigurationRepository;
    }

    private static Response asTextResponse(String body, Response.Status status) {
        return Response.status((Response.Status)status).type("text/plain").entity((Object)body).build();
    }

    @Nonnull
    @VisibleForTesting
    static SupportZipRequest parseSupportZipRequest(String jsonBody, String httpReferrer, SupportApplicationInfo applicationInfo) throws InvalidSupportZipRequestException {
        SupportZipRequest.Source source = SupportZipResource.inferSourceOfCreateZipRequest(httpReferrer);
        if (StringUtils.isBlank((CharSequence)jsonBody)) {
            return SupportZipRequest.withDefaultSettings(applicationInfo, source);
        }
        try {
            SupportZipRequest supportZipRequest = (SupportZipRequest)ObjectMapperFactory.getObjectMapper().readValue(jsonBody, SupportZipRequest.class);
            if (supportZipRequest.getItems() == null) {
                supportZipRequest.useDefaultItems(applicationInfo);
            }
            if (supportZipRequest.getItems().isEmpty()) {
                throw new InvalidSupportZipRequestException("You must specify one or more items to include in the Support zip");
            }
            if (supportZipRequest.appliesToNoNodes()) {
                throw new InvalidSupportZipRequestException("You must specify one or more nodeIds when generating the Support zip");
            }
            if (supportZipRequest.getFileConstraintSize() != null && !FILE_CONSTRAINT_SIZE_OPTIONS.contains(supportZipRequest.getFileConstraintSize())) {
                throw new InvalidSupportZipRequestException("The restriction on size of files for inclusion must be -1 (unlimited), 25, 100 or 500 megabytes");
            }
            if (supportZipRequest.getFileConstraintLastModified() != null && !FILE_CONSTRAINT_LASTMODIFIED_OPTIONS.contains((int)supportZipRequest.getFileConstraintLastModified())) {
                throw new InvalidSupportZipRequestException("The restriction on last modified date of files for inclusion must be -1 (unrestricted), 0 (today), 3, 5 or 10 days");
            }
            return supportZipRequest.withSource(source);
        }
        catch (IOException e) {
            throw new InvalidSupportZipRequestException(String.format("Cannot parse Support zip request from '%s'", jsonBody), e);
        }
    }

    private static SupportZipRequest.Source inferSourceOfCreateZipRequest(String httpReferrer) {
        return StringUtils.isBlank((CharSequence)httpReferrer) ? SupportZipRequest.Source.REST_V1 : SupportZipRequest.Source.WEB_V2;
    }

    private static Response asDownloadResponse(File fileToDownload) {
        return Response.ok((Object)fileToDownload, (MediaType)MediaType.APPLICATION_OCTET_STREAM_TYPE).header("Content-Disposition", (Object)("attachment; filename=\"" + fileToDownload.getName() + "\"")).build();
    }

    private static NotFoundException notFoundException(String filename) {
        return new NotFoundException(String.format("'%s' does not exist or is not downloadable", filename));
    }

    @GET
    @Path(value="legacy/status/{taskId}")
    @Deprecated
    public Response getSupportZipStatusLegacy(@PathParam(value="taskId") String taskId) {
        return this.supportZipService.getMonitor(taskId).map(monitor -> {
            Response.ResponseBuilder builder = Response.ok().entity(monitor.getAttributes());
            if (!monitor.isDone()) {
                builder.cacheControl(CacheControlUtils.NO_CACHE);
            }
            return builder.build();
        }).orElseThrow(TASK_NOT_FOUND);
    }

    @GET
    @Path(value="status/task/{taskId}")
    public LocalSupportZipTaskInfoDto getSupportZipStatus(@PathParam(value="taskId") String taskId) {
        return this.supportZipService.getMonitor(taskId).map(LocalSupportZipTaskInfoDto::localSupportZipTaskInfo).orElseThrow(TASK_NOT_FOUND);
    }

    @GET
    @Path(value="status/task")
    public Collection<LocalSupportZipTaskInfoDto> getSupportZipStatusList() {
        return this.supportZipService.getMonitors(false).stream().map(LocalSupportZipTaskInfoDto::localSupportZipTaskInfo).sorted(SUPPORT_ZIP_STATUS_SORTER).collect(Collectors.toList());
    }

    @GET
    @Path(value="status/cluster/{clusterTaskId}")
    public ClusteredZipTaskInfoDto getClusteredSupportZipStatus(@PathParam(value="clusterTaskId") String clusterTaskId) {
        List<NodeLocalSupportZipTaskInfoDto> clusteredMonitors = this.supportZipService.getClusteredMonitors(clusterTaskId).stream().map(this.nodeLocalSupportZipTaskInfoDtoFactory::create).collect(Collectors.toList());
        return new ClusteredZipTaskInfoDto(clusterTaskId, clusteredMonitors);
    }

    @GET
    @Path(value="info")
    public SupportZipInfoDto getInfo() {
        this.permissionValidationService.validateIsSysadmin();
        List<ClusterNodeDto> nodes = this.supportZipService.isClusterSupportZipSupported() ? this.clusterService.getNodeIds().stream().map(ClusterNodeDto::new).collect(Collectors.toList()) : null;
        ZipConfiguration storedConfiguration = this.zipConfigurationRepository.getConfiguration().orElse(ZipConfiguration.getDefaultConfiguration(this.applicationInfo));
        List<SupportZipItemDto> itemOptions = this.applicationInfo.getSupportZipBundles().stream().filter(SupportZipBundle::isApplicable).map(bundle -> SupportZipItemDto.supportZipOption(bundle, storedConfiguration)).collect(Collectors.toList());
        String instanceTitle = this.getInstanceTitle();
        return new SupportZipInfoDto(itemOptions, nodes, instanceTitle, storedConfiguration.getFileConstraintSize(), storedConfiguration.getFileConstraintLastModified());
    }

    private String getInstanceTitle() {
        return this.applicationInfo.getInstanceTitle().filter(StringUtils::isNotBlank).orElseGet(() -> this.applicationInfo.getText(INSTANCE_TITLE_I18N_KEY, new Serializable[]{this.applicationInfo.getApplicationName()}));
    }

    @POST
    @Path(value="local")
    @Consumes(value={"application/json"})
    public Object createLocalSupportZip(String jsonBody, @HeaderParam(value="Referer") String httpReferrer) {
        try {
            SupportZipRequest supportZipRequest = SupportZipResource.parseSupportZipRequest(jsonBody, httpReferrer, this.applicationInfo);
            return LocalSupportZipTaskInfoDto.localSupportZipTaskInfo(this.createLocalSupportZip(supportZipRequest));
        }
        catch (InvalidSupportZipRequestException e) {
            return SupportZipResource.asTextResponse(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    private CreateSupportZipMonitor createLocalSupportZip(SupportZipRequest supportZipRequest) {
        return this.supportZipService.createLocalSupportZipWithPermissionCheck(Objects.requireNonNull(supportZipRequest));
    }

    @POST
    @Path(value="cluster")
    @Consumes(value={"application/json"})
    public Object createClusterSupportZip(String jsonBody, @HeaderParam(value="Referer") String httpReferrer) {
        try {
            SupportZipRequest clusterSupportZipRequest = SupportZipResource.parseSupportZipRequest(jsonBody, httpReferrer, this.applicationInfo).forCluster();
            try {
                return this.supportZipService.createSupportZipsForCluster(clusterSupportZipRequest);
            }
            catch (NotClusteredException e) {
                return SupportZipResource.asTextResponse(NOT_CLUSTERED_MESSAGE, Response.Status.PRECONDITION_FAILED);
            }
            catch (ClusterMessagingException e) {
                return SupportZipResource.asTextResponse(e.getMessage(), Response.Status.BAD_REQUEST);
            }
        }
        catch (InvalidSupportZipRequestException e) {
            return SupportZipResource.asTextResponse(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path(value="download/{fileName}")
    public Response downloadExportFile(@PathParam(value="fileName") String filename) throws NotFoundException {
        try {
            this.permissionValidationService.validateIsSysadmin();
            File exportFile = this.applicationInfo.getExportFile(filename).orElseThrow(() -> SupportZipResource.notFoundException(filename));
            this.auditor.audit("stp.audit.summary.support-zip.downloaded");
            return SupportZipResource.asDownloadResponse(exportFile);
        }
        catch (AuthorisationException e) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)e.getMessage()).build();
        }
    }

    @DELETE
    @Path(value="task/{taskId}")
    public Response cancelSupportZipTask(@PathParam(value="taskId") String taskId) {
        try {
            this.supportZipService.cancelSupportZipTask(taskId);
            return Response.ok().build();
        }
        catch (TaskNotFoundException e) {
            return SupportZipResource.asTextResponse(e.getMessage(), Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path(value="cluster-status")
    public Object getLatestTasksForEachNodeInCluster() {
        if (!this.clusterService.isClustered()) {
            return SupportZipResource.asTextResponse(NOT_CLUSTERED_MESSAGE, Response.Status.PRECONDITION_FAILED);
        }
        TreeMap tasksByNodeId = Maps.newTreeMap();
        for (CreateSupportZipMonitor taskMonitor : this.supportZipService.getMonitors(true)) {
            taskMonitor.getNodeId().ifPresent(nodeId -> {
                Collection nodeTasks = tasksByNodeId.computeIfAbsent(nodeId, k -> Lists.newArrayList());
                nodeTasks.add(LocalSupportZipTaskInfoDto.localSupportZipTaskInfo(taskMonitor));
            });
        }
        tasksByNodeId.values().forEach(taskList -> taskList.sort(SUPPORT_ZIP_STATUS_SORTER));
        return tasksByNodeId;
    }

    @VisibleForTesting
    static class InvalidSupportZipRequestException
    extends Exception {
        private static final long serialVersionUID = 4350683095393332766L;

        InvalidSupportZipRequestException(String message) {
            this(message, null);
        }

        InvalidSupportZipRequestException(String message, @Nullable Throwable cause) {
            super(message, cause);
        }
    }
}

