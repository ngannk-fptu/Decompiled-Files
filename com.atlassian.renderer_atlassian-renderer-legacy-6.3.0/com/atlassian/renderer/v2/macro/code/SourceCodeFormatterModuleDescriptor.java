/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer.v2.macro.code;

import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.renderer.v2.macro.code.SourceCodeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceCodeFormatterModuleDescriptor
extends AbstractModuleDescriptor {
    private static final Logger log = LoggerFactory.getLogger(SourceCodeFormatterModuleDescriptor.class);
    private SourceCodeFormatter formatter;

    public Object getModule() {
        return this.getFormatter();
    }

    protected SourceCodeFormatter makeFormatterFromClass() {
        try {
            return (SourceCodeFormatter)this.getModuleClass().newInstance();
        }
        catch (Throwable t) {
            log.error("Unable to instantiate code formatter: " + this.getCompleteKey() + " " + t.getMessage());
            return null;
        }
    }

    public SourceCodeFormatter getFormatter() {
        if (this.formatter == null) {
            this.formatter = this.makeFormatterFromClass();
        }
        return this.formatter;
    }
}

