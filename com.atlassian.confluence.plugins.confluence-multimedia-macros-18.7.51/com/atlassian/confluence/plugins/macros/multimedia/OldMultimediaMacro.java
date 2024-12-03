/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.embedded.EmbeddedObject
 *  com.atlassian.confluence.renderer.embedded.EmbeddedResourceRenderer
 *  com.atlassian.confluence.renderer.embedded.EmbeddedResourceRendererManager
 *  com.atlassian.confluence.servlet.download.DispositionType
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.AttachmentMimeTypeTranslator
 *  com.atlassian.http.mime.ContentDispositionHeaderGuesser
 *  com.atlassian.http.mime.DownloadPolicy
 *  com.atlassian.http.mime.DownloadPolicyProvider
 *  com.atlassian.http.mime.HostileExtensionDetector
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.multimedia;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.embedded.EmbeddedObject;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceRendererManager;
import com.atlassian.confluence.servlet.download.DispositionType;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.AttachmentMimeTypeTranslator;
import com.atlassian.http.mime.ContentDispositionHeaderGuesser;
import com.atlassian.http.mime.DownloadPolicy;
import com.atlassian.http.mime.DownloadPolicyProvider;
import com.atlassian.http.mime.HostileExtensionDetector;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OldMultimediaMacro
extends BaseMacro {
    public static final Logger log = LoggerFactory.getLogger(OldMultimediaMacro.class);
    public static final String SPACE_KEY = "space";
    public static final String PAGE_KEY = "page";
    public static final String NAME_KEY = "name";
    public static final String DATE_KEY = "date";
    public static final String WIDTH_KEY = "width";
    public static final String HEIGHT_KEY = "height";
    public static final String AUTO_PLAY = "autostart";
    public static final String BLOGPOST_DATE_FIELD_FORMAT = "MM/dd/yyyy";
    private final EmbeddedResourceRendererManager embeddedResourceRendererManager;
    private final PageManager pageManager;
    private final AttachmentManager attachmentManager;
    private final SettingsManager settingsManager;
    private final AttachmentMimeTypeTranslator mimeTypeTranslator;

    public OldMultimediaMacro(EmbeddedResourceRendererManager embeddedResourceRendererManager, PageManager pageManager, AttachmentManager attachmentManager, SettingsManager settingsManager, AttachmentMimeTypeTranslator mimeTypeTranslator) {
        this.pageManager = pageManager;
        this.embeddedResourceRendererManager = embeddedResourceRendererManager;
        this.attachmentManager = attachmentManager;
        this.settingsManager = settingsManager;
        this.mimeTypeTranslator = mimeTypeTranslator;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        String pageName = (String)parameters.get(PAGE_KEY);
        String space = (String)parameters.get(SPACE_KEY);
        String fileName = (String)parameters.get(NAME_KEY);
        String date = (String)parameters.get(DATE_KEY);
        String width = (String)parameters.get(WIDTH_KEY);
        String height = (String)parameters.get(HEIGHT_KEY);
        Attachment attachment = this.getAttachment(space, pageName, date, fileName, (PageContext)renderContext);
        if (attachment == null) {
            throw new MacroException("Cannot find attachment '" + fileName + "'");
        }
        String contentType = attachment.getMediaType();
        if (this.mimeTypeTranslator != null) {
            contentType = this.mimeTypeTranslator.resolveMimeType(attachment.getFileName(), contentType);
        }
        if (!contentType.equals("application/x-shockwave-flash") && !this.isAttachmentFileSentInlineWithCurrentAttachmentSecurityLevel(attachment, contentType)) {
            throw new MacroException("Unable to display content of type '" + attachment.getMediaType() + "' with the current attachment security level");
        }
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put(WIDTH_KEY, width);
        properties.put(HEIGHT_KEY, height);
        if (parameters.containsKey(AUTO_PLAY)) {
            properties.put(AUTO_PLAY, (String)parameters.get(AUTO_PLAY));
        } else {
            properties.put(AUTO_PLAY, "false");
        }
        EmbeddedObject embeddedObject = new EmbeddedObject(attachment, properties);
        EmbeddedResourceRenderer renderer = this.embeddedResourceRendererManager.getResourceRenderer(embeddedObject);
        if (renderer == null) {
            throw new MacroException("Unable to display content of type '" + embeddedObject.getFileExtension() + "'");
        }
        return renderer.renderResource(embeddedObject, renderContext);
    }

    private boolean isAttachmentFileSentInlineWithCurrentAttachmentSecurityLevel(Attachment attachment, String contentType) {
        HostileExtensionDetector hostileExtensionDetector = new HostileExtensionDetector();
        final DownloadPolicy downloadPolicy = this.settingsManager.getGlobalSettings().getAttachmentSecurityLevel().getDownloadPolicyLevel();
        DownloadPolicyProvider downloadPolicyProvider = new DownloadPolicyProvider(){

            public DownloadPolicy getPolicy() {
                return downloadPolicy;
            }
        };
        ContentDispositionHeaderGuesser contentDispositionHeaderGuesser = new ContentDispositionHeaderGuesser(downloadPolicyProvider, hostileExtensionDetector);
        String dispositionTypeString = contentDispositionHeaderGuesser.guessContentDispositionHeader(attachment.getFileName(), contentType, "");
        return dispositionTypeString.toUpperCase(Locale.ENGLISH).equals(DispositionType.INLINE.name().toUpperCase(Locale.ENGLISH));
    }

    private Attachment getAttachment(String spaceKey, String pageTitle, String dateString, String fileName, PageContext context) {
        ContentEntityObject ceo = this.getContentEntityObject(spaceKey, pageTitle, dateString, context);
        if (ceo == null) {
            return null;
        }
        ceo = (ContentEntityObject)ceo.getLatestVersion();
        return this.attachmentManager.getAttachment(ceo, HtmlEscaper.escapeAll((String)fileName, (boolean)true));
    }

    private ContentEntityObject getContentEntityObject(String spaceKey, String pageTitle, String dateString, PageContext context) {
        ContentEntityObject contextEntity = context.getEntity();
        if (pageTitle == null) {
            ContentEntityObject ceo = contextEntity;
            if (contextEntity instanceof Comment) {
                ceo = ((Comment)contextEntity).getContainer();
            }
            return ceo;
        }
        if (spaceKey == null) {
            if (contextEntity instanceof Comment) {
                Comment comment = (Comment)contextEntity;
                ContentEntityObject owner = comment.getContainer();
                if (owner instanceof SpaceContentEntityObject) {
                    spaceKey = ((SpaceContentEntityObject)owner).getSpaceKey();
                }
            } else if (contextEntity instanceof SpaceContentEntityObject) {
                SpaceContentEntityObject spaceContentEntityObject = (SpaceContentEntityObject)contextEntity;
                spaceKey = spaceContentEntityObject.getSpaceKey();
            } else if (contextEntity instanceof Draft) {
                Draft draft = (Draft)contextEntity;
                spaceKey = draft.getDraftSpaceKey();
            } else {
                spaceKey = context.getSpaceKey();
            }
        }
        ContentEntityObject ceo = this.getAbstractPage(spaceKey, pageTitle, dateString);
        return ceo;
    }

    private ContentEntityObject getAbstractPage(String spaceKey, String pageTitle, String dateString) {
        Page ceo;
        if (dateString == null) {
            ceo = this.pageManager.getPage(spaceKey, pageTitle);
        } else {
            try {
                Date blogPostCreationDate = new SimpleDateFormat(BLOGPOST_DATE_FIELD_FORMAT).parse(dateString);
                ceo = this.pageManager.getBlogPost(spaceKey, pageTitle, BlogPost.toCalendar((Date)blogPostCreationDate));
            }
            catch (ParseException e) {
                log.error("Error parsing date parameter {} in multimedia macro", (Object)dateString);
                return null;
            }
        }
        return ceo;
    }
}

