/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.texen;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.ClassUtils;

public class Generator {
    public static final String OUTPUT_PATH = "output.path";
    public static final String TEMPLATE_PATH = "template.path";
    private static final String DEFAULT_TEXEN_PROPERTIES = "org/apache/velocity/texen/defaults/texen.properties";
    private Properties props = new Properties();
    private Context controlContext;
    private Hashtable writers = new Hashtable();
    private static Generator instance = new Generator();
    protected String outputEncoding;
    protected String inputEncoding;
    protected VelocityEngine ve;

    private Generator() {
        this.setDefaultProps();
    }

    public static Generator getInstance() {
        return instance;
    }

    public void setVelocityEngine(VelocityEngine ve) {
        this.ve = ve;
    }

    public Generator(String propFile) {
        try (BufferedInputStream bi = null;){
            bi = new BufferedInputStream(new FileInputStream(propFile));
            this.props.load(bi);
        }
        catch (IOException e) {
            System.err.println("Could not load " + propFile + ", falling back to defaults. (" + e.getMessage() + ")");
            this.setDefaultProps();
        }
    }

    public Generator(Properties props) {
        this.props = (Properties)props.clone();
    }

    protected void setDefaultProps() {
        ClassLoader classLoader = VelocityEngine.class.getClassLoader();
        try (InputStream inputStream = null;){
            inputStream = classLoader.getResourceAsStream(DEFAULT_TEXEN_PROPERTIES);
            this.props.load(inputStream);
        }
        catch (IOException ioe) {
            System.err.println("Cannot get default properties: " + ioe.getMessage());
        }
    }

    public void setTemplatePath(String templatePath) {
        this.props.put(TEMPLATE_PATH, templatePath);
    }

    public String getTemplatePath() {
        return this.props.getProperty(TEMPLATE_PATH);
    }

    public void setOutputPath(String outputPath) {
        this.props.put(OUTPUT_PATH, outputPath);
    }

    public String getOutputPath() {
        return this.props.getProperty(OUTPUT_PATH);
    }

    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    public void setInputEncoding(String inputEncoding) {
        this.inputEncoding = inputEncoding;
    }

    public Writer getWriter(String path, String encoding) throws Exception {
        Writer writer = encoding == null || encoding.length() == 0 || encoding.equals("8859-1") || encoding.equals("8859_1") ? new FileWriter(path) : new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(path), encoding));
        return writer;
    }

    public Template getTemplate(String templateName, String encoding) throws Exception {
        Template template = encoding == null || encoding.length() == 0 || encoding.equals("8859-1") || encoding.equals("8859_1") ? this.ve.getTemplate(templateName) : this.ve.getTemplate(templateName, encoding);
        return template;
    }

    public String parse(String inputTemplate, String outputFile) throws Exception {
        return this.parse(inputTemplate, outputFile, null, null);
    }

    public String parse(String inputTemplate, String outputFile, String objectID, Object object) throws Exception {
        return this.parse(inputTemplate, null, outputFile, null, objectID, object);
    }

    public String parse(String inputTemplate, String inputEncoding, String outputFile, String outputEncoding, String objectID, Object object) throws Exception {
        if (objectID != null && object != null) {
            this.controlContext.put(objectID, object);
        }
        Template template = this.getTemplate(inputTemplate, inputEncoding != null ? inputEncoding : this.inputEncoding);
        if (outputFile == null || outputFile.equals("")) {
            StringWriter sw = new StringWriter();
            template.merge(this.controlContext, sw);
            return sw.toString();
        }
        Writer writer = null;
        if (this.writers.get(outputFile) == null) {
            writer = this.getWriter(this.getOutputPath() + File.separator + outputFile, outputEncoding != null ? outputEncoding : this.outputEncoding);
            this.writers.put(outputFile, writer);
        } else {
            writer = (Writer)this.writers.get(outputFile);
        }
        VelocityContext vc = new VelocityContext(this.controlContext);
        template.merge(vc, writer);
        return "";
    }

    public String parse(String controlTemplate, Context controlContext) throws Exception {
        this.controlContext = controlContext;
        this.fillContextDefaults(this.controlContext);
        this.fillContextProperties(this.controlContext);
        Template template = this.getTemplate(controlTemplate, this.inputEncoding);
        StringWriter sw = new StringWriter();
        template.merge(controlContext, sw);
        return sw.toString();
    }

    protected Context getContext(Hashtable objs) {
        this.fillContextHash(this.controlContext, objs);
        return this.controlContext;
    }

    protected void fillContextHash(Context context, Hashtable objs) {
        Enumeration enumeration = objs.keys();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement().toString();
            context.put(key, objs.get(key));
        }
    }

    protected void fillContextDefaults(Context context) {
        context.put("generator", instance);
        context.put("outputDirectory", this.getOutputPath());
    }

    protected void fillContextProperties(Context context) {
        Enumeration<?> enumeration = this.props.propertyNames();
        while (enumeration.hasMoreElements()) {
            String nm = (String)enumeration.nextElement();
            if (!nm.startsWith("context.objects.")) continue;
            String contextObj = this.props.getProperty(nm);
            int colon = nm.lastIndexOf(46);
            String contextName = nm.substring(colon + 1);
            try {
                Object o = ClassUtils.getNewInstance(contextObj);
                context.put(contextName, o);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        for (Writer writer : this.writers.values()) {
            try {
                writer.flush();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            try {
                writer.close();
            }
            catch (IOException iOException) {}
        }
        this.writers.clear();
    }
}

