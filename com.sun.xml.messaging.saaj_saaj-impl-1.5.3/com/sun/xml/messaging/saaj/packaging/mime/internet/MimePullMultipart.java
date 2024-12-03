/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  org.jvnet.mimepull.MIMEConfig
 *  org.jvnet.mimepull.MIMEMessage
 *  org.jvnet.mimepull.MIMEPart
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.sun.xml.messaging.saaj.soap.AttachmentPartImpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.activation.DataSource;
import org.jvnet.mimepull.MIMEConfig;
import org.jvnet.mimepull.MIMEMessage;
import org.jvnet.mimepull.MIMEPart;

public class MimePullMultipart
extends MimeMultipart {
    private InputStream in = null;
    private String boundary = null;
    private MIMEMessage mm = null;
    private DataSource dataSource = null;
    private ContentType contType = null;
    private String startParam = null;
    private MIMEPart soapPart = null;

    public MimePullMultipart(DataSource ds, ContentType ct) throws MessagingException {
        this.parsed = false;
        this.contType = ct == null ? new ContentType(ds.getContentType()) : ct;
        this.dataSource = ds;
        this.boundary = this.contType.getParameter("boundary");
    }

    public MIMEPart readAndReturnSOAPPart() throws MessagingException {
        if (this.soapPart != null) {
            throw new MessagingException("Inputstream from datasource was already consumed");
        }
        this.readSOAPPart();
        return this.soapPart;
    }

    protected void readSOAPPart() throws MessagingException {
        try {
            if (this.soapPart != null) {
                return;
            }
            this.in = this.dataSource.getInputStream();
            MIMEConfig config = new MIMEConfig();
            this.mm = new MIMEMessage(this.in, this.boundary, config);
            String st = this.contType.getParameter("start");
            if (this.startParam == null) {
                this.soapPart = this.mm.getPart(0);
            } else {
                if (st != null && st.length() > 2 && st.charAt(0) == '<' && st.charAt(st.length() - 1) == '>') {
                    st = st.substring(1, st.length() - 1);
                }
                this.startParam = st;
                this.soapPart = this.mm.getPart(this.startParam);
            }
        }
        catch (IOException ex) {
            throw new MessagingException("No inputstream from datasource", ex);
        }
    }

    public void parseAll() throws MessagingException {
        if (this.parsed) {
            return;
        }
        if (this.soapPart == null) {
            this.readSOAPPart();
        }
        List prts = this.mm.getAttachments();
        for (MIMEPart part : prts) {
            if (part == this.soapPart) continue;
            new AttachmentPartImpl(part);
            this.addBodyPart(new MimeBodyPart(part));
        }
        this.parsed = true;
    }

    @Override
    protected void parse() throws MessagingException {
        this.parseAll();
    }
}

