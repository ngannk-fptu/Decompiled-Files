/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.velocity.Template
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.io.VelocityWriter
 *  org.apache.velocity.util.SimplePool
 */
package org.apache.velocity.tools.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.ConfigurationCleaner;
import org.apache.velocity.tools.config.ConfigurationUtils;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.generic.log.LogChuteCommonsLog;
import org.apache.velocity.tools.view.JeeConfig;
import org.apache.velocity.tools.view.JeeContextConfig;
import org.apache.velocity.tools.view.JeeFilterConfig;
import org.apache.velocity.tools.view.JeeServletConfig;
import org.apache.velocity.tools.view.ServletUtils;
import org.apache.velocity.tools.view.ViewToolContext;
import org.apache.velocity.tools.view.ViewToolManager;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.apache.velocity.util.SimplePool;

public class VelocityView
extends ViewToolManager {
    public static final String CONTENT_TYPE_KEY = "default.contentType";
    public static final String SERVLET_CONTEXT_KEY = ServletContext.class.getName();
    public static final String DEFAULT_CONTENT_TYPE = "text/html";
    public static final String DEFAULT_OUTPUT_ENCODING = "ISO-8859-1";
    public static final String TOOLS_KEY = "org.apache.velocity.tools";
    @Deprecated
    public static final String DEPRECATED_TOOLS_KEY = "org.apache.velocity.toolbox";
    public static final String USER_TOOLS_PATH = "/WEB-INF/tools.xml";
    @Deprecated
    public static final String DEPRECATED_USER_TOOLS_PATH = "/WEB-INF/toolbox.xml";
    public static final String DEFAULT_PROPERTIES_PATH = "/org/apache/velocity/tools/view/velocity.properties";
    public static final String PROPERTIES_KEY = "org.apache.velocity.properties";
    public static final String USER_PROPERTIES_PATH = "/WEB-INF/velocity.properties";
    public static final String LOAD_DEFAULTS_KEY = "org.apache.velocity.tools.loadDefaults";
    public static final String CLEAN_CONFIGURATION_KEY = "org.apache.velocity.tools.cleanConfiguration";
    public static final String USER_OVERWRITE_KEY = "org.apache.velocity.tools.userCanOverwriteTools";
    public static final String DEPRECATION_SUPPORT_MODE_KEY = "org.apache.velocity.tools.deprecationSupportMode";
    private static SimplePool writerPool = new SimplePool(40);
    private String defaultContentType = "text/html";
    private boolean deprecationSupportMode = true;

    public VelocityView(ServletConfig config) {
        this(new JeeServletConfig(config));
    }

    public VelocityView(FilterConfig config) {
        this(new JeeFilterConfig(config));
    }

    public VelocityView(ServletContext context) {
        this(new JeeContextConfig(context));
    }

    public VelocityView(JeeConfig config) {
        super(config.getServletContext(), false, false);
        this.init(config);
    }

    @Deprecated
    protected final void setDeprecationSupportMode(boolean support) {
        if (this.deprecationSupportMode != support) {
            this.deprecationSupportMode = support;
            this.debug("deprecationSupportMode is now %s", support ? "on" : "off");
        }
    }

    @Override
    public void setVelocityEngine(VelocityEngine engine) {
        if (engine == null) {
            throw new NullPointerException("VelocityEngine cannot be null");
        }
        super.setVelocityEngine(engine);
    }

    public String getDefaultContentType() {
        return this.defaultContentType;
    }

    public void setDefaultContentType(String type) {
        if (!this.defaultContentType.equals(type)) {
            this.defaultContentType = type;
            this.debug("Default Content-Type was changed to %s", type);
        }
    }

    protected String getProperty(String key, String alternate) {
        String prop = (String)this.velocity.getProperty(key);
        if (prop == null || prop.length() == 0) {
            return alternate;
        }
        return prop;
    }

    protected void init(JeeConfig config) {
        String allowOverwrite;
        String depMode;
        if (this.velocity == null) {
            this.velocity = new VelocityEngine();
        }
        if ((depMode = config.findInitParameter(DEPRECATION_SUPPORT_MODE_KEY)) != null && depMode.equalsIgnoreCase("false")) {
            this.setDeprecationSupportMode(false);
        }
        if ((allowOverwrite = config.findInitParameter(USER_OVERWRITE_KEY)) != null && allowOverwrite.equalsIgnoreCase("false")) {
            this.setUserCanOverwriteTools(false);
        }
        this.init(config, this.velocity);
        this.configure(config, this.factory);
        this.setEncoding(config);
    }

    protected void init(JeeConfig config, VelocityEngine velocity) {
        LogChuteCommonsLog.setVelocityLog(this.getLog());
        velocity.setApplicationAttribute((Object)SERVLET_CONTEXT_KEY, (Object)this.servletContext);
        this.configure(config, velocity);
        try {
            velocity.init();
        }
        catch (Exception e) {
            String msg = "Could not initialize VelocityEngine";
            this.getLog().error((Object)msg, (Throwable)e);
            e.printStackTrace();
            throw new RuntimeException(msg + ": " + e, e);
        }
    }

    protected void configure(JeeConfig config, VelocityEngine velocity) {
        ExtendedProperties defaultProperties = this.getProperties(DEFAULT_PROPERTIES_PATH, true);
        try {
            Class.forName("org.apache.velocity.tools.view.WebappUberspector");
        }
        catch (Throwable t) {
            List introspectors = defaultProperties.getList("runtime.introspector.uberspect");
            introspectors.remove("org.apache.velocity.tools.view.WebappUberspector");
            defaultProperties.setProperty("runtime.introspector.uberspect", (Object)introspectors);
        }
        velocity.setExtendedProperties(defaultProperties);
        String appPropsPath = this.servletContext.getInitParameter(PROPERTIES_KEY);
        this.setProps(velocity, appPropsPath, true);
        this.setProps(velocity, USER_PROPERTIES_PATH, false);
        String servletPropsPath = config.getInitParameter(PROPERTIES_KEY);
        this.setProps(velocity, servletPropsPath, true);
    }

    private boolean setProps(VelocityEngine velocity, String path, boolean require) {
        if (path == null) {
            return false;
        }
        ExtendedProperties props = this.getProperties(path, require);
        if (props == null) {
            return false;
        }
        this.debug("Configuring Velocity with properties at: %s", path);
        velocity.setExtendedProperties(props);
        return true;
    }

    protected void configure(JeeConfig config, ToolboxFactory factory) {
        String cleanConfig;
        FactoryConfiguration oldToolbox;
        FactoryConfiguration factoryConfig = new FactoryConfiguration("VelocityView.configure(config,factory)");
        boolean hasOldToolbox = false;
        if (this.deprecationSupportMode && (oldToolbox = this.getDeprecatedConfig(config)) != null) {
            hasOldToolbox = true;
            factoryConfig.addConfiguration(oldToolbox);
        }
        String loadDefaults = config.findInitParameter(LOAD_DEFAULTS_KEY);
        if (!hasOldToolbox && loadDefaults == null || "true".equalsIgnoreCase(loadDefaults)) {
            this.getLog().trace((Object)"Loading default tools configuration...");
            factoryConfig.addConfiguration(ConfigurationUtils.getDefaultTools());
        } else {
            this.debug("Default tools configuration has been suppressed%s", hasOldToolbox ? " to avoid conflicts with older application's context and toolbox definition." : ".");
        }
        FactoryConfiguration autoLoaded = ConfigurationUtils.getAutoLoaded(false);
        factoryConfig.addConfiguration(autoLoaded);
        String appToolsPath = this.servletContext.getInitParameter(TOOLS_KEY);
        this.setConfig(factoryConfig, appToolsPath, true);
        this.setConfig(factoryConfig, USER_TOOLS_PATH, false);
        String servletToolsPath = config.getInitParameter(TOOLS_KEY);
        this.setConfig(factoryConfig, servletToolsPath, true);
        FactoryConfiguration injected = ServletUtils.getConfiguration(this.servletContext);
        if (injected != null) {
            this.debug("Adding configuration instance in servletContext attributes as '%s'", TOOLS_KEY);
            factoryConfig.addConfiguration(injected);
        }
        if ("true".equals(cleanConfig = config.findInitParameter(CLEAN_CONFIGURATION_KEY))) {
            ConfigurationCleaner cleaner = new ConfigurationCleaner();
            cleaner.setLog(this.getLog());
            cleaner.clean(factoryConfig);
        }
        this.debug("Configuring factory with: %s", factoryConfig);
        this.configure(factoryConfig);
    }

    @Deprecated
    protected FactoryConfiguration getDeprecatedConfig(JeeConfig config) {
        FactoryConfiguration toolbox = null;
        String oldPath = config.findInitParameter(DEPRECATED_TOOLS_KEY);
        if (oldPath != null) {
            toolbox = this.getConfiguration(oldPath, true);
        } else {
            oldPath = DEPRECATED_USER_TOOLS_PATH;
            toolbox = this.getConfiguration(oldPath);
        }
        if (toolbox != null) {
            this.debug("Loaded deprecated configuration from: %s", oldPath);
            this.getLog().warn((Object)"Please upgrade to new \"/WEB-INF/tools.xml\" format and conventional location. Support for \"/WEB-INF/toolbox.xml\" format and conventional file name will be removed in a future version.");
        }
        return toolbox;
    }

    private boolean setConfig(FactoryConfiguration factory, String path, boolean require) {
        if (path == null) {
            return false;
        }
        FactoryConfiguration config = this.getConfiguration(path, require);
        if (config == null) {
            return false;
        }
        this.debug("Loaded configuration from: %s", path);
        factory.addConfiguration(config);
        return true;
    }

    protected InputStream getInputStream(String path, boolean required) {
        InputStream inputStream = ServletUtils.getInputStream(path, this.servletContext);
        if (inputStream == null) {
            String msg = "Did not find resource at: " + path;
            if (required) {
                this.getLog().error((Object)msg);
                throw new ResourceNotFoundException(msg);
            }
            this.debug(msg, new Object[0]);
            return null;
        }
        return inputStream;
    }

    protected ExtendedProperties getProperties(String path) {
        return this.getProperties(path, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ExtendedProperties getProperties(String path, boolean required) {
        InputStream inputStream;
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)("Searching for properties at: " + path));
        }
        if ((inputStream = this.getInputStream(path, required)) == null) {
            return null;
        }
        ExtendedProperties properties = new ExtendedProperties();
        try {
            properties.load(inputStream);
        }
        catch (IOException ioe) {
            String msg = "Failed to load properties at: " + path;
            this.getLog().error((Object)msg, (Throwable)ioe);
            if (required) {
                throw new RuntimeException(msg, ioe);
            }
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException ioe) {
                this.getLog().error((Object)("Failed to close input stream for " + path), (Throwable)ioe);
            }
        }
        return properties;
    }

    protected FactoryConfiguration getConfiguration(String path) {
        return this.getConfiguration(path, false);
    }

    protected FactoryConfiguration getConfiguration(String path, boolean required) {
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)("Searching for configuration at: " + path));
        }
        FactoryConfiguration config = null;
        try {
            config = ServletUtils.getConfiguration(path, this.servletContext, this.deprecationSupportMode);
            if (config == null) {
                String msg = "Did not find resource at: " + path;
                if (required) {
                    this.getLog().error((Object)msg);
                    throw new ResourceNotFoundException(msg);
                }
                this.debug(msg, new Object[0]);
            }
        }
        catch (ResourceNotFoundException rnfe) {
            throw rnfe;
        }
        catch (RuntimeException re) {
            if (required) {
                this.getLog().error((Object)re.getMessage(), (Throwable)re);
                throw re;
            }
            this.getLog().debug((Object)re.getMessage(), (Throwable)re);
        }
        return config;
    }

    protected void setEncoding(JeeConfig config) {
        this.defaultContentType = this.getProperty(CONTENT_TYPE_KEY, DEFAULT_CONTENT_TYPE);
        String encoding = this.getProperty("output.encoding", DEFAULT_OUTPUT_ENCODING);
        if (!DEFAULT_OUTPUT_ENCODING.equalsIgnoreCase(encoding)) {
            int index = this.defaultContentType.lastIndexOf("charset");
            if (index < 0) {
                this.defaultContentType = this.defaultContentType + "; charset=" + encoding;
            } else {
                this.getLog().info((Object)"Charset was already specified in the Content-Type property.  Output encoding property will be ignored.");
            }
        }
        this.debug("Default Content-Type is: %s", this.defaultContentType);
    }

    public Context render(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ViewToolContext context = this.createContext(request, response);
        Template template = this.getTemplate(request, response);
        this.merge(template, context, response.getWriter());
        return context;
    }

    public Context render(HttpServletRequest request, Writer out) throws IOException {
        ViewToolContext context = this.createContext(request, null);
        Template template = this.getTemplate(request);
        this.merge(template, context, out);
        return context;
    }

    @Override
    public ViewToolContext createContext(HttpServletRequest request, HttpServletResponse response) {
        ViewToolContext ctx = this.deprecationSupportMode ? new ChainedContext(this.velocity, request, response, this.servletContext) : new ViewToolContext(this.velocity, request, response, this.servletContext);
        this.prepareContext(ctx, request);
        return ctx;
    }

    public Template getTemplate(HttpServletRequest request) {
        return this.getTemplate(request, null);
    }

    public Template getTemplate(HttpServletRequest request, HttpServletResponse response) {
        String path = ServletUtils.getPath(request);
        if (response == null) {
            return this.getTemplate(path);
        }
        return this.getTemplate(path, response.getCharacterEncoding());
    }

    public Template getTemplate(String name) {
        return this.getTemplate(name, null);
    }

    public Template getTemplate(String name, String encoding) {
        try {
            if (encoding == null) {
                return this.velocity.getTemplate(name);
            }
            return this.velocity.getTemplate(name, encoding);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void merge(Template template, Context context, Writer writer) throws IOException {
        VelocityWriter vw = null;
        try {
            vw = (VelocityWriter)writerPool.get();
            if (vw == null) {
                vw = new VelocityWriter(writer, 4096, true);
            } else {
                vw.recycle(writer);
            }
            this.performMerge(template, context, (Writer)vw);
            vw.flush();
        }
        finally {
            if (vw != null) {
                try {
                    vw.recycle(null);
                    writerPool.put((Object)vw);
                }
                catch (Exception e) {
                    this.getLog().error((Object)("Trouble releasing VelocityWriter: " + e.getMessage()), (Throwable)e);
                }
            }
        }
    }

    protected void performMerge(Template template, Context context, Writer writer) throws IOException {
        template.merge(context, writer);
    }
}

