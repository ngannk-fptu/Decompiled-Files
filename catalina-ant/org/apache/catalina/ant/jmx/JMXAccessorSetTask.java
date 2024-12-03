/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant.jmx;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.apache.catalina.ant.jmx.JMXAccessorTask;
import org.apache.tools.ant.BuildException;

public class JMXAccessorSetTask
extends JMXAccessorTask {
    private String attribute;
    private String value;
    private String type;
    private boolean convert = false;

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

    public String getType() {
        return this.type;
    }

    public void setType(String valueType) {
        this.type = valueType;
    }

    public boolean isConvert() {
        return this.convert;
    }

    public void setConvert(boolean convert) {
        this.convert = convert;
    }

    @Override
    public String jmxExecute(MBeanServerConnection jmxServerConnection) throws Exception {
        if (this.getName() == null) {
            throw new BuildException("Must specify a 'name'");
        }
        if (this.attribute == null || this.value == null) {
            throw new BuildException("Must specify a 'attribute' and 'value' for set");
        }
        return this.jmxSet(jmxServerConnection, this.getName());
    }

    protected String jmxSet(MBeanServerConnection jmxServerConnection, String name) throws Exception {
        Object realValue;
        if (this.type != null) {
            realValue = this.convertStringToType(this.value, this.type);
        } else if (this.isConvert()) {
            String mType = this.getMBeanAttributeType(jmxServerConnection, name, this.attribute);
            realValue = this.convertStringToType(this.value, mType);
        } else {
            realValue = this.value;
        }
        jmxServerConnection.setAttribute(new ObjectName(name), new Attribute(this.attribute, realValue));
        return null;
    }

    protected String getMBeanAttributeType(MBeanServerConnection jmxServerConnection, String name, String attribute) throws Exception {
        ObjectName oname = new ObjectName(name);
        String mattrType = null;
        MBeanInfo minfo = jmxServerConnection.getMBeanInfo(oname);
        MBeanAttributeInfo[] attrs = minfo.getAttributes();
        for (int i = 0; mattrType == null && i < attrs.length; ++i) {
            if (!attribute.equals(attrs[i].getName())) continue;
            mattrType = attrs[i].getType();
        }
        return mattrType;
    }
}

