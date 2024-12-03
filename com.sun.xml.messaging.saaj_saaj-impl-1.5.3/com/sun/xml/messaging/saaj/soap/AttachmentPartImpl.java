/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.CommandInfo
 *  javax.activation.CommandMap
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.activation.MailcapCommandMap
 *  javax.xml.soap.AttachmentPart
 *  javax.xml.soap.MimeHeader
 *  javax.xml.soap.MimeHeaders
 *  javax.xml.soap.SOAPException
 *  org.jvnet.mimepull.Header
 *  org.jvnet.mimepull.MIMEPart
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.packaging.mime.internet.InternetHeaders;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimePartDataSource;
import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.sun.xml.messaging.saaj.packaging.mime.internet.hdr;
import com.sun.xml.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.messaging.saaj.util.FinalArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MailcapCommandMap;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import org.jvnet.mimepull.Header;
import org.jvnet.mimepull.MIMEPart;

public class AttachmentPartImpl
extends AttachmentPart {
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap", "com.sun.xml.messaging.saaj.soap.LocalStrings");
    private final MimeHeaders headers = new MimeHeaders();
    private MimeBodyPart rawContent = null;
    private DataHandler dataHandler = null;
    private MIMEPart mimePart = null;

    public AttachmentPartImpl() {
        AttachmentPartImpl.initializeJavaActivationHandlers();
    }

    public AttachmentPartImpl(MIMEPart part) {
        this.mimePart = part;
        List hdrs = part.getAllHeaders();
        for (Header hd : hdrs) {
            this.headers.addHeader(hd.getName(), hd.getValue());
        }
    }

    public int getSize() throws SOAPException {
        if (this.mimePart != null) {
            try {
                return this.mimePart.read().available();
            }
            catch (IOException e) {
                return -1;
            }
        }
        if (this.rawContent == null && this.dataHandler == null) {
            return 0;
        }
        if (this.rawContent != null) {
            try {
                return this.rawContent.getSize();
            }
            catch (Exception ex) {
                log.log(Level.SEVERE, "SAAJ0573.soap.attachment.getrawbytes.ioexception", new String[]{ex.getLocalizedMessage()});
                throw new SOAPExceptionImpl("Raw InputStream Error: " + ex);
            }
        }
        ByteOutputStream bout = new ByteOutputStream();
        try {
            this.dataHandler.writeTo((OutputStream)bout);
        }
        catch (IOException ex) {
            log.log(Level.SEVERE, "SAAJ0501.soap.data.handler.err", new String[]{ex.getLocalizedMessage()});
            throw new SOAPExceptionImpl("Data handler error: " + ex);
        }
        return bout.size();
    }

    public void clearContent() {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        this.dataHandler = null;
        this.rawContent = null;
    }

    public Object getContent() throws SOAPException {
        try {
            if (this.mimePart != null) {
                return this.mimePart.read();
            }
            if (this.dataHandler != null) {
                return this.getDataHandler().getContent();
            }
            if (this.rawContent != null) {
                return this.rawContent.getContent();
            }
            log.severe("SAAJ0572.soap.no.content.for.attachment");
            throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
        }
        catch (Exception ex) {
            log.log(Level.SEVERE, "SAAJ0575.soap.attachment.getcontent.exception", ex);
            throw new SOAPExceptionImpl(ex.getLocalizedMessage());
        }
    }

    public void setContent(Object object, String contentType) throws IllegalArgumentException {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        DataHandler dh = new DataHandler(object, contentType);
        this.setDataHandler(dh);
    }

    public DataHandler getDataHandler() throws SOAPException {
        if (this.mimePart != null) {
            return new DataHandler(new DataSource(){

                public InputStream getInputStream() throws IOException {
                    return AttachmentPartImpl.this.mimePart.read();
                }

                public OutputStream getOutputStream() throws IOException {
                    throw new UnsupportedOperationException("getOutputStream cannot be supported : You have enabled LazyAttachments Option");
                }

                public String getContentType() {
                    return AttachmentPartImpl.this.mimePart.getContentType();
                }

                public String getName() {
                    return "MIMEPart Wrapper DataSource";
                }
            });
        }
        if (this.dataHandler == null) {
            if (this.rawContent != null) {
                return new DataHandler((DataSource)new MimePartDataSource(this.rawContent));
            }
            log.severe("SAAJ0502.soap.no.handler.for.attachment");
            throw new SOAPExceptionImpl("No data handler associated with this attachment");
        }
        return this.dataHandler;
    }

    public void setDataHandler(DataHandler dataHandler) throws IllegalArgumentException {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        if (dataHandler == null) {
            log.severe("SAAJ0503.soap.no.null.to.dataHandler");
            throw new IllegalArgumentException("Null dataHandler argument to setDataHandler");
        }
        this.dataHandler = dataHandler;
        this.rawContent = null;
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "SAAJ0580.soap.set.Content-Type", new String[]{dataHandler.getContentType()});
        }
        this.setMimeHeader("Content-Type", dataHandler.getContentType());
    }

    public void removeAllMimeHeaders() {
        this.headers.removeAllHeaders();
    }

    public void removeMimeHeader(String header) {
        this.headers.removeHeader(header);
    }

    public String[] getMimeHeader(String name) {
        return this.headers.getHeader(name);
    }

    public void setMimeHeader(String name, String value) {
        this.headers.setHeader(name, value);
    }

    public void addMimeHeader(String name, String value) {
        this.headers.addHeader(name, value);
    }

    public Iterator<MimeHeader> getAllMimeHeaders() {
        return this.headers.getAllHeaders();
    }

    public Iterator<MimeHeader> getMatchingMimeHeaders(String[] names) {
        return this.headers.getMatchingHeaders(names);
    }

    public Iterator<MimeHeader> getNonMatchingMimeHeaders(String[] names) {
        return this.headers.getNonMatchingHeaders(names);
    }

    boolean hasAllHeaders(MimeHeaders hdrs) {
        if (hdrs != null) {
            Iterator i = hdrs.getAllHeaders();
            while (i.hasNext()) {
                MimeHeader hdr2 = (MimeHeader)i.next();
                String[] values = this.headers.getHeader(hdr2.getName());
                boolean found = false;
                if (values != null) {
                    for (int j = 0; j < values.length; ++j) {
                        if (!hdr2.getValue().equalsIgnoreCase(values[j])) continue;
                        found = true;
                        break;
                    }
                }
                if (found) continue;
                return false;
            }
        }
        return true;
    }

    MimeBodyPart getMimePart() throws SOAPException {
        try {
            if (this.mimePart != null) {
                return new MimeBodyPart(this.mimePart);
            }
            if (this.rawContent != null) {
                AttachmentPartImpl.copyMimeHeaders(this.headers, this.rawContent);
                return this.rawContent;
            }
            MimeBodyPart envelope = new MimeBodyPart();
            envelope.setDataHandler(this.dataHandler);
            AttachmentPartImpl.copyMimeHeaders(this.headers, envelope);
            return envelope;
        }
        catch (Exception ex) {
            log.severe("SAAJ0504.soap.cannot.externalize.attachment");
            throw new SOAPExceptionImpl("Unable to externalize attachment", ex);
        }
    }

    public static void copyMimeHeaders(MimeHeaders headers, MimeBodyPart mbp) throws SOAPException {
        Iterator i = headers.getAllHeaders();
        while (i.hasNext()) {
            try {
                MimeHeader mh = (MimeHeader)i.next();
                mbp.setHeader(mh.getName(), mh.getValue());
            }
            catch (Exception ex) {
                log.severe("SAAJ0505.soap.cannot.copy.mime.hdr");
                throw new SOAPExceptionImpl("Unable to copy MIME header", ex);
            }
        }
    }

    public static void copyMimeHeaders(MimeBodyPart mbp, AttachmentPartImpl ap) throws SOAPException {
        try {
            FinalArrayList<hdr> hdr2 = mbp.getAllHeaders();
            int sz = hdr2.size();
            for (int i = 0; i < sz; ++i) {
                com.sun.xml.messaging.saaj.packaging.mime.Header h = (com.sun.xml.messaging.saaj.packaging.mime.Header)hdr2.get(i);
                if (h.getName().equalsIgnoreCase("Content-Type")) continue;
                ap.addMimeHeader(h.getName(), h.getValue());
            }
        }
        catch (Exception ex) {
            log.severe("SAAJ0506.soap.cannot.copy.mime.hdrs.into.attachment");
            throw new SOAPExceptionImpl("Unable to copy MIME headers into attachment", ex);
        }
    }

    public void setBase64Content(InputStream content, String contentType) throws SOAPException {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        this.dataHandler = null;
        InputStream decoded = null;
        ByteOutputStream bos = null;
        try {
            decoded = MimeUtility.decode(content, "base64");
            InternetHeaders hdrs = new InternetHeaders();
            hdrs.setHeader("Content-Type", contentType);
            bos = new ByteOutputStream();
            bos.write(decoded);
            this.rawContent = new MimeBodyPart(hdrs, bos.getBytes(), bos.getCount());
            this.setMimeHeader("Content-Type", contentType);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "SAAJ0578.soap.attachment.setbase64content.exception", e);
            throw new SOAPExceptionImpl(e.getLocalizedMessage());
        }
        finally {
            if (bos != null) {
                bos.close();
            }
            try {
                if (decoded != null) {
                    decoded.close();
                }
            }
            catch (IOException ex) {
                throw new SOAPException((Throwable)ex);
            }
        }
    }

    public InputStream getBase64Content() throws SOAPException {
        InputStream stream;
        if (this.mimePart != null) {
            stream = this.mimePart.read();
        } else if (this.rawContent != null) {
            try {
                stream = this.rawContent.getInputStream();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "SAAJ0579.soap.attachment.getbase64content.exception", e);
                throw new SOAPExceptionImpl(e.getLocalizedMessage());
            }
        } else if (this.dataHandler != null) {
            try {
                stream = this.dataHandler.getInputStream();
            }
            catch (IOException e) {
                log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
                throw new SOAPExceptionImpl("DataHandler error" + e);
            }
        } else {
            log.severe("SAAJ0572.soap.no.content.for.attachment");
            throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
        }
        int size = 1024;
        if (stream != null) {
            try {
                int len;
                ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
                OutputStream ret = MimeUtility.encode(bos, "base64");
                byte[] buf = new byte[size];
                while ((len = stream.read(buf, 0, size)) != -1) {
                    ret.write(buf, 0, len);
                }
                ret.flush();
                buf = bos.toByteArray();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
                return byteArrayInputStream;
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "SAAJ0579.soap.attachment.getbase64content.exception", e);
                throw new SOAPExceptionImpl(e.getLocalizedMessage());
            }
            finally {
                try {
                    stream.close();
                }
                catch (IOException iOException) {}
            }
        }
        log.log(Level.SEVERE, "SAAJ0572.soap.no.content.for.attachment");
        throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
    }

    public void setRawContent(InputStream content, String contentType) throws SOAPException {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        this.dataHandler = null;
        ByteOutputStream bos = null;
        try {
            InternetHeaders hdrs = new InternetHeaders();
            hdrs.setHeader("Content-Type", contentType);
            bos = new ByteOutputStream();
            bos.write(content);
            this.rawContent = new MimeBodyPart(hdrs, bos.getBytes(), bos.getCount());
            this.setMimeHeader("Content-Type", contentType);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "SAAJ0576.soap.attachment.setrawcontent.exception", e);
            throw new SOAPExceptionImpl(e.getLocalizedMessage());
        }
        finally {
            if (bos != null) {
                bos.close();
            }
            try {
                content.close();
            }
            catch (IOException ex) {
                throw new SOAPException((Throwable)ex);
            }
        }
    }

    public void setRawContentBytes(byte[] content, int off, int len, String contentType) throws SOAPException {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        if (content == null) {
            throw new SOAPExceptionImpl("Null content passed to setRawContentBytes");
        }
        this.dataHandler = null;
        try {
            InternetHeaders hdrs = new InternetHeaders();
            hdrs.setHeader("Content-Type", contentType);
            this.rawContent = new MimeBodyPart(hdrs, content, off, len);
            this.setMimeHeader("Content-Type", contentType);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "SAAJ0576.soap.attachment.setrawcontent.exception", e);
            throw new SOAPExceptionImpl(e.getLocalizedMessage());
        }
    }

    public InputStream getRawContent() throws SOAPException {
        if (this.mimePart != null) {
            return this.mimePart.read();
        }
        if (this.rawContent != null) {
            try {
                return this.rawContent.getInputStream();
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", e);
                throw new SOAPExceptionImpl(e.getLocalizedMessage());
            }
        }
        if (this.dataHandler != null) {
            try {
                return this.dataHandler.getInputStream();
            }
            catch (IOException e) {
                log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
                throw new SOAPExceptionImpl("DataHandler error" + e);
            }
        }
        log.severe("SAAJ0572.soap.no.content.for.attachment");
        throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
    }

    public byte[] getRawContentBytes() throws SOAPException {
        if (this.mimePart != null) {
            try {
                InputStream ret = this.mimePart.read();
                return ASCIIUtility.getBytes(ret);
            }
            catch (IOException ex) {
                log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", ex);
                throw new SOAPExceptionImpl(ex);
            }
        }
        if (this.rawContent != null) {
            try {
                InputStream ret = this.rawContent.getInputStream();
                return ASCIIUtility.getBytes(ret);
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", e);
                throw new SOAPExceptionImpl(e);
            }
        }
        if (this.dataHandler != null) {
            try {
                InputStream ret = this.dataHandler.getInputStream();
                return ASCIIUtility.getBytes(ret);
            }
            catch (IOException e) {
                log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
                throw new SOAPExceptionImpl("DataHandler error" + e);
            }
        }
        log.severe("SAAJ0572.soap.no.content.for.attachment");
        throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public MimeHeaders getMimeHeaders() {
        return this.headers;
    }

    public static void initializeJavaActivationHandlers() {
        try {
            MailcapCommandMap mailMap;
            CommandMap map = CommandMap.getDefaultCommandMap();
            if (map instanceof MailcapCommandMap && !AttachmentPartImpl.cmdMapInitialized(mailMap = (MailcapCommandMap)map)) {
                mailMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.messaging.saaj.soap.XmlDataContentHandler");
                mailMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.messaging.saaj.soap.XmlDataContentHandler");
                mailMap.addMailcap("application/fastinfoset;;x-java-content-handler=com.sun.xml.messaging.saaj.soap.FastInfosetDataContentHandler");
                mailMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.messaging.saaj.soap.ImageDataContentHandler");
                mailMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.messaging.saaj.soap.StringDataContentHandler");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean cmdMapInitialized(MailcapCommandMap mailMap) {
        CommandInfo[] commands = mailMap.getAllCommands("application/fastinfoset");
        if (commands == null || commands.length == 0) {
            return false;
        }
        String saajClassName = "com.sun.xml.messaging.saaj.soap.FastInfosetDataContentHandler";
        for (CommandInfo command : commands) {
            String commandClass = command.getCommandClass();
            if (!saajClassName.equals(commandClass)) continue;
            return true;
        }
        return false;
    }
}

