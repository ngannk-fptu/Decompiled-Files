/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

public interface YamlNode {
    public static final String UNNAMED_NODE = "<unnamed>";

    public YamlNode parent();

    public String nodeName();

    public String path();
}

