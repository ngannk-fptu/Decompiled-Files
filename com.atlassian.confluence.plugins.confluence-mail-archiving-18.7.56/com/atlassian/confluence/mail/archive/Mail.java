/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.Content
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.mail.address.ConfluenceMailAddress
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.content.Content;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.mail.address.ConfluenceMailAddress;
import com.atlassian.confluence.spaces.Space;
import java.util.Collection;
import java.util.Date;

public interface Mail
extends Content {
    public static final int MESSAGE_ID_SIZE = 255;
    public static final int MESSAGE_SUBJECT_SIZE = 255;
    public static final String CONTENT_TYPE = "mail";
    public static final String DEFAULT_NO_SUBJECT = "[No Subject]";

    public CustomContentEntityObject getEntity();

    public String getSpaceKey();

    public String getMessageId();

    public String getMessageBody();

    public String getCanonicalSubject();

    public String getSubject();

    public String getInReplyTo();

    public Space getSpace();

    public Date getSentDate();

    public ConfluenceMailAddress getFrom();

    public Collection<ConfluenceMailAddress> getRecipients();

    public Collection<String> getReferences();
}

