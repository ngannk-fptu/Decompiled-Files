/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util.filter;

import javax.xml.namespace.QName;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.util.filter.AbstractParseFilter;

public class CompoundParseFilter
extends AbstractParseFilter
implements ParseFilter {
    private static final long serialVersionUID = -7871289035422204698L;
    protected final Condition condition;
    protected final ParseFilter[] filters;

    public CompoundParseFilter(Condition condition, ParseFilter ... filters) {
        this.filters = filters;
        this.condition = condition;
    }

    public CompoundParseFilter(ParseFilter ... filters) {
        this(Condition.ACCEPTABLE_TO_ANY, filters);
    }

    private ParseFilter[] getFilters() {
        return this.filters;
    }

    public boolean acceptable(QName qname) {
        for (ParseFilter filter : this.getFilters()) {
            switch (this.condition.evaluate(filter.acceptable(qname))) {
                case 1: {
                    return true;
                }
                case -1: {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean acceptable(QName qname, QName attribute) {
        for (ParseFilter filter : this.getFilters()) {
            switch (this.condition.evaluate(filter.acceptable(qname, attribute))) {
                case 1: {
                    return true;
                }
                case -1: {
                    return false;
                }
            }
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Condition {
        ACCEPTABLE_TO_ALL,
        ACCEPTABLE_TO_ANY,
        UNACCEPTABLE_TO_ALL,
        UNACCEPTABLE_TO_ANY;


        byte evaluate(boolean b) {
            if (b) {
                switch (this) {
                    case ACCEPTABLE_TO_ANY: {
                        return 1;
                    }
                    case UNACCEPTABLE_TO_ALL: {
                        return -1;
                    }
                }
            } else {
                switch (this) {
                    case ACCEPTABLE_TO_ALL: {
                        return -1;
                    }
                    case UNACCEPTABLE_TO_ANY: {
                        return 1;
                    }
                }
            }
            return 0;
        }
    }
}

