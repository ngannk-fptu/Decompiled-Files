/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.plugin.webresource.impl.http;

import com.atlassian.plugin.webresource.ResourceUtils;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.discovery.BundleFinder;
import com.atlassian.plugin.webresource.impl.discovery.ResourceFinder;
import com.atlassian.plugin.webresource.impl.helpers.BaseHelpers;
import com.atlassian.plugin.webresource.impl.helpers.ResourceServingHelpers;
import com.atlassian.plugin.webresource.impl.http.Controller;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.support.http.BaseRouter;
import com.atlassian.plugin.webresource.impl.support.http.Request;
import com.atlassian.plugin.webresource.impl.support.http.Response;
import com.atlassian.plugin.webresource.impl.support.http.ServingType;
import com.atlassian.plugin.webresource.models.LooselyTypedRequestExpander;
import com.atlassian.plugin.webresource.models.RawRequest;
import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router
extends BaseRouter<Controller> {
    public Router(final Globals globals) {
        super(globals);
        String resources = "resources";
        this.addRoute("/resources/:completeKey/*resourceName.map", new BaseRouter.Handler(){

            public void apply(Controller controller, String escapedCompleteKey, String escapedResourceName) {
                controller.serveResourceSourceMap(Router.unescapeSlashes(escapedCompleteKey), Router.unescapeSlashes(escapedResourceName), ServingType.RESOURCES_SINGLE);
            }
        });
        this.addRoute("/resources/:completeKey/*resourceName", new BaseRouter.Handler(){

            public void apply(Controller controller, String escapedCompleteKey, String escapedResourceName) {
                controller.serveResource(Router.unescapeSlashes(escapedCompleteKey), Router.unescapeSlashes(escapedResourceName), ServingType.RESOURCES_SINGLE);
            }
        });
        this.addRoute("/sources/:completeKey/*resourceName", new BaseRouter.Handler(){

            public void apply(Controller controller, String escapedCompleteKey, String escapedResourceName) {
                controller.serveSource(Router.unescapeSlashes(escapedCompleteKey), Router.unescapeSlashes(escapedResourceName), ServingType.SOURCES_SINGLE);
            }
        });
        this.addRoute("/contextbatch/:type/:encodedContexts/*batchPostfixOrResourceName.map", new BaseRouter.Handler(){

            public void apply(Controller controller, String type, String encodedContexts, String escapedBatchPostfixOrResourceName) {
                String batchPostfixOrResourceName = Router.unescapeSlashes(escapedBatchPostfixOrResourceName);
                RawRequest raw = Router.decodeContexts(encodedContexts);
                if (batchPostfixOrResourceName.equals("batch." + type)) {
                    controller.serveBatchSourceMap(raw, ServingType.CONTEXTBATCH, type, true, false);
                } else {
                    controller.serveResourceRelativeToBatchSourceMap(raw, batchPostfixOrResourceName, ServingType.CONTEXTBATCH_RESOURCE, true, false);
                }
            }
        });
        this.addRoute("/contextbatch/:type/:encodedContexts/*batchPostfixOrResourceName", new BaseRouter.Handler(){

            public void apply(Controller controller, String type, String encodedContexts, String escapedBatchPostfixOrResourceName) {
                String batchPostfixOrResourceName = Router.unescapeSlashes(escapedBatchPostfixOrResourceName);
                RawRequest raw = Router.decodeContexts(encodedContexts);
                if (batchPostfixOrResourceName.equals("batch." + type)) {
                    controller.serveBatch(raw, ServingType.CONTEXTBATCH, type, true, false, true, true);
                } else {
                    controller.serveResourceRelativeToBatch(raw, batchPostfixOrResourceName, ServingType.CONTEXTBATCH_RESOURCE, true, false);
                }
            }
        });
        this.addRoute("/batch/:completeKey/*batchPostfixOrResourceName.map", new BaseRouter.Handler(){

            public void apply(Controller controller, String escapedCompleteKey, String escapedBatchPostfixOrResourceName) {
                String completeKey = Router.unescapeSlashes(escapedCompleteKey);
                String batchPostfixOrResourceName = Router.unescapeSlashes(escapedBatchPostfixOrResourceName);
                RawRequest raw = new RawRequest();
                raw.include(new WebResourceKey(completeKey));
                if (completeKey.equals(ResourceUtils.getBasename(batchPostfixOrResourceName))) {
                    controller.serveBatchSourceMap(raw, ServingType.BATCH, ResourceUtils.getType(batchPostfixOrResourceName), false, true);
                } else {
                    controller.serveResourceRelativeToBatchSourceMap(raw, batchPostfixOrResourceName, ServingType.BATCH_RESOURCE, false, true);
                }
            }
        });
        this.addRoute("/batch/:completeKey/*batchPostfixOrResourceName", new BaseRouter.Handler(){

            public void apply(Request request, Response response, Controller controller, String[] arguments) {
                String completeKey = Router.unescapeSlashes(arguments[0]);
                String batchPostfixOrResourceName = Router.unescapeSlashes(arguments[1]);
                RawRequest raw = new RawRequest();
                raw.include(new WebResourceKey(completeKey));
                String type = ResourceUtils.getType(batchPostfixOrResourceName);
                if (completeKey.equals(ResourceUtils.getBasename(batchPostfixOrResourceName))) {
                    controller.serveBatch(raw, ServingType.BATCH, type, false, true, false, false);
                } else {
                    RequestCache requestCache = new RequestCache(globals);
                    LooselyTypedRequestExpander expander = new LooselyTypedRequestExpander(raw);
                    BundleFinder bundles = new BundleFinder(globals.getSnapshot()).included(expander.getIncluded()).excluded(expander.getExcluded(), BaseHelpers.isConditionsSatisfied(requestCache, request.getParams())).deep(false).deepFilter(BaseHelpers.isConditionsSatisfied(requestCache, request.getParams()));
                    ResourceFinder resources = new ResourceFinder(bundles, requestCache).filter(ResourceServingHelpers.shouldBeIncludedInBatch(type, request.getParams()));
                    if (!resources.end().isEmpty()) {
                        controller.serveBatch(raw, ServingType.BATCH, type, false, true, false, false);
                    } else {
                        controller.serveResourceRelativeToBatch(raw, batchPostfixOrResourceName, ServingType.BATCH_RESOURCE, false, true);
                    }
                }
            }
        });
    }

    protected Router(Globals globals, List<BaseRouter.Route> routes, boolean useAbsoluteUrl) {
        super(globals, routes, useAbsoluteUrl);
    }

    public static String resourceUrlAsStaticMethod(String completeKey, String resourceName, Map<String, String> params) {
        return Router.buildUrl(Router.interpolate("/resources/:completeKey/:resourceName", completeKey, resourceName), params);
    }

    public static String encodeContexts(Collection<String> includedContexts, Iterable<String> excludedContexts) {
        Iterable<String> excludedContextsList;
        Collection<String> includedContextsList;
        if (Config.isStaticContextOrderEnabled()) {
            includedContextsList = Router.sortContextList(includedContexts);
            excludedContextsList = Router.sortContextList(excludedContexts);
        } else {
            includedContextsList = includedContexts;
            excludedContextsList = excludedContexts;
        }
        String prefix = "_context:";
        StringBuilder buff = new StringBuilder();
        for (String context : includedContextsList) {
            buff.append(context.replace(prefix, "")).append(",");
        }
        for (String context : excludedContextsList) {
            buff.append("-").append(context.replace(prefix, "")).append(",");
        }
        buff.deleteCharAt(buff.length() - 1);
        return buff.toString();
    }

    private static List<String> sortContextList(Iterable<String> contexts) {
        ArrayList contextList = Lists.newArrayList(contexts);
        Collections.sort(contextList);
        return contextList;
    }

    public static RawRequest decodeContexts(String encodedContexts) {
        String[] tokens;
        RawRequest request = new RawRequest();
        for (String token : tokens = encodedContexts.split(",")) {
            if (token.startsWith("-")) {
                request.exclude(new WebResourceContextKey(token.substring(1)));
                continue;
            }
            request.include(new WebResourceContextKey(token));
        }
        return request;
    }

    public static boolean isSourceMap(Request request) {
        return request.isSourceMap();
    }

    public static String sourceMapUrlToUrl(String sourceMapUrl) {
        return sourceMapUrl.replaceAll("\\.map$", "").replaceAll("\\.map\\?", "?");
    }

    public static String escapeSlashes(String string) {
        return string.replaceAll("/", "::");
    }

    public static String unescapeSlashes(String string) {
        return string.replaceAll("::", "/");
    }

    public String contextBatchUrl(String key, String type, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String resourceHash, String bundleHash) {
        return this.buildUrlWithPrefix(Router.interpolate("/contextbatch/:type/:key/batch.:type", type, key, type), params, isResourceSupportCache, isResourceSupportCdn, resourceHash, bundleHash);
    }

    public String contextBatchSourceMapUrl(String key, String type, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String resourceHash, String bundleHash) {
        return this.buildUrlWithPrefix(Router.interpolate("/contextbatch/:type/:key/batch.:type.map", type, key, type), params, isResourceSupportCache, isResourceSupportCdn, resourceHash, bundleHash);
    }

    public String resourceUrlRelativeToContextBatch(String key, String type, String resourceName, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String resourceHash, String bundleHash) {
        return this.buildUrlWithPrefix(Router.interpolate("/contextbatch/:type/:key/:resourceName", type, key, resourceName), params, isResourceSupportCache, isResourceSupportCdn, resourceHash, bundleHash);
    }

    public String resourceUrl(String completeKey, String resourceName, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String resourceHash, String bundleHash) {
        return this.buildUrlWithPrefix(Router.interpolate("/resources/:completeKey/:resourceName", Router.escapeSlashes(completeKey), resourceName), params, isResourceSupportCache, isResourceSupportCdn, resourceHash, bundleHash);
    }

    public String resourceSourceMapUrl(String completeKey, String resourceName, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String resourceHash, String bundleHash) {
        return this.buildUrlWithPrefix(Router.interpolate("/resources/:completeKey/:resourceName.map", Router.escapeSlashes(completeKey), resourceName), params, isResourceSupportCache, isResourceSupportCdn, resourceHash, bundleHash);
    }

    public String pluginResourceUrl(String pluginKey, String resourceName, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String resourceHash, String bundleHash) {
        return this.buildUrlWithPrefix(Router.interpolate("/resources/:pluginKey/:resourceName", pluginKey, resourceName), params, isResourceSupportCache, isResourceSupportCdn, resourceHash, bundleHash);
    }

    public String resourceUrlWithoutHash(Resource resource, Map<String, String> params) {
        return Router.buildUrl(this.globals.getConfig().getBaseUrl() + Router.interpolate("/resources/:completeKey/:resourceName", resource.getKey(), resource.getName()), params);
    }

    public String sourceUrl(String completeKey, String resourceName, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String resourceHash, String bundleHash) {
        return Router.buildUrl(this.globals.getConfig().getBaseUrl() + Router.interpolate("/sources/:completeKey/:resourceName", Router.escapeSlashes(completeKey), resourceName), params);
    }

    public String prebuildSourceUrl(String completeKey, String resourceName, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String resourceHash, String bundleHash) {
        return this.sourceUrl(completeKey, Resource.getPrebuiltSourcePath(resourceName), params, isResourceSupportCache, isResourceSupportCdn, resourceHash, bundleHash);
    }

    public String sourceUrl(Resource resource) {
        return this.sourceUrl(resource.getKey(), resource.getName(), new HashMap<String, String>(), true, true, "", resource.getVersion());
    }

    public String prebuildSourceUrl(Resource resource) {
        return this.prebuildSourceUrl(resource.getKey(), resource.getName(), new HashMap<String, String>(), true, true, "", resource.getVersion());
    }

    public String webResourceBatchUrl(String completeKey, String type, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String resourceHash, String bundleHash) {
        String encodedCompleteKey = Router.escapeSlashes(completeKey);
        return this.buildUrlWithPrefix(Router.interpolate("/batch/:completeKey/:completeKey.:type", encodedCompleteKey, encodedCompleteKey, type), params, isResourceSupportCache, isResourceSupportCdn, resourceHash, bundleHash);
    }

    public String resourceUrlRelativeToWebResourceBatch(String completeKey, String resourceName, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String resourceHash, String bundleHash) {
        return this.buildUrlWithPrefix(Router.interpolate("/batch/:completeKey/:resourceName", Router.escapeSlashes(completeKey), resourceName), params, isResourceSupportCache, isResourceSupportCdn, resourceHash, bundleHash);
    }

    public String sourceMapUrl(String resourceUrl, Map<String, String> generatedParams) {
        return Router.buildUrl(resourceUrl + ".map", generatedParams);
    }

    public Router cloneWithNewUrlMode(boolean useAbsoluteUrl) {
        return new Router(this.globals, this.routes, useAbsoluteUrl);
    }

    public String buildUrlWithPrefix(String url, Map<String, String> params, boolean isResourceSupportCache, boolean isResourceSupportCdn, String hash, String bundleHash) {
        String urlWithParams = Router.buildUrl(url, params);
        Config config = this.globals.getConfig();
        if (isResourceSupportCache) {
            String hashWithCdnMark = hash + (isResourceSupportCdn ? "-CDN" : "-T");
            if (isResourceSupportCdn && config.isCdnEnabled()) {
                String prefix = config.getResourceUrlPrefix(hashWithCdnMark, bundleHash, false);
                return config.getResourceCdnPrefix(prefix + urlWithParams);
            }
            String prefix = config.getResourceUrlPrefix(hashWithCdnMark, bundleHash, this.useAbsoluteUrl);
            return prefix + urlWithParams;
        }
        return config.getBaseUrl(this.useAbsoluteUrl) + urlWithParams;
    }

    @Override
    protected Controller createController(Globals globals, Request request, Response response) {
        return new Controller(globals, request, response);
    }
}

