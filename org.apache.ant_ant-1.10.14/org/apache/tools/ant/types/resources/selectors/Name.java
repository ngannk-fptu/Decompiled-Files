/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class Name
implements ResourceSelector {
    private String regex = null;
    private String pattern;
    private boolean cs = true;
    private boolean handleDirSep = false;
    private RegularExpression reg;
    private Regexp expression;
    private Project project;

    public void setProject(Project p) {
        this.project = p;
    }

    public void setName(String n) {
        this.pattern = n;
    }

    public String getName() {
        return this.pattern;
    }

    public void setRegex(String r) {
        this.regex = r;
        this.reg = null;
    }

    public String getRegex() {
        return this.regex;
    }

    public void setCaseSensitive(boolean b) {
        this.cs = b;
    }

    public boolean isCaseSensitive() {
        return this.cs;
    }

    public void setHandleDirSep(boolean handleDirSep) {
        this.handleDirSep = handleDirSep;
    }

    public boolean doesHandledirSep() {
        return this.handleDirSep;
    }

    @Override
    public boolean isSelected(Resource r) {
        String n = r.getName();
        if (this.matches(n)) {
            return true;
        }
        String s = r.toString();
        return !s.equals(n) && this.matches(s);
    }

    private boolean matches(String name) {
        if (this.pattern != null) {
            return SelectorUtils.match(this.modify(this.pattern), this.modify(name), this.cs);
        }
        if (this.reg == null) {
            this.reg = new RegularExpression();
            this.reg.setPattern(this.regex);
            this.expression = this.reg.getRegexp(this.project);
        }
        return this.expression.matches(this.modify(name), RegexpUtil.asOptions(this.cs));
    }

    private String modify(String s) {
        if (s == null || !this.handleDirSep || !s.contains("\\")) {
            return s;
        }
        return s.replace('\\', '/');
    }
}

