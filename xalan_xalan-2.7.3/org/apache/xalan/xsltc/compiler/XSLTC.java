/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.apache.bcel.classfile.JavaClass;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SourceLoader;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public final class XSLTC {
    private Parser _parser = new Parser(this);
    private XMLReader _reader = null;
    private SourceLoader _loader = null;
    private Stylesheet _stylesheet;
    private int _modeSerial = 1;
    private int _stylesheetSerial = 1;
    private int _stepPatternSerial = 1;
    private int _helperClassSerial = 0;
    private int _attributeSetSerial = 0;
    private int[] _numberFieldIndexes;
    private int _nextGType;
    private Vector _namesIndex;
    private Hashtable _elements;
    private Hashtable _attributes;
    private int _nextNSType;
    private Vector _namespaceIndex;
    private Hashtable _namespaces;
    private Hashtable _namespacePrefixes;
    private Vector m_characterData;
    public static final int FILE_OUTPUT = 0;
    public static final int JAR_OUTPUT = 1;
    public static final int BYTEARRAY_OUTPUT = 2;
    public static final int CLASSLOADER_OUTPUT = 3;
    public static final int BYTEARRAY_AND_FILE_OUTPUT = 4;
    public static final int BYTEARRAY_AND_JAR_OUTPUT = 5;
    private boolean _debug = false;
    private String _jarFileName = null;
    private String _className = null;
    private String _packageName = null;
    private File _destDir = null;
    private int _outputType = 0;
    private Vector _classes;
    private Vector _bcelClasses;
    private boolean _callsNodeset = false;
    private boolean _multiDocument = false;
    private boolean _hasIdCall = false;
    private Vector _stylesheetNSAncestorPointers;
    private Vector _prefixURIPairs;
    private Vector _prefixURIPairsIdx;
    private boolean _templateInlining = false;
    private boolean _isSecureProcessing = false;

    public void setSecureProcessing(boolean flag) {
        this._isSecureProcessing = flag;
    }

    public boolean isSecureProcessing() {
        return this._isSecureProcessing;
    }

    public Parser getParser() {
        return this._parser;
    }

    public void setOutputType(int type) {
        this._outputType = type;
    }

    public Properties getOutputProperties() {
        return this._parser.getOutputProperties();
    }

    public void init() {
        this.reset();
        this._reader = null;
        this._classes = new Vector();
        this._bcelClasses = new Vector();
    }

    private void reset() {
        this._nextGType = 14;
        this._elements = new Hashtable();
        this._attributes = new Hashtable();
        this._namespaces = new Hashtable();
        this._namespaces.put("", new Integer(this._nextNSType));
        this._namesIndex = new Vector(128);
        this._namespaceIndex = new Vector(32);
        this._namespacePrefixes = new Hashtable();
        this._stylesheet = null;
        this._parser.init();
        this._modeSerial = 1;
        this._stylesheetSerial = 1;
        this._stepPatternSerial = 1;
        this._helperClassSerial = 0;
        this._attributeSetSerial = 0;
        this._multiDocument = false;
        this._hasIdCall = false;
        this._stylesheetNSAncestorPointers = null;
        this._prefixURIPairs = null;
        this._prefixURIPairsIdx = null;
        this._numberFieldIndexes = new int[]{-1, -1, -1};
    }

    public void setSourceLoader(SourceLoader loader) {
        this._loader = loader;
    }

    public void setTemplateInlining(boolean templateInlining) {
        this._templateInlining = templateInlining;
    }

    public boolean getTemplateInlining() {
        return this._templateInlining;
    }

    public void setPIParameters(String media, String title, String charset) {
        this._parser.setPIParameters(media, title, charset);
    }

    public boolean compile(URL url) {
        try {
            InputStream stream = url.openStream();
            InputSource input = new InputSource(stream);
            input.setSystemId(url.toString());
            return this.compile(input, this._className);
        }
        catch (IOException e) {
            this._parser.reportError(2, new ErrorMsg(e));
            return false;
        }
    }

    public boolean compile(URL url, String name) {
        try {
            InputStream stream = url.openStream();
            InputSource input = new InputSource(stream);
            input.setSystemId(url.toString());
            return this.compile(input, name);
        }
        catch (IOException e) {
            this._parser.reportError(2, new ErrorMsg(e));
            return false;
        }
    }

    public boolean compile(InputStream stream, String name) {
        InputSource input = new InputSource(stream);
        input.setSystemId(name);
        return this.compile(input, name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean compile(InputSource input, String name) {
        block17: {
            try {
                this.reset();
                String systemId = null;
                if (input != null) {
                    systemId = input.getSystemId();
                }
                if (this._className == null) {
                    if (name != null) {
                        this.setClassName(name);
                    } else if (systemId != null && systemId.length() != 0) {
                        this.setClassName(Util.baseName(systemId));
                    }
                    if (this._className == null || this._className.length() == 0) {
                        this.setClassName("GregorSamsa");
                    }
                }
                SyntaxTreeNode element = null;
                element = this._reader == null ? this._parser.parse(input) : this._parser.parse(this._reader, input);
                if (!this._parser.errorsFound() && element != null) {
                    this._stylesheet = this._parser.makeStylesheet(element);
                    this._stylesheet.setSourceLoader(this._loader);
                    this._stylesheet.setSystemId(systemId);
                    this._stylesheet.setParentStylesheet(null);
                    this._stylesheet.setTemplateInlining(this._templateInlining);
                    this._parser.setCurrentStylesheet(this._stylesheet);
                    this._parser.createAST(this._stylesheet);
                }
                if (this._parser.errorsFound() || this._stylesheet == null) break block17;
                this._stylesheet.setCallsNodeset(this._callsNodeset);
                this._stylesheet.setMultiDocument(this._multiDocument);
                this._stylesheet.setHasIdCall(this._hasIdCall);
                Class<?> clazz = this.getClass();
                synchronized (clazz) {
                    this._stylesheet.translate();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                this._parser.reportError(2, new ErrorMsg(e));
            }
            catch (Error e) {
                this._parser.reportError(2, new ErrorMsg(e));
            }
            finally {
                this._reader = null;
            }
        }
        return !this._parser.errorsFound();
    }

    public boolean compile(Vector stylesheets) {
        int count = stylesheets.size();
        if (count == 0) {
            return true;
        }
        if (count == 1) {
            Object url = stylesheets.firstElement();
            if (url instanceof URL) {
                return this.compile((URL)url);
            }
            return false;
        }
        Enumeration urls = stylesheets.elements();
        while (urls.hasMoreElements()) {
            this._className = null;
            Object url = urls.nextElement();
            if (!(url instanceof URL) || this.compile((URL)url)) continue;
            return false;
        }
        return true;
    }

    public byte[][] getBytecodes() {
        int count = this._classes.size();
        byte[][] result = new byte[count][1];
        for (int i = 0; i < count; ++i) {
            result[i] = (byte[])this._classes.elementAt(i);
        }
        return result;
    }

    public byte[][] compile(String name, InputSource input, int outputType) {
        this._outputType = outputType;
        if (this.compile(input, name)) {
            return this.getBytecodes();
        }
        return null;
    }

    public byte[][] compile(String name, InputSource input) {
        return this.compile(name, input, 2);
    }

    public void setXMLReader(XMLReader reader) {
        this._reader = reader;
    }

    public XMLReader getXMLReader() {
        return this._reader;
    }

    public Vector getErrors() {
        return this._parser.getErrors();
    }

    public Vector getWarnings() {
        return this._parser.getWarnings();
    }

    public void printErrors() {
        this._parser.printErrors();
    }

    public void printWarnings() {
        this._parser.printWarnings();
    }

    protected void setMultiDocument(boolean flag) {
        this._multiDocument = flag;
    }

    public boolean isMultiDocument() {
        return this._multiDocument;
    }

    protected void setCallsNodeset(boolean flag) {
        if (flag) {
            this.setMultiDocument(flag);
        }
        this._callsNodeset = flag;
    }

    public boolean callsNodeset() {
        return this._callsNodeset;
    }

    protected void setHasIdCall(boolean flag) {
        this._hasIdCall = flag;
    }

    public boolean hasIdCall() {
        return this._hasIdCall;
    }

    public void setClassName(String className) {
        String base = Util.baseName(className);
        String noext = Util.noExtName(base);
        String name = Util.toJavaName(noext);
        this._className = this._packageName == null ? name : this._packageName + '.' + name;
    }

    public String getClassName() {
        return this._className;
    }

    private String classFileName(String className) {
        return className.replace('.', File.separatorChar) + ".class";
    }

    private File getOutputFile(String className) {
        if (this._destDir != null) {
            return new File(this._destDir, this.classFileName(className));
        }
        return new File(this.classFileName(className));
    }

    public boolean setDestDirectory(String dstDirName) {
        File dir = new File(dstDirName);
        if (dir.exists() || dir.mkdirs()) {
            this._destDir = dir;
            return true;
        }
        this._destDir = null;
        return false;
    }

    public void setPackageName(String packageName) {
        this._packageName = packageName;
        if (this._className != null) {
            this.setClassName(this._className);
        }
    }

    public void setJarFileName(String jarFileName) {
        String JAR_EXT = ".jar";
        this._jarFileName = jarFileName.endsWith(".jar") ? jarFileName : jarFileName + ".jar";
        this._outputType = 1;
    }

    public String getJarFileName() {
        return this._jarFileName;
    }

    public void setStylesheet(Stylesheet stylesheet) {
        if (this._stylesheet == null) {
            this._stylesheet = stylesheet;
        }
    }

    public Stylesheet getStylesheet() {
        return this._stylesheet;
    }

    public int registerAttribute(QName name) {
        Integer code = (Integer)this._attributes.get(name.toString());
        if (code == null) {
            code = new Integer(this._nextGType++);
            this._attributes.put(name.toString(), code);
            String uri = name.getNamespace();
            String local = "@" + name.getLocalPart();
            if (uri != null && uri.length() != 0) {
                this._namesIndex.addElement(uri + ":" + local);
            } else {
                this._namesIndex.addElement(local);
            }
            if (name.getLocalPart().equals("*")) {
                this.registerNamespace(name.getNamespace());
            }
        }
        return code;
    }

    public int registerElement(QName name) {
        Integer code = (Integer)this._elements.get(name.toString());
        if (code == null) {
            code = new Integer(this._nextGType++);
            this._elements.put(name.toString(), code);
            this._namesIndex.addElement(name.toString());
        }
        if (name.getLocalPart().equals("*")) {
            this.registerNamespace(name.getNamespace());
        }
        return code;
    }

    public int registerNamespacePrefix(QName name) {
        Integer code = (Integer)this._namespacePrefixes.get(name.toString());
        if (code == null) {
            code = new Integer(this._nextGType++);
            this._namespacePrefixes.put(name.toString(), code);
            String uri = name.getNamespace();
            if (uri != null && uri.length() != 0) {
                this._namesIndex.addElement("?");
            } else {
                this._namesIndex.addElement("?" + name.getLocalPart());
            }
        }
        return code;
    }

    public int registerNamespacePrefix(String name) {
        Integer code = (Integer)this._namespacePrefixes.get(name);
        if (code == null) {
            code = new Integer(this._nextGType++);
            this._namespacePrefixes.put(name, code);
            this._namesIndex.addElement("?" + name);
        }
        return code;
    }

    public int registerNamespace(String namespaceURI) {
        Integer code = (Integer)this._namespaces.get(namespaceURI);
        if (code == null) {
            code = new Integer(this._nextNSType++);
            this._namespaces.put(namespaceURI, code);
            this._namespaceIndex.addElement(namespaceURI);
        }
        return code;
    }

    public int registerStylesheetPrefixMappingForRuntime(Hashtable prefixMap, int ancestorID) {
        if (this._stylesheetNSAncestorPointers == null) {
            this._stylesheetNSAncestorPointers = new Vector();
        }
        if (this._prefixURIPairs == null) {
            this._prefixURIPairs = new Vector();
        }
        if (this._prefixURIPairsIdx == null) {
            this._prefixURIPairsIdx = new Vector();
        }
        int currentNodeID = this._stylesheetNSAncestorPointers.size();
        this._stylesheetNSAncestorPointers.add(new Integer(ancestorID));
        Iterator prefixMapIterator = prefixMap.entrySet().iterator();
        int prefixNSPairStartIdx = this._prefixURIPairs.size();
        this._prefixURIPairsIdx.add(new Integer(prefixNSPairStartIdx));
        while (prefixMapIterator.hasNext()) {
            Map.Entry entry = prefixMapIterator.next();
            String prefix = (String)entry.getKey();
            String uri = (String)entry.getValue();
            this._prefixURIPairs.add(prefix);
            this._prefixURIPairs.add(uri);
        }
        return currentNodeID;
    }

    public Vector getNSAncestorPointers() {
        return this._stylesheetNSAncestorPointers;
    }

    public Vector getPrefixURIPairs() {
        return this._prefixURIPairs;
    }

    public Vector getPrefixURIPairsIdx() {
        return this._prefixURIPairsIdx;
    }

    public int nextModeSerial() {
        return this._modeSerial++;
    }

    public int nextStylesheetSerial() {
        return this._stylesheetSerial++;
    }

    public int nextStepPatternSerial() {
        return this._stepPatternSerial++;
    }

    public int[] getNumberFieldIndexes() {
        return this._numberFieldIndexes;
    }

    public int nextHelperClassSerial() {
        return this._helperClassSerial++;
    }

    public int nextAttributeSetSerial() {
        return this._attributeSetSerial++;
    }

    public Vector getNamesIndex() {
        return this._namesIndex;
    }

    public Vector getNamespaceIndex() {
        return this._namespaceIndex;
    }

    public String getHelperClassName() {
        return this.getClassName() + '$' + this._helperClassSerial++;
    }

    public void dumpClass(JavaClass clazz) {
        File parentFile;
        File outFile;
        String parentDir;
        if (!(this._outputType != 0 && this._outputType != 4 || (parentDir = (outFile = this.getOutputFile(clazz.getClassName())).getParent()) == null || (parentFile = new File(parentDir)).exists())) {
            parentFile.mkdirs();
        }
        try {
            switch (this._outputType) {
                case 0: {
                    clazz.dump(new BufferedOutputStream(new FileOutputStream(this.getOutputFile(clazz.getClassName()))));
                    break;
                }
                case 1: {
                    this._bcelClasses.addElement(clazz);
                    break;
                }
                case 2: 
                case 3: 
                case 4: 
                case 5: {
                    ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
                    clazz.dump(out);
                    this._classes.addElement(out.toByteArray());
                    if (this._outputType == 4) {
                        byte[] classByteArray = clazz.getBytes();
                        ByteArrayClassLoader classLoader = new ByteArrayClassLoader(classByteArray);
                        Class clz = classLoader.findClass(clazz.getClassName());
                        clazz.dump(new BufferedOutputStream(new FileOutputStream(this.getOutputFile(clazz.getClassName()))));
                        break;
                    }
                    if (this._outputType != 5) break;
                    this._bcelClasses.addElement(clazz);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String entryName(File f) throws IOException {
        return f.getName().replace(File.separatorChar, '/');
    }

    public void outputToJar() throws IOException {
        Manifest manifest = new Manifest();
        Attributes atrs = manifest.getMainAttributes();
        atrs.put(Attributes.Name.MANIFEST_VERSION, "1.2");
        Map<String, Attributes> map = manifest.getEntries();
        Enumeration classes = this._bcelClasses.elements();
        String now = new Date().toString();
        Attributes.Name dateAttr = new Attributes.Name("Date");
        while (classes.hasMoreElements()) {
            JavaClass clazz = (JavaClass)classes.nextElement();
            String className = clazz.getClassName().replace('.', '/');
            Attributes attr = new Attributes();
            attr.put(dateAttr, now);
            map.put(className + ".class", attr);
        }
        File jarFile = new File(this._destDir, this._jarFileName);
        JarOutputStream jos = new JarOutputStream((OutputStream)new FileOutputStream(jarFile), manifest);
        classes = this._bcelClasses.elements();
        while (classes.hasMoreElements()) {
            JavaClass clazz = (JavaClass)classes.nextElement();
            String className = clazz.getClassName().replace('.', '/');
            jos.putNextEntry(new JarEntry(className + ".class"));
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            clazz.dump(out);
            out.writeTo(jos);
        }
        jos.close();
    }

    public void setDebug(boolean debug) {
        this._debug = debug;
    }

    public boolean debug() {
        return this._debug;
    }

    public String getCharacterData(int index) {
        return ((StringBuffer)this.m_characterData.elementAt(index)).toString();
    }

    public int getCharacterDataCount() {
        return this.m_characterData != null ? this.m_characterData.size() : 0;
    }

    public int addCharacterData(String newData) {
        StringBuffer currData;
        if (this.m_characterData == null) {
            this.m_characterData = new Vector();
            currData = new StringBuffer();
            this.m_characterData.addElement(currData);
        } else {
            currData = (StringBuffer)this.m_characterData.elementAt(this.m_characterData.size() - 1);
        }
        if (newData.length() + currData.length() > 21845) {
            currData = new StringBuffer();
            this.m_characterData.addElement(currData);
        }
        int newDataOffset = currData.length();
        currData.append(newData);
        return newDataOffset;
    }

    public class ByteArrayClassLoader
    extends ClassLoader {
        byte[] ba;

        public ByteArrayClassLoader(byte[] bArray) {
            this.ba = bArray;
        }

        public Class findClass(String name) {
            return this.defineClass(name, this.ba, 0, this.ba.length);
        }
    }
}

