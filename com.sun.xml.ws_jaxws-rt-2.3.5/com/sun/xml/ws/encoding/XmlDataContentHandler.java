/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.ActivationDataFlavor
 *  javax.activation.DataContentHandler
 *  javax.activation.DataSource
 */
package com.sun.xml.ws.encoding;

import com.sun.xml.ws.encoding.ContentType;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlDataContentHandler
implements DataContentHandler {
    private final DataFlavor[] flavors = new DataFlavor[3];

    public XmlDataContentHandler() throws ClassNotFoundException {
        this.flavors[0] = new ActivationDataFlavor(StreamSource.class, "text/xml", "XML");
        this.flavors[1] = new ActivationDataFlavor(StreamSource.class, "application/xml", "XML");
        this.flavors[2] = new ActivationDataFlavor(String.class, "text/xml", "XML String");
    }

    public DataFlavor[] getTransferDataFlavors() {
        return Arrays.copyOf(this.flavors, this.flavors.length);
    }

    public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
        for (DataFlavor aFlavor : this.flavors) {
            if (!aFlavor.equals(df)) continue;
            return this.getContent(ds);
        }
        return null;
    }

    public Object getContent(DataSource ds) throws IOException {
        String ctStr = ds.getContentType();
        String charset = null;
        if (ctStr != null) {
            ContentType ct = new ContentType(ctStr);
            if (!this.isXml(ct)) {
                throw new IOException("Cannot convert DataSource with content type \"" + ctStr + "\" to object in XmlDataContentHandler");
            }
            charset = ct.getParameter("charset");
        }
        return charset != null ? new StreamSource(new InputStreamReader(ds.getInputStream()), charset) : new StreamSource(ds.getInputStream());
    }

    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        if (!(obj instanceof DataSource || obj instanceof Source || obj instanceof String)) {
            throw new IOException("Invalid Object type = " + obj.getClass() + ". XmlDataContentHandler can only convert DataSource|Source|String to XML.");
        }
        ContentType ct = new ContentType(mimeType);
        if (!this.isXml(ct)) {
            throw new IOException("Invalid content type \"" + mimeType + "\" for XmlDataContentHandler");
        }
        String charset = ct.getParameter("charset");
        if (obj instanceof String) {
            String s = (String)obj;
            if (charset == null) {
                charset = "utf-8";
            }
            OutputStreamWriter osw = new OutputStreamWriter(os, charset);
            osw.write(s, 0, s.length());
            osw.flush();
            return;
        }
        Source source = obj instanceof DataSource ? (Source)this.getContent((DataSource)obj) : (Source)obj;
        try {
            Transformer transformer = XmlUtil.newTransformer();
            if (charset != null) {
                transformer.setOutputProperty("encoding", charset);
            }
            StreamResult result = new StreamResult(os);
            transformer.transform(source, result);
        }
        catch (Exception ex) {
            throw new IOException("Unable to run the JAXP transformer in XmlDataContentHandler " + ex.getMessage());
        }
    }

    private boolean isXml(ContentType ct) {
        String primaryType = ct.getPrimaryType();
        return ct.getSubType().equalsIgnoreCase("xml") && (primaryType.equalsIgnoreCase("text") || primaryType.equalsIgnoreCase("application"));
    }
}

