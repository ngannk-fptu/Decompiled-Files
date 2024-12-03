/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.sling.api.SlingHttpServletRequest
 *  org.apache.sling.api.resource.ResourceResolver
 *  org.apache.sling.api.resource.observation.ExternalResourceChangeListener
 *  org.apache.sling.api.resource.observation.ResourceChange
 *  org.apache.sling.api.resource.observation.ResourceChange$ChangeType
 *  org.apache.sling.api.resource.observation.ResourceChangeListener
 *  org.apache.sling.api.scripting.SlingBindings
 *  org.apache.sling.api.scripting.SlingScriptHelper
 *  org.apache.sling.commons.classloader.ClassLoaderWriter
 *  org.apache.sling.commons.classloader.ClassLoaderWriterListener
 *  org.apache.sling.commons.classloader.DynamicClassLoaderManager
 *  org.apache.sling.commons.compiler.JavaCompiler
 *  org.apache.sling.scripting.api.AbstractScriptEngineFactory
 *  org.apache.sling.scripting.api.AbstractSlingScriptEngine
 *  org.apache.sling.scripting.api.resource.ScriptingResourceResolverProvider
 *  org.osgi.framework.BundleContext
 *  org.osgi.service.component.annotations.Activate
 *  org.osgi.service.component.annotations.Component
 *  org.osgi.service.component.annotations.Deactivate
 *  org.osgi.service.component.annotations.Reference
 *  org.osgi.service.component.annotations.ReferenceCardinality
 *  org.osgi.service.component.annotations.ReferencePolicy
 *  org.osgi.service.metatype.annotations.AttributeDefinition
 *  org.osgi.service.metatype.annotations.Designate
 *  org.osgi.service.metatype.annotations.ObjectClassDefinition
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.sling.scripting.jsp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.servlet.ServletContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.observation.ExternalResourceChangeListener;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.commons.classloader.ClassLoaderWriter;
import org.apache.sling.commons.classloader.ClassLoaderWriterListener;
import org.apache.sling.commons.classloader.DynamicClassLoaderManager;
import org.apache.sling.commons.compiler.JavaCompiler;
import org.apache.sling.scripting.api.AbstractScriptEngineFactory;
import org.apache.sling.scripting.api.AbstractSlingScriptEngine;
import org.apache.sling.scripting.api.resource.ScriptingResourceResolverProvider;
import org.apache.sling.scripting.jsp.JspServletConfig;
import org.apache.sling.scripting.jsp.JspServletContext;
import org.apache.sling.scripting.jsp.JspServletOptions;
import org.apache.sling.scripting.jsp.PrecompiledJSPRunner;
import org.apache.sling.scripting.jsp.SlingIOProvider;
import org.apache.sling.scripting.jsp.SlingPageException;
import org.apache.sling.scripting.jsp.SlingTldLocationsCache;
import org.apache.sling.scripting.jsp.jasper.compiler.JspRuntimeContext;
import org.apache.sling.scripting.jsp.jasper.runtime.AnnotationProcessor;
import org.apache.sling.scripting.jsp.jasper.runtime.JspApplicationContextImpl;
import org.apache.sling.scripting.jsp.jasper.servlet.JspServletWrapper;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service={ScriptEngineFactory.class, ResourceChangeListener.class, ClassLoaderWriterListener.class}, property={"extensions=jsp", "extensions=jspf", "extensions=jspx", "names=jsp", "names=JSP", "service.vendor=The Apache Software Foundation", "service.description=JSP Script Handler", "resource.change.types=CHANGED", "resource.change.types=REMOVED", "resource.paths=glob:**/*.jsp", "resource.paths=glob:**/*.jspf", "resource.paths=glob:**/*.jspx", "resource.paths=glob:**/*.tld", "resource.paths=glob:**/*.tag"})
@Designate(ocd=Config.class)
public class JspScriptEngineFactory
extends AbstractScriptEngineFactory
implements ResourceChangeListener,
ExternalResourceChangeListener,
ClassLoaderWriterListener {
    private final Logger logger = LoggerFactory.getLogger(((Object)((Object)this)).getClass());
    private static final Object BINDINGS_NOT_SWAPPED = new Object();
    private ServletContext slingServletContext;
    private volatile PrecompiledJSPRunner precompiledJSPRunner;
    @Reference
    private ClassLoaderWriter classLoaderWriter;
    @Reference
    private JavaCompiler javaCompiler;
    @Reference
    private ScriptingResourceResolverProvider scriptingResourceResolverProvider;
    private DynamicClassLoaderManager dynamicClassLoaderManager;
    private ClassLoader dynamicClassLoader;
    private SlingIOProvider ioProvider;
    private SlingTldLocationsCache tldLocationsCache;
    private JspRuntimeContext jspRuntimeContext;
    private JspServletOptions options;
    private JspServletConfig servletConfig;
    private JspRuntimeContext.JspFactoryHandler jspFactoryHandler;
    public static final String[] SCRIPT_TYPE = new String[]{"jsp", "jspf", "jspx"};
    public static final String[] NAMES = new String[]{"jsp", "JSP"};
    private static final String CONFIG_PATH = "/jsp.config";

    public JspScriptEngineFactory() {
        this.setExtensions(SCRIPT_TYPE);
        this.setNames(NAMES);
    }

    public ScriptEngine getScriptEngine() {
        return new JspScriptEngine();
    }

    public String getLanguageName() {
        return "Java Server Pages";
    }

    public String getLanguageVersion() {
        return "2.1";
    }

    public Object getParameter(String name) {
        if ("THREADING".equals(name)) {
            return "STATELESS";
        }
        return super.getParameter(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JspServletWrapper getJspWrapper(String scriptName) {
        JspRuntimeContext rctxt = this.getJspRuntimeContext();
        JspServletWrapper wrapper = rctxt.getWrapper(scriptName);
        if (wrapper != null) {
            if (wrapper.isValid()) {
                return wrapper;
            }
            JspScriptEngineFactory jspScriptEngineFactory = this;
            synchronized (jspScriptEngineFactory) {
                rctxt = this.getJspRuntimeContext();
                wrapper = rctxt.getWrapper(scriptName);
                if (wrapper != null) {
                    if (wrapper.isValid()) {
                        return wrapper;
                    }
                    this.renewJspRuntimeContext();
                    rctxt = this.getJspRuntimeContext();
                }
            }
        }
        wrapper = new JspServletWrapper(this.servletConfig, this.options, scriptName, false, rctxt);
        wrapper = rctxt.addWrapper(scriptName, wrapper);
        return wrapper;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Activate
    protected void activate(BundleContext bundleContext, Config config, Map<String, Object> properties) {
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.dynamicClassLoader);
        try {
            this.jspFactoryHandler = JspRuntimeContext.initFactoryHandler();
            this.tldLocationsCache = new SlingTldLocationsCache(bundleContext);
            this.ioProvider = new SlingIOProvider(this.classLoaderWriter, this.javaCompiler);
            this.options = new JspServletOptions(this.slingServletContext, this.ioProvider, properties, this.tldLocationsCache, config.default_is_session());
            JspServletContext jspServletContext = new JspServletContext(this.ioProvider, this.slingServletContext, this.tldLocationsCache);
            this.servletConfig = new JspServletConfig(jspServletContext, this.options.getProperties());
            this.precompiledJSPRunner = new PrecompiledJSPRunner(this.options);
        }
        finally {
            Thread.currentThread().setContextClassLoader(old);
        }
        this.checkJasperConfig();
        this.logger.info("Activating Apache Sling Script Engine for JSP with options {}", this.options.getProperties());
        this.logger.debug("IMPORTANT: Do not modify the generated servlet classes directly");
    }

    @Deactivate
    protected void deactivate(BundleContext bundleContext) {
        this.logger.info("Deactivating Apache Sling Script Engine for JSP");
        if (this.precompiledJSPRunner != null) {
            this.precompiledJSPRunner.cleanup();
            this.precompiledJSPRunner = null;
        }
        if (this.tldLocationsCache != null) {
            this.tldLocationsCache.deactivate(bundleContext);
            this.tldLocationsCache = null;
        }
        if (this.jspRuntimeContext != null) {
            this.destroyJspRuntimeContext(this.jspRuntimeContext);
            this.jspRuntimeContext = null;
        }
        this.ioProvider = null;
        this.jspFactoryHandler.destroy();
        this.jspFactoryHandler = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkJasperConfig() {
        Throwable throwable;
        boolean changed = false;
        InputStream is = null;
        try {
            throwable = null;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                is = this.classLoaderWriter.getInputStream(CONFIG_PATH);
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
                String oldKey = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                boolean bl = changed = !oldKey.equals(this.servletConfig.getConfigKey());
                if (changed) {
                    this.logger.info("Removing all class files due to jsp configuration change");
                }
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
        }
        catch (IOException notFound) {
            changed = true;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException notFound) {}
            }
        }
        if (changed) {
            try {
                throwable = null;
                try (OutputStream os = this.classLoaderWriter.getOutputStream(CONFIG_PATH);){
                    os.write(this.servletConfig.getConfigKey().getBytes(StandardCharsets.UTF_8));
                }
                catch (Throwable throwable3) {
                    throwable = throwable3;
                    throw throwable3;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.classLoaderWriter.delete("/org/apache/jsp");
        }
    }

    @Reference(target="(name=org.apache.sling)")
    protected void bindSlingServletContext(ServletContext context) {
        this.slingServletContext = context;
    }

    protected void unbindSlingServletContext(ServletContext slingServletContext) {
        try {
            if (slingServletContext != null) {
                slingServletContext.removeAttribute(JspApplicationContextImpl.class.getName());
                slingServletContext.removeAttribute(AnnotationProcessor.class.getName());
            }
        }
        catch (NullPointerException npe) {
            this.logger.debug("unbindSlingServletContext: ServletContext might already be unavailable", (Throwable)npe);
        }
        if (this.slingServletContext == slingServletContext) {
            this.slingServletContext = null;
        }
    }

    @Reference(cardinality=ReferenceCardinality.MANDATORY, policy=ReferencePolicy.STATIC)
    protected void bindDynamicClassLoaderManager(DynamicClassLoaderManager rclp) {
        if (this.dynamicClassLoader != null) {
            this.ungetClassLoader();
        }
        this.getClassLoader(rclp);
    }

    protected void unbindDynamicClassLoaderManager(DynamicClassLoaderManager rclp) {
        if (this.dynamicClassLoaderManager == rclp) {
            this.ungetClassLoader();
        }
    }

    private void getClassLoader(DynamicClassLoaderManager rclp) {
        this.dynamicClassLoaderManager = rclp;
        this.dynamicClassLoader = rclp.getDynamicClassLoader();
    }

    private void ungetClassLoader() {
        this.dynamicClassLoader = null;
        this.dynamicClassLoaderManager = null;
    }

    private void destroyJspRuntimeContext(JspRuntimeContext jrc) {
        if (jrc != null) {
            try {
                jrc.destroy();
            }
            catch (NullPointerException npe) {
                this.logger.debug("deactivate: ServletContext might already be unavailable", (Throwable)npe);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JspRuntimeContext getJspRuntimeContext() {
        if (this.jspRuntimeContext == null) {
            JspScriptEngineFactory jspScriptEngineFactory = this;
            synchronized (jspScriptEngineFactory) {
                if (this.jspRuntimeContext == null) {
                    this.jspRuntimeContext = new JspRuntimeContext(this.slingServletContext, this.options, this.ioProvider);
                }
            }
        }
        return this.jspRuntimeContext;
    }

    public void onChange(List<ResourceChange> changes) {
        for (ResourceChange change : changes) {
            JspRuntimeContext rctxt = this.jspRuntimeContext;
            if (rctxt == null || !rctxt.handleModification(change.getPath(), change.getType() == ResourceChange.ChangeType.REMOVED)) continue;
            this.renewJspRuntimeContext();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renewJspRuntimeContext() {
        JspRuntimeContext jrc;
        JspScriptEngineFactory jspScriptEngineFactory = this;
        synchronized (jspScriptEngineFactory) {
            jrc = this.jspRuntimeContext;
            this.jspRuntimeContext = null;
        }
        Thread t = new Thread(){

            @Override
            public void run() {
                JspScriptEngineFactory.this.destroyJspRuntimeContext(jrc);
            }
        };
        t.start();
    }

    public void onClassLoaderClear(String context) {
        JspRuntimeContext rctxt = this.jspRuntimeContext;
        if (rctxt != null) {
            this.renewJspRuntimeContext();
        }
    }

    private class JspScriptEngine
    extends AbstractSlingScriptEngine {
        JspScriptEngine() {
            super((ScriptEngineFactory)((Object)JspScriptEngineFactory.this));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void callJsp(SlingBindings slingBindings) {
            SlingScriptHelper scriptHelper = slingBindings.getSling();
            if (scriptHelper == null) {
                throw new IllegalStateException(String.format("The %s variable is missing from the bindings.", "sling"));
            }
            ResourceResolver resolver = JspScriptEngineFactory.this.scriptingResourceResolverProvider.getRequestScopedResourceResolver();
            if (resolver == null) {
                resolver = scriptHelper.getScript().getScriptResource().getResourceResolver();
            }
            SlingIOProvider io = JspScriptEngineFactory.this.ioProvider;
            JspRuntimeContext.JspFactoryHandler jspfh = JspScriptEngineFactory.this.jspFactoryHandler;
            if (io == null || jspfh == null) {
                throw new RuntimeException("callJsp: JSP Script Engine seems to be shut down concurrently; not calling " + scriptHelper.getScript().getScriptResource().getPath());
            }
            ResourceResolver oldResolver = io.setRequestResourceResolver(resolver);
            jspfh.incUsage();
            try {
                boolean contextHasPrecompiledJsp = JspScriptEngineFactory.this.precompiledJSPRunner.callPrecompiledJSP(JspScriptEngineFactory.this.getJspRuntimeContext(), JspScriptEngineFactory.this.jspFactoryHandler, JspScriptEngineFactory.this.servletConfig, slingBindings);
                if (!contextHasPrecompiledJsp) {
                    JspServletWrapper jsp = JspScriptEngineFactory.this.getJspWrapper(scriptHelper.getScript().getScriptResource().getPath());
                    jsp.service(slingBindings);
                }
            }
            finally {
                jspfh.decUsage();
                io.resetRequestResourceResolver(oldResolver);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void callErrorPageJsp(SlingBindings slingBindings, String scriptName) {
            SlingScriptHelper scriptHelper = slingBindings.getSling();
            if (scriptHelper == null) {
                throw new IllegalStateException(String.format("The %s variable is missing from the bindings.", "sling"));
            }
            ResourceResolver resolver = JspScriptEngineFactory.this.scriptingResourceResolverProvider.getRequestScopedResourceResolver();
            if (resolver == null) {
                resolver = scriptHelper.getScript().getScriptResource().getResourceResolver();
            }
            SlingIOProvider io = JspScriptEngineFactory.this.ioProvider;
            JspRuntimeContext.JspFactoryHandler jspfh = JspScriptEngineFactory.this.jspFactoryHandler;
            if (io == null || jspfh == null) {
                throw new RuntimeException("callJsp: JSP Script Engine seems to be shut down concurrently; not calling " + scriptHelper.getScript().getScriptResource().getPath());
            }
            ResourceResolver oldResolver = io.setRequestResourceResolver(resolver);
            jspfh.incUsage();
            try {
                JspServletWrapper errorJsp = JspScriptEngineFactory.this.getJspWrapper(scriptName);
                errorJsp.service(slingBindings);
                SlingHttpServletRequest request = slingBindings.getRequest();
                if (request != null) {
                    Throwable t = (Throwable)request.getAttribute("javax.servlet.jsp.jspException");
                    Object newException = request.getAttribute("javax.servlet.error.exception");
                    if (newException != null && newException == t) {
                        request.removeAttribute("javax.servlet.error.exception");
                    }
                    request.removeAttribute("javax.servlet.error.status_code");
                    request.removeAttribute("javax.servlet.error.request_uri");
                    request.removeAttribute("javax.servlet.error.status_code");
                    request.removeAttribute("javax.servlet.jsp.jspException");
                }
            }
            finally {
                jspfh.decUsage();
                io.resetRequestResourceResolver(oldResolver);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object eval(Reader script, ScriptContext context) throws ScriptException {
            Bindings props = context.getBindings(100);
            SlingBindings slingBindings = new SlingBindings();
            slingBindings.putAll((Map)props);
            SlingScriptHelper scriptHelper = (SlingScriptHelper)props.get("sling");
            if (scriptHelper != null) {
                ClassLoader old = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(JspScriptEngineFactory.this.dynamicClassLoader);
                SlingHttpServletRequest request = slingBindings.getRequest();
                Object oldSlingBindings = BINDINGS_NOT_SWAPPED;
                if (request != null) {
                    oldSlingBindings = request.getAttribute(SlingBindings.class.getName());
                    request.setAttribute(SlingBindings.class.getName(), (Object)slingBindings);
                }
                try {
                    this.callJsp(slingBindings);
                }
                catch (SlingPageException sje) {
                    this.callErrorPageJsp(slingBindings, sje.getErrorPage());
                }
                finally {
                    Thread.currentThread().setContextClassLoader(old);
                    if (request != null && oldSlingBindings != BINDINGS_NOT_SWAPPED) {
                        request.setAttribute(SlingBindings.class.getName(), oldSlingBindings);
                    }
                }
            }
            return null;
        }
    }

    @ObjectClassDefinition(name="Apache Sling JSP Script Handler", description="The JSP Script Handler supports development of JSP scripts to render response content on behalf of ScriptComponents. Internally Jasper 6.0.14 JSP Engine is used together with the Eclipse Java Compiler to compile generated Java code into Java class files. Some settings of Jasper may be configured as shown below. Note that JSP scripts are expected in the JCR repository and generated Java source and class files will be written to the JCR repository below the configured Compilation Location.")
    public static @interface Config {
        @AttributeDefinition(name="Target Version", description="The target JVM version for the compiled classes. If left empty, the default version, 1.6., is used. If the value \"auto\" is used, the current vm version will be used.")
        public String jasper_compilerTargetVM() default "auto";

        @AttributeDefinition(name="Source Version", description="The JVM version for the java/JSP source. If left empty, the default version, 1.6., is used. If the value \"auto\" is used, the current vm version will be used.")
        public String jasper_compilerSourceVM() default "auto";

        @AttributeDefinition(name="Generate Debug Info", description="Should the class file be compiled with debugging information? true or false, default true.")
        public boolean jasper_classdebuginfo() default true;

        @AttributeDefinition(name="Tag Pooling", description="Determines whether tag handler pooling is enabled. true or false, default true.")
        public boolean jasper_enablePooling() default true;

        @AttributeDefinition(name="Plugin Class-ID", description="The class-id value to be sent to Internet Explorer when using <jsp:plugin> tags. Default clsid:8AD9C840-044E-11D1-B3E9-00805F499D93.")
        public String jasper_ieClassId() default "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";

        @AttributeDefinition(name="Char Array Strings", description="Should text strings be generated as char arrays, to improve performance in some cases? Default false.")
        public boolean jasper_genStringAsCharArray() default false;

        @AttributeDefinition(name="Keep Generated Java", description="Should we keep the generated Java source code for each page instead of deleting it? true or false, default true.")
        public boolean jasper_keepgenerated() default true;

        @AttributeDefinition(name="Mapped Content", description="Should we generate static content with one print statement per input line, to ease debugging? true or false, default true.")
        public boolean jasper_mappedfile() default true;

        @AttributeDefinition(name="Trim Spaces", description="Should white spaces in template text between actions or directives be trimmed ?, default false.")
        public boolean jasper_trimSpaces() default false;

        @AttributeDefinition(name="Display Source Fragments", description="Should we include a source fragment in exception messages, which could be displayed to the developer")
        public boolean jasper_displaySourceFragments() default false;

        @AttributeDefinition(name="Default Session Value", description="Should a session be created by default for every JSP page? Warning - this behavior may produce unintended results and changing it will not impact previously-compiled pages.")
        public boolean default_is_session() default true;
    }
}

