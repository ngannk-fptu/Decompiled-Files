/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.trax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.xalan.xsltc.compiler.SourceLoader;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.dom.XSLTCDTMManager;
import org.apache.xalan.xsltc.trax.DOM2SAX;
import org.apache.xalan.xsltc.trax.ObjectFactory;
import org.apache.xalan.xsltc.trax.TemplatesHandlerImpl;
import org.apache.xalan.xsltc.trax.TemplatesImpl;
import org.apache.xalan.xsltc.trax.TrAXFilter;
import org.apache.xalan.xsltc.trax.TransformerHandlerImpl;
import org.apache.xalan.xsltc.trax.TransformerImpl;
import org.apache.xalan.xsltc.trax.Util;
import org.apache.xml.utils.StopParseException;
import org.apache.xml.utils.StylesheetPIHandler;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TransformerFactoryImpl
extends SAXTransformerFactory
implements SourceLoader,
ErrorListener {
    public static final String TRANSLET_NAME = "translet-name";
    public static final String DESTINATION_DIRECTORY = "destination-directory";
    public static final String PACKAGE_NAME = "package-name";
    public static final String JAR_NAME = "jar-name";
    public static final String GENERATE_TRANSLET = "generate-translet";
    public static final String AUTO_TRANSLET = "auto-translet";
    public static final String USE_CLASSPATH = "use-classpath";
    public static final String DEBUG = "debug";
    public static final String ENABLE_INLINING = "enable-inlining";
    public static final String INDENT_NUMBER = "indent-number";
    private ErrorListener _errorListener = this;
    private URIResolver _uriResolver = null;
    protected static final String DEFAULT_TRANSLET_NAME = "GregorSamsa";
    private String _transletName = "GregorSamsa";
    private String _destinationDirectory = null;
    private String _packageName = null;
    private String _jarFileName = null;
    private Hashtable _piParams = null;
    private boolean _debug = false;
    private boolean _enableInlining = false;
    private boolean _generateTranslet = false;
    private boolean _autoTranslet = false;
    private boolean _useClasspath = false;
    private int _indentNumber = -1;
    private Class m_DTMManagerClass = XSLTCDTMManager.getDTMManagerClass();
    private boolean _isSecureProcessing = false;

    @Override
    public void setErrorListener(ErrorListener listener) throws IllegalArgumentException {
        if (listener == null) {
            ErrorMsg err = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "TransformerFactory");
            throw new IllegalArgumentException(err.toString());
        }
        this._errorListener = listener;
    }

    @Override
    public ErrorListener getErrorListener() {
        return this._errorListener;
    }

    @Override
    public Object getAttribute(String name) throws IllegalArgumentException {
        if (name.equals(TRANSLET_NAME)) {
            return this._transletName;
        }
        if (name.equals(GENERATE_TRANSLET)) {
            return this._generateTranslet ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equals(AUTO_TRANSLET)) {
            return this._autoTranslet ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equals(ENABLE_INLINING)) {
            if (this._enableInlining) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        ErrorMsg err = new ErrorMsg("JAXP_INVALID_ATTR_ERR", name);
        throw new IllegalArgumentException(err.toString());
    }

    @Override
    public void setAttribute(String name, Object value) throws IllegalArgumentException {
        if (name.equals(TRANSLET_NAME) && value instanceof String) {
            this._transletName = (String)value;
            return;
        }
        if (name.equals(DESTINATION_DIRECTORY) && value instanceof String) {
            this._destinationDirectory = (String)value;
            return;
        }
        if (name.equals(PACKAGE_NAME) && value instanceof String) {
            this._packageName = (String)value;
            return;
        }
        if (name.equals(JAR_NAME) && value instanceof String) {
            this._jarFileName = (String)value;
            return;
        }
        if (name.equals(GENERATE_TRANSLET)) {
            if (value instanceof Boolean) {
                this._generateTranslet = (Boolean)value;
                return;
            }
            if (value instanceof String) {
                this._generateTranslet = ((String)value).equalsIgnoreCase("true");
                return;
            }
        } else if (name.equals(AUTO_TRANSLET)) {
            if (value instanceof Boolean) {
                this._autoTranslet = (Boolean)value;
                return;
            }
            if (value instanceof String) {
                this._autoTranslet = ((String)value).equalsIgnoreCase("true");
                return;
            }
        } else if (name.equals(USE_CLASSPATH)) {
            if (value instanceof Boolean) {
                this._useClasspath = (Boolean)value;
                return;
            }
            if (value instanceof String) {
                this._useClasspath = ((String)value).equalsIgnoreCase("true");
                return;
            }
        } else if (name.equals(DEBUG)) {
            if (value instanceof Boolean) {
                this._debug = (Boolean)value;
                return;
            }
            if (value instanceof String) {
                this._debug = ((String)value).equalsIgnoreCase("true");
                return;
            }
        } else if (name.equals(ENABLE_INLINING)) {
            if (value instanceof Boolean) {
                this._enableInlining = (Boolean)value;
                return;
            }
            if (value instanceof String) {
                this._enableInlining = ((String)value).equalsIgnoreCase("true");
                return;
            }
        } else if (name.equals(INDENT_NUMBER)) {
            if (value instanceof String) {
                try {
                    this._indentNumber = Integer.parseInt((String)value);
                    return;
                }
                catch (NumberFormatException numberFormatException) {}
            } else if (value instanceof Integer) {
                this._indentNumber = (Integer)value;
                return;
            }
        }
        ErrorMsg err = new ErrorMsg("JAXP_INVALID_ATTR_ERR", name);
        throw new IllegalArgumentException(err.toString());
    }

    @Override
    public void setFeature(String name, boolean value) throws TransformerConfigurationException {
        if (name == null) {
            ErrorMsg err = new ErrorMsg("JAXP_SET_FEATURE_NULL_NAME");
            throw new NullPointerException(err.toString());
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this._isSecureProcessing = value;
            return;
        }
        ErrorMsg err = new ErrorMsg("JAXP_UNSUPPORTED_FEATURE", name);
        throw new TransformerConfigurationException(err.toString());
    }

    @Override
    public boolean getFeature(String name) {
        String[] features = new String[]{"http://javax.xml.transform.dom.DOMSource/feature", "http://javax.xml.transform.dom.DOMResult/feature", "http://javax.xml.transform.sax.SAXSource/feature", "http://javax.xml.transform.sax.SAXResult/feature", "http://javax.xml.transform.stream.StreamSource/feature", "http://javax.xml.transform.stream.StreamResult/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature", "http://javax.xml.transform.sax.SAXTransformerFactory/feature/xmlfilter"};
        if (name == null) {
            ErrorMsg err = new ErrorMsg("JAXP_GET_FEATURE_NULL_NAME");
            throw new NullPointerException(err.toString());
        }
        for (int i = 0; i < features.length; ++i) {
            if (!name.equals(features[i])) continue;
            return true;
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this._isSecureProcessing;
        }
        return false;
    }

    @Override
    public URIResolver getURIResolver() {
        return this._uriResolver;
    }

    @Override
    public void setURIResolver(URIResolver resolver) {
        this._uriResolver = resolver;
    }

    @Override
    public Source getAssociatedStylesheet(Source source, String media, String title, String charset) throws TransformerConfigurationException {
        XMLReader reader = null;
        InputSource isource = null;
        StylesheetPIHandler _stylesheetPIHandler = new StylesheetPIHandler(null, media, title, charset);
        try {
            if (source instanceof DOMSource) {
                DOMSource domsrc = (DOMSource)source;
                String baseId = domsrc.getSystemId();
                Node node = domsrc.getNode();
                DOM2SAX dom2sax = new DOM2SAX(node);
                _stylesheetPIHandler.setBaseId(baseId);
                dom2sax.setContentHandler(_stylesheetPIHandler);
                dom2sax.parse();
            } else {
                SAXParser jaxpParser;
                isource = SAXSource.sourceToInputSource(source);
                String baseId = isource.getSystemId();
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                if (this._isSecureProcessing) {
                    try {
                        factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                    }
                    catch (SAXException node) {
                        // empty catch block
                    }
                }
                if ((reader = (jaxpParser = factory.newSAXParser()).getXMLReader()) == null) {
                    reader = XMLReaderFactory.createXMLReader();
                }
                _stylesheetPIHandler.setBaseId(baseId);
                reader.setContentHandler(_stylesheetPIHandler);
                reader.parse(isource);
            }
            if (this._uriResolver != null) {
                _stylesheetPIHandler.setURIResolver(this._uriResolver);
            }
        }
        catch (StopParseException factory) {
        }
        catch (ParserConfigurationException e) {
            throw new TransformerConfigurationException("getAssociatedStylesheets failed", e);
        }
        catch (SAXException se) {
            throw new TransformerConfigurationException("getAssociatedStylesheets failed", se);
        }
        catch (IOException ioe) {
            throw new TransformerConfigurationException("getAssociatedStylesheets failed", ioe);
        }
        return _stylesheetPIHandler.getAssociatedStylesheet();
    }

    @Override
    public Transformer newTransformer() throws TransformerConfigurationException {
        TransformerImpl result = new TransformerImpl(new Properties(), this._indentNumber, this);
        if (this._uriResolver != null) {
            result.setURIResolver(this._uriResolver);
        }
        if (this._isSecureProcessing) {
            result.setSecureProcessing(true);
        }
        return result;
    }

    @Override
    public Transformer newTransformer(Source source) throws TransformerConfigurationException {
        Templates templates = this.newTemplates(source);
        Transformer transformer = templates.newTransformer();
        if (this._uriResolver != null) {
            transformer.setURIResolver(this._uriResolver);
        }
        return transformer;
    }

    private void passWarningsToListener(Vector messages) throws TransformerException {
        if (this._errorListener == null || messages == null) {
            return;
        }
        int count = messages.size();
        for (int pos = 0; pos < count; ++pos) {
            ErrorMsg msg = (ErrorMsg)messages.elementAt(pos);
            if (msg.isWarningError()) {
                this._errorListener.error(new TransformerConfigurationException(msg.toString()));
                continue;
            }
            this._errorListener.warning(new TransformerConfigurationException(msg.toString()));
        }
    }

    private void passErrorsToListener(Vector messages) {
        try {
            if (this._errorListener == null || messages == null) {
                return;
            }
            int count = messages.size();
            for (int pos = 0; pos < count; ++pos) {
                String message = messages.elementAt(pos).toString();
                this._errorListener.error(new TransformerException(message));
            }
        }
        catch (TransformerException transformerException) {
            // empty catch block
        }
    }

    @Override
    public Templates newTemplates(Source source) throws TransformerConfigurationException {
        PIParamWrapper p;
        if (this._useClasspath) {
            String transletName = this.getTransletBaseName(source);
            if (this._packageName != null) {
                transletName = this._packageName + "." + transletName;
            }
            try {
                Class clazz = ObjectFactory.findProviderClass(transletName, ObjectFactory.findClassLoader(), true);
                this.resetTransientAttributes();
                return new TemplatesImpl(new Class[]{clazz}, transletName, null, this._indentNumber, this);
            }
            catch (ClassNotFoundException cnfe) {
                ErrorMsg err = new ErrorMsg("CLASS_NOT_FOUND_ERR", transletName);
                throw new TransformerConfigurationException(err.toString());
            }
            catch (Exception e) {
                ErrorMsg err = new ErrorMsg(new ErrorMsg("RUNTIME_ERROR_KEY") + e.getMessage());
                throw new TransformerConfigurationException(err.toString());
            }
        }
        if (this._autoTranslet) {
            byte[][] bytecodes = null;
            String transletClassName = this.getTransletBaseName(source);
            if (this._packageName != null) {
                transletClassName = this._packageName + "." + transletClassName;
            }
            if ((bytecodes = this._jarFileName != null ? this.getBytecodesFromJar(source, transletClassName) : this.getBytecodesFromClasses(source, transletClassName)) != null) {
                if (this._debug) {
                    if (this._jarFileName != null) {
                        System.err.println(new ErrorMsg("TRANSFORM_WITH_JAR_STR", (Object)transletClassName, (Object)this._jarFileName));
                    } else {
                        System.err.println(new ErrorMsg("TRANSFORM_WITH_TRANSLET_STR", transletClassName));
                    }
                }
                this.resetTransientAttributes();
                return new TemplatesImpl(bytecodes, transletClassName, null, this._indentNumber, this);
            }
        }
        XSLTC xsltc = new XSLTC();
        if (this._debug) {
            xsltc.setDebug(true);
        }
        if (this._enableInlining) {
            xsltc.setTemplateInlining(true);
        } else {
            xsltc.setTemplateInlining(false);
        }
        if (this._isSecureProcessing) {
            xsltc.setSecureProcessing(true);
        }
        xsltc.init();
        if (this._uriResolver != null) {
            xsltc.setSourceLoader(this);
        }
        if (this._piParams != null && this._piParams.get(source) != null && (p = (PIParamWrapper)this._piParams.get(source)) != null) {
            xsltc.setPIParameters(p._media, p._title, p._charset);
        }
        int outputType = 2;
        if (this._generateTranslet || this._autoTranslet) {
            xsltc.setClassName(this.getTransletBaseName(source));
            if (this._destinationDirectory != null) {
                xsltc.setDestDirectory(this._destinationDirectory);
            } else {
                File xslFile;
                String xslDir;
                String xslName = this.getStylesheetFileName(source);
                if (xslName != null && (xslDir = (xslFile = new File(xslName)).getParent()) != null) {
                    xsltc.setDestDirectory(xslDir);
                }
            }
            if (this._packageName != null) {
                xsltc.setPackageName(this._packageName);
            }
            if (this._jarFileName != null) {
                xsltc.setJarFileName(this._jarFileName);
                outputType = 5;
            } else {
                outputType = 4;
            }
        }
        InputSource input = Util.getInputSource(xsltc, source);
        byte[][] bytecodes = xsltc.compile(null, input, outputType);
        String transletName = xsltc.getClassName();
        if ((this._generateTranslet || this._autoTranslet) && bytecodes != null && this._jarFileName != null) {
            try {
                xsltc.outputToJar();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.resetTransientAttributes();
        if (this._errorListener != this) {
            try {
                this.passWarningsToListener(xsltc.getWarnings());
            }
            catch (TransformerException e) {
                throw new TransformerConfigurationException(e);
            }
        } else {
            xsltc.printWarnings();
        }
        if (bytecodes == null) {
            ErrorMsg err = new ErrorMsg("JAXP_COMPILE_ERR");
            TransformerConfigurationException exc = new TransformerConfigurationException(err.toString());
            if (this._errorListener != null) {
                this.passErrorsToListener(xsltc.getErrors());
                try {
                    this._errorListener.fatalError(exc);
                }
                catch (TransformerException transformerException) {}
            } else {
                xsltc.printErrors();
            }
            throw exc;
        }
        return new TemplatesImpl(bytecodes, transletName, xsltc.getOutputProperties(), this._indentNumber, this);
    }

    @Override
    public TemplatesHandler newTemplatesHandler() throws TransformerConfigurationException {
        TemplatesHandlerImpl handler = new TemplatesHandlerImpl(this._indentNumber, this);
        if (this._uriResolver != null) {
            handler.setURIResolver(this._uriResolver);
        }
        return handler;
    }

    @Override
    public TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
        Transformer transformer = this.newTransformer();
        if (this._uriResolver != null) {
            transformer.setURIResolver(this._uriResolver);
        }
        return new TransformerHandlerImpl((TransformerImpl)transformer);
    }

    @Override
    public TransformerHandler newTransformerHandler(Source src) throws TransformerConfigurationException {
        Transformer transformer = this.newTransformer(src);
        if (this._uriResolver != null) {
            transformer.setURIResolver(this._uriResolver);
        }
        return new TransformerHandlerImpl((TransformerImpl)transformer);
    }

    @Override
    public TransformerHandler newTransformerHandler(Templates templates) throws TransformerConfigurationException {
        Transformer transformer = templates.newTransformer();
        TransformerImpl internal = (TransformerImpl)transformer;
        return new TransformerHandlerImpl(internal);
    }

    @Override
    public XMLFilter newXMLFilter(Source src) throws TransformerConfigurationException {
        Templates templates = this.newTemplates(src);
        if (templates == null) {
            return null;
        }
        return this.newXMLFilter(templates);
    }

    @Override
    public XMLFilter newXMLFilter(Templates templates) throws TransformerConfigurationException {
        try {
            return new TrAXFilter(templates);
        }
        catch (TransformerConfigurationException e1) {
            if (this._errorListener != null) {
                try {
                    this._errorListener.fatalError(e1);
                    return null;
                }
                catch (TransformerException e2) {
                    new TransformerConfigurationException(e2);
                }
            }
            throw e1;
        }
    }

    @Override
    public void error(TransformerException e) throws TransformerException {
        Throwable wrapped = e.getException();
        if (wrapped != null) {
            System.err.println(new ErrorMsg("ERROR_PLUS_WRAPPED_MSG", (Object)e.getMessageAndLocation(), (Object)wrapped.getMessage()));
        } else {
            System.err.println(new ErrorMsg("ERROR_MSG", e.getMessageAndLocation()));
        }
        throw e;
    }

    @Override
    public void fatalError(TransformerException e) throws TransformerException {
        Throwable wrapped = e.getException();
        if (wrapped != null) {
            System.err.println(new ErrorMsg("FATAL_ERR_PLUS_WRAPPED_MSG", (Object)e.getMessageAndLocation(), (Object)wrapped.getMessage()));
        } else {
            System.err.println(new ErrorMsg("FATAL_ERR_MSG", e.getMessageAndLocation()));
        }
        throw e;
    }

    @Override
    public void warning(TransformerException e) throws TransformerException {
        Throwable wrapped = e.getException();
        if (wrapped != null) {
            System.err.println(new ErrorMsg("WARNING_PLUS_WRAPPED_MSG", (Object)e.getMessageAndLocation(), (Object)wrapped.getMessage()));
        } else {
            System.err.println(new ErrorMsg("WARNING_MSG", e.getMessageAndLocation()));
        }
    }

    @Override
    public InputSource loadSource(String href, String context, XSLTC xsltc) {
        try {
            Source source;
            if (this._uriResolver != null && (source = this._uriResolver.resolve(href, context)) != null) {
                return Util.getInputSource(xsltc, source);
            }
        }
        catch (TransformerException transformerException) {
            // empty catch block
        }
        return null;
    }

    private void resetTransientAttributes() {
        this._transletName = DEFAULT_TRANSLET_NAME;
        this._destinationDirectory = null;
        this._packageName = null;
        this._jarFileName = null;
    }

    private byte[][] getBytecodesFromClasses(Source source, String fullClassName) {
        byte[] bytes;
        int lastDotIndex;
        if (fullClassName == null) {
            return null;
        }
        String xslFileName = this.getStylesheetFileName(source);
        File xslFile = null;
        if (xslFileName != null) {
            xslFile = new File(xslFileName);
        }
        String transletName = (lastDotIndex = fullClassName.lastIndexOf(46)) > 0 ? fullClassName.substring(lastDotIndex + 1) : fullClassName;
        String transletPath = fullClassName.replace('.', '/');
        transletPath = this._destinationDirectory != null ? this._destinationDirectory + "/" + transletPath + ".class" : (xslFile != null && xslFile.getParent() != null ? xslFile.getParent() + "/" + transletPath + ".class" : transletPath + ".class");
        File transletFile = new File(transletPath);
        if (!transletFile.exists()) {
            return null;
        }
        if (xslFile != null && xslFile.exists()) {
            long xslTimestamp = xslFile.lastModified();
            long transletTimestamp = transletFile.lastModified();
            if (transletTimestamp < xslTimestamp) {
                return null;
            }
        }
        ArrayList<byte[]> bytecodes = new ArrayList<byte[]>();
        int fileLength = (int)transletFile.length();
        if (fileLength > 0) {
            FileInputStream input = null;
            try {
                input = new FileInputStream(transletFile);
            }
            catch (FileNotFoundException e) {
                return null;
            }
            bytes = new byte[fileLength];
            try {
                this.readFromInputStream(bytes, input, fileLength);
                input.close();
            }
            catch (IOException e) {
                return null;
            }
        }
        return null;
        bytecodes.add(bytes);
        String transletParentDir = transletFile.getParent();
        if (transletParentDir == null) {
            transletParentDir = System.getProperty("user.dir");
        }
        File transletParentFile = new File(transletParentDir);
        final String transletAuxPrefix = transletName + "$";
        File[] auxfiles = transletParentFile.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".class") && name.startsWith(transletAuxPrefix);
            }
        });
        for (int i = 0; i < auxfiles.length; ++i) {
            File auxfile = auxfiles[i];
            int auxlength = (int)auxfile.length();
            if (auxlength <= 0) continue;
            FileInputStream auxinput = null;
            try {
                auxinput = new FileInputStream(auxfile);
            }
            catch (FileNotFoundException e) {
                continue;
            }
            byte[] bytes2 = new byte[auxlength];
            try {
                this.readFromInputStream(bytes2, auxinput, auxlength);
                auxinput.close();
            }
            catch (IOException e) {
                continue;
            }
            bytecodes.add(bytes2);
        }
        int count = bytecodes.size();
        if (count > 0) {
            byte[][] result = new byte[count][1];
            for (int i = 0; i < count; ++i) {
                result[i] = (byte[])bytecodes.get(i);
            }
            return result;
        }
        return null;
    }

    private byte[][] getBytecodesFromJar(Source source, String fullClassName) {
        String xslFileName = this.getStylesheetFileName(source);
        File xslFile = null;
        if (xslFileName != null) {
            xslFile = new File(xslFileName);
        }
        String jarPath = null;
        jarPath = this._destinationDirectory != null ? this._destinationDirectory + "/" + this._jarFileName : (xslFile != null && xslFile.getParent() != null ? xslFile.getParent() + "/" + this._jarFileName : this._jarFileName);
        File file = new File(jarPath);
        if (!file.exists()) {
            return null;
        }
        if (xslFile != null && xslFile.exists()) {
            long xslTimestamp = xslFile.lastModified();
            long transletTimestamp = file.lastModified();
            if (transletTimestamp < xslTimestamp) {
                return null;
            }
        }
        ZipFile jarFile = null;
        try {
            jarFile = new ZipFile(file);
        }
        catch (IOException e) {
            return null;
        }
        String transletPath = fullClassName.replace('.', '/');
        String transletAuxPrefix = transletPath + "$";
        String transletFullName = transletPath + ".class";
        ArrayList<byte[]> bytecodes = new ArrayList<byte[]>();
        Enumeration<? extends ZipEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entry.getSize() <= 0L || !entryName.equals(transletFullName) && (!entryName.endsWith(".class") || !entryName.startsWith(transletAuxPrefix))) continue;
            try {
                InputStream input = jarFile.getInputStream(entry);
                int size = (int)entry.getSize();
                byte[] bytes = new byte[size];
                this.readFromInputStream(bytes, input, size);
                input.close();
                bytecodes.add(bytes);
            }
            catch (IOException e) {
                return null;
            }
        }
        int count = bytecodes.size();
        if (count > 0) {
            byte[][] result = new byte[count][1];
            for (int i = 0; i < count; ++i) {
                result[i] = (byte[])bytecodes.get(i);
            }
            return result;
        }
        return null;
    }

    private void readFromInputStream(byte[] bytes, InputStream input, int size) throws IOException {
        int n = 0;
        int offset = 0;
        for (int length = size; length > 0 && (n = input.read(bytes, offset, length)) > 0; length -= n) {
            offset += n;
        }
    }

    private String getTransletBaseName(Source source) {
        String baseName;
        String transletBaseName = null;
        if (!this._transletName.equals(DEFAULT_TRANSLET_NAME)) {
            return this._transletName;
        }
        String systemId = source.getSystemId();
        if (systemId != null && (baseName = Util.baseName(systemId)) != null) {
            baseName = Util.noExtName(baseName);
            transletBaseName = Util.toJavaName(baseName);
        }
        return transletBaseName != null ? transletBaseName : DEFAULT_TRANSLET_NAME;
    }

    private String getStylesheetFileName(Source source) {
        String systemId = source.getSystemId();
        if (systemId != null) {
            File file = new File(systemId);
            if (file.exists()) {
                return systemId;
            }
            URL url = null;
            try {
                url = new URL(systemId);
            }
            catch (MalformedURLException e) {
                return null;
            }
            if ("file".equals(url.getProtocol())) {
                return url.getFile();
            }
            return null;
        }
        return null;
    }

    protected Class getDTMManagerClass() {
        return this.m_DTMManagerClass;
    }

    private static class PIParamWrapper {
        public String _media = null;
        public String _title = null;
        public String _charset = null;

        public PIParamWrapper(String media, String title, String charset) {
            this._media = media;
            this._title = title;
            this._charset = charset;
        }
    }
}

