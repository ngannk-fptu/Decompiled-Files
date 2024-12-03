/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Filter<T> {
    private List<Pattern> includeList;
    private List<Pattern> excludeList;

    protected Filter() {
    }

    protected Filter(String filter) {
        this.includeList = this.parseFilter(filter);
        this.excludeList = this.parseFilter(null);
    }

    private List<Pattern> parseFilter(String filter) {
        ArrayList<Pattern> result = new ArrayList<Pattern>();
        if (filter != null && !filter.isEmpty()) {
            StringTokenizer st = new StringTokenizer(filter, ",");
            while (st.hasMoreTokens()) {
                String s = st.nextToken().toLowerCase().trim();
                s = s.replace("*", ".*");
                Pattern p = Pattern.compile(s);
                result.add(p);
            }
        }
        return result;
    }

    public List<T> getFilteredList(Collection<T> list) {
        ArrayList<T> result = new ArrayList<T>();
        for (T element : list) {
            if (!this.matches(element)) continue;
            result.add(element);
        }
        return result;
    }

    public boolean isEmptyFilter() {
        return this.includeList.isEmpty() && this.excludeList.isEmpty();
    }

    public abstract boolean matches(T var1);

    protected boolean matches(String s) {
        Matcher m;
        if (this.includeList.isEmpty() && this.excludeList.isEmpty()) {
            return true;
        }
        if (s == null) {
            return false;
        }
        boolean result = false;
        s = s.toLowerCase();
        if (this.includeList.isEmpty()) {
            result = true;
        } else {
            for (Pattern p : this.includeList) {
                m = p.matcher(s);
                if (!m.matches()) continue;
                result = true;
                break;
            }
        }
        for (Pattern p : this.excludeList) {
            m = p.matcher(s);
            System.out.println(m.toString() + " : " + s + " = " + m.matches());
            if (!m.matches()) continue;
            result = false;
            break;
        }
        return result;
    }
}

