/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.sun.jersey.api.core.InjectParam
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.HEAD
 *  javax.ws.rs.HeaderParam
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.conversion.rest;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.conversion.annotation.CheckIfServiceIsEnabled;
import com.atlassian.confluence.plugins.conversion.annotation.ConversionPath;
import com.atlassian.confluence.plugins.conversion.api.ConversionData;
import com.atlassian.confluence.plugins.conversion.api.ConversionResult;
import com.atlassian.confluence.plugins.conversion.api.ConversionStatus;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.conversion.impl.DefaultConversionManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.sun.jersey.api.core.InjectParam;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@AnonymousAllowed
@Path(value="conversion")
public class ConfluenceConversionServiceResource {
    private static final String PARAM_ATT_ID = "attachmentId";
    private static final String PARAM_VERSION = "version";
    private final AttachmentManager attachmentManager;
    private final PermissionManager permissionManager;
    private final DefaultConversionManager conversionManager;

    public ConfluenceConversionServiceResource(AttachmentManager attachmentManager, PermissionManager permissionManager, DefaultConversionManager conversionManager) {
        this.attachmentManager = attachmentManager;
        this.permissionManager = permissionManager;
        this.conversionManager = conversionManager;
    }

    private Attachment getAttachment(Long attachmentId, Integer version, boolean throwIfError) {
        if (attachmentId == null || attachmentId <= 0L) {
            if (throwIfError) {
                throw ConfluenceConversionServiceResource.newWebException(Response.Status.BAD_REQUEST, "Invalid attachmentId");
            }
            return null;
        }
        if (version == null || version <= 0) {
            if (throwIfError) {
                throw ConfluenceConversionServiceResource.newWebException(Response.Status.BAD_REQUEST, "Invalid version");
            }
            return null;
        }
        Attachment attachment = this.attachmentManager.getAttachment(attachmentId.longValue());
        if (attachment != null && attachment.getVersion() != version.intValue()) {
            attachment = this.attachmentManager.getAttachment(attachment.getContainer(), attachment.getFileName(), version.intValue());
        }
        if (attachment == null) {
            if (throwIfError) {
                throw ConfluenceConversionServiceResource.newWebException(Response.Status.NOT_FOUND, null);
            }
            return null;
        }
        if (!this.permissionManager.hasPermissionNoExemptions((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)attachment)) {
            if (throwIfError) {
                throw ConfluenceConversionServiceResource.newWebException(Response.Status.NOT_FOUND, null);
            }
            return null;
        }
        return attachment;
    }

    @Path(value="convert/{attachmentId}/{version}")
    @GET
    @ConversionPath(value=ConversionType.DOCUMENT)
    @CheckIfServiceIsEnabled
    public Response getDocumentStream(@PathParam(value="attachmentId") Long attachmentId, @PathParam(value="version") Integer version, @HeaderParam(value="Range") String rangeHeader) {
        return this.getStream(attachmentId, version, ConversionType.DOCUMENT, rangeHeader);
    }

    @Path(value="convert/{attachmentId}/{version}")
    @HEAD
    @ConversionPath(value=ConversionType.DOCUMENT)
    @CheckIfServiceIsEnabled
    public Response getDocumentConversionResult(@PathParam(value="attachmentId") Long attachmentId, @PathParam(value="version") Integer version) {
        return this.getConversionResult(attachmentId, version, ConversionType.DOCUMENT, true);
    }

    @Path(value="convert/results")
    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @ConversionPath(value=ConversionType.DOCUMENT)
    @CheckIfServiceIsEnabled
    public Map<Long, Integer> postDocumentConversionResults(@InjectParam List<AttachmentDesc> attachmentIds) {
        return this.getConversionResults(attachmentIds, ConversionType.DOCUMENT);
    }

    @Path(value="convertHD/{attachmentId}/{version}")
    @GET
    @ConversionPath(value=ConversionType.DOCUMENT_HD)
    @CheckIfServiceIsEnabled
    public Response getDocumentStreamHD(@PathParam(value="attachmentId") Long attachmentId, @PathParam(value="version") Integer version, @HeaderParam(value="Range") String rangeHeader) {
        return this.getStream(attachmentId, version, ConversionType.DOCUMENT_HD, rangeHeader);
    }

