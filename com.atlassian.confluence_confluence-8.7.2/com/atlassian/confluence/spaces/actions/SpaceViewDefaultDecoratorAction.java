/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.admin.actions.lookandfeel.ViewDefaultDecoratorAction;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceViewDefaultDecoratorAction
extends ViewDefaultDecoratorAction
implements SpaceAdministrative {
    private static final Logger log = LoggerFactory.getLogger(SpaceViewDefaultDecoratorAction.class);

    @Override
    protected String readDefaultTemplate() {
        return this.getTemplateFromResourceLoader(this.decoratorName, this.decoratorName);
    }
}

