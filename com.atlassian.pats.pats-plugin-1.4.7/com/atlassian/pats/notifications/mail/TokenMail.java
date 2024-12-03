/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.notifications.mail;

public class TokenMail {
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_MIME_TYPE = "text/html";
    private final String subject;
    private final String to;
    private final String body;
    private final String mimeType;
    private final String encoding;

    public TokenMail(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.mimeType = DEFAULT_MIME_TYPE;
        this.encoding = DEFAULT_ENCODING;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getTo() {
        return this.to;
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

    public String toString() {
        return "TokenMail(subject=" + this.getSubject() + ", to=" + this.getTo() + ", body=" + this.getBody() + ", mimeType=" + this.getMimeType() + ", encoding=" + this.getEncoding() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TokenMail)) {
            return false;
        }
        TokenMail other = (TokenMail)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$subject = this.getSubject();
        String other$subject = other.getSubject();
        if (this$subject == null ? other$subject != null : !this$subject.equals(other$subject)) {
            return false;
        }
        String this$to = this.getTo();
        String other$to = other.getTo();
        if (this$to == null ? other$to != null : !this$to.equals(other$to)) {
            return false;
        }
        String this$body = this.getBody();
        String other$body = other.getBody();
        if (this$body == null ? other$body != null : !this$body.equals(other$body)) {
            return false;
        }
        String this$mimeType = this.getMimeType();
        String other$mimeType = other.getMimeType();
        if (this$mimeType == null ? other$mimeType != null : !this$mimeType.equals(other$mimeType)) {
            return false;
        }
        String this$encoding = this.getEncoding();
        String other$encoding = other.getEncoding();
        return !(this$encoding == null ? other$encoding != null : !this$encoding.equals(other$encoding));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TokenMail;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $subject = this.getSubject();
        result = result * 59 + ($subject == null ? 43 : $subject.hashCode());
        String $to = this.getTo();
        result = result * 59 + ($to == null ? 43 : $to.hashCode());
        String $body = this.getBody();
        result = result * 59 + ($body == null ? 43 : $body.hashCode());
        String $mimeType = this.getMimeType();
        result = result * 59 + ($mimeType == null ? 43 : $mimeType.hashCode());
        String $encoding = this.getEncoding();
        result = result * 59 + ($encoding == null ? 43 : $encoding.hashCode());
        return result;
    }
}

