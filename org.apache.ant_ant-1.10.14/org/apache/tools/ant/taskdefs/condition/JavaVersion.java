/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.util.DeweyDecimal;
import org.apache.tools.ant.util.JavaEnvUtils;

public class JavaVersion
implements Condition {
    private String atMost = null;
    private String atLeast = null;
    private String exactly = null;

    @Override
    public boolean eval() throws BuildException {
        this.validate();
        DeweyDecimal actual = JavaEnvUtils.getParsedJavaVersion();
        if (null != this.atLeast) {
            return actual.isGreaterThanOrEqual(new DeweyDecimal(this.atLeast));
        }
        if (null != this.exactly) {
            return actual.isEqual(new DeweyDecimal(this.exactly));
        }
        if (this.atMost != null) {
            return actual.isLessThanOrEqual(new DeweyDecimal(this.atMost));
        }
        return false;
    }

    private void validate() throws BuildException {
        if (this.atLeast != null && this.exactly != null && this.atMost != null) {
            throw new BuildException("Only one of atleast or atmost or exactly may be set.");
        }
        if (null == this.atLeast && null == this.exactly && this.atMost == null) {
            throw new BuildException("One of atleast or atmost or exactly must be set.");
        }
        if (this.atLeast != null) {
            try {
                new DeweyDecimal(this.atLeast);
            }
            catch (NumberFormatException e) {
                throw new BuildException("The 'atleast' attribute is not a Dewey Decimal eg 1.1.0 : " + this.atLeast);
            }
        }
        if (this.atMost != null) {
            try {
                new DeweyDecimal(this.atMost);
            }
            catch (NumberFormatException e) {
                throw new BuildException("The 'atmost' attribute is not a Dewey Decimal eg 1.1.0 : " + this.atMost);
            }
        }
        try {
            new DeweyDecimal(this.exactly);
        }
        catch (NumberFormatException e) {
            throw new BuildException("The 'exactly' attribute is not a Dewey Decimal eg 1.1.0 : " + this.exactly);
        }
    }

    public String getAtLeast() {
        return this.atLeast;
    }

    public void setAtLeast(String atLeast) {
        this.atLeast = atLeast;
    }

    public String getAtMost() {
        return this.atMost;
    }

    public void setAtMost(String atMost) {
        this.atMost = atMost;
    }

    public String getExactly() {
        return this.exactly;
    }

    public void setExactly(String exactly) {
        this.exactly = exactly;
    }
}

