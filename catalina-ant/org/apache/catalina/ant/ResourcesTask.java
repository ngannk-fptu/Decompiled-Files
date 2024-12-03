/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.catalina.ant.AbstractCatalinaTask;
import org.apache.tools.ant.BuildException;

public class ResourcesTask
extends AbstractCatalinaTask {
    protected String type = null;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        if (this.type != null) {
            try {
                this.execute("/resources?type=" + URLEncoder.encode(this.type, this.getCharset()));
            }
            catch (UnsupportedEncodingException e) {
                throw new BuildException("Invalid 'charset' attribute: " + this.getCharset());
            }
        } else {
            this.execute("/resources");
        }
    }
}

