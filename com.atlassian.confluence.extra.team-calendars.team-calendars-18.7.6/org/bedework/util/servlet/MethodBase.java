/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.util.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public abstract class MethodBase {
    protected boolean debug;
    protected boolean dumpContent;
    protected transient Logger log;
    private final SimpleDateFormat httpDateFormatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss ");

    public abstract void init() throws ServletException;

    public abstract ObjectMapper getMapper();

    public abstract void doMethod(HttpServletRequest var1, HttpServletResponse var2) throws ServletException;

    protected String hrefFromPath(List<String> path, int start) {
        int sz = path.size();
        if (start == sz) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < sz; ++i) {
            sb.append("/");
            sb.append(path.get(i));
        }
        sb.append("/");
        return sb.toString();
    }

    protected void write(String s, HttpServletResponse resp) throws ServletException {
        if (s == null) {
            resp.setStatus(404);
            return;
        }
        try {
            PrintWriter pw = resp.getWriter();
            pw.write(s);
            pw.flush();
        }
        catch (Throwable t) {
            throw new ServletException(t);
        }
        resp.setStatus(200);
    }

    protected void writeJson(Object o, HttpServletResponse resp) throws ServletException {
        if (o == null) {
            resp.setStatus(404);
            return;
        }
        try {
            this.getMapper().writeValue((Writer)resp.getWriter(), o);
        }
        catch (Throwable t) {
            throw new ServletException(t);
        }
        resp.setStatus(200);
    }

    public List<String> getResourceUri(HttpServletRequest req) throws ServletException {
        String uri = req.getServletPath();
        if (uri == null || uri.length() == 0) {
            uri = "/";
        }
        return MethodBase.fixPath(uri);
    }

    public static List<String> fixPath(String path) throws ServletException {
        String decoded;
        if (path == null) {
            return null;
        }
        try {
            decoded = URLDecoder.decode(path, "UTF8");
        }
        catch (Throwable t) {
            throw new ServletException("bad path: " + path);
        }
        if (decoded == null) {
            return null;
        }
        if (decoded.indexOf(92) >= 0) {
            decoded = decoded.replace('\\', '/');
        }
        if (!decoded.startsWith("/")) {
            decoded = "/" + decoded;
        }
        while (decoded.contains("//")) {
            decoded = decoded.replaceAll("//", "/");
        }
        StringTokenizer st = new StringTokenizer(decoded, "/");
        ArrayList<String> al = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            if (s.equals(".")) continue;
            if (s.equals("..")) {
                if (al.size() == 0) {
                    return null;
                }
                al.remove(al.size() - 1);
                continue;
            }
            al.add(s);
        }
        return al;
    }

    protected void addHeaders(HttpServletResponse resp) throws ServletException {
        resp.addHeader("Allow", "POST, GET");
    }

    protected Object readJson(InputStream is, Class cl, HttpServletResponse resp) throws ServletException {
        if (is == null) {
            return null;
        }
        try {
            return this.getMapper().readValue(is, cl);
        }
        catch (Throwable t) {
            resp.setStatus(500);
            if (this.debug) {
                this.error(t);
            }
            throw new ServletException(t);
        }
    }

    protected Object readJson(InputStream is, TypeReference tr, HttpServletResponse resp) throws ServletException {
        if (is == null) {
            return null;
        }
        try {
            return this.getMapper().readValue(is, tr);
        }
        catch (Throwable t) {
            resp.setStatus(500);
            if (this.debug) {
                this.error(t);
            }
            throw new ServletException(t);
        }
    }

    protected void sendJsonError(HttpServletResponse resp, String msg) {
        block2: {
            try {
                resp.setStatus(200);
                resp.setContentType("application/json; charset=UTF-8");
                String json = "{\"status\": \"failed\", \"msg\": \"" + msg + "\"}";
                resp.setContentType("application/json; charset=UTF-8");
                ServletOutputStream os = resp.getOutputStream();
                byte[] bytes = json.getBytes();
                resp.setContentLength(bytes.length);
                os.write(bytes);
                os.close();
            }
            catch (Throwable ignored) {
                if (!this.debug) break block2;
                this.debugMsg("Unable to send error: " + msg);
            }
        }
    }

    protected void sendOkJsonData(HttpServletResponse resp) {
        String json = "{\"status\": \"ok\"}";
        this.sendOkJsonData(resp, "{\"status\": \"ok\"}");
    }

    protected void sendOkJsonData(HttpServletResponse resp, String data) {
        try {
            resp.setStatus(200);
            resp.setContentType("application/json; charset=UTF-8");
            ServletOutputStream os = resp.getOutputStream();
            byte[] bytes = data.getBytes();
            resp.setContentLength(bytes.length);
            os.write(bytes);
            os.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String formatHTTPDate(Timestamp val) {
        if (val == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = this.httpDateFormatter;
        synchronized (simpleDateFormat) {
            return this.httpDateFormatter.format(val) + "GMT";
        }
    }

    protected Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(this.getClass());
        }
        return this.log;
    }

    protected void debugMsg(String msg) {
        this.getLogger().debug(msg);
    }

    protected void error(Throwable t) {
        this.getLogger().error(this, t);
    }

    protected void error(String msg) {
        this.getLogger().error(msg);
    }

    protected void warn(String msg) {
        this.getLogger().warn(msg);
    }

    protected void logIt(String msg) {
        this.getLogger().info(msg);
    }

    protected void trace(String msg) {
        this.getLogger().debug(msg);
    }

    public static class MethodInfo {
        private final Class<? extends MethodBase> methodClass;
        private final boolean requiresAuth;

        public MethodInfo(Class<? extends MethodBase> methodClass, boolean requiresAuth) {
            this.methodClass = methodClass;
            this.requiresAuth = requiresAuth;
        }

        public Class<? extends MethodBase> getMethodClass() {
            return this.methodClass;
        }

        public boolean getRequiresAuth() {
            return this.requiresAuth;
        }
    }
}

