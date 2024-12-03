/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.method.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.Conventions;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.SessionAttributesHandler;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;

public final class ModelFactory {
    private static final Log logger = LogFactory.getLog(ModelFactory.class);
    private final List<ModelMethod> modelMethods = new ArrayList<ModelMethod>();
    private final WebDataBinderFactory dataBinderFactory;
    private final SessionAttributesHandler sessionAttributesHandler;

    public ModelFactory(@Nullable List<InvocableHandlerMethod> handlerMethods, WebDataBinderFactory binderFactory, SessionAttributesHandler attributeHandler) {
        if (handlerMethods != null) {
            for (InvocableHandlerMethod handlerMethod : handlerMethods) {
                this.modelMethods.add(new ModelMethod(handlerMethod));
            }
        }
        this.dataBinderFactory = binderFactory;
        this.sessionAttributesHandler = attributeHandler;
    }

    public void initModel(NativeWebRequest request, ModelAndViewContainer container, HandlerMethod handlerMethod) throws Exception {
        Map<String, Object> sessionAttributes = this.sessionAttributesHandler.retrieveAttributes(request);
        container.mergeAttributes(sessionAttributes);
        this.invokeModelAttributeMethods(request, container);
        for (String name : this.findSessionAttributeArguments(handlerMethod)) {
            if (container.containsAttribute(name)) continue;
            Object value = this.sessionAttributesHandler.retrieveAttribute(request, name);
            if (value == null) {
                throw new HttpSessionRequiredException("Expected session attribute '" + name + "'", name);
            }
            container.addAttribute(name, value);
        }
    }

    private void invokeModelAttributeMethods(NativeWebRequest request, ModelAndViewContainer container) throws Exception {
        while (!this.modelMethods.isEmpty()) {
            InvocableHandlerMethod modelMethod = this.getNextModelMethod(container).getHandlerMethod();
            ModelAttribute ann = modelMethod.getMethodAnnotation(ModelAttribute.class);
            Assert.state(ann != null, "No ModelAttribute annotation");
            if (container.containsAttribute(ann.name())) {
                if (ann.binding()) continue;
                container.setBindingDisabled(ann.name());
                continue;
            }
            Object returnValue = modelMethod.invokeForRequest(request, container, new Object[0]);
            if (modelMethod.isVoid()) {
                if (!StringUtils.hasText(ann.value()) || !logger.isDebugEnabled()) continue;
                logger.debug((Object)("Name in @ModelAttribute is ignored because method returns void: " + modelMethod.getShortLogMessage()));
                continue;
            }
            String returnValueName = ModelFactory.getNameForReturnValue(returnValue, modelMethod.getReturnType());
            if (!ann.binding()) {
                container.setBindingDisabled(returnValueName);
            }
            if (container.containsAttribute(returnValueName)) continue;
            container.addAttribute(returnValueName, returnValue);
        }
    }

    private ModelMethod getNextModelMethod(ModelAndViewContainer container) {
        for (ModelMethod modelMethod : this.modelMethods) {
            if (!modelMethod.checkDependencies(container)) continue;
            this.modelMethods.remove(modelMethod);
            return modelMethod;
        }
        ModelMethod modelMethod = this.modelMethods.get(0);
        this.modelMethods.remove(modelMethod);
        return modelMethod;
    }

    private List<String> findSessionAttributeArguments(HandlerMethod handlerMethod) {
        ArrayList<String> result = new ArrayList<String>();
        for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
            Class<?> paramType;
            String name;
            if (!parameter.hasParameterAnnotation(ModelAttribute.class) || !this.sessionAttributesHandler.isHandlerSessionAttribute(name = ModelFactory.getNameForParameter(parameter), paramType = parameter.getParameterType())) continue;
            result.add(name);
        }
        return result;
    }

    public void updateModel(NativeWebRequest request, ModelAndViewContainer container) throws Exception {
        ModelMap defaultModel = container.getDefaultModel();
        if (container.getSessionStatus().isComplete()) {
            this.sessionAttributesHandler.cleanupAttributes(request);
        } else {
            this.sessionAttributesHandler.storeAttributes(request, defaultModel);
        }
        if (!container.isRequestHandled() && container.getModel() == defaultModel) {
            this.updateBindingResult(request, defaultModel);
        }
    }

    private void updateBindingResult(NativeWebRequest request, ModelMap model) throws Exception {
        ArrayList keyNames = new ArrayList(model.keySet());
        for (String name : keyNames) {
            String bindingResultKey;
            Object value = model.get(name);
            if (value == null || !this.isBindingCandidate(name, value) || model.containsAttribute(bindingResultKey = BindingResult.MODEL_KEY_PREFIX + name)) continue;
            WebDataBinder dataBinder = this.dataBinderFactory.createBinder(request, value, name);
            model.put(bindingResultKey, dataBinder.getBindingResult());
        }
    }

    private boolean isBindingCandidate(String attributeName, Object value) {
        if (attributeName.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
            return false;
        }
        if (this.sessionAttributesHandler.isHandlerSessionAttribute(attributeName, value.getClass())) {
            return true;
        }
        return !value.getClass().isArray() && !(value instanceof Collection) && !(value instanceof Map) && !BeanUtils.isSimpleValueType(value.getClass());
    }

    public static String getNameForParameter(MethodParameter parameter) {
        ModelAttribute ann = parameter.getParameterAnnotation(ModelAttribute.class);
        String name = ann != null ? ann.value() : null;
        return StringUtils.hasText(name) ? name : Conventions.getVariableNameForParameter(parameter);
    }

    public static String getNameForReturnValue(@Nullable Object returnValue, MethodParameter returnType) {
        ModelAttribute ann = returnType.getMethodAnnotation(ModelAttribute.class);
        if (ann != null && StringUtils.hasText(ann.value())) {
            return ann.value();
        }
        Method method = returnType.getMethod();
        Assert.state(method != null, "No handler method");
        Class<?> containingClass = returnType.getContainingClass();
        Class<?> resolvedType = GenericTypeResolver.resolveReturnType(method, containingClass);
        return Conventions.getVariableNameForReturnType(method, resolvedType, returnValue);
    }

    private static class ModelMethod {
        private final InvocableHandlerMethod handlerMethod;
        private final Set<String> dependencies = new HashSet<String>();

        public ModelMethod(InvocableHandlerMethod handlerMethod) {
            this.handlerMethod = handlerMethod;
            for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
                if (!parameter.hasParameterAnnotation(ModelAttribute.class)) continue;
                this.dependencies.add(ModelFactory.getNameForParameter(parameter));
            }
        }

        public InvocableHandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        public boolean checkDependencies(ModelAndViewContainer mavContainer) {
            for (String name : this.dependencies) {
                if (mavContainer.containsAttribute(name)) continue;
                return false;
            }
            return true;
        }

        public String toString() {
            return this.handlerMethod.getMethod().toGenericString();
        }
    }
}

