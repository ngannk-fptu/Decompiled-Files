/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.XSLTLiaison;
import org.apache.tools.ant.taskdefs.XSLTLiaison2;
import org.apache.tools.ant.taskdefs.XSLTLiaison3;
import org.apache.tools.ant.taskdefs.XSLTLiaison4;
import org.apache.tools.ant.taskdefs.XSLTLogger;
import org.apache.tools.ant.taskdefs.XSLTLoggerAware;
import org.apache.tools.ant.taskdefs.optional.TraXLiaison;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.StringUtils;

public class XSLTProcess
extends MatchingTask
implements XSLTLogger {
    public static final String PROCESSOR_TRAX = "trax";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File destDir = null;
    private File baseDir = null;
    private String xslFile = null;
    private Resource xslResource = null;
    private String targetExtension = ".html";
    private String fileNameParameter = null;
    private String fileDirParameter = null;
    private final List<Param> params = new ArrayList<Param>();
    private File inFile = null;
    private File outFile = null;
    private String processor;
    private Path classpath = null;
    private XSLTLiaison liaison;
    private boolean stylesheetLoaded = false;
    private boolean force = false;
    private final List<OutputProperty> outputProperties = new Vector<OutputProperty>();
    private final XMLCatalog xmlCatalog = new XMLCatalog();
    private boolean performDirectoryScan = true;
    private Factory factory = null;
    private boolean reuseLoadedStylesheet = true;
    private AntClassLoader loader = null;
    private Mapper mapperElement = null;
    private final Union resources = new Union();
    private boolean useImplicitFileset = true;
    private boolean suppressWarnings = false;
    private boolean failOnTransformationError = true;
    private boolean failOnError = true;
    private boolean failOnNoResources = true;
    private XPathFactory xpathFactory;
    private XPath xpath;
    private final CommandlineJava.SysProperties sysProperties = new CommandlineJava.SysProperties();
    private TraceConfiguration traceConfiguration;

    public void setScanIncludedDirectories(boolean b) {
        this.performDirectoryScan = b;
    }

    public void setReloadStylesheet(boolean b) {
        this.reuseLoadedStylesheet = !b;
    }

    public void addMapper(Mapper mapper) {
        if (this.mapperElement != null) {
            this.handleError("Cannot define more than one mapper");
        } else {
            this.mapperElement = mapper;
        }
    }

    public void add(ResourceCollection rc) {
        this.resources.add(rc);
    }

    public void addConfiguredStyle(Resources rc) {
        if (rc.size() != 1) {
            this.handleError("The style element must be specified with exactly one nested resource.");
        } else {
            this.setXslResource(rc.iterator().next());
        }
    }

    public void setXslResource(Resource xslResource) {
        this.xslResource = xslResource;
    }

    public void add(FileNameMapper fileNameMapper) throws BuildException {
        Mapper mapper = new Mapper(this.getProject());
        mapper.add(fileNameMapper);
        this.addMapper(mapper);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        if ("style".equals(this.getTaskType())) {
            this.log("Warning: the task name <style> is deprecated. Use <xslt> instead.", 1);
        }
        File savedBaseDir = this.baseDir;
        String baseMessage = "specify the stylesheet either as a filename in style attribute or as a nested resource";
        if (this.xslResource == null && this.xslFile == null) {
            this.handleError("specify the stylesheet either as a filename in style attribute or as a nested resource");
            return;
        }
        if (this.xslResource != null && this.xslFile != null) {
            this.handleError("specify the stylesheet either as a filename in style attribute or as a nested resource but not as both");
            return;
        }
        if (this.inFile != null && !this.inFile.exists()) {
            this.handleError("input file " + this.inFile + " does not exist");
            return;
        }
        try {
            Resource styleResource;
            this.setupLoader();
            if (this.sysProperties.size() > 0) {
                this.sysProperties.setSystem();
            }
            if (this.baseDir == null) {
                this.baseDir = this.getProject().getBaseDir();
            }
            this.liaison = this.getLiaison();
            if (this.liaison instanceof XSLTLoggerAware) {
                ((XSLTLoggerAware)((Object)this.liaison)).setLogger(this);
            }
            this.log("Using " + this.liaison.getClass().toString(), 3);
            if (this.xslFile != null) {
                File alternative;
                File stylesheet = this.getProject().resolveFile(this.xslFile);
                if (!stylesheet.exists() && (alternative = FILE_UTILS.resolveFile(this.baseDir, this.xslFile)).exists()) {
                    this.log("DEPRECATED - the 'style' attribute should be relative to the project's");
                    this.log("             basedir, not the tasks's basedir.");
                    stylesheet = alternative;
                }
                FileResource fr = new FileResource();
                fr.setProject(this.getProject());
                fr.setFile(stylesheet);
                styleResource = fr;
            } else {
                styleResource = this.xslResource;
            }
            if (!styleResource.isExists()) {
                this.handleError("stylesheet " + styleResource + " doesn't exist.");
                return;
            }
            if (this.inFile != null && this.outFile != null) {
                this.process(this.inFile, this.outFile, styleResource);
                return;
            }
            this.checkDest();
            if (this.useImplicitFileset) {
                DirectoryScanner scanner = this.getDirectoryScanner(this.baseDir);
                this.log("Transforming into " + this.destDir, 2);
                for (String element : scanner.getIncludedFiles()) {
                    this.process(this.baseDir, element, this.destDir, styleResource);
                }
                if (this.performDirectoryScan) {
                    for (String dir : scanner.getIncludedDirectories()) {
                        String[] elements = new File(this.baseDir, dir).list();
                        if (elements == null) continue;
                        for (String element : new File(this.baseDir, dir).list()) {
                            this.process(this.baseDir, dir + File.separator + element, this.destDir, styleResource);
                        }
                    }
                }
            } else if (this.resources.isEmpty()) {
                if (this.failOnNoResources) {
                    this.handleError("no resources specified");
                }
                return;
            }
            this.processResources(styleResource);
        }
        finally {
            if (this.loader != null) {
                this.loader.resetThreadContextLoader();
                this.loader.cleanup();
                this.loader = null;
            }
            if (this.sysProperties.size() > 0) {
                this.sysProperties.restoreSystem();
            }
            this.liaison = null;
            this.stylesheetLoaded = false;
            this.baseDir = savedBaseDir;
        }
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public void setBasedir(File dir) {
        this.baseDir = dir;
    }

    public void setDestdir(File dir) {
        this.destDir = dir;
    }

    public void setExtension(String name) {
        this.targetExtension = name;
    }

    public void setStyle(String xslFile) {
        this.xslFile = xslFile;
    }

    public void setClasspath(Path classpath) {
        this.createClasspath().append(classpath);
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        this.createClasspath().setRefid(r);
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public void setUseImplicitFileset(boolean useimplicitfileset) {
        this.useImplicitFileset = useimplicitfileset;
    }

    public void addConfiguredXMLCatalog(XMLCatalog xmlCatalog) {
        this.xmlCatalog.addConfiguredXMLCatalog(xmlCatalog);
    }

    public void setFileNameParameter(String fileNameParameter) {
        this.fileNameParameter = fileNameParameter;
    }

    public void setFileDirParameter(String fileDirParameter) {
        this.fileDirParameter = fileDirParameter;
    }

    public void setSuppressWarnings(boolean b) {
        this.suppressWarnings = b;
    }

    public boolean getSuppressWarnings() {
        return this.suppressWarnings;
    }

    public void setFailOnTransformationError(boolean b) {
        this.failOnTransformationError = b;
    }

    public void setFailOnError(boolean b) {
        this.failOnError = b;
    }

    public void setFailOnNoResources(boolean b) {
        this.failOnNoResources = b;
    }

    public void addSysproperty(Environment.Variable sysp) {
        this.sysProperties.addVariable(sysp);
    }

    public void addSyspropertyset(PropertySet sysp) {
        this.sysProperties.addSyspropertyset(sysp);
    }

    public TraceConfiguration createTrace() {
        if (this.traceConfiguration != null) {
            throw new BuildException("can't have more than one trace configuration");
        }
        this.traceConfiguration = new TraceConfiguration();
        return this.traceConfiguration;
    }

    public TraceConfiguration getTraceConfiguration() {
        return this.traceConfiguration;
    }

    private void resolveProcessor(String proc) throws Exception {
        if (PROCESSOR_TRAX.equals(proc)) {
            this.liaison = new TraXLiaison();
        } else {
            Class<XSLTLiaison> clazz = this.loadClass(proc).asSubclass(XSLTLiaison.class);
            this.liaison = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
    }

    private Class<?> loadClass(String classname) throws ClassNotFoundException {
        this.setupLoader();
        if (this.loader == null) {
            return Class.forName(classname);
        }
        return Class.forName(classname, true, this.loader);
    }

    private void setupLoader() {
        if (this.classpath != null && this.loader == null) {
            this.loader = this.getProject().createClassLoader(this.classpath);
            this.loader.setThreadContextLoader();
        }
    }

    public void setOut(File outFile) {
        this.outFile = outFile;
    }

    public void setIn(File inFile) {
        this.inFile = inFile;
    }

    private void checkDest() {
        if (this.destDir == null) {
            this.handleError("destdir attributes must be set!");
        }
    }

    private void processResources(Resource stylesheet) {
        for (Resource r : this.resources) {
            FileResource f;
            if (!r.isExists()) continue;
            File base = this.baseDir;
            String name = r.getName();
            FileProvider fp = r.as(FileProvider.class);
            if (fp != null && (base = (f = ResourceUtils.asFileResource(fp)).getBaseDir()) == null) {
                name = f.getFile().getAbsolutePath();
            }
            this.process(base, name, this.destDir, stylesheet);
        }
    }

    private void process(File baseDir, String xmlFile, File destDir, Resource stylesheet) throws BuildException {
        File outF = null;
        try {
            long styleSheetLastModified = stylesheet.getLastModified();
            File inF = new File(baseDir, xmlFile);
            if (inF.isDirectory()) {
                this.log("Skipping " + inF + " it is a directory.", 3);
                return;
            }
            FileNameMapper mapper = this.mapperElement == null ? new StyleMapper() : this.mapperElement.getImplementation();
            String[] outFileName = mapper.mapFileName(xmlFile);
            if (outFileName == null || outFileName.length == 0) {
                this.log("Skipping " + this.inFile + " it cannot get mapped to output.", 3);
                return;
            }
            if (outFileName.length > 1) {
                this.log("Skipping " + this.inFile + " its mapping is ambiguous.", 3);
                return;
            }
            outF = new File(destDir, outFileName[0]);
            if (this.force || inF.lastModified() > outF.lastModified() || styleSheetLastModified > outF.lastModified()) {
                this.ensureDirectoryFor(outF);
                this.log("Processing " + inF + " to " + outF);
                this.configureLiaison(stylesheet);
                this.setLiaisonDynamicFileParameters(this.liaison, inF);
                this.liaison.transform(inF, outF);
            }
        }
        catch (Exception ex) {
            this.log("Failed to process " + this.inFile, 2);
            if (outF != null) {
                outF.delete();
            }
            this.handleTransformationError(ex);
        }
    }

    private void process(File inFile, File outFile, Resource stylesheet) throws BuildException {
        try {
            long styleSheetLastModified = stylesheet.getLastModified();
            this.log("In file " + inFile + " time: " + inFile.lastModified(), 4);
            this.log("Out file " + outFile + " time: " + outFile.lastModified(), 4);
            this.log("Style file " + this.xslFile + " time: " + styleSheetLastModified, 4);
            if (this.force || inFile.lastModified() >= outFile.lastModified() || styleSheetLastModified >= outFile.lastModified()) {
                this.ensureDirectoryFor(outFile);
                this.log("Processing " + inFile + " to " + outFile, 2);
                this.configureLiaison(stylesheet);
                this.setLiaisonDynamicFileParameters(this.liaison, inFile);
                this.liaison.transform(inFile, outFile);
            } else {
                this.log("Skipping input file " + inFile + " because it is older than output file " + outFile + " and so is the stylesheet " + stylesheet, 4);
            }
        }
        catch (Exception ex) {
            this.log("Failed to process " + inFile, 2);
            if (outFile != null) {
                outFile.delete();
            }
            this.handleTransformationError(ex);
        }
    }

    private void ensureDirectoryFor(File targetFile) throws BuildException {
        File directory = targetFile.getParentFile();
        if (!(directory.exists() || directory.mkdirs() || directory.isDirectory())) {
            this.handleError("Unable to create directory: " + directory.getAbsolutePath());
        }
    }

    public Factory getFactory() {
        return this.factory;
    }

    public XMLCatalog getXMLCatalog() {
        this.xmlCatalog.setProject(this.getProject());
        return this.xmlCatalog;
    }

    public Enumeration<OutputProperty> getOutputProperties() {
        return Collections.enumeration(this.outputProperties);
    }

    protected XSLTLiaison getLiaison() {
        if (this.liaison == null) {
            if (this.processor != null) {
                try {
                    this.resolveProcessor(this.processor);
                }
                catch (Exception e) {
                    this.handleError(e);
                }
            } else {
                try {
                    this.resolveProcessor(PROCESSOR_TRAX);
                }
                catch (Throwable e1) {
                    this.log(StringUtils.getStackTrace(e1), 0);
                    this.handleError(e1);
                }
            }
        }
        return this.liaison;
    }

    public Param createParam() {
        Param p = new Param();
        this.params.add(p);
        return p;
    }

    public OutputProperty createOutputProperty() {
        OutputProperty p = new OutputProperty();
        this.outputProperties.add(p);
        return p;
    }

    @Override
    public void init() throws BuildException {
        super.init();
        this.xmlCatalog.setProject(this.getProject());
        this.xpathFactory = XPathFactory.newInstance();
        this.xpath = this.xpathFactory.newXPath();
        this.xpath.setXPathVariableResolver(variableName -> this.getProject().getProperty(variableName.toString()));
    }

    @Deprecated
    protected void configureLiaison(File stylesheet) throws BuildException {
        FileResource fr = new FileResource();
        fr.setProject(this.getProject());
        fr.setFile(stylesheet);
        this.configureLiaison(fr);
    }

    protected void configureLiaison(Resource stylesheet) throws BuildException {
        if (this.stylesheetLoaded && this.reuseLoadedStylesheet) {
            return;
        }
        this.stylesheetLoaded = true;
        try {
            this.log("Loading stylesheet " + stylesheet.getName(), 2);
            if (this.liaison instanceof XSLTLiaison2) {
                ((XSLTLiaison2)this.liaison).configure(this);
            }
            if (this.liaison instanceof XSLTLiaison3) {
                ((XSLTLiaison3)this.liaison).setStylesheet(stylesheet);
            } else {
                FileProvider fp = stylesheet.as(FileProvider.class);
                if (fp != null) {
                    this.liaison.setStylesheet(fp.getFile());
                } else {
                    this.handleError(this.liaison.getClass().toString() + " accepts the stylesheet only as a file");
                    return;
                }
            }
            for (Param p : this.params) {
                if (!p.shouldUse()) continue;
                Object evaluatedParam = this.evaluateParam(p);
                if (this.liaison instanceof XSLTLiaison4) {
                    ((XSLTLiaison4)this.liaison).addParam(p.getName(), evaluatedParam);
                    continue;
                }
                if (evaluatedParam == null || evaluatedParam instanceof String) {
                    this.liaison.addParam(p.getName(), (String)evaluatedParam);
                    continue;
                }
                this.log("XSLTLiaison '" + this.liaison.getClass().getName() + "' supports only String parameters. Converting parameter '" + p.getName() + "' to its String value '" + evaluatedParam, 1);
                this.liaison.addParam(p.getName(), String.valueOf(evaluatedParam));
            }
        }
        catch (Exception ex) {
            this.log("Failed to transform using stylesheet " + stylesheet, 2);
            this.handleTransformationError(ex);
        }
    }

    private Object evaluateParam(Param param) throws XPathExpressionException {
        ParamType type;
        String typeName = param.getType();
        String expression = param.getExpression();
        if (typeName == null || typeName.isEmpty()) {
            type = ParamType.STRING;
        } else {
            try {
                type = ParamType.valueOf(typeName);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid XSLT parameter type: " + typeName, e);
            }
        }
        switch (type) {
            case STRING: {
                return expression;
            }
            case BOOLEAN: {
                return Boolean.parseBoolean(expression);
            }
            case DOUBLE: {
                return Double.parseDouble(expression);
            }
            case INT: {
                return Integer.parseInt(expression);
            }
            case LONG: {
                return Long.parseLong(expression);
            }
        }
        QName xpathType = ParamType.XPATH_TYPES.get((Object)type);
        if (xpathType == null) {
            throw new IllegalArgumentException("Invalid XSLT parameter type: " + typeName);
        }
        XPathExpression xpe = this.xpath.compile(expression);
        return xpe.evaluate((Object)null, xpathType);
    }

    private void setLiaisonDynamicFileParameters(XSLTLiaison liaison, File inFile) throws Exception {
        if (this.fileNameParameter != null) {
            liaison.addParam(this.fileNameParameter, inFile.getName());
        }
        if (this.fileDirParameter != null) {
            String fileName = FileUtils.getRelativePath(this.baseDir, inFile);
            File file = new File(fileName);
            liaison.addParam(this.fileDirParameter, file.getParent() != null ? file.getParent().replace('\\', '/') : ".");
        }
    }

    public Factory createFactory() throws BuildException {
        if (this.factory != null) {
            this.handleError("'factory' element must be unique");
        } else {
            this.factory = new Factory();
        }
        return this.factory;
    }

    protected void handleError(String msg) {
        if (this.failOnError) {
            throw new BuildException(msg, this.getLocation());
        }
        this.log(msg, 1);
    }

    protected void handleError(Throwable ex) {
        if (this.failOnError) {
            throw new BuildException(ex);
        }
        this.log("Caught an exception: " + ex, 1);
    }

    protected void handleTransformationError(Exception ex) {
        if (this.failOnError && this.failOnTransformationError) {
            throw new BuildException(ex);
        }
        this.log("Caught an error during transformation: " + ex, 1);
    }

    public static class Factory {
        private String name;
        private final List<Attribute> attributes = new ArrayList<Attribute>();
        private final List<Feature> features = new ArrayList<Feature>();

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void addAttribute(Attribute attr) {
            this.attributes.add(attr);
        }

        public Enumeration<Attribute> getAttributes() {
            return Collections.enumeration(this.attributes);
        }

        public void addFeature(Feature feature) {
            this.features.add(feature);
        }

        public Iterable<Feature> getFeatures() {
            return this.features;
        }

        public static class Feature {
            private String name;
            private boolean value;

            public Feature() {
            }

            public Feature(String name, boolean value) {
                this.name = name;
                this.value = value;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setValue(boolean value) {
                this.value = value;
            }

            public String getName() {
                return this.name;
            }

            public boolean getValue() {
                return this.value;
            }
        }

        public static class Attribute
        extends ProjectComponent
        implements DynamicConfigurator {
            private String name;
            private Object value;

            public String getName() {
                return this.name;
            }

            public Object getValue() {
                return this.value;
            }

            @Override
            public Object createDynamicElement(String name) throws BuildException {
                return null;
            }

            @Override
            public void setDynamicAttribute(String name, String value) throws BuildException {
                if ("name".equalsIgnoreCase(name)) {
                    this.name = value;
                } else if ("value".equalsIgnoreCase(name)) {
                    if ("true".equalsIgnoreCase(value)) {
                        this.value = Boolean.TRUE;
                    } else if ("false".equalsIgnoreCase(value)) {
                        this.value = Boolean.FALSE;
                    } else {
                        try {
                            this.value = Integer.valueOf(value);
                        }
                        catch (NumberFormatException e) {
                            this.value = value;
                        }
                    }
                } else if ("valueref".equalsIgnoreCase(name)) {
                    this.value = this.getProject().getReference(value);
                } else if ("classloaderforpath".equalsIgnoreCase(name)) {
                    this.value = ClasspathUtils.getClassLoaderForPath(this.getProject(), new Reference(this.getProject(), value));
                } else {
                    throw new BuildException("Unsupported attribute: %s", name);
                }
            }
        }
    }

    public final class TraceConfiguration {
        private boolean elements;
        private boolean extension;
        private boolean generation;
        private boolean selection;
        private boolean templates;

        public void setElements(boolean b) {
            this.elements = b;
        }

        public boolean getElements() {
            return this.elements;
        }

        public void setExtension(boolean b) {
            this.extension = b;
        }

        public boolean getExtension() {
            return this.extension;
        }

        public void setGeneration(boolean b) {
            this.generation = b;
        }

        public boolean getGeneration() {
            return this.generation;
        }

        public void setSelection(boolean b) {
            this.selection = b;
        }

        public boolean getSelection() {
            return this.selection;
        }

        public void setTemplates(boolean b) {
            this.templates = b;
        }

        public boolean getTemplates() {
            return this.templates;
        }

        public OutputStream getOutputStream() {
            return new LogOutputStream(XSLTProcess.this);
        }
    }

    private class StyleMapper
    implements FileNameMapper {
        private StyleMapper() {
        }

        @Override
        public void setFrom(String from) {
        }

        @Override
        public void setTo(String to) {
        }

        @Override
        public String[] mapFileName(String xmlFile) {
            int dotPos = xmlFile.lastIndexOf(46);
            if (dotPos > 0) {
                xmlFile = xmlFile.substring(0, dotPos);
            }
            return new String[]{xmlFile + XSLTProcess.this.targetExtension};
        }
    }

    public static class Param {
        private String name = null;
        private String expression = null;
        private String type;
        private Object ifCond;
        private Object unlessCond;
        private Project project;

        public void setProject(Project project) {
            this.project = project;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() throws BuildException {
            if (this.name == null) {
                throw new BuildException("Name attribute is missing.");
            }
            return this.name;
        }

        public String getExpression() throws BuildException {
            if (this.expression == null) {
                throw new BuildException("Expression attribute is missing.");
            }
            return this.expression;
        }

        public String getType() {
            return this.type;
        }

        public void setIf(Object ifCond) {
            this.ifCond = ifCond;
        }

        public void setIf(String ifProperty) {
            this.setIf((Object)ifProperty);
        }

        public void setUnless(Object unlessCond) {
            this.unlessCond = unlessCond;
        }

        public void setUnless(String unlessProperty) {
            this.setUnless((Object)unlessProperty);
        }

        public boolean shouldUse() {
            PropertyHelper ph = PropertyHelper.getPropertyHelper(this.project);
            return ph.testIfCondition(this.ifCond) && ph.testUnlessCondition(this.unlessCond);
        }
    }

    public static class OutputProperty {
        private String name;
        private String value;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static enum ParamType {
        STRING,
        BOOLEAN,
        INT,
        LONG,
        DOUBLE,
        XPATH_STRING,
        XPATH_BOOLEAN,
        XPATH_NUMBER,
        XPATH_NODE,
        XPATH_NODESET;

        public static final Map<ParamType, QName> XPATH_TYPES;

        static {
            EnumMap<ParamType, QName> m = new EnumMap<ParamType, QName>(ParamType.class);
            m.put(XPATH_STRING, XPathConstants.STRING);
            m.put(XPATH_BOOLEAN, XPathConstants.BOOLEAN);
            m.put(XPATH_NUMBER, XPathConstants.NUMBER);
            m.put(XPATH_NODE, XPathConstants.NODE);
            m.put(XPATH_NODESET, XPathConstants.NODESET);
            XPATH_TYPES = Collections.unmodifiableMap(m);
        }
    }
}

