/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.MultipartConfigElement
 *  javax.servlet.ServletContainerInitializer
 *  javax.servlet.ServletContext
 *  javax.servlet.SessionCookieConfig
 *  javax.servlet.annotation.HandlesTypes
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.JarScanType
 *  org.apache.tomcat.JarScanner
 *  org.apache.tomcat.JarScannerCallback
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.bcel.classfile.AnnotationElementValue
 *  org.apache.tomcat.util.bcel.classfile.AnnotationEntry
 *  org.apache.tomcat.util.bcel.classfile.ArrayElementValue
 *  org.apache.tomcat.util.bcel.classfile.ClassFormatException
 *  org.apache.tomcat.util.bcel.classfile.ClassParser
 *  org.apache.tomcat.util.bcel.classfile.ElementValue
 *  org.apache.tomcat.util.bcel.classfile.ElementValuePair
 *  org.apache.tomcat.util.bcel.classfile.JavaClass
 *  org.apache.tomcat.util.buf.UriUtil
 *  org.apache.tomcat.util.descriptor.InputSourceUtil
 *  org.apache.tomcat.util.descriptor.XmlErrorHandler
 *  org.apache.tomcat.util.descriptor.web.ContextEjb
 *  org.apache.tomcat.util.descriptor.web.ContextEnvironment
 *  org.apache.tomcat.util.descriptor.web.ContextLocalEjb
 *  org.apache.tomcat.util.descriptor.web.ContextResource
 *  org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef
 *  org.apache.tomcat.util.descriptor.web.ContextService
 *  org.apache.tomcat.util.descriptor.web.ErrorPage
 *  org.apache.tomcat.util.descriptor.web.FilterDef
 *  org.apache.tomcat.util.descriptor.web.FilterMap
 *  org.apache.tomcat.util.descriptor.web.FragmentJarScannerCallback
 *  org.apache.tomcat.util.descriptor.web.JspPropertyGroup
 *  org.apache.tomcat.util.descriptor.web.LoginConfig
 *  org.apache.tomcat.util.descriptor.web.MessageDestinationRef
 *  org.apache.tomcat.util.descriptor.web.MultipartDef
 *  org.apache.tomcat.util.descriptor.web.SecurityConstraint
 *  org.apache.tomcat.util.descriptor.web.SecurityRoleRef
 *  org.apache.tomcat.util.descriptor.web.ServletDef
 *  org.apache.tomcat.util.descriptor.web.SessionConfig
 *  org.apache.tomcat.util.descriptor.web.WebXml
 *  org.apache.tomcat.util.descriptor.web.WebXmlParser
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.digester.RuleSet
 *  org.apache.tomcat.util.file.ConfigFileLoader
 *  org.apache.tomcat.util.file.ConfigurationSource$Resource
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.scan.JarFactory
 */
