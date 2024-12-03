/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.core.datetime.RequestTimeThreadLocal
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.attachments;

import bucket.core.actions.PaginationSupport;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.extra.attachments.SpaceAttachments;
import com.atlassian.confluence.extra.attachments.SpaceAttachmentsUtils;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceAttachmentsMacro
extends BaseMacro
implements Macro {
    private static final Logger logger = LoggerFactory.getLogger(SpaceAttachmentsMacro.class);
    private static final String SPACE_KEY = "space";
    private static final String SHOW_FILTER_KEY = "showFilter";
    private final VelocityHelperService velocityHelperService;
    private final SpaceAttachmentsUtils spaceAttachmentsUtils;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final FormatSettingsManager formatSettingsManager;
    private final UserAccessor userAccessor;
    private PaginationSupport<Attachment> paginationSupport = new PaginationSupport(20);

    public SpaceAttachmentsMacro(@ComponentImport VelocityHelperService velocityHelperService, SpaceAttachmentsUtils spaceAttachmentsUtils, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport SpaceManager spaceManager, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport UserAccessor userAccessor, @ComponentImport PermissionManager permissionManager) {
        this.velocityHelperService = velocityHelperService;
        this.spaceAttachmentsUtils = spaceAttachmentsUtils;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.spaceManager = spaceManager;
        this.formatSettingsManager = formatSettingsManager;
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        String spaceKey = this.getSpaceKey(conversionContext.getEntity(), parameters);
        boolean showFilter = Boolean.valueOf(StringUtils.isBlank((CharSequence)parameters.get(SHOW_FILTER_KEY)) ? "true" : parameters.get(SHOW_FILTER_KEY));
        if (this.spaceManager.getSpace(spaceKey) == null || StringUtils.isBlank((CharSequence)spaceKey)) {
            return RenderUtils.blockError((String)this.getI18NBean().getText("confluence.extra.attachments.error.spacenotfound", Collections.singletonList(HtmlUtil.htmlEncode((String)spaceKey))), (String)"");
        }
        if (this.spaceManager.getSpace(spaceKey) != null && !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)this.spaceManager.getSpace(spaceKey))) {
            return RenderUtils.blockError((String)this.getI18NBean().getText("confluence.extra.attachments.error.user.not.authorized", (Object[])new String[]{HtmlUtil.htmlEncode((String)(AuthenticatedUserThreadLocal.get() == null ? this.getI18NBean().getText("anonymous.name") : AuthenticatedUserThreadLocal.getUsername()))}), (String)"");
        }
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        try {
            SpaceAttachments spaceAttachments = this.spaceAttachmentsUtils.getAttachmentList(spaceKey, 1, 0, 0, "date", null, null);
            ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
            contextMap.put("latestVersionsOfAttachments", spaceAttachments.getAttachmentList());
            contextMap.put("totalAttachments", spaceAttachments.getTotalAttachments());
            contextMap.put("totalPage", spaceAttachments.getTotalPage());
            contextMap.put("spaceKey", spaceKey);
            contextMap.put("title", conversionContext.getEntity().getTitle());
            contextMap.put("pageNumber", 1);
            contextMap.put("sortBy", "date");
            contextMap.put(SHOW_FILTER_KEY, showFilter);
            contextMap.put("allowFilterByFileExtension", spaceAttachments.getAttachmentList() != null && !spaceAttachments.getAttachmentList().isEmpty());
            contextMap.put("paginationSupport", this.paginationSupport);
            contextMap.put("remoteUser", remoteUser);
            contextMap.put("showAttachmentsNotFound", true);
            if (spaceAttachments.getAttachmentList().isEmpty()) {
                contextMap.put("messageKey", "attachments.no.attachments.to.space");
                contextMap.put("messageParameter", new String[]{spaceKey});
            }
            ConfluenceUserPreferences pref = this.userAccessor.getConfluenceUserPreferences((User)AuthenticatedUserThreadLocal.get());
            contextMap.put("friendlyDateFormatter", new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), pref.getDateFormatter(this.formatSettingsManager, this.localeManager)));
        }
        catch (InvalidSearchException e) {
            throw new MacroExecutionException((Throwable)e);
        }
        return this.velocityHelperService.getRenderedTemplate("templates/extra/attachments/spaceattachments.vm", contextMap);
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public boolean hasBody() {
        return false;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    private String getSpaceKey(ContentEntityObject contentObject, Map<String, String> params) {
        String spaceKey = params.get(SPACE_KEY);
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            if (contentObject instanceof SpaceContentEntityObject) {
                spaceKey = ((SpaceContentEntityObject)contentObject).getSpaceKey();
            } else if (contentObject instanceof Draft) {
                spaceKey = ((Draft)contentObject).getDraftSpaceKey();
            } else if (contentObject instanceof Comment) {
                ContentEntityObject commentOwner = ((Comment)contentObject).getContainer();
                spaceKey = this.getSpaceKey(commentOwner, params);
            }
        }
        if (spaceKey == null && logger.isDebugEnabled()) {
            logger.debug(String.format("Could not retrieve space key from content object: %s", contentObject));
        }
        return spaceKey;
    }
}

