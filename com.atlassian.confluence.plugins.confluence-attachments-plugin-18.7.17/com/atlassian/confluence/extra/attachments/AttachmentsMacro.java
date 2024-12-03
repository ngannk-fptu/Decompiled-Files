/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.content.render.image.ImageDimensions
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.core.datetime.RequestTimeThreadLocal
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.renderer.ConfluenceRenderContextOutputType
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.AttachmentComparator
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.attachments;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.extra.attachments.ImagePreviewRenderer;
import com.atlassian.confluence.extra.attachments.metrics.AttachmentsMacroMetrics;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.ConfluenceRenderContextOutputType;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.AttachmentComparator;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class AttachmentsMacro
extends BaseMacro
implements Macro,
EditorImagePlaceholder {
    private final AttachmentManager attachmentManager;
    private final PermissionManager permissionManager;
    private final VelocityHelperService velocityHelperService;
    private final PageManager pageManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final FormatSettingsManager formatSettingsManager;
    private final UserAccessor userAccessor;
    private final PluginAccessor pluginAccessor;
    private final ImagePreviewRenderer imagePreviewRenderer;
    private final EventPublisher eventPublisher;
    private final AccessModeService accessModeService;
    private final HttpContext httpContext;
    private static final String SORTORDER_ASCENDING = "ascending";
    private static final String SORTORDER_DESCENDING = "descending";
    private ContentEntityObject contentObject;
    private PageContext pageContext;
    private static final String PLACEHOLDER_IMAGE_PATH = "/download/resources/confluence.extra.attachments/placeholder.png";

    public AttachmentsMacro(@ComponentImport VelocityHelperService velocityHelperService, @ComponentImport PermissionManager permissionManager, @ComponentImport AttachmentManager attachmentManager, @ComponentImport PageManager pageManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport UserAccessor userAccessor, ImagePreviewRenderer imagePreviewRenderer, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport EventPublisher eventPublisher, @ComponentImport AccessModeService accessModeService, @ComponentImport HttpContext httpContext) {
        this.velocityHelperService = velocityHelperService;
        this.permissionManager = permissionManager;
        this.attachmentManager = attachmentManager;
        this.pageManager = pageManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.formatSettingsManager = formatSettingsManager;
        this.userAccessor = userAccessor;
        this.imagePreviewRenderer = imagePreviewRenderer;
        this.pluginAccessor = pluginAccessor;
        this.eventPublisher = eventPublisher;
        this.accessModeService = accessModeService;
        this.httpContext = httpContext;
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map<String, String> macroParameters, String ignoredMacroBody, ConversionContext conversionContext) {
        AttachmentsMacroMetrics metrics = new AttachmentsMacroMetrics();
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        String pageTitle = macroParameters.get("page");
        if (StringUtils.isNotBlank((CharSequence)pageTitle)) {
            this.contentObject = this.getPage(conversionContext.getPageContext(), pageTitle);
            this.pageContext = new PageContext(this.contentObject);
            if (this.contentObject == null) {
                return RenderUtils.blockError((String)this.getI18NBean().getText("confluence.extra.attachments.error.pagenotfound", Collections.singletonList(HtmlUtil.htmlEncode((String)pageTitle))), (String)"");
            }
            if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)this.contentObject)) {
                return RenderUtils.blockError((String)this.getI18NBean().getText("confluence.extra.attachments.error.nopagepermission", Collections.singletonList(HtmlUtil.htmlEncode((String)pageTitle))), (String)"");
            }
        } else {
            this.contentObject = conversionContext.getEntity();
            this.pageContext = conversionContext.getPageContext();
        }
        if (this.contentObject instanceof Comment) {
            this.contentObject = ((Comment)this.contentObject).getContainer();
        }
        HttpServletRequest servletRequest = this.httpContext.getRequest();
        Sorter sorter = Sorter.build(macroParameters, servletRequest);
        List<Attachment> attachments = this.findAttachments(macroParameters, sorter, metrics);
        metrics.templateModelBuildStart();
        Map<String, Object> contextMap = this.buildTemplateModel(macroParameters, conversionContext, (User)user, attachments, sorter, servletRequest);
        metrics.templateModelBuildFinish();
        metrics.templateRenderStart();
        String renderedOutput = this.velocityHelperService.getRenderedTemplate("templates/extra/attachments/attachmentsmacro.vm", contextMap);
        metrics.templateRenderFinish();
        metrics.publishTo(this.eventPublisher);
        return renderedOutput;
    }

    private Map<String, Object> buildTemplateModel(Map<String, String> parameters, ConversionContext conversionContext, User user, List<Attachment> attachments, Sorter sorter, HttpServletRequest servletRequest) {
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        contextMap.put("latestVersionsOfAttachments", attachments);
        contextMap.put("hasAttachFilePermissions", this.hasAttachFilePermissions(user));
        contextMap.put("page", this.contentObject);
        contextMap.put("macro", this);
        contextMap.put("old", AttachmentsMacro.getBooleanParameter(parameters, "old", true));
        contextMap.put("preview", AttachmentsMacro.getBooleanParameter(parameters, "preview", true));
        contextMap.put("upload", AttachmentsMacro.getBooleanParameter(parameters, "upload", true));
        contextMap.put("renderedInPreview", "preview".equals(conversionContext.getOutputType()));
        contextMap.put("max", 5);
        contextMap.put("remoteUser", user);
        contextMap.put("attachmentsMacroHelper", this);
        contextMap.put("showActions", !"pdf".equals(conversionContext.getOutputType()));
        contextMap.put("outputType", conversionContext.getOutputType());
        contextMap.put("macroParams", AttachmentsMacro.getMacroParametersWithSortByParamReadFromRequest(parameters, servletRequest));
        contextMap.put("uploadIFrameName", RandomStringUtils.randomAlphanumeric((int)64));
        contextMap.put("sortBy", sorter.sortBy);
        contextMap.put("sortOrder", sorter.sortOrder);
        contextMap.put("newSortOrder", SORTORDER_DESCENDING.equals(sorter.sortOrder) ? SORTORDER_ASCENDING : SORTORDER_DESCENDING);
        ConfluenceUserPreferences pref = this.userAccessor.getConfluenceUserPreferences(user);
        contextMap.put("friendlyDateFormatter", new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), pref.getDateFormatter(this.formatSettingsManager, this.localeManager)));
        contextMap.put("pdlEnabled", Long.parseLong(GeneralUtil.getBuildNumber()) >= 4000L);
        contextMap.put("spaceKey", conversionContext.getSpaceKey());
        contextMap.put("previewsEnabled", this.pluginAccessor.isPluginEnabled("com.atlassian.confluence.plugins.confluence-previews"));
        contextMap.put("accessMode", this.accessModeService.getAccessMode().name());
        return contextMap;
    }

    private List<Attachment> findAttachments(Map<String, String> macroParameters, Sorter sorter, AttachmentsMacroMetrics metrics) {
        if (this.contentObject != null && this.contentObject.getId() != 0L) {
            metrics.searchAttachmentsStart();
            ArrayList attachments = Lists.newArrayList((Iterable)this.attachmentManager.getLatestVersionsOfAttachments(this.contentObject));
            metrics.searchAttachmentsFinish(attachments.size());
            metrics.filterAndSortAttachmentsStart();
            List<Attachment> filteredAndSortedAttachments = attachments.stream().filter(AttachmentsMacro.attachmentFilenameFilter(macroParameters)).filter(AttachmentsMacro.requiredLabelsFilter(macroParameters)).sorted(sorter).collect(Collectors.toList());
            metrics.filterAndSortAttachmentsFinish(filteredAndSortedAttachments.size());
            return filteredAndSortedAttachments;
        }
        return Collections.emptyList();
    }

    private static Predicate<Attachment> attachmentFilenameFilter(Map<String, String> macroParameters) {
        String fileNamePatterns = macroParameters.get("patterns");
        if (fileNamePatterns != null) {
            List<Pattern> patterns = AttachmentsMacro.compileFilenamePatterns(fileNamePatterns);
            return attachment -> patterns.stream().anyMatch(pattern -> pattern.matcher(attachment.getFileName()).matches());
        }
        return x -> true;
    }

    private static Predicate<Attachment> requiredLabelsFilter(Map<String, String> macroParameters) {
        Collection<String> requiredLabels = AttachmentsMacro.getRequiredLabels(macroParameters);
        if (!requiredLabels.isEmpty()) {
            return attachment -> AttachmentsMacro.attachmentHasAllRequiredLabels(requiredLabels, attachment);
        }
        return x -> true;
    }

    private static Collection<String> getRequiredLabels(Map<String, String> macroParameters) {
        String labelsString = macroParameters.get("labels");
        return !StringUtils.isBlank((CharSequence)labelsString) ? Sets.newHashSet((Object[])labelsString.split(",")) : Collections.emptySet();
    }

    private static boolean attachmentHasAllRequiredLabels(Collection<String> requiredLabels, Attachment attachment) {
        return !attachment.getLabels().isEmpty() && requiredLabels.stream().map(String::trim).allMatch(requiredLabel -> AttachmentsMacro.hasLabel(attachment, requiredLabel));
    }

    private static boolean hasLabel(Attachment attachment, String requiredLabel) {
        return attachment.getLabels().stream().anyMatch(label -> requiredLabel.equals(label.getName()));
    }

    private static List<Pattern> compileFilenamePatterns(String fileNamePatterns) {
        return Arrays.stream(fileNamePatterns.split(",")).map(String::trim).map(pattern -> pattern.startsWith("*") ? "." + pattern : pattern).map(Pattern::compile).collect(Collectors.toList());
    }

    private Boolean hasAttachFilePermissions(User remoteUser) {
        try {
            return this.permissionManager.hasCreatePermission(remoteUser, (Object)this.contentObject, Attachment.class);
        }
        catch (IllegalStateException ex) {
            return false;
        }
    }

    public List<Attachment> getAllAttachmentVersions(Attachment attachment) {
        return this.attachmentManager.getAllVersions(attachment);
    }

    public boolean willBeRendered(Attachment attachment) {
        return this.imagePreviewRenderer.willBeRendered(attachment);
    }

    public WebInterfaceContext getWebInterfaceContext(WebInterfaceContext context, Attachment attachment) {
        DefaultWebInterfaceContext defaultContext = DefaultWebInterfaceContext.copyOf((WebInterfaceContext)context);
        defaultContext.setAttachment(attachment);
        if (this.contentObject instanceof AbstractPage || ConfluenceRenderContextOutputType.PAGE_GADGET.toString().equals(this.pageContext.getOutputType())) {
            defaultContext.setPage((AbstractPage)this.contentObject);
        }
        return defaultContext;
    }

    private static Boolean getBooleanParameter(Map<String, String> parameters, String name, boolean defaultValue) {
        return Optional.ofNullable(parameters.get(name)).map(Boolean::valueOf).orElse(defaultValue);
    }

    private static Map<String, String> getMacroParametersWithSortByParamReadFromRequest(Map<String, String> macroParams, HttpServletRequest servletRequest) {
        HashMap<String, String> combinedParams = new HashMap<String, String>(macroParams);
        Optional.ofNullable(servletRequest).map(req -> req.getParameter("sortBy")).filter(StringUtils::isNotBlank).ifPresent(sortBy -> combinedParams.put("sortBy", (String)sortBy));
        return combinedParams;
    }

    public String[] getAttachmentDetails(Attachment attachment) {
        return new String[]{StringEscapeUtils.escapeXml10((String)attachment.getFileName()), String.valueOf(attachment.getVersion())};
    }

    public String execute(Map parameters, String body, RenderContext renderContext) {
        return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private ContentEntityObject getPage(PageContext context, String pageTitleToRetrieve) {
        if (StringUtils.isBlank((CharSequence)pageTitleToRetrieve)) {
            return context.getEntity();
        }
        String spaceKey = context.getSpaceKey();
        String pageTitle = pageTitleToRetrieve;
        int colonIndex = pageTitleToRetrieve.indexOf(":");
        if (colonIndex != -1 && colonIndex != pageTitleToRetrieve.length() - 1) {
            spaceKey = pageTitleToRetrieve.substring(0, colonIndex);
            pageTitle = pageTitleToRetrieve.substring(colonIndex + 1);
        }
        return this.pageManager.getPage(spaceKey, pageTitle);
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> stringStringMap, ConversionContext conversionContext) {
        return new DefaultImagePlaceholder(PLACEHOLDER_IMAGE_PATH, true, new ImageDimensions(310, 172));
    }

    private static class Sorter
    implements Comparator<Attachment> {
        @Nonnull
        final String sortBy;
        @Nullable
        final String sortOrder;

        private Sorter(String sortBy, String sortOrder) {
            this.sortBy = sortBy;
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(Attachment o1, Attachment o2) {
            return new AttachmentComparator(this.sortBy, AttachmentsMacro.SORTORDER_DESCENDING.equals(this.sortOrder)).compare((Object)o1, (Object)o2);
        }

        static Sorter build(Map<String, String> macroParameters, HttpServletRequest servletRequest) {
            String sortBy = macroParameters.get("sortBy");
            String sortOrder = macroParameters.get("sortOrder");
            if (servletRequest != null) {
                String requestSortOrder;
                String requestSortBy = servletRequest.getParameter("sortBy");
                if (requestSortBy != null) {
                    sortBy = requestSortBy;
                }
                if ((requestSortOrder = servletRequest.getParameter("sortOrder")) != null) {
                    sortOrder = requestSortOrder;
                }
            }
            if (sortBy == null) {
                sortBy = "date";
            }
            return new Sorter(sortBy, sortOrder);
        }
    }
}

