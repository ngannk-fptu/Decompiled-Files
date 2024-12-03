/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.ajax.AjaxResponse
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.confluence.labels.service.RemoveLabelCommand;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.ajax.AjaxResponse;
import com.atlassian.user.User;

public class RemoveLabelAjaxAction
extends ConfluenceActionSupport
implements Beanable {
    private LabelsService labelsService;
    private Object bean;
    private String entityIdString;
    private String labelIdString;

    public String removeLabel() throws Exception {
        long entityId;
        try {
            entityId = Long.parseLong(this.entityIdString);
        }
        catch (NumberFormatException e) {
            this.bean = AjaxResponse.failure((String)(e.getClass() + ": Invalid page ID '" + this.entityIdString + "'"));
            return "error";
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        RemoveLabelCommand command = this.labelsService.newRemoveLabelCommand(this.labelIdString, (User)user, entityId);
        if (!command.isValid()) {
            ValidationError error = command.getValidationErrors().iterator().next();
            this.bean = AjaxResponse.failure((String)this.getText(error.getMessageKey(), error.getArgs()));
            return "error";
        }
        command.execute();
        this.bean = AjaxResponse.success((String)this.labelIdString);
        return "success";
    }

    public void setLabelsService(LabelsService labelsService) {
        this.labelsService = labelsService;
    }

    @Override
    public Object getBean() {
        return this.bean;
    }

    public void setLabelIdString(String labelIdString) {
        this.labelIdString = labelIdString;
    }

    public void setEntityIdString(String entityIdString) {
        this.entityIdString = entityIdString;
    }
}

