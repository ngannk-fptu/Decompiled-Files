/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.GenericServlet
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package freemarker.ext.servlet;

import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.IncludePage;
import freemarker.ext.servlet.InitParamParser;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FreemarkerServlet
extends HttpServlet {
    private static final Logger LOG = Logger.getLogger("freemarker.servlet");
    private static final Logger LOG_RT = Logger.getLogger("freemarker.runtime");
    public static final long serialVersionUID = -2440216393145762479L;
    public static final String INIT_PARAM_TEMPLATE_PATH = "TemplatePath";
    public static final String INIT_PARAM_NO_CACHE = "NoCache";
    public static final String INIT_PARAM_CONTENT_TYPE = "ContentType";
    public static final String INIT_PARAM_OVERRIDE_RESPONSE_CONTENT_TYPE = "OverrideResponseContentType";
    public static final String INIT_PARAM_RESPONSE_CHARACTER_ENCODING = "ResponseCharacterEncoding";
    public static final String INIT_PARAM_OVERRIDE_RESPONSE_LOCALE = "OverrideResponseLocale";
    public static final String INIT_PARAM_BUFFER_SIZE = "BufferSize";
    public static final String INIT_PARAM_META_INF_TLD_LOCATIONS = "MetaInfTldSources";
    public static final String INIT_PARAM_EXCEPTION_ON_MISSING_TEMPLATE = "ExceptionOnMissingTemplate";
    public static final String INIT_PARAM_CLASSPATH_TLDS = "ClasspathTlds";
    private static final String INIT_PARAM_DEBUG = "Debug";
    private static final String DEPR_INITPARAM_TEMPLATE_DELAY = "TemplateDelay";
    private static final String DEPR_INITPARAM_ENCODING = "DefaultEncoding";
    private static final String DEPR_INITPARAM_OBJECT_WRAPPER = "ObjectWrapper";
    private static final String DEPR_INITPARAM_WRAPPER_SIMPLE = "simple";
    private static final String DEPR_INITPARAM_WRAPPER_BEANS = "beans";
    private static final String DEPR_INITPARAM_WRAPPER_JYTHON = "jython";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER = "TemplateExceptionHandler";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_RETHROW = "rethrow";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_DEBUG = "debug";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_HTML_DEBUG = "htmlDebug";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_IGNORE = "ignore";
    private static final String DEPR_INITPARAM_DEBUG = "debug";
    private static final ContentType DEFAULT_CONTENT_TYPE = new ContentType("text/html");
    public static final String INIT_PARAM_VALUE_NEVER = "never";
    public static final String INIT_PARAM_VALUE_ALWAYS = "always";
    public static final String INIT_PARAM_VALUE_WHEN_TEMPLATE_HAS_MIME_TYPE = "whenTemplateHasMimeType";
    public static final String INIT_PARAM_VALUE_FROM_TEMPLATE = "fromTemplate";
    public static final String INIT_PARAM_VALUE_LEGACY = "legacy";
    public static final String INIT_PARAM_VALUE_DO_NOT_SET = "doNotSet";
    public static final String INIT_PARAM_VALUE_FORCE_PREFIX = "force ";
    public static final String SYSTEM_PROPERTY_META_INF_TLD_SOURCES = "org.freemarker.jsp.metaInfTldSources";
    public static final String SYSTEM_PROPERTY_CLASSPATH_TLDS = "org.freemarker.jsp.classpathTlds";
    public static final String META_INF_TLD_LOCATION_WEB_INF_PER_LIB_JARS = "webInfPerLibJars";
    public static final String META_INF_TLD_LOCATION_CLASSPATH = "classpath";
    public static final String META_INF_TLD_LOCATION_CLEAR = "clear";
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
    private static final String ATTR_SESSION_MODEL = ".freemarker.Session";
    @Deprecated
    private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
    @Deprecated
    private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";
    private static final String ATTR_JETTY_CP_TAGLIB_JAR_PATTERNS = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";
    private static final String EXPIRATION_DATE;
    private String templatePath;
    private boolean noCache;
    private Integer bufferSize;
    private boolean exceptionOnMissingTemplate;
    @Deprecated
    protected boolean debug;
    private Configuration config;
    private ObjectWrapper wrapper;
    private ContentType contentType;
    private OverrideResponseContentType overrideResponseContentType = (OverrideResponseContentType)this.initParamValueToEnum(this.getDefaultOverrideResponseContentType(), OverrideResponseContentType.values());
    private ResponseCharacterEncoding responseCharacterEncoding = ResponseCharacterEncoding.LEGACY;
    private Charset forcedResponseCharacterEncoding;
    private OverrideResponseLocale overrideResponseLocale = OverrideResponseLocale.ALWAYS;
    private List metaInfTldSources;
    private List classpathTlds;
    private Object lazyInitFieldsLock = new Object();
    private ServletContextHashModel servletContextModel;
    private TaglibFactory taglibFactory;
    private boolean objectWrapperMismatchWarnLogged;

    public void init() throws ServletException {
        try {
            this.initialize();
        }
        catch (Exception e) {
            throw new ServletException("Error while initializing " + ((Object)((Object)this)).getClass().getName() + " servlet; see cause exception.", (Throwable)e);
        }
    }

    private void initialize() throws InitParamValueException, MalformedWebXmlException, ConflictingInitParamsException {
        this.config = this.createConfiguration();
        String iciInitParamValue = this.getInitParameter("incompatible_improvements");
        if (iciInitParamValue != null) {
            try {
                this.config.setSetting("incompatible_improvements", iciInitParamValue);
            }
            catch (Exception e) {
                throw new InitParamValueException("incompatible_improvements", iciInitParamValue, e);
            }
        }
        if (!this.config.isTemplateExceptionHandlerExplicitlySet()) {
            this.config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        }
        if (!this.config.isLogTemplateExceptionsExplicitlySet()) {
            this.config.setLogTemplateExceptions(false);
        }
        this.contentType = DEFAULT_CONTENT_TYPE;
        this.wrapper = this.createObjectWrapper();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Using object wrapper: " + this.wrapper);
        }
        this.config.setObjectWrapper(this.wrapper);
        this.templatePath = this.getInitParameter(INIT_PARAM_TEMPLATE_PATH);
        if (this.templatePath == null && !this.config.isTemplateLoaderExplicitlySet()) {
            this.templatePath = "class://";
        }
        if (this.templatePath != null) {
            try {
                this.config.setTemplateLoader(this.createTemplateLoader(this.templatePath));
            }
            catch (Exception e) {
                throw new InitParamValueException(INIT_PARAM_TEMPLATE_PATH, this.templatePath, e);
            }
        }
        this.metaInfTldSources = this.createDefaultMetaInfTldSources();
        this.classpathTlds = this.createDefaultClassPathTlds();
        Enumeration initpnames = this.getServletConfig().getInitParameterNames();
        while (initpnames.hasMoreElements()) {
            String name = (String)initpnames.nextElement();
            String value = this.getInitParameter(name);
            if (name == null) {
                throw new MalformedWebXmlException("init-param without param-name. Maybe the web.xml is not well-formed?");
            }
            if (value == null) {
                throw new MalformedWebXmlException("init-param " + StringUtil.jQuote(name) + " without param-value. Maybe the web.xml is not well-formed?");
            }
            try {
                if (name.equals(DEPR_INITPARAM_OBJECT_WRAPPER) || name.equals("object_wrapper") || name.equals(INIT_PARAM_TEMPLATE_PATH) || name.equals("incompatible_improvements")) continue;
                if (name.equals(DEPR_INITPARAM_ENCODING)) {
                    if (this.getInitParameter("default_encoding") != null) {
                        throw new ConflictingInitParamsException("default_encoding", DEPR_INITPARAM_ENCODING);
                    }
                    this.config.setDefaultEncoding(value);
                    continue;
                }
                if (name.equals(DEPR_INITPARAM_TEMPLATE_DELAY)) {
                    if (this.getInitParameter("template_update_delay") != null) {
                        throw new ConflictingInitParamsException("template_update_delay", DEPR_INITPARAM_TEMPLATE_DELAY);
                    }
                    try {
                        this.config.setTemplateUpdateDelay(Integer.parseInt(value));
                    }
                    catch (NumberFormatException numberFormatException) {}
                    continue;
                }
                if (name.equals(DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER)) {
                    if (this.getInitParameter("template_exception_handler") != null) {
                        throw new ConflictingInitParamsException("template_exception_handler", DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER);
                    }
                    if (DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_RETHROW.equals(value)) {
                        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                        continue;
                    }
                    if ("debug".equals(value)) {
                        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
                        continue;
                    }
                    if (DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_HTML_DEBUG.equals(value)) {
                        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
                        continue;
                    }
                    if (DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_IGNORE.equals(value)) {
                        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
                        continue;
                    }
                    throw new InitParamValueException(DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER, value, "Not one of the supported values.");
                }
                if (name.equals(INIT_PARAM_NO_CACHE)) {
                    this.noCache = StringUtil.getYesNo(value);
                    continue;
                }
                if (name.equals(INIT_PARAM_BUFFER_SIZE)) {
                    this.bufferSize = this.parseSize(value);
                    continue;
                }
                if (name.equals("debug")) {
                    if (this.getInitParameter(INIT_PARAM_DEBUG) != null) {
                        throw new ConflictingInitParamsException(INIT_PARAM_DEBUG, "debug");
                    }
                    this.debug = StringUtil.getYesNo(value);
                    continue;
                }
                if (name.equals(INIT_PARAM_DEBUG)) {
                    this.debug = StringUtil.getYesNo(value);
                    continue;
                }
                if (name.equals(INIT_PARAM_CONTENT_TYPE)) {
                    this.contentType = new ContentType(value);
                    continue;
                }
                if (name.equals(INIT_PARAM_OVERRIDE_RESPONSE_CONTENT_TYPE)) {
                    this.overrideResponseContentType = (OverrideResponseContentType)this.initParamValueToEnum(value, OverrideResponseContentType.values());
                    continue;
                }
                if (name.equals(INIT_PARAM_RESPONSE_CHARACTER_ENCODING)) {
                    this.responseCharacterEncoding = (ResponseCharacterEncoding)this.initParamValueToEnum(value, ResponseCharacterEncoding.values());
                    if (this.responseCharacterEncoding != ResponseCharacterEncoding.FORCE_CHARSET) continue;
                    String charsetName = value.substring(INIT_PARAM_VALUE_FORCE_PREFIX.length()).trim();
                    this.forcedResponseCharacterEncoding = Charset.forName(charsetName);
                    continue;
                }
                if (name.equals(INIT_PARAM_OVERRIDE_RESPONSE_LOCALE)) {
                    this.overrideResponseLocale = (OverrideResponseLocale)this.initParamValueToEnum(value, OverrideResponseLocale.values());
                    continue;
                }
                if (name.equals(INIT_PARAM_EXCEPTION_ON_MISSING_TEMPLATE)) {
                    this.exceptionOnMissingTemplate = StringUtil.getYesNo(value);
                    continue;
                }
                if (name.equals(INIT_PARAM_META_INF_TLD_LOCATIONS)) {
                    this.metaInfTldSources = this.parseAsMetaInfTldLocations(value);
                    continue;
                }
                if (name.equals(INIT_PARAM_CLASSPATH_TLDS)) {
                    ArrayList newClasspathTlds = new ArrayList();
                    if (this.classpathTlds != null) {
                        newClasspathTlds.addAll(this.classpathTlds);
                    }
                    newClasspathTlds.addAll(InitParamParser.parseCommaSeparatedList(value));
                    this.classpathTlds = newClasspathTlds;
                    continue;
                }
                this.config.setSetting(name, value);
            }
            catch (ConflictingInitParamsException e) {
                throw e;
            }
            catch (Exception e) {
                throw new InitParamValueException(name, value, e);
            }
        }
        if (this.contentType.containsCharset && this.responseCharacterEncoding != ResponseCharacterEncoding.LEGACY) {
            throw new InitParamValueException(INIT_PARAM_CONTENT_TYPE, this.contentType.httpHeaderValue, new IllegalStateException("You can't specify the charset in the content type, because the \"ResponseCharacterEncoding\" init-param isn't set to \"legacy\"."));
        }
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private List parseAsMetaInfTldLocations(String value) throws ParseException {
        ArrayList<void> metaInfTldSources = null;
        List values = InitParamParser.parseCommaSeparatedList(value);
        for (String itemStr : values) {
            void var6_6;
            if (itemStr.equals(META_INF_TLD_LOCATION_WEB_INF_PER_LIB_JARS)) {
                TaglibFactory.WebInfPerLibJarMetaInfTldSource webInfPerLibJarMetaInfTldSource = TaglibFactory.WebInfPerLibJarMetaInfTldSource.INSTANCE;
            } else if (itemStr.startsWith(META_INF_TLD_LOCATION_CLASSPATH)) {
                String itemRightSide = itemStr.substring(META_INF_TLD_LOCATION_CLASSPATH.length()).trim();
                if (itemRightSide.length() == 0) {
                    TaglibFactory.ClasspathMetaInfTldSource classpathMetaInfTldSource = new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(".*", 32));
                } else {
                    if (!itemRightSide.startsWith(":")) throw new ParseException("Invalid \"classpath\" value syntax: " + value, -1);
                    String regexpStr = itemRightSide.substring(1).trim();
                    if (regexpStr.length() == 0) {
                        throw new ParseException("Empty regular expression after \"classpath:\"", -1);
                    }
                    TaglibFactory.ClasspathMetaInfTldSource classpathMetaInfTldSource = new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(regexpStr));
                }
            } else {
                if (!itemStr.startsWith(META_INF_TLD_LOCATION_CLEAR)) throw new ParseException("Item has no recognized source type prefix: " + itemStr, -1);
                TaglibFactory.ClearMetaInfTldSource clearMetaInfTldSource = TaglibFactory.ClearMetaInfTldSource.INSTANCE;
            }
            if (metaInfTldSources == null) {
                metaInfTldSources = new ArrayList<void>();
            }
            metaInfTldSources.add(var6_6);
        }
        return metaInfTldSources;
    }

    protected TemplateLoader createTemplateLoader(String templatePath) throws IOException {
        return InitParamParser.createTemplateLoader(templatePath, this.getConfiguration(), ((Object)((Object)this)).getClass(), this.getServletContext());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.process(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.process(request, response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        block29: {
            Template template;
            Locale locale;
            if (this.preprocessRequest(request, response)) {
                return;
            }
            if (this.bufferSize != null && !response.isCommitted()) {
                try {
                    response.setBufferSize(this.bufferSize.intValue());
                }
                catch (IllegalStateException e) {
                    LOG.debug("Can't set buffer size any more,", e);
                }
            }
            String templatePath = this.requestUrlToTemplatePath(request);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Requested template " + StringUtil.jQuoteNoXSS(templatePath) + ".");
            }
            if ((locale = request.getLocale()) == null || this.overrideResponseLocale != OverrideResponseLocale.NEVER) {
                locale = this.deduceLocale(templatePath, request, response);
            }
            try {
                template = this.config.getTemplate(templatePath, locale);
            }
            catch (TemplateNotFoundException e) {
                if (this.exceptionOnMissingTemplate) {
                    throw this.newServletExceptionWithFreeMarkerLogging("Template not found for name " + StringUtil.jQuoteNoXSS(templatePath) + ".", e);
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Responding HTTP 404 \"Not found\" for missing template " + StringUtil.jQuoteNoXSS(templatePath) + ".", e);
                }
                response.sendError(404, "Page template not found");
                return;
            }
            catch (freemarker.core.ParseException e) {
                throw this.newServletExceptionWithFreeMarkerLogging("Parsing error with template " + StringUtil.jQuoteNoXSS(templatePath) + ".", e);
            }
            catch (Exception e) {
                throw this.newServletExceptionWithFreeMarkerLogging("Unexpected error when loading template " + StringUtil.jQuoteNoXSS(templatePath) + ".", e);
            }
            boolean tempSpecContentTypeContainsCharset = false;
            if (response.getContentType() == null || this.overrideResponseContentType != OverrideResponseContentType.NEVER) {
                ContentType templateSpecificContentType = this.getTemplateSpecificContentType(template);
                if (templateSpecificContentType != null) {
                    response.setContentType(this.responseCharacterEncoding != ResponseCharacterEncoding.DO_NOT_SET ? templateSpecificContentType.httpHeaderValue : templateSpecificContentType.getMimeType());
                    tempSpecContentTypeContainsCharset = templateSpecificContentType.containsCharset;
                } else if (response.getContentType() == null || this.overrideResponseContentType == OverrideResponseContentType.ALWAYS) {
                    if (this.responseCharacterEncoding == ResponseCharacterEncoding.LEGACY && !this.contentType.containsCharset) {
                        response.setContentType(this.contentType.httpHeaderValue + "; charset=" + this.getTemplateSpecificOutputEncoding(template));
                    } else {
                        response.setContentType(this.contentType.httpHeaderValue);
                    }
                }
            }
            if (this.responseCharacterEncoding != ResponseCharacterEncoding.LEGACY && this.responseCharacterEncoding != ResponseCharacterEncoding.DO_NOT_SET) {
                if (this.responseCharacterEncoding != ResponseCharacterEncoding.FORCE_CHARSET) {
                    if (!tempSpecContentTypeContainsCharset) {
                        response.setCharacterEncoding(this.getTemplateSpecificOutputEncoding(template));
                    }
                } else {
                    response.setCharacterEncoding(this.forcedResponseCharacterEncoding.name());
                }
            }
            this.setBrowserCachingPolicy(response);
            ServletContext servletContext = this.getServletContext();
            try {
                this.logWarnOnObjectWrapperMismatch();
                TemplateModel model = this.createModel(this.wrapper, servletContext, request, response);
                if (!this.preTemplateProcess(request, response, template, model)) break block29;
                try {
                    String actualOutputCharset;
                    Environment env = template.createProcessingEnvironment(model, response.getWriter());
                    if (this.responseCharacterEncoding != ResponseCharacterEncoding.LEGACY && (actualOutputCharset = response.getCharacterEncoding()) != null) {
                        env.setOutputEncoding(actualOutputCharset);
                    }
                    this.processEnvironment(env, request, response);
                }
                finally {
                    this.postTemplateProcess(request, response, template, model);
                }
            }
            catch (TemplateException e) {
                TemplateExceptionHandler teh = this.config.getTemplateExceptionHandler();
                if (teh == TemplateExceptionHandler.HTML_DEBUG_HANDLER || teh == TemplateExceptionHandler.DEBUG_HANDLER || teh.getClass().getName().indexOf(INIT_PARAM_DEBUG) != -1) {
                    response.flushBuffer();
                }
                throw this.newServletExceptionWithFreeMarkerLogging("Error executing FreeMarker template", e);
            }
        }
    }

    protected void processEnvironment(Environment env, HttpServletRequest request, HttpServletResponse response) throws TemplateException, IOException {
        env.process();
    }

    private String getTemplateSpecificOutputEncoding(Template template) {
        String outputEncoding = this.responseCharacterEncoding == ResponseCharacterEncoding.LEGACY ? null : template.getOutputEncoding();
        return outputEncoding != null ? outputEncoding : template.getEncoding();
    }

    private ContentType getTemplateSpecificContentType(Template template) {
        Object contentTypeAttr = template.getCustomAttribute("content_type");
        if (contentTypeAttr != null) {
            return new ContentType(contentTypeAttr.toString());
        }
        String outputFormatMimeType = template.getOutputFormat().getMimeType();
        if (outputFormatMimeType != null) {
            if (this.responseCharacterEncoding == ResponseCharacterEncoding.LEGACY) {
                return new ContentType(outputFormatMimeType + "; charset=" + this.getTemplateSpecificOutputEncoding(template), true);
            }
            return new ContentType(outputFormatMimeType, false);
        }
        return null;
    }

    private ServletException newServletExceptionWithFreeMarkerLogging(String message, Throwable cause) throws ServletException {
        if (cause instanceof TemplateException) {
            LOG_RT.error(message, cause);
        } else {
            LOG.error(message, cause);
        }
        ServletException e = new ServletException(message, cause);
        try {
            e.initCause(cause);
        }
        catch (Exception exception) {
            // empty catch block
        }
        throw e;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void logWarnOnObjectWrapperMismatch() {
        if (this.wrapper != this.config.getObjectWrapper() && !this.objectWrapperMismatchWarnLogged && LOG.isWarnEnabled()) {
            boolean logWarn;
            FreemarkerServlet freemarkerServlet = this;
            synchronized (freemarkerServlet) {
                boolean bl = logWarn = !this.objectWrapperMismatchWarnLogged;
                if (logWarn) {
                    this.objectWrapperMismatchWarnLogged = true;
                }
            }
            if (logWarn) {
                LOG.warn(((Object)((Object)this)).getClass().getName() + ".wrapper != config.getObjectWrapper(); possibly the result of incorrect extension of " + FreemarkerServlet.class.getName() + ".");
            }
        }
    }

    protected Locale deduceLocale(String templatePath, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        return this.config.getLocale();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected TemplateModel createModel(ObjectWrapper objectWrapper, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws TemplateModelException {
        try {
            HttpSessionHashModel sessionModel;
            TaglibFactory taglibFactory;
            ServletContextHashModel servletContextModel;
            AllHttpScopesHashModel params = new AllHttpScopesHashModel(objectWrapper, servletContext, request);
            Object object = this.lazyInitFieldsLock;
            synchronized (object) {
                if (this.servletContextModel == null) {
                    servletContextModel = new ServletContextHashModel((GenericServlet)this, objectWrapper);
                    taglibFactory = this.createTaglibFactory(objectWrapper, servletContext);
                    servletContext.setAttribute(ATTR_APPLICATION_MODEL, (Object)servletContextModel);
                    servletContext.setAttribute(ATTR_JSP_TAGLIBS_MODEL, (Object)taglibFactory);
                    this.initializeServletContext(request, response);
                    this.taglibFactory = taglibFactory;
                    this.servletContextModel = servletContextModel;
                } else {
                    servletContextModel = this.servletContextModel;
                    taglibFactory = this.taglibFactory;
                }
            }
            params.putUnlistedModel(KEY_APPLICATION, servletContextModel);
            params.putUnlistedModel(KEY_APPLICATION_PRIVATE, servletContextModel);
            params.putUnlistedModel(KEY_JSP_TAGLIBS, taglibFactory);
            HttpSession session = request.getSession(false);
            if (session != null) {
                sessionModel = (HttpSessionHashModel)session.getAttribute(ATTR_SESSION_MODEL);
                if (sessionModel == null || sessionModel.isOrphaned(session)) {
                    sessionModel = new HttpSessionHashModel(session, objectWrapper);
                    this.initializeSessionAndInstallModel(request, response, sessionModel, session);
                }
            } else {
                sessionModel = new HttpSessionHashModel(this, request, response, objectWrapper);
            }
            params.putUnlistedModel(KEY_SESSION, sessionModel);
            HttpRequestHashModel requestModel = (HttpRequestHashModel)request.getAttribute(ATTR_REQUEST_MODEL);
            if (requestModel == null || requestModel.getRequest() != request) {
                requestModel = new HttpRequestHashModel(request, response, objectWrapper);
                request.setAttribute(ATTR_REQUEST_MODEL, (Object)requestModel);
                request.setAttribute(ATTR_REQUEST_PARAMETERS_MODEL, (Object)this.createRequestParametersHashModel(request));
            }
            params.putUnlistedModel(KEY_REQUEST, requestModel);
            params.putUnlistedModel(KEY_INCLUDE, new IncludePage(request, response));
            params.putUnlistedModel(KEY_REQUEST_PRIVATE, requestModel);
            HttpRequestParametersHashModel requestParametersModel = (HttpRequestParametersHashModel)request.getAttribute(ATTR_REQUEST_PARAMETERS_MODEL);
            params.putUnlistedModel(KEY_REQUEST_PARAMETERS, requestParametersModel);
            return params;
        }
        catch (IOException | ServletException e) {
            throw new TemplateModelException((Exception)e);
        }
    }

    protected TaglibFactory createTaglibFactory(ObjectWrapper objectWrapper, ServletContext servletContext) throws TemplateModelException {
        String sysPropVal;
        TaglibFactory taglibFactory = new TaglibFactory(servletContext);
        taglibFactory.setObjectWrapper(objectWrapper);
        ArrayList<TaglibFactory.ClasspathMetaInfTldSource> mergedMetaInfTldSources = new ArrayList<TaglibFactory.ClasspathMetaInfTldSource>();
        if (this.metaInfTldSources != null) {
            mergedMetaInfTldSources.addAll(this.metaInfTldSources);
        }
        if ((sysPropVal = SecurityUtilities.getSystemProperty(SYSTEM_PROPERTY_META_INF_TLD_SOURCES, null)) != null) {
            try {
                List metaInfTldSourcesSysProp = this.parseAsMetaInfTldLocations(sysPropVal);
                if (metaInfTldSourcesSysProp != null) {
                    mergedMetaInfTldSources.addAll(metaInfTldSourcesSysProp);
                }
            }
            catch (ParseException e) {
                throw new TemplateModelException("Failed to parse system property \"org.freemarker.jsp.metaInfTldSources\"", e);
            }
        }
        List jettyTaglibJarPatterns = null;
        try {
            String attrVal = (String)servletContext.getAttribute(ATTR_JETTY_CP_TAGLIB_JAR_PATTERNS);
            jettyTaglibJarPatterns = attrVal != null ? InitParamParser.parseCommaSeparatedPatterns(attrVal) : null;
        }
        catch (Exception e) {
            LOG.error("Failed to parse application context attribute \"org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern\" - it will be ignored", e);
        }
        if (jettyTaglibJarPatterns != null) {
            for (Pattern pattern : jettyTaglibJarPatterns) {
                mergedMetaInfTldSources.add(new TaglibFactory.ClasspathMetaInfTldSource(pattern));
            }
        }
        taglibFactory.setMetaInfTldSources(mergedMetaInfTldSources);
        ArrayList mergedClassPathTlds = new ArrayList();
        if (this.classpathTlds != null) {
            mergedClassPathTlds.addAll(this.classpathTlds);
        }
        if ((sysPropVal = SecurityUtilities.getSystemProperty(SYSTEM_PROPERTY_CLASSPATH_TLDS, null)) != null) {
            try {
                List classpathTldsSysProp = InitParamParser.parseCommaSeparatedList(sysPropVal);
                if (classpathTldsSysProp != null) {
                    mergedClassPathTlds.addAll(classpathTldsSysProp);
                }
            }
            catch (ParseException e) {
                throw new TemplateModelException("Failed to parse system property \"org.freemarker.jsp.classpathTlds\"", e);
            }
        }
        taglibFactory.setClasspathTlds(mergedClassPathTlds);
        return taglibFactory;
    }

    protected List createDefaultClassPathTlds() {
        return TaglibFactory.DEFAULT_CLASSPATH_TLDS;
    }

    protected List createDefaultMetaInfTldSources() {
        return TaglibFactory.DEFAULT_META_INF_TLD_SOURCES;
    }

    void initializeSessionAndInstallModel(HttpServletRequest request, HttpServletResponse response, HttpSessionHashModel sessionModel, HttpSession session) throws ServletException, IOException {
        session.setAttribute(ATTR_SESSION_MODEL, (Object)sessionModel);
        this.initializeSession(request, response);
    }

    protected String requestUrlToTemplatePath(HttpServletRequest request) throws ServletException {
        String includeServletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
        if (includeServletPath != null) {
            String includePathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
            return includePathInfo == null ? includeServletPath : includePathInfo;
        }
        String path = request.getPathInfo();
        if (path != null) {
            return path;
        }
        path = request.getServletPath();
        if (path != null) {
            return path;
        }
        return "";
    }

    protected boolean preprocessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return false;
    }

    protected Configuration createConfiguration() {
        return new Configuration();
    }

    protected void setConfigurationDefaults() {
    }

    protected ObjectWrapper createObjectWrapper() {
        String wrapper = this.getServletConfig().getInitParameter(DEPR_INITPARAM_OBJECT_WRAPPER);
        if (wrapper != null) {
            if (this.getInitParameter("object_wrapper") != null) {
                throw new RuntimeException("Conflicting init-params: object_wrapper and ObjectWrapper");
            }
            if (DEPR_INITPARAM_WRAPPER_BEANS.equals(wrapper)) {
                return ObjectWrapper.BEANS_WRAPPER;
            }
            if (DEPR_INITPARAM_WRAPPER_SIMPLE.equals(wrapper)) {
                return ObjectWrapper.SIMPLE_WRAPPER;
            }
            if (DEPR_INITPARAM_WRAPPER_JYTHON.equals(wrapper)) {
                try {
                    return (ObjectWrapper)Class.forName("freemarker.ext.jython.JythonWrapper").newInstance();
                }
                catch (InstantiationException e) {
                    throw new InstantiationError(e.getMessage());
                }
                catch (IllegalAccessException e) {
                    throw new IllegalAccessError(e.getMessage());
                }
                catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            }
            return this.createDefaultObjectWrapper();
        }
        wrapper = this.getInitParameter("object_wrapper");
        if (wrapper == null) {
            if (!this.config.isObjectWrapperExplicitlySet()) {
                return this.createDefaultObjectWrapper();
            }
            return this.config.getObjectWrapper();
        }
        try {
            this.config.setSetting("object_wrapper", wrapper);
        }
        catch (TemplateException e) {
            throw new RuntimeException("Failed to set object_wrapper", e);
        }
        return this.config.getObjectWrapper();
    }

    protected ObjectWrapper createDefaultObjectWrapper() {
        return Configuration.getDefaultObjectWrapper(this.config.getIncompatibleImprovements());
    }

    protected ObjectWrapper getObjectWrapper() {
        return this.wrapper;
    }

    @Deprecated
    protected final String getTemplatePath() {
        return this.templatePath;
    }

    protected HttpRequestParametersHashModel createRequestParametersHashModel(HttpServletRequest request) {
        return new HttpRequestParametersHashModel(request);
    }

    protected void initializeServletContext(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void initializeSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected boolean preTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel model) throws ServletException, IOException {
        return true;
    }

    protected void postTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel data) throws ServletException, IOException {
    }

    protected Configuration getConfiguration() {
        return this.config;
    }

    protected String getDefaultOverrideResponseContentType() {
        return INIT_PARAM_VALUE_ALWAYS;
    }

    private void setBrowserCachingPolicy(HttpServletResponse res) {
        if (this.noCache) {
            res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
            res.setHeader("Pragma", "no-cache");
            res.setHeader("Expires", EXPIRATION_DATE);
        }
    }

    private int parseSize(String value) throws ParseException {
        int unit;
        char c;
        int lastDigitIdx;
        for (lastDigitIdx = value.length() - 1; lastDigitIdx >= 0 && ((c = value.charAt(lastDigitIdx)) < '0' || c > '9'); --lastDigitIdx) {
        }
        int n = Integer.parseInt(value.substring(0, lastDigitIdx + 1).trim());
        String unitStr = value.substring(lastDigitIdx + 1).trim().toUpperCase();
        if (unitStr.length() == 0 || unitStr.equals("B")) {
            unit = 1;
        } else if (unitStr.equals("K") || unitStr.equals("KB") || unitStr.equals("KIB")) {
            unit = 1024;
        } else if (unitStr.equals("M") || unitStr.equals("MB") || unitStr.equals("MIB")) {
            unit = 0x100000;
        } else {
            throw new ParseException("Unknown unit: " + unitStr, lastDigitIdx + 1);
        }
        long size = (long)n * (long)unit;
        if (size < 0L) {
            throw new IllegalArgumentException("Buffer size can't be negative");
        }
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Buffer size can't bigger than 2147483647");
        }
        return (int)size;
    }

    private <T extends InitParamValueEnum> T initParamValueToEnum(String initParamValue, T[] enumValues) {
        for (T enumValue : enumValues) {
            String enumInitParamValue = enumValue.getInitParamValue();
            if (!initParamValue.equals(enumInitParamValue) && (!enumInitParamValue.endsWith("}") || !initParamValue.startsWith(enumInitParamValue.substring(0, enumInitParamValue.indexOf("${"))))) continue;
            return enumValue;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.jQuote(initParamValue));
        sb.append(" is not a one of the enumeration values: ");
        boolean first = true;
        for (T value : enumValues) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append(StringUtil.jQuote(value.getInitParamValue()));
        }
        throw new IllegalArgumentException(sb.toString());
    }

    static {
        GregorianCalendar expiration = new GregorianCalendar();
        expiration.roll(1, -1);
        SimpleDateFormat httpDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        EXPIRATION_DATE = httpDate.format(expiration.getTime());
    }

    private static enum OverrideResponseLocale implements InitParamValueEnum
    {
        ALWAYS("always"),
        NEVER("never");

        private final String initParamValue;

        private OverrideResponseLocale(String initParamValue) {
            this.initParamValue = initParamValue;
        }

        @Override
        public String getInitParamValue() {
            return this.initParamValue;
        }
    }

    private static enum ResponseCharacterEncoding implements InitParamValueEnum
    {
        LEGACY("legacy"),
        FROM_TEMPLATE("fromTemplate"),
        DO_NOT_SET("doNotSet"),
        FORCE_CHARSET("force ${charsetName}");

        private final String initParamValue;

        private ResponseCharacterEncoding(String initParamValue) {
            this.initParamValue = initParamValue;
        }

        @Override
        public String getInitParamValue() {
            return this.initParamValue;
        }
    }

    private static enum OverrideResponseContentType implements InitParamValueEnum
    {
        ALWAYS("always"),
        NEVER("never"),
        WHEN_TEMPLATE_HAS_MIME_TYPE("whenTemplateHasMimeType");

        private final String initParamValue;

        private OverrideResponseContentType(String initParamValue) {
            this.initParamValue = initParamValue;
        }

        @Override
        public String getInitParamValue() {
            return this.initParamValue;
        }
    }

    private static interface InitParamValueEnum {
        public String getInitParamValue();
    }

    private static class ContentType {
        private final String httpHeaderValue;
        private final boolean containsCharset;

        public ContentType(String httpHeaderValue) {
            this(httpHeaderValue, ContentType.contentTypeContainsCharset(httpHeaderValue));
        }

        public ContentType(String httpHeaderValue, boolean containsCharset) {
            this.httpHeaderValue = httpHeaderValue;
            this.containsCharset = containsCharset;
        }

        private static boolean contentTypeContainsCharset(String contentType) {
            int charsetIdx = contentType.toLowerCase().indexOf("charset=");
            if (charsetIdx != -1) {
                char c = '\u0000';
                --charsetIdx;
                while (charsetIdx >= 0 && Character.isWhitespace(c = contentType.charAt(charsetIdx))) {
                    --charsetIdx;
                }
                if (charsetIdx == -1 || c == ';') {
                    return true;
                }
            }
            return false;
        }

        private String getMimeType() {
            int scIdx = this.httpHeaderValue.indexOf(59);
            return (scIdx == -1 ? this.httpHeaderValue : this.httpHeaderValue.substring(0, scIdx)).trim();
        }
    }

    private static class MalformedWebXmlException
    extends Exception {
        MalformedWebXmlException(String message) {
            super(message);
        }
    }

    private static class ConflictingInitParamsException
    extends Exception {
        ConflictingInitParamsException(String recommendedName, String otherName) {
            super("Conflicting servlet init-params: " + StringUtil.jQuote(recommendedName) + " and " + StringUtil.jQuote(otherName) + ". Only use " + StringUtil.jQuote(recommendedName) + ".");
        }
    }

    private static class InitParamValueException
    extends Exception {
        InitParamValueException(String initParamName, String initParamValue, Throwable casue) {
            super("Failed to set the " + StringUtil.jQuote(initParamName) + " servlet init-param to " + StringUtil.jQuote(initParamValue) + "; see cause exception.", casue);
        }

        public InitParamValueException(String initParamName, String initParamValue, String cause) {
            super("Failed to set the " + StringUtil.jQuote(initParamName) + " servlet init-param to " + StringUtil.jQuote(initParamValue) + ": " + cause);
        }
    }
}

