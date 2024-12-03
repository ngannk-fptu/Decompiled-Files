/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight.service;

import com.atlassian.confluence.plugins.highlight.SelectionModificationException;
import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import com.atlassian.confluence.plugins.highlight.service.SelectionValidator;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="xmlModificationValidator")
public class XMLModificationValidator
extends SelectionValidator<XMLModification> {
    @Autowired
    public XMLModificationValidator(@ComponentImport PermissionManager permissionManager) {
        super(permissionManager);
    }

    @Override
    SelectionValidator<XMLModification> validateModification(XMLModification modification) throws SelectionModificationException {
        this.validateModification(modification.getXml());
        return this;
    }
}

