/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.email;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.Header;
import org.apache.tools.ant.taskdefs.email.Mailer;
import org.apache.tools.mail.MailMessage;

class PlainMailer
extends Mailer {
    PlainMailer() {
    }

    @Override
    public void send() {
        try {
            MailMessage mailMessage = new MailMessage(this.host, this.port);
            mailMessage.from(this.from.toString());
            boolean atLeastOneRcptReached = false;
            this.replyToList.stream().map(Object::toString).forEach(mailMessage::replyto);
            for (EmailAddress to : this.toList) {
                try {
                    mailMessage.to(to.toString());
                    atLeastOneRcptReached = true;
                }
                catch (IOException ex) {
                    this.badRecipient(to, ex);
                }
            }
            for (EmailAddress cc : this.ccList) {
                try {
                    mailMessage.cc(cc.toString());
                    atLeastOneRcptReached = true;
                }
                catch (IOException ex) {
                    this.badRecipient(cc, ex);
                }
            }
            for (EmailAddress bcc : this.bccList) {
                try {
                    mailMessage.bcc(bcc.toString());
                    atLeastOneRcptReached = true;
                }
                catch (IOException ex) {
                    this.badRecipient(bcc, ex);
                }
            }
            if (!atLeastOneRcptReached) {
                throw new BuildException("Couldn't reach any recipient");
            }
            if (this.subject != null) {
                mailMessage.setSubject(this.subject);
            }
            mailMessage.setHeader("Date", this.getDate());
            if (this.message.getCharset() != null) {
                mailMessage.setHeader("Content-Type", this.message.getMimeType() + "; charset=\"" + this.message.getCharset() + "\"");
            } else {
                mailMessage.setHeader("Content-Type", this.message.getMimeType());
            }
            if (this.headers != null) {
                for (Header h : this.headers) {
                    mailMessage.setHeader(h.getName(), h.getValue());
                }
            }
            PrintStream out = mailMessage.getPrintStream();
            this.message.print(out);
            if (this.files != null) {
                for (File f : this.files) {
                    this.attach(f, out);
                }
            }
            mailMessage.sendAndClose();
        }
        catch (IOException ioe) {
            throw new BuildException("IO error sending mail", ioe);
        }
    }

    protected void attach(File file, PrintStream out) throws IOException {
        if (!file.exists() || !file.canRead()) {
            throw new BuildException("File \"%s\" does not exist or is not readable.", file.getAbsolutePath());
        }
        if (this.includeFileNames) {
            out.println();
            String filename = file.getName();
            int filenamelength = filename.length();
            out.println(filename);
            for (int star = 0; star < filenamelength; ++star) {
                out.print('=');
            }
            out.println();
        }
        int maxBuf = 1024;
        byte[] buf = new byte[1024];
        try (InputStream finstr = Files.newInputStream(file.toPath(), new OpenOption[0]);
             BufferedInputStream in = new BufferedInputStream(finstr, buf.length);){
            int length;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
        }
    }

    private void badRecipient(EmailAddress rcpt, IOException reason) {
        String msg = "Failed to send mail to " + rcpt;
        if (this.shouldIgnoreInvalidRecipients()) {
            msg = msg + " because of :" + reason.getMessage();
            if (this.task != null) {
                this.task.log(msg, 1);
            } else {
                System.err.println(msg);
            }
        } else {
            throw new BuildException(msg, reason);
        }
    }
}

