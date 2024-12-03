/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.activation.DataSource
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.crowd.manager.mail;

import com.atlassian.crowd.manager.mail.TextEmailMessage;
import com.google.common.base.Preconditions;
import java.util.Map;
import javax.activation.DataSource;
import javax.mail.internet.InternetAddress;

public class HtmlEmailMessage
extends TextEmailMessage {
    private final String htmlBody;

    HtmlEmailMessage(Builder builder) {
        super(builder);
        Preconditions.checkState((boolean)this.getAttachments().isEmpty(), (Object)"Unable to build html email with attachments");
        this.htmlBody = (String)Preconditions.checkNotNull((Object)builder.htmlBody);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(TextEmailMessage textEmailMessage) {
        return new Builder(textEmailMessage);
    }

    public String getHtmlBody() {
        return this.htmlBody;
    }

    public static final class Builder
    extends TextEmailMessage.Builder {
        private String htmlBody;

        private Builder() {
        }

        private Builder(TextEmailMessage textEmailMessage) {
            super(textEmailMessage);
        }

        public Builder setHtmlBody(String htmlBody) {
            this.htmlBody = htmlBody;
            return this;
        }

        @Override
        public Builder setFrom(InternetAddress from) {
            super.setFrom(from);
            return this;
        }

        @Override
        public Builder setRecipientAddress(InternetAddress recipientAddress) {
            super.setRecipientAddress(recipientAddress);
            return this;
        }

        @Override
        public Builder setTo(Iterable<InternetAddress> to) {
            super.setTo(to);
            return this;
        }

        @Override
        public Builder setCc(Iterable<InternetAddress> cc) {
            super.setCc(cc);
            return this;
        }

        @Override
        public Builder setBcc(Iterable<InternetAddress> bcc) {
            super.setBcc(bcc);
            return this;
        }

        @Override
        public Builder setReplyTo(Iterable<InternetAddress> replyTo) {
            super.setReplyTo(replyTo);
            return this;
        }

        @Override
        public Builder setBody(String body) {
            super.setBody(body);
            return this;
        }

        @Override
        public Builder setSubject(String subject) {
            super.setSubject(subject);
            return this;
        }

        @Override
        public Builder setAttachments(Map<String, DataSource> attachments) {
            super.setAttachments(attachments);
            return this;
        }

        @Override
        public Builder setHeaders(Map<String, String> headers) {
            super.setHeaders(headers);
            return this;
        }

        @Override
        public HtmlEmailMessage build() {
            return new HtmlEmailMessage(this);
        }
    }
}

