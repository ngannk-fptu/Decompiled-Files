/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.config;

public class ConfigurationException
extends Exception {
    public ConfigurationException(String s) {
        super(s);
    }

    public ConfigurationException(String s, Throwable t) {
        super(s + " : " + t, t);
    }
}

