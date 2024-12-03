/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import javax.naming.Context;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.JndiBuilderProperties;

public class JndiBuilderParametersImpl
extends BasicBuilderParameters
implements JndiBuilderProperties<JndiBuilderParametersImpl> {
    private static final String PROP_CONTEXT = "context";
    private static final String PROP_PREFIX = "prefix";

    @Override
    public JndiBuilderParametersImpl setContext(Context ctx) {
        this.storeProperty(PROP_CONTEXT, ctx);
        return this;
    }

    @Override
    public JndiBuilderParametersImpl setPrefix(String p) {
        this.storeProperty(PROP_PREFIX, p);
        return this;
    }
}

