/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.om.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.activation.DataHandler;
import org.apache.axiom.attachments.ConfigurableDataHandler;
import org.apache.axiom.mime.MultipartWriter;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.util.CommonUtils;

public class OMMultipartWriter {
    private final OMOutputFormat format;
    private final MultipartWriter writer;
    private final boolean useCTEBase64;
    private final String rootPartContentType;

    public OMMultipartWriter(OutputStream out, OMOutputFormat format) {
        this.format = format;
        this.writer = format.getMultipartWriterFactory().createMultipartWriter(out, format.getMimeBoundary());
        this.useCTEBase64 = format != null && Boolean.TRUE.equals(format.getProperty("org.apache.axiom.om.OMFormat.use.cteBase64.forNonTextualAttachments"));
        String soapContentType = format.isSOAP11() ? "text/xml" : "application/soap+xml";
        this.rootPartContentType = format.isOptimized() ? "application/xop+xml; charset=" + format.getCharSetEncoding() + "; type=\"" + soapContentType + "\"" : soapContentType + "; charset=" + format.getCharSetEncoding();
    }

    private String getContentTransferEncoding(String contentType) {
        if (this.useCTEBase64 && !CommonUtils.isTextualPart(contentType)) {
            return "base64";
        }
        return "binary";
    }

    public String getRootPartContentType() {
        return this.rootPartContentType;
    }

    public OutputStream writeRootPart() throws IOException {
        return this.writer.writePart(this.rootPartContentType, "binary", this.format.getRootContentId());
    }

    public OutputStream writePart(String contentType, String contentID) throws IOException {
        return this.writer.writePart(contentType, this.getContentTransferEncoding(contentType), contentID);
    }

    public OutputStream writePart(String contentType, String contentID, List extraHeaders) throws IOException {
        return this.writer.writePart(contentType, this.getContentTransferEncoding(contentType), contentID, extraHeaders);
    }

    public void writePart(DataHandler dataHandler, String contentID, List extraHeaders) throws IOException {
        String contentTransferEncoding = null;
        if (dataHandler instanceof ConfigurableDataHandler) {
            contentTransferEncoding = ((ConfigurableDataHandler)dataHandler).getTransferEncoding();
        }
        if (contentTransferEncoding == null) {
            contentTransferEncoding = this.getContentTransferEncoding(dataHandler.getContentType());
        }
        this.writer.writePart(dataHandler, contentTransferEncoding, contentID, extraHeaders);
    }

    public void writePart(DataHandler dataHandler, String contentID) throws IOException {
        this.writePart(dataHandler, contentID, null);
    }

    public void complete() throws IOException {
        this.writer.complete();
    }
}

