/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.commons.lang.text.StrBuilder
 */
package org.apache.velocity.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.velocity.Template;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.EventHandler;
import org.apache.velocity.app.event.IncludeEventHandler;
import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapterImpl;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.ParserPool;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.VelocimacroFactory;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.Scope;
import org.apache.velocity.runtime.directive.StopCommand;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.log.LogManager;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.ContentResource;
import org.apache.velocity.runtime.resource.ResourceManager;
import org.apache.velocity.util.ClassUtils;
import org.apache.velocity.util.RuntimeServicesAware;
import org.apache.velocity.util.StringUtils;
import org.apache.velocity.util.introspection.ChainableUberspector;
import org.apache.velocity.util.introspection.Introspector;
import org.apache.velocity.util.introspection.LinkingUberspector;
import org.apache.velocity.util.introspection.Uberspect;
import org.apache.velocity.util.introspection.UberspectLoggable;

public class RuntimeInstance
implements RuntimeConstants,
RuntimeServices {
    private VelocimacroFactory vmFactory = null;
    private Log log = new Log();
    private ParserPool parserPool;
    private boolean initializing = false;
    private volatile boolean initialized = false;
    private ExtendedProperties overridingProperties = null;
    private Map runtimeDirectives = new Hashtable();
    private Map runtimeDirectivesShared;
    private ExtendedProperties configuration = new ExtendedProperties();
    private ResourceManager resourceManager = null;
    private EventCartridge eventCartridge = null;
    private Introspector introspector = null;
    private String evaluateScopeName = "evaluate";
    private boolean provideEvaluateScope = false;
    private Map applicationAttributes = null;
    private Uberspect uberSpect;
    private String encoding;
    static /* synthetic */ Class class$org$apache$velocity$app$event$ReferenceInsertionEventHandler;
    static /* synthetic */ Class class$org$apache$velocity$app$event$NullSetEventHandler;
    static /* synthetic */ Class class$org$apache$velocity$app$event$MethodExceptionEventHandler;
    static /* synthetic */ Class class$org$apache$velocity$app$event$IncludeEventHandler;
    static /* synthetic */ Class class$org$apache$velocity$app$event$InvalidReferenceEventHandler;

    public RuntimeInstance() {
        this.vmFactory = new VelocimacroFactory(this);
        this.introspector = new Introspector(this.getLog());
        this.applicationAttributes = new HashMap();
    }

    public synchronized void init() {
        if (!this.initialized && !this.initializing) {
            this.log.debug("Initializing Velocity, Calling init()...");
            this.initializing = true;
            this.log.trace("*******************************************************************");
            this.log.debug("Starting Apache Velocity v1.7 (compiled: 2010-11-19 12:14:37)");
            this.log.trace("RuntimeInstance initializing.");
            this.initializeProperties();
            this.initializeLog();
            this.initializeResourceManager();
            this.initializeDirectives();
            this.initializeEventHandlers();
            this.initializeParserPool();
            this.initializeIntrospection();
            this.initializeEvaluateScopeSettings();
            this.vmFactory.initVelocimacro();
            this.log.trace("RuntimeInstance successfully initialized.");
            this.initialized = true;
            this.initializing = false;
        }
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    private void requireInitialization() {
        if (!this.initialized) {
            try {
                this.init();
            }
            catch (Exception e) {
                this.getLog().error("Could not auto-initialize Velocity", e);
                throw new RuntimeException("Velocity could not be initialized!", e);
            }
        }
    }

    private void initializeIntrospection() {
        String[] uberspectors = this.configuration.getStringArray("runtime.introspector.uberspect");
        for (int i = 0; i < uberspectors.length; ++i) {
            String rm = uberspectors[i];
            Object o = null;
            try {
                o = ClassUtils.getNewInstance(rm);
            }
            catch (ClassNotFoundException cnfe) {
                String err = "The specified class for Uberspect (" + rm + ") does not exist or is not accessible to the current classloader.";
                this.log.error(err);
                throw new VelocityException(err, cnfe);
            }
            catch (InstantiationException ie) {
                throw new VelocityException("Could not instantiate class '" + rm + "'", ie);
            }
            catch (IllegalAccessException ae) {
                throw new VelocityException("Cannot access class '" + rm + "'", ae);
            }
            if (!(o instanceof Uberspect)) {
                String err = "The specified class for Uberspect (" + rm + ") does not implement " + Uberspect.class.getName() + "; Velocity is not initialized correctly.";
                this.log.error(err);
                throw new VelocityException(err);
            }
            Uberspect u = (Uberspect)o;
            if (u instanceof UberspectLoggable) {
                ((UberspectLoggable)((Object)u)).setLog(this.getLog());
            }
            if (u instanceof RuntimeServicesAware) {
                ((RuntimeServicesAware)((Object)u)).setRuntimeServices(this);
            }
            if (this.uberSpect == null) {
                this.uberSpect = u;
                continue;
            }
            if (u instanceof ChainableUberspector) {
                ((ChainableUberspector)u).wrap(this.uberSpect);
                this.uberSpect = u;
                continue;
            }
            this.uberSpect = new LinkingUberspector(this.uberSpect, u);
        }
        if (this.uberSpect == null) {
            String err = "It appears that no class was specified as the Uberspect.  Please ensure that all configuration information is correct.";
            this.log.error(err);
            throw new VelocityException(err);
        }
        this.uberSpect.init();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void setDefaultProperties() {
        InputStream inputStream = null;
        try {
            try {
                inputStream = this.getClass().getResourceAsStream("/org/apache/velocity/runtime/defaults/velocity.properties");
                this.configuration.load(inputStream);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Default Properties File: " + new File("org/apache/velocity/runtime/defaults/velocity.properties").getPath());
                }
            }
            catch (IOException ioe) {
                String msg = "Cannot get Velocity Runtime default properties!";
                this.log.error(msg, ioe);
                throw new RuntimeException(msg, ioe);
            }
            Object var5_2 = null;
        }
        catch (Throwable throwable) {
            Object var5_3 = null;
            try {
                if (inputStream == null) throw throwable;
                inputStream.close();
                throw throwable;
            }
            catch (IOException ioe) {
                String msg = "Cannot close Velocity Runtime default properties!";
                this.log.error(msg, ioe);
                throw new RuntimeException(msg, ioe);
            }
        }
        try {}
        catch (IOException ioe) {
            String msg = "Cannot close Velocity Runtime default properties!";
            this.log.error(msg, ioe);
            throw new RuntimeException(msg, ioe);
        }
        if (inputStream == null) return;
        inputStream.close();
    }

    public void setProperty(String key, Object value) {
        if (this.overridingProperties == null) {
            this.overridingProperties = new ExtendedProperties();
        }
        this.overridingProperties.setProperty(key, value);
    }

    public void setProperties(String fileName) {
        ExtendedProperties props = null;
        try {
            props = new ExtendedProperties(fileName);
        }
        catch (IOException e) {
            throw new VelocityException("Error reading properties from '" + fileName + "'", e);
        }
        Enumeration en = props.keys();
        while (en.hasMoreElements()) {
            String key = en.nextElement().toString();
            this.setProperty(key, props.get((Object)key));
        }
    }

    public void setProperties(Properties props) {
        Enumeration<Object> en = props.keys();
        while (en.hasMoreElements()) {
            String key = en.nextElement().toString();
            this.setProperty(key, props.get(key));
        }
    }

    public void setConfiguration(ExtendedProperties configuration) {
        if (this.overridingProperties == null) {
            this.overridingProperties = configuration;
        } else if (this.overridingProperties != configuration) {
            this.overridingProperties.combine(configuration);
        }
    }

    public void addProperty(String key, Object value) {
        if (this.overridingProperties == null) {
            this.overridingProperties = new ExtendedProperties();
        }
        this.overridingProperties.addProperty(key, value);
    }

    public void clearProperty(String key) {
        if (this.overridingProperties != null) {
            this.overridingProperties.clearProperty(key);
        }
    }

    public Object getProperty(String key) {
        Object o = null;
        if (!this.initialized && this.overridingProperties != null) {
            o = this.overridingProperties.get((Object)key);
        }
        if (o == null) {
            o = this.configuration.getProperty(key);
        }
        if (o instanceof String) {
            return StringUtils.nullTrim((String)o);
        }
        return o;
    }

    private void initializeProperties() {
        if (!this.configuration.isInitialized()) {
            this.setDefaultProperties();
        }
        if (this.overridingProperties != null) {
            this.configuration.combine(this.overridingProperties);
        }
    }

    public void init(Properties p) {
        this.setProperties(ExtendedProperties.convertProperties((Properties)p));
        this.init();
    }

    private void setProperties(ExtendedProperties p) {
        if (this.overridingProperties == null) {
            this.overridingProperties = p;
        } else {
            this.overridingProperties.combine(p);
        }
    }

    public void init(String configurationFile) {
        try {
            this.setProperties(new ExtendedProperties(configurationFile));
        }
        catch (IOException e) {
            throw new VelocityException("Error reading properties from '" + configurationFile + "'", e);
        }
        this.init();
    }

    private void initializeResourceManager() {
        Object o;
        String rm = this.getString("resource.manager.class");
        if (rm != null && rm.length() > 0) {
            o = null;
            try {
                o = ClassUtils.getNewInstance(rm);
            }
            catch (ClassNotFoundException cnfe) {
                String err = "The specified class for ResourceManager (" + rm + ") does not exist or is not accessible to the current classloader.";
                this.log.error(err);
                throw new VelocityException(err, cnfe);
            }
            catch (InstantiationException ie) {
                throw new VelocityException("Could not instantiate class '" + rm + "'", ie);
            }
            catch (IllegalAccessException ae) {
                throw new VelocityException("Cannot access class '" + rm + "'", ae);
            }
            if (!(o instanceof ResourceManager)) {
                String err = "The specified class for ResourceManager (" + rm + ") does not implement " + ResourceManager.class.getName() + "; Velocity is not initialized correctly.";
                this.log.error(err);
                throw new VelocityException(err);
            }
        } else {
            String err = "It appears that no class was specified as the ResourceManager.  Please ensure that all configuration information is correct.";
            this.log.error(err);
            throw new VelocityException(err);
        }
        this.resourceManager = (ResourceManager)o;
        this.resourceManager.initialize(this);
    }

    private void initializeEventHandlers() {
        String[] invalidReferenceSet;
        String[] includeHandler;
        String[] methodexception;
        String[] nullset;
        this.eventCartridge = new EventCartridge();
        String[] referenceinsertion = this.configuration.getStringArray("eventhandler.referenceinsertion.class");
        if (referenceinsertion != null) {
            for (int i = 0; i < referenceinsertion.length; ++i) {
                EventHandler ev = this.initializeSpecificEventHandler(referenceinsertion[i], "eventhandler.referenceinsertion.class", class$org$apache$velocity$app$event$ReferenceInsertionEventHandler == null ? RuntimeInstance.class$("org.apache.velocity.app.event.ReferenceInsertionEventHandler") : class$org$apache$velocity$app$event$ReferenceInsertionEventHandler);
                if (ev == null) continue;
                this.eventCartridge.addReferenceInsertionEventHandler((ReferenceInsertionEventHandler)ev);
            }
        }
        if ((nullset = this.configuration.getStringArray("eventhandler.nullset.class")) != null) {
            for (int i = 0; i < nullset.length; ++i) {
                EventHandler ev = this.initializeSpecificEventHandler(nullset[i], "eventhandler.nullset.class", class$org$apache$velocity$app$event$NullSetEventHandler == null ? RuntimeInstance.class$("org.apache.velocity.app.event.NullSetEventHandler") : class$org$apache$velocity$app$event$NullSetEventHandler);
                if (ev == null) continue;
                this.eventCartridge.addNullSetEventHandler((NullSetEventHandler)ev);
            }
        }
        if ((methodexception = this.configuration.getStringArray("eventhandler.methodexception.class")) != null) {
            for (int i = 0; i < methodexception.length; ++i) {
                EventHandler ev = this.initializeSpecificEventHandler(methodexception[i], "eventhandler.methodexception.class", class$org$apache$velocity$app$event$MethodExceptionEventHandler == null ? RuntimeInstance.class$("org.apache.velocity.app.event.MethodExceptionEventHandler") : class$org$apache$velocity$app$event$MethodExceptionEventHandler);
                if (ev == null) continue;
                this.eventCartridge.addMethodExceptionHandler((MethodExceptionEventHandler)ev);
            }
        }
        if ((includeHandler = this.configuration.getStringArray("eventhandler.include.class")) != null) {
            for (int i = 0; i < includeHandler.length; ++i) {
                EventHandler ev = this.initializeSpecificEventHandler(includeHandler[i], "eventhandler.include.class", class$org$apache$velocity$app$event$IncludeEventHandler == null ? RuntimeInstance.class$("org.apache.velocity.app.event.IncludeEventHandler") : class$org$apache$velocity$app$event$IncludeEventHandler);
                if (ev == null) continue;
                this.eventCartridge.addIncludeEventHandler((IncludeEventHandler)ev);
            }
        }
        if ((invalidReferenceSet = this.configuration.getStringArray("eventhandler.invalidreferences.class")) != null) {
            for (int i = 0; i < invalidReferenceSet.length; ++i) {
                EventHandler ev = this.initializeSpecificEventHandler(invalidReferenceSet[i], "eventhandler.invalidreferences.class", class$org$apache$velocity$app$event$InvalidReferenceEventHandler == null ? RuntimeInstance.class$("org.apache.velocity.app.event.InvalidReferenceEventHandler") : class$org$apache$velocity$app$event$InvalidReferenceEventHandler);
                if (ev == null) continue;
                this.eventCartridge.addInvalidReferenceEventHandler((InvalidReferenceEventHandler)ev);
            }
        }
    }

    private EventHandler initializeSpecificEventHandler(String classname, String paramName, Class EventHandlerInterface) {
        if (classname != null && classname.length() > 0) {
            Object o = null;
            try {
                o = ClassUtils.getNewInstance(classname);
            }
            catch (ClassNotFoundException cnfe) {
                String err = "The specified class for " + paramName + " (" + classname + ") does not exist or is not accessible to the current classloader.";
                this.log.error(err);
                throw new VelocityException(err, cnfe);
            }
            catch (InstantiationException ie) {
                throw new VelocityException("Could not instantiate class '" + classname + "'", ie);
            }
            catch (IllegalAccessException ae) {
                throw new VelocityException("Cannot access class '" + classname + "'", ae);
            }
            if (!EventHandlerInterface.isAssignableFrom(EventHandlerInterface)) {
                String err = "The specified class for " + paramName + " (" + classname + ") does not implement " + EventHandlerInterface.getName() + "; Velocity is not initialized correctly.";
                this.log.error(err);
                throw new VelocityException(err);
            }
            EventHandler ev = (EventHandler)o;
            if (ev instanceof RuntimeServicesAware) {
                ((RuntimeServicesAware)((Object)ev)).setRuntimeServices(this);
            }
            return ev;
        }
        return null;
    }

    private void initializeLog() {
        try {
            LogManager.updateLog(this.log, this);
        }
        catch (Exception e) {
            throw new VelocityException("Error initializing log: " + e.getMessage(), e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void initializeDirectives() {
        Properties directiveProperties = new Properties();
        InputStream inputStream = null;
        try {
            try {
                inputStream = this.getClass().getResourceAsStream("/org/apache/velocity/runtime/defaults/directive.properties");
                if (inputStream == null) {
                    throw new VelocityException("Error loading directive.properties! Something is very wrong if these properties aren't being located. Either your Velocity distribution is incomplete or your Velocity jar file is corrupted!");
                }
                directiveProperties.load(inputStream);
            }
            catch (IOException ioe) {
                String msg = "Error while loading directive properties!";
                this.log.error(msg, ioe);
                throw new RuntimeException(msg, ioe);
            }
            Object var6_3 = null;
        }
        catch (Throwable throwable) {
            Object var6_4 = null;
            try {
                if (inputStream == null) throw throwable;
                inputStream.close();
                throw throwable;
            }
            catch (IOException ioe) {
                String msg = "Cannot close directive properties!";
                this.log.error(msg, ioe);
                throw new RuntimeException(msg, ioe);
            }
        }
        try {}
        catch (IOException ioe) {
            String msg = "Cannot close directive properties!";
            this.log.error(msg, ioe);
            throw new RuntimeException(msg, ioe);
        }
        if (inputStream != null) {
            inputStream.close();
        }
        Enumeration<Object> directiveClasses = directiveProperties.elements();
        while (directiveClasses.hasMoreElements()) {
            String directiveClass = (String)directiveClasses.nextElement();
            this.loadDirective(directiveClass);
            this.log.debug("Loaded System Directive: " + directiveClass);
        }
        String[] userdirective = this.configuration.getStringArray("userdirective");
        int i = 0;
        while (i < userdirective.length) {
            this.loadDirective(userdirective[i]);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Loaded User Directive: " + userdirective[i]);
            }
            ++i;
        }
    }

    public synchronized void addDirective(Directive directive) {
        this.runtimeDirectives.put(directive.getName(), directive);
        this.updateSharedDirectivesMap();
    }

    public Directive getDirective(String name) {
        return (Directive)this.runtimeDirectivesShared.get(name);
    }

    public synchronized void removeDirective(String name) {
        this.runtimeDirectives.remove(name);
        this.updateSharedDirectivesMap();
    }

    private void updateSharedDirectivesMap() {
        HashMap tmp;
        this.runtimeDirectivesShared = tmp = new HashMap(this.runtimeDirectives);
    }

    public void loadDirective(String directiveClass) {
        try {
            Object o = ClassUtils.getNewInstance(directiveClass);
            if (!(o instanceof Directive)) {
                String msg = directiveClass + " does not implement " + Directive.class.getName() + "; it cannot be loaded.";
                this.log.error(msg);
                throw new VelocityException(msg);
            }
            Directive directive = (Directive)o;
            this.addDirective(directive);
        }
        catch (Exception e) {
            String msg = "Failed to load Directive: " + directiveClass;
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
    }

    private void initializeParserPool() {
        Object o;
        String pp = this.getString("parser.pool.class");
        if (pp != null && pp.length() > 0) {
            o = null;
            try {
                o = ClassUtils.getNewInstance(pp);
            }
            catch (ClassNotFoundException cnfe) {
                String err = "The specified class for ParserPool (" + pp + ") does not exist (or is not accessible to the current classloader.";
                this.log.error(err);
                throw new VelocityException(err, cnfe);
            }
            catch (InstantiationException ie) {
                throw new VelocityException("Could not instantiate class '" + pp + "'", ie);
            }
            catch (IllegalAccessException ae) {
                throw new VelocityException("Cannot access class '" + pp + "'", ae);
            }
            if (!(o instanceof ParserPool)) {
                String err = "The specified class for ParserPool (" + pp + ") does not implement " + ParserPool.class + " Velocity not initialized correctly.";
                this.log.error(err);
                throw new VelocityException(err);
            }
        } else {
            String err = "It appears that no class was specified as the ParserPool.  Please ensure that all configuration information is correct.";
            this.log.error(err);
            throw new VelocityException(err);
        }
        this.parserPool = (ParserPool)o;
        this.parserPool.initialize(this);
    }

    public Parser createNewParser() {
        this.requireInitialization();
        Parser parser = new Parser(this);
        return parser;
    }

    public SimpleNode parse(String string, String templateName) throws ParseException {
        return this.parse(new StringReader(string), templateName);
    }

    public SimpleNode parse(Reader reader, String templateName) throws ParseException {
        return this.parse(reader, templateName, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SimpleNode parse(Reader reader, String templateName, boolean dumpNamespace) throws ParseException {
        this.requireInitialization();
        Parser parser = this.parserPool.get();
        boolean keepParser = true;
        if (parser == null) {
            if (this.log.isInfoEnabled()) {
                this.log.info("Runtime : ran out of parsers. Creating a new one.  Please increment the parser.pool.size property. The current value is too small.");
            }
            parser = this.createNewParser();
            keepParser = false;
        }
        try {
            if (dumpNamespace) {
                this.dumpVMNamespace(templateName);
            }
            SimpleNode simpleNode = parser.parse(reader, templateName);
            return simpleNode;
        }
        finally {
            if (keepParser) {
                this.parserPool.put(parser);
            }
        }
    }

    private void initializeEvaluateScopeSettings() {
        String property = this.evaluateScopeName + '.' + "provide.scope.control";
        this.provideEvaluateScope = this.getBoolean(property, this.provideEvaluateScope);
    }

    public boolean evaluate(Context context, Writer out, String logTag, String instring) {
        return this.evaluate(context, out, logTag, new StringReader(instring));
    }

    public boolean evaluate(Context context, Writer writer, String logTag, Reader reader) {
        if (logTag == null) {
            throw new NullPointerException("logTag (i.e. template name) cannot be null, you must provide an identifier for the content being evaluated");
        }
        SimpleNode nodeTree = null;
        try {
            nodeTree = this.parse(reader, logTag);
        }
        catch (ParseException pex) {
            throw new ParseErrorException(pex, null);
        }
        catch (TemplateInitException pex) {
            throw new ParseErrorException(pex, null);
        }
        if (nodeTree == null) {
            return false;
        }
        return this.render(context, writer, logTag, nodeTree);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean render(Context context, Writer writer, String logTag, SimpleNode nodeTree) {
        Object obj2;
        InternalContextAdapterImpl ica = new InternalContextAdapterImpl(context);
        ica.pushCurrentTemplateName(logTag);
        try {
            try {
                nodeTree.init(ica, this);
            }
            catch (TemplateInitException pex) {
                throw new ParseErrorException(pex, null);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                String msg = "RuntimeInstance.render(): init exception for tag = " + logTag;
                this.getLog().error(msg, e);
                throw new VelocityException(msg, e);
            }
            try {
                if (this.provideEvaluateScope) {
                    Object previous = ica.get(this.evaluateScopeName);
                    context.put(this.evaluateScopeName, new Scope(this, previous));
                }
                nodeTree.render(ica, writer);
            }
            catch (StopCommand stop) {
                if (!stop.isFor(this)) {
                    throw stop;
                }
                if (this.getLog().isDebugEnabled()) {
                    this.getLog().debug(stop.getMessage());
                }
            }
            catch (IOException e) {
                throw new VelocityException("IO Error in writer: " + e.getMessage(), e);
            }
            Object var9_13 = null;
            ica.popCurrentTemplateName();
        }
        catch (Throwable throwable) {
            Object obj2;
            Object var9_14 = null;
            ica.popCurrentTemplateName();
            if (!this.provideEvaluateScope || !((obj2 = ica.get(this.evaluateScopeName)) instanceof Scope)) throw throwable;
            Scope scope = (Scope)obj2;
            if (scope.getParent() != null) {
                ica.put(this.evaluateScopeName, scope.getParent());
                throw throwable;
            } else if (scope.getReplaced() != null) {
                ica.put(this.evaluateScopeName, scope.getReplaced());
                throw throwable;
            } else {
                ica.remove(this.evaluateScopeName);
            }
            throw throwable;
        }
        if (!this.provideEvaluateScope || !((obj2 = ica.get(this.evaluateScopeName)) instanceof Scope)) return true;
        Scope scope = (Scope)obj2;
        if (scope.getParent() != null) {
            ica.put(this.evaluateScopeName, scope.getParent());
            return true;
        }
        if (scope.getReplaced() != null) {
            ica.put(this.evaluateScopeName, scope.getReplaced());
            return true;
        }
        ica.remove(this.evaluateScopeName);
        return true;
    }

    public boolean invokeVelocimacro(String vmName, String logTag, String[] params, Context context, Writer writer) {
        if (vmName == null || context == null || writer == null) {
            String msg = "RuntimeInstance.invokeVelocimacro() : invalid call : vmName, context, and writer must not be null";
            this.getLog().error(msg);
            throw new NullPointerException(msg);
        }
        if (logTag == null) {
            logTag = vmName;
        }
        if (params == null) {
            params = new String[]{};
        }
        if (!this.isVelocimacro(vmName, logTag)) {
            String msg = "RuntimeInstance.invokeVelocimacro() : VM '" + vmName + "' is not registered.";
            this.getLog().error(msg);
            throw new VelocityException(msg);
        }
        StrBuilder template = new StrBuilder("#");
        template.append(vmName);
        template.append("(");
        for (int i = 0; i < params.length; ++i) {
            template.append(" $");
            template.append(params[i]);
        }
        template.append(" )");
        return this.evaluate(context, writer, logTag, template.toString());
    }

    private String getDefaultEncoding() {
        if (this.encoding == null) {
            this.encoding = this.getString("input.encoding", "ISO-8859-1");
        }
        return this.encoding;
    }

    public Template getTemplate(String name) throws ResourceNotFoundException, ParseErrorException {
        return this.getTemplate(name, this.getDefaultEncoding());
    }

    public Template getTemplate(String name, String encoding) throws ResourceNotFoundException, ParseErrorException {
        this.requireInitialization();
        return (Template)this.resourceManager.getResource(name, 1, encoding);
    }

    public ContentResource getContent(String name) throws ResourceNotFoundException, ParseErrorException {
        return this.getContent(name, this.getDefaultEncoding());
    }

    public ContentResource getContent(String name, String encoding) throws ResourceNotFoundException, ParseErrorException {
        this.requireInitialization();
        return (ContentResource)this.resourceManager.getResource(name, 2, encoding);
    }

    public String getLoaderNameForResource(String resourceName) {
        this.requireInitialization();
        return this.resourceManager.getLoaderNameForResource(resourceName);
    }

    public Log getLog() {
        return this.log;
    }

    public void warn(Object message) {
        this.getLog().warn(message);
    }

    public void info(Object message) {
        this.getLog().info(message);
    }

    public void error(Object message) {
        this.getLog().error(message);
    }

    public void debug(Object message) {
        this.getLog().debug(message);
    }

    public String getString(String key, String defaultValue) {
        return this.configuration.getString(key, defaultValue);
    }

    public Directive getVelocimacro(String vmName, String templateName) {
        return this.vmFactory.getVelocimacro(vmName, templateName);
    }

    public Directive getVelocimacro(String vmName, String templateName, String renderingTemplate) {
        return this.vmFactory.getVelocimacro(vmName, templateName, renderingTemplate);
    }

    public boolean addVelocimacro(String name, String macro, String[] argArray, String sourceTemplate) {
        return this.vmFactory.addVelocimacro(name.intern(), macro, argArray, sourceTemplate);
    }

    public boolean addVelocimacro(String name, Node macro, String[] argArray, String sourceTemplate) {
        return this.vmFactory.addVelocimacro(name.intern(), macro, argArray, sourceTemplate);
    }

    public boolean isVelocimacro(String vmName, String templateName) {
        return this.vmFactory.isVelocimacro(vmName.intern(), templateName);
    }

    public boolean dumpVMNamespace(String namespace) {
        return this.vmFactory.dumpVMNamespace(namespace);
    }

    public String getString(String key) {
        return StringUtils.nullTrim(this.configuration.getString(key));
    }

    public int getInt(String key) {
        return this.configuration.getInt(key);
    }

    public int getInt(String key, int defaultValue) {
        return this.configuration.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean def) {
        return this.configuration.getBoolean(key, def);
    }

    public ExtendedProperties getConfiguration() {
        return this.configuration;
    }

    public Introspector getIntrospector() {
        return this.introspector;
    }

    public EventCartridge getApplicationEventCartridge() {
        return this.eventCartridge;
    }

    public Object getApplicationAttribute(Object key) {
        return this.applicationAttributes.get(key);
    }

    public Object setApplicationAttribute(Object key, Object o) {
        return this.applicationAttributes.put(key, o);
    }

    public Uberspect getUberspect() {
        return this.uberSpect;
    }
}

