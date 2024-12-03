/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterConfig
 *  javax.servlet.FilterRegistration
 *  javax.servlet.FilterRegistration$Dynamic
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.Servlet
 *  javax.servlet.ServletContainerInitializer
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextAttributeListener
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRegistration
 *  javax.servlet.ServletRegistration$Dynamic
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletRequestAttributeListener
 *  javax.servlet.ServletRequestEvent
 *  javax.servlet.ServletRequestListener
 *  javax.servlet.ServletSecurityElement
 *  javax.servlet.SessionCookieConfig
 *  javax.servlet.SessionTrackingMode
 *  javax.servlet.descriptor.JspConfigDescriptor
 *  javax.servlet.http.HttpSessionAttributeListener
 *  javax.servlet.http.HttpSessionIdListener
 *  javax.servlet.http.HttpSessionListener
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.InstanceManagerBindings
 *  org.apache.tomcat.JarScanner
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.compat.JreCompat
 *  org.apache.tomcat.util.descriptor.web.ApplicationParameter
 *  org.apache.tomcat.util.descriptor.web.ContextLocalEjb
 *  org.apache.tomcat.util.descriptor.web.ErrorPage
 *  org.apache.tomcat.util.descriptor.web.FilterDef
 *  org.apache.tomcat.util.descriptor.web.FilterMap
 *  org.apache.tomcat.util.descriptor.web.Injectable
 *  org.apache.tomcat.util.descriptor.web.InjectionTarget
 *  org.apache.tomcat.util.descriptor.web.LoginConfig
 *  org.apache.tomcat.util.descriptor.web.MessageDestination
 *  org.apache.tomcat.util.descriptor.web.MessageDestinationRef
 *  org.apache.tomcat.util.descriptor.web.SecurityCollection
 *  org.apache.tomcat.util.descriptor.web.SecurityConstraint
 *  org.apache.tomcat.util.http.CookieProcessor
 *  org.apache.tomcat.util.http.Rfc6265CookieProcessor
 *  org.apache.tomcat.util.scan.StandardJarScanner
 *  org.apache.tomcat.util.security.PrivilegedGetTccl
 *  org.apache.tomcat.util.security.PrivilegedSetTccl
 *  org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor
 */
package org.apache.catalina.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletSecurityElement;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.Globals;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.ThreadBindingListener;
import org.apache.catalina.Valve;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.DefaultInstanceManager;
import org.apache.catalina.core.NamingContextListener;
import org.apache.catalina.core.StandardContextValve;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.util.CharsetMapper;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.ErrorPageSupport;
import org.apache.catalina.util.ExtensionValidator;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.ContextBindings;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.InstanceManagerBindings;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.apache.tomcat.util.descriptor.web.ContextLocalEjb;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.Injectable;
import org.apache.tomcat.util.descriptor.web.InjectionTarget;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.MessageDestination;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.apache.tomcat.util.threads.ScheduledThreadPoolExecutor;

