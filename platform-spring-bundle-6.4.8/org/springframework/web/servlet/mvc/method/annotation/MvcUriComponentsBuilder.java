/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.SpringObjenesis;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.pattern.PathPatternParser;

public class MvcUriComponentsBuilder {
    public static final String MVC_URI_COMPONENTS_CONTRIBUTOR_BEAN_NAME = "mvcUriComponentsContributor";
    private static final Log logger = LogFactory.getLog(MvcUriComponentsBuilder.class);
    private static final SpringObjenesis objenesis = new SpringObjenesis();
    private static final PathMatcher pathMatcher = new AntPathMatcher();
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private static final CompositeUriComponentsContributor defaultUriComponentsContributor = new CompositeUriComponentsContributor(new PathVariableMethodArgumentResolver(), new RequestParamMethodArgumentResolver(false));
    private final UriComponentsBuilder baseUrl;

    protected MvcUriComponentsBuilder(UriComponentsBuilder baseUrl) {
        Assert.notNull((Object)baseUrl, "'baseUrl' is required");
        this.baseUrl = baseUrl;
    }

    public static MvcUriComponentsBuilder relativeTo(UriComponentsBuilder baseUrl) {
        return new MvcUriComponentsBuilder(baseUrl);
    }

    public static UriComponentsBuilder fromController(Class<?> controllerType) {
        return MvcUriComponentsBuilder.fromController(null, controllerType);
    }

    public static UriComponentsBuilder fromController(@Nullable UriComponentsBuilder builder, Class<?> controllerType) {
        builder = MvcUriComponentsBuilder.getBaseUrlToUse(builder);
        String prefix = MvcUriComponentsBuilder.getPathPrefix(controllerType);
        builder.path(prefix);
        String mapping = MvcUriComponentsBuilder.getClassMapping(controllerType);
        builder.path(mapping);
        return builder;
    }

    public static UriComponentsBuilder fromMethodName(Class<?> controllerType, String methodName, Object ... args) {
        Method method = MvcUriComponentsBuilder.getMethod(controllerType, methodName, args);
        return MvcUriComponentsBuilder.fromMethodInternal(null, controllerType, method, args);
    }

    public static UriComponentsBuilder fromMethodName(UriComponentsBuilder builder, Class<?> controllerType, String methodName, Object ... args) {
        Method method = MvcUriComponentsBuilder.getMethod(controllerType, methodName, args);
        return MvcUriComponentsBuilder.fromMethodInternal(builder, controllerType, method, args);
    }

    public static UriComponentsBuilder fromMethod(Class<?> controllerType, Method method, Object ... args) {
        return MvcUriComponentsBuilder.fromMethodInternal(null, controllerType, method, args);
    }

    public static UriComponentsBuilder fromMethod(UriComponentsBuilder baseUrl, @Nullable Class<?> controllerType, Method method, Object ... args) {
        return MvcUriComponentsBuilder.fromMethodInternal(baseUrl, controllerType != null ? controllerType : method.getDeclaringClass(), method, args);
    }

    public static UriComponentsBuilder fromMethodCall(Object info) {
        Assert.isInstanceOf(MethodInvocationInfo.class, info, "MethodInvocationInfo required");
        MethodInvocationInfo invocationInfo = (MethodInvocationInfo)info;
        Class<?> controllerType = invocationInfo.getControllerType();
        Method method = invocationInfo.getControllerMethod();
        Object[] arguments = invocationInfo.getArgumentValues();
        return MvcUriComponentsBuilder.fromMethodInternal(null, controllerType, method, arguments);
    }

    public static UriComponentsBuilder fromMethodCall(UriComponentsBuilder builder, Object info) {
        Assert.isInstanceOf(MethodInvocationInfo.class, info, "MethodInvocationInfo required");
        MethodInvocationInfo invocationInfo = (MethodInvocationInfo)info;
        Class<?> controllerType = invocationInfo.getControllerType();
        Method method = invocationInfo.getControllerMethod();
        Object[] arguments = invocationInfo.getArgumentValues();
        return MvcUriComponentsBuilder.fromMethodInternal(builder, controllerType, method, arguments);
    }

    public static <T> T on(Class<T> controllerType) {
        return MvcUriComponentsBuilder.controller(controllerType);
    }

    public static <T> T controller(Class<T> controllerType) {
        Assert.notNull(controllerType, "'controllerType' must not be null");
        return (T)ControllerMethodInvocationInterceptor.initProxy(controllerType, null);
    }

