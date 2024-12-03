/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant.jmx;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.apache.catalina.ant.jmx.JMXAccessorTask;
import org.apache.tools.ant.BuildException;

public class JMXAccessorGetTask
extends JMXAccessorTask {
    private String attribute;

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String jmxExecute(MBeanServerConnection jmxServerConnection) throws Exception {
        if (this.getName() == null) {
            throw new BuildException("Must specify a 'name'");
        }
        if (this.attribute == null) {
            throw new BuildException("Must specify a 'attribute' for get");
        }
        return this.jmxGet(jmxServerConnection, this.getName());
    }

    protected String jmxGet(MBeanServerConnection jmxServerConnection, String name) throws Exception {
        Object result;
        String error = null;
        if (this.isEcho()) {
            this.handleOutput("MBean " + name + " get attribute " + this.attribute);
        }
        if ((result = jmxServerConnection.getAttribute(new ObjectName(name), this.attribute)) != null) {
            this.echoResult(this.attribute, result);
            this.createProperty(result);
        } else {
            error = "Attribute " + this.attribute + " is empty";
        }
        return error;
    }
}