public class StandardContext
extends ContainerBase
implements Context,
NotificationEmitter {
    private static final Log log = LogFactory.getLog(StandardContext.class);
    protected boolean allowCasualMultipartParsing = false;
    private boolean swallowAbortedUploads = true;
    private String altDDName = null;
    private InstanceManager instanceManager = null;
    private boolean antiResourceLocking = false;
    private CopyOnWriteArrayList<String> applicationListeners = new CopyOnWriteArrayList();
    private final Set<Object> noPluggabilityListeners = new HashSet<Object>();
    private List<Object> applicationEventListenersList = new CopyOnWriteArrayList<Object>();
    private Object[] applicationLifecycleListenersObjects = new Object[0];
    private Map<ServletContainerInitializer, Set<Class<?>>> initializers = new LinkedHashMap();
    private ApplicationParameter[] applicationParameters = new ApplicationParameter[0];
    private final Object applicationParametersLock = new Object();
    private NotificationBroadcasterSupport broadcaster = null;
    private CharsetMapper charsetMapper = null;
    private String charsetMapperClass = "org.apache.catalina.util.CharsetMapper";
    private URL configFile = null;
    private boolean configured = false;
    private volatile SecurityConstraint[] constraints = new SecurityConstraint[0];
    private final Object constraintsLock = new Object();
    protected ApplicationContext context = null;
    private NoPluggabilityServletContext noPluggabilityServletContext = null;
    private boolean cookies = true;
    private boolean crossContext = false;
    private String encodedPath = null;
    private String path = null;
    private boolean delegate = JreCompat.isGraalAvailable();
    private boolean denyUncoveredHttpMethods;
    private String displayName = null;
    private String defaultContextXml;
    private String defaultWebXml;
    private boolean distributable = false;
    private String docBase = null;
    private final ErrorPageSupport errorPageSupport = new ErrorPageSupport();
    private Map<String, ApplicationFilterConfig> filterConfigs = new HashMap<String, ApplicationFilterConfig>();
    private Map<String, FilterDef> filterDefs = new HashMap<String, FilterDef>();
    private final ContextFilterMaps filterMaps = new ContextFilterMaps();
    private boolean ignoreAnnotations = false;
    private Loader loader = null;
    private final ReadWriteLock loaderLock = new ReentrantReadWriteLock();
    private LoginConfig loginConfig = null;
    protected Manager manager = null;
    private final ReadWriteLock managerLock = new ReentrantReadWriteLock();
    private NamingContextListener namingContextListener = null;
    private NamingResourcesImpl namingResources = null;
    private HashMap<String, MessageDestination> messageDestinations = new HashMap();
    private Map<String, String> mimeMappings = new HashMap<String, String>();
    private final Map<String, String> parameters = new ConcurrentHashMap<String, String>();
    private volatile boolean paused = false;
    private String publicId = null;
    private boolean reloadable = false;
    private boolean unpackWAR = true;
    private boolean copyXML = false;
    private boolean override = false;
    private String originalDocBase = null;
    private boolean privileged = false;
    private boolean replaceWelcomeFiles = false;
    private Map<String, String> roleMappings = new HashMap<String, String>();
    private String[] securityRoles = new String[0];
    private final Object securityRolesLock = new Object();
    private Map<String, String> servletMappings = new HashMap<String, String>();
    private final Object servletMappingsLock = new Object();
    private int sessionTimeout = 30;
    private AtomicLong sequenceNumber = new AtomicLong(0L);
    private boolean swallowOutput = false;
    private long unloadDelay = 2000L;
    private String[] watchedResources = new String[0];
    private final Object watchedResourcesLock = new Object();
    private String[] welcomeFiles = new String[0];
    private final Object welcomeFilesLock = new Object();
    private String[] wrapperLifecycles = new String[0];
    private final Object wrapperLifecyclesLock = new Object();
    private String[] wrapperListeners = new String[0];
    private final Object wrapperListenersLock = new Object();
    private String workDir = null;
    private String wrapperClassName = StandardWrapper.class.getName();
    private Class<?> wrapperClass = null;
    private boolean useNaming = true;
    private String namingContextName = null;
    private WebResourceRoot resources;
    private final ReadWriteLock resourcesLock = new ReentrantReadWriteLock();
    private long startupTime;
    private long startTime;
    private long tldScanTime;
    private String j2EEApplication = "none";
    private String j2EEServer = "none";
    private boolean webXmlValidation = Globals.STRICT_SERVLET_COMPLIANCE;
    private boolean webXmlNamespaceAware = Globals.STRICT_SERVLET_COMPLIANCE;
    private boolean xmlBlockExternal = true;
    private boolean tldValidation = Globals.STRICT_SERVLET_COMPLIANCE;
    private String sessionCookieName;
    private boolean useHttpOnly = true;
    private String sessionCookieDomain;
    private String sessionCookiePath;
    private boolean sessionCookiePathUsesTrailingSlash = false;
    private JarScanner jarScanner = null;
    private boolean clearReferencesRmiTargets = true;
    private boolean clearReferencesStopThreads = false;
    private boolean clearReferencesStopTimerThreads = false;
    private boolean clearReferencesHttpClientKeepAliveThread = true;
    private boolean renewThreadsWhenStoppingContext = true;
    private boolean clearReferencesObjectStreamClassCaches = true;
    private boolean clearReferencesThreadLocals = true;
    private boolean skipMemoryLeakChecksOnJvmShutdown = false;
    private boolean logEffectiveWebXml = false;
    private int effectiveMajorVersion = 3;
    private int effectiveMinorVersion = 0;
    private JspConfigDescriptor jspConfigDescriptor = null;
    private Set<String> resourceOnlyServlets = new HashSet<String>();
    private String webappVersion = "";
    private boolean addWebinfClassesResources = false;
    private boolean fireRequestListenersOnForwards = false;
    private Set<Servlet> createdServlets = new HashSet<Servlet>();
    private boolean preemptiveAuthentication = false;
    private boolean sendRedirectBody = false;
    private boolean jndiExceptionOnFailedWrite = true;
    private Map<String, String> postConstructMethods = new HashMap<String, String>();
    private Map<String, String> preDestroyMethods = new HashMap<String, String>();
    private String containerSciFilter;
    private Boolean failCtxIfServletStartFails;
    protected static final ThreadBindingListener DEFAULT_NAMING_LISTENER = new ThreadBindingListener(){

        @Override
        public void bind() {
        }

        @Override
        public void unbind() {
        }
    };
    protected ThreadBindingListener threadBindingListener = DEFAULT_NAMING_LISTENER;
    private final Object namingToken = new Object();
    private CookieProcessor cookieProcessor;
    private boolean validateClientProvidedNewSessionId = true;
    private boolean mapperContextRootRedirectEnabled = true;
    private boolean mapperDirectoryRedirectEnabled = false;
    private boolean useRelativeRedirects = !Globals.STRICT_SERVLET_COMPLIANCE;
    private boolean dispatchersUseEncodedPaths = true;
    private String requestEncoding = null;
    private String responseEncoding = null;
    private boolean allowMultipleLeadingForwardSlashInPath = false;
    private final AtomicLong inProgressAsyncCount = new AtomicLong(0L);
    private boolean createUploadTargets = false;
    private boolean parallelAnnotationScanning = false;
    private boolean useBloomFilterForArchives = false;
    private MBeanNotificationInfo[] notificationInfo;
    private String server = null;
    private String[] javaVMs = null;

    public StandardContext() {
        this.pipeline.setBasic(new StandardContextValve());
        this.broadcaster = new NotificationBroadcasterSupport();
        if (!Globals.STRICT_SERVLET_COMPLIANCE) {
            this.resourceOnlyServlets.add("jsp");
        }
    }

    @Override
    public void setCreateUploadTargets(boolean createUploadTargets) {
        this.createUploadTargets = createUploadTargets;
    }

    @Override
    public boolean getCreateUploadTargets() {
        return this.createUploadTargets;
    }

    @Override
    public void incrementInProgressAsyncCount() {
        this.inProgressAsyncCount.incrementAndGet();
    }

    @Override
    public void decrementInProgressAsyncCount() {
        this.inProgressAsyncCount.decrementAndGet();
    }

    public long getInProgressAsyncCount() {
        return this.inProgressAsyncCount.get();
    }

    @Override
    public void setAllowMultipleLeadingForwardSlashInPath(boolean allowMultipleLeadingForwardSlashInPath) {
        this.allowMultipleLeadingForwardSlashInPath = allowMultipleLeadingForwardSlashInPath;
    }

    @Override
    public boolean getAllowMultipleLeadingForwardSlashInPath() {
        return this.allowMultipleLeadingForwardSlashInPath;
    }

    @Override
    public String getRequestCharacterEncoding() {
        return this.requestEncoding;
    }

    @Override
    public void setRequestCharacterEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }

    @Override
    public String getResponseCharacterEncoding() {
        return this.responseEncoding;
    }

    @Override
    public void setResponseCharacterEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding == null ? null : new String(responseEncoding);
    }

    @Override
    public void setDispatchersUseEncodedPaths(boolean dispatchersUseEncodedPaths) {
        this.dispatchersUseEncodedPaths = dispatchersUseEncodedPaths;
    }

    @Override
    public boolean getDispatchersUseEncodedPaths() {
        return this.dispatchersUseEncodedPaths;
    }

    @Override
    public void setUseRelativeRedirects(boolean useRelativeRedirects) {
        this.useRelativeRedirects = useRelativeRedirects;
    }

    @Override
    public boolean getUseRelativeRedirects() {
        return this.useRelativeRedirects;
    }

    @Override
    public void setMapperContextRootRedirectEnabled(boolean mapperContextRootRedirectEnabled) {
        this.mapperContextRootRedirectEnabled = mapperContextRootRedirectEnabled;
    }

    @Override
    public boolean getMapperContextRootRedirectEnabled() {
        return this.mapperContextRootRedirectEnabled;
    }

    @Override
    public void setMapperDirectoryRedirectEnabled(boolean mapperDirectoryRedirectEnabled) {
        this.mapperDirectoryRedirectEnabled = mapperDirectoryRedirectEnabled;
    }

    @Override
    public boolean getMapperDirectoryRedirectEnabled() {
        return this.mapperDirectoryRedirectEnabled;
    }

    @Override
    public void setValidateClientProvidedNewSessionId(boolean validateClientProvidedNewSessionId) {
        this.validateClientProvidedNewSessionId = validateClientProvidedNewSessionId;
    }

    @Override
    public boolean getValidateClientProvidedNewSessionId() {
        return this.validateClientProvidedNewSessionId;
    }

    @Override
    public void setCookieProcessor(CookieProcessor cookieProcessor) {
        if (cookieProcessor == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.cookieProcessor.null"));
        }
        this.cookieProcessor = cookieProcessor;
    }

    @Override
    public CookieProcessor getCookieProcessor() {
        return this.cookieProcessor;
    }

    @Override
    public Object getNamingToken() {
        return this.namingToken;
    }

    @Override
    public void setContainerSciFilter(String containerSciFilter) {
        this.containerSciFilter = containerSciFilter;
    }

    @Override
    public String getContainerSciFilter() {
        return this.containerSciFilter;
    }

    @Override
    public boolean getSendRedirectBody() {
        return this.sendRedirectBody;
    }

    @Override
    public void setSendRedirectBody(boolean sendRedirectBody) {
        this.sendRedirectBody = sendRedirectBody;
    }

    @Override
    public boolean getPreemptiveAuthentication() {
        return this.preemptiveAuthentication;
    }

    @Override
    public void setPreemptiveAuthentication(boolean preemptiveAuthentication) {
        this.preemptiveAuthentication = preemptiveAuthentication;
    }

    @Override
    public void setFireRequestListenersOnForwards(boolean enable) {
        this.fireRequestListenersOnForwards = enable;
    }

    @Override
    public boolean getFireRequestListenersOnForwards() {
        return this.fireRequestListenersOnForwards;
    }

    @Override
    public void setAddWebinfClassesResources(boolean addWebinfClassesResources) {
        this.addWebinfClassesResources = addWebinfClassesResources;
    }

    @Override
    public boolean getAddWebinfClassesResources() {
        return this.addWebinfClassesResources;
    }

    @Override
    public void setWebappVersion(String webappVersion) {
        this.webappVersion = null == webappVersion ? "" : webappVersion;
    }

    @Override
    public String getWebappVersion() {
        return this.webappVersion;
    }

    @Override
    public String getBaseName() {
        return new ContextName(this.path, this.webappVersion).getBaseName();
    }

    @Override
    public String getResourceOnlyServlets() {
        return StringUtils.join(this.resourceOnlyServlets);
    }

    @Override
    public void setResourceOnlyServlets(String resourceOnlyServlets) {
        this.resourceOnlyServlets.clear();
        if (resourceOnlyServlets == null) {
            return;
        }
        for (String servletName : resourceOnlyServlets.split(",")) {
            if ((servletName = servletName.trim()).length() <= 0) continue;
            this.resourceOnlyServlets.add(servletName);
        }
    }

    @Override
    public boolean isResourceOnlyServlet(String servletName) {
        return this.resourceOnlyServlets.contains(servletName);
    }

    @Override
    public int getEffectiveMajorVersion() {
        return this.effectiveMajorVersion;
    }

    @Override
    public void setEffectiveMajorVersion(int effectiveMajorVersion) {
        this.effectiveMajorVersion = effectiveMajorVersion;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return this.effectiveMinorVersion;
    }

    @Override
    public void setEffectiveMinorVersion(int effectiveMinorVersion) {
        this.effectiveMinorVersion = effectiveMinorVersion;
    }

    @Override
    public void setLogEffectiveWebXml(boolean logEffectiveWebXml) {
        this.logEffectiveWebXml = logEffectiveWebXml;
    }

    @Override
    public boolean getLogEffectiveWebXml() {
        return this.logEffectiveWebXml;
    }

    @Override
    public Authenticator getAuthenticator() {
        Pipeline pipeline = this.getPipeline();
        if (pipeline != null) {
            Valve basic = pipeline.getBasic();
            if (basic instanceof Authenticator) {
                return (Authenticator)((Object)basic);
            }
            for (Valve valve : pipeline.getValves()) {
                if (!(valve instanceof Authenticator)) continue;
                return (Authenticator)((Object)valve);
            }
        }
        return null;
    }

    @Override
    public JarScanner getJarScanner() {
        if (this.jarScanner == null) {
            this.jarScanner = new StandardJarScanner();
        }
        return this.jarScanner;
    }

    @Override
    public void setJarScanner(JarScanner jarScanner) {
        this.jarScanner = jarScanner;
    }

    @Override
    public InstanceManager getInstanceManager() {
        return this.instanceManager;
    }

    @Override
    public void setInstanceManager(InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    public String getEncodedPath() {
        return this.encodedPath;
    }

    @Override
    public void setAllowCasualMultipartParsing(boolean allowCasualMultipartParsing) {
        this.allowCasualMultipartParsing = allowCasualMultipartParsing;
    }

    @Override
    public boolean getAllowCasualMultipartParsing() {
        return this.allowCasualMultipartParsing;
    }

    @Override
    public void setSwallowAbortedUploads(boolean swallowAbortedUploads) {
        this.swallowAbortedUploads = swallowAbortedUploads;
    }

    @Override
    public boolean getSwallowAbortedUploads() {
        return this.swallowAbortedUploads;
    }

    @Override
    public void addServletContainerInitializer(ServletContainerInitializer sci, Set<Class<?>> classes) {
        this.initializers.put(sci, classes);
    }

    public boolean getDelegate() {
        return this.delegate;
    }

    public void setDelegate(boolean delegate) {
        boolean oldDelegate = this.delegate;
        this.delegate = delegate;
        this.support.firePropertyChange("delegate", oldDelegate, this.delegate);
    }

    public boolean isUseNaming() {
        return this.useNaming;
    }

    public void setUseNaming(boolean useNaming) {
        this.useNaming = useNaming;
    }

    @Override
    public Object[] getApplicationEventListeners() {
        return this.applicationEventListenersList.toArray();
    }

    @Override
    public void setApplicationEventListeners(Object[] listeners) {
        this.applicationEventListenersList.clear();
        if (listeners != null && listeners.length > 0) {
            this.applicationEventListenersList.addAll(Arrays.asList(listeners));
        }
    }

    public void addApplicationEventListener(Object listener) {
        this.applicationEventListenersList.add(listener);
    }

    @Override
    public Object[] getApplicationLifecycleListeners() {
        return this.applicationLifecycleListenersObjects;
    }

    @Override
    public void setApplicationLifecycleListeners(Object[] listeners) {
        this.applicationLifecycleListenersObjects = listeners;
    }

    public void addApplicationLifecycleListener(Object listener) {
        int len = this.applicationLifecycleListenersObjects.length;
        Object[] newListeners = Arrays.copyOf(this.applicationLifecycleListenersObjects, len + 1);
        newListeners[len] = listener;
        this.applicationLifecycleListenersObjects = newListeners;
    }

    public boolean getAntiResourceLocking() {
        return this.antiResourceLocking;
    }

    public void setAntiResourceLocking(boolean antiResourceLocking) {
        boolean oldAntiResourceLocking = this.antiResourceLocking;
        this.antiResourceLocking = antiResourceLocking;
        this.support.firePropertyChange("antiResourceLocking", oldAntiResourceLocking, this.antiResourceLocking);
    }

    @Override
    @Deprecated
    public boolean getUseBloomFilterForArchives() {
        return this.useBloomFilterForArchives;
    }

    @Override
    @Deprecated
    public void setUseBloomFilterForArchives(boolean useBloomFilterForArchives) {
        boolean oldUseBloomFilterForArchives = this.useBloomFilterForArchives;
        this.useBloomFilterForArchives = useBloomFilterForArchives;
        this.support.firePropertyChange("useBloomFilterForArchives", oldUseBloomFilterForArchives, this.useBloomFilterForArchives);
    }

    @Override
    public void setParallelAnnotationScanning(boolean parallelAnnotationScanning) {
        boolean oldParallelAnnotationScanning = this.parallelAnnotationScanning;
        this.parallelAnnotationScanning = parallelAnnotationScanning;
        this.support.firePropertyChange("parallelAnnotationScanning", oldParallelAnnotationScanning, this.parallelAnnotationScanning);
    }

    @Override
    public boolean getParallelAnnotationScanning() {
        return this.parallelAnnotationScanning;
    }

    public CharsetMapper getCharsetMapper() {
        if (this.charsetMapper == null) {
            try {
                Class<?> clazz = Class.forName(this.charsetMapperClass);
                this.charsetMapper = (CharsetMapper)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.charsetMapper = new CharsetMapper();
            }
        }
        return this.charsetMapper;
    }

    public void setCharsetMapper(CharsetMapper mapper) {
        CharsetMapper oldCharsetMapper = this.charsetMapper;
        this.charsetMapper = mapper;
        if (mapper != null) {
            this.charsetMapperClass = mapper.getClass().getName();
        }
        this.support.firePropertyChange("charsetMapper", oldCharsetMapper, this.charsetMapper);
    }

    @Override
    public String getCharset(Locale locale) {
        return this.getCharsetMapper().getCharset(locale);
    }

    @Override
    public URL getConfigFile() {
        return this.configFile;
    }

    @Override
    public void setConfigFile(URL configFile) {
        this.configFile = configFile;
    }

    @Override
    public boolean getConfigured() {
        return this.configured;
    }

    @Override
    public void setConfigured(boolean configured) {
        boolean oldConfigured = this.configured;
        this.configured = configured;
        this.support.firePropertyChange("configured", oldConfigured, this.configured);
    }

    @Override
    public boolean getCookies() {
        return this.cookies;
    }

    @Override
    public void setCookies(boolean cookies) {
        boolean oldCookies = this.cookies;
        this.cookies = cookies;
        this.support.firePropertyChange("cookies", oldCookies, this.cookies);
    }

    @Override
    public String getSessionCookieName() {
        return this.sessionCookieName;
    }

    @Override
    public void setSessionCookieName(String sessionCookieName) {
        String oldSessionCookieName = this.sessionCookieName;
        this.sessionCookieName = sessionCookieName;
        this.support.firePropertyChange("sessionCookieName", oldSessionCookieName, sessionCookieName);
    }

    @Override
    public boolean getUseHttpOnly() {
        return this.useHttpOnly;
    }

    @Override
    public void setUseHttpOnly(boolean useHttpOnly) {
        boolean oldUseHttpOnly = this.useHttpOnly;
        this.useHttpOnly = useHttpOnly;
        this.support.firePropertyChange("useHttpOnly", oldUseHttpOnly, this.useHttpOnly);
    }

    @Override
    public String getSessionCookieDomain() {
        return this.sessionCookieDomain;
    }

    @Override
    public void setSessionCookieDomain(String sessionCookieDomain) {
        String oldSessionCookieDomain = this.sessionCookieDomain;
        this.sessionCookieDomain = sessionCookieDomain;
        this.support.firePropertyChange("sessionCookieDomain", oldSessionCookieDomain, sessionCookieDomain);
    }

    @Override
    public String getSessionCookiePath() {
        return this.sessionCookiePath;
    }

    @Override
    public void setSessionCookiePath(String sessionCookiePath) {
        String oldSessionCookiePath = this.sessionCookiePath;
        this.sessionCookiePath = sessionCookiePath;
        this.support.firePropertyChange("sessionCookiePath", oldSessionCookiePath, sessionCookiePath);
    }

    @Override
    public boolean getSessionCookiePathUsesTrailingSlash() {
        return this.sessionCookiePathUsesTrailingSlash;
    }

    @Override
    public void setSessionCookiePathUsesTrailingSlash(boolean sessionCookiePathUsesTrailingSlash) {
        this.sessionCookiePathUsesTrailingSlash = sessionCookiePathUsesTrailingSlash;
    }

    @Override
    public boolean getCrossContext() {
        return this.crossContext;
    }

    @Override
    public void setCrossContext(boolean crossContext) {
        boolean oldCrossContext = this.crossContext;
        this.crossContext = crossContext;
        this.support.firePropertyChange("crossContext", oldCrossContext, this.crossContext);
    }

    public String getDefaultContextXml() {
        return this.defaultContextXml;
    }

    public void setDefaultContextXml(String defaultContextXml) {
        this.defaultContextXml = defaultContextXml;
    }

    public String getDefaultWebXml() {
        return this.defaultWebXml;
    }

    public void setDefaultWebXml(String defaultWebXml) {
        this.defaultWebXml = defaultWebXml;
    }

    public long getStartupTime() {
        return this.startupTime;
    }

    public void setStartupTime(long startupTime) {
        this.startupTime = startupTime;
    }

    public long getTldScanTime() {
        return this.tldScanTime;
    }

    public void setTldScanTime(long tldScanTime) {
        this.tldScanTime = tldScanTime;
    }

    @Override
    public boolean getDenyUncoveredHttpMethods() {
        return this.denyUncoveredHttpMethods;
    }

    @Override
    public void setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {
        this.denyUncoveredHttpMethods = denyUncoveredHttpMethods;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getAltDDName() {
        return this.altDDName;
    }

    @Override
    public void setAltDDName(String altDDName) {
        this.altDDName = altDDName;
        if (this.context != null) {
            this.context.setAttribute("org.apache.catalina.deploy.alt_dd", altDDName);
        }
    }

    @Override
    public void setDisplayName(String displayName) {
        String oldDisplayName = this.displayName;
        this.displayName = displayName;
        this.support.firePropertyChange("displayName", oldDisplayName, this.displayName);
    }

    @Override
    public boolean getDistributable() {
        return this.distributable;
    }

    @Override
    public void setDistributable(boolean distributable) {
        boolean oldDistributable = this.distributable;
        this.distributable = distributable;
        this.support.firePropertyChange("distributable", oldDistributable, this.distributable);
    }

    @Override
    public String getDocBase() {
        return this.docBase;
    }

    @Override
    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    public String getJ2EEApplication() {
        return this.j2EEApplication;
    }

    public void setJ2EEApplication(String j2EEApplication) {
        this.j2EEApplication = j2EEApplication;
    }

    public String getJ2EEServer() {
        return this.j2EEServer;
    }

    public void setJ2EEServer(String j2EEServer) {
        this.j2EEServer = j2EEServer;
    }

    @Override
    public Loader getLoader() {
        Lock readLock = this.loaderLock.readLock();
        readLock.lock();
        try {
            Loader loader = this.loader;
            return loader;
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLoader(Loader loader) {
        Lock writeLock = this.loaderLock.writeLock();
        writeLock.lock();
        Loader oldLoader = null;
        try {
            oldLoader = this.loader;
            if (oldLoader == loader) {
                return;
            }
            this.loader = loader;
            if (this.getState().isAvailable() && oldLoader != null && oldLoader instanceof Lifecycle) {
                try {
                    ((Lifecycle)((Object)oldLoader)).stop();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("standardContext.setLoader.stop"), (Throwable)e);
                }
            }
            if (loader != null) {
                loader.setContext(this);
            }
            if (this.getState().isAvailable() && loader != null && loader instanceof Lifecycle) {
                try {
                    ((Lifecycle)((Object)loader)).start();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("standardContext.setLoader.start"), (Throwable)e);
                }
            }
        }
        finally {
            writeLock.unlock();
        }
        this.support.firePropertyChange("loader", oldLoader, loader);
    }

    @Override
    public Manager getManager() {
        Lock readLock = this.managerLock.readLock();
        readLock.lock();
        try {
            Manager manager = this.manager;
            return manager;
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setManager(Manager manager) {
        Lock writeLock = this.managerLock.writeLock();
        writeLock.lock();
        Manager oldManager = null;
        try {
            oldManager = this.manager;
            if (oldManager == manager) {
                return;
            }
            this.manager = manager;
            if (oldManager instanceof Lifecycle) {
                try {
                    ((Lifecycle)((Object)oldManager)).stop();
                    ((Lifecycle)((Object)oldManager)).destroy();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("standardContext.setManager.stop"), (Throwable)e);
                }
            }
            if (manager != null) {
                manager.setContext(this);
            }
            if (this.getState().isAvailable() && manager instanceof Lifecycle) {
                try {
                    ((Lifecycle)((Object)manager)).start();
                }
                catch (LifecycleException e) {
                    log.error((Object)sm.getString("standardContext.setManager.start"), (Throwable)e);
                }
            }
        }
        finally {
            writeLock.unlock();
        }
        this.support.firePropertyChange("manager", oldManager, manager);
    }

    @Override
    public boolean getIgnoreAnnotations() {
        return this.ignoreAnnotations;
    }

    @Override
    public void setIgnoreAnnotations(boolean ignoreAnnotations) {
        boolean oldIgnoreAnnotations = this.ignoreAnnotations;
        this.ignoreAnnotations = ignoreAnnotations;
        this.support.firePropertyChange("ignoreAnnotations", oldIgnoreAnnotations, this.ignoreAnnotations);
    }

    @Override
    public LoginConfig getLoginConfig() {
        return this.loginConfig;
    }

    @Override
    public void setLoginConfig(LoginConfig config) {
        String errorPage;
        if (config == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.loginConfig.required"));
        }
        String loginPage = config.getLoginPage();
        if (loginPage != null && !loginPage.startsWith("/")) {
            if (this.isServlet22()) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("standardContext.loginConfig.loginWarning", new Object[]{loginPage}));
                }
                config.setLoginPage("/" + loginPage);
            } else {
                throw new IllegalArgumentException(sm.getString("standardContext.loginConfig.loginPage", new Object[]{loginPage}));
            }
        }
        if ((errorPage = config.getErrorPage()) != null && !errorPage.startsWith("/")) {
            if (this.isServlet22()) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("standardContext.loginConfig.errorWarning", new Object[]{errorPage}));
                }
                config.setErrorPage("/" + errorPage);
            } else {
                throw new IllegalArgumentException(sm.getString("standardContext.loginConfig.errorPage", new Object[]{errorPage}));
            }
        }
        LoginConfig oldLoginConfig = this.loginConfig;
        this.loginConfig = config;
        this.support.firePropertyChange("loginConfig", oldLoginConfig, this.loginConfig);
    }

    @Override
    public NamingResourcesImpl getNamingResources() {
        if (this.namingResources == null) {
            this.setNamingResources(new NamingResourcesImpl());
        }
        return this.namingResources;
    }

    @Override
    public void setNamingResources(NamingResourcesImpl namingResources) {
        NamingResourcesImpl oldNamingResources = this.namingResources;
        this.namingResources = namingResources;
        if (namingResources != null) {
            namingResources.setContainer(this);
        }
        this.support.firePropertyChange("namingResources", oldNamingResources, this.namingResources);
        if (this.getState() == LifecycleState.NEW || this.getState() == LifecycleState.INITIALIZING || this.getState() == LifecycleState.INITIALIZED) {
            return;
        }
        if (oldNamingResources != null) {
            try {
                oldNamingResources.stop();
                oldNamingResources.destroy();
            }
            catch (LifecycleException e) {
                log.error((Object)sm.getString("standardContext.namingResource.destroy.fail"), (Throwable)e);
            }
        }
        if (namingResources != null) {
            try {
                namingResources.init();
                namingResources.start();
            }
            catch (LifecycleException e) {
                log.error((Object)sm.getString("standardContext.namingResource.init.fail"), (Throwable)e);
            }
        }
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setPath(String path) {
        boolean invalid = false;
        if (path == null || path.equals("/")) {
            invalid = true;
            this.path = "";
        } else if (path.isEmpty() || path.startsWith("/")) {
            this.path = path;
        } else {
            invalid = true;
            this.path = "/" + path;
        }
        if (this.path.endsWith("/")) {
            invalid = true;
            this.path = this.path.substring(0, this.path.length() - 1);
        }
        if (invalid) {
            log.warn((Object)sm.getString("standardContext.pathInvalid", new Object[]{path, this.path}));
        }
        this.encodedPath = URLEncoder.DEFAULT.encode(this.path, StandardCharsets.UTF_8);
        if (this.getName() == null) {
            this.setName(this.path);
        }
    }

    @Override
    public String getPublicId() {
        return this.publicId;
    }

    @Override
    public void setPublicId(String publicId) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Setting deployment descriptor public ID to '" + publicId + "'"));
        }
        String oldPublicId = this.publicId;
        this.publicId = publicId;
        this.support.firePropertyChange("publicId", oldPublicId, publicId);
    }

    @Override
    public boolean getReloadable() {
        return this.reloadable;
    }

    @Override
    public boolean getOverride() {
        return this.override;
    }

    public String getOriginalDocBase() {
        return this.originalDocBase;
    }

    public void setOriginalDocBase(String docBase) {
        this.originalDocBase = docBase;
    }

    @Override
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.getPrivileged()) {
            return this.getClass().getClassLoader();
        }
        if (this.parent != null) {
            return this.parent.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }

    @Override
    public boolean getPrivileged() {
        return this.privileged;
    }

    @Override
    public void setPrivileged(boolean privileged) {
        boolean oldPrivileged = this.privileged;
        this.privileged = privileged;
        this.support.firePropertyChange("privileged", oldPrivileged, this.privileged);
    }

    @Override
    public void setReloadable(boolean reloadable) {
        boolean oldReloadable = this.reloadable;
        this.reloadable = reloadable;
        this.support.firePropertyChange("reloadable", oldReloadable, this.reloadable);
    }

    @Override
    public void setOverride(boolean override) {
        boolean oldOverride = this.override;
        this.override = override;
        this.support.firePropertyChange("override", oldOverride, this.override);
    }

    public void setReplaceWelcomeFiles(boolean replaceWelcomeFiles) {
        boolean oldReplaceWelcomeFiles = this.replaceWelcomeFiles;
        this.replaceWelcomeFiles = replaceWelcomeFiles;
        this.support.firePropertyChange("replaceWelcomeFiles", oldReplaceWelcomeFiles, this.replaceWelcomeFiles);
    }

    @Override
    public ServletContext getServletContext() {
        if (this.context == null) {
            this.context = new ApplicationContext(this);
            if (this.altDDName != null) {
                this.context.setAttribute("org.apache.catalina.deploy.alt_dd", this.altDDName);
            }
        }
        return this.context.getFacade();
    }

    @Override
    public int getSessionTimeout() {
        return this.sessionTimeout;
    }

    @Override
    public void setSessionTimeout(int timeout) {
        int oldSessionTimeout = this.sessionTimeout;
        this.sessionTimeout = timeout == 0 ? -1 : timeout;
        this.support.firePropertyChange("sessionTimeout", oldSessionTimeout, this.sessionTimeout);
    }

    @Override
    public boolean getSwallowOutput() {
        return this.swallowOutput;
    }

    @Override
    public void setSwallowOutput(boolean swallowOutput) {
        boolean oldSwallowOutput = this.swallowOutput;
        this.swallowOutput = swallowOutput;
        this.support.firePropertyChange("swallowOutput", oldSwallowOutput, this.swallowOutput);
    }

    public long getUnloadDelay() {
        return this.unloadDelay;
    }

    public void setUnloadDelay(long unloadDelay) {
        long oldUnloadDelay = this.unloadDelay;
        this.unloadDelay = unloadDelay;
        this.support.firePropertyChange("unloadDelay", oldUnloadDelay, this.unloadDelay);
    }

    public boolean getUnpackWAR() {
        return this.unpackWAR;
    }

    public void setUnpackWAR(boolean unpackWAR) {
        this.unpackWAR = unpackWAR;
    }

    public boolean getCopyXML() {
        return this.copyXML;
    }

    public void setCopyXML(boolean copyXML) {
        this.copyXML = copyXML;
    }

    @Override
    public String getWrapperClass() {
        return this.wrapperClassName;
    }

    @Override
    public void setWrapperClass(String wrapperClassName) {
        this.wrapperClassName = wrapperClassName;
        try {
            this.wrapperClass = Class.forName(wrapperClassName);
            if (!StandardWrapper.class.isAssignableFrom(this.wrapperClass)) {
                throw new IllegalArgumentException(sm.getString("standardContext.invalidWrapperClass", new Object[]{wrapperClassName}));
            }
        }
        catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException(cnfe.getMessage());
        }
    }

    @Override
    public WebResourceRoot getResources() {
        Lock readLock = this.resourcesLock.readLock();
        readLock.lock();
        try {
            WebResourceRoot webResourceRoot = this.resources;
            return webResourceRoot;
        }
        finally {
            readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setResources(WebResourceRoot resources) {
        Lock writeLock = this.resourcesLock.writeLock();
        writeLock.lock();
        WebResourceRoot oldResources = null;
        try {
            if (this.getState().isAvailable()) {
                throw new IllegalStateException(sm.getString("standardContext.resourcesStart"));
            }
            oldResources = this.resources;
            if (oldResources == resources) {
                return;
            }
            this.resources = resources;
            if (oldResources != null) {
                oldResources.setContext(null);
            }
            if (resources != null) {
                resources.setContext(this);
            }
            this.support.firePropertyChange("resources", oldResources, resources);
        }
        finally {
            writeLock.unlock();
        }
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return this.jspConfigDescriptor;
    }

    @Override
    public void setJspConfigDescriptor(JspConfigDescriptor descriptor) {
        this.jspConfigDescriptor = descriptor;
    }

    @Override
    public ThreadBindingListener getThreadBindingListener() {
        return this.threadBindingListener;
    }

    @Override
    public void setThreadBindingListener(ThreadBindingListener threadBindingListener) {
        this.threadBindingListener = threadBindingListener;
    }

    public boolean getJndiExceptionOnFailedWrite() {
        return this.jndiExceptionOnFailedWrite;
    }

    public void setJndiExceptionOnFailedWrite(boolean jndiExceptionOnFailedWrite) {
        this.jndiExceptionOnFailedWrite = jndiExceptionOnFailedWrite;
    }

    public String getCharsetMapperClass() {
        return this.charsetMapperClass;
    }

    public void setCharsetMapperClass(String mapper) {
        String oldCharsetMapperClass = this.charsetMapperClass;
        this.charsetMapperClass = mapper;
        this.support.firePropertyChange("charsetMapperClass", oldCharsetMapperClass, this.charsetMapperClass);
    }

    public String getWorkPath() {
        if (this.getWorkDir() == null) {
            return null;
        }
        File workDir = new File(this.getWorkDir());
        if (!workDir.isAbsolute()) {
            try {
                workDir = new File(this.getCatalinaBase().getCanonicalFile(), this.getWorkDir());
            }
            catch (IOException e) {
                log.warn((Object)sm.getString("standardContext.workPath", new Object[]{this.getName()}), (Throwable)e);
            }
        }
        return workDir.getAbsolutePath();
    }

    public String getWorkDir() {
        return this.workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
        if (this.getState().isAvailable()) {
            this.postWorkDirectory();
        }
    }

    public boolean getClearReferencesRmiTargets() {
        return this.clearReferencesRmiTargets;
    }

    public void setClearReferencesRmiTargets(boolean clearReferencesRmiTargets) {
        boolean oldClearReferencesRmiTargets = this.clearReferencesRmiTargets;
        this.clearReferencesRmiTargets = clearReferencesRmiTargets;
        this.support.firePropertyChange("clearReferencesRmiTargets", oldClearReferencesRmiTargets, this.clearReferencesRmiTargets);
    }

    public boolean getClearReferencesStopThreads() {
        return this.clearReferencesStopThreads;
    }

    public void setClearReferencesStopThreads(boolean clearReferencesStopThreads) {
        boolean oldClearReferencesStopThreads = this.clearReferencesStopThreads;
        this.clearReferencesStopThreads = clearReferencesStopThreads;
        this.support.firePropertyChange("clearReferencesStopThreads", oldClearReferencesStopThreads, this.clearReferencesStopThreads);
    }

    public boolean getClearReferencesStopTimerThreads() {
        return this.clearReferencesStopTimerThreads;
    }

    public void setClearReferencesStopTimerThreads(boolean clearReferencesStopTimerThreads) {
        boolean oldClearReferencesStopTimerThreads = this.clearReferencesStopTimerThreads;
        this.clearReferencesStopTimerThreads = clearReferencesStopTimerThreads;
        this.support.firePropertyChange("clearReferencesStopTimerThreads", oldClearReferencesStopTimerThreads, this.clearReferencesStopTimerThreads);
    }

    public boolean getClearReferencesHttpClientKeepAliveThread() {
        return this.clearReferencesHttpClientKeepAliveThread;
    }

    public void setClearReferencesHttpClientKeepAliveThread(boolean clearReferencesHttpClientKeepAliveThread) {
        this.clearReferencesHttpClientKeepAliveThread = clearReferencesHttpClientKeepAliveThread;
    }

    public boolean getRenewThreadsWhenStoppingContext() {
        return this.renewThreadsWhenStoppingContext;
    }

    public void setRenewThreadsWhenStoppingContext(boolean renewThreadsWhenStoppingContext) {
        boolean oldRenewThreadsWhenStoppingContext = this.renewThreadsWhenStoppingContext;
        this.renewThreadsWhenStoppingContext = renewThreadsWhenStoppingContext;
        this.support.firePropertyChange("renewThreadsWhenStoppingContext", oldRenewThreadsWhenStoppingContext, this.renewThreadsWhenStoppingContext);
    }

    public boolean getClearReferencesObjectStreamClassCaches() {
        return this.clearReferencesObjectStreamClassCaches;
    }

    public void setClearReferencesObjectStreamClassCaches(boolean clearReferencesObjectStreamClassCaches) {
        boolean oldClearReferencesObjectStreamClassCaches = this.clearReferencesObjectStreamClassCaches;
        this.clearReferencesObjectStreamClassCaches = clearReferencesObjectStreamClassCaches;
        this.support.firePropertyChange("clearReferencesObjectStreamClassCaches", oldClearReferencesObjectStreamClassCaches, this.clearReferencesObjectStreamClassCaches);
    }

    public boolean getClearReferencesThreadLocals() {
        return this.clearReferencesThreadLocals;
    }

    public void setClearReferencesThreadLocals(boolean clearReferencesThreadLocals) {
        boolean oldClearReferencesThreadLocals = this.clearReferencesThreadLocals;
        this.clearReferencesThreadLocals = clearReferencesThreadLocals;
        this.support.firePropertyChange("clearReferencesThreadLocals", oldClearReferencesThreadLocals, this.clearReferencesThreadLocals);
    }

    public boolean getSkipMemoryLeakChecksOnJvmShutdown() {
        return this.skipMemoryLeakChecksOnJvmShutdown;
    }

    public void setSkipMemoryLeakChecksOnJvmShutdown(boolean skipMemoryLeakChecksOnJvmShutdown) {
        this.skipMemoryLeakChecksOnJvmShutdown = skipMemoryLeakChecksOnJvmShutdown;
    }

    public Boolean getFailCtxIfServletStartFails() {
        return this.failCtxIfServletStartFails;
    }

    public void setFailCtxIfServletStartFails(Boolean failCtxIfServletStartFails) {
        Boolean oldFailCtxIfServletStartFails = this.failCtxIfServletStartFails;
        this.failCtxIfServletStartFails = failCtxIfServletStartFails;
        this.support.firePropertyChange("failCtxIfServletStartFails", oldFailCtxIfServletStartFails, failCtxIfServletStartFails);
    }

    protected boolean getComputedFailCtxIfServletStartFails() {
        if (this.failCtxIfServletStartFails != null) {
            return this.failCtxIfServletStartFails;
        }
        if (this.getParent() instanceof StandardHost) {
            return ((StandardHost)this.getParent()).isFailCtxIfServletStartFails();
        }
        return false;
    }

    @Override
    public void addApplicationListener(String listener) {
        if (this.applicationListeners.addIfAbsent(listener)) {
            this.fireContainerEvent("addApplicationListener", listener);
        } else {
            log.info((Object)sm.getString("standardContext.duplicateListener", new Object[]{listener}));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addApplicationParameter(ApplicationParameter parameter) {
        Object object = this.applicationParametersLock;
        synchronized (object) {
            String newName = parameter.getName();
            for (ApplicationParameter p : this.applicationParameters) {
                if (!newName.equals(p.getName()) || p.getOverride()) continue;
                return;
            }
            ApplicationParameter[] results = Arrays.copyOf(this.applicationParameters, this.applicationParameters.length + 1);
            results[this.applicationParameters.length] = parameter;
            this.applicationParameters = results;
        }
        this.fireContainerEvent("addApplicationParameter", parameter);
    }

    @Override
    public void addChild(Container child) {
        Wrapper oldJspServlet = null;
        if (!(child instanceof Wrapper)) {
            throw new IllegalArgumentException(sm.getString("standardContext.notWrapper"));
        }
        boolean isJspServlet = "jsp".equals(child.getName());
        if (isJspServlet && (oldJspServlet = (Wrapper)this.findChild("jsp")) != null) {
            this.removeChild(oldJspServlet);
        }
        super.addChild(child);
        if (isJspServlet && oldJspServlet != null) {
            String[] jspMappings = oldJspServlet.findMappings();
            for (int i = 0; jspMappings != null && i < jspMappings.length; ++i) {
                this.addServletMappingDecoded(jspMappings[i], child.getName());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addConstraint(SecurityConstraint constraint) {
        SecurityCollection[] collections;
        for (SecurityCollection collection : collections = constraint.findCollections()) {
            String[] patterns = collection.findPatterns();
            for (int j = 0; j < patterns.length; ++j) {
                patterns[j] = this.adjustURLPattern(patterns[j]);
                if (this.validateURLPattern(patterns[j])) continue;
                throw new IllegalArgumentException(sm.getString("standardContext.securityConstraint.pattern", new Object[]{patterns[j]}));
            }
            if (collection.findMethods().length <= 0 || collection.findOmittedMethods().length <= 0) continue;
            throw new IllegalArgumentException(sm.getString("standardContext.securityConstraint.mixHttpMethod"));
        }
        Object object = this.constraintsLock;
        synchronized (object) {
            SecurityConstraint[] results = Arrays.copyOf(this.constraints, this.constraints.length + 1);
            results[this.constraints.length] = constraint;
            this.constraints = results;
        }
    }

    @Override
    public void addErrorPage(ErrorPage errorPage) {
        if (errorPage == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.errorPage.required"));
        }
        String location = errorPage.getLocation();
        if (location != null && !location.startsWith("/")) {
            if (this.isServlet22()) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("standardContext.errorPage.warning", new Object[]{location}));
                }
                errorPage.setLocation("/" + location);
            } else {
                throw new IllegalArgumentException(sm.getString("standardContext.errorPage.error", new Object[]{location}));
            }
        }
        this.errorPageSupport.add(errorPage);
        this.fireContainerEvent("addErrorPage", errorPage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addFilterDef(FilterDef filterDef) {
        Map<String, FilterDef> map = this.filterDefs;
        synchronized (map) {
            this.filterDefs.put(filterDef.getFilterName(), filterDef);
        }
        this.fireContainerEvent("addFilterDef", filterDef);
    }

    @Override
    public void addFilterMap(FilterMap filterMap) {
        this.validateFilterMap(filterMap);
        this.filterMaps.add(filterMap);
        this.fireContainerEvent("addFilterMap", filterMap);
    }

    @Override
    public void addFilterMapBefore(FilterMap filterMap) {
        this.validateFilterMap(filterMap);
        this.filterMaps.addBefore(filterMap);
        this.fireContainerEvent("addFilterMap", filterMap);
    }

    private void validateFilterMap(FilterMap filterMap) {
        String filterName = filterMap.getFilterName();
        String[] servletNames = filterMap.getServletNames();
        String[] urlPatterns = filterMap.getURLPatterns();
        if (this.findFilterDef(filterName) == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.filterMap.name", new Object[]{filterName}));
        }
        if (!filterMap.getMatchAllServletNames() && !filterMap.getMatchAllUrlPatterns() && servletNames.length == 0 && urlPatterns.length == 0) {
            throw new IllegalArgumentException(sm.getString("standardContext.filterMap.either"));
        }
        for (String urlPattern : urlPatterns) {
            if (this.validateURLPattern(urlPattern)) continue;
            throw new IllegalArgumentException(sm.getString("standardContext.filterMap.pattern", new Object[]{urlPattern}));
        }
    }

    @Override
    public void addLocaleEncodingMappingParameter(String locale, String encoding) {
        this.getCharsetMapper().addCharsetMappingFromDeploymentDescriptor(locale, encoding);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addMessageDestination(MessageDestination md) {
        HashMap<String, MessageDestination> hashMap = this.messageDestinations;
        synchronized (hashMap) {
            this.messageDestinations.put(md.getName(), md);
        }
        this.fireContainerEvent("addMessageDestination", md.getName());
    }

    @Deprecated
    public void addMessageDestinationRef(MessageDestinationRef mdr) {
        this.getNamingResources().addMessageDestinationRef(mdr);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addMimeMapping(String extension, String mimeType) {
        Map<String, String> map = this.mimeMappings;
        synchronized (map) {
            this.mimeMappings.put(extension.toLowerCase(Locale.ENGLISH), mimeType);
        }
        this.fireContainerEvent("addMimeMapping", extension);
    }

    @Override
    public void addParameter(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.parameter.required"));
        }
        String oldValue = this.parameters.putIfAbsent(name, value);
        if (oldValue != null) {
            throw new IllegalArgumentException(sm.getString("standardContext.parameter.duplicate", new Object[]{name}));
        }
        this.fireContainerEvent("addParameter", name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addRoleMapping(String role, String link) {
        Map<String, String> map = this.roleMappings;
        synchronized (map) {
            this.roleMappings.put(role, link);
        }
        this.fireContainerEvent("addRoleMapping", role);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSecurityRole(String role) {
        Object object = this.securityRolesLock;
        synchronized (object) {
            String[] results = Arrays.copyOf(this.securityRoles, this.securityRoles.length + 1);
            results[this.securityRoles.length] = role;
            this.securityRoles = results;
        }
        this.fireContainerEvent("addSecurityRole", role);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addServletMappingDecoded(String pattern, String name, boolean jspWildCard) {
        if (this.findChild(name) == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.servletMap.name", new Object[]{name}));
        }
        String adjustedPattern = this.adjustURLPattern(pattern);
        if (!this.validateURLPattern(adjustedPattern)) {
            throw new IllegalArgumentException(sm.getString("standardContext.servletMap.pattern", new Object[]{adjustedPattern}));
        }
        Object object = this.servletMappingsLock;
        synchronized (object) {
            String name2 = this.servletMappings.get(adjustedPattern);
            if (name2 != null) {
                Wrapper wrapper = (Wrapper)this.findChild(name2);
                wrapper.removeMapping(adjustedPattern);
            }
            this.servletMappings.put(adjustedPattern, name);
        }
        Wrapper wrapper = (Wrapper)this.findChild(name);
        wrapper.addMapping(adjustedPattern);
        this.fireContainerEvent("addServletMapping", adjustedPattern);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addWatchedResource(String name) {
        Object object = this.watchedResourcesLock;
        synchronized (object) {
            String[] results = Arrays.copyOf(this.watchedResources, this.watchedResources.length + 1);
            results[this.watchedResources.length] = name;
            this.watchedResources = results;
        }
        this.fireContainerEvent("addWatchedResource", name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addWelcomeFile(String name) {
        Object object = this.welcomeFilesLock;
        synchronized (object) {
            if (this.replaceWelcomeFiles) {
                this.fireContainerEvent("clearWelcomeFiles", null);
                this.welcomeFiles = new String[0];
                this.setReplaceWelcomeFiles(false);
            }
            String[] results = Arrays.copyOf(this.welcomeFiles, this.welcomeFiles.length + 1);
            results[this.welcomeFiles.length] = name;
            this.welcomeFiles = results;
        }
        if (this.getState().equals((Object)LifecycleState.STARTED)) {
            this.fireContainerEvent("addWelcomeFile", name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addWrapperLifecycle(String listener) {
        Object object = this.wrapperLifecyclesLock;
        synchronized (object) {
            String[] results = Arrays.copyOf(this.wrapperLifecycles, this.wrapperLifecycles.length + 1);
            results[this.wrapperLifecycles.length] = listener;
            this.wrapperLifecycles = results;
        }
        this.fireContainerEvent("addWrapperLifecycle", listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addWrapperListener(String listener) {
        Object object = this.wrapperListenersLock;
        synchronized (object) {
            String[] results = Arrays.copyOf(this.wrapperListeners, this.wrapperListeners.length + 1);
            results[this.wrapperListeners.length] = listener;
            this.wrapperListeners = results;
        }
        this.fireContainerEvent("addWrapperListener", listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Wrapper createWrapper() {
        Object listener;
        Class<?> clazz;
        Wrapper wrapper = null;
        if (this.wrapperClass != null) {
            try {
                wrapper = (Wrapper)this.wrapperClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                log.error((Object)sm.getString("standardContext.createWrapper.error"), t);
                return null;
            }
        } else {
            wrapper = new StandardWrapper();
        }
        Object object = this.wrapperLifecyclesLock;
        synchronized (object) {
            for (String wrapperLifecycle : this.wrapperLifecycles) {
                try {
                    clazz = Class.forName(wrapperLifecycle);
                    listener = (LifecycleListener)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    wrapper.addLifecycleListener((LifecycleListener)listener);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    log.error((Object)sm.getString("standardContext.createWrapper.listenerError"), t);
                    return null;
                }
            }
        }
        object = this.wrapperListenersLock;
        synchronized (object) {
            for (String wrapperListener : this.wrapperListeners) {
                try {
                    clazz = Class.forName(wrapperListener);
                    listener = (ContainerListener)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    wrapper.addContainerListener((ContainerListener)listener);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    log.error((Object)sm.getString("standardContext.createWrapper.containerListenerError"), t);
                    return null;
                }
            }
        }
        return wrapper;
    }

    @Override
    public String[] findApplicationListeners() {
        return this.applicationListeners.toArray(new String[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ApplicationParameter[] findApplicationParameters() {
        Object object = this.applicationParametersLock;
        synchronized (object) {
            return this.applicationParameters;
        }
    }

    @Override
    public SecurityConstraint[] findConstraints() {
        return this.constraints;
    }

    @Override
    public ErrorPage findErrorPage(int errorCode) {
        return this.errorPageSupport.find(errorCode);
    }

    @Override
    @Deprecated
    public ErrorPage findErrorPage(String exceptionType) {
        return this.errorPageSupport.find(exceptionType);
    }

    @Override
    public ErrorPage findErrorPage(Throwable exceptionType) {
        return this.errorPageSupport.find(exceptionType);
    }

    @Override
    public ErrorPage[] findErrorPages() {
        return this.errorPageSupport.findAll();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FilterDef findFilterDef(String filterName) {
        Map<String, FilterDef> map = this.filterDefs;
        synchronized (map) {
            return this.filterDefs.get(filterName);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FilterDef[] findFilterDefs() {
        Map<String, FilterDef> map = this.filterDefs;
        synchronized (map) {
            return this.filterDefs.values().toArray(new FilterDef[0]);
        }
    }

    @Override
    public FilterMap[] findFilterMaps() {
        return this.filterMaps.asArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MessageDestination findMessageDestination(String name) {
        HashMap<String, MessageDestination> hashMap = this.messageDestinations;
        synchronized (hashMap) {
            return this.messageDestinations.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MessageDestination[] findMessageDestinations() {
        HashMap<String, MessageDestination> hashMap = this.messageDestinations;
        synchronized (hashMap) {
            return this.messageDestinations.values().toArray(new MessageDestination[0]);
        }
    }

    @Deprecated
    public MessageDestinationRef findMessageDestinationRef(String name) {
        return this.getNamingResources().findMessageDestinationRef(name);
    }

    @Deprecated
    public MessageDestinationRef[] findMessageDestinationRefs() {
        return this.getNamingResources().findMessageDestinationRefs();
    }

    @Override
    public String findMimeMapping(String extension) {
        return this.mimeMappings.get(extension.toLowerCase(Locale.ENGLISH));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] findMimeMappings() {
        Map<String, String> map = this.mimeMappings;
        synchronized (map) {
            return this.mimeMappings.keySet().toArray(new String[0]);
        }
    }

    @Override
    public String findParameter(String name) {
        return this.parameters.get(name);
    }

    @Override
    public String[] findParameters() {
        return this.parameters.keySet().toArray(new String[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String findRoleMapping(String role) {
        String realRole = null;
        Map<String, String> map = this.roleMappings;
        synchronized (map) {
            realRole = this.roleMappings.get(role);
        }
        if (realRole != null) {
            return realRole;
        }
        return role;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean findSecurityRole(String role) {
        Object object = this.securityRolesLock;
        synchronized (object) {
            for (String securityRole : this.securityRoles) {
                if (!role.equals(securityRole)) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] findSecurityRoles() {
        Object object = this.securityRolesLock;
        synchronized (object) {
            return this.securityRoles;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String findServletMapping(String pattern) {
        Object object = this.servletMappingsLock;
        synchronized (object) {
            return this.servletMappings.get(pattern);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] findServletMappings() {
        Object object = this.servletMappingsLock;
        synchronized (object) {
            return this.servletMappings.keySet().toArray(new String[0]);
        }
    }

    @Override
    @Deprecated
    public String findStatusPage(int status) {
        ErrorPage errorPage = this.findErrorPage(status);
        if (errorPage != null) {
            return errorPage.getLocation();
        }
        return null;
    }

    @Override
    @Deprecated
    public int[] findStatusPages() {
        ErrorPage[] errorPages = this.findErrorPages();
        int size = errorPages.length;
        int[] temp = new int[size];
        int count = 0;
        for (int i = 0; i < size; ++i) {
            if (errorPages[i].getExceptionType() != null) continue;
            temp[count++] = errorPages[i].getErrorCode();
        }
        int[] result = new int[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean findWelcomeFile(String name) {
        Object object = this.welcomeFilesLock;
        synchronized (object) {
            for (String welcomeFile : this.welcomeFiles) {
                if (!name.equals(welcomeFile)) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] findWatchedResources() {
        Object object = this.watchedResourcesLock;
        synchronized (object) {
            return this.watchedResources;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] findWelcomeFiles() {
        Object object = this.welcomeFilesLock;
        synchronized (object) {
            return this.welcomeFiles;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] findWrapperLifecycles() {
        Object object = this.wrapperLifecyclesLock;
        synchronized (object) {
            return this.wrapperLifecycles;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] findWrapperListeners() {
        Object object = this.wrapperListenersLock;
        synchronized (object) {
            return this.wrapperListeners;
        }
    }

    @Override
    public synchronized void reload() {
        if (!this.getState().isAvailable()) {
            throw new IllegalStateException(sm.getString("standardContext.notStarted", new Object[]{this.getName()}));
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("standardContext.reloadingStarted", new Object[]{this.getName()}));
        }
        this.setPaused(true);
        try {
            this.stop();
        }
        catch (LifecycleException e) {
            log.error((Object)sm.getString("standardContext.stoppingContext", new Object[]{this.getName()}), (Throwable)e);
        }
        try {
            this.start();
        }
        catch (LifecycleException e) {
            log.error((Object)sm.getString("standardContext.startingContext", new Object[]{this.getName()}), (Throwable)e);
        }
        this.setPaused(false);
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("standardContext.reloadingCompleted", new Object[]{this.getName()}));
        }
    }

    @Override
    public void removeApplicationListener(String listener) {
        if (this.applicationListeners.remove(listener)) {
            this.fireContainerEvent("removeApplicationListener", listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeApplicationParameter(String name) {
        Object object = this.applicationParametersLock;
        synchronized (object) {
            int n = -1;
            for (int i = 0; i < this.applicationParameters.length; ++i) {
                if (!name.equals(this.applicationParameters[i].getName())) continue;
                n = i;
                break;
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            ApplicationParameter[] results = new ApplicationParameter[this.applicationParameters.length - 1];
            for (int i = 0; i < this.applicationParameters.length; ++i) {
                if (i == n) continue;
                results[j++] = this.applicationParameters[i];
            }
            this.applicationParameters = results;
        }
        this.fireContainerEvent("removeApplicationParameter", name);
    }

    @Override
    public void removeChild(Container child) {
        if (!(child instanceof Wrapper)) {
            throw new IllegalArgumentException(sm.getString("standardContext.notWrapper"));
        }
        super.removeChild(child);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeConstraint(SecurityConstraint constraint) {
        Object object = this.constraintsLock;
        synchronized (object) {
            int n = -1;
            for (int i = 0; i < this.constraints.length; ++i) {
                if (!this.constraints[i].equals(constraint)) continue;
                n = i;
                break;
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            SecurityConstraint[] results = new SecurityConstraint[this.constraints.length - 1];
            for (int i = 0; i < this.constraints.length; ++i) {
                if (i == n) continue;
                results[j++] = this.constraints[i];
            }
            this.constraints = results;
        }
        this.fireContainerEvent("removeConstraint", constraint);
    }

    @Override
    public void removeErrorPage(ErrorPage errorPage) {
        this.errorPageSupport.remove(errorPage);
        this.fireContainerEvent("removeErrorPage", errorPage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFilterDef(FilterDef filterDef) {
        Map<String, FilterDef> map = this.filterDefs;
        synchronized (map) {
            this.filterDefs.remove(filterDef.getFilterName());
        }
        this.fireContainerEvent("removeFilterDef", filterDef);
    }

    @Override
    public void removeFilterMap(FilterMap filterMap) {
        this.filterMaps.remove(filterMap);
        this.fireContainerEvent("removeFilterMap", filterMap);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeMessageDestination(String name) {
        HashMap<String, MessageDestination> hashMap = this.messageDestinations;
        synchronized (hashMap) {
            this.messageDestinations.remove(name);
        }
        this.fireContainerEvent("removeMessageDestination", name);
    }

    @Deprecated
    public void removeMessageDestinationRef(String name) {
        this.getNamingResources().removeMessageDestinationRef(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMimeMapping(String extension) {
        Map<String, String> map = this.mimeMappings;
        synchronized (map) {
            this.mimeMappings.remove(extension);
        }
        this.fireContainerEvent("removeMimeMapping", extension);
    }

    @Override
    public void removeParameter(String name) {
        this.parameters.remove(name);
        this.fireContainerEvent("removeParameter", name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRoleMapping(String role) {
        Map<String, String> map = this.roleMappings;
        synchronized (map) {
            this.roleMappings.remove(role);
        }
        this.fireContainerEvent("removeRoleMapping", role);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSecurityRole(String role) {
        Object object = this.securityRolesLock;
        synchronized (object) {
            int n = -1;
            for (int i = 0; i < this.securityRoles.length; ++i) {
                if (!role.equals(this.securityRoles[i])) continue;
                n = i;
                break;
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.securityRoles.length - 1];
            for (int i = 0; i < this.securityRoles.length; ++i) {
                if (i == n) continue;
                results[j++] = this.securityRoles[i];
            }
            this.securityRoles = results;
        }
        this.fireContainerEvent("removeSecurityRole", role);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeServletMapping(String pattern) {
        String name = null;
        Object object = this.servletMappingsLock;
        synchronized (object) {
            name = this.servletMappings.remove(pattern);
        }
        Wrapper wrapper = (Wrapper)this.findChild(name);
        if (wrapper != null) {
            wrapper.removeMapping(pattern);
        }
        this.fireContainerEvent("removeServletMapping", pattern);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeWatchedResource(String name) {
        Object object = this.watchedResourcesLock;
        synchronized (object) {
            int n = -1;
            for (int i = 0; i < this.watchedResources.length; ++i) {
                if (!this.watchedResources[i].equals(name)) continue;
                n = i;
                break;
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.watchedResources.length - 1];
            for (int i = 0; i < this.watchedResources.length; ++i) {
                if (i == n) continue;
                results[j++] = this.watchedResources[i];
            }
            this.watchedResources = results;
        }
        this.fireContainerEvent("removeWatchedResource", name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeWelcomeFile(String name) {
        Object object = this.welcomeFilesLock;
        synchronized (object) {
            int n = -1;
            for (int i = 0; i < this.welcomeFiles.length; ++i) {
                if (!this.welcomeFiles[i].equals(name)) continue;
                n = i;
                break;
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.welcomeFiles.length - 1];
            for (int i = 0; i < this.welcomeFiles.length; ++i) {
                if (i == n) continue;
                results[j++] = this.welcomeFiles[i];
            }
            this.welcomeFiles = results;
        }
        if (this.getState().equals((Object)LifecycleState.STARTED)) {
            this.fireContainerEvent("removeWelcomeFile", name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeWrapperLifecycle(String listener) {
        Object object = this.wrapperLifecyclesLock;
        synchronized (object) {
            int n = -1;
            for (int i = 0; i < this.wrapperLifecycles.length; ++i) {
                if (!this.wrapperLifecycles[i].equals(listener)) continue;
                n = i;
                break;
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.wrapperLifecycles.length - 1];
            for (int i = 0; i < this.wrapperLifecycles.length; ++i) {
                if (i == n) continue;
                results[j++] = this.wrapperLifecycles[i];
            }
            this.wrapperLifecycles = results;
        }
        this.fireContainerEvent("removeWrapperLifecycle", listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeWrapperListener(String listener) {
        Object object = this.wrapperListenersLock;
        synchronized (object) {
            int n = -1;
            for (int i = 0; i < this.wrapperListeners.length; ++i) {
                if (!this.wrapperListeners[i].equals(listener)) continue;
                n = i;
                break;
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.wrapperListeners.length - 1];
            for (int i = 0; i < this.wrapperListeners.length; ++i) {
                if (i == n) continue;
                results[j++] = this.wrapperListeners[i];
            }
            this.wrapperListeners = results;
        }
        this.fireContainerEvent("removeWrapperListener", listener);
    }

    public long getProcessingTime() {
        long result = 0L;
        Container[] children = this.findChildren();
        if (children != null) {
            for (Container child : children) {
                result += ((StandardWrapper)child).getProcessingTime();
            }
        }
        return result;
    }

    public long getMaxTime() {
        long result = 0L;
        Container[] children = this.findChildren();
        if (children != null) {
            for (Container child : children) {
                long time = ((StandardWrapper)child).getMaxTime();
                if (time <= result) continue;
                result = time;
            }
        }
        return result;
    }

    public long getMinTime() {
        long result = -1L;
        Container[] children = this.findChildren();
        if (children != null) {
            for (Container child : children) {
                long time = ((StandardWrapper)child).getMinTime();
                if (result >= 0L && time >= result) continue;
                result = time;
            }
        }
        return result;
    }

    public int getRequestCount() {
        int result = 0;
        Container[] children = this.findChildren();
        if (children != null) {
            for (Container child : children) {
                result += ((StandardWrapper)child).getRequestCount();
            }
        }
        return result;
    }

    public int getErrorCount() {
        int result = 0;
        Container[] children = this.findChildren();
        if (children != null) {
            for (Container child : children) {
                result += ((StandardWrapper)child).getErrorCount();
            }
        }
        return result;
    }

    @Override
    public String getRealPath(String path) {
        if ("".equals(path)) {
            path = "/";
        }
        if (this.resources != null) {
            try {
                WebResource resource = this.resources.getResource(path);
                String canonicalPath = resource.getCanonicalPath();
                if (canonicalPath == null) {
                    return null;
                }
                if ((resource.isDirectory() && !canonicalPath.endsWith(File.separator) || !resource.exists()) && path.endsWith("/")) {
                    return canonicalPath + File.separatorChar;
                }
                return canonicalPath;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return null;
    }

    public void dynamicServletCreated(Servlet servlet) {
        this.createdServlets.add(servlet);
    }

    public boolean wasCreatedDynamicServlet(Servlet servlet) {
        return this.createdServlets.contains(servlet);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean filterStart() {
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug((Object)"Starting filters");
        }
        boolean ok = true;
        Map<String, FilterDef> map = this.filterDefs;
        synchronized (map) {
            this.filterConfigs.clear();
            for (Map.Entry<String, FilterDef> entry : this.filterDefs.entrySet()) {
                String name = entry.getKey();
                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug((Object)(" Starting filter '" + name + "'"));
                }
                try {
                    ApplicationFilterConfig filterConfig = new ApplicationFilterConfig(this, entry.getValue());
                    this.filterConfigs.put(name, filterConfig);
                }
                catch (Throwable t) {
                    t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.getLogger().error((Object)sm.getString("standardContext.filterStart", new Object[]{name}), t);
                    ok = false;
                }
            }
        }
        return ok;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean filterStop() {
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug((Object)"Stopping filters");
        }
        Map<String, FilterDef> map = this.filterDefs;
        synchronized (map) {
            for (Map.Entry<String, ApplicationFilterConfig> entry : this.filterConfigs.entrySet()) {
                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug((Object)(" Stopping filter '" + entry.getKey() + "'"));
                }
                ApplicationFilterConfig filterConfig = entry.getValue();
                filterConfig.release();
            }
            this.filterConfigs.clear();
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public FilterConfig findFilterConfig(String name) {
        Map<String, FilterDef> map = this.filterDefs;
        synchronized (map) {
            return this.filterConfigs.get(name);
        }
    }

    public boolean listenerStart() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Configuring application event listeners");
        }
        String[] listeners = this.findApplicationListeners();
        Object[] results = new Object[listeners.length];
        boolean ok = true;
        for (int i = 0; i < results.length; ++i) {
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug((Object)(" Configuring event listener class '" + listeners[i] + "'"));
            }
            try {
                String listener = listeners[i];
                results[i] = this.getInstanceManager().newInstance(listener);
                continue;
            }
            catch (Throwable t) {
                t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
                ExceptionUtils.handleThrowable((Throwable)t);
                this.getLogger().error((Object)sm.getString("standardContext.applicationListener", new Object[]{listeners[i]}), t);
                ok = false;
            }
        }
        if (!ok) {
            this.getLogger().error((Object)sm.getString("standardContext.applicationSkipped"));
            return false;
        }
        ArrayList<Object> eventListeners = new ArrayList<Object>();
        ArrayList<Object> lifecycleListeners = new ArrayList<Object>();
        for (Object result : results) {
            if (result instanceof ServletContextAttributeListener || result instanceof ServletRequestAttributeListener || result instanceof ServletRequestListener || result instanceof HttpSessionIdListener || result instanceof HttpSessionAttributeListener) {
                eventListeners.add(result);
            }
            if (!(result instanceof ServletContextListener) && !(result instanceof HttpSessionListener)) continue;
            lifecycleListeners.add(result);
        }
        eventListeners.addAll(Arrays.asList(this.getApplicationEventListeners()));
        this.setApplicationEventListeners(eventListeners.toArray());
        for (Object lifecycleListener : this.getApplicationLifecycleListeners()) {
            lifecycleListeners.add(lifecycleListener);
            if (!(lifecycleListener instanceof ServletContextListener)) continue;
            this.noPluggabilityListeners.add(lifecycleListener);
        }
        this.setApplicationLifecycleListeners(lifecycleListeners.toArray());
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug((Object)"Sending application start events");
        }
        this.getServletContext();
        this.context.setNewServletContextListenerAllowed(false);
        Object[] instances = this.getApplicationLifecycleListeners();
        if (instances == null || instances.length == 0) {
            return ok;
        }
        ServletContextEvent event = new ServletContextEvent(this.getServletContext());
        ServletContextEvent tldEvent = null;
        if (this.noPluggabilityListeners.size() > 0) {
            this.noPluggabilityServletContext = new NoPluggabilityServletContext(this.getServletContext());
            tldEvent = new ServletContextEvent((ServletContext)this.noPluggabilityServletContext);
        }
        for (Object instance : instances) {
            if (!(instance instanceof ServletContextListener)) continue;
            ServletContextListener listener = (ServletContextListener)instance;
            try {
                this.fireContainerEvent("beforeContextInitialized", listener);
                if (this.noPluggabilityListeners.contains(listener)) {
                    listener.contextInitialized(tldEvent);
                } else {
                    listener.contextInitialized(event);
                }
                this.fireContainerEvent("afterContextInitialized", listener);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.fireContainerEvent("afterContextInitialized", listener);
                this.getLogger().error((Object)sm.getString("standardContext.listenerStart", new Object[]{instance.getClass().getName()}), t);
                ok = false;
            }
        }
        return ok;
    }

    public boolean listenerStop() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Sending application stop events");
        }
        boolean ok = true;
        Object[] listeners = this.getApplicationLifecycleListeners();
        if (listeners != null && listeners.length > 0) {
            ServletContextEvent event = new ServletContextEvent(this.getServletContext());
            ServletContextEvent tldEvent = null;
            if (this.noPluggabilityServletContext != null) {
                tldEvent = new ServletContextEvent((ServletContext)this.noPluggabilityServletContext);
            }
            for (int i = 0; i < listeners.length; ++i) {
                int j = listeners.length - 1 - i;
                if (listeners[j] == null) continue;
                if (listeners[j] instanceof ServletContextListener) {
                    ServletContextListener listener = (ServletContextListener)listeners[j];
                    try {
                        this.fireContainerEvent("beforeContextDestroyed", listener);
                        if (this.noPluggabilityListeners.contains(listener)) {
                            listener.contextDestroyed(tldEvent);
                        } else {
                            listener.contextDestroyed(event);
                        }
                        this.fireContainerEvent("afterContextDestroyed", listener);
                    }
                    catch (Throwable t) {
                        ExceptionUtils.handleThrowable((Throwable)t);
                        this.fireContainerEvent("afterContextDestroyed", listener);
                        this.getLogger().error((Object)sm.getString("standardContext.listenerStop", new Object[]{listeners[j].getClass().getName()}), t);
                        ok = false;
                    }
                }
                try {
                    if (this.getInstanceManager() == null) continue;
                    this.getInstanceManager().destroyInstance(listeners[j]);
                    continue;
                }
                catch (Throwable t) {
                    t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.getLogger().error((Object)sm.getString("standardContext.listenerStop", new Object[]{listeners[j].getClass().getName()}), t);
                    ok = false;
                }
            }
        }
        if ((listeners = this.getApplicationEventListeners()) != null) {
            for (int i = 0; i < listeners.length; ++i) {
                int j = listeners.length - 1 - i;
                if (listeners[j] == null) continue;
                try {
                    if (this.getInstanceManager() == null) continue;
                    this.getInstanceManager().destroyInstance(listeners[j]);
                    continue;
                }
                catch (Throwable t) {
                    t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.getLogger().error((Object)sm.getString("standardContext.listenerStop", new Object[]{listeners[j].getClass().getName()}), t);
                    ok = false;
                }
            }
        }
        this.setApplicationEventListeners(null);
        this.setApplicationLifecycleListeners(null);
        this.noPluggabilityServletContext = null;
        this.noPluggabilityListeners.clear();
        return ok;
    }

    public void resourcesStart() throws LifecycleException {
        WebResource webinfClassesResource;
        if (!this.resources.getState().isAvailable()) {
            this.resources.start();
        }
        if (this.effectiveMajorVersion >= 3 && this.addWebinfClassesResources && (webinfClassesResource = this.resources.getResource("/WEB-INF/classes/META-INF/resources")).isDirectory()) {
            this.getResources().createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", webinfClassesResource.getURL(), "/");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean resourcesStop() {
        boolean ok = true;
        Lock writeLock = this.resourcesLock.writeLock();
        writeLock.lock();
        try {
            if (this.resources != null) {
                this.resources.stop();
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            log.error((Object)sm.getString("standardContext.resourcesStop"), t);
            ok = false;
        }
        finally {
            writeLock.unlock();
        }
        return ok;
    }

    public boolean loadOnStartup(Container[] children) {
        TreeMap<Integer, ArrayList> map = new TreeMap<Integer, ArrayList>();
        for (Container child : children) {
            Wrapper wrapper = (Wrapper)child;
            int loadOnStartup = wrapper.getLoadOnStartup();
            if (loadOnStartup < 0) continue;
            Integer key = loadOnStartup;
            map.computeIfAbsent(key, k -> new ArrayList()).add(wrapper);
        }
        for (ArrayList list : map.values()) {
            for (Wrapper wrapper : list) {
                try {
                    wrapper.load();
                }
                catch (ServletException e) {
                    this.getLogger().error((Object)sm.getString("standardContext.loadOnStartup.loadException", new Object[]{this.getName(), wrapper.getName()}), StandardWrapper.getRootCause(e));
                    if (!this.getComputedFailCtxIfServletStartFails()) continue;
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        Notification notification;
        String useNamingProperty;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Starting " + this.getBaseName()));
        }
        if (this.getObjectName() != null) {
            Notification notification2 = new Notification("j2ee.state.starting", this.getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification2);
        }
        this.setConfigured(false);
        boolean ok = true;
        if (this.namingResources != null) {
            this.namingResources.start();
        }
        this.postWorkDirectory();
        if (this.getResources() == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Configuring default Resources");
            }
            try {
                this.setResources(new StandardRoot(this));
            }
            catch (IllegalArgumentException e) {
                log.error((Object)sm.getString("standardContext.resourcesInit"), (Throwable)e);
                ok = false;
            }
        }
        if (ok) {
            this.resourcesStart();
        }
        if (this.getLoader() == null) {
            WebappLoader webappLoader = new WebappLoader();
            webappLoader.setDelegate(this.getDelegate());
            this.setLoader(webappLoader);
        }
        if (this.cookieProcessor == null) {
            this.cookieProcessor = new Rfc6265CookieProcessor();
        }
        this.getCharsetMapper();
        boolean dependencyCheck = true;
        try {
            dependencyCheck = ExtensionValidator.validateApplication(this.getResources(), this);
        }
        catch (IOException ioe) {
            log.error((Object)sm.getString("standardContext.extensionValidationError"), (Throwable)ioe);
            dependencyCheck = false;
        }
        if (!dependencyCheck) {
            ok = false;
        }
        if ((useNamingProperty = System.getProperty("catalina.useNaming")) != null && useNamingProperty.equals("false")) {
            this.useNaming = false;
        }
        if (ok && this.isUseNaming() && this.getNamingContextListener() == null) {
            NamingContextListener ncl = new NamingContextListener();
            ncl.setName(this.getNamingContextName());
            ncl.setExceptionOnFailedWrite(this.getJndiExceptionOnFailedWrite());
            this.addLifecycleListener(ncl);
            this.setNamingContextListener(ncl);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Processing standard container startup");
        }
        ClassLoader oldCCL = this.bindThread();
        try {
            if (ok) {
                void var7_20;
                Loader loader = this.getLoader();
                if (loader instanceof Lifecycle) {
                    ((Lifecycle)((Object)loader)).start();
                }
                if (loader.getClassLoader() instanceof WebappClassLoaderBase) {
                    WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase)loader.getClassLoader();
                    webappClassLoaderBase.setClearReferencesRmiTargets(this.getClearReferencesRmiTargets());
                    webappClassLoaderBase.setClearReferencesStopThreads(this.getClearReferencesStopThreads());
                    webappClassLoaderBase.setClearReferencesStopTimerThreads(this.getClearReferencesStopTimerThreads());
                    webappClassLoaderBase.setClearReferencesHttpClientKeepAliveThread(this.getClearReferencesHttpClientKeepAliveThread());
                    webappClassLoaderBase.setClearReferencesObjectStreamClassCaches(this.getClearReferencesObjectStreamClassCaches());
                    webappClassLoaderBase.setClearReferencesThreadLocals(this.getClearReferencesThreadLocals());
                    webappClassLoaderBase.setSkipMemoryLeakChecksOnJvmShutdown(this.getSkipMemoryLeakChecksOnJvmShutdown());
                }
                this.unbindThread(oldCCL);
                oldCCL = this.bindThread();
                this.logger = null;
                this.getLogger();
                Realm realm = this.getRealmInternal();
                if (null != realm) {
                    if (realm instanceof Lifecycle) {
                        ((Lifecycle)((Object)realm)).start();
                    }
                    CredentialHandler credentialHandler = new CredentialHandler(){

                        @Override
                        public boolean matches(String inputCredentials, String storedCredentials) {
                            return StandardContext.this.getRealmInternal().getCredentialHandler().matches(inputCredentials, storedCredentials);
                        }

                        @Override
                        public String mutate(String inputCredentials) {
                            return StandardContext.this.getRealmInternal().getCredentialHandler().mutate(inputCredentials);
                        }
                    };
                    this.context.setAttribute("org.apache.catalina.CredentialHandler", credentialHandler);
                }
                this.fireLifecycleEvent("configure_start", null);
                for (Container child : this.findChildren()) {
                    if (child.getState().isAvailable()) continue;
                    child.start();
                }
                if (this.pipeline instanceof Lifecycle) {
                    ((Lifecycle)((Object)this.pipeline)).start();
                }
                Object var7_17 = null;
                Manager manager = this.getManager();
                if (manager == null) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("standardContext.cluster.noManager", new Object[]{this.getCluster() != null, this.distributable}));
                    }
                    if (this.getCluster() != null && this.distributable) {
                        try {
                            Manager manager2 = this.getCluster().createManager(this.getName());
                        }
                        catch (Exception ex) {
                            log.error((Object)sm.getString("standardContext.cluster.managerError"), (Throwable)ex);
                            ok = false;
                        }
                    } else {
                        StandardManager standardManager = new StandardManager();
                    }
                }
                if (var7_20 != null) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("standardContext.manager", new Object[]{var7_20.getClass().getName()}));
                    }
                    this.setManager((Manager)var7_20);
                }
                if (manager != null && this.getCluster() != null && this.distributable) {
                    this.getCluster().registerManager(manager);
                }
            }
            if (!this.getConfigured()) {
                log.error((Object)sm.getString("standardContext.configurationFail"));
                ok = false;
            }
            if (ok) {
                this.getServletContext().setAttribute("org.apache.catalina.resources", (Object)this.getResources());
                if (this.getInstanceManager() == null) {
                    this.setInstanceManager(this.createInstanceManager());
                }
                this.getServletContext().setAttribute(InstanceManager.class.getName(), (Object)this.getInstanceManager());
                InstanceManagerBindings.bind((ClassLoader)this.getLoader().getClassLoader(), (InstanceManager)this.getInstanceManager());
                this.getServletContext().setAttribute(JarScanner.class.getName(), (Object)this.getJarScanner());
                this.getServletContext().setAttribute("org.apache.catalina.webappVersion", (Object)this.getWebappVersion());
                if (!Globals.IS_SECURITY_ENABLED) {
                    this.getServletContext().setAttribute(ScheduledThreadPoolExecutor.class.getName(), (Object)Container.getService(this).getServer().getUtilityExecutor());
                }
            }
            this.mergeParameters();
            for (Map.Entry entry : this.initializers.entrySet()) {
                try {
                    ((ServletContainerInitializer)entry.getKey()).onStartup((Set)entry.getValue(), this.getServletContext());
                }
                catch (ServletException servletException) {
                    log.error((Object)sm.getString("standardContext.sciFail"), (Throwable)servletException);
                    ok = false;
                    break;
                }
            }
            if (ok && !this.listenerStart()) {
                log.error((Object)sm.getString("standardContext.listenerFail"));
                ok = false;
            }
            if (ok) {
                this.checkConstraintsForUncoveredMethods(this.findConstraints());
            }
            try {
                Manager manager = this.getManager();
                if (manager instanceof Lifecycle) {
                    ((Lifecycle)((Object)manager)).start();
                }
            }
            catch (Exception e) {
                log.error((Object)sm.getString("standardContext.managerFail"), (Throwable)e);
                ok = false;
            }
            if (ok && !this.filterStart()) {
                log.error((Object)sm.getString("standardContext.filterFail"));
                ok = false;
            }
            if (ok && !this.loadOnStartup(this.findChildren())) {
                log.error((Object)sm.getString("standardContext.servletFail"));
                ok = false;
            }
            super.threadStart();
        }
        finally {
            this.unbindThread(oldCCL);
        }
        if (ok) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Starting completed");
            }
        } else {
            log.error((Object)sm.getString("standardContext.startFailed", new Object[]{this.getName()}));
        }
        this.startTime = System.currentTimeMillis();
        if (ok && this.getObjectName() != null) {
            notification = new Notification("j2ee.state.running", this.getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification);
        }
        this.getResources().gc();
        if (!ok) {
            this.setState(LifecycleState.FAILED);
            if (this.getObjectName() != null) {
                notification = new Notification("j2ee.object.failed", this.getObjectName(), this.sequenceNumber.getAndIncrement());
                this.broadcaster.sendNotification(notification);
            }
        } else {
            this.setState(LifecycleState.STARTING);
        }
    }

    private void checkConstraintsForUncoveredMethods(SecurityConstraint[] constraints) {
        SecurityConstraint[] newConstraints;
        for (SecurityConstraint constraint : newConstraints = SecurityConstraint.findUncoveredHttpMethods((SecurityConstraint[])constraints, (boolean)this.getDenyUncoveredHttpMethods(), (Log)this.getLogger())) {
            this.addConstraint(constraint);
        }
    }

    @Override
    public InstanceManager createInstanceManager() {
        javax.naming.Context context = null;
        if (this.isUseNaming() && this.getNamingContextListener() != null) {
            context = this.getNamingContextListener().getEnvContext();
        }
        Map<String, Map<String, String>> injectionMap = this.buildInjectionMap(this.getIgnoreAnnotations() ? new NamingResourcesImpl() : this.getNamingResources());
        return new DefaultInstanceManager(context, injectionMap, this, this.getClass().getClassLoader());
    }

    private Map<String, Map<String, String>> buildInjectionMap(NamingResourcesImpl namingResources) {
        HashMap<String, Map<String, String>> injectionMap = new HashMap<String, Map<String, String>>();
        for (ContextLocalEjb contextLocalEjb : namingResources.findLocalEjbs()) {
            this.addInjectionTarget((Injectable)contextLocalEjb, injectionMap);
        }
        for (ContextLocalEjb contextLocalEjb : namingResources.findEjbs()) {
            this.addInjectionTarget((Injectable)contextLocalEjb, injectionMap);
        }
        for (ContextLocalEjb contextLocalEjb : namingResources.findEnvironments()) {
            this.addInjectionTarget((Injectable)contextLocalEjb, injectionMap);
        }
        for (ContextLocalEjb contextLocalEjb : namingResources.findMessageDestinationRefs()) {
            this.addInjectionTarget((Injectable)contextLocalEjb, injectionMap);
        }
        for (ContextLocalEjb contextLocalEjb : namingResources.findResourceEnvRefs()) {
            this.addInjectionTarget((Injectable)contextLocalEjb, injectionMap);
        }
        for (ContextLocalEjb contextLocalEjb : namingResources.findResources()) {
            this.addInjectionTarget((Injectable)contextLocalEjb, injectionMap);
        }
        for (ContextLocalEjb contextLocalEjb : namingResources.findServices()) {
            this.addInjectionTarget((Injectable)contextLocalEjb, injectionMap);
        }
        return injectionMap;
    }

    private void addInjectionTarget(Injectable resource, Map<String, Map<String, String>> injectionMap) {
        List injectionTargets = resource.getInjectionTargets();
        if (injectionTargets != null && injectionTargets.size() > 0) {
            String jndiName = resource.getName();
            for (InjectionTarget injectionTarget : injectionTargets) {
                String clazz = injectionTarget.getTargetClass();
                injectionMap.computeIfAbsent(clazz, k -> new HashMap()).put(injectionTarget.getTargetName(), jndiName);
            }
        }
    }

    private void mergeParameters() {
        ApplicationParameter[] params;
        String[] names;
        HashMap<String, String> mergedParams = new HashMap<String, String>();
        for (String s : names = this.findParameters()) {
            mergedParams.put(s, this.findParameter(s));
        }
        for (ApplicationParameter param : params = this.findApplicationParameters()) {
            if (param.getOverride()) {
                mergedParams.computeIfAbsent(param.getName(), k -> param.getValue());
                continue;
            }
            mergedParams.put(param.getName(), param.getValue());
        }
        ServletContext sc = this.getServletContext();
        for (Map.Entry entry : mergedParams.entrySet()) {
            sc.setInitParameter((String)entry.getKey(), (String)entry.getValue());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        if (this.getObjectName() != null) {
            Notification notification = new Notification("j2ee.state.stopping", this.getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification);
        }
        long limit = System.currentTimeMillis() + this.unloadDelay;
        while (this.inProgressAsyncCount.get() > 0L && System.currentTimeMillis() < limit) {
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException e) {
                log.info((Object)sm.getString("standardContext.stop.asyncWaitInterrupted"), (Throwable)e);
                break;
            }
        }
        this.setState(LifecycleState.STOPPING);
        ClassLoader oldCCL = this.bindThread();
        try {
            Loader loader;
            Realm realm;
            Container[] children = this.findChildren();
            this.threadStop();
            for (Container child : children) {
                child.stop();
            }
            this.filterStop();
            Manager manager = this.getManager();
            if (manager instanceof Lifecycle && ((Lifecycle)((Object)manager)).getState().isAvailable()) {
                ((Lifecycle)((Object)manager)).stop();
            }
            this.listenerStop();
            this.setCharsetMapper(null);
            if (log.isDebugEnabled()) {
                log.debug((Object)"Processing standard container shutdown");
            }
            if (this.namingResources != null) {
                this.namingResources.stop();
            }
            this.fireLifecycleEvent("configure_stop", null);
            if (this.pipeline instanceof Lifecycle && ((Lifecycle)((Object)this.pipeline)).getState().isAvailable()) {
                ((Lifecycle)((Object)this.pipeline)).stop();
            }
            if (this.context != null) {
                this.context.clearAttributes();
            }
            if ((realm = this.getRealmInternal()) instanceof Lifecycle) {
                ((Lifecycle)((Object)realm)).stop();
            }
            if ((loader = this.getLoader()) instanceof Lifecycle) {
                ClassLoader classLoader = loader.getClassLoader();
                ((Lifecycle)((Object)loader)).stop();
                if (classLoader != null) {
                    InstanceManagerBindings.unbind((ClassLoader)classLoader);
                }
            }
            this.resourcesStop();
        }
        finally {
            this.unbindThread(oldCCL);
        }
        if (this.getObjectName() != null) {
            Notification notification = new Notification("j2ee.state.stopped", this.getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification);
        }
        this.context = null;
        try {
            this.resetContext();
        }
        catch (Exception ex) {
            log.error((Object)("Error resetting context " + this + " " + ex), (Throwable)ex);
        }
        this.setInstanceManager(null);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Stopping complete");
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        Manager manager;
        Loader loader;
        if (this.getObjectName() != null) {
            Notification notification = new Notification("j2ee.object.deleted", this.getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification);
        }
        if (this.namingResources != null) {
            this.namingResources.destroy();
        }
        if ((loader = this.getLoader()) instanceof Lifecycle) {
            ((Lifecycle)((Object)loader)).destroy();
        }
        if ((manager = this.getManager()) instanceof Lifecycle) {
            ((Lifecycle)((Object)manager)).destroy();
        }
        if (this.resources != null) {
            this.resources.destroy();
        }
        super.destroyInternal();
    }

    @Override
    public void backgroundProcess() {
        InstanceManager instanceManager;
        WebResourceRoot resources;
        Manager manager;
        if (!this.getState().isAvailable()) {
            return;
        }
        Loader loader = this.getLoader();
        if (loader != null) {
            try {
                loader.backgroundProcess();
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("standardContext.backgroundProcess.loader", new Object[]{loader}), (Throwable)e);
            }
        }
        if ((manager = this.getManager()) != null) {
            try {
                manager.backgroundProcess();
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("standardContext.backgroundProcess.manager", new Object[]{manager}), (Throwable)e);
            }
        }
        if ((resources = this.getResources()) != null) {
            try {
                resources.backgroundProcess();
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("standardContext.backgroundProcess.resources", new Object[]{resources}), (Throwable)e);
            }
        }
        if ((instanceManager = this.getInstanceManager()) != null) {
            try {
                instanceManager.backgroundProcess();
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("standardContext.backgroundProcess.instanceManager", new Object[]{resources}), (Throwable)e);
            }
        }
        super.backgroundProcess();
    }

    private void resetContext() throws Exception {
        for (Container child : this.findChildren()) {
            this.removeChild(child);
        }
        this.startupTime = 0L;
        this.startTime = 0L;
        this.tldScanTime = 0L;
        this.distributable = false;
        this.applicationListeners.clear();
        this.applicationEventListenersList.clear();
        this.applicationLifecycleListenersObjects = new Object[0];
        this.jspConfigDescriptor = null;
        this.initializers.clear();
        this.createdServlets.clear();
        this.postConstructMethods.clear();
        this.preDestroyMethods.clear();
        if (log.isDebugEnabled()) {
            log.debug((Object)("resetContext " + this.getObjectName()));
        }
    }

    protected String adjustURLPattern(String urlPattern) {
        if (urlPattern == null) {
            return urlPattern;
        }
        if (urlPattern.startsWith("/") || urlPattern.startsWith("*.")) {
            return urlPattern;
        }
        if (!this.isServlet22()) {
            return urlPattern;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("standardContext.urlPattern.patternWarning", new Object[]{urlPattern}));
        }
        return "/" + urlPattern;
    }

    @Override
    public boolean isServlet22() {
        return "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN".equals(this.publicId);
    }

    @Override
    public Set<String> addServletSecurity(ServletRegistration.Dynamic registration, ServletSecurityElement servletSecurityElement) {
        HashSet<String> conflicts = new HashSet<String>();
        Collection urlPatterns = registration.getMappings();
        for (String urlPattern : urlPatterns) {
            SecurityConstraint[] newSecurityConstraints;
            SecurityConstraint[] securityConstraints;
            boolean foundConflict = false;
            for (SecurityConstraint securityConstraint : securityConstraints = this.findConstraints()) {
                SecurityCollection[] collections;
                for (SecurityCollection collection : collections = securityConstraint.findCollections()) {
                    if (!collection.findPattern(urlPattern)) continue;
                    if (collection.isFromDescriptor()) {
                        foundConflict = true;
                        conflicts.add(urlPattern);
                        break;
                    }
                    collection.removePattern(urlPattern);
                    if (collection.findPatterns().length != 0) continue;
                    securityConstraint.removeCollection(collection);
                }
                if (securityConstraint.findCollections().length == 0) {
                    this.removeConstraint(securityConstraint);
                }
                if (foundConflict) break;
            }
            if (foundConflict) continue;
            for (SecurityConstraint securityConstraint : newSecurityConstraints = SecurityConstraint.createConstraints((ServletSecurityElement)servletSecurityElement, (String)urlPattern)) {
                this.addConstraint(securityConstraint);
            }
        }
        return conflicts;
    }

    protected ClassLoader bindThread() {
        ClassLoader oldContextClassLoader = this.bind(false, null);
        if (this.isUseNaming()) {
            try {
                ContextBindings.bindThread(this, this.getNamingToken());
            }
            catch (NamingException namingException) {
                // empty catch block
            }
        }
        return oldContextClassLoader;
    }

    protected void unbindThread(ClassLoader oldContextClassLoader) {
        if (this.isUseNaming()) {
            ContextBindings.unbindThread(this, this.getNamingToken());
        }
        this.unbind(false, oldContextClassLoader);
    }

    public ClassLoader bind(boolean usePrivilegedAction, ClassLoader originalClassLoader) {
        Loader loader = this.getLoader();
        ClassLoader webApplicationClassLoader = null;
        if (loader != null) {
            webApplicationClassLoader = loader.getClassLoader();
        }
        Thread currentThread = Thread.currentThread();
        if (originalClassLoader == null) {
            if (usePrivilegedAction) {
                PrivilegedGetTccl pa = new PrivilegedGetTccl(currentThread);
                originalClassLoader = (ClassLoader)AccessController.doPrivileged(pa);
            } else {
                originalClassLoader = currentThread.getContextClassLoader();
            }
        }
        if (webApplicationClassLoader == null || webApplicationClassLoader == originalClassLoader) {
            return null;
        }
        ThreadBindingListener threadBindingListener = this.getThreadBindingListener();
        if (usePrivilegedAction) {
            PrivilegedSetTccl pa = new PrivilegedSetTccl(currentThread, webApplicationClassLoader);
            AccessController.doPrivileged(pa);
        } else {
            currentThread.setContextClassLoader(webApplicationClassLoader);
        }
        if (threadBindingListener != null) {
            try {
                threadBindingListener.bind();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                log.error((Object)sm.getString("standardContext.threadBindingListenerError", new Object[]{this.getName()}), t);
            }
        }
        return originalClassLoader;
    }

    public void unbind(boolean usePrivilegedAction, ClassLoader originalClassLoader) {
        if (originalClassLoader == null) {
            return;
        }
        if (this.threadBindingListener != null) {
            try {
                this.threadBindingListener.unbind();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                log.error((Object)sm.getString("standardContext.threadBindingListenerError", new Object[]{this.getName()}), t);
            }
        }
        Thread currentThread = Thread.currentThread();
        if (usePrivilegedAction) {
            PrivilegedSetTccl pa = new PrivilegedSetTccl(currentThread, originalClassLoader);
            AccessController.doPrivileged(pa);
        } else {
            currentThread.setContextClassLoader(originalClassLoader);
        }
    }

    private String getNamingContextName() {
        if (this.namingContextName == null) {
            Container parent = this.getParent();
            if (parent == null) {
                this.namingContextName = this.getName();
            } else {
                ArrayDeque<String> stk = new ArrayDeque<String>();
                StringBuilder buff = new StringBuilder();
                while (parent != null) {
                    stk.addFirst(parent.getName());
                    parent = parent.getParent();
                }
                while (!stk.isEmpty()) {
                    buff.append('/').append((String)stk.remove());
                }
                buff.append(this.getName());
                this.namingContextName = buff.toString();
            }
        }
        return this.namingContextName;
    }

    public NamingContextListener getNamingContextListener() {
        return this.namingContextListener;
    }

    public void setNamingContextListener(NamingContextListener namingContextListener) {
        this.namingContextListener = namingContextListener;
    }

    @Override
    public boolean getPaused() {
        return this.paused;
    }

    @Override
    public boolean fireRequestInitEvent(ServletRequest request) {
        Object[] instances = this.getApplicationEventListeners();
        if (instances != null && instances.length > 0) {
            ServletRequestEvent event = new ServletRequestEvent(this.getServletContext(), request);
            for (Object instance : instances) {
                if (instance == null || !(instance instanceof ServletRequestListener)) continue;
                ServletRequestListener listener = (ServletRequestListener)instance;
                try {
                    listener.requestInitialized(event);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.getLogger().error((Object)sm.getString("standardContext.requestListener.requestInit", new Object[]{instance.getClass().getName()}), t);
                    request.setAttribute("javax.servlet.error.exception", (Object)t);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean fireRequestDestroyEvent(ServletRequest request) {
        Object[] instances = this.getApplicationEventListeners();
        if (instances != null && instances.length > 0) {
            ServletRequestEvent event = new ServletRequestEvent(this.getServletContext(), request);
            for (int i = 0; i < instances.length; ++i) {
                int j = instances.length - 1 - i;
                if (instances[j] == null || !(instances[j] instanceof ServletRequestListener)) continue;
                ServletRequestListener listener = (ServletRequestListener)instances[j];
                try {
                    listener.requestDestroyed(event);
                    continue;
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.getLogger().error((Object)sm.getString("standardContext.requestListener.requestDestroyed", new Object[]{instances[j].getClass().getName()}), t);
                    request.setAttribute("javax.servlet.error.exception", (Object)t);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void addPostConstructMethod(String clazz, String method) {
        if (clazz == null || method == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.postconstruct.required"));
        }
        if (this.postConstructMethods.get(clazz) != null) {
            throw new IllegalArgumentException(sm.getString("standardContext.postconstruct.duplicate", new Object[]{clazz}));
        }
        this.postConstructMethods.put(clazz, method);
        this.fireContainerEvent("addPostConstructMethod", clazz);
    }

    @Override
    public void removePostConstructMethod(String clazz) {
        this.postConstructMethods.remove(clazz);
        this.fireContainerEvent("removePostConstructMethod", clazz);
    }

    @Override
    public void addPreDestroyMethod(String clazz, String method) {
        if (clazz == null || method == null) {
            throw new IllegalArgumentException(sm.getString("standardContext.predestroy.required"));
        }
        if (this.preDestroyMethods.get(clazz) != null) {
            throw new IllegalArgumentException(sm.getString("standardContext.predestroy.duplicate", new Object[]{clazz}));
        }
        this.preDestroyMethods.put(clazz, method);
        this.fireContainerEvent("addPreDestroyMethod", clazz);
    }

    @Override
    public void removePreDestroyMethod(String clazz) {
        this.preDestroyMethods.remove(clazz);
        this.fireContainerEvent("removePreDestroyMethod", clazz);
    }

    @Override
    public String findPostConstructMethod(String clazz) {
        return this.postConstructMethods.get(clazz);
    }

    @Override
    public String findPreDestroyMethod(String clazz) {
        return this.preDestroyMethods.get(clazz);
    }

    @Override
    public Map<String, String> findPostConstructMethods() {
        return this.postConstructMethods;
    }

    @Override
    public Map<String, String> findPreDestroyMethods() {
        return this.preDestroyMethods;
    }

    protected void postWorkDirectory() {
        File dir;
        String workDir = this.getWorkDir();
        if (workDir == null || workDir.length() == 0) {
            String temp;
            String hostName = null;
            String engineName = null;
            String hostWorkDir = null;
            Container parentHost = this.getParent();
            if (parentHost != null) {
                Container parentEngine;
                hostName = parentHost.getName();
                if (parentHost instanceof StandardHost) {
                    hostWorkDir = ((StandardHost)parentHost).getWorkDir();
                }
                if ((parentEngine = parentHost.getParent()) != null) {
                    engineName = parentEngine.getName();
                }
            }
            if (hostName == null || hostName.length() < 1) {
                hostName = "_";
            }
            if (engineName == null || engineName.length() < 1) {
                engineName = "_";
            }
            if ((temp = this.getBaseName()).startsWith("/")) {
                temp = temp.substring(1);
            }
            temp = temp.replace('/', '_');
            if ((temp = temp.replace('\\', '_')).length() < 1) {
                temp = "ROOT";
            }
            workDir = hostWorkDir != null ? hostWorkDir + File.separator + temp : "work" + File.separator + engineName + File.separator + hostName + File.separator + temp;
            this.setWorkDir(workDir);
        }
        if (!(dir = new File(workDir)).isAbsolute()) {
            String catalinaHomePath = null;
            try {
                catalinaHomePath = this.getCatalinaBase().getCanonicalPath();
                dir = new File(catalinaHomePath, workDir);
            }
            catch (IOException e) {
                log.warn((Object)sm.getString("standardContext.workCreateException", new Object[]{workDir, catalinaHomePath, this.getName()}), (Throwable)e);
            }
        }
        if (!dir.mkdirs() && !dir.isDirectory()) {
            log.warn((Object)sm.getString("standardContext.workCreateFail", new Object[]{dir, this.getName()}));
        }
        if (this.context == null) {
            this.getServletContext();
        }
        this.context.setAttribute("javax.servlet.context.tempdir", dir);
        this.context.setAttributeReadOnly("javax.servlet.context.tempdir");
    }

    private void setPaused(boolean paused) {
        this.paused = paused;
    }

    private boolean validateURLPattern(String urlPattern) {
        if (urlPattern == null) {
            return false;
        }
        if (urlPattern.indexOf(10) >= 0 || urlPattern.indexOf(13) >= 0) {
            return false;
        }
        if (urlPattern.equals("")) {
            return true;
        }
        if (urlPattern.startsWith("*.")) {
            if (urlPattern.indexOf(47) < 0) {
                this.checkUnusualURLPattern(urlPattern);
                return true;
            }
            return false;
        }
        if (urlPattern.startsWith("/") && !urlPattern.contains("*.")) {
            this.checkUnusualURLPattern(urlPattern);
            return true;
        }
        return false;
    }

    private void checkUnusualURLPattern(String urlPattern) {
        if (log.isInfoEnabled() && (urlPattern.endsWith("*") && (urlPattern.length() < 2 || urlPattern.charAt(urlPattern.length() - 2) != '/') || urlPattern.startsWith("*.") && urlPattern.length() > 2 && urlPattern.lastIndexOf(46) > 1)) {
            log.info((Object)sm.getString("standardContext.suspiciousUrl", new Object[]{urlPattern, this.getName()}));
        }
    }

    @Override
    protected String getObjectNameKeyProperties() {
        StringBuilder keyProperties = new StringBuilder("j2eeType=WebModule,");
        keyProperties.append(this.getObjectKeyPropertiesNameOnly());
        keyProperties.append(",J2EEApplication=");
        keyProperties.append(this.getJ2EEApplication());
        keyProperties.append(",J2EEServer=");
        keyProperties.append(this.getJ2EEServer());
        return keyProperties.toString();
    }

    private String getObjectKeyPropertiesNameOnly() {
        StringBuilder result = new StringBuilder("name=//");
        String hostname = this.getParent().getName();
        if (hostname == null) {
            result.append("DEFAULT");
        } else {
            result.append(hostname);
        }
        String contextName = this.getName();
        if (!contextName.startsWith("/")) {
            result.append('/');
        }
        result.append(contextName);
        return result.toString();
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.namingResources != null) {
            this.namingResources.init();
        }
        if (this.getObjectName() != null) {
            Notification notification = new Notification("j2ee.object.created", this.getObjectName(), this.sequenceNumber.getAndIncrement());
            this.broadcaster.sendNotification(notification);
        }
    }

    @Override
    public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object object) throws ListenerNotFoundException {
        this.broadcaster.removeNotificationListener(listener, filter, object);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        if (this.notificationInfo == null) {
            this.notificationInfo = new MBeanNotificationInfo[]{new MBeanNotificationInfo(new String[]{"j2ee.object.created"}, Notification.class.getName(), "web application is created"), new MBeanNotificationInfo(new String[]{"j2ee.state.starting"}, Notification.class.getName(), "change web application is starting"), new MBeanNotificationInfo(new String[]{"j2ee.state.running"}, Notification.class.getName(), "web application is running"), new MBeanNotificationInfo(new String[]{"j2ee.state.stopping"}, Notification.class.getName(), "web application start to stopped"), new MBeanNotificationInfo(new String[]{"j2ee.object.stopped"}, Notification.class.getName(), "web application is stopped"), new MBeanNotificationInfo(new String[]{"j2ee.object.deleted"}, Notification.class.getName(), "web application is deleted"), new MBeanNotificationInfo(new String[]{"j2ee.object.failed"}, Notification.class.getName(), "web application failed")};
        }
        return this.notificationInfo;
    }

    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object object) throws IllegalArgumentException {
        this.broadcaster.addNotificationListener(listener, filter, object);
    }

    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        this.broadcaster.removeNotificationListener(listener);
    }

    public String[] getWelcomeFiles() {
        return this.findWelcomeFiles();
    }

    @Override
    public boolean getXmlNamespaceAware() {
        return this.webXmlNamespaceAware;
    }

    @Override
    public void setXmlNamespaceAware(boolean webXmlNamespaceAware) {
        this.webXmlNamespaceAware = webXmlNamespaceAware;
    }

    @Override
    public void setXmlValidation(boolean webXmlValidation) {
        this.webXmlValidation = webXmlValidation;
    }

    @Override
    public boolean getXmlValidation() {
        return this.webXmlValidation;
    }

    @Override
    public void setXmlBlockExternal(boolean xmlBlockExternal) {
        this.xmlBlockExternal = xmlBlockExternal;
    }

    @Override
    public boolean getXmlBlockExternal() {
        return this.xmlBlockExternal;
    }

    @Override
    public void setTldValidation(boolean tldValidation) {
        this.tldValidation = tldValidation;
    }

    @Override
    public boolean getTldValidation() {
        return this.tldValidation;
    }

    public String getServer() {
        return this.server;
    }

    public String setServer(String server) {
        this.server = server;
        return this.server;
    }

    @Deprecated
    public String[] getJavaVMs() {
        return this.javaVMs;
    }

    @Deprecated
    public String[] setJavaVMs(String[] javaVMs) {
        this.javaVMs = javaVMs;
        return javaVMs;
    }

    public long getStartTime() {
        return this.startTime;
    }

    private static class NoPluggabilityServletContext
    implements ServletContext {
        private final ServletContext sc;

        NoPluggabilityServletContext(ServletContext sc) {
            this.sc = sc;
        }

        public String getContextPath() {
            return this.sc.getContextPath();
        }

        public ServletContext getContext(String uripath) {
            return this.sc.getContext(uripath);
        }

        public int getMajorVersion() {
            return this.sc.getMajorVersion();
        }

        public int getMinorVersion() {
            return this.sc.getMinorVersion();
        }

        public int getEffectiveMajorVersion() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public int getEffectiveMinorVersion() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public String getMimeType(String file) {
            return this.sc.getMimeType(file);
        }

        public Set<String> getResourcePaths(String path) {
            return this.sc.getResourcePaths(path);
        }

        public URL getResource(String path) throws MalformedURLException {
            return this.sc.getResource(path);
        }

        public InputStream getResourceAsStream(String path) {
            return this.sc.getResourceAsStream(path);
        }

        public RequestDispatcher getRequestDispatcher(String path) {
            return this.sc.getRequestDispatcher(path);
        }

        public RequestDispatcher getNamedDispatcher(String name) {
            return this.sc.getNamedDispatcher(name);
        }

        @Deprecated
        public Servlet getServlet(String name) throws ServletException {
            return this.sc.getServlet(name);
        }

        @Deprecated
        public Enumeration<Servlet> getServlets() {
            return this.sc.getServlets();
        }

        @Deprecated
        public Enumeration<String> getServletNames() {
            return this.sc.getServletNames();
        }

        public void log(String msg) {
            this.sc.log(msg);
        }

        @Deprecated
        public void log(Exception exception, String msg) {
            this.sc.log(exception, msg);
        }

        public void log(String message, Throwable throwable) {
            this.sc.log(message, throwable);
        }

        public String getRealPath(String path) {
            return this.sc.getRealPath(path);
        }

        public String getServerInfo() {
            return this.sc.getServerInfo();
        }

        public String getInitParameter(String name) {
            return this.sc.getInitParameter(name);
        }

        public Enumeration<String> getInitParameterNames() {
            return this.sc.getInitParameterNames();
        }

        public boolean setInitParameter(String name, String value) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public Object getAttribute(String name) {
            return this.sc.getAttribute(name);
        }

        public Enumeration<String> getAttributeNames() {
            return this.sc.getAttributeNames();
        }

        public void setAttribute(String name, Object object) {
            this.sc.setAttribute(name, object);
        }

        public void removeAttribute(String name) {
            this.sc.removeAttribute(name);
        }

        public String getServletContextName() {
            return this.sc.getServletContextName();
        }

        public ServletRegistration.Dynamic addServlet(String servletName, String className) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public ServletRegistration.Dynamic addJspFile(String jspName, String jspFile) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public ServletRegistration getServletRegistration(String servletName) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public Map<String, ? extends ServletRegistration> getServletRegistrations() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public FilterRegistration.Dynamic addFilter(String filterName, String className) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public FilterRegistration getFilterRegistration(String filterName) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public SessionCookieConfig getSessionCookieConfig() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public void addListener(String className) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public <T extends EventListener> void addListener(T t) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public void addListener(Class<? extends EventListener> listenerClass) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public JspConfigDescriptor getJspConfigDescriptor() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public ClassLoader getClassLoader() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public void declareRoles(String ... roleNames) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public String getVirtualServerName() {
            return this.sc.getVirtualServerName();
        }

        public int getSessionTimeout() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public void setSessionTimeout(int sessionTimeout) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public String getRequestCharacterEncoding() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public void setRequestCharacterEncoding(String encoding) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public String getResponseCharacterEncoding() {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }

        public void setResponseCharacterEncoding(String encoding) {
            throw new UnsupportedOperationException(ContainerBase.sm.getString("noPluggabilityServletContext.notAllowed"));
        }
    }

    private static final class ContextFilterMaps {
        private final Object lock = new Object();
        private FilterMap[] array = new FilterMap[0];
        private int insertPoint = 0;

        private ContextFilterMaps() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public FilterMap[] asArray() {
            Object object = this.lock;
            synchronized (object) {
                return this.array;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void add(FilterMap filterMap) {
            Object object = this.lock;
            synchronized (object) {
                FilterMap[] results = Arrays.copyOf(this.array, this.array.length + 1);
                results[this.array.length] = filterMap;
                this.array = results;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addBefore(FilterMap filterMap) {
            Object object = this.lock;
            synchronized (object) {
                FilterMap[] results = new FilterMap[this.array.length + 1];
                System.arraycopy(this.array, 0, results, 0, this.insertPoint);
                System.arraycopy(this.array, this.insertPoint, results, this.insertPoint + 1, this.array.length - this.insertPoint);
                results[this.insertPoint] = filterMap;
                this.array = results;
                ++this.insertPoint;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void remove(FilterMap filterMap) {
            Object object = this.lock;
            synchronized (object) {
                int n = -1;
                for (int i = 0; i < this.array.length; ++i) {
                    if (this.array[i] != filterMap) continue;
                    n = i;
                    break;
                }
                if (n < 0) {
                    return;
                }
                FilterMap[] results = new FilterMap[this.array.length - 1];
                System.arraycopy(this.array, 0, results, 0, n);
                System.arraycopy(this.array, n + 1, results, n, this.array.length - 1 - n);
                this.array = results;
                if (n < this.insertPoint) {
                    --this.insertPoint;
                }
            }
        }
    }
}

