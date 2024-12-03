/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.ClassLoaderLogManager
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.Digester$GeneratedCodeLoader
 *  org.apache.tomcat.util.digester.Rule
 *  org.apache.tomcat.util.digester.RuleSet
 *  org.apache.tomcat.util.file.ConfigFileLoader
 *  org.apache.tomcat.util.file.ConfigurationSource
 *  org.apache.tomcat.util.file.ConfigurationSource$Resource
 *  org.apache.tomcat.util.log.SystemLogHandler
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import org.apache.catalina.Container;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.security.SecurityConfig;
import org.apache.catalina.startup.AddPortOffsetRule;
import org.apache.catalina.startup.Bootstrap;
import org.apache.catalina.startup.CatalinaBaseConfigurationSource;
import org.apache.catalina.startup.CertificateCreateRule;
import org.apache.catalina.startup.ConnectorCreateRule;
import org.apache.catalina.startup.ContextRuleSet;
import org.apache.catalina.startup.EngineRuleSet;
import org.apache.catalina.startup.HostRuleSet;
import org.apache.catalina.startup.ListenerCreateRule;
import org.apache.catalina.startup.NamingRuleSet;
import org.apache.juli.ClassLoaderLogManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;

public class Catalina {
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.startup");
    public static final String SERVER_XML = "conf/server.xml";
    protected boolean await = false;
    protected String configFile = "conf/server.xml";
    protected ClassLoader parentClassLoader = Catalina.class.getClassLoader();
    protected Server server = null;
    protected boolean useShutdownHook = true;
    protected Thread shutdownHook = null;
    protected boolean useNaming = true;
    protected boolean loaded = false;
    protected boolean generateCode = false;
    protected File generatedCodeLocation = null;
    protected String generatedCodeLocationParameter = null;
    protected String generatedCodePackage = "catalinaembedded";
    protected boolean useGeneratedCode = false;
    private static final Log log = LogFactory.getLog(Catalina.class);

    public Catalina() {
        this.setSecurityProtection();
        ExceptionUtils.preload();
    }

    public void setConfigFile(String file) {
        this.configFile = file;
    }

    public String getConfigFile() {
        return this.configFile;
    }

    public void setUseShutdownHook(boolean useShutdownHook) {
        this.useShutdownHook = useShutdownHook;
    }

    public boolean getUseShutdownHook() {
        return this.useShutdownHook;
    }

    public boolean getGenerateCode() {
        return this.generateCode;
    }

    public void setGenerateCode(boolean generateCode) {
        this.generateCode = generateCode;
    }

    public boolean getUseGeneratedCode() {
        return this.useGeneratedCode;
    }

    public void setUseGeneratedCode(boolean useGeneratedCode) {
        this.useGeneratedCode = useGeneratedCode;
    }

    public File getGeneratedCodeLocation() {
        return this.generatedCodeLocation;
    }

    public void setGeneratedCodeLocation(File generatedCodeLocation) {
        this.generatedCodeLocation = generatedCodeLocation;
    }

    public String getGeneratedCodePackage() {
        return this.generatedCodePackage;
    }

    public void setGeneratedCodePackage(String generatedCodePackage) {
        this.generatedCodePackage = generatedCodePackage;
    }

