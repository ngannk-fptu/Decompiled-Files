/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.SavableAttachment
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.multipart.FilePart
 *  com.atlassian.plugins.rest.common.multipart.MultipartConfigClass
 *  com.atlassian.plugins.rest.common.multipart.MultipartFormParam
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.net.MediaType
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.rest;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.CustomEmojiDeletedByAdminAuditEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.CustomEmojiDeletedByUserAuditEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.CustomEmojiUploadedAuditEvent;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.entity.ConfluenceCustomEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.entity.CustomEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.exception.EmoticonException;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.EmoticonModel;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.EmoticonsResource;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.interceptor.CustomEmojisPermissionInterceptor;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.interceptor.CustomEmojisUploadPermission;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.multipart.ConfluenceEmojiMultipartConfig;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.CustomEmoticonService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartConfigClass;
import com.atlassian.plugins.rest.common.multipart.MultipartFormParam;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import com.sun.jersey.spi.container.ResourceFilters;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/custom")
@InterceptorChain(value={CustomEmojisPermissionInterceptor.class})
public class CustomEmoticonResource {
    private static final Logger log = LoggerFactory.getLogger(EmoticonsResource.class);
    private static final String INVALID_FILE_TYPE = "INVALID_FILE_TYPE";
    private static final String EMPTY_SHORTCUT_NAME = "EMPTY_SHORTCUT_OR_NAME";
    private static final long MAX_FILENAME_LENGTH = 50L;
    private final CustomEmoticonService customEmoticonService;
    private final EventPublisher eventPublisher;

    public CustomEmoticonResource(@Qualifier(value="customEmoticonService") CustomEmoticonService customEmoticonService, @ComponentImport EventPublisher eventPublisher) {
        this.customEmoticonService = customEmoticonService;
        this.eventPublisher = eventPublisher;
    }

