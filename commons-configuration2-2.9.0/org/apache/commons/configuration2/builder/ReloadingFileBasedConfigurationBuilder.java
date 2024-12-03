/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.Map;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.DefaultReloadingDetectorFactory;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.ReloadingDetectorFactory;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingControllerSupport;
import org.apache.commons.configuration2.reloading.ReloadingDetector;

public class ReloadingFileBasedConfigurationBuilder<T extends FileBasedConfiguration>
extends FileBasedConfigurationBuilder<T>
implements ReloadingControllerSupport {
    private static final ReloadingDetectorFactory DEFAULT_DETECTOR_FACTORY = new DefaultReloadingDetectorFactory();
    private final ReloadingController reloadingController = this.createReloadingController();
    private volatile ReloadingDetector resultReloadingDetector;

    public ReloadingFileBasedConfigurationBuilder(Class<? extends T> resCls, Map<String, Object> params) {
        super(resCls, params);
    }

    public ReloadingFileBasedConfigurationBuilder(Class<? extends T> resCls, Map<String, Object> params, boolean allowFailOnInit) {
        super(resCls, params, allowFailOnInit);
    }

    public ReloadingFileBasedConfigurationBuilder(Class<? extends T> resCls) {
        super(resCls);
    }

    @Override
    public ReloadingController getReloadingController() {
        return this.reloadingController;
    }

    @Override
    public ReloadingFileBasedConfigurationBuilder<T> configure(BuilderParameters ... params) {
        super.configure(params);
        return this;
    }

    protected ReloadingDetector createReloadingDetector(FileHandler handler, FileBasedBuilderParametersImpl fbparams) throws ConfigurationException {
        return ReloadingFileBasedConfigurationBuilder.fetchDetectorFactory(fbparams).createReloadingDetector(handler, fbparams);
    }

    @Override
    protected void initFileHandler(FileHandler handler) throws ConfigurationException {
        super.initFileHandler(handler);
        this.resultReloadingDetector = this.createReloadingDetector(handler, FileBasedBuilderParametersImpl.fromParameters(this.getParameters(), true));
    }

    private ReloadingController createReloadingController() {
        ReloadingDetector ctrlDetector = this.createReloadingDetectorForController();
        ReloadingController ctrl = new ReloadingController(ctrlDetector);
        this.connectToReloadingController(ctrl);
        return ctrl;
    }

    private ReloadingDetector createReloadingDetectorForController() {
        return new ReloadingDetector(){

            @Override
            public void reloadingPerformed() {
                ReloadingDetector detector = ReloadingFileBasedConfigurationBuilder.this.resultReloadingDetector;
                if (detector != null) {
                    detector.reloadingPerformed();
                }
            }

            @Override
            public boolean isReloadingRequired() {
                ReloadingDetector detector = ReloadingFileBasedConfigurationBuilder.this.resultReloadingDetector;
                return detector != null && detector.isReloadingRequired();
            }
        };
    }

    private static ReloadingDetectorFactory fetchDetectorFactory(FileBasedBuilderParametersImpl params) {
        ReloadingDetectorFactory factory = params.getReloadingDetectorFactory();
        return factory != null ? factory : DEFAULT_DETECTOR_FACTORY;
    }
}

