/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant;

import org.apache.catalina.ant.AbstractCatalinaTask;
import org.apache.tools.ant.BuildException;

public class FindLeaksTask
extends AbstractCatalinaTask {
    private boolean statusLine = true;

    public void setStatusLine(boolean statusLine) {
        this.statusLine = statusLine;
    }

    public boolean getStatusLine() {
        return this.statusLine;
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        this.execute("/findleaks?statusLine=" + Boolean.toString(this.statusLine));
    }
}

