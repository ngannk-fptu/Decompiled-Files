/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.ReferenceMode
 */
package com.atlassian.plugin;

import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.ReferenceMode;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class XmlPluginArtifact
implements PluginArtifact {
    private final File xmlFile;

    public XmlPluginArtifact(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public boolean doesResourceExist(String name) {
        return false;
    }

    public InputStream getResourceAsStream(String name) {
        return null;
    }

    public String getName() {
        return this.xmlFile.getName();
    }

    public InputStream getInputStream() {
        try {
            return new BufferedInputStream(new FileInputStream(this.xmlFile));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find XML file for eading: " + this.xmlFile, e);
        }
    }

    public File toFile() {
        return this.xmlFile;
    }

    public boolean containsJavaExecutableCode() {
        return false;
    }

    public boolean containsSpringContext() {
        return false;
    }

    public ReferenceMode getReferenceMode() {
        return ReferenceMode.FORBID_REFERENCE;
    }
}

