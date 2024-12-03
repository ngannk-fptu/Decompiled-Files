/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.dispatcher;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public class ExecuteOperations {
    private Dispatcher dispatcher;

    public ExecuteOperations(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public boolean executeStaticResourceRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StaticContentLoader staticResourceLoader;
        String resourcePath = RequestUtils.getServletPath(request);
        if ("".equals(resourcePath) && null != request.getPathInfo()) {
            resourcePath = request.getPathInfo();
        }
        if ((staticResourceLoader = this.dispatcher.getStaticContentLoader()).canHandle(resourcePath)) {
            staticResourceLoader.findStaticResource(resourcePath, request, response);
            return true;
        }
        return false;
    }

    public void executeAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ServletException {
        this.dispatcher.serviceAction(request, response, mapping);
    }
}