    @Path(value="convertHD/{attachmentId}/{version}")
    @HEAD
    @ConversionPath(value=ConversionType.DOCUMENT_HD)
    @CheckIfServiceIsEnabled
    public Response getDocumentConversionResultHD(@PathParam(value="attachmentId") Long attachmentId, @PathParam(value="version") Integer version) {
        return this.getConversionResult(attachmentId, version, ConversionType.DOCUMENT_HD, true);
    }

    @Path(value="convertHD/results")
    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @ConversionPath(value=ConversionType.DOCUMENT_HD)
    @CheckIfServiceIsEnabled
    public Map<Long, Integer> postDocumentConversionResultsHD(@InjectParam List<AttachmentDesc> attachmentIds) {
        return this.getConversionResults(attachmentIds, ConversionType.DOCUMENT_HD);
    }

    @Path(value="poster/{attachmentId}/{version}")
    @GET
    @ConversionPath(value=ConversionType.POSTER)
    @CheckIfServiceIsEnabled
    public Response getPosterStream(@PathParam(value="attachmentId") Long attachmentId, @PathParam(value="version") Integer version, @HeaderParam(value="Range") String rangeHeader) {
        return this.getStream(attachmentId, version, ConversionType.POSTER, rangeHeader);
    }

    @Path(value="poster/{attachmentId}/{version}")
    @HEAD
    @ConversionPath(value=ConversionType.POSTER)
    @CheckIfServiceIsEnabled
    public Response getPosterConversionResult(@PathParam(value="attachmentId") Long attachmentId, @PathParam(value="version") Integer version) {
        return this.getConversionResult(attachmentId, version, ConversionType.POSTER, false);
    }

    @Path(value="poster/results")
    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @ConversionPath(value=ConversionType.POSTER)
    @CheckIfServiceIsEnabled
    public Map<Long, Integer> postPosterConversionResults(@InjectParam List<AttachmentDesc> attachmentIds) {
        return this.getConversionResults(attachmentIds, ConversionType.POSTER);
    }

    @Path(value="posterHD/{attachmentId}/{version}")
    @GET
    @ConversionPath(value=ConversionType.POSTER_HD)
    @CheckIfServiceIsEnabled
    public Response getPosterStreamHD(@PathParam(value="attachmentId") Long attachmentId, @PathParam(value="version") Integer version, @HeaderParam(value="Range") String rangeHeader) {
        return this.getStream(attachmentId, version, ConversionType.POSTER_HD, rangeHeader);
    }

    @Path(value="posterHD/{attachmentId}/{version}")
    @HEAD
    @ConversionPath(value=ConversionType.POSTER_HD)
    @CheckIfServiceIsEnabled
    public Response getPosterConversionResultHD(@PathParam(value="attachmentId") Long attachmentId, @PathParam(value="version") Integer version) {
        return this.getConversionResult(attachmentId, version, ConversionType.POSTER_HD, false);
    }

    @Path(value="posterHD/results")
    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @ConversionPath(value=ConversionType.POSTER_HD)
    @CheckIfServiceIsEnabled
    public Map<Long, Integer> postPosterConversionResultsHD(@InjectParam List<AttachmentDesc> attachmentIds) {
        return this.getConversionResults(attachmentIds, ConversionType.POSTER_HD);
    }

    @Path(value="thumbnail/{attachmentId}/{version}")
    @GET
    @ConversionPath(value=ConversionType.THUMBNAIL)
    @CheckIfServiceIsEnabled
    public Response getThumbnailStream(@PathParam(value="attachmentId") Long attachmentId, @PathParam(value="version") Integer version, @HeaderParam(value="Range") String rangeHeader) {
        return this.getStream(attachmentId, version, ConversionType.THUMBNAIL, rangeHeader);
    }

