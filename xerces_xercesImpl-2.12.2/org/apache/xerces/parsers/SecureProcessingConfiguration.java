/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;
import org.apache.xerces.impl.XMLEntityDescription;
import org.apache.xerces.impl.dtd.XMLDTDProcessor;
import org.apache.xerces.parsers.SecuritySupport;
import org.apache.xerces.parsers.XIncludeAwareParserConfiguration;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDTDFilter;
import org.apache.xerces.xni.parser.XMLDTDScanner;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;

public final class SecureProcessingConfiguration
extends XIncludeAwareParserConfiguration {
    private static final String SECURITY_MANAGER_PROPERTY = "http://apache.org/xml/properties/security-manager";
    private static final String ENTITY_RESOLVER_PROPERTY = "http://apache.org/xml/properties/internal/entity-resolver";
    private static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    private static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    private static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    private static final boolean DEBUG = SecureProcessingConfiguration.isDebugEnabled();
    private static Properties jaxpProperties = null;
    private static long lastModified = -1L;
    private static final int SECURITY_MANAGER_DEFAULT_ENTITY_EXPANSION_LIMIT = 100000;
    private static final int SECURITY_MANAGER_DEFAULT_MAX_OCCUR_NODE_LIMIT = 3000;
    private static final String ENTITY_EXPANSION_LIMIT_PROPERTY_NAME = "jdk.xml.entityExpansionLimit";
    private static final String MAX_OCCUR_LIMIT_PROPERTY_NAME = "jdk.xml.maxOccur";
    private static final String TOTAL_ENTITY_SIZE_LIMIT_PROPERTY_NAME = "jdk.xml.totalEntitySizeLimit";
    private static final String MAX_GENERAL_ENTITY_SIZE_LIMIT_PROPERTY_NAME = "jdk.xml.maxGeneralEntitySizeLimit";
    private static final String MAX_PARAMETER_ENTITY_SIZE_LIMIT_PROPERTY_NAME = "jdk.xml.maxParameterEntitySizeLimit";
    private static final String RESOLVE_EXTERNAL_ENTITIES_PROPERTY_NAME = "jdk.xml.resolveExternalEntities";
    private static final int ENTITY_EXPANSION_LIMIT_DEFAULT_VALUE = 64000;
    private static final int MAX_OCCUR_LIMIT_DEFAULT_VALUE = 5000;
    private static final int TOTAL_ENTITY_SIZE_LIMIT_DEFAULT_VALUE = 50000000;
    private static final int MAX_GENERAL_ENTITY_SIZE_LIMIT_DEFAULT_VALUE = Integer.MAX_VALUE;
    private static final int MAX_PARAMETER_ENTITY_SIZE_LIMIT_DEFAULT_VALUE = Integer.MAX_VALUE;
    private static final boolean RESOLVE_EXTERNAL_ENTITIES_DEFAULT_VALUE = true;
    protected final int ENTITY_EXPANSION_LIMIT_SYSTEM_VALUE;
    protected final int MAX_OCCUR_LIMIT_SYSTEM_VALUE;
    protected final int TOTAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE;
    protected final int MAX_GENERAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE;
    protected final int MAX_PARAMETER_ENTITY_SIZE_LIMIT_SYSTEM_VALUE;
    protected final boolean RESOLVE_EXTERNAL_ENTITIES_SYSTEM_VALUE;
    private final boolean fJavaSecurityManagerEnabled = System.getSecurityManager() != null;
    private boolean fLimitSpecified;
    private SecurityManager fSecurityManager;
    private InternalEntityMonitor fInternalEntityMonitor;
    private final ExternalEntityMonitor fExternalEntityMonitor;
    private int fTotalEntitySize = 0;

    public SecureProcessingConfiguration() {
        this(null, null, null);
    }

    public SecureProcessingConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }

    public SecureProcessingConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this(symbolTable, xMLGrammarPool, null);
    }

    public SecureProcessingConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLComponentManager xMLComponentManager) {
        super(symbolTable, xMLGrammarPool, xMLComponentManager);
        this.ENTITY_EXPANSION_LIMIT_SYSTEM_VALUE = this.getPropertyValue(ENTITY_EXPANSION_LIMIT_PROPERTY_NAME, 64000);
        this.MAX_OCCUR_LIMIT_SYSTEM_VALUE = this.getPropertyValue(MAX_OCCUR_LIMIT_PROPERTY_NAME, 5000);
        this.TOTAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE = this.getPropertyValue(TOTAL_ENTITY_SIZE_LIMIT_PROPERTY_NAME, 50000000);
        this.MAX_GENERAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE = this.getPropertyValue(MAX_GENERAL_ENTITY_SIZE_LIMIT_PROPERTY_NAME, Integer.MAX_VALUE);
        this.MAX_PARAMETER_ENTITY_SIZE_LIMIT_SYSTEM_VALUE = this.getPropertyValue(MAX_PARAMETER_ENTITY_SIZE_LIMIT_PROPERTY_NAME, Integer.MAX_VALUE);
        this.RESOLVE_EXTERNAL_ENTITIES_SYSTEM_VALUE = this.getPropertyValue(RESOLVE_EXTERNAL_ENTITIES_PROPERTY_NAME, true);
        if (this.fJavaSecurityManagerEnabled || this.fLimitSpecified) {
            if (!this.RESOLVE_EXTERNAL_ENTITIES_SYSTEM_VALUE) {
                super.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
                super.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
                super.setFeature(LOAD_EXTERNAL_DTD, false);
            }
            this.fSecurityManager = new SecurityManager();
            this.fSecurityManager.setEntityExpansionLimit(this.ENTITY_EXPANSION_LIMIT_SYSTEM_VALUE);
            this.fSecurityManager.setMaxOccurNodeLimit(this.MAX_OCCUR_LIMIT_SYSTEM_VALUE);
            super.setProperty(SECURITY_MANAGER_PROPERTY, this.fSecurityManager);
        }
        this.fExternalEntityMonitor = new ExternalEntityMonitor();
        super.setProperty(ENTITY_RESOLVER_PROPERTY, this.fExternalEntityMonitor);
    }

    protected void checkEntitySizeLimits(int n, int n2, boolean bl) {
        this.fTotalEntitySize += n2;
        if (this.fTotalEntitySize > this.TOTAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "TotalEntitySizeLimitExceeded", new Object[]{new Integer(this.TOTAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE)}, (short)2);
        }
        if (bl) {
            if (n > this.MAX_PARAMETER_ENTITY_SIZE_LIMIT_SYSTEM_VALUE) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MaxParameterEntitySizeLimitExceeded", new Object[]{new Integer(this.MAX_PARAMETER_ENTITY_SIZE_LIMIT_SYSTEM_VALUE)}, (short)2);
            }
        } else if (n > this.MAX_GENERAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MaxGeneralEntitySizeLimitExceeded", new Object[]{new Integer(this.MAX_GENERAL_ENTITY_SIZE_LIMIT_SYSTEM_VALUE)}, (short)2);
        }
    }

    @Override
    public Object getProperty(String string) throws XMLConfigurationException {
        if (SECURITY_MANAGER_PROPERTY.equals(string)) {
            return this.fSecurityManager;
        }
        if (ENTITY_RESOLVER_PROPERTY.equals(string)) {
            return this.fExternalEntityMonitor;
        }
        return super.getProperty(string);
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        if (SECURITY_MANAGER_PROPERTY.equals(string)) {
            if (object == null && this.fJavaSecurityManagerEnabled) {
                return;
            }
            this.fSecurityManager = (SecurityManager)object;
            if (this.fSecurityManager != null) {
                if (this.fSecurityManager.getEntityExpansionLimit() == 100000) {
                    this.fSecurityManager.setEntityExpansionLimit(this.ENTITY_EXPANSION_LIMIT_SYSTEM_VALUE);
                }
                if (this.fSecurityManager.getMaxOccurNodeLimit() == 3000) {
                    this.fSecurityManager.setMaxOccurNodeLimit(this.MAX_OCCUR_LIMIT_SYSTEM_VALUE);
                }
            }
        } else if (ENTITY_RESOLVER_PROPERTY.equals(string)) {
            this.fExternalEntityMonitor.setEntityResolver((XMLEntityResolver)object);
            return;
        }
        super.setProperty(string, object);
    }

    @Override
    protected void configurePipeline() {
        super.configurePipeline();
        this.configurePipelineCommon(true);
    }

    @Override
    protected void configureXML11Pipeline() {
        super.configureXML11Pipeline();
        this.configurePipelineCommon(false);
    }

    private void configurePipelineCommon(boolean bl) {
        if (this.fSecurityManager != null) {
            XMLDTDProcessor xMLDTDProcessor;
            XMLDTDScanner xMLDTDScanner;
            this.fTotalEntitySize = 0;
            if (this.fInternalEntityMonitor == null) {
                this.fInternalEntityMonitor = new InternalEntityMonitor();
            }
            if (bl) {
                xMLDTDScanner = this.fDTDScanner;
                xMLDTDProcessor = this.fDTDProcessor;
            } else {
                xMLDTDScanner = this.fXML11DTDScanner;
                xMLDTDProcessor = this.fXML11DTDProcessor;
            }
            xMLDTDScanner.setDTDHandler(this.fInternalEntityMonitor);
            this.fInternalEntityMonitor.setDTDSource(xMLDTDScanner);
            this.fInternalEntityMonitor.setDTDHandler(xMLDTDProcessor);
            xMLDTDProcessor.setDTDSource(this.fInternalEntityMonitor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private int getPropertyValue(String string, int n) {
        block35: {
            try {
                String string2 = SecuritySupport.getSystemProperty(string);
                if (string2 != null && string2.length() > 0) {
                    if (DEBUG) {
                        SecureProcessingConfiguration.debugPrintln("found system property \"" + string + "\", value=" + string2);
                    }
                    int n2 = Integer.parseInt(string2);
                    this.fLimitSpecified = true;
                    if (n2 <= 0) return Integer.MAX_VALUE;
                    return n2;
                }
            }
            catch (VirtualMachineError virtualMachineError) {
                throw virtualMachineError;
            }
            catch (ThreadDeath threadDeath) {
                throw threadDeath;
            }
            catch (Throwable throwable) {
                if (!DEBUG) break block35;
                SecureProcessingConfiguration.debugPrintln(throwable.getClass().getName() + ": " + throwable.getMessage());
                throwable.printStackTrace();
            }
        }
        try {
            Object object;
            boolean bl = false;
            File file = null;
            try {
                object = SecuritySupport.getSystemProperty("java.home");
                String string3 = (String)object + File.separator + "lib" + File.separator + "jaxp.properties";
                file = new File(string3);
                bl = SecuritySupport.getFileExists(file);
            }
            catch (SecurityException securityException) {
                lastModified = -1L;
                jaxpProperties = null;
            }
            object = SecureProcessingConfiguration.class;
            synchronized (SecureProcessingConfiguration.class) {
                int n3 = 0;
                FileInputStream fileInputStream = null;
                try {
                    if (lastModified >= 0L) {
                        if (bl && lastModified < (lastModified = SecuritySupport.getLastModified(file))) {
                            n3 = 1;
                        } else if (!bl) {
                            lastModified = -1L;
                            jaxpProperties = null;
                        }
                    } else if (bl) {
                        n3 = 1;
                        lastModified = SecuritySupport.getLastModified(file);
                    }
                    if (n3 == 1) {
                        jaxpProperties = new Properties();
                        fileInputStream = SecuritySupport.getFileInputStream(file);
                        jaxpProperties.load(fileInputStream);
                    }
                }
                catch (Exception exception) {
                    lastModified = -1L;
                    jaxpProperties = null;
                }
                finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
                // ** MonitorExit[var5_13] (shouldn't be in output)
                if (jaxpProperties == null || (object = jaxpProperties.getProperty(string)) == null || ((String)object).length() <= 0) return n;
                if (DEBUG) {
                    SecureProcessingConfiguration.debugPrintln("found \"" + string + "\" in jaxp.properties, value=" + (String)object);
                }
                n3 = Integer.parseInt((String)object);
                this.fLimitSpecified = true;
                if (n3 <= 0) return Integer.MAX_VALUE;
                return n3;
            }
        }
        catch (VirtualMachineError virtualMachineError) {
            throw virtualMachineError;
        }
        catch (ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (Throwable throwable) {
            if (!DEBUG) return n;
            SecureProcessingConfiguration.debugPrintln(throwable.getClass().getName() + ": " + throwable.getMessage());
            throwable.printStackTrace();
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean getPropertyValue(String string, boolean bl) {
        block35: {
            try {
                String string2 = SecuritySupport.getSystemProperty(string);
                if (string2 != null && string2.length() > 0) {
                    if (DEBUG) {
                        SecureProcessingConfiguration.debugPrintln("found system property \"" + string + "\", value=" + string2);
                    }
                    boolean bl2 = Boolean.valueOf(string2);
                    this.fLimitSpecified = true;
                    return bl2;
                }
            }
            catch (VirtualMachineError virtualMachineError) {
                throw virtualMachineError;
            }
            catch (ThreadDeath threadDeath) {
                throw threadDeath;
            }
            catch (Throwable throwable) {
                if (!DEBUG) break block35;
                SecureProcessingConfiguration.debugPrintln(throwable.getClass().getName() + ": " + throwable.getMessage());
                throwable.printStackTrace();
            }
        }
        try {
            Object object;
            boolean bl3 = false;
            File file = null;
            try {
                object = SecuritySupport.getSystemProperty("java.home");
                String string3 = (String)object + File.separator + "lib" + File.separator + "jaxp.properties";
                file = new File(string3);
                bl3 = SecuritySupport.getFileExists(file);
            }
            catch (SecurityException securityException) {
                lastModified = -1L;
                jaxpProperties = null;
            }
            object = SecureProcessingConfiguration.class;
            synchronized (SecureProcessingConfiguration.class) {
                boolean bl4 = false;
                FileInputStream fileInputStream = null;
                try {
                    if (lastModified >= 0L) {
                        if (bl3 && lastModified < (lastModified = SecuritySupport.getLastModified(file))) {
                            bl4 = true;
                        } else if (!bl3) {
                            lastModified = -1L;
                            jaxpProperties = null;
                        }
                    } else if (bl3) {
                        bl4 = true;
                        lastModified = SecuritySupport.getLastModified(file);
                    }
                    if (bl4) {
                        jaxpProperties = new Properties();
                        fileInputStream = SecuritySupport.getFileInputStream(file);
                        jaxpProperties.load(fileInputStream);
                    }
                }
                catch (Exception exception) {
                    lastModified = -1L;
                    jaxpProperties = null;
                }
                finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
                // ** MonitorExit[var5_13] (shouldn't be in output)
                if (jaxpProperties == null || (object = jaxpProperties.getProperty(string)) == null || ((String)object).length() <= 0) return bl;
                if (DEBUG) {
                    SecureProcessingConfiguration.debugPrintln("found \"" + string + "\" in jaxp.properties, value=" + (String)object);
                }
                bl4 = Boolean.valueOf((String)object);
                this.fLimitSpecified = true;
                return bl4;
            }
        }
        catch (VirtualMachineError virtualMachineError) {
            throw virtualMachineError;
        }
        catch (ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (Throwable throwable) {
            if (!DEBUG) return bl;
            SecureProcessingConfiguration.debugPrintln(throwable.getClass().getName() + ": " + throwable.getMessage());
            throwable.printStackTrace();
        }
        return bl;
    }

    private static boolean isDebugEnabled() {
        try {
            String string = SecuritySupport.getSystemProperty("xerces.debug");
            return string != null && !"false".equals(string);
        }
        catch (SecurityException securityException) {
            return false;
        }
    }

    private static void debugPrintln(String string) {
        if (DEBUG) {
            System.err.println("XERCES: " + string);
        }
    }

    final class ExternalEntityMonitor
    implements XMLEntityResolver {
        private XMLEntityResolver fEntityResolver;

        ExternalEntityMonitor() {
        }

        @Override
        public XMLInputSource resolveEntity(XMLResourceIdentifier xMLResourceIdentifier) throws XNIException, IOException {
            XMLInputSource xMLInputSource = null;
            if (this.fEntityResolver != null) {
                xMLInputSource = this.fEntityResolver.resolveEntity(xMLResourceIdentifier);
            }
            if (SecureProcessingConfiguration.this.fSecurityManager != null && xMLResourceIdentifier instanceof XMLEntityDescription) {
                String string;
                Object object;
                Object object2;
                boolean bl;
                String string2 = ((XMLEntityDescription)xMLResourceIdentifier).getEntityName();
                boolean bl2 = bl = string2 != null && string2.startsWith("%");
                if (xMLInputSource == null) {
                    object2 = xMLResourceIdentifier.getPublicId();
                    object = xMLResourceIdentifier.getExpandedSystemId();
                    string = xMLResourceIdentifier.getBaseSystemId();
                    xMLInputSource = new XMLInputSource((String)object2, (String)object, string);
                }
                if ((object2 = xMLInputSource.getCharacterStream()) != null) {
                    xMLInputSource.setCharacterStream(new ReaderMonitor((Reader)object2, bl));
                } else {
                    object = xMLInputSource.getByteStream();
                    if (object != null) {
                        xMLInputSource.setByteStream(new InputStreamMonitor((InputStream)object, bl));
                    } else {
                        string = xMLResourceIdentifier.getExpandedSystemId();
                        URL uRL = new URL(string);
                        object = uRL.openStream();
                        xMLInputSource.setByteStream(new InputStreamMonitor((InputStream)object, bl));
                    }
                }
            }
            return xMLInputSource;
        }

        public void setEntityResolver(XMLEntityResolver xMLEntityResolver) {
            this.fEntityResolver = xMLEntityResolver;
        }

        public XMLEntityResolver getEntityResolver() {
            return this.fEntityResolver;
        }

        final class ReaderMonitor
        extends FilterReader {
            private final boolean isPE;
            private int size;

            protected ReaderMonitor(Reader reader, boolean bl) {
                super(reader);
                this.size = 0;
                this.isPE = bl;
            }

            @Override
            public int read() throws IOException {
                int n = super.read();
                if (n != -1) {
                    ++this.size;
                    SecureProcessingConfiguration.this.checkEntitySizeLimits(this.size, 1, this.isPE);
                }
                return n;
            }

            @Override
            public int read(char[] cArray, int n, int n2) throws IOException {
                int n3 = super.read(cArray, n, n2);
                if (n3 > 0) {
                    this.size += n3;
                    SecureProcessingConfiguration.this.checkEntitySizeLimits(this.size, n3, this.isPE);
                }
                return n3;
            }
        }

        final class InputStreamMonitor
        extends FilterInputStream {
            private final boolean isPE;
            private int size;

            protected InputStreamMonitor(InputStream inputStream, boolean bl) {
                super(inputStream);
                this.size = 0;
                this.isPE = bl;
            }

            @Override
            public int read() throws IOException {
                int n = super.read();
                if (n != -1) {
                    ++this.size;
                    SecureProcessingConfiguration.this.checkEntitySizeLimits(this.size, 1, this.isPE);
                }
                return n;
            }

            @Override
            public int read(byte[] byArray, int n, int n2) throws IOException {
                int n3 = super.read(byArray, n, n2);
                if (n3 > 0) {
                    this.size += n3;
                    SecureProcessingConfiguration.this.checkEntitySizeLimits(this.size, n3, this.isPE);
                }
                return n3;
            }
        }
    }

    final class InternalEntityMonitor
    implements XMLDTDFilter {
        private XMLDTDSource fDTDSource;
        private XMLDTDHandler fDTDHandler;

        @Override
        public void startDTD(XMLLocator xMLLocator, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.startDTD(xMLLocator, augmentations);
            }
        }

        @Override
        public void startParameterEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.startParameterEntity(string, xMLResourceIdentifier, string2, augmentations);
            }
        }

        @Override
        public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.textDecl(string, string2, augmentations);
            }
        }

        @Override
        public void endParameterEntity(String string, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.endParameterEntity(string, augmentations);
            }
        }

        @Override
        public void startExternalSubset(XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.startExternalSubset(xMLResourceIdentifier, augmentations);
            }
        }

        @Override
        public void endExternalSubset(Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.endExternalSubset(augmentations);
            }
        }

        @Override
        public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.comment(xMLString, augmentations);
            }
        }

        @Override
        public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.processingInstruction(string, xMLString, augmentations);
            }
        }

        @Override
        public void elementDecl(String string, String string2, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.elementDecl(string, string2, augmentations);
            }
        }

        @Override
        public void startAttlist(String string, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.startAttlist(string, augmentations);
            }
        }

        @Override
        public void attributeDecl(String string, String string2, String string3, String[] stringArray, String string4, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.attributeDecl(string, string2, string3, stringArray, string4, xMLString, xMLString2, augmentations);
            }
        }

        @Override
        public void endAttlist(Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.endAttlist(augmentations);
            }
        }

        @Override
        public void internalEntityDecl(String string, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
            SecureProcessingConfiguration.this.checkEntitySizeLimits(xMLString.length, xMLString.length, string != null && string.startsWith("%"));
            if (this.fDTDHandler != null) {
                this.fDTDHandler.internalEntityDecl(string, xMLString, xMLString2, augmentations);
            }
        }

        @Override
        public void externalEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.externalEntityDecl(string, xMLResourceIdentifier, augmentations);
            }
        }

        @Override
        public void unparsedEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.unparsedEntityDecl(string, xMLResourceIdentifier, string2, augmentations);
            }
        }

        @Override
        public void notationDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.notationDecl(string, xMLResourceIdentifier, augmentations);
            }
        }

        @Override
        public void startConditional(short s, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.startConditional(s, augmentations);
            }
        }

        @Override
        public void ignoredCharacters(XMLString xMLString, Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.ignoredCharacters(xMLString, augmentations);
            }
        }

        @Override
        public void endConditional(Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.endConditional(augmentations);
            }
        }

        @Override
        public void endDTD(Augmentations augmentations) throws XNIException {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.endDTD(augmentations);
            }
        }

        @Override
        public void setDTDSource(XMLDTDSource xMLDTDSource) {
            this.fDTDSource = xMLDTDSource;
        }

        @Override
        public XMLDTDSource getDTDSource() {
            return this.fDTDSource;
        }

        @Override
        public void setDTDHandler(XMLDTDHandler xMLDTDHandler) {
            this.fDTDHandler = xMLDTDHandler;
        }

        @Override
        public XMLDTDHandler getDTDHandler() {
            return this.fDTDHandler;
        }
    }
}

