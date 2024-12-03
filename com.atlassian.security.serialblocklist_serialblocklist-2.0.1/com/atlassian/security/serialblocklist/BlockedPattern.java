/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.serialblocklist;

import java.util.ArrayList;
import java.util.List;

public class BlockedPattern {
    private List<String> prefixes = new ArrayList<String>();
    private List<String> parentClasses = new ArrayList<String>();
    private List<String> suffixes = new ArrayList<String>();

    public List<String> getPrefixes() {
        return this.prefixes;
    }

    public void setPrefixes(List<String> prefixes) {
        if (prefixes != null) {
            this.prefixes.addAll(prefixes);
        }
    }

    public List<String> getParentClasses() {
        return this.parentClasses;
    }

    public void setParentClasses(List<String> parentClasses) {
        if (parentClasses != null) {
            this.parentClasses.addAll(parentClasses);
        }
    }

    public List<String> getSuffixes() {
        return this.suffixes;
    }

    public void setSuffixes(List<String> suffixes) {
        if (suffixes != null) {
            this.suffixes.addAll(suffixes);
        }
    }

    boolean isEmpty() {
        return this.prefixes.isEmpty() && this.parentClasses.isEmpty() && this.suffixes.isEmpty();
    }

    public String toString() {
        return "BlockedPattern{prefixes=" + this.prefixes + ", parentClasses=" + this.parentClasses + ", suffixes=" + this.suffixes + '}';
    }
}

