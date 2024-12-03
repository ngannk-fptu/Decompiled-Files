/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ObjectFactory
 *  com.opensymphony.xwork2.inject.Container
 *  com.opensymphony.xwork2.inject.Inject
 *  com.opensymphony.xwork2.util.ValueStack
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.StrutsException
 *  org.apache.struts2.views.TagLibraryDirectiveProvider
 *  org.apache.struts2.views.util.ContextUtil
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.tools.ToolContext
 *  org.apache.velocity.tools.ToolManager
 */
package org.apache.struts2.views.velocity;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.views.TagLibraryDirectiveProvider;
import org.apache.struts2.views.util.ContextUtil;
import org.apache.struts2.views.velocity.StrutsVelocityContext;
import org.apache.struts2.views.velocity.VelocityStrutsUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;

public class VelocityManager {
    private static final Logger LOG = LogManager.getLogger(VelocityManager.class);
    private ObjectFactory objectFactory;
    public static final String KEY_VELOCITY_STRUTS_CONTEXT = ".KEY_velocity.struts2.context";
    private VelocityEngine velocityEngine;
    protected ToolManager toolboxManager = null;
    private String toolBoxLocation;
    private List<String> chainedContextNames = Collections.emptyList();
    private Properties velocityProperties;
    private String customConfigFile;
    private List<TagLibraryDirectiveProvider> tagLibraries;

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Inject
    public void setContainer(Container container) {
        List list = container.getInstanceNames(TagLibraryDirectiveProvider.class).stream().map(prefix -> (TagLibraryDirectiveProvider)container.getInstance(TagLibraryDirectiveProvider.class, prefix)).collect(Collectors.toList());
        this.tagLibraries = Collections.unmodifiableList(list);
    }

    public VelocityEngine getVelocityEngine() {
        return this.velocityEngine;
    }

