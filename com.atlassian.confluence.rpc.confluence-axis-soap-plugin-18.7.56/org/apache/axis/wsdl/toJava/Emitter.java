/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.WSDLException
 */
package org.apache.axis.wsdl.toJava;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.constants.Scope;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.i18n.Messages;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.gen.GeneratorFactory;
import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.GeneratedFileInfo;
import org.apache.axis.wsdl.toJava.JavaGeneratorFactory;
import org.apache.axis.wsdl.toJava.Namespaces;
import org.apache.axis.wsdl.toJava.Utils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Emitter
extends Parser {
    public static final String DEFAULT_NSTOPKG_FILE = "NStoPkg.properties";
    protected HashMap namespaceMap = new HashMap();
    protected String typeMappingVersion = "1.2";
    protected BaseTypeMapping baseTypeMapping = null;
    protected Namespaces namespaces = null;
    protected String NStoPkgFilename = null;
    private boolean bEmitServer = false;
    private boolean bDeploySkeleton = false;
    private boolean bEmitTestCase = false;
    private boolean bGenerateAll = false;
    private boolean bHelperGeneration = false;
    private boolean bBuildFileGeneration = false;
    private boolean typeCollisionProtection = true;
    private boolean allowInvalidURL = false;
    private String packageName = null;
    private Scope scope = null;
    private GeneratedFileInfo fileInfo = new GeneratedFileInfo();
    private HashMap delayedNamespacesMap = new HashMap();
    private String outputDir = null;
    protected List nsIncludes = new ArrayList();
    protected List nsExcludes = new ArrayList();
    protected List properties = new ArrayList();
    private String implementationClassName = null;
    private TypeMapping defaultTM = null;
    private TypeMappingRegistryImpl tmr = new TypeMappingRegistryImpl();
    private HashMap qName2ClassMap;
    private ServiceDesc serviceDesc;
    private boolean isDeploy;
    static /* synthetic */ Class class$org$apache$axis$wsdl$toJava$Emitter;

    public Emitter() {
        this.setFactory(new JavaGeneratorFactory(this));
    }

    public void setServerSide(boolean value) {
        this.bEmitServer = value;
    }

    public boolean isServerSide() {
        return this.bEmitServer;
    }

    public void setSkeletonWanted(boolean value) {
        this.bDeploySkeleton = value;
    }

    public boolean isSkeletonWanted() {
        return this.bDeploySkeleton;
    }

    public void setHelperWanted(boolean value) {
        this.bHelperGeneration = value;
    }

    public boolean isHelperWanted() {
        return this.bHelperGeneration;
    }

    public void setTestCaseWanted(boolean value) {
        this.bEmitTestCase = value;
    }

    public boolean isTestCaseWanted() {
        return this.bEmitTestCase;
    }

    public boolean isBuildFileWanted() {
        return this.bBuildFileGeneration;
    }

    public void setBuildFileWanted(boolean value) {
        this.bBuildFileGeneration = value;
    }

    public void setAllWanted(boolean all) {
        this.bGenerateAll = all;
    }

    public boolean isAllWanted() {
        return this.bGenerateAll;
    }

    public Namespaces getNamespaces() {
        return this.namespaces;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getOutputDir() {
        return this.outputDir;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return this.scope;
    }

    public void setNStoPkg(String NStoPkgFilename) {
        if (NStoPkgFilename != null) {
            this.NStoPkgFilename = NStoPkgFilename;
        }
    }

    public void setNamespaceMap(HashMap map) {
        this.delayedNamespacesMap = map;
    }

    public HashMap getNamespaceMap() {
        return this.delayedNamespacesMap;
    }

    public void setNamespaceIncludes(List nsIncludes) {
        this.nsIncludes = nsIncludes;
    }

    public List getNamespaceIncludes() {
        return this.nsIncludes;
    }

    public void setNamespaceExcludes(List nsExcludes) {
        this.nsExcludes = nsExcludes;
    }

    public List getNamespaceExcludes() {
        return this.nsExcludes;
    }

    public void setProperties(List properties) {
        this.properties = properties;
    }

    public List getProperties() {
        return this.properties;
    }

    public TypeMapping getDefaultTypeMapping() {
        if (this.defaultTM == null) {
            this.defaultTM = (TypeMapping)this.tmr.getTypeMapping("http://schemas.xmlsoap.org/soap/encoding/");
        }
        return this.defaultTM;
    }

    public void setDefaultTypeMapping(TypeMapping defaultTM) {
        this.defaultTM = defaultTM;
    }

    public void setFactory(String factory) {
        try {
            GeneratorFactory genFac;
            Class clazz = ClassUtils.forName(factory);
            try {
                Constructor ctor = clazz.getConstructor(this.getClass());
                genFac = (GeneratorFactory)ctor.newInstance(this);
            }
            catch (NoSuchMethodException ex) {
                genFac = (GeneratorFactory)clazz.newInstance();
            }
            this.setFactory(genFac);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public GeneratedFileInfo getGeneratedFileInfo() {
        return this.fileInfo;
    }

    public List getGeneratedClassNames() {
        return this.fileInfo.getClassNames();
    }

    public List getGeneratedFileNames() {
        return this.fileInfo.getFileNames();
    }

    public String getPackage(String namespace) {
        return this.namespaces.getCreate(namespace);
    }

    public String getPackage(QName qName) {
        return this.getPackage(qName.getNamespaceURI());
    }

    public String getJavaName(QName qName) {
        if (qName.getLocalPart().indexOf("[") > 0) {
            String localPart = qName.getLocalPart().substring(0, qName.getLocalPart().indexOf("["));
            QName eQName = new QName(qName.getNamespaceURI(), localPart);
            return this.getJavaName(eQName) + "[]";
        }
        if (qName.getNamespaceURI().equalsIgnoreCase("java")) {
            return qName.getLocalPart();
        }
        String fullJavaName = this.getFactory().getBaseTypeMapping().getBaseName(qName);
        if (fullJavaName != null) {
            return fullJavaName;
        }
        fullJavaName = this.getJavaNameHook(qName);
        if (fullJavaName != null) {
            return fullJavaName;
        }
        String pkg = this.getPackage(qName.getNamespaceURI());
        fullJavaName = pkg != null && pkg.length() > 0 ? pkg + "." + Utils.xmlNameToJavaClass(qName.getLocalPart()) : Utils.xmlNameToJavaClass(qName.getLocalPart());
        return fullJavaName;
    }

    protected String getJavaNameHook(QName qname) {
        return null;
    }

    public String getJavaVariableName(QName typeQName, QName xmlName, boolean isElement) {
        String javaName = this.getJavaVariableNameHook(typeQName, xmlName, isElement);
        if (javaName == null) {
            String elemName = Utils.getLastLocalPart(xmlName.getLocalPart());
            javaName = Utils.xmlNameToJava(elemName);
        }
        return javaName;
    }

    protected String getJavaVariableNameHook(QName typeQName, QName xmlName, boolean isElement) {
        return null;
    }

    public void run(String wsdlURL) throws Exception {
        this.setup();
        super.run(wsdlURL);
    }

    public void run(String context, Document doc) throws IOException, SAXException, WSDLException, ParserConfigurationException {
        this.setup();
        super.run(context, doc);
    }

    private void setup() throws IOException {
        if (this.baseTypeMapping == null) {
            this.setTypeMappingVersion(this.typeMappingVersion);
        }
        this.getFactory().setBaseTypeMapping(this.baseTypeMapping);
        this.namespaces = new Namespaces(this.outputDir);
        if (this.packageName != null) {
            this.namespaces.setDefaultPackage(this.packageName);
        } else {
            this.getNStoPkgFromPropsFile(this.namespaces);
            if (this.delayedNamespacesMap != null) {
                this.namespaces.putAll((Map)this.delayedNamespacesMap);
            }
        }
    }

    protected void sanityCheck(SymbolTable symbolTable) {
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry)v.elementAt(i);
                String namespace = entry.getQName().getNamespaceURI();
                String packageName = Utils.makePackageName(namespace);
                String localName = entry.getQName().getLocalPart();
                if (!localName.equals(packageName) || !packageName.equals(this.namespaces.getCreate(namespace))) continue;
                packageName = packageName + "_pkg";
                this.namespaces.put(namespace, packageName);
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    private void getNStoPkgFromPropsFile(HashMap namespaces) throws IOException {
        mappings = new Properties();
        if (this.NStoPkgFilename != null) {
            try {
                mappings.load(new FileInputStream(this.NStoPkgFilename));
                if (!this.verbose) ** GOTO lbl23
                System.out.println(Messages.getMessage("nsToPkgFileLoaded00", this.NStoPkgFilename));
            }
            catch (Throwable t) {
                throw new IOException(Messages.getMessage("nsToPkgFileNotFound00", this.NStoPkgFilename));
            }
        } else {
            try {
                mappings.load(new FileInputStream("NStoPkg.properties"));
                if (this.verbose) {
                    System.out.println(Messages.getMessage("nsToPkgFileLoaded00", "NStoPkg.properties"));
                }
            }
            catch (Throwable t) {
                try {
                    mappings.load(ClassUtils.getResourceAsStream(Emitter.class$org$apache$axis$wsdl$toJava$Emitter == null ? (Emitter.class$org$apache$axis$wsdl$toJava$Emitter = Emitter.class$("org.apache.axis.wsdl.toJava.Emitter")) : Emitter.class$org$apache$axis$wsdl$toJava$Emitter, "NStoPkg.properties"));
                    if (this.verbose) {
                        System.out.println(Messages.getMessage("nsToPkgDefaultFileLoaded00", "NStoPkg.properties"));
                    }
                }
                catch (Throwable t1) {
                    // empty catch block
                }
            }
        }
lbl23:
        // 5 sources

        keys = mappings.propertyNames();
        while (keys.hasMoreElements()) {
            key = (String)keys.nextElement();
            namespaces.put(key, mappings.getProperty(key));
        }
    }

    public String getTypeMappingVersion() {
        return this.typeMappingVersion;
    }

    public void setTypeMappingVersion(String typeMappingVersion) {
        this.typeMappingVersion = typeMappingVersion;
        this.tmr.doRegisterFromVersion(typeMappingVersion);
        this.baseTypeMapping = new BaseTypeMapping(){
            final TypeMapping defaultTM;
            {
                this.defaultTM = Emitter.this.getDefaultTypeMapping();
            }

            public String getBaseName(QName qNameIn) {
                QName qName = new QName(qNameIn.getNamespaceURI(), qNameIn.getLocalPart());
                Class cls = this.defaultTM.getClassForQName(qName);
                if (cls == null) {
                    return null;
                }
                return JavaUtils.getTextClassName(cls.getName());
            }
        };
    }

    public GeneratorFactory getWriterFactory() {
        return this.getFactory();
    }

    public void emit(String uri) throws Exception {
        this.run(uri);
    }

    public void emit(String context, Document doc) throws IOException, SAXException, WSDLException, ParserConfigurationException {
        this.run(context, doc);
    }

    public void generateServerSide(boolean value) {
        this.setServerSide(value);
    }

    public boolean getGenerateServerSide() {
        return this.isServerSide();
    }

    public void deploySkeleton(boolean value) {
        this.setSkeletonWanted(value);
    }

    public boolean getDeploySkeleton() {
        return this.isSkeletonWanted();
    }

    public void setHelperGeneration(boolean value) {
        this.setHelperWanted(value);
    }

    public boolean getHelperGeneration() {
        return this.isHelperWanted();
    }

    public void generateImports(boolean generateImports) {
        this.setImports(generateImports);
    }

    public void debug(boolean value) {
        this.setDebug(value);
    }

    public boolean getDebug() {
        return this.isDebug();
    }

    public void verbose(boolean value) {
        this.setVerbose(value);
    }

    public boolean getVerbose() {
        return this.isVerbose();
    }

    public void generateTestCase(boolean value) {
        this.setTestCaseWanted(value);
    }

    public void generateAll(boolean all) {
        this.setAllWanted(all);
    }

    public boolean isTypeCollisionProtection() {
        return this.typeCollisionProtection;
    }

    public void setTypeCollisionProtection(boolean value) {
        this.typeCollisionProtection = value;
    }

    public String getImplementationClassName() {
        return this.implementationClassName;
    }

    public void setImplementationClassName(String implementationClassName) {
        this.implementationClassName = implementationClassName;
    }

    public boolean isAllowInvalidURL() {
        return this.allowInvalidURL;
    }

    public void setAllowInvalidURL(boolean allowInvalidURL) {
        this.allowInvalidURL = allowInvalidURL;
    }

    public void setQName2ClassMap(HashMap map) {
        this.qName2ClassMap = map;
    }

    public HashMap getQName2ClassMap() {
        return this.qName2ClassMap;
    }

    public ServiceDesc getServiceDesc() {
        return this.serviceDesc;
    }

    public void setServiceDesc(ServiceDesc serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public boolean isDeploy() {
        return this.isDeploy;
    }

    public void setDeploy(boolean isDeploy) {
        this.isDeploy = isDeploy;
    }

    protected boolean doesExist(String className) {
        try {
            ClassUtils.forName(className);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public void setWrapArrays(boolean wrapArrays) {
        this.wrapArrays = wrapArrays;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

