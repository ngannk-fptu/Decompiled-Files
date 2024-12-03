/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.OMMultipartWriter;
import org.apache.axiom.om.impl.OptimizationPolicyImpl;
import org.apache.axiom.om.util.CommonUtils;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.om.util.XMLStreamWriterFilter;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.axiom.util.stax.xop.XOPEncodingStreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MTOMXMLStreamWriter
implements XMLStreamWriter {
    private static final Log log = LogFactory.getLog(MTOMXMLStreamWriter.class);
    private XMLStreamWriter xmlWriter;
    private OutputStream outStream;
    private List otherParts = new LinkedList();
    private OMMultipartWriter multipartWriter;
    private OutputStream rootPartOutputStream;
    private OMOutputFormat format = new OMOutputFormat();
    private final OptimizationPolicy optimizationPolicy;
    private final boolean preserveAttachments;
    private boolean isEndDocument = false;
    private boolean isComplete = false;
    private int depth = 0;
    private XMLStreamWriterFilter xmlStreamWriterFilter = null;

    public MTOMXMLStreamWriter(XMLStreamWriter xmlWriter) {
        this.xmlWriter = xmlWriter;
        if (log.isTraceEnabled()) {
            log.trace((Object)("Call Stack =" + CommonUtils.callStackToString()));
        }
        this.optimizationPolicy = new OptimizationPolicyImpl(this.format);
        this.preserveAttachments = true;
    }

    public MTOMXMLStreamWriter(OutputStream outStream, OMOutputFormat format) throws XMLStreamException, FactoryConfigurationError {
        this(outStream, format, true);
    }

    public MTOMXMLStreamWriter(OutputStream outStream, OMOutputFormat format, boolean preserveAttachments) throws XMLStreamException, FactoryConfigurationError {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Creating MTOMXMLStreamWriter");
            log.debug((Object)("OutputStream =" + outStream.getClass()));
            log.debug((Object)("OMFormat = " + format.toString()));
            log.debug((Object)("preserveAttachments = " + preserveAttachments));
        }
        if (log.isTraceEnabled()) {
            log.trace((Object)("Call Stack =" + CommonUtils.callStackToString()));
        }
        this.format = format;
        this.outStream = outStream;
        this.preserveAttachments = preserveAttachments;
        String encoding = format.getCharSetEncoding();
        if (encoding == null) {
            encoding = "utf-8";
            format.setCharSetEncoding("utf-8");
        }
        this.optimizationPolicy = new OptimizationPolicyImpl(format);
        if (format.isOptimized()) {
            this.multipartWriter = new OMMultipartWriter(outStream, format);
            try {
                this.rootPartOutputStream = this.multipartWriter.writeRootPart();
            }
            catch (IOException ex) {
                throw new XMLStreamException(ex);
            }
            ContentIDGenerator contentIDGenerator = new ContentIDGenerator(){

                public String generateContentID(String existingContentID) {
                    return existingContentID != null ? existingContentID : MTOMXMLStreamWriter.this.getNextContentId();
                }
            };
            this.xmlWriter = new XOPEncodingStreamWriter(StAXUtils.createXMLStreamWriter(format.getStAXWriterConfiguration(), this.rootPartOutputStream, encoding), contentIDGenerator, this.optimizationPolicy);
        } else {
            this.xmlWriter = StAXUtils.createXMLStreamWriter(format.getStAXWriterConfiguration(), outStream, format.getCharSetEncoding());
        }
        this.xmlStreamWriterFilter = format.getXmlStreamWriterFilter();
        if (this.xmlStreamWriterFilter != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Installing XMLStreamWriterFilter " + this.xmlStreamWriterFilter));
            }
            this.xmlStreamWriterFilter.setDelegate(this.xmlWriter);
            this.xmlWriter = this.xmlStreamWriterFilter;
        }
    }

    public void writeStartElement(String string) throws XMLStreamException {
        this.xmlWriter.writeStartElement(string);
        ++this.depth;
    }

    public void writeStartElement(String string, String string1) throws XMLStreamException {
        this.xmlWriter.writeStartElement(string, string1);
        ++this.depth;
    }

    public void writeStartElement(String string, String string1, String string2) throws XMLStreamException {
        this.xmlWriter.writeStartElement(string, string1, string2);
        ++this.depth;
    }

    public void writeEmptyElement(String string, String string1) throws XMLStreamException {
        this.xmlWriter.writeEmptyElement(string, string1);
    }

    public void writeEmptyElement(String string, String string1, String string2) throws XMLStreamException {
        this.xmlWriter.writeEmptyElement(string, string1, string2);
    }

    public void writeEmptyElement(String string) throws XMLStreamException {
        this.xmlWriter.writeEmptyElement(string);
    }

    public void writeEndElement() throws XMLStreamException {
        this.xmlWriter.writeEndElement();
        --this.depth;
    }

    public void writeEndDocument() throws XMLStreamException {
        log.debug((Object)"writeEndDocument");
        this.xmlWriter.writeEndDocument();
        this.isEndDocument = true;
    }

    public void close() throws XMLStreamException {
        log.debug((Object)"close");
        this.xmlWriter.close();
    }

    public void flush() throws XMLStreamException {
        log.debug((Object)"Calling MTOMXMLStreamWriter.flush");
        this.xmlWriter.flush();
        if (this.format.isOptimized() && !this.isComplete & (this.isEndDocument || this.depth == 0)) {
            log.debug((Object)"The XML writing is completed.  Now the attachments are written");
            this.isComplete = true;
            try {
                this.rootPartOutputStream.close();
                XOPEncodingStreamWriter encoder = (XOPEncodingStreamWriter)this.xmlWriter;
                for (String contentID : encoder.getContentIDs()) {
                    DataHandler dataHandler = encoder.getDataHandler(contentID);
                    if (this.preserveAttachments || !(dataHandler instanceof DataHandlerExt)) {
                        this.multipartWriter.writePart(dataHandler, contentID);
                        continue;
                    }
                    OutputStream out = this.multipartWriter.writePart(dataHandler.getContentType(), contentID);
                    BufferUtils.inputStream2OutputStream(((DataHandlerExt)dataHandler).readOnce(), out);
                    out.close();
                }
                for (Part part : this.otherParts) {
                    this.multipartWriter.writePart(part.getDataHandler(), part.getContentID());
                }
                this.multipartWriter.complete();
            }
            catch (IOException e) {
                throw new OMException(e);
            }
        }
    }

    public void writeAttribute(String string, String string1) throws XMLStreamException {
        this.xmlWriter.writeAttribute(string, string1);
    }

    public void writeAttribute(String string, String string1, String string2, String string3) throws XMLStreamException {
        this.xmlWriter.writeAttribute(string, string1, string2, string3);
    }

    public void writeAttribute(String string, String string1, String string2) throws XMLStreamException {
        this.xmlWriter.writeAttribute(string, string1, string2);
    }

    public void writeNamespace(String string, String string1) throws XMLStreamException {
        this.xmlWriter.writeNamespace(string, string1);
    }

    public void writeDefaultNamespace(String string) throws XMLStreamException {
        this.xmlWriter.writeDefaultNamespace(string);
    }

    public void writeComment(String string) throws XMLStreamException {
        this.xmlWriter.writeComment(string);
    }

    public void writeProcessingInstruction(String string) throws XMLStreamException {
        this.xmlWriter.writeProcessingInstruction(string);
    }

    public void writeProcessingInstruction(String string, String string1) throws XMLStreamException {
        this.xmlWriter.writeProcessingInstruction(string, string1);
    }

    public void writeCData(String string) throws XMLStreamException {
        this.xmlWriter.writeCData(string);
    }

    public void writeDTD(String string) throws XMLStreamException {
        this.xmlWriter.writeDTD(string);
    }

    public void writeEntityRef(String string) throws XMLStreamException {
        this.xmlWriter.writeEntityRef(string);
    }

    public void writeStartDocument() throws XMLStreamException {
        this.xmlWriter.writeStartDocument();
    }

    public void writeStartDocument(String string) throws XMLStreamException {
        this.xmlWriter.writeStartDocument(string);
    }

    public void writeStartDocument(String string, String string1) throws XMLStreamException {
        this.xmlWriter.writeStartDocument(string, string1);
    }

    public void writeCharacters(String string) throws XMLStreamException {
        this.xmlWriter.writeCharacters(string);
    }

    public void writeCharacters(char[] chars, int i, int i1) throws XMLStreamException {
        this.xmlWriter.writeCharacters(chars, i, i1);
    }

    public String getPrefix(String string) throws XMLStreamException {
        return this.xmlWriter.getPrefix(string);
    }

    public void setPrefix(String string, String string1) throws XMLStreamException {
        this.xmlWriter.setPrefix(string, string1);
    }

    public void setDefaultNamespace(String string) throws XMLStreamException {
        this.xmlWriter.setDefaultNamespace(string);
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        this.xmlWriter.setNamespaceContext(namespaceContext);
    }

    public NamespaceContext getNamespaceContext() {
        return this.xmlWriter.getNamespaceContext();
    }

    public Object getProperty(String string) throws IllegalArgumentException {
        return this.xmlWriter.getProperty(string);
    }

    public boolean isOptimized() {
        return this.format.isOptimized();
    }

    public String getContentType() {
        return this.format.getContentType();
    }

    public void writeOptimized(OMText node) {
        log.debug((Object)"Start MTOMXMLStreamWriter.writeOptimized()");
        this.otherParts.add(new Part(node.getContentID(), (DataHandler)node.getDataHandler()));
        log.debug((Object)"Exit MTOMXMLStreamWriter.writeOptimized()");
    }

    public boolean isOptimizedThreshold(OMText node) {
        try {
            return this.optimizationPolicy.isOptimized((DataHandler)node.getDataHandler(), true);
        }
        catch (IOException ex) {
            return true;
        }
    }

    public String prepareDataHandler(DataHandler dataHandler) {
        boolean doOptimize;
        try {
            doOptimize = this.optimizationPolicy.isOptimized(dataHandler, true);
        }
        catch (IOException ex) {
            doOptimize = true;
        }
        if (doOptimize) {
            String contentID = this.getNextContentId();
            this.otherParts.add(new Part(contentID, dataHandler));
            return contentID;
        }
        return null;
    }

    public void setXmlStreamWriter(XMLStreamWriter xmlWriter) {
        this.xmlWriter = xmlWriter;
    }

    public XMLStreamWriter getXmlStreamWriter() {
        return this.xmlWriter;
    }

    public String getMimeBoundary() {
        return this.format.getMimeBoundary();
    }

    public String getRootContentId() {
        return this.format.getRootContentId();
    }

    public String getNextContentId() {
        return this.format.getNextContentId();
    }

    public String getCharSetEncoding() {
        return this.format.getCharSetEncoding();
    }

    public void setCharSetEncoding(String charSetEncoding) {
        this.format.setCharSetEncoding(charSetEncoding);
    }

    public String getXmlVersion() {
        return this.format.getXmlVersion();
    }

    public void setXmlVersion(String xmlVersion) {
        this.format.setXmlVersion(xmlVersion);
    }

    public void setSoap11(boolean b) {
        this.format.setSOAP11(b);
    }

    public boolean isIgnoreXMLDeclaration() {
        return this.format.isIgnoreXMLDeclaration();
    }

    public void setIgnoreXMLDeclaration(boolean ignoreXMLDeclaration) {
        this.format.setIgnoreXMLDeclaration(ignoreXMLDeclaration);
    }

    public void setDoOptimize(boolean b) {
        this.format.setDoOptimize(b);
    }

    public OMOutputFormat getOutputFormat() {
        return this.format;
    }

    public void setOutputFormat(OMOutputFormat format) {
        this.format = format;
    }

    public OutputStream getOutputStream() throws XMLStreamException {
        if (this.xmlStreamWriterFilter != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("getOutputStream returning null due to presence of XMLStreamWriterFilter " + this.xmlStreamWriterFilter));
            }
            return null;
        }
        OutputStream os = null;
        os = this.rootPartOutputStream != null ? this.rootPartOutputStream : this.outStream;
        if (log.isDebugEnabled()) {
            if (os == null) {
                log.debug((Object)"Direct access to the output stream is not available.");
            } else if (this.rootPartOutputStream != null) {
                log.debug((Object)("Returning access to the buffered xml stream: " + this.rootPartOutputStream));
            } else {
                log.debug((Object)("Returning access to the original output stream: " + os));
            }
        }
        if (os != null) {
            this.writeCharacters("");
            this.flush();
        }
        return os;
    }

    public void setFilter(XMLStreamWriterFilter filter) {
        if (filter != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("setting filter " + filter.getClass()));
            }
            this.xmlStreamWriterFilter = filter;
            filter.setDelegate(this.xmlWriter);
            this.xmlWriter = filter;
        }
    }

    public XMLStreamWriterFilter removeFilter() {
        XMLStreamWriterFilter filter = null;
        if (this.xmlStreamWriterFilter != null) {
            filter = this.xmlStreamWriterFilter;
            if (log.isDebugEnabled()) {
                log.debug((Object)("removing filter " + filter.getClass()));
            }
            this.xmlWriter = this.xmlStreamWriterFilter.getDelegate();
            filter.setDelegate(null);
            this.xmlStreamWriterFilter = this.xmlWriter instanceof XMLStreamWriterFilter ? (XMLStreamWriterFilter)this.xmlWriter : null;
        }
        return filter;
    }

    private static class Part {
        private final String contentID;
        private final DataHandler dataHandler;

        public Part(String contentID, DataHandler dataHandler) {
            this.contentID = contentID;
            this.dataHandler = dataHandler;
        }

        public String getContentID() {
            return this.contentID;
        }

        public DataHandler getDataHandler() {
            return this.dataHandler;
        }
    }
}