    public Context createContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        Context context = this.buildToolContext();
        if (context == null) {
            context = this.buildContext(stack, req, res);
        }
        req.setAttribute(KEY_VELOCITY_STRUTS_CONTEXT, (Object)context);
        return context;
    }

    protected Context buildContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        List<VelocityContext> chainedContexts = this.prepareChainedContexts(req, res, stack.getContext());
        StrutsVelocityContext context = new StrutsVelocityContext(chainedContexts, stack);
        ContextUtil.getStandardContext((ValueStack)stack, (HttpServletRequest)req, (HttpServletResponse)res).forEach((arg_0, arg_1) -> ((Context)context).put(arg_0, arg_1));
        VelocityStrutsUtil util = new VelocityStrutsUtil(this.velocityEngine, (Context)context, stack, req, res);
        context.put("struts", (Object)util);
        return context;
    }

    protected Context buildToolContext() {
        ServletContext ctx;
        if (this.toolboxManager == null) {
            return null;
        }
        try {
            ctx = ServletActionContext.getServletContext();
        }
        catch (NullPointerException e) {
            return null;
        }
        if (ctx == null) {
            return null;
        }
        ToolContext toolContext = new ToolContext(this.velocityEngine);
        toolContext.addToolbox(this.toolboxManager.getToolboxFactory().createToolbox("request"));
        return toolContext;
    }

    protected List<VelocityContext> prepareChainedContexts(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Map<String, Object> extraContext) {
        ArrayList<VelocityContext> contextList = new ArrayList<VelocityContext>();
        for (String className : this.chainedContextNames) {
            try {
                VelocityContext velocityContext = (VelocityContext)this.objectFactory.buildBean(className, extraContext);
                contextList.add(velocityContext);
            }
            catch (Exception e) {
                LOG.warn(String.format("Unable to instantiate chained VelocityContext %s, skipping", className), (Throwable)e);
            }
        }
        return contextList;
    }

    public synchronized void init(ServletContext context) {
        if (this.velocityEngine == null) {
            this.velocityEngine = this.newVelocityEngine(context);
        }
        this.initToolbox(context);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Properties loadConfiguration(ServletContext context) {
        String userdirective;
        if (context == null) {
            String gripe = "Error attempting to create a loadConfiguration from a null ServletContext!";
            LOG.error(gripe);
            throw new IllegalArgumentException(gripe);
        }
        Properties properties = new Properties();
        this.applyDefaultConfiguration(context, properties);
        String defaultUserDirective = properties.getProperty("userdirective");
        String configfile = this.customConfigFile != null ? this.customConfigFile : "velocity.properties";
        configfile = configfile.trim();
        InputStream in = null;
        String resourceLocation = null;
        try {
            String filename;
            if (context.getRealPath(configfile) != null && (filename = context.getRealPath(configfile)) != null) {
                File file = new File(filename);
                if (file.isFile()) {
                    resourceLocation = file.getCanonicalPath() + " from file system";
                    in = new FileInputStream(file);
                }
                if (in == null && (file = new File(context.getRealPath("/WEB-INF/" + configfile))).isFile()) {
                    resourceLocation = file.getCanonicalPath() + " from file system";
                    in = new FileInputStream(file);
                }
            }
            if (in == null && (in = VelocityManager.class.getClassLoader().getResourceAsStream(configfile)) != null) {
                resourceLocation = configfile + " from classloader";
            }
            if (in != null) {
                LOG.info("Initializing velocity using {}", resourceLocation);
                properties.load(in);
            }
        }
        catch (IOException e) {
            LOG.warn("Unable to load velocity configuration {}", resourceLocation, (Object)e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {}
            }
        }
        if (this.velocityProperties != null) {
            for (Object o : this.velocityProperties.keySet()) {
                String key = (String)o;
                properties.setProperty(key, this.velocityProperties.getProperty(key));
            }
        }
        userdirective = (userdirective = properties.getProperty("userdirective")) == null || userdirective.trim().isEmpty() ? defaultUserDirective : userdirective.trim() + "," + defaultUserDirective;
        properties.setProperty("userdirective", userdirective);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing Velocity with the following properties ...");
            for (Object o : properties.keySet()) {
                String key = (String)o;
                String value = properties.getProperty(key);
                LOG.debug("    '{}' = '{}'", (Object)key, (Object)value);
            }
        }
        return properties;
    }

    @Inject(value="struts.velocity.configfile")
    public void setCustomConfigFile(String val) {
        this.customConfigFile = val;
    }

    @Inject(value="struts.velocity.toolboxlocation")
    public void setToolBoxLocation(String toolboxLocation) {
        this.toolBoxLocation = toolboxLocation;
    }

    public ToolManager getToolboxManager() {
        return this.toolboxManager;
    }

    @Inject(value="struts.velocity.contexts")
    public void setChainedContexts(String contexts) {
        this.chainedContextNames = Arrays.stream(contexts.split(",")).filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    protected void initToolbox(ServletContext servletContext) {
        if (StringUtils.isBlank((CharSequence)this.toolBoxLocation)) {
            LOG.debug("Skipping ToolManager initialisation, [{}] was not defined", (Object)"struts.velocity.toolboxlocation");
            return;
        }
        LOG.debug("Configuring Velocity ToolManager with {}", (Object)this.toolBoxLocation);
        this.toolboxManager = new ToolManager();
        this.toolboxManager.configure(this.toolBoxLocation);
    }

    protected VelocityEngine newVelocityEngine(ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Error attempting to create a new VelocityEngine from a null ServletContext!");
        }
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setApplicationAttribute((Object)ServletContext.class.getName(), (Object)context);
        try {
            velocityEngine.init(this.loadConfiguration(context));
        }
        catch (Exception e) {
            throw new StrutsException("Unable to instantiate VelocityEngine!", (Throwable)e);
        }
        return velocityEngine;
    }

    private void applyDefaultConfiguration(ServletContext context, Properties properties) {
        LOG.debug("Load a default resource loader definition if there isn't one present.");
        if (properties.getProperty("resource.loader") == null) {
            properties.setProperty("resource.loader", "strutsfile, strutsclass");
        }
        if (context.getRealPath("") != null) {
            properties.setProperty("strutsfile.resource.loader.description", "Velocity File Resource Loader");
            properties.setProperty("strutsfile.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
            properties.setProperty("strutsfile.resource.loader.path", context.getRealPath(""));
            properties.setProperty("strutsfile.resource.loader.modificationCheckInterval", "2");
            properties.setProperty("strutsfile.resource.loader.cache", "true");
        } else {
            String prop = properties.getProperty("resource.loader");
            if (prop.contains("strutsfile,")) {
                prop = prop.replace("strutsfile,", "");
            } else if (prop.contains(", strutsfile")) {
                prop = prop.replace(", strutsfile", "");
            } else if (prop.contains("strutsfile")) {
                prop = prop.replace("strutsfile", "");
            }
            properties.setProperty("resource.loader", prop);
        }
        properties.setProperty("strutsclass.resource.loader.description", "Velocity Classpath Resource Loader");
        properties.setProperty("strutsclass.resource.loader.class", "org.apache.struts2.views.velocity.StrutsResourceLoader");
        properties.setProperty("strutsclass.resource.loader.modificationCheckInterval", "2");
        properties.setProperty("strutsclass.resource.loader.cache", "true");
        String directives = this.tagLibraries.stream().map(TagLibraryDirectiveProvider::getDirectiveClasses).flatMap(Collection::stream).map(directive -> directive.getName() + ",").collect(Collectors.joining());
        String userdirective = properties.getProperty("userdirective");
        userdirective = userdirective == null || userdirective.trim().isEmpty() ? directives : userdirective.trim() + "," + directives;
        properties.setProperty("userdirective", userdirective);
    }

    public Properties getVelocityProperties() {
        return this.velocityProperties;
    }

    public void setVelocityProperties(Properties velocityProperties) {
        this.velocityProperties = velocityProperties;
    }
}

