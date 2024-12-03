/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;
import org.springframework.web.util.UrlPathHelper;

public abstract class AbstractMessageConverterMethodProcessor
extends AbstractMessageConverterMethodArgumentResolver
implements HandlerMethodReturnValueHandler {
    private static final Set<String> SAFE_EXTENSIONS = new HashSet<String>(Arrays.asList("txt", "text", "yml", "properties", "csv", "json", "xml", "atom", "rss", "png", "jpe", "jpeg", "jpg", "gif", "wbmp", "bmp"));
    private static final Set<String> SAFE_MEDIA_BASE_TYPES = new HashSet<String>(Arrays.asList("audio", "image", "video"));
    private static final List<MediaType> ALL_APPLICATION_MEDIA_TYPES = Arrays.asList(MediaType.ALL, new MediaType("application"));
    private static final Type RESOURCE_REGION_LIST_TYPE = new ParameterizedTypeReference<List<ResourceRegion>>(){}.getType();
    private final ContentNegotiationManager contentNegotiationManager;
    private final Set<String> safeExtensions = new HashSet<String>();

    protected AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters) {
        this(converters, null, null);
    }

    protected AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager contentNegotiationManager) {
        this(converters, contentNegotiationManager, null);
    }

    protected AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager, @Nullable List<Object> requestResponseBodyAdvice) {
        super(converters, requestResponseBodyAdvice);
        this.contentNegotiationManager = manager != null ? manager : new ContentNegotiationManager();
        this.safeExtensions.addAll(this.contentNegotiationManager.getAllFileExtensions());
        this.safeExtensions.addAll(SAFE_EXTENSIONS);
    }

    protected ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        return new ServletServerHttpResponse(response);
    }

    protected <T> void writeWithMessageConverters(T value, MethodParameter returnType, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        ServletServerHttpRequest inputMessage = this.createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = this.createOutputMessage(webRequest);
        this.writeWithMessageConverters(value, returnType, inputMessage, outputMessage);
    }

    protected <T> void writeWithMessageConverters(@Nullable T value, MethodParameter returnType, ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        boolean isContentTypePreset;
        Object targetType;
        Class valueType;
        Object body2;
        if (value instanceof CharSequence) {
            body2 = value.toString();
            valueType = String.class;
            targetType = String.class;
        } else {
            body2 = value;
            valueType = this.getReturnValueType(body2, returnType);
            targetType = GenericTypeResolver.resolveType(this.getGenericType(returnType), returnType.getContainingClass());
        }
        if (this.isResourceType(value, returnType)) {
            outputMessage.getHeaders().set("Accept-Ranges", "bytes");
            if (value != null && inputMessage.getHeaders().getFirst("Range") != null && outputMessage.getServletResponse().getStatus() == 200) {
                Resource resource = (Resource)value;
                try {
                    List<HttpRange> httpRanges = inputMessage.getHeaders().getRange();
                    outputMessage.getServletResponse().setStatus(HttpStatus.PARTIAL_CONTENT.value());
                    body2 = HttpRange.toResourceRegions(httpRanges, resource);
                    valueType = body2.getClass();
                    targetType = RESOURCE_REGION_LIST_TYPE;
                }
                catch (IllegalArgumentException ex) {
                    outputMessage.getHeaders().set("Content-Range", "bytes */" + resource.contentLength());
                    outputMessage.getServletResponse().setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
                }
            }
        }
        MediaType selectedMediaType = null;
        MediaType contentType = outputMessage.getHeaders().getContentType();
        boolean bl = isContentTypePreset = contentType != null && contentType.isConcrete();
        if (isContentTypePreset) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Found 'Content-Type:" + contentType + "' in response"));
            }
            selectedMediaType = contentType;
        } else {
            List<MediaType> acceptableTypes;
            HttpServletRequest request = inputMessage.getServletRequest();
            try {
                acceptableTypes = this.getAcceptableMediaTypes(request);
            }
            catch (HttpMediaTypeNotAcceptableException ex) {
                int series = outputMessage.getServletResponse().getStatus() / 100;
                if (body2 == null || series == 4 || series == 5) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Ignoring error response content (if any). " + (Object)((Object)ex)));
                    }
                    return;
                }
                throw ex;
            }
            List<MediaType> producibleTypes = this.getProducibleMediaTypes(request, valueType, (Type)targetType);
            if (body2 != null && producibleTypes.isEmpty()) {
                throw new HttpMessageNotWritableException("No converter found for return value of type: " + valueType);
            }
            ArrayList<MediaType> mediaTypesToUse = new ArrayList<MediaType>();
            for (MediaType requestedType : acceptableTypes) {
                for (MediaType producibleType : producibleTypes) {
                    if (!requestedType.isCompatibleWith(producibleType)) continue;
                    mediaTypesToUse.add(this.getMostSpecificMediaType(requestedType, producibleType));
                }
            }
            if (mediaTypesToUse.isEmpty()) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("No match for " + acceptableTypes + ", supported: " + producibleTypes));
                }
                if (body2 != null) {
                    throw new HttpMediaTypeNotAcceptableException(producibleTypes);
                }
                return;
            }
            MediaType.sortBySpecificityAndQuality(mediaTypesToUse);
            for (MediaType mediaType : mediaTypesToUse) {
                if (mediaType.isConcrete()) {
                    selectedMediaType = mediaType;
                    break;
                }
                if (!mediaType.isPresentIn(ALL_APPLICATION_MEDIA_TYPES)) continue;
                selectedMediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Using '" + selectedMediaType + "', given " + acceptableTypes + " and supported " + producibleTypes));
            }
        }
        if (selectedMediaType != null) {
            selectedMediaType = selectedMediaType.removeQualityValue();
            for (HttpMessageConverter converter : this.messageConverters) {
                GenericHttpMessageConverter genericConverter;
                GenericHttpMessageConverter genericHttpMessageConverter = genericConverter = converter instanceof GenericHttpMessageConverter ? (GenericHttpMessageConverter)converter : null;
                if (!(genericConverter != null ? ((GenericHttpMessageConverter)converter).canWrite((Type)targetType, valueType, selectedMediaType) : converter.canWrite(valueType, selectedMediaType))) continue;
                body2 = this.getAdvice().beforeBodyWrite(body2, returnType, selectedMediaType, (Class<? extends HttpMessageConverter<?>>)converter.getClass(), (ServerHttpRequest)inputMessage, (ServerHttpResponse)outputMessage);
                if (body2 != null) {
                    Object theBody = body2;
                    LogFormatUtils.traceDebug(this.logger, traceOn -> "Writing [" + LogFormatUtils.formatValue(theBody, traceOn == false) + "]");
                    this.addContentDispositionHeader(inputMessage, outputMessage);
                    if (genericConverter != null) {
                        genericConverter.write(body2, (Type)targetType, selectedMediaType, outputMessage);
                    } else {
                        converter.write(body2, selectedMediaType, outputMessage);
                    }
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)"Nothing to write: null body");
                }
                return;
            }
        }
        if (body2 != null) {
            Set producibleMediaTypes = (Set)inputMessage.getServletRequest().getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
            if (isContentTypePreset || !CollectionUtils.isEmpty(producibleMediaTypes)) {
                throw new HttpMessageNotWritableException("No converter for [" + valueType + "] with preset Content-Type '" + contentType + "'");
            }
            throw new HttpMediaTypeNotAcceptableException(this.getSupportedMediaTypes(body2.getClass()));
        }
    }

    protected Class<?> getReturnValueType(@Nullable Object value, MethodParameter returnType) {
        return value != null ? value.getClass() : returnType.getParameterType();
    }

    protected boolean isResourceType(@Nullable Object value, MethodParameter returnType) {
        Class<?> clazz = this.getReturnValueType(value, returnType);
        return clazz != InputStreamResource.class && Resource.class.isAssignableFrom(clazz);
    }

    private Type getGenericType(MethodParameter returnType) {
        if (HttpEntity.class.isAssignableFrom(returnType.getParameterType())) {
            return ResolvableType.forType(returnType.getGenericParameterType()).getGeneric(new int[0]).getType();
        }
        return returnType.getGenericParameterType();
    }

    protected List<MediaType> getProducibleMediaTypes(HttpServletRequest request, Class<?> valueClass) {
        return this.getProducibleMediaTypes(request, valueClass, null);
    }

    protected List<MediaType> getProducibleMediaTypes(HttpServletRequest request, Class<?> valueClass, @Nullable Type targetType) {
        Set mediaTypes = (Set)request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            return new ArrayList<MediaType>(mediaTypes);
        }
        ArrayList<MediaType> result = new ArrayList<MediaType>();
        for (HttpMessageConverter converter : this.messageConverters) {
            if (converter instanceof GenericHttpMessageConverter && targetType != null) {
                if (!((GenericHttpMessageConverter)converter).canWrite(targetType, valueClass, null)) continue;
                result.addAll(converter.getSupportedMediaTypes(valueClass));
                continue;
            }
            if (!converter.canWrite(valueClass, null)) continue;
            result.addAll(converter.getSupportedMediaTypes(valueClass));
        }
        return result.isEmpty() ? Collections.singletonList(MediaType.ALL) : result;
    }

    private List<MediaType> getAcceptableMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
        return this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
    }

    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        MediaType produceTypeToUse = produceType.copyQualityValue(acceptType);
        return MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceTypeToUse) <= 0 ? acceptType : produceTypeToUse;
    }

    private void addContentDispositionHeader(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        if (headers.containsKey("Content-Disposition")) {
            return;
        }
        try {
            int status = response.getServletResponse().getStatus();
            if (status < 200 || status > 299 && status < 400) {
                return;
            }
        }
        catch (Throwable status) {
            // empty catch block
        }
        HttpServletRequest servletRequest = request.getServletRequest();
        String requestUri = UrlPathHelper.rawPathInstance.getOriginatingRequestUri(servletRequest);
        int index = requestUri.lastIndexOf(47) + 1;
        String filename = requestUri.substring(index);
        String pathParams = "";
        index = filename.indexOf(59);
        if (index != -1) {
            pathParams = filename.substring(index);
            filename = filename.substring(0, index);
        }
        filename = UrlPathHelper.defaultInstance.decodeRequestString(servletRequest, filename);
        String ext = StringUtils.getFilenameExtension(filename);
        pathParams = UrlPathHelper.defaultInstance.decodeRequestString(servletRequest, pathParams);
        String extInPathParams = StringUtils.getFilenameExtension(pathParams);
        if (!this.safeExtension(servletRequest, ext) || !this.safeExtension(servletRequest, extInPathParams)) {
            headers.add("Content-Disposition", "inline;filename=f.txt");
        }
    }

    private boolean safeExtension(HttpServletRequest request, @Nullable String extension) {
        String name;
        Set mediaTypes;
        if (!StringUtils.hasText(extension)) {
            return true;
        }
        if (this.safeExtensions.contains(extension = extension.toLowerCase(Locale.ENGLISH))) {
            return true;
        }
        String pattern = (String)request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern != null && pattern.endsWith("." + extension)) {
            return true;
        }
        if (extension.equals("html") && !CollectionUtils.isEmpty(mediaTypes = (Set)request.getAttribute(name = HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE)) && mediaTypes.contains(MediaType.TEXT_HTML)) {
            return true;
        }
        MediaType mediaType = this.resolveMediaType((ServletRequest)request, extension);
        return mediaType != null && this.safeMediaType(mediaType);
    }

    @Nullable
    private MediaType resolveMediaType(ServletRequest request, String extension) {
        MediaType result = null;
        String rawMimeType = request.getServletContext().getMimeType("file." + extension);
        if (StringUtils.hasText(rawMimeType)) {
            result = MediaType.parseMediaType(rawMimeType);
        }
        if (result == null || MediaType.APPLICATION_OCTET_STREAM.equals(result)) {
            result = MediaTypeFactory.getMediaType("file." + extension).orElse(null);
        }
        return result;
    }

    private boolean safeMediaType(MediaType mediaType) {
        return SAFE_MEDIA_BASE_TYPES.contains(mediaType.getType()) || mediaType.getSubtype().endsWith("+xml");
    }
}

