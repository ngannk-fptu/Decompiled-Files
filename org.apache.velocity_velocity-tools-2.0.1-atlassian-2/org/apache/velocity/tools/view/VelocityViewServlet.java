/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang.StringEscapeUtils
 *  org.apache.velocity.Template
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.view.ServletUtils;
import org.apache.velocity.tools.view.VelocityView;

public class VelocityViewServlet
extends HttpServlet {
    public static final String BUFFER_OUTPUT_PARAM = "org.apache.velocity.tools.bufferOutput";
    private static final long serialVersionUID = -3329444102562079189L;
    private transient VelocityView view;
    private boolean bufferOutput = false;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.getVelocityView();
        String buffer = this.findInitParameter(config, BUFFER_OUTPUT_PARAM);
        if (buffer != null && buffer.equals("true")) {
            this.bufferOutput = true;
            this.getLog().debug((Object)"VelocityViewServlet will buffer mergeTemplate output.");
        }
    }

    protected String findInitParameter(ServletConfig config, String key) {
        String param = config.getInitParameter(key);
        if (param == null || param.length() == 0) {
            ServletContext servletContext = config.getServletContext();
            param = servletContext.getInitParameter(key);
        }
        return param;
    }

    protected VelocityView getVelocityView() {
        if (this.view == null) {
            this.setVelocityView(ServletUtils.getVelocityView(this.getServletConfig()));
            assert (this.view != null);
        }
        return this.view;
    }

    protected void setVelocityView(VelocityView view) {
        this.view = view;
    }

    protected String getVelocityProperty(String name, String alternate) {
        return this.getVelocityView().getProperty(name, alternate);
    }

    protected Log getLog() {
        return this.getVelocityView().getLog();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doRequest(request, response);
    }

    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Context context = null;
        try {
            context = this.createContext(request, response);
            this.fillContext(context, request);
            this.setContentType(request, response);
            Template template = this.handleRequest(request, response, context);
            this.mergeTemplate(template, context, response);
        }
        catch (IOException e) {
            this.error(request, response, e);
            throw e;
        }
        catch (ResourceNotFoundException e) {
            this.manageResourceNotFound(request, response, e);
        }
        catch (RuntimeException e) {
            this.error(request, response, e);
            throw e;
        }
        finally {
            this.requestCleanup(request, response, context);
        }
    }

    protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) {
        return this.getTemplate(request, response);
    }

    protected Context createContext(HttpServletRequest request, HttpServletResponse response) {
        return this.getVelocityView().createContext(request, response);
    }

    protected void fillContext(Context context, HttpServletRequest request) {
    }

    protected void setContentType(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(this.getVelocityView().getDefaultContentType());
    }

    protected Template getTemplate(HttpServletRequest request, HttpServletResponse response) {
        return this.getVelocityView().getTemplate(request, response);
    }

    protected Template getTemplate(String name) {
        return this.getVelocityView().getTemplate(name);
    }

    protected void mergeTemplate(Template template, Context context, HttpServletResponse response) throws IOException {
        Writer writer = this.bufferOutput ? new StringWriter() : response.getWriter();
        this.getVelocityView().merge(template, context, writer);
        if (this.bufferOutput) {
            response.getWriter().write(writer.toString());
        }
    }

    protected void error(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        String path = ServletUtils.getPath(request);
        if (response.isCommitted()) {
            this.getLog().error((Object)"An error occured but the response headers have already been sent.");
            this.getLog().error((Object)("Error processing a template for path '" + path + "'"), e);
            return;
        }
        try {
            this.getLog().error((Object)("Error processing a template for path '" + path + "'"), e);
            StringBuilder html = new StringBuilder();
            html.append("<html>\n");
            html.append("<head><title>Error</title></head>\n");
            html.append("<body>\n");
            html.append("<h2>VelocityView : Error processing a template for path '");
            html.append(StringEscapeUtils.escapeHtml((String)ServletUtils.getPath(request)));
            html.append("'</h2>\n");
            Throwable cause = e;
            String why = cause.getMessage();
            if (why != null && why.length() > 0) {
                html.append(StringEscapeUtils.escapeHtml((String)why));
                html.append("\n<br>\n");
            }
            if (cause instanceof MethodInvocationException) {
                cause = ((MethodInvocationException)cause).getWrappedThrowable();
            }
            StringWriter sw = new StringWriter();
            cause.printStackTrace(new PrintWriter(sw));
            html.append("<pre>\n");
            html.append(StringEscapeUtils.escapeHtml((String)sw.toString()));
            html.append("</pre>\n");
            html.append("</body>\n");
            html.append("</html>");
            response.getWriter().write(html.toString());
        }
        catch (Exception e2) {
            String msg = "Exception while printing error screen";
            this.getLog().error((Object)msg, (Throwable)e2);
            throw new RuntimeException(msg, e);
        }
    }

    protected void manageResourceNotFound(HttpServletRequest request, HttpServletResponse response, ResourceNotFoundException e) throws IOException {
        String path = ServletUtils.getPath(request);
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("Resource not found for path '" + path + "'"), (Throwable)e);
        }
        String message = e.getMessage();
        if (response.isCommitted() || path == null || message == null || !message.contains("'" + path + "'")) {
            this.error(request, response, e);
            throw e;
        }
        response.sendError(404, path);
    }

    protected void requestCleanup(HttpServletRequest request, HttpServletResponse response, Context context) {
    }
}

