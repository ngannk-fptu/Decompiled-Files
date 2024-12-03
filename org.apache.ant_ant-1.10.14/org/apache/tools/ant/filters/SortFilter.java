/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;

public final class SortFilter
extends BaseParamFilterReader
implements ChainableReader {
    private static final String REVERSE_KEY = "reverse";
    private static final String COMPARATOR_KEY = "comparator";
    private Comparator<? super String> comparator = null;
    private boolean reverse;
    private List<String> lines;
    private String line = null;
    private Iterator<String> iterator = null;

    public SortFilter() {
    }

    public SortFilter(Reader in) {
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
            if (this.lines == null) {
                this.lines = new ArrayList<String>();
                this.line = this.readLine();
                while (this.line != null) {
                    this.lines.add(this.line);
                    this.line = this.readLine();
                }
                this.sort();
                this.iterator = this.lines.iterator();
            }
            if (this.iterator.hasNext()) {
                this.line = this.iterator.next();
            } else {
                this.line = null;
                this.lines = null;
                this.iterator = null;
            }
            if (this.line != null) {
                return this.read();
            }
        }
        return ch;
    }

    @Override
    public Reader chain(Reader rdr) {
        SortFilter newFilter = new SortFilter(rdr);
        newFilter.setReverse(this.isReverse());
        newFilter.setComparator(this.getComparator());
        newFilter.setInitialized(true);
        return newFilter;
    }

    public boolean isReverse() {
        return this.reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public Comparator<? super String> getComparator() {
        return this.comparator;
    }

    public void setComparator(Comparator<? super String> comparator) {
        this.comparator = comparator;
    }

    public void add(Comparator<? super String> comparator) {
        if (this.comparator != null && comparator != null) {
            throw new BuildException("can't have more than one comparator");
        }
        this.setComparator(comparator);
    }

    private void initialize() {
        Parameter[] params = this.getParameters();
        if (params != null) {
            for (Parameter param : params) {
                String paramName = param.getName();
                if (REVERSE_KEY.equals(paramName)) {
                    this.setReverse(Boolean.parseBoolean(param.getValue()));
                    continue;
                }
                if (!COMPARATOR_KEY.equals(paramName)) continue;
                try {
                    String className = param.getValue();
                    Comparator comparatorInstance = (Comparator)Class.forName(className).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    this.setComparator(comparatorInstance);
                }
                catch (ClassCastException e) {
                    throw new BuildException("Value of comparator attribute should implement java.util.Comparator interface");
                }
                catch (Exception e) {
                    throw new BuildException(e);
                }
            }
        }
    }

    private void sort() {
        if (this.comparator == null) {
            if (this.isReverse()) {
                this.lines.sort(Comparator.reverseOrder());
            } else {
                Collections.sort(this.lines);
            }
        } else {
            this.lines.sort(this.comparator);
        }
    }
}

