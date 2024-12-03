/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.selectors.BaseSelector;
import org.apache.tools.ant.types.selectors.ExtendFileSelector;

public abstract class BaseExtendSelector
extends BaseSelector
implements ExtendFileSelector {
    protected Parameter[] parameters = null;

    @Override
    public void setParameters(Parameter ... parameters) {
        this.parameters = parameters;
    }

    protected Parameter[] getParameters() {
        return this.parameters;
    }

    @Override
    public abstract boolean isSelected(File var1, String var2, File var3) throws BuildException;
}

