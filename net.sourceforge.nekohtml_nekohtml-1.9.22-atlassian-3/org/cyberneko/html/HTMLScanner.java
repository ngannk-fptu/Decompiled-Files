/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.util.EncodingMap
 *  org.apache.xerces.util.NamespaceSupport
 *  org.apache.xerces.util.URI
 *  org.apache.xerces.util.URI$MalformedURIException
 *  org.apache.xerces.util.XMLAttributesImpl
 *  org.apache.xerces.util.XMLResourceIdentifierImpl
 *  org.apache.xerces.util.XMLStringBuffer
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.NamespaceContext
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XMLDocumentHandler
 *  org.apache.xerces.xni.XMLLocator
 *  org.apache.xerces.xni.XMLResourceIdentifier
 *  org.apache.xerces.xni.XMLString
 *  org.apache.xerces.xni.XNIException
 *  org.apache.xerces.xni.parser.XMLComponentManager
 *  org.apache.xerces.xni.parser.XMLConfigurationException
 *  org.apache.xerces.xni.parser.XMLDocumentScanner
 *  org.apache.xerces.xni.parser.XMLInputSource
 *  org.cyberneko.html.xercesbridge.XercesBridge
 */
package org.cyberneko.html;

import java.io.EOFException;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.BitSet;
import java.util.Locale;
import java.util.Stack;
import org.apache.xerces.util.EncodingMap;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentScanner;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.HTMLAugmentations;
import org.cyberneko.html.HTMLComponent;
import org.cyberneko.html.HTMLElements;
import org.cyberneko.html.HTMLEntities;
import org.cyberneko.html.HTMLErrorReporter;
import org.cyberneko.html.HTMLEventInfo;
import org.cyberneko.html.xercesbridge.XercesBridge;

