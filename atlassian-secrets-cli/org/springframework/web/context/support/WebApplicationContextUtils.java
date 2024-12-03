/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.faces.context.ExternalContext
 *  javax.faces.context.FacesContext
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package org.springframework.web.context.support;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.support.ServletConfigPropertySource;
import org.springframework.web.context.support.ServletContextPropertySource;
import org.springframework.web.context.support.ServletContextScope;

public abstract class WebApplicationContextUtils {
    private static final boolean jsfPresent = ClassUtils.isPresent("javax.faces.context.FacesContext", RequestContextHolder.class.getClassLoader());

    public static WebApplicationContext getRequiredWebApplicationContext(ServletContext sc) throws IllegalStateException {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(sc);
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
        }
        return wac;
    }

    @Nullable
    public static WebApplicationContext getWebApplicationContext(ServletContext sc) {
        return WebApplicationContextUtils.getWebApplicationContext(sc, WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    @Nullable
    public static WebApplicationContext getWebApplicationContext(ServletContext sc, String attrName) {
        Assert.notNull((Object)sc, "ServletContext must not be null");
        Object attr = sc.getAttribute(attrName);
        if (attr == null) {
            return null;
        }
        if (attr instanceof RuntimeException) {
            throw (RuntimeException)attr;
        }
        if (attr instanceof Error) {
            throw (Error)attr;
        }
        if (attr instanceof Exception) {
            throw new IllegalStateException((Exception)attr);
        }
        if (!(attr instanceof WebApplicationContext)) {
            throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
        }
        return (WebApplicationContext)attr;
    }

    @Nullable
    public static WebApplicationContext findWebApplicationContext(ServletContext sc) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(sc);
        if (wac == null) {
            Enumeration attrNames = sc.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String attrName = (String)attrNames.nextElement();
                Object attrValue = sc.getAttribute(attrName);
                if (!(attrValue instanceof WebApplicationContext)) continue;
                if (wac != null) {
                    throw new IllegalStateException("No unique WebApplicationContext found: more than one DispatcherServlet registered with publishContext=true?");
                }
                wac = (WebApplicationContext)attrValue;
            }
        }
        return wac;
    }

    public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory) {
        WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, null);
    }

    public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory, @Nullable ServletContext sc) {
        beanFactory.registerScope("request", new RequestScope());
        beanFactory.registerScope("session", new SessionScope());
        if (sc != null) {
            ServletContextScope appScope = new ServletContextScope(sc);
            beanFactory.registerScope("application", appScope);
            sc.setAttribute(ServletContextScope.class.getName(), (Object)appScope);
        }
        beanFactory.registerResolvableDependency(ServletRequest.class, new RequestObjectFactory());
        beanFactory.registerResolvableDependency(ServletResponse.class, new ResponseObjectFactory());
        beanFactory.registerResolvableDependency(HttpSession.class, new SessionObjectFactory());
        beanFactory.registerResolvableDependency(WebRequest.class, new WebRequestObjectFactory());
        if (jsfPresent) {
            FacesDependencyRegistrar.registerFacesDependencies(beanFactory);
        }
    }

    public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf, @Nullable ServletContext sc) {
        WebApplicationContextUtils.registerEnvironmentBeans(bf, sc, null);
    }

    public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf, @Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
        if (servletContext != null && !bf.containsBean("servletContext")) {
            bf.registerSingleton("servletContext", servletContext);
        }
        if (servletConfig != null && !bf.containsBean("servletConfig")) {
            bf.registerSingleton("servletConfig", servletConfig);
        }
        if (!bf.containsBean("contextParameters")) {
            String paramName;
            Enumeration paramNameEnum;
            HashMap<String, String> parameterMap = new HashMap<String, String>();
            if (servletContext != null) {
                paramNameEnum = servletContext.getInitParameterNames();
                while (paramNameEnum.hasMoreElements()) {
                    paramName = (String)paramNameEnum.nextElement();
                    parameterMap.put(paramName, servletContext.getInitParameter(paramName));
                }
            }
            if (servletConfig != null) {
                paramNameEnum = servletConfig.getInitParameterNames();
                while (paramNameEnum.hasMoreElements()) {
                    paramName = (String)paramNameEnum.nextElement();
                    parameterMap.put(paramName, servletConfig.getInitParameter(paramName));
                }
            }
            bf.registerSingleton("contextParameters", Collections.unmodifiableMap(parameterMap));
        }
        if (!bf.containsBean("contextAttributes")) {
            HashMap<String, Object> attributeMap = new HashMap<String, Object>();
            if (servletContext != null) {
                Enumeration attrNameEnum = servletContext.getAttributeNames();
                while (attrNameEnum.hasMoreElements()) {
                    String attrName = (String)attrNameEnum.nextElement();
                    attributeMap.put(attrName, servletContext.getAttribute(attrName));
                }
            }
            bf.registerSingleton("contextAttributes", Collections.unmodifiableMap(attributeMap));
        }
    }

    public static void initServletPropertySources(MutablePropertySources propertySources, ServletContext servletContext) {
        WebApplicationContextUtils.initServletPropertySources(propertySources, servletContext, null);
    }

    public static void initServletPropertySources(MutablePropertySources sources, @Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
        Assert.notNull((Object)sources, "'propertySources' must not be null");
        String name = "servletContextInitParams";
        if (servletContext != null && sources.contains(name) && sources.get(name) instanceof PropertySource.StubPropertySource) {
            sources.replace(name, new ServletContextPropertySource(name, servletContext));
        }
        name = "servletConfigInitParams";
        if (servletConfig != null && sources.contains(name) && sources.get(name) instanceof PropertySource.StubPropertySource) {
            sources.replace(name, new ServletConfigPropertySource(name, servletConfig));
        }
    }

    private static ServletRequestAttributes currentRequestAttributes() {
        RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();
        if (!(requestAttr instanceof ServletRequestAttributes)) {
            throw new IllegalStateException("Current request is not a servlet request");
        }
        return (ServletRequestAttributes)requestAttr;
    }

    private static class FacesDependencyRegistrar {
        private FacesDependencyRegistrar() {
        }

        public static void registerFacesDependencies(ConfigurableListableBeanFactory beanFactory) {
            beanFactory.registerResolvableDependency(FacesContext.class, new ObjectFactory<FacesContext>(){

                @Override
                public FacesContext getObject() {
                    return FacesContext.getCurrentInstance();
                }

                public String toString() {
                    return "Current JSF FacesContext";
                }
            });
            beanFactory.registerResolvableDependency(ExternalContext.class, new ObjectFactory<ExternalContext>(){

                @Override
                public ExternalContext getObject() {
                    return FacesContext.getCurrentInstance().getExternalContext();
                }

                public String toString() {
                    return "Current JSF ExternalContext";
                }
            });
        }
    }

    private static class WebRequestObjectFactory
    implements ObjectFactory<WebRequest>,
    Serializable {
        private WebRequestObjectFactory() {
        }

        @Override
        public WebRequest getObject() {
            ServletRequestAttributes requestAttr = WebApplicationContextUtils.currentRequestAttributes();
            return new ServletWebRequest(requestAttr.getRequest(), requestAttr.getResponse());
        }

        public String toString() {
            return "Current ServletWebRequest";
        }
    }

    private static class SessionObjectFactory
    implements ObjectFactory<HttpSession>,
    Serializable {
        private SessionObjectFactory() {
        }

        @Override
        public HttpSession getObject() {
            return WebApplicationContextUtils.currentRequestAttributes().getRequest().getSession();
        }

        public String toString() {
            return "Current HttpSession";
        }
    }

    private static class ResponseObjectFactory
    implements ObjectFactory<ServletResponse>,
    Serializable {
        private ResponseObjectFactory() {
        }

        @Override
        public ServletResponse getObject() {
            HttpServletResponse response = WebApplicationContextUtils.currentRequestAttributes().getResponse();
            if (response == null) {
                throw new IllegalStateException("Current servlet response not available - consider using RequestContextFilter instead of RequestContextListener");
            }
            return response;
        }

        public String toString() {
            return "Current HttpServletResponse";
        }
    }

    private static class RequestObjectFactory
    implements ObjectFactory<ServletRequest>,
    Serializable {
        private RequestObjectFactory() {
        }

        @Override
        public ServletRequest getObject() {
            return WebApplicationContextUtils.currentRequestAttributes().getRequest();
        }

        public String toString() {
            return "Current HttpServletRequest";
        }
    }
}

