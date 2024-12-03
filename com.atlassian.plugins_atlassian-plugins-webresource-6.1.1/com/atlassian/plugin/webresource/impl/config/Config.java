/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.servlet.ContentTypeResolver
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  com.atlassian.plugin.util.validation.ValidationException
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.sourcemap.Util
 *  com.atlassian.webresource.api.assembler.resource.CompleteWebResourceKey
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.atlassian.webresource.spi.CompilerEntry
 *  com.atlassian.webresource.spi.CompilerUtil
 *  com.atlassian.webresource.spi.NoOpResourceCompiler
 *  com.atlassian.webresource.spi.ResourceCompiler
 *  com.google.common.base.Joiner
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nonnull
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.collections.MapUtils
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.impl.config;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.ContentTypeResolver;
import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.plugin.util.validation.ValidationException;
import com.atlassian.plugin.webresource.Flags;
import com.atlassian.plugin.webresource.PluginResourceContainer;
import com.atlassian.plugin.webresource.ResourceBatchingConfiguration;
import com.atlassian.plugin.webresource.ResourceUtils;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.plugin.webresource.WebResourceTransformation;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugin.webresource.cdn.CdnResourceUrlTransformer;
import com.atlassian.plugin.webresource.cdn.CdnResourceUrlTransformerImpl;
import com.atlassian.plugin.webresource.cdn.mapper.DefaultWebResourceMapper;
import com.atlassian.plugin.webresource.cdn.mapper.MappingParser;
import com.atlassian.plugin.webresource.cdn.mapper.NoOpWebResourceMapper;
import com.atlassian.plugin.webresource.cdn.mapper.WebResourceMapper;
import com.atlassian.plugin.webresource.condition.DecoratingCondition;
import com.atlassian.plugin.webresource.graph.DependencyGraph;
import com.atlassian.plugin.webresource.graph.DependencyGraphBuilder;
import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.CachedTransformers;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.annotators.ListOfAnnotators;
import com.atlassian.plugin.webresource.impl.annotators.LocationContentAnnotator;
import com.atlassian.plugin.webresource.impl.annotators.ResourceContentAnnotator;
import com.atlassian.plugin.webresource.impl.annotators.SemicolonResourceContentAnnotator;
import com.atlassian.plugin.webresource.impl.annotators.TryCatchJsResourceContentAnnotator;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.Context;
import com.atlassian.plugin.webresource.impl.snapshot.Deprecation;
import com.atlassian.plugin.webresource.impl.snapshot.RootPage;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import com.atlassian.plugin.webresource.impl.snapshot.WebResource;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.snapshot.resource.ResourceFactory;
import com.atlassian.plugin.webresource.impl.support.ConditionInstanceCache;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.plugin.webresource.impl.support.http.BaseRouter;
import com.atlassian.plugin.webresource.models.Requestable;
import com.atlassian.plugin.webresource.models.SuperBatchKey;
import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.prebake.PrebakeConfig;
import com.atlassian.plugin.webresource.transformer.StaticTransformers;
import com.atlassian.plugin.webresource.transformer.TransformerCache;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.util.HashBuilder;
import com.atlassian.plugin.webresource.util.TimeSpan;
import com.atlassian.sourcemap.Util;
import com.atlassian.webresource.api.assembler.resource.CompleteWebResourceKey;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.atlassian.webresource.spi.CompilerEntry;
import com.atlassian.webresource.spi.CompilerUtil;
import com.atlassian.webresource.spi.NoOpResourceCompiler;
import com.atlassian.webresource.spi.ResourceCompiler;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
    public static final String IEONLY_PARAM_NAME = "ieonly";
    public static final String SOURCE_PARAM_NAME = "source";
    public static final String BATCH_PARAM_NAME = "batch";
    public static final String MEDIA_PARAM_NAME = "media";
    public static final String CONTENT_TYPE_PARAM_NAME = "content-type";
    public static final String CACHE_PARAM_NAME = "cache";
    public static final String ALLOW_PUBLIC_USE_PARAM_NAME = "allow-public-use";
    public static final String CONDITIONAL_COMMENT_PARAM_NAME = "conditionalComment";
    public static final String ASYNC_SCRIPT_PARAM_NAME = "async";
    public static final String DEFER_SCRIPT_PARAM_NAME = "defer";
    public static final String INITIAL_RENDERED_SCRIPT_PARAM_NAME = "data-initially-rendered";
    public static final String WRM_KEY_PARAM_NAME = "data-wrm-key";
    public static final String WRM_BATCH_TYPE_PARAM_NAME = "data-wrm-batch-type";
    public static final String DOWNLOAD_PARAM_VALUE = "download";
    public static final String JS_TYPE = "js";
    public static final String JS_CONTENT_TYPE = "application/javascript";
    public static final String CSS_TYPE = "css";
    public static final String CSS_CONTENT_TYPE = "text/css";
    public static final String LESS_TYPE = "less";
    public static final String SOY_TYPE = "soy";
    public static final String CSS_EXTENSION = ".css";
    public static final String LESS_EXTENSION = ".less";
    public static final String JS_EXTENSION = ".js";
    public static final String SOY_EXTENSION = ".soy";
    public static final String[] BATCH_TYPES = new String[]{"css", "js"};
    public static final int GET_URL_MAX_LENGTH = 8192;
    public static final String[] HTTP_PARAM_NAMES = new String[]{"ieonly", "media", "content-type", "cache", "conditionalComment", "allow-public-use"};
    public static final List<String> PARAMS_SORT_ORDER = Arrays.asList("cache", "media", "conditionalComment", "ieonly");
    public static final String CONTEXT_PREFIX = "_context";
    public static final String SUPER_BATCH_CONTEXT_KEY = "_super";
    public static final String SUPERBATCH_KEY = "_context:_super";
    public static final String SYNCBATCH_CONTEXT_KEY = "_sync";
    public static final String SYNCBATCH_KEY = "_context:_sync";
    public static final String INCREMENTAL_CACHE_SIZE = "plugin.webresource.incrementalcache.size";
    private static final int ATLASSIAN_MODULES_VERSION = 2;
    private static final String DISABLE_MINIFICATION = "atlassian.webresource.disable.minification";
    private static final String DISABLE_URL_CACHING = "atlassian.webresource.disable.url.caching";
    private static final String GLOBAL_MINIFICATION_ENABLED = "atlassian.webresource.enable.global.minification";
    private static final String PREBAKE_FEATURE_ENABLED = "atlassian.webresource.enable.prebake";
    private static final String CT_CDN_BASE_URL = "atlassian.webresource.ct.cdn.base.url";
    private static final String PREBAKE_CSS_RESOURCES = "atlassian.webresource.enable.css.prebake";
    private static final String IGNORE_PREBAKE_WARNINGS = "atlassian.webresource.ignore.prebake.warnings";
    private static final String STATIC_CONTEXT_ORDER_ENABLED = "atlassian.webresource.stable.contexts.enable";
    @VisibleForTesting
    public static final String ENABLE_BUNDLE_HASH_VALIDATION = "atlassian.webresource.enable.bundle.hash.validation";
    @VisibleForTesting
    public static final String DISABLE_PERFORMANCE_TRACKING = "atlassian.webresource.performance.tracking.disable";
    private static final String COMPILED_RESOURCE_LOCATION_PARAMETER = "compiledLocation";
    private static final String PLUGIN_KEY = "com.atlassian.plugins.atlassian-plugins-webresource-plugin";
    private static final Pattern SNAPSHOT_PLUGIN_REGEX = Pattern.compile("SNAPSHOT", 2);
    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private final WebResourceIntegration integration;
    private final ResourceBatchingConfiguration batchingConfiguration;
    private final WebResourceUrlProvider urlProvider;
    private final boolean isContentCacheEnabled;
    private final int contentCacheSize;
    private final Integer incrementalCacheSize;
    private final boolean usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins;
    private final TransformerCache transformerCache;
    private final ResourceFactory resourceFactory;
    private static ResettableLazyReference<DependencyGraph<Requestable>> dependencyGraph;
    private final ResettableLazyReference<WebResourceMapper> cdnUrlMapper = new ResettableLazyReference<WebResourceMapper>(){

        protected WebResourceMapper create() throws Exception {
            if (Config.this.integration.isCtCdnMappingEnabled()) {
                Optional prebakeConfig;
                Optional<Object> optional = prebakeConfig = Config.this.integration.getCDNStrategy() != null ? Config.this.integration.getCDNStrategy().getPrebakeConfig() : Optional.empty();
                if (prebakeConfig.isPresent()) {
                    try {
                        log.info("Creating DefaultWebResourceMapper");
                        return new DefaultWebResourceMapper(Config.this.integration, new MappingParser(), (PrebakeConfig)prebakeConfig.get(), Config.this.computeGlobalStateHash(), Config.this.getCtCdnBaseUrl(), Config.this.integration.getBaseUrl(UrlMode.RELATIVE));
                    }
                    catch (FileNotFoundException e) {
                        return new NoOpWebResourceMapper(e);
                    }
                    catch (Exception e) {
                        log.info("DefaultWebResourceMapper was not created properly. Pre-baked CT-CDN is disabled but will fall back to product's CDN Strategy, if any.", (Throwable)e);
                        return new NoOpWebResourceMapper(e);
                    }
                }
                log.info("Creating NoOpWebResourceMapper since PrebakeConfig is not present. Pre-baked CT-CDN is disabled but will fall back to product's CDN Strategy, if any.");
                return new NoOpWebResourceMapper(new Exception("PrebakeConfig not present"));
            }
            log.info("Creating NoOpWebResourceMapper since WebResourceIntegration#isCtCdnMappingEnabled flag is disabled. Pre-baked CT-CDN is disabled but will fall back to product's CDN Strategy, if any.");
            return new NoOpWebResourceMapper(new Exception("CT-CDN is explicitly disabled by the product."));
        }
    };
    private final CdnResourceUrlTransformer cdnResourceUrlTransformer;
    private final ResourceCompiler resourceCompiler;
    private final int urlCacheSize;
    private final boolean isUrlCachingEnabled;
    private Logger supportLogger = Support.LOGGER;
    private ContentTypeResolver contentTypeResolver;
    private StaticTransformers staticTransformers;
    private Optional<Boolean> syncbatchCreated = Optional.empty();
    private Optional<Boolean> superbatchCreated = Optional.empty();

    public Config(ResourceBatchingConfiguration batchingConfiguration, WebResourceIntegration integration, WebResourceUrlProvider urlProvider, ServletContextFactory servletContextFactory, TransformerCache transformerCache, ResourceCompiler resourceCompiler) {
        this.batchingConfiguration = batchingConfiguration;
        this.integration = integration;
        this.urlProvider = urlProvider;
        this.transformerCache = transformerCache;
        this.isContentCacheEnabled = Flags.isFileCacheEnabled();
        this.contentCacheSize = Flags.getFileCacheSize(1000);
        this.isUrlCachingEnabled = !Boolean.getBoolean(DISABLE_URL_CACHING);
        this.urlCacheSize = Flags.getFileCacheSize(200);
        this.incrementalCacheSize = Integer.getInteger(INCREMENTAL_CACHE_SIZE, 1000);
        this.cdnResourceUrlTransformer = new CdnResourceUrlTransformerImpl(this);
        this.resourceCompiler = resourceCompiler == null ? new NoOpResourceCompiler() : resourceCompiler;
        this.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins = integration.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins();
        this.resourceFactory = new ResourceFactory(servletContextFactory, integration);
        dependencyGraph = new ResettableLazyReference<DependencyGraph<Requestable>>(){

            protected DependencyGraph<Requestable> create() throws Exception {
                return DependencyGraph.builder().build();
            }
        };
    }

    public static boolean isContextKey(String key) {
        return key.contains(CONTEXT_PREFIX);
    }

    public static String virtualContextKeyToWebResourceKey(String virtualContextKey) {
        return virtualContextKey.replace("_context:", "");
    }

    protected static List<ResourceLocation> getResourceLocations(WebResourceModuleDescriptor webResourceModuleDescriptor, boolean compiledResourcesEnabled) {
        ArrayList<ResourceLocation> resourceDescriptors = new ArrayList<ResourceLocation>();
        for (ResourceDescriptor resourceDescriptor : webResourceModuleDescriptor.getResourceDescriptors()) {
            String compiledLocation;
            if (!DOWNLOAD_PARAM_VALUE.equals(resourceDescriptor.getType())) continue;
            ResourceLocation resourceLocation = resourceDescriptor.getResourceLocationForName(null);
            if (compiledResourcesEnabled && (compiledLocation = resourceDescriptor.getParameter(COMPILED_RESOURCE_LOCATION_PARAMETER)) != null) {
                ResourceLocation newLocation;
                resourceLocation = newLocation = new ResourceLocation(compiledLocation, resourceLocation.getName(), resourceLocation.getType(), resourceLocation.getContentType(), resourceLocation.getContent(), resourceLocation.getParams());
            }
            resourceDescriptors.add(resourceLocation);
        }
        return resourceDescriptors;
    }

    public static boolean isWebResourceKey(@Nonnull String completeKey) {
        return completeKey.contains(":");
    }

    public static boolean isNotWebResourceKey(@Nonnull String completeKey) {
        return !Config.isWebResourceKey(completeKey);
    }

    public static boolean isStaticContextOrderEnabled() {
        return Boolean.getBoolean(STATIC_CONTEXT_ORDER_ENABLED);
    }

    public static String getPluginVersionOrInstallTime(Plugin plugin, boolean usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins) {
        String version;
        PluginInformation pluginInfo = plugin.getPluginInformation();
        String string = version = pluginInfo != null ? pluginInfo.getVersion() : "unknown";
        if (usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins && SNAPSHOT_PLUGIN_REGEX.matcher(version).find()) {
            Date loadedAt = plugin.getDateLoaded() == null ? new Date() : plugin.getDateLoaded();
            return version + "-" + loadedAt.getTime();
        }
        return version;
    }

    public void setContentTypeResolver(ContentTypeResolver contentTypeResolver) {
        if (this.contentTypeResolver != null) {
            throw new RuntimeException("content type resolver already set!");
        }
        this.contentTypeResolver = contentTypeResolver;
    }

    public boolean isContentCacheEnabled() {
        return this.isContentCacheEnabled;
    }

    public int getContentCacheSize() {
        return this.contentCacheSize;
    }

    public int getIncrementalCacheSize() {
        return this.incrementalCacheSize;
    }

    public File getCacheDirectory() {
        return this.integration.getTemporaryDirectory();
    }

    @Nonnull
    public static DependencyGraph<Requestable> getDependencyGraph() {
        return (DependencyGraph)dependencyGraph.get();
    }

    public boolean isSourceMapEnabledFor(String typeOrContentType) {
        return this.isSourceMapEnabled() && Util.isSourceMapSupportedBy((String)typeOrContentType);
    }

    public boolean isSourceMapEnabled() {
        return this.batchingConfiguration.isSourceMapEnabled();
    }

    public boolean isCdnEnabled() {
        return null != this.integration.getCDNStrategy() && this.integration.getCDNStrategy().supportsCdn();
    }

    public String getBaseUrl() {
        return this.getBaseUrl(true);
    }

    public String getBaseUrl(boolean isAbsolute) {
        try {
            return BaseRouter.joinWithSlashWithoutEmpty(this.urlProvider.getBaseUrl(isAbsolute ? UrlMode.ABSOLUTE : UrlMode.RELATIVE), DOWNLOAD_PARAM_VALUE);
        }
        catch (AssertionError e) {
            if (isAbsolute && ((Throwable)((Object)e)).getMessage().contains("Unsupported URLMode")) {
                return this.getBaseUrl(false);
            }
            throw e;
        }
    }

    public String getResourceUrlPrefix(String hash, String bundleHash, boolean isAbsolute) {
        return this.urlProvider.getStaticResourcePrefix(hash, bundleHash, isAbsolute ? UrlMode.ABSOLUTE : UrlMode.RELATIVE) + "/" + DOWNLOAD_PARAM_VALUE;
    }

    public String getResourceCdnPrefix(String url) {
        return this.getWebResourceMapper().mapSingle(url).orElseGet(() -> this.integration.getCDNStrategy().transformRelativeUrl(url));
    }

    public String getContentType(String path) {
        return this.contentTypeResolver.getContentType(path);
    }

    public ResourceContentAnnotator getContentAnnotator(String type) {
        ArrayList<ResourceContentAnnotator> annotators = new ArrayList<ResourceContentAnnotator>();
        if (JS_TYPE.equals(type)) {
            annotators.add(new SemicolonResourceContentAnnotator());
            if (this.isJavaScriptTryCatchWrappingEnabled()) {
                annotators.add(new TryCatchJsResourceContentAnnotator());
            }
            annotators.add(new LocationContentAnnotator());
        } else if (CSS_TYPE.equals(type)) {
            annotators.add(new LocationContentAnnotator());
        }
        return new ListOfAnnotators(annotators);
    }

    private List<CompleteWebResourceKey> getSyncWebResourceKeys() {
        return new ArrayList<CompleteWebResourceKey>((Collection)MoreObjects.firstNonNull(this.integration.getSyncWebResourceKeys(), Collections.emptyList()));
    }

    public StaticTransformers getStaticTransformers() {
        return this.staticTransformers;
    }

    public void setStaticTransformers(StaticTransformers staticTransformers) {
        if (this.staticTransformers != null) {
            throw new RuntimeException("static transformers already set!");
        }
        this.staticTransformers = staticTransformers;
    }

    protected void ensureNoLegacyStuff(Snapshot snapshot) {
        if (this.integration.forbidCondition1AndTransformer1()) {
            LinkedList<String> resourcesWithLegacyConditions = new LinkedList<String>();
            for (Bundle bundle : snapshot.getWebResourcesWithLegacyConditions()) {
                if (this.integration.allowedCondition1Keys().contains(bundle.getKey())) continue;
                resourcesWithLegacyConditions.add(bundle.getKey());
            }
            LinkedList<String> resourcesWithLegacyTransformers = new LinkedList<String>();
            for (Bundle bundle : snapshot.getWebResourcesWithLegacyTransformers()) {
                if (this.integration.allowedTransform1Keys().contains(bundle.getKey())) continue;
                resourcesWithLegacyTransformers.add(bundle.getKey());
            }
            if (resourcesWithLegacyConditions.size() > 0 || resourcesWithLegacyTransformers.size() > 0) {
                ArrayList<String> arrayList = new ArrayList<String>();
                if (resourcesWithLegacyConditions.size() > 0) {
                    arrayList.add("legacy conditions: \"" + Joiner.on((String)"\", \"").join(resourcesWithLegacyConditions) + "\"");
                }
                if (resourcesWithLegacyTransformers.size() > 0) {
                    arrayList.add("legacy transformers: \"" + Joiner.on((String)"\", \"").join(resourcesWithLegacyTransformers) + "\"");
                }
                throw new ValidationException("there are web resources with " + Joiner.on((String)", and ").join(arrayList), Collections.emptyList());
            }
        }
    }

    public boolean isSyncContextCreated() {
        return this.syncbatchCreated.orElseThrow(() -> new RuntimeException("Called before getWebResourcesWithoutCache()"));
    }

    public boolean isSuperbatchCreated() {
        return this.superbatchCreated.orElseThrow(() -> new RuntimeException("Called before getWebResourcesWithoutCache()"));
    }

    public Snapshot getWebResourcesWithoutCache() {
        HashMap<WebResource, CachedTransformers> webResourcesTransformations = new HashMap<WebResource, CachedTransformers>();
        HashMap<String, Bundle> cachedBundles = new HashMap<String, Bundle>();
        HashMap<String, RootPage> rootPages = new HashMap<String, RootPage>();
        HashMap<WebResource, CachedCondition> webResourcesCondition = new HashMap<WebResource, CachedCondition>();
        HashMap<String, Deprecation> webResourceDeprecationWarnings = new HashMap<String, Deprecation>();
        HashSet<WebResource> webResourcesWithLegacyConditions = new HashSet<WebResource>();
        HashSet<WebResource> webResourcesWithLegacyTransformers = new HashSet<WebResource>();
        HashSet<WebResource> webResourcesWithDisabledMinification = new HashSet<WebResource>();
        Snapshot snapshot = new Snapshot(this, cachedBundles, rootPages, webResourcesTransformations, webResourcesCondition, webResourceDeprecationWarnings, webResourcesWithLegacyConditions, webResourcesWithLegacyTransformers, webResourcesWithDisabledMinification);
        HashMap<String, IntermediaryContextData> intermediaryContexts = new HashMap<String, IntermediaryContextData>();
        ConditionInstanceCache conditionInstanceCache = new ConditionInstanceCache();
        final List webResourceDescriptors = this.integration.getPluginAccessor().getEnabledModuleDescriptorsByClass(WebResourceModuleDescriptor.class);
        for (WebResourceModuleDescriptor webResourceModuleDescriptor : webResourceDescriptors) {
            if (webResourceModuleDescriptor == null || !webResourceModuleDescriptor.isDeprecated()) continue;
            String completeKey = webResourceModuleDescriptor.getCompleteKey();
            webResourceDeprecationWarnings.put(completeKey, webResourceModuleDescriptor.getDeprecation());
        }
        for (WebResourceModuleDescriptor webResourceModuleDescriptor : webResourceDescriptors) {
            Object msg;
            Object resourceLocation2;
            if (webResourceModuleDescriptor == null) continue;
            boolean isRootPage = webResourceModuleDescriptor.isRootPage();
            Plugin plugin = webResourceModuleDescriptor.getPlugin();
            Date updatedAt = plugin.getDateLoaded() == null ? new Date() : plugin.getDateLoaded();
            String completeKey = webResourceModuleDescriptor.getCompleteKey();
            DecoratingCondition condition = webResourceModuleDescriptor.getCondition();
            boolean hasLegacyConditions = condition != null && !condition.canEncodeStateIntoUrl();
            List<WebResourceTransformation> transformations = webResourceModuleDescriptor.getTransformations();
            boolean hasLegacyTransformers = false;
            for (WebResourceTransformation webResourceTransformation : transformations) {
                if (webResourceTransformation.containsOnlyPureUrlReadingTransformers(this.transformerCache)) continue;
                hasLegacyTransformers = true;
                break;
            }
            HashMap<String, Set<String>> locationResourceTypes = new HashMap<String, Set<String>>();
            for (Object resourceLocation2 : Config.getResourceLocations(webResourceModuleDescriptor, this.integration.isCompiledResourceEnabled())) {
                String nameType = ResourceUtils.getType(resourceLocation2.getName());
                String locationType = ResourceUtils.getType(resourceLocation2.getLocation());
                String nameOrLocationType = nameType.isEmpty() ? locationType : nameType;
                HashSet<String> list = (HashSet<String>)locationResourceTypes.get(nameOrLocationType);
                if (list == null) {
                    list = new HashSet<String>();
                    locationResourceTypes.put(nameOrLocationType, list);
                }
                list.add(locationType);
            }
            ArrayList<String> arrayList = new ArrayList<String>();
            for (String key : webResourceModuleDescriptor.getDependencies()) {
                if (rootPages.containsKey(key)) {
                    msg = "invalid dependency found for \"" + completeKey + "\": \"" + key + "\" cannot be as a dependency because it is tagged as a root-page and will be ignored.";
                    this.supportLogger.error((String)msg);
                    continue;
                }
                if (!Config.isWebResourceKey(key)) {
                    this.supportLogger.warn("the dependency \"" + key + "\" doesn't look like the key of the web resource and will be ignored.");
                    continue;
                }
                if (webResourceDeprecationWarnings.containsKey(key)) {
                    msg = ((Deprecation)webResourceDeprecationWarnings.get(key)).buildLogMessage() + " (required by \"" + completeKey + "\")";
                    if (Flags.isDevMode()) {
                        this.supportLogger.warn((String)msg);
                    } else {
                        this.supportLogger.debug((String)msg);
                    }
                }
                arrayList.add(key);
            }
            resourceLocation2 = webResourceModuleDescriptor.getContextDependencies().iterator();
            while (resourceLocation2.hasNext()) {
                String key;
                key = (String)resourceLocation2.next();
                if (!isRootPage) {
                    msg = "ignoring dependency \"" + key + "\" in \"" + completeKey + "\": context dependencies are only supported in root-pages at the moment";
                    this.supportLogger.error((String)msg);
                    continue;
                }
                if (Config.isWebResourceKey(key)) {
                    this.supportLogger.warn("the context dependency \"" + key + "\" look like the key of the web resource and will be ignored.");
                    continue;
                }
                arrayList.add("_context:" + key);
            }
            TransformerParameters transformerParameters = new TransformerParameters(webResourceModuleDescriptor.getPluginKey(), webResourceModuleDescriptor.getKey());
            WebResource webResource = new WebResource(snapshot, completeKey, arrayList, updatedAt, Config.getPluginVersionOrInstallTime(plugin, this.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins), true, transformerParameters, locationResourceTypes);
            cachedBundles.put(completeKey, webResource);
            if (isRootPage) {
                RootPage rootPage = new RootPage(webResource);
                rootPages.put(completeKey, rootPage);
            }
            if (hasLegacyConditions) {
                webResourcesWithLegacyConditions.add(webResource);
            }
            if (hasLegacyTransformers) {
                webResourcesWithLegacyTransformers.add(webResource);
            }
            if (webResourceModuleDescriptor.isDisableMinification()) {
                webResourcesWithDisabledMinification.add(webResource);
            }
            if (isRootPage && webResourceModuleDescriptor.getContexts().size() > 1) {
                msg = "web-resource \"" + completeKey + "\" cannot be added to a context because it's tagged as a root-page";
                this.supportLogger.error((String)msg);
                throw new RuntimeException((String)msg);
            }
            for (String context : webResourceModuleDescriptor.getContexts()) {
                if (context.equals(completeKey)) continue;
                String contextResourceKey = "_context:" + context;
                IntermediaryContextData contextData = (IntermediaryContextData)intermediaryContexts.get(contextResourceKey);
                if (contextData == null) {
                    contextData = new IntermediaryContextData();
                    intermediaryContexts.put(contextResourceKey, contextData);
                }
                contextData.dependencies.add(completeKey);
            }
            if (condition != null) {
                CachedCondition cachedCondition = conditionInstanceCache.intern(condition);
                webResourcesCondition.put(webResource, cachedCondition);
            }
            if (transformations.isEmpty()) continue;
            webResourcesTransformations.put(webResource, new CachedTransformers(transformations));
        }
        for (Map.Entry entry : intermediaryContexts.entrySet()) {
            String contextResourceKey = (String)entry.getKey();
            IntermediaryContextData contextData = (IntermediaryContextData)entry.getValue();
            this.updateContextData(cachedBundles, contextData);
            cachedBundles.put(contextResourceKey, new Context(snapshot, contextResourceKey, contextData.dependencies, contextData.updatedAt, contextData.version, true));
        }
        this.syncbatchCreated = Optional.of(this.constructSyncbatch(snapshot, cachedBundles));
        this.superbatchCreated = Optional.of(this.constructSuperbatch(snapshot, cachedBundles));
        for (CachedTransformers cachedTransformers : webResourcesTransformations.values()) {
            cachedTransformers.computeParamKeys(this.transformerCache);
        }
        dependencyGraph.reset();
        dependencyGraph = new ResettableLazyReference<DependencyGraph<Requestable>>(){

            protected DependencyGraph<Requestable> create() throws Exception {
                DependencyGraphBuilder builder = DependencyGraph.builder();
                if (Config.this.isSuperbatchCreated()) {
                    SuperBatchKey SUPERBATCH = SuperBatchKey.getInstance();
                    Config.this.batchingConfiguration.getSuperBatchModuleCompleteKeys().forEach(dep -> builder.addWebResourceDependency(SUPERBATCH, (String)dep));
                } else {
                    log.debug("Ignoring super-batch dependencies; super-batching is probably disabled in resource batching configuration.");
                }
                if (Config.this.isSyncContextCreated()) {
                    WebResourceContextKey SYNCBATCH = new WebResourceContextKey(Config.SYNCBATCH_CONTEXT_KEY);
                    Config.this.getSyncWebResourceKeys().stream().map(CompleteWebResourceKey::getCompleteKey).forEach(dep -> builder.addWebResourceDependency(SYNCBATCH, (String)dep));
                }
                builder.addDependencies(webResourceDescriptors);
                return builder.build();
            }
        };
        this.ensureNoLegacyStuff(snapshot);
        return snapshot;
    }

    private boolean constructSyncbatch(Snapshot snapshot, Map<String, Bundle> cachedBundles) {
        List<String> dependencies = this.getSyncWebResourceKeys().stream().map(CompleteWebResourceKey::getCompleteKey).collect(Collectors.toList());
        if (!dependencies.isEmpty()) {
            this.addSpecialContext(snapshot, cachedBundles, SYNCBATCH_KEY, dependencies);
            return true;
        }
        return false;
    }

    private boolean constructSuperbatch(Snapshot snapshot, Map<String, Bundle> cachedBundles) {
        ArrayList<String> dependencies = new ArrayList<String>();
        if (this.isSuperBatchingEnabled()) {
            dependencies.addAll(this.getBeforeAllResources());
            dependencies.addAll(this.batchingConfiguration.getSuperBatchModuleCompleteKeys());
        }
        if (!dependencies.isEmpty()) {
            this.addSpecialContext(snapshot, cachedBundles, SUPERBATCH_KEY, dependencies);
            return true;
        }
        return false;
    }

    private void addSpecialContext(Snapshot snapshot, Map<String, Bundle> bundles, String key, List<String> dependencies) {
        IntermediaryContextData contextData = new IntermediaryContextData();
        contextData.updatedAt = new Date(0L);
        contextData.dependencies = dependencies;
        this.updateContextData(bundles, contextData);
        bundles.put(key, new Bundle(snapshot, key, dependencies, contextData.updatedAt, contextData.version, true));
    }

    private void updateContextData(Map<String, Bundle> bundles, IntermediaryContextData contextData) {
        for (String dependency : contextData.dependencies) {
            Bundle bundle = bundles.get(dependency);
            if (null == bundle) continue;
            Date updatedAt = bundle.getUpdatedAt();
            String version = bundle.getVersion();
            if (contextData.updatedAt == null || contextData.updatedAt.before(updatedAt)) {
                contextData.updatedAt = updatedAt;
            }
            contextData.version = HashBuilder.buildHash(contextData.version, version);
        }
    }

    public LinkedHashMap<String, Resource> getResourcesWithoutCache(Bundle bundle) {
        LinkedHashMap<String, Resource> resources = new LinkedHashMap<String, Resource>();
        WebResourceModuleDescriptor webResourceDescriptor = this.getWebResourceModuleDescriptor(bundle.getKey());
        if (webResourceDescriptor != null) {
            for (ResourceLocation resourceLocation : Config.getResourceLocations(webResourceDescriptor, this.integration.isCompiledResourceEnabled())) {
                Resource resource = this.buildResource(bundle, resourceLocation);
                resources.put(resource.getName(), resource);
            }
        }
        return resources;
    }

    public LinkedHashMap<String, Jsonable> getWebResourceData(String key) {
        LinkedHashMap<String, Jsonable> data = new LinkedHashMap<String, Jsonable>();
        WebResourceModuleDescriptor webResourceDescriptor = this.getWebResourceModuleDescriptor(key);
        if (webResourceDescriptor != null) {
            for (Map.Entry<String, WebResourceDataProvider> entry : webResourceDescriptor.getDataProviders().entrySet()) {
                data.put(entry.getKey(), (Jsonable)entry.getValue().get());
            }
        }
        return data;
    }

    private WebResourceModuleDescriptor getWebResourceModuleDescriptor(String key) {
        ModuleDescriptor moduleDescriptor;
        try {
            moduleDescriptor = this.integration.getPluginAccessor().getEnabledPluginModule(key);
        }
        catch (RuntimeException e) {
            moduleDescriptor = null;
        }
        if (moduleDescriptor == null) {
            return null;
        }
        if (!(moduleDescriptor instanceof WebResourceModuleDescriptor)) {
            return null;
        }
        return (WebResourceModuleDescriptor)moduleDescriptor;
    }

    private Plugin getPlugin(String key) {
        if (key.contains(":")) {
            ModuleDescriptor moduleDescriptor = this.integration.getPluginAccessor().getEnabledPluginModule(key);
            return (Plugin)Preconditions.checkNotNull((Object)moduleDescriptor.getPlugin());
        }
        return (Plugin)Preconditions.checkNotNull((Object)this.integration.getPluginAccessor().getEnabledPlugin(key));
    }

    public boolean isMinificationEnabled() {
        return !Boolean.getBoolean(DISABLE_MINIFICATION) && !Flags.isDevMode();
    }

    protected Resource buildResource(Bundle bundle, ResourceLocation resourceLocation) {
        return this.resourceFactory.createResource(bundle, resourceLocation, ResourceUtils.getType(resourceLocation.getName()), ResourceUtils.getType(resourceLocation.getLocation()));
    }

    public Resource getModuleResource(String completeKey, String name) {
        if (!Config.isWebResourceKey(completeKey)) {
            return null;
        }
        ModuleDescriptor moduleDescriptor = this.integration.getPluginAccessor().getEnabledPluginModule(completeKey);
        if (moduleDescriptor == null) {
            return null;
        }
        ResourceLocation resourceLocation = moduleDescriptor.getResourceLocation(DOWNLOAD_PARAM_VALUE, name);
        if (resourceLocation == null) {
            return null;
        }
        Plugin plugin = moduleDescriptor.getPlugin();
        Date updatedAt = plugin.getDateLoaded() == null ? new Date() : plugin.getDateLoaded();
        PluginResourceContainer resourceContainer = new PluginResourceContainer(new Snapshot(this), completeKey, updatedAt, Config.getPluginVersionOrInstallTime(plugin, this.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins));
        return this.buildResource(resourceContainer, resourceLocation);
    }

    public Resource getPluginResource(String pluginKey, String name) {
        Plugin plugin = this.integration.getPluginAccessor().getPlugin(pluginKey);
        if (plugin == null) {
            return null;
        }
        ResourceLocation resourceLocation = plugin.getResourceLocation(DOWNLOAD_PARAM_VALUE, name);
        if (resourceLocation == null) {
            return null;
        }
        PluginResourceContainer resourceContainer = new PluginResourceContainer(new Snapshot(this), pluginKey, plugin.getDateLoaded(), Config.getPluginVersionOrInstallTime(plugin, this.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins));
        Resource resource = this.buildResource(resourceContainer, resourceLocation);
        return resource;
    }

    public boolean optimiseSourceMapsForDevelopment() {
        return this.batchingConfiguration.optimiseSourceMapsForDevelopment();
    }

    public boolean isSyncBatchingEnabled() {
        return !this.integration.getSyncWebResourceKeys().isEmpty();
    }

    public boolean isSuperBatchingEnabled() {
        return this.batchingConfiguration.isSuperBatchingEnabled() && !this.batchingConfiguration.getSuperBatchModuleCompleteKeys().isEmpty();
    }

    public boolean isBatchContentTrackingEnabled() {
        return this.batchingConfiguration.isBatchContentTrackingEnabled();
    }

    public boolean isContextBatchingEnabled() {
        return this.batchingConfiguration.isContextBatchingEnabled();
    }

    public boolean isWebResourceBatchingEnabled() {
        return this.batchingConfiguration.isPluginWebResourceBatchingEnabled();
    }

    public boolean isIncrementalCacheEnabled() {
        return this.integration.isIncrementalCacheEnabled();
    }

    public int partialHashCode() {
        return this.integration.getSuperBatchVersion().hashCode();
    }

    @Deprecated
    public WebResourceIntegration getIntegration() {
        return this.integration;
    }

    @Deprecated
    public TransformerCache getTransformerCache() {
        return this.transformerCache;
    }

    public int getUrlCacheSize() {
        return this.urlCacheSize;
    }

    @Deprecated
    public ResourceBatchingConfiguration getBatchingConfiguration() {
        return this.batchingConfiguration;
    }

    @Deprecated
    public boolean isDeferJsAttributeEnabled() {
        return this.integration.isDeferJsAttributeEnabled();
    }

    public TimeSpan getDefaultBigPipeDeadline() {
        return this.integration.getBigPipeConfiguration().getDefaultBigPipeDeadline();
    }

    public boolean getBigPipeDeadlineDisabled() {
        return this.integration.getBigPipeConfiguration().getBigPipeDeadlineDisabled();
    }

    public String computeGlobalStateHash() {
        LinkedList<String> state = new LinkedList<String>();
        state.add("productver");
        state.add(this.integration.getHostApplicationVersion());
        state.add("annotators");
        state.add(String.valueOf(this.getContentAnnotator(JS_TYPE).hashCode()));
        state.add(String.valueOf(this.getContentAnnotator(CSS_TYPE).hashCode()));
        state.add("plugins");
        ArrayList plugins = new ArrayList(this.integration.getPluginAccessor().getEnabledPlugins());
        Collections.sort(plugins, this.consistentPluginOrder());
        for (Plugin plugin : plugins) {
            state.add(plugin.getKey());
            state.add(Config.getPluginVersionOrInstallTime(plugin, this.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins));
        }
        String globalProductStateHash = HashBuilder.buildHash(state);
        log.info("Calculated global state hash {} based on {}", (Object)globalProductStateHash, state);
        return globalProductStateHash;
    }

    private Comparator<Plugin> consistentPluginOrder() {
        return Comparator.comparing(p -> p.getKey() + "-" + Config.getPluginVersionOrInstallTime(p, this.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins));
    }

    public List<String> getBeforeAllResources() {
        return Collections.emptyList();
    }

    public CdnResourceUrlTransformer getCdnResourceUrlTransformer() {
        return this.cdnResourceUrlTransformer;
    }

    public boolean isPreBakeEnabled() {
        return Boolean.getBoolean(PREBAKE_FEATURE_ENABLED);
    }

    public static boolean isBundleHashValidationEnabled() {
        String defaultState = Boolean.TRUE.toString();
        String property = System.getProperty(ENABLE_BUNDLE_HASH_VALIDATION, defaultState);
        return !Boolean.FALSE.toString().equalsIgnoreCase(property);
    }

    public boolean isGlobalMinificationEnabled() {
        return this.isMinificationEnabled() && Boolean.getBoolean(GLOBAL_MINIFICATION_ENABLED);
    }

    public String getCtCdnBaseUrl() {
        return System.getProperty(CT_CDN_BASE_URL);
    }

    public ResourceCompiler getResourceCompiler() {
        return this.resourceCompiler;
    }

    public void runResourceCompilation(Snapshot snapshot) {
        LinkedHashSet compilerEntries = new LinkedHashSet();
        RequestCache requestCache = new RequestCache(null);
        snapshot.forEachBundle(bundle -> {
            List entries;
            LinkedHashMap<String, Resource> resources = bundle.getResources(requestCache);
            if (MapUtils.isNotEmpty(resources) && CollectionUtils.isNotEmpty(entries = resources.values().stream().filter(res -> res.getNameOrLocationType().equals(JS_TYPE)).flatMap(resource -> {
                try (InputStream in = resource.getStreamFor(resource.getPath());){
                    Stream stream = in != null ? Stream.of(CompilerEntry.ofKeyValue((String)resource.getPath(), (Object)IOUtils.toString((InputStream)in, (String)CompilerUtil.CHARSET.name()))) : Stream.empty();
                    return stream;
                }
                catch (IOException e) {
                    this.supportLogger.warn(String.format("Error compiling %s", resource.getKey()), (Throwable)e);
                    return Stream.empty();
                }
            }).collect(Collectors.toList()))) {
                compilerEntries.addAll(entries);
            }
        });
        this.resourceCompiler.compile(compilerEntries.stream());
    }

    public boolean resplitMergedContextBatchesForThisRequest() {
        return this.batchingConfiguration.resplitMergedContextBatchesForThisRequest();
    }

    public WebResourceMapper getWebResourceMapper() {
        return (WebResourceMapper)this.cdnUrlMapper.get();
    }

    public void reloadWebResourceMapper() throws Exception {
        NoOpWebResourceMapper noOpWebResourceMapper;
        this.cdnUrlMapper.reset();
        WebResourceMapper wrm = (WebResourceMapper)this.cdnUrlMapper.get();
        if (wrm instanceof NoOpWebResourceMapper && (noOpWebResourceMapper = (NoOpWebResourceMapper)wrm).reason().isPresent()) {
            throw noOpWebResourceMapper.reason().get();
        }
    }

    public boolean isJavaScriptTryCatchWrappingEnabled() {
        return this.batchingConfiguration.isJavaScriptTryCatchWrappingEnabled();
    }

    public boolean isCSSPrebakingEnabled() {
        return Boolean.getBoolean(PREBAKE_CSS_RESOURCES);
    }

    public boolean ignorePrebakeWarnings() {
        return Boolean.getBoolean(IGNORE_PREBAKE_WARNINGS);
    }

    public boolean isUrlCachingEnabled() {
        return this.isUrlCachingEnabled;
    }

    public boolean isUrlGenerationCacheEnabled() {
        return this.isUrlCachingEnabled();
    }

    void setLogger(Logger logger) {
        this.supportLogger = logger;
    }

    public ResourceFactory getResourceFactory() {
        return this.resourceFactory;
    }

    public boolean isPerformanceTrackingEnabled() {
        return !this.getBooleanFromDarkFeatureManagerThenSystemProperty(DISABLE_PERFORMANCE_TRACKING);
    }

    @VisibleForTesting
    protected boolean getBooleanFromDarkFeatureManagerThenSystemProperty(String name) {
        return Optional.ofNullable(this.integration.getDarkFeatureManager()).map(darkFeatureManager -> darkFeatureManager.isEnabledForAllUsers(name)).flatMap(val -> val).orElseGet(() -> Boolean.getBoolean(name) || Boolean.getBoolean("atlassian.darkfeature." + name));
    }

    protected static class IntermediaryContextData {
        public List<String> dependencies = new ArrayList<String>();
        public Date updatedAt;
        public String version = "";

        protected IntermediaryContextData() {
        }
    }
}

