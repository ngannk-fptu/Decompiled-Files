/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.MultiFileConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.CombinedReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingControllerSupport;

public class ReloadingMultiFileConfigurationBuilder<T extends FileBasedConfiguration>
extends MultiFileConfigurationBuilder<T>
implements ReloadingControllerSupport {
    private final ReloadingController reloadingController = this.createReloadingController();

    public ReloadingMultiFileConfigurationBuilder(Class<T> resCls, Map<String, Object> params, boolean allowFailOnInit) {
        super(resCls, params, allowFailOnInit);
    }

    public ReloadingMultiFileConfigurationBuilder(Class<T> resCls, Map<String, Object> params) {
        super(resCls, params);
    }

    public ReloadingMultiFileConfigurationBuilder(Class<T> resCls) {
        super(resCls);
    }

    @Override
    public ReloadingController getReloadingController() {
        return this.reloadingController;
    }

    @Override
    protected FileBasedConfigurationBuilder<T> createManagedBuilder(String fileName, Map<String, Object> params) throws ConfigurationException {
        return new ReloadingFileBasedConfigurationBuilder(this.getResultClass(), params, this.isAllowFailOnInit());
    }

    private ReloadingController createReloadingController() {
        Set empty = Collections.emptySet();
        return new CombinedReloadingController(empty){

            @Override
            public Collection<ReloadingController> getSubControllers() {
                return ReloadingMultiFileConfigurationBuilder.this.getManagedBuilders().values().stream().map(b -> ((ReloadingControllerSupport)((Object)b)).getReloadingController()).collect(Collectors.toList());
            }
        };
    }
}

