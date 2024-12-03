/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.MethodAccessor
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.DefaultLocaleProviderFactory;
import com.opensymphony.xwork2.DefaultTextProvider;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.StrutsTextProviderFactory;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ContainerProvider;
import com.opensymphony.xwork2.config.FileManagerFactoryProvider;
import com.opensymphony.xwork2.config.FileManagerProvider;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.config.impl.ActionConfigMatcher;
import com.opensymphony.xwork2.config.impl.LocatableConstantFactory;
import com.opensymphony.xwork2.config.impl.NamespaceMatch;
import com.opensymphony.xwork2.config.impl.NamespaceMatcher;
import com.opensymphony.xwork2.config.providers.EnvsValueSubstitutor;
import com.opensymphony.xwork2.config.providers.InterceptorBuilder;
import com.opensymphony.xwork2.config.providers.ValueSubstitutor;
import com.opensymphony.xwork2.conversion.ConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.ConversionFileProcessor;
import com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.conversion.impl.ArrayConverter;
import com.opensymphony.xwork2.conversion.impl.CollectionConverter;
import com.opensymphony.xwork2.conversion.impl.DateConverter;
import com.opensymphony.xwork2.conversion.impl.DefaultConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.impl.DefaultConversionFileProcessor;
import com.opensymphony.xwork2.conversion.impl.DefaultObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.NumberConverter;
import com.opensymphony.xwork2.conversion.impl.StringConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkBasicConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.factory.ActionFactory;
import com.opensymphony.xwork2.factory.ConverterFactory;
import com.opensymphony.xwork2.factory.DefaultActionFactory;
import com.opensymphony.xwork2.factory.DefaultInterceptorFactory;
import com.opensymphony.xwork2.factory.DefaultResultFactory;
import com.opensymphony.xwork2.factory.DefaultUnknownHandlerFactory;
import com.opensymphony.xwork2.factory.DefaultValidatorFactory;
import com.opensymphony.xwork2.factory.InterceptorFactory;
import com.opensymphony.xwork2.factory.ResultFactory;
import com.opensymphony.xwork2.factory.StrutsConverterFactory;
import com.opensymphony.xwork2.factory.UnknownHandlerFactory;
import com.opensymphony.xwork2.factory.ValidatorFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.ognl.BeanInfoCacheFactory;
import com.opensymphony.xwork2.ognl.DefaultOgnlBeanInfoCacheFactory;
import com.opensymphony.xwork2.ognl.DefaultOgnlExpressionCacheFactory;
import com.opensymphony.xwork2.ognl.ExpressionCacheFactory;
import com.opensymphony.xwork2.ognl.OgnlCacheFactory;
import com.opensymphony.xwork2.ognl.OgnlReflectionProvider;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.ognl.OgnlValueStackFactory;
import com.opensymphony.xwork2.ognl.SecurityMemberAccess;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.ognl.accessor.RootAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkMethodAccessor;
import com.opensymphony.xwork2.util.OgnlTextParser;
import com.opensymphony.xwork2.util.PatternMatcher;
import com.opensymphony.xwork2.util.StrutsLocalizedTextProvider;
import com.opensymphony.xwork2.util.TextParser;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import com.opensymphony.xwork2.util.fs.DefaultFileManagerFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import ognl.MethodAccessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.conversion.StrutsConversionPropertiesProcessor;
import org.apache.struts2.conversion.StrutsTypeConverterCreator;
import org.apache.struts2.conversion.StrutsTypeConverterHolder;
import org.apache.struts2.ognl.OgnlGuard;
import org.apache.struts2.ognl.ProviderAllowlist;
import org.apache.struts2.ognl.StrutsOgnlGuard;

