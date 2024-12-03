/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om;

import java.util.HashMap;
import org.apache.axiom.mime.MultipartWriterFactory;
import org.apache.axiom.mime.impl.axiom.AxiomMultipartWriterFactory;
import org.apache.axiom.om.util.StAXWriterConfiguration;
import org.apache.axiom.om.util.XMLStreamWriterFilter;
import org.apache.axiom.util.UIDGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OMOutputFormat {
    private static final Log log = LogFactory.getLog(OMOutputFormat.class);
    private String mimeBoundary;
    private String rootContentId;
    private int nextid;
    private boolean doOptimize;
    private boolean doingSWA;
    private boolean isSoap11;
    private int optimizedThreshold;
    public static final String DEFAULT_CHAR_SET_ENCODING = "utf-8";
    private String charSetEncoding;
    private String xmlVersion;
    private String contentType;
    private boolean contentTypeSet;
    private boolean ignoreXMLDeclaration;
    private boolean autoCloseWriter;
    public static final String ACTION_PROPERTY = "action";
    private XMLStreamWriterFilter xmlStreamWriterFilter = null;
    private StAXWriterConfiguration writerConfiguration;
    private MultipartWriterFactory multipartWriterFactory;
    public static final String USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS = "org.apache.axiom.om.OMFormat.use.cteBase64.forNonTextualAttachments";
    public static final String RESPECT_SWA_ATTACHMENT_ORDER = "org.apache.axiom.om.OMFormat.respectSWAAttachmentOrder";
    public static final Boolean RESPECT_SWA_ATTACHMENT_ORDER_DEFAULT = Boolean.TRUE;
    private HashMap map;

    public OMOutputFormat() {
        this.isSoap11 = true;
    }

    public OMOutputFormat(OMOutputFormat format) {
        this.doOptimize = format.doOptimize;
        this.doingSWA = format.doingSWA;
        this.isSoap11 = format.isSoap11;
        this.optimizedThreshold = format.optimizedThreshold;
        this.charSetEncoding = format.charSetEncoding;
        this.xmlVersion = format.xmlVersion;
        if (format.contentTypeSet) {
            this.contentTypeSet = true;
            this.contentType = format.contentType;
        }
        this.ignoreXMLDeclaration = format.ignoreXMLDeclaration;
        this.autoCloseWriter = format.autoCloseWriter;
        this.xmlStreamWriterFilter = format.xmlStreamWriterFilter;
        this.writerConfiguration = format.writerConfiguration;
        this.multipartWriterFactory = format.multipartWriterFactory;
        if (format.map != null) {
            this.map = new HashMap(format.map);
        }
    }

    public Object getProperty(String key) {
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public Object setProperty(String key, Object value) {
        if (this.map == null) {
            this.map = new HashMap();
        }
        return this.map.put(key, value);
    }

    public boolean containsKey(String key) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsKey(key);
    }

    public boolean isOptimized() {
        return this.doOptimize && !this.doingSWA;
    }

    public String getContentType() {
        String ct = null;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Start getContentType: " + this.toString()));
        }
        if (this.contentType == null) {
            this.contentType = this.isSoap11 ? "text/xml" : "application/soap+xml";
        }
        ct = this.isOptimized() ? this.getContentTypeForMTOM(this.contentType) : (this.isDoingSWA() ? this.getContentTypeForSwA(this.contentType) : this.contentType);
        if (log.isDebugEnabled()) {
            log.debug((Object)("getContentType= {" + ct + "}   " + this.toString()));
        }
        return ct;
    }

    public void setContentType(String c) {
        this.contentTypeSet = true;
        this.contentType = c;
    }

    public String getMimeBoundary() {
        if (this.mimeBoundary == null) {
            this.mimeBoundary = UIDGenerator.generateMimeBoundary();
        }
        return this.mimeBoundary;
    }

    public String getRootContentId() {
        if (this.rootContentId == null) {
            this.rootContentId = "0." + UIDGenerator.generateContentId();
        }
        return this.rootContentId;
    }

    public String getNextContentId() {
        ++this.nextid;
        return this.nextid + "." + UIDGenerator.generateContentId();
    }

    public String getCharSetEncoding() {
        return this.charSetEncoding;
    }

    public void setCharSetEncoding(String charSetEncoding) {
        this.charSetEncoding = charSetEncoding;
    }

    public String getXmlVersion() {
        return this.xmlVersion;
    }

    public void setXmlVersion(String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }

    public void setSOAP11(boolean b) {
        this.isSoap11 = b;
    }

    public boolean isSOAP11() {
        return this.isSoap11;
    }

    public boolean isIgnoreXMLDeclaration() {
        return this.ignoreXMLDeclaration;
    }

    public void setIgnoreXMLDeclaration(boolean ignoreXMLDeclaration) {
        this.ignoreXMLDeclaration = ignoreXMLDeclaration;
    }

    public void setDoOptimize(boolean optimize) {
        this.doOptimize = optimize;
    }

    public boolean isDoingSWA() {
        return this.doingSWA;
    }

    public void setDoingSWA(boolean doingSWA) {
        this.doingSWA = doingSWA;
    }

    public String getContentTypeForMTOM(String SOAPContentType) {
        String action;
        if (this.containsKey(ACTION_PROPERTY) && (action = (String)this.getProperty(ACTION_PROPERTY)) != null && action.length() > 0) {
            SOAPContentType = SOAPContentType + "; action=\\\"" + action + "\\\"";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("multipart/related");
        sb.append("; ");
        sb.append("boundary=");
        sb.append("\"");
        sb.append(this.getMimeBoundary());
        sb.append("\"");
        sb.append("; ");
        sb.append("type=\"application/xop+xml\"");
        sb.append("; ");
        sb.append("start=\"<").append(this.getRootContentId()).append(">\"");
        sb.append("; ");
        sb.append("start-info=\"").append(SOAPContentType).append("\"");
        return sb.toString();
    }

    public String getContentTypeForSwA(String SOAPContentType) {
        StringBuffer sb = new StringBuffer();
        sb.append("multipart/related");
        sb.append("; ");
        sb.append("boundary=");
        sb.append("\"");
        sb.append(this.getMimeBoundary());
        sb.append("\"");
        sb.append("; ");
        sb.append("type=\"").append(SOAPContentType).append("\"");
        sb.append("; ");
        sb.append("start=\"<").append(this.getRootContentId()).append(">\"");
        return sb.toString();
    }

    public boolean isAutoCloseWriter() {
        return this.autoCloseWriter;
    }

    public void setAutoCloseWriter(boolean autoCloseWriter) {
        this.autoCloseWriter = autoCloseWriter;
    }

    public void setMimeBoundary(String mimeBoundary) {
        this.mimeBoundary = mimeBoundary;
    }

    public void setRootContentId(String rootContentId) {
        this.rootContentId = rootContentId;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("OMOutputFormat [");
        sb.append(" mimeBoundary =");
        sb.append(this.mimeBoundary);
        sb.append(" rootContentId=");
        sb.append(this.rootContentId);
        sb.append(" doOptimize=");
        sb.append(this.doOptimize);
        sb.append(" doingSWA=");
        sb.append(this.doingSWA);
        sb.append(" isSOAP11=");
        sb.append(this.isSoap11);
        sb.append(" charSetEncoding=");
        sb.append(this.charSetEncoding);
        sb.append(" xmlVersion=");
        sb.append(this.xmlVersion);
        sb.append(" contentType=");
        sb.append(this.contentType);
        sb.append(" ignoreXmlDeclaration=");
        sb.append(this.ignoreXMLDeclaration);
        sb.append(" autoCloseWriter=");
        sb.append(this.autoCloseWriter);
        sb.append(" actionProperty=");
        sb.append(this.getProperty(ACTION_PROPERTY));
        sb.append(" optimizedThreshold=");
        sb.append(this.optimizedThreshold);
        sb.append("]");
        return sb.toString();
    }

    public void setOptimizedThreshold(int optimizedThreshold) {
        this.optimizedThreshold = optimizedThreshold;
    }

    public int getOptimizedThreshold() {
        return this.optimizedThreshold;
    }

    public XMLStreamWriterFilter getXmlStreamWriterFilter() {
        return this.xmlStreamWriterFilter;
    }

    public void setXmlStreamWriterFilter(XMLStreamWriterFilter xmlStreamWriterFilter) {
        this.xmlStreamWriterFilter = xmlStreamWriterFilter;
    }

    public StAXWriterConfiguration getStAXWriterConfiguration() {
        return this.writerConfiguration == null ? StAXWriterConfiguration.DEFAULT : this.writerConfiguration;
    }

    public void setStAXWriterConfiguration(StAXWriterConfiguration writerConfiguration) {
        this.writerConfiguration = writerConfiguration;
    }

    public MultipartWriterFactory getMultipartWriterFactory() {
        return this.multipartWriterFactory == null ? AxiomMultipartWriterFactory.INSTANCE : this.multipartWriterFactory;
    }

    public void setMultipartWriterFactory(MultipartWriterFactory multipartWriterFactory) {
        this.multipartWriterFactory = multipartWriterFactory;
    }
}

