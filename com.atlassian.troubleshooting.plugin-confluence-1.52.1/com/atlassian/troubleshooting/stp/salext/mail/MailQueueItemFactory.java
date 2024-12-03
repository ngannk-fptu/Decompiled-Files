/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import com.atlassian.mail.Email;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.AbstractSupportMailQueueItem;
import com.atlassian.troubleshooting.stp.salext.mail.SimpleSupportMailQueueItem;
import com.atlassian.troubleshooting.stp.salext.mail.SupportRequest;
import com.atlassian.troubleshooting.stp.salext.mail.SupportRequestAttachment;
import com.atlassian.troubleshooting.stp.salext.mail.SupportRequestMailQueueItem;
import java.io.Serializable;

public class MailQueueItemFactory {
    public SupportRequestAttachment newAttachment(String propertiesFileName, String dataType, String propertyInfo) {
        return new SupportRequestAttachment(propertiesFileName, dataType, (Serializable)((Object)propertyInfo));
    }

    public AbstractSupportMailQueueItem newSupportRequestMailQueueItem(SupportRequest supportRequest, SupportApplicationInfo info) {
        return new SupportRequestMailQueueItem(supportRequest, info);
    }

    public AbstractSupportMailQueueItem newSimpleSupportMailQueueItem(Email email) {
        return new SimpleSupportMailQueueItem(email);
    }
}

