/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationLookup;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.beanutils.BeanDeclaration;
import org.apache.commons.configuration2.beanutils.BeanHelper;
import org.apache.commons.configuration2.beanutils.CombinedBeanDeclaration;
import org.apache.commons.configuration2.beanutils.XMLBeanDeclaration;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.FileBasedBuilderProperties;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.XMLBuilderParametersImpl;
import org.apache.commons.configuration2.builder.XMLBuilderProperties;
import org.apache.commons.configuration2.builder.combined.BaseConfigurationBuilderProvider;
import org.apache.commons.configuration2.builder.combined.CombinedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilderProvider;
import org.apache.commons.configuration2.builder.combined.ConfigurationBuilderProvider;
import org.apache.commons.configuration2.builder.combined.ConfigurationDeclaration;
import org.apache.commons.configuration2.builder.combined.FileExtensionConfigurationBuilderProvider;
import org.apache.commons.configuration2.builder.combined.MultiFileConfigurationBuilderProvider;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.configuration2.resolver.CatalogResolver;
import org.apache.commons.configuration2.tree.OverrideCombiner;
import org.apache.commons.configuration2.tree.UnionCombiner;
import org.xml.sax.EntityResolver;

public class CombinedConfigurationBuilder
extends BasicConfigurationBuilder<CombinedConfiguration> {
    public static final String ADDITIONAL_NAME = CombinedConfigurationBuilder.class.getName() + "/ADDITIONAL_CONFIG";
    static final String CONFIG_BEAN_FACTORY_NAME = CombinedConfigurationBuilder.class.getName() + ".CONFIG_BEAN_FACTORY_NAME";
    static final String ATTR_NAME = "[@config-name]";
    static final String ATTR_ATNAME = "at";
    static final String ATTR_AT_RES = "[@config-at]";
    static final String ATTR_AT = "[@at]";
    static final String ATTR_OPTIONALNAME = "optional";
    static final String ATTR_OPTIONAL_RES = "[@config-optional]";
    static final String ATTR_OPTIONAL = "[@optional]";
    static final String ATTR_FORCECREATE = "[@config-forceCreate]";
    static final String ATTR_RELOAD = "[@config-reload]";
    static final String KEY_SYSTEM_PROPS = "[@systemProperties]";
    static final String SEC_HEADER = "header";
    static final String KEY_UNION = "additional";
    static final String[] CONFIG_SECTIONS = new String[]{"additional", "override", "header"};
    static final String KEY_OVERRIDE = "override";
    static final String KEY_OVERRIDE_LIST = "header.combiner.override.list-nodes.node";
    static final String KEY_ADDITIONAL_LIST = "header.combiner.additional.list-nodes.node";
    static final String KEY_CONFIGURATION_PROVIDERS = "header.providers.provider";
    static final String KEY_PROVIDER_KEY = "[@config-tag]";
    static final String KEY_CONFIGURATION_LOOKUPS = "header.lookups.lookup";
    static final String KEY_ENTITY_RESOLVER = "header.entity-resolver";
    static final String KEY_LOOKUP_KEY = "[@config-prefix]";
    static final String FILE_SYSTEM = "header.fileSystem";
    static final String KEY_RESULT = "header.result";
    static final String KEY_COMBINER = "header.result.nodeCombiner";
    static final String EXT_XML = "xml";
    private static final String BASIC_BUILDER = "org.apache.commons.configuration2.builder.BasicConfigurationBuilder";
    private static final String FILE_BUILDER = "org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder";
    private static final String RELOADING_BUILDER = "org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder";
    private static final String FILE_PARAMS = "org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl";
    private static final ConfigurationBuilderProvider PROPERTIES_PROVIDER = new FileExtensionConfigurationBuilderProvider("org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder", "org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder", "org.apache.commons.configuration2.XMLPropertiesConfiguration", "org.apache.commons.configuration2.PropertiesConfiguration", "xml", Collections.singletonList("org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl"));
    private static final ConfigurationBuilderProvider XML_PROVIDER = new BaseConfigurationBuilderProvider("org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder", "org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder", "org.apache.commons.configuration2.XMLConfiguration", Collections.singletonList("org.apache.commons.configuration2.builder.XMLBuilderParametersImpl"));
    private static final BaseConfigurationBuilderProvider JNDI_PROVIDER = new BaseConfigurationBuilderProvider("org.apache.commons.configuration2.builder.BasicConfigurationBuilder", null, "org.apache.commons.configuration2.JNDIConfiguration", Collections.singletonList("org.apache.commons.configuration2.builder.JndiBuilderParametersImpl"));
    private static final BaseConfigurationBuilderProvider SYSTEM_PROVIDER = new BaseConfigurationBuilderProvider("org.apache.commons.configuration2.builder.BasicConfigurationBuilder", null, "org.apache.commons.configuration2.SystemConfiguration", Collections.singletonList("org.apache.commons.configuration2.builder.BasicBuilderParameters"));
    private static final BaseConfigurationBuilderProvider INI_PROVIDER = new BaseConfigurationBuilderProvider("org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder", "org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder", "org.apache.commons.configuration2.INIConfiguration", Collections.singletonList("org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl"));
    private static final BaseConfigurationBuilderProvider ENV_PROVIDER = new BaseConfigurationBuilderProvider("org.apache.commons.configuration2.builder.BasicConfigurationBuilder", null, "org.apache.commons.configuration2.EnvironmentConfiguration", Collections.singletonList("org.apache.commons.configuration2.builder.BasicBuilderParameters"));
    private static final BaseConfigurationBuilderProvider PLIST_PROVIDER = new FileExtensionConfigurationBuilderProvider("org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder", "org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder", "org.apache.commons.configuration2.plist.XMLPropertyListConfiguration", "org.apache.commons.configuration2.plist.PropertyListConfiguration", "xml", Collections.singletonList("org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl"));
    private static final BaseConfigurationBuilderProvider COMBINED_PROVIDER = new CombinedConfigurationBuilderProvider();
    private static final MultiFileConfigurationBuilderProvider MULTI_XML_PROVIDER = new MultiFileConfigurationBuilderProvider("org.apache.commons.configuration2.XMLConfiguration", "org.apache.commons.configuration2.builder.XMLBuilderParametersImpl");
    private static final String[] DEFAULT_TAGS = new String[]{"properties", "xml", "hierarchicalXml", "plist", "ini", "system", "env", "jndi", "configuration", "multiFile"};
    private static final ConfigurationBuilderProvider[] DEFAULT_PROVIDERS = new ConfigurationBuilderProvider[]{PROPERTIES_PROVIDER, XML_PROVIDER, XML_PROVIDER, PLIST_PROVIDER, INI_PROVIDER, SYSTEM_PROVIDER, ENV_PROVIDER, JNDI_PROVIDER, COMBINED_PROVIDER, MULTI_XML_PROVIDER};
    private static final Map<String, ConfigurationBuilderProvider> DEFAULT_PROVIDERS_MAP = CombinedConfigurationBuilder.createDefaultProviders();
    private ConfigurationBuilder<? extends HierarchicalConfiguration<?>> definitionBuilder;
    private HierarchicalConfiguration<?> definitionConfiguration;
    private ConfigurationSourceData sourceData;
    private CombinedBuilderParametersImpl currentParameters;
    private XMLBuilderParametersImpl currentXMLParameters;
    private CombinedConfiguration currentConfiguration;
    private ConfigurationInterpolator parentInterpolator;

    public CombinedConfigurationBuilder() {
        super(CombinedConfiguration.class);
    }

    public CombinedConfigurationBuilder(Map<String, Object> params) {
        super(CombinedConfiguration.class, params);
    }

    public CombinedConfigurationBuilder(Map<String, Object> params, boolean allowFailOnInit) {
        super(CombinedConfiguration.class, params, allowFailOnInit);
    }

    public synchronized ConfigurationBuilder<? extends HierarchicalConfiguration<?>> getDefinitionBuilder() throws ConfigurationException {
        if (this.definitionBuilder == null) {
            this.definitionBuilder = this.setupDefinitionBuilder(this.getParameters());
            this.addDefinitionBuilderChangeListener(this.definitionBuilder);
        }
        return this.definitionBuilder;
    }

    public CombinedConfigurationBuilder configure(BuilderParameters ... params) {
        super.configure(params);
        return this;
    }

    public synchronized ConfigurationBuilder<? extends Configuration> getNamedBuilder(String name) throws ConfigurationException {
        if (this.sourceData == null) {
            throw new ConfigurationException("Information about child builders has not been setup yet! Call getConfiguration() first.");
        }
        ConfigurationBuilder<? extends Configuration> builder = this.sourceData.getNamedBuilder(name);
        if (builder == null) {
            throw new ConfigurationException("Builder cannot be resolved: " + name);
        }
        return builder;
    }

    public synchronized Set<String> builderNames() {
        if (this.sourceData == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(this.sourceData.builderNames());
    }

    @Override
    public synchronized void resetParameters() {
        super.resetParameters();
        this.definitionBuilder = null;
        this.definitionConfiguration = null;
        this.currentParameters = null;
        this.currentXMLParameters = null;
        if (this.sourceData != null) {
            this.sourceData.cleanUp();
            this.sourceData = null;
        }
    }

    protected ConfigurationBuilder<? extends HierarchicalConfiguration<?>> setupDefinitionBuilder(Map<String, Object> params) throws ConfigurationException {
        FileBasedBuilderParametersImpl fileParams;
        CombinedBuilderParametersImpl cbParams = CombinedBuilderParametersImpl.fromParameters(params);
        if (cbParams != null) {
            ConfigurationBuilder<? extends HierarchicalConfiguration<?>> defBuilder = cbParams.getDefinitionBuilder();
            if (defBuilder != null) {
                return defBuilder;
            }
            if (cbParams.getDefinitionBuilderParameters() != null) {
                return this.createXMLDefinitionBuilder(cbParams.getDefinitionBuilderParameters());
            }
        }
        if ((fileParams = FileBasedBuilderParametersImpl.fromParameters(params)) != null) {
            return this.createXMLDefinitionBuilder(fileParams);
        }
        throw new ConfigurationException("No builder for configuration definition specified!");
    }

    protected ConfigurationBuilder<? extends HierarchicalConfiguration<?>> createXMLDefinitionBuilder(BuilderParameters builderParams) {
        return new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class).configure(new BuilderParameters[]{builderParams});
    }

    protected HierarchicalConfiguration<?> getDefinitionConfiguration() throws ConfigurationException {
        if (this.definitionConfiguration == null) {
            this.definitionConfiguration = this.getDefinitionBuilder().getConfiguration();
        }
        return this.definitionConfiguration;
    }

    protected synchronized Collection<ConfigurationBuilder<? extends Configuration>> getChildBuilders() {
        return this.sourceData.getChildBuilders();
    }

    @Override
    protected BeanDeclaration createResultDeclaration(Map<String, Object> params) throws ConfigurationException {
        BeanDeclaration paramsDecl = super.createResultDeclaration(params);
        XMLBeanDeclaration resultDecl = new XMLBeanDeclaration(this.getDefinitionConfiguration(), KEY_RESULT, true, CombinedConfiguration.class.getName());
        return new CombinedBeanDeclaration(resultDecl, paramsDecl);
    }

    @Override
    protected void initResultInstance(CombinedConfiguration result) throws ConfigurationException {
        super.initResultInstance(result);
        this.currentConfiguration = result;
        HierarchicalConfiguration<?> config = this.getDefinitionConfiguration();
        if (config.getMaxIndex(KEY_COMBINER) < 0) {
            result.setNodeCombiner(new OverrideCombiner());
        }
        this.setUpCurrentParameters();
        CombinedConfigurationBuilder.initNodeCombinerListNodes(result, config, KEY_OVERRIDE_LIST);
        this.registerConfiguredProviders(config);
        this.setUpCurrentXMLParameters();
        this.currentXMLParameters.setFileSystem(this.initFileSystem(config));
        this.initSystemProperties(config, this.getBasePath());
        this.registerConfiguredLookups(config, result);
        this.configureEntityResolver(config, this.currentXMLParameters);
        this.setUpParentInterpolator(this.currentConfiguration, config);
        ConfigurationSourceData data = this.getSourceData();
        boolean createBuilders = data.getChildBuilders().isEmpty();
        List<ConfigurationBuilder<? extends Configuration>> overrideBuilders = data.createAndAddConfigurations(result, data.getOverrideSources(), data.overrideBuilders);
        if (createBuilders) {
            data.overrideBuilders.addAll(overrideBuilders);
        }
        if (!data.getUnionSources().isEmpty()) {
            CombinedConfiguration addConfig = this.createAdditionalsConfiguration(result);
            result.addConfiguration(addConfig, ADDITIONAL_NAME);
            CombinedConfigurationBuilder.initNodeCombinerListNodes(addConfig, config, KEY_ADDITIONAL_LIST);
            List<ConfigurationBuilder<? extends Configuration>> unionBuilders = data.createAndAddConfigurations(addConfig, data.unionDeclarations, data.unionBuilders);
            if (createBuilders) {
                data.unionBuilders.addAll(unionBuilders);
            }
        }
        result.isEmpty();
        this.currentConfiguration = null;
    }

    protected CombinedConfiguration createAdditionalsConfiguration(CombinedConfiguration resultConfig) {
        CombinedConfiguration addConfig = new CombinedConfiguration(new UnionCombiner());
        addConfig.setListDelimiterHandler(resultConfig.getListDelimiterHandler());
        return addConfig;
    }

    protected void registerConfiguredLookups(HierarchicalConfiguration<?> defConfig, Configuration resultConfig) throws ConfigurationException {
        Map<String, Lookup> lookups = defConfig.configurationsAt(KEY_CONFIGURATION_LOOKUPS).stream().collect(Collectors.toMap(config -> config.getString(KEY_LOOKUP_KEY), config -> (Lookup)this.fetchBeanHelper().createBean(new XMLBeanDeclaration(config))));
        if (!lookups.isEmpty()) {
            ConfigurationInterpolator defCI = defConfig.getInterpolator();
            if (defCI != null) {
                defCI.registerLookups(lookups);
            }
            resultConfig.getInterpolator().registerLookups(lookups);
        }
    }

    protected FileSystem initFileSystem(HierarchicalConfiguration<?> config) throws ConfigurationException {
        if (config.getMaxIndex(FILE_SYSTEM) == 0) {
            XMLBeanDeclaration decl = new XMLBeanDeclaration(config, FILE_SYSTEM);
            return (FileSystem)this.fetchBeanHelper().createBean(decl);
        }
        return null;
    }

    protected void initSystemProperties(HierarchicalConfiguration<?> config, String basePath) throws ConfigurationException {
        String fileName = config.getString(KEY_SYSTEM_PROPS);
        if (fileName != null) {
            try {
                SystemConfiguration.setSystemProperties(basePath, fileName);
            }
            catch (Exception ex) {
                throw new ConfigurationException("Error setting system properties from " + fileName, ex);
            }
        }
    }

    protected void configureEntityResolver(HierarchicalConfiguration<?> config, XMLBuilderParametersImpl xmlParams) throws ConfigurationException {
        if (config.getMaxIndex(KEY_ENTITY_RESOLVER) == 0) {
            String basePath;
            XMLBeanDeclaration decl = new XMLBeanDeclaration(config, KEY_ENTITY_RESOLVER, true);
            EntityResolver resolver = (EntityResolver)this.fetchBeanHelper().createBean(decl, CatalogResolver.class);
            FileSystem fileSystem = xmlParams.getFileHandler().getFileSystem();
            if (fileSystem != null) {
                BeanHelper.setProperty(resolver, "fileSystem", fileSystem);
            }
            if ((basePath = xmlParams.getFileHandler().getBasePath()) != null) {
                BeanHelper.setProperty(resolver, "baseDir", basePath);
            }
            ConfigurationInterpolator ci = new ConfigurationInterpolator();
            ci.registerLookups(this.fetchPrefixLookups());
            BeanHelper.setProperty(resolver, "interpolator", ci);
            xmlParams.setEntityResolver(resolver);
        }
    }

    protected ConfigurationBuilderProvider providerForTag(String tagName) {
        return this.currentParameters.providerForTag(tagName);
    }

    protected void initChildBuilderParameters(BuilderParameters params) {
        this.initDefaultChildParameters(params);
        if (params instanceof BasicBuilderParameters) {
            this.initChildBasicParameters((BasicBuilderParameters)params);
        }
        if (params instanceof XMLBuilderProperties) {
            this.initChildXMLParameters((XMLBuilderProperties)((Object)params));
        }
        if (params instanceof FileBasedBuilderProperties) {
            this.initChildFileBasedParameters((FileBasedBuilderProperties)((Object)params));
        }
        if (params instanceof CombinedBuilderParametersImpl) {
            this.initChildCombinedParameters((CombinedBuilderParametersImpl)params);
        }
    }

    void initChildEventListeners(BasicConfigurationBuilder<? extends Configuration> dest) {
        this.copyEventListeners(dest);
    }

    CombinedConfiguration getConfigurationUnderConstruction() {
        return this.currentConfiguration;
    }

    void initBean(Object bean, BeanDeclaration decl) {
        this.fetchBeanHelper().initBean(bean, decl);
    }

    private void setUpCurrentParameters() {
        this.currentParameters = CombinedBuilderParametersImpl.fromParameters(this.getParameters(), true);
        this.currentParameters.registerMissingProviders(DEFAULT_PROVIDERS_MAP);
    }

    private void setUpCurrentXMLParameters() throws ConfigurationException {
        this.currentXMLParameters = new XMLBuilderParametersImpl();
        this.initDefaultBasePath();
    }

    private void setUpParentInterpolator(Configuration resultConfig, Configuration defConfig) {
        this.parentInterpolator = new ConfigurationInterpolator();
        this.parentInterpolator.addDefaultLookup(new ConfigurationLookup(resultConfig));
        ConfigurationInterpolator defInterpolator = defConfig.getInterpolator();
        if (defInterpolator != null) {
            defInterpolator.setParentInterpolator(this.parentInterpolator);
        }
    }

    private void initDefaultBasePath() throws ConfigurationException {
        assert (this.currentParameters != null) : "Current parameters undefined!";
        if (this.currentParameters.getBasePath() != null) {
            this.currentXMLParameters.setBasePath(this.currentParameters.getBasePath());
        } else {
            ConfigurationBuilder<? extends HierarchicalConfiguration<?>> defBuilder = this.getDefinitionBuilder();
            if (defBuilder instanceof FileBasedConfigurationBuilder) {
                FileBasedConfigurationBuilder fileBuilder = (FileBasedConfigurationBuilder)defBuilder;
                URL url = fileBuilder.getFileHandler().getURL();
                this.currentXMLParameters.setBasePath(url != null ? url.toExternalForm() : fileBuilder.getFileHandler().getBasePath());
            }
        }
    }

    private void initDefaultChildParameters(BuilderParameters params) {
        this.currentParameters.getChildDefaultParametersManager().initializeParameters(params);
    }

    private void initChildBasicParameters(BasicBuilderParameters params) {
        params.setPrefixLookups((Map)this.fetchPrefixLookups());
        params.setParentInterpolator(this.parentInterpolator);
        if (this.currentParameters.isInheritSettings()) {
            params.inheritFrom(this.getParameters());
        }
    }

    private void initChildFileBasedParameters(FileBasedBuilderProperties<?> params) {
        params.setBasePath(this.getBasePath());
        params.setFileSystem(this.currentXMLParameters.getFileHandler().getFileSystem());
    }

    private void initChildXMLParameters(XMLBuilderProperties<?> params) {
        params.setEntityResolver(this.currentXMLParameters.getEntityResolver());
    }

    private void initChildCombinedParameters(CombinedBuilderParametersImpl params) {
        params.registerMissingProviders(this.currentParameters);
        params.setBasePath(this.getBasePath());
    }

    private ConfigurationSourceData getSourceData() throws ConfigurationException {
        if (this.sourceData == null) {
            if (this.currentParameters == null) {
                this.setUpCurrentParameters();
                this.setUpCurrentXMLParameters();
            }
            this.sourceData = this.createSourceData();
        }
        return this.sourceData;
    }

    private ConfigurationSourceData createSourceData() throws ConfigurationException {
        ConfigurationSourceData result = new ConfigurationSourceData();
        result.initFromDefinitionConfiguration(this.getDefinitionConfiguration());
        return result;
    }

    private String getBasePath() {
        return this.currentXMLParameters.getFileHandler().getBasePath();
    }

    private void registerConfiguredProviders(HierarchicalConfiguration<?> defConfig) {
        defConfig.configurationsAt(KEY_CONFIGURATION_PROVIDERS).forEach(config -> {
            XMLBeanDeclaration decl = new XMLBeanDeclaration(config);
            String key = config.getString(KEY_PROVIDER_KEY);
            this.currentParameters.registerProvider(key, (ConfigurationBuilderProvider)this.fetchBeanHelper().createBean(decl));
        });
    }

    private void addDefinitionBuilderChangeListener(ConfigurationBuilder<? extends HierarchicalConfiguration<?>> defBuilder) {
        defBuilder.addEventListener(ConfigurationBuilderEvent.RESET, (? super T event) -> {
            CombinedConfigurationBuilder combinedConfigurationBuilder = this;
            synchronized (combinedConfigurationBuilder) {
                this.reset();
                this.definitionBuilder = defBuilder;
            }
        });
    }

    private Map<String, ? extends Lookup> fetchPrefixLookups() {
        CombinedConfiguration cc = this.getConfigurationUnderConstruction();
        return cc != null ? cc.getInterpolator().getLookups() : null;
    }

    private Collection<ConfigurationDeclaration> createDeclarations(Collection<? extends HierarchicalConfiguration<?>> configs) {
        return configs.stream().map(c -> new ConfigurationDeclaration(this, (HierarchicalConfiguration<?>)c)).collect(Collectors.toList());
    }

    private static void initNodeCombinerListNodes(CombinedConfiguration cc, HierarchicalConfiguration<?> defConfig, String key) {
        defConfig.getList(key).forEach(listNode -> cc.getNodeCombiner().addListNode((String)listNode));
    }

    private static Map<String, ConfigurationBuilderProvider> createDefaultProviders() {
        HashMap<String, ConfigurationBuilderProvider> providers = new HashMap<String, ConfigurationBuilderProvider>();
        for (int i = 0; i < DEFAULT_TAGS.length; ++i) {
            providers.put(DEFAULT_TAGS[i], DEFAULT_PROVIDERS[i]);
        }
        return providers;
    }

    private class ConfigurationSourceData {
        private final List<ConfigurationDeclaration> overrideDeclarations = new ArrayList<ConfigurationDeclaration>();
        private final List<ConfigurationDeclaration> unionDeclarations = new ArrayList<ConfigurationDeclaration>();
        private final List<ConfigurationBuilder<? extends Configuration>> overrideBuilders = new ArrayList<ConfigurationBuilder<? extends Configuration>>();
        private final List<ConfigurationBuilder<? extends Configuration>> unionBuilders = new ArrayList<ConfigurationBuilder<? extends Configuration>>();
        private final Map<String, ConfigurationBuilder<? extends Configuration>> namedBuilders = new HashMap<String, ConfigurationBuilder<? extends Configuration>>();
        private final Collection<ConfigurationBuilder<? extends Configuration>> allBuilders = new LinkedList<ConfigurationBuilder<? extends Configuration>>();
        private final EventListener<ConfigurationBuilderEvent> changeListener = this.createBuilderChangeListener();

        public void initFromDefinitionConfiguration(HierarchicalConfiguration<?> config) throws ConfigurationException {
            this.overrideDeclarations.addAll(CombinedConfigurationBuilder.this.createDeclarations(this.fetchTopLevelOverrideConfigs(config)));
            this.overrideDeclarations.addAll(CombinedConfigurationBuilder.this.createDeclarations(config.childConfigurationsAt(CombinedConfigurationBuilder.KEY_OVERRIDE)));
            this.unionDeclarations.addAll(CombinedConfigurationBuilder.this.createDeclarations(config.childConfigurationsAt(CombinedConfigurationBuilder.KEY_UNION)));
        }

        public List<ConfigurationBuilder<? extends Configuration>> createAndAddConfigurations(CombinedConfiguration ccResult, List<ConfigurationDeclaration> srcDecl, List<ConfigurationBuilder<? extends Configuration>> builders) throws ConfigurationException {
            boolean createBuilders = builders.isEmpty();
            List<ConfigurationBuilder<? extends Configuration>> newBuilders = createBuilders ? new ArrayList<ConfigurationBuilder<? extends Configuration>>(srcDecl.size()) : builders;
            for (int i = 0; i < srcDecl.size(); ++i) {
                ConfigurationBuilder<? extends Configuration> b;
                if (createBuilders) {
                    b = this.createConfigurationBuilder(srcDecl.get(i));
                    newBuilders.add(b);
                } else {
                    b = builders.get(i);
                }
                this.addChildConfiguration(ccResult, srcDecl.get(i), b);
            }
            return newBuilders;
        }

        public void cleanUp() {
            this.getChildBuilders().forEach(b -> b.removeEventListener(ConfigurationBuilderEvent.RESET, this.changeListener));
            this.namedBuilders.clear();
        }

        public Collection<ConfigurationBuilder<? extends Configuration>> getChildBuilders() {
            return this.allBuilders;
        }

        public List<ConfigurationDeclaration> getOverrideSources() {
            return this.overrideDeclarations;
        }

        public List<ConfigurationDeclaration> getUnionSources() {
            return this.unionDeclarations;
        }

        public ConfigurationBuilder<? extends Configuration> getNamedBuilder(String name) {
            return this.namedBuilders.get(name);
        }

        public Set<String> builderNames() {
            return this.namedBuilders.keySet();
        }

        private ConfigurationBuilder<? extends Configuration> createConfigurationBuilder(ConfigurationDeclaration decl) throws ConfigurationException {
            ConfigurationBuilderProvider provider = CombinedConfigurationBuilder.this.providerForTag(decl.getConfiguration().getRootElementName());
            if (provider == null) {
                throw new ConfigurationException("Unsupported configuration source: " + decl.getConfiguration().getRootElementName());
            }
            ConfigurationBuilder<? extends Configuration> builder = provider.getConfigurationBuilder(decl);
            if (decl.getName() != null) {
                this.namedBuilders.put(decl.getName(), builder);
            }
            this.allBuilders.add(builder);
            builder.addEventListener(ConfigurationBuilderEvent.RESET, this.changeListener);
            return builder;
        }

        private void addChildConfiguration(CombinedConfiguration ccResult, ConfigurationDeclaration decl, ConfigurationBuilder<? extends Configuration> builder) throws ConfigurationException {
            block2: {
                try {
                    ccResult.addConfiguration(builder.getConfiguration(), decl.getName(), decl.getAt());
                }
                catch (ConfigurationException cex) {
                    if (decl.isOptional()) break block2;
                    throw cex;
                }
            }
        }

        private EventListener<ConfigurationBuilderEvent> createBuilderChangeListener() {
            return event -> CombinedConfigurationBuilder.this.resetResult();
        }

        private List<? extends HierarchicalConfiguration<?>> fetchTopLevelOverrideConfigs(HierarchicalConfiguration<?> config) {
            List<HierarchicalConfiguration<?>> configs = config.childConfigurationsAt(null);
            Iterator<HierarchicalConfiguration<?>> it = configs.iterator();
            block0: while (it.hasNext()) {
                String nodeName = it.next().getRootElementName();
                for (String element : CONFIG_SECTIONS) {
                    if (!element.equals(nodeName)) continue;
                    it.remove();
                    continue block0;
                }
            }
            return configs;
        }
    }
}

