/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HTMLServiceImpl {
    private StorageFormatCleaner storageFormatCleaner;

    @Autowired
    public HTMLServiceImpl(@ComponentImport StorageFormatCleaner storageFormatCleaner) {
        this.storageFormatCleaner = storageFormatCleaner;
    }

    public String clean(String html) {
        if (html == null) {
            return null;
        }
        return this.storageFormatCleaner.cleanQuietly(html);
    }
}

