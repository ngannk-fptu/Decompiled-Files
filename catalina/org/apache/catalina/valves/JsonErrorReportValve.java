/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.coyote.ActionCode
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.json.JSONFilter
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.valves;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.json.JSONFilter;
import org.apache.tomcat.util.res.StringManager;

public class JsonErrorReportValve
extends ErrorReportValve {
    @Override
    protected void report(Request request, Response response, Throwable throwable) {
        int statusCode = response.getStatus();
        if (statusCode < 400 || response.getContentWritten() > 0L || !response.setErrorReported()) {
            return;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, (Object)result);
        if (!result.get()) {
            return;
        }
        StringManager smClient = StringManager.getManager((String)"org.apache.catalina.valves", request.getLocales());
        response.setLocale(smClient.getLocale());
        String type = null;
        type = throwable != null ? smClient.getString("errorReportValve.exceptionReport") : smClient.getString("errorReportValve.statusReport");
        String message = response.getMessage();
        if (message == null && throwable != null) {
            message = throwable.getMessage();
        }
        String description = null;
        description = smClient.getString("http." + statusCode + ".desc");
        if (description == null) {
            if (message == null || message.isEmpty()) {
                return;
            }
            description = smClient.getString("errorReportValve.noDescription");
        }
        String jsonReport = "{\n  \"type\": \"" + JSONFilter.escape((String)type) + "\",\n  \"message\": \"" + JSONFilter.escape((String)message) + "\",\n  \"description\": \"" + JSONFilter.escape((String)description) + "\"\n}";
        try {
            block10: {
                try {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    if (!this.container.getLogger().isDebugEnabled()) break block10;
                    this.container.getLogger().debug((Object)"Failure to set the content-type of response", t);
                }
            }
            PrintWriter writer = response.getReporter();
            if (writer != null) {
                ((Writer)writer).write(jsonReport);
                response.finishResponse();
                return;
            }
        }
        catch (IOException | IllegalStateException exception) {
            // empty catch block
        }
    }
}

