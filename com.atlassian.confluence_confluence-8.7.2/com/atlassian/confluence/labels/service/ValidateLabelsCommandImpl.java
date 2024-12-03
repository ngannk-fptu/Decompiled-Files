/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.labels.service;

import com.atlassian.confluence.core.service.AbstractServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.service.LabelValidationHelper;
import com.atlassian.confluence.labels.service.ValidateLabelsCommand;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collection;

public class ValidateLabelsCommandImpl
extends AbstractServiceCommand
implements ValidateLabelsCommand {
    private User user;
    private String labelsString;
    Collection<String> labelNames;

    ValidateLabelsCommandImpl(String labelsString, User user) {
        this.labelsString = labelsString;
        this.user = user;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        this.labelNames = LabelUtil.split(this.labelsString.toLowerCase());
        LabelValidationHelper validationHelper = new LabelValidationHelper(validator, this.user, null, null);
        validationHelper.validateLabels(this.labelNames);
    }

    @Override
    protected boolean isAuthorizedInternal() {
        return true;
    }

    @Override
    protected void executeInternal() {
    }

    @Override
    public Collection<Label> getValidLabels() {
        ArrayList<Label> validLabels = new ArrayList<Label>();
        for (String label : this.labelNames) {
            validLabels.add(new Label(label));
        }
        return validLabels;
    }
}

