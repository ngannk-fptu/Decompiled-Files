/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.cache.ClassTemplateLoader
 *  freemarker.cache.FileTemplateLoader
 *  freemarker.cache.MultiTemplateLoader
 *  freemarker.cache.TemplateLoader
 *  freemarker.cache.WebappTemplateLoader
 *  freemarker.core.HTMLOutputFormat
 *  freemarker.core.OutputFormat
 *  freemarker.core.TemplateClassResolver
 *  freemarker.ext.jsp.TaglibFactory
 *  freemarker.ext.servlet.HttpRequestHashModel
 *  freemarker.ext.servlet.HttpRequestParametersHashModel
 *  freemarker.ext.servlet.HttpSessionHashModel
 *  freemarker.ext.servlet.ServletContextHashModel
 *  freemarker.template.Configuration
 *  freemarker.template.ObjectWrapper
 *  freemarker.template.TemplateException
 *  freemarker.template.TemplateExceptionHandler
 *  freemarker.template.TemplateModel
 *  freemarker.template.Version
 *  freemarker.template.utility.StringUtil
 *  javax.servlet.GenericServlet
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.OutputFormat;
import freemarker.core.TemplateClassResolver;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.Version;
import freemarker.template.utility.StringUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.views.JspSupportServlet;
import org.apache.struts2.views.TagLibraryModelProvider;
import org.apache.struts2.views.freemarker.FreemarkerThemeTemplateLoader;
import org.apache.struts2.views.freemarker.ScopesHashModel;
import org.apache.struts2.views.freemarker.StrutsBeanWrapper;
import org.apache.struts2.views.freemarker.StrutsClassTemplateLoader;
import org.apache.struts2.views.util.ContextUtil;

public class FreemarkerManager {
    public static final String INITPARAM_TEMPLATE_PATH = "TemplatePath";
    public static final String INITPARAM_NOCACHE = "NoCache";
    public static final String INITPARAM_CONTENT_TYPE = "ContentType";
    public static final String DEFAULT_CONTENT_TYPE = "text/html";
    public static final String INITPARAM_DEBUG = "Debug";
    public static final String KEY_REQUEST = "Request";
    public static final String KEY_INCLUDE = "include_page";
    public static final String KEY_REQUEST_PRIVATE = "__FreeMarkerServlet.Request__";
    public static final String KEY_REQUEST_PARAMETERS = "RequestParameters";
    public static final String KEY_SESSION = "Session";
    public static final String KEY_APPLICATION = "Application";
    public static final String KEY_APPLICATION_PRIVATE = "__FreeMarkerServlet.Application__";
    public static final String KEY_JSP_TAGLIBS = "JspTaglibs";
    private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
    private static final String ATTR_REQUEST_PARAMETERS_MODEL = ".freemarker.RequestParameters";
    private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
    private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";
    public static final String ATTR_TEMPLATE_MODEL = ".freemarker.TemplateModel";
    public static final String KEY_REQUEST_PARAMETERS_STRUTS = "Parameters";
    public static final String KEY_HASHMODEL_PRIVATE = "__FreeMarkerManager.Request__";
    public static final String EXPIRATION_DATE;
    boolean contentTypeEvaluated = false;
    private static final Logger LOG;
    public static final String CONFIG_SERVLET_CONTEXT_KEY = "freemarker.Configuration";
    public static final String KEY_EXCEPTION = "exception";
    protected String templatePath;
    protected boolean nocache;
    protected boolean debug;
    protected Configuration config;
    protected ObjectWrapper wrapper;
    protected String contentType = null;
    protected boolean noCharsetInContentType = true;
    protected String encoding;
    protected boolean altMapWrapper;
    protected boolean cacheBeanWrapper;
    protected int mruMaxStrongSize;
    protected String templateUpdateDelay;
    protected Map<String, TagLibraryModelProvider> tagLibraries;
    private FileManager fileManager;
    private FreemarkerThemeTemplateLoader themeTemplateLoader;

    @Inject(value="struts.i18n.encoding")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Inject(value="struts.freemarker.wrapper.altMap")
    public void setWrapperAltMap(String val) {
        this.altMapWrapper = "true".equals(val);
    }

