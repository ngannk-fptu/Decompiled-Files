/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import java.util.List;
import org.apache.struts2.ServletActionContext;

public abstract class AbstractSpaceAdminAction
extends AbstractSpaceAction
implements SpaceAware {
    @Override
    public String doDefault() throws Exception {
        if (this.getSpace() == null) {
            ServletActionContext.getResponse().sendError(404, "Space not found: " + this.getKey());
            return "ERROR";
        }
        return "input";
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    @Override
    protected List<String> getPermissionTypes() {
        List<String> permissions = super.getPermissionTypes();
        this.addPermissionTypeTo("SETSPACEPERMISSIONS", permissions);
        return permissions;
    }
}