public class HTMLScanner
implements XMLDocumentScanner,
XMLLocator,
HTMLComponent {
    public static final String HTML_4_01_STRICT_PUBID = "-//W3C//DTD HTML 4.01//EN";
    public static final String HTML_4_01_STRICT_SYSID = "http://www.w3.org/TR/html4/strict.dtd";
    public static final String HTML_4_01_TRANSITIONAL_PUBID = "-//W3C//DTD HTML 4.01 Transitional//EN";
    public static final String HTML_4_01_TRANSITIONAL_SYSID = "http://www.w3.org/TR/html4/loose.dtd";
    public static final String HTML_4_01_FRAMESET_PUBID = "-//W3C//DTD HTML 4.01 Frameset//EN";
    public static final String HTML_4_01_FRAMESET_SYSID = "http://www.w3.org/TR/html4/frameset.dtd";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String REPORT_ERRORS = "http://cyberneko.org/html/features/report-errors";
    public static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    public static final String NOTIFY_XML_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    public static final String NOTIFY_HTML_BUILTIN_REFS = "http://cyberneko.org/html/features/scanner/notify-builtin-refs";
    public static final String FIX_MSWINDOWS_REFS = "http://cyberneko.org/html/features/scanner/fix-mswindows-refs";
    public static final String SCRIPT_STRIP_COMMENT_DELIMS = "http://cyberneko.org/html/features/scanner/script/strip-comment-delims";
    public static final String SCRIPT_STRIP_CDATA_DELIMS = "http://cyberneko.org/html/features/scanner/script/strip-cdata-delims";
    public static final String STYLE_STRIP_COMMENT_DELIMS = "http://cyberneko.org/html/features/scanner/style/strip-comment-delims";
    public static final String STYLE_STRIP_CDATA_DELIMS = "http://cyberneko.org/html/features/scanner/style/strip-cdata-delims";
    public static final String IGNORE_SPECIFIED_CHARSET = "http://cyberneko.org/html/features/scanner/ignore-specified-charset";
    public static final String CDATA_SECTIONS = "http://cyberneko.org/html/features/scanner/cdata-sections";
    public static final String OVERRIDE_DOCTYPE = "http://cyberneko.org/html/features/override-doctype";
    public static final String INSERT_DOCTYPE = "http://cyberneko.org/html/features/insert-doctype";
    public static final String PARSE_NOSCRIPT_CONTENT = "http://cyberneko.org/html/features/parse-noscript-content";
    public static final String ALLOW_SELFCLOSING_IFRAME = "http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe";
    public static final String ALLOW_SELFCLOSING_TAGS = "http://cyberneko.org/html/features/scanner/allow-selfclosing-tags";
    protected static final String NORMALIZE_ATTRIBUTES = "http://cyberneko.org/html/features/scanner/normalize-attrs";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://cyberneko.org/html/features/augmentations", "http://cyberneko.org/html/features/report-errors", "http://apache.org/xml/features/scanner/notify-char-refs", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://cyberneko.org/html/features/scanner/notify-builtin-refs", "http://cyberneko.org/html/features/scanner/fix-mswindows-refs", "http://cyberneko.org/html/features/scanner/script/strip-cdata-delims", "http://cyberneko.org/html/features/scanner/script/strip-comment-delims", "http://cyberneko.org/html/features/scanner/style/strip-cdata-delims", "http://cyberneko.org/html/features/scanner/style/strip-comment-delims", "http://cyberneko.org/html/features/scanner/ignore-specified-charset", "http://cyberneko.org/html/features/scanner/cdata-sections", "http://cyberneko.org/html/features/override-doctype", "http://cyberneko.org/html/features/insert-doctype", "http://cyberneko.org/html/features/scanner/normalize-attrs", "http://cyberneko.org/html/features/parse-noscript-content", "http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe", "http://cyberneko.org/html/features/scanner/allow-selfclosing-tags"};
    private static final Boolean[] RECOGNIZED_FEATURES_DEFAULTS = new Boolean[]{null, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE};
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String DEFAULT_ENCODING = "http://cyberneko.org/html/properties/default-encoding";
    protected static final String ERROR_REPORTER = "http://cyberneko.org/html/properties/error-reporter";
    protected static final String DOCTYPE_PUBID = "http://cyberneko.org/html/properties/doctype/pubid";
    protected static final String DOCTYPE_SYSID = "http://cyberneko.org/html/properties/doctype/sysid";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://cyberneko.org/html/properties/names/elems", "http://cyberneko.org/html/properties/names/attrs", "http://cyberneko.org/html/properties/default-encoding", "http://cyberneko.org/html/properties/error-reporter", "http://cyberneko.org/html/properties/doctype/pubid", "http://cyberneko.org/html/properties/doctype/sysid"};
    private static final Object[] RECOGNIZED_PROPERTIES_DEFAULTS = new Object[]{null, null, "Windows-1252", null, "-//W3C//DTD HTML 4.01 Transitional//EN", "http://www.w3.org/TR/html4/loose.dtd"};
    protected static final short STATE_CONTENT = 0;
    protected static final short STATE_MARKUP_BRACKET = 1;
    protected static final short STATE_START_DOCUMENT = 10;
    protected static final short STATE_END_DOCUMENT = 11;
    protected static final short NAMES_NO_CHANGE = 0;
    protected static final short NAMES_UPPERCASE = 1;
    protected static final short NAMES_LOWERCASE = 2;
    protected static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final boolean DEBUG_SCANNER = false;
    private static final boolean DEBUG_SCANNER_STATE = false;
    private static final boolean DEBUG_BUFFER = false;
    private static final boolean DEBUG_CHARSET = false;
    protected static final boolean DEBUG_CALLBACKS = false;
    protected static final HTMLEventInfo SYNTHESIZED_ITEM = new HTMLEventInfo.SynthesizedItem();
    private static final BitSet ENTITY_CHARS = new BitSet();
    protected boolean fAugmentations;
    protected boolean fReportErrors;
    protected boolean fNotifyCharRefs;
    protected boolean fNotifyXmlBuiltinRefs;
    protected boolean fNotifyHtmlBuiltinRefs;
    protected boolean fFixWindowsCharRefs;
    protected boolean fScriptStripCDATADelims;
    protected boolean fScriptStripCommentDelims;
    protected boolean fStyleStripCDATADelims;
    protected boolean fStyleStripCommentDelims;
    protected boolean fIgnoreSpecifiedCharset;
    protected boolean fCDATASections;
    protected boolean fOverrideDoctype;
    protected boolean fInsertDoctype;
    protected boolean fNormalizeAttributes;
    protected boolean fParseNoScriptContent;
    protected boolean fParseNoFramesContent;
    protected boolean fAllowSelfclosingIframe;
    protected boolean fAllowSelfclosingTags;
    protected short fNamesElems;
    protected short fNamesAttrs;
    protected String fDefaultIANAEncoding;
    protected HTMLErrorReporter fErrorReporter;
    protected String fDoctypePubid;
    protected String fDoctypeSysid;
    protected int fBeginLineNumber;
    protected int fBeginColumnNumber;
    protected int fBeginCharacterOffset;
    protected int fEndLineNumber;
    protected int fEndColumnNumber;
    protected int fEndCharacterOffset;
    protected PlaybackInputStream fByteStream;
    protected CurrentEntity fCurrentEntity;
    protected final Stack fCurrentEntityStack = new Stack();
    protected Scanner fScanner;
    protected short fScannerState;
    protected XMLDocumentHandler fDocumentHandler;
    protected String fIANAEncoding;
    protected String fJavaEncoding;
    protected boolean fIso8859Encoding;
    protected int fElementCount;
    protected int fElementDepth;
    protected Scanner fContentScanner = new ContentScanner();
    protected SpecialScanner fSpecialScanner = new SpecialScanner();
    protected final XMLStringBuffer fStringBuffer = new XMLStringBuffer(1024);
    private final XMLStringBuffer fStringBuffer2 = new XMLStringBuffer(1024);
    private final XMLStringBuffer fNonNormAttr = new XMLStringBuffer(128);
    private final HTMLAugmentations fInfosetAugs = new HTMLAugmentations();
    private final LocationItem fLocationItem = new LocationItem();
    private final boolean[] fSingleBoolean = new boolean[]{false};
    private final XMLResourceIdentifierImpl fResourceId = new XMLResourceIdentifierImpl();
    private final char REPLACEMENT_CHARACTER = (char)65533;

    public void pushInputSource(XMLInputSource inputSource) {
        Reader reader = this.getReader(inputSource);
        this.fCurrentEntityStack.push(this.fCurrentEntity);
        String encoding = inputSource.getEncoding();
        String publicId = inputSource.getPublicId();
        String baseSystemId = inputSource.getBaseSystemId();
        String literalSystemId = inputSource.getSystemId();
        String expandedSystemId = HTMLScanner.expandSystemId(literalSystemId, baseSystemId);
        this.fCurrentEntity = new CurrentEntity(reader, encoding, publicId, baseSystemId, literalSystemId, expandedSystemId);
    }

    private Reader getReader(XMLInputSource inputSource) {
        Reader reader = inputSource.getCharacterStream();
        if (reader == null) {
            try {
                return new InputStreamReader(inputSource.getByteStream(), this.fJavaEncoding);
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
        }
        return reader;
    }

    public void evaluateInputSource(XMLInputSource inputSource) {
        Scanner previousScanner = this.fScanner;
        short previousScannerState = this.fScannerState;
        CurrentEntity previousEntity = this.fCurrentEntity;
        Reader reader = this.getReader(inputSource);
        String encoding = inputSource.getEncoding();
        String publicId = inputSource.getPublicId();
        String baseSystemId = inputSource.getBaseSystemId();
        String literalSystemId = inputSource.getSystemId();
        String expandedSystemId = HTMLScanner.expandSystemId(literalSystemId, baseSystemId);
        this.fCurrentEntity = new CurrentEntity(reader, encoding, publicId, baseSystemId, literalSystemId, expandedSystemId);
        this.setScanner(this.fContentScanner);
        this.setScannerState((short)0);
        try {
            do {
                this.fScanner.scan(false);
            } while (this.fScannerState != 11);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.setScanner(previousScanner);
        this.setScannerState(previousScannerState);
        this.fCurrentEntity = previousEntity;
    }

    public void cleanup(boolean closeall) {
        int size = this.fCurrentEntityStack.size();
        if (size > 0) {
            int i;
            if (this.fCurrentEntity != null) {
                this.fCurrentEntity.closeQuietly();
            }
            int n = i = closeall ? 0 : 1;
            while (i < size) {
                this.fCurrentEntity = (CurrentEntity)this.fCurrentEntityStack.pop();
                this.fCurrentEntity.closeQuietly();
                ++i;
            }
        } else if (closeall && this.fCurrentEntity != null) {
            this.fCurrentEntity.closeQuietly();
        }
    }

    public String getEncoding() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.encoding : null;
    }

    public String getPublicId() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.publicId : null;
    }

    public String getBaseSystemId() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.baseSystemId : null;
    }

    public String getLiteralSystemId() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.literalSystemId : null;
    }

    public String getExpandedSystemId() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.expandedSystemId : null;
    }

    public int getLineNumber() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.getLineNumber() : -1;
    }

    public int getColumnNumber() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.getColumnNumber() : -1;
    }

    public String getXMLVersion() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.version : null;
    }

    public int getCharacterOffset() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.getCharacterOffset() : -1;
    }

    @Override
    public Boolean getFeatureDefault(String featureId) {
        int length = RECOGNIZED_FEATURES != null ? RECOGNIZED_FEATURES.length : 0;
        for (int i = 0; i < length; ++i) {
            if (!RECOGNIZED_FEATURES[i].equals(featureId)) continue;
            return RECOGNIZED_FEATURES_DEFAULTS[i];
        }
        return null;
    }

    @Override
    public Object getPropertyDefault(String propertyId) {
        int length = RECOGNIZED_PROPERTIES != null ? RECOGNIZED_PROPERTIES.length : 0;
        for (int i = 0; i < length; ++i) {
            if (!RECOGNIZED_PROPERTIES[i].equals(propertyId)) continue;
            return RECOGNIZED_PROPERTIES_DEFAULTS[i];
        }
        return null;
    }

    public String[] getRecognizedFeatures() {
        return RECOGNIZED_FEATURES;
    }

    public String[] getRecognizedProperties() {
        return RECOGNIZED_PROPERTIES;
    }

    public void reset(XMLComponentManager manager) throws XMLConfigurationException {
        this.fAugmentations = manager.getFeature(AUGMENTATIONS);
        this.fReportErrors = manager.getFeature(REPORT_ERRORS);
        this.fNotifyCharRefs = manager.getFeature(NOTIFY_CHAR_REFS);
        this.fNotifyXmlBuiltinRefs = manager.getFeature(NOTIFY_XML_BUILTIN_REFS);
        this.fNotifyHtmlBuiltinRefs = manager.getFeature(NOTIFY_HTML_BUILTIN_REFS);
        this.fFixWindowsCharRefs = manager.getFeature(FIX_MSWINDOWS_REFS);
        this.fScriptStripCDATADelims = manager.getFeature(SCRIPT_STRIP_CDATA_DELIMS);
        this.fScriptStripCommentDelims = manager.getFeature(SCRIPT_STRIP_COMMENT_DELIMS);
        this.fStyleStripCDATADelims = manager.getFeature(STYLE_STRIP_CDATA_DELIMS);
        this.fStyleStripCommentDelims = manager.getFeature(STYLE_STRIP_COMMENT_DELIMS);
        this.fIgnoreSpecifiedCharset = manager.getFeature(IGNORE_SPECIFIED_CHARSET);
        this.fCDATASections = manager.getFeature(CDATA_SECTIONS);
        this.fOverrideDoctype = manager.getFeature(OVERRIDE_DOCTYPE);
        this.fInsertDoctype = manager.getFeature(INSERT_DOCTYPE);
        this.fNormalizeAttributes = manager.getFeature(NORMALIZE_ATTRIBUTES);
        this.fParseNoScriptContent = manager.getFeature(PARSE_NOSCRIPT_CONTENT);
        this.fAllowSelfclosingIframe = manager.getFeature(ALLOW_SELFCLOSING_IFRAME);
        this.fAllowSelfclosingTags = manager.getFeature(ALLOW_SELFCLOSING_TAGS);
        this.fNamesElems = HTMLScanner.getNamesValue(String.valueOf(manager.getProperty(NAMES_ELEMS)));
        this.fNamesAttrs = HTMLScanner.getNamesValue(String.valueOf(manager.getProperty(NAMES_ATTRS)));
        this.fDefaultIANAEncoding = String.valueOf(manager.getProperty(DEFAULT_ENCODING));
        this.fErrorReporter = (HTMLErrorReporter)manager.getProperty(ERROR_REPORTER);
        this.fDoctypePubid = String.valueOf(manager.getProperty(DOCTYPE_PUBID));
        this.fDoctypeSysid = String.valueOf(manager.getProperty(DOCTYPE_SYSID));
    }

    public void setFeature(String featureId, boolean state) {
        if (featureId.equals(AUGMENTATIONS)) {
            this.fAugmentations = state;
        } else if (featureId.equals(IGNORE_SPECIFIED_CHARSET)) {
            this.fIgnoreSpecifiedCharset = state;
        } else if (featureId.equals(NOTIFY_CHAR_REFS)) {
            this.fNotifyCharRefs = state;
        } else if (featureId.equals(NOTIFY_XML_BUILTIN_REFS)) {
            this.fNotifyXmlBuiltinRefs = state;
        } else if (featureId.equals(NOTIFY_HTML_BUILTIN_REFS)) {
            this.fNotifyHtmlBuiltinRefs = state;
        } else if (featureId.equals(FIX_MSWINDOWS_REFS)) {
            this.fFixWindowsCharRefs = state;
        } else if (featureId.equals(SCRIPT_STRIP_CDATA_DELIMS)) {
            this.fScriptStripCDATADelims = state;
        } else if (featureId.equals(SCRIPT_STRIP_COMMENT_DELIMS)) {
            this.fScriptStripCommentDelims = state;
        } else if (featureId.equals(STYLE_STRIP_CDATA_DELIMS)) {
            this.fStyleStripCDATADelims = state;
        } else if (featureId.equals(STYLE_STRIP_COMMENT_DELIMS)) {
            this.fStyleStripCommentDelims = state;
        } else if (featureId.equals(PARSE_NOSCRIPT_CONTENT)) {
            this.fParseNoScriptContent = state;
        } else if (featureId.equals(ALLOW_SELFCLOSING_IFRAME)) {
            this.fAllowSelfclosingIframe = state;
        } else if (featureId.equals(ALLOW_SELFCLOSING_TAGS)) {
            this.fAllowSelfclosingTags = state;
        }
    }

    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
        if (propertyId.equals(NAMES_ELEMS)) {
            this.fNamesElems = HTMLScanner.getNamesValue(String.valueOf(value));
            return;
        }
        if (propertyId.equals(NAMES_ATTRS)) {
            this.fNamesAttrs = HTMLScanner.getNamesValue(String.valueOf(value));
            return;
        }
        if (propertyId.equals(DEFAULT_ENCODING)) {
            this.fDefaultIANAEncoding = String.valueOf(value);
            return;
        }
    }

    public void setInputSource(XMLInputSource source) throws IOException {
        this.fElementCount = 0;
        this.fElementDepth = -1;
        this.fByteStream = null;
        this.fCurrentEntityStack.removeAllElements();
        this.fBeginLineNumber = 1;
        this.fBeginColumnNumber = 1;
        this.fBeginCharacterOffset = 0;
        this.fEndLineNumber = this.fBeginLineNumber;
        this.fEndColumnNumber = this.fBeginColumnNumber;
        this.fEndCharacterOffset = this.fBeginCharacterOffset;
        this.fJavaEncoding = this.fIANAEncoding = this.fDefaultIANAEncoding;
        String encoding = source.getEncoding();
        String publicId = source.getPublicId();
        String baseSystemId = source.getBaseSystemId();
        String literalSystemId = source.getSystemId();
        String expandedSystemId = HTMLScanner.expandSystemId(literalSystemId, baseSystemId);
        Reader reader = source.getCharacterStream();
        if (reader == null) {
            InputStream inputStream = source.getByteStream();
            if (inputStream == null) {
                URL url = new URL(expandedSystemId);
                inputStream = url.openStream();
            }
            this.fByteStream = new PlaybackInputStream(inputStream);
            String[] encodings = new String[2];
            if (encoding == null) {
                this.fByteStream.detectEncoding(encodings);
            } else {
                encodings[0] = encoding;
            }
            if (encodings[0] == null) {
                encodings[0] = this.fDefaultIANAEncoding;
                if (this.fReportErrors) {
                    this.fErrorReporter.reportWarning("HTML1000", null);
                }
            }
            if (encodings[1] == null) {
                encodings[1] = EncodingMap.getIANA2JavaMapping((String)encodings[0].toUpperCase(Locale.ENGLISH));
                if (encodings[1] == null) {
                    encodings[1] = encodings[0];
                    if (this.fReportErrors) {
                        this.fErrorReporter.reportWarning("HTML1001", new Object[]{encodings[0]});
                    }
                }
            }
            this.fIANAEncoding = encodings[0];
            this.fJavaEncoding = encodings[1];
            this.fIso8859Encoding = this.fIANAEncoding == null || this.fIANAEncoding.toUpperCase(Locale.ENGLISH).startsWith("ISO-8859") || this.fIANAEncoding.equalsIgnoreCase(this.fDefaultIANAEncoding);
            encoding = this.fIANAEncoding;
            reader = new InputStreamReader((InputStream)this.fByteStream, this.fJavaEncoding);
        }
        this.fCurrentEntity = new CurrentEntity(reader, encoding, publicId, baseSystemId, literalSystemId, expandedSystemId);
        this.setScanner(this.fContentScanner);
        this.setScannerState((short)10);
    }

    public boolean scanDocument(boolean complete) throws XNIException, IOException {
        do {
            if (this.fScanner.scan(complete)) continue;
            return false;
        } while (complete);
        return true;
    }

    public void setDocumentHandler(XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
    }

    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    protected static String getValue(XMLAttributes attrs, String aname) {
        int length = attrs != null ? attrs.getLength() : 0;
        for (int i = 0; i < length; ++i) {
            if (!attrs.getQName(i).equalsIgnoreCase(aname)) continue;
            return attrs.getValue(i);
        }
        return null;
    }

    public static String expandSystemId(String systemId, String baseSystemId) {
        if (systemId == null || systemId.length() == 0) {
            return systemId;
        }
        try {
            URI uri = new URI(systemId);
            if (uri != null) {
                return systemId;
            }
        }
        catch (URI.MalformedURIException uri) {
            // empty catch block
        }
        String id = HTMLScanner.fixURI(systemId);
        URI base = null;
        URI uri = null;
        try {
            if (baseSystemId == null || baseSystemId.length() == 0 || baseSystemId.equals(systemId)) {
                String dir;
                try {
                    dir = HTMLScanner.fixURI(System.getProperty("user.dir"));
                }
                catch (SecurityException se) {
                    dir = "";
                }
                if (!dir.endsWith("/")) {
                    dir = dir + "/";
                }
                base = new URI("file", "", dir, null, null);
            } else {
                try {
                    base = new URI(HTMLScanner.fixURI(baseSystemId));
                }
                catch (URI.MalformedURIException e) {
                    String dir;
                    try {
                        dir = HTMLScanner.fixURI(System.getProperty("user.dir"));
                    }
                    catch (SecurityException se) {
                        dir = "";
                    }
                    if (baseSystemId.indexOf(58) != -1) {
                        base = new URI("file", "", HTMLScanner.fixURI(baseSystemId), null, null);
                    }
                    if (!dir.endsWith("/")) {
                        dir = dir + "/";
                    }
                    dir = dir + HTMLScanner.fixURI(baseSystemId);
                    base = new URI("file", "", dir, null, null);
                }
            }
            uri = new URI(base, id);
        }
        catch (URI.MalformedURIException malformedURIException) {
            // empty catch block
        }
        if (uri == null) {
            return systemId;
        }
        return uri.toString();
    }

    protected static String fixURI(String str) {
        if ((str = str.replace(File.separatorChar, '/')).length() >= 2) {
            char ch1 = str.charAt(1);
            if (ch1 == ':') {
                char ch0 = String.valueOf(str.charAt(0)).toUpperCase(Locale.ENGLISH).charAt(0);
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    str = "/" + str;
                }
            } else if (ch1 == '/' && str.charAt(0) == '/') {
                str = "file:" + str;
            }
        }
        return str;
    }

    protected static final String modifyName(String name, short mode) {
        switch (mode) {
            case 1: {
                return name.toUpperCase(Locale.ENGLISH);
            }
            case 2: {
                return name.toLowerCase(Locale.ENGLISH);
            }
        }
        return name;
    }

    protected static final short getNamesValue(String value) {
        if (value.equals("lower")) {
            return 2;
        }
        if (value.equals("upper")) {
            return 1;
        }
        return 0;
    }

    protected int fixWindowsCharacter(int origChar) {
        switch (origChar) {
            case 130: {
                return 8218;
            }
            case 131: {
                return 402;
            }
            case 132: {
                return 8222;
            }
            case 133: {
                return 8230;
            }
            case 134: {
                return 8224;
            }
            case 135: {
                return 8225;
            }
            case 136: {
                return 710;
            }
            case 137: {
                return 8240;
            }
            case 138: {
                return 352;
            }
            case 139: {
                return 8249;
            }
            case 140: {
                return 338;
            }
            case 145: {
                return 8216;
            }
            case 146: {
                return 8217;
            }
            case 147: {
                return 8220;
            }
            case 148: {
                return 8221;
            }
            case 149: {
                return 8226;
            }
            case 150: {
                return 8211;
            }
            case 151: {
                return 8212;
            }
            case 152: {
                return 732;
            }
            case 153: {
                return 8482;
            }
            case 154: {
                return 353;
            }
            case 155: {
                return 8250;
            }
            case 156: {
                return 339;
            }
            case 159: {
                return 376;
            }
        }
        return origChar;
    }

    protected int read() throws IOException {
        return this.fCurrentEntity.read();
    }

    protected void setScanner(Scanner scanner) {
        this.fScanner = scanner;
    }

    protected void setScannerState(short state) {
        this.fScannerState = state;
    }

    protected void scanDoctype() throws IOException {
        int c;
        String root = null;
        String pubid = null;
        String sysid = null;
        if (this.skipSpaces()) {
            root = this.scanName(true);
            if (root == null) {
                if (this.fReportErrors) {
                    this.fErrorReporter.reportError("HTML1014", null);
                }
            } else {
                root = HTMLScanner.modifyName(root, this.fNamesElems);
            }
            if (this.skipSpaces()) {
                if (this.skip("PUBLIC", false)) {
                    this.skipSpaces();
                    pubid = this.scanLiteral();
                    if (this.skipSpaces()) {
                        sysid = this.scanLiteral();
                    }
                } else if (this.skip("SYSTEM", false)) {
                    this.skipSpaces();
                    sysid = this.scanLiteral();
                }
            }
        }
        while ((c = this.fCurrentEntity.read()) != -1) {
            if (c == 60) {
                this.fCurrentEntity.rewind();
                break;
            }
            if (c == 62) break;
            if (c != 91) continue;
            this.skipMarkup(true);
            break;
        }
        if (this.fDocumentHandler != null) {
            if (this.fOverrideDoctype) {
                pubid = this.fDoctypePubid;
                sysid = this.fDoctypeSysid;
            }
            this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
            this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
            this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
            this.fDocumentHandler.doctypeDecl(root, pubid, sysid, this.locationAugs());
        }
    }

    protected String scanLiteral() throws IOException {
        int quote = this.fCurrentEntity.read();
        if (quote == 39 || quote == 34) {
            int c;
            StringBuilder str = new StringBuilder();
            while ((c = this.fCurrentEntity.read()) != -1 && c != quote) {
                if (c == 13 || c == 10) {
                    this.fCurrentEntity.rewind();
                    this.skipNewlines();
                    str.append(' ');
                    continue;
                }
                if (c == 60) {
                    this.fCurrentEntity.rewind();
                    break;
                }
                this.appendChar(str, c, null);
            }
            if (c == -1) {
                if (this.fReportErrors) {
                    this.fErrorReporter.reportError("HTML1007", null);
                }
                throw new EOFException();
            }
            return str.toString();
        }
        this.fCurrentEntity.rewind();
        return null;
    }

    protected String scanName(boolean strict) throws IOException {
        int length;
        if (this.fCurrentEntity.offset == this.fCurrentEntity.length && this.fCurrentEntity.load(0) == -1) {
            return null;
        }
        int offset = this.fCurrentEntity.offset;
        while (true) {
            if (this.fCurrentEntity.hasNext()) {
                char c = this.fCurrentEntity.getNextChar();
                if ((!strict || Character.isLetterOrDigit(c) || c == '-' || c == '.' || c == ':' || c == '_') && (strict || !Character.isWhitespace(c) && c != '=' && c != '/' && c != '>')) continue;
                this.fCurrentEntity.rewind();
            }
            if (this.fCurrentEntity.offset != this.fCurrentEntity.length) break;
            length = this.fCurrentEntity.length - offset;
            System.arraycopy(this.fCurrentEntity.buffer, offset, this.fCurrentEntity.buffer, 0, length);
            int count = this.fCurrentEntity.load(length);
            offset = 0;
            if (count == -1) break;
        }
        String name = (length = this.fCurrentEntity.offset - offset) > 0 ? new String(this.fCurrentEntity.buffer, offset, length) : null;
        return name;
    }

    protected int scanEntityRef(XMLStringBuffer str, boolean content) throws IOException {
        boolean invalidEntityInAttribute;
        str.clear();
        str.append('&');
        boolean endsWithSemicolon = false;
        while (true) {
            int c;
            if ((c = this.fCurrentEntity.read()) == 59) {
                str.append(';');
                endsWithSemicolon = true;
                break;
            }
            if (c == -1) break;
            if (!ENTITY_CHARS.get(c) && c != 35) {
                this.fCurrentEntity.rewind();
                break;
            }
            this.appendChar(str, c, null);
        }
        if (!endsWithSemicolon && this.fReportErrors) {
            this.fErrorReporter.reportWarning("HTML1004", null);
        }
        if (str.length == 1) {
            if (content && this.fDocumentHandler != null && this.fElementCount >= this.fElementDepth) {
                this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
                this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
                this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
                this.fDocumentHandler.characters((XMLString)str, this.locationAugs());
            }
            return -1;
        }
        String name = endsWithSemicolon ? str.toString().substring(1, str.length - 1) : str.toString().substring(1);
        if (name.startsWith("#")) {
            int value;
            block20: {
                value = -1;
                try {
                    value = name.startsWith("#x") || name.startsWith("#X") ? Integer.parseInt(name.substring(2), 16) : Integer.parseInt(name.substring(1));
                    if (this.fFixWindowsCharRefs && this.fIso8859Encoding) {
                        value = this.fixWindowsCharacter(value);
                    }
                    if (content && this.fDocumentHandler != null && this.fElementCount >= this.fElementDepth) {
                        this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
                        this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
                        this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
                        if (this.fNotifyCharRefs) {
                            XMLResourceIdentifier id = this.resourceId();
                            String encoding = null;
                            this.fDocumentHandler.startGeneralEntity(name, id, encoding, this.locationAugs());
                        }
                        str.clear();
                        this.appendChar(str, value, name);
                        this.fDocumentHandler.characters((XMLString)str, this.locationAugs());
                        if (this.fNotifyCharRefs) {
                            this.fDocumentHandler.endGeneralEntity(name, this.locationAugs());
                        }
                    }
                }
                catch (NumberFormatException e) {
                    if (this.fReportErrors) {
                        this.fErrorReporter.reportError("HTML1005", new Object[]{name});
                    }
                    if (!content || this.fDocumentHandler == null || this.fElementCount < this.fElementDepth) break block20;
                    this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
                    this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
                    this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
                    this.fDocumentHandler.characters((XMLString)str, this.locationAugs());
                }
            }
            return value;
        }
        int c = HTMLEntities.get(name);
        boolean bl = invalidEntityInAttribute = !content && !endsWithSemicolon && c > 256;
        if (c == -1 || invalidEntityInAttribute) {
            if (this.fReportErrors) {
                this.fErrorReporter.reportWarning("HTML1006", new Object[]{name});
            }
            if (content && this.fDocumentHandler != null && this.fElementCount >= this.fElementDepth) {
                this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
                this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
                this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
                this.fDocumentHandler.characters((XMLString)str, this.locationAugs());
            }
            return -1;
        }
        if (content && this.fDocumentHandler != null && this.fElementCount >= this.fElementDepth) {
            boolean notify;
            this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
            this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
            this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
            boolean bl2 = notify = this.fNotifyHtmlBuiltinRefs || this.fNotifyXmlBuiltinRefs && HTMLScanner.builtinXmlRef(name);
            if (notify) {
                XMLResourceIdentifier id = this.resourceId();
                String encoding = null;
                this.fDocumentHandler.startGeneralEntity(name, id, encoding, this.locationAugs());
            }
            str.clear();
            this.appendChar(str, c, null);
            this.fDocumentHandler.characters((XMLString)str, this.locationAugs());
            if (notify) {
                this.fDocumentHandler.endGeneralEntity(name, this.locationAugs());
            }
        }
        return c;
    }

    protected boolean skip(String s, boolean caseSensitive) throws IOException {
        int length = s != null ? s.length() : 0;
        for (int i = 0; i < length; ++i) {
            if (this.fCurrentEntity.offset == this.fCurrentEntity.length) {
                System.arraycopy(this.fCurrentEntity.buffer, this.fCurrentEntity.offset - i, this.fCurrentEntity.buffer, 0, i);
                if (this.fCurrentEntity.load(i) == -1) {
                    this.fCurrentEntity.offset = 0;
                    return false;
                }
            }
            char c0 = s.charAt(i);
            char c1 = this.fCurrentEntity.getNextChar();
            if (!caseSensitive) {
                c0 = String.valueOf(c0).toUpperCase(Locale.ENGLISH).charAt(0);
                c1 = String.valueOf(c1).toUpperCase(Locale.ENGLISH).charAt(0);
            }
            if (c0 == c1) continue;
            this.fCurrentEntity.rewind(i + 1);
            return false;
        }
        return true;
    }

    protected boolean skipMarkup(boolean balance) throws IOException {
        int depth = 1;
        boolean slashgt = false;
        block0: while (this.fCurrentEntity.offset != this.fCurrentEntity.length || this.fCurrentEntity.load(0) != -1) {
            while (this.fCurrentEntity.hasNext()) {
                char c = this.fCurrentEntity.getNextChar();
                if (balance && c == '<') {
                    ++depth;
                    continue;
                }
                if (c == '>') {
                    if (--depth != 0) continue;
                    break block0;
                }
                if (c == '/') {
                    if (this.fCurrentEntity.offset == this.fCurrentEntity.length && this.fCurrentEntity.load(0) == -1) break block0;
                    c = this.fCurrentEntity.getNextChar();
                    if (c == '>') {
                        slashgt = true;
                        if (--depth != 0) continue;
                        break block0;
                    }
                    this.fCurrentEntity.rewind();
                    continue;
                }
                if (c != '\r' && c != '\n') continue;
                this.fCurrentEntity.rewind();
                this.skipNewlines();
            }
        }
        return slashgt;
    }

    protected boolean skipSpaces() throws IOException {
        boolean spaces = false;
        while (this.fCurrentEntity.offset != this.fCurrentEntity.length || this.fCurrentEntity.load(0) != -1) {
            char c = this.fCurrentEntity.getNextChar();
            if (!Character.isWhitespace(c)) {
                this.fCurrentEntity.rewind();
                break;
            }
            spaces = true;
            if (c != '\r' && c != '\n') continue;
            this.fCurrentEntity.rewind();
            this.skipNewlines();
        }
        return spaces;
    }

    protected int skipNewlines() throws IOException {
        if (!this.fCurrentEntity.hasNext() && this.fCurrentEntity.load(0) == -1) {
            return 0;
        }
        char c = this.fCurrentEntity.getCurrentChar();
        int newlines = 0;
        if (c == '\n' || c == '\r') {
            do {
                if ((c = this.fCurrentEntity.getNextChar()) == '\r') {
                    ++newlines;
                    if (this.fCurrentEntity.offset == this.fCurrentEntity.length) {
                        this.fCurrentEntity.offset = newlines;
                        if (this.fCurrentEntity.load(newlines) == -1) break;
                    }
                    if (this.fCurrentEntity.getCurrentChar() != '\n') continue;
                    ++this.fCurrentEntity.offset;
                    ++this.fCurrentEntity.characterOffset_;
                    continue;
                }
                if (c == '\n') {
                    ++newlines;
                    if (this.fCurrentEntity.offset != this.fCurrentEntity.length) continue;
                    this.fCurrentEntity.offset = newlines;
                    if (this.fCurrentEntity.load(newlines) != -1) continue;
                    break;
                }
                this.fCurrentEntity.rewind();
                break;
            } while (this.fCurrentEntity.offset < this.fCurrentEntity.length - 1);
            this.fCurrentEntity.incLine(newlines);
        }
        return newlines;
    }

    protected final Augmentations locationAugs() {
        HTMLAugmentations augs = null;
        if (this.fAugmentations) {
            this.fLocationItem.setValues(this.fBeginLineNumber, this.fBeginColumnNumber, this.fBeginCharacterOffset, this.fEndLineNumber, this.fEndColumnNumber, this.fEndCharacterOffset);
            augs = this.fInfosetAugs;
            augs.removeAllItems();
            augs.putItem(AUGMENTATIONS, this.fLocationItem);
        }
        return augs;
    }

    protected final Augmentations synthesizedAugs() {
        HTMLAugmentations augs = null;
        if (this.fAugmentations) {
            augs = this.fInfosetAugs;
            augs.removeAllItems();
            augs.putItem(AUGMENTATIONS, SYNTHESIZED_ITEM);
        }
        return augs;
    }

    protected final XMLResourceIdentifier resourceId() {
        this.fResourceId.clear();
        return this.fResourceId;
    }

    protected static boolean builtinXmlRef(String name) {
        return name.equals("amp") || name.equals("lt") || name.equals("gt") || name.equals("quot") || name.equals("apos");
    }

    private void appendChar(XMLStringBuffer str, int value, String name) {
        if (value > 65535) {
            try {
                char[] chars = Character.toChars(value);
                str.append(chars, 0, chars.length);
            }
            catch (IllegalArgumentException e) {
                if (this.fReportErrors) {
                    if (name == null) {
                        name = "&#" + value + ';';
                    }
                    this.fErrorReporter.reportError("HTML1005", new Object[]{name});
                }
                str.append('\ufffd');
            }
        } else {
            str.append((char)value);
        }
    }

    private void appendChar(StringBuilder str, int value, String name) {
        if (value > 65535) {
            try {
                char[] chars = Character.toChars(value);
                str.append(chars, 0, chars.length);
            }
            catch (IllegalArgumentException e) {
                if (this.fReportErrors) {
                    if (name == null) {
                        name = "&#" + value + ';';
                    }
                    this.fErrorReporter.reportError("HTML1005", new Object[]{name});
                }
                this.fStringBuffer.append('\ufffd');
            }
        } else {
            str.append((char)value);
        }
    }

    boolean isEncodingCompatible(String encoding1, String encoding2) {
        try {
            try {
                return this.canRoundtrip(encoding1, encoding2);
            }
            catch (UnsupportedOperationException e) {
                try {
                    return this.canRoundtrip(encoding2, encoding1);
                }
                catch (UnsupportedOperationException e1) {
                    return false;
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    private boolean canRoundtrip(String encodeCharset, String decodeCharset) throws UnsupportedEncodingException {
        String reference = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=";
        byte[] bytesEncoding1 = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=".getBytes(encodeCharset);
        String referenceWithEncoding2 = new String(bytesEncoding1, decodeCharset);
        return "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=".equals(referenceWithEncoding2);
    }

    private boolean endsWith(XMLStringBuffer buffer, String string) {
        int l = string.length();
        if (buffer.length < l) {
            return false;
        }
        String s = new String(buffer.ch, buffer.length - l, l);
        return string.equals(s);
    }

    protected int readPreservingBufferContent() throws IOException {
        if (this.fCurrentEntity.offset == this.fCurrentEntity.length && this.fCurrentEntity.load(this.fCurrentEntity.length) < 1) {
            return -1;
        }
        char c = this.fCurrentEntity.getNextChar();
        return c;
    }

    private boolean endCommentAvailable() throws IOException {
        int nbCaret = 0;
        int originalOffset = this.fCurrentEntity.offset;
        int originalColumnNumber = this.fCurrentEntity.getColumnNumber();
        int originalCharacterOffset = this.fCurrentEntity.getCharacterOffset();
        while (true) {
            int c;
            if ((c = this.readPreservingBufferContent()) == -1) {
                this.fCurrentEntity.restorePosition(originalOffset, originalColumnNumber, originalCharacterOffset);
                return false;
            }
            if (c == 62 && nbCaret >= 2) {
                this.fCurrentEntity.restorePosition(originalOffset, originalColumnNumber, originalCharacterOffset);
                return true;
            }
            if (c == 45) {
                ++nbCaret;
                continue;
            }
            nbCaret = 0;
        }
    }

    static void reduceToContent(XMLStringBuffer buffer, String startMarker, String endMarker) {
        char c;
        int i;
        int startContent = -1;
        int l1 = startMarker.length();
        int l2 = endMarker.length();
        for (i = 0; i < buffer.length - l1 - l2; ++i) {
            c = buffer.ch[buffer.offset + i];
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == startMarker.charAt(0) && startMarker.equals(new String(buffer.ch, buffer.offset + i, l1))) {
                startContent = buffer.offset + i + l1;
                break;
            }
            return;
        }
        if (startContent == -1) {
            return;
        }
        for (i = buffer.length - 1; i > startContent + l2; --i) {
            c = buffer.ch[buffer.offset + i];
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == endMarker.charAt(l2 - 1) && endMarker.equals(new String(buffer.ch, buffer.offset + i - l2 + 1, l2))) {
                buffer.length = buffer.offset + i - startContent - 2;
                buffer.offset = startContent;
                return;
            }
            return;
        }
    }

    static {
        String str = "-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < "-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz".length(); ++i) {
            char c = "-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz".charAt(i);
            ENTITY_CHARS.set(c);
        }
    }

    protected static class LocationItem
    implements HTMLEventInfo,
    Cloneable {
        protected int fBeginLineNumber;
        protected int fBeginColumnNumber;
        protected int fBeginCharacterOffset;
        protected int fEndLineNumber;
        protected int fEndColumnNumber;
        protected int fEndCharacterOffset;

        public LocationItem() {
        }

        LocationItem(LocationItem other) {
            this.setValues(other.fBeginLineNumber, other.fBeginColumnNumber, other.fBeginCharacterOffset, other.fEndLineNumber, other.fEndColumnNumber, other.fEndCharacterOffset);
        }

        public void setValues(int beginLine, int beginColumn, int beginOffset, int endLine, int endColumn, int endOffset) {
            this.fBeginLineNumber = beginLine;
            this.fBeginColumnNumber = beginColumn;
            this.fBeginCharacterOffset = beginOffset;
            this.fEndLineNumber = endLine;
            this.fEndColumnNumber = endColumn;
            this.fEndCharacterOffset = endOffset;
        }

        @Override
        public int getBeginLineNumber() {
            return this.fBeginLineNumber;
        }

        @Override
        public int getBeginColumnNumber() {
            return this.fBeginColumnNumber;
        }

        @Override
        public int getBeginCharacterOffset() {
            return this.fBeginCharacterOffset;
        }

        @Override
        public int getEndLineNumber() {
            return this.fEndLineNumber;
        }

        @Override
        public int getEndColumnNumber() {
            return this.fEndColumnNumber;
        }

        @Override
        public int getEndCharacterOffset() {
            return this.fEndCharacterOffset;
        }

        @Override
        public boolean isSynthesized() {
            return false;
        }

        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append(this.fBeginLineNumber);
            str.append(':');
            str.append(this.fBeginColumnNumber);
            str.append(':');
            str.append(this.fBeginCharacterOffset);
            str.append(':');
            str.append(this.fEndLineNumber);
            str.append(':');
            str.append(this.fEndColumnNumber);
            str.append(':');
            str.append(this.fEndCharacterOffset);
            return str.toString();
        }
    }

    public static class PlaybackInputStream
    extends FilterInputStream {
        private static final boolean DEBUG_PLAYBACK = false;
        protected boolean fPlayback = false;
        protected boolean fCleared = false;
        protected boolean fDetected = false;
        protected byte[] fByteBuffer = new byte[1024];
        protected int fByteOffset = 0;
        protected int fByteLength = 0;
        public int fPushbackOffset = 0;
        public int fPushbackLength = 0;

        public PlaybackInputStream(InputStream in) {
            super(in);
        }

        public void detectEncoding(String[] encodings) throws IOException {
            if (this.fDetected) {
                throw new IOException("Should not detect encoding twice.");
            }
            this.fDetected = true;
            int b1 = this.read();
            if (b1 == -1) {
                return;
            }
            int b2 = this.read();
            if (b2 == -1) {
                this.fPushbackLength = 1;
                return;
            }
            if (b1 == 239 && b2 == 187) {
                int b3 = this.read();
                if (b3 == 191) {
                    this.fPushbackOffset = 3;
                    encodings[0] = "UTF-8";
                    encodings[1] = "UTF8";
                    return;
                }
                this.fPushbackLength = 3;
            }
            if (b1 == 255 && b2 == 254) {
                encodings[0] = "UTF-16";
                encodings[1] = "UnicodeLittleUnmarked";
                return;
            }
            if (b1 == 254 && b2 == 255) {
                encodings[0] = "UTF-16";
                encodings[1] = "UnicodeBigUnmarked";
                return;
            }
            this.fPushbackLength = 2;
        }

        public void playback() {
            this.fPlayback = true;
        }

        public void clear() {
            if (!this.fPlayback) {
                this.fCleared = true;
                this.fByteBuffer = null;
            }
        }

        @Override
        public int read() throws IOException {
            if (this.fPushbackOffset < this.fPushbackLength) {
                return this.fByteBuffer[this.fPushbackOffset++];
            }
            if (this.fCleared) {
                return this.in.read();
            }
            if (this.fPlayback) {
                byte c = this.fByteBuffer[this.fByteOffset++];
                if (this.fByteOffset == this.fByteLength) {
                    this.fCleared = true;
                    this.fByteBuffer = null;
                }
                return c;
            }
            int c = this.in.read();
            if (c != -1) {
                if (this.fByteLength == this.fByteBuffer.length) {
                    byte[] newarray = new byte[this.fByteLength + 1024];
                    System.arraycopy(this.fByteBuffer, 0, newarray, 0, this.fByteLength);
                    this.fByteBuffer = newarray;
                }
                this.fByteBuffer[this.fByteLength++] = (byte)c;
            }
            return c;
        }

        @Override
        public int read(byte[] array) throws IOException {
            return this.read(array, 0, array.length);
        }

        @Override
        public int read(byte[] array, int offset, int length) throws IOException {
            if (this.fPushbackOffset < this.fPushbackLength) {
                int count = this.fPushbackLength - this.fPushbackOffset;
                if (count > length) {
                    count = length;
                }
                System.arraycopy(this.fByteBuffer, this.fPushbackOffset, array, offset, count);
                this.fPushbackOffset += count;
                return count;
            }
            if (this.fCleared) {
                return this.in.read(array, offset, length);
            }
            if (this.fPlayback) {
                if (this.fByteOffset + length > this.fByteLength) {
                    length = this.fByteLength - this.fByteOffset;
                }
                System.arraycopy(this.fByteBuffer, this.fByteOffset, array, offset, length);
                this.fByteOffset += length;
                if (this.fByteOffset == this.fByteLength) {
                    this.fCleared = true;
                    this.fByteBuffer = null;
                }
                return length;
            }
            int count = this.in.read(array, offset, length);
            if (count != -1) {
                if (this.fByteLength + count > this.fByteBuffer.length) {
                    byte[] newarray = new byte[this.fByteLength + count + 512];
                    System.arraycopy(this.fByteBuffer, 0, newarray, 0, this.fByteLength);
                    this.fByteBuffer = newarray;
                }
                System.arraycopy(array, offset, this.fByteBuffer, this.fByteLength, count);
                this.fByteLength += count;
            }
            return count;
        }
    }

    public class PlainTextScanner
    implements Scanner {
        private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();

        @Override
        public boolean scan(boolean complete) throws IOException {
            this.scanCharacters(this.fStringBuffer);
            return false;
        }

        protected void scanCharacters(XMLStringBuffer buffer) throws IOException {
            int c;
            while ((c = HTMLScanner.this.fCurrentEntity.read()) != -1) {
                HTMLScanner.this.appendChar(buffer, c, null);
                if (c != 10) continue;
                HTMLScanner.this.fCurrentEntity.incLine();
            }
            if (buffer.length > 0 && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters((XMLString)buffer, HTMLScanner.this.locationAugs());
                HTMLScanner.this.fDocumentHandler.endDocument(HTMLScanner.this.locationAugs());
            }
        }
    }

    public class SpecialScanner
    implements Scanner {
        protected String fElementName;
        protected boolean fStyle;
        protected boolean fTextarea;
        protected boolean fTitle;
        private final QName fQName = new QName();
        private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();

        public Scanner setElementName(String ename) {
            this.fElementName = ename;
            this.fStyle = this.fElementName.equalsIgnoreCase("STYLE");
            this.fTextarea = this.fElementName.equalsIgnoreCase("TEXTAREA");
            this.fTitle = this.fElementName.equalsIgnoreCase("TITLE");
            return this;
        }

        @Override
        public boolean scan(boolean complete) throws IOException {
            boolean next;
            do {
                try {
                    next = false;
                    switch (HTMLScanner.this.fScannerState) {
                        case 0: {
                            HTMLScanner.this.fBeginLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                            HTMLScanner.this.fBeginColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                            HTMLScanner.this.fBeginCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                            int c = HTMLScanner.this.fCurrentEntity.read();
                            if (c == 60) {
                                HTMLScanner.this.setScannerState((short)1);
                                break;
                            }
                            if (c == 38) {
                                if (this.fTextarea || this.fTitle) {
                                    HTMLScanner.this.scanEntityRef(this.fStringBuffer, true);
                                    break;
                                }
                                this.fStringBuffer.clear();
                                this.fStringBuffer.append('&');
                            } else {
                                if (c == -1) {
                                    if (HTMLScanner.this.fReportErrors) {
                                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                                    }
                                    throw new EOFException();
                                }
                                HTMLScanner.this.fCurrentEntity.rewind();
                                this.fStringBuffer.clear();
                            }
                            this.scanCharacters(this.fStringBuffer, -1);
                            break;
                        }
                        case 1: {
                            int delimiter = -1;
                            int c = HTMLScanner.this.fCurrentEntity.read();
                            if (c == 47) {
                                String ename = HTMLScanner.this.scanName(true);
                                if (ename != null) {
                                    if (ename.equalsIgnoreCase(this.fElementName)) {
                                        if (HTMLScanner.this.fCurrentEntity.read() == 62) {
                                            ename = HTMLScanner.modifyName(ename, HTMLScanner.this.fNamesElems);
                                            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                                                this.fQName.setValues(null, ename, ename, null);
                                                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                                                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                                                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                                                HTMLScanner.this.fDocumentHandler.endElement(this.fQName, HTMLScanner.this.locationAugs());
                                            }
                                            HTMLScanner.this.setScanner(HTMLScanner.this.fContentScanner);
                                            HTMLScanner.this.setScannerState((short)0);
                                            return true;
                                        }
                                        HTMLScanner.this.fCurrentEntity.rewind();
                                    }
                                    this.fStringBuffer.clear();
                                    this.fStringBuffer.append("</");
                                    this.fStringBuffer.append(ename);
                                } else {
                                    this.fStringBuffer.clear();
                                    this.fStringBuffer.append("</");
                                }
                            } else {
                                this.fStringBuffer.clear();
                                this.fStringBuffer.append('<');
                                HTMLScanner.this.appendChar(this.fStringBuffer, c, null);
                            }
                            this.scanCharacters(this.fStringBuffer, delimiter);
                            HTMLScanner.this.setScannerState((short)0);
                            break;
                        }
                    }
                }
                catch (EOFException e) {
                    HTMLScanner.this.setScanner(HTMLScanner.this.fContentScanner);
                    if (HTMLScanner.this.fCurrentEntityStack.empty()) {
                        HTMLScanner.this.setScannerState((short)11);
                    } else {
                        HTMLScanner.this.fCurrentEntity = (CurrentEntity)HTMLScanner.this.fCurrentEntityStack.pop();
                        HTMLScanner.this.setScannerState((short)0);
                    }
                    return true;
                }
            } while (next || complete);
            return true;
        }

        protected void scanCharacters(XMLStringBuffer buffer, int delimiter) throws IOException {
            block0: while (true) {
                int c;
                if ((c = HTMLScanner.this.fCurrentEntity.read()) == -1 || c == 60 || c == 38) {
                    if (c == -1) break;
                    HTMLScanner.this.fCurrentEntity.rewind();
                    break;
                }
                if (c == 13 || c == 10) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    int newlines = HTMLScanner.this.skipNewlines();
                    int i = 0;
                    while (true) {
                        if (i >= newlines) continue block0;
                        buffer.append('\n');
                        ++i;
                    }
                }
                HTMLScanner.this.appendChar(buffer, c, null);
            }
            if (this.fStyle) {
                if (HTMLScanner.this.fStyleStripCommentDelims) {
                    HTMLScanner.reduceToContent(buffer, "<!--", "-->");
                }
                if (HTMLScanner.this.fStyleStripCDATADelims) {
                    HTMLScanner.reduceToContent(buffer, "<![CDATA[", "]]>");
                }
            }
            if (buffer.length > 0 && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters((XMLString)buffer, HTMLScanner.this.locationAugs());
            }
        }
    }

    public class ContentScanner
    implements Scanner {
        private final QName fQName = new QName();
        private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();

        @Override
        public boolean scan(boolean complete) throws IOException {
            boolean next;
            do {
                try {
                    next = false;
                    switch (HTMLScanner.this.fScannerState) {
                        case 0: {
                            HTMLScanner.this.fBeginLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                            HTMLScanner.this.fBeginColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                            HTMLScanner.this.fBeginCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                            int c = HTMLScanner.this.fCurrentEntity.read();
                            if (c == 60) {
                                HTMLScanner.this.setScannerState((short)1);
                                next = true;
                                break;
                            }
                            if (c == 38) {
                                HTMLScanner.this.scanEntityRef(HTMLScanner.this.fStringBuffer, true);
                                break;
                            }
                            if (c == -1) {
                                throw new EOFException();
                            }
                            HTMLScanner.this.fCurrentEntity.rewind();
                            this.scanCharacters();
                            break;
                        }
                        case 1: {
                            int c = HTMLScanner.this.fCurrentEntity.read();
                            if (c == 33) {
                                if (HTMLScanner.this.skip("--->", false) || HTMLScanner.this.skip("-->", false) || HTMLScanner.this.skip("->", false) || HTMLScanner.this.skip(">", false)) {
                                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                                    HTMLScanner.this.fDocumentHandler.comment((XMLString)new XMLStringBuffer(), HTMLScanner.this.locationAugs());
                                } else if (HTMLScanner.this.skip("--", false)) {
                                    this.scanComment();
                                } else if (HTMLScanner.this.skip("[CDATA[", false)) {
                                    this.scanCDATA();
                                } else if (HTMLScanner.this.skip("DOCTYPE", false)) {
                                    HTMLScanner.this.scanDoctype();
                                } else {
                                    if (HTMLScanner.this.fReportErrors) {
                                        HTMLScanner.this.fErrorReporter.reportError("HTML1002", null);
                                    }
                                    HTMLScanner.this.skipMarkup(true);
                                }
                            } else if (c == 63) {
                                this.scanPI();
                            } else if (c == 47) {
                                this.scanEndElement();
                            } else {
                                if (c == -1) {
                                    if (HTMLScanner.this.fReportErrors) {
                                        HTMLScanner.this.fErrorReporter.reportError("HTML1003", null);
                                    }
                                    if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                                        HTMLScanner.this.fStringBuffer.clear();
                                        HTMLScanner.this.fStringBuffer.append('<');
                                        HTMLScanner.this.fDocumentHandler.characters((XMLString)HTMLScanner.this.fStringBuffer, null);
                                    }
                                    throw new EOFException();
                                }
                                HTMLScanner.this.fCurrentEntity.rewind();
                                ++HTMLScanner.this.fElementCount;
                                ((HTMLScanner)HTMLScanner.this).fSingleBoolean[0] = false;
                                String ename = this.scanStartElement(HTMLScanner.this.fSingleBoolean);
                                String enameLC = ename == null ? null : ename.toLowerCase(Locale.ROOT);
                                HTMLScanner.this.fBeginLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                                HTMLScanner.this.fBeginColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                                HTMLScanner.this.fBeginCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                                if ("script".equals(enameLC)) {
                                    this.scanScriptContent();
                                } else if (!HTMLScanner.this.fAllowSelfclosingTags && !HTMLScanner.this.fAllowSelfclosingIframe && "iframe".equals(enameLC)) {
                                    this.scanUntilEndTag("iframe");
                                } else if (!HTMLScanner.this.fParseNoScriptContent && "noscript".equals(enameLC)) {
                                    this.scanUntilEndTag("noscript");
                                } else if (!HTMLScanner.this.fParseNoFramesContent && "noframes".equals(enameLC)) {
                                    this.scanUntilEndTag("noframes");
                                } else if (ename != null && !HTMLScanner.this.fSingleBoolean[0] && HTMLElements.getElement(enameLC).isSpecial() && (!ename.equalsIgnoreCase("TITLE") || this.isEnded(enameLC))) {
                                    if (ename.equalsIgnoreCase("PLAINTEXT")) {
                                        HTMLScanner.this.setScanner(new PlainTextScanner());
                                    } else {
                                        HTMLScanner.this.setScanner(HTMLScanner.this.fSpecialScanner.setElementName(ename));
                                        HTMLScanner.this.setScannerState((short)0);
                                    }
                                    return true;
                                }
                            }
                            HTMLScanner.this.setScannerState((short)0);
                            break;
                        }
                        case 10: {
                            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                                HTMLScanner locator = HTMLScanner.this;
                                String encoding = HTMLScanner.this.fIANAEncoding;
                                Augmentations augs = HTMLScanner.this.locationAugs();
                                NamespaceSupport nscontext = new NamespaceSupport();
                                XercesBridge.getInstance().XMLDocumentHandler_startDocument(HTMLScanner.this.fDocumentHandler, (XMLLocator)locator, encoding, (NamespaceContext)nscontext, augs);
                            }
                            if (HTMLScanner.this.fInsertDoctype && HTMLScanner.this.fDocumentHandler != null) {
                                String root = HTMLElements.getElement((short)46).name;
                                root = HTMLScanner.modifyName(root, HTMLScanner.this.fNamesElems);
                                String pubid = HTMLScanner.this.fDoctypePubid;
                                String sysid = HTMLScanner.this.fDoctypeSysid;
                                HTMLScanner.this.fDocumentHandler.doctypeDecl(root, pubid, sysid, HTMLScanner.this.synthesizedAugs());
                            }
                            HTMLScanner.this.setScannerState((short)0);
                            break;
                        }
                        case 11: {
                            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth && complete) {
                                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                                HTMLScanner.this.fDocumentHandler.endDocument(HTMLScanner.this.locationAugs());
                            }
                            return false;
                        }
                        default: {
                            throw new RuntimeException("unknown scanner state: " + HTMLScanner.this.fScannerState);
                        }
                    }
                }
                catch (EOFException e) {
                    if (HTMLScanner.this.fCurrentEntityStack.empty()) {
                        HTMLScanner.this.setScannerState((short)11);
                    } else {
                        HTMLScanner.this.fCurrentEntity = (CurrentEntity)HTMLScanner.this.fCurrentEntityStack.pop();
                    }
                    next = true;
                }
            } while (next || complete);
            return true;
        }

        private void scanUntilEndTag(String tagName) throws IOException {
            int c;
            XMLStringBuffer buffer = new XMLStringBuffer();
            String end = "/" + tagName;
            int lengthToScan = tagName.length() + 2;
            while ((c = HTMLScanner.this.fCurrentEntity.read()) != -1) {
                String next;
                if (c == 60 && (next = this.nextContent(lengthToScan) + " ").length() >= lengthToScan && end.equalsIgnoreCase(next.substring(0, end.length())) && ('>' == next.charAt(lengthToScan - 1) || Character.isWhitespace(next.charAt(lengthToScan - 1)))) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    break;
                }
                if (c == 13 || c == 10) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    int newlines = HTMLScanner.this.skipNewlines();
                    for (int i = 0; i < newlines; ++i) {
                        buffer.append('\n');
                    }
                    continue;
                }
                HTMLScanner.this.appendChar(buffer, c, null);
            }
            if (buffer.length > 0 && HTMLScanner.this.fDocumentHandler != null) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters((XMLString)buffer, HTMLScanner.this.locationAugs());
            }
        }

        private void scanScriptContent() throws IOException {
            int c;
            XMLStringBuffer buffer = new XMLStringBuffer();
            boolean waitForEndComment = false;
            while ((c = HTMLScanner.this.fCurrentEntity.read()) != -1) {
                if (c == 45 && HTMLScanner.this.endsWith(buffer, "<!-")) {
                    waitForEndComment = HTMLScanner.this.endCommentAvailable();
                } else if (!waitForEndComment && c == 60) {
                    String next = this.nextContent(8) + " ";
                    if (next.length() >= 8 && "/script".equalsIgnoreCase(next.substring(0, 7)) && ('>' == next.charAt(7) || Character.isWhitespace(next.charAt(7)))) {
                        HTMLScanner.this.fCurrentEntity.rewind();
                        break;
                    }
                } else if (c == 62 && HTMLScanner.this.endsWith(buffer, "--")) {
                    waitForEndComment = false;
                }
                if (c == 13 || c == 10) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    int newlines = HTMLScanner.this.skipNewlines();
                    for (int i = 0; i < newlines; ++i) {
                        buffer.append('\n');
                    }
                    continue;
                }
                HTMLScanner.this.appendChar(buffer, c, null);
            }
            if (HTMLScanner.this.fScriptStripCommentDelims) {
                HTMLScanner.reduceToContent(buffer, "<!--", "-->");
            }
            if (HTMLScanner.this.fScriptStripCDATADelims) {
                HTMLScanner.reduceToContent(buffer, "<![CDATA[", "]]>");
            }
            if (buffer.length > 0 && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters((XMLString)buffer, HTMLScanner.this.locationAugs());
            }
        }

        protected String nextContent(int len) throws IOException {
            int originalOffset = HTMLScanner.this.fCurrentEntity.offset;
            int originalColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
            int originalCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
            char[] buff = new char[len];
            int nbRead = 0;
            for (nbRead = 0; nbRead < len; ++nbRead) {
                int c;
                if (HTMLScanner.this.fCurrentEntity.offset == HTMLScanner.this.fCurrentEntity.length) {
                    if (HTMLScanner.this.fCurrentEntity.length != HTMLScanner.this.fCurrentEntity.buffer.length) break;
                    HTMLScanner.this.fCurrentEntity.load(HTMLScanner.this.fCurrentEntity.buffer.length);
                }
                if ((c = HTMLScanner.this.fCurrentEntity.read()) == -1) break;
                buff[nbRead] = (char)c;
            }
            HTMLScanner.this.fCurrentEntity.restorePosition(originalOffset, originalColumnNumber, originalCharacterOffset);
            return new String(buff, 0, nbRead);
        }

        protected void scanCharacters() throws IOException {
            int newlines;
            HTMLScanner.this.fStringBuffer.clear();
            while ((newlines = HTMLScanner.this.skipNewlines()) != 0 || HTMLScanner.this.fCurrentEntity.offset != HTMLScanner.this.fCurrentEntity.length) {
                int next;
                int offset;
                for (int i = offset = HTMLScanner.this.fCurrentEntity.offset - newlines; i < HTMLScanner.this.fCurrentEntity.offset; ++i) {
                    HTMLScanner.this.fCurrentEntity.buffer[i] = 10;
                }
                while (HTMLScanner.this.fCurrentEntity.hasNext()) {
                    char c = HTMLScanner.this.fCurrentEntity.getNextChar();
                    if (c != '<' && c != '&' && c != '\n' && c != '\r') continue;
                    HTMLScanner.this.fCurrentEntity.rewind();
                    break;
                }
                if (HTMLScanner.this.fCurrentEntity.offset > offset && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fStringBuffer.append(HTMLScanner.this.fCurrentEntity.buffer, offset, HTMLScanner.this.fCurrentEntity.offset - offset);
                }
                boolean hasNext = HTMLScanner.this.fCurrentEntity.offset < HTMLScanner.this.fCurrentEntity.buffer.length;
                int n = next = hasNext ? (int)HTMLScanner.this.fCurrentEntity.getCurrentChar() : -1;
                if (next != 38 && next != 60 && next != -1) continue;
                break;
            }
            if (HTMLScanner.this.fStringBuffer.length != 0) {
                HTMLScanner.this.fDocumentHandler.characters((XMLString)HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
            }
        }

        protected void scanCDATA() throws IOException {
            HTMLScanner.this.fStringBuffer.clear();
            if (HTMLScanner.this.fCDATASections) {
                if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fDocumentHandler.startCDATA(HTMLScanner.this.locationAugs());
                }
            } else {
                HTMLScanner.this.fStringBuffer.append("[CDATA[");
            }
            boolean eof = this.scanMarkupContent(HTMLScanner.this.fStringBuffer, ']');
            if (!HTMLScanner.this.fCDATASections) {
                HTMLScanner.this.fStringBuffer.append("]]");
            }
            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                if (HTMLScanner.this.fCDATASections) {
                    HTMLScanner.this.fDocumentHandler.characters((XMLString)HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
                    HTMLScanner.this.fDocumentHandler.endCDATA(HTMLScanner.this.locationAugs());
                } else {
                    HTMLScanner.this.fDocumentHandler.comment((XMLString)HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
                }
            }
            if (eof) {
                throw new EOFException();
            }
        }

        protected void scanComment() throws IOException {
            boolean eof;
            XMLStringBuffer buffer;
            block8: {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                buffer = new XMLStringBuffer();
                eof = this.scanMarkupContent(buffer, '-');
                if (eof) {
                    HTMLScanner.this.fCurrentEntity.resetBuffer(buffer, HTMLScanner.this.fEndLineNumber, HTMLScanner.this.fEndColumnNumber, HTMLScanner.this.fEndCharacterOffset);
                    buffer = new XMLStringBuffer();
                    block0: while (true) {
                        int c;
                        if ((c = HTMLScanner.this.fCurrentEntity.read()) == -1) {
                            if (HTMLScanner.this.fReportErrors) {
                                HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                            }
                            eof = true;
                            break block8;
                        }
                        if (c == 10 || c == 13) {
                            HTMLScanner.this.fCurrentEntity.rewind();
                            int newlines = HTMLScanner.this.skipNewlines();
                            int i = 0;
                            while (true) {
                                if (i >= newlines) continue block0;
                                buffer.append('\n');
                                ++i;
                            }
                        }
                        if (c == 62) break;
                        HTMLScanner.this.appendChar(buffer, c, null);
                    }
                    eof = false;
                }
            }
            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.comment((XMLString)buffer, HTMLScanner.this.locationAugs());
            }
            if (eof) {
                throw new EOFException();
            }
        }

        protected boolean scanMarkupContent(XMLStringBuffer buffer, char cend) throws IOException {
            int c = -1;
            block0: while (true) {
                int i;
                if ((c = HTMLScanner.this.fCurrentEntity.read()) == cend) {
                    int count = 1;
                    while ((c = HTMLScanner.this.fCurrentEntity.read()) == cend) {
                        ++count;
                    }
                    if (c == -1) {
                        if (!HTMLScanner.this.fReportErrors) break;
                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                        break;
                    }
                    if (count < 2) {
                        buffer.append((char)cend);
                        HTMLScanner.this.fCurrentEntity.rewind();
                        continue;
                    }
                    if (c != 62) {
                        for (i = 0; i < count; ++i) {
                            buffer.append((char)cend);
                        }
                        HTMLScanner.this.fCurrentEntity.rewind();
                        continue;
                    }
                    for (i = 0; i < count - 2; ++i) {
                        buffer.append((char)cend);
                    }
                    break;
                }
                if (c == 10 || c == 13) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    int newlines = HTMLScanner.this.skipNewlines();
                    i = 0;
                    while (true) {
                        if (i >= newlines) continue block0;
                        buffer.append('\n');
                        ++i;
                    }
                }
                if (c == -1) {
                    if (!HTMLScanner.this.fReportErrors) break;
                    HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                    break;
                }
                HTMLScanner.this.appendChar(buffer, c, null);
            }
            return c == -1;
        }

        protected void scanPI() throws IOException {
            String target;
            if (HTMLScanner.this.fReportErrors) {
                HTMLScanner.this.fErrorReporter.reportWarning("HTML1008", null);
            }
            if ((target = HTMLScanner.this.scanName(true)) != null && !target.equalsIgnoreCase("xml")) {
                int c;
                block17: {
                    while (true) {
                        if ((c = HTMLScanner.this.fCurrentEntity.read()) == 13 || c == 10) {
                            if (c == 13 && (c = HTMLScanner.this.fCurrentEntity.read()) != 10) {
                                --HTMLScanner.this.fCurrentEntity.offset;
                                --HTMLScanner.this.fCurrentEntity.characterOffset_;
                            }
                            HTMLScanner.this.fCurrentEntity.incLine();
                            continue;
                        }
                        if (c == -1) break block17;
                        if (c != 32 && c != 9) break;
                    }
                    HTMLScanner.this.fCurrentEntity.rewind();
                }
                HTMLScanner.this.fStringBuffer.clear();
                while (true) {
                    if ((c = HTMLScanner.this.fCurrentEntity.read()) == 63 || c == 47) {
                        char c0 = (char)c;
                        c = HTMLScanner.this.fCurrentEntity.read();
                        if (c == -1 || c == 62) break;
                        HTMLScanner.this.fStringBuffer.append(c0);
                        HTMLScanner.this.fCurrentEntity.rewind();
                        continue;
                    }
                    if (c == 13 || c == 10) {
                        HTMLScanner.this.fStringBuffer.append('\n');
                        if (c == 13 && (c = HTMLScanner.this.fCurrentEntity.read()) != 10) {
                            --HTMLScanner.this.fCurrentEntity.offset;
                            --HTMLScanner.this.fCurrentEntity.characterOffset_;
                        }
                        HTMLScanner.this.fCurrentEntity.incLine();
                        continue;
                    }
                    if (c == -1) break;
                    if (c == 62) {
                        if (HTMLScanner.this.fDocumentHandler != null) {
                            HTMLScanner.this.fStringBuffer.append(target);
                            HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                            HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                            HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                            HTMLScanner.this.fDocumentHandler.comment((XMLString)HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
                        }
                        return;
                    }
                    HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, c, null);
                }
                XMLStringBuffer data = HTMLScanner.this.fStringBuffer;
                if (HTMLScanner.this.fDocumentHandler != null) {
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fDocumentHandler.processingInstruction(target, (XMLString)data, HTMLScanner.this.locationAugs());
                }
            } else {
                int beginLineNumber = HTMLScanner.this.fBeginLineNumber;
                int beginColumnNumber = HTMLScanner.this.fBeginColumnNumber;
                int beginCharacterOffset = HTMLScanner.this.fBeginCharacterOffset;
                this.fAttributes.removeAllAttributes();
                int aindex = 0;
                while (this.scanPseudoAttribute(this.fAttributes)) {
                    if (this.fAttributes.getValue(aindex).length() == 0) {
                        this.fAttributes.removeAttributeAt(aindex);
                        continue;
                    }
                    this.fAttributes.getName(aindex, this.fQName);
                    this.fQName.rawname = this.fQName.rawname.toLowerCase(Locale.ROOT);
                    this.fAttributes.setName(aindex, this.fQName);
                    ++aindex;
                }
                if (HTMLScanner.this.fDocumentHandler != null) {
                    boolean xmlDeclNow;
                    String version = this.fAttributes.getValue("version");
                    String encoding = this.fAttributes.getValue("encoding");
                    String standalone = this.fAttributes.getValue("standalone");
                    boolean bl = xmlDeclNow = HTMLScanner.this.fIgnoreSpecifiedCharset || !this.changeEncoding(encoding);
                    if (xmlDeclNow) {
                        HTMLScanner.this.fBeginLineNumber = beginLineNumber;
                        HTMLScanner.this.fBeginColumnNumber = beginColumnNumber;
                        HTMLScanner.this.fBeginCharacterOffset = beginCharacterOffset;
                        HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                        HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                        HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                        HTMLScanner.this.fDocumentHandler.xmlDecl(version, encoding, standalone, HTMLScanner.this.locationAugs());
                    }
                }
            }
        }

        protected String scanStartElement(boolean[] empty) throws IOException {
            int c;
            String ename = HTMLScanner.this.scanName(true);
            int length = ename != null ? ename.length() : 0;
            int n = c = length > 0 ? (int)ename.charAt(0) : -1;
            if (length == 0 || (c < 97 || c > 122) && (c < 65 || c > 90)) {
                if (HTMLScanner.this.fReportErrors) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1009", null);
                }
                if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    HTMLScanner.this.fStringBuffer.clear();
                    HTMLScanner.this.fStringBuffer.append('<');
                    if (length > 0) {
                        HTMLScanner.this.fStringBuffer.append(ename);
                    }
                    HTMLScanner.this.fDocumentHandler.characters((XMLString)HTMLScanner.this.fStringBuffer, null);
                }
                return null;
            }
            ename = HTMLScanner.modifyName(ename, HTMLScanner.this.fNamesElems);
            this.fAttributes.removeAllAttributes();
            int beginLineNumber = HTMLScanner.this.fBeginLineNumber;
            int beginColumnNumber = HTMLScanner.this.fBeginColumnNumber;
            int beginCharacterOffset = HTMLScanner.this.fBeginCharacterOffset;
            while (this.scanAttribute(this.fAttributes, empty)) {
            }
            HTMLScanner.this.fBeginLineNumber = beginLineNumber;
            HTMLScanner.this.fBeginColumnNumber = beginColumnNumber;
            HTMLScanner.this.fBeginCharacterOffset = beginCharacterOffset;
            if (HTMLScanner.this.fByteStream != null && HTMLScanner.this.fElementDepth == -1) {
                if (ename.equalsIgnoreCase("META") && !HTMLScanner.this.fIgnoreSpecifiedCharset) {
                    String httpEquiv = HTMLScanner.getValue((XMLAttributes)this.fAttributes, "http-equiv");
                    if (httpEquiv != null && httpEquiv.equalsIgnoreCase("content-type")) {
                        int index1;
                        String content = HTMLScanner.getValue((XMLAttributes)this.fAttributes, "content");
                        if (content != null && (index1 = (content = this.removeSpaces(content)).toLowerCase(Locale.ROOT).indexOf("charset=")) != -1) {
                            int index2 = content.indexOf(59, index1);
                            String charset = index2 != -1 ? content.substring(index1 + 8, index2) : content.substring(index1 + 8);
                            this.changeEncoding(charset);
                        }
                    } else {
                        String metaCharset = HTMLScanner.getValue((XMLAttributes)this.fAttributes, "charset");
                        if (metaCharset != null) {
                            this.changeEncoding(metaCharset);
                        }
                    }
                } else if (ename.equalsIgnoreCase("BODY")) {
                    HTMLScanner.this.fByteStream.clear();
                    HTMLScanner.this.fByteStream = null;
                } else {
                    HTMLElements.Element element = HTMLElements.getElement(ename);
                    if (element.parent != null && element.parent.length > 0 && element.parent[0].code == 14) {
                        HTMLScanner.this.fByteStream.clear();
                        HTMLScanner.this.fByteStream = null;
                    }
                }
            }
            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                this.fQName.setValues(null, ename, ename, null);
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                if (empty[0] && !"BR".equalsIgnoreCase(ename)) {
                    HTMLScanner.this.fDocumentHandler.emptyElement(this.fQName, (XMLAttributes)this.fAttributes, HTMLScanner.this.locationAugs());
                } else {
                    HTMLScanner.this.fDocumentHandler.startElement(this.fQName, (XMLAttributes)this.fAttributes, HTMLScanner.this.locationAugs());
                }
            }
            return ename;
        }

        private String removeSpaces(String content) {
            StringBuilder sb = null;
            for (int i = content.length() - 1; i >= 0; --i) {
                if (!Character.isWhitespace(content.charAt(i))) continue;
                if (sb == null) {
                    sb = new StringBuilder(content);
                }
                sb.deleteCharAt(i);
            }
            return sb == null ? content : sb.toString();
        }

        private boolean changeEncoding(String charset) {
            if (charset == null || HTMLScanner.this.fByteStream == null) {
                return false;
            }
            charset = charset.trim();
            boolean encodingChanged = false;
            try {
                String ianaEncoding = charset;
                String javaEncoding = EncodingMap.getIANA2JavaMapping((String)ianaEncoding.toUpperCase(Locale.ENGLISH));
                if (javaEncoding == null) {
                    javaEncoding = ianaEncoding;
                    if (HTMLScanner.this.fReportErrors) {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1001", new Object[]{ianaEncoding});
                    }
                }
                if (!javaEncoding.equals(HTMLScanner.this.fJavaEncoding)) {
                    if (!HTMLScanner.this.isEncodingCompatible(javaEncoding, HTMLScanner.this.fJavaEncoding)) {
                        if (HTMLScanner.this.fReportErrors) {
                            HTMLScanner.this.fErrorReporter.reportError("HTML1015", new Object[]{javaEncoding, HTMLScanner.this.fJavaEncoding});
                        }
                    } else {
                        HTMLScanner.this.fIso8859Encoding = ianaEncoding.toUpperCase(Locale.ENGLISH).startsWith("ISO-8859") || ianaEncoding.equalsIgnoreCase(HTMLScanner.this.fDefaultIANAEncoding);
                        HTMLScanner.this.fJavaEncoding = javaEncoding;
                        HTMLScanner.this.fCurrentEntity.setStream(new InputStreamReader((InputStream)HTMLScanner.this.fByteStream, javaEncoding));
                        HTMLScanner.this.fByteStream.playback();
                        HTMLScanner.this.fElementDepth = HTMLScanner.this.fElementCount;
                        HTMLScanner.this.fElementCount = 0;
                        encodingChanged = true;
                    }
                }
            }
            catch (UnsupportedEncodingException e) {
                if (HTMLScanner.this.fReportErrors) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1010", new Object[]{charset});
                }
                HTMLScanner.this.fByteStream.clear();
                HTMLScanner.this.fByteStream = null;
            }
            return encodingChanged;
        }

        protected boolean scanAttribute(XMLAttributesImpl attributes, boolean[] empty) throws IOException {
            return this.scanAttribute(attributes, empty, '/');
        }

        protected boolean scanPseudoAttribute(XMLAttributesImpl attributes) throws IOException {
            return this.scanAttribute(attributes, HTMLScanner.this.fSingleBoolean, '?');
        }

        protected boolean scanAttribute(XMLAttributesImpl attributes, boolean[] empty, char endc) throws IOException {
            boolean skippedSpaces = HTMLScanner.this.skipSpaces();
            HTMLScanner.this.fBeginLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
            HTMLScanner.this.fBeginColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
            HTMLScanner.this.fBeginCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
            int c = HTMLScanner.this.fCurrentEntity.read();
            if (c == -1) {
                if (HTMLScanner.this.fReportErrors) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                }
                return false;
            }
            if (c == 62) {
                return false;
            }
            if (c == 60) {
                HTMLScanner.this.fCurrentEntity.rewind();
                return false;
            }
            HTMLScanner.this.fCurrentEntity.rewind();
            String aname = HTMLScanner.this.scanName(false);
            if (aname == null) {
                if (HTMLScanner.this.fReportErrors) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1011", null);
                }
                empty[0] = HTMLScanner.this.skipMarkup(false);
                return false;
            }
            if (!skippedSpaces && HTMLScanner.this.fReportErrors) {
                HTMLScanner.this.fErrorReporter.reportError("HTML1013", new Object[]{aname});
            }
            aname = HTMLScanner.modifyName(aname, HTMLScanner.this.fNamesAttrs);
            HTMLScanner.this.skipSpaces();
            c = HTMLScanner.this.fCurrentEntity.read();
            if (c == -1) {
                if (HTMLScanner.this.fReportErrors) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                }
                throw new EOFException();
            }
            if (c == 47 || c == 62) {
                this.fQName.setValues(null, aname, aname, null);
                attributes.addAttribute(this.fQName, "CDATA", "");
                attributes.setSpecified(attributes.getLength() - 1, true);
                if (HTMLScanner.this.fAugmentations) {
                    this.addLocationItem((XMLAttributes)attributes, attributes.getLength() - 1);
                }
                if (c == 47) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    empty[0] = HTMLScanner.this.skipMarkup(false);
                }
                return false;
            }
            if (c == 61) {
                HTMLScanner.this.skipSpaces();
                c = HTMLScanner.this.fCurrentEntity.read();
                if (c == -1) {
                    if (HTMLScanner.this.fReportErrors) {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                    }
                    throw new EOFException();
                }
                if (c == 62) {
                    this.fQName.setValues(null, aname, aname, null);
                    attributes.addAttribute(this.fQName, "CDATA", "");
                    attributes.setSpecified(attributes.getLength() - 1, true);
                    if (HTMLScanner.this.fAugmentations) {
                        this.addLocationItem((XMLAttributes)attributes, attributes.getLength() - 1);
                    }
                    return false;
                }
                HTMLScanner.this.fStringBuffer.clear();
                HTMLScanner.this.fNonNormAttr.clear();
                if (c != 39 && c != 34) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    while (true) {
                        if (Character.isWhitespace((char)(c = HTMLScanner.this.fCurrentEntity.read())) || c == 62) break;
                        if (c == -1) {
                            if (HTMLScanner.this.fReportErrors) {
                                HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                            }
                            throw new EOFException();
                        }
                        if (c == 38) {
                            int ce = HTMLScanner.this.scanEntityRef(HTMLScanner.this.fStringBuffer2, false);
                            if (ce != -1) {
                                HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, ce, null);
                            } else {
                                HTMLScanner.this.fStringBuffer.append((XMLString)HTMLScanner.this.fStringBuffer2);
                            }
                            HTMLScanner.this.fNonNormAttr.append((XMLString)HTMLScanner.this.fStringBuffer2);
                            continue;
                        }
                        HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, c, null);
                        HTMLScanner.this.appendChar(HTMLScanner.this.fNonNormAttr, c, null);
                    }
                    HTMLScanner.this.fCurrentEntity.rewind();
                    this.fQName.setValues(null, aname, aname, null);
                    String avalue = HTMLScanner.this.fStringBuffer.toString();
                    attributes.addAttribute(this.fQName, "CDATA", avalue);
                    int lastattr = attributes.getLength() - 1;
                    attributes.setSpecified(lastattr, true);
                    attributes.setNonNormalizedValue(lastattr, HTMLScanner.this.fNonNormAttr.toString());
                    if (HTMLScanner.this.fAugmentations) {
                        this.addLocationItem((XMLAttributes)attributes, attributes.getLength() - 1);
                    }
                    return true;
                }
                char quote = (char)c;
                boolean isStart = true;
                boolean prevSpace = false;
                do {
                    boolean acceptSpace = !HTMLScanner.this.fNormalizeAttributes || !isStart && !prevSpace;
                    c = HTMLScanner.this.fCurrentEntity.read();
                    if (c == -1) {
                        if (!HTMLScanner.this.fReportErrors) break;
                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                        break;
                    }
                    if (c == 38) {
                        isStart = false;
                        int ce = HTMLScanner.this.scanEntityRef(HTMLScanner.this.fStringBuffer2, false);
                        if (ce != -1) {
                            HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, ce, null);
                        } else {
                            HTMLScanner.this.fStringBuffer.append((XMLString)HTMLScanner.this.fStringBuffer2);
                        }
                        HTMLScanner.this.fNonNormAttr.append((XMLString)HTMLScanner.this.fStringBuffer2);
                    } else if (c == 32 || c == 9) {
                        if (acceptSpace) {
                            HTMLScanner.this.fStringBuffer.append((char)(HTMLScanner.this.fNormalizeAttributes ? 32 : (char)c));
                        }
                        HTMLScanner.this.fNonNormAttr.append((char)c);
                    } else if (c == 13 || c == 10) {
                        if (c == 13) {
                            int c2 = HTMLScanner.this.fCurrentEntity.read();
                            if (c2 == 10) {
                                HTMLScanner.this.fNonNormAttr.append('\r');
                                c = c2;
                            } else if (c2 != -1) {
                                HTMLScanner.this.fCurrentEntity.rewind();
                            }
                        }
                        if (acceptSpace) {
                            HTMLScanner.this.fStringBuffer.append(HTMLScanner.this.fNormalizeAttributes ? (char)' ' : '\n');
                        }
                        HTMLScanner.this.fCurrentEntity.incLine();
                        HTMLScanner.this.fNonNormAttr.append((char)c);
                    } else if (c != quote) {
                        isStart = false;
                        HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, c, null);
                        HTMLScanner.this.appendChar(HTMLScanner.this.fNonNormAttr, c, null);
                    }
                    prevSpace = c == 32 || c == 9 || c == 13 || c == 10;
                    boolean bl = isStart = isStart && prevSpace;
                } while (c != quote);
                if (HTMLScanner.this.fNormalizeAttributes && HTMLScanner.this.fStringBuffer.length > 0 && HTMLScanner.this.fStringBuffer.ch[HTMLScanner.this.fStringBuffer.length - 1] == ' ') {
                    --HTMLScanner.this.fStringBuffer.length;
                }
                this.fQName.setValues(null, aname, aname, null);
                String avalue = HTMLScanner.this.fStringBuffer.toString();
                attributes.addAttribute(this.fQName, "CDATA", avalue);
                int lastattr = attributes.getLength() - 1;
                attributes.setSpecified(lastattr, true);
                attributes.setNonNormalizedValue(lastattr, HTMLScanner.this.fNonNormAttr.toString());
                if (HTMLScanner.this.fAugmentations) {
                    this.addLocationItem((XMLAttributes)attributes, attributes.getLength() - 1);
                }
            } else {
                this.fQName.setValues(null, aname, aname, null);
                attributes.addAttribute(this.fQName, "CDATA", "");
                attributes.setSpecified(attributes.getLength() - 1, true);
                HTMLScanner.this.fCurrentEntity.rewind();
                if (HTMLScanner.this.fAugmentations) {
                    this.addLocationItem((XMLAttributes)attributes, attributes.getLength() - 1);
                }
            }
            return true;
        }

        protected void addLocationItem(XMLAttributes attributes, int index) {
            HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
            HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
            HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
            LocationItem locationItem = new LocationItem();
            locationItem.setValues(HTMLScanner.this.fBeginLineNumber, HTMLScanner.this.fBeginColumnNumber, HTMLScanner.this.fBeginCharacterOffset, HTMLScanner.this.fEndLineNumber, HTMLScanner.this.fEndColumnNumber, HTMLScanner.this.fEndCharacterOffset);
            Augmentations augs = attributes.getAugmentations(index);
            augs.putItem(HTMLScanner.AUGMENTATIONS, (Object)locationItem);
        }

        protected void scanEndElement() throws IOException {
            String ename = HTMLScanner.this.scanName(true);
            if (HTMLScanner.this.fReportErrors && ename == null) {
                HTMLScanner.this.fErrorReporter.reportError("HTML1012", null);
            }
            HTMLScanner.this.skipMarkup(false);
            if (ename != null) {
                ename = HTMLScanner.modifyName(ename, HTMLScanner.this.fNamesElems);
                if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    this.fQName.setValues(null, ename, ename, null);
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fDocumentHandler.endElement(this.fQName, HTMLScanner.this.locationAugs());
                }
            }
        }

        private boolean isEnded(String ename) {
            String content = new String(HTMLScanner.this.fCurrentEntity.buffer, HTMLScanner.this.fCurrentEntity.offset, HTMLScanner.this.fCurrentEntity.length - HTMLScanner.this.fCurrentEntity.offset);
            return content.toLowerCase(Locale.ROOT).indexOf("</" + ename.toLowerCase(Locale.ROOT) + ">") != -1;
        }
    }

    public static class CurrentEntity {
        private Reader stream_;
        private String encoding;
        public final String publicId;
        public final String baseSystemId;
        public final String literalSystemId;
        public final String expandedSystemId;
        public final String version = "1.0";
        private int lineNumber_ = 1;
        private int columnNumber_ = 1;
        public int characterOffset_ = 0;
        public char[] buffer = new char[2048];
        public int offset = 0;
        public int length = 0;
        private boolean endReached_ = false;

        public CurrentEntity(Reader stream, String encoding, String publicId, String baseSystemId, String literalSystemId, String expandedSystemId) {
            this.stream_ = stream;
            this.encoding = encoding;
            this.publicId = publicId;
            this.baseSystemId = baseSystemId;
            this.literalSystemId = literalSystemId;
            this.expandedSystemId = expandedSystemId;
        }

        private char getCurrentChar() {
            return this.buffer[this.offset];
        }

        private char getNextChar() {
            ++this.characterOffset_;
            ++this.columnNumber_;
            return this.buffer[this.offset++];
        }

        private void closeQuietly() {
            try {
                this.stream_.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }

        boolean hasNext() {
            return this.offset < this.length;
        }

        protected int load(int offset) throws IOException {
            int count;
            if (offset == this.buffer.length) {
                int adjust = this.buffer.length / 4;
                char[] array = new char[this.buffer.length + adjust];
                System.arraycopy(this.buffer, 0, array, 0, this.length);
                this.buffer = array;
            }
            if ((count = this.stream_.read(this.buffer, offset, this.buffer.length - offset)) == -1) {
                this.endReached_ = true;
            }
            this.length = count != -1 ? count + offset : offset;
            this.offset = offset;
            return count;
        }

        protected int read() throws IOException {
            if (this.offset == this.length) {
                if (this.endReached_) {
                    return -1;
                }
                if (this.load(0) == -1) {
                    return -1;
                }
            }
            char c = this.buffer[this.offset++];
            ++this.characterOffset_;
            ++this.columnNumber_;
            return c;
        }

        private void debugBufferIfNeeded(String prefix) {
            this.debugBufferIfNeeded(prefix, "");
        }

        private void debugBufferIfNeeded(String prefix, String suffix) {
        }

        private void setStream(InputStreamReader inputStreamReader) {
            this.stream_ = inputStreamReader;
            this.characterOffset_ = 0;
            this.length = 0;
            this.offset = 0;
            this.columnNumber_ = 1;
            this.lineNumber_ = 1;
            this.encoding = inputStreamReader.getEncoding();
        }

        private void rewind() {
            --this.offset;
            --this.characterOffset_;
            --this.columnNumber_;
        }

        private void rewind(int i) {
            this.offset -= i;
            this.characterOffset_ -= i;
            this.columnNumber_ -= i;
        }

        private void incLine() {
            ++this.lineNumber_;
            this.columnNumber_ = 1;
        }

        private void incLine(int nbLines) {
            this.lineNumber_ += nbLines;
            this.columnNumber_ = 1;
        }

        public int getLineNumber() {
            return this.lineNumber_;
        }

        private void resetBuffer(XMLStringBuffer buffer, int lineNumber, int columnNumber, int characterOffset) {
            this.lineNumber_ = lineNumber;
            this.columnNumber_ = columnNumber;
            this.characterOffset_ = characterOffset;
            this.buffer = buffer.ch;
            this.offset = buffer.offset;
            this.length = buffer.length;
        }

        private int getColumnNumber() {
            return this.columnNumber_;
        }

        private void restorePosition(int originalOffset, int originalColumnNumber, int originalCharacterOffset) {
            this.offset = originalOffset;
            this.columnNumber_ = originalColumnNumber;
            this.characterOffset_ = originalCharacterOffset;
        }

        private int getCharacterOffset() {
            return this.characterOffset_;
        }
    }

    public static interface Scanner {
        public boolean scan(boolean var1) throws IOException;
    }
}

