/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.beanutils.BeanHelper;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.DefaultParametersHandler;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;

public class CopyObjectDefaultHandler
implements DefaultParametersHandler<Object> {
    private final BuilderParameters source;

    public CopyObjectDefaultHandler(BuilderParameters src) {
        if (src == null) {
            throw new IllegalArgumentException("Source object must not be null!");
        }
        this.source = src;
    }

    public BuilderParameters getSource() {
        return this.source;
    }

    @Override
    public void initializeDefaults(Object parameters) {
        try {
            BeanHelper.copyProperties(parameters, this.getSource().getParameters());
            BeanHelper.copyProperties(parameters, this.getSource());
        }
        catch (Exception e) {
            throw new ConfigurationRuntimeException(e);
        }
    }
}

