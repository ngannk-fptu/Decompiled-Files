/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.Arrays;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.builder.BuilderConfigurationWrapperFactory;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.BaseConfigurationBuilderProvider;
import org.apache.commons.configuration2.builder.combined.ConfigurationDeclaration;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingControllerSupport;

public class MultiFileConfigurationBuilderProvider
extends BaseConfigurationBuilderProvider {
    private static final String BUILDER_CLASS = "org.apache.commons.configuration2.builder.combined.MultiFileConfigurationBuilder";
    private static final String RELOADING_BUILDER_CLASS = "org.apache.commons.configuration2.builder.combined.ReloadingMultiFileConfigurationBuilder";
    private static final String PARAM_CLASS = "org.apache.commons.configuration2.builder.combined.MultiFileBuilderParametersImpl";

    public MultiFileConfigurationBuilderProvider(String configCls, String paramCls) {
        super(BUILDER_CLASS, RELOADING_BUILDER_CLASS, configCls, Arrays.asList(paramCls, PARAM_CLASS));
    }

    @Override
    public ConfigurationBuilder<? extends Configuration> getConfigurationBuilder(ConfigurationDeclaration decl) throws ConfigurationException {
        ConfigurationBuilder<? extends Configuration> multiBuilder = super.getConfigurationBuilder(decl);
        Configuration wrapConfig = this.createWrapperConfiguration(multiBuilder);
        return MultiFileConfigurationBuilderProvider.createWrapperBuilder(multiBuilder, wrapConfig);
    }

    private Configuration createWrapperConfiguration(ConfigurationBuilder builder) {
        Class<?> configClass = ConfigurationUtils.loadClassNoEx(this.getConfigurationClass());
        Class ifcClass = HierarchicalConfiguration.class.isAssignableFrom(configClass) ? HierarchicalConfiguration.class : Configuration.class;
        return BuilderConfigurationWrapperFactory.createBuilderConfigurationWrapper(ifcClass, builder, BuilderConfigurationWrapperFactory.EventSourceSupport.BUILDER);
    }

    private static ConfigurationBuilder<? extends Configuration> createWrapperBuilder(ConfigurationBuilder<? extends Configuration> multiBuilder, Configuration wrapConfig) {
        if (multiBuilder instanceof ReloadingControllerSupport) {
            return new ReloadableWrapperBuilder(wrapConfig, multiBuilder);
        }
        return new WrapperBuilder(wrapConfig, multiBuilder);
    }

    private static class ReloadableWrapperBuilder
    extends WrapperBuilder
    implements ReloadingControllerSupport {
        private final ReloadingControllerSupport ctrlSupport;

        public ReloadableWrapperBuilder(Configuration conf, ConfigurationBuilder<? extends Configuration> bldr) {
            super(conf, bldr);
            this.ctrlSupport = (ReloadingControllerSupport)((Object)bldr);
        }

        @Override
        public ReloadingController getReloadingController() {
            return this.ctrlSupport.getReloadingController();
        }
    }

    private static class WrapperBuilder
    implements ConfigurationBuilder<Configuration> {
        private final Configuration configuration;
        private final ConfigurationBuilder<? extends Configuration> builder;

        public WrapperBuilder(Configuration conf, ConfigurationBuilder<? extends Configuration> bldr) {
            this.configuration = conf;
            this.builder = bldr;
        }

        @Override
        public Configuration getConfiguration() throws ConfigurationException {
            return this.configuration;
        }

        @Override
        public <T extends Event> void addEventListener(EventType<T> eventType, EventListener<? super T> listener) {
            this.builder.addEventListener(eventType, listener);
        }

        @Override
        public <T extends Event> boolean removeEventListener(EventType<T> eventType, EventListener<? super T> listener) {
            return this.builder.removeEventListener(eventType, listener);
        }
    }
}

