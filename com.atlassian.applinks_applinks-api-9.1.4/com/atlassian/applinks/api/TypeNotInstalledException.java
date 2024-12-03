/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api;

import java.net.URI;

public class TypeNotInstalledException
extends Exception {
    private final String type;
    private final String name;
    private final URI rpcUrl;

    @Deprecated
    public TypeNotInstalledException(String type) {
        this(type, null, null);
    }

    public TypeNotInstalledException(String type, String name, URI rpcUrl) {
        this.type = type;
        this.name = name;
        this.rpcUrl = rpcUrl;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public URI getRpcUrl() {
        return this.rpcUrl;
    }

    public String getMessageKey() {
        return "applinks.type.not.installed";
    }
}

