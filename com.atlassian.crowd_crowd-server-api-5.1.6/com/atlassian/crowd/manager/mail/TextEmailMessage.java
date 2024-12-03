/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  javax.activation.DataSource
 *  javax.mail.internet.InternetAddress
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.crowd.manager.mail;

import com.atlassian.crowd.manager.mail.EmailMessage;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.activation.DataSource;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TextEmailMessage
implements EmailMessage {
    private final InternetAddress from;
    private final Collection<InternetAddress> to;
    private final Collection<InternetAddress> cc;
    private final Collection<InternetAddress> bcc;
    private final Collection<InternetAddress> replyTo;
    private final String body;
    private final String subject;
    private final Map<String, DataSource> attachments;
    private final Map<String, String> headers;

    protected TextEmailMessage(Builder builder) {
        this.from = builder.from;
        this.to = builder.to != null ? builder.to : Collections.emptyList();
        this.cc = builder.cc != null ? builder.cc : Collections.emptyList();
        this.bcc = builder.bcc != null ? builder.bcc : Collections.emptyList();
        this.replyTo = builder.replyTo != null ? builder.replyTo : Collections.emptyList();
        this.body = builder.body != null ? builder.body : "";
        this.subject = builder.subject != null ? builder.subject : "";
        this.attachments = builder.attachments != null ? builder.attachments : Collections.emptyMap();
        this.headers = builder.headers != null ? builder.headers : Collections.emptyMap();
        Preconditions.checkArgument((!this.to.isEmpty() || !this.cc.isEmpty() || !this.bcc.isEmpty() ? 1 : 0) != 0, (Object)"No recipients chosen for the e-mail");
    }

    @Override
    public Optional<InternetAddress> getFrom() {
        return Optional.ofNullable(this.from);
    }

    @Override
    public Collection<InternetAddress> getTo() {
        return this.to;
    }

    @Override
    public Collection<InternetAddress> getCc() {
        return this.cc;
    }

    @Override
    public Collection<InternetAddress> getBcc() {
        return this.bcc;
    }

    @Override
    public Collection<InternetAddress> getReplyTo() {
        return this.replyTo;
    }

    @Override
    public String getBody() {
        return this.body;
    }

    @Override
    public String getSubject() {
        return this.subject;
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public Map<String, DataSource> getAttachments() {
        return this.attachments;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(TextEmailMessage textEmailMessage) {
        return new Builder(textEmailMessage);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextEmailMessage)) {
            return false;
        }
        TextEmailMessage that = (TextEmailMessage)o;
        return Objects.equals(this.from, that.from) && Objects.equals(this.to, that.to) && Objects.equals(this.cc, that.cc) && Objects.equals(this.bcc, that.bcc) && Objects.equals(this.replyTo, that.replyTo) && Objects.equals(this.body, that.body) && Objects.equals(this.subject, that.subject) && Objects.equals(this.attachments, that.attachments) && Objects.equals(this.headers, that.headers);
    }

    public int hashCode() {
        return Objects.hash(this.from, this.to, this.cc, this.bcc, this.replyTo, this.body, this.subject, this.attachments, this.headers);
    }

    public String toString() {
        return new ToStringBuilder((Object)this, ToStringStyle.SHORT_PREFIX_STYLE).append("from", (Object)this.from).append("to", this.to).append("cc", this.cc).append("bcc", this.bcc).append("replyTo", this.replyTo).append("body", (Object)this.body).append("subject", (Object)this.subject).append("attachments", this.attachments).append("headers", this.headers).toString();
    }

    public static class Builder {
        protected InternetAddress from;
        protected Collection<InternetAddress> to;
        protected Collection<InternetAddress> cc;
        protected Collection<InternetAddress> bcc;
        protected Collection<InternetAddress> replyTo;
        protected String body;
        protected String subject;
        protected Map<String, DataSource> attachments;
        protected Map<String, String> headers;

        protected Builder() {
        }

        protected Builder(TextEmailMessage textEmailMessage) {
            this.from = textEmailMessage.from;
            this.to = textEmailMessage.to;
            this.cc = textEmailMessage.cc;
            this.bcc = textEmailMessage.bcc;
            this.replyTo = textEmailMessage.replyTo;
            this.body = textEmailMessage.body;
            this.subject = textEmailMessage.subject;
            this.attachments = textEmailMessage.attachments;
            this.headers = textEmailMessage.headers;
        }

        public Builder setFrom(InternetAddress from) {
            this.from = from;
            return this;
        }

        @Deprecated
        public Builder setRecipientAddress(InternetAddress recipientAddress) {
            return this.setTo(Collections.singletonList(recipientAddress));
        }

        public Builder setTo(Iterable<InternetAddress> to) {
            this.to = Lists.newArrayList(to);
            return this;
        }

        public Builder setCc(Iterable<InternetAddress> cc) {
            this.cc = Lists.newArrayList(cc);
            return this;
        }

        public Builder setBcc(Iterable<InternetAddress> bcc) {
            this.bcc = Lists.newArrayList(bcc);
            return this;
        }

        public Builder setReplyTo(Iterable<InternetAddress> replyTo) {
            this.replyTo = Lists.newArrayList(replyTo);
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setAttachments(Map<String, DataSource> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public TextEmailMessage build() {
            return new TextEmailMessage(this);
        }
    }
}

