/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class Matches
extends ProjectComponent
implements Condition {
    private String string;
    private boolean caseSensitive = true;
    private boolean multiLine = false;
    private boolean singleLine = false;
    private RegularExpression regularExpression;

    public void setString(String string) {
        this.string = string;
    }

    public void setPattern(String pattern) {
        if (this.regularExpression != null) {
            throw new BuildException("Only one regular expression is allowed.");
        }
        this.regularExpression = new RegularExpression();
        this.regularExpression.setPattern(pattern);
    }

    public void addRegexp(RegularExpression regularExpression) {
        if (this.regularExpression != null) {
            throw new BuildException("Only one regular expression is allowed.");
        }
        this.regularExpression = regularExpression;
    }

    public void setCasesensitive(boolean b) {
        this.caseSensitive = b;
    }

    public void setMultiline(boolean b) {
        this.multiLine = b;
    }

    public void setSingleLine(boolean b) {
        this.singleLine = b;
    }

    @Override
    public boolean eval() throws BuildException {
        if (this.string == null) {
            throw new BuildException("Parameter string is required in matches.");
        }
        if (this.regularExpression == null) {
            throw new BuildException("Missing pattern in matches.");
        }
        int options = RegexpUtil.asOptions(this.caseSensitive, this.multiLine, this.singleLine);
        Regexp regexp = this.regularExpression.getRegexp(this.getProject());
        return regexp.matches(this.string, options);
    }
}

