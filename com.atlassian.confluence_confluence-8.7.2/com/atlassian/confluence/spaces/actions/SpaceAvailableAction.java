/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class SpaceAvailableAction
extends AbstractSpaceAction
implements Beanable {
    private boolean available;
    private String message;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.available = true;
        if (StringUtils.isBlank((CharSequence)this.key)) {
            this.message = this.getText("space.key.empty");
            this.available = false;
        } else if (!Space.isValidGlobalSpaceKey(this.key)) {
            this.message = this.getText("space.key.invalid");
            this.available = false;
        } else if (this.spaceManager.getSpace(this.key) != null) {
            this.message = this.getText("space.key.exists");
            this.available = false;
        }
        return "success";
    }

    @Override
    public Object getBean() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("available", this.available);
        result.put("message", this.message);
        return result;
    }
}

