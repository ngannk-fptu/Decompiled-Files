/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.DefaultParametersHandler;
import org.apache.commons.configuration2.builder.DefaultParametersManager;
import org.apache.commons.configuration2.builder.combined.CombinedBuilderProperties;
import org.apache.commons.configuration2.builder.combined.ConfigurationBuilderProvider;

public class CombinedBuilderParametersImpl
extends BasicBuilderParameters
implements CombinedBuilderProperties<CombinedBuilderParametersImpl> {
    private static final String PARAM_KEY = "config-" + CombinedBuilderParametersImpl.class.getName();
    private ConfigurationBuilder<? extends HierarchicalConfiguration<?>> definitionBuilder;
    private BuilderParameters definitionBuilderParameters;
    private final Map<String, ConfigurationBuilderProvider> providers = new HashMap<String, ConfigurationBuilderProvider>();
    private final Collection<BuilderParameters> childParameters = new LinkedList<BuilderParameters>();
    private DefaultParametersManager childDefaultParametersManager;
    private String basePath;
    private boolean inheritSettings = true;

    public static CombinedBuilderParametersImpl fromParameters(Map<String, ?> params) {
        return CombinedBuilderParametersImpl.fromParameters(params, false);
    }

    public static CombinedBuilderParametersImpl fromParameters(Map<String, ?> params, boolean createIfMissing) {
        CombinedBuilderParametersImpl result = (CombinedBuilderParametersImpl)params.get(PARAM_KEY);
        if (result == null && createIfMissing) {
            result = new CombinedBuilderParametersImpl();
        }
        return result;
    }

    @Override
    public void inheritFrom(Map<String, ?> source) {
        super.inheritFrom(source);
        CombinedBuilderParametersImpl srcParams = CombinedBuilderParametersImpl.fromParameters(source);
        if (srcParams != null) {
            this.setChildDefaultParametersManager(srcParams.getChildDefaultParametersManager());
            this.setInheritSettings(srcParams.isInheritSettings());
        }
    }

    public boolean isInheritSettings() {
        return this.inheritSettings;
    }

    @Override
    public CombinedBuilderParametersImpl setInheritSettings(boolean inheritSettings) {
        this.inheritSettings = inheritSettings;
        return this;
    }

    public ConfigurationBuilder<? extends HierarchicalConfiguration<?>> getDefinitionBuilder() {
        return this.definitionBuilder;
    }

    @Override
    public CombinedBuilderParametersImpl setDefinitionBuilder(ConfigurationBuilder<? extends HierarchicalConfiguration<?>> builder) {
        this.definitionBuilder = builder;
        return this;
    }

    @Override
    public CombinedBuilderParametersImpl registerProvider(String tagName, ConfigurationBuilderProvider provider) {
        if (tagName == null) {
            throw new IllegalArgumentException("Tag name must not be null!");
        }
        if (provider == null) {
            throw new IllegalArgumentException("Provider must not be null!");
        }
        this.providers.put(tagName, provider);
        return this;
    }

    public CombinedBuilderParametersImpl registerMissingProviders(Map<String, ConfigurationBuilderProvider> providers) {
        if (providers == null) {
            throw new IllegalArgumentException("Map with providers must not be null!");
        }
        providers.forEach((k, v) -> {
            if (!this.providers.containsKey(k)) {
                this.registerProvider((String)k, (ConfigurationBuilderProvider)v);
            }
        });
        return this;
    }

    public CombinedBuilderParametersImpl registerMissingProviders(CombinedBuilderParametersImpl params) {
        if (params == null) {
            throw new IllegalArgumentException("Source parameters must not be null!");
        }
        return this.registerMissingProviders(params.getProviders());
    }

    public Map<String, ConfigurationBuilderProvider> getProviders() {
        return Collections.unmodifiableMap(this.providers);
    }

    public ConfigurationBuilderProvider providerForTag(String tagName) {
        return this.providers.get(tagName);
    }

    public String getBasePath() {
        return this.basePath;
    }

    @Override
    public CombinedBuilderParametersImpl setBasePath(String path) {
        this.basePath = path;
        return this;
    }

    public BuilderParameters getDefinitionBuilderParameters() {
        return this.definitionBuilderParameters;
    }

    @Override
    public CombinedBuilderParametersImpl setDefinitionBuilderParameters(BuilderParameters params) {
        this.definitionBuilderParameters = params;
        return this;
    }

    public Collection<? extends BuilderParameters> getDefaultChildParameters() {
        return new ArrayList<BuilderParameters>(this.childParameters);
    }

    public DefaultParametersManager getChildDefaultParametersManager() {
        if (this.childDefaultParametersManager == null) {
            this.childDefaultParametersManager = new DefaultParametersManager();
        }
        return this.childDefaultParametersManager;
    }

    @Override
    public CombinedBuilderParametersImpl setChildDefaultParametersManager(DefaultParametersManager manager) {
        this.childDefaultParametersManager = manager;
        return this;
    }

    @Override
    public <D> CombinedBuilderParametersImpl registerChildDefaultsHandler(Class<D> paramClass, DefaultParametersHandler<? super D> handler) {
        this.getChildDefaultParametersManager().registerDefaultsHandler(paramClass, handler);
        return this;
    }

    @Override
    public <D> CombinedBuilderParametersImpl registerChildDefaultsHandler(Class<D> paramClass, DefaultParametersHandler<? super D> handler, Class<?> startClass) {
        this.getChildDefaultParametersManager().registerDefaultsHandler(paramClass, handler, startClass);
        return this;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = super.getParameters();
        params.put(PARAM_KEY, this);
        return params;
    }

    @Override
    public CombinedBuilderParametersImpl clone() {
        CombinedBuilderParametersImpl copy = (CombinedBuilderParametersImpl)super.clone();
        copy.setDefinitionBuilderParameters((BuilderParameters)ConfigurationUtils.cloneIfPossible(this.getDefinitionBuilderParameters()));
        return copy;
    }
}

