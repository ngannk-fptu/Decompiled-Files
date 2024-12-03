/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.Part
 */
package org.springframework.web.multipart.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

public final class MultipartResolutionDelegate {
    public static final Object UNRESOLVABLE = new Object();

    private MultipartResolutionDelegate() {
    }

    @Nullable
    public static MultipartRequest resolveMultipartRequest(NativeWebRequest webRequest) {
        MultipartRequest multipartRequest = webRequest.getNativeRequest(MultipartRequest.class);
        if (multipartRequest != null) {
            return multipartRequest;
        }
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null && MultipartResolutionDelegate.isMultipartContent(servletRequest)) {
            return new StandardMultipartHttpServletRequest(servletRequest);
        }
        return null;
    }

    public static boolean isMultipartRequest(HttpServletRequest request) {
        return WebUtils.getNativeRequest((ServletRequest)request, MultipartHttpServletRequest.class) != null || MultipartResolutionDelegate.isMultipartContent(request);
    }

    private static boolean isMultipartContent(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }

    static MultipartHttpServletRequest asMultipartHttpServletRequest(HttpServletRequest request) {
        MultipartHttpServletRequest unwrapped = WebUtils.getNativeRequest((ServletRequest)request, MultipartHttpServletRequest.class);
        if (unwrapped != null) {
            return unwrapped;
        }
        return new StandardMultipartHttpServletRequest(request);
    }

    public static boolean isMultipartArgument(MethodParameter parameter) {
        Class<?> paramType = parameter.getNestedParameterType();
        return MultipartFile.class == paramType || MultipartResolutionDelegate.isMultipartFileCollection(parameter) || MultipartResolutionDelegate.isMultipartFileArray(parameter) || Part.class == paramType || MultipartResolutionDelegate.isPartCollection(parameter) || MultipartResolutionDelegate.isPartArray(parameter);
    }

    @Nullable
    public static Object resolveMultipartArgument(String name, MethodParameter parameter, HttpServletRequest request) throws Exception {
        boolean isMultipart;
        MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest((ServletRequest)request, MultipartHttpServletRequest.class);
        boolean bl = isMultipart = multipartRequest != null || MultipartResolutionDelegate.isMultipartContent(request);
        if (MultipartFile.class == parameter.getNestedParameterType()) {
            if (!isMultipart) {
                return null;
            }
            if (multipartRequest == null) {
                multipartRequest = new StandardMultipartHttpServletRequest(request);
            }
            return multipartRequest.getFile(name);
        }
        if (MultipartResolutionDelegate.isMultipartFileCollection(parameter)) {
            List files;
            if (!isMultipart) {
                return null;
            }
            if (multipartRequest == null) {
                multipartRequest = new StandardMultipartHttpServletRequest(request);
            }
            return !(files = multipartRequest.getFiles(name)).isEmpty() ? files : null;
        }
        if (MultipartResolutionDelegate.isMultipartFileArray(parameter)) {
            List files;
            if (!isMultipart) {
                return null;
            }
            if (multipartRequest == null) {
                multipartRequest = new StandardMultipartHttpServletRequest(request);
            }
            return !(files = multipartRequest.getFiles(name)).isEmpty() ? files.toArray(new MultipartFile[0]) : null;
        }
        if (Part.class == parameter.getNestedParameterType()) {
            if (!isMultipart) {
                return null;
            }
            return request.getPart(name);
        }
        if (MultipartResolutionDelegate.isPartCollection(parameter)) {
            if (!isMultipart) {
                return null;
            }
            List<Part> parts = MultipartResolutionDelegate.resolvePartList(request, name);
            return !parts.isEmpty() ? parts : null;
        }
        if (MultipartResolutionDelegate.isPartArray(parameter)) {
            if (!isMultipart) {
                return null;
            }
            List<Part> parts = MultipartResolutionDelegate.resolvePartList(request, name);
            return !parts.isEmpty() ? parts.toArray(new Part[0]) : null;
        }
        return UNRESOLVABLE;
    }

    private static boolean isMultipartFileCollection(MethodParameter methodParam) {
        return MultipartFile.class == MultipartResolutionDelegate.getCollectionParameterType(methodParam);
    }

    private static boolean isMultipartFileArray(MethodParameter methodParam) {
        return MultipartFile.class == methodParam.getNestedParameterType().getComponentType();
    }

    private static boolean isPartCollection(MethodParameter methodParam) {
        return Part.class == MultipartResolutionDelegate.getCollectionParameterType(methodParam);
    }

    private static boolean isPartArray(MethodParameter methodParam) {
        return Part.class == methodParam.getNestedParameterType().getComponentType();
    }

    @Nullable
    private static Class<?> getCollectionParameterType(MethodParameter methodParam) {
        Class<?> valueType;
        Class<?> paramType = methodParam.getNestedParameterType();
        if ((Collection.class == paramType || List.class.isAssignableFrom(paramType)) && (valueType = ResolvableType.forMethodParameter(methodParam).asCollection().resolveGeneric(new int[0])) != null) {
            return valueType;
        }
        return null;
    }

    private static List<Part> resolvePartList(HttpServletRequest request, String name) throws Exception {
        Collection parts = request.getParts();
        ArrayList<Part> result = new ArrayList<Part>(parts.size());
        for (Part part : parts) {
            if (!part.getName().equals(name)) continue;
            result.add(part);
        }
        return result;
    }
}

