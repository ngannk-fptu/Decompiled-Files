/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant.jmx;

import org.apache.catalina.ant.jmx.JMXAccessorConditionBase;
import org.apache.tools.ant.BuildException;

public class JMXAccessorEqualsCondition
extends JMXAccessorConditionBase {
    public boolean eval() {
        String value = this.getValue();
        if (value == null) {
            throw new BuildException("value attribute is not set");
        }
        if (this.getName() == null || this.getAttribute() == null) {
            throw new BuildException("Must specify an MBean name and attribute for equals condition");
        }
        String jmxValue = this.accessJMXValue();
        if (jmxValue != null) {
            return jmxValue.equals(value);
        }
        return false;
    }
}