package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;
import javax.servlet.annotation.HandlesTypes;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Valve;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.startup.ContextRuleSet;
import org.apache.catalina.startup.ExpandWar;
import org.apache.catalina.startup.NamingRuleSet;
import org.apache.catalina.startup.WebAnnotationSet;
import org.apache.catalina.startup.WebappServiceLoader;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.Introspection;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.Jar;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.bcel.classfile.AnnotationElementValue;
import org.apache.tomcat.util.bcel.classfile.AnnotationEntry;
import org.apache.tomcat.util.bcel.classfile.ArrayElementValue;
import org.apache.tomcat.util.bcel.classfile.ClassFormatException;
import org.apache.tomcat.util.bcel.classfile.ClassParser;
import org.apache.tomcat.util.bcel.classfile.ElementValue;
import org.apache.tomcat.util.bcel.classfile.ElementValuePair;
import org.apache.tomcat.util.bcel.classfile.JavaClass;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.descriptor.InputSourceUtil;
import org.apache.tomcat.util.descriptor.XmlErrorHandler;
import org.apache.tomcat.util.descriptor.web.ContextEjb;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextLocalEjb;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.descriptor.web.FragmentJarScannerCallback;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroup;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.descriptor.web.MultipartDef;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.descriptor.web.SecurityRoleRef;
import org.apache.tomcat.util.descriptor.web.ServletDef;
import org.apache.tomcat.util.descriptor.web.SessionConfig;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.scan.JarFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class ContextConfig
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(ContextConfig.class);
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.startup");
    protected static final LoginConfig DUMMY_LOGIN_CONFIG = new LoginConfig("NONE", null, null, null);
    protected static final Properties authenticators;
    protected static long deploymentCount;
    protected static final Map<Host, DefaultWebXmlCacheEntry> hostWebXmlCache;
    private static final Set<ServletContainerInitializer> EMPTY_SCI_SET;
    protected Map<String, Authenticator> customAuthenticators;
    protected volatile Context context = null;
    protected String defaultWebXml = null;
    protected boolean ok = false;
    protected String originalDocBase = null;
    private File antiLockingDocBase = null;
    protected final Map<ServletContainerInitializer, Set<Class<?>>> initializerClassMap = new LinkedHashMap();
    protected final Map<Class<?>, Set<ServletContainerInitializer>> typeInitializerMap = new HashMap();
    protected boolean handlesTypesAnnotations = false;
    protected boolean handlesTypesNonAnnotations = false;

    public String getDefaultWebXml() {
        if (this.defaultWebXml == null) {
            this.defaultWebXml = "conf/web.xml";
        }
        return this.defaultWebXml;
    }

    public void setDefaultWebXml(String path) {
        this.defaultWebXml = path;
    }

    public void setCustomAuthenticators(Map<String, Authenticator> customAuthenticators) {
        this.customAuthenticators = customAuthenticators;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            this.context = (Context)event.getLifecycle();
        }
        catch (ClassCastException e) {
            log.error((Object)sm.getString("contextConfig.cce", new Object[]{event.getLifecycle()}), (Throwable)e);
            return;
        }
        if (event.getType().equals("configure_start")) {
            this.configureStart();
        } else if (event.getType().equals("before_start")) {
            this.beforeStart();
        } else if (event.getType().equals("after_start")) {
            if (this.originalDocBase != null) {
                this.context.setDocBase(this.originalDocBase);
            }
        } else if (event.getType().equals("configure_stop")) {
            this.configureStop();
        } else if (event.getType().equals("after_init")) {
            this.init();
        } else if (event.getType().equals("after_destroy")) {
            this.destroy();
        }
    }

    protected void applicationAnnotationsConfig() {
        long t1 = System.currentTimeMillis();
        WebAnnotationSet.loadApplicationAnnotations(this.context);
        long t2 = System.currentTimeMillis();
        if (this.context instanceof StandardContext) {
            ((StandardContext)this.context).setStartupTime(t2 - t1 + ((StandardContext)this.context).getStartupTime());
        }
    }

    protected void authenticatorConfig() {
        Pipeline pipeline;
        LoginConfig loginConfig = this.context.getLoginConfig();
        if (loginConfig == null) {
            loginConfig = DUMMY_LOGIN_CONFIG;
            this.context.setLoginConfig(loginConfig);
        }
        if (this.context.getAuthenticator() != null) {
            return;
        }
        if (this.context.getRealm() == null) {
            log.error((Object)sm.getString("contextConfig.missingRealm"));
            this.ok = false;
            return;
        }
        Valve authenticator = null;
        if (this.customAuthenticators != null) {
            authenticator = (Valve)((Object)this.customAuthenticators.get(loginConfig.getAuthMethod()));
        }
        if (authenticator == null) {
            if (authenticators == null) {
                log.error((Object)sm.getString("contextConfig.authenticatorResources"));
                this.ok = false;
                return;
            }
            String authenticatorName = authenticators.getProperty(loginConfig.getAuthMethod());
            if (authenticatorName == null) {
                log.error((Object)sm.getString("contextConfig.authenticatorMissing", new Object[]{loginConfig.getAuthMethod()}));
                this.ok = false;
                return;
            }
            try {
                Class<?> authenticatorClass = Class.forName(authenticatorName);
                authenticator = (Valve)authenticatorClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                log.error((Object)sm.getString("contextConfig.authenticatorInstantiate", new Object[]{authenticatorName}), t);
                this.ok = false;
            }
        }
        if (authenticator != null && (pipeline = this.context.getPipeline()) != null) {
            pipeline.addValve(authenticator);
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("contextConfig.authenticatorConfigured", new Object[]{loginConfig.getAuthMethod()}));
            }
        }
    }

    protected Digester createContextDigester() {
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
        digester.setFakeAttributes(fakeAttributes);
        ContextRuleSet contextRuleSet = new ContextRuleSet("", false);
        digester.addRuleSet((RuleSet)contextRuleSet);
        NamingRuleSet namingRuleSet = new NamingRuleSet("Context/");
        digester.addRuleSet((RuleSet)namingRuleSet);
        return digester;
    }

    protected boolean getGenerateCode() {
        Catalina catalina = Container.getService(this.context).getServer().getCatalina();
        if (catalina != null) {
            return catalina.getGenerateCode();
        }
        return false;
    }

    protected boolean getUseGeneratedCode() {
        Catalina catalina = Container.getService(this.context).getServer().getCatalina();
        if (catalina != null) {
            return catalina.getUseGeneratedCode();
        }
        return false;
    }

    protected File getGeneratedCodeLocation() {
        Catalina catalina = Container.getService(this.context).getServer().getCatalina();
        if (catalina != null) {
            return catalina.getGeneratedCodeLocation();
        }
        return null;
    }

    protected String getGeneratedCodePackage() {
        Catalina catalina = Container.getService(this.context).getServer().getCatalina();
        if (catalina != null) {
            return catalina.getGeneratedCodePackage();
        }
        return "generatedCodePackage";
    }

    protected static String getContextXmlPackageName(String generatedCodePackage, Container container) {
        StringBuilder result = new StringBuilder();
        Container host = null;
        Container engine = null;
        while (container != null) {
            if (container instanceof Host) {
                host = container;
            } else if (container instanceof Engine) {
                engine = container;
            }
            container = container.getParent();
        }
        result.append(generatedCodePackage);
        if (engine != null) {
            result.append('.');
        }
        if (engine != null) {
            result.append(engine.getName());
            if (host != null) {
                result.append('.');
            }
        }
        if (host != null) {
            result.append(host.getName());
        }
        return result.toString();
    }

    protected File getContextXmlJavaSource(String contextXmlPackageName, String contextXmlSimpleClassName) {
        String path;
        File generatedSourceFolder = this.getGeneratedCodeLocation();
        File packageFolder = new File(generatedSourceFolder, path = contextXmlPackageName.replace('.', File.separatorChar));
        if (packageFolder.isDirectory() || packageFolder.mkdirs()) {
            return new File(packageFolder, contextXmlSimpleClassName + ".java");
        }
        return null;
    }

    protected void generateClassHeader(Digester digester, String packageName, String resourceName) {
        StringBuilder code = digester.getGeneratedCode();
        code.append("package ").append(packageName).append(';').append(System.lineSeparator());
        code.append("public class ").append(resourceName).append(" implements ");
        code.append(ContextXml.class.getName().replace('$', '.'));
        code.append(" {").append(System.lineSeparator());
        code.append("public void load(");
        code.append(Context.class.getName());
        String contextArgument = digester.toVariableName((Object)this.context);
        code.append(' ').append(contextArgument).append(") {").append(System.lineSeparator());
        digester.setKnown((Object)this.context);
        code.append(this.context.getClass().getName()).append(' ').append(digester.toVariableName((Object)this.context));
        code.append(" = (").append(this.context.getClass().getName()).append(") ").append(contextArgument);
        code.append(';').append(System.lineSeparator());
    }

    protected void generateClassFooter(Digester digester) {
        StringBuilder code = digester.getGeneratedCode();
        code.append('}').append(System.lineSeparator());
        code.append('}').append(System.lineSeparator());
    }

    protected void contextConfig(Digester digester) {
        ContextXml contextXml;
        File contextXmlJavaSource;
        String contextXmlClassName;
        String contextXmlSimpleClassName;
        String contextXmlPackageName;
        boolean useGeneratedCode;
        boolean generateCode;
        block62: {
            String defaultContextXml = null;
            generateCode = this.getGenerateCode();
            useGeneratedCode = this.getUseGeneratedCode();
            contextXmlPackageName = null;
            contextXmlSimpleClassName = null;
            contextXmlClassName = null;
            contextXmlJavaSource = null;
            if (this.context instanceof StandardContext) {
                defaultContextXml = ((StandardContext)this.context).getDefaultContextXml();
            }
            if (defaultContextXml == null) {
                defaultContextXml = "conf/context.xml";
            }
            contextXml = null;
            if (!this.context.getOverride()) {
                block60: {
                    if (useGeneratedCode || generateCode) {
                        contextXmlPackageName = this.getGeneratedCodePackage();
                        contextXmlSimpleClassName = "ContextXmlDefault";
                        contextXmlClassName = contextXmlPackageName + "." + contextXmlSimpleClassName;
                    }
                    if (useGeneratedCode) {
                        contextXml = (ContextXml)Digester.loadGeneratedClass(contextXmlClassName);
                    }
                    if (contextXml != null) {
                        contextXml.load(this.context);
                        contextXml = null;
                    } else if (!useGeneratedCode) {
                        try (ConfigurationSource.Resource contextXmlResource = ConfigFileLoader.getSource().getResource(defaultContextXml);){
                            if (generateCode) {
                                contextXmlJavaSource = this.getContextXmlJavaSource(contextXmlPackageName, contextXmlSimpleClassName);
                                if (contextXmlJavaSource != null) {
                                    digester.startGeneratingCode();
                                    this.generateClassHeader(digester, contextXmlPackageName, contextXmlSimpleClassName);
                                } else {
                                    generateCode = false;
                                }
                            }
                            URL defaultContextUrl = contextXmlResource.getURI().toURL();
                            this.processContextConfig(digester, defaultContextUrl, contextXmlResource.getInputStream());
                            if (!generateCode) break block60;
                            this.generateClassFooter(digester);
                            try (FileWriter writer = new FileWriter(contextXmlJavaSource);){
                                writer.write(digester.getGeneratedCode().toString());
                            }
                            digester.endGeneratingCode();
                            Digester.addGeneratedClass((String)contextXmlClassName);
                        }
                        catch (MalformedURLException e) {
                            log.error((Object)sm.getString("contextConfig.badUrl", new Object[]{defaultContextXml}), (Throwable)e);
                        }
                        catch (IOException e) {
                            // empty catch block
                        }
                    }
                }
                if (useGeneratedCode || generateCode) {
                    contextXmlPackageName = ContextConfig.getContextXmlPackageName(this.getGeneratedCodePackage(), this.context);
                    contextXmlSimpleClassName = "ContextXmlDefault";
                    contextXmlClassName = contextXmlPackageName + "." + contextXmlSimpleClassName;
                }
                if (useGeneratedCode) {
                    contextXml = (ContextXml)Digester.loadGeneratedClass(contextXmlClassName);
                }
                if (contextXml != null) {
                    contextXml.load(this.context);
                    contextXml = null;
                } else if (!useGeneratedCode) {
                    String hostContextFile = Container.getConfigPath(this.context, "context.xml.default");
                    try (ConfigurationSource.Resource contextXmlResource = ConfigFileLoader.getSource().getResource(hostContextFile);){
                        if (generateCode) {
                            contextXmlJavaSource = this.getContextXmlJavaSource(contextXmlPackageName, contextXmlSimpleClassName);
                            digester.startGeneratingCode();
                            this.generateClassHeader(digester, contextXmlPackageName, contextXmlSimpleClassName);
                        }
                        URL defaultContextUrl = contextXmlResource.getURI().toURL();
                        this.processContextConfig(digester, defaultContextUrl, contextXmlResource.getInputStream());
                        if (!generateCode) break block62;
                        this.generateClassFooter(digester);
                        try (FileWriter writer = new FileWriter(contextXmlJavaSource);){
                            writer.write(digester.getGeneratedCode().toString());
                        }
                        digester.endGeneratingCode();
                        Digester.addGeneratedClass((String)contextXmlClassName);
                    }
                    catch (MalformedURLException e) {
                        log.error((Object)sm.getString("contextConfig.badUrl", new Object[]{hostContextFile}), (Throwable)e);
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }
        }
        if (this.context.getConfigFile() != null) {
            if (useGeneratedCode || generateCode) {
                contextXmlPackageName = ContextConfig.getContextXmlPackageName(this.getGeneratedCodePackage(), this.context);
                contextXmlSimpleClassName = "ContextXml_" + this.context.getName().replace('/', '_').replace("-", "__");
                contextXmlClassName = contextXmlPackageName + "." + contextXmlSimpleClassName;
            }
            if (useGeneratedCode) {
                contextXml = (ContextXml)Digester.loadGeneratedClass(contextXmlClassName);
            }
            if (contextXml != null) {
                contextXml.load(this.context);
                contextXml = null;
            } else if (!useGeneratedCode) {
                if (generateCode) {
                    contextXmlJavaSource = this.getContextXmlJavaSource(contextXmlPackageName, contextXmlSimpleClassName);
                    digester.startGeneratingCode();
                    this.generateClassHeader(digester, contextXmlPackageName, contextXmlSimpleClassName);
                }
                this.processContextConfig(digester, this.context.getConfigFile(), null);
                if (generateCode) {
                    this.generateClassFooter(digester);
                    try (FileWriter writer = new FileWriter(contextXmlJavaSource);){
                        writer.write(digester.getGeneratedCode().toString());
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    digester.endGeneratingCode();
                    Digester.addGeneratedClass((String)contextXmlClassName);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processContextConfig(Digester digester, URL contextXml, InputStream stream) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Processing context [" + this.context.getName() + "] configuration file [" + contextXml + "]"));
        }
        InputSource source = null;
        try {
            source = new InputSource(contextXml.toString());
            if (stream == null) {
                URLConnection xmlConn = contextXml.openConnection();
                xmlConn.setUseCaches(false);
                stream = xmlConn.getInputStream();
            }
        }
        catch (Exception e) {
            log.error((Object)sm.getString("contextConfig.contextMissing", new Object[]{contextXml}), (Throwable)e);
        }
        if (source == null) {
            return;
        }
        try {
            source.setByteStream(stream);
            digester.setClassLoader(this.getClass().getClassLoader());
            digester.setUseContextClassLoader(false);
            digester.push((Object)this.context.getParent());
            digester.push((Object)this.context);
            XmlErrorHandler errorHandler = new XmlErrorHandler();
            digester.setErrorHandler((ErrorHandler)errorHandler);
            digester.parse(source);
            if (errorHandler.getWarnings().size() > 0 || errorHandler.getErrors().size() > 0) {
                errorHandler.logFindings(log, contextXml.toString());
                this.ok = false;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Successfully processed context [" + this.context.getName() + "] configuration file [" + contextXml + "]"));
            }
        }
        catch (SAXParseException e) {
            log.error((Object)sm.getString("contextConfig.contextParse", new Object[]{this.context.getName()}), (Throwable)e);
            log.error((Object)sm.getString("contextConfig.defaultPosition", new Object[]{"" + e.getLineNumber(), "" + e.getColumnNumber()}));
            this.ok = false;
        }
        catch (Exception e) {
            log.error((Object)sm.getString("contextConfig.contextParse", new Object[]{this.context.getName()}), (Throwable)e);
            this.ok = false;
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (IOException e) {
                log.error((Object)sm.getString("contextConfig.contextClose"), (Throwable)e);
            }
        }
    }

    protected void fixDocBase() throws IOException {
        String docBase;
        File docBaseConfiguredFile;
        Host host = (Host)this.context.getParent();
        File appBase = host.getAppBaseFile();
        String docBaseConfigured = this.context.getDocBase();
        if (docBaseConfigured == null) {
            String path = this.context.getPath();
            if (path == null) {
                return;
            }
            ContextName cn = new ContextName(path, this.context.getWebappVersion());
            docBaseConfigured = cn.getBaseName();
        }
        String docBaseAbsolute = !(docBaseConfiguredFile = new File(docBaseConfigured)).isAbsolute() ? new File(appBase, docBaseConfigured).getAbsolutePath() : docBaseConfiguredFile.getAbsolutePath();
        File docBaseAbsoluteFile = new File(docBaseAbsolute);
        String originalDocBase = docBaseAbsolute;
        ContextName cn = new ContextName(this.context.getPath(), this.context.getWebappVersion());
        String pathName = cn.getBaseName();
        boolean unpackWARs = true;
        if (host instanceof StandardHost && (unpackWARs = ((StandardHost)host).isUnpackWARs()) && this.context instanceof StandardContext) {
            unpackWARs = ((StandardContext)this.context).getUnpackWAR();
        }
        boolean docBaseAbsoluteInAppBase = docBaseAbsolute.startsWith(appBase.getPath() + File.separatorChar);
        if (docBaseAbsolute.toLowerCase(Locale.ENGLISH).endsWith(".war") && !docBaseAbsoluteFile.isDirectory()) {
            URL war = UriUtil.buildJarUrl((File)docBaseAbsoluteFile);
            if (unpackWARs) {
                docBaseAbsolute = ExpandWar.expand(host, war, pathName);
                docBaseAbsoluteFile = new File(docBaseAbsolute);
                if (this.context instanceof StandardContext) {
                    ((StandardContext)this.context).setOriginalDocBase(originalDocBase);
                }
            } else {
                ExpandWar.validate(host, war, pathName);
            }
        } else {
            File docBaseAbsoluteFileWar = new File(docBaseAbsolute + ".war");
            URL war = null;
            if (docBaseAbsoluteFileWar.exists() && docBaseAbsoluteInAppBase) {
                war = UriUtil.buildJarUrl((File)docBaseAbsoluteFileWar);
            }
            if (docBaseAbsoluteFile.exists()) {
                if (war != null && unpackWARs) {
                    ExpandWar.expand(host, war, pathName);
                }
            } else {
                if (war != null) {
                    if (unpackWARs) {
                        docBaseAbsolute = ExpandWar.expand(host, war, pathName);
                        docBaseAbsoluteFile = new File(docBaseAbsolute);
                    } else {
                        docBaseAbsoluteFile = docBaseAbsoluteFileWar;
                        ExpandWar.validate(host, war, pathName);
                    }
                }
                if (this.context instanceof StandardContext) {
                    ((StandardContext)this.context).setOriginalDocBase(originalDocBase);
                }
            }
        }
        String docBaseCanonical = docBaseAbsoluteFile.getCanonicalPath();
        boolean docBaseCanonicalInAppBase = docBaseAbsoluteFile.getCanonicalFile().toPath().startsWith(appBase.toPath());
        if (docBaseCanonicalInAppBase) {
            docBase = docBaseCanonical.substring(appBase.getPath().length());
            if ((docBase = docBase.replace(File.separatorChar, '/')).startsWith("/")) {
                docBase = docBase.substring(1);
            }
        } else {
            docBase = docBaseCanonical.replace(File.separatorChar, '/');
        }
        this.context.setDocBase(docBase);
    }

    protected void antiLocking() {
        if (this.context instanceof StandardContext && ((StandardContext)this.context).getAntiResourceLocking()) {
            String path;
            Host host = (Host)this.context.getParent();
            String docBase = this.context.getDocBase();
            if (docBase == null) {
                return;
            }
            this.originalDocBase = docBase;
            File docBaseFile = new File(docBase);
            if (!docBaseFile.isAbsolute()) {
                docBaseFile = new File(host.getAppBaseFile(), docBase);
            }
            if ((path = this.context.getPath()) == null) {
                return;
            }
            ContextName cn = new ContextName(path, this.context.getWebappVersion());
            docBase = cn.getBaseName();
            String tmp = System.getProperty("java.io.tmpdir");
            File tmpFile = new File(tmp);
            if (!tmpFile.isDirectory()) {
                log.error((Object)sm.getString("contextConfig.noAntiLocking", new Object[]{tmp, this.context.getName()}));
                return;
            }
            this.antiLockingDocBase = this.originalDocBase.toLowerCase(Locale.ENGLISH).endsWith(".war") ? new File(tmpFile, deploymentCount++ + "-" + docBase + ".war") : new File(tmpFile, deploymentCount++ + "-" + docBase);
            this.antiLockingDocBase = this.antiLockingDocBase.getAbsoluteFile();
            if (log.isDebugEnabled()) {
                log.debug((Object)("Anti locking context[" + this.context.getName() + "] setting docBase to " + this.antiLockingDocBase.getPath()));
            }
            ExpandWar.delete(this.antiLockingDocBase);
            if (ExpandWar.copy(docBaseFile, this.antiLockingDocBase)) {
                this.context.setDocBase(this.antiLockingDocBase.getPath());
            }
        }
    }

    protected synchronized void init() {
        Digester contextDigester = null;
        if (!this.getUseGeneratedCode()) {
            contextDigester = this.createContextDigester();
            contextDigester.getParser();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("contextConfig.init"));
        }
        this.context.setConfigured(false);
        this.ok = true;
        this.contextConfig(contextDigester);
    }

    protected synchronized void beforeStart() {
        try {
            this.fixDocBase();
        }
        catch (IOException e) {
            log.error((Object)sm.getString("contextConfig.fixDocBase", new Object[]{this.context.getName()}), (Throwable)e);
        }
        this.antiLocking();
    }

    protected synchronized void configureStart() {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("contextConfig.start"));
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("contextConfig.xmlSettings", new Object[]{this.context.getName(), this.context.getXmlValidation(), this.context.getXmlNamespaceAware()}));
        }
        this.webConfig();
        if (!this.context.getIgnoreAnnotations()) {
            this.applicationAnnotationsConfig();
        }
        if (this.ok) {
            this.validateSecurityRoles();
        }
        if (this.ok) {
            this.authenticatorConfig();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Pipeline Configuration:");
            Pipeline pipeline = this.context.getPipeline();
            Valve[] valves = null;
            if (pipeline != null) {
                valves = pipeline.getValves();
            }
            if (valves != null) {
                for (Valve valve : valves) {
                    log.debug((Object)("  " + valve.getClass().getName()));
                }
            }
            log.debug((Object)"======================");
        }
        if (this.ok) {
            this.context.setConfigured(true);
        } else {
            log.error((Object)sm.getString("contextConfig.unavailable"));
            this.context.setConfigured(false);
        }
    }

    protected synchronized void configureStop() {
        int i;
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("contextConfig.stop"));
        }
        Container[] children = this.context.findChildren();
        for (i = 0; i < children.length; ++i) {
            this.context.removeChild(children[i]);
        }
        SecurityConstraint[] securityConstraints = this.context.findConstraints();
        for (i = 0; i < securityConstraints.length; ++i) {
            this.context.removeConstraint(securityConstraints[i]);
        }
        ErrorPage[] errorPages = this.context.findErrorPages();
        for (i = 0; i < errorPages.length; ++i) {
            this.context.removeErrorPage(errorPages[i]);
        }
        FilterDef[] filterDefs = this.context.findFilterDefs();
        for (i = 0; i < filterDefs.length; ++i) {
            this.context.removeFilterDef(filterDefs[i]);
        }
        FilterMap[] filterMaps = this.context.findFilterMaps();
        for (i = 0; i < filterMaps.length; ++i) {
            this.context.removeFilterMap(filterMaps[i]);
        }
        String[] mimeMappings = this.context.findMimeMappings();
        for (i = 0; i < mimeMappings.length; ++i) {
            this.context.removeMimeMapping(mimeMappings[i]);
        }
        String[] parameters = this.context.findParameters();
        for (i = 0; i < parameters.length; ++i) {
            this.context.removeParameter(parameters[i]);
        }
        String[] securityRoles = this.context.findSecurityRoles();
        for (i = 0; i < securityRoles.length; ++i) {
            this.context.removeSecurityRole(securityRoles[i]);
        }
        String[] servletMappings = this.context.findServletMappings();
        for (i = 0; i < servletMappings.length; ++i) {
            this.context.removeServletMapping(servletMappings[i]);
        }
        String[] welcomeFiles = this.context.findWelcomeFiles();
        for (i = 0; i < welcomeFiles.length; ++i) {
            this.context.removeWelcomeFile(welcomeFiles[i]);
        }
        String[] wrapperLifecycles = this.context.findWrapperLifecycles();
        for (i = 0; i < wrapperLifecycles.length; ++i) {
            this.context.removeWrapperLifecycle(wrapperLifecycles[i]);
        }
        String[] wrapperListeners = this.context.findWrapperListeners();
        for (i = 0; i < wrapperListeners.length; ++i) {
            this.context.removeWrapperListener(wrapperListeners[i]);
        }
        if (this.antiLockingDocBase != null) {
            ExpandWar.delete(this.antiLockingDocBase, false);
        }
        this.initializerClassMap.clear();
        this.typeInitializerMap.clear();
        this.ok = true;
    }

    protected synchronized void destroy() {
        String workDir;
        Server s;
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("contextConfig.destroy"));
        }
        if ((s = this.getServer()) != null && !s.getState().isAvailable()) {
            return;
        }
        if (this.context instanceof StandardContext && (workDir = ((StandardContext)this.context).getWorkPath()) != null) {
            ExpandWar.delete(new File(workDir));
        }
    }

    private Server getServer() {
        Container c;
        for (c = this.context; c != null && !(c instanceof Engine); c = c.getParent()) {
        }
        if (c == null) {
            return null;
        }
        Service s = ((Engine)c).getService();
        if (s == null) {
            return null;
        }
        return s.getServer();
    }

    protected void validateSecurityRoles() {
        Container[] wrappers;
        SecurityConstraint[] constraints;
        for (SecurityConstraint constraint : constraints = this.context.findConstraints()) {
            String[] roles;
            for (String role : roles = constraint.findAuthRoles()) {
                if ("*".equals(role) || this.context.findSecurityRole(role)) continue;
                log.warn((Object)sm.getString("contextConfig.role.auth", new Object[]{role}));
                this.context.addSecurityRole(role);
            }
        }
        for (Container container : wrappers = this.context.findChildren()) {
            String[] names;
            Wrapper wrapper = (Wrapper)container;
            String runAs = wrapper.getRunAs();
            if (runAs != null && !this.context.findSecurityRole(runAs)) {
                log.warn((Object)sm.getString("contextConfig.role.runas", new Object[]{runAs}));
                this.context.addSecurityRole(runAs);
            }
            for (String name : names = wrapper.findSecurityReferences()) {
                String link = wrapper.findSecurityReference(name);
                if (link == null || this.context.findSecurityRole(link)) continue;
                log.warn((Object)sm.getString("contextConfig.role.link", new Object[]{link}));
                this.context.addSecurityRole(link);
            }
        }
    }

    protected File getHostConfigBase() {
        File file = null;
        if (this.context.getParent() instanceof Host) {
            file = ((Host)this.context.getParent()).getConfigBaseFile();
        }
        return file;
    }

    protected void webConfig() {
        WebXmlParser webXmlParser = new WebXmlParser(this.context.getXmlNamespaceAware(), this.context.getXmlValidation(), this.context.getXmlBlockExternal());
        HashSet<WebXml> defaults = new HashSet<WebXml>();
        defaults.add(this.getDefaultWebXmlFragment(webXmlParser));
        HashSet<WebXml> tomcatWebXml = new HashSet<WebXml>();
        tomcatWebXml.add(this.getTomcatWebXmlFragment(webXmlParser));
        WebXml webXml = this.createWebXml();
        InputSource contextWebXml = this.getContextWebXmlSource();
        if (!webXmlParser.parseWebXml(contextWebXml, webXml, false)) {
            this.ok = false;
        }
        ServletContext sContext = this.context.getServletContext();
        Map<String, WebXml> fragments = this.processJarsForWebFragments(webXml, webXmlParser);
        Set orderedFragments = null;
        orderedFragments = WebXml.orderWebFragments((WebXml)webXml, fragments, (ServletContext)sContext);
        if (this.ok) {
            this.processServletContainerInitializers();
        }
        if (!webXml.isMetadataComplete() || this.typeInitializerMap.size() > 0) {
            this.processClasses(webXml, orderedFragments);
        }
        if (!webXml.isMetadataComplete()) {
            if (this.ok) {
                this.ok = webXml.merge(orderedFragments);
            }
            webXml.merge(tomcatWebXml);
            webXml.merge(defaults);
            if (this.ok) {
                this.convertJsps(webXml);
            }
            if (this.ok) {
                this.configureContext(webXml);
            }
        } else {
            webXml.merge(tomcatWebXml);
            webXml.merge(defaults);
            this.convertJsps(webXml);
            this.configureContext(webXml);
        }
        if (this.context.getLogEffectiveWebXml()) {
            log.info((Object)sm.getString("contextConfig.effectiveWebXml", new Object[]{webXml.toXml()}));
        }
        if (this.ok) {
            LinkedHashSet<WebXml> resourceJars = new LinkedHashSet<WebXml>(orderedFragments);
            for (WebXml fragment : fragments.values()) {
                if (resourceJars.contains(fragment)) continue;
                resourceJars.add(fragment);
            }
            this.processResourceJARs(resourceJars);
        }
        if (this.ok) {
            for (Map.Entry<ServletContainerInitializer, Set<Class<?>>> entry : this.initializerClassMap.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    this.context.addServletContainerInitializer(entry.getKey(), null);
                    continue;
                }
                this.context.addServletContainerInitializer(entry.getKey(), entry.getValue());
            }
        }
    }

    protected void processClasses(WebXml webXml, Set<WebXml> orderedFragments) {
        AbstractMap javaClassCache = this.context.getParallelAnnotationScanning() ? new ConcurrentHashMap() : new HashMap();
        if (this.ok) {
            WebResource[] webResources;
            for (WebResource webResource : webResources = this.context.getResources().listResources("/WEB-INF/classes")) {
                if ("META-INF".equals(webResource.getName())) continue;
                this.processAnnotationsWebResource(webResource, webXml, webXml.isMetadataComplete(), javaClassCache);
            }
        }
        if (this.ok) {
            this.processAnnotations(orderedFragments, webXml.isMetadataComplete(), javaClassCache);
        }
        javaClassCache.clear();
    }

    private void configureContext(WebXml webxml) {
        this.context.setPublicId(webxml.getPublicId());
        this.context.setEffectiveMajorVersion(webxml.getMajorVersion());
        this.context.setEffectiveMinorVersion(webxml.getMinorVersion());
        for (Map.Entry entry : webxml.getContextParams().entrySet()) {
            this.context.addParameter((String)entry.getKey(), (String)entry.getValue());
        }
        this.context.setDenyUncoveredHttpMethods(webxml.getDenyUncoveredHttpMethods());
        this.context.setDisplayName(webxml.getDisplayName());
        this.context.setDistributable(webxml.isDistributable());
        for (ContextLocalEjb ejbLocalRef : webxml.getEjbLocalRefs().values()) {
            this.context.getNamingResources().addLocalEjb(ejbLocalRef);
        }
        for (ContextEjb ejbRef : webxml.getEjbRefs().values()) {
            this.context.getNamingResources().addEjb(ejbRef);
        }
        for (ContextEnvironment environment : webxml.getEnvEntries().values()) {
            this.context.getNamingResources().addEnvironment(environment);
        }
        for (ErrorPage errorPage : webxml.getErrorPages().values()) {
            this.context.addErrorPage(errorPage);
        }
        for (FilterDef filter : webxml.getFilters().values()) {
            if (filter.getAsyncSupported() == null) {
                filter.setAsyncSupported("false");
            }
            this.context.addFilterDef(filter);
        }
        for (FilterMap filterMap : webxml.getFilterMappings()) {
            this.context.addFilterMap(filterMap);
        }
        this.context.setJspConfigDescriptor(webxml.getJspConfigDescriptor());
        for (String listener : webxml.getListeners()) {
            this.context.addApplicationListener(listener);
        }
        for (Map.Entry entry : webxml.getLocaleEncodingMappings().entrySet()) {
            this.context.addLocaleEncodingMappingParameter((String)entry.getKey(), (String)entry.getValue());
        }
        if (webxml.getLoginConfig() != null) {
            this.context.setLoginConfig(webxml.getLoginConfig());
        }
        for (MessageDestinationRef mdr : webxml.getMessageDestinationRefs().values()) {
            this.context.getNamingResources().addMessageDestinationRef(mdr);
        }
        this.context.setIgnoreAnnotations(webxml.isMetadataComplete());
        for (Map.Entry entry : webxml.getMimeMappings().entrySet()) {
            this.context.addMimeMapping((String)entry.getKey(), (String)entry.getValue());
        }
        this.context.setRequestCharacterEncoding(webxml.getRequestCharacterEncoding());
        for (Object resource : webxml.getResourceEnvRefs().values()) {
            this.context.getNamingResources().addResourceEnvRef((ContextResourceEnvRef)resource);
        }
        for (Object resource : webxml.getResourceRefs().values()) {
            this.context.getNamingResources().addResource((ContextResource)resource);
        }
        this.context.setResponseCharacterEncoding(webxml.getResponseCharacterEncoding());
        boolean allAuthenticatedUsersIsAppRole = webxml.getSecurityRoles().contains("**");
        for (SecurityConstraint securityConstraint : webxml.getSecurityConstraints()) {
            if (allAuthenticatedUsersIsAppRole) {
                securityConstraint.treatAllAuthenticatedUsersAsApplicationRole();
            }
            this.context.addConstraint(securityConstraint);
        }
        for (String string : webxml.getSecurityRoles()) {
            this.context.addSecurityRole(string);
        }
        for (ContextService contextService : webxml.getServiceRefs().values()) {
            this.context.getNamingResources().addService(contextService);
        }
        for (ServletDef servletDef : webxml.getServlets().values()) {
            Wrapper wrapper = this.context.createWrapper();
            if (servletDef.getLoadOnStartup() != null) {
                wrapper.setLoadOnStartup(servletDef.getLoadOnStartup());
            }
            if (servletDef.getEnabled() != null) {
                wrapper.setEnabled(servletDef.getEnabled());
            }
            wrapper.setName(servletDef.getServletName());
            Map params = servletDef.getParameterMap();
            for (Map.Entry entry : params.entrySet()) {
                wrapper.addInitParameter((String)entry.getKey(), (String)entry.getValue());
            }
            wrapper.setRunAs(servletDef.getRunAs());
            Set roleRefs = servletDef.getSecurityRoleRefs();
            for (SecurityRoleRef roleRef : roleRefs) {
                wrapper.addSecurityReference(roleRef.getName(), roleRef.getLink());
            }
            wrapper.setServletClass(servletDef.getServletClass());
            MultipartDef multipartDef = servletDef.getMultipartDef();
            if (multipartDef != null) {
                long maxFileSize = -1L;
                long maxRequestSize = -1L;
                int fileSizeThreshold = 0;
                if (null != multipartDef.getMaxFileSize()) {
                    maxFileSize = Long.parseLong(multipartDef.getMaxFileSize());
                }
                if (null != multipartDef.getMaxRequestSize()) {
                    maxRequestSize = Long.parseLong(multipartDef.getMaxRequestSize());
                }
                if (null != multipartDef.getFileSizeThreshold()) {
                    fileSizeThreshold = Integer.parseInt(multipartDef.getFileSizeThreshold());
                }
                wrapper.setMultipartConfigElement(new MultipartConfigElement(multipartDef.getLocation(), maxFileSize, maxRequestSize, fileSizeThreshold));
            }
            if (servletDef.getAsyncSupported() != null) {
                wrapper.setAsyncSupported(servletDef.getAsyncSupported());
            }
            wrapper.setOverridable(servletDef.isOverridable());
            this.context.addChild(wrapper);
        }
        for (Map.Entry entry : webxml.getServletMappings().entrySet()) {
            this.context.addServletMappingDecoded((String)entry.getKey(), (String)entry.getValue());
        }
        SessionConfig sessionConfig = webxml.getSessionConfig();
        if (sessionConfig != null) {
            if (sessionConfig.getSessionTimeout() != null) {
                this.context.setSessionTimeout(sessionConfig.getSessionTimeout());
            }
            SessionCookieConfig sessionCookieConfig = this.context.getServletContext().getSessionCookieConfig();
            sessionCookieConfig.setName(sessionConfig.getCookieName());
            sessionCookieConfig.setDomain(sessionConfig.getCookieDomain());
            sessionCookieConfig.setPath(sessionConfig.getCookiePath());
            sessionCookieConfig.setComment(sessionConfig.getCookieComment());
            if (sessionConfig.getCookieHttpOnly() != null) {
                sessionCookieConfig.setHttpOnly(sessionConfig.getCookieHttpOnly().booleanValue());
            }
            if (sessionConfig.getCookieSecure() != null) {
                sessionCookieConfig.setSecure(sessionConfig.getCookieSecure().booleanValue());
            }
            if (sessionConfig.getCookieMaxAge() != null) {
                sessionCookieConfig.setMaxAge(sessionConfig.getCookieMaxAge().intValue());
            }
            if (sessionConfig.getSessionTrackingModes().size() > 0) {
                this.context.getServletContext().setSessionTrackingModes((Set)sessionConfig.getSessionTrackingModes());
            }
        }
        for (String string : webxml.getWelcomeFiles()) {
            if (string == null || string.length() <= 0) continue;
            this.context.addWelcomeFile(string);
        }
        for (JspPropertyGroup jspPropertyGroup : webxml.getJspPropertyGroups()) {
            String jspServletName = this.context.findServletMapping("*.jsp");
            if (jspServletName == null) {
                jspServletName = "jsp";
            }
            if (this.context.findChild(jspServletName) != null) {
                for (String string : jspPropertyGroup.getUrlPatterns()) {
                    this.context.addServletMappingDecoded(string, jspServletName, true);
                }
                continue;
            }
            if (!log.isDebugEnabled()) continue;
            for (String string : jspPropertyGroup.getUrlPatterns()) {
                log.debug((Object)("Skipping " + string + " , no servlet " + jspServletName));
            }
        }
        for (Map.Entry entry : webxml.getPostConstructMethods().entrySet()) {
            this.context.addPostConstructMethod((String)entry.getKey(), (String)entry.getValue());
        }
        for (Map.Entry entry : webxml.getPreDestroyMethods().entrySet()) {
            this.context.addPreDestroyMethod((String)entry.getKey(), (String)entry.getValue());
        }
    }

    private WebXml getTomcatWebXmlFragment(WebXmlParser webXmlParser) {
        WebXml webXmlTomcatFragment = this.createWebXml();
        webXmlTomcatFragment.setOverridable(true);
        webXmlTomcatFragment.setDistributable(true);
        webXmlTomcatFragment.setAlwaysAddWelcomeFiles(false);
        WebResource resource = this.context.getResources().getResource("/WEB-INF/tomcat-web.xml");
        if (resource.isFile()) {
            try {
                InputSource source = new InputSource(resource.getURL().toURI().toString());
                source.setByteStream(resource.getInputStream());
                if (!webXmlParser.parseWebXml(source, webXmlTomcatFragment, false)) {
                    this.ok = false;
                }
            }
            catch (URISyntaxException e) {
                log.error((Object)sm.getString("contextConfig.tomcatWebXmlError"), (Throwable)e);
            }
        }
        return webXmlTomcatFragment;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private WebXml getDefaultWebXmlFragment(WebXmlParser webXmlParser) {
        URL url;
        URI uri;
        URLConnection uc;
        Host host = (Host)this.context.getParent();
        DefaultWebXmlCacheEntry entry = hostWebXmlCache.get(host);
        InputSource globalWebXml = this.getGlobalWebXmlSource();
        InputSource hostWebXml = this.getHostWebXmlSource();
        long globalTimeStamp = 0L;
        long hostTimeStamp = 0L;
        if (globalWebXml != null) {
            uc = null;
            try {
                uri = new URI(globalWebXml.getSystemId());
                url = uri.toURL();
                uc = url.openConnection();
                globalTimeStamp = uc.getLastModified();
            }
            catch (IOException | IllegalArgumentException | URISyntaxException e) {
                globalTimeStamp = -1L;
            }
            finally {
                if (uc != null) {
                    try {
                        uc.getInputStream().close();
                    }
                    catch (IOException e) {
                        ExceptionUtils.handleThrowable((Throwable)e);
                        globalTimeStamp = -1L;
                    }
                }
            }
        }
        if (hostWebXml != null) {
            uc = null;
            try {
                uri = new URI(hostWebXml.getSystemId());
                url = uri.toURL();
                uc = url.openConnection();
                hostTimeStamp = uc.getLastModified();
            }
            catch (IOException | IllegalArgumentException | URISyntaxException e) {
                hostTimeStamp = -1L;
            }
            finally {
                if (uc != null) {
                    try {
                        uc.getInputStream().close();
                    }
                    catch (IOException e) {
                        ExceptionUtils.handleThrowable((Throwable)e);
                        hostTimeStamp = -1L;
                    }
                }
            }
        }
        if (entry != null && entry.getGlobalTimeStamp() == globalTimeStamp && entry.getHostTimeStamp() == hostTimeStamp) {
            InputSourceUtil.close((InputSource)globalWebXml);
            InputSourceUtil.close((InputSource)hostWebXml);
            return entry.getWebXml();
        }
        Pipeline pipeline = host.getPipeline();
        synchronized (pipeline) {
            entry = hostWebXmlCache.get(host);
            if (entry != null && entry.getGlobalTimeStamp() == globalTimeStamp && entry.getHostTimeStamp() == hostTimeStamp) {
                return entry.getWebXml();
            }
            WebXml webXmlDefaultFragment = this.createWebXml();
            webXmlDefaultFragment.setOverridable(true);
            webXmlDefaultFragment.setDistributable(true);
            webXmlDefaultFragment.setAlwaysAddWelcomeFiles(false);
            if (globalWebXml == null) {
                log.info((Object)sm.getString("contextConfig.defaultMissing"));
            } else if (!webXmlParser.parseWebXml(globalWebXml, webXmlDefaultFragment, false)) {
                this.ok = false;
            }
            webXmlDefaultFragment.setReplaceWelcomeFiles(true);
            if (!webXmlParser.parseWebXml(hostWebXml, webXmlDefaultFragment, false)) {
                this.ok = false;
            }
            if (globalTimeStamp != -1L && hostTimeStamp != -1L) {
                entry = new DefaultWebXmlCacheEntry(webXmlDefaultFragment, globalTimeStamp, hostTimeStamp);
                hostWebXmlCache.put(host, entry);
                host.addLifecycleListener(new HostWebXmlCacheCleaner());
            }
            return webXmlDefaultFragment;
        }
    }

    private void convertJsps(WebXml webXml) {
        HashMap<String, String> jspInitParams;
        ServletDef jspServlet = (ServletDef)webXml.getServlets().get("jsp");
        if (jspServlet == null) {
            jspInitParams = new HashMap<String, String>();
            Wrapper w = (Wrapper)this.context.findChild("jsp");
            if (w != null) {
                String[] params;
                for (String param : params = w.findInitParameters()) {
                    jspInitParams.put(param, w.findInitParameter(param));
                }
            }
        } else {
            jspInitParams = jspServlet.getParameterMap();
        }
        for (ServletDef servletDef : webXml.getServlets().values()) {
            if (servletDef.getJspFile() == null) continue;
            this.convertJsp(servletDef, jspInitParams);
        }
    }

    private void convertJsp(ServletDef servletDef, Map<String, String> jspInitParams) {
        servletDef.setServletClass("org.apache.jasper.servlet.JspServlet");
        String jspFile = servletDef.getJspFile();
        if (jspFile != null && !jspFile.startsWith("/")) {
            if (this.context.isServlet22()) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("contextConfig.jspFile.warning", new Object[]{jspFile}));
                }
                jspFile = "/" + jspFile;
            } else {
                throw new IllegalArgumentException(sm.getString("contextConfig.jspFile.error", new Object[]{jspFile}));
            }
        }
        servletDef.getParameterMap().put("jspFile", jspFile);
        servletDef.setJspFile(null);
        for (Map.Entry<String, String> initParam : jspInitParams.entrySet()) {
            servletDef.addInitParameter(initParam.getKey(), initParam.getValue());
        }
    }

    protected WebXml createWebXml() {
        return new WebXml();
    }

    protected void processServletContainerInitializers() {
        List<ServletContainerInitializer> detectedScis;
        try {
            WebappServiceLoader<ServletContainerInitializer> loader = new WebappServiceLoader<ServletContainerInitializer>(this.context);
            detectedScis = loader.load(ServletContainerInitializer.class);
        }
        catch (IOException e) {
            log.error((Object)sm.getString("contextConfig.servletContainerInitializerFail", new Object[]{this.context.getName()}), (Throwable)e);
            this.ok = false;
            return;
        }
        for (ServletContainerInitializer sci : detectedScis) {
            Class[] types;
            HandlesTypes ht;
            this.initializerClassMap.put(sci, new HashSet());
            try {
                ht = sci.getClass().getAnnotation(HandlesTypes.class);
            }
            catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.info((Object)sm.getString("contextConfig.sci.debug", new Object[]{sci.getClass().getName()}), (Throwable)e);
                    continue;
                }
                log.info((Object)sm.getString("contextConfig.sci.info", new Object[]{sci.getClass().getName()}));
                continue;
            }
            if (ht == null || (types = ht.value()) == null) continue;
            for (Class type : types) {
                if (type.isAnnotation()) {
                    this.handlesTypesAnnotations = true;
                } else {
                    this.handlesTypesNonAnnotations = true;
                }
                this.typeInitializerMap.computeIfAbsent(type, k -> new HashSet()).add(sci);
            }
        }
    }

    protected void processResourceJARs(Set<WebXml> fragments) {
        block7: for (WebXml fragment : fragments) {
            URL url = fragment.getURL();
            try {
                File file;
                File resources;
                if ("jar".equals(url.getProtocol()) || url.toString().endsWith(".jar")) {
                    Jar jar = JarFactory.newInstance((URL)url);
                    try {
                        jar.nextEntry();
                        String entryName = jar.getEntryName();
                        while (entryName != null) {
                            if (entryName.startsWith("META-INF/resources/")) {
                                this.context.getResources().createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", url, "/META-INF/resources");
                                continue block7;
                            }
                            jar.nextEntry();
                            entryName = jar.getEntryName();
                        }
                        continue;
                    }
                    finally {
                        if (jar != null) {
                            jar.close();
                        }
                        continue;
                    }
                }
                if (!"file".equals(url.getProtocol()) || !(resources = new File(file = new File(url.toURI()), "META-INF/resources/")).isDirectory()) continue;
                this.context.getResources().createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", resources.getAbsolutePath(), null, "/");
            }
            catch (IOException | URISyntaxException e) {
                log.error((Object)sm.getString("contextConfig.resourceJarFail", new Object[]{url, this.context.getName()}));
            }
        }
    }

    protected InputSource getGlobalWebXmlSource() {
        if (this.defaultWebXml == null && this.context instanceof StandardContext) {
            this.defaultWebXml = ((StandardContext)this.context).getDefaultWebXml();
        }
        if (this.defaultWebXml == null) {
            this.getDefaultWebXml();
        }
        if ("org/apache/catalina/startup/NO_DEFAULT_XML".equals(this.defaultWebXml)) {
            return null;
        }
        return this.getWebXmlSource(this.defaultWebXml, true);
    }

    protected InputSource getHostWebXmlSource() {
        File hostConfigBase = this.getHostConfigBase();
        if (hostConfigBase == null) {
            return null;
        }
        return this.getWebXmlSource(hostConfigBase.getPath(), false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected InputSource getContextWebXmlSource() {
        InputStream stream = null;
        InputSource source = null;
        URL url = null;
        String altDDName = null;
        ServletContext servletContext = this.context.getServletContext();
        try {
            if (servletContext != null) {
                altDDName = (String)servletContext.getAttribute("org.apache.catalina.deploy.alt_dd");
                if (altDDName != null) {
                    try {
                        stream = new FileInputStream(altDDName);
                        url = new File(altDDName).toURI().toURL();
                    }
                    catch (FileNotFoundException e) {
                        log.error((Object)sm.getString("contextConfig.altDDNotFound", new Object[]{altDDName}));
                    }
                    catch (MalformedURLException e) {
                        log.error((Object)sm.getString("contextConfig.applicationUrl"));
                    }
                } else {
                    stream = servletContext.getResourceAsStream("/WEB-INF/web.xml");
                    try {
                        url = servletContext.getResource("/WEB-INF/web.xml");
                    }
                    catch (MalformedURLException e) {
                        log.error((Object)sm.getString("contextConfig.applicationUrl"));
                    }
                }
            }
            if (stream == null || url == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)(sm.getString("contextConfig.applicationMissing") + " " + this.context));
                }
            } else {
                source = new InputSource(url.toExternalForm());
                source.setByteStream(stream);
            }
        }
        finally {
            if (source == null && stream != null) {
                try {
                    stream.close();
                }
                catch (IOException iOException) {}
            }
        }
        return source;
    }

    public String getConfigBasePath() {
        String path = null;
        if (this.context.getParent() instanceof Host) {
            Host host = (Host)this.context.getParent();
            if (host.getXmlBase() != null) {
                path = host.getXmlBase();
            } else {
                StringBuilder xmlDir = new StringBuilder("conf");
                Container parent = host.getParent();
                if (parent instanceof Engine) {
                    xmlDir.append('/');
                    xmlDir.append(parent.getName());
                }
                xmlDir.append('/');
                xmlDir.append(host.getName());
                path = xmlDir.toString();
            }
        }
        return path;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected InputSource getWebXmlSource(String filename, boolean global) {
        ConfigurationSource.Resource webXmlResource = null;
        try {
            if (global) {
                webXmlResource = "conf/web.xml".equals(filename) ? ConfigFileLoader.getSource().getSharedWebXml() : ConfigFileLoader.getSource().getResource(filename);
            } else {
                String hostWebXml = Container.getConfigPath(this.context, "web.xml.default");
                webXmlResource = ConfigFileLoader.getSource().getResource(hostWebXml);
            }
        }
        catch (IOException e) {
            return null;
        }
        InputStream stream = null;
        InputSource source = null;
        try {
            stream = webXmlResource.getInputStream();
            source = new InputSource(webXmlResource.getURI().toString());
            if (stream != null) {
                source.setByteStream(stream);
            }
        }
        catch (Exception e) {
            log.error((Object)sm.getString("contextConfig.defaultError", new Object[]{filename, webXmlResource.getURI()}), (Throwable)e);
        }
        finally {
            if (source == null && stream != null) {
                try {
                    stream.close();
                }
                catch (IOException iOException) {}
            }
        }
        return source;
    }

    protected Map<String, WebXml> processJarsForWebFragments(WebXml application, WebXmlParser webXmlParser) {
        JarScanner jarScanner = this.context.getJarScanner();
        boolean delegate = false;
        if (this.context instanceof StandardContext) {
            delegate = ((StandardContext)this.context).getDelegate();
        }
        boolean parseRequired = true;
        Set absoluteOrder = application.getAbsoluteOrdering();
        if (absoluteOrder != null && absoluteOrder.isEmpty() && !this.context.getXmlValidation()) {
            parseRequired = false;
        }
        FragmentJarScannerCallback callback = new FragmentJarScannerCallback(webXmlParser, delegate, parseRequired);
        jarScanner.scan(JarScanType.PLUGGABILITY, this.context.getServletContext(), (JarScannerCallback)callback);
        if (!callback.isOk()) {
            this.ok = false;
        }
        return callback.getFragments();
    }

    protected void processAnnotations(Set<WebXml> fragments, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        if (this.context.getParallelAnnotationScanning()) {
            this.processAnnotationsInParallel(fragments, handlesTypesOnly, javaClassCache);
        } else {
            for (WebXml fragment : fragments) {
                this.scanWebXmlFragment(handlesTypesOnly, fragment, javaClassCache);
            }
        }
    }

    private void scanWebXmlFragment(boolean handlesTypesOnly, WebXml fragment, Map<String, JavaClassCacheEntry> javaClassCache) {
        boolean htOnly = handlesTypesOnly || !fragment.getWebappJar() || fragment.isMetadataComplete();
        WebXml annotations = new WebXml();
        annotations.setDistributable(true);
        URL url = fragment.getURL();
        this.processAnnotationsUrl(url, annotations, htOnly, javaClassCache);
        HashSet<WebXml> set = new HashSet<WebXml>();
        set.add(annotations);
        fragment.merge(set);
    }

    protected void processAnnotationsInParallel(Set<WebXml> fragments, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        ScheduledExecutorService pool;
        Server s = this.getServer();
        ScheduledExecutorService scheduledExecutorService = pool = s == null ? null : s.getUtilityExecutor();
        if (pool != null) {
            ArrayList futures = new ArrayList(fragments.size());
            for (WebXml webXml : fragments) {
                AnnotationScanTask task = new AnnotationScanTask(webXml, handlesTypesOnly, javaClassCache);
                futures.add(pool.submit(task));
            }
            try {
                for (Future future : futures) {
                    future.get();
                }
            }
            catch (Exception e) {
                throw new RuntimeException(sm.getString("contextConfig.processAnnotationsInParallelFailure"), e);
            }
        } else {
            for (WebXml fragment : fragments) {
                this.scanWebXmlFragment(handlesTypesOnly, fragment, javaClassCache);
            }
        }
    }

    protected void processAnnotationsWebResource(WebResource webResource, WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        if (webResource.isDirectory()) {
            WebResource[] webResources = webResource.getWebResourceRoot().listResources(webResource.getWebappPath());
            if (webResources.length > 0) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("contextConfig.processAnnotationsWebDir.debug", new Object[]{webResource.getURL()}));
                }
                for (WebResource r : webResources) {
                    this.processAnnotationsWebResource(r, fragment, handlesTypesOnly, javaClassCache);
                }
            }
        } else if (webResource.isFile() && webResource.getName().endsWith(".class")) {
            try (InputStream is = webResource.getInputStream();){
                this.processAnnotationsStream(is, fragment, handlesTypesOnly, javaClassCache);
            }
            catch (IOException | ClassFormatException e) {
                log.error((Object)sm.getString("contextConfig.inputStreamWebResource", new Object[]{webResource.getWebappPath()}), e);
            }
        }
    }

    protected void processAnnotationsUrl(URL url, WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        if (url == null) {
            return;
        }
        if ("jar".equals(url.getProtocol()) || url.toString().endsWith(".jar")) {
            this.processAnnotationsJar(url, fragment, handlesTypesOnly, javaClassCache);
        } else if ("file".equals(url.getProtocol())) {
            try {
                this.processAnnotationsFile(new File(url.toURI()), fragment, handlesTypesOnly, javaClassCache);
            }
            catch (URISyntaxException e) {
                log.error((Object)sm.getString("contextConfig.fileUrl", new Object[]{url}), (Throwable)e);
            }
        } else {
            log.error((Object)sm.getString("contextConfig.unknownUrlProtocol", new Object[]{url.getProtocol(), url}));
        }
    }

    protected void processAnnotationsJar(URL url, WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        try (Jar jar = JarFactory.newInstance((URL)url);){
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("contextConfig.processAnnotationsJar.debug", new Object[]{url}));
            }
            jar.nextEntry();
            String entryName = jar.getEntryName();
            while (entryName != null) {
                if (entryName.endsWith(".class")) {
                    try (InputStream is = jar.getEntryInputStream();){
                        this.processAnnotationsStream(is, fragment, handlesTypesOnly, javaClassCache);
                    }
                    catch (IOException | ClassFormatException e) {
                        log.error((Object)sm.getString("contextConfig.inputStreamJar", new Object[]{entryName, url}), e);
                    }
                }
                jar.nextEntry();
                entryName = jar.getEntryName();
            }
        }
        catch (IOException e) {
            log.error((Object)sm.getString("contextConfig.jarFile", new Object[]{url}), (Throwable)e);
        }
    }

    protected void processAnnotationsFile(File file, WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
        if (file.isDirectory()) {
            String[] dirs = file.list();
            if (dirs != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("contextConfig.processAnnotationsDir.debug", new Object[]{file}));
                }
                for (String dir : dirs) {
                    this.processAnnotationsFile(new File(file, dir), fragment, handlesTypesOnly, javaClassCache);
                }
            }
        } else if (file.getName().endsWith(".class") && file.canRead()) {
            try (FileInputStream fis = new FileInputStream(file);){
                this.processAnnotationsStream(fis, fragment, handlesTypesOnly, javaClassCache);
            }
            catch (IOException | ClassFormatException e) {
                log.error((Object)sm.getString("contextConfig.inputStreamFile", new Object[]{file.getAbsolutePath()}), e);
            }
        }
    }

    protected void processAnnotationsStream(InputStream is, WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) throws ClassFormatException, IOException {
        ClassParser parser = new ClassParser(is);
        JavaClass clazz = parser.parse();
        this.checkHandlesTypes(clazz, javaClassCache);
        if (handlesTypesOnly) {
            return;
        }
        this.processClass(fragment, clazz);
    }

    protected void processClass(WebXml fragment, JavaClass clazz) {
        AnnotationEntry[] annotationsEntries = clazz.getAnnotationEntries();
        if (annotationsEntries != null) {
            String className = clazz.getClassName();
            for (AnnotationEntry ae : annotationsEntries) {
                String type = ae.getAnnotationType();
                if ("Ljavax/servlet/annotation/WebServlet;".equals(type)) {
                    this.processAnnotationWebServlet(className, ae, fragment);
                    continue;
                }
                if ("Ljavax/servlet/annotation/WebFilter;".equals(type)) {
                    this.processAnnotationWebFilter(className, ae, fragment);
                    continue;
                }
                if (!"Ljavax/servlet/annotation/WebListener;".equals(type)) continue;
                fragment.addListener(className);
            }
        }
    }

    protected void checkHandlesTypes(JavaClass javaClass, Map<String, JavaClassCacheEntry> javaClassCache) {
        AnnotationEntry[] annotationEntries;
        if (this.typeInitializerMap.size() == 0) {
            return;
        }
        if ((javaClass.getAccessFlags() & 0x2000) != 0) {
            return;
        }
        String className = javaClass.getClassName();
        Class<?> clazz = null;
        if (this.handlesTypesNonAnnotations) {
            this.populateJavaClassCache(className, javaClass, javaClassCache);
            JavaClassCacheEntry entry = javaClassCache.get(className);
            if (entry.getSciSet() == null) {
                try {
                    this.populateSCIsForCacheEntry(entry, javaClassCache);
                }
                catch (StackOverflowError soe) {
                    throw new IllegalStateException(sm.getString("contextConfig.annotationsStackOverflow", new Object[]{this.context.getName(), this.classHierarchyToString(className, entry, javaClassCache)}));
                }
            }
            if (!entry.getSciSet().isEmpty()) {
                clazz = Introspection.loadClass(this.context, className);
                if (clazz == null) {
                    return;
                }
                for (ServletContainerInitializer sci : entry.getSciSet()) {
                    Set classes = this.initializerClassMap.computeIfAbsent(sci, k -> new HashSet());
                    classes.add(clazz);
                }
            }
        }
        if (this.handlesTypesAnnotations && (annotationEntries = javaClass.getAllAnnotationEntries()) != null) {
            block3: for (Map.Entry<Class<?>, Set<ServletContainerInitializer>> entry : this.typeInitializerMap.entrySet()) {
                if (!entry.getKey().isAnnotation()) continue;
                String entryClassName = entry.getKey().getName();
                for (AnnotationEntry annotationEntry : annotationEntries) {
                    if (!entryClassName.equals(ContextConfig.getClassName(annotationEntry.getAnnotationType()))) continue;
                    if (clazz == null && (clazz = Introspection.loadClass(this.context, className)) == null) {
                        return;
                    }
                    for (ServletContainerInitializer sci : entry.getValue()) {
                        this.initializerClassMap.get(sci).add(clazz);
                    }
                    continue block3;
                }
            }
        }
    }

    private String classHierarchyToString(String className, JavaClassCacheEntry entry, Map<String, JavaClassCacheEntry> javaClassCache) {
        JavaClassCacheEntry start = entry;
        StringBuilder msg = new StringBuilder(className);
        msg.append("->");
        String parentName = entry.getSuperclassName();
        JavaClassCacheEntry parent = javaClassCache.get(parentName);
        for (int count = 0; count < 100 && parent != null && parent != start; ++count) {
            msg.append(parentName);
            msg.append("->");
            parentName = parent.getSuperclassName();
            parent = javaClassCache.get(parentName);
        }
        msg.append(parentName);
        return msg.toString();
    }

    private void populateJavaClassCache(String className, JavaClass javaClass, Map<String, JavaClassCacheEntry> javaClassCache) {
        if (javaClassCache.containsKey(className)) {
            return;
        }
        javaClassCache.put(className, new JavaClassCacheEntry(javaClass));
        this.populateJavaClassCache(javaClass.getSuperclassName(), javaClassCache);
        for (String interfaceName : javaClass.getInterfaceNames()) {
            this.populateJavaClassCache(interfaceName, javaClassCache);
        }
    }

    private void populateJavaClassCache(String className, Map<String, JavaClassCacheEntry> javaClassCache) {
        if (!javaClassCache.containsKey(className)) {
            String name = className.replace('.', '/') + ".class";
            try (InputStream is = this.context.getLoader().getClassLoader().getResourceAsStream(name);){
                if (is == null) {
                    return;
                }
                ClassParser parser = new ClassParser(is);
                JavaClass clazz = parser.parse();
                this.populateJavaClassCache(clazz.getClassName(), clazz, javaClassCache);
            }
            catch (IOException | ClassFormatException e) {
                log.debug((Object)sm.getString("contextConfig.invalidSciHandlesTypes", new Object[]{className}), e);
            }
        }
    }

    private void populateSCIsForCacheEntry(JavaClassCacheEntry cacheEntry, Map<String, JavaClassCacheEntry> javaClassCache) {
        HashSet<ServletContainerInitializer> result = new HashSet<ServletContainerInitializer>();
        String superClassName = cacheEntry.getSuperclassName();
        JavaClassCacheEntry superClassCacheEntry = javaClassCache.get(superClassName);
        if (cacheEntry.equals(superClassCacheEntry)) {
            cacheEntry.setSciSet(EMPTY_SCI_SET);
            return;
        }
        if (superClassCacheEntry != null) {
            if (superClassCacheEntry.getSciSet() == null) {
                this.populateSCIsForCacheEntry(superClassCacheEntry, javaClassCache);
            }
            result.addAll(superClassCacheEntry.getSciSet());
        }
        result.addAll(this.getSCIsForClass(superClassName));
        for (String interfaceName : cacheEntry.getInterfaceNames()) {
            JavaClassCacheEntry interfaceEntry = javaClassCache.get(interfaceName);
            if (interfaceEntry != null) {
                if (interfaceEntry.getSciSet() == null) {
                    this.populateSCIsForCacheEntry(interfaceEntry, javaClassCache);
                }
                result.addAll(interfaceEntry.getSciSet());
            }
            result.addAll(this.getSCIsForClass(interfaceName));
        }
        cacheEntry.setSciSet(result.isEmpty() ? EMPTY_SCI_SET : result);
    }

    private Set<ServletContainerInitializer> getSCIsForClass(String className) {
        for (Map.Entry<Class<?>, Set<ServletContainerInitializer>> entry : this.typeInitializerMap.entrySet()) {
            Class<?> clazz = entry.getKey();
            if (clazz.isAnnotation() || !clazz.getName().equals(className)) continue;
            return entry.getValue();
        }
        return EMPTY_SCI_SET;
    }

    private static String getClassName(String internalForm) {
        if (!internalForm.startsWith("L")) {
            return internalForm;
        }
        return internalForm.substring(1, internalForm.length() - 1).replace('/', '.');
    }

    protected void processAnnotationWebServlet(String className, AnnotationEntry ae, WebXml fragment) {
        boolean isWebXMLservletDef;
        ServletDef servletDef;
        String servletName = null;
        List evps = ae.getElementValuePairs();
        for (ElementValuePair evp : evps) {
            String name = evp.getNameString();
            if (!"name".equals(name)) continue;
            servletName = evp.getValue().stringifyValue();
            break;
        }
        if (servletName == null) {
            servletName = className;
        }
        if ((servletDef = (ServletDef)fragment.getServlets().get(servletName)) == null) {
            servletDef = new ServletDef();
            servletDef.setServletName(servletName);
            servletDef.setServletClass(className);
            isWebXMLservletDef = false;
        } else {
            isWebXMLservletDef = true;
        }
        boolean urlPatternsSet = false;
        String[] urlPatterns = null;
        for (ElementValuePair evp : evps) {
            String name = evp.getNameString();
            if ("value".equals(name) || "urlPatterns".equals(name)) {
                if (urlPatternsSet) {
                    throw new IllegalArgumentException(sm.getString("contextConfig.urlPatternValue", new Object[]{"WebServlet", className}));
                }
                urlPatternsSet = true;
                urlPatterns = this.processAnnotationsStringArray(evp.getValue());
                continue;
            }
            if ("description".equals(name)) {
                if (servletDef.getDescription() != null) continue;
                servletDef.setDescription(evp.getValue().stringifyValue());
                continue;
            }
            if ("displayName".equals(name)) {
                if (servletDef.getDisplayName() != null) continue;
                servletDef.setDisplayName(evp.getValue().stringifyValue());
                continue;
            }
            if ("largeIcon".equals(name)) {
                if (servletDef.getLargeIcon() != null) continue;
                servletDef.setLargeIcon(evp.getValue().stringifyValue());
                continue;
            }
            if ("smallIcon".equals(name)) {
                if (servletDef.getSmallIcon() != null) continue;
                servletDef.setSmallIcon(evp.getValue().stringifyValue());
                continue;
            }
            if ("asyncSupported".equals(name)) {
                if (servletDef.getAsyncSupported() != null) continue;
                servletDef.setAsyncSupported(evp.getValue().stringifyValue());
                continue;
            }
            if ("loadOnStartup".equals(name)) {
                if (servletDef.getLoadOnStartup() != null) continue;
                servletDef.setLoadOnStartup(evp.getValue().stringifyValue());
                continue;
            }
            if (!"initParams".equals(name)) continue;
            Map<String, String> map = this.processAnnotationWebInitParams(evp.getValue());
            if (isWebXMLservletDef) {
                Map webXMLInitParams = servletDef.getParameterMap();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (webXMLInitParams.get(entry.getKey()) != null) continue;
                    servletDef.addInitParameter(entry.getKey(), entry.getValue());
                }
                continue;
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                servletDef.addInitParameter(entry.getKey(), entry.getValue());
            }
        }
        if (!isWebXMLservletDef && urlPatterns != null) {
            fragment.addServlet(servletDef);
        }
        if (urlPatterns != null && !fragment.getServletMappings().containsValue(servletName)) {
            for (Iterator iterator : urlPatterns) {
                fragment.addServletMapping(iterator, servletName);
            }
        }
    }

    protected void processAnnotationWebFilter(String className, AnnotationEntry ae, WebXml fragment) {
        boolean isWebXMLfilterDef;
        String filterName = null;
        List evps = ae.getElementValuePairs();
        for (ElementValuePair evp : evps) {
            String name = evp.getNameString();
            if (!"filterName".equals(name)) continue;
            filterName = evp.getValue().stringifyValue();
            break;
        }
        if (filterName == null) {
            filterName = className;
        }
        FilterDef filterDef = (FilterDef)fragment.getFilters().get(filterName);
        FilterMap filterMap = new FilterMap();
        if (filterDef == null) {
            filterDef = new FilterDef();
            filterDef.setFilterName(filterName);
            filterDef.setFilterClass(className);
            isWebXMLfilterDef = false;
        } else {
            isWebXMLfilterDef = true;
        }
        boolean urlPatternsSet = false;
        boolean servletNamesSet = false;
        boolean dispatchTypesSet = false;
        String[] urlPatterns = null;
        for (ElementValuePair evp : evps) {
            int urlPattern2;
            String name = evp.getNameString();
            if ("value".equals(name) || "urlPatterns".equals(name)) {
                if (urlPatternsSet) {
                    throw new IllegalArgumentException(sm.getString("contextConfig.urlPatternValue", new Object[]{"WebFilter", className}));
                }
                urlPatterns = this.processAnnotationsStringArray(evp.getValue());
                urlPatternsSet = urlPatterns.length > 0;
                for (String urlPattern2 : urlPatterns) {
                    filterMap.addURLPattern(urlPattern2);
                }
                continue;
            }
            if ("servletNames".equals(name)) {
                String[] servletNames = this.processAnnotationsStringArray(evp.getValue());
                servletNamesSet = servletNames.length > 0;
                String[] stringArray = servletNames;
                int n = stringArray.length;
                for (urlPattern2 = 0; urlPattern2 < n; ++urlPattern2) {
                    String servletName = stringArray[urlPattern2];
                    filterMap.addServletName(servletName);
                }
                continue;
            }
            if ("dispatcherTypes".equals(name)) {
                String[] dispatcherTypes = this.processAnnotationsStringArray(evp.getValue());
                dispatchTypesSet = dispatcherTypes.length > 0;
                String[] stringArray = dispatcherTypes;
                int n = stringArray.length;
                for (urlPattern2 = 0; urlPattern2 < n; ++urlPattern2) {
                    String dispatcherType = stringArray[urlPattern2];
                    filterMap.setDispatcher(dispatcherType);
                }
                continue;
            }
            if ("description".equals(name)) {
                if (filterDef.getDescription() != null) continue;
                filterDef.setDescription(evp.getValue().stringifyValue());
                continue;
            }
            if ("displayName".equals(name)) {
                if (filterDef.getDisplayName() != null) continue;
                filterDef.setDisplayName(evp.getValue().stringifyValue());
                continue;
            }
            if ("largeIcon".equals(name)) {
                if (filterDef.getLargeIcon() != null) continue;
                filterDef.setLargeIcon(evp.getValue().stringifyValue());
                continue;
            }
            if ("smallIcon".equals(name)) {
                if (filterDef.getSmallIcon() != null) continue;
                filterDef.setSmallIcon(evp.getValue().stringifyValue());
                continue;
            }
            if ("asyncSupported".equals(name)) {
                if (filterDef.getAsyncSupported() != null) continue;
                filterDef.setAsyncSupported(evp.getValue().stringifyValue());
                continue;
            }
            if (!"initParams".equals(name)) continue;
            Map<String, String> initParams = this.processAnnotationWebInitParams(evp.getValue());
            if (isWebXMLfilterDef) {
                Map webXMLInitParams = filterDef.getParameterMap();
                for (Map.Entry<String, String> entry : initParams.entrySet()) {
                    if (webXMLInitParams.get(entry.getKey()) != null) continue;
                    filterDef.addInitParameter(entry.getKey(), entry.getValue());
                }
                continue;
            }
            for (Map.Entry<String, String> entry : initParams.entrySet()) {
                filterDef.addInitParameter(entry.getKey(), entry.getValue());
            }
        }
        if (!isWebXMLfilterDef) {
            fragment.addFilter(filterDef);
            if (urlPatternsSet || servletNamesSet) {
                filterMap.setFilterName(filterName);
                fragment.addFilterMapping(filterMap);
            }
        }
        if (urlPatternsSet || dispatchTypesSet) {
            Set fmap = fragment.getFilterMappings();
            String[] descMap = null;
            for (String[] map : fmap) {
                if (!filterName.equals(map.getFilterName())) continue;
                descMap = map;
                break;
            }
            if (descMap != null) {
                String[] urlsPatterns = descMap.getURLPatterns();
                if (urlPatternsSet && (urlsPatterns == null || urlsPatterns.length == 0)) {
                    for (String urlPattern : filterMap.getURLPatterns()) {
                        descMap.addURLPattern(urlPattern);
                    }
                }
                String[] dispatcherNames = descMap.getDispatcherNames();
                if (dispatchTypesSet && (dispatcherNames == null || dispatcherNames.length == 0)) {
                    for (String dis : filterMap.getDispatcherNames()) {
                        descMap.setDispatcher(dis);
                    }
                }
            }
        }
    }

    protected String[] processAnnotationsStringArray(ElementValue ev) {
        ArrayList<String> values = new ArrayList<String>();
        if (ev instanceof ArrayElementValue) {
            ElementValue[] arrayValues;
            for (ElementValue value : arrayValues = ((ArrayElementValue)ev).getElementValuesArray()) {
                values.add(value.stringifyValue());
            }
        } else {
            values.add(ev.stringifyValue());
        }
        return values.toArray(new String[0]);
    }

    protected Map<String, String> processAnnotationWebInitParams(ElementValue ev) {
        HashMap<String, String> result = new HashMap<String, String>();
        if (ev instanceof ArrayElementValue) {
            ElementValue[] arrayValues;
            for (ElementValue value : arrayValues = ((ArrayElementValue)ev).getElementValuesArray()) {
                if (!(value instanceof AnnotationElementValue)) continue;
                List evps = ((AnnotationElementValue)value).getAnnotationEntry().getElementValuePairs();
                String initParamName = null;
                String initParamValue = null;
                for (ElementValuePair evp : evps) {
                    if ("name".equals(evp.getNameString())) {
                        initParamName = evp.getValue().stringifyValue();
                        continue;
                    }
                    if (!"value".equals(evp.getNameString())) continue;
                    initParamValue = evp.getValue().stringifyValue();
                }
                result.put(initParamName, initParamValue);
            }
        }
        return result;
    }

    static {
        Properties props = new Properties();
        try (InputStream is = ContextConfig.class.getClassLoader().getResourceAsStream("org/apache/catalina/startup/Authenticators.properties");){
            if (is != null) {
                props.load(is);
            }
        }
        catch (IOException ioe) {
            props = null;
        }
        authenticators = props;
        deploymentCount = 0L;
        hostWebXmlCache = new ConcurrentHashMap<Host, DefaultWebXmlCacheEntry>();
        EMPTY_SCI_SET = Collections.emptySet();
    }

    public static interface ContextXml {
        public void load(Context var1);
    }

    private static class DefaultWebXmlCacheEntry {
        private final WebXml webXml;
        private final long globalTimeStamp;
        private final long hostTimeStamp;

        DefaultWebXmlCacheEntry(WebXml webXml, long globalTimeStamp, long hostTimeStamp) {
            this.webXml = webXml;
            this.globalTimeStamp = globalTimeStamp;
            this.hostTimeStamp = hostTimeStamp;
        }

        public WebXml getWebXml() {
            return this.webXml;
        }

        public long getGlobalTimeStamp() {
            return this.globalTimeStamp;
        }

        public long getHostTimeStamp() {
            return this.hostTimeStamp;
        }
    }

    private static class HostWebXmlCacheCleaner
    implements LifecycleListener {
        private HostWebXmlCacheCleaner() {
        }

        @Override
        public void lifecycleEvent(LifecycleEvent event) {
            if ("after_destroy".equals(event.getType())) {
                Host host = (Host)event.getSource();
                hostWebXmlCache.remove(host);
            }
        }
    }

    private class AnnotationScanTask
    implements Runnable {
        private final WebXml fragment;
        private final boolean handlesTypesOnly;
        private Map<String, JavaClassCacheEntry> javaClassCache;

        private AnnotationScanTask(WebXml fragment, boolean handlesTypesOnly, Map<String, JavaClassCacheEntry> javaClassCache) {
            this.fragment = fragment;
            this.handlesTypesOnly = handlesTypesOnly;
            this.javaClassCache = javaClassCache;
        }

        @Override
        public void run() {
            ContextConfig.this.scanWebXmlFragment(this.handlesTypesOnly, this.fragment, this.javaClassCache);
        }
    }

    static class JavaClassCacheEntry {
        public final String superclassName;
        public final String[] interfaceNames;
        private Set<ServletContainerInitializer> sciSet = null;

        JavaClassCacheEntry(JavaClass javaClass) {
            this.superclassName = javaClass.getSuperclassName();
            this.interfaceNames = javaClass.getInterfaceNames();
        }

        public String getSuperclassName() {
            return this.superclassName;
        }

        public String[] getInterfaceNames() {
            return this.interfaceNames;
        }

        public Set<ServletContainerInitializer> getSciSet() {
            return this.sciSet;
        }

        public void setSciSet(Set<ServletContainerInitializer> sciSet) {
            this.sciSet = sciSet;
        }
    }
}

