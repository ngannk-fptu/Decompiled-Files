/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts.action.ActionMessages
 */
package org.apache.velocity.tools.struts;

import org.apache.struts.action.ActionMessages;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.struts.ActionMessagesTool;
import org.apache.velocity.tools.struts.StrutsUtils;

@DefaultKey(value="errors")
@ValidScope(value={"request"})
public class ErrorsTool
extends ActionMessagesTool {
    @Override
    protected ActionMessages getActionMessages() {
        if (this.actionMsgs == null) {
            this.actionMsgs = StrutsUtils.getErrors(this.request);
        }
        return this.actionMsgs;
    }

    public String getMsgs() {
        return this.getMsgs(null, null);
    }

    public String getMsgs(String property) {
        return this.getMsgs(property, null);
    }

    public String getMsgs(String property, String bundle) {
        return StrutsUtils.errorMarkup(property, bundle, this.request, this.request.getSession(false), this.application);
    }
}

