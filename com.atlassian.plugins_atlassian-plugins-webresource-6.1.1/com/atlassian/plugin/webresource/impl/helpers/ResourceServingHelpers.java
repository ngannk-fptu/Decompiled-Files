/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.transformer.TwoPhaseResourceTransformer
 *  com.atlassian.sourcemap.ReadableSourceMap
 *  com.atlassian.sourcemap.SourceMapJoiner
 *  com.atlassian.sourcemap.Util
 *  com.atlassian.sourcemap.WritableSourceMap
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugin.webresource.impl.helpers;

import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.WebResourceTransformation;
import com.atlassian.plugin.webresource.impl.CachedTransformers;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.annotators.ResourceContentAnnotator;
import com.atlassian.plugin.webresource.impl.helpers.url.UrlGenerationHelpers;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.WebResource;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.plugin.webresource.impl.support.ContentImpl;
import com.atlassian.plugin.webresource.impl.support.LineCountingProxyOutputStream;
import com.atlassian.plugin.webresource.impl.support.SourceMapJoinerStub;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.plugin.webresource.impl.support.factory.InitialContentFactory;
import com.atlassian.plugin.webresource.impl.support.http.BaseRouter;
import com.atlassian.plugin.webresource.transformer.StaticTransformers;
import com.atlassian.plugin.webresource.transformer.TwoPhaseResourceTransformer;
import com.atlassian.sourcemap.ReadableSourceMap;
import com.atlassian.sourcemap.SourceMapJoiner;
import com.atlassian.sourcemap.Util;
import com.atlassian.sourcemap.WritableSourceMap;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourceServingHelpers
extends UrlGenerationHelpers {
    public static Resource getResource(RequestCache requestCache, String completeKey, String resourceName) {
        Resource resource = ResourceServingHelpers.getWebResourceResource(requestCache, completeKey, resourceName);
        if (resource == null) {
            resource = ResourceServingHelpers.getResourceRelativeToWebResource(requestCache, completeKey, resourceName);
        }
        if (resource == null) {
            resource = ResourceServingHelpers.getFromOSGiPluginModuleResource(requestCache.getGlobals(), completeKey, resourceName);
        }
        if (resource == null) {
            resource = ResourceServingHelpers.getPluginResource(requestCache.getGlobals(), completeKey, resourceName);
        }
        if (resource == null) {
            resource = ResourceServingHelpers.getResourceRelativeToPlugin(requestCache.getGlobals(), completeKey, resourceName);
        }
        return resource;
    }

    public static Resource getResource(RequestCache requestCache, Collection<String> bundles, String resourceName) {
        String key;
        Resource resource = null;
        Iterator<String> iterator = bundles.iterator();
        while (iterator.hasNext() && (resource = ResourceServingHelpers.getResource(requestCache, key = iterator.next(), resourceName)) == null) {
        }
        return resource;
    }

    public static Content transform(final Globals globals, final LinkedHashSet<String> requiredResources, final String url, final String type, Supplier<Collection<Resource>> resourcesSupplier, final Map<String, String> params) {
        final Collection<Resource> resources = resourcesSupplier.get();
        return new ContentImpl(null, true){

            @Override
            public boolean isPresent() {
                return !resources.isEmpty();
            }

            @Override
            public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                ResourceContentAnnotator annotator = globals.getConfig().getContentAnnotator(type);
                try {
                    SourceMapJoiner sourceMapJoiner = isSourceMapEnabled ? new SourceMapJoiner() : new SourceMapJoinerStub();
                    boolean isFirst = true;
                    for (Resource resource : resources) {
                        if (!isFirst) {
                            out.write(10);
                        }
                        LineCountingProxyOutputStream lineCountingStream = new LineCountingProxyOutputStream(out);
                        OutputStream outOrLineCountingStream = isSourceMapEnabled ? lineCountingStream : out;
                        int offset = 0;
                        if (isFirst) {
                            offset += annotator.beforeAllResourcesInBatch(requiredResources, url, params, outOrLineCountingStream);
                        }
                        Content content = ResourceServingHelpers.transformSafely(globals, requiredResources, url, resource, params, false);
                        ReadableSourceMap sourceMap = content.writeTo(outOrLineCountingStream, isSourceMapEnabled);
                        int resourceLength = lineCountingStream.getLinesCount() - (offset += annotator.beforeResourceInBatch(requiredResources, resource, params, outOrLineCountingStream));
                        annotator.afterResourceInBatch(requiredResources, resource, params, outOrLineCountingStream);
                        if (isSourceMapEnabled && sourceMap == null) {
                            String singleResourceUrl = globals.getRouter().resourceUrlWithoutHash(resource, params);
                            sourceMap = WritableSourceMap.toReadableSourceMap((WritableSourceMap)Util.create1to1SourceMap((int)resourceLength, (String)singleResourceUrl));
                        }
                        sourceMapJoiner.add(sourceMap, lineCountingStream.getLinesCount(), offset);
                        if (!isFirst) continue;
                        isFirst = false;
                    }
                    annotator.afterAllResourcesInBatch(requiredResources, url, params, out);
                    return WritableSourceMap.toReadableSourceMap((WritableSourceMap)sourceMapJoiner.join());
                }
                catch (IOException e) {
                    Support.logIOException(e);
                    return null;
                }
            }
        };
    }

    public static Content transform(final Globals globals, LinkedHashSet<String> requiredResources, String url, final Resource resource, final Map<String, String> params, boolean applyAnnotators) {
        final Content content = ResourceServingHelpers.transformWithoutCache(globals, requiredResources, url, resource, params, applyAnnotators);
        if (resource.getParent().hasLegacyTransformers() || globals.getConfig().isSourceMapEnabledFor(resource.getNameOrLocationType())) {
            return content;
        }
        return new ContentImpl(content.getContentType(), content.isTransformed()){

            @Override
            public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                if (isSourceMapEnabled) {
                    Support.LOGGER.warn("internal error, source map could not be used with incremental transformer cache!");
                }
                String key = ResourceServingHelpers.buildKey(globals, resource, params);
                globals.getTemporaryIncrementalCache().cache("transformer", key, out, out1 -> content.writeTo(out1, isSourceMapEnabled));
                return null;
            }
        };
    }

    public static String buildKey(Globals globals, Resource resource, Map<String, String> params) {
        String baseKey = resource.getKey() + ":" + resource.getPath();
        if (resource.getParent() instanceof WebResource) {
            WebResource webResource = (WebResource)resource.getParent();
            HashSet<String> usedParameterKeys = new HashSet<String>();
            CachedTransformers transformers = webResource.getTransformers();
            if (transformers != null) {
                usedParameterKeys.addAll(transformers.getParamKeys());
            }
            usedParameterKeys.addAll(globals.getConfig().getStaticTransformers().getParamKeys());
            HashMap<String, String> usedParameters = new HashMap<String, String>();
            for (String key : usedParameterKeys) {
                if (!params.containsKey(key)) continue;
                usedParameters.put(key, params.get(key));
            }
            return BaseRouter.buildUrl(baseKey, usedParameters);
        }
        return baseKey;
    }

    public static Content transformWithoutCache(final Globals globals, final LinkedHashSet<String> requiredResources, final String url, final Resource resource, final Map<String, String> params, boolean applyAnnotators) {
        Preconditions.checkArgument((!resource.isRedirect() ? 1 : 0) != 0, (Object)"can't transform redirect resource!");
        Content content = resource.getContent();
        if (!resource.isTransformable()) {
            return content;
        }
        content = new InitialContentFactory(globals).lookup(resource).toContent(content);
        String resourceAsSourceUrl = globals.getRouter().sourceUrl(resource);
        content = ResourceServingHelpers.applyTransformers(globals, resource, content, params, resourceAsSourceUrl);
        content = ResourceServingHelpers.applyStaticTransformers(globals, resource, content, params, resourceAsSourceUrl);
        if (applyAnnotators) {
            final Content immutableContent = content;
            return new ContentImpl(content.getContentType(), true){

                @Override
                public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                    ResourceContentAnnotator annotator = globals.getConfig().getContentAnnotator(resource.getNameOrLocationType());
                    try {
                        int offset = annotator.beforeResource(requiredResources, url, resource, params, out);
                        ReadableSourceMap sourceMap = immutableContent.writeTo(out, isSourceMapEnabled);
                        if (isSourceMapEnabled && sourceMap != null) {
                            sourceMap.addOffset(offset);
                        }
                        annotator.afterResource(requiredResources, url, resource, params, out);
                        return sourceMap;
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
        return content;
    }

    public static Content transformSafely(Globals globals, LinkedHashSet<String> requiredResources, String url, Resource resource, Map<String, String> params, boolean applyAnnotators) {
        Content content;
        try {
            content = ResourceServingHelpers.transform(globals, requiredResources, url, resource, params, applyAnnotators);
        }
        catch (RuntimeException e) {
            Support.LOGGER.warn("can't transform resource " + resource.getKey() + ":" + resource.getName(), (Throwable)e);
            content = ResourceServingHelpers.buildEmptyContent(null);
        }
        return ResourceServingHelpers.buildSafeContent(content, resource.getFullName());
    }

    public static Predicate<Resource> shouldBeIncludedInBatch(String type, Map<String, String> params) {
        return resource -> type.equals(resource.getNameOrLocationType()) && resource.isBatchable(params);
    }

    public static DownloadableResource asDownloadableResource(final Content content) {
        return new DownloadableResource(){

            public boolean isResourceModified(HttpServletRequest request, HttpServletResponse response) {
                throw new RuntimeException("not supported for content wrapper!");
            }

            public void serveResource(HttpServletRequest request, HttpServletResponse response) {
                throw new RuntimeException("not supported for content wrapper!");
            }

            public void streamResource(OutputStream out) {
                content.writeTo(out, false);
            }

            public String getContentType() {
                return content.getContentType();
            }
        };
    }

    public static Content asContent(final DownloadableResource downloadableResource, final ReadableSourceMap sourceMap, boolean isTransformed) {
        return new ContentImpl(downloadableResource.getContentType(), isTransformed){

            @Override
            public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                try {
                    downloadableResource.streamResource(out);
                }
                catch (DownloadException e) {
                    Support.LOGGER.debug("Error while serving file: DownloadException");
                }
                return sourceMap;
            }
        };
    }

    public static Content buildEmptyContent(String contentType) {
        return new ContentImpl(contentType, false){

            @Override
            public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                return null;
            }
        };
    }

    public static Content buildSafeContent(final Content content, final String fullResourceName) {
        return new Content(){

            @Override
            public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                try {
                    return content.writeTo(out, isSourceMapEnabled);
                }
                catch (RuntimeException e) {
                    Support.LOGGER.warn("error in `Content.writeTo` for " + fullResourceName, (Throwable)e);
                    return null;
                }
            }

            @Override
            public String getContentType() {
                try {
                    return content.getContentType();
                }
                catch (RuntimeException e) {
                    Support.LOGGER.warn("error in `Content.getContentType` for " + fullResourceName, (Throwable)e);
                    return null;
                }
            }

            @Override
            public boolean isTransformed() {
                try {
                    return content.isTransformed();
                }
                catch (RuntimeException e) {
                    Support.LOGGER.warn("error in `Content.isTransformed` for " + fullResourceName, (Throwable)e);
                    return false;
                }
            }
        };
    }

    protected static Content applyTransformers(Globals globals, Resource resource, Content content, Map<String, String> params, String sourceUrl) {
        if (resource.getParent() instanceof WebResource) {
            WebResource webResource = (WebResource)resource.getParent();
            CachedTransformers transformers = webResource.getTransformers();
            if (transformers == null) {
                return content;
            }
            Content lastContent = content;
            for (WebResourceTransformation transformation : transformers.getTransformations()) {
                lastContent = ResourceServingHelpers.repairSourceMapChain(lastContent, innerLastContent -> {
                    if (transformation.matches(resource.getResourceLocation())) {
                        if (transformation instanceof TwoPhaseResourceTransformer) {
                            ((TwoPhaseResourceTransformer)transformation).loadTwoPhaseProperties(resource.getResourceLocation(), resource::getStreamFor);
                        }
                        innerLastContent = transformation.transform(globals.getConfig().getCdnResourceUrlTransformer(), globals.getConfig().getTransformerCache(), resource, innerLastContent, resource.getResourceLocation(), QueryParams.of((Map)params), sourceUrl);
                    }
                    return innerLastContent;
                });
            }
            return lastContent;
        }
        return content;
    }

    protected static Content repairSourceMapChain(final Content input, RepairSourceMapChainCallback cb) {
        final boolean[] isInputSourceMapEnabled = new boolean[1];
        final ReadableSourceMap[] inputSourceMap = new ReadableSourceMap[1];
        ContentImpl wrapper = new ContentImpl(input.getContentType(), input.isTransformed()){

            @Override
            public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                inputSourceMap[0] = input.writeTo(out, isInputSourceMapEnabled[0]);
                return inputSourceMap[0];
            }
        };
        final Content transformed = cb.apply(wrapper);
        return new ContentImpl(transformed.getContentType(), transformed.isTransformed()){

            @Override
            public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                isInputSourceMapEnabled[0] = isSourceMapEnabled;
                ReadableSourceMap sourceMap = transformed.writeTo(out, isSourceMapEnabled);
                return sourceMap != null ? sourceMap : inputSourceMap[0];
            }
        };
    }

    protected static Content applyStaticTransformers(Globals globals, Resource resource, Content content, Map<String, String> params, String sourceUrl) {
        if (resource.getParent() instanceof WebResource) {
            return ResourceServingHelpers.repairSourceMapChain(content, innerContent -> {
                StaticTransformers staticTransformers = globals.getConfig().getStaticTransformers();
                staticTransformers.loadTwoPhaseProperties(resource.getResourceLocation(), resource::getStreamFor);
                return staticTransformers.transform(innerContent, resource.getParent().getTransformerParameters(), resource.getResourceLocation(), QueryParams.of((Map)params), sourceUrl);
            });
        }
        return content;
    }

    protected static Resource getWebResourceResource(RequestCache requestCache, String completeKey, String resourceName) {
        Bundle bundle = requestCache.getSnapshot().get(completeKey);
        if (bundle == null) {
            return null;
        }
        return bundle.getResources(requestCache).get(resourceName);
    }

    public static Resource getFromOSGiPluginModuleResource(Globals globals, String completeKeyOrPluginKey, String resourceName) {
        return globals.getConfig().getModuleResource(completeKeyOrPluginKey, resourceName);
    }

    @Deprecated
    protected static Resource getResourceRelativeToWebResource(RequestCache requestCache, String completeKey, String resourceName) {
        Bundle bundle = requestCache.getSnapshot().get(completeKey);
        if (bundle == null) {
            return null;
        }
        String filePath = "";
        Resource resource = null;
        while (resource == null) {
            String[] parts = ResourceServingHelpers.splitLastPathPart(resourceName);
            if (parts == null) {
                return null;
            }
            resourceName = parts[0];
            filePath = parts[1] + filePath;
            resource = bundle.getResources(requestCache).get(resourceName);
        }
        String finalFilePath = filePath;
        return requestCache.getGlobals().getConfig().getResourceFactory().createResourceWithRelativePath(resource.getParent(), resource.getResourceLocation(), resource.getNameType(), resource.getLocationType(), finalFilePath);
    }

    protected static Resource getPluginResource(Globals globals, String completeKeyOrPluginKey, String resourceName) {
        return globals.getConfig().getPluginResource(ResourceServingHelpers.getPluginKey(completeKeyOrPluginKey), resourceName);
    }

    @Deprecated
    protected static Resource getResourceRelativeToPlugin(Globals globals, String completeKeyOrPluginKey, String resourceName) {
        String pluginKey = ResourceServingHelpers.getPluginKey(completeKeyOrPluginKey);
        String filePath = "";
        Resource resource = null;
        while (resource == null) {
            String[] parts = ResourceServingHelpers.splitLastPathPart(resourceName);
            if (parts == null) {
                return null;
            }
            resourceName = parts[0];
            filePath = parts[1] + filePath;
            resource = globals.getConfig().getPluginResource(pluginKey, resourceName);
        }
        String finalFilePath = filePath;
        return globals.getConfig().getResourceFactory().createResourceWithRelativePath(resource.getParent(), resource.getResourceLocation(), resource.getNameType(), resource.getLocationType(), finalFilePath);
    }

    @Deprecated
    public static String[] splitLastPathPart(String resourcePath) {
        int indexOfSlash = resourcePath.lastIndexOf(47);
        if (resourcePath.endsWith("/")) {
            indexOfSlash = resourcePath.lastIndexOf(47, indexOfSlash - 1);
        }
        if (indexOfSlash < 0) {
            return null;
        }
        return new String[]{resourcePath.substring(0, indexOfSlash + 1), resourcePath.substring(indexOfSlash + 1)};
    }

    protected static String getPluginKey(String completeKeyOrPluginKey) {
        return completeKeyOrPluginKey.split(":")[0];
    }

    protected static interface RepairSourceMapChainCallback {
        public Content apply(Content var1);
    }
}

