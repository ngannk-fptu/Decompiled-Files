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

import groovy.servlet.AbstractHttpServlet;
import groovy.servlet.ServletBinding;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemplateServlet
extends AbstractHttpServlet {
    private final Map<String, TemplateCacheEntry> cache = new WeakHashMap<String, TemplateCacheEntry>();
    private TemplateEngine engine = null;
    private boolean generateBy = true;
    private String fileEncodingParamVal = null;
    private static final String GROOVY_SOURCE_ENCODING = "groovy.source.encoding";

    private Template findCachedTemplate(String key, File file) {
        TemplateCacheEntry entry;
        Template template = null;
        if (this.verbose) {
            this.log("Looking for cached template by key \"" + key + "\"");
        }
        if ((entry = this.cache.get(key)) != null) {
            if (entry.validate(file)) {
                if (this.verbose) {
                    this.log("Cache hit! " + entry);
                }
                template = entry.template;
            } else if (this.verbose) {
                this.log("Cached template " + key + " needs recompilation! " + entry);
            }
        } else if (this.verbose) {
            this.log("Cache miss for " + key);
        }
        return template;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Template createAndStoreTemplate(String key, InputStream inputStream, File file) throws Exception {
        if (this.verbose) {
            this.log("Creating new template from " + key + "...");
        }
        Reader reader = null;
        try {
            String fileEncoding = this.fileEncodingParamVal != null ? this.fileEncodingParamVal : System.getProperty(GROOVY_SOURCE_ENCODING);
            reader = fileEncoding == null ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, fileEncoding);
            Template template = this.engine.createTemplate(reader);
            this.cache.put(key, new TemplateCacheEntry(file, template, this.verbose));
            if (this.verbose) {
                this.log("Created and added template to cache. [key=" + key + "] " + this.cache.get(key));
            }
            if (template == null) {
                throw new ServletException("Template is null? Should not happen here!");
            }
            Template template2 = template;
            return template2;
        }
        finally {
            if (reader != null) {
                reader.close();
            } else if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    protected Template getTemplate(File file) throws ServletException {
        String key = file.getAbsolutePath();
        Template template = this.findCachedTemplate(key, file);
        if (template == null) {
            try {
                template = this.createAndStoreTemplate(key, new FileInputStream(file), file);
            }
            catch (Exception e) {
                throw new ServletException("Creation of template failed: " + e, (Throwable)e);
            }
        }
        return template;
    }

    protected Template getTemplate(URL url) throws ServletException {
        String key = url.toString();
        Template template = this.findCachedTemplate(key, null);
        if (template == null) {
            try {
                template = this.createAndStoreTemplate(key, url.openConnection().getInputStream(), null);
            }
            catch (Exception e) {
                throw new ServletException("Creation of template failed: " + e, (Throwable)e);
            }
        }
        return template;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.engine = this.initTemplateEngine(config);
        if (this.engine == null) {
            throw new ServletException("Template engine not instantiated.");
        }
        String value = config.getInitParameter("generated.by");
        if (value != null) {
            this.generateBy = Boolean.valueOf(value);
        }
        if ((value = config.getInitParameter(GROOVY_SOURCE_ENCODING)) != null) {
            this.fileEncodingParamVal = value;
        }
        this.log("Servlet " + this.getClass().getName() + " initialized on " + this.engine.getClass());
    }

    protected TemplateEngine initTemplateEngine(ServletConfig config) {
        String name = config.getInitParameter("template.engine");
        if (name == null) {
            return new SimpleTemplateEngine();
        }
        try {
            return (TemplateEngine)Class.forName(name).newInstance();
        }
        catch (InstantiationException e) {
            this.log("Could not instantiate template engine: " + name, e);
        }
        catch (IllegalAccessException e) {
            this.log("Could not access template engine class: " + name, e);
        }
        catch (ClassNotFoundException e) {
            this.log("Could not find template engine class: " + name, e);
        }
        return null;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Template template;
        long getMillis;
        String name;
        File file;
        if (this.verbose) {
            this.log("Creating/getting cached template...");
        }
        if ((file = this.getScriptUriAsFile(request)) != null) {
            name = file.getName();
            if (!file.exists()) {
                response.sendError(404);
                return;
            }
            if (!file.canRead()) {
                response.sendError(403, "Can not read \"" + name + "\"!");
                return;
            }
            getMillis = System.currentTimeMillis();
            template = this.getTemplate(file);
            getMillis = System.currentTimeMillis() - getMillis;
        } else {
            name = this.getScriptUri(request);
            URL url = this.servletContext.getResource(name);
            if (url == null) {
                response.sendError(404);
                return;
            }
            getMillis = System.currentTimeMillis();
            template = this.getTemplate(url);
            getMillis = System.currentTimeMillis() - getMillis;
        }
        ServletBinding binding = new ServletBinding(request, response, this.servletContext);
        this.setVariables(binding);
        response.setContentType("text/html; charset=" + this.encoding);
        response.setStatus(200);
        Writer out = (Writer)binding.getVariable("out");
        if (out == null) {
            out = response.getWriter();
        }
        if (this.verbose) {
            this.log("Making template \"" + name + "\"...");
        }
        long makeMillis = System.currentTimeMillis();
        template.make(binding.getVariables()).writeTo(out);
        makeMillis = System.currentTimeMillis() - makeMillis;
        if (this.generateBy) {
            StringBuilder sb = new StringBuilder(100);
            sb.append("\n<!-- Generated by Groovy TemplateServlet [create/get=");
            sb.append(Long.toString(getMillis));
            sb.append(" ms, make=");
            sb.append(Long.toString(makeMillis));
            sb.append(" ms] -->\n");
            out.write(sb.toString());
        }
        response.flushBuffer();
        if (this.verbose) {
            this.log("Template \"" + name + "\" request responded. [create/get=" + getMillis + " ms, make=" + makeMillis + " ms]");
        }
    }

    private static class TemplateCacheEntry {
        Date date;
        long hit;
        long lastModified;
        long length;
        Template template;

        public TemplateCacheEntry(File file, Template template, boolean timestamp) {
            if (template == null) {
                throw new NullPointerException("template");
            }
            this.date = timestamp ? new Date(System.currentTimeMillis()) : null;
            this.hit = 0L;
            if (file != null) {
                this.lastModified = file.lastModified();
                this.length = file.length();
            }
            this.template = template;
        }

        public boolean validate(File file) {
            if (file != null) {
                if (file.lastModified() != this.lastModified) {
                    return false;
                }
                if (file.length() != this.length) {
                    return false;
                }
            }
            ++this.hit;
            return true;
        }

        public String toString() {
            if (this.date == null) {
                return "Hit #" + this.hit;
            }
            return "Hit #" + this.hit + " since " + this.date;
        }
    }
}

