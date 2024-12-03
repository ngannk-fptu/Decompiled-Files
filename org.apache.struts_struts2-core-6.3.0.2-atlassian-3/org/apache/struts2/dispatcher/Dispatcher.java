/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.LocaleUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.message.ParameterizedMessage
 */
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.FileManagerFactoryProvider;
import com.opensymphony.xwork2.config.FileManagerProvider;
import com.opensymphony.xwork2.config.ServletContextAwareConfigurationProvider;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.StrutsException;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.PropertiesConfigurationProvider;
import org.apache.struts2.config.StrutsBeanSelectionProvider;
import org.apache.struts2.config.StrutsJavaConfiguration;
import org.apache.struts2.config.StrutsJavaConfigurationProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.dispatcher.ApplicationMap;
import org.apache.struts2.dispatcher.AttributeMap;
import org.apache.struts2.dispatcher.ContainerHolder;
import org.apache.struts2.dispatcher.DispatcherErrorHandler;
import org.apache.struts2.dispatcher.DispatcherListener;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.apache.struts2.util.fs.JBossFileManager;

public class Dispatcher {
    private static final Logger LOG = LogManager.getLogger(Dispatcher.class);
    public static final String REQUEST_POST_METHOD = "POST";
    public static final String MULTIPART_FORM_DATA_REGEX = "^multipart/form-data(?:\\s*;\\s*boundary=[0-9a-zA-Z'()+_,\\-./:=?]{1,70})?(?:\\s*;\\s*charset=[a-zA-Z\\-0-9]{3,14})?";
    private static final String CONFIG_SPLIT_REGEX = "\\s*,\\s*";
    private static final ThreadLocal<Dispatcher> instance = new ThreadLocal();
    private static final List<DispatcherListener> dispatcherListeners = new CopyOnWriteArrayList<DispatcherListener>();
    private Container injectedContainer;
    private boolean devMode;
    private boolean disableRequestAttributeValueStackLookup;
    private String defaultEncoding;
    private String defaultLocale;
    private String multipartSaveDir;
    private boolean multipartSupportEnabled = true;
    private Pattern multipartValidationPattern = Pattern.compile("^multipart/form-data(?:\\s*;\\s*boundary=[0-9a-zA-Z'()+_,\\-./:=?]{1,70})?(?:\\s*;\\s*charset=[a-zA-Z\\-0-9]{3,14})?");
    private static final String DEFAULT_CONFIGURATION_PATHS = "struts-default.xml,struts-plugin.xml,struts.xml";
    private boolean paramsWorkaroundEnabled = false;
    private boolean handleException;
    private DispatcherErrorHandler errorHandler;
    protected ConfigurationManager configurationManager;
    private ObjectFactory objectFactory;
    private ActionProxyFactory actionProxyFactory;
    private LocaleProviderFactory localeProviderFactory;
    private StaticContentLoader staticContentLoader;
    private ActionMapper actionMapper;
    private ValueStackFactory valueStackFactory;
    protected ServletContext servletContext;
    protected Map<String, String> initParams;

    public static Dispatcher getInstance() {
        return instance.get();
    }

    public static void setInstance(Dispatcher instance) {
        Dispatcher.instance.set(instance);
    }

    public static void clearInstance() {
        instance.remove();
    }

    public static void addDispatcherListener(DispatcherListener listener) {
        dispatcherListeners.add(listener);
    }

    public static void removeDispatcherListener(DispatcherListener listener) {
        dispatcherListeners.remove(listener);
    }

    public Dispatcher(ServletContext servletContext, Map<String, String> initParams) {
        this.servletContext = servletContext;
        this.initParams = initParams;
    }

    public static Dispatcher getInstance(ServletContext servletContext) {
        return (Dispatcher)servletContext.getAttribute("com.opensymphony.xwork2.dispatcher.ServletDispatcher");
    }

    @Inject(value="struts.devMode")
    public void setDevMode(String mode) {
        this.devMode = Boolean.parseBoolean(mode);
    }

    public boolean isDevMode() {
        return this.devMode;
    }

    @Inject(value="struts.disableRequestAttributeValueStackLookup", required=false)
    public void setDisableRequestAttributeValueStackLookup(String disableRequestAttributeValueStackLookup) {
        this.disableRequestAttributeValueStackLookup = BooleanUtils.toBoolean((String)disableRequestAttributeValueStackLookup);
    }

