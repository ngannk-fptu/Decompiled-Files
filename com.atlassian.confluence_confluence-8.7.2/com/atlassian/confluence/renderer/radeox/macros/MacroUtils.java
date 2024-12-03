/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.ContextUtils
 *  com.atlassian.spring.container.ContainerManager
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionContext
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.util.ValueStack
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.velocity.context.Context
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.radeox.macro.parameter.MacroParameter
 */
package com.atlassian.confluence.renderer.radeox.macros;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.struts.RestrictedValueStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.WikiRendererContextKeys;
import com.atlassian.confluence.setup.struts.ConfluenceVelocityManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceActionForDecorating;
import com.atlassian.confluence.util.ConfluenceMockServletRequest;
import com.atlassian.confluence.velocity.ContextUtils;
import com.atlassian.confluence.web.context.StaticHttpContext;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.velocity.context.Context;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.radeox.macro.parameter.MacroParameter;

public final class MacroUtils {
    private MacroUtils() {
        throw new AssertionError((Object)"Cannot instantiate utility class");
    }

    public static Map<String, Object> defaultVelocityContext() {
        return MacroUtils.defaultVelocityContext(null);
    }

    public static Map<String, Object> defaultVelocityContext(@Nullable Space space) {
        return ContextUtils.toMap((Context)MacroUtils.createDefaultVelocityContext(space));
    }

    public static Map<String, Object> requiredVelocityContext(List<String> requiredKeys) {
        if (CollectionUtils.isEmpty(requiredKeys)) {
            return Collections.emptyMap();
        }
        return MacroUtils.defaultVelocityContext().entrySet().stream().filter(e -> requiredKeys.contains(e.getKey()) && e.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Context createDefaultVelocityContext() {
        return MacroUtils.createDefaultVelocityContext(null);
    }

    private static Context createDefaultVelocityContext(@Nullable Space space) {
        Context ctx = ConfluenceVelocityManager.getConfluenceVelocityContext();
        if (ActionContext.getContext() != null && ActionContext.getContext().getValueStack() != null) {
            ValueStack stack = ActionContext.getContext().getValueStack();
            ctx.put("stack", (Object)new RestrictedValueStack(stack));
        }
        StaticHttpContext httpContext = new StaticHttpContext();
        HttpServletRequest request = httpContext.getRequest();
        HttpServletResponse response = httpContext.getResponse();
        Object requestObj = Objects.requireNonNullElseGet(request, ConfluenceMockServletRequest::new);
        ctx.put("request", requestObj);
        ctx.put("response", (Object)response);
        ctx.put("req", requestObj);
        ctx.put("res", (Object)response);
        ctx.put("action", (Object)MacroUtils.getConfluenceActionSupport(space));
        return ctx;
    }

    public static Action getConfluenceActionSupport() {
        return MacroUtils.getConfluenceActionSupport(null);
    }

    private static Action getConfluenceActionSupport(@Nullable Space space) {
        return (Action)Optional.ofNullable(ActionContext.getContext()).map(ActionContext::getActionInvocation).map(ActionInvocation::getAction).orElseGet(() -> {
            ConfluenceActionSupport action = space == null ? new ConfluenceActionSupport() : new SpaceActionForDecorating(space);
            ContainerManager.autowireComponent((Object)action);
            return action;
        });
    }

    public static ContentEntityObject getContentEntityObject(MacroParameter macroParameter) {
        Map contextParams = macroParameter.getContext().getParameters();
        PageContext context = WikiRendererContextKeys.getPageContext(contextParams);
        return context.getEntity();
    }
}

