/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugins.emailgateway.api.CreatePageAttachmentHandleException
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailGatewaySettingsManager
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailHandlingException
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailStagingException
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailStagingService
 *  com.atlassian.confluence.plugins.emailgateway.api.EmailToContentConverter
 *  com.atlassian.confluence.plugins.emailgateway.api.NoMatchingUserToCreatePageException
 *  com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail
 *  com.atlassian.confluence.plugins.emailgateway.api.SerializableAttachment
 *  com.atlassian.confluence.plugins.emailgateway.api.StagingEmailHandler
 *  com.atlassian.confluence.plugins.emailgateway.api.UsersByEmailService
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.emailtopage;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.emailgateway.api.CreatePageAttachmentHandleException;
import com.atlassian.confluence.plugins.emailgateway.api.EmailGatewaySettingsManager;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHandlingException;
import com.atlassian.confluence.plugins.emailgateway.api.EmailStagingException;
import com.atlassian.confluence.plugins.emailgateway.api.EmailStagingService;
import com.atlassian.confluence.plugins.emailgateway.api.EmailToContentConverter;
import com.atlassian.confluence.plugins.emailgateway.api.NoMatchingUserToCreatePageException;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.api.SerializableAttachment;
import com.atlassian.confluence.plugins.emailgateway.api.StagingEmailHandler;
import com.atlassian.confluence.plugins.emailgateway.api.UsersByEmailService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;

public class CreatePageEmailHandler
implements StagingEmailHandler<Page> {
    private final EmailStagingService emailStagingService;
    private final EmailToContentConverter<Page> converter;
    private final EmailGatewaySettingsManager emailGatewaySettingsManager;
    private final UsersByEmailService usersByEmailService;
    private final SettingsManager settingsManager;

    public CreatePageEmailHandler(EmailStagingService emailStagingService, EmailToContentConverter<Page> converter, EmailGatewaySettingsManager emailGatewaySettingsManager, UsersByEmailService usersByEmailService, SettingsManager settingsManager) {
        this.emailStagingService = emailStagingService;
        this.converter = converter;
        this.emailGatewaySettingsManager = emailGatewaySettingsManager;
        this.usersByEmailService = usersByEmailService;
        this.settingsManager = settingsManager;
    }

    public boolean handle(ReceivedEmail email) throws EmailHandlingException {
        if (!this.emailGatewaySettingsManager.isAllowToCreatePageByEmail()) {
            return false;
        }
        String userAddress = email.getSender().getAddress();
        try {
            User user = this.usersByEmailService.getUniqueUserByEmail(userAddress);
            if (user == null) {
                throw new EntityException("No user with email address: " + userAddress);
            }
        }
        catch (EntityException e) {
            throw new NoMatchingUserToCreatePageException(userAddress);
        }
        for (SerializableAttachment attachment : email.getAttachments()) {
            if ((long)attachment.getContents().length <= this.settingsManager.getGlobalSettings().getAttachmentMaxSize()) continue;
            throw new CreatePageAttachmentHandleException("Cannot create page because big attachments");
        }
        try {
            this.emailStagingService.stageEmailThread(email);
        }
        catch (EmailStagingException e) {
            throw new EmailHandlingException(e);
        }
        return true;
    }

    public EmailToContentConverter<Page> getConverter() {
        return this.converter;
    }
}

