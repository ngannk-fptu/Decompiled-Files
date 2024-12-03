/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko;

import java.io.EOFException;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Locale;
import java.util.Stack;
import org.htmlunit.cyberneko.HTMLComponent;
import org.htmlunit.cyberneko.HTMLConfiguration;
import org.htmlunit.cyberneko.HTMLElements;
import org.htmlunit.cyberneko.HTMLEntitiesParser;
import org.htmlunit.cyberneko.HTMLErrorReporter;
import org.htmlunit.cyberneko.HTMLEventInfo;
import org.htmlunit.cyberneko.xerces.util.EncodingMap;
import org.htmlunit.cyberneko.xerces.util.NamespaceSupport;
import org.htmlunit.cyberneko.xerces.util.URI;
import org.htmlunit.cyberneko.xerces.util.XMLAttributesImpl;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XMLDocumentHandler;
import org.htmlunit.cyberneko.xerces.xni.XMLLocator;
import org.htmlunit.cyberneko.xerces.xni.XMLString;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLComponentManager;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentScanner;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLInputSource;

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
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://cyberneko.org/html/features/augmentations", "http://cyberneko.org/html/features/report-errors", "http://cyberneko.org/html/features/scanner/script/strip-cdata-delims", "http://cyberneko.org/html/features/scanner/script/strip-comment-delims", "http://cyberneko.org/html/features/scanner/style/strip-cdata-delims", "http://cyberneko.org/html/features/scanner/style/strip-comment-delims", "http://cyberneko.org/html/features/scanner/ignore-specified-charset", "http://cyberneko.org/html/features/scanner/cdata-sections", "http://cyberneko.org/html/features/override-doctype", "http://cyberneko.org/html/features/insert-doctype", "http://cyberneko.org/html/features/scanner/normalize-attrs", "http://cyberneko.org/html/features/parse-noscript-content", "http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe", "http://cyberneko.org/html/features/scanner/allow-selfclosing-tags"};
    private static final Boolean[] RECOGNIZED_FEATURES_DEFAULTS = new Boolean[]{null, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE};
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String DEFAULT_ENCODING = "http://cyberneko.org/html/properties/default-encoding";
    protected static final String ERROR_REPORTER = "http://cyberneko.org/html/properties/error-reporter";
    protected static final String DOCTYPE_PUBID = "http://cyberneko.org/html/properties/doctype/pubid";
    protected static final String DOCTYPE_SYSID = "http://cyberneko.org/html/properties/doctype/sysid";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://cyberneko.org/html/properties/names/elems", "http://cyberneko.org/html/properties/names/attrs", "http://cyberneko.org/html/properties/default-encoding", "http://cyberneko.org/html/properties/error-reporter", "http://cyberneko.org/html/properties/doctype/pubid", "http://cyberneko.org/html/properties/doctype/sysid"};
    private static final Object[] RECOGNIZED_PROPERTIES_DEFAULTS = new Object[]{null, null, "Windows-1252", null, "-//W3C//DTD HTML 4.01 Transitional//EN", "http://www.w3.org/TR/html4/loose.dtd"};
    private static final char REPLACEMENT_CHARACTER = '\ufffd';
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
    private boolean fAugmentations_;
    private boolean fReportErrors_;
    private boolean fScriptStripCDATADelims_;
    private boolean fScriptStripCommentDelims_;
    private boolean fStyleStripCDATADelims_;
    private boolean fStyleStripCommentDelims_;
    private boolean fIgnoreSpecifiedCharset_;
    private boolean fCDATASections_;
    private boolean fOverrideDoctype_;
    private boolean fInsertDoctype_;
    private boolean fNormalizeAttributes_;
    private boolean fParseNoScriptContent_;
    private boolean fAllowSelfclosingIframe_;
    private boolean fAllowSelfclosingTags_;
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
    protected final Stack<CurrentEntity> fCurrentEntityStack = new Stack();
    protected Scanner fScanner;
    protected short fScannerState;
    protected XMLDocumentHandler fDocumentHandler;
    protected String fIANAEncoding;
    protected String fJavaEncoding;
    protected int fElementCount;
    protected int fElementDepth;
    protected Scanner fContentScanner = new ContentScanner();
    protected final SpecialScanner fSpecialScanner = new SpecialScanner();
    protected final XMLString fStringBuffer = new XMLString();
    private final XMLString fStringBuffer2 = new XMLString();
    private final boolean[] fSingleBoolean = new boolean[]{false};
    private final HTMLConfiguration htmlConfiguration_;

    HTMLScanner(HTMLConfiguration htmlConfiguration) {
        this.htmlConfiguration_ = htmlConfiguration;
    }

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
                this.fCurrentEntity = this.fCurrentEntityStack.pop();
                this.fCurrentEntity.closeQuietly();
                ++i;
            }
        } else if (closeall && this.fCurrentEntity != null) {
            this.fCurrentEntity.closeQuietly();
        }
    }

    @Override
    public String getEncoding() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.encoding_ : null;
    }

    @Override
    public String getPublicId() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.publicId : null;
    }

    @Override
    public String getBaseSystemId() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.baseSystemId : null;
    }

    @Override
    public String getLiteralSystemId() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.literalSystemId : null;
    }

    @Override
    public String getExpandedSystemId() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.expandedSystemId : null;
    }

    @Override
    public int getLineNumber() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.getLineNumber() : -1;
    }

    @Override
    public int getColumnNumber() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.getColumnNumber() : -1;
    }

    @Override
    public String getXMLVersion() {
        return this.fCurrentEntity != null ? this.fCurrentEntity.version : null;
    }

    @Override
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

    @Override
    public String[] getRecognizedFeatures() {
        return RECOGNIZED_FEATURES;
    }

    @Override
    public String[] getRecognizedProperties() {
        return RECOGNIZED_PROPERTIES;
    }

    @Override
    public void reset(XMLComponentManager manager) throws XMLConfigurationException {
        this.fAugmentations_ = manager.getFeature(AUGMENTATIONS);
        this.fReportErrors_ = manager.getFeature(REPORT_ERRORS);
        this.fScriptStripCDATADelims_ = manager.getFeature(SCRIPT_STRIP_CDATA_DELIMS);
        this.fScriptStripCommentDelims_ = manager.getFeature(SCRIPT_STRIP_COMMENT_DELIMS);
        this.fStyleStripCDATADelims_ = manager.getFeature(STYLE_STRIP_CDATA_DELIMS);
        this.fStyleStripCommentDelims_ = manager.getFeature(STYLE_STRIP_COMMENT_DELIMS);
        this.fIgnoreSpecifiedCharset_ = manager.getFeature(IGNORE_SPECIFIED_CHARSET);
        this.fCDATASections_ = manager.getFeature(CDATA_SECTIONS);
        this.fOverrideDoctype_ = manager.getFeature(OVERRIDE_DOCTYPE);
        this.fInsertDoctype_ = manager.getFeature(INSERT_DOCTYPE);
        this.fNormalizeAttributes_ = manager.getFeature(NORMALIZE_ATTRIBUTES);
        this.fParseNoScriptContent_ = manager.getFeature(PARSE_NOSCRIPT_CONTENT);
        this.fAllowSelfclosingIframe_ = manager.getFeature(ALLOW_SELFCLOSING_IFRAME);
        this.fAllowSelfclosingTags_ = manager.getFeature(ALLOW_SELFCLOSING_TAGS);
        this.fNamesElems = HTMLScanner.getNamesValue(String.valueOf(manager.getProperty(NAMES_ELEMS)));
        this.fNamesAttrs = HTMLScanner.getNamesValue(String.valueOf(manager.getProperty(NAMES_ATTRS)));
        this.fDefaultIANAEncoding = String.valueOf(manager.getProperty(DEFAULT_ENCODING));
        this.fErrorReporter = (HTMLErrorReporter)manager.getProperty(ERROR_REPORTER);
        this.fDoctypePubid = String.valueOf(manager.getProperty(DOCTYPE_PUBID));
        this.fDoctypeSysid = String.valueOf(manager.getProperty(DOCTYPE_SYSID));
    }

    @Override
    public void setFeature(String featureId, boolean state) {
        if (featureId.equals(AUGMENTATIONS)) {
            this.fAugmentations_ = state;
        } else if (featureId.equals(IGNORE_SPECIFIED_CHARSET)) {
            this.fIgnoreSpecifiedCharset_ = state;
        } else if (featureId.equals(SCRIPT_STRIP_CDATA_DELIMS)) {
            this.fScriptStripCDATADelims_ = state;
        } else if (featureId.equals(SCRIPT_STRIP_COMMENT_DELIMS)) {
            this.fScriptStripCommentDelims_ = state;
        } else if (featureId.equals(STYLE_STRIP_CDATA_DELIMS)) {
            this.fStyleStripCDATADelims_ = state;
        } else if (featureId.equals(STYLE_STRIP_COMMENT_DELIMS)) {
            this.fStyleStripCommentDelims_ = state;
        } else if (featureId.equals(PARSE_NOSCRIPT_CONTENT)) {
            this.fParseNoScriptContent_ = state;
        } else if (featureId.equals(ALLOW_SELFCLOSING_IFRAME)) {
            this.fAllowSelfclosingIframe_ = state;
        } else if (featureId.equals(ALLOW_SELFCLOSING_TAGS)) {
            this.fAllowSelfclosingTags_ = state;
        }
    }

    @Override
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

    @Override
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
                if (this.fReportErrors_) {
                    this.fErrorReporter.reportWarning("HTML1000", null);
                }
            }
            if (encodings[1] == null) {
                encodings[1] = EncodingMap.getIANA2JavaMapping(encodings[0].toUpperCase(Locale.ROOT));
                if (encodings[1] == null) {
                    encodings[1] = encodings[0];
                    if (this.fReportErrors_) {
                        this.fErrorReporter.reportWarning("HTML1001", new Object[]{encodings[0]});
                    }
                }
            }
            this.fIANAEncoding = encodings[0];
            this.fJavaEncoding = encodings[1];
            encoding = this.fIANAEncoding;
            reader = new InputStreamReader((InputStream)this.fByteStream, this.fJavaEncoding);
        }
        this.fCurrentEntity = new CurrentEntity(reader, encoding, publicId, baseSystemId, literalSystemId, expandedSystemId);
        this.setScanner(this.fContentScanner);
        this.setScannerState((short)10);
    }

    @Override
    public boolean scanDocument(boolean complete) throws XNIException, IOException {
        do {
            if (this.fScanner.scan(complete)) continue;
            return false;
        } while (complete);
        return true;
    }

    @Override
    public void setDocumentHandler(XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    protected static String getValue(XMLAttributes attrs, String aname) {
        if (attrs != null) {
            int length = attrs.getLength();
            for (int i = 0; i < length; ++i) {
                if (!attrs.getQName(i).equalsIgnoreCase(aname)) continue;
                return attrs.getValue(i);
            }
        }
        return null;
    }

    public static String expandSystemId(String systemId, String baseSystemId) {
        if (systemId == null || systemId.length() == 0) {
            return systemId;
        }
        try {
            new URI(systemId);
            return systemId;
        }
        catch (URI.MalformedURIException malformedURIException) {
            String id = HTMLScanner.fixURI(systemId);
            URI uri = null;
            try {
                URI base;
                block17: {
                    if (baseSystemId == null || baseSystemId.length() == 0 || baseSystemId.equals(systemId)) {
                        String dir;
                        try {
                            dir = HTMLScanner.fixURI(System.getProperty("user.dir")).replaceAll(" ", "%20");
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
                                dir = HTMLScanner.fixURI(System.getProperty("user.dir")).replaceAll(" ", "%20");
                            }
                            catch (SecurityException se) {
                                dir = "";
                            }
                            if (baseSystemId.indexOf(58) != -1) {
                                base = new URI("file", "", HTMLScanner.fixURI(baseSystemId), null, null);
                                break block17;
                            }
                            if (!dir.endsWith("/")) {
                                dir = dir + "/";
                            }
                            dir = dir + HTMLScanner.fixURI(baseSystemId);
                            base = new URI("file", "", dir, null, null);
                        }
                    }
                }
                uri = new URI(base, id);
            }
            catch (URI.MalformedURIException malformedURIException2) {
                // empty catch block
            }
            if (uri == null) {
                return systemId;
            }
            return uri.toString();
        }
    }

    protected static String fixURI(String str) {
        if ((str = str.replace(File.separatorChar, '/')).length() >= 2) {
            char ch1 = str.charAt(1);
            if (ch1 == ':') {
                char ch0 = String.valueOf(str.charAt(0)).toUpperCase(Locale.ROOT).charAt(0);
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    str = "/" + str;
                }
            } else if (ch1 == '/' && str.charAt(0) == '/') {
                str = "file:" + str;
            }
        }
        return str;
    }

    protected static String modifyName(String name, short mode) {
        switch (mode) {
            case 1: {
                return name.toUpperCase(Locale.ROOT);
            }
            case 2: {
                return name.toLowerCase(Locale.ROOT);
            }
        }
        return name;
    }

    protected static short getNamesValue(String value) {
        if ("lower".equals(value)) {
            return 2;
        }
        if ("upper".equals(value)) {
            return 1;
        }
        return 0;
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
                if (this.fReportErrors_) {
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
            if (this.fOverrideDoctype_) {
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
                if (this.fReportErrors_) {
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
        if (this.fCurrentEntity.offset_ == this.fCurrentEntity.length_ && this.fCurrentEntity.load(0) == -1) {
            return null;
        }
        int offset = this.fCurrentEntity.offset_;
        while (true) {
            if (this.fCurrentEntity.hasNext()) {
                char c = this.fCurrentEntity.getNextChar();
                if ((!strict || Character.isLetterOrDigit(c) || c == '-' || c == '.' || c == ':' || c == '_') && (strict || !Character.isWhitespace(c) && c != '=' && c != '/' && c != '>')) continue;
                this.fCurrentEntity.rewind();
            }
            if (this.fCurrentEntity.offset_ != this.fCurrentEntity.length_) break;
            length = this.fCurrentEntity.length_ - offset;
            System.arraycopy(this.fCurrentEntity.buffer_, offset, this.fCurrentEntity.buffer_, 0, length);
            int count = this.fCurrentEntity.load(length);
            offset = 0;
            if (count == -1) break;
        }
        String name = (length = this.fCurrentEntity.offset_ - offset) > 0 ? new String(this.fCurrentEntity.buffer_, offset, length) : null;
        return name;
    }

    protected int scanEntityRef(XMLString str, boolean content) throws IOException {
        str.clear();
        str.append('&');
        int nextChar = this.readPreservingBufferContent();
        if (nextChar == -1) {
            return this.returnEntityRefString(str, content);
        }
        str.append((char)nextChar);
        HTMLEntitiesParser parser = new HTMLEntitiesParser();
        if (35 == nextChar) {
            nextChar = this.readPreservingBufferContent();
            if (nextChar != -1) {
                str.append((char)nextChar);
            }
            while (nextChar != -1 && parser.parseNumeric(nextChar)) {
                nextChar = this.readPreservingBufferContent();
                if (nextChar == -1) continue;
                str.append((char)nextChar);
            }
            String match = parser.getMatch();
            if (match == null) {
                String consumed = str.toString();
                this.fCurrentEntity.rewind(consumed.length() - 1);
                str.clear();
                str.append('&');
            } else {
                this.fCurrentEntity.rewind(parser.getRewindCount());
                str.clear();
                str.append(match);
            }
            return this.returnEntityRefString(str, content);
        }
        while (nextChar != -1 && parser.parse(nextChar)) {
            nextChar = this.readPreservingBufferContent();
            if (nextChar == -1) continue;
            str.append((char)nextChar);
        }
        String match = parser.getMatch();
        if (match == null) {
            String consumed = str.toString();
            this.fCurrentEntity.rewind(consumed.length() - 1);
            str.clear();
            str.append('&');
        } else {
            this.fCurrentEntity.rewind(parser.getRewindCount());
            if (parser.endsWithSemicolon()) {
                str.clear();
                str.append(match);
            } else {
                if (this.fReportErrors_) {
                    this.fErrorReporter.reportWarning("HTML1004", null);
                }
                if (content) {
                    str.clear();
                    str.append(match);
                } else {
                    String consumed = str.toString();
                    int matchLength = parser.getMatchLength() + 1;
                    if (matchLength < consumed.length()) {
                        nextChar = consumed.charAt(matchLength);
                        if (61 == nextChar || 48 <= nextChar && nextChar <= 57 || 65 <= nextChar && nextChar <= 90 || 97 <= nextChar && nextChar <= 122) {
                            str.clear();
                            str.append(consumed.substring(0, parser.getMatchLength() + 1));
                        } else {
                            str.clear();
                            str.append(match);
                        }
                    } else {
                        str.clear();
                        str.append(match);
                    }
                }
            }
        }
        return this.returnEntityRefString(str, content);
    }

    private int returnEntityRefString(XMLString str, boolean content) {
        if (content && this.fDocumentHandler != null && this.fElementCount >= this.fElementDepth) {
            this.fEndLineNumber = this.fCurrentEntity.getLineNumber();
            this.fEndColumnNumber = this.fCurrentEntity.getColumnNumber();
            this.fEndCharacterOffset = this.fCurrentEntity.getCharacterOffset();
            this.fDocumentHandler.characters(str, this.locationAugs());
        }
        return -1;
    }

    protected boolean skip(String s, boolean caseSensitive) throws IOException {
        int length = s != null ? s.length() : 0;
        for (int i = 0; i < length; ++i) {
            if (this.fCurrentEntity.offset_ == this.fCurrentEntity.length_) {
                System.arraycopy(this.fCurrentEntity.buffer_, this.fCurrentEntity.offset_ - i, this.fCurrentEntity.buffer_, 0, i);
                if (this.fCurrentEntity.load(i) == -1) {
                    this.fCurrentEntity.offset_ = 0;
                    return false;
                }
            }
            char c0 = s.charAt(i);
            char c1 = this.fCurrentEntity.getNextChar();
            if (!caseSensitive) {
                c0 = String.valueOf(c0).toUpperCase(Locale.ROOT).charAt(0);
                c1 = String.valueOf(c1).toUpperCase(Locale.ROOT).charAt(0);
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
        block0: while (this.fCurrentEntity.offset_ != this.fCurrentEntity.length_ || this.fCurrentEntity.load(0) != -1) {
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
                    if (this.fCurrentEntity.offset_ == this.fCurrentEntity.length_ && this.fCurrentEntity.load(0) == -1) break block0;
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
        while (this.fCurrentEntity.offset_ != this.fCurrentEntity.length_ || this.fCurrentEntity.load(0) != -1) {
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
                    if (this.fCurrentEntity.offset_ == this.fCurrentEntity.length_) {
                        this.fCurrentEntity.offset_ = newlines;
                        if (this.fCurrentEntity.load(newlines) == -1) break;
                    }
                    if (this.fCurrentEntity.getCurrentChar() != '\n') continue;
                    this.fCurrentEntity.offset_++;
                    this.fCurrentEntity.characterOffset_++;
                    continue;
                }
                if (c == '\n') {
                    ++newlines;
                    if (this.fCurrentEntity.offset_ != this.fCurrentEntity.length_) continue;
                    this.fCurrentEntity.offset_ = newlines;
                    if (this.fCurrentEntity.load(newlines) != -1) continue;
                    break;
                }
                this.fCurrentEntity.rewind();
                break;
            } while (this.fCurrentEntity.offset_ < this.fCurrentEntity.length_ - 1);
            this.fCurrentEntity.incLine(newlines);
        }
        return newlines;
    }

    protected final Augmentations locationAugs() {
        if (this.fAugmentations_) {
            return new LocationItem(this.fBeginLineNumber, this.fBeginColumnNumber, this.fBeginCharacterOffset, this.fEndLineNumber, this.fEndColumnNumber, this.fEndCharacterOffset);
        }
        return null;
    }

    protected final Augmentations synthesizedAugs() {
        if (this.fAugmentations_) {
            return SYNTHESIZED_ITEM;
        }
        return null;
    }

    protected static boolean builtinXmlRef(String name) {
        return "amp".equals(name) || "lt".equals(name) || "gt".equals(name) || "quot".equals(name) || "apos".equals(name);
    }

    private void appendChar(XMLString str, int value, String name) {
        if (value > 65535) {
            try {
                char[] chars = Character.toChars(value);
                str.append(chars, 0, chars.length);
            }
            catch (IllegalArgumentException e) {
                if (this.fReportErrors_) {
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
                if (this.fReportErrors_) {
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
                return HTMLScanner.canRoundtrip(encoding1, encoding2);
            }
            catch (UnsupportedOperationException e) {
                try {
                    return HTMLScanner.canRoundtrip(encoding2, encoding1);
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

    private static boolean canRoundtrip(String encodeCharset, String decodeCharset) throws UnsupportedEncodingException {
        String reference = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=";
        byte[] bytesEncoding1 = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=".getBytes(encodeCharset);
        String referenceWithEncoding2 = new String(bytesEncoding1, decodeCharset);
        return "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=".equals(referenceWithEncoding2);
    }

    protected int readPreservingBufferContent() throws IOException {
        if (this.fCurrentEntity.offset_ == this.fCurrentEntity.length_ && this.fCurrentEntity.load(this.fCurrentEntity.length_) < 1) {
            return -1;
        }
        char c = this.fCurrentEntity.getNextChar();
        return c;
    }

    private boolean endCommentAvailable() throws IOException {
        int nbCaret = 0;
        int originalOffset = this.fCurrentEntity.offset_;
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
            if (c == 33 && nbCaret >= 2) continue;
            if (c == 45) {
                ++nbCaret;
                continue;
            }
            nbCaret = 0;
        }
    }

    private static final class LocationItem
    implements HTMLEventInfo {
        private final int beginLineNumber_;
        private final int beginColumnNumber_;
        private final int beginCharacterOffset_;
        private final int endLineNumber_;
        private final int endColumnNumber_;
        private final int endCharacterOffset_;

        LocationItem(int beginLine, int beginColumn, int beginOffset, int endLine, int endColumn, int endOffset) {
            this.beginLineNumber_ = beginLine;
            this.beginColumnNumber_ = beginColumn;
            this.beginCharacterOffset_ = beginOffset;
            this.endLineNumber_ = endLine;
            this.endColumnNumber_ = endColumn;
            this.endCharacterOffset_ = endOffset;
        }

        @Override
        public int getBeginLineNumber() {
            return this.beginLineNumber_;
        }

        @Override
        public int getBeginColumnNumber() {
            return this.beginColumnNumber_;
        }

        @Override
        public int getBeginCharacterOffset() {
            return this.beginCharacterOffset_;
        }

        @Override
        public int getEndLineNumber() {
            return this.endLineNumber_;
        }

        @Override
        public int getEndColumnNumber() {
            return this.endColumnNumber_;
        }

        @Override
        public int getEndCharacterOffset() {
            return this.endCharacterOffset_;
        }

        @Override
        public boolean isSynthesized() {
            return false;
        }

        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append(this.beginLineNumber_);
            str.append(':');
            str.append(this.beginColumnNumber_);
            str.append(':');
            str.append(this.beginCharacterOffset_);
            str.append(':');
            str.append(this.endLineNumber_);
            str.append(':');
            str.append(this.endColumnNumber_);
            str.append(':');
            str.append(this.endCharacterOffset_);
            return str.toString();
        }
    }

    private static final class PlaybackInputStream
    extends FilterInputStream {
        private static final boolean DEBUG_PLAYBACK = false;
        private boolean playback_ = false;
        private boolean cleared_ = false;
        private boolean detected_ = false;
        private byte[] byteBuffer_ = new byte[1024];
        private int byteOffset_ = 0;
        private int byteLength_ = 0;
        private int pushbackOffset_ = 0;
        private int pushbackLength_ = 0;

        PlaybackInputStream(InputStream in) {
            super(in);
        }

        public void detectEncoding(String[] encodings) throws IOException {
            if (this.detected_) {
                throw new IOException("Should not detect encoding twice.");
            }
            this.detected_ = true;
            int b1 = this.read();
            if (b1 == -1) {
                return;
            }
            int b2 = this.read();
            if (b2 == -1) {
                this.pushbackLength_ = 1;
                return;
            }
            if (b1 == 239 && b2 == 187) {
                int b3 = this.read();
                if (b3 == 191) {
                    this.pushbackOffset_ = 3;
                    encodings[0] = "UTF-8";
                    encodings[1] = "UTF8";
                    return;
                }
                this.pushbackLength_ = 3;
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
            this.pushbackLength_ = 2;
        }

        public void playback() {
            this.playback_ = true;
        }

        public void clear() {
            if (!this.playback_) {
                this.cleared_ = true;
                this.byteBuffer_ = null;
            }
        }

        @Override
        public int read() throws IOException {
            if (this.pushbackOffset_ < this.pushbackLength_) {
                return this.byteBuffer_[this.pushbackOffset_++];
            }
            if (this.cleared_) {
                return this.in.read();
            }
            if (this.playback_) {
                byte c = this.byteBuffer_[this.byteOffset_++];
                if (this.byteOffset_ == this.byteLength_) {
                    this.cleared_ = true;
                    this.byteBuffer_ = null;
                }
                return c;
            }
            int c = this.in.read();
            if (c != -1) {
                if (this.byteLength_ == this.byteBuffer_.length) {
                    byte[] newarray = new byte[this.byteLength_ + 1024];
                    System.arraycopy(this.byteBuffer_, 0, newarray, 0, this.byteLength_);
                    this.byteBuffer_ = newarray;
                }
                this.byteBuffer_[this.byteLength_++] = (byte)c;
            }
            return c;
        }

        @Override
        public int read(byte[] array) throws IOException {
            return this.read(array, 0, array.length);
        }

        @Override
        public int read(byte[] array, int offset, int length) throws IOException {
            if (this.pushbackOffset_ < this.pushbackLength_) {
                int count = this.pushbackLength_ - this.pushbackOffset_;
                if (count > length) {
                    count = length;
                }
                System.arraycopy(this.byteBuffer_, this.pushbackOffset_, array, offset, count);
                this.pushbackOffset_ += count;
                return count;
            }
            if (this.cleared_) {
                return this.in.read(array, offset, length);
            }
            if (this.playback_) {
                if (this.byteOffset_ + length > this.byteLength_) {
                    length = this.byteLength_ - this.byteOffset_;
                }
                System.arraycopy(this.byteBuffer_, this.byteOffset_, array, offset, length);
                this.byteOffset_ += length;
                if (this.byteOffset_ == this.byteLength_) {
                    this.cleared_ = true;
                    this.byteBuffer_ = null;
                }
                return length;
            }
            int count = this.in.read(array, offset, length);
            if (count != -1) {
                if (this.byteLength_ + count > this.byteBuffer_.length) {
                    byte[] newarray = new byte[this.byteLength_ + count + 512];
                    System.arraycopy(this.byteBuffer_, 0, newarray, 0, this.byteLength_);
                    this.byteBuffer_ = newarray;
                }
                System.arraycopy(array, offset, this.byteBuffer_, this.byteLength_, count);
                this.byteLength_ += count;
            }
            return count;
        }
    }

    public class PlainTextScanner
    implements Scanner {
        private final XMLString xmlString_ = new XMLString();

        @Override
        public boolean scan(boolean complete) throws IOException {
            this.scanCharacters(this.xmlString_);
            return false;
        }

        protected void scanCharacters(XMLString buffer) throws IOException {
            int c;
            while ((c = HTMLScanner.this.fCurrentEntity.read()) != -1) {
                HTMLScanner.this.appendChar(buffer, c, null);
                if (c != 10) continue;
                HTMLScanner.this.fCurrentEntity.incLine();
            }
            if (buffer.length() > 0 && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters(buffer, HTMLScanner.this.locationAugs());
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
        private final QName fQName_ = new QName();
        private final XMLString xmlString_ = new XMLString();

        public Scanner setElementName(String ename) {
            this.fElementName = ename;
            this.fStyle = "STYLE".equalsIgnoreCase(this.fElementName);
            this.fTextarea = "TEXTAREA".equalsIgnoreCase(this.fElementName);
            this.fTitle = "TITLE".equalsIgnoreCase(this.fElementName);
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
                            if (c == -1) {
                                if (HTMLScanner.this.fReportErrors_) {
                                    HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                                }
                                throw new EOFException();
                            }
                            if (c == 60) {
                                HTMLScanner.this.setScannerState((short)1);
                                break;
                            }
                            if (c == 38) {
                                if (this.fTextarea || this.fTitle) {
                                    HTMLScanner.this.scanEntityRef(this.xmlString_, true);
                                    break;
                                }
                                this.xmlString_.clear();
                                this.xmlString_.append('&');
                            } else {
                                HTMLScanner.this.fCurrentEntity.rewind();
                                this.xmlString_.clear();
                            }
                            this.scanCharacters(this.xmlString_, -1);
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
                                                this.fQName_.setValues(null, ename, ename, null);
                                                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                                                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                                                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                                                HTMLScanner.this.fDocumentHandler.endElement(this.fQName_, HTMLScanner.this.locationAugs());
                                            }
                                            HTMLScanner.this.setScanner(HTMLScanner.this.fContentScanner);
                                            HTMLScanner.this.setScannerState((short)0);
                                            return true;
                                        }
                                        HTMLScanner.this.fCurrentEntity.rewind();
                                    }
                                    this.xmlString_.clear();
                                    this.xmlString_.append("</");
                                    this.xmlString_.append(ename);
                                } else {
                                    this.xmlString_.clear();
                                    this.xmlString_.append("</");
                                }
                            } else {
                                this.xmlString_.clear();
                                this.xmlString_.append('<');
                                HTMLScanner.this.appendChar(this.xmlString_, c, null);
                            }
                            this.scanCharacters(this.xmlString_, -1);
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
                        HTMLScanner.this.fCurrentEntity = HTMLScanner.this.fCurrentEntityStack.pop();
                        HTMLScanner.this.setScannerState((short)0);
                    }
                    return true;
                }
            } while (next || complete);
            return true;
        }

        protected void scanCharacters(XMLString buffer, int delimiter) throws IOException {
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
                if (HTMLScanner.this.fStyleStripCommentDelims_) {
                    buffer.reduceToContent("<!--", "-->");
                }
                if (HTMLScanner.this.fStyleStripCDATADelims_) {
                    buffer.reduceToContent("<![CDATA[", "]]>");
                }
            }
            if (buffer.length() > 0 && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters(buffer, HTMLScanner.this.locationAugs());
            }
        }
    }

    public class ContentScanner
    implements Scanner {
        private final QName qName_ = new QName();
        private final XMLAttributesImpl attributes_ = new XMLAttributesImpl();

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
                            if (c == -1) {
                                throw new EOFException();
                            }
                            if (c == 60) {
                                HTMLScanner.this.setScannerState((short)1);
                                next = true;
                                break;
                            }
                            if (c == 38) {
                                HTMLScanner.this.scanEntityRef(HTMLScanner.this.fStringBuffer, true);
                                break;
                            }
                            HTMLScanner.this.fCurrentEntity.rewind();
                            this.scanCharacters();
                            break;
                        }
                        case 1: {
                            int c = HTMLScanner.this.fCurrentEntity.read();
                            if (c == -1) {
                                if (HTMLScanner.this.fReportErrors_) {
                                    HTMLScanner.this.fErrorReporter.reportError("HTML1003", null);
                                }
                                if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                                    HTMLScanner.this.fStringBuffer.clear();
                                    HTMLScanner.this.fStringBuffer.append('<');
                                    HTMLScanner.this.fDocumentHandler.characters(HTMLScanner.this.fStringBuffer, null);
                                }
                                throw new EOFException();
                            }
                            if (c == 33) {
                                if (HTMLScanner.this.skip("--->", false) || HTMLScanner.this.skip("-->", false) || HTMLScanner.this.skip("->", false) || HTMLScanner.this.skip(">", false)) {
                                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                                    HTMLScanner.this.fDocumentHandler.comment(new XMLString(), HTMLScanner.this.locationAugs());
                                } else if (HTMLScanner.this.skip("--", false)) {
                                    this.scanComment();
                                } else if (HTMLScanner.this.skip("[CDATA[", false)) {
                                    this.scanCDATA();
                                } else if (HTMLScanner.this.skip("DOCTYPE", false)) {
                                    HTMLScanner.this.scanDoctype();
                                } else {
                                    if (HTMLScanner.this.fReportErrors_) {
                                        HTMLScanner.this.fErrorReporter.reportError("HTML1002", null);
                                    }
                                    HTMLScanner.this.skipMarkup(true);
                                }
                            } else if (c == 63) {
                                this.scanPI();
                            } else if (c == 47) {
                                this.scanEndElement();
                            } else {
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
                                } else if (!HTMLScanner.this.fAllowSelfclosingTags_ && !HTMLScanner.this.fAllowSelfclosingIframe_ && "iframe".equals(enameLC)) {
                                    this.scanUntilEndTag("iframe");
                                } else if (!HTMLScanner.this.fParseNoScriptContent_ && "noscript".equals(enameLC)) {
                                    this.scanUntilEndTag("noscript");
                                } else if ("noframes".equals(enameLC)) {
                                    this.scanUntilEndTag("noframes");
                                } else if ("noembed".equals(enameLC)) {
                                    this.scanUntilEndTag("noembed");
                                } else if (ename != null && HTMLScanner.this.htmlConfiguration_.getHtmlElements().getElement(enameLC).isSpecial() && (!"title".equals(enameLC) || this.isEnded(enameLC))) {
                                    if ("plaintext".equals(enameLC)) {
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
                                HTMLScanner.this.fDocumentHandler.startDocument(locator, encoding, nscontext, augs);
                            }
                            if (HTMLScanner.this.fInsertDoctype_ && HTMLScanner.this.fDocumentHandler != null) {
                                String root = ((HTMLScanner)HTMLScanner.this).htmlConfiguration_.getHtmlElements().getElement((short)59).name;
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
                        HTMLScanner.this.fCurrentEntity = HTMLScanner.this.fCurrentEntityStack.pop();
                    }
                    next = true;
                }
            } while (next || complete);
            return true;
        }

        private void scanUntilEndTag(String tagName) throws IOException {
            int c;
            XMLString xmlString = new XMLString();
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
                        xmlString.append('\n');
                    }
                    continue;
                }
                HTMLScanner.this.appendChar(xmlString, c, null);
            }
            if (xmlString.length() > 0 && HTMLScanner.this.fDocumentHandler != null) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters(xmlString, HTMLScanner.this.locationAugs());
            }
        }

        private void scanScriptContent() throws IOException {
            int c;
            XMLString xmlString = new XMLString();
            boolean waitForEndComment = false;
            boolean invalidComment = false;
            while ((c = HTMLScanner.this.fCurrentEntity.read()) != -1) {
                if (c == 45 && xmlString.endsWith("<!-")) {
                    waitForEndComment = HTMLScanner.this.endCommentAvailable();
                } else if (!waitForEndComment && c == 60) {
                    String next = this.nextContent(8) + " ";
                    if (next.length() >= 8 && "/script".equalsIgnoreCase(next.substring(0, 7)) && ('>' == next.charAt(7) || Character.isWhitespace(next.charAt(7)))) {
                        HTMLScanner.this.fCurrentEntity.rewind();
                        break;
                    }
                } else if (c == 62) {
                    if (xmlString.endsWith("--")) {
                        waitForEndComment = false;
                    }
                    if (xmlString.endsWith("--!")) {
                        invalidComment = true;
                        waitForEndComment = false;
                    }
                }
                if (c == 13 || c == 10) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    int newlines = HTMLScanner.this.skipNewlines();
                    for (int i = 0; i < newlines; ++i) {
                        xmlString.append('\n');
                    }
                    continue;
                }
                HTMLScanner.this.appendChar(xmlString, c, null);
            }
            if (HTMLScanner.this.fScriptStripCommentDelims_) {
                if (invalidComment) {
                    xmlString.reduceToContent("<!--", "--!>");
                } else {
                    xmlString.reduceToContent("<!--", "-->");
                }
            }
            if (HTMLScanner.this.fScriptStripCDATADelims_) {
                xmlString.reduceToContent("<![CDATA[", "]]>");
            }
            if (xmlString.length() > 0 && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.characters(xmlString, HTMLScanner.this.locationAugs());
            }
        }

        protected String nextContent(int len) throws IOException {
            int nbRead;
            int originalOffset = HTMLScanner.this.fCurrentEntity.offset_;
            int originalColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
            int originalCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
            char[] buff = new char[len];
            for (nbRead = 0; nbRead < len; ++nbRead) {
                int c;
                if (HTMLScanner.this.fCurrentEntity.offset_ == HTMLScanner.this.fCurrentEntity.length_) {
                    if (HTMLScanner.this.fCurrentEntity.length_ != HTMLScanner.this.fCurrentEntity.buffer_.length) break;
                    HTMLScanner.this.fCurrentEntity.load(HTMLScanner.this.fCurrentEntity.buffer_.length);
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
            while ((newlines = HTMLScanner.this.skipNewlines()) != 0 || HTMLScanner.this.fCurrentEntity.offset_ != HTMLScanner.this.fCurrentEntity.length_) {
                int next;
                int offset;
                for (int i = offset = HTMLScanner.this.fCurrentEntity.offset_ - newlines; i < HTMLScanner.this.fCurrentEntity.offset_; ++i) {
                    ((CurrentEntity)HTMLScanner.this.fCurrentEntity).buffer_[i] = 10;
                }
                while (HTMLScanner.this.fCurrentEntity.hasNext()) {
                    char c = HTMLScanner.this.fCurrentEntity.getNextChar();
                    if (c != '<' && c != '&' && c != '\n' && c != '\r') continue;
                    HTMLScanner.this.fCurrentEntity.rewind();
                    break;
                }
                if (HTMLScanner.this.fCurrentEntity.offset_ > offset && HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fStringBuffer.append(HTMLScanner.this.fCurrentEntity.buffer_, offset, HTMLScanner.this.fCurrentEntity.offset_ - offset);
                }
                boolean hasNext = HTMLScanner.this.fCurrentEntity.offset_ < HTMLScanner.this.fCurrentEntity.buffer_.length;
                int n = next = hasNext ? (int)HTMLScanner.this.fCurrentEntity.getCurrentChar() : -1;
                if (next != 38 && next != 60 && next != -1) continue;
                break;
            }
            if (HTMLScanner.this.fStringBuffer.length() != 0) {
                HTMLScanner.this.fDocumentHandler.characters(HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
            }
        }

        protected void scanCDATA() throws IOException {
            HTMLScanner.this.fStringBuffer.clear();
            if (HTMLScanner.this.fCDATASections_) {
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
            if (!HTMLScanner.this.fCDATASections_) {
                HTMLScanner.this.fStringBuffer.append("]]");
            }
            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                if (HTMLScanner.this.fCDATASections_) {
                    HTMLScanner.this.fDocumentHandler.characters(HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
                    HTMLScanner.this.fDocumentHandler.endCDATA(HTMLScanner.this.locationAugs());
                } else {
                    HTMLScanner.this.fDocumentHandler.comment(HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
                }
            }
            if (eof) {
                throw new EOFException();
            }
        }

        protected void scanComment() throws IOException {
            boolean eof;
            XMLString xmlString;
            block8: {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                xmlString = new XMLString();
                eof = this.scanMarkupContent(xmlString, '-');
                if (eof) {
                    HTMLScanner.this.fCurrentEntity.resetBuffer(xmlString, HTMLScanner.this.fEndLineNumber, HTMLScanner.this.fEndColumnNumber, HTMLScanner.this.fEndCharacterOffset);
                    xmlString = new XMLString();
                    block0: while (true) {
                        int c;
                        if ((c = HTMLScanner.this.fCurrentEntity.read()) == -1) {
                            if (HTMLScanner.this.fReportErrors_) {
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
                                xmlString.append('\n');
                                ++i;
                            }
                        }
                        if (c == 62) break;
                        HTMLScanner.this.appendChar(xmlString, c, null);
                    }
                    eof = false;
                }
            }
            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                HTMLScanner.this.fDocumentHandler.comment(xmlString, HTMLScanner.this.locationAugs());
            }
            if (eof) {
                throw new EOFException();
            }
        }

        protected boolean scanMarkupContent(XMLString xmlString, char cend) throws IOException {
            int c;
            block0: while (true) {
                int i;
                if ((c = HTMLScanner.this.fCurrentEntity.read()) == -1) {
                    if (!HTMLScanner.this.fReportErrors_) break;
                    HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                    break;
                }
                if (c == cend) {
                    int count = 1;
                    while ((c = HTMLScanner.this.fCurrentEntity.read()) == cend) {
                        ++count;
                    }
                    if (c == -1) {
                        if (!HTMLScanner.this.fReportErrors_) break;
                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                        break;
                    }
                    if (count < 2) {
                        xmlString.append(cend);
                        HTMLScanner.this.fCurrentEntity.rewind();
                        continue;
                    }
                    if (c != 62) {
                        for (i = 0; i < count; ++i) {
                            xmlString.append(cend);
                        }
                        HTMLScanner.this.fCurrentEntity.rewind();
                        continue;
                    }
                    for (i = 0; i < count - 2; ++i) {
                        xmlString.append(cend);
                    }
                    break;
                }
                if (c == 10 || c == 13) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    int newlines = HTMLScanner.this.skipNewlines();
                    i = 0;
                    while (true) {
                        if (i >= newlines) continue block0;
                        xmlString.append('\n');
                        ++i;
                    }
                }
                HTMLScanner.this.appendChar(xmlString, c, null);
            }
            return c == -1;
        }

        protected void scanPI() throws IOException {
            String target;
            if (HTMLScanner.this.fReportErrors_) {
                HTMLScanner.this.fErrorReporter.reportWarning("HTML1008", null);
            }
            if ((target = HTMLScanner.this.scanName(true)) != null && !"xml".equalsIgnoreCase(target)) {
                int c;
                while ((c = HTMLScanner.this.fCurrentEntity.read()) != -1) {
                    if (c == 13 || c == 10) {
                        if (c == 13) {
                            c = HTMLScanner.this.fCurrentEntity.read();
                            if (c == -1) break;
                            if (c != 10) {
                                HTMLScanner.this.fCurrentEntity.offset_--;
                                HTMLScanner.this.fCurrentEntity.characterOffset_--;
                            }
                        }
                        HTMLScanner.this.fCurrentEntity.incLine();
                        continue;
                    }
                    if (c == 32 || c == 9) continue;
                    HTMLScanner.this.fCurrentEntity.rewind();
                    break;
                }
                HTMLScanner.this.fStringBuffer.clear();
                while ((c = HTMLScanner.this.fCurrentEntity.read()) != -1) {
                    if (c == 63 || c == 47) {
                        char c0 = (char)c;
                        c = HTMLScanner.this.fCurrentEntity.read();
                        if (c == -1 || c == 62) break;
                        HTMLScanner.this.fStringBuffer.append(c0);
                        HTMLScanner.this.fCurrentEntity.rewind();
                        continue;
                    }
                    if (c == 13 || c == 10) {
                        HTMLScanner.this.fStringBuffer.append('\n');
                        if (c == 13) {
                            c = HTMLScanner.this.fCurrentEntity.read();
                            if (c == -1) break;
                            if (c != 10) {
                                HTMLScanner.this.fCurrentEntity.offset_--;
                                HTMLScanner.this.fCurrentEntity.characterOffset_--;
                            }
                        }
                        HTMLScanner.this.fCurrentEntity.incLine();
                        continue;
                    }
                    if (c == 62) {
                        if (HTMLScanner.this.fDocumentHandler != null) {
                            HTMLScanner.this.fStringBuffer.append(target);
                            HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                            HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                            HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                            HTMLScanner.this.fDocumentHandler.comment(HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
                        }
                        return;
                    }
                    HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, c, null);
                }
                if (HTMLScanner.this.fDocumentHandler != null) {
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fDocumentHandler.processingInstruction(target, HTMLScanner.this.fStringBuffer, HTMLScanner.this.locationAugs());
                }
            } else {
                int beginLineNumber = HTMLScanner.this.fBeginLineNumber;
                int beginColumnNumber = HTMLScanner.this.fBeginColumnNumber;
                int beginCharacterOffset = HTMLScanner.this.fBeginCharacterOffset;
                this.attributes_.removeAllAttributes();
                int aindex = 0;
                while (this.scanPseudoAttribute(this.attributes_)) {
                    if (this.attributes_.getValue(aindex).length() == 0) {
                        this.attributes_.removeAttributeAt(aindex);
                        continue;
                    }
                    this.attributes_.getName(aindex, this.qName_);
                    this.qName_.rawname = this.qName_.rawname.toLowerCase(Locale.ROOT);
                    this.attributes_.setName(aindex, this.qName_);
                    ++aindex;
                }
                if (HTMLScanner.this.fDocumentHandler != null) {
                    boolean xmlDeclNow;
                    String version = this.attributes_.getValue("version");
                    String encoding = this.attributes_.getValue("encoding");
                    String standalone = this.attributes_.getValue("standalone");
                    boolean bl = xmlDeclNow = HTMLScanner.this.fIgnoreSpecifiedCharset_ || !this.changeEncoding(encoding);
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
                if (HTMLScanner.this.fReportErrors_) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1009", null);
                }
                if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    HTMLScanner.this.fStringBuffer.clear();
                    HTMLScanner.this.fStringBuffer.append('<');
                    if (length > 0) {
                        HTMLScanner.this.fStringBuffer.append(ename);
                    }
                    HTMLScanner.this.fDocumentHandler.characters(HTMLScanner.this.fStringBuffer, null);
                }
                return null;
            }
            ename = HTMLScanner.modifyName(ename, HTMLScanner.this.fNamesElems);
            this.attributes_.removeAllAttributes();
            int beginLineNumber = HTMLScanner.this.fBeginLineNumber;
            int beginColumnNumber = HTMLScanner.this.fBeginColumnNumber;
            int beginCharacterOffset = HTMLScanner.this.fBeginCharacterOffset;
            while (this.scanAttribute(this.attributes_, empty)) {
            }
            HTMLScanner.this.fBeginLineNumber = beginLineNumber;
            HTMLScanner.this.fBeginColumnNumber = beginColumnNumber;
            HTMLScanner.this.fBeginCharacterOffset = beginCharacterOffset;
            if (HTMLScanner.this.fByteStream != null && HTMLScanner.this.fElementDepth == -1) {
                if ("META".equalsIgnoreCase(ename) && !HTMLScanner.this.fIgnoreSpecifiedCharset_) {
                    String httpEquiv = HTMLScanner.getValue(this.attributes_, "http-equiv");
                    if (httpEquiv != null && "content-type".equalsIgnoreCase(httpEquiv)) {
                        int index1;
                        String content = HTMLScanner.getValue(this.attributes_, "content");
                        if (content != null && (index1 = (content = this.removeSpaces(content)).toLowerCase(Locale.ROOT).indexOf("charset=")) != -1) {
                            int index2 = content.indexOf(59, index1);
                            String charset = index2 != -1 ? content.substring(index1 + 8, index2) : content.substring(index1 + 8);
                            this.changeEncoding(charset);
                        }
                    } else {
                        String metaCharset = HTMLScanner.getValue(this.attributes_, "charset");
                        if (metaCharset != null) {
                            this.changeEncoding(metaCharset);
                        }
                    }
                } else if ("BODY".equalsIgnoreCase(ename)) {
                    HTMLScanner.this.fByteStream.clear();
                    HTMLScanner.this.fByteStream = null;
                } else {
                    HTMLElements.Element element = HTMLScanner.this.htmlConfiguration_.getHtmlElements().getElement(ename);
                    if (element.parent != null && element.parent.length > 0 && element.parent[0].code == 18) {
                        HTMLScanner.this.fByteStream.clear();
                        HTMLScanner.this.fByteStream = null;
                    }
                }
            }
            if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                this.qName_.setValues(null, ename, ename, null);
                HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                if (empty[0] && !"BR".equalsIgnoreCase(ename)) {
                    HTMLScanner.this.fDocumentHandler.emptyElement(this.qName_, this.attributes_, HTMLScanner.this.locationAugs());
                } else {
                    HTMLScanner.this.fDocumentHandler.startElement(this.qName_, this.attributes_, HTMLScanner.this.locationAugs());
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
                String javaEncoding = EncodingMap.getIANA2JavaMapping(charset.toUpperCase(Locale.ROOT));
                if (javaEncoding == null) {
                    javaEncoding = charset;
                    if (HTMLScanner.this.fReportErrors_) {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1001", new Object[]{charset});
                    }
                }
                if (!javaEncoding.equals(HTMLScanner.this.fJavaEncoding)) {
                    if (!HTMLScanner.this.isEncodingCompatible(javaEncoding, HTMLScanner.this.fJavaEncoding)) {
                        if (HTMLScanner.this.fReportErrors_) {
                            HTMLScanner.this.fErrorReporter.reportError("HTML1015", new Object[]{javaEncoding, HTMLScanner.this.fJavaEncoding});
                        }
                    } else {
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
                if (HTMLScanner.this.fReportErrors_) {
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
                if (HTMLScanner.this.fReportErrors_) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                }
                return false;
            }
            if (c == 62) {
                return false;
            }
            if (c == 60) {
                HTMLScanner.this.fCurrentEntity.rewind();
                if (HTMLScanner.this.fReportErrors_) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1016", null);
                }
                return false;
            }
            HTMLScanner.this.fCurrentEntity.rewind();
            String aname = HTMLScanner.this.scanName(false);
            if (aname == null) {
                if (HTMLScanner.this.fReportErrors_) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1011", null);
                }
                HTMLScanner.this.skipSpaces();
                if (!HTMLScanner.this.fCurrentEntity.hasNext() || '=' != HTMLScanner.this.fCurrentEntity.getNextChar()) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    empty[0] = HTMLScanner.this.skipMarkup(false);
                    return false;
                }
                aname = '=' + HTMLScanner.this.scanName(false);
            }
            if (!skippedSpaces && HTMLScanner.this.fReportErrors_) {
                HTMLScanner.this.fErrorReporter.reportError("HTML1013", new Object[]{aname});
            }
            aname = HTMLScanner.modifyName(aname, HTMLScanner.this.fNamesAttrs);
            HTMLScanner.this.skipSpaces();
            c = HTMLScanner.this.fCurrentEntity.read();
            if (c == -1) {
                if (HTMLScanner.this.fReportErrors_) {
                    HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                }
                throw new EOFException();
            }
            if (c == 47 || c == 62) {
                this.qName_.setValues(null, aname, aname, null);
                attributes.addAttribute(this.qName_, "CDATA", "");
                attributes.setSpecified(attributes.getLength() - 1, true);
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
                    if (HTMLScanner.this.fReportErrors_) {
                        HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                    }
                    throw new EOFException();
                }
                if (c == 62) {
                    this.qName_.setValues(null, aname, aname, null);
                    attributes.addAttribute(this.qName_, "CDATA", "");
                    attributes.setSpecified(attributes.getLength() - 1, true);
                    return false;
                }
                HTMLScanner.this.fStringBuffer.clear();
                if (c != 39 && c != 34) {
                    HTMLScanner.this.fCurrentEntity.rewind();
                    while (true) {
                        if ((c = HTMLScanner.this.fCurrentEntity.read()) == -1) {
                            if (HTMLScanner.this.fReportErrors_) {
                                HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                            }
                            throw new EOFException();
                        }
                        if (Character.isWhitespace((char)c) || c == 62) break;
                        if (c == 38) {
                            int ce = HTMLScanner.this.scanEntityRef(HTMLScanner.this.fStringBuffer2, false);
                            if (ce != -1) {
                                HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, ce, null);
                                continue;
                            }
                            HTMLScanner.this.fStringBuffer.append(HTMLScanner.this.fStringBuffer2);
                            continue;
                        }
                        HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, c, null);
                    }
                    HTMLScanner.this.fCurrentEntity.rewind();
                    this.qName_.setValues(null, aname, aname, null);
                    String avalue = HTMLScanner.this.fStringBuffer.toString();
                    attributes.addAttribute(this.qName_, "CDATA", avalue);
                    int lastattr = attributes.getLength() - 1;
                    attributes.setSpecified(lastattr, true);
                    return true;
                }
                char quote = (char)c;
                boolean isStart = true;
                boolean prevSpace = false;
                do {
                    boolean acceptSpace = !HTMLScanner.this.fNormalizeAttributes_ || !isStart && !prevSpace;
                    c = HTMLScanner.this.fCurrentEntity.read();
                    if (c == -1) {
                        if (HTMLScanner.this.fReportErrors_) {
                            HTMLScanner.this.fErrorReporter.reportError("HTML1007", null);
                        }
                        throw new EOFException();
                    }
                    if (c == 38) {
                        isStart = false;
                        int ce = HTMLScanner.this.scanEntityRef(HTMLScanner.this.fStringBuffer2, false);
                        if (ce != -1) {
                            HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, ce, null);
                        } else {
                            HTMLScanner.this.fStringBuffer.append(HTMLScanner.this.fStringBuffer2);
                        }
                    } else if (c == 32 || c == 9) {
                        if (acceptSpace) {
                            HTMLScanner.this.fStringBuffer.append((char)(HTMLScanner.this.fNormalizeAttributes_ ? 32 : (char)c));
                        }
                    } else if (c == 13 || c == 10) {
                        if (c == 13) {
                            int c2 = HTMLScanner.this.fCurrentEntity.read();
                            if (c2 == 10) {
                                c = c2;
                            } else if (c2 != -1) {
                                HTMLScanner.this.fCurrentEntity.rewind();
                            }
                        }
                        if (acceptSpace) {
                            HTMLScanner.this.fStringBuffer.append(HTMLScanner.this.fNormalizeAttributes_ ? (char)' ' : '\n');
                        }
                        HTMLScanner.this.fCurrentEntity.incLine();
                    } else if (c != quote) {
                        isStart = false;
                        HTMLScanner.this.appendChar(HTMLScanner.this.fStringBuffer, c, null);
                    }
                    prevSpace = c == 32 || c == 9 || c == 13 || c == 10;
                    boolean bl = isStart = isStart && prevSpace;
                } while (c != quote);
                if (HTMLScanner.this.fNormalizeAttributes_ && HTMLScanner.this.fStringBuffer.length() > 0) {
                    HTMLScanner.this.fStringBuffer.trimWhitespaceAtEnd();
                }
                this.qName_.setValues(null, aname, aname, null);
                String avalue = HTMLScanner.this.fStringBuffer.toString();
                attributes.addAttribute(this.qName_, "CDATA", avalue);
                int lastattr = attributes.getLength() - 1;
                attributes.setSpecified(lastattr, true);
            } else {
                this.qName_.setValues(null, aname, aname, null);
                attributes.addAttribute(this.qName_, "CDATA", "");
                attributes.setSpecified(attributes.getLength() - 1, true);
                HTMLScanner.this.fCurrentEntity.rewind();
            }
            return true;
        }

        protected void scanEndElement() throws IOException {
            String ename = HTMLScanner.this.scanName(true);
            if (HTMLScanner.this.fReportErrors_ && ename == null) {
                HTMLScanner.this.fErrorReporter.reportError("HTML1012", null);
            }
            HTMLScanner.this.skipMarkup(false);
            if (ename != null) {
                ename = HTMLScanner.modifyName(ename, HTMLScanner.this.fNamesElems);
                if (HTMLScanner.this.fDocumentHandler != null && HTMLScanner.this.fElementCount >= HTMLScanner.this.fElementDepth) {
                    this.qName_.setValues(null, ename, ename, null);
                    HTMLScanner.this.fEndLineNumber = HTMLScanner.this.fCurrentEntity.getLineNumber();
                    HTMLScanner.this.fEndColumnNumber = HTMLScanner.this.fCurrentEntity.getColumnNumber();
                    HTMLScanner.this.fEndCharacterOffset = HTMLScanner.this.fCurrentEntity.getCharacterOffset();
                    HTMLScanner.this.fDocumentHandler.endElement(this.qName_, HTMLScanner.this.locationAugs());
                }
            }
        }

        private boolean isEnded(String ename) {
            String content = new String(HTMLScanner.this.fCurrentEntity.buffer_, HTMLScanner.this.fCurrentEntity.offset_, HTMLScanner.this.fCurrentEntity.length_ - HTMLScanner.this.fCurrentEntity.offset_);
            return content.toLowerCase(Locale.ROOT).contains("</" + ename.toLowerCase(Locale.ROOT) + ">");
        }
    }

    public static final class CurrentEntity {
        private Reader stream_;
        private String encoding_;
        public final String publicId;
        public final String baseSystemId;
        public final String literalSystemId;
        public final String expandedSystemId;
        public final String version = "1.0";
        private int lineNumber_ = 1;
        private int columnNumber_ = 1;
        private int characterOffset_ = 0;
        private char[] buffer_ = new char[2048];
        private int offset_ = 0;
        private int length_ = 0;
        private boolean endReached_ = false;

        public CurrentEntity(Reader stream, String encoding, String publicId, String baseSystemId, String literalSystemId, String expandedSystemId) {
            this.stream_ = stream;
            this.encoding_ = encoding;
            this.publicId = publicId;
            this.baseSystemId = baseSystemId;
            this.literalSystemId = literalSystemId;
            this.expandedSystemId = expandedSystemId;
        }

        private char getCurrentChar() {
            return this.buffer_[this.offset_];
        }

        private char getNextChar() {
            ++this.characterOffset_;
            ++this.columnNumber_;
            return this.buffer_[this.offset_++];
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
            return this.offset_ < this.length_;
        }

        protected int load(int loadOffset) throws IOException {
            int count;
            if (loadOffset == this.buffer_.length) {
                int adjust = this.buffer_.length / 4;
                char[] array = new char[this.buffer_.length + adjust];
                System.arraycopy(this.buffer_, 0, array, 0, this.length_);
                this.buffer_ = array;
            }
            if ((count = this.stream_.read(this.buffer_, loadOffset, this.buffer_.length - loadOffset)) == -1) {
                this.endReached_ = true;
            }
            this.length_ = count != -1 ? count + loadOffset : loadOffset;
            this.offset_ = loadOffset;
            return count;
        }

        protected int read() throws IOException {
            if (this.offset_ == this.length_) {
                if (this.endReached_) {
                    return -1;
                }
                if (this.load(0) == -1) {
                    return -1;
                }
            }
            char c = this.buffer_[this.offset_++];
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
            this.offset_ = 0;
            this.length_ = 0;
            this.characterOffset_ = 0;
            this.lineNumber_ = 1;
            this.columnNumber_ = 1;
            this.encoding_ = inputStreamReader.getEncoding();
        }

        private void rewind() {
            --this.offset_;
            --this.characterOffset_;
            --this.columnNumber_;
        }

        private void rewind(int i) {
            this.offset_ -= i;
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

        private void resetBuffer(XMLString xmlBuffer, int lineNumber, int columnNumber, int characterOffset) {
            this.lineNumber_ = lineNumber;
            this.columnNumber_ = columnNumber;
            this.characterOffset_ = characterOffset;
            this.buffer_ = xmlBuffer.getChars();
            this.offset_ = 0;
            this.length_ = xmlBuffer.length();
        }

        private int getColumnNumber() {
            return this.columnNumber_;
        }

        private void restorePosition(int originalOffset, int originalColumnNumber, int originalCharacterOffset) {
            this.offset_ = originalOffset;
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

