/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Address
 *  javax.mail.Session
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMessage$RecipientType
 *  org.apache.commons.logging.Log
 *  org.apache.commons.net.pop3.POP3Client
 *  org.apache.commons.net.pop3.POP3MessageInfo
 *  org.apache.commons.net.smtp.SMTPClient
 *  org.apache.commons.net.smtp.SMTPReply
 */
package org.apache.axis.transport.mail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.rmi.server.UID;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.pop3.POP3MessageInfo;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;

public class MailSender
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$mail$MailSender == null ? (class$org$apache$axis$transport$mail$MailSender = MailSender.class$("org.apache.axis.transport.mail.MailSender")) : class$org$apache$axis$transport$mail$MailSender).getName());
    private UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
    Properties prop = new Properties();
    Session session = Session.getDefaultInstance((Properties)this.prop, null);
    static /* synthetic */ Class class$org$apache$axis$transport$mail$MailSender;

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("enter00", "MailSender::invoke"));
        }
        try {
            String id = this.writeUsingSMTP(msgContext);
            this.readUsingPOP3(id, msgContext);
        }
        catch (Exception e) {
            log.debug((Object)e);
            throw AxisFault.makeFault(e);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("exit00", "HTTPDispatchHandler::invoke"));
        }
    }

    private String writeUsingSMTP(MessageContext msgContext) throws Exception {
        String action;
        String id = new UID().toString();
        String smtpHost = msgContext.getStrProp("transport.mail.smtp.host");
        SMTPClient client = new SMTPClient();
        client.connect(smtpHost);
        System.out.print(client.getReplyString());
        int reply = client.getReplyCode();
        if (!SMTPReply.isPositiveCompletion((int)reply)) {
            client.disconnect();
            AxisFault fault = new AxisFault("SMTP", "( SMTP server refused connection )", null, null);
            throw fault;
        }
        client.login(smtpHost);
        System.out.print(client.getReplyString());
        reply = client.getReplyCode();
        if (!SMTPReply.isPositiveCompletion((int)reply)) {
            client.disconnect();
            AxisFault fault = new AxisFault("SMTP", "( SMTP server refused connection )", null, null);
            throw fault;
        }
        String fromAddress = msgContext.getStrProp("transport.mail.from");
        String toAddress = msgContext.getStrProp("transport.mail.to");
        MimeMessage msg = new MimeMessage(this.session);
        msg.setFrom((Address)new InternetAddress(fromAddress));
        msg.addRecipient(MimeMessage.RecipientType.TO, (Address)new InternetAddress(toAddress));
        String string = action = msgContext.useSOAPAction() ? msgContext.getSOAPActionURI() : "";
        if (action == null) {
            action = "";
        }
        Message reqMessage = msgContext.getRequestMessage();
        msg.addHeader("User-Agent", Messages.getMessage("axisUserAgent"));
        msg.addHeader("SOAPAction", action);
        msg.setDisposition("inline");
        msg.setSubject(id);
        ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
        reqMessage.writeTo(out);
        msg.setContent((Object)out.toString(), reqMessage.getContentType(msgContext.getSOAPConstants()));
        ByteArrayOutputStream out2 = new ByteArrayOutputStream(8192);
        msg.writeTo((OutputStream)out2);
        client.setSender(fromAddress);
        System.out.print(client.getReplyString());
        client.addRecipient(toAddress);
        System.out.print(client.getReplyString());
        Writer writer = client.sendMessageData();
        System.out.print(client.getReplyString());
        writer.write(out2.toString());
        writer.flush();
        writer.close();
        System.out.print(client.getReplyString());
        if (!client.completePendingCommand()) {
            System.out.print(client.getReplyString());
            AxisFault fault = new AxisFault("SMTP", "( Failed to send email )", null, null);
            throw fault;
        }
        System.out.print(client.getReplyString());
        client.logout();
        client.disconnect();
        return id;
    }

    private void readUsingPOP3(String id, MessageContext msgContext) throws Exception {
        String pop3Host = msgContext.getStrProp("transport.mail.pop3.host");
        String pop3User = msgContext.getStrProp("transport.mail.pop3.userid");
        String pop3passwd = msgContext.getStrProp("transport.mail.pop3.password");
        POP3MessageInfo[] messages = null;
        MimeMessage mimeMsg = null;
        POP3Client pop3 = new POP3Client();
        pop3.setDefaultTimeout(60000);
        for (int i = 0; i < 12; ++i) {
            pop3.connect(pop3Host);
            if (!pop3.login(pop3User, pop3passwd)) {
                pop3.disconnect();
                AxisFault fault = new AxisFault("POP3", "( Could not login to server.  Check password. )", null, null);
                throw fault;
            }
            messages = pop3.listMessages();
            if (messages != null && messages.length > 0) {
                StringBuffer buffer = null;
                for (int j = 0; j < messages.length; ++j) {
                    int ch;
                    Reader reader = pop3.retrieveMessage(messages[j].number);
                    if (reader == null) {
                        AxisFault fault = new AxisFault("POP3", "( Could not retrieve message header. )", null, null);
                        throw fault;
                    }
                    buffer = new StringBuffer();
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    while ((ch = bufferedReader.read()) != -1) {
                        buffer.append((char)ch);
                    }
                    bufferedReader.close();
                    if (buffer.toString().indexOf(id) != -1) {
                        ByteArrayInputStream bais = new ByteArrayInputStream(buffer.toString().getBytes());
                        Properties prop = new Properties();
                        Session session = Session.getDefaultInstance((Properties)prop, null);
                        mimeMsg = new MimeMessage(session, (InputStream)bais);
                        pop3.deleteMessage(messages[j].number);
                        break;
                    }
                    buffer = null;
                }
            }
            pop3.logout();
            pop3.disconnect();
            if (mimeMsg != null) break;
            Thread.sleep(5000L);
        }
        if (mimeMsg == null) {
            pop3.logout();
            pop3.disconnect();
            AxisFault fault = new AxisFault("POP3", "( Could not retrieve message list. )", null, null);
            throw fault;
        }
        String contentType = mimeMsg.getContentType();
        String contentLocation = mimeMsg.getContentID();
        Message outMsg = new Message(mimeMsg.getInputStream(), false, contentType, contentLocation);
        outMsg.setMessageType("response");
        msgContext.setResponseMessage(outMsg);
        if (log.isDebugEnabled()) {
            log.debug((Object)("\n" + Messages.getMessage("xmlRecd00")));
            log.debug((Object)"-----------------------------------------------");
            log.debug((Object)outMsg.getSOAPPartAsString());
        }
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

