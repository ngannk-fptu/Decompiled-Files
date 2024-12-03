/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.CommandActionHelper;
import com.atlassian.confluence.core.service.ServiceCommand;

public abstract class AbstractCommandAction
extends ConfluenceActionSupport
implements Beanable {
    private CommandActionHelper helper;

    @Override
    public boolean isPermitted() {
        return this.getCommandActionHelper().isAuthorized();
    }

    @Override
    public void validate() {
        this.getCommandActionHelper().validate(this);
    }

    public String execute() {
        return this.getCommandActionHelper().execute(this);
    }

    @Override
    public Object getBean() {
        return this.getCommandActionHelper().getCommand();
    }

    protected abstract ServiceCommand createCommand();

    private CommandActionHelper getCommandActionHelper() {
        if (this.helper == null) {
            this.helper = new CommandActionHelper(this.createCommand());
        }
        return this.helper;
    }
}

