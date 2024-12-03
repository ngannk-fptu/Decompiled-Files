/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.google.common.base.Predicate
 *  com.google.common.base.Throwables
 *  javax.annotation.Nullable
 *  javax.mail.MessagingException
 *  javax.mail.internet.MimeMessage
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.CountingOutputStream
 *  org.apache.commons.io.output.NullOutputStream
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nullable;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class MessageSizeFilter
implements Predicate<MimeMessage> {
    private static final Logger log = LoggerFactory.getLogger(MessageSizeFilter.class);
    private final long maxSizeThreshold;

    public MessageSizeFilter(SettingsManager settingsManager) {
        Settings globalSettings = settingsManager.getGlobalSettings();
        this.maxSizeThreshold = globalSettings.getAttachmentMaxSize() * (long)globalSettings.getMaxAttachmentsInUI();
    }

    public boolean apply(@Nullable MimeMessage mimeMessage) {
        boolean pass = false;
        try {
            if (mimeMessage != null) {
                int messageSize = MessageSizeFilter.sizeOfInBytes(mimeMessage);
                if ((long)messageSize > this.maxSizeThreshold) {
                    log.warn("mimeMessage {} is too large ({} > {})", (Object[])new String[]{mimeMessage.getMessageID(), String.format("%,d bytes", messageSize), String.format("%,d bytes", this.maxSizeThreshold)});
                } else {
                    pass = true;
                }
            } else {
                log.error("mimeMessage is null, cannot apply filter");
            }
            return pass;
        }
        catch (MessagingException e) {
            throw Throwables.propagate((Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int sizeOfInBytes(MimeMessage mimeMessage) {
        int n;
        InputStream inputStream = mimeMessage.getRawInputStream();
        try {
            CountingOutputStream countingOutputStream = new CountingOutputStream((OutputStream)new NullOutputStream());
            IOUtils.copy((InputStream)inputStream, (OutputStream)countingOutputStream);
            inputStream.close();
            n = countingOutputStream.getCount();
        }
        catch (Throwable throwable) {
            try {
                inputStream.close();
                throw throwable;
            }
            catch (MessagingException e) {
                throw Throwables.propagate((Throwable)e);
            }
            catch (IOException e) {
                throw Throwables.propagate((Throwable)e);
            }
        }
        inputStream.close();
        return n;
    }
}

