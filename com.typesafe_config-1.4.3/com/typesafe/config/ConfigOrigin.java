/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import java.net.URL;
import java.util.List;

public interface ConfigOrigin {
    public String description();

    public String filename();

    public URL url();

    public String resource();

    public int lineNumber();

    public List<String> comments();

    public ConfigOrigin withComments(List<String> var1);

    public ConfigOrigin withLineNumber(int var1);
}

