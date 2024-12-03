/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyPackageDoc;
import org.codehaus.groovy.groovydoc.GroovyRootDoc;
import org.codehaus.groovy.tools.groovydoc.GroovyDocTemplateEngine;
import org.codehaus.groovy.tools.groovydoc.GroovyDocTool;
import org.codehaus.groovy.tools.groovydoc.OutputTool;
import org.codehaus.groovy.tools.shell.util.Logger;

public class GroovyDocWriter {
    private final Logger log = Logger.create(GroovyDocWriter.class);
    private GroovyDocTool tool;
    private OutputTool output;
    private GroovyDocTemplateEngine templateEngine;
    private static final String FS = "/";
    private Properties properties;

    public GroovyDocWriter(GroovyDocTool tool, OutputTool output, GroovyDocTemplateEngine templateEngine, Properties properties) {
        this.tool = tool;
        this.output = output;
        this.templateEngine = templateEngine;
        this.properties = properties;
    }

    public void writeClasses(GroovyRootDoc rootDoc, String destdir) throws Exception {
        for (GroovyClassDoc classDoc : rootDoc.classes()) {
            this.writeClassToOutput(classDoc, destdir);
        }
    }

    public void writeClassToOutput(GroovyClassDoc classDoc, String destdir) throws Exception {
        if (classDoc.isPublic() || classDoc.isProtected() && "true".equals(this.properties.getProperty("protectedScope")) || classDoc.isPackagePrivate() && "true".equals(this.properties.getProperty("packageScope")) || "true".equals(this.properties.getProperty("privateScope"))) {
            String destFileName = destdir + FS + classDoc.getFullPathName() + ".html";
            this.log.debug("Generating " + destFileName);
            String renderedSrc = this.templateEngine.applyClassTemplates(classDoc);
            this.output.writeToOutput(destFileName, renderedSrc, this.properties.getProperty("fileEncoding"));
        }
    }

    public void writePackages(GroovyRootDoc rootDoc, String destdir) throws Exception {
        for (GroovyPackageDoc packageDoc : rootDoc.specifiedPackages()) {
            if (new File(packageDoc.name()).isAbsolute()) continue;
            this.output.makeOutputArea(destdir + FS + packageDoc.name());
            this.writePackageToOutput(packageDoc, destdir);
        }
        StringBuilder sb = new StringBuilder();
        for (GroovyPackageDoc packageDoc : rootDoc.specifiedPackages()) {
            sb.append(packageDoc.nameWithDots());
            sb.append("\n");
        }
        String destFileName = destdir + FS + "package-list";
        this.log.debug("Generating " + destFileName);
        this.output.writeToOutput(destFileName, sb.toString(), this.properties.getProperty("fileEncoding"));
    }

    public void writePackageToOutput(GroovyPackageDoc packageDoc, String destdir) throws Exception {
        Iterator<String> templates = this.templateEngine.packageTemplatesIterator();
        while (templates.hasNext()) {
            String template = templates.next();
            String renderedSrc = this.templateEngine.applyPackageTemplate(template, packageDoc);
            String destFileName = destdir + FS + packageDoc.name() + FS + GroovyDocTool.getFile(template);
            this.log.debug("Generating " + destFileName);
            this.output.writeToOutput(destFileName, renderedSrc, this.properties.getProperty("fileEncoding"));
        }
    }

    public void writeRoot(GroovyRootDoc rootDoc, String destdir) throws Exception {
        this.output.makeOutputArea(destdir);
        this.writeRootDocToOutput(rootDoc, destdir);
    }

    public void writeRootDocToOutput(GroovyRootDoc rootDoc, String destdir) throws Exception {
        Iterator<String> templates = this.templateEngine.docTemplatesIterator();
        while (templates.hasNext()) {
            String template = templates.next();
            String destFileName = destdir + FS + GroovyDocTool.getFile(template);
            this.log.debug("Generating " + destFileName);
            if (GroovyDocWriter.hasBinaryExtension(template)) {
                this.templateEngine.copyBinaryResource(template, destFileName);
                continue;
            }
            String renderedSrc = this.templateEngine.applyRootDocTemplate(template, rootDoc);
            this.output.writeToOutput(destFileName, renderedSrc, this.properties.getProperty("fileEncoding"));
        }
    }

    private static boolean hasBinaryExtension(String template) {
        return template.endsWith(".gif") || template.endsWith(".ico");
    }
}

