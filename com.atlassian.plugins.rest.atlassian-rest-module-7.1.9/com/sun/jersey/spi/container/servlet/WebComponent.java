/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.sun.jersey.spi.container.servlet;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.core.ClasspathResourceConfig;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.core.TraceInformation;
import com.sun.jersey.api.core.servlet.WebAppResourceConfig;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.server.impl.InitialContextHelper;
import com.sun.jersey.server.impl.ThreadLocalInvoker;
import com.sun.jersey.server.impl.application.DeferredResourceConfig;
import com.sun.jersey.server.impl.cdi.CDIComponentProviderFactoryInitializer;
import com.sun.jersey.server.impl.container.servlet.JSPTemplateProcessor;
import com.sun.jersey.server.impl.ejb.EJBComponentProviderFactoryInitilizer;
import com.sun.jersey.server.impl.managedbeans.ManagedBeanComponentProviderFactoryInitilizer;
import com.sun.jersey.server.impl.monitoring.GlassFishMonitoringInitializer;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.container.ContainerListener;
import com.sun.jersey.spi.container.ContainerNotifier;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.ReloadListener;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationFactory;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class WebComponent
implements ContainerListener {
    public static final String APPLICATION_CONFIG_CLASS = "javax.ws.rs.Application";
    public static final String RESOURCE_CONFIG_CLASS = "com.sun.jersey.config.property.resourceConfigClass";
    public static final String JSP_TEMPLATES_BASE_PATH = "com.sun.jersey.config.property.JSPTemplatesBasePath";
    private static final Logger LOGGER = Logger.getLogger(WebComponent.class.getName());
    private final ThreadLocalInvoker<HttpServletRequest> requestInvoker = new ThreadLocalInvoker();
    private final ThreadLocalInvoker<HttpServletResponse> responseInvoker = new ThreadLocalInvoker();
    private WebConfig config;
    private ResourceConfig resourceConfig;
    private WebApplication application;

    public WebComponent() {
    }

    public WebComponent(Application app) {
        if (app == null) {
            throw new IllegalArgumentException();
        }
        this.resourceConfig = app instanceof ResourceConfig ? (ResourceConfig)app : new ApplicationAdapter(app);
    }

    public WebConfig getWebConfig() {
        return this.config;
    }

    public ResourceConfig getResourceConfig() {
        return this.resourceConfig;
    }

    public void init(WebConfig webConfig) throws ServletException {
        this.config = webConfig;
        if (this.resourceConfig == null) {
            this.resourceConfig = this.createResourceConfig(this.config);
        }
        this.load();
        Object o = this.resourceConfig.getProperties().get("com.sun.jersey.spi.container.ContainerNotifier");
        if (o instanceof List) {
            List list = (List)o;
            for (Object elem : list) {
                if (!(elem instanceof ContainerNotifier)) continue;
                ContainerNotifier crf = (ContainerNotifier)elem;
                crf.addListener(this);
            }
        } else if (o instanceof ContainerNotifier) {
            ContainerNotifier crf = (ContainerNotifier)o;
            crf.addListener(this);
        }
    }

    public void destroy() {
        if (this.application != null) {
            this.application.destroy();
        }
    }

    public int service(URI baseUri, URI requestUri, final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebApplication _application = this.application;
        ContainerRequest cRequest = this.createRequest(_application, request, baseUri, requestUri);
        cRequest.setSecurityContext(new SecurityContext(){

            @Override
            public Principal getUserPrincipal() {
                return request.getUserPrincipal();
            }

            @Override
            public boolean isUserInRole(String role) {
                return request.isUserInRole(role);
            }

            @Override
            public boolean isSecure() {
                return request.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return request.getAuthType();
            }
        });
        try {
            this.filterFormParameters(request, cRequest);
            UriRuleProbeProvider.requestStart(requestUri);
            this.requestInvoker.set(request);
            this.responseInvoker.set(response);
            Writer w = new Writer(response);
            _application.handleRequest(cRequest, w);
            int n = w.cResponse.getStatus();
            return n;
        }
        catch (WebApplicationException ex) {
            Response exResponse = ex.getResponse();
            String entity = exResponse.getEntity() != null ? exResponse.getEntity().toString() : null;
            response.sendError(exResponse.getStatus(), entity);
            int n = exResponse.getStatus();
            return n;
        }
        catch (MappableContainerException ex) {
            this.traceOnException(cRequest, response);
            throw new ServletException(ex.getCause());
        }
        catch (ContainerException ex) {
            this.traceOnException(cRequest, response);
            throw new ServletException((Throwable)ex);
        }
        catch (RuntimeException ex) {
            this.traceOnException(cRequest, response);
            throw ex;
        }
        finally {
            UriRuleProbeProvider.requestEnd();
            this.requestInvoker.set(null);
            this.responseInvoker.set(null);
        }
    }

    protected ContainerRequest createRequest(WebApplication app, HttpServletRequest request, URI baseUri, URI requestUri) throws IOException {
        return new ContainerRequest(app, request.getMethod(), baseUri, requestUri, this.getHeaders(request), (InputStream)request.getInputStream());
    }

    private void traceOnException(ContainerRequest cRequest, final HttpServletResponse response) {
        if (cRequest.isTracingEnabled()) {
            TraceInformation ti = (TraceInformation)cRequest.getProperties().get(TraceInformation.class.getName());
            ti.addTraceHeaders(new TraceInformation.TraceHeaderListener(){

                @Override
                public void onHeader(String name, String value) {
                    response.addHeader(name, value);
                }
            });
        }
    }

    protected WebApplication create() {
        return WebApplicationFactory.createWebApplication();
    }

    protected void configure(WebConfig wc, ResourceConfig rc, WebApplication wa) {
        this.configureJndiResources(rc);
        rc.getSingletons().add(new ContextInjectableProvider<HttpServletRequest>((Type)((Object)HttpServletRequest.class), (HttpServletRequest)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{HttpServletRequest.class}, this.requestInvoker)));
        rc.getSingletons().add(new ContextInjectableProvider<HttpServletResponse>((Type)((Object)HttpServletResponse.class), (HttpServletResponse)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{HttpServletResponse.class}, this.responseInvoker)));
        GenericEntity<ThreadLocal<HttpServletRequest>> requestThreadLocal = new GenericEntity<ThreadLocal<HttpServletRequest>>(this.requestInvoker.getImmutableThreadLocal()){};
        rc.getSingletons().add(new ContextInjectableProvider(requestThreadLocal.getType(), requestThreadLocal.getEntity()));
        GenericEntity<ThreadLocal<HttpServletResponse>> responseThreadLocal = new GenericEntity<ThreadLocal<HttpServletResponse>>(this.responseInvoker.getImmutableThreadLocal()){};
        rc.getSingletons().add(new ContextInjectableProvider(responseThreadLocal.getType(), responseThreadLocal.getEntity()));
        rc.getSingletons().add(new ContextInjectableProvider<ServletContext>((Type)((Object)ServletContext.class), wc.getServletContext()));
        rc.getSingletons().add(new ContextInjectableProvider<WebConfig>((Type)((Object)WebConfig.class), wc));
        rc.getClasses().add(JSPTemplateProcessor.class);
        EJBComponentProviderFactoryInitilizer.initialize(rc);
        CDIComponentProviderFactoryInitializer.initialize(wc, rc, wa);
        ManagedBeanComponentProviderFactoryInitilizer.initialize(rc);
        GlassFishMonitoringInitializer.initialize();
    }

    protected void initiate(ResourceConfig rc, WebApplication wa) {
        wa.initiate(rc);
    }

    public void load() {
        WebApplication _application = this.create();
        this.configure(this.config, this.resourceConfig, _application);
        this.initiate(this.resourceConfig, _application);
        this.application = _application;
    }

    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig wc) throws ServletException {
        return this.getWebAppResourceConfig(props, wc);
    }

    @Override
    public void onReload() {
        WebApplication oldApplication = this.application;
        WebApplication newApplication = this.create();
        this.initiate(this.resourceConfig, newApplication);
        this.application = newApplication;
        if (this.resourceConfig instanceof ReloadListener) {
            ((ReloadListener)((Object)this.resourceConfig)).onReload();
        }
        oldApplication.destroy();
    }

    ResourceConfig getWebAppResourceConfig(Map<String, Object> props, WebConfig webConfig) throws ServletException {
        return new WebAppResourceConfig(props, webConfig.getServletContext());
    }

    private ResourceConfig createResourceConfig(WebConfig webConfig) throws ServletException {
        Map<String, Object> props = this.getInitParams(webConfig);
        ResourceConfig rc = this.createResourceConfig(webConfig, props);
        rc.setPropertiesAndFeatures(props);
        return rc;
    }

    private ResourceConfig createResourceConfig(WebConfig webConfig, Map<String, Object> props) throws ServletException {
        String resourceConfigClassName = webConfig.getInitParameter(RESOURCE_CONFIG_CLASS);
        if (resourceConfigClassName == null) {
            resourceConfigClassName = webConfig.getInitParameter(APPLICATION_CONFIG_CLASS);
        }
        if (resourceConfigClassName == null) {
            String packages = webConfig.getInitParameter("com.sun.jersey.config.property.packages");
            if (packages != null) {
                props.put("com.sun.jersey.config.property.packages", packages);
                return new PackagesResourceConfig(props);
            }
            ResourceConfig defaultConfig = webConfig.getDefaultResourceConfig(props);
            if (defaultConfig != null) {
                return defaultConfig;
            }
            return this.getDefaultResourceConfig(props, webConfig);
        }
        try {
            Class resourceConfigClass = AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(resourceConfigClassName));
            if (resourceConfigClass == ClasspathResourceConfig.class) {
                String[] paths = this.getPaths(webConfig.getInitParameter("com.sun.jersey.config.property.classpath"));
                props.put("com.sun.jersey.config.property.classpath", paths);
                return new ClasspathResourceConfig(props);
            }
            if (ResourceConfig.class.isAssignableFrom(resourceConfigClass)) {
                try {
                    Constructor constructor = resourceConfigClass.getConstructor(Map.class);
                    if (ClasspathResourceConfig.class.isAssignableFrom(resourceConfigClass)) {
                        String[] paths = this.getPaths(webConfig.getInitParameter("com.sun.jersey.config.property.classpath"));
                        props.put("com.sun.jersey.config.property.classpath", paths);
                    }
                    return (ResourceConfig)constructor.newInstance(props);
                }
                catch (NoSuchMethodException constructor) {
                }
                catch (Exception e) {
                    throw new ServletException((Throwable)e);
                }
                return new DeferredResourceConfig((Class<? extends Application>)resourceConfigClass.asSubclass(ResourceConfig.class));
            }
            if (Application.class.isAssignableFrom(resourceConfigClass)) {
                return new DeferredResourceConfig((Class<? extends Application>)resourceConfigClass.asSubclass(Application.class));
            }
            String message = "Resource configuration class, " + resourceConfigClassName + ", is not a super class of " + Application.class;
            throw new ServletException(message);
        }
        catch (ClassNotFoundException e) {
            String message = "Resource configuration class, " + resourceConfigClassName + ", could not be loaded";
            throw new ServletException(message, (Throwable)e);
        }
        catch (PrivilegedActionException e) {
            String message = "Resource configuration class, " + resourceConfigClassName + ", could not be loaded";
            throw new ServletException(message, e.getCause());
        }
    }

    private Map<String, Object> getInitParams(WebConfig webConfig) {
        HashMap<String, Object> props = new HashMap<String, Object>();
        Enumeration names = webConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            props.put(name, webConfig.getInitParameter(name));
        }
        return props;
    }

    private String[] getPaths(String classpath) throws ServletException {
        ServletContext context = this.config.getServletContext();
        if (classpath == null) {
            String[] paths = new String[]{context.getRealPath("/WEB-INF/lib"), context.getRealPath("/WEB-INF/classes")};
            if (paths[0] == null && paths[1] == null) {
                String message = "The default deployment configuration that scans for classes in /WEB-INF/lib and /WEB-INF/classes is not supported for the application server.Try using the package scanning configuration, see the JavaDoc for " + PackagesResourceConfig.class.getName() + " and the property " + "com.sun.jersey.config.property.packages" + ".";
                throw new ServletException(message);
            }
            return paths;
        }
        String[] virtualPaths = classpath.split(";");
        ArrayList<String> resourcePaths = new ArrayList<String>();
        for (String virtualPath : virtualPaths) {
            String path;
            if ((virtualPath = virtualPath.trim()).length() == 0 || (path = context.getRealPath(virtualPath)) == null) continue;
            resourcePaths.add(path);
        }
        if (resourcePaths.isEmpty()) {
            String message = "None of the declared classpath locations, " + classpath + ", could be resolved. This could be because the default deployment configuration that scans for classes in classpath locations is not supported. Try using the package scanning configuration, see the JavaDoc for " + PackagesResourceConfig.class.getName() + " and the property " + "com.sun.jersey.config.property.packages" + ".";
            throw new ServletException(message);
        }
        return resourcePaths.toArray(new String[resourcePaths.size()]);
    }

    private void configureJndiResources(ResourceConfig rc) {
        InitialContext x = InitialContextHelper.getInitialContext();
        if (x != null) {
            Iterator<Class<?>> i = rc.getClasses().iterator();
            while (i.hasNext()) {
                Class<?> c = i.next();
                if (!c.isInterface()) continue;
                try {
                    Object o = x.lookup(c.getName());
                    if (o == null) continue;
                    i.remove();
                    rc.getSingletons().add(o);
                    LOGGER.log(Level.INFO, "An instance of the class " + c.getName() + " is found by JNDI look up using the class name as the JNDI name. The instance will be registered as a singleton.");
                }
                catch (NamingException ex) {
                    LOGGER.log(Level.CONFIG, "JNDI lookup failed for Jersey application resource " + c.getName(), ex);
                }
            }
        }
    }

    private void filterFormParameters(HttpServletRequest servletRequest, ContainerRequest containerRequet) throws IOException {
        if (MediaTypes.typeEquals(MediaType.APPLICATION_FORM_URLENCODED_TYPE, containerRequet.getMediaType()) && !this.isEntityPresent(containerRequet)) {
            Form f = new Form();
            Enumeration e = servletRequest.getParameterNames();
            while (e.hasMoreElements()) {
                String name = (String)e.nextElement();
                String[] values = servletRequest.getParameterValues(name);
                f.put(name, Arrays.asList(values));
            }
            if (!f.isEmpty()) {
                containerRequet.getProperties().put("com.sun.jersey.api.representation.form", f);
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "A servlet request, to the URI " + containerRequet.getRequestUri() + ", contains form parameters in the request body but the request body has been consumed by the servlet or a servlet filter accessing the request parameters. Only resource methods using @FormParam will work as expected. Resource methods consuming the request body by other means will not work as expected.");
                }
            }
        }
    }

    private boolean isEntityPresent(ContainerRequest cr) throws IOException {
        InputStream in = cr.getEntityInputStream();
        if (!in.markSupported()) {
            in = new BufferedInputStream(in, ReaderWriter.BUFFER_SIZE);
            cr.setEntityInputStream(in);
        }
        in.mark(1);
        if (in.read() == -1) {
            return false;
        }
        in.reset();
        return true;
    }

    private InBoundHeaders getHeaders(HttpServletRequest request) {
        InBoundHeaders rh = new InBoundHeaders();
        Enumeration names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            LinkedList valueList = new LinkedList();
            Enumeration values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                valueList.add(values.nextElement());
            }
            rh.put(name, valueList);
        }
        return rh;
    }

    protected static class ContextInjectableProvider<T>
    extends SingletonTypeInjectableProvider<Context, T> {
        protected ContextInjectableProvider(Type type, T instance) {
            super(type, instance);
        }
    }

    private static final class Writer
    extends OutputStream
    implements ContainerResponseWriter {
        final HttpServletResponse response;
        ContainerResponse cResponse;
        long contentLength;
        OutputStream out;
        boolean statusAndHeadersWritten = false;

        Writer(HttpServletResponse response) {
            this.response = response;
        }

        @Override
        public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse cResponse) throws IOException {
            this.contentLength = contentLength;
            this.cResponse = cResponse;
            this.statusAndHeadersWritten = false;
            return this;
        }

        @Override
        public void finish() throws IOException {
            if (this.statusAndHeadersWritten) {
                return;
            }
            this.writeHeaders();
            this.writeStatus();
        }

        private void writeStatus() {
            Response.StatusType statusType = this.cResponse.getStatusType();
            String reasonPhrase = statusType.getReasonPhrase();
            if (reasonPhrase != null) {
                this.response.setStatus(statusType.getStatusCode(), reasonPhrase);
            } else {
                this.response.setStatus(statusType.getStatusCode());
            }
        }

        @Override
        public void write(int b) throws IOException {
            this.initiate();
            this.out.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (b.length > 0) {
                this.initiate();
                this.out.write(b);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (len > 0) {
                this.initiate();
                this.out.write(b, off, len);
            }
        }

        @Override
        public void flush() throws IOException {
            this.writeStatusAndHeaders();
            if (this.out != null) {
                this.out.flush();
            }
        }

        @Override
        public void close() throws IOException {
            this.initiate();
            this.out.close();
        }

        void initiate() throws IOException {
            if (this.out == null) {
                this.writeStatusAndHeaders();
                this.out = this.response.getOutputStream();
            }
        }

        void writeStatusAndHeaders() {
            if (this.statusAndHeadersWritten) {
                return;
            }
            this.writeHeaders();
            this.writeStatus();
            this.statusAndHeadersWritten = true;
        }

        void writeHeaders() {
            MultivaluedMap<String, Object> headers = this.cResponse.getHttpHeaders();
            for (Map.Entry e : headers.entrySet()) {
                for (Object v : (List)e.getValue()) {
                    this.response.addHeader((String)e.getKey(), ContainerResponse.getHeaderValue(v));
                }
            }
            if (this.contentLength != -1L && this.contentLength < Integer.MAX_VALUE) {
                this.response.setContentLength((int)this.contentLength);
            }
        }
    }
}

