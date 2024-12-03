/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Multipart
 */
package com.atlassian.upm.mail;

import com.atlassian.upm.api.util.Option;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.mail.Multipart;

public class UpmEmail {
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_MIME_TYPE = "text/plain";
    private final String subject;
    private final Set<String> to;
    private final Option<String> from;
    private final Option<String> fromName;
    private final Set<String> cc;
    private final Set<String> bcc;
    private final Set<String> replyTo;
    private final Option<String> inReplyTo;
    private final String body;
    private final Option<String> mimeType;
    private final Option<String> encoding;
    private final Option<Multipart> multipart;
    private final Option<String> messageId;
    private final Map<String, String> headers;

    private UpmEmail(Builder builder) {
        this.to = Collections.unmodifiableSet(new HashSet(builder.to));
        this.subject = builder.subject;
        this.from = builder.from;
        this.fromName = builder.fromName;
        this.cc = Collections.unmodifiableSet(new HashSet(builder.cc));
        this.bcc = Collections.unmodifiableSet(new HashSet(builder.bcc));
        this.replyTo = Collections.unmodifiableSet(new HashSet(builder.replyTo));
        this.inReplyTo = builder.inReplyTo;
        this.body = builder.body;
        this.mimeType = builder.mimeType;
        this.encoding = builder.encoding;
        this.multipart = builder.multipart;
        this.messageId = builder.messageId;
        this.headers = Collections.unmodifiableMap(new HashMap(builder.headers));
    }

    public static Builder builder(String subject, String body) {
        return new Builder(subject, body);
    }

    public String getSubject() {
        return this.subject;
    }

    public Set<String> getTo() {
        return this.to;
    }

    public Option<String> getFrom() {
        return this.from;
    }

    public Option<String> getFromName() {
        return this.fromName;
    }

    public Set<String> getCc() {
        return this.cc;
    }

    public Set<String> getBcc() {
        return this.bcc;
    }

    public Set<String> getReplyTo() {
        return this.replyTo;
    }

    public Option<String> getInReplyTo() {
        return this.inReplyTo;
    }

    public String getBody() {
        return this.body;
    }

    public Option<String> getMimeType() {
        return this.mimeType;
    }

    public Option<String> getEncoding() {
        return this.encoding;
    }

    public Option<Multipart> getMultipart() {
        return this.multipart;
    }

    public Option<String> getMessageId() {
        return this.messageId;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getMimeTypeAndEncoding() {
        String mimeType = this.mimeType.getOrElse(DEFAULT_MIME_TYPE);
        String encoding = this.encoding.getOrElse(DEFAULT_ENCODING);
        return mimeType + "; charset=" + encoding;
    }

    public static enum Format {
        HTML("text/html"),
        TEXT("text/plain");

        private final String mimeType;

        private Format(String mimeType) {
            this.mimeType = mimeType;
        }

        public String getMimeType() {
            return this.mimeType;
        }
    }

    public static final class Builder {
        private String subject;
        private Set<String> to = new HashSet<String>();
        private Option<String> from = Option.none();
        private Option<String> fromName = Option.none();
        private Set<String> cc = new HashSet<String>();
        private Set<String> bcc = new HashSet<String>();
        private Set<String> replyTo = new HashSet<String>();
        private Option<String> inReplyTo = Option.none();
        private String body;
        private Option<String> mimeType = Option.none();
        private Option<String> encoding = Option.none();
        private Option<Multipart> multipart = Option.none();
        private Option<String> messageId = Option.none();
        private Map<String, String> headers = new HashMap<String, String>();

        public Builder(String subject, String body) {
            this.subject = Objects.requireNonNull(subject, "subject");
            this.body = Objects.requireNonNull(body, "body");
        }

        public Builder addTo(String to) {
            this.to.add(to);
            return this;
        }

        public Builder addTo(Set<String> to) {
            this.to.addAll(to);
            return this;
        }

        public Builder from(String from) {
            this.from = Option.some(from);
            return this;
        }

        public Builder fromName(Option<String> fromName) {
            this.fromName = fromName;
            return this;
        }

        public Builder addCc(String cc) {
            this.cc.add(cc);
            return this;
        }

        public Builder addBcc(String bcc) {
            this.bcc.add(bcc);
            return this;
        }

        public Builder addReplyTo(String replyTo) {
            this.replyTo.add(replyTo);
            return this;
        }

        public Builder inReplyTo(Option<String> inReplyTo) {
            this.inReplyTo = inReplyTo;
            return this;
        }

        public Builder mimeType(String mimeType) {
            this.mimeType = Option.some(mimeType);
            return this;
        }

        public Builder encoding(String encoding) {
            this.encoding = Option.some(encoding);
            return this;
        }

        public Builder multipart(Option<Multipart> multipart) {
            this.multipart = multipart;
            return this;
        }

        public Builder messageId(Option<String> messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder addHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder addHeaders(Map<String, String> headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                this.headers.put(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public UpmEmail build() {
            return new UpmEmail(this);
        }
    }
}

