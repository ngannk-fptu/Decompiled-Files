/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.selectors.FileSelector;

public abstract class BaseSelector
extends DataType
implements FileSelector {
    private String errmsg = null;
    private Throwable cause;

    public void setError(String msg) {
        if (this.errmsg == null) {
            this.errmsg = msg;
        }
    }

    public void setError(String msg, Throwable cause) {
        if (this.errmsg == null) {
            this.errmsg = msg;
            this.cause = cause;
        }
    }

    public String getError() {
        return this.errmsg;
    }

    public void verifySettings() {
        if (this.isReference()) {
            this.getRef().verifySettings();
        }
    }

    public void validate() {
        if (this.getError() == null) {
            this.verifySettings();
        }
        if (this.getError() != null) {
            throw new BuildException(this.errmsg, this.cause);
        }
        if (!this.isReference()) {
            this.dieOnCircularReference();
        }
    }

    @Override
    public abstract boolean isSelected(File var1, String var2, File var3);

    private BaseSelector getRef() {
        return this.getCheckedRef(BaseSelector.class);
    }
}

