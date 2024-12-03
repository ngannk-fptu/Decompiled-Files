/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.api.model.web.WebItemView
 *  com.atlassian.confluence.api.model.web.WebItemView$Builder
 *  com.atlassian.confluence.api.model.web.WebPanelView
 *  com.atlassian.confluence.api.model.web.WebPanelView$Builder
 *  com.atlassian.confluence.api.model.web.WebSectionView
 *  com.atlassian.confluence.api.model.web.WebSectionView$Builder
 *  com.atlassian.confluence.api.service.web.WebView
 *  com.atlassian.confluence.api.service.web.WebViewService
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.atlassian.plugin.web.model.WebParam
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.fugue.Pair
 *  javax.activation.DataSource
 *  javax.servlet.http.HttpServletRequest
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.web.service;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.model.web.WebItemView;
import com.atlassian.confluence.api.model.web.WebPanelView;
import com.atlassian.confluence.api.model.web.WebSectionView;
import com.atlassian.confluence.api.service.web.WebView;
import com.atlassian.confluence.api.service.web.WebViewService;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.mail.embed.MimeBodyPartDataSource;
import com.atlassian.confluence.mail.embed.MimeBodyPartRecorder;
import com.atlassian.confluence.mail.embed.MimeBodyPartReference;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.descriptors.ConfluenceWebItemModuleDescriptor;
import com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebIcon;
import com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebLabel;
import com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebLink;
import com.atlassian.confluence.web.WebMenuManager;
import com.atlassian.confluence.web.WebMenuSection;
import com.atlassian.confluence.web.service.WebContextFactory;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.plugin.web.model.WebParam;
import com.google.common.collect.ImmutableMap;
import io.atlassian.fugue.Pair;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebViewServiceImpl
implements WebViewService {
    private static final Logger log = LoggerFactory.getLogger(WebViewServiceImpl.class);
    private static final boolean failFast = Boolean.parseBoolean(System.getProperty("atlassian.dev.mode"));
    private final WebInterfaceManager webInterfaceManager;
    private final WebMenuManager webMenuManager;
    private final WebContextFactory webContextFactory;
    private final MimeBodyPartRecorder mimeBodyPartRecorder;
    private final DataSourceFactory dataSourceFactory;
    private final Map<String, List<String>> webItemBlacklist;
    private static final String ICON_SUFFIX = MimeBodyPartDataSource.encode(":icon");

    public WebViewServiceImpl(WebInterfaceManager webInterfaceManager, WebMenuManager webMenuManager, WebContextFactory webContextFactory, MimeBodyPartRecorder mimeBodyPartRecorder, DataSourceFactory dataSourceFactory, Map<String, List<String>> webItemBlacklist) {
        this.webInterfaceManager = webInterfaceManager;
        this.webMenuManager = webMenuManager;
        this.webContextFactory = webContextFactory;
        this.mimeBodyPartRecorder = mimeBodyPartRecorder;
        this.dataSourceFactory = dataSourceFactory;
        this.webItemBlacklist = ImmutableMap.copyOf(webItemBlacklist);
    }

    public WebView forContent(String contentId) {
        WebInterfaceContext webInterfaceContext = this.webContextFactory.createWebInterfaceContext(contentId != null ? Long.valueOf(contentId) : null);
        return this.forContext(webInterfaceContext);
    }

    public WebView forContent(ContentId contentId) {
        return this.forContent(contentId, (Map<String, Object>)ImmutableMap.of());
    }

    public WebView forContent(@Nullable ContentId contentId, Map<String, Object> additionalContext) {
        Long id = contentId != null ? Long.valueOf(contentId.asLong()) : null;
        WebInterfaceContext webInterfaceContext = this.webContextFactory.createWebInterfaceContext(id, additionalContext);
        return this.forContext(webInterfaceContext);
    }

    public WebView forGeneric() {
        return this.forContent((ContentId)null);
    }

    public WebView forSpace(String spaceKey) {
        WebInterfaceContext webInterfaceContext = this.webContextFactory.createWebInterfaceContextForSpace(spaceKey);
        return this.forContext(webInterfaceContext);
    }

    private WebView forContext(final WebInterfaceContext webInterfaceContext) {
        return new WebView(){

            public Iterable<WebItemView> getItemsForSection(String section, Map<String, Object> additionalContext) {
                return WebViewServiceImpl.this.transformWebItems(WebViewServiceImpl.this.webInterfaceManager.getDisplayableItems(section, WebViewServiceImpl.joinWebInterfaceAndAdditionalContext(webInterfaceContext, additionalContext).toMap()), WebViewServiceImpl.this.webContextFactory.createWebItemTemplateContext(webInterfaceContext, additionalContext));
            }

            public Iterable<WebSectionView> getSectionsForLocation(String location, Map<String, Object> additionalContext) {
                return this.getSectionsForLocations(Collections.singleton(location), additionalContext);
            }

            public Iterable<WebSectionView> getSectionsForLocations(Collection<String> locations, Map<String, Object> additionalContext) {
                return WebViewServiceImpl.this.transformWebSections(WebViewServiceImpl.this.webMenuManager.getMenu(locations, WebViewServiceImpl.joinWebInterfaceAndAdditionalContext(webInterfaceContext, additionalContext)).getSections(), WebViewServiceImpl.this.webContextFactory.createTemplateContext(webInterfaceContext, additionalContext));
            }

            public Iterable<WebPanelView> getPanelsForLocation(String location, Map<String, Object> additionalContext) {
                Map<String, Object> context = WebViewServiceImpl.this.webContextFactory.createWebPanelTemplateContext(webInterfaceContext, additionalContext);
                return WebViewServiceImpl.this.transformWebPanels(WebViewServiceImpl.this.webInterfaceManager.getDisplayableWebPanelDescriptors(location, context), context);
            }

            public Iterable<WebPanelView> getPanelsForLocations(Collection<String> locations, Map<String, Object> additionalContext) {
                return locations.stream().flatMap(location -> StreamSupport.stream(this.getPanelsForLocation((String)location, additionalContext).spliterator(), false)).collect(Collectors.toList());
            }

            public Map<String, Object> getWebPanelVelocityContext() {
                return WebViewServiceImpl.this.webContextFactory.createWebPanelTemplateContext(webInterfaceContext, null);
            }
        };
    }

    private static WebInterfaceContext joinWebInterfaceAndAdditionalContext(WebInterfaceContext webInterfaceContext, Map<String, Object> additionalContext) {
        if (additionalContext == null || additionalContext.isEmpty()) {
            return webInterfaceContext;
        }
        DefaultWebInterfaceContext joined = DefaultWebInterfaceContext.copyOf(webInterfaceContext);
        joined.setParameters(additionalContext);
        return joined;
    }

    private Iterable<WebItemView> transformWebItems(List<WebItemModuleDescriptor> displayableItems, Map<String, Object> webItemContextSupplier) {
        return displayableItems.stream().filter(new NotBlacklisted()).map(new WebItemModuleDescriptorToWebItemViewTransformer(webItemContextSupplier)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Iterable<WebSectionView> transformWebSections(List<WebMenuSection> sections, Map<String, Object> webContextSupplier) {
        return sections.stream().map(new WebMenuSectionToWebSectionViewTransformer(webContextSupplier)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Iterable<WebPanelView> transformWebPanels(List<WebPanelModuleDescriptor> displayablePanels, Map<String, Object> webContextSupplier) {
        return displayablePanels.stream().map(new WebPanelModuleDescriptorToWebPanelView(webContextSupplier)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static class RecordIconMimeBodyPartFunction
    implements Function<PluginDataSourceFactory, Iterable<Pair<Icon, Optional<MimeBodyPartReference>>>> {
        private final MimeBodyPartRecorder mimeBodyPartRecorder;
        private final ConfluenceWebItemModuleDescriptor confluenceDescriptor;
        private final EmbeddedResourceParamRecordingPredicate recordingPredicate;

        public RecordIconMimeBodyPartFunction(MimeBodyPartRecorder mimeBodyPartRecorder, ConfluenceWebItemModuleDescriptor webItemModuleDescriptor) {
            this.mimeBodyPartRecorder = mimeBodyPartRecorder;
            this.confluenceDescriptor = webItemModuleDescriptor;
            this.recordingPredicate = new EmbeddedResourceParamRecordingPredicate();
        }

        @Override
        public Iterable<Pair<Icon, Optional<MimeBodyPartReference>>> apply(@NonNull PluginDataSourceFactory pluginDataSourceFactory) {
            return pluginDataSourceFactory.getResourcesFromModules(this.confluenceDescriptor.getKey(), this.recordingPredicate).map(new ConvertToIconReferencePairFunction()).orElse(Collections.emptyList());
        }

        private static Icon makeIconForEmbedding(URI iconSid, String resourceName, ImmutableMap<String, String> resourceParams) {
            int height;
            int width;
            try {
                String widthParam = (String)resourceParams.get((Object)"width");
                String heightParam = (String)resourceParams.get((Object)"height");
                width = Integer.parseInt(widthParam == null ? "16" : widthParam, 10);
                height = Integer.parseInt(heightParam == null ? "16" : heightParam, 10);
            }
            catch (NumberFormatException e) {
                if (ConfluenceSystemProperties.isDevMode()) {
                    throw new IllegalArgumentException(resourceName + " : width or height of the resource descriptor parameter needs to be a number. Using default of 16x16", e);
                }
                log.warn(resourceName + " : width or height of the resource descriptor parameter needs to be a number. Using default of 16x16");
                width = 16;
                height = 16;
            }
            return new Icon(iconSid.toASCIIString(), width, height, false);
        }

        private static class EmbeddedResourceParamRecordingPredicate
        implements Predicate<PluginDataSourceFactory.ResourceView> {
            private final Map<String, ImmutableMap<String, String>> index = new HashMap<String, ImmutableMap<String, String>>(4);

            private EmbeddedResourceParamRecordingPredicate() {
            }

            @Override
            public boolean test(PluginDataSourceFactory.ResourceView resource) {
                if (PluginDataSourceFactory.FilterByType.EMBEDDED.test(resource)) {
                    this.index.put(resource.name(), (ImmutableMap<String, String>)ImmutableMap.copyOf(resource.params()));
                    return true;
                }
                return false;
            }

            public ImmutableMap<String, String> getParametersForResource(String resourceName) {
                return this.index.get(resourceName);
            }
        }

        private class ConvertToIconReferencePairFunction
        implements Function<Iterable<DataSource>, Iterable<Pair<Icon, Optional<MimeBodyPartReference>>>> {
            private ConvertToIconReferencePairFunction() {
            }

            @Override
            public Iterable<Pair<Icon, Optional<MimeBodyPartReference>>> apply(Iterable<DataSource> dataSources) {
                return StreamSupport.stream(dataSources.spliterator(), false).map(dataSource -> {
                    Optional<MimeBodyPartReference> mimeBodyPartReference = RecordIconMimeBodyPartFunction.this.mimeBodyPartRecorder.trackSource((DataSource)dataSource);
                    ImmutableMap<String, String> parameters = RecordIconMimeBodyPartFunction.this.recordingPredicate.getParametersForResource(dataSource.getName());
                    Icon icon = RecordIconMimeBodyPartFunction.makeIconForEmbedding(mimeBodyPartReference.get().getLocator(), dataSource.getName(), parameters);
                    return Pair.pair((Object)icon, mimeBodyPartReference);
                }).collect(Collectors.toList());
            }
        }
    }

    private static class WebPanelModuleDescriptorToWebPanelView
    implements Function<WebPanelModuleDescriptor, WebPanelView> {
        private final Map<String, Object> webContext;

        public WebPanelModuleDescriptorToWebPanelView(Map<String, Object> webContext) {
            this.webContext = webContext;
        }

        @Override
        public WebPanelView apply(@Nullable WebPanelModuleDescriptor input) {
            try {
                String renderedPanel = ((WebPanel)input.getModule()).getHtml(this.webContext);
                WebPanelView.Builder panelBuilder = new WebPanelView.Builder();
                HttpServletRequest request = ServletContextThreadLocal.getRequest();
                String label = input.getWebLabel() != null ? input.getWebLabel().getDisplayableLabel(request, this.webContext) : null;
                return panelBuilder.setModuleKey(input.getKey()).setCompleteKey(input.getCompleteKey()).setLabel(label).setWeight(input.getWeight()).setName(input.getName()).setLocation(input.getLocation()).create(renderedPanel);
            }
            catch (RuntimeException e) {
                if (failFast) {
                    throw e;
                }
                log.debug("Transformation of [{}] to an instance of [{}] failed. The input will be filtered from the results, see trace for cause.", (Object)input, (Object)WebPanelView.class.getName());
                log.trace(e.getMessage(), (Throwable)e);
                return null;
            }
        }
    }

    private class WebMenuSectionToWebSectionViewTransformer
    implements Function<WebMenuSection, WebSectionView> {
        private final Map<String, Object> webContext;

        public WebMenuSectionToWebSectionViewTransformer(Map<String, Object> webContext) {
            this.webContext = webContext;
        }

        @Override
        public WebSectionView apply(WebMenuSection input) {
            try {
                WebSectionView.Builder sectionBuilder = new WebSectionView.Builder();
                sectionBuilder.addItems(WebViewServiceImpl.this.transformWebItems(input.getItems(), this.webContext));
                sectionBuilder.setStyleClass(input.getClassName());
                return sectionBuilder.create(input.getId(), input.getLabel());
            }
            catch (RuntimeException e) {
                if (failFast) {
                    throw e;
                }
                log.debug("Construction of [{}] using given [{}] failed. The input will be filtered from the results, see trace for cause.", (Object)WebSectionView.class.getName(), (Object)input);
                log.trace(e.getMessage(), (Throwable)e);
                return null;
            }
        }
    }

    private class WebItemModuleDescriptorToWebItemViewTransformer
    implements Function<WebItemModuleDescriptor, WebItemView> {
        private final Map<String, Object> webContext;

        public WebItemModuleDescriptorToWebItemViewTransformer(Map<String, Object> webContext) {
            this.webContext = webContext;
        }

        @Override
        public WebItemView apply(WebItemModuleDescriptor descriptor) {
            try {
                ConfluenceWebItemModuleDescriptor confluenceDescriptor = (ConfluenceWebItemModuleDescriptor)descriptor;
                HttpServletRequest request = ServletContextThreadLocal.getRequest();
                ConfluenceWebLink webLink = confluenceDescriptor.getLink();
                String key = descriptor.getKey();
                String url = webLink.getDisplayableUrl(request, this.webContext);
                String urlWithoutContextPath = webLink.getRenderedUrl(this.webContext);
                String label = confluenceDescriptor.getLabel().getDisplayableLabel(request, this.webContext);
                String id = webLink.getId();
                String accessKey = webLink.getAccessKey(new HashMap<String, Object>(this.webContext));
                String styleClass = confluenceDescriptor.getStyleClass();
                int weight = confluenceDescriptor.getWeight();
                ConfluenceWebLabel tooltip = confluenceDescriptor.getTooltip();
                Map<String, String> params = this.getParams(descriptor);
                WebItemView.Builder builder = WebItemView.builder().setId(id).setModuleKey(descriptor.getKey()).setCompleteKey(descriptor.getCompleteKey()).setSection(descriptor.getSection()).setAccessKey(accessKey).setStyleClass(styleClass).setUrlWithoutContextPath(urlWithoutContextPath);
                if (tooltip != null) {
                    builder.setTooltip(tooltip.getDisplayableLabel(request, this.webContext));
                }
                builder.setIcon(this.buildIcon(confluenceDescriptor, request));
                if (params != null) {
                    builder.setParams(params);
                }
                return builder.create(key, url, label, weight);
            }
            catch (RuntimeException e) {
                if (failFast) {
                    throw e;
                }
                log.debug("Transformation of [{}] to an instance of [{}] failed. The input will be filtered from the results, see trace for cause.", (Object)descriptor, (Object)WebItemView.class.getName());
                log.trace(e.getMessage(), (Throwable)e);
                return null;
            }
        }

        private Optional<Icon> buildIcon(ConfluenceWebItemModuleDescriptor confluenceDescriptor, HttpServletRequest request) {
            ConfluenceWebIcon webIcon = confluenceDescriptor.getIcon();
            if (webIcon == null) {
                return Optional.empty();
            }
            if (!WebViewServiceImpl.this.mimeBodyPartRecorder.isRecording()) {
                String url = webIcon.getUrl().getDisplayableUrl(request, this.webContext);
                return Optional.of(new Icon(url, webIcon.getHeight(), webIcon.getWidth(), false));
            }
            Optional<PluginDataSourceFactory> pluginDataSourceFactory = WebViewServiceImpl.this.dataSourceFactory.createForPlugin(confluenceDescriptor.getPluginKey());
            RecordIconMimeBodyPartFunction bodyParter = new RecordIconMimeBodyPartFunction(WebViewServiceImpl.this.mimeBodyPartRecorder, confluenceDescriptor);
            Iterable<Pair<Icon, Optional<MimeBodyPartReference>>> iconMimeBodyPartPairs = pluginDataSourceFactory.map(bodyParter).get();
            for (Pair<Icon, Optional<MimeBodyPartReference>> p : iconMimeBodyPartPairs) {
                if (!((MimeBodyPartReference)((Optional)p.right()).get()).getSource().getName().endsWith(ICON_SUFFIX)) continue;
                log.debug("Located embedded resource named 'icon' with SID: {}", (Object)((Icon)p.left()).getPath());
                return Optional.of((Icon)p.left());
            }
            log.warn("WebIcon not found in plugin: {}", (Object)webIcon.getUrl());
            return Optional.empty();
        }

        private Map<String, String> getParams(WebItemModuleDescriptor descriptor) {
            WebParam webParams = descriptor.getWebParams();
            if (webParams.getParams() == null || webParams.getParams().size() == 0) {
                return null;
            }
            HashMap<String, String> params = new HashMap<String, String>();
            for (String paramKey : webParams.getParams().keySet()) {
                params.put(paramKey, webParams.getRenderedParam(paramKey, new HashMap<String, Object>(this.webContext)));
            }
            return params;
        }
    }

    private class NotBlacklisted
    implements Predicate<WebItemModuleDescriptor> {
        private NotBlacklisted() {
        }

        @Override
        public boolean test(@NonNull WebItemModuleDescriptor input) {
            List<String> blacklistedSection = WebViewServiceImpl.this.webItemBlacklist.get(input.getSection());
            if (blacklistedSection != null && blacklistedSection.contains(input.getKey())) {
                log.debug("WebItem [{}] is blacklisted.", (Object)input);
                return false;
            }
            return true;
        }
    }
}