    public static MethodArgumentBuilder fromMappingName(String mappingName) {
        return MvcUriComponentsBuilder.fromMappingName(null, mappingName);
    }

    public static MethodArgumentBuilder fromMappingName(@Nullable UriComponentsBuilder builder, String name) {
        RequestMappingInfoHandlerMapping mapping;
        WebApplicationContext wac = MvcUriComponentsBuilder.getWebApplicationContext();
        Assert.notNull((Object)wac, "No WebApplicationContext");
        Map<String, RequestMappingInfoHandlerMapping> map = wac.getBeansOfType(RequestMappingInfoHandlerMapping.class);
        List<HandlerMethod> handlerMethods = null;
        Iterator<RequestMappingInfoHandlerMapping> iterator = map.values().iterator();
        while (iterator.hasNext() && (handlerMethods = (mapping = iterator.next()).getHandlerMethodsForMappingName(name)) == null) {
        }
        if (handlerMethods == null) {
            throw new IllegalArgumentException("Mapping not found: " + name);
        }
        if (handlerMethods.size() != 1) {
            throw new IllegalArgumentException("No unique match for mapping " + name + ": " + handlerMethods);
        }
        HandlerMethod handlerMethod = handlerMethods.get(0);
        Class<?> controllerType = handlerMethod.getBeanType();
        Method method = handlerMethod.getMethod();
        return new MethodArgumentBuilder(builder, controllerType, method);
    }

    public UriComponentsBuilder withController(Class<?> controllerType) {
        return MvcUriComponentsBuilder.fromController(this.baseUrl, controllerType);
    }

    public UriComponentsBuilder withMethodName(Class<?> controllerType, String methodName, Object ... args) {
        return MvcUriComponentsBuilder.fromMethodName(this.baseUrl, controllerType, methodName, args);
    }

    public UriComponentsBuilder withMethodCall(Object invocationInfo) {
        return MvcUriComponentsBuilder.fromMethodCall(this.baseUrl, invocationInfo);
    }

    public MethodArgumentBuilder withMappingName(String mappingName) {
        return MvcUriComponentsBuilder.fromMappingName(this.baseUrl, mappingName);
    }

    public UriComponentsBuilder withMethod(Class<?> controllerType, Method method, Object ... args) {
        return MvcUriComponentsBuilder.fromMethod(this.baseUrl, controllerType, method, args);
    }

    private static UriComponentsBuilder fromMethodInternal(@Nullable UriComponentsBuilder builder, Class<?> controllerType, Method method, Object ... args) {
        builder = MvcUriComponentsBuilder.getBaseUrlToUse(builder);
        String prefix = MvcUriComponentsBuilder.getPathPrefix(controllerType);
        builder.path(prefix);
        String typePath = MvcUriComponentsBuilder.getClassMapping(controllerType);
        String methodPath = MvcUriComponentsBuilder.getMethodMapping(method);
        String path = pathMatcher.combine(typePath, methodPath);
        path = PathPatternParser.defaultInstance.initFullPathPattern(path);
        builder.path(path);
        return MvcUriComponentsBuilder.applyContributors(builder, method, args);
    }

    private static UriComponentsBuilder getBaseUrlToUse(@Nullable UriComponentsBuilder baseUrl) {
        return baseUrl == null ? ServletUriComponentsBuilder.fromCurrentServletMapping() : baseUrl.cloneBuilder();
    }

    private static String getPathPrefix(Class<?> controllerType) {
        WebApplicationContext wac = MvcUriComponentsBuilder.getWebApplicationContext();
        if (wac != null) {
            Map<String, RequestMappingHandlerMapping> map = wac.getBeansOfType(RequestMappingHandlerMapping.class);
            for (RequestMappingHandlerMapping mapping : map.values()) {
                String prefix;
                if (!mapping.isHandler(controllerType) || (prefix = mapping.getPathPrefix(controllerType)) == null) continue;
                return prefix;
            }
        }
        return "";
    }

    private static String getClassMapping(Class<?> controllerType) {
        Assert.notNull(controllerType, "'controllerType' must not be null");
        RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(controllerType, RequestMapping.class);
        if (mapping == null) {
            return "/";
        }
        Object[] paths = mapping.path();
        if (ObjectUtils.isEmpty(paths) || !StringUtils.hasLength((String)paths[0])) {
            return "/";
        }
        if (paths.length > 1 && logger.isTraceEnabled()) {
            logger.trace((Object)("Using first of multiple paths on " + controllerType.getName()));
        }
        return paths[0];
    }

