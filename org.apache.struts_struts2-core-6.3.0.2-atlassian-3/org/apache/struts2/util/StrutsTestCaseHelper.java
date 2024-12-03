/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import java.util.Collections;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.DispatcherErrorHandler;

public class StrutsTestCaseHelper {
    public static Dispatcher initDispatcher(ServletContext ctx, Map<String, String> params) {
        DispatcherWrapper du = new DispatcherWrapper(ctx, params != null ? params : Collections.emptyMap());
        du.init();
        Dispatcher.setInstance(du);
        Container container = du.getContainer();
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getActionContext().withContainer(container).withValueStack(stack).bind();
        return du;
    }

    public static void tearDown(Dispatcher dispatcher) {
        if (dispatcher != null && dispatcher.getConfigurationManager() != null) {
            dispatcher.cleanup();
        }
        StrutsTestCaseHelper.tearDown();
    }

    public static void tearDown() {
        new Dispatcher(null, null).cleanUpAfterInit();
        Dispatcher.clearInstance();
        ActionContext.clear();
    }

    private static class MockErrorHandler
    implements DispatcherErrorHandler {
        private MockErrorHandler() {
        }

        @Override
        public void init(ServletContext ctx) {
        }

        @Override
        public void handleError(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
            System.out.println("Dispatcher#sendError: " + code);
            e.printStackTrace(System.out);
        }
    }

    private static class DispatcherWrapper
    extends Dispatcher {
        public DispatcherWrapper(ServletContext ctx, Map<String, String> params) {
            super(ctx, params);
            super.setDispatcherErrorHandler(new MockErrorHandler());
        }

        @Override
        public void setDispatcherErrorHandler(DispatcherErrorHandler errorHandler) {
        }
    }
}

