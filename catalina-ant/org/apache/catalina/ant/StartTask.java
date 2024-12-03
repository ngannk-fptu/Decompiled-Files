/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant;

import org.apache.catalina.ant.AbstractCatalinaCommandTask;
import org.apache.tools.ant.BuildException;

public class StartTask
extends AbstractCatalinaCommandTask {
    @Override
    public void execute() throws BuildException {
        super.execute();
        this.execute(this.createQueryString("/start").toString());
    }
}

