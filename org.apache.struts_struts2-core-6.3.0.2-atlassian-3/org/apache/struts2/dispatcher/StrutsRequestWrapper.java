/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  org.apache.commons.lang3.BooleanUtils
 */
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.BooleanUtils;

public class StrutsRequestWrapper
extends HttpServletRequestWrapper {
    private static final String REQUEST_WRAPPER_GET_ATTRIBUTE = "__requestWrapper.getAttribute";
    private final boolean disableRequestAttributeValueStackLookup;

    public StrutsRequestWrapper(HttpServletRequest req) {
        this(req, false);
    }

    public StrutsRequestWrapper(HttpServletRequest req, boolean disableRequestAttributeValueStackLookup) {
        super(req);
        this.disableRequestAttributeValueStackLookup = disableRequestAttributeValueStackLookup;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getAttribute(String key) {
        boolean alreadyIn;
        if (key == null) {
            throw new NullPointerException("You must specify a key value");
        }
        if (this.disableRequestAttributeValueStackLookup || key.startsWith("javax.servlet")) {
            return super.getAttribute(key);
        }
        ActionContext ctx = ActionContext.getContext();
        Object attribute = super.getAttribute(key);
        if (ctx != null && attribute == null && !(alreadyIn = BooleanUtils.isTrue((Boolean)((Boolean)ctx.get(REQUEST_WRAPPER_GET_ATTRIBUTE)))) && !key.contains("#")) {
            try {
                ctx.put(REQUEST_WRAPPER_GET_ATTRIBUTE, Boolean.TRUE);
                ValueStack stack = ctx.getValueStack();
                if (stack != null) {
                    attribute = stack.findValue(key);
                }
            }
            finally {
                ctx.put(REQUEST_WRAPPER_GET_ATTRIBUTE, Boolean.FALSE);
            }
        }
        return attribute;
    }
}

