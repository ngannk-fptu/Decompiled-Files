/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.StringEscapeUtils
 */
package com.atlassian.confluence.plugins.emailtracker;

import com.atlassian.confluence.plugins.emailtracker.EmailTrackerService;
import com.atlassian.confluence.plugins.emailtracker.InvalidTrackingRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

public class EmailTrackerServlet
extends HttpServlet {
    private final EmailTrackerService tracker;

    public EmailTrackerServlet(EmailTrackerService tracker) {
        this.tracker = tracker;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse httpServletResponse) throws IOException {
        try {
            Map<String, String> params = this.extractParams(req.getParameterMap());
            String urlToQuery = req.getRequestURL().toString();
            this.tracker.handleTrackingRequest(urlToQuery, params);
            this.writeResponse(httpServletResponse);
        }
        catch (InvalidTrackingRequestException e) {
            httpServletResponse.sendError(400, "Invalid tracking request: " + StringEscapeUtils.escapeHtml((String)req.getRequestURI()));
        }
        catch (Exception ex) {
            httpServletResponse.sendError(500);
        }
    }

    private Map<String, String> extractParams(Map<String, String[]> parameterMap) {
        return parameterMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ((String[])entry.getValue())[0]));
    }

    private void writeResponse(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("image/png");
        try (InputStream inputStream = ((Object)((Object)this)).getClass().getResourceAsStream("/images/trackback.png");
             ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();){
            IOUtils.copy((InputStream)inputStream, (OutputStream)responseOutputStream);
        }
    }
}

