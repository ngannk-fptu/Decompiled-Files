/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.action;

import com.opensymphony.xwork2.ActionSupport;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.action.ServletResponseAware;

public abstract class CspReportAction
extends ActionSupport
implements ServletRequestAware,
ServletResponseAware {
    private HttpServletRequest request;

    @Override
    public void withServletRequest(HttpServletRequest request) {
        if (!this.isCspReportRequest(request)) {
            return;
        }
        try {
            BufferedReader reader = request.getReader();
            String cspReport = reader.readLine();
            this.processReport(cspReport);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private boolean isCspReportRequest(HttpServletRequest request) {
        if (!"POST".equals(request.getMethod()) || request.getContentLength() <= 0) {
            return false;
        }
        String contentType = request.getContentType();
        return "application/csp-report".equals(contentType);
    }

    @Override
    public void withServletResponse(HttpServletResponse response) {
        response.setStatus(204);
    }

    abstract void processReport(String var1);

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getServletRequest() {
        return this.request;
    }
}

