/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class Equals
implements Condition {
    private static final int REQUIRED = 3;
    private Object arg1;
    private Object arg2;
    private boolean trim = false;
    private boolean caseSensitive = true;
    private int args;
    private boolean forcestring = false;

    public void setArg1(Object arg1) {
        if (arg1 instanceof String) {
            this.setArg1((String)arg1);
        } else {
            this.setArg1Internal(arg1);
        }
    }

    public void setArg1(String arg1) {
        this.setArg1Internal(arg1);
    }

    private void setArg1Internal(Object arg1) {
        this.arg1 = arg1;
        this.args |= 1;
    }

    public void setArg2(Object arg2) {
        if (arg2 instanceof String) {
            this.setArg2((String)arg2);
        } else {
            this.setArg2Internal(arg2);
        }
    }

    public void setArg2(String arg2) {
        this.setArg2Internal(arg2);
    }

    private void setArg2Internal(Object arg2) {
        this.arg2 = arg2;
        this.args |= 2;
    }

    public void setTrim(boolean b) {
        this.trim = b;
    }

    public void setCasesensitive(boolean b) {
        this.caseSensitive = b;
    }

    public void setForcestring(boolean forcestring) {
        this.forcestring = forcestring;
    }

    @Override
    public boolean eval() throws BuildException {
        if ((this.args & 3) != 3) {
            throw new BuildException("both arg1 and arg2 are required in equals");
        }
        if (this.arg1 == this.arg2 || this.arg1 != null && this.arg1.equals(this.arg2)) {
            return true;
        }
        if (this.forcestring) {
            this.arg1 = this.arg1 == null || this.arg1 instanceof String ? this.arg1 : this.arg1.toString();
            Object object = this.arg2 = this.arg2 == null || this.arg2 instanceof String ? this.arg2 : this.arg2.toString();
        }
        if (this.arg1 instanceof String && this.trim) {
            this.arg1 = ((String)this.arg1).trim();
        }
        if (this.arg2 instanceof String && this.trim) {
            this.arg2 = ((String)this.arg2).trim();
        }
        if (this.arg1 instanceof String && this.arg2 instanceof String) {
            String s1 = (String)this.arg1;
            String s2 = (String)this.arg2;
            return this.caseSensitive ? s1.equals(s2) : s1.equalsIgnoreCase(s2);
        }
        return false;
    }
}

