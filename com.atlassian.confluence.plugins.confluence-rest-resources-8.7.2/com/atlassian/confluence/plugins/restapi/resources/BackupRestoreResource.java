/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobDetails
 *  com.atlassian.confluence.api.model.backuprestore.JobFilter
 *  com.atlassian.confluence.api.model.backuprestore.JobFilter$Builder
 *  com.atlassian.confluence.api.model.backuprestore.JobOperation
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobState
 *  com.atlassian.confluence.api.model.backuprestore.SiteBackupJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SiteBackupSettings
 *  com.atlassian.confluence.api.model.backuprestore.SiteRestoreJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SiteRestoreSettings
 *  com.atlassian.confluence.api.model.backuprestore.SpaceBackupJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SpaceBackupSettings
 *  com.atlassian.confluence.api.model.backuprestore.SpaceRestoreJobDetails
 *  com.atlassian.confluence.api.model.backuprestore.SpaceRestoreSettings
 *  com.atlassian.confluence.api.service.backuprestore.BackupRestoreService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.multipart.FilePart
 *  com.atlassian.plugins.rest.common.multipart.MultipartFormParam
 *  com.atlassian.plugins.rest.common.security.SystemAdminOnly
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.collect.ImmutableSet
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.StreamingOutput
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.api.model.backuprestore.JobDetails;
import com.atlassian.confluence.api.model.backuprestore.JobFilter;
import com.atlassian.confluence.api.model.backuprestore.JobOperation;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobState;
import com.atlassian.confluence.api.model.backuprestore.SiteBackupJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SiteBackupSettings;
import com.atlassian.confluence.api.model.backuprestore.SiteRestoreJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SiteRestoreSettings;
import com.atlassian.confluence.api.model.backuprestore.SpaceBackupJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SpaceBackupSettings;
import com.atlassian.confluence.api.model.backuprestore.SpaceRestoreJobDetails;
import com.atlassian.confluence.api.model.backuprestore.SpaceRestoreSettings;
import com.atlassian.confluence.api.service.backuprestore.BackupRestoreService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartFormParam;
import com.atlassian.plugins.rest.common.security.SystemAdminOnly;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/backup-restore")
public class BackupRestoreResource {
    private static final Set<String> ALLOWED_ZIP_MIME_TYPES = ImmutableSet.of((Object)"application/zip", (Object)"application/x-zip-compressed");
    private final BackupRestoreService backupRestoreService;

    public BackupRestoreResource(@ComponentImport BackupRestoreService backupRestoreService) {
        this.backupRestoreService = backupRestoreService;
    }

    @POST
    @Path(value="/backup/space")
    public Response createSpaceBackupJob(SpaceBackupSettings settings) {
        SpaceBackupJobDetails job = this.backupRestoreService.createSpaceBackupJob(settings);
        return Response.ok((Object)job).build();
    }

    @POST
    @WebSudoRequired
    @SystemAdminOnly
    @Path(value="/backup/site")
    public Response createSiteBackupJob(SiteBackupSettings settings) {
        SiteBackupJobDetails job = this.backupRestoreService.createSiteBackupJob(settings);
        return Response.ok((Object)job).build();
    }

    @POST
    @WebSudoRequired
    @SystemAdminOnly
    @Path(value="/restore/space")
    public Response createSpaceRestoreJob(SpaceRestoreSettings settings) {
        SpaceRestoreJobDetails job = this.backupRestoreService.createSpaceRestoreJob(settings);
        return Response.ok((Object)job).build();
    }

    @POST
    @WebSudoRequired
    @SystemAdminOnly
    @Path(value="/restore/site")
    public Response createSiteRestoreJob(SiteRestoreSettings settings) {
        SiteRestoreJobDetails job = this.backupRestoreService.createSiteRestoreJob(settings);
        return Response.ok((Object)job).build();
    }

    @POST
    @WebSudoRequired
    @SystemAdminOnly
    @Consumes(value={"multipart/form-data"})
    @Path(value="/restore/space/upload")
    public Response createSpaceRestoreJobForUploadedBackupFile(@MultipartFormParam(value="file") FilePart file) throws IOException {
        BackupRestoreResource.validateZipFile(file);
        SpaceRestoreSettings spaceRestoreSettings = new SpaceRestoreSettings();
        spaceRestoreSettings.setFileName(file.getName());
        SpaceRestoreJobDetails job = this.backupRestoreService.createSpaceRestoreJob(spaceRestoreSettings, file.getInputStream());
        return Response.ok((Object)job).build();
    }