    @Inject(value="struts.locale", required=false)
    public void setDefaultLocale(String val) {
        this.defaultLocale = val;
    }

    @Inject(value="struts.i18n.encoding")
    public void setDefaultEncoding(String val) {
        this.defaultEncoding = val;
    }

    @Inject(value="struts.multipart.saveDir")
    public void setMultipartSaveDir(String val) {
        this.multipartSaveDir = val;
    }

    @Deprecated
    public void setMultipartHandler(String val) {
    }

    @Inject(value="struts.multipart.enabled", required=false)
    public void setMultipartSupportEnabled(String multipartSupportEnabled) {
        this.multipartSupportEnabled = Boolean.parseBoolean(multipartSupportEnabled);
    }

    @Inject(value="struts.multipart.validationRegex", required=false)
    public void setMultipartValidationRegex(String multipartValidationRegex) {
        this.multipartValidationPattern = Pattern.compile(multipartValidationRegex);
    }

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    public ValueStackFactory getValueStackFactory() {
        return this.valueStackFactory;
    }

    @Inject(value="struts.handle.exception")
    public void setHandleException(String handleException) {
        this.handleException = Boolean.parseBoolean(handleException);
    }

    @Inject(value="struts.dispatcher.parametersWorkaround")
    public void setDispatchersParametersWorkaround(String dispatchersParametersWorkaround) {
        this.paramsWorkaroundEnabled = Boolean.parseBoolean(dispatchersParametersWorkaround) || this.servletContext != null && StringUtils.contains((CharSequence)this.servletContext.getServerInfo(), (CharSequence)"WebLogic");
    }

    public boolean isHandleException() {
        return this.handleException;
    }

    @Inject
    public void setDispatcherErrorHandler(DispatcherErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setActionProxyFactory(ActionProxyFactory actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }

    public ActionProxyFactory getActionProxyFactory() {
        return this.actionProxyFactory;
    }

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        this.localeProviderFactory = localeProviderFactory;
    }

    @Inject
    public void setStaticContentLoader(StaticContentLoader staticContentLoader) {
        this.staticContentLoader = staticContentLoader;
    }

    public StaticContentLoader getStaticContentLoader() {
        return this.staticContentLoader;
    }

    @Inject
    public void setActionMapper(ActionMapper actionMapper) {
        this.actionMapper = actionMapper;
    }

    public ActionMapper getActionMapper() {
        return this.actionMapper;
    }

    public void cleanup() {
        if (this.objectFactory == null) {
            LOG.warn("Object Factory is null, something is seriously wrong, no clean up will be performed");
        }
        if (this.objectFactory instanceof ObjectFactoryDestroyable) {
            try {
                ((ObjectFactoryDestroyable)((Object)this.objectFactory)).destroy();
            }
            catch (Exception e) {
                LOG.error("Exception occurred while destroying ObjectFactory [{}]", (Object)this.objectFactory.toString(), (Object)e);
            }
        }
        instance.remove();
        this.servletContext.setAttribute("com.opensymphony.xwork2.dispatcher.ServletDispatcher", null);
        if (!dispatcherListeners.isEmpty()) {
            for (DispatcherListener l : dispatcherListeners) {
                l.dispatcherDestroyed(this);
            }
        }
        HashSet<Interceptor> interceptors = new HashSet<Interceptor>();
        Collection<PackageConfig> packageConfigs = this.configurationManager.getConfiguration().getPackageConfigs().values();
        for (PackageConfig packageConfig : packageConfigs) {
            for (Object config : packageConfig.getAllInterceptorConfigs().values()) {
                if (!(config instanceof InterceptorStackConfig)) continue;
                for (InterceptorMapping interceptorMapping : ((InterceptorStackConfig)config).getInterceptors()) {
                    interceptors.add(interceptorMapping.getInterceptor());
                }
            }
        }
        for (Interceptor interceptor : interceptors) {
            interceptor.destroy();
        }
        ContainerHolder.clear();
        ActionContext.clear();
        this.configurationManager.destroyConfiguration();
        this.configurationManager = null;
    }

