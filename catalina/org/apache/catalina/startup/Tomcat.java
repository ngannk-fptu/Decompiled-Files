/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.SingleThreadModel
 *  javax.servlet.annotation.WebServlet
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.UriUtil
 *  org.apache.tomcat.util.compat.JreCompat
 *  org.apache.tomcat.util.descriptor.web.LoginConfig
 *  org.apache.tomcat.util.file.ConfigFileLoader
 *  org.apache.tomcat.util.file.ConfigurationSource
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.annotation.WebServlet;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Wrapper;
import org.apache.catalina.authenticator.NonLoginAuthenticator;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.NamingContextListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.catalina.security.SecurityClassLoad;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.startup.CatalinaBaseConfigurationSource;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.WebAnnotationSet;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.IOTools;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class Tomcat {
    private static final StringManager sm = StringManager.getManager(Tomcat.class);
    private final Map<String, Logger> pinnedLoggers = new HashMap<String, Logger>();
    protected Server server;
    protected int port = 8080;
    protected String hostname = "localhost";
    protected String basedir;
    private final Map<String, String> userPass = new HashMap<String, String>();
    private final Map<String, List<String>> userRoles = new HashMap<String, List<String>>();
    private final Map<String, Principal> userPrincipals = new HashMap<String, Principal>();
    private boolean addDefaultWebXmlToWebapp = true;
    static final String[] silences = new String[]{"org.apache.coyote.http11.Http11NioProtocol", "org.apache.catalina.core.StandardService", "org.apache.catalina.core.StandardEngine", "org.apache.catalina.startup.ContextConfig", "org.apache.catalina.core.ApplicationContext", "org.apache.catalina.core.AprLifecycleListener"};
    private boolean silent = false;

    public Tomcat() {
        ExceptionUtils.preload();
    }

    public void setBaseDir(String basedir) {
        this.basedir = basedir;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHostname(String s) {
        this.hostname = s;
    }

    public Context addWebapp(String contextPath, String docBase) {
        return this.addWebapp(this.getHost(), contextPath, docBase);
    }

    public Context addWebapp(String contextPath, URL source) throws IOException {
        ContextName cn = new ContextName(contextPath, null);
        Host h = this.getHost();
        if (h.findChild(cn.getName()) != null) {
            throw new IllegalArgumentException(sm.getString("tomcat.addWebapp.conflictChild", new Object[]{source, contextPath, cn.getName()}));
        }
        File targetWar = new File(h.getAppBaseFile(), cn.getBaseName() + ".war");
        File targetDir = new File(h.getAppBaseFile(), cn.getBaseName());
        if (targetWar.exists()) {
            throw new IllegalArgumentException(sm.getString("tomcat.addWebapp.conflictFile", new Object[]{source, contextPath, targetWar.getAbsolutePath()}));
        }
        if (targetDir.exists()) {
            throw new IllegalArgumentException(sm.getString("tomcat.addWebapp.conflictFile", new Object[]{source, contextPath, targetDir.getAbsolutePath()}));
        }
        URLConnection uConn = source.openConnection();
        try (InputStream is = uConn.getInputStream();
             FileOutputStream os = new FileOutputStream(targetWar);){
            IOTools.flow(is, os);
        }
        return this.addWebapp(contextPath, targetWar.getAbsolutePath());
    }

    public Context addContext(String contextPath, String docBase) {
        return this.addContext(this.getHost(), contextPath, docBase);
    }

    public Wrapper addServlet(String contextPath, String servletName, String servletClass) {
        Container ctx = this.getHost().findChild(contextPath);
        return Tomcat.addServlet((Context)ctx, servletName, servletClass);
    }

    public static Wrapper addServlet(Context ctx, String servletName, String servletClass) {
        Wrapper sw = ctx.createWrapper();
        if (sw == null) {
            return null;
        }
        sw.setServletClass(servletClass);
        sw.setName(servletName);
        ctx.addChild(sw);
        return sw;
    }

    public Wrapper addServlet(String contextPath, String servletName, Servlet servlet) {
        Container ctx = this.getHost().findChild(contextPath);
        return Tomcat.addServlet((Context)ctx, servletName, servlet);
    }

    public static Wrapper addServlet(Context ctx, String servletName, Servlet servlet) {
        ExistingStandardWrapper sw = new ExistingStandardWrapper(servlet);
        sw.setName(servletName);
        ctx.addChild(sw);
        return sw;
    }

    public void init(ConfigurationSource source) {
        this.init(source, null);
    }

    public void init(ConfigurationSource source, String[] catalinaArguments) {
        ConfigFileLoader.setSource((ConfigurationSource)source);
        this.addDefaultWebXmlToWebapp = false;
        Catalina catalina = new Catalina();
        if (catalinaArguments == null) {
            catalina.load();
        } else {
            catalina.load(catalinaArguments);
        }
        this.server = catalina.getServer();
    }

    public void init() throws LifecycleException {
        this.getServer();
        this.server.init();
    }

    public void start() throws LifecycleException {
        this.getServer();
        this.server.start();
    }

    public void stop() throws LifecycleException {
        this.getServer();
        this.server.stop();
    }

    public void destroy() throws LifecycleException {
        this.getServer();
        this.server.destroy();
    }

    public void addUser(String user, String pass) {
        this.userPass.put(user, pass);
    }

    public void addRole(String user, String role) {
        this.userRoles.computeIfAbsent(user, k -> new ArrayList()).add(role);
    }

    public Connector getConnector() {
        Service service = this.getService();
        if (service.findConnectors().length > 0) {
            return service.findConnectors()[0];
        }
        Connector connector = new Connector("HTTP/1.1");
        connector.setPort(this.port);
        service.addConnector(connector);
        return connector;
    }

    public void setConnector(Connector connector) {
        Service service = this.getService();
        boolean found = false;
        for (Connector serviceConnector : service.findConnectors()) {
            if (connector != serviceConnector) continue;
            found = true;
            break;
        }
        if (!found) {
            service.addConnector(connector);
        }
    }

    public Service getService() {
        return this.getServer().findServices()[0];
    }

    public void setHost(Host host) {
        Engine engine = this.getEngine();
        boolean found = false;
        for (Container engineHost : engine.findChildren()) {
            if (engineHost != host) continue;
            found = true;
            break;
        }
        if (!found) {
            engine.addChild(host);
        }
    }

    public Host getHost() {
        Engine engine = this.getEngine();
        if (engine.findChildren().length > 0) {
            return (Host)engine.findChildren()[0];
        }
        StandardHost host = new StandardHost();
        host.setName(this.hostname);
        this.getEngine().addChild(host);
        return host;
    }

    public Engine getEngine() {
        Service service = this.getServer().findServices()[0];
        if (service.getContainer() != null) {
            return service.getContainer();
        }
        StandardEngine engine = new StandardEngine();
        engine.setName("Tomcat");
        engine.setDefaultHost(this.hostname);
        engine.setRealm(this.createDefaultRealm());
        service.setContainer(engine);
        return engine;
    }

    public Server getServer() {
        if (this.server != null) {
            return this.server;
        }
        System.setProperty("catalina.useNaming", "false");
        this.server = new StandardServer();
        this.initBaseDir();
        ConfigFileLoader.setSource((ConfigurationSource)new CatalinaBaseConfigurationSource(new File(this.basedir), null));
        this.server.setPort(-1);
        StandardService service = new StandardService();
        service.setName("Tomcat");
        this.server.addService(service);
        return this.server;
    }

    public Context addContext(Host host, String contextPath, String dir) {
        return this.addContext(host, contextPath, contextPath, dir);
    }

    public Context addContext(Host host, String contextPath, String contextName, String dir) {
        this.silence(host, contextName);
        Context ctx = this.createContext(host, contextPath);
        ctx.setName(contextName);
        ctx.setPath(contextPath);
        ctx.setDocBase(dir);
        ctx.addLifecycleListener(new FixContextListener());
        if (host == null) {
            this.getHost().addChild(ctx);
        } else {
            host.addChild(ctx);
        }
        return ctx;
    }

    public Context addWebapp(Host host, String contextPath, String docBase) {
        LifecycleListener listener = null;
        try {
            Class<?> clazz = Class.forName(this.getHost().getConfigClass());
            listener = (LifecycleListener)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
        return this.addWebapp(host, contextPath, docBase, listener);
    }

    public Context addWebapp(Host host, String contextPath, String docBase, LifecycleListener config) {
        this.silence(host, contextPath);
        Context ctx = this.createContext(host, contextPath);
        ctx.setPath(contextPath);
        ctx.setDocBase(docBase);
        if (this.addDefaultWebXmlToWebapp) {
            ctx.addLifecycleListener(this.getDefaultWebXmlListener());
        }
        ctx.setConfigFile(this.getWebappConfigFile(docBase, contextPath));
        ctx.addLifecycleListener(config);
        if (this.addDefaultWebXmlToWebapp && config instanceof ContextConfig) {
            ((ContextConfig)config).setDefaultWebXml(this.noDefaultWebXmlPath());
        }
        if (host == null) {
            this.getHost().addChild(ctx);
        } else {
            host.addChild(ctx);
        }
        return ctx;
    }

    public LifecycleListener getDefaultWebXmlListener() {
        return new DefaultWebXmlListener();
    }

    public String noDefaultWebXmlPath() {
        return "org/apache/catalina/startup/NO_DEFAULT_XML";
    }

    protected Realm createDefaultRealm() {
        return new SimpleRealm();
    }

    protected void initBaseDir() {
        File baseFile;
        String catalinaHome = System.getProperty("catalina.home");
        if (this.basedir == null) {
            this.basedir = System.getProperty("catalina.base");
        }
        if (this.basedir == null) {
            this.basedir = catalinaHome;
        }
        if (this.basedir == null) {
            this.basedir = System.getProperty("user.dir") + "/tomcat." + this.port;
        }
        if ((baseFile = new File(this.basedir)).exists()) {
            if (!baseFile.isDirectory()) {
                throw new IllegalArgumentException(sm.getString("tomcat.baseDirNotDir", new Object[]{baseFile}));
            }
        } else if (!baseFile.mkdirs()) {
            throw new IllegalStateException(sm.getString("tomcat.baseDirMakeFail", new Object[]{baseFile}));
        }
        try {
            baseFile = baseFile.getCanonicalFile();
        }
        catch (IOException e) {
            baseFile = baseFile.getAbsoluteFile();
        }
        this.server.setCatalinaBase(baseFile);
        System.setProperty("catalina.base", baseFile.getPath());
        this.basedir = baseFile.getPath();
        if (catalinaHome == null) {
            this.server.setCatalinaHome(baseFile);
        } else {
            File homeFile = new File(catalinaHome);
            if (!homeFile.isDirectory() && !homeFile.mkdirs()) {
                throw new IllegalStateException(sm.getString("tomcat.homeDirMakeFail", new Object[]{homeFile}));
            }
            try {
                homeFile = homeFile.getCanonicalFile();
            }
            catch (IOException e) {
                homeFile = homeFile.getAbsoluteFile();
            }
            this.server.setCatalinaHome(homeFile);
        }
        System.setProperty("catalina.home", this.server.getCatalinaHome().getPath());
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
        for (String s : silences) {
            Logger logger = Logger.getLogger(s);
            this.pinnedLoggers.put(s, logger);
            if (silent) {
                logger.setLevel(Level.WARNING);
                continue;
            }
            logger.setLevel(Level.INFO);
        }
    }

    private void silence(Host host, String contextPath) {
        String loggerName = this.getLoggerName(host, contextPath);
        Logger logger = Logger.getLogger(loggerName);
        this.pinnedLoggers.put(loggerName, logger);
        if (this.silent) {
            logger.setLevel(Level.WARNING);
        } else {
            logger.setLevel(Level.INFO);
        }
    }

    public void setAddDefaultWebXmlToWebapp(boolean addDefaultWebXmlToWebapp) {
        this.addDefaultWebXmlToWebapp = addDefaultWebXmlToWebapp;
    }

    private String getLoggerName(Host host, String contextName) {
        if (host == null) {
            host = this.getHost();
        }
        StringBuilder loggerName = new StringBuilder();
        loggerName.append(ContainerBase.class.getName());
        loggerName.append(".[");
        loggerName.append(host.getParent().getName());
        loggerName.append("].[");
        loggerName.append(host.getName());
        loggerName.append("].[");
        if (contextName == null || contextName.equals("")) {
            loggerName.append('/');
        } else if (contextName.startsWith("##")) {
            loggerName.append('/');
            loggerName.append(contextName);
        }
        loggerName.append(']');
        return loggerName.toString();
    }

    private Context createContext(Host host, String url) {
        String defaultContextClass = StandardContext.class.getName();
        String contextClass = StandardContext.class.getName();
        if (host == null) {
            host = this.getHost();
        }
        if (host instanceof StandardHost) {
            contextClass = ((StandardHost)host).getContextClass();
        }
        try {
            if (defaultContextClass.equals(contextClass)) {
                return new StandardContext();
            }
            return (Context)Class.forName(contextClass).getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("tomcat.noContextClass", new Object[]{contextClass, host, url}), e);
        }
    }

    public void enableNaming() {
        this.getServer();
        this.server.addLifecycleListener(new NamingContextListener());
        System.setProperty("catalina.useNaming", "true");
        String value = "org.apache.naming";
        String oldValue = System.getProperty("java.naming.factory.url.pkgs");
        if (oldValue != null) {
            value = oldValue.contains(value) ? oldValue : value + ":" + oldValue;
        }
        System.setProperty("java.naming.factory.url.pkgs", value);
        value = System.getProperty("java.naming.factory.initial");
        if (value == null) {
            System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
        }
    }

    public void initWebappDefaults(String contextPath) {
        Container ctx = this.getHost().findChild(contextPath);
        Tomcat.initWebappDefaults((Context)ctx);
    }

    public static void initWebappDefaults(Context ctx) {
        Wrapper servlet = Tomcat.addServlet(ctx, "default", "org.apache.catalina.servlets.DefaultServlet");
        servlet.setLoadOnStartup(1);
        servlet.setOverridable(true);
        servlet = Tomcat.addServlet(ctx, "jsp", "org.apache.jasper.servlet.JspServlet");
        servlet.addInitParameter("fork", "false");
        servlet.setLoadOnStartup(3);
        servlet.setOverridable(true);
        ctx.addServletMappingDecoded("/", "default");
        ctx.addServletMappingDecoded("*.jsp", "jsp");
        ctx.addServletMappingDecoded("*.jspx", "jsp");
        ctx.setSessionTimeout(30);
        Tomcat.addDefaultMimeTypeMappings(ctx);
        ctx.addWelcomeFile("index.html");
        ctx.addWelcomeFile("index.htm");
        ctx.addWelcomeFile("index.jsp");
    }

    public static void addDefaultMimeTypeMappings(Context context) {
        Properties defaultMimeMappings = new Properties();
        try (InputStream is = Tomcat.class.getResourceAsStream("MimeTypeMappings.properties");){
            defaultMimeMappings.load(is);
            for (Map.Entry<Object, Object> entry : defaultMimeMappings.entrySet()) {
                context.addMimeMapping((String)entry.getKey(), (String)entry.getValue());
            }
        }
        catch (IOException e) {
            throw new IllegalStateException(sm.getString("tomcat.defaultMimeTypeMappingsFail"), e);
        }
    }

    protected URL getWebappConfigFile(String path, String contextName) {
        File docBase = new File(path);
        if (docBase.isDirectory()) {
            return this.getWebappConfigFileFromDirectory(docBase, contextName);
        }
        return this.getWebappConfigFileFromWar(docBase, contextName);
    }

    private URL getWebappConfigFileFromDirectory(File docBase, String contextName) {
        URL result = null;
        File webAppContextXml = new File(docBase, "META-INF/context.xml");
        if (webAppContextXml.exists()) {
            try {
                result = webAppContextXml.toURI().toURL();
            }
            catch (MalformedURLException e) {
                Logger.getLogger(this.getLoggerName(this.getHost(), contextName)).log(Level.WARNING, sm.getString("tomcat.noContextXml", new Object[]{docBase}), e);
            }
        }
        return result;
    }

    private URL getWebappConfigFileFromWar(File docBase, String contextName) {
        URL result = null;
        try (JarFile jar = new JarFile(docBase);){
            JarEntry entry = jar.getJarEntry("META-INF/context.xml");
            if (entry != null) {
                result = UriUtil.buildJarUrl((File)docBase, (String)"META-INF/context.xml");
            }
        }
        catch (IOException e) {
            Logger.getLogger(this.getLoggerName(this.getHost(), contextName)).log(Level.WARNING, sm.getString("tomcat.noContextXml", new Object[]{docBase}), e);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String[] catalinaArguments = null;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("--no-jmx")) {
                Registry.disableRegistry();
                continue;
            }
            if (!args[i].equals("--catalina")) continue;
            ArrayList<String> result = new ArrayList<String>();
            for (int j = i + 1; j < args.length; ++j) {
                result.add(args[j]);
            }
            catalinaArguments = result.toArray(new String[0]);
            break;
        }
        SecurityClassLoad.securityClassLoad(Thread.currentThread().getContextClassLoader());
        Tomcat tomcat = new Tomcat();
        tomcat.init(null, catalinaArguments);
        boolean await = false;
        String path = "";
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("--war")) {
                if (++i >= args.length) {
                    throw new IllegalArgumentException(sm.getString("tomcat.invalidCommandLine", new Object[]{args[i - 1]}));
                }
                File war = new File(args[i]);
                tomcat.addWebapp(path, war.getAbsolutePath());
                continue;
            }
            if (args[i].equals("--path")) {
                if (++i >= args.length) {
                    throw new IllegalArgumentException(sm.getString("tomcat.invalidCommandLine", new Object[]{args[i - 1]}));
                }
                path = args[i];
                continue;
            }
            if (args[i].equals("--await")) {
                await = true;
                continue;
            }
            if (args[i].equals("--no-jmx")) continue;
            if (args[i].equals("--catalina")) break;
            throw new IllegalArgumentException(sm.getString("tomcat.invalidCommandLine", new Object[]{args[i]}));
        }
        tomcat.start();
        if (await) {
            tomcat.getServer().await();
        }
    }

    static {
        if (JreCompat.isGraalAvailable()) {
            try (FileInputStream is = new FileInputStream(new File(System.getProperty("java.util.logging.config.file", "conf/logging.properties")));){
                LogManager.getLogManager().readConfiguration(is);
            }
            catch (IOException | SecurityException exception) {
                // empty catch block
            }
        }
    }

    public static class ExistingStandardWrapper
    extends StandardWrapper {
        private final Servlet existing;

        public ExistingStandardWrapper(Servlet existing) {
            this.existing = existing;
            if (existing instanceof SingleThreadModel) {
                this.singleThreadModel = true;
                this.instancePool = new Stack();
            }
            this.asyncSupported = ExistingStandardWrapper.hasAsync(existing);
        }

        private static boolean hasAsync(Servlet existing) {
            boolean result = false;
            Class<?> clazz = existing.getClass();
            WebServlet ws = clazz.getAnnotation(WebServlet.class);
            if (ws != null) {
                result = ws.asyncSupported();
            }
            return result;
        }

        @Override
        public synchronized Servlet loadServlet() throws ServletException {
            if (this.singleThreadModel) {
                Servlet instance;
                try {
                    instance = (Servlet)this.existing.getClass().getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (ReflectiveOperationException e) {
                    throw new ServletException((Throwable)e);
                }
                instance.init((ServletConfig)this.facade);
                return instance;
            }
            if (!this.instanceInitialized) {
                this.existing.init((ServletConfig)this.facade);
                this.instanceInitialized = true;
            }
            return this.existing;
        }

        @Override
        public long getAvailable() {
            return 0L;
        }

        @Override
        public boolean isUnavailable() {
            return false;
        }

        @Override
        public Servlet getServlet() {
            return this.existing;
        }

        @Override
        public String getServletClass() {
            return this.existing.getClass().getName();
        }
    }

    public static class FixContextListener
    implements LifecycleListener {
        @Override
        public void lifecycleEvent(LifecycleEvent event) {
            try {
                Context context = (Context)event.getLifecycle();
                if (event.getType().equals("configure_start")) {
                    context.setConfigured(true);
                    if (!JreCompat.isGraalAvailable()) {
                        WebAnnotationSet.loadApplicationAnnotations(context);
                    }
                    if (context.getLoginConfig() == null) {
                        context.setLoginConfig(new LoginConfig("NONE", null, null, null));
                        context.getPipeline().addValve(new NonLoginAuthenticator());
                    }
                }
            }
            catch (ClassCastException classCastException) {
                // empty catch block
            }
        }
    }

    public static class DefaultWebXmlListener
    implements LifecycleListener {
        @Override
        public void lifecycleEvent(LifecycleEvent event) {
            if ("before_start".equals(event.getType())) {
                Tomcat.initWebappDefaults((Context)event.getLifecycle());
            }
        }
    }

    private class SimpleRealm
    extends RealmBase {
        private SimpleRealm() {
        }

        @Override
        protected String getPassword(String username) {
            return (String)Tomcat.this.userPass.get(username);
        }

        @Override
        protected Principal getPrincipal(String username) {
            String pass;
            Principal p = (Principal)Tomcat.this.userPrincipals.get(username);
            if (p == null && (pass = (String)Tomcat.this.userPass.get(username)) != null) {
                p = new GenericPrincipal(username, pass, (List)Tomcat.this.userRoles.get(username));
                Tomcat.this.userPrincipals.put(username, p);
            }
            return p;
        }
    }
}

