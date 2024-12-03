/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 */
package com.atlassian.confluence.plugins.gatekeeper.controllers;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.plugins.gatekeeper.controllers.AbstractPermissionsAction;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationLevel;

@ReadOnlyAccessAllowed
public class SpacePermissionsAction
extends AbstractPermissionsAction {
    public SpacePermissionsAction() {
        super(EvaluationLevel.EVALUATE_SPACE);
    }

    public void setKey(String key) {
        super.setKey(key);
        this.setSpaceKey(key);
    }
}

