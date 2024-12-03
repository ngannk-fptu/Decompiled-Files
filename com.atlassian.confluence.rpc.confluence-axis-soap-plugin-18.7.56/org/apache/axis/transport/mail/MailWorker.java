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
 *  org.apache.commons.net.smtp.SMTPClient
 *  org.apache.commons.net.smtp.SMTPReply
 */
package org.apache.axis.transport.mail;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.mail.MailServer;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;

public class MailWorker
implements Runnable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$mail$MailWorker == null ? (class$org$apache$axis$transport$mail$MailWorker = MailWorker.class$("org.apache.axis.transport.mail.MailWorker")) : class$org$apache$axis$transport$mail$MailWorker).getName());
    private MailServer server;
    private MimeMessage mimeMessage;
    private static String transportName = "Mail";
    private Properties prop = new Properties();
    private Session session = Session.getDefaultInstance((Properties)this.prop, null);
    static /* synthetic */ Class class$org$apache$axis$transport$mail$MailWorker;

    public MailWorker(MailServer server, MimeMessage mimeMessage) {
        this.server = server;
        this.mimeMessage = mimeMessage;
    }

    public void run() {
        AxisServer engine = MailServer.getAxisServer();
        MessageContext msgContext = new MessageContext(engine);
        StringBuffer soapAction = new StringBuffer();
        StringBuffer fileName = new StringBuffer();
        StringBuffer contentType = new StringBuffer();
        StringBuffer contentLocation = new StringBuffer();
        Message responseMsg = null;
        try {
            msgContext.setTargetService(null);
        }
        catch (AxisFault fault) {
            // empty catch block
        }
        msgContext.setResponseMessage(null);
        msgContext.reset();
        msgContext.setTransportName(transportName);
        responseMsg = null;
        try {
            try {
                this.parseHeaders(this.mimeMessage, contentType, contentLocation, soapAction);
                msgContext.setProperty("realpath", fileName.toString());
                msgContext.setProperty("path", fileName.toString());
                msgContext.setProperty("jws.classDir", "jwsClasses");
                String soapActionString = soapAction.toString();
                if (soapActionString != null) {
                    msgContext.setUseSOAPAction(true);
                    msgContext.setSOAPActionURI(soapActionString);
                }
                Message requestMsg = new Message(this.mimeMessage.getInputStream(), false, contentType.toString(), contentLocation.toString());
                msgContext.setRequestMessage(requestMsg);
                engine.invoke(msgContext);
                responseMsg = msgContext.getResponseMessage();
                if (responseMsg == null) {
                    throw new AxisFault(Messages.getMessage("nullResponse00"));
                }
            }
            catch (Exception e) {
                AxisFault af;
                e.printStackTrace();
                if (e instanceof AxisFault) {
                    af = (AxisFault)e;
                    log.debug((Object)Messages.getMessage("serverFault00"), (Throwable)af);
                } else {
                    af = AxisFault.makeFault(e);
                }
                responseMsg = msgContext.getResponseMessage();
                if (responseMsg == null) {
                    responseMsg = new Message(af);
                }
                try {
                    SOAPEnvelope env = responseMsg.getSOAPEnvelope();
                    env.clearBody();
                    env.addBodyElement(new SOAPFault((AxisFault)e));
                }
                catch (AxisFault fault) {
                    // empty catch block
                }
            }
            String replyTo = ((InternetAddress)this.mimeMessage.getReplyTo()[0]).getAddress();
            String sendFrom = ((InternetAddress)this.mimeMessage.getAllRecipients()[0]).getAddress();
            String subject = "Re: " + this.mimeMessage.getSubject();
            this.writeUsingSMTP(msgContext, this.server.getHost(), sendFrom, replyTo, subject, responseMsg);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
        }
        if (msgContext.getProperty("quit.requested") != null) {
            try {
                this.server.stop();
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    private void writeUsingSMTP(MessageContext msgContext, String smtpHost, String sendFrom, String replyTo, String subject, Message output) throws Exception {
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
        MimeMessage msg = new MimeMessage(this.session);
        msg.setFrom((Address)new InternetAddress(sendFrom));
        msg.addRecipient(MimeMessage.RecipientType.TO, (Address)new InternetAddress(replyTo));
        msg.setDisposition("inline");
        msg.setSubject(subject);
        ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
        output.writeTo(out);
        msg.setContent((Object)out.toString(), output.getContentType(msgContext.getSOAPConstants()));
        ByteArrayOutputStream out2 = new ByteArrayOutputStream(8192);
        msg.writeTo((OutputStream)out2);
        client.setSender(sendFrom);
        System.out.print(client.getReplyString());
        client.addRecipient(replyTo);
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
    }

    private void parseHeaders(MimeMessage mimeMessage, StringBuffer contentType, StringBuffer contentLocation, StringBuffer soapAction) throws Exception {
        contentType.append(mimeMessage.getContentType());
        contentLocation.append(mimeMessage.getContentID());
        String[] values = mimeMessage.getHeader("SOAPAction");
        if (values != null) {
            soapAction.append(values[0]);
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

