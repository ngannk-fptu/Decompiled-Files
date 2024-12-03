/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.email;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.Header;
import org.apache.tools.ant.taskdefs.email.Mailer;
import org.apache.tools.ant.taskdefs.email.Message;
import org.apache.tools.ant.taskdefs.email.PlainMailer;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.ClasspathUtils;

public class EmailTask
extends Task {
    private static final int SMTP_PORT = 25;
    public static final String AUTO = "auto";
    public static final String MIME = "mime";
    public static final String UU = "uu";
    public static final String PLAIN = "plain";
    private String encoding = "auto";
    private String host = "localhost";
    private Integer port = null;
    private String subject = null;
    private Message message = null;
    private boolean failOnError = true;
    private boolean includeFileNames = false;
    private String messageMimeType = null;
    private String messageFileInputEncoding;
    private EmailAddress from = null;
    private Vector<EmailAddress> replyToList = new Vector();
    private Vector<EmailAddress> toList = new Vector();
    private Vector<EmailAddress> ccList = new Vector();
    private Vector<EmailAddress> bccList = new Vector();
    private Vector<Header> headers = new Vector();
    private Path attachments = null;
    private String charset = null;
    private String user = null;
    private String password = null;
    private boolean ssl = false;
    private boolean starttls = false;
    private boolean ignoreInvalidRecipients = false;

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSSL(boolean ssl) {
        this.ssl = ssl;
    }

    public void setEnableStartTLS(boolean b) {
        this.starttls = b;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding.getValue();
    }

    public void setMailport(int port) {
        this.port = port;
    }

    public void setMailhost(String host) {
        this.host = host;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an email");
        }
        this.message = new Message(message);
        this.message.setProject(this.getProject());
    }

    public void setMessageFile(File file) {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an email");
        }
        this.message = new Message(file);
        this.message.setProject(this.getProject());
    }

    public void setMessageMimeType(String type) {
        this.messageMimeType = type;
    }

    public void addMessage(Message message) throws BuildException {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an email");
        }
        this.message = message;
    }

    public void addFrom(EmailAddress address) {
        if (this.from != null) {
            throw new BuildException("Emails can only be from one address");
        }
        this.from = address;
    }

    public void setFrom(String address) {
        if (this.from != null) {
            throw new BuildException("Emails can only be from one address");
        }
        this.from = new EmailAddress(address);
    }

    public void addReplyTo(EmailAddress address) {
        this.replyToList.add(address);
    }

    public void setReplyTo(String address) {
        this.replyToList.add(new EmailAddress(address));
    }

    public void addTo(EmailAddress address) {
        this.toList.add(address);
    }

    public void setToList(String list) {
        StringTokenizer tokens = new StringTokenizer(list, ",");
        while (tokens.hasMoreTokens()) {
            this.toList.add(new EmailAddress(tokens.nextToken()));
        }
    }

    public void addCc(EmailAddress address) {
        this.ccList.add(address);
    }

    public void setCcList(String list) {
        StringTokenizer tokens = new StringTokenizer(list, ",");
        while (tokens.hasMoreTokens()) {
            this.ccList.add(new EmailAddress(tokens.nextToken()));
        }
    }

    public void addBcc(EmailAddress address) {
        this.bccList.add(address);
    }

    public void setBccList(String list) {
        StringTokenizer tokens = new StringTokenizer(list, ",");
        while (tokens.hasMoreTokens()) {
            this.bccList.add(new EmailAddress(tokens.nextToken()));
        }
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void setFiles(String filenames) {
        StringTokenizer t = new StringTokenizer(filenames, ", ");
        while (t.hasMoreTokens()) {
            this.createAttachments().add(new FileResource(this.getProject().resolveFile(t.nextToken())));
        }
    }

    public void addFileset(FileSet fs) {
        this.createAttachments().add(fs);
    }

    public Path createAttachments() {
        if (this.attachments == null) {
            this.attachments = new Path(this.getProject());
        }
        return this.attachments.createPath();
    }

    public Header createHeader() {
        Header h = new Header();
        this.headers.add(h);
        return h;
    }

    public void setIncludefilenames(boolean includeFileNames) {
        this.includeFileNames = includeFileNames;
    }

    public boolean getIncludeFileNames() {
        return this.includeFileNames;
    }

    public void setIgnoreInvalidRecipients(boolean b) {
        this.ignoreInvalidRecipients = b;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() {
        Message savedMessage = this.message;
        try {
            Mailer mailer = null;
            boolean autoFound = false;
            if (MIME.equals(this.encoding) || AUTO.equals(this.encoding)) {
                try {
                    mailer = ClasspathUtils.newInstance(this.getMailerImplementation(), EmailTask.class.getClassLoader(), Mailer.class);
                    autoFound = true;
                    this.log("Using MIME mail", 3);
                }
                catch (BuildException e) {
                    this.logBuildException("Failed to initialise MIME mail: ", e);
                }
            }
            if (!(autoFound || this.user == null && this.password == null || !UU.equals(this.encoding) && !PLAIN.equals(this.encoding))) {
                throw new BuildException("SMTP auth only possible with MIME mail");
            }
            if (!autoFound && (this.ssl || this.starttls) && (UU.equals(this.encoding) || PLAIN.equals(this.encoding))) {
                throw new BuildException("SSL and STARTTLS only possible with MIME mail");
            }
            if (UU.equals(this.encoding) || AUTO.equals(this.encoding) && !autoFound) {
                try {
                    mailer = ClasspathUtils.newInstance("org.apache.tools.ant.taskdefs.email.UUMailer", EmailTask.class.getClassLoader(), Mailer.class);
                    autoFound = true;
                    this.log("Using UU mail", 3);
                }
                catch (BuildException e) {
                    this.logBuildException("Failed to initialise UU mail: ", e);
                }
            }
            if (PLAIN.equals(this.encoding) || AUTO.equals(this.encoding) && !autoFound) {
                mailer = new PlainMailer();
                autoFound = true;
                this.log("Using plain mail", 3);
            }
            if (mailer == null) {
                throw new BuildException("Failed to initialise encoding: %s", this.encoding);
            }
            if (this.message == null) {
                this.message = new Message();
                this.message.setProject(this.getProject());
            }
            if (this.from == null || this.from.getAddress() == null) {
                throw new BuildException("A from element is required");
            }
            if (this.toList.isEmpty() && this.ccList.isEmpty() && this.bccList.isEmpty()) {
                throw new BuildException("At least one of to, cc or bcc must be supplied");
            }
            if (this.messageMimeType != null) {
                if (this.message.isMimeTypeSpecified()) {
                    throw new BuildException("The mime type can only be specified in one location");
                }
                this.message.setMimeType(this.messageMimeType);
            }
            if (this.charset != null) {
                if (this.message.getCharset() != null) {
                    throw new BuildException("The charset can only be specified in one location");
                }
                this.message.setCharset(this.charset);
            }
            this.message.setInputEncoding(this.messageFileInputEncoding);
            Vector<File> files = new Vector<File>();
            if (this.attachments != null) {
                for (Resource r : this.attachments) {
                    files.add(r.as(FileProvider.class).getFile());
                }
            }
            this.log("Sending email: " + this.subject, 2);
            this.log("From " + this.from, 3);
            this.log("ReplyTo " + this.replyToList, 3);
            this.log("To " + this.toList, 3);
            this.log("Cc " + this.ccList, 3);
            this.log("Bcc " + this.bccList, 3);
            mailer.setHost(this.host);
            if (this.port != null) {
                mailer.setPort(this.port);
                mailer.setPortExplicitlySpecified(true);
            } else {
                mailer.setPort(25);
                mailer.setPortExplicitlySpecified(false);
            }
            mailer.setUser(this.user);
            mailer.setPassword(this.password);
            mailer.setSSL(this.ssl);
            mailer.setEnableStartTLS(this.starttls);
            mailer.setMessage(this.message);
            mailer.setFrom(this.from);
            mailer.setReplyToList(this.replyToList);
            mailer.setToList(this.toList);
            mailer.setCcList(this.ccList);
            mailer.setBccList(this.bccList);
            mailer.setFiles(files);
            mailer.setSubject(this.subject);
            mailer.setTask(this);
            mailer.setIncludeFileNames(this.includeFileNames);
            mailer.setHeaders(this.headers);
            mailer.setIgnoreInvalidRecipients(this.ignoreInvalidRecipients);
            mailer.send();
            int count = files.size();
            this.log("Sent email with " + count + " attachment" + (count == 1 ? "" : "s"), 2);
        }
        catch (BuildException e) {
            this.logBuildException("Failed to send email: ", e);
            if (this.failOnError) {
                throw e;
            }
        }
        catch (Exception e) {
            this.log("Failed to send email: " + e.getMessage(), 1);
            if (this.failOnError) {
                throw new BuildException(e);
            }
        }
        finally {
            this.message = savedMessage;
        }
    }

    private String getMailerImplementation() {
        try {
            Class.forName("jakarta.activation.DataHandler");
            Class.forName("jakarta.mail.internet.MimeMessage");
            return "org.apache.tools.ant.taskdefs.email.JakartaMimeMailer";
        }
        catch (ClassNotFoundException cnfe) {
            this.logBuildException("Could not find Jakarta MIME mail: ", new BuildException(cnfe));
            try {
                Class.forName("javax.activation.DataHandler");
                Class.forName("javax.mail.internet.MimeMessage");
                return "org.apache.tools.ant.taskdefs.email.MimeMailer";
            }
            catch (ClassNotFoundException cnfe2) {
                this.logBuildException("Could not find MIME mail: ", new BuildException(cnfe2));
                return "org.apache.tools.ant.taskdefs.email.Mailer";
            }
        }
    }

    private void logBuildException(String reason, BuildException e) {
        Throwable t = e.getCause() == null ? e : e.getCause();
        this.log(reason + t.getMessage(), 1);
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setMessageFileInputEncoding(String encoding) {
        this.messageFileInputEncoding = encoding;
    }

    public static class Encoding
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{EmailTask.AUTO, EmailTask.MIME, EmailTask.UU, EmailTask.PLAIN};
        }
    }
}

