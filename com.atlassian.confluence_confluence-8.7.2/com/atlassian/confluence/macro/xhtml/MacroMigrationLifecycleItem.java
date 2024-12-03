/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.confluence.content.render.xhtml.migration.SiteMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroMigrationLifecycleItem
implements LifecycleItem {
    private static final Logger log = LoggerFactory.getLogger(MacroMigrationLifecycleItem.class);
    private SiteMigrator xhtmlWikiMarkupMacroSiteMigrator;

    public void startup(LifecycleContext lifecycleContext) throws Exception {
    }

    public void shutdown(LifecycleContext lifecycleContext) throws Exception {
    }

    public void setXhtmlWikiMarkupMacroSiteMigrator(SiteMigrator xhtmlWikiMarkupMacroSiteMigrator) {
        this.xhtmlWikiMarkupMacroSiteMigrator = xhtmlWikiMarkupMacroSiteMigrator;
    }
}

