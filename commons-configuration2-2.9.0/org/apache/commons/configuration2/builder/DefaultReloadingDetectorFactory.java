/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.ReloadingDetectorFactory;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.reloading.FileHandlerReloadingDetector;
import org.apache.commons.configuration2.reloading.ReloadingDetector;

public class DefaultReloadingDetectorFactory
implements ReloadingDetectorFactory {
    @Override
    public ReloadingDetector createReloadingDetector(FileHandler handler, FileBasedBuilderParametersImpl params) throws ConfigurationException {
        Long refreshDelay = params.getReloadingRefreshDelay();
        FileHandlerReloadingDetector fileHandlerReloadingDetector = refreshDelay != null ? new FileHandlerReloadingDetector(handler, refreshDelay) : new FileHandlerReloadingDetector(handler);
        fileHandlerReloadingDetector.refresh();
        return fileHandlerReloadingDetector;
    }
}

