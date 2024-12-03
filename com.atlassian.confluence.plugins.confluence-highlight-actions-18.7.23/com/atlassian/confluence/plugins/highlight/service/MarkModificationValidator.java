/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight.service;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.highlight.SelectionModificationException;
import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import com.atlassian.confluence.plugins.highlight.service.SelectionValidator;
import com.atlassian.confluence.plugins.highlight.service.XMLModificationValidator;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MarkModificationValidator
extends XMLModificationValidator {
    @Autowired
    public MarkModificationValidator(@ComponentImport PermissionManager permissionManager) {
        super(permissionManager);
    }

    protected XMLModificationValidator validatePermissions(AbstractPage abstractPage) {
        return this;
    }

    @Override
    protected SelectionValidator<XMLModification> validatePage(long pageId, AbstractPage abstractPage, long lastFetchTime) throws SelectionModificationException {
        return this;
    }
}

