/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.dispatcher.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ExecuteOperations;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.servlet.ServletHostConfig;

public class StrutsServlet
extends HttpServlet {
    private PrepareOperations prepare;
    private ExecuteOperations execute;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init(ServletConfig filterConfig) throws ServletException {
        InitOperations init = new InitOperations();
        Dispatcher dispatcher = null;
        try {
            ServletHostConfig config = new ServletHostConfig(filterConfig);
            dispatcher = init.initDispatcher(config);
            init.initStaticContentLoader(config, dispatcher);
            this.prepare = new PrepareOperations(dispatcher);
            this.execute = new ExecuteOperations(dispatcher);
        }
        finally {
            if (dispatcher != null) {
                dispatcher.cleanUpAfterInit();
            }
            init.cleanup();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            this.prepare.createActionContext(request, response);
            this.prepare.assignDispatcherToThread();
            this.prepare.setEncodingAndLocale(request, response);
            request = this.prepare.wrapRequest(request);
            ActionMapping mapping = this.prepare.findActionMapping(request, response);
            if (mapping == null) {
                boolean handled = this.execute.executeStaticResourceRequest(request, response);
                if (!handled) {
                    throw new ServletException("Resource loading not supported, use the StrutsPrepareAndExecuteFilter instead.");
                }
            } else {
                this.execute.executeAction(request, response, mapping);
            }
        }
        finally {
            this.prepare.cleanupRequest(request);
        }
    }

    public void destroy() {
        this.prepare.cleanupDispatcher();
    }
}

