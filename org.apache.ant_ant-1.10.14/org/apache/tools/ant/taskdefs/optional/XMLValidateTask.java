/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Vector;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DTDLocation;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.ParserAdapter;

public class XMLValidateTask
extends Task {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    protected static final String INIT_FAILED_MSG = "Could not start xml validation: ";
    protected boolean failOnError = true;
    protected boolean warn = true;
    protected boolean lenient = false;
    protected String readerClassName = null;
    protected File file = null;
    protected Vector<FileSet> filesets = new Vector();
    protected Path classpath;
    protected XMLReader xmlReader = null;
    protected ValidatorErrorHandler errorHandler = new ValidatorErrorHandler();
    private Vector<Attribute> attributeList = new Vector();
    private final Vector<Property> propertyList = new Vector();
    private XMLCatalog xmlCatalog = new XMLCatalog();
    public static final String MESSAGE_FILES_VALIDATED = " file(s) have been successfully validated.";
    private AntClassLoader readerLoader = null;

    public void setFailOnError(boolean fail) {
        this.failOnError = fail;
    }

    public void setWarn(boolean bool) {
        this.warn = bool;
    }

    public void setLenient(boolean bool) {
        this.lenient = bool;
    }

    public void setClassName(String className) {
        this.readerClassName = className;
    }

    public void setClasspath(Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
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

    public void setFile(File file) {
        this.file = file;
    }

    public void addConfiguredXMLCatalog(XMLCatalog catalog) {
        this.xmlCatalog.addConfiguredXMLCatalog(catalog);
    }

    public void addFileset(FileSet set) {
        this.filesets.addElement(set);
    }

    public Attribute createAttribute() {
        Attribute feature = new Attribute();
        this.attributeList.addElement(feature);
        return feature;
    }

    public Property createProperty() {
        Property prop = new Property();
        this.propertyList.addElement(prop);
        return prop;
    }

    @Override
    public void init() throws BuildException {
        super.init();
        this.xmlCatalog.setProject(this.getProject());
    }

    public DTDLocation createDTD() {
        DTDLocation dtdLocation = new DTDLocation();
        this.xmlCatalog.addDTD(dtdLocation);
        return dtdLocation;
    }

    protected EntityResolver getEntityResolver() {
        return this.xmlCatalog;
    }

    protected XMLReader getXmlReader() {
        return this.xmlReader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        try {
            int fileProcessed = 0;
            if (this.file == null && this.filesets.isEmpty()) {
                throw new BuildException("Specify at least one source - a file or a fileset.");
            }
            if (this.file != null) {
                if (this.file.exists() && this.file.canRead() && this.file.isFile()) {
                    this.doValidate(this.file);
                    ++fileProcessed;
                } else {
                    String errorMsg = "File " + this.file + " cannot be read";
                    if (this.failOnError) {
                        throw new BuildException(errorMsg);
                    }
                    this.log(errorMsg, 0);
                }
            }
            for (FileSet fs : this.filesets) {
                DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
                for (String fileName : ds.getIncludedFiles()) {
                    File srcFile = new File(fs.getDir(this.getProject()), fileName);
                    this.doValidate(srcFile);
                    ++fileProcessed;
                }
            }
            this.onSuccessfulValidation(fileProcessed);
        }
        finally {
            this.cleanup();
        }
    }

    protected void onSuccessfulValidation(int fileProcessed) {
        this.log(fileProcessed + MESSAGE_FILES_VALIDATED);
    }

    protected void initValidator() {
        this.xmlReader = this.createXmlReader();
        this.xmlReader.setEntityResolver(this.getEntityResolver());
        this.xmlReader.setErrorHandler(this.errorHandler);
        if (!this.isSax1Parser()) {
            if (!this.lenient) {
                this.setFeature("http://xml.org/sax/features/validation", true);
            }
            for (Attribute feature : this.attributeList) {
                this.setFeature(feature.getName(), feature.getValue());
            }
            for (Property prop : this.propertyList) {
                this.setProperty(prop.getName(), prop.getValue());
            }
        }
    }

    protected boolean isSax1Parser() {
        return this.xmlReader instanceof ParserAdapter;
    }

    protected XMLReader createXmlReader() {
        XMLReader newReader;
        Object reader = null;
        if (this.readerClassName == null) {
            reader = this.createDefaultReaderOrParser();
        } else {
            Class<?> readerClass = null;
            try {
                if (this.classpath != null) {
                    this.readerLoader = this.getProject().createClassLoader(this.classpath);
                    readerClass = Class.forName(this.readerClassName, true, this.readerLoader);
                } else {
                    readerClass = Class.forName(this.readerClassName);
                }
                reader = readerClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                throw new BuildException(INIT_FAILED_MSG + this.readerClassName, e);
            }
        }
        if (reader instanceof XMLReader) {
            newReader = (XMLReader)reader;
            this.log("Using SAX2 reader " + reader.getClass().getName(), 3);
        } else if (reader instanceof Parser) {
            newReader = new ParserAdapter((Parser)reader);
            this.log("Using SAX1 parser " + reader.getClass().getName(), 3);
        } else {
            throw new BuildException(INIT_FAILED_MSG + reader.getClass().getName() + " implements nor SAX1 Parser nor SAX2 XMLReader.");
        }
        return newReader;
    }

    protected void cleanup() {
        if (this.readerLoader != null) {
            this.readerLoader.cleanup();
            this.readerLoader = null;
        }
    }

    private Object createDefaultReaderOrParser() {
        Object reader;
        try {
            reader = this.createDefaultReader();
        }
        catch (BuildException exc) {
            reader = JAXPUtils.getParser();
        }
        return reader;
    }

    protected XMLReader createDefaultReader() {
        return JAXPUtils.getXMLReader();
    }

    protected void setFeature(String feature, boolean value) throws BuildException {
        this.log("Setting feature " + feature + "=" + value, 4);
        try {
            this.xmlReader.setFeature(feature, value);
        }
        catch (SAXNotRecognizedException e) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't recognize feature " + feature, e, this.getLocation());
        }
        catch (SAXNotSupportedException e) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't support feature " + feature, e, this.getLocation());
        }
    }

    protected void setProperty(String name, String value) throws BuildException {
        if (name == null || value == null) {
            throw new BuildException("Property name and value must be specified.");
        }
        try {
            this.xmlReader.setProperty(name, value);
        }
        catch (SAXNotRecognizedException e) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't recognize property " + name, e, this.getLocation());
        }
        catch (SAXNotSupportedException e) {
            throw new BuildException("Parser " + this.xmlReader.getClass().getName() + " doesn't support property " + name, e, this.getLocation());
        }
    }

    protected boolean doValidate(File afile) {
        this.initValidator();
        boolean result = true;
        try {
            this.log("Validating " + afile.getName() + "... ", 3);
            this.errorHandler.init(afile);
            InputSource is = new InputSource(Files.newInputStream(afile.toPath(), new OpenOption[0]));
            String uri = FILE_UTILS.toURI(afile.getAbsolutePath());
            is.setSystemId(uri);
            this.xmlReader.parse(is);
        }
        catch (SAXException ex) {
            this.log("Caught when validating: " + ex.toString(), 4);
            if (this.failOnError) {
                throw new BuildException("Could not validate document " + afile);
            }
            this.log("Could not validate document " + afile + ": " + ex.toString());
            result = false;
        }
        catch (IOException ex) {
            throw new BuildException("Could not validate document " + afile, ex);
        }
        if (this.errorHandler.getFailure()) {
            if (this.failOnError) {
                throw new BuildException(afile + " is not a valid XML document.");
            }
            result = false;
            this.log(afile + " is not a valid XML document", 0);
        }
        return result;
    }

    protected class ValidatorErrorHandler
    implements ErrorHandler {
        protected File currentFile = null;
        protected String lastErrorMessage = null;
        protected boolean failed = false;

        protected ValidatorErrorHandler() {
        }

        public void init(File file) {
            this.currentFile = file;
            this.failed = false;
        }

        public boolean getFailure() {
            return this.failed;
        }

        @Override
        public void fatalError(SAXParseException exception) {
            this.failed = true;
            this.doLog(exception, 0);
        }

        @Override
        public void error(SAXParseException exception) {
            this.failed = true;
            this.doLog(exception, 0);
        }

        @Override
        public void warning(SAXParseException exception) {
            if (XMLValidateTask.this.warn) {
                this.doLog(exception, 1);
            }
        }

        private void doLog(SAXParseException e, int logLevel) {
            XMLValidateTask.this.log(this.getMessage(e), logLevel);
        }

        private String getMessage(SAXParseException e) {
            String sysID = e.getSystemId();
            if (sysID != null) {
                String name = sysID;
                if (sysID.startsWith("file:")) {
                    try {
                        name = FILE_UTILS.fromURI(sysID);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                int line = e.getLineNumber();
                int col = e.getColumnNumber();
                return name + (line == -1 ? "" : ":" + line + (col == -1 ? "" : ":" + col)) + ": " + e.getMessage();
            }
            return e.getMessage();
        }
    }

    public static class Attribute {
        private String attributeName = null;
        private boolean attributeValue;

        public void setName(String name) {
            this.attributeName = name;
        }

        public void setValue(boolean value) {
            this.attributeValue = value;
        }

        public String getName() {
            return this.attributeName;
        }

        public boolean getValue() {
            return this.attributeValue;
        }
    }

    public static final class Property {
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
}

