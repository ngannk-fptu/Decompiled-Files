/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package groovy.servlet;

import groovy.lang.Binding;
import groovy.xml.MarkupBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.runtime.MethodClosure;

public class ServletBinding
extends Binding {
    private boolean initialized;

    public ServletBinding(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
        super.setVariable("request", request);
        super.setVariable("response", response);
        super.setVariable("context", context);
        super.setVariable("application", context);
        super.setVariable("session", request.getSession(false));
        Map params = this.collectParams(request);
        super.setVariable("params", params);
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        Enumeration names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String headerName = (String)names.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        super.setVariable("headers", headers);
    }

    private Map collectParams(HttpServletRequest request) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            if (super.getVariables().containsKey(name)) continue;
            String[] values = request.getParameterValues(name);
            if (values.length == 1) {
                params.put(name, values[0]);
                continue;
            }
            params.put(name, values);
        }
        return params;
    }

    @Override
    public void setVariable(String name, Object value) {
        this.lazyInit();
        ServletBinding.validateArgs(name, "Can't bind variable to");
        ServletBinding.excludeReservedName(name, "out");
        ServletBinding.excludeReservedName(name, "sout");
        ServletBinding.excludeReservedName(name, "html");
        ServletBinding.excludeReservedName(name, "json");
        ServletBinding.excludeReservedName(name, "forward");
        ServletBinding.excludeReservedName(name, "include");
        ServletBinding.excludeReservedName(name, "redirect");
        super.setVariable(name, value);
    }

    @Override
    public Map getVariables() {
        this.lazyInit();
        return super.getVariables();
    }

    @Override
    public Object getVariable(String name) {
        this.lazyInit();
        ServletBinding.validateArgs(name, "No variable with");
        return super.getVariable(name);
    }

    private void lazyInit() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        HttpServletResponse response = (HttpServletResponse)super.getVariable("response");
        ServletOutput output = new ServletOutput(response);
        super.setVariable("out", output.getWriter());
        super.setVariable("sout", output.getOutputStream());
        MarkupBuilder builder = new MarkupBuilder(output.getWriter());
        builder.setExpandEmptyElements(true);
        super.setVariable("html", builder);
        try {
            Class<?> jsonBuilderClass = this.getClass().getClassLoader().loadClass("groovy.json.StreamingJsonBuilder");
            Constructor<?> writerConstructor = jsonBuilderClass.getConstructor(Writer.class);
            super.setVariable("json", writerConstructor.newInstance(output.getWriter()));
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        MethodClosure c = new MethodClosure((Object)this, "forward");
        super.setVariable("forward", c);
        c = new MethodClosure((Object)this, "include");
        super.setVariable("include", c);
        c = new MethodClosure((Object)this, "redirect");
        super.setVariable("redirect", c);
    }

    private static void validateArgs(String name, String message) {
        if (name == null) {
            throw new IllegalArgumentException(message + " null key.");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException(message + " blank key name. [length=0]");
        }
    }

    private static void excludeReservedName(String name, String reservedName) {
        if (reservedName.equals(name)) {
            throw new IllegalArgumentException("Can't bind variable to key named '" + name + "'.");
        }
    }

    public void forward(String path) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)super.getVariable("request");
        HttpServletResponse response = (HttpServletResponse)super.getVariable("response");
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.forward((ServletRequest)request, (ServletResponse)response);
    }

    public void include(String path) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)super.getVariable("request");
        HttpServletResponse response = (HttpServletResponse)super.getVariable("response");
        RequestDispatcher dispatcher = request.getRequestDispatcher(path);
        dispatcher.include((ServletRequest)request, (ServletResponse)response);
    }

    public void redirect(String location) throws IOException {
        HttpServletResponse response = (HttpServletResponse)super.getVariable("response");
        response.sendRedirect(location);
    }

    private static class ServletOutput {
        private HttpServletResponse response;
        private ServletOutputStream outputStream;
        private PrintWriter writer;

        public ServletOutput(HttpServletResponse response) {
            this.response = response;
        }

        private ServletOutputStream getResponseStream() throws IOException {
            if (this.writer != null) {
                throw new IllegalStateException("The variable 'out' or 'html' have been used already. Use either out/html or sout, not both.");
            }
            if (this.outputStream == null) {
                this.outputStream = this.response.getOutputStream();
            }
            return this.outputStream;
        }

        public ServletOutputStream getOutputStream() {
            return new ServletOutputStream(){

                public void write(int b) throws IOException {
                    ServletOutput.this.getResponseStream().write(b);
                }

                public void close() throws IOException {
                    ServletOutput.this.getResponseStream().close();
                }

                public void flush() throws IOException {
                    ServletOutput.this.getResponseStream().flush();
                }

                public void write(byte[] b) throws IOException {
                    ServletOutput.this.getResponseStream().write(b);
                }

                public void write(byte[] b, int off, int len) throws IOException {
                    ServletOutput.this.getResponseStream().write(b, off, len);
                }
            };
        }

        private PrintWriter getResponseWriter() {
            if (this.outputStream != null) {
                throw new IllegalStateException("The variable 'sout' have been used already. Use either out/html or sout, not both.");
            }
            if (this.writer == null) {
                try {
                    this.writer = this.response.getWriter();
                }
                catch (IOException ioe) {
                    this.writer = new PrintWriter(new ByteArrayOutputStream());
                    throw new IllegalStateException("unable to get response writer", ioe);
                }
            }
            return this.writer;
        }

        public PrintWriter getWriter() {
            return new PrintWriter(new InvalidOutputStream()){

                @Override
                public boolean checkError() {
                    return ServletOutput.this.getResponseWriter().checkError();
                }

                @Override
                public void close() {
                    ServletOutput.this.getResponseWriter().close();
                }

                @Override
                public void flush() {
                    ServletOutput.this.getResponseWriter().flush();
                }

                @Override
                public void write(char[] buf) {
                    ServletOutput.this.getResponseWriter().write(buf);
                }

                @Override
                public void write(char[] buf, int off, int len) {
                    ServletOutput.this.getResponseWriter().write(buf, off, len);
                }

                @Override
                public void write(int c) {
                    ServletOutput.this.getResponseWriter().write(c);
                }

                @Override
                public void write(String s, int off, int len) {
                    ServletOutput.this.getResponseWriter().write(s, off, len);
                }

                @Override
                public void println() {
                    ServletOutput.this.getResponseWriter().println();
                }

                @Override
                public PrintWriter format(String format, Object ... args) {
                    ServletOutput.this.getResponseWriter().format(format, args);
                    return this;
                }

                @Override
                public PrintWriter format(Locale l, String format, Object ... args) {
                    ServletOutput.this.getResponseWriter().format(l, format, args);
                    return this;
                }
            };
        }
    }

    private static class InvalidOutputStream
    extends OutputStream {
        private InvalidOutputStream() {
        }

        @Override
        public void write(int b) {
            throw new GroovyBugError("Any write calls to this stream are invalid!");
        }
    }
}

