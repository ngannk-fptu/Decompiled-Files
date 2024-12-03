/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Task
 */
package org.apache.velocity.texen.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.texen.Generator;
import org.apache.velocity.util.StringUtils;

public class TexenTask
extends Task {
    private static final String ERR_MSG_FRAGMENT = ". For more information consult the velocity log, or invoke ant with the -debug flag.";
    protected String controlTemplate;
    protected String templatePath;
    protected String outputDirectory;
    protected String outputFile;
    protected String outputEncoding;
    protected String inputEncoding;
    protected ExtendedProperties contextProperties;
    protected boolean useClasspath;
    protected String logFile;
    protected String useResourceLoaderCache = "false";
    protected String resourceLoaderModificationCheckInterval = "2";

    public void setControlTemplate(String controlTemplate) {
        this.controlTemplate = controlTemplate;
    }

    public String getControlTemplate() {
        return this.controlTemplate;
    }

    public void setTemplatePath(String templatePath) throws Exception {
        StringBuffer resolvedPath = new StringBuffer();
        StringTokenizer st = new StringTokenizer(templatePath, ",");
        while (st.hasMoreTokens()) {
            File fullPath = this.project.resolveFile(st.nextToken());
            resolvedPath.append(fullPath.getCanonicalPath());
            if (!st.hasMoreTokens()) continue;
            resolvedPath.append(",");
        }
        this.templatePath = resolvedPath.toString();
        System.out.println(templatePath);
    }

    public String getTemplatePath() {
        return this.templatePath;
    }

    public void setOutputDirectory(File outputDirectory) {
        try {
            this.outputDirectory = outputDirectory.getCanonicalPath();
        }
        catch (IOException ioe) {
            throw new BuildException((Throwable)ioe);
        }
    }

    public String getOutputDirectory() {
        return this.outputDirectory;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    public void setInputEncoding(String inputEncoding) {
        this.inputEncoding = inputEncoding;
    }

    public String getOutputFile() {
        return this.outputFile;
    }

    public void setLogFile(String log) {
        this.logFile = log;
    }

    public String getLogFile() {
        return this.logFile;
    }

    public void setContextProperties(String file) {
        String[] sources = StringUtils.split(file, ",");
        this.contextProperties = new ExtendedProperties();
        for (int i = 0; i < sources.length; ++i) {
            ExtendedProperties source = new ExtendedProperties();
            try {
                File fullPath = this.project.resolveFile(sources[i]);
                this.log("Using contextProperties file: " + fullPath);
                source.load((InputStream)new FileInputStream(fullPath));
            }
            catch (IOException e) {
                ClassLoader classLoader = ((Object)((Object)this)).getClass().getClassLoader();
                try {
                    InputStream inputStream = classLoader.getResourceAsStream(sources[i]);
                    if (inputStream == null) {
                        throw new BuildException("Context properties file " + sources[i] + " could not be found in the file system or on the classpath!");
                    }
                    source.load(inputStream);
                }
                catch (IOException ioe) {
                    source = null;
                }
            }
            if (source == null) continue;
            Iterator j = source.getKeys();
            while (j.hasNext()) {
                String name = (String)j.next();
                String value = StringUtils.nullTrim(source.getString(name));
                this.contextProperties.setProperty(name, (Object)value);
            }
        }
    }

    public ExtendedProperties getContextProperties() {
        return this.contextProperties;
    }

    public void setUseClasspath(boolean useClasspath) {
        this.useClasspath = useClasspath;
    }

    public void setUseResourceLoaderCache(String useResourceLoaderCache) {
        this.useResourceLoaderCache = useResourceLoaderCache;
    }

    public void setResourceLoaderModificationCheckInterval(String resourceLoaderModificationCheckInterval) {
        this.resourceLoaderModificationCheckInterval = resourceLoaderModificationCheckInterval;
    }

    public Context initControlContext() throws Exception {
        return new VelocityContext();
    }

    public void execute() throws BuildException {
        if (this.templatePath == null && !this.useClasspath) {
            throw new BuildException("The template path needs to be defined if you are not using the classpath for locating templates!");
        }
        if (this.controlTemplate == null) {
            throw new BuildException("The control template needs to be defined!");
        }
        if (this.outputDirectory == null) {
            throw new BuildException("The output directory needs to be defined!");
        }
        if (this.outputFile == null) {
            throw new BuildException("The output file needs to be defined!");
        }
        VelocityEngine ve = new VelocityEngine();
        try {
            File file;
            if (this.templatePath != null) {
                this.log("Using templatePath: " + this.templatePath, 3);
                ve.setProperty("file.resource.loader.path", this.templatePath);
            }
            if (this.useClasspath) {
                this.log("Using classpath");
                ve.addProperty("resource.loader", "classpath");
                ve.setProperty("classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
                ve.setProperty("classpath.resource.loader.cache", this.useResourceLoaderCache);
                ve.setProperty("classpath.resource.loader.modificationCheckInterval", this.resourceLoaderModificationCheckInterval);
            }
            if (this.logFile != null) {
                ve.setProperty("runtime.log", this.logFile);
            }
            ve.init();
            Generator generator = Generator.getInstance();
            generator.setVelocityEngine(ve);
            generator.setOutputPath(this.outputDirectory);
            generator.setInputEncoding(this.inputEncoding);
            generator.setOutputEncoding(this.outputEncoding);
            if (this.templatePath != null) {
                generator.setTemplatePath(this.templatePath);
            }
            if (!(file = new File(this.outputDirectory)).exists()) {
                file.mkdirs();
            }
            String path = this.outputDirectory + File.separator + this.outputFile;
            this.log("Generating to file " + path, 2);
            Writer writer = generator.getWriter(path, this.outputEncoding);
            Context c = this.initControlContext();
            this.populateInitialContext(c);
            if (this.contextProperties != null) {
                Iterator i = this.contextProperties.getKeys();
                while (i.hasNext()) {
                    String property = (String)i.next();
                    String value = StringUtils.nullTrim(this.contextProperties.getString(property));
                    try {
                        c.put(property, new Integer(value));
                    }
                    catch (NumberFormatException nfe) {
                        String booleanString = this.contextProperties.testBoolean(value);
                        if (booleanString != null) {
                            c.put(property, Boolean.valueOf(booleanString));
                            continue;
                        }
                        if (property.endsWith("file.contents")) {
                            value = StringUtils.fileContentsToString(this.project.resolveFile(value).getCanonicalPath());
                            property = property.substring(0, property.indexOf("file.contents") - 1);
                        }
                        c.put(property, value);
                    }
                }
            }
            writer.write(generator.parse(this.controlTemplate, c));
            writer.flush();
            writer.close();
            generator.shutdown();
            this.cleanup();
        }
        catch (BuildException e) {
            throw e;
        }
        catch (MethodInvocationException e) {
            throw new BuildException("Exception thrown by '" + e.getReferenceName() + "." + e.getMethodName() + "'" + ERR_MSG_FRAGMENT, e.getWrappedThrowable());
        }
        catch (ParseErrorException e) {
            throw new BuildException("Velocity syntax error. For more information consult the velocity log, or invoke ant with the -debug flag.", (Throwable)e);
        }
        catch (ResourceNotFoundException e) {
            throw new BuildException("Resource not found. For more information consult the velocity log, or invoke ant with the -debug flag.", (Throwable)e);
        }
        catch (Exception e) {
            throw new BuildException("Generation failed. For more information consult the velocity log, or invoke ant with the -debug flag.", (Throwable)e);
        }
    }

    protected void populateInitialContext(Context context) throws Exception {
        context.put("now", new Date().toString());
    }

    protected void cleanup() throws Exception {
    }
}

