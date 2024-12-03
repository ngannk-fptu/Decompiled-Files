/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.email;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.Header;
import org.apache.tools.ant.taskdefs.email.Message;
import org.apache.tools.ant.util.DateUtils;

public abstract class Mailer {
    protected String host = null;
    protected int port = -1;
    protected String user = null;
    protected String password = null;
    protected boolean SSL = false;
    protected Message message;
    protected EmailAddress from;
    protected Vector<EmailAddress> replyToList = null;
    protected Vector<EmailAddress> toList = null;
    protected Vector<EmailAddress> ccList = null;
    protected Vector<EmailAddress> bccList = null;
    protected Vector<File> files = null;
    protected String subject = null;
    protected Task task;
    protected boolean includeFileNames = false;
    protected Vector<Header> headers = null;
    private boolean ignoreInvalidRecipients = false;
    private boolean starttls = false;
    private boolean portExplicitlySpecified = false;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPortExplicitlySpecified(boolean explicit) {
        this.portExplicitlySpecified = explicit;
    }

    protected boolean isPortExplicitlySpecified() {
        return this.portExplicitlySpecified;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSSL(boolean ssl) {
        this.SSL = ssl;
    }

    public void setEnableStartTLS(boolean b) {
        this.starttls = b;
    }

    protected boolean isStartTLSEnabled() {
        return this.starttls;
    }

    public void setMessage(Message m) {
        this.message = m;
    }

    public void setFrom(EmailAddress from) {
        this.from = from;
    }

    public void setReplyToList(Vector<EmailAddress> list) {
        this.replyToList = list;
    }

    public void setToList(Vector<EmailAddress> list) {
        this.toList = list;
    }

    public void setCcList(Vector<EmailAddress> list) {
        this.ccList = list;
    }

    public void setBccList(Vector<EmailAddress> list) {
        this.bccList = list;
    }

    public void setFiles(Vector<File> files) {
        this.files = files;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setIncludeFileNames(boolean b) {
        this.includeFileNames = b;
    }

    public void setHeaders(Vector<Header> v) {
        this.headers = v;
    }

    public abstract void send() throws BuildException;

    public void setIgnoreInvalidRecipients(boolean b) {
        this.ignoreInvalidRecipients = b;
    }

    protected boolean shouldIgnoreInvalidRecipients() {
        return this.ignoreInvalidRecipients;
    }

    protected final String getDate() {
        return DateUtils.getDateForHeader();
    }
}

