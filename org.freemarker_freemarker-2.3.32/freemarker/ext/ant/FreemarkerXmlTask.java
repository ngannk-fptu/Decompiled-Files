/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.DirectoryScanner
 *  org.apache.tools.ant.taskdefs.MatchingTask
 */
package freemarker.ext.ant;

import freemarker.core.Environment;
import freemarker.ext.ant.JythonAntTask;
import freemarker.ext.dom.NodeModel;
import freemarker.ext.xml.NodeListModel;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.SecurityUtilities;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

@Deprecated
public class FreemarkerXmlTask
extends MatchingTask {
    private JythonAntTask prepareModel;
    private JythonAntTask prepareEnvironment;
    private final DocumentBuilderFactory builderFactory;
    private DocumentBuilder builder;
    private Configuration cfg = new Configuration();
    private File destDir;
    private File baseDir;
    private File templateDir;
    private String templateName;
    private Template parsedTemplate;
    private long templateFileLastModified = 0L;
    private String projectAttribute = null;
    private File projectFile = null;
    private TemplateModel projectTemplate;
    private TemplateNodeModel projectNode;
    private TemplateModel propertiesTemplate;
    private TemplateModel userPropertiesTemplate;
    private long projectFileLastModified = 0L;
    private boolean incremental = true;
    private String extension = ".html";
    private String encoding;
    private String templateEncoding = this.encoding = SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
    private boolean validation = false;
    private String models = "";
    private final Map modelsMap = new HashMap();

    public FreemarkerXmlTask() {
        this.builderFactory = DocumentBuilderFactory.newInstance();
        this.builderFactory.setNamespaceAware(true);
    }

    public void setBasedir(File dir) {
        this.baseDir = dir;
    }

    public void setDestdir(File dir) {
        this.destDir = dir;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setTemplate(String templateName) {
        this.templateName = templateName;
    }

    public void setTemplateDir(File templateDir) throws BuildException {
        this.templateDir = templateDir;
        try {
            this.cfg.setDirectoryForTemplateLoading(templateDir);
        }
        catch (Exception e) {
            throw new BuildException((Throwable)e);
        }
    }

    public void setProjectfile(String projectAttribute) {
        this.projectAttribute = projectAttribute;
    }

    public void setIncremental(String incremental) {
        this.incremental = !incremental.equalsIgnoreCase("false") && !incremental.equalsIgnoreCase("no") && !incremental.equalsIgnoreCase("off");
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setTemplateEncoding(String inputEncoding) {
        this.templateEncoding = inputEncoding;
    }

    public void setValidation(boolean validation) {
        this.validation = validation;
    }

    public void setModels(String models) {
        this.models = models;
    }

    public void execute() throws BuildException {
        if (this.baseDir == null) {
            this.baseDir = this.getProject().getBaseDir();
        }
        if (this.destDir == null) {
            String msg = "destdir attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        File templateFile = null;
        if (this.templateDir == null) {
            if (this.templateName != null) {
                templateFile = new File(this.templateName);
                if (!templateFile.isAbsolute()) {
                    templateFile = new File(this.getProject().getBaseDir(), this.templateName);
                }
                this.templateDir = templateFile.getParentFile();
                this.templateName = templateFile.getName();
            } else {
                this.templateDir = this.baseDir;
            }
            this.setTemplateDir(this.templateDir);
        } else if (this.templateName != null) {
            if (new File(this.templateName).isAbsolute()) {
                throw new BuildException("Do not specify an absolute location for the template as well as a templateDir");
            }
            templateFile = new File(this.templateDir, this.templateName);
        }
        if (templateFile != null) {
            this.templateFileLastModified = templateFile.lastModified();
        }
        try {
            if (this.templateName != null) {
                this.parsedTemplate = this.cfg.getTemplate(this.templateName, this.templateEncoding);
            }
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.toString());
        }
        this.log("Transforming into: " + this.destDir.getAbsolutePath(), 2);
        if (this.projectAttribute != null && this.projectAttribute.length() > 0) {
            this.projectFile = new File(this.baseDir, this.projectAttribute);
            if (this.projectFile.isFile()) {
                this.projectFileLastModified = this.projectFile.lastModified();
            } else {
                this.log("Project file is defined, but could not be located: " + this.projectFile.getAbsolutePath(), 2);
                this.projectFile = null;
            }
        }
        this.generateModels();
        DirectoryScanner scanner = this.getDirectoryScanner(this.baseDir);
        this.propertiesTemplate = FreemarkerXmlTask.wrapMap(this.project.getProperties());
        this.userPropertiesTemplate = FreemarkerXmlTask.wrapMap(this.project.getUserProperties());
        this.builderFactory.setValidating(this.validation);
        try {
            this.builder = this.builderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new BuildException("Could not create document builder", (Throwable)e, this.getLocation());
        }
        String[] list = scanner.getIncludedFiles();
        for (int i = 0; i < list.length; ++i) {
            this.process(this.baseDir, list[i], this.destDir);
        }
    }

    public void addConfiguredJython(JythonAntTask jythonAntTask) {
        this.prepareEnvironment = jythonAntTask;
    }

    public void addConfiguredPrepareModel(JythonAntTask prepareModel) {
        this.prepareModel = prepareModel;
    }

    public void addConfiguredPrepareEnvironment(JythonAntTask prepareEnvironment) {
        this.prepareEnvironment = prepareEnvironment;
    }

    private void process(File baseDir, String xmlFile, File destDir) throws BuildException {
        block23: {
            File outFile = null;
            File inFile = null;
            try {
                inFile = new File(baseDir, xmlFile);
                outFile = new File(destDir, xmlFile.substring(0, xmlFile.lastIndexOf(46)) + this.extension);
                if (this.incremental && inFile.lastModified() <= outFile.lastModified() && this.templateFileLastModified <= outFile.lastModified() && this.projectFileLastModified <= outFile.lastModified()) break block23;
                this.ensureDirectoryFor(outFile);
                this.log("Input:  " + xmlFile, 2);
                if (this.projectTemplate == null && this.projectFile != null) {
                    Document doc = this.builder.parse(this.projectFile);
                    this.projectTemplate = new NodeListModel(this.builder.parse(this.projectFile));
                    this.projectNode = NodeModel.wrap(doc);
                }
                Document docNode = this.builder.parse(inFile);
                NodeListModel document = new NodeListModel(docNode);
                NodeModel docNodeModel = NodeModel.wrap(docNode);
                HashMap<String, NodeListModel> root = new HashMap<String, NodeListModel>();
                root.put("document", document);
                this.insertDefaults(root);
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(outFile), this.encoding));){
                    if (this.parsedTemplate == null) {
                        throw new BuildException("No template file specified in build script or in XML file");
                    }
                    if (this.prepareModel != null) {
                        HashMap<String, Object> vars = new HashMap<String, Object>();
                        vars.put("model", root);
                        vars.put("doc", docNode);
                        if (this.projectNode != null) {
                            vars.put("project", ((NodeModel)this.projectNode).getNode());
                        }
                        this.prepareModel.execute(vars);
                    }
                    Environment env = this.parsedTemplate.createProcessingEnvironment(root, writer);
                    env.setCurrentVisitorNode(docNodeModel);
                    if (this.prepareEnvironment != null) {
                        HashMap<String, Object> vars = new HashMap<String, Object>();
                        vars.put("env", env);
                        vars.put("doc", docNode);
                        if (this.projectNode != null) {
                            vars.put("project", ((NodeModel)this.projectNode).getNode());
                        }
                        this.prepareEnvironment.execute(vars);
                    }
                    env.process();
                    ((Writer)writer).flush();
                }
                this.log("Output: " + outFile, 2);
            }
            catch (SAXParseException spe) {
                Exception rootCause = spe;
                if (spe.getException() != null) {
                    rootCause = spe.getException();
                }
                this.log("XML parsing error in " + inFile.getAbsolutePath(), 0);
                this.log("Line number " + spe.getLineNumber());
                this.log("Column number " + spe.getColumnNumber());
                throw new BuildException((Throwable)rootCause, this.getLocation());
            }
            catch (Throwable e) {
                if (outFile != null && !outFile.delete() && outFile.exists()) {
                    this.log("Failed to delete " + outFile, 1);
                }
                e.printStackTrace();
                throw new BuildException(e, this.getLocation());
            }
        }
    }

    private void generateModels() {
        StringTokenizer modelTokenizer = new StringTokenizer(this.models, ",; ");
        while (modelTokenizer.hasMoreTokens()) {
            String modelSpec = modelTokenizer.nextToken();
            String name = null;
            String clazz = null;
            int sep = modelSpec.indexOf(61);
            if (sep == -1) {
                clazz = modelSpec;
                int dot = clazz.lastIndexOf(46);
                name = dot == -1 ? clazz : clazz.substring(dot + 1);
            } else {
                name = modelSpec.substring(0, sep);
                clazz = modelSpec.substring(sep + 1);
            }
            try {
                this.modelsMap.put(name, ClassUtil.forName(clazz).newInstance());
            }
            catch (Exception e) {
                throw new BuildException((Throwable)e);
            }
        }
    }

    private void ensureDirectoryFor(File targetFile) throws BuildException {
        File directory = new File(targetFile.getParent());
        if (!directory.exists() && !directory.mkdirs()) {
            throw new BuildException("Unable to create directory: " + directory.getAbsolutePath(), this.getLocation());
        }
    }

    private static TemplateModel wrapMap(Map table) {
        SimpleHash model = new SimpleHash(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
        for (Map.Entry entry : table.entrySet()) {
            model.put(String.valueOf(entry.getKey()), new SimpleScalar(String.valueOf(entry.getValue())));
        }
        return model;
    }

    protected void insertDefaults(Map root) {
        root.put("properties", this.propertiesTemplate);
        root.put("userProperties", this.userPropertiesTemplate);
        if (this.projectTemplate != null) {
            root.put("project", this.projectTemplate);
            root.put("project_node", this.projectNode);
        }
        if (this.modelsMap.size() > 0) {
            for (Map.Entry entry : this.modelsMap.entrySet()) {
                root.put(entry.getKey(), entry.getValue());
            }
        }
    }
}

