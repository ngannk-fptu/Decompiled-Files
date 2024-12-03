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
import com.atlassian.confluence.labels.service.LabelsService;
import com.atlassian.confluence.labels.service.ValidateLabelsCommand;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.ajax.AjaxResponse;
import com.google.common.collect.Maps;
import java.util.HashMap;

public class ValidateLabelAjaxAction
extends ConfluenceActionSupport
implements Beanable {
    private Object bean;
    private LabelsService labelsService;
    private String labelString;

    public String validateLabel() throws Exception {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        ValidateLabelsCommand command = this.labelsService.newValidateLabelCommand(this.labelString, user);
        if (!command.isValid()) {
            ValidationError error = command.getValidationErrors().iterator().next();
            this.bean = AjaxResponse.failure((String)this.getText(error.getMessageKey(), error.getArgs()));
            return "error";
        }
        HashMap contextMap = Maps.newHashMap();
        contextMap.put("labels", command.getValidLabels());
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

    public String getLabelString() {
        return this.labelString;
    }

    public void setLabelString(String labelString) {
        this.labelString = labelString;
    }
}

