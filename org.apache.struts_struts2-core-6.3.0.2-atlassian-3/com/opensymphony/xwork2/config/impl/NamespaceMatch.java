/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.impl;

import java.util.Map;

public class NamespaceMatch {
    private String pattern;
    private Map<String, String> variables;

    public NamespaceMatch(String pattern, Map<String, String> variables) {
        this.pattern = pattern;
        this.variables = variables;
    }

    public String getPattern() {
        return this.pattern;
    }

    public Map<String, String> getVariables() {
        return this.variables;
    }
}

