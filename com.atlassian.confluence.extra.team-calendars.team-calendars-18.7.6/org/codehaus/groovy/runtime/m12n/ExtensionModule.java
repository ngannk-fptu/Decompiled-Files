/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.m12n;

import groovy.lang.MetaMethod;
import java.util.List;

public abstract class ExtensionModule {
    private final String name;
    private final String version;

    public ExtensionModule(String moduleName, String moduleVersion) {
        this.name = moduleName;
        this.version = moduleVersion;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public abstract List<MetaMethod> getMetaMethods();

    public String toString() {
        StringBuilder sb = new StringBuilder("ExtensionModule{");
        sb.append("name='").append(this.name).append('\'');
        sb.append(", version='").append(this.version).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

