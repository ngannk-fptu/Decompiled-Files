/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.CombinedReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingControllerSupport;

public class ReloadingCombinedConfigurationBuilder
extends CombinedConfigurationBuilder
implements ReloadingControllerSupport {
    private ReloadingController reloadingController;

    public ReloadingCombinedConfigurationBuilder() {
    }

    public ReloadingCombinedConfigurationBuilder(Map<String, Object> params, boolean allowFailOnInit) {
        super(params, allowFailOnInit);
    }

    public ReloadingCombinedConfigurationBuilder(Map<String, Object> params) {
        super(params);
    }

    @Override
    public ReloadingCombinedConfigurationBuilder configure(BuilderParameters ... params) {
        super.configure(params);
        return this;
    }

    @Override
    public synchronized ReloadingController getReloadingController() {
        return this.reloadingController;
    }

    @Override
    public CombinedConfiguration getConfiguration() throws ConfigurationException {
        CombinedConfiguration result = (CombinedConfiguration)super.getConfiguration();
        this.reloadingController.resetReloadingState();
        return result;
    }

    @Override
    protected ConfigurationBuilder<? extends HierarchicalConfiguration<?>> createXMLDefinitionBuilder(BuilderParameters builderParams) {
        return new ReloadingFileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class).configure(new BuilderParameters[]{builderParams});
    }

    @Override
    protected void initResultInstance(CombinedConfiguration result) throws ConfigurationException {
        super.initResultInstance(result);
        if (this.reloadingController == null) {
            this.reloadingController = this.createReloadingController();
        }
    }

    protected ReloadingController createReloadingController() throws ConfigurationException {
        LinkedList<ReloadingController> subControllers = new LinkedList<ReloadingController>();
        ConfigurationBuilder<? extends HierarchicalConfiguration<?>> defBuilder = this.getDefinitionBuilder();
        ReloadingCombinedConfigurationBuilder.obtainReloadingController(subControllers, defBuilder);
        this.getChildBuilders().forEach(b -> ReloadingCombinedConfigurationBuilder.obtainReloadingController(subControllers, b));
        CombinedReloadingController ctrl = new CombinedReloadingController(subControllers);
        ctrl.resetInitialReloadingState();
        return ctrl;
    }

    public static void obtainReloadingController(Collection<ReloadingController> subControllers, Object builder) {
        if (builder instanceof ReloadingControllerSupport) {
            subControllers.add(((ReloadingControllerSupport)builder).getReloadingController());
        }
    }
}

