/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class DeleteUserProfilePictureAction
extends AbstractUsersAction {
    private static final Logger log = LoggerFactory.getLogger(DeleteUserProfilePictureAction.class);
    private PersonalInformationManager personalInformationManager;
    private AttachmentManager attachmentManager;

    @Override
    public void validate() {
        super.validate();
        if (this.getUser() == null) {
            this.addActionError(this.getText("user.doesnt.exist"));
        }
    }

    public String execute() {
        try {
            List<Attachment> userProfilePictures = this.getProfilePictures();
            this.attachmentManager.removeAttachments(userProfilePictures);
            this.addActionMessage("delete.user.profile.picture.success", this.username);
            return "success";
        }
        catch (Exception e) {
            this.addActionError("delete.user.profile.picture.error", this.username);
            log.error("Error deleting profile picture for user", (Throwable)e);
            return "error";
        }
    }

    private List<Attachment> getProfilePictures() {
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation(this.getUser());
        return this.attachmentManager.getLatestVersionsOfAttachmentsWithAnyStatus(personalInformation);
    }

    public boolean hasProfilePictures() {
        return !this.getProfilePictures().isEmpty();
    }

    public void setPersonalInformationManager(PersonalInformationManager personalInformationManager) {
        this.personalInformationManager = personalInformationManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }
}

