/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.mail.archive.MailAccount;
import com.atlassian.confluence.mail.archive.MailPollResult;
import com.atlassian.confluence.spaces.Space;
import java.util.List;

public interface MailAccountManager {
    public MailPollResult updateAccountStatus(MailAccount var1);

    public List<MailPollResult> poll(Space var1);

    public MailPollResult poll(Space var1, MailAccount var2);

    public List<MailAccount> getMailAccounts(Space var1);

    public MailAccount addMailAccount(Space var1, MailAccount var2);

    public void removeMailAccount(Space var1, int var2);

    public MailAccount getMailAccount(Space var1, int var2);

    public void updateAccount(Space var1, MailAccount var2);

    public List pollAllSpaces();
}

