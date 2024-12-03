/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.struts2.config.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.config.entities.BeanConfig;
import org.apache.struts2.dispatcher.StaticContentLoader;

public class ConstantConfig {
    private Boolean devMode;
    private Boolean i18nReload;
    private String i18nEncoding;
    private Boolean configurationXmlReload;
    private List<String> actionExtension;
    private List<Pattern> actionExcludePattern;
    private Integer urlHttpPort;
    private Integer urlHttpsPort;
    private String urlIncludeParams;
    private BeanConfig urlRenderer;
    private BeanConfig objectFactory;
    private BeanConfig objectFactoryActionFactory;
    private BeanConfig objectFactoryResultFactory;
    private BeanConfig objectFactoryConverterFactory;
    private BeanConfig objectFactoryInterceptorFactory;
    private BeanConfig objectFactoryValidatorFactory;
    private BeanConfig objectFactoryUnknownHandlerFactory;
    private BeanConfig objectTypeDeterminer;
    private Locale locale;
    private Boolean dispatcherParametersWorkaround;
    private BeanConfig freemarkerManagerClassname;
    private String freemarkerTemplatesCacheUpdateDelay;
    private Boolean freemarkerBeanwrapperCache;
    private Integer freemarkerMruMaxStrongSize;
    private BeanConfig velocityManagerClassname;
    private String velocityConfigfile;
    private String velocityToolboxlocation;
    private List<String> velocityContexts;
    private String uiTemplateDir;
    private String uiTheme;
    private String uiThemeExpansionToken;
    private Long multipartMaxSize;
    private Long multipartMaxFiles;
    private Long multipartMaxFileSize;
    private Long multipartMaxStringLength;
    private String multipartSaveDir;
    private Integer multipartBufferSize;
    private BeanConfig multipartParser;
    private Boolean multipartEnabled;
    private Pattern multipartValidationRegex;
    private String objectFactorySpringAutoWire;
    private Boolean objectFactorySpringAutoWireAlwaysRespect;
    private Boolean objectFactorySpringUseClassCache;
    private Boolean objectFactorySpringEnableAopSupport;
    private Boolean xsltNocache;
    private List<String> customProperties;
    private List<String> customI18nResources;
    private BeanConfig mapperClass;
    private List<String> mapperPrefixMapping;
    private Boolean serveStatic;
    private Boolean serveStaticBrowserCache;
    private Boolean enableDynamicMethodInvocation;
    private Boolean enableSlashesInActionNames;
    private List<String> mapperComposite;
    private BeanConfig actionProxyFactory;
    private Boolean freemarkerWrapperAltMap;
    private BeanConfig xworkConverter;
    private Boolean mapperAlwaysSelectFullNamespace;
    private BeanConfig localeProviderFactory;
    private String mapperIdParameterName;
    private Boolean ognlAllowStaticFieldAccess;
    private BeanConfig actionValidatorManager;
    private BeanConfig valueStackFactory;
    private BeanConfig reflectionProvider;
    private BeanConfig reflectionContextFactory;
    private BeanConfig patternMatcher;
    private BeanConfig staticContentLoader;
    private BeanConfig unknownHandlerManager;
    private Boolean elThrowExceptionOnFailure;
    private Boolean ognlLogMissingProperties;
    private Boolean ognlEnableExpressionCache;
    private Boolean ognlEnableEvalExpression;
    private Boolean disableRequestAttributeValueStackLookup;
    private BeanConfig viewUrlHelper;
    private BeanConfig converterCollection;
    private BeanConfig converterArray;
    private BeanConfig converterDate;
    private BeanConfig converterNumber;
    private BeanConfig converterString;
    private Boolean handleException;
    private BeanConfig converterPropertiesProcessor;
    private BeanConfig converterFileProcessor;
    private BeanConfig converterAnnotationProcessor;
    private BeanConfig converterCreator;
    private BeanConfig ConverterHolder;
    private BeanConfig expressionParser;
    private Pattern allowedActionNames;
    private String defaultActionName;
    private Pattern allowedMethodNames;
    private String defaultMethodName;
    private Boolean mapperActionPrefixEnabled;
    private Boolean mapperActionPrefixCrossNamespaces;
    private String uiTemplateSuffix;
    private BeanConfig dispatcherErrorHandler;
    private Set<Class<?>> excludedClasses;
    private List<Pattern> excludedPackageNamePatterns;
    private Set<String> excludedPackageNames;
    private Set<Class<?>> excludedPackageExemptClasses;
    private Set<Class<?>> devModeExcludedClasses;
    private List<Pattern> devModeExcludedPackageNamePatterns;
    private Set<String> devModeExcludedPackageNames;
    private Set<Class<?>> devModeExcludedPackageExemptClasses;
    private BeanConfig excludedPatternsChecker;
    private BeanConfig acceptedPatternsChecker;
    private BeanConfig notExcludedAcceptedPatternsChecker;
    private Set<Pattern> overrideExcludedPatterns;
    private Set<Pattern> overrideAcceptedPatterns;
    private Set<Pattern> additionalExcludedPatterns;
    private Set<Pattern> additionalAcceptedPatterns;
    private BeanConfig contentTypeMatcher;
    private String strictMethodInvocationMethodRegex;
    private BeanConfig textProviderFactory;
    private BeanConfig localizedTextProvider;
    private Boolean disallowProxyMemberAccess;
    private Integer ognlAutoGrowthCollectionLimit;
    private String staticContentPath;
    private BeanConfig expressionCacheFactory;
    private BeanConfig beaninfoCacheFactory;

    protected String beanConfToString(BeanConfig beanConf) {
        return beanConf == null ? null : beanConf.getName();
    }

    private String classesToString(Set<Class<?>> classes) {
        ArrayList<String> list = null;
        if (classes != null && !classes.isEmpty()) {
            list = new ArrayList<String>();
            for (Class<?> c : classes) {
                list.add(c.getName());
            }
        }
        return StringUtils.join(list, (char)',');
    }

