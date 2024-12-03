/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts.action.ActionMessage
 *  org.apache.struts.action.ActionMessages
 *  org.apache.struts.util.MessageResources
 */
package org.apache.velocity.tools.struts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.struts.MessageResourcesTool;
import org.apache.velocity.tools.struts.StrutsUtils;

@DefaultKey(value="messages")
@ValidScope(value={"request"})
public class ActionMessagesTool
extends MessageResourcesTool {
    protected ActionMessages actionMsgs;

    protected ActionMessages getActionMessages() {
        if (this.actionMsgs == null) {
            this.actionMsgs = StrutsUtils.getMessages(this.request);
        }
        return this.actionMsgs;
    }

    public boolean exist() {
        if (this.getActionMessages() == null) {
            return false;
        }
        return !this.actionMsgs.isEmpty();
    }

    public boolean exist(String property) {
        if (this.getActionMessages() == null) {
            return false;
        }
        return this.actionMsgs.size(property) > 0;
    }

    public int getSize() {
        if (this.getActionMessages() == null) {
            return 0;
        }
        return this.actionMsgs.size();
    }

    public int getSize(String property) {
        if (this.getActionMessages() == null) {
            return 0;
        }
        return this.actionMsgs.size(property);
    }

    public List getGlobal() {
        return this.get(this.getGlobalName());
    }

    public List getAll() {
        return this.get(null);
    }

    public List getAll(String bundle) {
        return this.get(null, bundle);
    }

    public List get(String property) {
        return this.get(property, null);
    }

    public List get(String property, String bundle) {
        ActionMessages actionMsgs = this.getActionMessages();
        if (actionMsgs == null || actionMsgs.isEmpty()) {
            return null;
        }
        Iterator msgs = property == null ? actionMsgs.get() : actionMsgs.get(property);
        if (!msgs.hasNext()) {
            return null;
        }
        MessageResources res = this.getResources(bundle);
        ArrayList<String> list = new ArrayList<String>();
        while (msgs.hasNext()) {
            ActionMessage msg = (ActionMessage)msgs.next();
            String message = null;
            if (res != null && msg.isResource() && (message = res.getMessage(this.getLocale(), msg.getKey(), msg.getValues())) == null) {
                this.LOG.warn((Object)("ActionMessagesTool : Message for key " + msg.getKey() + " could not be found in message resources."));
            }
            if (message == null) {
                message = msg.getKey();
            }
            list.add(message);
        }
        return list;
    }

    public String getGlobalName() {
        return "org.apache.struts.action.GLOBAL_MESSAGE";
    }
}

