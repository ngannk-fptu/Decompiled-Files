/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.selectors.BaseExtendSelector;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class FilenameSelector
extends BaseExtendSelector {
    public static final String NAME_KEY = "name";
    public static final String CASE_KEY = "casesensitive";
    public static final String NEGATE_KEY = "negate";
    public static final String REGEX_KEY = "regex";
    private String pattern = null;
    private String regex = null;
    private boolean casesensitive = true;
    private boolean negated = false;
    private RegularExpression reg;
    private Regexp expression;

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("{filenameselector name: ");
        if (this.pattern != null) {
            buf.append(this.pattern);
        }
        if (this.regex != null) {
            buf.append(this.regex).append(" [as regular expression]");
        }
        buf.append(" negate: ").append(this.negated);
        buf.append(" casesensitive: ").append(this.casesensitive);
        buf.append("}");
        return buf.toString();
    }

    public void setName(String pattern) {
        if ((pattern = pattern.replace('/', File.separatorChar).replace('\\', File.separatorChar)).endsWith(File.separator)) {
            pattern = pattern + "**";
        }
        this.pattern = pattern;
    }

    public void setRegex(String pattern) {
        this.regex = pattern;
        this.reg = null;
    }

    public void setCasesensitive(boolean casesensitive) {
        this.casesensitive = casesensitive;
    }

    public void setNegate(boolean negated) {
        this.negated = negated;
    }

    @Override
    public void setParameters(Parameter ... parameters) {
        super.setParameters(parameters);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                String paramname = parameter.getName();
                if (NAME_KEY.equalsIgnoreCase(paramname)) {
                    this.setName(parameter.getValue());
                    continue;
                }
                if (CASE_KEY.equalsIgnoreCase(paramname)) {
                    this.setCasesensitive(Project.toBoolean(parameter.getValue()));
                    continue;
                }
                if (NEGATE_KEY.equalsIgnoreCase(paramname)) {
                    this.setNegate(Project.toBoolean(parameter.getValue()));
                    continue;
                }
                if (REGEX_KEY.equalsIgnoreCase(paramname)) {
                    this.setRegex(parameter.getValue());
                    continue;
                }
                this.setError("Invalid parameter " + paramname);
            }
        }
    }

    @Override
    public void verifySettings() {
        if (this.pattern == null && this.regex == null) {
            this.setError("The name or regex attribute is required");
        } else if (this.pattern != null && this.regex != null) {
            this.setError("Only one of name and regex attribute is allowed");
        }
    }

    @Override
    public boolean isSelected(File basedir, String filename, File file) {
        this.validate();
        if (this.pattern != null) {
            return SelectorUtils.matchPath(this.pattern, filename, this.casesensitive) == !this.negated;
        }
        if (this.reg == null) {
            this.reg = new RegularExpression();
            this.reg.setPattern(this.regex);
            this.expression = this.reg.getRegexp(this.getProject());
        }
        int options = RegexpUtil.asOptions(this.casesensitive);
        return this.expression.matches(filename, options) == !this.negated;
    }
}

