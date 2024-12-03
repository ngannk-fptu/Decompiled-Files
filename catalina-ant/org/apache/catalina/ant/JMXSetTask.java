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

public class JMXSetTask
extends AbstractCatalinaTask {
    protected String bean = null;
    protected String attribute = null;
    protected String value = null;

    public String getBean() {
        return this.bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        if (this.bean == null || this.attribute == null || this.value == null) {
            throw new BuildException("Must specify 'bean', 'attribute' and 'value' attributes");
        }
        this.log("Setting attribute " + this.attribute + " in bean " + this.bean + " to " + this.value);
        try {
            this.execute("/jmxproxy/?set=" + URLEncoder.encode(this.bean, this.getCharset()) + "&att=" + URLEncoder.encode(this.attribute, this.getCharset()) + "&val=" + URLEncoder.encode(this.value, this.getCharset()));
        }
        catch (UnsupportedEncodingException e) {
            throw new BuildException("Invalid 'charset' attribute: " + this.getCharset());
        }
    }
}

