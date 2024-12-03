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

public final class LineContains
extends BaseParamFilterReader
implements ChainableReader {
    private static final String CONTAINS_KEY = "contains";
    private static final String NEGATE_KEY = "negate";
    private Vector<String> contains = new Vector();
    private String line = null;
    private boolean negate = false;
    private boolean matchAny = false;

    public LineContains() {
    }

    public LineContains(Reader in) {
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
            int containsSize = this.contains.size();
            this.line = this.readLine();
            while (this.line != null) {
                boolean matches = true;
                for (int i = 0; i < containsSize; ++i) {
                    String containsStr = this.contains.elementAt(i);
                    matches = this.line.contains(containsStr);
                    if (!matches) {
                        if (!this.matchAny) break;
                        continue;
                    }
                    if (this.matchAny) break;
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

    public void addConfiguredContains(Contains contains) {
        this.contains.addElement(contains.getValue());
    }

    public void setNegate(boolean b) {
        this.negate = b;
    }

    public boolean isNegated() {
        return this.negate;
    }

    public void setMatchAny(boolean matchAny) {
        this.matchAny = matchAny;
    }

    public boolean isMatchAny() {
        return this.matchAny;
    }

    private void setContains(Vector<String> contains) {
        this.contains = contains;
    }

    private Vector<String> getContains() {
        return this.contains;
    }

    @Override
    public Reader chain(Reader rdr) {
        LineContains newFilter = new LineContains(rdr);
        newFilter.setContains(this.getContains());
        newFilter.setNegate(this.isNegated());
        newFilter.setMatchAny(this.isMatchAny());
        return newFilter;
    }

    private void initialize() {
        Parameter[] params = this.getParameters();
        if (params != null) {
            for (Parameter param : params) {
                if (CONTAINS_KEY.equals(param.getType())) {
                    this.contains.addElement(param.getValue());
                    continue;
                }
                if (!NEGATE_KEY.equals(param.getType())) continue;
                this.setNegate(Project.toBoolean(param.getValue()));
            }
        }
    }

    public static class Contains {
        private String value;

        public final void setValue(String contains) {
            this.value = contains;
        }

        public final String getValue() {
            return this.value;
        }
    }
}

