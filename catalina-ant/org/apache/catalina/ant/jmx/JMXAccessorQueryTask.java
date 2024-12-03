/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant.jmx;

import java.util.Set;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.apache.catalina.ant.jmx.JMXAccessorTask;
import org.apache.tools.ant.BuildException;

public class JMXAccessorQueryTask
extends JMXAccessorTask {
    private boolean attributebinding = false;

    public boolean isAttributebinding() {
        return this.attributebinding;
    }

    public void setAttributebinding(boolean attributeBinding) {
        this.attributebinding = attributeBinding;
    }

    @Override
    public String jmxExecute(MBeanServerConnection jmxServerConnection) throws Exception {
        if (this.getName() == null) {
            throw new BuildException("Must specify a 'name'");
        }
        return this.jmxQuery(jmxServerConnection, this.getName());
    }

    protected String jmxQuery(MBeanServerConnection jmxServerConnection, String qry) {
        String isError = null;
        Set<ObjectName> names = null;
        String resultproperty = this.getResultproperty();
        try {
            names = jmxServerConnection.queryNames(new ObjectName(qry), null);
            if (resultproperty != null) {
                this.setProperty(resultproperty + ".Length", Integer.toString(names.size()));
            }
        }
        catch (Exception e) {
            if (this.isEcho()) {
                this.handleErrorOutput(e.getMessage());
            }
            return "Can't query mbeans " + qry;
        }
        if (resultproperty != null) {
            int oindex = 0;
            String pname = null;
            for (ObjectName oname : names) {
                pname = resultproperty + "." + Integer.toString(oindex) + ".";
                ++oindex;
                this.setProperty(pname + "Name", oname.toString());
                if (!this.isAttributebinding()) continue;
                this.bindAttributes(jmxServerConnection, pname, oname);
            }
        }
        return isError;
    }

    protected void bindAttributes(MBeanServerConnection jmxServerConnection, String pname, ObjectName oname) {
        try {
            MBeanInfo minfo = jmxServerConnection.getMBeanInfo(oname);
            MBeanAttributeInfo[] attrs = minfo.getAttributes();
            Object value = null;
            for (MBeanAttributeInfo attr : attrs) {
                String attName;
                if (!attr.isReadable() || (attName = attr.getName()).indexOf(61) >= 0 || attName.indexOf(58) >= 0 || attName.indexOf(32) >= 0) continue;
                try {
                    value = jmxServerConnection.getAttribute(oname, attName);
                }
                catch (Exception e) {
                    if (!this.isEcho()) continue;
                    this.handleErrorOutput("Error getting attribute " + oname + " " + pname + attName + " " + e.toString());
                    continue;
                }
                if (value == null || "modelerType".equals(attName)) continue;
                this.createProperty(pname + attName, value);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

