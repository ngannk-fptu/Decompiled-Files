/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceException
 *  com.atlassian.confluence.spaces.Space
 *  javax.mail.internet.MimeMessage
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.core.ConfluenceException;
import com.atlassian.confluence.mail.archive.Mail;
import com.atlassian.confluence.spaces.Space;
import javax.mail.internet.MimeMessage;

public interface MailContentManager {
    public Mail getById(long var1);

    public Mail getFirstMailAfter(Mail var1);

    public Mail getFirstMailBefore(Mail var1);

    public int findMailTotal(Space var1);

    public boolean spaceHasMail(Space var1);

    public Iterable<Mail> getSpaceMail(Space var1, int var2, int var3);

    public Mail storeIncomingMail(Space var1, MimeMessage var2) throws ConfluenceException;

    public void removeMailInSpace(Space var1);
}

