/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.util.Iterator;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;
import org.w3c.dom.Document;

public abstract class SOAPPart
implements Document,
Node {
    public abstract SOAPEnvelope getEnvelope() throws SOAPException;

    public String getContentId() {
        String[] values = this.getMimeHeader("Content-Id");
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

    public void setContentId(String contentId) {
        this.setMimeHeader("Content-Id", contentId);
    }

    public void setContentLocation(String contentLocation) {
        this.setMimeHeader("Content-Location", contentLocation);
    }

    public abstract void removeMimeHeader(String var1);

    public abstract void removeAllMimeHeaders();

    public abstract String[] getMimeHeader(String var1);

    public abstract void setMimeHeader(String var1, String var2);

    public abstract void addMimeHeader(String var1, String var2);

    public abstract Iterator<MimeHeader> getAllMimeHeaders();

    public abstract Iterator<MimeHeader> getMatchingMimeHeaders(String[] var1);

    public abstract Iterator<MimeHeader> getNonMatchingMimeHeaders(String[] var1);

    public abstract void setContent(Source var1) throws SOAPException;

    public abstract Source getContent() throws SOAPException;
}

