/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpFactory;

public class RegularExpression
extends DataType {
    public static final String DATA_TYPE_NAME = "regexp";
    private boolean alreadyInit = false;
    private static final RegexpFactory FACTORY = new RegexpFactory();
    private Regexp regexp = null;
    private String myPattern;
    private boolean setPatternPending = false;

    private void init(Project p) {
        if (!this.alreadyInit) {
            this.regexp = FACTORY.newRegexp(p);
            this.alreadyInit = true;
        }
    }

    private void setPattern() {
        if (this.setPatternPending) {
            this.regexp.setPattern(this.myPattern);
            this.setPatternPending = false;
        }
    }

    public void setPattern(String pattern) {
        if (this.regexp == null) {
            this.myPattern = pattern;
            this.setPatternPending = true;
        } else {
            this.regexp.setPattern(pattern);
        }
    }

    public String getPattern(Project p) {
        this.init(p);
        if (this.isReference()) {
            return this.getRef(p).getPattern(p);
        }
        this.setPattern();
        return this.regexp.getPattern();
    }

    public Regexp getRegexp(Project p) {
        this.init(p);
        if (this.isReference()) {
            return this.getRef(p).getRegexp(p);
        }
        this.setPattern();
        return this.regexp;
    }

    public RegularExpression getRef(Project p) {
        return this.getCheckedRef(RegularExpression.class, this.getDataTypeName(), p);
    }
}