    @Path(value="thumbnail/{attachmentId}/{version}")
    @HEAD
    @ConversionPath(value=ConversionType.THUMBNAIL)
    @CheckIfServiceIsEnabled
    public Response getThumbnailConversionResult(@PathParam(value="attachmentId") Long attachmentId, @PathParam(value="version") Integer version) {
        return this.getConversionResult(attachmentId, version, ConversionType.THUMBNAIL, false);
    }

    @Path(value="thumbnail/results")
    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @ConversionPath(value=ConversionType.THUMBNAIL)
    @CheckIfServiceIsEnabled
    public Map<Long, Integer> postThumbnailConversionResults(@InjectParam List<AttachmentDesc> attachmentIds) {
        return this.getConversionResults(attachmentIds, ConversionType.THUMBNAIL);
    }

    @VisibleForTesting
    Response getStream(Long attachmentId, Integer version, ConversionType conversionType, @Nullable String rangeHeader) {
        ConversionData fileData;
        Attachment attachment = this.getAttachment(attachmentId, version, true);
        ConversionResult conversionResult = this.conversionManager.getConversionResult(attachment, conversionType);
        try {
            fileData = conversionResult.getConversionData(Optional.ofNullable(rangeHeader));
        }
        catch (RangeNotSatisfiableException e) {
            return Response.status((int)416).entity((Object)"Invalid byte range specified").build();
        }
        catch (FileNotFoundException e) {
            return Response.status((int)conversionResult.getConversionStatus().getStatus()).build();
        }
        if (rangeHeader == null) {
            return Response.ok((Object)fileData.getStreamingOutput(), (String)fileData.getContentType()).header("Content-Length", (Object)fileData.getContentLength()).header("Accept-Ranges", (Object)"bytes").build();
        }
        Response.ResponseBuilder builder = Response.status((int)206).type(fileData.getContentType()).entity((Object)fileData.getStreamingOutput()).header("Content-Length", (Object)fileData.getContentLength()).header("Accept-Ranges", (Object)"bytes");
        fileData.getContentRange().ifPresent(range -> builder.header("Content-Range", range));
        return builder.build();
    }

    @VisibleForTesting
    Response getConversionResult(Long attachmentId, Integer version, ConversionType conversionType, boolean ignoreMediaTypeErrors) {
        Attachment attachment = this.getAttachment(attachmentId, version, true);
        ConversionResult conversionResult = this.conversionManager.getConversionResult(attachment, conversionType);
        if (conversionResult.getConversionStatus() != ConversionStatus.CONVERTED) {
            return Response.status((int)conversionResult.getConversionStatus().getStatus()).build();
        }
        Optional<String> contentType = conversionResult.getContentType();
        if (!contentType.isPresent()) {
            if (ignoreMediaTypeErrors) {
                contentType = Optional.of("application/octet-stream");
            } else {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Cannot convert").build();
            }
        }
        ConversionStatus conversionStatus = conversionResult.getConversionStatus();
        return Response.status((int)conversionStatus.getStatus()).type(contentType.get()).build();
    }

    private Map<Long, Integer> getConversionResults(List<AttachmentDesc> attachmentIds, ConversionType conversionType) {
        HashMap<Long, Integer> result = new HashMap<Long, Integer>();
        for (AttachmentDesc attachmentDesc : attachmentIds) {
            Long id = attachmentDesc.id;
            Attachment attachment = this.getAttachment(id, attachmentDesc.v, false);
            if (attachment == null) {
                result.put(id, ConversionStatus.ERROR.getStatus());
                continue;
            }
            ConversionResult conversionResult = this.conversionManager.getConversionResult(attachment, conversionType);
            result.put(id, conversionResult.getConversionStatus().getStatus());
        }
        return result;
    }

    private static WebApplicationException newWebException(Response.Status status, String message) {
        return new WebApplicationException(Response.status((Response.Status)status).entity((Object)message).type("text/plain").build());
    }

    private static final class AttachmentDesc {
        @FormParam(value="id")
        public Long id;
        @FormParam(value="v")
        public Integer v;

        private AttachmentDesc() {
        }
    }
}

