/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.tools.view.context.ChainedContext
 *  org.apache.velocity.tools.view.servlet.ServletToolboxManager
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.web.servlet.view.velocity;

import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.apache.velocity.tools.view.servlet.ServletToolboxManager;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.view.velocity.VelocityView;

public class VelocityToolboxView
extends VelocityView {
    private String toolboxConfigLocation;

    public void setToolboxConfigLocation(String toolboxConfigLocation) {
        this.toolboxConfigLocation = toolboxConfigLocation;
    }

    protected String getToolboxConfigLocation() {
        return this.toolboxConfigLocation;
    }

    @Override
    protected Context createVelocityContext(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ChainedContext velocityContext = new ChainedContext((Context)new VelocityContext(model), this.getVelocityEngine(), request, response, this.getServletContext());
        if (this.getToolboxConfigLocation() != null) {
            ServletToolboxManager toolboxManager = ServletToolboxManager.getInstance((ServletContext)this.getServletContext(), (String)this.getToolboxConfigLocation());
            Map toolboxContext = toolboxManager.getToolbox((Object)velocityContext);
            velocityContext.setToolbox(toolboxContext);
        }
        return velocityContext;
    }

    @Override
    protected void initTool(Object tool, Context velocityContext) {
        Method initMethod = ClassUtils.getMethodIfAvailable(tool.getClass(), (String)"init", (Class[])new Class[]{Object.class});
        if (initMethod != null) {
            ReflectionUtils.invokeMethod((Method)initMethod, (Object)tool, (Object[])new Object[]{velocityContext});
        }
    }
}

