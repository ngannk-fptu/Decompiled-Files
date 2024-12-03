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

public class JMXAccessorUnregisterTask
extends JMXAccessorTask {
    @Override
    public String jmxExecute(MBeanServerConnection jmxServerConnection) throws Exception {
        if (this.getName() == null) {
            throw new BuildException("Must specify a 'name'");
        }
        return this.jmxUuregister(jmxServerConnection, this.getName());
    }

    protected String jmxUuregister(MBeanServerConnection jmxServerConnection, String name) throws Exception {
        String error = null;
        if (this.isEcho()) {
            this.handleOutput("Unregister MBean " + name);
        }
        jmxServerConnection.unregisterMBean(new ObjectName(name));
        return error;
    }
}

