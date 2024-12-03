/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.combined.BaseConfigurationBuilderProvider;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.ConfigurationDeclaration;
import org.apache.commons.configuration2.builder.combined.ReloadingCombinedConfigurationBuilder;

public class CombinedConfigurationBuilderProvider
extends BaseConfigurationBuilderProvider {
    private static final String BUILDER_CLASS = "org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder";
    private static final String RELOADING_BUILDER_CLASS = "org.apache.commons.configuration2.builder.combined.ReloadingCombinedConfigurationBuilder";
    private static final String CONFIGURATION_CLASS = "org.apache.commons.configuration2.CombinedConfiguration";
    private static final String COMBINED_PARAMS = "org.apache.commons.configuration2.builder.combined.CombinedBuilderParametersImpl";
    private static final String FILE_PARAMS = "org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl";

    public CombinedConfigurationBuilderProvider() {
        super(BUILDER_CLASS, RELOADING_BUILDER_CLASS, CONFIGURATION_CLASS, Arrays.asList(COMBINED_PARAMS, FILE_PARAMS));
    }

    @Override
    protected BasicConfigurationBuilder<? extends Configuration> createBuilder(ConfigurationDeclaration decl, Collection<BuilderParameters> params) throws Exception {
        CombinedConfigurationBuilder builder = decl.isReload() ? new ReloadingCombinedConfigurationBuilder() : new CombinedConfigurationBuilder();
        decl.getConfigurationBuilder().initChildEventListeners(builder);
        return builder;
    }

    @Override
    protected void initializeParameterObjects(ConfigurationDeclaration decl, Collection<BuilderParameters> params) throws Exception {
        BasicBuilderParameters basicParams = (BasicBuilderParameters)params.iterator().next();
        CombinedConfigurationBuilderProvider.setUpBasicParameters(decl.getConfigurationBuilder().getConfigurationUnderConstruction(), basicParams);
        super.initializeParameterObjects(decl, params);
    }

    private static void setUpBasicParameters(CombinedConfiguration config, BasicBuilderParameters params) {
        params.setListDelimiterHandler(config.getListDelimiterHandler()).setLogger(config.getLogger()).setThrowExceptionOnMissing(config.isThrowExceptionOnMissing()).setConfigurationDecoder(config.getConfigurationDecoder());
    }
}