    public void setParentClassLoader(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }

    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        return ClassLoader.getSystemClassLoader();
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }

    public boolean isUseNaming() {
        return this.useNaming;
    }

    public void setUseNaming(boolean useNaming) {
        this.useNaming = useNaming;
    }

    public void setAwait(boolean b) {
        this.await = b;
    }

    public boolean isAwait() {
        return this.await;
    }

    protected boolean arguments(String[] args) {
        boolean isConfig = false;
        boolean isGenerateCode = false;
        if (args.length < 1) {
            this.usage();
            return false;
        }
        for (String arg : args) {
            if (isConfig) {
                this.configFile = arg;
                isConfig = false;
                continue;
            }
            if (arg.equals("-config")) {
                isConfig = true;
                continue;
            }
            if (arg.equals("-generateCode")) {
                this.setGenerateCode(true);
                isGenerateCode = true;
                continue;
            }
            if (arg.equals("-useGeneratedCode")) {
                this.setUseGeneratedCode(true);
                isGenerateCode = false;
                continue;
            }
            if (arg.equals("-nonaming")) {
                this.setUseNaming(false);
                isGenerateCode = false;
                continue;
            }
            if (arg.equals("-help")) {
                this.usage();
                return false;
            }
            if (arg.equals("start")) {
                isGenerateCode = false;
                continue;
            }
            if (arg.equals("configtest")) {
                isGenerateCode = false;
                continue;
            }
            if (arg.equals("stop")) {
                isGenerateCode = false;
                continue;
            }
            if (isGenerateCode) {
                this.generatedCodeLocationParameter = arg;
                isGenerateCode = false;
                continue;
            }
            this.usage();
            return false;
        }
        return true;
    }

    protected File configFile() {
        File file = new File(this.configFile);
        if (!file.isAbsolute()) {
            file = new File(Bootstrap.getCatalinaBase(), this.configFile);
        }
        return file;
    }

    protected Digester createStartDigester() {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setRulesValidation(true);
        HashMap fakeAttributes = new HashMap();
        ArrayList<String> objectAttrs = new ArrayList<String>();
        objectAttrs.add("className");
        fakeAttributes.put(Object.class, objectAttrs);
        ArrayList<String> contextAttrs = new ArrayList<String>();
        contextAttrs.add("source");
        fakeAttributes.put(StandardContext.class, contextAttrs);
        ArrayList<String> connectorAttrs = new ArrayList<String>();
        connectorAttrs.add("portOffset");
        fakeAttributes.put(Connector.class, connectorAttrs);
        digester.setFakeAttributes(fakeAttributes);
        digester.setUseContextClassLoader(true);
        digester.addObjectCreate("Server", "org.apache.catalina.core.StandardServer", "className");
        digester.addSetProperties("Server");
        digester.addSetNext("Server", "setServer", "org.apache.catalina.Server");
        digester.addObjectCreate("Server/GlobalNamingResources", "org.apache.catalina.deploy.NamingResourcesImpl");
        digester.addSetProperties("Server/GlobalNamingResources");
        digester.addSetNext("Server/GlobalNamingResources", "setGlobalNamingResources", "org.apache.catalina.deploy.NamingResourcesImpl");
        digester.addRule("Server/Listener", (Rule)new ListenerCreateRule(null, "className"));
        digester.addSetProperties("Server/Listener");
        digester.addSetNext("Server/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service", "org.apache.catalina.core.StandardService", "className");
        digester.addSetProperties("Server/Service");
        digester.addSetNext("Server/Service", "addService", "org.apache.catalina.Service");
        digester.addObjectCreate("Server/Service/Listener", null, "className");
        digester.addSetProperties("Server/Service/Listener");
        digester.addSetNext("Server/Service/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service/Executor", "org.apache.catalina.core.StandardThreadExecutor", "className");
        digester.addSetProperties("Server/Service/Executor");
        digester.addSetNext("Server/Service/Executor", "addExecutor", "org.apache.catalina.Executor");
        digester.addRule("Server/Service/Connector", (Rule)new ConnectorCreateRule());
        digester.addSetProperties("Server/Service/Connector", new String[]{"executor", "sslImplementationName", "protocol"});
        digester.addSetNext("Server/Service/Connector", "addConnector", "org.apache.catalina.connector.Connector");
        digester.addRule("Server/Service/Connector", (Rule)new AddPortOffsetRule());
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig", "org.apache.tomcat.util.net.SSLHostConfig");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig", "addSslHostConfig", "org.apache.tomcat.util.net.SSLHostConfig");
        digester.addRule("Server/Service/Connector/SSLHostConfig/Certificate", (Rule)new CertificateCreateRule());
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig/Certificate", new String[]{"type"});
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/Certificate", "addCertificate", "org.apache.tomcat.util.net.SSLHostConfigCertificate");
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig/OpenSSLConf", "org.apache.tomcat.util.net.openssl.OpenSSLConf");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig/OpenSSLConf");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/OpenSSLConf", "setOpenSslConf", "org.apache.tomcat.util.net.openssl.OpenSSLConf");
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd", "org.apache.tomcat.util.net.openssl.OpenSSLConfCmd");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd", "addCmd", "org.apache.tomcat.util.net.openssl.OpenSSLConfCmd");
        digester.addObjectCreate("Server/Service/Connector/Listener", null, "className");
        digester.addSetProperties("Server/Service/Connector/Listener");
        digester.addSetNext("Server/Service/Connector/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service/Connector/UpgradeProtocol", null, "className");
        digester.addSetProperties("Server/Service/Connector/UpgradeProtocol");
        digester.addSetNext("Server/Service/Connector/UpgradeProtocol", "addUpgradeProtocol", "org.apache.coyote.UpgradeProtocol");
        digester.addRuleSet((RuleSet)new NamingRuleSet("Server/GlobalNamingResources/"));
        digester.addRuleSet((RuleSet)new EngineRuleSet("Server/Service/"));
        digester.addRuleSet((RuleSet)new HostRuleSet("Server/Service/Engine/"));
        digester.addRuleSet((RuleSet)new ContextRuleSet("Server/Service/Engine/Host/"));
        this.addClusterRuleSet(digester, "Server/Service/Engine/Host/Cluster/");
        digester.addRuleSet((RuleSet)new NamingRuleSet("Server/Service/Engine/Host/Context/"));
        digester.addRule("Server/Service/Engine", (Rule)new SetParentClassLoaderRule(this.parentClassLoader));
        this.addClusterRuleSet(digester, "Server/Service/Engine/Cluster/");
        return digester;
    }

    private void addClusterRuleSet(Digester digester, String prefix) {
        block3: {
            Class<?> clazz = null;
            Constructor<?> constructor = null;
            try {
                clazz = Class.forName("org.apache.catalina.ha.ClusterRuleSet");
                constructor = clazz.getConstructor(String.class);
                RuleSet ruleSet = (RuleSet)constructor.newInstance(prefix);
                digester.addRuleSet(ruleSet);
            }
            catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("catalina.noCluster", new Object[]{e.getClass().getName() + ": " + e.getMessage()}), (Throwable)e);
                }
                if (!log.isInfoEnabled()) break block3;
                log.info((Object)sm.getString("catalina.noCluster", new Object[]{e.getClass().getName() + ": " + e.getMessage()}));
            }
        }
    }

    protected Digester createStopDigester() {
        Digester digester = new Digester();
        digester.setUseContextClassLoader(true);
        digester.addObjectCreate("Server", "org.apache.catalina.core.StandardServer", "className");
        digester.addSetProperties("Server");
        digester.addSetNext("Server", "setServer", "org.apache.catalina.Server");
        return digester;
    }

    protected void parseServerXml(boolean start) {
        block28: {
            ConfigFileLoader.setSource((ConfigurationSource)new CatalinaBaseConfigurationSource(Bootstrap.getCatalinaBaseFile(), this.getConfigFile()));
            File file = this.configFile();
            if (this.useGeneratedCode && !Digester.isGeneratedCodeLoaderSet()) {
                String loaderClassName = this.generatedCodePackage + ".DigesterGeneratedCodeLoader";
                try {
                    Digester.GeneratedCodeLoader loader = (Digester.GeneratedCodeLoader)Catalina.class.getClassLoader().loadClass(loaderClassName).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    Digester.setGeneratedCodeLoader((Digester.GeneratedCodeLoader)loader);
                }
                catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.info((Object)sm.getString("catalina.noLoader", new Object[]{loaderClassName}), (Throwable)e);
                    } else {
                        log.info((Object)sm.getString("catalina.noLoader", new Object[]{loaderClassName}));
                    }
                    this.useGeneratedCode = false;
                }
            }
            File serverXmlLocation = null;
            String xmlClassName = null;
            if (this.generateCode || this.useGeneratedCode) {
                String string = xmlClassName = start ? this.generatedCodePackage + ".ServerXml" : this.generatedCodePackage + ".ServerXmlStop";
            }
            if (this.generateCode) {
                if (this.generatedCodeLocationParameter != null) {
                    this.generatedCodeLocation = new File(this.generatedCodeLocationParameter);
                    if (!this.generatedCodeLocation.isAbsolute()) {
                        this.generatedCodeLocation = new File(Bootstrap.getCatalinaHomeFile(), this.generatedCodeLocationParameter);
                    }
                } else {
                    this.generatedCodeLocation = new File(Bootstrap.getCatalinaHomeFile(), "work");
                }
                if (!(serverXmlLocation = new File(this.generatedCodeLocation, this.generatedCodePackage)).isDirectory() && !serverXmlLocation.mkdirs()) {
                    log.warn((Object)sm.getString("catalina.generatedCodeLocationError", new Object[]{this.generatedCodeLocation.getAbsolutePath()}));
                    this.generateCode = false;
                }
            }
            ServerXml serverXml = null;
            if (this.useGeneratedCode) {
                serverXml = (ServerXml)Digester.loadGeneratedClass((String)xmlClassName);
            }
            if (serverXml != null) {
                serverXml.load(this);
            } else {
                try (ConfigurationSource.Resource resource = ConfigFileLoader.getSource().getServerXml();){
                    Digester digester = start ? this.createStartDigester() : this.createStopDigester();
                    InputStream inputStream = resource.getInputStream();
                    InputSource inputSource = new InputSource(resource.getURI().toURL().toString());
                    inputSource.setByteStream(inputStream);
                    digester.push((Object)this);
                    if (this.generateCode) {
                        digester.startGeneratingCode();
                        this.generateClassHeader(digester, start);
                    }
                    digester.parse(inputSource);
                    if (!this.generateCode) break block28;
                    this.generateClassFooter(digester);
                    try (FileWriter writer = new FileWriter(new File(serverXmlLocation, start ? "ServerXml.java" : "ServerXmlStop.java"));){
                        writer.write(digester.getGeneratedCode().toString());
                    }
                    digester.endGeneratingCode();
                    Digester.addGeneratedClass((String)xmlClassName);
                }
                catch (Exception e) {
                    log.warn((Object)sm.getString("catalina.configFail", new Object[]{file.getAbsolutePath()}), (Throwable)e);
                    if (!file.exists() || file.canRead()) break block28;
                    log.warn((Object)sm.getString("catalina.incorrectPermissions"));
                }
            }
        }
    }

    public void stopServer() {
        this.stopServer(null);
    }

    public void stopServer(String[] arguments) {
        Server s;
        if (arguments != null) {
            this.arguments(arguments);
        }
        if ((s = this.getServer()) == null) {
            this.parseServerXml(false);
            if (this.getServer() == null) {
                log.error((Object)sm.getString("catalina.stopError"));
                System.exit(1);
            }
        } else {
            try {
                s.stop();
                s.destroy();
            }
            catch (LifecycleException e) {
                log.error((Object)sm.getString("catalina.stopError"), (Throwable)e);
            }
            return;
        }
        s = this.getServer();
        if (s.getPortWithOffset() > 0) {
            try (Socket socket = new Socket(s.getAddress(), s.getPortWithOffset());
                 OutputStream stream = socket.getOutputStream();){
                String shutdown = s.getShutdown();
                for (int i = 0; i < shutdown.length(); ++i) {
                    stream.write(shutdown.charAt(i));
                }
                stream.flush();
            }
            catch (ConnectException ce) {
                log.error((Object)sm.getString("catalina.stopServer.connectException", new Object[]{s.getAddress(), String.valueOf(s.getPortWithOffset()), String.valueOf(s.getPort()), String.valueOf(s.getPortOffset())}));
                log.error((Object)sm.getString("catalina.stopError"), (Throwable)ce);
                System.exit(1);
            }
            catch (IOException e) {
                log.error((Object)sm.getString("catalina.stopError"), (Throwable)e);
                System.exit(1);
            }
        } else {
            log.error((Object)sm.getString("catalina.stopServer"));
            System.exit(1);
        }
    }

    public void load() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        long t1 = System.nanoTime();
        this.initDirs();
        this.initNaming();
        this.parseServerXml(true);
        Server s = this.getServer();
        if (s == null) {
            return;
        }
        this.getServer().setCatalina(this);
        this.getServer().setCatalinaHome(Bootstrap.getCatalinaHomeFile());
        this.getServer().setCatalinaBase(Bootstrap.getCatalinaBaseFile());
        this.initStreams();
        try {
            this.getServer().init();
        }
        catch (LifecycleException e) {
            if (Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE")) {
                throw new Error(e);
            }
            log.error((Object)sm.getString("catalina.initError"), (Throwable)e);
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("catalina.init", new Object[]{Long.toString(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t1))}));
        }
    }

    public void load(String[] args) {
        try {
            if (this.arguments(args)) {
                this.load();
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void start() {
        if (this.getServer() == null) {
            this.load();
        }
        if (this.getServer() == null) {
            log.fatal((Object)sm.getString("catalina.noServer"));
            return;
        }
        long t1 = System.nanoTime();
        try {
            this.getServer().start();
        }
        catch (LifecycleException e) {
            log.fatal((Object)sm.getString("catalina.serverStartFail"), (Throwable)e);
            try {
                this.getServer().destroy();
            }
            catch (LifecycleException e1) {
                log.debug((Object)"destroy() failed for failed Server ", (Throwable)e1);
            }
            return;
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("catalina.startup", new Object[]{Long.toString(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t1))}));
        }
        if (this.generateCode) {
            this.generateLoader();
        }
        if (this.useShutdownHook) {
            if (this.shutdownHook == null) {
                this.shutdownHook = new CatalinaShutdownHook();
            }
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
            LogManager logManager = LogManager.getLogManager();
            if (logManager instanceof ClassLoaderLogManager) {
                ((ClassLoaderLogManager)logManager).setUseShutdownHook(false);
            }
        }
        if (this.await) {
            this.await();
            this.stop();
        }
    }

    public void stop() {
        try {
            if (this.useShutdownHook) {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                LogManager logManager = LogManager.getLogManager();
                if (logManager instanceof ClassLoaderLogManager) {
                    ((ClassLoaderLogManager)logManager).setUseShutdownHook(true);
                }
            }
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
        }
        try {
            Server s = this.getServer();
            LifecycleState state = s.getState();
            if (LifecycleState.STOPPING_PREP.compareTo(state) > 0 || LifecycleState.DESTROYED.compareTo(state) < 0) {
                s.stop();
                s.destroy();
            }
        }
        catch (LifecycleException e) {
            log.error((Object)sm.getString("catalina.stopError"), (Throwable)e);
        }
    }

    public void await() {
        this.getServer().await();
    }

    protected void usage() {
        System.out.println(sm.getString("catalina.usage"));
    }

    @Deprecated
    protected void initDirs() {
    }

    protected void initStreams() {
        System.setOut((PrintStream)new SystemLogHandler(System.out));
        System.setErr((PrintStream)new SystemLogHandler(System.err));
    }

    protected void initNaming() {
        if (!this.useNaming) {
            log.info((Object)sm.getString("catalina.noNaming"));
            System.setProperty("catalina.useNaming", "false");
        } else {
            System.setProperty("catalina.useNaming", "true");
            String value = "org.apache.naming";
            String oldValue = System.getProperty("java.naming.factory.url.pkgs");
            if (oldValue != null) {
                value = value + ":" + oldValue;
            }
            System.setProperty("java.naming.factory.url.pkgs", value);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Setting naming prefix=" + value));
            }
            if ((value = System.getProperty("java.naming.factory.initial")) == null) {
                System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
            } else {
                log.debug((Object)("INITIAL_CONTEXT_FACTORY already set " + value));
            }
        }
    }

    protected void setSecurityProtection() {
        SecurityConfig securityConfig = SecurityConfig.newInstance();
        securityConfig.setPackageDefinition();
        securityConfig.setPackageAccess();
    }

    protected void generateLoader() {
        String loaderClassName = "DigesterGeneratedCodeLoader";
        StringBuilder code = new StringBuilder();
        code.append("package ").append(this.generatedCodePackage).append(';').append(System.lineSeparator());
        code.append("public class ").append(loaderClassName);
        code.append(" implements org.apache.tomcat.util.digester.Digester.GeneratedCodeLoader {").append(System.lineSeparator());
        code.append("public Object loadGeneratedCode(String className) {").append(System.lineSeparator());
        code.append("switch (className) {").append(System.lineSeparator());
        for (String generatedClassName : Digester.getGeneratedClasses()) {
            code.append("case \"").append(generatedClassName).append("\" : return new ").append(generatedClassName);
            code.append("();").append(System.lineSeparator());
        }
        code.append("default: return null; }").append(System.lineSeparator());
        code.append("}}").append(System.lineSeparator());
        File loaderLocation = new File(this.generatedCodeLocation, this.generatedCodePackage);
        try (FileWriter writer = new FileWriter(new File(loaderLocation, loaderClassName + ".java"));){
            writer.write(code.toString());
        }
        catch (IOException e) {
            log.debug((Object)"Error writing code loader", (Throwable)e);
        }
    }

    protected void generateClassHeader(Digester digester, boolean start) {
        StringBuilder code = digester.getGeneratedCode();
        code.append("package ").append(this.generatedCodePackage).append(';').append(System.lineSeparator());
        code.append("public class ServerXml");
        if (!start) {
            code.append("Stop");
        }
        code.append(" implements ");
        code.append(ServerXml.class.getName().replace('$', '.')).append(" {").append(System.lineSeparator());
        code.append("public void load(").append(Catalina.class.getName());
        code.append(' ').append(digester.toVariableName((Object)this)).append(") {").append(System.lineSeparator());
    }

    protected void generateClassFooter(Digester digester) {
        StringBuilder code = digester.getGeneratedCode();
        code.append('}').append(System.lineSeparator());
        code.append('}').append(System.lineSeparator());
    }

    final class SetParentClassLoaderRule
    extends Rule {
        ClassLoader parentClassLoader = null;

        SetParentClassLoaderRule(ClassLoader parentClassLoader) {
            this.parentClassLoader = parentClassLoader;
        }

        public void begin(String namespace, String name, Attributes attributes) throws Exception {
            if (this.digester.getLogger().isDebugEnabled()) {
                this.digester.getLogger().debug((Object)"Setting parent class loader");
            }
            Container top = (Container)this.digester.peek();
            top.setParentClassLoader(this.parentClassLoader);
            StringBuilder code = this.digester.getGeneratedCode();
            if (code != null) {
                code.append(this.digester.toVariableName((Object)top)).append(".setParentClassLoader(");
                code.append(this.digester.toVariableName((Object)Catalina.this)).append(".getParentClassLoader());");
                code.append(System.lineSeparator());
            }
        }
    }

    public static interface ServerXml {
        public void load(Catalina var1);
    }

    protected class CatalinaShutdownHook
    extends Thread {
        protected CatalinaShutdownHook() {
        }

        @Override
        public void run() {
            try {
                if (Catalina.this.getServer() != null) {
                    Catalina.this.stop();
                }
            }
            catch (Throwable ex) {
                ExceptionUtils.handleThrowable((Throwable)ex);
                log.error((Object)sm.getString("catalina.shutdownHookFail"), ex);
            }
            finally {
                LogManager logManager = LogManager.getLogManager();
                if (logManager instanceof ClassLoaderLogManager) {
                    ((ClassLoaderLogManager)logManager).shutdown();
                }
            }
        }
    }
}

