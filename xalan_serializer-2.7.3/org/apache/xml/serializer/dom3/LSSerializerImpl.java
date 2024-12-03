/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer.dom3;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.xml.serializer.DOM3Serializer;
import org.apache.xml.serializer.Encodings;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.apache.xml.serializer.dom3.DOMErrorImpl;
import org.apache.xml.serializer.dom3.DOMStringListImpl;
import org.apache.xml.serializer.utils.SystemIDResolver;
import org.apache.xml.serializer.utils.Utils;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;

public final class LSSerializerImpl
implements DOMConfiguration,
LSSerializer {
    private static final String DEFAULT_END_OF_LINE;
    private Serializer fXMLSerializer = null;
    protected int fFeatures = 0;
    private DOM3Serializer fDOMSerializer = null;
    private LSSerializerFilter fSerializerFilter = null;
    private Node fVisitedNode = null;
    private String fEndOfLine = DEFAULT_END_OF_LINE;
    private DOMErrorHandler fDOMErrorHandler = null;
    private Properties fDOMConfigProperties = null;
    private String fEncoding;
    private static final int CANONICAL = 1;
    private static final int CDATA = 2;
    private static final int CHARNORMALIZE = 4;
    private static final int COMMENTS = 8;
    private static final int DTNORMALIZE = 16;
    private static final int ELEM_CONTENT_WHITESPACE = 32;
    private static final int ENTITIES = 64;
    private static final int INFOSET = 128;
    private static final int NAMESPACES = 256;
    private static final int NAMESPACEDECLS = 512;
    private static final int NORMALIZECHARS = 1024;
    private static final int SPLITCDATA = 2048;
    private static final int VALIDATE = 4096;
    private static final int SCHEMAVALIDATE = 8192;
    private static final int WELLFORMED = 16384;
    private static final int DISCARDDEFAULT = 32768;
    private static final int PRETTY_PRINT = 65536;
    private static final int IGNORE_CHAR_DENORMALIZE = 131072;
    private static final int XMLDECL = 262144;
    private String[] fRecognizedParameters = new String[]{"canonical-form", "cdata-sections", "check-character-normalization", "comments", "datatype-normalization", "element-content-whitespace", "entities", "infoset", "namespaces", "namespace-declarations", "split-cdata-sections", "validate", "validate-if-schema", "well-formed", "discard-default-content", "format-pretty-print", "ignore-unknown-character-denormalizations", "xml-declaration", "error-handler"};

    public LSSerializerImpl() {
        this.fFeatures |= 2;
        this.fFeatures |= 8;
        this.fFeatures |= 0x20;
        this.fFeatures |= 0x40;
        this.fFeatures |= 0x100;
        this.fFeatures |= 0x200;
        this.fFeatures |= 0x800;
        this.fFeatures |= 0x4000;
        this.fFeatures |= 0x8000;
        this.fFeatures |= 0x40000;
        this.fDOMConfigProperties = new Properties();
        this.initializeSerializerProps();
        Properties configProps = OutputPropertiesFactory.getDefaultMethodProperties("xml");
        this.fXMLSerializer = SerializerFactory.getSerializer(configProps);
        this.fXMLSerializer.setOutputFormat(this.fDOMConfigProperties);
    }

    public void initializeSerializerProps() {
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}canonical-form", "default:no");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}cdata-sections", "default:yes");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}check-character-normalization", "default:no");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}comments", "default:yes");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}datatype-normalization", "default:no");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}element-content-whitespace", "default:yes");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}entities", "default:yes");
        this.fDOMConfigProperties.setProperty("{http://xml.apache.org/xerces-2j}entities", "default:yes");
        if ((this.fFeatures & 0x80) != 0) {
            this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}namespaces", "default:yes");
            this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}namespace-declarations", "default:yes");
            this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}comments", "default:yes");
            this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}element-content-whitespace", "default:yes");
            this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}well-formed", "default:yes");
            this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}entities", "default:no");
            this.fDOMConfigProperties.setProperty("{http://xml.apache.org/xerces-2j}entities", "default:no");
            this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}cdata-sections", "default:no");
            this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}validate-if-schema", "default:no");
            this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}datatype-normalization", "default:no");
        }
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}namespaces", "default:yes");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}namespace-declarations", "default:yes");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}split-cdata-sections", "default:yes");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}validate", "default:no");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}validate-if-schema", "default:no");
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}well-formed", "default:yes");
        this.fDOMConfigProperties.setProperty("indent", "default:yes");
        this.fDOMConfigProperties.setProperty("{http://xml.apache.org/xalan}indent-amount", Integer.toString(3));
        this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}discard-default-content", "default:yes");
        this.fDOMConfigProperties.setProperty("omit-xml-declaration", "no");
    }

    @Override
    public boolean canSetParameter(String name, Object value) {
        if (value instanceof Boolean) {
            if (name.equalsIgnoreCase("cdata-sections") || name.equalsIgnoreCase("comments") || name.equalsIgnoreCase("entities") || name.equalsIgnoreCase("infoset") || name.equalsIgnoreCase("element-content-whitespace") || name.equalsIgnoreCase("namespaces") || name.equalsIgnoreCase("namespace-declarations") || name.equalsIgnoreCase("split-cdata-sections") || name.equalsIgnoreCase("well-formed") || name.equalsIgnoreCase("discard-default-content") || name.equalsIgnoreCase("format-pretty-print") || name.equalsIgnoreCase("xml-declaration")) {
                return true;
            }
            if (name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("check-character-normalization") || name.equalsIgnoreCase("datatype-normalization") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("validate")) {
                return (Boolean)value == false;
            }
            if (name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                return (Boolean)value;
            }
        } else if (name.equalsIgnoreCase("error-handler") && value == null || value instanceof DOMErrorHandler) {
            return true;
        }
        return false;
    }

    @Override
    public Object getParameter(String name) throws DOMException {
        if (name.equalsIgnoreCase("comments")) {
            return (this.fFeatures & 8) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("cdata-sections")) {
            return (this.fFeatures & 2) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("entities")) {
            return (this.fFeatures & 0x40) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("namespaces")) {
            return (this.fFeatures & 0x100) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("namespace-declarations")) {
            return (this.fFeatures & 0x200) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("split-cdata-sections")) {
            return (this.fFeatures & 0x800) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("well-formed")) {
            return (this.fFeatures & 0x4000) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("discard-default-content")) {
            return (this.fFeatures & 0x8000) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("format-pretty-print")) {
            return (this.fFeatures & 0x10000) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("xml-declaration")) {
            return (this.fFeatures & 0x40000) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("element-content-whitespace")) {
            return (this.fFeatures & 0x20) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("format-pretty-print")) {
            return (this.fFeatures & 0x10000) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
            return Boolean.TRUE;
        }
        if (name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("check-character-normalization") || name.equalsIgnoreCase("datatype-normalization") || name.equalsIgnoreCase("validate") || name.equalsIgnoreCase("validate-if-schema")) {
            return Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("infoset")) {
            if ((this.fFeatures & 0x40) == 0 && (this.fFeatures & 2) == 0 && (this.fFeatures & 0x20) != 0 && (this.fFeatures & 0x100) != 0 && (this.fFeatures & 0x200) != 0 && (this.fFeatures & 0x4000) != 0 && (this.fFeatures & 8) != 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        if (name.equalsIgnoreCase("error-handler")) {
            return this.fDOMErrorHandler;
        }
        if (name.equalsIgnoreCase("schema-location") || name.equalsIgnoreCase("schema-type")) {
            return null;
        }
        String msg = Utils.messages.createMessage("FEATURE_NOT_FOUND", new Object[]{name});
        throw new DOMException(8, msg);
    }

    @Override
    public DOMStringList getParameterNames() {
        return new DOMStringListImpl(this.fRecognizedParameters);
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public void setParameter(String name, Object value) throws DOMException {
        if (value instanceof Boolean) {
            boolean state = (Boolean)value;
            if (name.equalsIgnoreCase("comments")) {
                int n = this.fFeatures = state ? this.fFeatures | 8 : this.fFeatures & 0xFFFFFFF7;
                if (state) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}comments", "explicit:yes");
                    return;
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}comments", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("cdata-sections")) {
                int n = this.fFeatures = state ? this.fFeatures | 2 : this.fFeatures & 0xFFFFFFFD;
                if (state) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}cdata-sections", "explicit:yes");
                    return;
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}cdata-sections", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("entities")) {
                int n = this.fFeatures = state ? this.fFeatures | 0x40 : this.fFeatures & 0xFFFFFFBF;
                if (state) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}entities", "explicit:yes");
                    this.fDOMConfigProperties.setProperty("{http://xml.apache.org/xerces-2j}entities", "explicit:yes");
                    return;
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}entities", "explicit:no");
                this.fDOMConfigProperties.setProperty("{http://xml.apache.org/xerces-2j}entities", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("namespaces")) {
                int n = this.fFeatures = state ? this.fFeatures | 0x100 : this.fFeatures & 0xFFFFFEFF;
                if (state) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}namespaces", "explicit:yes");
                    return;
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}namespaces", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("namespace-declarations")) {
                int n = this.fFeatures = state ? this.fFeatures | 0x200 : this.fFeatures & 0xFFFFFDFF;
                if (state) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}namespace-declarations", "explicit:yes");
                    return;
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}namespace-declarations", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("split-cdata-sections")) {
                int n = this.fFeatures = state ? this.fFeatures | 0x800 : this.fFeatures & 0xFFFFF7FF;
                if (state) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}split-cdata-sections", "explicit:yes");
                    return;
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}split-cdata-sections", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("well-formed")) {
                int n = this.fFeatures = state ? this.fFeatures | 0x4000 : this.fFeatures & 0xFFFFBFFF;
                if (state) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}well-formed", "explicit:yes");
                    return;
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}well-formed", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("discard-default-content")) {
                int n = this.fFeatures = state ? this.fFeatures | 0x8000 : this.fFeatures & 0xFFFF7FFF;
                if (state) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}discard-default-content", "explicit:yes");
                    return;
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}discard-default-content", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("format-pretty-print")) {
                int n = this.fFeatures = state ? this.fFeatures | 0x10000 : this.fFeatures & 0xFFFEFFFF;
                if (state) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}format-pretty-print", "explicit:yes");
                    return;
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}format-pretty-print", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("xml-declaration")) {
                int n = this.fFeatures = state ? this.fFeatures | 0x40000 : this.fFeatures & 0xFFFBFFFF;
                if (state) {
                    this.fDOMConfigProperties.setProperty("omit-xml-declaration", "no");
                    return;
                }
                this.fDOMConfigProperties.setProperty("omit-xml-declaration", "yes");
                return;
            }
            if (name.equalsIgnoreCase("element-content-whitespace")) {
                int n = this.fFeatures = state ? this.fFeatures | 0x20 : this.fFeatures & 0xFFFFFFDF;
                if (state) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}element-content-whitespace", "explicit:yes");
                    return;
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}element-content-whitespace", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                if (!state) {
                    String msg = Utils.messages.createMessage("FEATURE_NOT_SUPPORTED", new Object[]{name});
                    throw new DOMException(9, msg);
                }
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}ignore-unknown-character-denormalizations", "explicit:yes");
                return;
            }
            if (name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("validate") || name.equalsIgnoreCase("check-character-normalization") || name.equalsIgnoreCase("datatype-normalization")) {
                if (state) {
                    String msg = Utils.messages.createMessage("FEATURE_NOT_SUPPORTED", new Object[]{name});
                    throw new DOMException(9, msg);
                }
                if (name.equalsIgnoreCase("canonical-form")) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}canonical-form", "explicit:no");
                    return;
                }
                if (name.equalsIgnoreCase("validate-if-schema")) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}validate-if-schema", "explicit:no");
                    return;
                }
                if (name.equalsIgnoreCase("validate")) {
                    this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}validate", "explicit:no");
                    return;
                }
                if (name.equalsIgnoreCase("validate-if-schema")) {
                    this.fDOMConfigProperties.setProperty("check-character-normalizationcheck-character-normalization", "explicit:no");
                    return;
                }
                if (!name.equalsIgnoreCase("datatype-normalization")) return;
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}datatype-normalization", "explicit:no");
                return;
            }
            if (name.equalsIgnoreCase("infoset")) {
                if (!state) return;
                this.fFeatures &= 0xFFFFFFBF;
                this.fFeatures &= 0xFFFFFFFD;
                this.fFeatures &= 0xFFFFDFFF;
                this.fFeatures &= 0xFFFFFFEF;
                this.fFeatures |= 0x100;
                this.fFeatures |= 0x200;
                this.fFeatures |= 0x4000;
                this.fFeatures |= 0x20;
                this.fFeatures |= 8;
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}namespaces", "explicit:yes");
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}namespace-declarations", "explicit:yes");
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}comments", "explicit:yes");
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}element-content-whitespace", "explicit:yes");
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}well-formed", "explicit:yes");
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}entities", "explicit:no");
                this.fDOMConfigProperties.setProperty("{http://xml.apache.org/xerces-2j}entities", "explicit:no");
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}cdata-sections", "explicit:no");
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}validate-if-schema", "explicit:no");
                this.fDOMConfigProperties.setProperty("{http://www.w3.org/TR/DOM-Level-3-LS}datatype-normalization", "explicit:no");
                return;
            }
            if (!(name.equalsIgnoreCase("error-handler") || name.equalsIgnoreCase("schema-location") || name.equalsIgnoreCase("schema-type"))) {
                String msg = Utils.messages.createMessage("FEATURE_NOT_FOUND", new Object[]{name});
                throw new DOMException(8, msg);
            }
            String msg = Utils.messages.createMessage("TYPE_MISMATCH_ERR", new Object[]{name});
            throw new DOMException(17, msg);
        }
        if (name.equalsIgnoreCase("error-handler")) {
            if (value != null && !(value instanceof DOMErrorHandler)) {
                String msg = Utils.messages.createMessage("TYPE_MISMATCH_ERR", new Object[]{name});
                throw new DOMException(17, msg);
            }
            this.fDOMErrorHandler = (DOMErrorHandler)value;
            return;
        }
        if (name.equalsIgnoreCase("schema-location") || name.equalsIgnoreCase("schema-type")) {
            if (value == null) return;
            if (!(value instanceof String)) {
                String msg = Utils.messages.createMessage("TYPE_MISMATCH_ERR", new Object[]{name});
                throw new DOMException(17, msg);
            }
            String msg = Utils.messages.createMessage("FEATURE_NOT_SUPPORTED", new Object[]{name});
            throw new DOMException(9, msg);
        }
        if (!(name.equalsIgnoreCase("comments") || name.equalsIgnoreCase("cdata-sections") || name.equalsIgnoreCase("entities") || name.equalsIgnoreCase("namespaces") || name.equalsIgnoreCase("namespace-declarations") || name.equalsIgnoreCase("split-cdata-sections") || name.equalsIgnoreCase("well-formed") || name.equalsIgnoreCase("discard-default-content") || name.equalsIgnoreCase("format-pretty-print") || name.equalsIgnoreCase("xml-declaration") || name.equalsIgnoreCase("element-content-whitespace") || name.equalsIgnoreCase("ignore-unknown-character-denormalizations") || name.equalsIgnoreCase("canonical-form") || name.equalsIgnoreCase("validate-if-schema") || name.equalsIgnoreCase("validate") || name.equalsIgnoreCase("check-character-normalization") || name.equalsIgnoreCase("datatype-normalization") || name.equalsIgnoreCase("infoset"))) {
            String msg = Utils.messages.createMessage("FEATURE_NOT_FOUND", new Object[]{name});
            throw new DOMException(8, msg);
        }
        String msg = Utils.messages.createMessage("TYPE_MISMATCH_ERR", new Object[]{name});
        throw new DOMException(17, msg);
    }

    @Override
    public DOMConfiguration getDomConfig() {
        return this;
    }

    @Override
    public LSSerializerFilter getFilter() {
        return this.fSerializerFilter;
    }

    @Override
    public String getNewLine() {
        return this.fEndOfLine;
    }

    @Override
    public void setFilter(LSSerializerFilter filter) {
        this.fSerializerFilter = filter;
    }

    @Override
    public void setNewLine(String newLine) {
        this.fEndOfLine = newLine != null ? newLine : DEFAULT_END_OF_LINE;
    }

    @Override
    public boolean write(Node nodeArg, LSOutput destination) throws LSException {
        if (destination == null) {
            String msg = Utils.messages.createMessage("no-output-specified", null);
            if (this.fDOMErrorHandler != null) {
                this.fDOMErrorHandler.handleError(new DOMErrorImpl(3, msg, "no-output-specified"));
            }
            throw new LSException(82, msg);
        }
        if (nodeArg == null) {
            return false;
        }
        Serializer serializer = this.fXMLSerializer;
        serializer.reset();
        if (nodeArg != this.fVisitedNode) {
            String xmlVersion = this.getXMLVersion(nodeArg);
            this.fEncoding = destination.getEncoding();
            if (this.fEncoding == null) {
                this.fEncoding = this.getInputEncoding(nodeArg);
                String string = this.fEncoding != null ? this.fEncoding : (this.fEncoding = this.getXMLEncoding(nodeArg) == null ? "UTF-8" : this.getXMLEncoding(nodeArg));
            }
            if (!Encodings.isRecognizedEncoding(this.fEncoding)) {
                String msg = Utils.messages.createMessage("unsupported-encoding", null);
                if (this.fDOMErrorHandler != null) {
                    this.fDOMErrorHandler.handleError(new DOMErrorImpl(3, msg, "unsupported-encoding"));
                }
                throw new LSException(82, msg);
            }
            serializer.getOutputFormat().setProperty("version", xmlVersion);
            this.fDOMConfigProperties.setProperty("{http://xml.apache.org/xerces-2j}xml-version", xmlVersion);
            this.fDOMConfigProperties.setProperty("encoding", this.fEncoding);
            if ((nodeArg.getNodeType() != 9 || nodeArg.getNodeType() != 1 || nodeArg.getNodeType() != 6) && (this.fFeatures & 0x40000) != 0) {
                this.fDOMConfigProperties.setProperty("omit-xml-declaration", "default:no");
            }
            this.fVisitedNode = nodeArg;
        }
        this.fXMLSerializer.setOutputFormat(this.fDOMConfigProperties);
        try {
            Writer writer = destination.getCharacterStream();
            if (writer == null) {
                OutputStream outputStream = destination.getByteStream();
                if (outputStream == null) {
                    String uri = destination.getSystemId();
                    if (uri == null) {
                        String msg = Utils.messages.createMessage("no-output-specified", null);
                        if (this.fDOMErrorHandler != null) {
                            this.fDOMErrorHandler.handleError(new DOMErrorImpl(3, msg, "no-output-specified"));
                        }
                        throw new LSException(82, msg);
                    }
                    String absoluteURI = SystemIDResolver.getAbsoluteURI(uri);
                    URL url = new URL(absoluteURI);
                    OutputStream urlOutStream = null;
                    String protocol = url.getProtocol();
                    String host = url.getHost();
                    if (protocol.equalsIgnoreCase("file") && (host == null || host.length() == 0 || host.equals("localhost"))) {
                        urlOutStream = new FileOutputStream(LSSerializerImpl.getPathWithoutEscapes(url.getPath()));
                    } else {
                        URLConnection urlCon = url.openConnection();
                        urlCon.setDoInput(false);
                        urlCon.setDoOutput(true);
                        urlCon.setUseCaches(false);
                        urlCon.setAllowUserInteraction(false);
                        if (urlCon instanceof HttpURLConnection) {
                            HttpURLConnection httpCon = (HttpURLConnection)urlCon;
                            httpCon.setRequestMethod("PUT");
                        }
                        urlOutStream = urlCon.getOutputStream();
                    }
                    serializer.setOutputStream(urlOutStream);
                } else {
                    serializer.setOutputStream(outputStream);
                }
            } else {
                serializer.setWriter(writer);
            }
            if (this.fDOMSerializer == null) {
                this.fDOMSerializer = (DOM3Serializer)serializer.asDOM3Serializer();
            }
            if (this.fDOMErrorHandler != null) {
                this.fDOMSerializer.setErrorHandler(this.fDOMErrorHandler);
            }
            if (this.fSerializerFilter != null) {
                this.fDOMSerializer.setNodeFilter(this.fSerializerFilter);
            }
            this.fDOMSerializer.setNewLine(this.fEndOfLine.toCharArray());
            this.fDOMSerializer.serializeDOM3(nodeArg);
        }
        catch (UnsupportedEncodingException ue) {
            String msg = Utils.messages.createMessage("unsupported-encoding", null);
            if (this.fDOMErrorHandler != null) {
                this.fDOMErrorHandler.handleError(new DOMErrorImpl(3, msg, "unsupported-encoding", ue));
            }
            throw (LSException)LSSerializerImpl.createLSException((short)82, ue).fillInStackTrace();
        }
        catch (LSException lse) {
            throw lse;
        }
        catch (RuntimeException e) {
            throw (LSException)LSSerializerImpl.createLSException((short)82, e).fillInStackTrace();
        }
        catch (Exception e) {
            if (this.fDOMErrorHandler != null) {
                this.fDOMErrorHandler.handleError(new DOMErrorImpl(3, e.getMessage(), null, e));
            }
            throw (LSException)LSSerializerImpl.createLSException((short)82, e).fillInStackTrace();
        }
        return true;
    }

    @Override
    public String writeToString(Node nodeArg) throws DOMException, LSException {
        if (nodeArg == null) {
            return null;
        }
        Serializer serializer = this.fXMLSerializer;
        serializer.reset();
        if (nodeArg != this.fVisitedNode) {
            String xmlVersion = this.getXMLVersion(nodeArg);
            serializer.getOutputFormat().setProperty("version", xmlVersion);
            this.fDOMConfigProperties.setProperty("{http://xml.apache.org/xerces-2j}xml-version", xmlVersion);
            this.fDOMConfigProperties.setProperty("encoding", "UTF-16");
            if ((nodeArg.getNodeType() != 9 || nodeArg.getNodeType() != 1 || nodeArg.getNodeType() != 6) && (this.fFeatures & 0x40000) != 0) {
                this.fDOMConfigProperties.setProperty("omit-xml-declaration", "default:no");
            }
            this.fVisitedNode = nodeArg;
        }
        this.fXMLSerializer.setOutputFormat(this.fDOMConfigProperties);
        StringWriter output = new StringWriter();
        try {
            serializer.setWriter(output);
            if (this.fDOMSerializer == null) {
                this.fDOMSerializer = (DOM3Serializer)serializer.asDOM3Serializer();
            }
            if (this.fDOMErrorHandler != null) {
                this.fDOMSerializer.setErrorHandler(this.fDOMErrorHandler);
            }
            if (this.fSerializerFilter != null) {
                this.fDOMSerializer.setNodeFilter(this.fSerializerFilter);
            }
            this.fDOMSerializer.setNewLine(this.fEndOfLine.toCharArray());
            this.fDOMSerializer.serializeDOM3(nodeArg);
        }
        catch (LSException lse) {
            throw lse;
        }
        catch (RuntimeException e) {
            throw (LSException)LSSerializerImpl.createLSException((short)82, e).fillInStackTrace();
        }
        catch (Exception e) {
            if (this.fDOMErrorHandler != null) {
                this.fDOMErrorHandler.handleError(new DOMErrorImpl(3, e.getMessage(), null, e));
            }
            throw (LSException)LSSerializerImpl.createLSException((short)82, e).fillInStackTrace();
        }
        return output.toString();
    }

    @Override
    public boolean writeToURI(Node nodeArg, String uri) throws LSException {
        if (nodeArg == null) {
            return false;
        }
        Serializer serializer = this.fXMLSerializer;
        serializer.reset();
        if (nodeArg != this.fVisitedNode) {
            String xmlVersion = this.getXMLVersion(nodeArg);
            this.fEncoding = this.getInputEncoding(nodeArg);
            if (this.fEncoding == null) {
                this.fEncoding = this.fEncoding != null ? this.fEncoding : (this.getXMLEncoding(nodeArg) == null ? "UTF-8" : this.getXMLEncoding(nodeArg));
            }
            serializer.getOutputFormat().setProperty("version", xmlVersion);
            this.fDOMConfigProperties.setProperty("{http://xml.apache.org/xerces-2j}xml-version", xmlVersion);
            this.fDOMConfigProperties.setProperty("encoding", this.fEncoding);
            if ((nodeArg.getNodeType() != 9 || nodeArg.getNodeType() != 1 || nodeArg.getNodeType() != 6) && (this.fFeatures & 0x40000) != 0) {
                this.fDOMConfigProperties.setProperty("omit-xml-declaration", "default:no");
            }
            this.fVisitedNode = nodeArg;
        }
        this.fXMLSerializer.setOutputFormat(this.fDOMConfigProperties);
        try {
            if (uri == null) {
                String msg = Utils.messages.createMessage("no-output-specified", null);
                if (this.fDOMErrorHandler != null) {
                    this.fDOMErrorHandler.handleError(new DOMErrorImpl(3, msg, "no-output-specified"));
                }
                throw new LSException(82, msg);
            }
            String absoluteURI = SystemIDResolver.getAbsoluteURI(uri);
            URL url = new URL(absoluteURI);
            OutputStream urlOutStream = null;
            String protocol = url.getProtocol();
            String host = url.getHost();
            if (protocol.equalsIgnoreCase("file") && (host == null || host.length() == 0 || host.equals("localhost"))) {
                urlOutStream = new FileOutputStream(LSSerializerImpl.getPathWithoutEscapes(url.getPath()));
            } else {
                URLConnection urlCon = url.openConnection();
                urlCon.setDoInput(false);
                urlCon.setDoOutput(true);
                urlCon.setUseCaches(false);
                urlCon.setAllowUserInteraction(false);
                if (urlCon instanceof HttpURLConnection) {
                    HttpURLConnection httpCon = (HttpURLConnection)urlCon;
                    httpCon.setRequestMethod("PUT");
                }
                urlOutStream = urlCon.getOutputStream();
            }
            serializer.setOutputStream(urlOutStream);
            if (this.fDOMSerializer == null) {
                this.fDOMSerializer = (DOM3Serializer)serializer.asDOM3Serializer();
            }
            if (this.fDOMErrorHandler != null) {
                this.fDOMSerializer.setErrorHandler(this.fDOMErrorHandler);
            }
            if (this.fSerializerFilter != null) {
                this.fDOMSerializer.setNodeFilter(this.fSerializerFilter);
            }
            this.fDOMSerializer.setNewLine(this.fEndOfLine.toCharArray());
            this.fDOMSerializer.serializeDOM3(nodeArg);
        }
        catch (LSException lse) {
            throw lse;
        }
        catch (RuntimeException e) {
            throw (LSException)LSSerializerImpl.createLSException((short)82, e).fillInStackTrace();
        }
        catch (Exception e) {
            if (this.fDOMErrorHandler != null) {
                this.fDOMErrorHandler.handleError(new DOMErrorImpl(3, e.getMessage(), null, e));
            }
            throw (LSException)LSSerializerImpl.createLSException((short)82, e).fillInStackTrace();
        }
        return true;
    }

    protected String getXMLVersion(Node nodeArg) {
        Document doc = null;
        if (nodeArg != null && (doc = nodeArg.getNodeType() == 9 ? (Document)nodeArg : nodeArg.getOwnerDocument()) != null && doc.getImplementation().hasFeature("Core", "3.0")) {
            return doc.getXmlVersion();
        }
        return "1.0";
    }

    protected String getXMLEncoding(Node nodeArg) {
        Document doc = null;
        if (nodeArg != null && (doc = nodeArg.getNodeType() == 9 ? (Document)nodeArg : nodeArg.getOwnerDocument()) != null && doc.getImplementation().hasFeature("Core", "3.0")) {
            return doc.getXmlEncoding();
        }
        return "UTF-8";
    }

    protected String getInputEncoding(Node nodeArg) {
        Document doc = null;
        if (nodeArg != null && (doc = nodeArg.getNodeType() == 9 ? (Document)nodeArg : nodeArg.getOwnerDocument()) != null && doc.getImplementation().hasFeature("Core", "3.0")) {
            return doc.getInputEncoding();
        }
        return null;
    }

    public DOMErrorHandler getErrorHandler() {
        return this.fDOMErrorHandler;
    }

    private static String getPathWithoutEscapes(String origPath) {
        if (origPath != null && origPath.length() != 0 && origPath.indexOf(37) != -1) {
            StringTokenizer tokenizer = new StringTokenizer(origPath, "%");
            StringBuffer result = new StringBuffer(origPath.length());
            int size = tokenizer.countTokens();
            result.append(tokenizer.nextToken());
            for (int i = 1; i < size; ++i) {
                String token = tokenizer.nextToken();
                if (token.length() >= 2 && LSSerializerImpl.isHexDigit(token.charAt(0)) && LSSerializerImpl.isHexDigit(token.charAt(1))) {
                    result.append((char)Integer.valueOf(token.substring(0, 2), 16).intValue());
                    token = token.substring(2);
                }
                result.append(token);
            }
            return result.toString();
        }
        return origPath;
    }

    private static boolean isHexDigit(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

    private static LSException createLSException(short code, Throwable cause) {
        LSException lse = new LSException(code, cause != null ? cause.getMessage() : null);
        if (cause != null && ThrowableMethods.fgThrowableMethodsAvailable) {
            try {
                ThrowableMethods.fgThrowableInitCauseMethod.invoke((Object)lse, cause);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return lse;
    }

    static {
        String lineSeparator = (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    return System.getProperty("line.separator");
                }
                catch (SecurityException securityException) {
                    return null;
                }
            }
        });
        DEFAULT_END_OF_LINE = lineSeparator != null && (lineSeparator.equals("\r\n") || lineSeparator.equals("\r")) ? lineSeparator : "\n";
    }

    static class ThrowableMethods {
        private static Method fgThrowableInitCauseMethod = null;
        private static boolean fgThrowableMethodsAvailable = false;

        private ThrowableMethods() {
        }

        static {
            try {
                fgThrowableInitCauseMethod = Throwable.class.getMethod("initCause", Throwable.class);
                fgThrowableMethodsAvailable = true;
            }
            catch (Exception exc) {
                fgThrowableInitCauseMethod = null;
                fgThrowableMethodsAvailable = false;
            }
        }
    }
}

