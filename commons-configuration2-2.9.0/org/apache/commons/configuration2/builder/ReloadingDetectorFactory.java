/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.reloading.ReloadingDetector;

public interface ReloadingDetectorFactory {
    public ReloadingDetector createReloadingDetector(FileHandler var1, FileBasedBuilderParametersImpl var2) throws ConfigurationException;
}