    private void init_FileManager() throws ClassNotFoundException {
        if (this.initParams.containsKey("struts.fileManager")) {
            String fileManagerClassName = this.initParams.get("struts.fileManager");
            Class<?> fileManagerClass = Class.forName(fileManagerClassName);
            LOG.info("Custom FileManager specified: {}", (Object)fileManagerClassName);
            this.configurationManager.addContainerProvider(new FileManagerProvider(fileManagerClass, fileManagerClass.getSimpleName()));
        } else {
            this.configurationManager.addContainerProvider(new FileManagerProvider(JBossFileManager.class, "jboss"));
        }
        if (this.initParams.containsKey("struts.fileManagerFactory")) {
            String fileManagerFactoryClassName = this.initParams.get("struts.fileManagerFactory");
            Class<?> fileManagerFactoryClass = Class.forName(fileManagerFactoryClassName);
            LOG.info("Custom FileManagerFactory specified: {}", (Object)fileManagerFactoryClassName);
            this.configurationManager.addContainerProvider(new FileManagerFactoryProvider(fileManagerFactoryClass));
        }
    }

    private void init_DefaultProperties() {
        this.configurationManager.addContainerProvider(new DefaultPropertiesProvider());
    }

    private void init_LegacyStrutsProperties() {
        this.configurationManager.addContainerProvider(new PropertiesConfigurationProvider());
    }

    private void init_TraditionalXmlConfigurations() {
        String configPaths = this.initParams.get("config");
        if (configPaths == null) {
            configPaths = DEFAULT_CONFIGURATION_PATHS;
        }
        this.loadConfigPaths(configPaths);
    }

    private void loadConfigPaths(String configPaths) {
        String[] files;
        for (String file : files = configPaths.split(CONFIG_SPLIT_REGEX)) {
            if (!file.endsWith(".xml")) {
                throw new IllegalArgumentException("Invalid configuration file name");
            }
            this.configurationManager.addContainerProvider(this.createStrutsXmlConfigurationProvider(file, this.servletContext));
        }
    }

    protected XmlConfigurationProvider createStrutsXmlConfigurationProvider(String filename, ServletContext ctx) {
        return new StrutsXmlConfigurationProvider(filename, ctx);
    }

    @Deprecated
    protected XmlConfigurationProvider createStrutsXmlConfigurationProvider(String filename, boolean errorIfMissing, ServletContext ctx) {
        return this.createStrutsXmlConfigurationProvider(filename, ctx);
    }

    private void init_JavaConfigurations() {
        String configClasses = this.initParams.get("javaConfigClasses");
        if (configClasses != null) {
            String[] classes;
            for (String cname : classes = configClasses.split(CONFIG_SPLIT_REGEX)) {
                try {
                    Class cls = ClassLoaderUtil.loadClass(cname, this.getClass());
                    StrutsJavaConfiguration config = (StrutsJavaConfiguration)cls.newInstance();
                    this.configurationManager.addContainerProvider(this.createJavaConfigurationProvider(config));
                }
                catch (InstantiationException e) {
                    throw new ConfigurationException("Unable to instantiate java configuration: " + cname, e);
                }
                catch (IllegalAccessException e) {
                    throw new ConfigurationException("Unable to access java configuration: " + cname, e);
                }
                catch (ClassNotFoundException e) {
                    throw new ConfigurationException("Unable to locate java configuration class: " + cname, e);
                }
            }
        }
    }

    protected StrutsJavaConfigurationProvider createJavaConfigurationProvider(StrutsJavaConfiguration config) {
        return new StrutsJavaConfigurationProvider(config);
    }

    private void init_CustomConfigurationProviders() {
        String configProvs = this.initParams.get("configProviders");
        if (configProvs != null) {
            String[] classes;
            for (String cname : classes = configProvs.split(CONFIG_SPLIT_REGEX)) {
                try {
                    Class cls = ClassLoaderUtil.loadClass(cname, this.getClass());
                    ConfigurationProvider prov = (ConfigurationProvider)cls.newInstance();
                    if (prov instanceof ServletContextAwareConfigurationProvider) {
                        ((ServletContextAwareConfigurationProvider)prov).initWithContext(this.servletContext);
                    }
                    this.configurationManager.addContainerProvider(prov);
                }
                catch (InstantiationException e) {
                    throw new ConfigurationException("Unable to instantiate provider: " + cname, e);
                }
                catch (IllegalAccessException e) {
                    throw new ConfigurationException("Unable to access provider: " + cname, e);
                }
                catch (ClassNotFoundException e) {
                    throw new ConfigurationException("Unable to locate provider class: " + cname, e);
                }
            }
        }
    }

