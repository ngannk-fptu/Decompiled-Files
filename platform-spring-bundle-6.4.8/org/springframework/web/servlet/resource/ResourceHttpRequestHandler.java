/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet.resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.DefaultResourceResolverChain;
import org.springframework.web.servlet.resource.DefaultResourceTransformerChain;
import org.springframework.web.servlet.resource.HttpResource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.UrlPathHelper;

public class ResourceHttpRequestHandler
extends WebContentGenerator
implements HttpRequestHandler,
EmbeddedValueResolverAware,
InitializingBean,
CorsConfigurationSource {
    private static final Log logger = LogFactory.getLog(ResourceHttpRequestHandler.class);
    private static final String URL_RESOURCE_CHARSET_PREFIX = "[charset=";
    private final List<String> locationValues = new ArrayList<String>(4);
    private final List<Resource> locationResources = new ArrayList<Resource>(4);
    private final List<Resource> locationsToUse = new ArrayList<Resource>(4);
    private final Map<Resource, Charset> locationCharsets = new HashMap<Resource, Charset>(4);
    private final List<ResourceResolver> resourceResolvers = new ArrayList<ResourceResolver>(4);
    private final List<ResourceTransformer> resourceTransformers = new ArrayList<ResourceTransformer>(4);
    @Nullable
    private ResourceResolverChain resolverChain;
    @Nullable
    private ResourceTransformerChain transformerChain;
    @Nullable
    private ResourceHttpMessageConverter resourceHttpMessageConverter;
    @Nullable
    private ResourceRegionHttpMessageConverter resourceRegionHttpMessageConverter;
    @Nullable
    private ContentNegotiationManager contentNegotiationManager;
    private final Map<String, MediaType> mediaTypes = new HashMap<String, MediaType>(4);
    @Nullable
    private CorsConfiguration corsConfiguration;
    @Nullable
    private UrlPathHelper urlPathHelper;
    private boolean useLastModified = true;
    private boolean optimizeLocations = false;
    @Nullable
    private StringValueResolver embeddedValueResolver;

    public ResourceHttpRequestHandler() {
        super(HttpMethod.GET.name(), HttpMethod.HEAD.name());
    }

    public void setLocationValues(List<String> locations) {
        Assert.notNull(locations, "Locations list must not be null");
        this.locationValues.clear();
        this.locationValues.addAll(locations);
    }

    public void setLocations(List<Resource> locations) {
        Assert.notNull(locations, "Locations list must not be null");
        this.locationResources.clear();
        this.locationResources.addAll(locations);
    }

    public List<Resource> getLocations() {
        if (this.locationsToUse.isEmpty()) {
            return this.locationResources;
        }
        return this.locationsToUse;
    }

    public void setResourceResolvers(@Nullable List<ResourceResolver> resourceResolvers) {
        this.resourceResolvers.clear();
        if (resourceResolvers != null) {
            this.resourceResolvers.addAll(resourceResolvers);
        }
    }

    public List<ResourceResolver> getResourceResolvers() {
        return this.resourceResolvers;
    }

    public void setResourceTransformers(@Nullable List<ResourceTransformer> resourceTransformers) {
        this.resourceTransformers.clear();
        if (resourceTransformers != null) {
            this.resourceTransformers.addAll(resourceTransformers);
        }
    }

    public List<ResourceTransformer> getResourceTransformers() {
        return this.resourceTransformers;
    }

    public void setResourceHttpMessageConverter(@Nullable ResourceHttpMessageConverter messageConverter) {
        this.resourceHttpMessageConverter = messageConverter;
    }

    @Nullable
    public ResourceHttpMessageConverter getResourceHttpMessageConverter() {
        return this.resourceHttpMessageConverter;
    }

    public void setResourceRegionHttpMessageConverter(@Nullable ResourceRegionHttpMessageConverter messageConverter) {
        this.resourceRegionHttpMessageConverter = messageConverter;
    }

    @Nullable
    public ResourceRegionHttpMessageConverter getResourceRegionHttpMessageConverter() {
        return this.resourceRegionHttpMessageConverter;
    }

    @Deprecated
    public void setContentNegotiationManager(@Nullable ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    @Nullable
    @Deprecated
    public ContentNegotiationManager getContentNegotiationManager() {
        return this.contentNegotiationManager;
    }

    public void setMediaTypes(Map<String, MediaType> mediaTypes) {
        mediaTypes.forEach((ext, mediaType) -> this.mediaTypes.put(ext.toLowerCase(Locale.ENGLISH), (MediaType)mediaType));
    }

    public Map<String, MediaType> getMediaTypes() {
        return this.mediaTypes;
    }

    public void setCorsConfiguration(CorsConfiguration corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }

    @Override
    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        return this.corsConfiguration;
    }

    public void setUrlPathHelper(@Nullable UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Nullable
    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    public void setUseLastModified(boolean useLastModified) {
        this.useLastModified = useLastModified;
    }

    public boolean isUseLastModified() {
        return this.useLastModified;
    }

    public void setOptimizeLocations(boolean optimizeLocations) {
        this.optimizeLocations = optimizeLocations;
    }

    public boolean isOptimizeLocations() {
        return this.optimizeLocations;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PathExtensionContentNegotiationStrategy strategy;
        ContentNegotiationManager manager;
        this.resolveResourceLocations();
        if (this.resourceResolvers.isEmpty()) {
            this.resourceResolvers.add(new PathResourceResolver());
        }
        this.initAllowedLocations();
        this.resolverChain = new DefaultResourceResolverChain(this.resourceResolvers);
        this.transformerChain = new DefaultResourceTransformerChain(this.resolverChain, this.resourceTransformers);
        if (this.resourceHttpMessageConverter == null) {
            this.resourceHttpMessageConverter = new ResourceHttpMessageConverter();
        }
        if (this.resourceRegionHttpMessageConverter == null) {
            this.resourceRegionHttpMessageConverter = new ResourceRegionHttpMessageConverter();
        }
        if ((manager = this.getContentNegotiationManager()) != null) {
            this.setMediaTypes(manager.getMediaTypeMappings());
        }
        if ((strategy = this.initContentNegotiationStrategy()) != null) {
            this.setMediaTypes(strategy.getMediaTypes());
        }
    }

    private void resolveResourceLocations() {
        List<Object> result = new ArrayList<Resource>();
        if (!this.locationValues.isEmpty()) {
            ApplicationContext applicationContext = this.obtainApplicationContext();
            for (String location : this.locationValues) {
                if (this.embeddedValueResolver != null) {
                    String resolvedLocation = this.embeddedValueResolver.resolveStringValue(location);
                    if (resolvedLocation == null) {
                        throw new IllegalArgumentException("Location resolved to null: " + location);
                    }
                    location = resolvedLocation;
                }
                Charset charset = null;
                if ((location = location.trim()).startsWith(URL_RESOURCE_CHARSET_PREFIX)) {
                    int endIndex = location.indexOf(93, URL_RESOURCE_CHARSET_PREFIX.length());
                    if (endIndex == -1) {
                        throw new IllegalArgumentException("Invalid charset syntax in location: " + location);
                    }
                    String value = location.substring(URL_RESOURCE_CHARSET_PREFIX.length(), endIndex);
                    charset = Charset.forName(value);
                    location = location.substring(endIndex + 1);
                }
                Resource resource = applicationContext.getResource(location);
                if (location.equals("/") && !(resource instanceof ServletContextResource)) {
                    throw new IllegalStateException("The String-based location \"/\" should be relative to the web application root but resolved to a Resource of type: " + resource.getClass() + ". If this is intentional, please pass it as a pre-configured Resource via setLocations.");
                }
                result.add(resource);
                if (charset == null) continue;
                if (!(resource instanceof UrlResource)) {
                    throw new IllegalArgumentException("Unexpected charset for non-UrlResource: " + resource);
                }
                this.locationCharsets.put(resource, charset);
            }
        }
        result.addAll(this.locationResources);
        if (this.isOptimizeLocations()) {
            result = result.stream().filter(Resource::exists).collect(Collectors.toList());
        }
        this.locationsToUse.clear();
        this.locationsToUse.addAll(result);
    }

    protected void initAllowedLocations() {
        if (CollectionUtils.isEmpty(this.getLocations())) {
            return;
        }
        for (int i2 = this.getResourceResolvers().size() - 1; i2 >= 0; --i2) {
            if (!(this.getResourceResolvers().get(i2) instanceof PathResourceResolver)) continue;
            PathResourceResolver pathResolver = (PathResourceResolver)this.getResourceResolvers().get(i2);
            if (ObjectUtils.isEmpty(pathResolver.getAllowedLocations())) {
                pathResolver.setAllowedLocations(this.getLocations().toArray(new Resource[0]));
            }
            if (this.urlPathHelper == null) break;
            pathResolver.setLocationCharsets(this.locationCharsets);
            pathResolver.setUrlPathHelper(this.urlPathHelper);
            break;
        }
    }

    @Nullable
    @Deprecated
    protected PathExtensionContentNegotiationStrategy initContentNegotiationStrategy() {
        return null;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Resource resource = this.getResource(request);
        if (resource == null) {
            logger.debug((Object)"Resource not found");
            response.sendError(404);
            return;
        }
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setHeader("Allow", this.getAllowHeader());
            return;
        }
        this.checkRequest(request);
        if (this.isUseLastModified() && new ServletWebRequest(request, response).checkNotModified(resource.lastModified())) {
            logger.trace((Object)"Resource not modified");
            return;
        }
        this.prepareResponse(response);
        MediaType mediaType = this.getMediaType(request, resource);
        this.setHeaders(response, resource, mediaType);
        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
        if (request.getHeader("Range") == null) {
            Assert.state(this.resourceHttpMessageConverter != null, "Not initialized");
            this.resourceHttpMessageConverter.write(resource, mediaType, outputMessage);
        } else {
            Assert.state(this.resourceRegionHttpMessageConverter != null, "Not initialized");
            ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
            try {
                List<HttpRange> httpRanges = inputMessage.getHeaders().getRange();
                response.setStatus(206);
                this.resourceRegionHttpMessageConverter.write(HttpRange.toResourceRegions(httpRanges, resource), mediaType, outputMessage);
            }
            catch (IllegalArgumentException ex) {
                response.setHeader("Content-Range", "bytes */" + resource.contentLength());
                response.sendError(416);
            }
        }
    }

    @Nullable
    protected Resource getResource(HttpServletRequest request) throws IOException {
        String path = (String)request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (path == null) {
            throw new IllegalStateException("Required request attribute '" + HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE + "' is not set");
        }
        if (!StringUtils.hasText(path = this.processPath(path)) || this.isInvalidPath(path)) {
            return null;
        }
        if (this.isInvalidEncodedPath(path)) {
            return null;
        }
        Assert.notNull((Object)this.resolverChain, "ResourceResolverChain not initialized.");
        Assert.notNull((Object)this.transformerChain, "ResourceTransformerChain not initialized.");
        Resource resource = this.resolverChain.resolveResource(request, path, this.getLocations());
        if (resource != null) {
            resource = this.transformerChain.transform(request, resource);
        }
        return resource;
    }

    protected String processPath(String path) {
        path = StringUtils.replace(path, "\\", "/");
        path = this.cleanDuplicateSlashes(path);
        return this.cleanLeadingSlash(path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String cleanDuplicateSlashes(String path) {
        StringBuilder sb = null;
        char prev = '\u0000';
        for (int i2 = 0; i2 < path.length(); ++i2) {
            char curr = path.charAt(i2);
            try {
                if (curr == '/' && prev == '/') {
                    if (sb != null) continue;
                    sb = new StringBuilder(path.substring(0, i2));
                    continue;
                }
                if (sb == null) continue;
                sb.append(path.charAt(i2));
                continue;
            }
            finally {
                prev = curr;
            }
        }
        return sb != null ? sb.toString() : path;
    }

    private String cleanLeadingSlash(String path) {
        boolean slash = false;
        for (int i2 = 0; i2 < path.length(); ++i2) {
            if (path.charAt(i2) == '/') {
                slash = true;
                continue;
            }
            if (path.charAt(i2) <= ' ' || path.charAt(i2) == '\u007f') continue;
            if (i2 == 0 || i2 == 1 && slash) {
                return path;
            }
            return slash ? "/" + path.substring(i2) : path.substring(i2);
        }
        return slash ? "/" : "";
    }

    private boolean isInvalidEncodedPath(String path) {
        if (path.contains("%")) {
            try {
                String decodedPath = URLDecoder.decode(path, "UTF-8");
                if (this.isInvalidPath(decodedPath)) {
                    return true;
                }
                if (this.isInvalidPath(decodedPath = this.processPath(decodedPath))) {
                    return true;
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
        }
        return false;
    }

    protected boolean isInvalidPath(String path) {
        if (path.contains("WEB-INF") || path.contains("META-INF")) {
            if (logger.isWarnEnabled()) {
                logger.warn((Object)LogFormatUtils.formatValue("Path with \"WEB-INF\" or \"META-INF\": [" + path + "]", -1, true));
            }
            return true;
        }
        if (path.contains(":/")) {
            String relativePath;
            String string = relativePath = path.charAt(0) == '/' ? path.substring(1) : path;
            if (ResourceUtils.isUrl(relativePath) || relativePath.startsWith("url:")) {
                if (logger.isWarnEnabled()) {
                    logger.warn((Object)LogFormatUtils.formatValue("Path represents URL or has \"url:\" prefix: [" + path + "]", -1, true));
                }
                return true;
            }
        }
        if (path.contains("..") && StringUtils.cleanPath(path).contains("../")) {
            if (logger.isWarnEnabled()) {
                logger.warn((Object)LogFormatUtils.formatValue("Path contains \"../\" after call to StringUtils#cleanPath: [" + path + "]", -1, true));
            }
            return true;
        }
        return false;
    }

    @Nullable
    protected MediaType getMediaType(HttpServletRequest request, Resource resource) {
        MediaType result = null;
        String mimeType = request.getServletContext().getMimeType(resource.getFilename());
        if (StringUtils.hasText(mimeType)) {
            result = MediaType.parseMediaType(mimeType);
        }
        if (result == null || MediaType.APPLICATION_OCTET_STREAM.equals(result)) {
            List<MediaType> mediaTypes;
            MediaType mediaType = null;
            String filename = resource.getFilename();
            String ext = StringUtils.getFilenameExtension(filename);
            if (ext != null) {
                mediaType = this.mediaTypes.get(ext.toLowerCase(Locale.ENGLISH));
            }
            if (mediaType == null && !CollectionUtils.isEmpty(mediaTypes = MediaTypeFactory.getMediaTypes(filename))) {
                mediaType = mediaTypes.get(0);
            }
            if (mediaType != null) {
                result = mediaType;
            }
        }
        return result;
    }

    protected void setHeaders(HttpServletResponse response, Resource resource, @Nullable MediaType mediaType) throws IOException {
        if (mediaType != null) {
            response.setContentType(mediaType.toString());
        }
        if (resource instanceof HttpResource) {
            HttpHeaders resourceHeaders = ((HttpResource)resource).getResponseHeaders();
            resourceHeaders.forEach((headerName, headerValues) -> {
                boolean first = true;
                for (String headerValue : headerValues) {
                    if (first) {
                        response.setHeader(headerName, headerValue);
                    } else {
                        response.addHeader(headerName, headerValue);
                    }
                    first = false;
                }
            });
        }
        response.setHeader("Accept-Ranges", "bytes");
    }

    public String toString() {
        return "ResourceHttpRequestHandler " + this.locationToString(this.getLocations());
    }

    private String locationToString(List<Resource> locations) {
        return locations.toString().replaceAll("class path resource", "classpath").replaceAll("ServletContext resource", "ServletContext");
    }
}

