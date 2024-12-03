/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package javax.xml.soap;

import java.io.InputStream;
import java.util.Iterator;
import javax.activation.DataHandler;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPException;

public abstract class AttachmentPart {
    public abstract int getSize() throws SOAPException;

    public abstract void clearContent();

    public abstract Object getContent() throws SOAPException;

    public abstract InputStream getRawContent() throws SOAPException;

    public abstract byte[] getRawContentBytes() throws SOAPException;

    public abstract InputStream getBase64Content() throws SOAPException;

    public abstract void setContent(Object var1, String var2);

    public abstract void setRawContent(InputStream var1, String var2) throws SOAPException;

    public abstract void setRawContentBytes(byte[] var1, int var2, int var3, String var4) throws SOAPException;

    public abstract void setBase64Content(InputStream var1, String var2) throws SOAPException;

    public abstract DataHandler getDataHandler() throws SOAPException;

    public abstract void setDataHandler(DataHandler var1);

    public String getContentId() {
        String[] values = this.getMimeHeader("Content-ID");
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    public String getContentLocation() {
        String[] values = this.getMimeHeader("Content-Location");
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    public String getContentType() {
        String[] values = this.getMimeHeader("Content-Type");
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    public void setContentId(String contentId) {
        this.setMimeHeader("Content-ID", contentId);
    }

    public void setContentLocation(String contentLocation) {
        this.setMimeHeader("Content-Location", contentLocation);
    }

    public void setContentType(String contentType) {
        this.setMimeHeader("Content-Type", contentType);
    }

    public abstract void removeMimeHeader(String var1);

    public abstract void removeAllMimeHeaders();

    public abstract String[] getMimeHeader(String var1);

    public abstract void setMimeHeader(String var1, String var2);

    public abstract void addMimeHeader(String var1, String var2);

    public abstract Iterator<MimeHeader> getAllMimeHeaders();

    public abstract Iterator<MimeHeader> getMatchingMimeHeaders(String[] var1);

    public abstract Iterator<MimeHeader> getNonMatchingMimeHeaders(String[] var1);
}

