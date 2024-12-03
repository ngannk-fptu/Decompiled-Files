/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.mail.address.ConfluenceMailAddress
 *  com.atlassian.confluence.spaces.Space
 *  javax.mail.MessagingException
 *  javax.mail.internet.MimeMessage
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.mail.address.ConfluenceMailAddress;
import com.atlassian.confluence.mail.archive.Mail;
import com.atlassian.confluence.spaces.Space;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentBackedMail
implements Mail {
    private static final Logger log = LoggerFactory.getLogger(ContentBackedMail.class);
    private static final Pattern MESSAGE_ID_PATTERN = Pattern.compile("<\\S+>");
    private final CustomContentEntityObject content;
    private MimeMessage cachedMimeMessage;
    private String canonicalSubject;
    private String inReplyTo;
    private Collection<String> references;

    public static boolean isMailContentEntity(CustomContentEntityObject contentEntityObject) {
        return "com.atlassian.confluence.plugins.confluence-mail-archiving:mail".equals(contentEntityObject.getPluginModuleKey());
    }

    public static ContentBackedMail newInstance(CustomContentEntityObject content) {
        if (content == null) {
            return null;
        }
        return new ContentBackedMail(content);
    }

    private ContentBackedMail(CustomContentEntityObject content) {
        if (!ContentBackedMail.isMailContentEntity(content)) {
            throw new IllegalArgumentException("Object " + content + " is not a Mail content object.");
        }
        this.content = content;
    }

    @Override
    public CustomContentEntityObject getEntity() {
        return this.content;
    }

    @Override
    public String getMessageId() {
        return this.content.getProperties().getStringProperty("messageId");
    }

    @Override
    public String getMessageBody() {
        try {
            return this.getMimeMessage().getContent().toString();
        }
        catch (MessagingException e) {
            log.warn("Could not parse mail into a MimeMessage: " + e.getMessage(), (Throwable)e);
            return null;
        }
        catch (IOException e) {
            log.warn("Could not retrieve content of mail: " + e.getMessage(), (Throwable)e);
            return null;
        }
    }

    @Override
    public String getSpaceKey() {
        return this.getEntity().getSpaceKey();
    }

    @Override
    public Date getSentDate() {
        return this.getEntity().getCreationDate();
    }

    @Override
    public ConfluenceMailAddress getFrom() {
        try {
            List<ConfluenceMailAddress> from = ContentBackedMail.createDummyAddresses(this.getMimeMessage().getHeader("From"));
            return from.isEmpty() ? null : from.get(0);
        }
        catch (MessagingException e1) {
            log.warn("Could not retrieve headers from MimeMessage: " + e1.getMessage(), (Throwable)e1);
            return null;
        }
    }

    @Override
    public Collection<ConfluenceMailAddress> getRecipients() {
        List<Object> recipients = new ArrayList();
        try {
            MimeMessage mimeMessage = this.getMimeMessage();
            if (mimeMessage != null) {
                String[] to = mimeMessage.getHeader("To");
                String[] cc = mimeMessage.getHeader("Cc");
                String[] bcc = mimeMessage.getHeader("Bcc");
                int numRecip = (to != null ? to.length : 0) + (cc != null ? cc.length : 0) + (bcc != null ? bcc.length : 0);
                String[] addresses = new String[numRecip];
                int pos = 0;
                if (to != null) {
                    System.arraycopy(to, 0, addresses, pos, to.length);
                    pos += to.length;
                }
                if (cc != null) {
                    System.arraycopy(cc, 0, addresses, pos, cc.length);
                    pos += cc.length;
                }
                if (bcc != null) {
                    System.arraycopy(bcc, 0, addresses, pos, bcc.length);
                    pos += bcc.length;
                }
                recipients = ContentBackedMail.createDummyAddresses(addresses);
            }
        }
        catch (MessagingException e) {
            log.warn("Could not retrieve headers from MimeMessage: " + e.getMessage(), (Throwable)e);
        }
        return recipients == null ? Collections.emptyList() : recipients;
    }

    @Override
    public String getCanonicalSubject() {
        if (this.canonicalSubject == null) {
            this.canonicalSubject = ContentBackedMail.canonicalizeSubject(this.getSubject());
        }
        return this.canonicalSubject;
    }

    @Override
    public String getSubject() {
        try {
            if (this.hasSubject()) {
                return this.getMimeMessage().getSubject().trim();
            }
            return "[No Subject]";
        }
        catch (MessagingException e) {
            log.warn("Could not parse mail into a MimeMessage: " + e.getMessage(), (Throwable)e);
            return null;
        }
    }

    private boolean hasSubject() {
        try {
            return StringUtils.isNotBlank((CharSequence)this.getMimeMessage().getSubject());
        }
        catch (MessagingException e) {
            log.warn("Could not parse mail into a MimeMessage: " + e.getMessage(), (Throwable)e);
            return false;
        }
    }

    @Override
    public String getInReplyTo() {
        if (this.inReplyTo == null) {
            try {
                String[] values = this.getMimeMessage().getHeader("In-Reply-To");
                this.inReplyTo = values != null && values.length > 0 ? this.extractLastMessageId(values[0]) : "";
            }
            catch (MessagingException exception) {
                log.warn("Could not parse mail into a MimeMessage: " + exception.getMessage(), (Throwable)exception);
            }
        }
        return this.inReplyTo;
    }

    @Override
    public Space getSpace() {
        return this.content.getSpace();
    }

    private String extractLastMessageId(String replyToHeader) {
        Matcher matcher = MESSAGE_ID_PATTERN.matcher(replyToHeader);
        String id = "";
        while (matcher.find()) {
            id = matcher.group(0);
        }
        return id;
    }

    @Override
    public Collection<String> getReferences() {
        if (this.references != null) {
            return this.references;
        }
        try {
            ArrayList<String> tmpReferences = new ArrayList<String>();
            String[] values = this.getMimeMessage().getHeader("References");
            if (values != null && values.length > 0) {
                this.extractReferencesFromHeader(tmpReferences, values[0]);
            }
            if (StringUtils.isNotBlank((CharSequence)this.getInReplyTo()) && !tmpReferences.contains(this.getInReplyTo())) {
                tmpReferences.add(this.inReplyTo);
            }
            this.references = tmpReferences;
        }
        catch (MessagingException e) {
            log.warn("Could not parse mail into a MimeMessage: " + e.getMessage(), (Throwable)e);
        }
        return this.references;
    }

    private void extractReferencesFromHeader(List<String> references, String headerValue) {
        if (StringUtils.isNotBlank((CharSequence)headerValue)) {
            Collections.addAll(references, headerValue.split("\\s+"));
        }
    }

    private MimeMessage getMimeMessage() throws MessagingException {
        if (this.cachedMimeMessage == null && this.content.getBodyAsString() != null) {
            this.cachedMimeMessage = new MimeMessage(null, (InputStream)new ByteArrayInputStream(this.content.getBodyAsString().getBytes(Charset.forName("UTF-8"))));
        }
        return this.cachedMimeMessage;
    }

    private static List<ConfluenceMailAddress> createDummyAddresses(String[] addressStrings) {
        if (addressStrings == null) {
            return Collections.emptyList();
        }
        ArrayList<ConfluenceMailAddress> addresses = new ArrayList<ConfluenceMailAddress>(addressStrings.length);
        for (String addressString : addressStrings) {
            addresses.add(new ConfluenceMailAddress(addressString));
        }
        return addresses;
    }

    static String canonicalizeSubject(String title) {
        if (StringUtils.isBlank((CharSequence)title)) {
            return title;
        }
        String simplifiedTitle = title.trim();
        simplifiedTitle = simplifiedTitle.replaceFirst("^[rR][eE]([\\[\\(]\\d+[\\]\\)])?:(.*)", "$2");
        simplifiedTitle = simplifiedTitle.replaceFirst("^[aA][wW]([\\[\\(]\\d+[\\]\\)])?:(.*)", "$2");
        if (!(simplifiedTitle = simplifiedTitle.replaceFirst("^\\[[fF][wW][dD]:(.*)\\]$", "$1")).equals(title)) {
            return ContentBackedMail.canonicalizeSubject(simplifiedTitle);
        }
        return simplifiedTitle;
    }
}

