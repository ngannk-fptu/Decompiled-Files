/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.ajax.AjaxResponse
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.labels.service.AddLabelsCommand;
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.ajax.AjaxResponse;
import com.google.common.collect.Maps;
import java.util.HashMap;

public class AddLabelAjaxAction
extends ConfluenceActionSupport
implements Beanable {
    private Object bean;
    private LabelsService labelsService;
    private String entityIdString;
    private String labelString;
    private String entityType;
    private String spaceKey;

    public String addLabel() throws Exception {
        long entityId = Long.parseLong(this.entityIdString);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        AddLabelsCommand command = this.labelsService.newAddLabelCommand(this.labelString, user, entityId, this.entityType);
        if (!command.isValid()) {
            ValidationError error = command.getValidationErrors().iterator().next();
            this.bean = AjaxResponse.failure((String)this.getText(error.getMessageKey(), error.getArgs()));
            return "error";
        }
        command.execute();
        HashMap contextMap = Maps.newHashMap();
        contextMap.put("labels", command.getAddedLabels());
        contextMap.put("spaceKey", this.spaceKey);
        contextMap.put("success", true);
        this.bean = contextMap;
        return "success";
    }

    public void setLabelsService(LabelsService labelsService) {
        this.labelsService = labelsService;
    }

    @Override
    public Object getBean() {
        return this.bean;
    }

    public void setEntityIdString(String entityIdString) {
        this.entityIdString = entityIdString;
    }

    public void setLabelString(String labelString) {
        this.labelString = labelString;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }
}

