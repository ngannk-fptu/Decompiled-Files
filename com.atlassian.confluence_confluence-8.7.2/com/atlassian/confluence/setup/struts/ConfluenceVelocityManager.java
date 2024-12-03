/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.context.ChainedVelocityContext
 *  com.atlassian.confluence.velocity.htmlsafe.ConfluenceEventCartridgeProcessor
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessor
 *  com.opensymphony.xwork2.util.ValueStack
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.views.util.ContextUtil
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.app.event.EventCartridge
 *  org.apache.velocity.app.event.EventHandler
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 *  org.apache.velocity.context.Context
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.struts;

import com.atlassian.confluence.impl.struts.RestrictedValueStack;
import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.confluence.setup.struts.CompositeContext;
import com.atlassian.confluence.setup.struts.OutputAwareStrutsVelocityContext;
import com.atlassian.confluence.setup.velocity.ConfluenceStaticContextItemProvider;
import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import com.atlassian.confluence.velocity.context.ChainedVelocityContext;
import com.atlassian.confluence.velocity.htmlsafe.ConfluenceEventCartridgeProcessor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessor;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.views.util.ContextUtil;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.EventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceVelocityManager
extends VelocityManager {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceVelocityManager.class);
    private static final VelocityContextItemProvider STATIC_CONTEXT_ITEM_PROVIDER = new ConfluenceStaticContextItemProvider();
    private static final EventCartridgeProcessor CARTRIDGE_PROCESSOR = new ConfluenceEventCartridgeProcessor();

    protected VelocityEngine newVelocityEngine(ServletContext context) {
        log.info("ConfluenceVelocityManager creating new VelocityEngine from ServletContext");
        return super.newVelocityEngine(context);
    }

    protected Context buildContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        OutputAwareStrutsVelocityContext confluenceContext = new OutputAwareStrutsVelocityContext(ConfluenceVelocityManager.newApplicationContexts(), stack);
        ContextUtil.getStandardContext((ValueStack)stack, (HttpServletRequest)req, (HttpServletResponse)res).forEach((arg_0, arg_1) -> ((Context)confluenceContext).put(arg_0, arg_1));
        confluenceContext.put("stack", new RestrictedValueStack(stack));
        confluenceContext.remove("struts");
        confluenceContext.put("req", req);
        confluenceContext.put("res", res);
        return ConfluenceVelocityManager.processContextForRendering((Context)confluenceContext);
    }

    public static Context processContextForRendering(Context context) {
        EventCartridge cartridge = new EventCartridge();
        cartridge.addEventHandler((EventHandler)((ReferenceInsertionEventHandler)(reference, value) -> {
            log.debug("resolving reference [{}]", (Object)reference);
            return value;
        }));
        CARTRIDGE_PROCESSOR.processCartridge(cartridge);
        cartridge.attachToContext(context);
        return context;
    }

    public static Context getConfluenceVelocityContext() {
        return ConfluenceVelocityManager.processContextForRendering((Context)new ChainedVelocityContext(CompositeContext.composite(ConfluenceVelocityManager.newApplicationContexts())));
    }

    private static List<VelocityContext> newApplicationContexts() {
        return List.of(new VelocityContext(STATIC_CONTEXT_ITEM_PROVIDER.getContextMap()), new VelocityContext(!ContainerManager.isContainerSetup() && SetupContext.get() != null ? Map.of("webResourceManager", SetupContext.get().getBean("setupWebResourceManager")) : ((VelocityContextItemProvider)ContainerManager.getComponent((String)"velocityContextItemProvider", VelocityContextItemProvider.class)).getContextMap()));
    }
}

