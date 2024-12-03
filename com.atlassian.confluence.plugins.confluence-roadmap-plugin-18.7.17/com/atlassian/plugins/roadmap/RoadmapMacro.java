/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.renderer.ConfluenceRenderContextOutputType
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.UserAgentUtil
 *  com.atlassian.confluence.util.UserAgentUtil$BrowserMajorVersion
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.ConfluenceRenderContextOutputType;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.UserAgentUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.roadmap.PageLinkParser;
import com.atlassian.plugins.roadmap.RoadmapMacroCacheSupplier;
import com.atlassian.plugins.roadmap.TimelinePlannerJsonBuilder;
import com.atlassian.plugins.roadmap.analytics.RoadmapAnalyticObject;
import com.atlassian.plugins.roadmap.models.Bar;
import com.atlassian.plugins.roadmap.models.Lane;
import com.atlassian.plugins.roadmap.models.RoadmapPageLink;
import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.atlassian.plugins.roadmap.renderer.PNGRoadMapRenderer;
import com.atlassian.plugins.roadmap.renderer.SVGRoadMapRenderer;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class RoadmapMacro
implements Macro,
EditorImagePlaceholder {
    private static final String PLACEHOLDER_SERVLET = "/plugins/servlet/roadmap/image/placeholder";
    private static final int PLACEHOLDER_WIDTH_MAX = 1000;
    private static final int PLACEHOLDER_HEIGHT_MAX = 300;
    private static final String PARAM_RENDER_CONTEXT = "renderContext";
    private static final Set<String> PNG_CONTEXTS = ImmutableSet.of((Object)"email", (Object)"pdf", (Object)ConfluenceRenderContextOutputType.PAGE_GADGET.toString());
    private static final Set<String> SVG_CONTEXTS = ImmutableSet.of((Object)"display", (Object)"preview");
    private static final String PARAM_PAGE_LINK = "pagelinks";
    private static final String PARAM_MAP_LINK = "maplinks";
    private static final String MOBILE_OUTPUT_DEVICE_TYPE = "mobile";
    public static final String LINKS_DELIMITER = "~~~~~";
    private static final String REQUIRED_CONTEXT = "roadmap-view-resources";
    private final RoadmapMacroCacheSupplier cacheSupplier;
    private final I18nResolver i18n;
    private final EventPublisher eventPublisher;
    private final PermissionManager permissionManager;
    private final PageLinkParser pageLinkParser;
    private final PageBuilderService pageBuilderService;

    public RoadmapMacro(I18nResolver i18n, EventPublisher eventPublisher, RoadmapMacroCacheSupplier cacheSupplier, PermissionManager permissionManager, PageLinkParser pageLinkParser, PageBuilderService pageBuilderService) {
        this.i18n = i18n;
        this.eventPublisher = eventPublisher;
        this.cacheSupplier = cacheSupplier;
        this.permissionManager = permissionManager;
        this.pageLinkParser = pageLinkParser;
        this.pageBuilderService = pageBuilderService;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        this.pageBuilderService.assembler().resources().requireContext(REQUIRED_CONTEXT);
        try {
            if (this.isTimelinePlanner(parameters)) {
                return this.getTimelinePlannerView(parameters, context);
            }
            return this.getRoadmapView(parameters, context);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> parameters, ConversionContext conversionContext) {
        return this.generateImagePlaceholder(parameters);
    }

    private String getRoadmapView(Map<String, String> parameters, ConversionContext context) throws UnsupportedEncodingException {
        Map ctx = MacroUtils.defaultVelocityContext();
        String title = parameters.get("title");
        if (title != null) {
            ctx.put("title", URLDecoder.decode(title, "UTF-8"));
        }
        ctx.put("id", context.getEntity().getId());
        ctx.put("version", context.getEntity().getVersion());
        ctx.put("hash", parameters.get("hash"));
        return VelocityUtils.getRenderedTemplate((String)"templates/view.vm", (Map)ctx);
    }

    private String getTimelinePlannerView(Map<String, String> parameters, ConversionContext context) throws IOException {
        TimelinePlanner roadmap = TimelinePlannerJsonBuilder.fromJson(parameters.get("source"));
        this.updateLinkedPagesTitle(roadmap, parameters.get(PARAM_PAGE_LINK), parameters.get(PARAM_MAP_LINK), context);
        Map ctx = MacroUtils.defaultVelocityContext();
        ContentEntityObject contentEntity = context.getEntity();
        String outputType = context.getOutputType();
        if (SVG_CONTEXTS.contains(outputType) || PNG_CONTEXTS.contains(outputType)) {
            ctx.put("id", contentEntity.getId());
            ctx.put("version", contentEntity.getVersion());
            ctx.put("hash", parameters.get("hash"));
            if (SVG_CONTEXTS.contains(outputType) && !UserAgentUtil.isBrowserMajorVersion((UserAgentUtil.BrowserMajorVersion)UserAgentUtil.BrowserMajorVersion.MSIE8) && !MOBILE_OUTPUT_DEVICE_TYPE.equals(context.getOutputDeviceType())) {
                SVGRoadMapRenderer svgRoadMapRenderer = new SVGRoadMapRenderer();
                svgRoadMapRenderer.setI18n(this.i18n);
                String svgXmlCode = svgRoadMapRenderer.renderAsString(roadmap);
                ctx.put("svgHtml", svgXmlCode);
            }
        } else {
            PNGRoadMapRenderer pngRoadMapRenderer = new PNGRoadMapRenderer();
            pngRoadMapRenderer.setI18n(this.i18n);
            ctx.put("data", pngRoadMapRenderer.renderAsBase64(roadmap));
        }
        this.setPagePermission(ctx, contentEntity);
        ctx.put(PARAM_RENDER_CONTEXT, outputType);
        if (StringUtils.equals((CharSequence)context.getOutputType(), (CharSequence)"display")) {
            this.eventPublisher.publish((Object)new RoadmapAnalyticObject(roadmap));
        }
        return VelocityUtils.getRenderedTemplate((String)"templates/timeline-planner-view.vm", (Map)ctx);
    }

    private boolean isTimelinePlanner(Map<String, String> parameters) {
        return Boolean.parseBoolean(parameters.get("timeline"));
    }

    private void setPagePermission(Map<String, Object> context, ContentEntityObject entityObject) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        context.put("canUserEditPage", this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, (Object)entityObject));
    }

    private void updateLinkedPagesTitle(TimelinePlanner roadmap, String pageLinks, String maplinks, ConversionContext context) {
        String[] pageLinkArray = StringUtils.splitByWholeSeparator((String)pageLinks, (String)LINKS_DELIMITER);
        Object[] mapLinkArray = StringUtils.splitByWholeSeparator((String)maplinks, (String)LINKS_DELIMITER);
        if (pageLinkArray == null || mapLinkArray == null || pageLinkArray.length != mapLinkArray.length) {
            return;
        }
        for (Lane lane : roadmap.getLanes()) {
            for (Bar bar : lane.getBars()) {
                int barIdIndex = ArrayUtils.indexOf((Object[])mapLinkArray, (Object)bar.getId());
                if (barIdIndex <= -1) continue;
                String pageTitle = pageLinkArray[barIdIndex];
                RoadmapPageLink pageLink = this.pageLinkParser.resolveConfluenceLink(pageTitle, context.getSpaceKey());
                if (pageLink.getId() == null) {
                    pageLink.setId(bar.getPageLink().getId());
                }
                bar.setPageLink(pageLink);
            }
        }
    }

    private ImagePlaceholder generateImagePlaceholder(Map<String, String> parameters) {
        this.cacheSupplier.getMarcoSourceCache().put((Object)parameters.get("hash"), (Object)parameters.get("source"));
        String placeholderUrl = "/plugins/servlet/roadmap/image/placeholder?hash=" + parameters.get("hash") + "&width=1000&height=300";
        if (parameters.get("timeline") != null) {
            placeholderUrl = placeholderUrl + "&timeline=true";
        }
        return new DefaultImagePlaceholder(placeholderUrl, false, null);
    }
}