    @POST
    @WebSudoRequired
    @SystemAdminOnly
    @Consumes(value={"multipart/form-data"})
    @Path(value="/restore/site/upload")
    public Response createSiteRestoreJobForUploadedBackupFile(@MultipartFormParam(value="file") FilePart file) throws IOException {
        BackupRestoreResource.validateZipFile(file);
        SiteRestoreSettings siteRestoreSettings = new SiteRestoreSettings();
        siteRestoreSettings.setFileName(file.getName());
        SiteRestoreJobDetails job = this.backupRestoreService.createSiteRestoreJob(siteRestoreSettings, file.getInputStream());
        return Response.ok((Object)job).build();
    }

    @GET
    @Path(value="/jobs/{jobId}")
    public Response getJob(@PathParam(value="jobId") Long jobId) {
        if (jobId == null) {
            throw new BadRequestException("jodId couldn't be null");
        }
        JobDetails job = this.backupRestoreService.getJob(jobId.longValue());
        return Response.ok((Object)job).build();
    }

    @GET
    @Path(value="/jobs")
    public Response findJobs(@QueryParam(value="limit") @DefaultValue(value="25") Integer limit, @QueryParam(value="fromDate") String fromDate, @QueryParam(value="toDate") String toDate, @QueryParam(value="jobScope") JobScope jobScope, @QueryParam(value="jobOperation") JobOperation jobOperation, @QueryParam(value="jobStates") List<JobState> jobStates, @QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="owner") String owner) {
        JobFilter jobFilter;
        try {
            JobFilter.Builder builder = JobFilter.builder();
            builder.setJobScope(jobScope);
            builder.setJobOperation(jobOperation);
            jobStates.forEach(arg_0 -> ((JobFilter.Builder)builder).addJobState(arg_0));
            builder.setOwner(owner);
            builder.setFromDate(BackupRestoreResource.convertDate(fromDate));
            builder.setToDate(BackupRestoreResource.convertDate(toDate));
            builder.setSpaceKey(spaceKey);
            builder.setLimit(limit.intValue());
            jobFilter = builder.build();
        }
        catch (IllegalArgumentException e) {
            throw new BadRequestException((Throwable)e);
        }
        List job = this.backupRestoreService.findJobs(jobFilter);
        return Response.ok((Object)job).build();
    }

    @GET
    @WebSudoRequired
    @SystemAdminOnly
    @Path(value="/restore/files")
    public Response getFiles(@QueryParam(value="jobScope") JobScope jobScope) {
        List fileInfoList = this.backupRestoreService.getFiles(jobScope);
        return Response.ok((Object)fileInfoList).build();
    }

    @PUT
    @Path(value="/jobs/{jobId}/cancel")
    public Response cancelJob(@PathParam(value="jobId") Long jobId) {
        if (jobId == null) {
            throw new BadRequestException("jodId couldn't be null");
        }
        JobDetails job = this.backupRestoreService.cancelJob(jobId.longValue());
        return Response.ok((Object)job).build();
    }

    @GET
    @Path(value="/jobs/{jobId}/download")
    public Response downloadBackupFile(@PathParam(value="jobId") Long jobId) {
        if (jobId == null) {
            throw new BadRequestException("jodId couldn't be null");
        }
        try {
            File backupFile = this.backupRestoreService.getBackupFile(jobId);
            StreamingOutput streamingOutput = outputStream -> {
                try (FileInputStream inputStream = new FileInputStream(backupFile);){
                    IOUtils.copyLarge((InputStream)inputStream, (OutputStream)outputStream);
                }
            };
            return Response.ok((Object)streamingOutput, (MediaType)MediaType.APPLICATION_OCTET_STREAM_TYPE).header("Content-Disposition", (Object)("attachment; filename=\"" + backupFile.getName() + "\"")).header("Content-Length", (Object)Long.toString(backupFile.length())).build();
        }
        catch (NotFoundException e) {
            throw new NotFoundException(String.format("Job, file or permissions missing for jobId %d", jobId));
        }
    }

    @PUT
    @WebSudoRequired
    @SystemAdminOnly
    @Path(value="/jobs/clear-queue")
    public Response cancelAllQueuedJobs() {
        int cancelledJobsCount = this.backupRestoreService.cancelAllQueuedJobs();
        return Response.ok((Object)cancelledJobsCount).build();
    }

    private static Instant convertDate(String stringDate) {
        if (StringUtils.isEmpty((CharSequence)stringDate)) {
            return null;
        }
        try {
            return Instant.parse(stringDate);
        }
        catch (DateTimeParseException e) {
            throw new BadRequestException(String.format("Date %s is in invalid format. Supported date format is `yyyy-MM-ddTHH:mm:ss.SSSZ`", stringDate));
        }
    }

    private static void validateZipFile(FilePart file) {
        if (!ALLOWED_ZIP_MIME_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("The uploaded file should be a valid zip file.");
        }
    }
}