public class DefaultConfiguration
implements Configuration {
    public static final Map<String, Object> BOOTSTRAP_CONSTANTS;
    protected static final Logger LOG;
    protected Map<String, PackageConfig> packageContexts = new LinkedHashMap<String, PackageConfig>();
    protected RuntimeConfiguration runtimeConfiguration;
    protected Container container;
    protected String defaultFrameworkBeanName;
    protected Set<String> loadedFileNames = new TreeSet<String>();
    protected List<UnknownHandlerConfig> unknownHandlerStack;
    ObjectFactory objectFactory;

    public DefaultConfiguration() {
        this("default");
    }

    public DefaultConfiguration(String defaultBeanName) {
        this.defaultFrameworkBeanName = defaultBeanName;
    }

    @Override
    public PackageConfig getPackageConfig(String name) {
        return this.packageContexts.get(name);
    }

    @Override
    public List<UnknownHandlerConfig> getUnknownHandlerStack() {
        return this.unknownHandlerStack;
    }

    @Override
    public void setUnknownHandlerStack(List<UnknownHandlerConfig> unknownHandlerStack) {
        this.unknownHandlerStack = unknownHandlerStack;
    }

    @Override
    public Set<String> getPackageConfigNames() {
        return this.packageContexts.keySet();
    }

    @Override
    public Map<String, PackageConfig> getPackageConfigs() {
        return this.packageContexts;
    }

    @Override
    public Set<String> getLoadedFileNames() {
        return this.loadedFileNames;
    }

    @Override
    public RuntimeConfiguration getRuntimeConfiguration() {
        return this.runtimeConfiguration;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void addPackageConfig(String name, PackageConfig packageContext) {
        PackageConfig check = this.packageContexts.get(name);
        if (check != null) {
            if (check.getLocation() != null && packageContext.getLocation() != null && check.getLocation().equals(packageContext.getLocation())) {
                LOG.debug("The package name '{}' is already been loaded by the same location and could be removed: {}", (Object)name, (Object)packageContext.getLocation());
            } else {
                throw new ConfigurationException("The package name '" + name + "' at location " + packageContext.getLocation() + " is already been used by another package at location " + check.getLocation(), (Object)packageContext);
            }
        }
        this.packageContexts.put(name, packageContext);
    }

    @Override
    public PackageConfig removePackageConfig(String packageName) {
        return this.packageContexts.remove(packageName);
    }

    @Override
    public void destroy() {
        this.packageContexts.clear();
        this.loadedFileNames.clear();
    }

    @Override
    public void rebuildRuntimeConfiguration() {
        this.runtimeConfiguration = this.buildRuntimeConfiguration();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized List<PackageProvider> reloadContainer(List<ContainerProvider> providers) throws ConfigurationException {
        this.packageContexts.clear();
        this.loadedFileNames.clear();
        ArrayList<PackageProvider> packageProviders = new ArrayList<PackageProvider>();
        ContainerProperties props = new ContainerProperties();
        ContainerBuilder builder = new ContainerBuilder();
        Container bootstrap = this.createBootstrapContainer(providers);
        for (ContainerProvider containerProvider : providers) {
            bootstrap.inject(containerProvider);
            containerProvider.init(this);
            containerProvider.register(builder, props);
        }
        props.setConstants(builder);
        builder.factory(Configuration.class, new Factory<Configuration>(){

            @Override
            public Configuration create(Context context) throws Exception {
                return DefaultConfiguration.this;
            }

            @Override
            public Class<? extends Configuration> type() {
                return DefaultConfiguration.this.getClass();
            }
        });
        ActionContext oldContext = ActionContext.getContext();
        try {
            this.setContext(bootstrap);
            this.container = builder.create(false);
            this.setContext(this.container);
            this.objectFactory = this.container.getInstance(ObjectFactory.class);
            for (ContainerProvider containerProvider : providers) {
                if (!(containerProvider instanceof PackageProvider)) continue;
                this.container.inject(containerProvider);
                ((PackageProvider)((Object)containerProvider)).loadPackages();
                packageProviders.add((PackageProvider)((Object)containerProvider));
            }
            Set<String> set = this.container.getInstanceNames(PackageProvider.class);
            for (String name : set) {
                PackageProvider provider = this.container.getInstance(PackageProvider.class, name);
                provider.init(this);
                provider.loadPackages();
                packageProviders.add(provider);
            }
            this.rebuildRuntimeConfiguration();
        }
        finally {
            if (oldContext == null) {
                ActionContext.clear();
            }
        }
        return packageProviders;
    }

    protected ActionContext setContext(Container cont) {
        ValueStack vs = cont.getInstance(ValueStackFactory.class).createValueStack();
        return ActionContext.of(vs.getContext()).bind();
    }

    protected Container createBootstrapContainer(List<ContainerProvider> providers) {
        ContainerBuilder builder = new ContainerBuilder();
        boolean fmFactoryRegistered = false;
        for (ContainerProvider containerProvider : providers) {
            if (containerProvider instanceof FileManagerProvider) {
                containerProvider.register(builder, null);
            }
            if (!(containerProvider instanceof FileManagerFactoryProvider)) continue;
            containerProvider.register(builder, null);
            fmFactoryRegistered = true;
        }
        DefaultConfiguration.bootstrapFactories(builder);
        DefaultConfiguration.bootstrapTypeConverters(builder);
        if (!fmFactoryRegistered) {
            builder.factory(FileManagerFactory.class, DefaultFileManagerFactory.class, Scope.SINGLETON);
        }
        for (Map.Entry entry : BOOTSTRAP_CONSTANTS.entrySet()) {
            builder.constant((String)entry.getKey(), String.valueOf(entry.getValue()));
        }
        return builder.create(true);
    }

    public static ContainerBuilder bootstrapFactories(ContainerBuilder builder) {
        return builder.factory(ObjectFactory.class, Scope.PROTOTYPE).factory(ActionFactory.class, DefaultActionFactory.class, Scope.PROTOTYPE).factory(ResultFactory.class, DefaultResultFactory.class, Scope.PROTOTYPE).factory(InterceptorFactory.class, DefaultInterceptorFactory.class, Scope.PROTOTYPE).factory(ValidatorFactory.class, DefaultValidatorFactory.class, Scope.PROTOTYPE).factory(ConverterFactory.class, StrutsConverterFactory.class, Scope.PROTOTYPE).factory(UnknownHandlerFactory.class, DefaultUnknownHandlerFactory.class, Scope.PROTOTYPE).factory(FileManager.class, "system", DefaultFileManager.class, Scope.SINGLETON).factory(ReflectionProvider.class, OgnlReflectionProvider.class, Scope.SINGLETON).factory(ValueStackFactory.class, OgnlValueStackFactory.class, Scope.SINGLETON).factory(XWorkConverter.class, Scope.SINGLETON).factory(XWorkBasicConverter.class, Scope.SINGLETON).factory(ConversionPropertiesProcessor.class, StrutsConversionPropertiesProcessor.class, Scope.SINGLETON).factory(ConversionFileProcessor.class, DefaultConversionFileProcessor.class, Scope.SINGLETON).factory(ConversionAnnotationProcessor.class, DefaultConversionAnnotationProcessor.class, Scope.SINGLETON).factory(TypeConverterCreator.class, StrutsTypeConverterCreator.class, Scope.SINGLETON).factory(TypeConverterHolder.class, StrutsTypeConverterHolder.class, Scope.SINGLETON).factory(TextProvider.class, "system", DefaultTextProvider.class, Scope.SINGLETON).factory(LocalizedTextProvider.class, StrutsLocalizedTextProvider.class, Scope.SINGLETON).factory(TextProviderFactory.class, StrutsTextProviderFactory.class, Scope.SINGLETON).factory(LocaleProviderFactory.class, DefaultLocaleProviderFactory.class, Scope.SINGLETON).factory(TextParser.class, OgnlTextParser.class, Scope.SINGLETON).factory(ObjectTypeDeterminer.class, DefaultObjectTypeDeterminer.class, Scope.SINGLETON).factory(RootAccessor.class, CompoundRootAccessor.class, Scope.SINGLETON).factory(MethodAccessor.class, XWorkMethodAccessor.class, Scope.SINGLETON).factory(ExpressionCacheFactory.class, DefaultOgnlExpressionCacheFactory.class, Scope.SINGLETON).factory(BeanInfoCacheFactory.class, DefaultOgnlBeanInfoCacheFactory.class, Scope.SINGLETON).factory(OgnlUtil.class, Scope.SINGLETON).factory(SecurityMemberAccess.class, Scope.PROTOTYPE).factory(OgnlGuard.class, StrutsOgnlGuard.class, Scope.SINGLETON).factory(ProviderAllowlist.class, Scope.SINGLETON).factory(ValueSubstitutor.class, EnvsValueSubstitutor.class, Scope.SINGLETON);
    }

    public static ContainerBuilder bootstrapTypeConverters(ContainerBuilder builder) {
        return builder.factory(TypeConverter.class, "struts.converter.collection", CollectionConverter.class, Scope.SINGLETON).factory(TypeConverter.class, "struts.converter.array", ArrayConverter.class, Scope.SINGLETON).factory(TypeConverter.class, "struts.converter.date", DateConverter.class, Scope.SINGLETON).factory(TypeConverter.class, "struts.converter.number", NumberConverter.class, Scope.SINGLETON).factory(TypeConverter.class, "struts.converter.string", StringConverter.class, Scope.SINGLETON);
    }

    protected synchronized RuntimeConfiguration buildRuntimeConfiguration() throws ConfigurationException {
        LinkedHashMap namespaceActionConfigs = new LinkedHashMap();
        LinkedHashMap<String, String> namespaceConfigs = new LinkedHashMap<String, String>();
        for (PackageConfig packageConfig : this.packageContexts.values()) {
            if (packageConfig.isAbstract()) continue;
            String namespace = packageConfig.getNamespace();
            LinkedHashMap<String, ActionConfig> configs = (LinkedHashMap<String, ActionConfig>)namespaceActionConfigs.get(namespace);
            if (configs == null) {
                configs = new LinkedHashMap<String, ActionConfig>();
            }
            Map<String, ActionConfig> actionConfigs = packageConfig.getAllActionConfigs();
            Iterator<String> iterator = actionConfigs.keySet().iterator();
            while (iterator.hasNext()) {
                String o;
                String actionName = o = iterator.next();
                ActionConfig baseConfig = actionConfigs.get(actionName);
                configs.put(actionName, this.buildFullActionConfig(packageConfig, baseConfig));
            }
            namespaceActionConfigs.put(namespace, configs);
            if (packageConfig.getFullDefaultActionRef() == null) continue;
            namespaceConfigs.put(namespace, packageConfig.getFullDefaultActionRef());
        }
        PatternMatcher matcher = this.container.getInstance(PatternMatcher.class);
        boolean appendNamedParameters = Boolean.parseBoolean(this.container.getInstance(String.class, "struts.matcher.appendNamedParameters"));
        return new RuntimeConfigurationImpl(Collections.unmodifiableMap(namespaceActionConfigs), Collections.unmodifiableMap(namespaceConfigs), matcher, appendNamedParameters);
    }

    private void setDefaultResults(Map<String, ResultConfig> results, PackageConfig packageContext) {
        String defaultResult = packageContext.getFullDefaultResultType();
        for (Map.Entry<String, ResultConfig> entry : results.entrySet()) {
            if (entry.getValue() != null) continue;
            ResultTypeConfig resultTypeConfig = packageContext.getAllResultTypeConfigs().get(defaultResult);
            entry.setValue(new ResultConfig.Builder(null, resultTypeConfig.getClassName()).build());
        }
    }

    private ActionConfig buildFullActionConfig(PackageConfig packageContext, ActionConfig baseConfig) throws ConfigurationException {
        String methodRegex;
        String defaultInterceptorRefName;
        TreeMap<String, String> params = new TreeMap<String, String>(baseConfig.getParams());
        TreeMap<String, ResultConfig> results = new TreeMap<String, ResultConfig>();
        if (!baseConfig.getPackageName().equals(packageContext.getName()) && this.packageContexts.containsKey(baseConfig.getPackageName())) {
            results.putAll(this.packageContexts.get(baseConfig.getPackageName()).getAllGlobalResults());
        } else {
            results.putAll(packageContext.getAllGlobalResults());
        }
        results.putAll(baseConfig.getResults());
        this.setDefaultResults(results, packageContext);
        ArrayList<InterceptorMapping> interceptors = new ArrayList<InterceptorMapping>(baseConfig.getInterceptors());
        if (interceptors.size() <= 0 && (defaultInterceptorRefName = packageContext.getFullDefaultInterceptorRef()) != null) {
            interceptors.addAll(InterceptorBuilder.constructInterceptorReference(new PackageConfig.Builder(packageContext), defaultInterceptorRefName, new LinkedHashMap<String, String>(), packageContext.getLocation(), this.objectFactory));
        }
        if ((methodRegex = this.container.getInstance(String.class, "struts.strictMethodInvocation.methodRegex")) == null) {
            methodRegex = "([A-Za-z0-9_$]*)";
        }
        LOG.debug("Using pattern [{}] to match allowed methods when SMI is disabled!", (Object)methodRegex);
        return new ActionConfig.Builder(baseConfig).addParams(params).addResultConfigs(results).defaultClassName(packageContext.getDefaultClassRef()).interceptors(interceptors).setStrictMethodInvocation(packageContext.isStrictMethodInvocation()).setDefaultMethodRegex(methodRegex).addExceptionMappings(packageContext.getAllExceptionMappingConfigs()).build();
    }

    static {
        HashMap<String, Object> constants = new HashMap<String, Object>();
        constants.put("struts.devMode", Boolean.FALSE);
        constants.put("struts.configuration.xml.reload", Boolean.FALSE);
        constants.put("struts.matcher.appendNamedParameters", Boolean.TRUE);
        constants.put("struts.ognl.expressionCacheType", (Object)OgnlCacheFactory.CacheType.BASIC);
        constants.put("struts.ognl.expressionCacheMaxSize", 10000);
        constants.put("struts.ognl.beanInfoCacheType", (Object)OgnlCacheFactory.CacheType.BASIC);
        constants.put("struts.ognl.beanInfoCacheMaxSize", 10000);
        constants.put("struts.enable.DynamicMethodInvocation", Boolean.FALSE);
        BOOTSTRAP_CONSTANTS = Collections.unmodifiableMap(constants);
        LOG = LogManager.getLogger(DefaultConfiguration.class);
    }

    class ContainerProperties
    extends LocatableProperties {
        private static final long serialVersionUID = -7320625750836896089L;

        ContainerProperties() {
        }

        @Override
        public Object setProperty(String key, String value) {
            String oldValue = this.getProperty(key);
            if (LOG.isInfoEnabled() && oldValue != null && !oldValue.equals(value) && !DefaultConfiguration.this.defaultFrameworkBeanName.equals(oldValue)) {
                LOG.info("Overriding property {} - old value: {} new value: {}", (Object)key, (Object)oldValue, (Object)value);
            }
            return super.setProperty(key, value);
        }

        public void setConstants(ContainerBuilder builder) {
            for (Object keyobj : this.keySet()) {
                String key = (String)keyobj;
                builder.factory(String.class, key, new LocatableConstantFactory<String>(this.getProperty(key), this.getPropertyLocation(key)));
            }
        }
    }

    private static class RuntimeConfigurationImpl
    implements RuntimeConfiguration {
        private final Map<String, Map<String, ActionConfig>> namespaceActionConfigs;
        private final Map<String, ActionConfigMatcher> namespaceActionConfigMatchers;
        private final NamespaceMatcher namespaceMatcher;
        private final Map<String, String> namespaceConfigs;

        public RuntimeConfigurationImpl(Map<String, Map<String, ActionConfig>> namespaceActionConfigs, Map<String, String> namespaceConfigs, PatternMatcher<int[]> matcher, boolean appendNamedParameters) {
            this.namespaceActionConfigs = namespaceActionConfigs;
            this.namespaceConfigs = namespaceConfigs;
            this.namespaceActionConfigMatchers = new LinkedHashMap<String, ActionConfigMatcher>();
            this.namespaceMatcher = new NamespaceMatcher(matcher, namespaceActionConfigs.keySet(), appendNamedParameters);
            for (Map.Entry<String, Map<String, ActionConfig>> entry : namespaceActionConfigs.entrySet()) {
                ActionConfigMatcher configMatcher = new ActionConfigMatcher(matcher, entry.getValue(), true, appendNamedParameters);
                this.namespaceActionConfigMatchers.put(entry.getKey(), configMatcher);
            }
        }

        @Override
        public ActionConfig getActionConfig(String namespace, String name) {
            NamespaceMatch match;
            ActionConfig config = this.findActionConfigInNamespace(namespace, name);
            if (config == null && (match = (NamespaceMatch)this.namespaceMatcher.match(namespace)) != null && (config = this.findActionConfigInNamespace(match.getPattern(), name)) != null) {
                config = new ActionConfig.Builder(config).addParams(match.getVariables()).build();
            }
            if (config == null && StringUtils.isNotBlank((CharSequence)namespace)) {
                config = this.findActionConfigInNamespace("", name);
            }
            return config;
        }

        private ActionConfig findActionConfigInNamespace(String namespace, String name) {
            String defaultActionRef;
            Map<String, ActionConfig> actions;
            ActionConfig config = null;
            if (namespace == null) {
                namespace = "";
            }
            if ((actions = this.namespaceActionConfigs.get(namespace)) != null && (config = actions.get(name)) == null && (config = (ActionConfig)this.namespaceActionConfigMatchers.get(namespace).match(name)) == null && (defaultActionRef = this.namespaceConfigs.get(namespace)) != null) {
                config = actions.get(defaultActionRef);
            }
            return config;
        }

        @Override
        public Map<String, Map<String, ActionConfig>> getActionConfigs() {
            return this.namespaceActionConfigs;
        }

        public String toString() {
            StringBuilder buff = new StringBuilder("RuntimeConfiguration - actions are\n");
            for (Map.Entry<String, Map<String, ActionConfig>> entry : this.namespaceActionConfigs.entrySet()) {
                Map<String, ActionConfig> actionConfigs = entry.getValue();
                for (String s : actionConfigs.keySet()) {
                    buff.append(entry.getKey()).append("/").append(s).append("\n");
                }
            }
            return buff.toString();
        }
    }
}

