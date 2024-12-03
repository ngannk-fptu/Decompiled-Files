/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.Part
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.method.annotation;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.util.UriComponentsBuilder;

public class RequestParamMethodArgumentResolver
extends AbstractNamedValueMethodArgumentResolver
implements UriComponentsContributor {
    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);
    private final boolean useDefaultResolution;

    public RequestParamMethodArgumentResolver(boolean useDefaultResolution) {
        this.useDefaultResolution = useDefaultResolution;
    }

    public RequestParamMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory, boolean useDefaultResolution) {
        super(beanFactory);
        this.useDefaultResolution = useDefaultResolution;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(RequestParam.class)) {
            if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
                RequestParam requestParam = (RequestParam)parameter.getParameterAnnotation(RequestParam.class);
                return requestParam != null && StringUtils.hasText((String)requestParam.name());
            }
            return true;
        }
        if (parameter.hasParameterAnnotation(RequestPart.class)) {
            return false;
        }
        if (MultipartResolutionDelegate.isMultipartArgument(parameter = parameter.nestedIfOptional())) {
            return true;
        }
        if (this.useDefaultResolution) {
            return BeanUtils.isSimpleProperty((Class)parameter.getNestedParameterType());
        }
        return false;
    }

    @Override
    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestParam ann = (RequestParam)parameter.getParameterAnnotation(RequestParam.class);
        return ann != null ? new RequestParamNamedValueInfo(ann) : new RequestParamNamedValueInfo();
    }

    @Override
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        String[] paramValues;
        String[] files;
        Object mpArg;
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null && (mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest)) != MultipartResolutionDelegate.UNRESOLVABLE) {
            return mpArg;
        }
        String[] arg = null;
        MultipartRequest multipartRequest = request.getNativeRequest(MultipartRequest.class);
        if (multipartRequest != null && !(files = multipartRequest.getFiles(name)).isEmpty()) {
            String[] stringArray = arg = files.size() == 1 ? files.get(0) : files;
        }
        if (arg == null && (paramValues = request.getParameterValues(name)) != null) {
            arg = paramValues.length == 1 ? paramValues[0] : paramValues;
        }
        return arg;
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        this.handleMissingValueInternal(name, parameter, request, false);
    }

    @Override
    protected void handleMissingValueAfterConversion(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        this.handleMissingValueInternal(name, parameter, request, true);
    }

    protected void handleMissingValueInternal(String name, MethodParameter parameter, NativeWebRequest request, boolean missingAfterConversion) throws Exception {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
            if (servletRequest == null || !MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                throw new MultipartException("Current request is not a multipart request");
            }
            throw new MissingServletRequestPartException(name);
        }
        throw new MissingServletRequestParameterException(name, parameter.getNestedParameterType().getSimpleName(), missingAfterConversion);
    }

    @Override
    public void contributeMethodArgument(MethodParameter parameter, @Nullable Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
        Class paramType = parameter.getNestedParameterType();
        if (Map.class.isAssignableFrom(paramType) || MultipartFile.class == paramType || Part.class == paramType) {
            return;
        }
        RequestParam requestParam = (RequestParam)parameter.getParameterAnnotation(RequestParam.class);
        String name = requestParam != null && StringUtils.hasLength((String)requestParam.name()) ? requestParam.name() : parameter.getParameterName();
        Assert.state((name != null ? 1 : 0) != 0, (String)"Unresolvable parameter name");
        parameter = parameter.nestedIfOptional();
        if (value instanceof Optional) {
            value = ((Optional)value).orElse(null);
        }
        if (value == null) {
            if (!(requestParam == null || requestParam.required() && requestParam.defaultValue().equals("\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n"))) {
                return;
            }
            builder.queryParam(name, new Object[0]);
        } else if (value instanceof Collection) {
            for (Object element : (Collection)value) {
                element = this.formatUriValue(conversionService, TypeDescriptor.nested((MethodParameter)parameter, (int)1), element);
                builder.queryParam(name, element);
            }
        } else {
            builder.queryParam(name, this.formatUriValue(conversionService, new TypeDescriptor(parameter), value));
        }
    }

    @Nullable
    protected String formatUriValue(@Nullable ConversionService cs, @Nullable TypeDescriptor sourceType, @Nullable Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String)value;
        }
        if (cs != null) {
            return (String)cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        }
        return value.toString();
    }

    private static class RequestParamNamedValueInfo
    extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo {
        public RequestParamNamedValueInfo() {
            super("", false, "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n");
        }

        public RequestParamNamedValueInfo(RequestParam annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }
}

