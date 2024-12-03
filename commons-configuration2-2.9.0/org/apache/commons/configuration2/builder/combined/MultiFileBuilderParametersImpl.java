/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.Map;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.combined.MultiFileBuilderProperties;

public class MultiFileBuilderParametersImpl
extends BasicBuilderParameters
implements MultiFileBuilderProperties<MultiFileBuilderParametersImpl> {
    private static final String PARAM_KEY = "config-" + MultiFileBuilderParametersImpl.class.getName();
    private BuilderParameters managedBuilderParameters;
    private String filePattern;

    public static MultiFileBuilderParametersImpl fromParameters(Map<String, Object> params) {
        return MultiFileBuilderParametersImpl.fromParameters(params, false);
    }

    public static MultiFileBuilderParametersImpl fromParameters(Map<String, Object> params, boolean createIfMissing) {
        MultiFileBuilderParametersImpl instance = (MultiFileBuilderParametersImpl)params.get(PARAM_KEY);
        if (instance == null && createIfMissing) {
            instance = new MultiFileBuilderParametersImpl();
        }
        return instance;
    }

    public String getFilePattern() {
        return this.filePattern;
    }

    @Override
    public MultiFileBuilderParametersImpl setFilePattern(String p) {
        this.filePattern = p;
        return this;
    }

    public BuilderParameters getManagedBuilderParameters() {
        return this.managedBuilderParameters;
    }

    @Override
    public MultiFileBuilderParametersImpl setManagedBuilderParameters(BuilderParameters p) {
        this.managedBuilderParameters = p;
        return this;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = super.getParameters();
        params.put(PARAM_KEY, this);
        return params;
    }

    @Override
    public MultiFileBuilderParametersImpl clone() {
        MultiFileBuilderParametersImpl copy = (MultiFileBuilderParametersImpl)super.clone();
        copy.setManagedBuilderParameters((BuilderParameters)ConfigurationUtils.cloneIfPossible(this.getManagedBuilderParameters()));
        return copy;
    }
}

