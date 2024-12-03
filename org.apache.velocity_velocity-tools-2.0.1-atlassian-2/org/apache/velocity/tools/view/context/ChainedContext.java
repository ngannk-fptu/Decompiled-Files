/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 */
package org.apache.velocity.tools.view.context;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.ViewToolContext;
import org.apache.velocity.tools.view.context.ViewContext;

@Deprecated
public class ChainedContext
extends ViewToolContext
implements ViewContext {
    private Map<String, Object> oldToolbox;

    public ChainedContext(VelocityEngine velocity, HttpServletRequest request, HttpServletResponse response, ServletContext application) {
        super(velocity, request, response, application);
    }

    public ChainedContext(Context ctx, VelocityEngine velocity, HttpServletRequest request, HttpServletResponse response, ServletContext application) {
        this(velocity, request, response, application);
        if (ctx != null) {
            for (Object key : ctx.getKeys()) {
                String skey = String.valueOf(key);
                this.put(skey, ctx.get(skey));
            }
        }
    }

    public void setToolbox(Map<String, Object> box) {
        this.oldToolbox = box;
    }

    @Override
    public Map<String, Object> getToolbox() {
        if (this.oldToolbox != null) {
            HashMap<String, Object> box = new HashMap<String, Object>(this.oldToolbox);
            box.putAll(super.getToolbox());
            return box;
        }
        return super.getToolbox();
    }

    @Override
    protected Object internalGet(String key) {
        Object o = null;
        if (this.oldToolbox != null && (o = this.oldToolbox.get(key)) != null) {
            return o;
        }
        return super.internalGet(key);
    }
}

