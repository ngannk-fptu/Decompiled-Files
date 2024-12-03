/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.content.CustomContentManager$SortField
 *  com.atlassian.confluence.content.CustomContentManager$SortOrder
 *  com.atlassian.confluence.core.ConfluenceException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.util.SingleUseIterable
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.mail.MailUtils
 *  com.atlassian.mail.MailUtils$Attachment
 *  javax.mail.Header
 *  javax.mail.Message
 *  javax.mail.MessagingException
 *  javax.mail.Session
 *  javax.mail.internet.MimeMessage
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ConfluenceException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.mail.archive.Mail;
import com.atlassian.confluence.mail.archive.MailContentManager;
import com.atlassian.confluence.mail.archive.content.ContentBackedMail;
import com.atlassian.confluence.mail.archive.content.MailQueryFactory;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.SingleUseIterable;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.mail.MailUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Optional;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMailContentManager
implements MailContentManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultMailContentManager.class);
    private final CustomContentManager customContentManager;
    private final AttachmentManager attachmentManager;

    public DefaultMailContentManager(CustomContentManager customContentManager, AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
        this.customContentManager = customContentManager;
    }

    @Override
    public Mail getById(long id) {
        return ContentBackedMail.newInstance(this.customContentManager.getById(id));
    }

    @Override
    public Mail getFirstMailAfter(Mail mail) {
        return ContentBackedMail.newInstance((CustomContentEntityObject)this.customContentManager.findFirstObjectByQuery(MailQueryFactory.findNextInSpaceById(mail.getSpace().getId(), mail.getEntity().getId())));
    }

    @Override
    public Mail getFirstMailBefore(Mail mail) {
        return ContentBackedMail.newInstance((CustomContentEntityObject)this.customContentManager.findFirstObjectByQuery(MailQueryFactory.findPreviousInSpaceById(mail.getSpace().getId(), mail.getEntity().getId())));
    }

    @Override
    public int findMailTotal(Space space) {
        return this.customContentManager.findTotalInSpace(space, "com.atlassian.confluence.plugins.confluence-mail-archiving:mail");
    }

    @Override
    public boolean spaceHasMail(Space space) {
        return this.findMailTotal(space) > 0;
    }

    @Override
    public Iterable<Mail> getSpaceMail(Space space, int startIndex, int maxResults) {
        return MailWrappingIterator.iterable(this.customContentManager.findCurrentInSpace(space, "com.atlassian.confluence.plugins.confluence-mail-archiving:mail", startIndex, maxResults, CustomContentManager.SortField.CREATED, CustomContentManager.SortOrder.DESC));
    }

    @Override
    public Mail storeIncomingMail(Space space, MimeMessage mimeMessage) throws ConfluenceException {
        String messageId = null;
        try {
            messageId = mimeMessage.getMessageID();
        }
        catch (MessagingException e) {
            log.warn("Cannot access messageId in mimeMessage (MessageId = " + messageId + ")", (Throwable)e);
        }
        catch (NullPointerException e) {
            log.warn("mimeMessage is null: mimeMessage: [" + mimeMessage + "]", (Throwable)e);
        }
        Mail existingMail = this.getMailItemFromSpace(space, messageId);
        if (existingMail != null) {
            return existingMail;
        }
        try {
            CustomContentEntityObject entity = this.customContentManager.newPluginContentEntityObject("com.atlassian.confluence.plugins.confluence-mail-archiving:mail");
            entity.setPluginModuleKey("com.atlassian.confluence.plugins.confluence-mail-archiving:mail");
            MimeMessage storedMimeMessage = new MimeMessage((Session)null);
            this.copyHeaders(mimeMessage, storedMimeMessage);
            String body = MailUtils.getBody((Message)mimeMessage);
            storedMimeMessage.setContent((Object)StringUtils.defaultIfEmpty((CharSequence)body, (CharSequence)""), "text/plain; charset=UTF-8; format=flowed");
            entity.setSpace(space);
            this.setMimeMessage(entity, storedMimeMessage);
            if (messageId == null) {
                messageId = storedMimeMessage.getMessageID();
            }
            if (messageId != null && messageId.length() > 255) {
                messageId = messageId.substring(0, 255);
            }
            entity.getProperties().setStringProperty("messageId", messageId);
            this.customContentManager.saveContentEntity((ContentEntityObject)entity, DefaultSaveContext.DEFAULT);
            this.addAttachments(entity, MailUtils.getAttachments((Message)mimeMessage));
            return ContentBackedMail.newInstance(entity);
        }
        catch (Exception e) {
            log.warn("Could not store mail message " + e.getMessage(), (Throwable)e);
            throw new ConfluenceException("Could not store incoming mail: " + e.getMessage(), (Throwable)e);
        }
    }

    private void setMimeMessage(CustomContentEntityObject entity, MimeMessage message) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (message.getContent() != null) {
                message.writeTo((OutputStream)out);
                String content = new String(out.toByteArray(), "UTF-8");
                entity.setBodyAsString(content);
            } else {
                entity.setBodyAsString("");
            }
            Date creationDate = message.getReceivedDate();
            if (creationDate == null) {
                creationDate = message.getSentDate();
            }
            entity.setCreationDate(creationDate);
            String title = this.getDisplayableSubject(message);
            if (title != null && title.length() > 255) {
                log.warn("long subject will be truncated: " + title);
                title = title.substring(0, 255);
            }
            entity.setTitle(title);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Unexpected exception dealing with internal IO streams: " + e.getMessage(), (Throwable)e);
            throw new InfrastructureException("Unexpected exception dealing with internal IO streams: " + e.getMessage(), (Throwable)e);
        }
    }

    private Optional<String> getSubject(MimeMessage message) {
        try {
            if (StringUtils.isNotBlank((CharSequence)message.getSubject())) {
                return Optional.of(message.getSubject().trim());
            }
        }
        catch (MessagingException e) {
            log.warn("Could not parse mail into a MimeMessage: " + e.getMessage(), (Throwable)e);
        }
        return Optional.empty();
    }

    private String getDisplayableSubject(MimeMessage message) {
        return this.getSubject(message).orElse("[No Subject]");
    }

    @Override
    public void removeMailInSpace(Space space) {
        this.customContentManager.removeAllInSpace("com.atlassian.confluence.plugins.confluence-mail-archiving:mail", space);
    }

    private Mail getMailItemFromSpace(Space space, String messageId) {
        return ContentBackedMail.newInstance((CustomContentEntityObject)this.customContentManager.findFirstObjectByQuery(MailQueryFactory.findInSpaceByMessageId(space.getId(), messageId)));
    }

    private void copyHeaders(MimeMessage from, MimeMessage to) throws MessagingException {
        Enumeration headers = from.getAllHeaders();
        while (headers.hasMoreElements()) {
            Header header = (Header)headers.nextElement();
            if (header.getName().equalsIgnoreCase("Content-Transfer-Encoding")) continue;
            to.setHeader(header.getName(), header.getValue());
        }
    }

    private void addAttachments(CustomContentEntityObject entity, MailUtils.Attachment[] attachments) throws IOException {
        for (int i = 0; i < attachments.length; ++i) {
            MailUtils.Attachment mailAttachment = attachments[i];
            Attachment attachment = new Attachment();
            String filename = this.getAttachmentFilename(mailAttachment);
            if (StringUtils.isNotBlank((CharSequence)filename)) {
                attachment.setFileName(filename);
            } else {
                attachment.setFileName("unnamed-" + i);
            }
            attachment.setMediaType(mailAttachment.getContentType());
            attachment.setFileSize((long)mailAttachment.getContents().length);
            entity.addAttachment(attachment);
            this.attachmentManager.saveAttachment(attachment, null, (InputStream)new ByteArrayInputStream(mailAttachment.getContents()));
        }
    }

    private String getAttachmentFilename(MailUtils.Attachment mailAttachment) throws IOException {
        String filename = mailAttachment.getFilename();
        if (filename != null) {
            filename = MailUtils.fixMimeEncodedFilename((String)filename);
            filename = this.stripInvalidFilenameCharacters(filename);
        }
        return filename;
    }

    private String stripInvalidFilenameCharacters(String filename) {
        if (StringUtils.isBlank((CharSequence)filename)) {
            return filename;
        }
        StringBuilder result = new StringBuilder();
        char[] stringAsChars = filename.toCharArray();
        int prevChar = 0;
        int length = 0;
        for (int n : stringAsChars) {
            if (length > 255) break;
            if (n == 92 || n == 47 || n == 58 || n == 42 || n == 63 || n == 34 || n == 60 || n == 62 || n == 124 || n == 61 || n == 32 && prevChar == 32) continue;
            result.append((char)n);
            ++length;
            prevChar = n;
        }
        return result.toString();
    }

    private static class MailWrappingIterator
    implements Iterator<Mail> {
        private final Iterator<CustomContentEntityObject> delegate;

        static Iterable<Mail> iterable(Iterator<CustomContentEntityObject> delegate) {
            return SingleUseIterable.create((Iterator)new MailWrappingIterator(delegate));
        }

        private MailWrappingIterator(Iterator<CustomContentEntityObject> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public Mail next() {
            return ContentBackedMail.newInstance(this.delegate.next());
        }

        @Override
        public void remove() {
            this.delegate.remove();
        }
    }
}