    private static String getMethodMapping(Method method) {
        Assert.notNull((Object)method, "'method' must not be null");
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (requestMapping == null) {
            throw new IllegalArgumentException("No @RequestMapping on: " + method.toGenericString());
        }
        Object[] paths = requestMapping.path();
        if (ObjectUtils.isEmpty(paths) || !StringUtils.hasLength((String)paths[0])) {
            return "/";
        }
        if (paths.length > 1 && logger.isTraceEnabled()) {
            logger.trace((Object)("Using first of multiple paths on " + method.toGenericString()));
        }
        return paths[0];
    }

    private static Method getMethod(Class<?> controllerType, String methodName, Object ... args) {
        ReflectionUtils.MethodFilter selector = method -> {
            String name = method.getName();
            int argLength = method.getParameterCount();
            return name.equals(methodName) && argLength == args.length;
        };
        Set<Method> methods = MethodIntrospector.selectMethods(controllerType, selector);
        if (methods.size() == 1) {
            return methods.iterator().next();
        }
        if (methods.size() > 1) {
            throw new IllegalArgumentException(String.format("Found two methods named '%s' accepting arguments %s in controller %s: [%s]", methodName, Arrays.asList(args), controllerType.getName(), methods));
        }
        throw new IllegalArgumentException("No method named '" + methodName + "' with " + args.length + " arguments found in controller " + controllerType.getName());
    }

    private static UriComponentsBuilder applyContributors(UriComponentsBuilder builder, Method method, Object ... args) {
        int argCount;
        CompositeUriComponentsContributor contributor = MvcUriComponentsBuilder.getUriComponentsContributor();
        int paramCount = method.getParameterCount();
        if (paramCount != (argCount = args.length)) {
            throw new IllegalArgumentException("Number of method parameters " + paramCount + " does not match number of argument values " + argCount);
        }
        HashMap<String, Object> uriVars = new HashMap<String, Object>();
        for (int i2 = 0; i2 < paramCount; ++i2) {
            SynthesizingMethodParameter param = new SynthesizingMethodParameter(method, i2);
            param.initParameterNameDiscovery(parameterNameDiscoverer);
            contributor.contributeMethodArgument(param, args[i2], builder, uriVars);
        }
        return builder.uriVariables(uriVars);
    }

