/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public final class LineContainsRegExp
extends BaseParamFilterReader
implements ChainableReader {
    private static final String REGEXP_KEY = "regexp";
    private static final String NEGATE_KEY = "negate";
    private static final String CS_KEY = "casesensitive";
    private Vector<RegularExpression> regexps = new Vector();
    private String line = null;
    private boolean negate = false;
    private int regexpOptions = 0;

    public LineContainsRegExp() {
    }

    public LineContainsRegExp(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch = -1;
        if (this.line != null) {
            ch = this.line.charAt(0);
            this.line = this.line.length() == 1 ? null : this.line.substring(1);
        } else {
            this.line = this.readLine();
            while (this.line != null) {
                boolean matches = true;
                for (RegularExpression regexp : this.regexps) {
                    if (regexp.getRegexp(this.getProject()).matches(this.line, this.regexpOptions)) continue;
                    matches = false;
                    break;
                }
                if (matches ^ this.isNegated()) break;
                this.line = this.readLine();
            }
            if (this.line != null) {
                return this.read();
            }
        }
        return ch;
    }

    public void addConfiguredRegexp(RegularExpression regExp) {
        this.regexps.addElement(regExp);
    }

    private void setRegexps(Vector<RegularExpression> regexps) {
        this.regexps = regexps;
    }

    private Vector<RegularExpression> getRegexps() {
        return this.regexps;
    }

    @Override
    public Reader chain(Reader rdr) {
        LineContainsRegExp newFilter = new LineContainsRegExp(rdr);
        newFilter.setRegexps(this.getRegexps());
        newFilter.setNegate(this.isNegated());
        newFilter.setCaseSensitive(!RegexpUtil.hasFlag(this.regexpOptions, 256));
        return newFilter;
    }

    public void setNegate(boolean b) {
        this.negate = b;
    }

    public void setCaseSensitive(boolean b) {
        this.regexpOptions = RegexpUtil.asOptions(b);
    }

    public boolean isNegated() {
        return this.negate;
    }

    public void setRegexp(String pattern) {
        RegularExpression regexp = new RegularExpression();
        regexp.setPattern(pattern);
        this.regexps.addElement(regexp);
    }

    private void initialize() {
        Parameter[] params = this.getParameters();
        if (params != null) {
            for (Parameter param : params) {
                if (REGEXP_KEY.equals(param.getType())) {
                    this.setRegexp(param.getValue());
                    continue;
                }
                if (NEGATE_KEY.equals(param.getType())) {
                    this.setNegate(Project.toBoolean(param.getValue()));
                    continue;
                }
                if (!CS_KEY.equals(param.getType())) continue;
                this.setCaseSensitive(Project.toBoolean(param.getValue()));
            }
        }
    }
}

