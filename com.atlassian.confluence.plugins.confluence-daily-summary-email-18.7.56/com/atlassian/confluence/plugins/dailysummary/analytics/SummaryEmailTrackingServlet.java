/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.plugins.dailysummary.analytics;

import com.atlassian.confluence.plugins.dailysummary.analytics.SummaryEmailTrackBackEvent;
import com.atlassian.event.api.EventPublisher;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

@Deprecated
public class SummaryEmailTrackingServlet
extends HttpServlet {
    private final EventPublisher eventPublisher;

    public SummaryEmailTrackingServlet(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        try {
            this.eventPublisher.publish((Object)new SummaryEmailTrackBackEvent(req.getParameter("schedule")));
            this.writeResponse(httpServletResponse);
        }
        catch (Exception ex) {
            httpServletResponse.sendError(500);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeResponse(HttpServletResponse httpServletResponse) throws IOException {
        InputStream inputStream = null;
        ServletOutputStream responseOutputStream = null;
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setContentType("image/png");
        try {
            inputStream = ((Object)((Object)this)).getClass().getResourceAsStream("/images/trackback.png");
            responseOutputStream = httpServletResponse.getOutputStream();
            IOUtils.copy((InputStream)inputStream, (OutputStream)responseOutputStream);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(responseOutputStream);
            throw throwable;
        }
        IOUtils.closeQuietly((InputStream)inputStream);
        IOUtils.closeQuietly((OutputStream)responseOutputStream);
    }
}

