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
 *  com.atlassian.confluence.macro.ResourceAware
 *  com.atlassian.confluence.plugin.webresource.Counter
 *  com.atlassian.confluence.renderer.ConfluenceRenderContextOutputType
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.GadgetId
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetState
 *  com.atlassian.gadgets.view.GadgetViewFactory
 *  com.atlassian.gadgets.view.ModuleId
 *  com.atlassian.gadgets.view.View
 *  com.atlassian.gadgets.view.View$Builder
 *  com.atlassian.gadgets.view.ViewComponent
 *  com.atlassian.gadgets.view.ViewType
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gadgets;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.ResourceAware;
import com.atlassian.confluence.plugin.webresource.Counter;
import com.atlassian.confluence.plugins.gadgets.GadgetCounter;
import com.atlassian.confluence.plugins.gadgets.events.GadgetMacroRenderedEvent;
import com.atlassian.confluence.plugins.gadgets.preferencesextractor.GadgetPreferencesExtractor;
import com.atlassian.confluence.plugins.gadgets.preferencesextractor.WikiMarkupGadgetPreferencesExtractor;
import com.atlassian.confluence.plugins.gadgets.preferencesextractor.XhtmlGadgetPreferencesExtractor;
import com.atlassian.confluence.plugins.gadgets.requestcontext.RequestContextBuilder;
import com.atlassian.confluence.plugins.gadgets.whitelist.GadgetWhiteListManager;
import com.atlassian.confluence.renderer.ConfluenceRenderContextOutputType;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.GadgetId;
import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.view.GadgetViewFactory;
import com.atlassian.gadgets.view.ModuleId;
import com.atlassian.gadgets.view.View;
import com.atlassian.gadgets.view.ViewComponent;
import com.atlassian.gadgets.view.ViewType;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GadgetMacro
extends BaseMacro
implements Macro,
EditorImagePlaceholder,
ResourceAware {
    private static final Logger log = LoggerFactory.getLogger(GadgetMacro.class);
    public static final String DEFAULT_WIDTH = "450";
    public static final String URL_PARAMETER = "url";
    public static final String WIDTH_PARAMETER = "width";
    public static final String BORDER_PARAMETER = "border";
    public static final String AUTHOR_PARAMETER = "author";
    public static final String PREFERENCES_PARAMETER = "preferences";
    private static final String MACRO_VIEW_PARAMETER = "view";
    private static final String IGNORE_CACHE_PARAMETER = "ignoreCache";
    private static final String GADGET_COUNT = "gadgetCount";
    private static final String GADGET_PLACEHOLDER_SERVLET = "/plugins/servlet/gadgets/placeholder";
    private static final String FORCE_WRITE_PARAM = "forceWrite";
    private final GadgetViewFactory gadgetViewFactory;
    private final PageBuilderService pageBuilderService;
    private final GadgetWhiteListManager whiteListManager;
    private final I18nResolver resolver;
    private final RequestContextBuilder requestContextBuilder;
    private final Counter counter;
    private final EventPublisher eventPublisher;

    public GadgetMacro(GadgetViewFactory gadgetViewFactory, PageBuilderService pageBuilderService, GadgetWhiteListManager whiteListManager, I18nResolver resolver, RequestContextBuilder requestContextBuilder, GadgetCounter gadgetCounter, EventPublisher eventPublisher) {
        this.gadgetViewFactory = gadgetViewFactory;
        this.pageBuilderService = pageBuilderService;
        this.whiteListManager = whiteListManager;
        this.resolver = resolver;
        this.requestContextBuilder = requestContextBuilder;
        this.counter = gadgetCounter;
        this.eventPublisher = eventPublisher;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map paramsRaw, String body, RenderContext renderContext) throws MacroException {
        try {
            PageContext pageContext = (PageContext)renderContext;
            StringWriter buffer = new StringWriter();
            this.requireGadgetRenderResources();
            this.renderGadget(body, new WikiMarkupGadgetPreferencesExtractor(), new MacroHelper(paramsRaw, pageContext), buffer);
            return buffer.toString();
        }
        catch (MacroExecutionException | IOException e) {
            throw new MacroException(e);
        }
    }

    private void requireGadgetRenderResources() {
        this.pageBuilderService.assembler().resources().requireContext("render-gadget");
    }

    public String execute(Map<String, String> params, String body, ConversionContext conversionContext) throws MacroExecutionException {
        PageContext pageContext = conversionContext != null ? conversionContext.getPageContext() : null;
        this.requireGadgetRenderResources();
        MacroHelper macroHelper = new MacroHelper(params, pageContext);
        try {
            StringWriter writer = new StringWriter();
            this.renderGadget(body, new XhtmlGadgetPreferencesExtractor(), macroHelper, writer);
            return writer.toString();
        }
        catch (IOException e) {
            throw new MacroExecutionException("Failed to render gadget " + macroHelper.getUriString(), (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderGadget(String body, GadgetPreferencesExtractor gadgetPreferencesExtractor, MacroHelper macroHelper, Writer output) throws MacroExecutionException, IOException {
        try {
            macroHelper.assertNotNestedGadget();
            this.generateMacroDiv(body, gadgetPreferencesExtractor, macroHelper, output);
        }
        finally {
            macroHelper.publishEvent();
        }
    }

    private void generateMacroDiv(String macroBody, GadgetPreferencesExtractor gadgetPreferencesExtractor, MacroHelper macroHelper, Writer output) throws MacroExecutionException, IOException {
        URI gadgetUri = macroHelper.gadgetUri;
        ModuleId moduleId = macroHelper.generateModuleId();
        try {
            Map<String, String> gadgetParams = this.buildGadgetParams(macroBody, gadgetPreferencesExtractor, macroHelper);
            macroHelper.metrics.createGadgetViewStart();
            ViewComponent gadgetView = this.getGadgetView(gadgetUri, gadgetParams, moduleId, macroHelper);
            macroHelper.metrics.createGadgetViewFinish();
            macroHelper.metrics.renderGadgetViewStart();
            this.renderGadgetView(macroHelper, output, moduleId, gadgetView);
            macroHelper.metrics.renderGadgetViewFinish();
        }
        catch (GadgetParsingException ex) {
            macroHelper.throwMacroExecutionException(ex);
        }
    }

    private void renderGadgetView(MacroHelper macroHelper, Writer output, ModuleId moduleId, ViewComponent gadgetView) throws IOException {
        output.append("<div class=\"").append("gadgetContainer-").append(String.valueOf(moduleId.value())).append("\"");
        output.append(" style =\"");
        macroHelper.writeStyle(output);
        output.append("\">");
        gadgetView.writeTo(output);
        output.append("</div>");
    }

    protected boolean isGadgetUrlPredefined() {
        return false;
    }

    protected String getPredefinedGadgetUrl() {
        return null;
    }

    private Map<String, String> buildGadgetParams(String macroBody, GadgetPreferencesExtractor gadgetPreferencesExtractor, MacroHelper macroHelper) {
        HashMap<String, String> gadgetParams = new HashMap<String, String>();
        gadgetParams.putAll(macroHelper.params);
        gadgetParams.putAll(gadgetPreferencesExtractor.getGadgetPreferences(macroHelper.params, macroBody));
        gadgetParams.remove(": = | RAW | = :");
        gadgetParams.remove(URL_PARAMETER);
        gadgetParams.remove(BORDER_PARAMETER);
        gadgetParams.remove(MACRO_VIEW_PARAMETER);
        return gadgetParams;
    }

    private ViewComponent getGadgetView(URI gadgetUri, Map<String, String> gadgetParams, ModuleId moduleId, MacroHelper macroHelper) throws MacroExecutionException {
        GadgetId gadgetId = GadgetMacro.buildGadgetIdFromSpecURI(gadgetUri);
        GadgetState gadgetState = GadgetState.gadget((GadgetId)gadgetId).specUri(gadgetUri).userPrefs(gadgetParams).build();
        GadgetRequestContext gadgetContext = this.requestContextBuilder.buildRequestContext(macroHelper.ignoreCache());
        ViewType viewType = this.getViewType(macroHelper.getViewMode(), gadgetState, gadgetContext);
        View view = new View.Builder().viewType(viewType).writable(macroHelper.allowWrites()).build();
        return this.gadgetViewFactory.createGadgetView(gadgetState, moduleId, view, gadgetContext);
    }

    private ViewType getViewType(String viewName, GadgetState gadgetState, GadgetRequestContext context) throws MacroExecutionException {
        ViewType type;
        try {
            type = ViewType.valueOf((String)viewName);
        }
        catch (IllegalArgumentException e) {
            type = ViewType.DEFAULT;
        }
        if (!this.gadgetViewFactory.canRenderInViewType(gadgetState, type, context)) {
            if (type != ViewType.DEFAULT) {
                type = ViewType.DEFAULT;
            } else {
                throw new MacroExecutionException(this.resolver.getText("gadget.macro.cannotrenderview", new Serializable[]{viewName}));
            }
        }
        return type;
    }

    private static GadgetId buildGadgetIdFromSpecURI(URI gadgetUri) {
        return GadgetId.valueOf((String)DigestUtils.md5Hex((String)gadgetUri.toString()));
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> parameters, ConversionContext context) {
        String url = parameters.get(URL_PARAMETER);
        if (url != null) {
            try {
                byte[] bytes = url.getBytes("UTF-8");
                String placeholderUrl = "/s/" + this.counter.getCounter() + "/_/plugins/servlet/gadgets/placeholder?gadgetId=" + Base64.encodeBase64URLSafeString((byte[])bytes);
                return new DefaultImagePlaceholder(placeholderUrl, false, null);
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return new DefaultImagePlaceholder("/images/icons/macrobrowser/macro-placeholder-default.png", false, null);
    }

    public String getResourcePath() {
        return null;
    }

    public void setResourcePath(String resourcePath) {
    }

    private class MacroHelper {
        final Map<String, String> params;
        final PageContext pageContext;
        final GadgetMacroRenderedEvent.Builder metrics = GadgetMacroRenderedEvent.builder();
        final URI gadgetUri;

        MacroHelper(Map<String, String> params, PageContext pageContext) throws MacroExecutionException {
            this.params = params;
            this.pageContext = pageContext;
            this.gadgetUri = this.extractGadgetUri();
        }

        void publishEvent() {
            GadgetMacro.this.eventPublisher.publish((Object)this.metrics.build());
        }

        boolean ignoreCache() {
            String ignoreCacheParam = this.params.remove(GadgetMacro.IGNORE_CACHE_PARAMETER);
            boolean ignoreCache = ignoreCacheParam == null ? Boolean.getBoolean("atlassian.disable.caches") : Boolean.valueOf(ignoreCacheParam);
            this.metrics.withIgnoreCache(ignoreCache);
            return ignoreCache;
        }

        void writeStyle(Writer output) throws IOException {
            if (this.addBorder()) {
                output.append("border:1px solid #CCC; padding:5px; overflow:auto;");
            }
            output.append(" ").append(GadgetMacro.WIDTH_PARAMETER).append(":").append(HtmlUtil.htmlEncode((String)this.getWidth()));
        }

        String getWidth() {
            Object widthValue = this.params.get(GadgetMacro.WIDTH_PARAMETER);
            if (StringUtils.isBlank((CharSequence)widthValue)) {
                widthValue = GadgetMacro.DEFAULT_WIDTH;
            }
            if (StringUtils.isNumeric((CharSequence)widthValue)) {
                widthValue = (String)widthValue + "px";
            }
            return widthValue;
        }

        boolean addBorder() {
            if (this.params.get(GadgetMacro.BORDER_PARAMETER) != null) {
                return Boolean.valueOf(this.params.get(GadgetMacro.BORDER_PARAMETER));
            }
            return true;
        }

        void assertNotNestedGadget() throws MacroExecutionException {
            if (ConfluenceRenderContextOutputType.PAGE_GADGET.toString().equals(this.pageContext.getOutputType())) {
                throw new MacroExecutionException(GadgetMacro.this.resolver.getText("gadget.cannot.view.gadget.in.page.gadget"));
            }
        }

        private URI extractGadgetUri() throws MacroExecutionException {
            String uriString = this.getUriString();
            try {
                URI gadgetUri = new URI(uriString);
                this.metrics.withGadgetUri(gadgetUri);
                log.debug("Gadget URI [" + gadgetUri + "]");
                return gadgetUri;
            }
            catch (URISyntaxException e) {
                throw new MacroExecutionException(GadgetMacro.this.resolver.getText("gadgets.invalid.uri", new Serializable[]{uriString}));
            }
        }

        private String getUriString() throws MacroExecutionException {
            String uriString = GadgetMacro.this.isGadgetUrlPredefined() ? GadgetMacro.this.getPredefinedGadgetUrl() : this.params.get(GadgetMacro.URL_PARAMETER);
            if (StringUtils.isBlank((CharSequence)uriString)) {
                throw new MacroExecutionException(GadgetMacro.this.resolver.getText("gadget.uri.not.entered"));
            }
            return uriString;
        }

        boolean allowWrites() {
            boolean allowWrites = false;
            if ("true".equals(this.params.get(GadgetMacro.FORCE_WRITE_PARAM))) {
                allowWrites = true;
            }
            this.metrics.withAllowWrites(allowWrites);
            return allowWrites;
        }

        String getViewMode() {
            String viewMode = this.params.get(GadgetMacro.MACRO_VIEW_PARAMETER);
            if (viewMode == null) {
                viewMode = ViewType.DEFAULT.toString();
            }
            this.metrics.withViewMode(viewMode);
            return viewMode;
        }

        ModuleId generateModuleId() {
            AtomicInteger count = (AtomicInteger)this.pageContext.getParam((Object)GadgetMacro.GADGET_COUNT);
            if (count == null) {
                count = new AtomicInteger(1);
                this.pageContext.addParam((Object)GadgetMacro.GADGET_COUNT, (Object)count);
            } else {
                count.getAndIncrement();
            }
            long entityId = this.getEntityId();
            long hashedId = count.longValue() << 32 ^ entityId << 16;
            return ModuleId.valueOf((long)hashedId);
        }

        long getEntityId() {
            ContentEntityObject entity = this.pageContext.getEntity();
            if (entity != null) {
                return entity.getId();
            }
            return (long)(Math.random() * 8192.0);
        }

        void throwMacroExecutionException(GadgetParsingException ex) throws MacroExecutionException {
            this.metrics.gadgetWhitelistCheckStart();
            boolean isAllowed = GadgetMacro.this.whiteListManager.isAllowedGadgetUri(this.gadgetUri);
            this.metrics.gadgetWhitelistCheckFinish();
            if (!isAllowed) {
                throw new MacroExecutionException(GadgetMacro.this.resolver.getText("gadgets.not.allowed.uri", new Serializable[]{this.gadgetUri}));
            }
            throw new MacroExecutionException("Error rendering gadget [ " + this.gadgetUri + " ] ", (Throwable)ex);
        }
    }
}