    private static CompositeUriComponentsContributor getUriComponentsContributor() {
        WebApplicationContext wac = MvcUriComponentsBuilder.getWebApplicationContext();
        if (wac != null) {
            try {
                return wac.getBean(MVC_URI_COMPONENTS_CONTRIBUTOR_BEAN_NAME, CompositeUriComponentsContributor.class);
            }
            catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
                // empty catch block
            }
        }
        return defaultUriComponentsContributor;
    }

    @Nullable
    private static WebApplicationContext getWebApplicationContext() {
        String attributeName;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        WebApplicationContext wac = (WebApplicationContext)request.getAttribute(attributeName = DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (wac == null) {
            return null;
        }
        return wac;
    }

    public static class MethodArgumentBuilder {
        private final Class<?> controllerType;
        private final Method method;
        private final Object[] argumentValues;
        private final UriComponentsBuilder baseUrl;

        public MethodArgumentBuilder(Class<?> controllerType, Method method) {
            this(null, controllerType, method);
        }

        public MethodArgumentBuilder(@Nullable UriComponentsBuilder baseUrl, Class<?> controllerType, Method method) {
            Assert.notNull(controllerType, "'controllerType' is required");
            Assert.notNull((Object)method, "'method' is required");
            this.baseUrl = baseUrl != null ? baseUrl : UriComponentsBuilder.fromPath(MethodArgumentBuilder.getPath());
            this.controllerType = controllerType;
            this.method = method;
            this.argumentValues = new Object[method.getParameterCount()];
        }

        private static String getPath() {
            ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
            String path = builder.build().getPath();
            return path != null ? path : "";
        }

        public MethodArgumentBuilder arg(int index, Object value) {
            this.argumentValues[index] = value;
            return this;
        }

        public MethodArgumentBuilder encode() {
            this.baseUrl.encode();
            return this;
        }

        public String build() {
            return MvcUriComponentsBuilder.fromMethodInternal(this.baseUrl, this.controllerType, this.method, this.argumentValues).build().encode().toUriString();
        }

        public String buildAndExpand(Object ... uriVars) {
            return MvcUriComponentsBuilder.fromMethodInternal(this.baseUrl, this.controllerType, this.method, this.argumentValues).buildAndExpand(uriVars).encode().toString();
        }
    }

    private static class ControllerMethodInvocationInterceptor
    implements MethodInterceptor,
    InvocationHandler,
    MethodInvocationInfo {
        private final Class<?> controllerType;
        @Nullable
        private Method controllerMethod;
        @Nullable
        private Object[] argumentValues;

        ControllerMethodInvocationInterceptor(Class<?> controllerType) {
            this.controllerType = controllerType;
        }

        @Override
        @Nullable
        public Object intercept(@Nullable Object obj, Method method, Object[] args, @Nullable MethodProxy proxy) {
            switch (method.getName()) {
                case "getControllerType": {
                    return this.controllerType;
                }
                case "getControllerMethod": {
                    return this.controllerMethod;
                }
                case "getArgumentValues": {
                    return this.argumentValues;
                }
            }
            if (ReflectionUtils.isObjectMethod(method)) {
                return ReflectionUtils.invokeMethod(method, obj, args);
            }
            this.controllerMethod = method;
            this.argumentValues = args;
            Class<?> returnType = method.getReturnType();
            try {
                return returnType == Void.TYPE ? null : returnType.cast(ControllerMethodInvocationInterceptor.initProxy(returnType, this));
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Failed to create proxy for controller method return type: " + method, ex);
            }
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, @Nullable Object[] args) {
            return this.intercept(proxy, method, args != null ? args : new Object[]{}, null);
        }

        @Override
        public Class<?> getControllerType() {
            return this.controllerType;
        }

        @Override
        public Method getControllerMethod() {
            Assert.state(this.controllerMethod != null, "Not initialized yet");
            return this.controllerMethod;
        }

        @Override
        public Object[] getArgumentValues() {
            Assert.state(this.argumentValues != null, "Not initialized yet");
            return this.argumentValues;
        }

        private static <T> T initProxy(Class<?> controllerType, @Nullable ControllerMethodInvocationInterceptor interceptor) {
            ControllerMethodInvocationInterceptor controllerMethodInvocationInterceptor = interceptor = interceptor != null ? interceptor : new ControllerMethodInvocationInterceptor(controllerType);
            if (controllerType == Object.class) {
                return (T)interceptor;
            }
            if (controllerType.isInterface()) {
                ClassLoader classLoader = controllerType.getClassLoader();
                if (classLoader == null) {
                    classLoader = MethodInvocationInfo.class.getClassLoader();
                } else if (classLoader.getParent() == null) {
                    ClassLoader miiClassLoader = MethodInvocationInfo.class.getClassLoader();
                    for (ClassLoader miiParent = miiClassLoader.getParent(); miiParent != null; miiParent = miiParent.getParent()) {
                        if (classLoader != miiParent) continue;
                        classLoader = miiClassLoader;
                        break;
                    }
                }
                Class[] ifcs = new Class[]{controllerType, MethodInvocationInfo.class};
                return (T)Proxy.newProxyInstance(classLoader, ifcs, (InvocationHandler)interceptor);
            }
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(controllerType);
            enhancer.setInterfaces(new Class[]{MethodInvocationInfo.class});
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setCallbackType(MethodInterceptor.class);
            Class proxyClass = enhancer.createClass();
            T proxy = null;
            if (objenesis.isWorthTrying()) {
                try {
                    proxy = objenesis.newInstance(proxyClass, enhancer.getUseCache());
                }
                catch (ObjenesisException ex) {
                    logger.debug((Object)"Failed to create controller proxy, falling back on default constructor", (Throwable)ex);
                }
            }
            if (proxy == null) {
                try {
                    proxy = ReflectionUtils.accessibleConstructor(proxyClass, new Class[0]).newInstance(new Object[0]);
                }
                catch (Throwable ex) {
                    throw new IllegalStateException("Failed to create controller proxy or use default constructor", ex);
                }
            }
            ((Factory)proxy).setCallbacks(new Callback[]{interceptor});
            return proxy;
        }
    }

    public static interface MethodInvocationInfo {
        public Class<?> getControllerType();

        public Method getControllerMethod();

        public Object[] getArgumentValues();
    }
}

