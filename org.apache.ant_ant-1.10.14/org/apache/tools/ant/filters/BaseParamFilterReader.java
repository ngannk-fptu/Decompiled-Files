/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.Reader;
import org.apache.tools.ant.filters.BaseFilterReader;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Parameterizable;

public abstract class BaseParamFilterReader
extends BaseFilterReader
implements Parameterizable {
    private Parameter[] parameters;

    public BaseParamFilterReader() {
    }

    public BaseParamFilterReader(Reader in) {
        super(in);
    }

    @Override
    public final void setParameters(Parameter ... parameters) {
        this.parameters = parameters;
        this.setInitialized(false);
    }

    protected final Parameter[] getParameters() {
        return this.parameters;
    }
}

