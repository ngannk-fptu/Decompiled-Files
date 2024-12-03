/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.xerces.impl.XML11EntityScanner;
import org.apache.xerces.impl.XMLEntityHandler;
import org.apache.xerces.impl.XMLEntityScanner;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.io.ASCIIReader;
import org.apache.xerces.impl.io.Latin1Reader;
import org.apache.xerces.impl.io.UCSReader;
import org.apache.xerces.impl.io.UTF16Reader;
import org.apache.xerces.impl.io.UTF8Reader;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.EncodingMap;
import org.apache.xerces.util.HTTPInputSource;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLEntityDescriptionImpl;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XMLEntityManager
implements XMLComponent,
XMLEntityResolver {
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
    public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 512;
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
    protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/validation", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/warn-on-duplicate-entitydef", "http://apache.org/xml/features/standard-uri-conformant"};
    private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{null, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE};
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/input-buffer-size", "http://apache.org/xml/properties/security-manager"};
    private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null, null, null, new Integer(2048), null};
    private static final String XMLEntity = "[xml]".intern();
    private static final String DTDEntity = "[dtd]".intern();
    private static final boolean DEBUG_BUFFER = false;
    private static final boolean DEBUG_ENTITIES = false;
    private static final boolean DEBUG_ENCODINGS = false;
    private static final boolean DEBUG_RESOLVER = false;
    protected boolean fValidation;
    protected boolean fExternalGeneralEntities = true;
    protected boolean fExternalParameterEntities = true;
    protected boolean fAllowJavaEncodings;
    protected boolean fWarnDuplicateEntityDef;
    protected boolean fStrictURI;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected ValidationManager fValidationManager;
    protected int fBufferSize = 2048;
    protected SecurityManager fSecurityManager = null;
    protected boolean fStandalone;
    protected boolean fHasPEReferences;
    protected boolean fInExternalSubset = false;
    protected XMLEntityHandler fEntityHandler;
    protected XMLEntityScanner fEntityScanner;
    protected XMLEntityScanner fXML10EntityScanner;
    protected XMLEntityScanner fXML11EntityScanner;
    protected int fEntityExpansionLimit = 0;
    protected int fEntityExpansionCount = 0;
    protected final Hashtable fEntities = new Hashtable();
    protected final Stack fEntityStack = new Stack();
    protected ScannedEntity fCurrentEntity;
    protected Hashtable fDeclaredEntities;
    private final XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
    private final Augmentations fEntityAugs = new AugmentationsImpl();
    private final ByteBufferPool fSmallByteBufferPool = new ByteBufferPool(this.fBufferSize);
    private final ByteBufferPool fLargeByteBufferPool = new ByteBufferPool(this.fBufferSize << 1);
    private byte[] fTempByteBuffer = null;
    private final CharacterBufferPool fCharacterBufferPool = new CharacterBufferPool(this.fBufferSize, 512);
    protected Stack fReaderStack = new Stack();
    private static String gUserDir;
    private static URI gUserDirURI;
    private static final boolean[] gNeedEscaping;
    private static final char[] gAfterEscaping1;
    private static final char[] gAfterEscaping2;
    private static final char[] gHexChs;
    private static PrivilegedAction GET_USER_DIR_SYSTEM_PROPERTY;

    public XMLEntityManager() {
        this(null);
    }

    public XMLEntityManager(XMLEntityManager xMLEntityManager) {
        this.fDeclaredEntities = xMLEntityManager != null ? xMLEntityManager.getDeclaredEntities() : null;
        this.setScannerVersion((short)1);
    }

    public void setStandalone(boolean bl) {
        this.fStandalone = bl;
    }

    public boolean isStandalone() {
        return this.fStandalone;
    }

    final void notifyHasPEReferences() {
        this.fHasPEReferences = true;
    }

    final boolean hasPEReferences() {
        return this.fHasPEReferences;
    }

    public void setEntityHandler(XMLEntityHandler xMLEntityHandler) {
        this.fEntityHandler = xMLEntityHandler;
    }

    public XMLResourceIdentifier getCurrentResourceIdentifier() {
        return this.fResourceIdentifier;
    }

    public ScannedEntity getCurrentEntity() {
        return this.fCurrentEntity;
    }

    public void addInternalEntity(String string, String string2, int n) {
        if (!this.fEntities.containsKey(string)) {
            InternalEntity internalEntity = new InternalEntity(string, string2, this.fInExternalSubset, n);
            this.fEntities.put(string, internalEntity);
        } else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[]{string}, (short)0);
        }
    }

    public void addInternalEntity(String string, String string2) {
        this.addInternalEntity(string, string2, 0);
    }

    public int getParamEntityRefCount(String string) {
        Entity entity;
        if (string != null && string.length() > 0 && string.charAt(0) == '%' && (entity = (Entity)this.fEntities.get(string)) != null && !entity.isExternal()) {
            return ((InternalEntity)entity).paramEntityRefs;
        }
        return 0;
    }

    public void addExternalEntity(String string, String string2, String string3, String string4) throws IOException {
        if (!this.fEntities.containsKey(string)) {
            if (string4 == null) {
                int n = this.fEntityStack.size();
                if (n == 0 && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
                    string4 = this.fCurrentEntity.entityLocation.getExpandedSystemId();
                }
                for (int i = n - 1; i >= 0; --i) {
                    ScannedEntity scannedEntity = (ScannedEntity)this.fEntityStack.elementAt(i);
                    if (scannedEntity.entityLocation == null || scannedEntity.entityLocation.getExpandedSystemId() == null) continue;
                    string4 = scannedEntity.entityLocation.getExpandedSystemId();
                    break;
                }
            }
            ExternalEntity externalEntity = new ExternalEntity(string, new XMLEntityDescriptionImpl(string, string2, string3, string4, XMLEntityManager.expandSystemId(string3, string4, false)), null, this.fInExternalSubset);
            this.fEntities.put(string, externalEntity);
        } else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[]{string}, (short)0);
        }
    }

    public boolean isExternalEntity(String string) {
        Entity entity = (Entity)this.fEntities.get(string);
        if (entity == null) {
            return false;
        }
        return entity.isExternal();
    }

    public boolean isEntityDeclInExternalSubset(String string) {
        Entity entity = (Entity)this.fEntities.get(string);
        if (entity == null) {
            return false;
        }
        return entity.isEntityDeclInExternalSubset();
    }

    public void addUnparsedEntity(String string, String string2, String string3, String string4, String string5) {
        if (!this.fEntities.containsKey(string)) {
            ExternalEntity externalEntity = new ExternalEntity(string, new XMLEntityDescriptionImpl(string, string2, string3, string4, null), string5, this.fInExternalSubset);
            this.fEntities.put(string, externalEntity);
        } else if (this.fWarnDuplicateEntityDef) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[]{string}, (short)0);
        }
    }

    public boolean isUnparsedEntity(String string) {
        Entity entity = (Entity)this.fEntities.get(string);
        if (entity == null) {
            return false;
        }
        return entity.isUnparsed();
    }

    public boolean isDeclaredEntity(String string) {
        Entity entity = (Entity)this.fEntities.get(string);
        return entity != null;
    }

    @Override
    public XMLInputSource resolveEntity(XMLResourceIdentifier xMLResourceIdentifier) throws IOException, XNIException {
        boolean bl;
        if (xMLResourceIdentifier == null) {
            return null;
        }
        String string = xMLResourceIdentifier.getPublicId();
        String string2 = xMLResourceIdentifier.getLiteralSystemId();
        String string3 = xMLResourceIdentifier.getBaseSystemId();
        String string4 = xMLResourceIdentifier.getExpandedSystemId();
        boolean bl2 = bl = string4 == null;
        if (string3 == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null && (string3 = this.fCurrentEntity.entityLocation.getExpandedSystemId()) != null) {
            bl = true;
        }
        XMLInputSource xMLInputSource = null;
        if (this.fEntityResolver != null) {
            if (bl) {
                string4 = XMLEntityManager.expandSystemId(string2, string3, false);
            }
            xMLResourceIdentifier.setBaseSystemId(string3);
            xMLResourceIdentifier.setExpandedSystemId(string4);
            xMLInputSource = this.fEntityResolver.resolveEntity(xMLResourceIdentifier);
        }
        if (xMLInputSource == null) {
            xMLInputSource = new XMLInputSource(string, string2, string3);
        }
        return xMLInputSource;
    }

    public void startEntity(String string, boolean bl) throws IOException, XNIException {
        int n;
        int n2;
        Entity entity = (Entity)this.fEntities.get(string);
        if (entity == null) {
            if (this.fEntityHandler != null) {
                String string2 = null;
                this.fResourceIdentifier.clear();
                this.fEntityAugs.removeAllItems();
                this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                this.fEntityHandler.startEntity(string, this.fResourceIdentifier, string2, this.fEntityAugs);
                this.fEntityAugs.removeAllItems();
                this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                this.fEntityHandler.endEntity(string, this.fEntityAugs);
            }
            return;
        }
        boolean bl2 = entity.isExternal();
        if (bl2 && (this.fValidationManager == null || !this.fValidationManager.isCachedDTD())) {
            boolean bl3;
            n2 = entity.isUnparsed() ? 1 : 0;
            n = string.startsWith("%") ? 1 : 0;
            boolean bl4 = bl3 = n == 0;
            if (n2 != 0 || bl3 && !this.fExternalGeneralEntities || n != 0 && !this.fExternalParameterEntities) {
                if (this.fEntityHandler != null) {
                    this.fResourceIdentifier.clear();
                    String string3 = null;
                    ExternalEntity externalEntity = (ExternalEntity)entity;
                    String string4 = externalEntity.entityLocation != null ? externalEntity.entityLocation.getLiteralSystemId() : null;
                    String string5 = externalEntity.entityLocation != null ? externalEntity.entityLocation.getBaseSystemId() : null;
                    String string6 = XMLEntityManager.expandSystemId(string4, string5, false);
                    this.fResourceIdentifier.setValues(externalEntity.entityLocation != null ? externalEntity.entityLocation.getPublicId() : null, string4, string5, string6);
                    this.fEntityAugs.removeAllItems();
                    this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                    this.fEntityHandler.startEntity(string, this.fResourceIdentifier, string3, this.fEntityAugs);
                    this.fEntityAugs.removeAllItems();
                    this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                    this.fEntityHandler.endEntity(string, this.fEntityAugs);
                }
                return;
            }
        }
        for (n = n2 = this.fEntityStack.size(); n >= 0; --n) {
            Entity entity2;
            Entity entity3 = entity2 = n == n2 ? this.fCurrentEntity : (Entity)this.fEntityStack.elementAt(n);
            if (entity2.name != string) continue;
            StringBuffer stringBuffer = new StringBuffer(string);
            for (int i = n + 1; i < n2; ++i) {
                entity2 = (Entity)this.fEntityStack.elementAt(i);
                stringBuffer.append(" -> ");
                stringBuffer.append(entity2.name);
            }
            stringBuffer.append(" -> ");
            stringBuffer.append(this.fCurrentEntity.name);
            stringBuffer.append(" -> ");
            stringBuffer.append(string);
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RecursiveReference", new Object[]{string, stringBuffer.toString()}, (short)2);
            if (this.fEntityHandler != null) {
                this.fResourceIdentifier.clear();
                String string7 = null;
                if (bl2) {
                    ExternalEntity externalEntity = (ExternalEntity)entity;
                    String string8 = externalEntity.entityLocation != null ? externalEntity.entityLocation.getLiteralSystemId() : null;
                    String string9 = externalEntity.entityLocation != null ? externalEntity.entityLocation.getBaseSystemId() : null;
                    String string10 = XMLEntityManager.expandSystemId(string8, string9, false);
                    this.fResourceIdentifier.setValues(externalEntity.entityLocation != null ? externalEntity.entityLocation.getPublicId() : null, string8, string9, string10);
                }
                this.fEntityAugs.removeAllItems();
                this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                this.fEntityHandler.startEntity(string, this.fResourceIdentifier, string7, this.fEntityAugs);
                this.fEntityAugs.removeAllItems();
                this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
                this.fEntityHandler.endEntity(string, this.fEntityAugs);
            }
            return;
        }
        XMLInputSource xMLInputSource = null;
        if (bl2) {
            ExternalEntity externalEntity = (ExternalEntity)entity;
            xMLInputSource = this.resolveEntity(externalEntity.entityLocation);
        } else {
            InternalEntity internalEntity = (InternalEntity)entity;
            StringReader stringReader = new StringReader(internalEntity.text);
            xMLInputSource = new XMLInputSource(null, null, null, stringReader, null);
        }
        this.startEntity(string, xMLInputSource, bl, bl2);
    }

    public void startDocumentEntity(XMLInputSource xMLInputSource) throws IOException, XNIException {
        this.startEntity(XMLEntity, xMLInputSource, false, true);
    }

    public void startDTDEntity(XMLInputSource xMLInputSource) throws IOException, XNIException {
        this.startEntity(DTDEntity, xMLInputSource, false, true);
    }

    public void startExternalSubset() {
        this.fInExternalSubset = true;
    }

    public void endExternalSubset() {
        this.fInExternalSubset = false;
    }

    public void startEntity(String string, XMLInputSource xMLInputSource, boolean bl, boolean bl2) throws IOException, XNIException {
        String string2 = this.setupCurrentEntity(string, xMLInputSource, bl, bl2);
        if (this.fSecurityManager != null) {
            this.fEntityExpansionCount += this.getParamEntityRefCount(string);
            if (this.fEntityExpansionCount++ > this.fEntityExpansionLimit) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityExpansionLimitExceeded", new Object[]{new Integer(this.fEntityExpansionLimit)}, (short)2);
                this.fEntityExpansionCount = 0;
            }
        }
        if (this.fEntityHandler != null) {
            this.fEntityHandler.startEntity(string, this.fResourceIdentifier, string2, null);
        }
    }

    public String setupCurrentEntity(String string, XMLInputSource xMLInputSource, boolean bl, boolean bl2) throws IOException, XNIException {
        String string2 = xMLInputSource.getPublicId();
        Object object = xMLInputSource.getSystemId();
        String string3 = xMLInputSource.getBaseSystemId();
        String string4 = xMLInputSource.getEncoding();
        boolean bl3 = string4 != null;
        Boolean bl4 = null;
        this.fTempByteBuffer = null;
        Object object2 = null;
        Reader reader = xMLInputSource.getCharacterStream();
        Object object3 = XMLEntityManager.expandSystemId((String)object, string3, this.fStrictURI);
        if (string3 == null) {
            string3 = object3;
        }
        if (reader == null) {
            Object object4;
            Object object5;
            int n;
            Object object6;
            Object object7;
            object2 = xMLInputSource.getByteStream();
            if (object2 == null) {
                object7 = new URL((String)object3);
                object6 = ((URL)object7).openConnection();
                if (!(object6 instanceof HttpURLConnection)) {
                    object2 = ((URLConnection)object6).getInputStream();
                } else {
                    n = 1;
                    if (xMLInputSource instanceof HTTPInputSource) {
                        object5 = (HttpURLConnection)object6;
                        object4 = (HTTPInputSource)xMLInputSource;
                        Iterator iterator = ((HTTPInputSource)object4).getHTTPRequestProperties();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry)iterator.next();
                            ((URLConnection)object5).setRequestProperty((String)entry.getKey(), (String)entry.getValue());
                        }
                        n = ((HTTPInputSource)object4).getFollowHTTPRedirects() ? 1 : 0;
                        if (n == 0) {
                            ((HttpURLConnection)object5).setInstanceFollowRedirects(n != 0);
                        }
                    }
                    object2 = ((URLConnection)object6).getInputStream();
                    if (n != 0 && !((String)(object5 = ((URLConnection)object6).getURL().toString())).equals(object3)) {
                        object = object5;
                        object3 = object5;
                    }
                }
            }
            object2 = object7 = new RewindableInputStream((InputStream)object2);
            if (string4 == null) {
                object6 = new byte[4];
                for (n = 0; n < 4; ++n) {
                    object6[n] = (byte)((RewindableInputStream)object7).readAndBuffer();
                }
                if (n == 4) {
                    object5 = this.getEncodingInfo((byte[])object6, n);
                    string4 = ((EncodingInfo)object5).autoDetectedEncoding;
                    object4 = ((EncodingInfo)object5).readerEncoding;
                    bl4 = ((EncodingInfo)object5).isBigEndian;
                    ((InputStream)object2).reset();
                    if (((EncodingInfo)object5).hasBOM) {
                        if (object4 == "UTF-8") {
                            ((InputStream)object2).skip(3L);
                        } else if (object4 == "UTF-16") {
                            ((InputStream)object2).skip(2L);
                        }
                    }
                    reader = this.createReader((InputStream)object2, (String)object4, bl4);
                } else {
                    reader = this.createReader((InputStream)object2, string4, bl4);
                }
            } else if ((string4 = string4.toUpperCase(Locale.ENGLISH)).equals("UTF-8")) {
                object6 = new int[3];
                for (n = 0; n < 3; ++n) {
                    object6[n] = ((RewindableInputStream)object7).readAndBuffer();
                    if (object6[n] == -1) break;
                }
                if (n == 3) {
                    if (object6[0] != 239 || object6[1] != 187 || object6[2] != 191) {
                        ((InputStream)object2).reset();
                    }
                } else {
                    ((InputStream)object2).reset();
                }
                reader = this.createReader((InputStream)object2, "UTF-8", bl4);
            } else if (string4.equals("UTF-16")) {
                object6 = new int[4];
                for (n = 0; n < 4; ++n) {
                    object6[n] = ((RewindableInputStream)object7).readAndBuffer();
                    if (object6[n] == -1) break;
                }
                ((InputStream)object2).reset();
                if (n >= 2) {
                    Object object8 = object6[0];
                    Object object9 = object6[1];
                    if (object8 == 254 && object9 == 255) {
                        bl4 = Boolean.TRUE;
                        ((InputStream)object2).skip(2L);
                    } else if (object8 == 255 && object9 == 254) {
                        bl4 = Boolean.FALSE;
                        ((InputStream)object2).skip(2L);
                    } else if (n == 4) {
                        Object object10 = object6[2];
                        Object object11 = object6[3];
                        if (object8 == false && object9 == 60 && object10 == false && object11 == 63) {
                            bl4 = Boolean.TRUE;
                        }
                        if (object8 == 60 && object9 == false && object10 == 63 && object11 == false) {
                            bl4 = Boolean.FALSE;
                        }
                    }
                }
                reader = this.createReader((InputStream)object2, "UTF-16", bl4);
            } else if (string4.equals("ISO-10646-UCS-4")) {
                object6 = new int[4];
                for (n = 0; n < 4; ++n) {
                    object6[n] = ((RewindableInputStream)object7).readAndBuffer();
                    if (object6[n] == -1) break;
                }
                ((InputStream)object2).reset();
                if (n == 4) {
                    if (object6[0] == false && object6[1] == false && object6[2] == false && object6[3] == 60) {
                        bl4 = Boolean.TRUE;
                    } else if (object6[0] == 60 && object6[1] == false && object6[2] == false && object6[3] == false) {
                        bl4 = Boolean.FALSE;
                    }
                }
                reader = this.createReader((InputStream)object2, string4, bl4);
            } else if (string4.equals("ISO-10646-UCS-2")) {
                object6 = new int[4];
                for (n = 0; n < 4; ++n) {
                    object6[n] = ((RewindableInputStream)object7).readAndBuffer();
                    if (object6[n] == -1) break;
                }
                ((InputStream)object2).reset();
                if (n == 4) {
                    if (object6[0] == false && object6[1] == 60 && object6[2] == false && object6[3] == 63) {
                        bl4 = Boolean.TRUE;
                    } else if (object6[0] == 60 && object6[1] == false && object6[2] == 63 && object6[3] == false) {
                        bl4 = Boolean.FALSE;
                    }
                }
                reader = this.createReader((InputStream)object2, string4, bl4);
            } else {
                reader = this.createReader((InputStream)object2, string4, bl4);
            }
        }
        this.fReaderStack.push(reader);
        if (this.fCurrentEntity != null) {
            this.fEntityStack.push(this.fCurrentEntity);
        }
        this.fCurrentEntity = new ScannedEntity(string, new XMLResourceIdentifierImpl(string2, (String)object, string3, (String)object3), (InputStream)object2, reader, this.fTempByteBuffer, string4, bl, false, bl2);
        this.fCurrentEntity.setEncodingExternallySpecified(bl3);
        this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
        this.fResourceIdentifier.setValues(string2, (String)object, string3, (String)object3);
        return string4;
    }

    public void setScannerVersion(short s) {
        if (s == 1) {
            if (this.fXML10EntityScanner == null) {
                this.fXML10EntityScanner = new XMLEntityScanner();
            }
            this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
            this.fEntityScanner = this.fXML10EntityScanner;
            this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
        } else {
            if (this.fXML11EntityScanner == null) {
                this.fXML11EntityScanner = new XML11EntityScanner();
            }
            this.fXML11EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
            this.fEntityScanner = this.fXML11EntityScanner;
            this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
        }
    }

    public XMLEntityScanner getEntityScanner() {
        if (this.fEntityScanner == null) {
            if (this.fXML10EntityScanner == null) {
                this.fXML10EntityScanner = new XMLEntityScanner();
            }
            this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
            this.fEntityScanner = this.fXML10EntityScanner;
        }
        return this.fEntityScanner;
    }

    public void closeReaders() {
        for (int i = this.fReaderStack.size() - 1; i >= 0; --i) {
            try {
                ((Reader)this.fReaderStack.pop()).close();
                continue;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        boolean bl;
        try {
            bl = xMLComponentManager.getFeature(PARSER_SETTINGS);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            bl = true;
        }
        if (!bl) {
            this.reset();
            return;
        }
        try {
            this.fValidation = xMLComponentManager.getFeature(VALIDATION);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fValidation = false;
        }
        try {
            this.fExternalGeneralEntities = xMLComponentManager.getFeature(EXTERNAL_GENERAL_ENTITIES);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fExternalGeneralEntities = true;
        }
        try {
            this.fExternalParameterEntities = xMLComponentManager.getFeature(EXTERNAL_PARAMETER_ENTITIES);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fExternalParameterEntities = true;
        }
        try {
            this.fAllowJavaEncodings = xMLComponentManager.getFeature(ALLOW_JAVA_ENCODINGS);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fAllowJavaEncodings = false;
        }
        try {
            this.fWarnDuplicateEntityDef = xMLComponentManager.getFeature(WARN_ON_DUPLICATE_ENTITYDEF);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fWarnDuplicateEntityDef = false;
        }
        try {
            this.fStrictURI = xMLComponentManager.getFeature(STANDARD_URI_CONFORMANT);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fStrictURI = false;
        }
        this.fSymbolTable = (SymbolTable)xMLComponentManager.getProperty(SYMBOL_TABLE);
        this.fErrorReporter = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
        try {
            this.fEntityResolver = (XMLEntityResolver)xMLComponentManager.getProperty(ENTITY_RESOLVER);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fEntityResolver = null;
        }
        try {
            this.fValidationManager = (ValidationManager)xMLComponentManager.getProperty(VALIDATION_MANAGER);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fValidationManager = null;
        }
        try {
            this.fSecurityManager = (SecurityManager)xMLComponentManager.getProperty(SECURITY_MANAGER);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fSecurityManager = null;
        }
        this.reset();
    }

    public void reset() {
        this.fEntityExpansionLimit = this.fSecurityManager != null ? this.fSecurityManager.getEntityExpansionLimit() : 0;
        this.fStandalone = false;
        this.fHasPEReferences = false;
        this.fEntities.clear();
        this.fEntityStack.removeAllElements();
        this.fEntityExpansionCount = 0;
        this.fCurrentEntity = null;
        if (this.fXML10EntityScanner != null) {
            this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
        }
        if (this.fXML11EntityScanner != null) {
            this.fXML11EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
        }
        if (this.fDeclaredEntities != null) {
            for (Map.Entry entry : this.fDeclaredEntities.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                this.fEntities.put(k, v);
            }
        }
        this.fEntityHandler = null;
    }

    @Override
    public String[] getRecognizedFeatures() {
        return (String[])RECOGNIZED_FEATURES.clone();
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        int n;
        if (string.startsWith("http://apache.org/xml/features/") && (n = string.length() - "http://apache.org/xml/features/".length()) == "allow-java-encodings".length() && string.endsWith("allow-java-encodings")) {
            this.fAllowJavaEncodings = bl;
        }
    }

    @Override
    public String[] getRecognizedProperties() {
        return (String[])RECOGNIZED_PROPERTIES.clone();
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        if (string.startsWith("http://apache.org/xml/properties/")) {
            Integer n;
            int n2 = string.length() - "http://apache.org/xml/properties/".length();
            if (n2 == "internal/symbol-table".length() && string.endsWith("internal/symbol-table")) {
                this.fSymbolTable = (SymbolTable)object;
                return;
            }
            if (n2 == "internal/error-reporter".length() && string.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)object;
                return;
            }
            if (n2 == "internal/entity-resolver".length() && string.endsWith("internal/entity-resolver")) {
                this.fEntityResolver = (XMLEntityResolver)object;
                return;
            }
            if (n2 == "input-buffer-size".length() && string.endsWith("input-buffer-size") && (n = (Integer)object) != null && n > 64) {
                this.fBufferSize = n;
                this.fEntityScanner.setBufferSize(this.fBufferSize);
                this.fSmallByteBufferPool.setBufferSize(this.fBufferSize);
                this.fLargeByteBufferPool.setBufferSize(this.fBufferSize << 1);
                this.fCharacterBufferPool.setExternalBufferSize(this.fBufferSize);
            }
            if (n2 == "security-manager".length() && string.endsWith("security-manager")) {
                this.fSecurityManager = (SecurityManager)object;
                this.fEntityExpansionLimit = this.fSecurityManager != null ? this.fSecurityManager.getEntityExpansionLimit() : 0;
            }
        }
    }

    @Override
    public Boolean getFeatureDefault(String string) {
        for (int i = 0; i < RECOGNIZED_FEATURES.length; ++i) {
            if (!RECOGNIZED_FEATURES[i].equals(string)) continue;
            return FEATURE_DEFAULTS[i];
        }
        return null;
    }

    @Override
    public Object getPropertyDefault(String string) {
        for (int i = 0; i < RECOGNIZED_PROPERTIES.length; ++i) {
            if (!RECOGNIZED_PROPERTIES[i].equals(string)) continue;
            return PROPERTY_DEFAULTS[i];
        }
        return null;
    }

    private static synchronized URI getUserDir() throws URI.MalformedURIException {
        int n;
        int n2;
        String string = "";
        try {
            string = (String)AccessController.doPrivileged(GET_USER_DIR_SYSTEM_PROPERTY);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (string.length() == 0) {
            return new URI("file", "", "", null, null);
        }
        if (gUserDirURI != null && string.equals(gUserDir)) {
            return gUserDirURI;
        }
        gUserDir = string;
        char c = File.separatorChar;
        string = string.replace(c, '/');
        int n3 = string.length();
        StringBuffer stringBuffer = new StringBuffer(n3 * 3);
        if (n3 >= 2 && string.charAt(1) == ':' && (n2 = Character.toUpperCase(string.charAt(0))) >= 65 && n2 <= 90) {
            stringBuffer.append('/');
        }
        for (n = 0; n < n3 && (n2 = string.charAt(n)) < 128; ++n) {
            if (gNeedEscaping[n2]) {
                stringBuffer.append('%');
                stringBuffer.append(gAfterEscaping1[n2]);
                stringBuffer.append(gAfterEscaping2[n2]);
                continue;
            }
            stringBuffer.append((char)n2);
        }
        if (n < n3) {
            byte[] byArray = null;
            try {
                byArray = string.substring(n).getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                return new URI("file", "", string, null, null);
            }
            for (byte by : byArray) {
                if (by < 0) {
                    n2 = by + 256;
                    stringBuffer.append('%');
                    stringBuffer.append(gHexChs[n2 >> 4]);
                    stringBuffer.append(gHexChs[n2 & 0xF]);
                    continue;
                }
                if (gNeedEscaping[by]) {
                    stringBuffer.append('%');
                    stringBuffer.append(gAfterEscaping1[by]);
                    stringBuffer.append(gAfterEscaping2[by]);
                    continue;
                }
                stringBuffer.append((char)by);
            }
        }
        if (!string.endsWith("/")) {
            stringBuffer.append('/');
        }
        gUserDirURI = new URI("file", "", stringBuffer.toString(), null, null);
        return gUserDirURI;
    }

    public static void absolutizeAgainstUserDir(URI uRI) throws URI.MalformedURIException {
        uRI.absolutize(XMLEntityManager.getUserDir());
    }

    public static String expandSystemId(String string, String string2, boolean bl) throws URI.MalformedURIException {
        if (string == null) {
            return null;
        }
        if (bl) {
            return XMLEntityManager.expandSystemIdStrictOn(string, string2);
        }
        try {
            return XMLEntityManager.expandSystemIdStrictOff(string, string2);
        }
        catch (URI.MalformedURIException malformedURIException) {
            if (string.length() == 0) {
                return string;
            }
            String string3 = XMLEntityManager.fixURI(string);
            URI uRI = null;
            URI uRI2 = null;
            try {
                if (string2 == null || string2.length() == 0 || string2.equals(string)) {
                    uRI = XMLEntityManager.getUserDir();
                } else {
                    try {
                        uRI = new URI(XMLEntityManager.fixURI(string2).trim());
                    }
                    catch (URI.MalformedURIException malformedURIException2) {
                        uRI = string2.indexOf(58) != -1 ? new URI("file", "", XMLEntityManager.fixURI(string2).trim(), null, null) : new URI(XMLEntityManager.getUserDir(), XMLEntityManager.fixURI(string2));
                    }
                }
                uRI2 = new URI(uRI, string3.trim());
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (uRI2 == null) {
                return string;
            }
            return uRI2.toString();
        }
    }

    private static String expandSystemIdStrictOn(String string, String string2) throws URI.MalformedURIException {
        URI uRI = new URI(string, true);
        if (uRI.isAbsoluteURI()) {
            return string;
        }
        URI uRI2 = null;
        if (string2 == null || string2.length() == 0) {
            uRI2 = XMLEntityManager.getUserDir();
        } else {
            uRI2 = new URI(string2, true);
            if (!uRI2.isAbsoluteURI()) {
                uRI2.absolutize(XMLEntityManager.getUserDir());
            }
        }
        uRI.absolutize(uRI2);
        return uRI.toString();
    }

    private static String expandSystemIdStrictOff(String string, String string2) throws URI.MalformedURIException {
        URI uRI = new URI(string, true);
        if (uRI.isAbsoluteURI()) {
            if (uRI.getScheme().length() > 1) {
                return string;
            }
            throw new URI.MalformedURIException();
        }
        URI uRI2 = null;
        if (string2 == null || string2.length() == 0) {
            uRI2 = XMLEntityManager.getUserDir();
        } else {
            uRI2 = new URI(string2, true);
            if (!uRI2.isAbsoluteURI()) {
                uRI2.absolutize(XMLEntityManager.getUserDir());
            }
        }
        uRI.absolutize(uRI2);
        return uRI.toString();
    }

    public static OutputStream createOutputStream(String string) throws IOException {
        String string2 = XMLEntityManager.expandSystemId(string, null, true);
        URL uRL = new URL(string2 != null ? string2 : string);
        OutputStream outputStream = null;
        String string3 = uRL.getProtocol();
        String string4 = uRL.getHost();
        if (string3.equals("file") && (string4 == null || string4.length() == 0 || string4.equals("localhost"))) {
            File file;
            File file2 = new File(XMLEntityManager.getPathWithoutEscapes(uRL.getPath()));
            if (!file2.exists() && (file = file2.getParentFile()) != null && !file.exists()) {
                file.mkdirs();
            }
            outputStream = new FileOutputStream(file2);
        } else {
            URLConnection uRLConnection = uRL.openConnection();
            uRLConnection.setDoInput(false);
            uRLConnection.setDoOutput(true);
            uRLConnection.setUseCaches(false);
            if (uRLConnection instanceof HttpURLConnection) {
                HttpURLConnection httpURLConnection = (HttpURLConnection)uRLConnection;
                httpURLConnection.setRequestMethod("PUT");
            }
            outputStream = uRLConnection.getOutputStream();
        }
        return outputStream;
    }

    private static String getPathWithoutEscapes(String string) {
        if (string != null && string.length() != 0 && string.indexOf(37) != -1) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, "%");
            StringBuffer stringBuffer = new StringBuffer(string.length());
            int n = stringTokenizer.countTokens();
            stringBuffer.append(stringTokenizer.nextToken());
            for (int i = 1; i < n; ++i) {
                String string2 = stringTokenizer.nextToken();
                stringBuffer.append((char)Integer.valueOf(string2.substring(0, 2), 16).intValue());
                stringBuffer.append(string2.substring(2));
            }
            return stringBuffer.toString();
        }
        return string;
    }

    void endEntity() throws XNIException {
        if (this.fEntityHandler != null) {
            this.fEntityHandler.endEntity(this.fCurrentEntity.name, null);
        }
        try {
            this.fCurrentEntity.reader.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (!this.fReaderStack.isEmpty()) {
            this.fReaderStack.pop();
        }
        this.fCharacterBufferPool.returnBuffer(this.fCurrentEntity.fCharacterBuffer);
        if (this.fCurrentEntity.fByteBuffer != null) {
            if (this.fCurrentEntity.fByteBuffer.length == this.fBufferSize) {
                this.fSmallByteBufferPool.returnBuffer(this.fCurrentEntity.fByteBuffer);
            } else {
                this.fLargeByteBufferPool.returnBuffer(this.fCurrentEntity.fByteBuffer);
            }
        }
        this.fCurrentEntity = this.fEntityStack.size() > 0 ? (ScannedEntity)this.fEntityStack.pop() : null;
        this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
    }

    protected EncodingInfo getEncodingInfo(byte[] byArray, int n) {
        if (n < 2) {
            return EncodingInfo.UTF_8;
        }
        int n2 = byArray[0] & 0xFF;
        int n3 = byArray[1] & 0xFF;
        if (n2 == 254 && n3 == 255) {
            return EncodingInfo.UTF_16_BIG_ENDIAN_WITH_BOM;
        }
        if (n2 == 255 && n3 == 254) {
            return EncodingInfo.UTF_16_LITTLE_ENDIAN_WITH_BOM;
        }
        if (n < 3) {
            return EncodingInfo.UTF_8;
        }
        int n4 = byArray[2] & 0xFF;
        if (n2 == 239 && n3 == 187 && n4 == 191) {
            return EncodingInfo.UTF_8_WITH_BOM;
        }
        if (n < 4) {
            return EncodingInfo.UTF_8;
        }
        int n5 = byArray[3] & 0xFF;
        if (n2 == 0 && n3 == 0 && n4 == 0 && n5 == 60) {
            return EncodingInfo.UCS_4_BIG_ENDIAN;
        }
        if (n2 == 60 && n3 == 0 && n4 == 0 && n5 == 0) {
            return EncodingInfo.UCS_4_LITTLE_ENDIAN;
        }
        if (n2 == 0 && n3 == 0 && n4 == 60 && n5 == 0) {
            return EncodingInfo.UCS_4_UNUSUAL_BYTE_ORDER;
        }
        if (n2 == 0 && n3 == 60 && n4 == 0 && n5 == 0) {
            return EncodingInfo.UCS_4_UNUSUAL_BYTE_ORDER;
        }
        if (n2 == 0 && n3 == 60 && n4 == 0 && n5 == 63) {
            return EncodingInfo.UTF_16_BIG_ENDIAN;
        }
        if (n2 == 60 && n3 == 0 && n4 == 63 && n5 == 0) {
            return EncodingInfo.UTF_16_LITTLE_ENDIAN;
        }
        if (n2 == 76 && n3 == 111 && n4 == 167 && n5 == 148) {
            return EncodingInfo.EBCDIC;
        }
        return EncodingInfo.UTF_8;
    }

    /*
     * Enabled aggressive block sorting
     */
    protected Reader createReader(InputStream inputStream, String string, Boolean bl) throws IOException {
        if (string == "UTF-8") return this.createUTF8Reader(inputStream);
        if (string == null) {
            return this.createUTF8Reader(inputStream);
        }
        if (string == "UTF-16" && bl != null) {
            return this.createUTF16Reader(inputStream, bl);
        }
        String string2 = string.toUpperCase(Locale.ENGLISH);
        if (string2.equals("UTF-8")) {
            return this.createUTF8Reader(inputStream);
        }
        if (string2.equals("UTF-16BE")) {
            return this.createUTF16Reader(inputStream, true);
        }
        if (string2.equals("UTF-16LE")) {
            return this.createUTF16Reader(inputStream, false);
        }
        if (string2.equals("ISO-10646-UCS-4")) {
            if (bl != null) {
                boolean bl2 = bl;
                if (!bl2) return new UCSReader(inputStream, 4);
                return new UCSReader(inputStream, 8);
            }
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[]{string}, (short)2);
        }
        if (string2.equals("ISO-10646-UCS-2")) {
            if (bl != null) {
                boolean bl3 = bl;
                if (!bl3) return new UCSReader(inputStream, 1);
                return new UCSReader(inputStream, 2);
            }
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[]{string}, (short)2);
        }
        boolean bl4 = XMLChar.isValidIANAEncoding(string);
        boolean bl5 = XMLChar.isValidJavaEncoding(string);
        if (!bl4 || this.fAllowJavaEncodings && !bl5) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[]{string}, (short)2);
            return this.createLatin1Reader(inputStream);
        }
        String string3 = EncodingMap.getIANA2JavaMapping(string2);
        if (string3 == null) {
            if (this.fAllowJavaEncodings) {
                string3 = string;
                return new InputStreamReader(inputStream, string3);
            }
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[]{string}, (short)2);
            return this.createLatin1Reader(inputStream);
        }
        if (string3.equals("ASCII")) {
            return this.createASCIIReader(inputStream);
        }
        if (!string3.equals("ISO8859_1")) return new InputStreamReader(inputStream, string3);
        return this.createLatin1Reader(inputStream);
    }

    private Reader createUTF8Reader(InputStream inputStream) {
        if (this.fTempByteBuffer == null) {
            this.fTempByteBuffer = this.fSmallByteBufferPool.getBuffer();
        }
        return new UTF8Reader(inputStream, this.fTempByteBuffer, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }

    private Reader createUTF16Reader(InputStream inputStream, boolean bl) {
        if (this.fTempByteBuffer == null) {
            this.fTempByteBuffer = this.fLargeByteBufferPool.getBuffer();
        } else if (this.fTempByteBuffer.length == this.fBufferSize) {
            this.fSmallByteBufferPool.returnBuffer(this.fTempByteBuffer);
            this.fTempByteBuffer = this.fLargeByteBufferPool.getBuffer();
        }
        return new UTF16Reader(inputStream, this.fTempByteBuffer, bl, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }

    private Reader createASCIIReader(InputStream inputStream) {
        if (this.fTempByteBuffer == null) {
            this.fTempByteBuffer = this.fSmallByteBufferPool.getBuffer();
        }
        return new ASCIIReader(inputStream, this.fTempByteBuffer, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    }

    private Reader createLatin1Reader(InputStream inputStream) {
        if (this.fTempByteBuffer == null) {
            this.fTempByteBuffer = this.fSmallByteBufferPool.getBuffer();
        }
        return new Latin1Reader(inputStream, this.fTempByteBuffer);
    }

    protected static String fixURI(String string) {
        int n;
        int n2;
        string = string.replace(File.separatorChar, '/');
        StringBuffer stringBuffer = null;
        if (string.length() >= 2) {
            n2 = string.charAt(1);
            if (n2 == 58) {
                n = Character.toUpperCase(string.charAt(0));
                if (n >= 65 && n <= 90) {
                    stringBuffer = new StringBuffer(string.length() + 8);
                    stringBuffer.append("file:///");
                }
            } else if (n2 == 47 && string.charAt(0) == '/') {
                stringBuffer = new StringBuffer(string.length() + 5);
                stringBuffer.append("file:");
            }
        }
        if ((n2 = string.indexOf(32)) < 0) {
            if (stringBuffer != null) {
                stringBuffer.append(string);
                string = stringBuffer.toString();
            }
        } else {
            if (stringBuffer == null) {
                stringBuffer = new StringBuffer(string.length());
            }
            for (n = 0; n < n2; ++n) {
                stringBuffer.append(string.charAt(n));
            }
            stringBuffer.append("%20");
            for (n = n2 + 1; n < string.length(); ++n) {
                if (string.charAt(n) == ' ') {
                    stringBuffer.append("%20");
                    continue;
                }
                stringBuffer.append(string.charAt(n));
            }
            string = stringBuffer.toString();
        }
        return string;
    }

    Hashtable getDeclaredEntities() {
        return this.fEntities;
    }

    static final void print(ScannedEntity scannedEntity) {
    }

    static /* synthetic */ byte[] access$402(XMLEntityManager xMLEntityManager, byte[] byArray) {
        xMLEntityManager.fTempByteBuffer = byArray;
        return byArray;
    }

    static {
        gNeedEscaping = new boolean[128];
        gAfterEscaping1 = new char[128];
        gAfterEscaping2 = new char[128];
        gHexChs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0; i <= 31; ++i) {
            XMLEntityManager.gNeedEscaping[i] = true;
            XMLEntityManager.gAfterEscaping1[i] = gHexChs[i >> 4];
            XMLEntityManager.gAfterEscaping2[i] = gHexChs[i & 0xF];
        }
        XMLEntityManager.gNeedEscaping[127] = true;
        XMLEntityManager.gAfterEscaping1[127] = 55;
        XMLEntityManager.gAfterEscaping2[127] = 70;
        for (char c : new char[]{' ', '<', '>', '#', '%', '\"', '{', '}', '|', '\\', '^', '~', '[', ']', '`'}) {
            XMLEntityManager.gNeedEscaping[c] = true;
            XMLEntityManager.gAfterEscaping1[c] = gHexChs[c >> 4];
            XMLEntityManager.gAfterEscaping2[c] = gHexChs[c & 0xF];
        }
        GET_USER_DIR_SYSTEM_PROPERTY = new PrivilegedAction(){

            public Object run() {
                return System.getProperty("user.dir");
            }
        };
    }

    protected final class RewindableInputStream
    extends InputStream {
        private InputStream fInputStream;
        private byte[] fData = new byte[64];
        private int fStartOffset;
        private int fEndOffset;
        private int fOffset;
        private int fLength;
        private int fMark;

        public RewindableInputStream(InputStream inputStream) {
            this.fInputStream = inputStream;
            this.fStartOffset = 0;
            this.fEndOffset = -1;
            this.fOffset = 0;
            this.fLength = 0;
            this.fMark = 0;
        }

        public void setStartOffset(int n) {
            this.fStartOffset = n;
        }

        public void rewind() {
            this.fOffset = this.fStartOffset;
        }

        public int readAndBuffer() throws IOException {
            int n;
            if (this.fOffset == this.fData.length) {
                byte[] byArray = new byte[this.fOffset << 1];
                System.arraycopy(this.fData, 0, byArray, 0, this.fOffset);
                this.fData = byArray;
            }
            if ((n = this.fInputStream.read()) == -1) {
                this.fEndOffset = this.fOffset;
                return -1;
            }
            this.fData[this.fLength++] = (byte)n;
            ++this.fOffset;
            return n & 0xFF;
        }

        @Override
        public int read() throws IOException {
            if (this.fOffset < this.fLength) {
                return this.fData[this.fOffset++] & 0xFF;
            }
            if (this.fOffset == this.fEndOffset) {
                return -1;
            }
            if (XMLEntityManager.this.fCurrentEntity.mayReadChunks) {
                return this.fInputStream.read();
            }
            return this.readAndBuffer();
        }

        @Override
        public int read(byte[] byArray, int n, int n2) throws IOException {
            int n3 = this.fLength - this.fOffset;
            if (n3 == 0) {
                if (this.fOffset == this.fEndOffset) {
                    return -1;
                }
                if (XMLEntityManager.this.fCurrentEntity.mayReadChunks) {
                    return this.fInputStream.read(byArray, n, n2);
                }
                int n4 = this.readAndBuffer();
                if (n4 == -1) {
                    this.fEndOffset = this.fOffset;
                    return -1;
                }
                byArray[n] = (byte)n4;
                return 1;
            }
            if (n2 < n3) {
                if (n2 <= 0) {
                    return 0;
                }
            } else {
                n2 = n3;
            }
            if (byArray != null) {
                System.arraycopy(this.fData, this.fOffset, byArray, n, n2);
            }
            this.fOffset += n2;
            return n2;
        }

        @Override
        public long skip(long l) throws IOException {
            if (l <= 0L) {
                return 0L;
            }
            int n = this.fLength - this.fOffset;
            if (n == 0) {
                if (this.fOffset == this.fEndOffset) {
                    return 0L;
                }
                return this.fInputStream.skip(l);
            }
            if (l <= (long)n) {
                this.fOffset = (int)((long)this.fOffset + l);
                return l;
            }
            this.fOffset += n;
            if (this.fOffset == this.fEndOffset) {
                return n;
            }
            return this.fInputStream.skip(l -= (long)n) + (long)n;
        }

        @Override
        public int available() throws IOException {
            int n = this.fLength - this.fOffset;
            if (n == 0) {
                if (this.fOffset == this.fEndOffset) {
                    return -1;
                }
                return XMLEntityManager.this.fCurrentEntity.mayReadChunks ? this.fInputStream.available() : 0;
            }
            return n;
        }

        @Override
        public void mark(int n) {
            this.fMark = this.fOffset;
        }

        @Override
        public void reset() {
            this.fOffset = this.fMark;
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void close() throws IOException {
            if (this.fInputStream != null) {
                this.fInputStream.close();
                this.fInputStream = null;
            }
        }
    }

    private static final class CharacterBufferPool {
        private static final int DEFAULT_POOL_SIZE = 3;
        private CharacterBuffer[] fInternalBufferPool;
        private CharacterBuffer[] fExternalBufferPool;
        private int fExternalBufferSize;
        private int fInternalBufferSize;
        private int fPoolSize;
        private int fInternalTop;
        private int fExternalTop;

        public CharacterBufferPool(int n, int n2) {
            this(3, n, n2);
        }

        public CharacterBufferPool(int n, int n2, int n3) {
            this.fExternalBufferSize = n2;
            this.fInternalBufferSize = n3;
            this.fPoolSize = n;
            this.init();
        }

        private void init() {
            this.fInternalBufferPool = new CharacterBuffer[this.fPoolSize];
            this.fExternalBufferPool = new CharacterBuffer[this.fPoolSize];
            this.fInternalTop = -1;
            this.fExternalTop = -1;
        }

        public CharacterBuffer getBuffer(boolean bl) {
            if (bl) {
                if (this.fExternalTop > -1) {
                    return this.fExternalBufferPool[this.fExternalTop--];
                }
                return new CharacterBuffer(true, this.fExternalBufferSize);
            }
            if (this.fInternalTop > -1) {
                return this.fInternalBufferPool[this.fInternalTop--];
            }
            return new CharacterBuffer(false, this.fInternalBufferSize);
        }

        public void returnBuffer(CharacterBuffer characterBuffer) {
            if (characterBuffer.isExternal) {
                if (this.fExternalTop < this.fExternalBufferPool.length - 1) {
                    this.fExternalBufferPool[++this.fExternalTop] = characterBuffer;
                }
            } else if (this.fInternalTop < this.fInternalBufferPool.length - 1) {
                this.fInternalBufferPool[++this.fInternalTop] = characterBuffer;
            }
        }

        public void setExternalBufferSize(int n) {
            this.fExternalBufferSize = n;
            this.fExternalBufferPool = new CharacterBuffer[this.fPoolSize];
            this.fExternalTop = -1;
        }
    }

    private static final class CharacterBuffer {
        private final char[] ch;
        private final boolean isExternal;

        public CharacterBuffer(boolean bl, int n) {
            this.isExternal = bl;
            this.ch = new char[n];
        }
    }

    private static final class ByteBufferPool {
        private static final int DEFAULT_POOL_SIZE = 3;
        private int fPoolSize;
        private int fBufferSize;
        private byte[][] fByteBufferPool;
        private int fDepth;

        public ByteBufferPool(int n) {
            this(3, n);
        }

        public ByteBufferPool(int n, int n2) {
            this.fPoolSize = n;
            this.fBufferSize = n2;
            this.fByteBufferPool = new byte[this.fPoolSize][];
            this.fDepth = 0;
        }

        public byte[] getBuffer() {
            return this.fDepth > 0 ? this.fByteBufferPool[--this.fDepth] : new byte[this.fBufferSize];
        }

        public void returnBuffer(byte[] byArray) {
            if (this.fDepth < this.fByteBufferPool.length) {
                this.fByteBufferPool[this.fDepth++] = byArray;
            }
        }

        public void setBufferSize(int n) {
            this.fBufferSize = n;
            this.fByteBufferPool = new byte[this.fPoolSize][];
            this.fDepth = 0;
        }
    }

    private static class EncodingInfo {
        public static final EncodingInfo UTF_8 = new EncodingInfo("UTF-8", null, false);
        public static final EncodingInfo UTF_8_WITH_BOM = new EncodingInfo("UTF-8", null, true);
        public static final EncodingInfo UTF_16_BIG_ENDIAN = new EncodingInfo("UTF-16BE", "UTF-16", Boolean.TRUE, false);
        public static final EncodingInfo UTF_16_BIG_ENDIAN_WITH_BOM = new EncodingInfo("UTF-16BE", "UTF-16", Boolean.TRUE, true);
        public static final EncodingInfo UTF_16_LITTLE_ENDIAN = new EncodingInfo("UTF-16LE", "UTF-16", Boolean.FALSE, false);
        public static final EncodingInfo UTF_16_LITTLE_ENDIAN_WITH_BOM = new EncodingInfo("UTF-16LE", "UTF-16", Boolean.FALSE, true);
        public static final EncodingInfo UCS_4_BIG_ENDIAN = new EncodingInfo("ISO-10646-UCS-4", Boolean.TRUE, false);
        public static final EncodingInfo UCS_4_LITTLE_ENDIAN = new EncodingInfo("ISO-10646-UCS-4", Boolean.FALSE, false);
        public static final EncodingInfo UCS_4_UNUSUAL_BYTE_ORDER = new EncodingInfo("ISO-10646-UCS-4", null, false);
        public static final EncodingInfo EBCDIC = new EncodingInfo("CP037", null, false);
        public final String autoDetectedEncoding;
        public final String readerEncoding;
        public final Boolean isBigEndian;
        public final boolean hasBOM;

        private EncodingInfo(String string, Boolean bl, boolean bl2) {
            this(string, string, bl, bl2);
        }

        private EncodingInfo(String string, String string2, Boolean bl, boolean bl2) {
            this.autoDetectedEncoding = string;
            this.readerEncoding = string2;
            this.isBigEndian = bl;
            this.hasBOM = bl2;
        }
    }

    public class ScannedEntity
    extends Entity {
        public InputStream stream;
        public Reader reader;
        public XMLResourceIdentifier entityLocation;
        public int lineNumber;
        public int columnNumber;
        public String encoding;
        boolean externallySpecifiedEncoding;
        public String xmlVersion;
        public boolean literal;
        public boolean isExternal;
        public char[] ch;
        public int position;
        public int baseCharOffset;
        public int startPosition;
        public int count;
        public boolean mayReadChunks;
        private CharacterBuffer fCharacterBuffer;
        private byte[] fByteBuffer;

        public ScannedEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, InputStream inputStream, Reader reader, byte[] byArray, String string2, boolean bl, boolean bl2, boolean bl3) {
            super(string, XMLEntityManager.this.fInExternalSubset);
            this.lineNumber = 1;
            this.columnNumber = 1;
            this.externallySpecifiedEncoding = false;
            this.xmlVersion = "1.0";
            this.ch = null;
            this.entityLocation = xMLResourceIdentifier;
            this.stream = inputStream;
            this.reader = reader;
            this.encoding = string2;
            this.literal = bl;
            this.mayReadChunks = bl2;
            this.isExternal = bl3;
            this.fCharacterBuffer = XMLEntityManager.this.fCharacterBufferPool.getBuffer(bl3);
            this.ch = this.fCharacterBuffer.ch;
            this.fByteBuffer = byArray;
        }

        @Override
        public final boolean isExternal() {
            return this.isExternal;
        }

        @Override
        public final boolean isUnparsed() {
            return false;
        }

        public void setReader(InputStream inputStream, String string, Boolean bl) throws IOException {
            XMLEntityManager.access$402(XMLEntityManager.this, this.fByteBuffer);
            this.reader = XMLEntityManager.this.createReader(inputStream, string, bl);
            this.fByteBuffer = XMLEntityManager.this.fTempByteBuffer;
        }

        public String getExpandedSystemId() {
            int n = XMLEntityManager.this.fEntityStack.size();
            for (int i = n - 1; i >= 0; --i) {
                ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (scannedEntity.entityLocation == null || scannedEntity.entityLocation.getExpandedSystemId() == null) continue;
                return scannedEntity.entityLocation.getExpandedSystemId();
            }
            return null;
        }

        public String getLiteralSystemId() {
            int n = XMLEntityManager.this.fEntityStack.size();
            for (int i = n - 1; i >= 0; --i) {
                ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (scannedEntity.entityLocation == null || scannedEntity.entityLocation.getLiteralSystemId() == null) continue;
                return scannedEntity.entityLocation.getLiteralSystemId();
            }
            return null;
        }

        public int getLineNumber() {
            int n = XMLEntityManager.this.fEntityStack.size();
            for (int i = n - 1; i >= 0; --i) {
                ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (!scannedEntity.isExternal()) continue;
                return scannedEntity.lineNumber;
            }
            return -1;
        }

        public int getColumnNumber() {
            int n = XMLEntityManager.this.fEntityStack.size();
            for (int i = n - 1; i >= 0; --i) {
                ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (!scannedEntity.isExternal()) continue;
                return scannedEntity.columnNumber;
            }
            return -1;
        }

        public int getCharacterOffset() {
            int n = XMLEntityManager.this.fEntityStack.size();
            for (int i = n - 1; i >= 0; --i) {
                ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (!scannedEntity.isExternal()) continue;
                return scannedEntity.baseCharOffset + (scannedEntity.position - scannedEntity.startPosition);
            }
            return -1;
        }

        public String getEncoding() {
            int n = XMLEntityManager.this.fEntityStack.size();
            for (int i = n - 1; i >= 0; --i) {
                ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (!scannedEntity.isExternal()) continue;
                return scannedEntity.encoding;
            }
            return null;
        }

        public String getXMLVersion() {
            int n = XMLEntityManager.this.fEntityStack.size();
            for (int i = n - 1; i >= 0; --i) {
                ScannedEntity scannedEntity = (ScannedEntity)XMLEntityManager.this.fEntityStack.elementAt(i);
                if (!scannedEntity.isExternal()) continue;
                return scannedEntity.xmlVersion;
            }
            return null;
        }

        public boolean isEncodingExternallySpecified() {
            return this.externallySpecifiedEncoding;
        }

        public void setEncodingExternallySpecified(boolean bl) {
            this.externallySpecifiedEncoding = bl;
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("name=\"").append(this.name).append('\"');
            stringBuffer.append(",ch=");
            stringBuffer.append(this.ch);
            stringBuffer.append(",position=").append(this.position);
            stringBuffer.append(",count=").append(this.count);
            stringBuffer.append(",baseCharOffset=").append(this.baseCharOffset);
            stringBuffer.append(",startPosition=").append(this.startPosition);
            return stringBuffer.toString();
        }
    }

    protected static class ExternalEntity
    extends Entity {
        public XMLResourceIdentifier entityLocation;
        public String notation;

        public ExternalEntity() {
            this.clear();
        }

        public ExternalEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, boolean bl) {
            super(string, bl);
            this.entityLocation = xMLResourceIdentifier;
            this.notation = string2;
        }

        @Override
        public final boolean isExternal() {
            return true;
        }

        @Override
        public final boolean isUnparsed() {
            return this.notation != null;
        }

        @Override
        public void clear() {
            super.clear();
            this.entityLocation = null;
            this.notation = null;
        }

        @Override
        public void setValues(Entity entity) {
            super.setValues(entity);
            this.entityLocation = null;
            this.notation = null;
        }

        public void setValues(ExternalEntity externalEntity) {
            super.setValues(externalEntity);
            this.entityLocation = externalEntity.entityLocation;
            this.notation = externalEntity.notation;
        }
    }

    protected static class InternalEntity
    extends Entity {
        public String text;
        public int paramEntityRefs;

        public InternalEntity() {
            this.clear();
        }

        public InternalEntity(String string, String string2, boolean bl) {
            super(string, bl);
            this.text = string2;
        }

        public InternalEntity(String string, String string2, boolean bl, int n) {
            this(string, string2, bl);
            this.paramEntityRefs = n;
        }

        @Override
        public final boolean isExternal() {
            return false;
        }

        @Override
        public final boolean isUnparsed() {
            return false;
        }

        @Override
        public void clear() {
            super.clear();
            this.text = null;
        }

        @Override
        public void setValues(Entity entity) {
            super.setValues(entity);
            this.text = null;
        }

        public void setValues(InternalEntity internalEntity) {
            super.setValues(internalEntity);
            this.text = internalEntity.text;
        }
    }

    public static abstract class Entity {
        public String name;
        public boolean inExternalSubset;

        public Entity() {
            this.clear();
        }

        public Entity(String string, boolean bl) {
            this.name = string;
            this.inExternalSubset = bl;
        }

        public boolean isEntityDeclInExternalSubset() {
            return this.inExternalSubset;
        }

        public abstract boolean isExternal();

        public abstract boolean isUnparsed();

        public void clear() {
            this.name = null;
            this.inExternalSubset = false;
        }

        public void setValues(Entity entity) {
            this.name = entity.name;
            this.inExternalSubset = entity.inExternalSubset;
        }
    }
}

