/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.codehaus.groovy.groovydoc.GroovyRootDoc;
import org.codehaus.groovy.tools.groovydoc.GroovyDocTemplateEngine;
import org.codehaus.groovy.tools.groovydoc.GroovyDocWriter;
import org.codehaus.groovy.tools.groovydoc.GroovyRootDocBuilder;
import org.codehaus.groovy.tools.groovydoc.LinkArgument;
import org.codehaus.groovy.tools.groovydoc.OutputTool;
import org.codehaus.groovy.tools.groovydoc.ResourceManager;
import org.codehaus.groovy.tools.shell.util.Logger;

public class GroovyDocTool {
    private final Logger log = Logger.create(GroovyDocTool.class);
    private final GroovyRootDocBuilder rootDocBuilder;
    private final GroovyDocTemplateEngine templateEngine;
    protected Properties properties;

    public GroovyDocTool(String[] sourcepaths) {
        this(null, sourcepaths, null);
    }

    public GroovyDocTool(ResourceManager resourceManager, String[] sourcepaths, String classTemplate) {
        this(resourceManager, sourcepaths, new String[0], new String[0], new String[]{classTemplate}, new ArrayList<LinkArgument>(), new Properties());
    }

    public GroovyDocTool(ResourceManager resourceManager, String[] sourcepaths, String[] docTemplates, String[] packageTemplates, String[] classTemplates, List<LinkArgument> links, Properties properties) {
        this.rootDocBuilder = new GroovyRootDocBuilder(this, sourcepaths, links, properties);
        String defaultCharset = Charset.defaultCharset().name();
        String fileEncoding = properties.getProperty("fileEncoding");
        String charset = properties.getProperty("charset");
        if (fileEncoding == null || fileEncoding.length() == 0) {
            fileEncoding = charset;
        }
        if (charset == null || charset.length() == 0) {
            charset = fileEncoding;
        }
        properties.setProperty("fileEncoding", fileEncoding != null && fileEncoding.length() != 0 ? fileEncoding : defaultCharset);
        properties.setProperty("charset", charset != null && charset.length() != 0 ? charset : defaultCharset);
        this.properties = properties;
        this.templateEngine = resourceManager == null ? null : new GroovyDocTemplateEngine(this, resourceManager, docTemplates, packageTemplates, classTemplates, properties);
    }

    public void add(List<String> filenames) throws IOException {
        if (this.templateEngine != null) {
            this.log.debug("Loading source files for " + filenames);
        }
        this.rootDocBuilder.buildTree(filenames);
    }

    public GroovyRootDoc getRootDoc() {
        return this.rootDocBuilder.getRootDoc();
    }

    public void renderToOutput(OutputTool output, String destdir) throws Exception {
        if ("true".equals(this.properties.getProperty("privateScope"))) {
            this.properties.setProperty("packageScope", "true");
        }
        if ("true".equals(this.properties.getProperty("packageScope"))) {
            this.properties.setProperty("protectedScope", "true");
        }
        if ("true".equals(this.properties.getProperty("protectedScope"))) {
            this.properties.setProperty("publicScope", "true");
        }
        if (this.templateEngine == null) {
            throw new UnsupportedOperationException("No template engine was found");
        }
        GroovyDocWriter writer = new GroovyDocWriter(this, output, this.templateEngine, this.properties);
        GroovyRootDoc rootDoc = this.rootDocBuilder.getRootDoc();
        writer.writeRoot(rootDoc, destdir);
        writer.writePackages(rootDoc, destdir);
        writer.writeClasses(rootDoc, destdir);
    }

    static String getPath(String filename) {
        String path = new File(filename).getParent();
        if (path == null || path.length() == 1 && !Character.isJavaIdentifierStart(path.charAt(0))) {
            path = "DefaultPackage";
        }
        return path;
    }

    static String getFile(String filename) {
        return new File(filename).getName();
    }
}

