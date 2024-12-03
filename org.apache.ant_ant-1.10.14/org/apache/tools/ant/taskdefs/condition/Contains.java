/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class Contains
implements Condition {
    private String string;
    private String subString;
    private boolean caseSensitive = true;

    public void setString(String string) {
        this.string = string;
    }

    public void setSubstring(String subString) {
        this.subString = subString;
    }

    public void setCasesensitive(boolean b) {
        this.caseSensitive = b;
    }

    @Override
    public boolean eval() throws BuildException {
        if (this.string == null || this.subString == null) {
            throw new BuildException("both string and substring are required in contains");
        }
        return this.caseSensitive ? this.string.contains(this.subString) : this.string.toLowerCase().contains(this.subString.toLowerCase());
    }
}