    public Map<String, String> getAllAsStringsMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("struts.devMode", Objects.toString(this.devMode, null));
        map.put("struts.i18n.reload", Objects.toString(this.i18nReload, null));
        map.put("struts.i18n.encoding", this.i18nEncoding);
        map.put("struts.configuration.xml.reload", Objects.toString(this.configurationXmlReload, null));
        map.put("struts.action.extension", StringUtils.join(this.actionExtension, (char)','));
        map.put("struts.action.excludePattern", StringUtils.join(this.actionExcludePattern, (char)','));
        map.put("struts.url.http.port", Objects.toString(this.urlHttpPort, null));
        map.put("struts.url.https.port", Objects.toString(this.urlHttpsPort, null));
        map.put("struts.url.includeParams", this.urlIncludeParams);
        map.put("struts.urlRenderer", this.beanConfToString(this.urlRenderer));
        map.put("struts.objectFactory", this.beanConfToString(this.objectFactory));
        map.put("struts.objectFactory.actionFactory", this.beanConfToString(this.objectFactoryActionFactory));
        map.put("struts.objectFactory.resultFactory", this.beanConfToString(this.objectFactoryResultFactory));
        map.put("struts.objectFactory.converterFactory", this.beanConfToString(this.objectFactoryConverterFactory));
        map.put("struts.objectFactory.interceptorFactory", this.beanConfToString(this.objectFactoryInterceptorFactory));
        map.put("struts.objectFactory.validatorFactory", this.beanConfToString(this.objectFactoryValidatorFactory));
        map.put("struts.objectFactory.unknownHandlerFactory", this.beanConfToString(this.objectFactoryUnknownHandlerFactory));
        map.put("struts.objectTypeDeterminer", this.beanConfToString(this.objectTypeDeterminer));
        map.put("struts.locale", this.locale == null ? null : this.locale.getLanguage());
        map.put("struts.dispatcher.parametersWorkaround", Objects.toString(this.dispatcherParametersWorkaround, null));
        map.put("struts.freemarker.manager.classname", this.beanConfToString(this.freemarkerManagerClassname));
        map.put("struts.freemarker.templatesCache.updateDelay", this.freemarkerTemplatesCacheUpdateDelay);
        map.put("struts.freemarker.beanwrapperCache", Objects.toString(this.freemarkerBeanwrapperCache, null));
        map.put("struts.freemarker.mru.max.strong.size", Objects.toString(this.freemarkerMruMaxStrongSize, null));
        map.put("struts.velocity.configfile", this.velocityConfigfile);
        map.put("struts.velocity.toolboxlocation", this.velocityToolboxlocation);
        map.put("struts.velocity.contexts", StringUtils.join(this.velocityContexts, (char)','));
        map.put("struts.ui.templateDir", this.uiTemplateDir);
        map.put("struts.ui.theme", this.uiTheme);
        map.put("struts.ui.theme.expansion.token", this.uiThemeExpansionToken);
        map.put("struts.multipart.maxSize", Objects.toString(this.multipartMaxSize, null));
        map.put("struts.multipart.maxFiles", Objects.toString(this.multipartMaxFiles, null));
        map.put("struts.multipart.maxFileSize", Objects.toString(this.multipartMaxFileSize, null));
        map.put("struts.multipart.maxStringLength", Objects.toString(this.multipartMaxStringLength, null));
        map.put("struts.multipart.saveDir", this.multipartSaveDir);
        map.put("struts.multipart.bufferSize", Objects.toString(this.multipartBufferSize, null));
        map.put("struts.multipart.parser", this.beanConfToString(this.multipartParser));
        map.put("struts.multipart.enabled", Objects.toString(this.multipartEnabled, null));
        map.put("struts.multipart.validationRegex", Objects.toString(this.multipartValidationRegex, null));
        map.put("struts.objectFactory.spring.autoWire", this.objectFactorySpringAutoWire);
        map.put("struts.objectFactory.spring.autoWire.alwaysRespect", Objects.toString(this.objectFactorySpringAutoWireAlwaysRespect, null));
        map.put("struts.objectFactory.spring.useClassCache", Objects.toString(this.objectFactorySpringUseClassCache, null));
        map.put("struts.objectFactory.spring.enableAopSupport", Objects.toString(this.objectFactorySpringEnableAopSupport, null));
        map.put("struts.custom.properties", StringUtils.join(this.customProperties, (char)','));
        map.put("struts.custom.i18n.resources", StringUtils.join(this.customI18nResources, (char)','));
        map.put("struts.mapper.class", this.beanConfToString(this.mapperClass));
        map.put("struts.mapper.prefixMapping", StringUtils.join(this.mapperPrefixMapping, (char)','));
        map.put("struts.serve.static", Objects.toString(this.serveStatic, null));
        map.put("struts.serve.static.browserCache", Objects.toString(this.serveStaticBrowserCache, null));
        map.put("struts.enable.DynamicMethodInvocation", Objects.toString(this.enableDynamicMethodInvocation, null));
        map.put("struts.enable.SlashesInActionNames", Objects.toString(this.enableSlashesInActionNames, null));
        map.put("struts.mapper.composite", StringUtils.join(this.mapperComposite, (char)','));
        map.put("struts.actionProxyFactory", this.beanConfToString(this.actionProxyFactory));
        map.put("struts.freemarker.wrapper.altMap", Objects.toString(this.freemarkerWrapperAltMap, null));
        map.put("struts.xworkConverter", this.beanConfToString(this.xworkConverter));
        map.put("struts.mapper.alwaysSelectFullNamespace", Objects.toString(this.mapperAlwaysSelectFullNamespace, null));
        map.put("struts.localeProviderFactory", this.beanConfToString(this.localeProviderFactory));
        map.put("struts.mapper.idParameterName", this.mapperIdParameterName);
        map.put("struts.ognl.allowStaticFieldAccess", Objects.toString(this.ognlAllowStaticFieldAccess, null));
        map.put("struts.actionValidatorManager", this.beanConfToString(this.actionValidatorManager));
        map.put("struts.valueStackFactory", this.beanConfToString(this.valueStackFactory));
        map.put("struts.reflectionProvider", this.beanConfToString(this.reflectionProvider));
        map.put("struts.reflectionContextFactory", this.beanConfToString(this.reflectionContextFactory));
        map.put("struts.patternMatcher", this.beanConfToString(this.patternMatcher));
        map.put("struts.staticContentLoader", this.beanConfToString(this.staticContentLoader));
        map.put("struts.unknownHandlerManager", this.beanConfToString(this.unknownHandlerManager));
        map.put("struts.el.throwExceptionOnFailure", Objects.toString(this.elThrowExceptionOnFailure, null));
        map.put("struts.ognl.logMissingProperties", Objects.toString(this.ognlLogMissingProperties, null));
        map.put("struts.ognl.enableExpressionCache", Objects.toString(this.ognlEnableExpressionCache, null));
        map.put("struts.ognl.enableEvalExpression", Objects.toString(this.ognlEnableEvalExpression, null));
        map.put("struts.disableRequestAttributeValueStackLookup", Objects.toString(this.disableRequestAttributeValueStackLookup, null));
        map.put("struts.view.urlHelper", this.beanConfToString(this.viewUrlHelper));
        map.put("struts.converter.collection", this.beanConfToString(this.converterCollection));
        map.put("struts.converter.array", this.beanConfToString(this.converterArray));
        map.put("struts.converter.date", this.beanConfToString(this.converterDate));
        map.put("struts.converter.number", this.beanConfToString(this.converterNumber));
        map.put("struts.converter.string", this.beanConfToString(this.converterString));
        map.put("struts.handle.exception", Objects.toString(this.handleException, null));
        map.put("struts.converter.properties.processor", this.beanConfToString(this.converterPropertiesProcessor));
        map.put("struts.converter.file.processor", this.beanConfToString(this.converterFileProcessor));
        map.put("struts.converter.annotation.processor", this.beanConfToString(this.converterAnnotationProcessor));
        map.put("struts.converter.creator", this.beanConfToString(this.converterCreator));
        map.put("struts.converter.holder", this.beanConfToString(this.ConverterHolder));
        map.put("struts.expression.parser", this.beanConfToString(this.expressionParser));
        map.put("struts.allowed.action.names", Objects.toString(this.allowedActionNames, null));
        map.put("struts.default.action.name", this.defaultActionName);
        map.put("struts.allowed.method.names", Objects.toString(this.allowedMethodNames, null));
        map.put("struts.default.method.name", this.defaultMethodName);
        map.put("struts.mapper.action.prefix.enabled", Objects.toString(this.mapperActionPrefixEnabled, null));
        map.put("struts.ui.templateSuffix", this.uiTemplateSuffix);
        map.put("struts.dispatcher.errorHandler", this.beanConfToString(this.dispatcherErrorHandler));
        map.put("struts.excludedClasses", this.classesToString(this.excludedClasses));
        map.put("struts.excludedPackageNamePatterns", StringUtils.join(this.excludedPackageNamePatterns, (char)','));
        map.put("struts.excludedPackageNames", StringUtils.join(this.excludedPackageNames, (char)','));
        map.put("struts.excludedPackageExemptClasses", this.classesToString(this.excludedPackageExemptClasses));
        map.put("struts.devMode.excludedClasses", this.classesToString(this.devModeExcludedClasses));
        map.put("struts.devMode.excludedPackageNamePatterns", StringUtils.join(this.devModeExcludedPackageNamePatterns, (char)','));
        map.put("struts.devMode.excludedPackageNames", StringUtils.join(this.devModeExcludedPackageNames, (char)','));
        map.put("struts.devMode.excludedPackageExemptClasses", this.classesToString(this.devModeExcludedPackageExemptClasses));
        map.put("struts.excludedPatterns.checker", this.beanConfToString(this.excludedPatternsChecker));
        map.put("struts.acceptedPatterns.checker", this.beanConfToString(this.acceptedPatternsChecker));
        map.put("struts.notExcludedAcceptedPatterns.checker", this.beanConfToString(this.notExcludedAcceptedPatternsChecker));
        map.put("struts.override.excludedPatterns", StringUtils.join(this.overrideExcludedPatterns, (char)','));
        map.put("struts.override.acceptedPatterns", StringUtils.join(this.overrideAcceptedPatterns, (char)','));
        map.put("struts.additional.excludedPatterns", StringUtils.join(this.additionalExcludedPatterns, (char)','));
        map.put("struts.additional.acceptedPatterns", StringUtils.join(this.additionalAcceptedPatterns, (char)','));
        map.put("struts.contentTypeMatcher", this.beanConfToString(this.contentTypeMatcher));
        map.put("struts.strictMethodInvocation.methodRegex", this.strictMethodInvocationMethodRegex);
        map.put("struts.textProviderFactory", this.beanConfToString(this.textProviderFactory));
        map.put("struts.localizedTextProvider", this.beanConfToString(this.localizedTextProvider));
        map.put("struts.disallowProxyMemberAccess", Objects.toString(this.disallowProxyMemberAccess, null));
        map.put("struts.ognl.autoGrowthCollectionLimit", Objects.toString(this.ognlAutoGrowthCollectionLimit, null));
        map.put("struts.ui.staticContentPath", Objects.toString(this.staticContentPath, "/static"));
        map.put("struts.ognl.expressionCacheFactory", this.beanConfToString(this.expressionCacheFactory));
        map.put("struts.ognl.beanInfoCacheFactory", this.beanConfToString(this.beaninfoCacheFactory));
        return map;
    }

    public Boolean getDevMode() {
        return this.devMode;
    }

    public void setDevMode(Boolean devMode) {
        this.devMode = devMode;
    }

    public Boolean getI18nReload() {
        return this.i18nReload;
    }

    public void setI18nReload(Boolean i18nReload) {
        this.i18nReload = i18nReload;
    }

    public String getI18nEncoding() {
        return this.i18nEncoding;
    }

    public void setI18nEncoding(String i18nEncoding) {
        this.i18nEncoding = i18nEncoding;
    }

    public Boolean getConfigurationXmlReload() {
        return this.configurationXmlReload;
    }

    public void setConfigurationXmlReload(Boolean configurationXmlReload) {
        this.configurationXmlReload = configurationXmlReload;
    }

    public List<String> getActionExtension() {
        return this.actionExtension;
    }

    public void setActionExtension(List<String> actionExtension) {
        this.actionExtension = actionExtension;
    }

    public List<Pattern> getActionExcludePattern() {
        return this.actionExcludePattern;
    }

    public void setActionExcludePattern(List<Pattern> actionExcludePattern) {
        this.actionExcludePattern = actionExcludePattern;
    }

    public Integer getUrlHttpPort() {
        return this.urlHttpPort;
    }

    public void setUrlHttpPort(Integer urlHttpPort) {
        this.urlHttpPort = urlHttpPort;
    }

    public Integer getUrlHttpsPort() {
        return this.urlHttpsPort;
    }

    public void setUrlHttpsPort(Integer urlHttpsPort) {
        this.urlHttpsPort = urlHttpsPort;
    }

    public String getUrlIncludeParams() {
        return this.urlIncludeParams;
    }

    public void setUrlIncludeParams(String urlIncludeParams) {
        this.urlIncludeParams = urlIncludeParams;
    }

    public BeanConfig getUrlRenderer() {
        return this.urlRenderer;
    }

    public void setUrlRenderer(BeanConfig urlRenderer) {
        this.urlRenderer = urlRenderer;
    }

    public void setUrlRenderer(Class<?> clazz) {
        this.urlRenderer = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactory() {
        return this.objectFactory;
    }

    public void setObjectFactory(BeanConfig objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setObjectFactory(Class<?> clazz) {
        this.objectFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryActionFactory() {
        return this.objectFactoryActionFactory;
    }

    public void setObjectFactoryActionFactory(BeanConfig objectFactoryActionFactory) {
        this.objectFactoryActionFactory = objectFactoryActionFactory;
    }

    public void setObjectFactoryActionFactory(Class<?> clazz) {
        this.objectFactoryActionFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryResultFactory() {
        return this.objectFactoryResultFactory;
    }

    public void setObjectFactoryResultFactory(BeanConfig objectFactoryResultFactory) {
        this.objectFactoryResultFactory = objectFactoryResultFactory;
    }

    public void setObjectFactoryResultFactory(Class<?> clazz) {
        this.objectFactoryResultFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryConverterFactory() {
        return this.objectFactoryConverterFactory;
    }

    public void setObjectFactoryConverterFactory(BeanConfig objectFactoryConverterFactory) {
        this.objectFactoryConverterFactory = objectFactoryConverterFactory;
    }

    public void setObjectFactoryConverterFactory(Class<?> clazz) {
        this.objectFactoryConverterFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryInterceptorFactory() {
        return this.objectFactoryInterceptorFactory;
    }

    public void setObjectFactoryInterceptorFactory(BeanConfig objectFactoryInterceptorFactory) {
        this.objectFactoryInterceptorFactory = objectFactoryInterceptorFactory;
    }

    public void setObjectFactoryInterceptorFactory(Class<?> clazz) {
        this.objectFactoryInterceptorFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryValidatorFactory() {
        return this.objectFactoryValidatorFactory;
    }

    public void setObjectFactoryValidatorFactory(BeanConfig objectFactoryValidatorFactory) {
        this.objectFactoryValidatorFactory = objectFactoryValidatorFactory;
    }

    public void setObjectFactoryValidatorFactory(Class<?> clazz) {
        this.objectFactoryValidatorFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectFactoryUnknownHandlerFactory() {
        return this.objectFactoryUnknownHandlerFactory;
    }

    public void setObjectFactoryUnknownHandlerFactory(BeanConfig objectFactoryUnknownHandlerFactory) {
        this.objectFactoryUnknownHandlerFactory = objectFactoryUnknownHandlerFactory;
    }

    public void setObjectFactoryUnknownHandlerFactory(Class<?> clazz) {
        this.objectFactoryUnknownHandlerFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getObjectTypeDeterminer() {
        return this.objectTypeDeterminer;
    }

    public void setObjectTypeDeterminer(BeanConfig objectTypeDeterminer) {
        this.objectTypeDeterminer = objectTypeDeterminer;
    }

    public void setObjectTypeDeterminer(Class<?> clazz) {
        this.objectTypeDeterminer = new BeanConfig(clazz, clazz.getName());
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Boolean getDispatcherParametersWorkaround() {
        return this.dispatcherParametersWorkaround;
    }

    public void setDispatcherParametersWorkaround(Boolean dispatcherParametersWorkaround) {
        this.dispatcherParametersWorkaround = dispatcherParametersWorkaround;
    }

    public BeanConfig getFreemarkerManagerClassname() {
        return this.freemarkerManagerClassname;
    }

    public void setFreemarkerManagerClassname(BeanConfig freemarkerManagerClassname) {
        this.freemarkerManagerClassname = freemarkerManagerClassname;
    }

    public void setFreemarkerManagerClassname(Class<?> clazz) {
        this.freemarkerManagerClassname = new BeanConfig(clazz, clazz.getName());
    }

    public String getFreemarkerTemplatesCacheUpdateDelay() {
        return this.freemarkerTemplatesCacheUpdateDelay;
    }

    public void setFreemarkerTemplatesCacheUpdateDelay(String freemarkerTemplatesCacheUpdateDelay) {
        this.freemarkerTemplatesCacheUpdateDelay = freemarkerTemplatesCacheUpdateDelay;
    }

    public Boolean getFreemarkerBeanwrapperCache() {
        return this.freemarkerBeanwrapperCache;
    }

    public void setFreemarkerBeanwrapperCache(Boolean freemarkerBeanwrapperCache) {
        this.freemarkerBeanwrapperCache = freemarkerBeanwrapperCache;
    }

    public Integer getFreemarkerMruMaxStrongSize() {
        return this.freemarkerMruMaxStrongSize;
    }

    public void setFreemarkerMruMaxStrongSize(Integer freemarkerMruMaxStrongSize) {
        this.freemarkerMruMaxStrongSize = freemarkerMruMaxStrongSize;
    }

    public BeanConfig getVelocityManagerClassname() {
        return this.velocityManagerClassname;
    }

    public void setVelocityManagerClassname(BeanConfig velocityManagerClassname) {
        this.velocityManagerClassname = velocityManagerClassname;
    }

    public void setVelocityManagerClassname(Class<?> clazz) {
        this.velocityManagerClassname = new BeanConfig(clazz, clazz.getName());
    }

    public String getVelocityConfigfile() {
        return this.velocityConfigfile;
    }

    public void setVelocityConfigfile(String velocityConfigfile) {
        this.velocityConfigfile = velocityConfigfile;
    }

    public String getVelocityToolboxlocation() {
        return this.velocityToolboxlocation;
    }

    public void setVelocityToolboxlocation(String velocityToolboxlocation) {
        this.velocityToolboxlocation = velocityToolboxlocation;
    }

    public List<String> getVelocityContexts() {
        return this.velocityContexts;
    }

    public void setVelocityContexts(List<String> velocityContexts) {
        this.velocityContexts = velocityContexts;
    }

    public String getUiTemplateDir() {
        return this.uiTemplateDir;
    }

    public void setUiTemplateDir(String uiTemplateDir) {
        this.uiTemplateDir = uiTemplateDir;
    }

    public String getUiTheme() {
        return this.uiTheme;
    }

    public void setUiTheme(String uiTheme) {
        this.uiTheme = uiTheme;
    }

    public String getUiThemeExpansionToken() {
        return this.uiThemeExpansionToken;
    }

    public void setUiThemeExpansionToken(String uiThemeExpansionToken) {
        this.uiThemeExpansionToken = uiThemeExpansionToken;
    }

    public Long getMultipartMaxSize() {
        return this.multipartMaxSize;
    }

    public void setMultipartMaxSize(Long multipartMaxSize) {
        this.multipartMaxSize = multipartMaxSize;
    }

    public Long getMultipartMaxFiles() {
        return this.multipartMaxFiles;
    }

    public void setMultipartMaxFiles(Long multipartMaxFiles) {
        this.multipartMaxFiles = multipartMaxFiles;
    }

    public Long getMultipartMaxFileSize() {
        return this.multipartMaxFileSize;
    }

    public void setMultipartMaxFileSize(Long multipartMaxFileSize) {
        this.multipartMaxFileSize = multipartMaxFileSize;
    }

    public Long getMultipartMaxStringLength() {
        return this.multipartMaxStringLength;
    }

    public void setMultipartMaxStringLength(Long multipartMaxStringLength) {
        this.multipartMaxStringLength = multipartMaxStringLength;
    }

    public String getMultipartSaveDir() {
        return this.multipartSaveDir;
    }

    public void setMultipartSaveDir(String multipartSaveDir) {
        this.multipartSaveDir = multipartSaveDir;
    }

    public Integer getMultipartBufferSize() {
        return this.multipartBufferSize;
    }

    public void setMultipartBufferSize(Integer multipartBufferSize) {
        this.multipartBufferSize = multipartBufferSize;
    }

    public BeanConfig getMultipartParser() {
        return this.multipartParser;
    }

    public void setMultipartParser(BeanConfig multipartParser) {
        this.multipartParser = multipartParser;
    }

    public void setMultipartParser(Class<?> clazz) {
        this.multipartParser = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getMultipartEnabled() {
        return this.multipartEnabled;
    }

    public void setMultipartEnabled(Boolean multipartEnabled) {
        this.multipartEnabled = multipartEnabled;
    }

    public Pattern getMultipartValidationRegex() {
        return this.multipartValidationRegex;
    }

    public void setMultipartValidationRegex(Pattern multipartValidationRegex) {
        this.multipartValidationRegex = multipartValidationRegex;
    }

    public String getObjectFactorySpringAutoWire() {
        return this.objectFactorySpringAutoWire;
    }

    public void setObjectFactorySpringAutoWire(String objectFactorySpringAutoWire) {
        this.objectFactorySpringAutoWire = objectFactorySpringAutoWire;
    }

    public Boolean getObjectFactorySpringAutoWireAlwaysRespect() {
        return this.objectFactorySpringAutoWireAlwaysRespect;
    }

    public void setObjectFactorySpringAutoWireAlwaysRespect(Boolean objectFactorySpringAutoWireAlwaysRespect) {
        this.objectFactorySpringAutoWireAlwaysRespect = objectFactorySpringAutoWireAlwaysRespect;
    }

    public Boolean getObjectFactorySpringUseClassCache() {
        return this.objectFactorySpringUseClassCache;
    }

    public void setObjectFactorySpringUseClassCache(Boolean objectFactorySpringUseClassCache) {
        this.objectFactorySpringUseClassCache = objectFactorySpringUseClassCache;
    }

    public Boolean getObjectFactorySpringEnableAopSupport() {
        return this.objectFactorySpringEnableAopSupport;
    }

    public void setObjectFactorySpringEnableAopSupport(Boolean objectFactorySpringEnableAopSupport) {
        this.objectFactorySpringEnableAopSupport = objectFactorySpringEnableAopSupport;
    }

    public Boolean getXsltNocache() {
        return this.xsltNocache;
    }

    public void setXsltNocache(Boolean xsltNocache) {
        this.xsltNocache = xsltNocache;
    }

    public List<String> getCustomProperties() {
        return this.customProperties;
    }

    public void setCustomProperties(List<String> customProperties) {
        this.customProperties = customProperties;
    }

    public List<String> getCustomI18nResources() {
        return this.customI18nResources;
    }

    public void setCustomI18nResources(List<String> customI18nResources) {
        this.customI18nResources = customI18nResources;
    }

    public BeanConfig getMapperClass() {
        return this.mapperClass;
    }

    public void setMapperClass(BeanConfig mapperClass) {
        this.mapperClass = mapperClass;
    }

    public void setMapperClass(Class<?> clazz) {
        this.mapperClass = new BeanConfig(clazz, clazz.getName());
    }

    public List<String> getMapperPrefixMapping() {
        return this.mapperPrefixMapping;
    }

    public void setMapperPrefixMapping(List<String> mapperPrefixMapping) {
        this.mapperPrefixMapping = mapperPrefixMapping;
    }

    public Boolean getServeStatic() {
        return this.serveStatic;
    }

    public void setServeStatic(Boolean serveStatic) {
        this.serveStatic = serveStatic;
    }

    public Boolean getServeStaticBrowserCache() {
        return this.serveStaticBrowserCache;
    }

    public void setServeStaticBrowserCache(Boolean serveStaticBrowserCache) {
        this.serveStaticBrowserCache = serveStaticBrowserCache;
    }

    public Boolean getEnableDynamicMethodInvocation() {
        return this.enableDynamicMethodInvocation;
    }

    public void setEnableDynamicMethodInvocation(Boolean enableDynamicMethodInvocation) {
        this.enableDynamicMethodInvocation = enableDynamicMethodInvocation;
    }

    public Boolean getEnableSlashesInActionNames() {
        return this.enableSlashesInActionNames;
    }

    public void setEnableSlashesInActionNames(Boolean enableSlashesInActionNames) {
        this.enableSlashesInActionNames = enableSlashesInActionNames;
    }

    public List<String> getMapperComposite() {
        return this.mapperComposite;
    }

    public void setMapperComposite(List<String> mapperComposite) {
        this.mapperComposite = mapperComposite;
    }

    public BeanConfig getActionProxyFactory() {
        return this.actionProxyFactory;
    }

    public void setActionProxyFactory(BeanConfig actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }

    public void setActionProxyFactory(Class<?> clazz) {
        this.actionProxyFactory = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getFreemarkerWrapperAltMap() {
        return this.freemarkerWrapperAltMap;
    }

    public void setFreemarkerWrapperAltMap(Boolean freemarkerWrapperAltMap) {
        this.freemarkerWrapperAltMap = freemarkerWrapperAltMap;
    }

    public BeanConfig getXworkConverter() {
        return this.xworkConverter;
    }

    public void setXworkConverter(BeanConfig xworkConverter) {
        this.xworkConverter = xworkConverter;
    }

    public void setXworkConverter(Class<?> clazz) {
        this.xworkConverter = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getMapperAlwaysSelectFullNamespace() {
        return this.mapperAlwaysSelectFullNamespace;
    }

    public void setMapperAlwaysSelectFullNamespace(Boolean mapperAlwaysSelectFullNamespace) {
        this.mapperAlwaysSelectFullNamespace = mapperAlwaysSelectFullNamespace;
    }

    public BeanConfig getLocaleProviderFactory() {
        return this.localeProviderFactory;
    }

    public void setLocaleProviderFactory(BeanConfig localeProviderFactory) {
        this.localeProviderFactory = localeProviderFactory;
    }

    public void setLocaleProviderFactory(Class<?> clazz) {
        this.localeProviderFactory = new BeanConfig(clazz, clazz.getName());
    }

    public String getMapperIdParameterName() {
        return this.mapperIdParameterName;
    }

    public void setMapperIdParameterName(String mapperIdParameterName) {
        this.mapperIdParameterName = mapperIdParameterName;
    }

    public Boolean getOgnlAllowStaticFieldAccess() {
        return this.ognlAllowStaticFieldAccess;
    }

    public void setOgnlAllowStaticFieldAccess(Boolean ognlAllowStaticFieldAccess) {
        this.ognlAllowStaticFieldAccess = ognlAllowStaticFieldAccess;
    }

    public BeanConfig getActionValidatorManager() {
        return this.actionValidatorManager;
    }

    public void setActionValidatorManager(BeanConfig actionValidatorManager) {
        this.actionValidatorManager = actionValidatorManager;
    }

    public void setActionValidatorManager(Class<?> clazz) {
        this.actionValidatorManager = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getValueStackFactory() {
        return this.valueStackFactory;
    }

    public void setValueStackFactory(BeanConfig valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    public void setValueStackFactory(Class<?> clazz) {
        this.valueStackFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getReflectionProvider() {
        return this.reflectionProvider;
    }

    public void setReflectionProvider(BeanConfig reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    public void setReflectionProvider(Class<?> clazz) {
        this.reflectionProvider = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getReflectionContextFactory() {
        return this.reflectionContextFactory;
    }

    public void setReflectionContextFactory(BeanConfig reflectionContextFactory) {
        this.reflectionContextFactory = reflectionContextFactory;
    }

    public void setReflectionContextFactory(Class<?> clazz) {
        this.reflectionContextFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getPatternMatcher() {
        return this.patternMatcher;
    }

    public void setPatternMatcher(BeanConfig patternMatcher) {
        this.patternMatcher = patternMatcher;
    }

    public void setPatternMatcher(Class<?> clazz) {
        this.patternMatcher = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getStaticContentLoader() {
        return this.staticContentLoader;
    }

    public void setStaticContentLoader(BeanConfig staticContentLoader) {
        this.staticContentLoader = staticContentLoader;
    }

    public void setStaticContentLoader(Class<?> clazz) {
        this.staticContentLoader = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getUnknownHandlerManager() {
        return this.unknownHandlerManager;
    }

    public void setUnknownHandlerManager(BeanConfig unknownHandlerManager) {
        this.unknownHandlerManager = unknownHandlerManager;
    }

    public void setUnknownHandlerManager(Class<?> clazz) {
        this.unknownHandlerManager = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getElThrowExceptionOnFailure() {
        return this.elThrowExceptionOnFailure;
    }

    public void setElThrowExceptionOnFailure(Boolean elThrowExceptionOnFailure) {
        this.elThrowExceptionOnFailure = elThrowExceptionOnFailure;
    }

    public Boolean getOgnlLogMissingProperties() {
        return this.ognlLogMissingProperties;
    }

    public void setOgnlLogMissingProperties(Boolean ognlLogMissingProperties) {
        this.ognlLogMissingProperties = ognlLogMissingProperties;
    }

    public Boolean getOgnlEnableExpressionCache() {
        return this.ognlEnableExpressionCache;
    }

    public void setOgnlEnableExpressionCache(Boolean ognlEnableExpressionCache) {
        this.ognlEnableExpressionCache = ognlEnableExpressionCache;
    }

    public Boolean getOgnlEnableEvalExpression() {
        return this.ognlEnableEvalExpression;
    }

    public void setOgnlEnableEvalExpression(Boolean ognlEnableEvalExpression) {
        this.ognlEnableEvalExpression = ognlEnableEvalExpression;
    }

    public Boolean getDisableRequestAttributeValueStackLookup() {
        return this.disableRequestAttributeValueStackLookup;
    }

    public void setDisableRequestAttributeValueStackLookup(Boolean disableRequestAttributeValueStackLookup) {
        this.disableRequestAttributeValueStackLookup = disableRequestAttributeValueStackLookup;
    }

    public BeanConfig getViewUrlHelper() {
        return this.viewUrlHelper;
    }

    public void setViewUrlHelper(BeanConfig viewUrlHelper) {
        this.viewUrlHelper = viewUrlHelper;
    }

    public void setViewUrlHelper(Class<?> clazz) {
        this.viewUrlHelper = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterCollection() {
        return this.converterCollection;
    }

    public void setConverterCollection(BeanConfig converterCollection) {
        this.converterCollection = converterCollection;
    }

    public void setConverterCollection(Class<?> clazz) {
        this.converterCollection = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterArray() {
        return this.converterArray;
    }

    public void setConverterArray(BeanConfig converterArray) {
        this.converterArray = converterArray;
    }

    public void setConverterArray(Class<?> clazz) {
        this.converterArray = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterDate() {
        return this.converterDate;
    }

    public void setConverterDate(BeanConfig converterDate) {
        this.converterDate = converterDate;
    }

    public void setConverterDate(Class<?> clazz) {
        this.converterDate = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterNumber() {
        return this.converterNumber;
    }

    public void setConverterNumber(BeanConfig converterNumber) {
        this.converterNumber = converterNumber;
    }

    public void setConverterNumber(Class<?> clazz) {
        this.converterNumber = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterString() {
        return this.converterString;
    }

    public void setConverterString(BeanConfig converterString) {
        this.converterString = converterString;
    }

    public void setConverterString(Class<?> clazz) {
        this.converterString = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getHandleException() {
        return this.handleException;
    }

    public void setHandleException(Boolean handleException) {
        this.handleException = handleException;
    }

    public BeanConfig getConverterPropertiesProcessor() {
        return this.converterPropertiesProcessor;
    }

    public void setConverterPropertiesProcessor(BeanConfig converterPropertiesProcessor) {
        this.converterPropertiesProcessor = converterPropertiesProcessor;
    }

    public void setConverterPropertiesProcessor(Class<?> clazz) {
        this.converterPropertiesProcessor = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterFileProcessor() {
        return this.converterFileProcessor;
    }

    public void setConverterFileProcessor(BeanConfig converterFileProcessor) {
        this.converterFileProcessor = converterFileProcessor;
    }

    public void setConverterFileProcessor(Class<?> clazz) {
        this.converterFileProcessor = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterAnnotationProcessor() {
        return this.converterAnnotationProcessor;
    }

    public void setConverterAnnotationProcessor(BeanConfig converterAnnotationProcessor) {
        this.converterAnnotationProcessor = converterAnnotationProcessor;
    }

    public void setConverterAnnotationProcessor(Class<?> clazz) {
        this.converterAnnotationProcessor = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterCreator() {
        return this.converterCreator;
    }

    public void setConverterCreator(BeanConfig converterCreator) {
        this.converterCreator = converterCreator;
    }

    public void setConverterCreator(Class<?> clazz) {
        this.converterCreator = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getConverterHolder() {
        return this.ConverterHolder;
    }

    public void setConverterHolder(BeanConfig ConverterHolder) {
        this.ConverterHolder = ConverterHolder;
    }

    public void setConverterHolder(Class<?> clazz) {
        this.ConverterHolder = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getExpressionParser() {
        return this.expressionParser;
    }

    public void setExpressionParser(BeanConfig expressionParser) {
        this.expressionParser = expressionParser;
    }

    public void setExpressionParser(Class<?> clazz) {
        this.expressionParser = new BeanConfig(clazz, clazz.getName());
    }

    public Pattern getAllowedActionNames() {
        return this.allowedActionNames;
    }

    public void setAllowedActionNames(Pattern allowedActionNames) {
        this.allowedActionNames = allowedActionNames;
    }

    public String getDefaultActionName() {
        return this.defaultActionName;
    }

    public void setDefaultActionName(String defaultActionName) {
        this.defaultActionName = defaultActionName;
    }

    public Pattern getAllowedMethodNames() {
        return this.allowedMethodNames;
    }

    public void setAllowedMethodNames(Pattern allowedMethodNames) {
        this.allowedMethodNames = allowedMethodNames;
    }

    public String getDefaultMethodName() {
        return this.defaultMethodName;
    }

    public void setDefaultMethodName(String defaultMethodName) {
        this.defaultMethodName = defaultMethodName;
    }

    public Boolean getMapperActionPrefixEnabled() {
        return this.mapperActionPrefixEnabled;
    }

    public void setMapperActionPrefixEnabled(Boolean mapperActionPrefixEnabled) {
        this.mapperActionPrefixEnabled = mapperActionPrefixEnabled;
    }

    public Boolean getMapperActionPrefixCrossNamespaces() {
        return this.mapperActionPrefixCrossNamespaces;
    }

    public void setMapperActionPrefixCrossNamespaces(Boolean mapperActionPrefixCrossNamespaces) {
        this.mapperActionPrefixCrossNamespaces = mapperActionPrefixCrossNamespaces;
    }

    public String getUiTemplateSuffix() {
        return this.uiTemplateSuffix;
    }

    public void setUiTemplateSuffix(String uiTemplateSuffix) {
        this.uiTemplateSuffix = uiTemplateSuffix;
    }

    public BeanConfig getDispatcherErrorHandler() {
        return this.dispatcherErrorHandler;
    }

    public void setDispatcherErrorHandler(BeanConfig dispatcherErrorHandler) {
        this.dispatcherErrorHandler = dispatcherErrorHandler;
    }

    public void setDispatcherErrorHandler(Class<?> clazz) {
        this.dispatcherErrorHandler = new BeanConfig(clazz, clazz.getName());
    }

    public Set<Class<?>> getExcludedClasses() {
        return this.excludedClasses;
    }

    public void setExcludedClasses(Set<Class<?>> excludedClasses) {
        this.excludedClasses = excludedClasses;
    }

    public List<Pattern> getExcludedPackageNamePatterns() {
        return this.excludedPackageNamePatterns;
    }

    public void setExcludedPackageNamePatterns(List<Pattern> excludedPackageNamePatterns) {
        this.excludedPackageNamePatterns = excludedPackageNamePatterns;
    }

    public Set<String> getExcludedPackageNames() {
        return this.excludedPackageNames;
    }

    public void setExcludedPackageNames(Set<String> excludedPackageNames) {
        this.excludedPackageNames = excludedPackageNames;
    }

    public Set<Class<?>> getExcludedPackageExemptClasses() {
        return this.excludedPackageExemptClasses;
    }

    public void setExcludedPackageExemptClasses(Set<Class<?>> excludedPackageExemptClasses) {
        this.excludedPackageExemptClasses = excludedPackageExemptClasses;
    }

    public Set<Class<?>> getDevModeExcludedClasses() {
        return this.devModeExcludedClasses;
    }

    public void setDevModeExcludedClasses(Set<Class<?>> devModeExcludedClasses) {
        this.devModeExcludedClasses = devModeExcludedClasses;
    }

    public List<Pattern> getDevModeExcludedPackageNamePatterns() {
        return this.devModeExcludedPackageNamePatterns;
    }

    public void setDevModeExcludedPackageNamePatterns(List<Pattern> devModeExcludedPackageNamePatterns) {
        this.devModeExcludedPackageNamePatterns = devModeExcludedPackageNamePatterns;
    }

    public Set<String> getDevModeExcludedPackageNames() {
        return this.devModeExcludedPackageNames;
    }

    public void setDevModeExcludedPackageNames(Set<String> devModeExcludedPackageNames) {
        this.devModeExcludedPackageNames = devModeExcludedPackageNames;
    }

    public Set<Class<?>> getDevModeExcludedPackageExemptClasses() {
        return this.devModeExcludedPackageExemptClasses;
    }

    public void setDevModeExcludedPackageExemptClasses(Set<Class<?>> devModeExcludedPackageExemptClasses) {
        this.devModeExcludedPackageExemptClasses = devModeExcludedPackageExemptClasses;
    }

    public BeanConfig getExcludedPatternsChecker() {
        return this.excludedPatternsChecker;
    }

    public void setExcludedPatternsChecker(BeanConfig excludedPatternsChecker) {
        this.excludedPatternsChecker = excludedPatternsChecker;
    }

    public void setExcludedPatternsChecker(Class<?> clazz) {
        this.excludedPatternsChecker = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getAcceptedPatternsChecker() {
        return this.acceptedPatternsChecker;
    }

    public void setAcceptedPatternsChecker(BeanConfig acceptedPatternsChecker) {
        this.acceptedPatternsChecker = acceptedPatternsChecker;
    }

    public void setAcceptedPatternsChecker(Class<?> clazz) {
        this.acceptedPatternsChecker = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getNotExcludedAcceptedPatternsChecker() {
        return this.notExcludedAcceptedPatternsChecker;
    }

    public void setNotExcludedAcceptedPatternsChecker(BeanConfig notExcludedAcceptedPatternsChecker) {
        this.notExcludedAcceptedPatternsChecker = notExcludedAcceptedPatternsChecker;
    }

    public void setNotExcludedAcceptedPatternsChecker(Class<?> clazz) {
        this.notExcludedAcceptedPatternsChecker = new BeanConfig(clazz, clazz.getName());
    }

    public Set<Pattern> getOverrideExcludedPatterns() {
        return this.overrideExcludedPatterns;
    }

    public void setOverrideExcludedPatterns(Set<Pattern> overrideExcludedPatterns) {
        this.overrideExcludedPatterns = overrideExcludedPatterns;
    }

    public Set<Pattern> getOverrideAcceptedPatterns() {
        return this.overrideAcceptedPatterns;
    }

    public void setOverrideAcceptedPatterns(Set<Pattern> overrideAcceptedPatterns) {
        this.overrideAcceptedPatterns = overrideAcceptedPatterns;
    }

    public Set<Pattern> getAdditionalExcludedPatterns() {
        return this.additionalExcludedPatterns;
    }

    public void setAdditionalExcludedPatterns(Set<Pattern> additionalExcludedPatterns) {
        this.additionalExcludedPatterns = additionalExcludedPatterns;
    }

    public Set<Pattern> getAdditionalAcceptedPatterns() {
        return this.additionalAcceptedPatterns;
    }

    public void setAdditionalAcceptedPatterns(Set<Pattern> additionalAcceptedPatterns) {
        this.additionalAcceptedPatterns = additionalAcceptedPatterns;
    }

    public BeanConfig getContentTypeMatcher() {
        return this.contentTypeMatcher;
    }

    public void setContentTypeMatcher(BeanConfig contentTypeMatcher) {
        this.contentTypeMatcher = contentTypeMatcher;
    }

    public void setContentTypeMatcher(Class<?> clazz) {
        this.contentTypeMatcher = new BeanConfig(clazz, clazz.getName());
    }

    public String getStrictMethodInvocationMethodRegex() {
        return this.strictMethodInvocationMethodRegex;
    }

    public void setStrictMethodInvocationMethodRegex(String strictMethodInvocationMethodRegex) {
        this.strictMethodInvocationMethodRegex = strictMethodInvocationMethodRegex;
    }

    public BeanConfig getTextProviderFactory() {
        return this.textProviderFactory;
    }

    public void setTextProviderFactory(BeanConfig textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }

    public void setTextProviderFactory(Class<?> clazz) {
        this.textProviderFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getLocalizedTextProvider() {
        return this.localizedTextProvider;
    }

    public void setLocalizedTextProvider(BeanConfig localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    public void setLocalizedTextProvider(Class<?> clazz) {
        this.localizedTextProvider = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getDisallowProxyMemberAccess() {
        return this.disallowProxyMemberAccess;
    }

    public void setDisallowProxyMemberAccess(Boolean disallowProxyMemberAccess) {
        this.disallowProxyMemberAccess = disallowProxyMemberAccess;
    }

    public Integer getOgnlAutoGrowthCollectionLimit() {
        return this.ognlAutoGrowthCollectionLimit;
    }

    public void setOgnlAutoGrowthCollectionLimit(Integer ognlAutoGrowthCollectionLimit) {
        this.ognlAutoGrowthCollectionLimit = ognlAutoGrowthCollectionLimit;
    }

    public String getStaticContentPath() {
        return this.staticContentPath;
    }

    public void setStaticContentPath(String staticContentPath) {
        this.staticContentPath = StaticContentLoader.Validator.validateStaticContentPath(staticContentPath);
    }

    public BeanConfig getExpressionCacheFactory() {
        return this.expressionCacheFactory;
    }

    public void setExpressionCacheFactory(BeanConfig expressionCacheFactory) {
        this.expressionCacheFactory = expressionCacheFactory;
    }

    public void setExpressionCacheFactory(Class<?> clazz) {
        this.expressionCacheFactory = new BeanConfig(clazz, clazz.getName());
    }

    public BeanConfig getBeaninfoCacheFactory() {
        return this.beaninfoCacheFactory;
    }

    public void setBeaninfoCacheFactory(BeanConfig beaninfoCacheFactory) {
        this.beaninfoCacheFactory = beaninfoCacheFactory;
    }

    public void setBeaninfoCacheFactory(Class<?> clazz) {
        this.beaninfoCacheFactory = new BeanConfig(clazz, clazz.getName());
    }
}

