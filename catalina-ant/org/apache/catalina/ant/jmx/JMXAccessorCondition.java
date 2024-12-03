/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant.jmx;

import org.apache.catalina.ant.jmx.JMXAccessorConditionBase;
import org.apache.tools.ant.BuildException;

public class JMXAccessorCondition
extends JMXAccessorConditionBase {
    private String operation = "==";
    private String type = "long";
    private String unlessCondition;
    private String ifCondition;

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIf() {
        return this.ifCondition;
    }

    public void setIf(String c) {
        this.ifCondition = c;
    }

    public String getUnless() {
        return this.unlessCondition;
    }

    public void setUnless(String c) {
        this.unlessCondition = c;
    }

    protected boolean testIfCondition() {
        if (this.ifCondition == null || this.ifCondition.isEmpty()) {
            return true;
        }
        return this.getProject().getProperty(this.ifCondition) != null;
    }

    protected boolean testUnlessCondition() {
        if (this.unlessCondition == null || "".equals(this.unlessCondition)) {
            return true;
        }
        return this.getProject().getProperty(this.unlessCondition) == null;
    }

    public boolean eval() {
        String value = this.getValue();
        if (this.operation == null) {
            throw new BuildException("operation attribute is not set");
        }
        if (value == null) {
            throw new BuildException("value attribute is not set");
        }
        if (this.getName() == null || this.getAttribute() == null) {
            throw new BuildException("Must specify an MBean name and attribute for condition");
        }
        if (this.testIfCondition() && this.testUnlessCondition()) {
            String jmxValue = this.accessJMXValue();
            if (jmxValue != null) {
                String op = this.getOperation();
                if ("==".equals(op)) {
                    return jmxValue.equals(value);
                }
                if ("!=".equals(op)) {
                    return !jmxValue.equals(value);
                }
                if ("long".equals(this.type)) {
                    long jvalue = Long.parseLong(jmxValue);
                    long lvalue = Long.parseLong(value);
                    if (">".equals(op)) {
                        return jvalue > lvalue;
                    }
                    if (">=".equals(op)) {
                        return jvalue >= lvalue;
                    }
                    if ("<".equals(op)) {
                        return jvalue < lvalue;
                    }
                    if ("<=".equals(op)) {
                        return jvalue <= lvalue;
                    }
                } else if ("double".equals(this.type)) {
                    double jvalue = Double.parseDouble(jmxValue);
                    double dvalue = Double.parseDouble(value);
                    if (">".equals(op)) {
                        return jvalue > dvalue;
                    }
                    if (">=".equals(op)) {
                        return jvalue >= dvalue;
                    }
                    if ("<".equals(op)) {
                        return jvalue < dvalue;
                    }
                    if ("<=".equals(op)) {
                        return jvalue <= dvalue;
                    }
                }
            }
            return false;
        }
        return true;
    }
}

