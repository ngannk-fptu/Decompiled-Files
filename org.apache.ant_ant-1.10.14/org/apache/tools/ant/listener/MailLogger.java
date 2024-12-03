/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.listener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.Header;
import org.apache.tools.ant.taskdefs.email.Mailer;
import org.apache.tools.ant.taskdefs.email.Message;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.mail.MailMessage;

public class MailLogger
extends DefaultLogger {
    private static final String DEFAULT_MIME_TYPE = "text/plain";
    private StringBuffer buffer = new StringBuffer();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Override
    public void buildFinished(BuildEvent event) {
        super.buildFinished(event);
        Project project = event.getProject();
        Hashtable<String, Object> properties = project.getProperties();
        Properties fileProperties = new Properties();
        String filename = (String)properties.get("MailLogger.properties.file");
        if (filename != null) {
            InputStream is = null;
            try {
                is = Files.newInputStream(Paths.get(filename, new String[0]), new OpenOption[0]);
                fileProperties.load(is);
            }
            catch (IOException iOException) {
                FileUtils.close(is);
                catch (Throwable throwable) {
                    FileUtils.close(is);
                    throw throwable;
                }
            }
            FileUtils.close(is);
        }
        fileProperties.stringPropertyNames().forEach(key -> properties.put((String)key, project.replaceProperties(fileProperties.getProperty((String)key))));
        boolean success = event.getException() == null;
        String prefix = success ? "success" : "failure";
        try {
            boolean notify = Project.toBoolean(this.getValue(properties, prefix + ".notify", "on"));
            if (!notify) {
                return;
            }
            Values values = new Values().mailhost(this.getValue(properties, "mailhost", "localhost")).port(Integer.parseInt(this.getValue(properties, "port", String.valueOf(25)))).user(this.getValue(properties, "user", "")).password(this.getValue(properties, "password", "")).ssl(Project.toBoolean(this.getValue(properties, "ssl", "off"))).starttls(Project.toBoolean(this.getValue(properties, "starttls.enable", "off"))).from(this.getValue(properties, "from", null)).replytoList(this.getValue(properties, "replyto", "")).toList(this.getValue(properties, prefix + ".to", null)).toCcList(this.getValue(properties, prefix + ".cc", "")).toBccList(this.getValue(properties, prefix + ".bcc", "")).mimeType(this.getValue(properties, "mimeType", DEFAULT_MIME_TYPE)).charset(this.getValue(properties, "charset", "")).body(this.getValue(properties, prefix + ".body", "")).subject(this.getValue(properties, prefix + ".subject", success ? "Build Success" : "Build Failure"));
            if (values.user().isEmpty() && values.password().isEmpty() && !values.ssl() && !values.starttls()) {
                this.sendMail(values, this.buffer.substring(0));
            } else {
                this.sendMimeMail(event.getProject(), values, this.buffer.substring(0));
            }
        }
        catch (Exception e) {
            System.out.println("MailLogger failed to send e-mail!");
            e.printStackTrace(System.err);
        }
    }

    @Override
    protected void log(String message) {
        this.buffer.append(message).append(System.lineSeparator());
    }

    private String getValue(Map<String, Object> properties, String name, String defaultValue) {
        String propertyName = "MailLogger." + name;
        String value = (String)properties.get(propertyName);
        if (value == null) {
            value = defaultValue;
        }
        if (value == null) {
            throw new RuntimeException("Missing required parameter: " + propertyName);
        }
        return value;
    }

    private void sendMail(Values values, String message) throws IOException {
        StringTokenizer t;
        MailMessage mailMessage = new MailMessage(values.mailhost(), values.port());
        mailMessage.setHeader("Date", DateUtils.getDateForHeader());
        mailMessage.from(values.from());
        if (!values.replytoList().isEmpty()) {
            t = new StringTokenizer(values.replytoList(), ", ", false);
            while (t.hasMoreTokens()) {
                mailMessage.replyto(t.nextToken());
            }
        }
        t = new StringTokenizer(values.toList(), ", ", false);
        while (t.hasMoreTokens()) {
            mailMessage.to(t.nextToken());
        }
        mailMessage.setSubject(values.subject());
        if (values.charset().isEmpty()) {
            mailMessage.setHeader("Content-Type", values.mimeType());
        } else {
            mailMessage.setHeader("Content-Type", values.mimeType() + "; charset=\"" + values.charset() + "\"");
        }
        PrintStream ps = mailMessage.getPrintStream();
        ps.println(values.body().isEmpty() ? message : values.body());
        mailMessage.sendAndClose();
    }

    private void sendMimeMail(Project project, Values values, String message) {
        Mailer mailer = null;
        try {
            mailer = ClasspathUtils.newInstance(this.getMailerImplementation(), MailLogger.class.getClassLoader(), Mailer.class);
        }
        catch (BuildException e) {
            this.logBuildException("Failed to initialise MIME mail: ", e);
            return;
        }
        Vector<EmailAddress> replyToList = this.splitEmailAddresses(values.replytoList());
        mailer.setHost(values.mailhost());
        mailer.setPort(values.port());
        mailer.setUser(values.user());
        mailer.setPassword(values.password());
        mailer.setSSL(values.ssl());
        mailer.setEnableStartTLS(values.starttls());
        Message mymessage = new Message(!values.body().isEmpty() ? values.body() : message);
        mymessage.setProject(project);
        mymessage.setMimeType(values.mimeType());
        if (!values.charset().isEmpty()) {
            mymessage.setCharset(values.charset());
        }
        mailer.setMessage(mymessage);
        mailer.setFrom(new EmailAddress(values.from()));
        mailer.setReplyToList(replyToList);
        Vector<EmailAddress> toList = this.splitEmailAddresses(values.toList());
        mailer.setToList(toList);
        Vector<EmailAddress> toCcList = this.splitEmailAddresses(values.toCcList());
        mailer.setCcList(toCcList);
        Vector<EmailAddress> toBccList = this.splitEmailAddresses(values.toBccList());
        mailer.setBccList(toBccList);
        mailer.setFiles(new Vector<File>());
        mailer.setSubject(values.subject());
        mailer.setHeaders(new Vector<Header>());
        mailer.send();
    }

    private Vector<EmailAddress> splitEmailAddresses(String listString) {
        return Stream.of(listString.split(",")).map(EmailAddress::new).collect(Collectors.toCollection(Vector::new));
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
        Throwable t;
        Throwable throwable = t = e.getCause() == null ? e : e.getCause();
        if (1 <= this.msgOutputLevel) {
            this.log(reason + t.getMessage());
        }
    }

    private static class Values {
        private String mailhost;
        private int port;
        private String user;
        private String password;
        private boolean ssl;
        private String from;
        private String replytoList;
        private String toList;
        private String toCcList;
        private String toBccList;
        private String subject;
        private String charset;
        private String mimeType;
        private String body;
        private boolean starttls;

        private Values() {
        }

        public String mailhost() {
            return this.mailhost;
        }

        public Values mailhost(String mailhost) {
            this.mailhost = mailhost;
            return this;
        }

        public int port() {
            return this.port;
        }

        public Values port(int port) {
            this.port = port;
            return this;
        }

        public String user() {
            return this.user;
        }

        public Values user(String user) {
            this.user = user;
            return this;
        }

        public String password() {
            return this.password;
        }

        public Values password(String password) {
            this.password = password;
            return this;
        }

        public boolean ssl() {
            return this.ssl;
        }

        public Values ssl(boolean ssl) {
            this.ssl = ssl;
            return this;
        }

        public String from() {
            return this.from;
        }

        public Values from(String from) {
            this.from = from;
            return this;
        }

        public String replytoList() {
            return this.replytoList;
        }

        public Values replytoList(String replytoList) {
            this.replytoList = replytoList;
            return this;
        }

        public String toList() {
            return this.toList;
        }

        public Values toList(String toList) {
            this.toList = toList;
            return this;
        }

        public String toCcList() {
            return this.toCcList;
        }

        public Values toCcList(String toCcList) {
            this.toCcList = toCcList;
            return this;
        }

        public String toBccList() {
            return this.toBccList;
        }

        public Values toBccList(String toBccList) {
            this.toBccList = toBccList;
            return this;
        }

        public String subject() {
            return this.subject;
        }

        public Values subject(String subject) {
            this.subject = subject;
            return this;
        }

        public String charset() {
            return this.charset;
        }

        public Values charset(String charset) {
            this.charset = charset;
            return this;
        }

        public String mimeType() {
            return this.mimeType;
        }

        public Values mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public String body() {
            return this.body;
        }

        public Values body(String body) {
            this.body = body;
            return this;
        }

        public boolean starttls() {
            return this.starttls;
        }

        public Values starttls(boolean starttls) {
            this.starttls = starttls;
            return this;
        }
    }
}