    @Inject(value="struts.freemarker.beanwrapperCache")
    public void setCacheBeanWrapper(String val) {
        this.cacheBeanWrapper = "true".equals(val);
    }

    @Inject(value="struts.freemarker.mru.max.strong.size")
    public void setMruMaxStrongSize(String size) {
        this.mruMaxStrongSize = Integer.parseInt(size);
    }

    @Inject(value="struts.freemarker.templatesCache.updateDelay", required=false)
    public void setTemplateUpdateDelay(String delay) {
        this.templateUpdateDelay = delay;
    }

    @Inject
    public void setContainer(Container container) {
        HashMap<String, TagLibraryModelProvider> map = new HashMap<String, TagLibraryModelProvider>();
        Set<String> prefixes = container.getInstanceNames(TagLibraryModelProvider.class);
        for (String prefix : prefixes) {
            map.put(prefix, container.getInstance(TagLibraryModelProvider.class, prefix));
        }
        this.tagLibraries = Collections.unmodifiableMap(map);
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    @Inject
    public void setThemeTemplateLoader(FreemarkerThemeTemplateLoader themeTemplateLoader) {
        this.themeTemplateLoader = themeTemplateLoader;
    }

    public boolean getNoCharsetInContentType() {
        return this.noCharsetInContentType;
    }

    public String getTemplatePath() {
        return this.templatePath;
    }

    public boolean getNocache() {
        return this.nocache;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public ObjectWrapper getWrapper() {
        return this.wrapper;
    }

    public String getContentType() {
        return this.contentType;
    }

    public synchronized Configuration getConfiguration(ServletContext servletContext) {
        if (this.config == null) {
            try {
                this.init(servletContext);
            }
            catch (TemplateException e) {
                LOG.error("Cannot load freemarker configuration: ", (Throwable)e);
            }
            servletContext.setAttribute(CONFIG_SERVLET_CONTEXT_KEY, (Object)this.config);
        }
        return this.config;
    }

    public void init(ServletContext servletContext) throws TemplateException {
        this.config = this.createConfiguration(servletContext);
        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        this.contentType = DEFAULT_CONTENT_TYPE;
        this.wrapper = this.createObjectWrapper(servletContext);
        LOG.debug("Using object wrapper of class {}", (Object)this.wrapper.getClass().getName());
        this.config.setObjectWrapper(this.wrapper);
        this.templatePath = servletContext.getInitParameter(INITPARAM_TEMPLATE_PATH);
        if (this.templatePath == null) {
            this.templatePath = servletContext.getInitParameter("templatePath");
        }
        this.configureTemplateLoader(this.createTemplateLoader(servletContext, this.templatePath));
        this.loadSettings(servletContext);
    }

    protected void configureTemplateLoader(TemplateLoader templateLoader) {
        this.themeTemplateLoader.init(templateLoader);
        this.config.setTemplateLoader((TemplateLoader)this.themeTemplateLoader);
    }

    protected Configuration createConfiguration(ServletContext servletContext) throws TemplateException {
        Version incompatibleImprovements = this.getFreemarkerVersion(servletContext);
        Configuration configuration = new Configuration(incompatibleImprovements);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        if (this.mruMaxStrongSize > 0) {
            LOG.debug("Sets Configuration.CACHE_STORAGE_KEY to strong:{}", (Object)this.mruMaxStrongSize);
            configuration.setSetting("cache_storage", "strong:" + this.mruMaxStrongSize);
        }
        if (this.templateUpdateDelay != null) {
            LOG.debug("Sets Configuration.TEMPLATE_UPDATE_DELAY_KEY to {}", (Object)this.templateUpdateDelay);
            configuration.setSetting("template_update_delay", this.templateUpdateDelay);
        }
        if (this.encoding != null) {
            LOG.debug("Sets DefaultEncoding to {}", (Object)this.encoding);
            configuration.setDefaultEncoding(this.encoding);
        }
        LOG.debug("Disabled localized lookups");
        configuration.setLocalizedLookup(false);
        LOG.debug("Enabled whitespace stripping");
        configuration.setWhitespaceStripping(true);
        LOG.debug("Sets NewBuiltinClassResolver to TemplateClassResolver.SAFER_RESOLVER");
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
        LOG.debug("Sets HTML as an output format and escaping policy");
        configuration.setAutoEscapingPolicy(21);
        configuration.setOutputFormat((OutputFormat)HTMLOutputFormat.INSTANCE);
        return configuration;
    }

    protected Version getFreemarkerVersion(ServletContext servletContext) {
        Version incompatibleImprovements = Configuration.VERSION_2_3_28;
        String incompatibleImprovementsParam = servletContext.getInitParameter("freemarker.incompatible_improvements");
        if (incompatibleImprovementsParam != null) {
            incompatibleImprovements = new Version(incompatibleImprovementsParam);
        }
        return incompatibleImprovements;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ScopesHashModel buildScopesHashModel(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper, ValueStack stack) {
        HttpRequestHashModel requestModel;
        ScopesHashModel model = new ScopesHashModel(wrapper, servletContext, request, stack);
        ServletContext servletContext2 = servletContext;
        synchronized (servletContext2) {
            ServletContextHashModel servletContextModel = (ServletContextHashModel)servletContext.getAttribute(ATTR_APPLICATION_MODEL);
            if (servletContextModel == null) {
                JspSupportServlet servlet = JspSupportServlet.jspSupportServlet;
                if (servlet != null) {
                    servletContextModel = new ServletContextHashModel((GenericServlet)servlet, wrapper);
                    servletContext.setAttribute(ATTR_APPLICATION_MODEL, (Object)servletContextModel);
                } else {
                    servletContextModel = new ServletContextHashModel(servletContext, wrapper);
                    servletContext.setAttribute(ATTR_APPLICATION_MODEL, (Object)servletContextModel);
                }
                TaglibFactory taglibs = new TaglibFactory(servletContext);
                taglibs.setObjectWrapper(wrapper);
                servletContext.setAttribute(ATTR_JSP_TAGLIBS_MODEL, (Object)taglibs);
            }
            model.put(KEY_APPLICATION, servletContextModel);
            model.putUnlistedModel(KEY_APPLICATION_PRIVATE, (TemplateModel)servletContextModel);
        }
        model.put(KEY_JSP_TAGLIBS, (TemplateModel)servletContext.getAttribute(ATTR_JSP_TAGLIBS_MODEL));
        HttpSession session = request.getSession(false);
        if (session != null) {
            model.put(KEY_SESSION, new HttpSessionHashModel(session, wrapper));
        }
        if ((requestModel = (HttpRequestHashModel)request.getAttribute(ATTR_REQUEST_MODEL)) == null || requestModel.getRequest() != request) {
            requestModel = new HttpRequestHashModel(request, response, wrapper);
            request.setAttribute(ATTR_REQUEST_MODEL, (Object)requestModel);
        }
        model.put(KEY_REQUEST, requestModel);
        HttpRequestParametersHashModel reqParametersModel = (HttpRequestParametersHashModel)request.getAttribute(ATTR_REQUEST_PARAMETERS_MODEL);
        if (reqParametersModel == null || requestModel.getRequest() != request) {
            reqParametersModel = new HttpRequestParametersHashModel(request);
            request.setAttribute(ATTR_REQUEST_PARAMETERS_MODEL, (Object)reqParametersModel);
        }
        model.put(ATTR_REQUEST_PARAMETERS_MODEL, reqParametersModel);
        model.put(KEY_REQUEST_PARAMETERS_STRUTS, reqParametersModel);
        return model;
    }

    protected ObjectWrapper createObjectWrapper(ServletContext servletContext) {
        Version incompatibleImprovements = this.getFreemarkerVersion(servletContext);
        StrutsBeanWrapper wrapper = new StrutsBeanWrapper(this.altMapWrapper, incompatibleImprovements);
        wrapper.setUseCache(this.cacheBeanWrapper);
        return wrapper;
    }

    protected TemplateLoader createTemplateLoader(ServletContext servletContext, String templatePath) {
        ClassTemplateLoader templatePathLoader = null;
        try {
            if (templatePath != null) {
                if (templatePath.startsWith("class://")) {
                    templatePathLoader = new ClassTemplateLoader(this.getClass(), templatePath.substring(7));
                } else if (templatePath.startsWith("file://")) {
                    templatePathLoader = new FileTemplateLoader(new File(templatePath.substring(7)));
                }
            }
        }
        catch (IOException e) {
            LOG.error("Invalid template path specified: {}", (Object)e.getMessage(), (Object)e);
        }
        return templatePathLoader != null ? new MultiTemplateLoader(new TemplateLoader[]{templatePathLoader, new WebappTemplateLoader(servletContext), new StrutsClassTemplateLoader()}) : new MultiTemplateLoader(new TemplateLoader[]{new WebappTemplateLoader(servletContext), new StrutsClassTemplateLoader()});
    }

    protected void loadSettings(ServletContext servletContext) {
        try (InputStream in = this.fileManager.loadFile(ClassLoaderUtil.getResource("freemarker.properties", this.getClass()));){
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                for (Object o : p.keySet()) {
                    String name = (String)o;
                    String value = (String)p.get(name);
                    if (name == null) {
                        throw new IOException("init-param without param-name.  Maybe the freemarker.properties is not well-formed?");
                    }
                    if (value == null) {
                        throw new IOException("init-param without param-value.  Maybe the freemarker.properties is not well-formed?");
                    }
                    this.addSetting(name, value);
                }
            }
        }
        catch (IOException e) {
            LOG.error("Error while loading freemarker settings from /freemarker.properties", (Throwable)e);
        }
        catch (TemplateException e) {
            LOG.error("Error while loading freemarker settings from /freemarker.properties", (Throwable)e);
        }
    }

    public void addSetting(String name, String value) throws TemplateException {
        if (name.equals(INITPARAM_NOCACHE)) {
            this.nocache = StringUtil.getYesNo((String)value);
        } else if (name.equals(INITPARAM_DEBUG)) {
            this.debug = StringUtil.getYesNo((String)value);
        } else if (name.equals(INITPARAM_CONTENT_TYPE)) {
            this.contentType = value;
        } else {
            this.config.setSetting(name, value);
        }
        if (this.contentType != null && !this.contentTypeEvaluated) {
            int i = this.contentType.toLowerCase().indexOf("charset=");
            this.contentTypeEvaluated = true;
            if (i != -1) {
                char c = ' ';
                --i;
                while (i >= 0 && Character.isWhitespace(c = (char)this.contentType.charAt(i))) {
                    --i;
                }
                if (i == -1 || c == ';') {
                    this.noCharsetInContentType = false;
                }
            }
        }
    }

    public ScopesHashModel buildTemplateModel(ValueStack stack, Object action, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper) {
        ScopesHashModel model = this.buildScopesHashModel(servletContext, request, response, wrapper, stack);
        this.populateContext(model, stack, action, request, response);
        if (this.tagLibraries != null) {
            for (Map.Entry<String, TagLibraryModelProvider> entry : this.tagLibraries.entrySet()) {
                model.put(entry.getKey(), entry.getValue().getModels(stack, request, response));
            }
        }
        request.setAttribute(ATTR_TEMPLATE_MODEL, (Object)model);
        return model;
    }

    protected void populateContext(ScopesHashModel model, ValueStack stack, Object action, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> standard = ContextUtil.getStandardContext(stack, request, response);
        model.putAll(standard);
        Throwable exception = (Throwable)request.getAttribute("javax.servlet.error.exception");
        if (exception == null) {
            exception = (Throwable)request.getAttribute("javax.servlet.error.JspException");
        }
        if (exception != null) {
            model.put(KEY_EXCEPTION, exception);
        }
    }

    static {
        GregorianCalendar expiration = new GregorianCalendar();
        expiration.roll(1, -1);
        SimpleDateFormat httpDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        EXPIRATION_DATE = httpDate.format(expiration.getTime());
        LOG = LogManager.getLogger(FreemarkerManager.class);
    }
}

