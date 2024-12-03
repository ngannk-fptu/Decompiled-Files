/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.assembler;

import com.sun.xml.ws.assembler.MetroConfigName;

public class MetroConfigNameImpl
implements MetroConfigName {
    private final String defaultFileName;
    private final String appFileName;

    public MetroConfigNameImpl(String defaultFileName, String appFileName) {
        this.defaultFileName = defaultFileName;
        this.appFileName = appFileName;
    }

    @Override
    public String getDefaultFileName() {
        return this.defaultFileName;
    }

    @Override
    public String getAppFileName() {
        return this.appFileName;
    }
}