    @POST
    @Consumes(value={"multipart/form-data"})
    @Path(value="/")
    @CustomEmojisUploadPermission
    @MultipartConfigClass(value=ConfluenceEmojiMultipartConfig.class)
    @PublicApi
    public Response create(@MultipartFormParam(value="shortcut") FilePart shortcutParam, @MultipartFormParam(value="name") FilePart nameParam, @MultipartFormParam(value="file") FilePart filePart) throws EmoticonException, IOException {
        if (!this.isFileTypeAValidImageType(filePart.getContentType())) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)INVALID_FILE_TYPE).build();
        }
        Optional<String> shortcut = CustomEmoticonResource.getUTF8String(shortcutParam);
        Optional<String> name = CustomEmoticonResource.getUTF8String(nameParam);
        if (!shortcut.isPresent() || !name.isPresent() || shortcut.get() == null || name.get() == null || StringUtils.isEmpty((CharSequence)name.get().trim()) || StringUtils.isEmpty((CharSequence)shortcut.get().trim())) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)EMPTY_SHORTCUT_NAME).build();
        }
        ConfluenceCustomEmoticon emoticon = ConfluenceCustomEmoticon.newlyConfluenceCustomEmoticon(shortcut.get().trim(), name.get().trim());
        try (InputStream filePartInputStream = filePart.getInputStream();){
            SavableAttachment savableAttachment = this.createSavableAttachment(filePart, filePartInputStream, shortcut.get().trim());
            this.customEmoticonService.create(emoticon, savableAttachment);
            this.eventPublisher.publish((Object)new CustomEmojiUploadedAuditEvent(emoticon.getShortcut(), emoticon.getName()));
            Response response = Response.status((Response.Status)Response.Status.OK).entity((Object)("Successfully added emoji with shortcut: " + shortcut.get())).build();
            return response;
        }
    }

    public static Optional<String> getUTF8String(FilePart param) {
        Optional<String> optional;
        block8: {
            InputStream is = param.getInputStream();
            try {
                optional = Optional.of(IOUtils.toString((InputStream)is, (Charset)StandardCharsets.UTF_8));
                if (is == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    log.error("Error decoding a stream of extracted text", (Throwable)e);
                    return Optional.empty();
                }
            }
            is.close();
        }
        return optional;
    }

    @GET
    @AnonymousAllowed
    @Produces(value={"application/json"})
    @Path(value="/")
    public Map<String, Object> getCustom(@DefaultValue(value="") @QueryParam(value="shortcut") String shortcut) {
        AtlaskitEmoticonModel.Custom[] customEmoticons = new AtlaskitEmoticonModel.Custom[]{};
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS");
        if (!AuthenticatedUserThreadLocal.isAnonymousUser()) {
            Stream<CustomEmoticon> customEmojiStream = StreamSupport.stream(this.customEmoticonService.list().spliterator(), false);
            if (!StringUtils.isEmpty((CharSequence)shortcut)) {
                customEmojiStream = customEmojiStream.filter(emoticonModel -> "".equals(shortcut) || shortcut.equals(emoticonModel.getShortcut()));
            }
            customEmoticons = (AtlaskitEmoticonModel.Custom[])customEmojiStream.map(EmoticonModel.fromCustomEmoticon()).sorted(Comparator.comparing(EmoticonModel::getCreatedDate)).map(emoticonModel -> new AtlaskitEmoticonModel.Custom(emoticonModel.getShortcut(), emoticonModel.getName(), ":" + emoticonModel.getShortcut() + ":", ":" + emoticonModel.getShortcut() + ":", "SITE", "CUSTOM", -1000L, new AtlaskitEmoticonModel.ImageRepresentation(64, 64, emoticonModel.getUrl()), true, emoticonModel.getCreatorUserId(), emoticonModel.getCreatedDate() != null ? simpleDateFormat.format(emoticonModel.getCreatedDate()) : "", null, null)).toArray(AtlaskitEmoticonModel.Custom[]::new);
        }
        return ImmutableMap.builder().put((Object)"emojis", (Object)customEmoticons).build();
    }

    @DELETE
    @ResourceFilters(value={AdminOnlyResourceFilter.class})
    public Response delete(@QueryParam(value="shortcut") String shortcut) throws ServiceException {
        if (ObjectUtils.isEmpty((Object)shortcut)) {
            throw new IllegalArgumentException("shortcut can't be empty");
        }
        Optional<CustomEmoticon> customEmoticon = this.customEmoticonService.findByShortcut(shortcut).stream().findFirst();
        if (!customEmoticon.isPresent()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)"No emoji with provided shortcut").build();
        }
        CustomEmoticon emoticon = customEmoticon.get();
        this.customEmoticonService.delete(shortcut);
        this.eventPublisher.publish((Object)new CustomEmojiDeletedByAdminAuditEvent(shortcut, emoticon.getName(), emoticon.getCreatorUserId()));
        return Response.status((Response.Status)Response.Status.OK).entity((Object)"Successfully removed emoji").build();
    }

    @DELETE
    @ResourceFilters(value={AdminOnlyResourceFilter.class})
    @Path(value="admin/invalid")
    public Response deleteInvalid() {
        this.customEmoticonService.cleanupInvalidEmoticon();
        return Response.status((Response.Status)Response.Status.OK).entity((Object)"Successfully removed invalid emoji").build();
    }

    @DELETE
    @Path(value="/delete-your-upload")
    public Response deleteYourUpload(@QueryParam(value="shortcut") String shortcut) throws ServiceException {
        if (ObjectUtils.isEmpty((Object)shortcut)) {
            throw new IllegalArgumentException("shortcut can't be empty");
        }
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        if (confluenceUser == null) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)"A user is required").build();
        }
        Optional<CustomEmoticon> customEmoticon = this.customEmoticonService.findByShortcut(shortcut).stream().findFirst();
        if (!customEmoticon.isPresent()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)"No emoji with provided shortcut").build();
        }
        if (!confluenceUser.getName().equals(customEmoticon.get().getCreatorUserId())) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)"A user is not the creator").build();
        }
        CustomEmoticon emoticon = customEmoticon.get();
        this.customEmoticonService.delete(shortcut);
        this.eventPublisher.publish((Object)new CustomEmojiDeletedByUserAuditEvent(shortcut, emoticon.getName(), emoticon.getCreatorUserId()));
        return Response.status((Response.Status)Response.Status.OK).entity((Object)"Successfully removed emoji").build();
    }

    private SavableAttachment createSavableAttachment(FilePart filePart, InputStream filePartInputStream, String shortcut) {
        Attachment attachment = new Attachment();
        attachment.setFileName(CustomEmoticonResource.sanitizeFileName(filePart.getName(), shortcut));
        attachment.setFileSize(filePart.getSize());
        attachment.setMediaType(filePart.getContentType());
        return new SavableAttachment(attachment, null, filePartInputStream);
    }

    private boolean isFileTypeAValidImageType(String fileType) {
        return MediaType.GIF.toString().equals(fileType) || MediaType.JPEG.toString().equals(fileType) || MediaType.PNG.toString().equals(fileType);
    }

    public static String sanitizeFileName(String fileName, String shortcut) {
        String fileNameWithoutExtension = FilenameUtils.getBaseName((String)fileName);
        String fileNameExtension = FilenameUtils.getExtension((String)fileName);
        if (fileNameWithoutExtension == null || fileNameWithoutExtension.isEmpty() || (long)((String)fileName).length() > 50L) {
            fileName = shortcut + "." + fileNameExtension;
        }
        return HtmlUtil.htmlEncode((String)fileName);
    }
}

