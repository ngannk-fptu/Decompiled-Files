/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Multipart
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.mail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Multipart;
import org.apache.commons.lang3.StringUtils;

public class Email
implements Serializable {
    private static final long serialVersionUID = 1763820874219520737L;
    private String to;
    private String subject;
    private String from;
    private String fromName;
    private String cc;
    private String bcc;
    private String replyTo;
    private String inReplyTo;
    private String body;
    private String mimeType;
    private String encoding;
    private Multipart multipart;
    private String messageId;
    private Map headers;
    private boolean excludeSubjectPrefix;

    private void init(String to) {
        this.to = to;
        this.subject = "";
        this.body = "";
        this.mimeType = "text/plain";
        this.encoding = "UTF-8";
        this.headers = new HashMap();
        this.excludeSubjectPrefix = false;
        this.loadDefaultHeaders();
    }

    public Email(String to) {
        if (StringUtils.isBlank((CharSequence)to)) {
            throw new IllegalArgumentException("'To' is a required field");
        }
        this.init(to);
    }

    public Email(String to, String cc, String bcc) {
        if (StringUtils.isBlank((CharSequence)to) && StringUtils.isBlank((CharSequence)cc) && StringUtils.isBlank((CharSequence)bcc)) {
            throw new IllegalArgumentException("One of 'To', 'CC' or 'BCC' is required");
        }
        this.init(to);
        this.cc = cc;
        this.bcc = bcc;
    }

    protected void loadDefaultHeaders() {
        this.headers.put("Precedence", "bulk");
        this.headers.put("Auto-Submitted", "auto-generated");
    }

    public Email setFrom(String from) {
        this.from = from;
        return this;
    }

    public Email setFromName(String fromName) {
        this.fromName = fromName;
        return this;
    }

    public Email setTo(String to) {
        this.to = to;
        return this;
    }

    public Email setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Email setCc(String cc) {
        this.cc = cc;
        return this;
    }

    public Email setBcc(String bcc) {
        this.bcc = bcc;
        return this;
    }

    public Email setReplyTo(String replyTo) {
        this.replyTo = replyTo;
        return this;
    }

    public Email setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
        return this;
    }

    public Email setBody(String body) {
        this.body = body;
        return this;
    }

    public Email setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public Email setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public Email setMultipart(Multipart multipart) {
        this.multipart = multipart;
        return this;
    }

    public Email setExcludeSubjectPrefix(boolean excludeSubjectPrefix) {
        this.excludeSubjectPrefix = excludeSubjectPrefix;
        return this;
    }

    public String getFrom() {
        return this.from;
    }

    public String getFromName() {
        return this.fromName;
    }

    public String getTo() {
        return this.to;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getCc() {
        return this.cc;
    }

    public String getBcc() {
        return this.bcc;
    }

    public String getReplyTo() {
        return this.replyTo;
    }

    public String getInReplyTo() {
        return this.inReplyTo;
    }

    public String getBody() {
        return this.body;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public Multipart getMultipart() {
        return this.multipart;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isExcludeSubjectPrefix() {
        return this.excludeSubjectPrefix;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Email)) {
            return false;
        }
        Email email = (Email)o;
        if (this.bcc != null ? !this.bcc.equals(email.bcc) : email.bcc != null) {
            return false;
        }
        if (this.cc != null ? !this.cc.equals(email.cc) : email.cc != null) {
            return false;
        }
        if (this.encoding != null ? !this.encoding.equals(email.encoding) : email.encoding != null) {
            return false;
        }
        if (this.from != null ? !this.from.equals(email.from) : email.from != null) {
            return false;
        }
        if (this.fromName != null ? !this.fromName.equals(email.fromName) : email.fromName != null) {
            return false;
        }
        if (this.inReplyTo != null ? !this.inReplyTo.equals(email.inReplyTo) : email.inReplyTo != null) {
            return false;
        }
        if (this.messageId != null ? !this.messageId.equals(email.messageId) : email.messageId != null) {
            return false;
        }
        if (this.mimeType != null ? !this.mimeType.equals(email.mimeType) : email.mimeType != null) {
            return false;
        }
        if (this.multipart != null ? !this.multipart.equals(email.multipart) : email.multipart != null) {
            return false;
        }
        if (this.replyTo != null ? !this.replyTo.equals(email.replyTo) : email.replyTo != null) {
            return false;
        }
        if (this.subject != null ? !this.subject.equals(email.subject) : email.subject != null) {
            return false;
        }
        if (!this.to.equals(email.to)) {
            return false;
        }
        return this.excludeSubjectPrefix == email.excludeSubjectPrefix;
    }

    public int hashCode() {
        int result = this.to.hashCode();
        result = 29 * result + (this.subject != null ? this.subject.hashCode() : 0);
        result = 29 * result + (this.from != null ? this.from.hashCode() : 0);
        result = 29 * result + (this.fromName != null ? this.fromName.hashCode() : 0);
        result = 29 * result + (this.cc != null ? this.cc.hashCode() : 0);
        result = 29 * result + (this.bcc != null ? this.bcc.hashCode() : 0);
        result = 29 * result + (this.replyTo != null ? this.replyTo.hashCode() : 0);
        result = 29 * result + (this.inReplyTo != null ? this.inReplyTo.hashCode() : 0);
        result = 29 * result + (this.mimeType != null ? this.mimeType.hashCode() : 0);
        result = 29 * result + (this.encoding != null ? this.encoding.hashCode() : 0);
        result = 29 * result + (this.multipart != null ? this.multipart.hashCode() : 0);
        result = 29 * result + (this.messageId != null ? this.messageId.hashCode() : 0);
        result = 29 * result + Boolean.hashCode(this.excludeSubjectPrefix);
        return result;
    }

    public String toString() {
        return "To='" + this.to + "' Subject='" + this.subject + "' From='" + this.from + "' FromName='" + this.fromName + "' Cc='" + this.cc + "' Bcc='" + this.bcc + "' ReplyTo='" + this.replyTo + "' InReplyTo='" + this.inReplyTo + "' MimeType='" + this.mimeType + "' Encoding='" + this.encoding + "' Multipart='" + this.multipart + "' MessageId='" + this.messageId + "' ExcludeSubjectPrefix=" + Boolean.toString(this.excludeSubjectPrefix) + "'";
    }

    public void addHeader(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
    }

    public String removeHeader(String headerName) {
        if (this.headers.containsKey(headerName)) {
            return (String)this.headers.remove(headerName);
        }
        return null;
    }

    public Map getHeaders() {
        return this.headers;
    }
}

