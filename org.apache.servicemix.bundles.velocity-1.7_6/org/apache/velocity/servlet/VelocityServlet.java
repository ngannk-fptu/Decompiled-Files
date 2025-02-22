/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.velocity.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.util.SimplePool;

public abstract class VelocityServlet
extends HttpServlet {
    public static final String REQUEST = "req";
    public static final String RESPONSE = "res";
    public static final String CONTENT_TYPE = "default.contentType";
    public static final String DEFAULT_CONTENT_TYPE = "text/html";
    public static final String DEFAULT_OUTPUT_ENCODING = "ISO-8859-1";
    private static String defaultContentType;
    protected static final String INIT_PROPS_KEY = "org.apache.velocity.properties";
    private static final String OLD_INIT_PROPS_KEY = "properties";
    private static SimplePool writerPool;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.initVelocity(config);
        defaultContentType = RuntimeSingleton.getString(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
    }

    protected void initVelocity(ServletConfig config) throws ServletException {
        try {
            Properties props = this.loadConfiguration(config);
            Velocity.init(props);
        }
        catch (Exception e) {
            throw new ServletException("Error initializing Velocity: " + e, (Throwable)e);
        }
    }

    protected Properties loadConfiguration(ServletConfig config) throws IOException, FileNotFoundException {
        String propsFile = config.getInitParameter(INIT_PROPS_KEY);
        if (propsFile == null || propsFile.length() == 0) {
            ServletContext sc = config.getServletContext();
            propsFile = config.getInitParameter(OLD_INIT_PROPS_KEY);
            if (propsFile == null || propsFile.length() == 0) {
                propsFile = sc.getInitParameter(INIT_PROPS_KEY);
                if ((propsFile == null || propsFile.length() == 0) && (propsFile = sc.getInitParameter(OLD_INIT_PROPS_KEY)) != null && propsFile.length() > 0) {
                    sc.log("Use of the properties initialization parameter 'properties' has been deprecated by 'org.apache.velocity.properties'");
                }
            } else {
                sc.log("Use of the properties initialization parameter 'properties' has been deprecated by 'org.apache.velocity.properties'");
            }
        }
        Properties p = new Properties();
        if (propsFile != null) {
            p.load(this.getServletContext().getResourceAsStream(propsFile));
        }
        return p;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doRequest(request, response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Context context = null;
        try {
            context = this.createContext(request, response);
            this.setContentType(request, response);
            Template template = this.handleRequest(request, response, context);
            if (template == null) {
                return;
            }
            this.mergeTemplate(template, context, response);
        }
        catch (Exception e) {
            this.error(request, response, e);
        }
        finally {
            this.requestCleanup(request, response, context);
        }
    }

    protected void requestCleanup(HttpServletRequest request, HttpServletResponse response, Context context) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void mergeTemplate(Template template, Context context, HttpServletResponse response) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, IOException, UnsupportedEncodingException, Exception {
        block8: {
            ServletOutputStream output = response.getOutputStream();
            VelocityWriter vw = null;
            String encoding = response.getCharacterEncoding();
            try {
                vw = (VelocityWriter)writerPool.get();
                if (vw == null) {
                    vw = new VelocityWriter(new OutputStreamWriter((OutputStream)output, encoding), 4096, true);
                } else {
                    vw.recycle(new OutputStreamWriter((OutputStream)output, encoding));
                }
                template.merge(context, vw);
                Object var8_7 = null;
                if (vw == null) break block8;
            }
            catch (Throwable throwable) {
                Object var8_8 = null;
                if (vw != null) {
                    try {
                        vw.flush();
                    }
                    catch (IOException e) {
                        // empty catch block
                    }
                    vw.recycle(null);
                    writerPool.put(vw);
                }
                throw throwable;
            }
            try {
                vw.flush();
            }
            catch (IOException e) {
                // empty catch block
            }
            vw.recycle(null);
            writerPool.put(vw);
            {
            }
        }
    }

    protected void setContentType(HttpServletRequest request, HttpServletResponse response) {
        String encoding;
        String contentType = defaultContentType;
        int index = contentType.lastIndexOf(59) + 1;
        if ((index <= 0 || index < contentType.length() && contentType.indexOf("charset", index) == -1) && !DEFAULT_OUTPUT_ENCODING.equalsIgnoreCase(encoding = this.chooseCharacterEncoding(request))) {
            contentType = contentType + "; charset=" + encoding;
        }
        response.setContentType(contentType);
    }

    protected String chooseCharacterEncoding(HttpServletRequest request) {
        return RuntimeSingleton.getString("output.encoding", DEFAULT_OUTPUT_ENCODING);
    }

    protected Context createContext(HttpServletRequest request, HttpServletResponse response) {
        VelocityContext context = new VelocityContext();
        context.put(REQUEST, request);
        context.put(RESPONSE, response);
        return context;
    }

    public Template getTemplate(String name) throws ResourceNotFoundException, ParseErrorException, Exception {
        return RuntimeSingleton.getTemplate(name);
    }

    public Template getTemplate(String name, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception {
        return RuntimeSingleton.getTemplate(name, encoding);
    }

    protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) throws Exception {
        Template t = this.handleRequest(ctx);
        if (t == null) {
            throw new Exception("handleRequest(Context) returned null - no template selected!");
        }
        return t;
    }

    protected Template handleRequest(Context ctx) throws Exception {
        throw new Exception("You must override VelocityServlet.handleRequest( Context)  or VelocityServlet.handleRequest( HttpServletRequest,  HttpServletResponse, Context)");
    }

    protected void error(HttpServletRequest request, HttpServletResponse response, Exception cause) throws ServletException, IOException {
        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html.append("<title>Error</title>");
        html.append("<body bgcolor=\"#ffffff\">");
        html.append("<h2>VelocityServlet: Error processing the template</h2>");
        html.append("<pre>");
        String why = cause.getMessage();
        if (why != null && why.trim().length() > 0) {
            html.append(why);
            html.append("<br>");
        }
        StringWriter sw = new StringWriter();
        cause.printStackTrace(new PrintWriter(sw));
        html.append(sw.toString());
        html.append("</pre>");
        html.append("</body>");
        html.append("</html>");
        response.getOutputStream().print(html.toString());
    }

    static {
        writerPool = new SimplePool(40);
    }
}