    private void init_FilterInitParameters() {
        this.configurationManager.addContainerProvider(new ConfigurationProvider(){

            @Override
            public void destroy() {
            }

            @Override
            public void init(Configuration configuration) throws ConfigurationException {
            }

            @Override
            public void loadPackages() throws ConfigurationException {
            }

            @Override
            public boolean needsReload() {
                return false;
            }

            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                props.putAll(Dispatcher.this.initParams);
            }
        });
    }

    private void init_AliasStandardObjects() {
        this.configurationManager.addContainerProvider(new StrutsBeanSelectionProvider());
    }

    private void init_DeferredXmlConfigurations() {
        this.loadConfigPaths("struts-deferred.xml");
    }

    public void init() {
        if (this.configurationManager == null) {
            this.configurationManager = this.createConfigurationManager("default");
        }
        try {
            this.init_FileManager();
            this.init_DefaultProperties();
            this.init_TraditionalXmlConfigurations();
            this.init_JavaConfigurations();
            this.init_LegacyStrutsProperties();
            this.init_CustomConfigurationProviders();
            this.init_FilterInitParameters();
            this.init_AliasStandardObjects();
            this.init_DeferredXmlConfigurations();
            this.getContainer();
            if (!dispatcherListeners.isEmpty()) {
                for (DispatcherListener l : dispatcherListeners) {
                    l.dispatcherInitialized(this);
                }
            }
            this.errorHandler.init(this.servletContext);
            if (this.servletContext.getAttribute("com.opensymphony.xwork2.dispatcher.ServletDispatcher") == null) {
                this.servletContext.setAttribute("com.opensymphony.xwork2.dispatcher.ServletDispatcher", (Object)this);
            }
        }
        catch (Exception ex) {
            LOG.error("Dispatcher initialization failed", (Throwable)ex);
            throw new StrutsException(ex);
        }
    }

    protected ConfigurationManager createConfigurationManager(String name) {
        return new ConfigurationManager(name);
    }

    public void serviceAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ServletException {
        ActionContext ctx;
        boolean nullStack;
        Map<String, Object> extraContext = this.createContextMap(request, response, mapping);
        ValueStack stack = (ValueStack)request.getAttribute("struts.valueStack");
        boolean bl = nullStack = stack == null;
        if (nullStack && (ctx = ActionContext.getContext()) != null) {
            stack = ctx.getValueStack();
        }
        if (stack != null) {
            extraContext = ActionContext.of(extraContext).withValueStack(this.valueStackFactory.createValueStack(stack)).getContextMap();
        }
        try {
            String actionNamespace = mapping.getNamespace();
            String actionName = mapping.getName();
            String actionMethod = mapping.getMethod();
            LOG.trace("Processing action, namespace: {}, name: {}, method: {}", (Object)actionNamespace, (Object)actionName, (Object)actionMethod);
            ActionProxy proxy = this.prepareActionProxy(extraContext, actionNamespace, actionName, actionMethod);
            request.setAttribute("struts.valueStack", (Object)proxy.getInvocation().getStack());
            if (mapping.getResult() != null) {
                Result result = mapping.getResult();
                result.execute(proxy.getInvocation());
            } else {
                proxy.execute();
            }
            if (!nullStack) {
                request.setAttribute("struts.valueStack", (Object)stack);
            }
        }
        catch (ConfigurationException e) {
            this.logConfigurationException(request, e);
            this.sendError(request, response, 404, e);
        }
        catch (Exception e) {
            if (this.handleException || this.devMode) {
                if (this.devMode) {
                    LOG.debug("Dispatcher serviceAction failed", (Throwable)e);
                }
                this.sendError(request, response, 500, e);
            }
            throw new ServletException((Throwable)e);
        }
    }

    protected ActionProxy prepareActionProxy(Map<String, Object> extraContext, String actionNamespace, String actionName, String actionMethod) {
        ActionProxy proxy;
        ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
        if (invocation == null || invocation.isExecuted()) {
            LOG.trace("Creating a new action, namespace: {}, name: {}, method: {}", (Object)actionNamespace, (Object)actionName, (Object)actionMethod);
            proxy = this.createActionProxy(actionNamespace, actionName, actionMethod, extraContext);
        } else {
            proxy = invocation.getProxy();
            if (this.isSameAction(proxy, actionNamespace, actionName, actionMethod)) {
                LOG.trace("Proxy: {} matches requested action, namespace: {}, name: {}, method: {} - reusing proxy", (Object)proxy, (Object)actionNamespace, (Object)actionName, (Object)actionMethod);
            } else {
                LOG.trace("Proxy: {} doesn't match action namespace: {}, name: {}, method: {} - creating new proxy", (Object)proxy, (Object)actionNamespace, (Object)actionName, (Object)actionMethod);
                proxy = this.createActionProxy(actionNamespace, actionName, actionMethod, extraContext);
            }
        }
        return proxy;
    }

    protected ActionProxy createActionProxy(String namespace, String name, String method, Map<String, Object> extraContext) {
        return this.actionProxyFactory.createActionProxy(namespace, name, method, extraContext, true, false);
    }

    protected boolean isSameAction(ActionProxy actionProxy, String namespace, String actionName, String method) {
        return Objects.equals(namespace, actionProxy.getNamespace()) && Objects.equals(actionName, actionProxy.getActionName()) && Objects.equals(method, actionProxy.getMethod());
    }

    protected void logConfigurationException(HttpServletRequest request, ConfigurationException e) {
        String uri = request.getRequestURI();
        if (request.getQueryString() != null) {
            uri = uri + "?" + request.getQueryString();
        }
        if (this.devMode) {
            LOG.error("Could not find action or result: {}", (Object)uri, (Object)e);
        } else if (LOG.isWarnEnabled()) {
            LOG.warn("Could not find action or result: {}", (Object)uri, (Object)e);
        }
    }

    public Map<String, Object> createContextMap(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) {
        RequestMap requestMap = new RequestMap(request);
        HttpParameters params = HttpParameters.create(request.getParameterMap()).build();
        SessionMap session = new SessionMap(request);
        ApplicationMap application = new ApplicationMap(this.servletContext);
        Map<String, Object> extraContext = this.createContextMap(requestMap, params, session, application, request, response);
        if (mapping != null) {
            extraContext.put("struts.actionMapping", mapping);
        }
        return extraContext;
    }

    public Map<String, Object> createContextMap(Map<String, Object> requestMap, HttpParameters parameters, Map<String, Object> sessionMap, Map<String, Object> applicationMap, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> extraContext = ActionContext.of().withParameters(parameters).withSession(sessionMap).withApplication(applicationMap).withLocale(this.getLocale(request)).withServletRequest(request).withServletResponse(response).withServletContext(this.servletContext).with("request", requestMap).with("session", sessionMap).with("application", applicationMap).with("parameters", parameters).getContextMap();
        AttributeMap attrMap = new AttributeMap(extraContext);
        extraContext.put("attr", attrMap);
        return extraContext;
    }

    protected Locale getLocale(HttpServletRequest request) {
        Locale locale;
        if (this.defaultLocale != null) {
            try {
                locale = LocaleUtils.toLocale((String)this.defaultLocale);
            }
            catch (IllegalArgumentException e) {
                try {
                    locale = request.getLocale();
                    LOG.warn((Message)new ParameterizedMessage("Cannot convert 'struts.locale' = [{}] to proper locale, defaulting to request locale [{}]", (Object)this.defaultLocale, (Object)locale), (Throwable)e);
                }
                catch (RuntimeException rex) {
                    LOG.warn((Message)new ParameterizedMessage("Cannot convert 'struts.locale' = [{}] to proper locale, and cannot get locale from HTTP Request, falling back to system default locale", (Object)this.defaultLocale), (Throwable)rex);
                    locale = Locale.getDefault();
                }
            }
        } else {
            try {
                locale = request.getLocale();
            }
            catch (RuntimeException rex) {
                LOG.warn("Cannot get locale from HTTP Request, falling back to system default locale", (Throwable)rex);
                locale = Locale.getDefault();
            }
        }
        return locale;
    }

    protected String getSaveDir() {
        String saveDir = this.multipartSaveDir.trim();
        if (saveDir.equals("")) {
            File tempdir = (File)this.servletContext.getAttribute("javax.servlet.context.tempdir");
            LOG.info("Unable to find 'struts.multipart.saveDir' property setting. Defaulting to javax.servlet.context.tempdir");
            if (tempdir != null) {
                saveDir = tempdir.toString();
                this.setMultipartSaveDir(saveDir);
            }
        } else {
            File multipartSaveDir = new File(saveDir);
            if (!multipartSaveDir.exists() && !multipartSaveDir.mkdirs()) {
                String logMessage;
                try {
                    logMessage = "Could not find create multipart save directory '" + multipartSaveDir.getCanonicalPath() + "'.";
                }
                catch (IOException e) {
                    logMessage = "Could not find create multipart save directory '" + multipartSaveDir.toString() + "'.";
                }
                if (this.devMode) {
                    LOG.error(logMessage);
                } else {
                    LOG.warn(logMessage);
                }
            }
        }
        LOG.debug("saveDir={}", (Object)saveDir);
        return saveDir;
    }

    public void prepare(HttpServletRequest request, HttpServletResponse response) {
        this.getContainer();
        String encoding = null;
        if (this.defaultEncoding != null) {
            encoding = this.defaultEncoding;
        }
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            encoding = "UTF-8";
        }
        Locale locale = this.getLocale(request);
        if (encoding != null) {
            this.applyEncoding(request, encoding);
            this.applyEncoding(response, encoding);
        }
        if (locale != null) {
            response.setLocale(locale);
        }
        if (this.paramsWorkaroundEnabled) {
            request.getParameter("foo");
        }
    }

    private void applyEncoding(HttpServletRequest request, String encoding) {
        try {
            if (!encoding.equals(request.getCharacterEncoding())) {
                request.setCharacterEncoding(encoding);
            }
        }
        catch (Exception e) {
            LOG.error((Message)new ParameterizedMessage("Error setting character encoding to '{}' on request - ignoring.", (Object)encoding), (Throwable)e);
        }
    }

    private void applyEncoding(HttpServletResponse response, String encoding) {
        try {
            if (!encoding.equals(response.getCharacterEncoding())) {
                response.setCharacterEncoding(encoding);
            }
        }
        catch (Exception e) {
            LOG.error((Message)new ParameterizedMessage("Error setting character encoding to '{}' on response - ignoring.", (Object)encoding), (Throwable)e);
        }
    }

    public HttpServletRequest wrapRequest(HttpServletRequest request) throws IOException {
        if (request instanceof StrutsRequestWrapper) {
            return request;
        }
        request = this.isMultipartSupportEnabled((HttpServletRequest)request) && this.isMultipartRequest((HttpServletRequest)request) ? new MultiPartRequestWrapper(this.getMultiPartRequest(), (HttpServletRequest)request, this.getSaveDir(), this.localeProviderFactory.createLocaleProvider(), this.disableRequestAttributeValueStackLookup) : new StrutsRequestWrapper((HttpServletRequest)request, this.disableRequestAttributeValueStackLookup);
        return request;
    }

    protected boolean isMultipartSupportEnabled(HttpServletRequest request) {
        return this.multipartSupportEnabled;
    }

    protected boolean isMultipartRequest(HttpServletRequest request) {
        String httpMethod = request.getMethod();
        String contentType = request.getContentType();
        return REQUEST_POST_METHOD.equalsIgnoreCase(httpMethod) && contentType != null && this.multipartValidationPattern.matcher(contentType.toLowerCase(Locale.ENGLISH)).matches();
    }

    protected MultiPartRequest getMultiPartRequest() {
        return this.getContainer().getInstance(MultiPartRequest.class);
    }

    public void cleanUpRequest(HttpServletRequest request) {
        ContainerHolder.clear();
        if (!(request instanceof MultiPartRequestWrapper)) {
            return;
        }
        MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper)request;
        multiWrapper.cleanUp();
    }

    public void sendError(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
        this.errorHandler.handleError(request, response, code, e);
    }

    public void cleanUpAfterInit() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Cleaning up resources used to init Dispatcher");
        }
        ContainerHolder.clear();
    }

    public ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    public Container getContainer() {
        if (ContainerHolder.get() == null) {
            try {
                ContainerHolder.store(this.getConfigurationManager().getConfiguration().getContainer());
            }
            catch (NullPointerException e) {
                throw new IllegalStateException("ConfigurationManager and/or Configuration should not be null", e);
            }
        }
        if (this.injectedContainer != ContainerHolder.get()) {
            this.injectedContainer = ContainerHolder.get();
            this.injectedContainer.inject(this);
        }
        return ContainerHolder.get();
    }

    public static class Locator {
        public Location getLocation(Object obj) {
            Location loc = LocationUtils.getLocation(obj);
            if (loc == null) {
                return Location.UNKNOWN;
            }
            return loc;
        }
    }
}

