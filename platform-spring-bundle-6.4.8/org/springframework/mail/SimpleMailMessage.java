/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.mail;

import java.io.Serializable;
import java.util.Date;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailMessage;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class SimpleMailMessage
implements MailMessage,
Serializable {
    @Nullable
    private String from;
    @Nullable
    private String replyTo;
    @Nullable
    private String[] to;
    @Nullable
    private String[] cc;
    @Nullable
    private String[] bcc;
    @Nullable
    private Date sentDate;
    @Nullable
    private String subject;
    @Nullable
    private String text;

    public SimpleMailMessage() {
    }

    public SimpleMailMessage(SimpleMailMessage original) {
        Assert.notNull((Object)original, "'original' message argument must not be null");
        this.from = original.getFrom();
        this.replyTo = original.getReplyTo();
        this.to = SimpleMailMessage.copyOrNull(original.getTo());
        this.cc = SimpleMailMessage.copyOrNull(original.getCc());
        this.bcc = SimpleMailMessage.copyOrNull(original.getBcc());
        this.sentDate = original.getSentDate();
        this.subject = original.getSubject();
        this.text = original.getText();
    }

    @Override
    public void setFrom(@Nullable String from) {
        this.from = from;
    }

    @Nullable
    public String getFrom() {
        return this.from;
    }

    @Override
    public void setReplyTo(@Nullable String replyTo) {
        this.replyTo = replyTo;
    }

    @Nullable
    public String getReplyTo() {
        return this.replyTo;
    }

    @Override
    public void setTo(@Nullable String to) {
        this.to = new String[]{to};
    }

    @Override
    public void setTo(String ... to) {
        this.to = to;
    }

    @Nullable
    public String[] getTo() {
        return this.to;
    }

    @Override
    public void setCc(@Nullable String cc) {
        this.cc = new String[]{cc};
    }

    @Override
    public void setCc(String ... cc) {
        this.cc = cc;
    }

    @Nullable
    public String[] getCc() {
        return this.cc;
    }

    @Override
    public void setBcc(@Nullable String bcc) {
        this.bcc = new String[]{bcc};
    }

    @Override
    public void setBcc(String ... bcc) {
        this.bcc = bcc;
    }

    @Nullable
    public String[] getBcc() {
        return this.bcc;
    }

    @Override
    public void setSentDate(@Nullable Date sentDate) {
        this.sentDate = sentDate;
    }

    @Nullable
    public Date getSentDate() {
        return this.sentDate;
    }

    @Override
    public void setSubject(@Nullable String subject) {
        this.subject = subject;
    }

    @Nullable
    public String getSubject() {
        return this.subject;
    }

    @Override
    public void setText(@Nullable String text) {
        this.text = text;
    }

    @Nullable
    public String getText() {
        return this.text;
    }

    public void copyTo(MailMessage target) {
        Assert.notNull((Object)target, "'target' MailMessage must not be null");
        if (this.getFrom() != null) {
            target.setFrom(this.getFrom());
        }
        if (this.getReplyTo() != null) {
            target.setReplyTo(this.getReplyTo());
        }
        if (this.getTo() != null) {
            target.setTo(SimpleMailMessage.copy(this.getTo()));
        }
        if (this.getCc() != null) {
            target.setCc(SimpleMailMessage.copy(this.getCc()));
        }
        if (this.getBcc() != null) {
            target.setBcc(SimpleMailMessage.copy(this.getBcc()));
        }
        if (this.getSentDate() != null) {
            target.setSentDate(this.getSentDate());
        }
        if (this.getSubject() != null) {
            target.setSubject(this.getSubject());
        }
        if (this.getText() != null) {
            target.setText(this.getText());
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SimpleMailMessage)) {
            return false;
        }
        SimpleMailMessage otherMessage = (SimpleMailMessage)other;
        return ObjectUtils.nullSafeEquals(this.from, otherMessage.from) && ObjectUtils.nullSafeEquals(this.replyTo, otherMessage.replyTo) && ObjectUtils.nullSafeEquals(this.to, otherMessage.to) && ObjectUtils.nullSafeEquals(this.cc, otherMessage.cc) && ObjectUtils.nullSafeEquals(this.bcc, otherMessage.bcc) && ObjectUtils.nullSafeEquals(this.sentDate, otherMessage.sentDate) && ObjectUtils.nullSafeEquals(this.subject, otherMessage.subject) && ObjectUtils.nullSafeEquals(this.text, otherMessage.text);
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.from);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.replyTo);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.to);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.cc);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.bcc);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.sentDate);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.subject);
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SimpleMailMessage: ");
        sb.append("from=").append(this.from).append("; ");
        sb.append("replyTo=").append(this.replyTo).append("; ");
        sb.append("to=").append(StringUtils.arrayToCommaDelimitedString(this.to)).append("; ");
        sb.append("cc=").append(StringUtils.arrayToCommaDelimitedString(this.cc)).append("; ");
        sb.append("bcc=").append(StringUtils.arrayToCommaDelimitedString(this.bcc)).append("; ");
        sb.append("sentDate=").append(this.sentDate).append("; ");
        sb.append("subject=").append(this.subject).append("; ");
        sb.append("text=").append(this.text);
        return sb.toString();
    }

    @Nullable
    private static String[] copyOrNull(@Nullable String[] state) {
        if (state == null) {
            return null;
        }
        return SimpleMailMessage.copy(state);
    }

    private static String[] copy(String[] state) {
        return (String[])state.clone();
    }
}

