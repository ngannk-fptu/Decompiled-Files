/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.result.StrutsResultSupport;

public class PlainTextResult
extends StrutsResultSupport {
    public static final int BUFFER_SIZE = 1024;
    private static final Logger LOG = LogManager.getLogger(PlainTextResult.class);
    private static final long serialVersionUID = 3633371605905583950L;
    private String charSet;

    public PlainTextResult() {
    }

    public PlainTextResult(String location) {
        super(location);
    }

    public String getCharSet() {
        return this.charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        Charset charset = this.readCharset();
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        this.applyCharset(charset, response);
        this.applyAdditionalHeaders(response);
        String location = this.adjustLocation(finalLocation);
        try (PrintWriter writer = response.getWriter();
             InputStream resourceAsStream = this.readStream(invocation, location);
             InputStreamReader reader = new InputStreamReader(resourceAsStream, charset == null ? Charset.defaultCharset() : charset);){
            this.logWrongStream(finalLocation, resourceAsStream);
            this.sendStream(writer, reader);
        }
    }

    protected InputStream readStream(ActionInvocation invocation, String location) {
        ServletContext servletContext = invocation.getInvocationContext().getServletContext();
        return servletContext.getResourceAsStream(location);
    }

    protected void logWrongStream(String finalLocation, InputStream resourceAsStream) {
        if (resourceAsStream == null) {
            LOG.warn("Resource at location [{}] cannot be obtained (return null) from ServletContext !!!", (Object)finalLocation);
        }
    }

    protected void sendStream(PrintWriter writer, InputStreamReader reader) throws IOException {
        int charRead;
        char[] buffer = new char[1024];
        while ((charRead = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, charRead);
        }
    }

    protected String adjustLocation(String location) {
        if (location.charAt(0) != '/') {
            return "/" + location;
        }
        return location;
    }

    protected void applyAdditionalHeaders(HttpServletResponse response) {
        response.setHeader("Content-Disposition", "inline");
    }

    protected void applyCharset(Charset charset, HttpServletResponse response) {
        if (charset != null) {
            response.setContentType("text/plain; charset=" + this.charSet);
        } else {
            response.setContentType("text/plain");
        }
    }

    protected Charset readCharset() {
        Charset charset = null;
        if (this.charSet != null) {
            if (Charset.isSupported(this.charSet)) {
                charset = Charset.forName(this.charSet);
            } else {
                LOG.warn("charset [{}] is not recognized", (Object)charset);
                charset = null;
            }
        }
        return charset;
    }
}

