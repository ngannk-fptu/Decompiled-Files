/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package groovy.servlet;

import groovy.lang.Closure;
import groovy.servlet.AbstractHttpServlet;
import groovy.servlet.ServletBinding;
import groovy.servlet.ServletCategory;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.groovy.runtime.GroovyCategorySupport;

public class GroovyServlet
extends AbstractHttpServlet {
    private GroovyScriptEngine gse;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.gse = this.createGroovyScriptEngine();
        this.servletContext.log("Groovy servlet initialized on " + this.gse + ".");
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String scriptUri = this.getScriptUri(request);
        response.setContentType("text/html; charset=" + this.encoding);
        final ServletBinding binding = new ServletBinding(request, response, this.servletContext);
        this.setVariables(binding);
        try {
            Closure closure = new Closure(this.gse){

                @Override
                public Object call() {
                    try {
                        return ((GroovyScriptEngine)this.getDelegate()).run(scriptUri, binding);
                    }
                    catch (ResourceException e) {
                        throw new RuntimeException(e);
                    }
                    catch (ScriptException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            GroovyCategorySupport.use(ServletCategory.class, closure);
        }
        catch (RuntimeException runtimeException) {
            StringBuilder error = new StringBuilder("GroovyServlet Error: ");
            error.append(" script: '");
            error.append(scriptUri);
            error.append("': ");
            Throwable e = runtimeException.getCause();
            if (e == null) {
                error.append(" Script processing failed.\n");
                error.append(runtimeException.getMessage());
                if (runtimeException.getStackTrace().length > 0) {
                    error.append(runtimeException.getStackTrace()[0].toString());
                }
                this.servletContext.log(error.toString());
                System.err.println(error.toString());
                runtimeException.printStackTrace(System.err);
                response.sendError(500, error.toString());
                return;
            }
            if (e instanceof ResourceException) {
                error.append(" Script not found, sending 404.");
                this.servletContext.log(error.toString());
                System.err.println(error.toString());
                response.sendError(404);
                return;
            }
            this.servletContext.log("An error occurred processing the request", (Throwable)runtimeException);
            error.append(e.getMessage());
            if (e.getStackTrace().length > 0) {
                error.append(e.getStackTrace()[0].toString());
            }
            this.servletContext.log(e.toString());
            System.err.println(e.toString());
            runtimeException.printStackTrace(System.err);
            response.sendError(500, e.toString());
        }
    }

    protected GroovyScriptEngine createGroovyScriptEngine() {
        return new GroovyScriptEngine(this);
    }
}

