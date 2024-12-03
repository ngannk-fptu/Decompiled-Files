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
import com.atlassian.confluence.plugins.highlight.model.CellModification;
import com.atlassian.confluence.plugins.highlight.model.TableModification;
import com.atlassian.confluence.plugins.highlight.service.SelectionValidator;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TableModificationValidator
extends SelectionValidator<TableModification> {
    @Autowired
    public TableModificationValidator(@ComponentImport PermissionManager permissionManager) {
        super(permissionManager);
    }

    @Override
    public SelectionValidator<TableModification> validateModification(TableModification modification) throws SelectionModificationException {
        if (modification.getCellModifications() == null || modification.getCellModifications().isEmpty()) {
            throw new SelectionModificationException(SelectionModificationException.Type.INCORRECT_MODIFICATION, "No content for insert into table");
        }
        for (CellModification cellModification : modification.getCellModifications()) {
            if (cellModification.getRow() < 0) {
                throw new SelectionModificationException(SelectionModificationException.Type.INCORRECT_MODIFICATION, "The index of row for insert into table is wrong: " + cellModification.getRow());
            }
            this.validateModification(cellModification.getXml());
        }
        return this;
    }
}

