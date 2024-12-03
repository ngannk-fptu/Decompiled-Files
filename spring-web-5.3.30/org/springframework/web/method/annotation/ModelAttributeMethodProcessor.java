/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.Part
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeanInstantiationException
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.PropertyAccessException
 *  org.springframework.beans.TypeMismatchException
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.validation.BindException
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.Errors
 *  org.springframework.validation.SmartValidator
 *  org.springframework.validation.Validator
 *  org.springframework.validation.annotation.ValidationAnnotationUtils
 */
package org.springframework.web.method.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.ValidationAnnotationUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletPartUtils;

public class ModelAttributeMethodProcessor
implements HandlerMethodArgumentResolver,
HandlerMethodReturnValueHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final boolean annotationNotRequired;

    public ModelAttributeMethodProcessor(boolean annotationNotRequired) {
        this.annotationNotRequired = annotationNotRequired;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ModelAttribute.class) || this.annotationNotRequired && !BeanUtils.isSimpleProperty((Class)parameter.getParameterType());
    }

    @Override
    @Nullable
    public final Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Assert.state((mavContainer != null ? 1 : 0) != 0, (String)"ModelAttributeMethodProcessor requires ModelAndViewContainer");
        Assert.state((binderFactory != null ? 1 : 0) != 0, (String)"ModelAttributeMethodProcessor requires WebDataBinderFactory");
        String name = ModelFactory.getNameForParameter(parameter);
        ModelAttribute ann = (ModelAttribute)parameter.getParameterAnnotation(ModelAttribute.class);
        if (ann != null) {
            mavContainer.setBinding(name, ann.binding());
        }
        Object attribute = null;
        BindingResult bindingResult = null;
        if (mavContainer.containsAttribute(name)) {
            attribute = mavContainer.getModel().get((Object)name);
        } else {
            try {
                attribute = this.createAttribute(name, parameter, binderFactory, webRequest);
            }
            catch (BindException ex) {
                if (this.isBindExceptionRequired(parameter)) {
                    throw ex;
                }
                attribute = parameter.getParameterType() == Optional.class ? Optional.empty() : ex.getTarget();
                bindingResult = ex.getBindingResult();
            }
        }
        if (bindingResult == null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
            if (binder.getTarget() != null) {
                if (!mavContainer.isBindingDisabled(name)) {
                    this.bindRequestParameters(binder, webRequest);
                }
                this.validateIfApplicable(binder, parameter);
                if (binder.getBindingResult().hasErrors() && this.isBindExceptionRequired(binder, parameter)) {
                    throw new BindException(binder.getBindingResult());
                }
            }
            if (!parameter.getParameterType().isInstance(attribute)) {
                attribute = binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
            }
            bindingResult = binder.getBindingResult();
        }
        Map bindingResultModel = bindingResult.getModel();
        mavContainer.removeAttributes(bindingResultModel);
        mavContainer.addAllAttributes(bindingResultModel);
        return attribute;
    }

    protected Object createAttribute(String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {
        MethodParameter nestedParameter = parameter.nestedIfOptional();
        Class clazz = nestedParameter.getNestedParameterType();
        Constructor ctor = BeanUtils.getResolvableConstructor((Class)clazz);
        Optional<Object> attribute = this.constructAttribute(ctor, attributeName, parameter, binderFactory, webRequest);
        if (parameter != nestedParameter) {
            attribute = Optional.of(attribute);
        }
        return attribute;
    }

    protected Object constructAttribute(Constructor<?> ctor, String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {
        Object value;
        if (ctor.getParameterCount() == 0) {
            return BeanUtils.instantiateClass(ctor, (Object[])new Object[0]);
        }
        String[] paramNames = BeanUtils.getParameterNames(ctor);
        Class<?>[] paramTypes = ctor.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        WebDataBinder binder = binderFactory.createBinder(webRequest, null, attributeName);
        String fieldDefaultPrefix = binder.getFieldDefaultPrefix();
        String fieldMarkerPrefix = binder.getFieldMarkerPrefix();
        boolean bindingFailure = false;
        HashSet<String> failedParams = new HashSet<String>(4);
        for (int i = 0; i < paramNames.length; ++i) {
            String paramName = paramNames[i];
            Class<?> paramType = paramTypes[i];
            value = webRequest.getParameterValues(paramName);
            if (ObjectUtils.isArray((Object)value) && Array.getLength(value) == 1) {
                value = Array.get(value, 0);
            }
            if (value == null) {
                if (fieldDefaultPrefix != null) {
                    value = webRequest.getParameter(fieldDefaultPrefix + paramName);
                }
                if (value == null) {
                    value = fieldMarkerPrefix != null && webRequest.getParameter(fieldMarkerPrefix + paramName) != null ? binder.getEmptyValue(paramType) : this.resolveConstructorArgument(paramName, paramType, webRequest);
                }
            }
            try {
                FieldAwareConstructorParameter methodParam = new FieldAwareConstructorParameter(ctor, i, paramName);
                if (value == null && methodParam.isOptional()) {
                    args[i] = methodParam.getParameterType() == Optional.class ? Optional.empty() : null;
                    continue;
                }
                args[i] = binder.convertIfNecessary(value, paramType, methodParam);
                continue;
            }
            catch (TypeMismatchException ex) {
                ex.initPropertyName(paramName);
                args[i] = null;
                failedParams.add(paramName);
                binder.getBindingResult().recordFieldValue(paramName, paramType, value);
                binder.getBindingErrorProcessor().processPropertyAccessException((PropertyAccessException)ex, binder.getBindingResult());
                bindingFailure = true;
            }
        }
        if (bindingFailure) {
            BindingResult result = binder.getBindingResult();
            for (int i = 0; i < paramNames.length; ++i) {
                String paramName = paramNames[i];
                if (failedParams.contains(paramName)) continue;
                value = args[i];
                result.recordFieldValue(paramName, paramTypes[i], value);
                this.validateValueIfApplicable(binder, parameter, ctor.getDeclaringClass(), paramName, value);
            }
            if (!parameter.isOptional()) {
                try {
                    final Object target = BeanUtils.instantiateClass(ctor, (Object[])args);
                    throw new BindException(result){

                        public Object getTarget() {
                            return target;
                        }
                    };
                }
                catch (BeanInstantiationException beanInstantiationException) {
                    // empty catch block
                }
            }
            throw new BindException(result);
        }
        return BeanUtils.instantiateClass(ctor, (Object[])args);
    }

    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ((WebRequestDataBinder)binder).bind(request);
    }

    @Nullable
    public Object resolveConstructorArgument(String paramName, Class<?> paramType, NativeWebRequest request) throws Exception {
        Part parts;
        HttpServletRequest servletRequest;
        MultipartRequest multipartRequest = request.getNativeRequest(MultipartRequest.class);
        if (multipartRequest != null) {
            List<MultipartFile> files = multipartRequest.getFiles(paramName);
            if (!files.isEmpty()) {
                return files.size() == 1 ? files.get(0) : files;
            }
        } else if (StringUtils.startsWithIgnoreCase((String)request.getHeader("Content-Type"), (String)"multipart/form-data") && (servletRequest = request.getNativeRequest(HttpServletRequest.class)) != null && HttpMethod.POST.matches(servletRequest.getMethod()) && !(parts = StandardServletPartUtils.getParts(servletRequest, paramName)).isEmpty()) {
            return parts.size() == 1 ? parts.get(0) : parts;
        }
        return null;
    }

    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        for (Annotation ann : parameter.getParameterAnnotations()) {
            Object[] validationHints = ValidationAnnotationUtils.determineValidationHints((Annotation)ann);
            if (validationHints == null) continue;
            binder.validate(validationHints);
            break;
        }
    }

    protected void validateValueIfApplicable(WebDataBinder binder, MethodParameter parameter, Class<?> targetType, String fieldName, @Nullable Object value) {
        for (Annotation ann : parameter.getParameterAnnotations()) {
            Object[] validationHints = ValidationAnnotationUtils.determineValidationHints((Annotation)ann);
            if (validationHints == null) continue;
            for (Validator validator : binder.getValidators()) {
                if (!(validator instanceof SmartValidator)) continue;
                try {
                    ((SmartValidator)validator).validateValue(targetType, fieldName, value, (Errors)binder.getBindingResult(), validationHints);
                }
                catch (IllegalArgumentException illegalArgumentException) {}
            }
            break;
        }
    }

    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        return this.isBindExceptionRequired(parameter);
    }

    protected boolean isBindExceptionRequired(MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
        boolean hasBindingResult = paramTypes.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]);
        return !hasBindingResult;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ModelAttribute.class) || this.annotationNotRequired && !BeanUtils.isSimpleProperty((Class)returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue != null) {
            String name = ModelFactory.getNameForReturnValue(returnValue, returnType);
            mavContainer.addAttribute(name, returnValue);
        }
    }

    private static class FieldAwareConstructorParameter
    extends MethodParameter {
        private final String parameterName;
        @Nullable
        private volatile Annotation[] combinedAnnotations;

        public FieldAwareConstructorParameter(Constructor<?> constructor, int parameterIndex, String parameterName) {
            super(constructor, parameterIndex);
            this.parameterName = parameterName;
        }

        public Annotation[] getParameterAnnotations() {
            Annotation[] anns = this.combinedAnnotations;
            if (anns == null) {
                anns = super.getParameterAnnotations();
                try {
                    Field field = this.getDeclaringClass().getDeclaredField(this.parameterName);
                    Annotation[] fieldAnns = field.getAnnotations();
                    if (fieldAnns.length > 0) {
                        ArrayList<Annotation> merged = new ArrayList<Annotation>(anns.length + fieldAnns.length);
                        merged.addAll(Arrays.asList(anns));
                        for (Annotation fieldAnn : fieldAnns) {
                            boolean existingType = false;
                            for (Annotation ann : anns) {
                                if (ann.annotationType() != fieldAnn.annotationType()) continue;
                                existingType = true;
                                break;
                            }
                            if (existingType) continue;
                            merged.add(fieldAnn);
                        }
                        anns = merged.toArray(new Annotation[0]);
                    }
                }
                catch (NoSuchFieldException | SecurityException exception) {
                    // empty catch block
                }
                this.combinedAnnotations = anns;
            }
            return anns;
        }

        public String getParameterName() {
            return this.parameterName;
        }
    }
}

