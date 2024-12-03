/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant;

import org.apache.catalina.ant.AbstractCatalinaCommandTask;
import org.apache.tools.ant.BuildException;

public class SessionsTask
extends AbstractCatalinaCommandTask {
    protected String idle = null;

    public String getIdle() {
        return this.idle;
    }

    public void setIdle(String idle) {
        this.idle = idle;
    }

    @Override
    public StringBuilder createQueryString(String command) {
        StringBuilder buffer = super.createQueryString(command);
        if (this.path != null && this.idle != null) {
            buffer.append("&idle=");
            buffer.append(this.idle);
        }
        return buffer;
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        this.execute(this.createQueryString("/sessions").toString());
    }
}

