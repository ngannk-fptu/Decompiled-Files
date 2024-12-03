/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.service.DraftService
 *  com.atlassian.confluence.content.service.DraftService$DraftType
 *  com.atlassian.confluence.core.AttachmentResource
 *  com.atlassian.confluence.core.Beanable
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.InputStreamAttachmentResource
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.confluence.pages.FileUploadManager
 *  com.atlassian.confluence.pages.attachments.AttachmentDataStreamSizeMismatchException
 *  com.atlassian.core.util.FileSize
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  javax.servlet.ServletInputStream
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.codec.binary.Base64InputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.interceptor.ServletRequestAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.dragdrop;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InputStreamAttachmentResource;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.pages.attachments.AttachmentDataStreamSizeMismatchException;
import com.atlassian.confluence.plugins.dragdrop.service.DragAndDropService;
import com.atlassian.core.util.FileSize;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadAction
extends ConfluenceActionSupport
implements Beanable,
ServletRequestAware {
    private static final Logger log = LoggerFactory.getLogger(UploadAction.class);
    @ComponentImport
    private FileUploadManager fileUploadManager;
    @ComponentImport
    private AttachmentManager attachmentManager;
    private DragAndDropService dragAndDropService;
    @ComponentImport
    private ContentEntityManager contentEntityManager;
    @ComponentImport
    private DraftService draftService;
    private long pageId;
    private long draftId;
    private long dragAndDropEntityId;
    private String filename;
    private String mimeType;
    private long size;
    private boolean minorEdit;
    private boolean withEditorPlaceholder = true;
    private String contentType;
    private boolean isVFMSupported;
    private final Map<String, Object> jsonResult = new HashMap<String, Object>(2);
    private HttpServletRequest httpServletRequest;

    public void validate() {
        long maxUploadSize;
        super.validate();
        if (StringUtils.isBlank((CharSequence)this.filename)) {
            this.addActionError(this.getText("upload.filename.cannot.be.blank"));
        }
        if (this.size > (maxUploadSize = this.settingsManager.getGlobalSettings().getAttachmentMaxSize())) {
            ServletActionContext.getResponse().setStatus(413);
            String error = this.getText("upload.size.limit.exceeded", (List)ImmutableList.of((Object)FileSize.format((long)this.size), (Object)FileSize.format((long)maxUploadSize)));
            this.addActionError(error);
        }
    }

    public boolean isPermitted() {
        if (this.accessModeService.isReadOnlyAccessModeEnabled()) {
            return false;
        }
        return super.isPermitted();
    }

    public String execute() throws Exception {
        try {
            ContentEntityObject content;
            if (this.pageId == 0L && (this.draftId > 0L || this.getAuthenticatedUser() == null)) {
                Draft draft;
                String draftSpaceKey;
                content = this.contentEntityManager.getById(this.draftId);
                if (content == null && this.getAuthenticatedUser() == null) {
                    content = this.draftService.findDraftForEditor(0L, DraftService.DraftType.PAGE, null);
                }
                if (content == null) {
                    String noDraftFoundMessage = "Expecting to find draft for the new piece of content being created but none found.";
                    log.debug("Expecting to find draft for the new piece of content being created but none found. draftId = " + this.draftId);
                    throw new RuntimeException("Expecting to find draft for the new piece of content being created but none found.");
                }
                if (DraftsTransitionHelper.isLegacyDraft((ContentEntityObject)content) && StringUtils.isNotBlank((CharSequence)(draftSpaceKey = (draft = (Draft)content).getDraftSpaceKey())) && draftSpaceKey.toLowerCase().startsWith("%7e")) {
                    draft.setDraftSpaceKey("~" + draftSpaceKey.substring(3));
                }
            } else if (this.pageId != 0L) {
                content = this.contentEntityManager.getById(this.pageId);
            } else if (this.dragAndDropEntityId != 0L) {
                content = this.contentEntityManager.getById(this.dragAndDropEntityId);
            } else {
                throw new RuntimeException("No valid pageId or draftType specified for this action.");
            }
            if (!this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), (Object)content, Attachment.class)) {
                String attachNotPermitted = this.getText("attach.not.permitted");
                this.addActionError(attachNotPermitted);
                log.debug("client attempted to create an attachment but has no permission on page with id {}", (Object)content.getIdAsString());
                ServletActionContext.getResponse().setStatus(403);
                return "error";
            }
            InputStream inStream = this.getStreamForEncoding(this.httpServletRequest);
            this.fileUploadManager.storeResource((AttachmentResource)new InputStreamAttachmentResource(inStream, this.filename, this.mimeType, this.size, null, this.minorEdit), content);
            if (this.withEditorPlaceholder) {
                this.jsonResult.put("htmlForEditor", this.dragAndDropService.getAttachmentEditorHtml(this.filename, content, this.isVFMSupported, this.contentType));
            }
            this.jsonResult.put("data", this.getDetails(this.filename, content));
        }
        catch (AttachmentDataStreamSizeMismatchException e) {
            if (e.getActualSize() < e.getExpectedSize()) {
                String message = this.getText("upload.cancelled", (List)ImmutableList.of((Object)this.filename));
                log.debug(message);
                this.addActionError(message);
            }
            ServletActionContext.getResponse().setStatus(500);
            log.debug("Client attempted to upload a file with a content length smaller than the actual size of the file.");
            return "error";
        }
        catch (XhtmlException e) {
            ServletActionContext.getResponse().setStatus(500);
            this.addActionError(e.getMessage());
            log.error("Failed to generate html to embed dragged resource into content", (Throwable)e);
            return "error";
        }
        catch (RuntimeException e) {
            ServletActionContext.getResponse().setStatus(500);
            this.addActionError(e.getMessage());
            log.error("Failed to save file.", (Throwable)e);
            return "error";
        }
        return "success";
    }

    private InputStream getStreamForEncoding(HttpServletRequest request) throws UnsupportedEncodingException, IOException {
        String encoding = request.getHeader("Content-Encoding");
        ServletInputStream requestStream = request.getInputStream();
        if (encoding == null || encoding.isEmpty()) {
            return requestStream;
        }
        if (encoding.equals("base64")) {
            return new Base64InputStream((InputStream)requestStream);
        }
        throw new UnsupportedEncodingException(encoding + " is not supported");
    }

    private Map<String, String> getDetails(String fileName, ContentEntityObject content) {
        HashMap<String, String> data = new HashMap<String, String>();
        Attachment attachment = this.attachmentManager.getAttachment(content, fileName);
        data.put("id", String.valueOf(attachment.getId()));
        data.put("src", attachment.getDownloadPath());
        data.put("ownerId", String.valueOf(content.getId()));
        return data;
    }

    public Object getBean() {
        return this.jsonResult;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setMinorEdit(boolean minorEdit) {
        this.minorEdit = minorEdit;
    }

    public void setWithEditorPlaceholder(boolean withEditorPlaceholder) {
        this.withEditorPlaceholder = withEditorPlaceholder;
    }

    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public void setFileUploadManager(FileUploadManager fileUploadManager) {
        this.fileUploadManager = fileUploadManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public void setDraftId(long draftId) {
        this.draftId = draftId;
    }

    public void setDragAndDropService(DragAndDropService dragAndDropService) {
        this.dragAndDropService = dragAndDropService;
    }

    public void setDraftService(DraftService draftService) {
        this.draftService = draftService;
    }

    public void setDragAndDropEntityId(long dragAndDropEntityId) {
        this.dragAndDropEntityId = dragAndDropEntityId;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public Map<String, Object> getJsonResult() {
        return this.jsonResult;
    }

    public void setIsVFMSupported(boolean isVFMSupported) {
        this.isVFMSupported = isVFMSupported;
    }
}

