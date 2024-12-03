/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.activation.FileDataSource
 *  javax.mail.BodyPart
 *  javax.mail.Header
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.Session
 *  javax.mail.internet.ContentType
 *  javax.mail.internet.InternetHeaders
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMultipart
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.attachments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.soap.MimeHeader;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.Part;
import org.apache.axis.attachments.AttachmentUtils;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.SessionUtils;
import org.apache.commons.logging.Log;

public class MimeUtils {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$attachments$MimeUtils == null ? (class$org$apache$axis$attachments$MimeUtils = MimeUtils.class$("org.apache.axis.attachments.MimeUtils")) : class$org$apache$axis$attachments$MimeUtils).getName());
    public static String[] filter = new String[]{"Message-ID", "Mime-Version", "Content-Type"};
    static /* synthetic */ Class class$org$apache$axis$attachments$MimeUtils;

    public static long getContentLength(Multipart mp) throws MessagingException, IOException {
        int totalParts = mp.getCount();
        long totalContentLength = 0L;
        for (int i = 0; i < totalParts; ++i) {
            MimeBodyPart bp = (MimeBodyPart)mp.getBodyPart(i);
            totalContentLength += MimeUtils.getContentLength(bp);
        }
        String ctype = mp.getContentType();
        ContentType ct = new ContentType(ctype);
        String boundaryStr = ct.getParameter("boundary");
        int boundaryStrLen = boundaryStr.length() + 4;
        return totalContentLength + (long)(boundaryStrLen * (totalParts + 1)) + (long)(2 * totalParts) + 4L;
    }

    protected static long getContentLength(MimeBodyPart bp) {
        long headerLength = -1L;
        long dataSize = -1L;
        try {
            headerLength = MimeUtils.getHeaderLength(bp);
            DataHandler dh = bp.getDataHandler();
            DataSource ds = dh.getDataSource();
            if (ds instanceof FileDataSource) {
                FileDataSource fdh = (FileDataSource)ds;
                File df = fdh.getFile();
                if (!df.exists()) {
                    throw new RuntimeException(Messages.getMessage("noFile", df.getAbsolutePath()));
                }
                dataSize = df.length();
            } else {
                dataSize = bp.getSize();
                if (-1L == dataSize) {
                    int bytesread;
                    dataSize = 0L;
                    InputStream in = ds.getInputStream();
                    byte[] readbuf = new byte[65536];
                    do {
                        if ((bytesread = in.read(readbuf)) <= 0) continue;
                        dataSize += (long)bytesread;
                    } while (bytesread > -1);
                    in.close();
                }
            }
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
        }
        return dataSize + headerLength;
    }

    private static long getHeaderLength(MimeBodyPart bp) throws MessagingException, IOException {
        MimeBodyPart headersOnly = new MimeBodyPart(new InternetHeaders(), new byte[0]);
        Enumeration en = bp.getAllHeaders();
        while (en.hasMoreElements()) {
            Header header = (Header)en.nextElement();
            headersOnly.addHeader(header.getName(), header.getValue());
        }
        ByteArrayOutputStream bas = new ByteArrayOutputStream(16384);
        headersOnly.writeTo((OutputStream)bas);
        bas.close();
        return bas.size();
    }

    public static void writeToMultiPartStream(OutputStream os, MimeMultipart mp) {
        try {
            Properties props = AxisProperties.getProperties();
            props.setProperty("mail.smtp.host", "localhost");
            Session session = Session.getInstance((Properties)props, null);
            MimeMessage message = new MimeMessage(session);
            message.setContent((Multipart)mp);
            message.saveChanges();
            message.writeTo(os, filter);
        }
        catch (MessagingException e) {
            log.error((Object)Messages.getMessage("javaxMailMessagingException00"), (Throwable)e);
        }
        catch (IOException e) {
            log.error((Object)Messages.getMessage("javaIOException00"), (Throwable)e);
        }
    }

    public static String getContentType(MimeMultipart mp) {
        StringBuffer contentType = new StringBuffer(mp.getContentType());
        int i = 0;
        while (i < contentType.length()) {
            char ch = contentType.charAt(i);
            if (ch == '\r' || ch == '\n') {
                contentType.deleteCharAt(i);
                continue;
            }
            ++i;
        }
        return contentType.toString();
    }

    public static MimeMultipart createMP(String env, Collection parts) throws AxisFault {
        MimeMultipart multipart = null;
        try {
            String rootCID = SessionUtils.generateSessionId();
            multipart = new MimeMultipart("related; type=\"text/xml\"; start=\"<" + rootCID + ">\"");
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(env, "UTF-8");
            messageBodyPart.setHeader("Content-Type", "text/xml; charset=UTF-8");
            messageBodyPart.setHeader("Content-Id", "<" + rootCID + ">");
            messageBodyPart.setHeader("Content-Transfer-Encoding", "binary");
            multipart.addBodyPart((BodyPart)messageBodyPart);
            Iterator it = parts.iterator();
            while (it.hasNext()) {
                Part part = (Part)it.next();
                DataHandler dh = AttachmentUtils.getActivationDataHandler(part);
                String contentID = part.getContentId();
                messageBodyPart = new MimeBodyPart();
                messageBodyPart.setDataHandler(dh);
                String contentType = part.getContentType();
                if (contentType == null || contentType.trim().length() == 0) {
                    contentType = dh.getContentType();
                }
                if (contentType == null || contentType.trim().length() == 0) {
                    contentType = "application/octet-stream";
                }
                messageBodyPart.setHeader("Content-Type", contentType);
                messageBodyPart.setHeader("Content-Id", "<" + contentID + ">");
                messageBodyPart.setHeader("Content-Transfer-Encoding", "binary");
                Iterator i = part.getNonMatchingMimeHeaders(new String[]{"Content-Type", "Content-Id", "Content-Transfer-Encoding"});
                while (i.hasNext()) {
                    MimeHeader header = (MimeHeader)i.next();
                    messageBodyPart.setHeader(header.getName(), header.getValue());
                }
                multipart.addBodyPart((BodyPart)messageBodyPart);
            }
        }
        catch (MessagingException e) {
            log.error((Object)Messages.getMessage("javaxMailMessagingException00"), (Throwable)e);
        }
        return multipart;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

