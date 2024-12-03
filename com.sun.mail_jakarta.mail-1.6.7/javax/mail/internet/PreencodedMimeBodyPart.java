/*
 * Decompiled with CFR 0.152.
 */
package javax.mail.internet;

import com.sun.mail.util.LineOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

public class PreencodedMimeBodyPart
extends MimeBodyPart {
    private String encoding;

    public PreencodedMimeBodyPart(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String getEncoding() throws MessagingException {
        return this.encoding;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException, MessagingException {
        LineOutputStream los = null;
        los = os instanceof LineOutputStream ? (LineOutputStream)os : new LineOutputStream(os);
        Enumeration<String> hdrLines = this.getAllHeaderLines();
        while (hdrLines.hasMoreElements()) {
            los.writeln(hdrLines.nextElement());
        }
        los.writeln();
        this.getDataHandler().writeTo(os);
        os.flush();
    }

    @Override
    protected void updateHeaders() throws MessagingException {
        super.updateHeaders();
        MimeBodyPart.setEncoding(this, this.encoding);
    }
}

