/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.sal.api.user.UserKey
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.mail;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mail.AbstractAtlassianMailService;
import com.atlassian.upm.mail.UpmEmail;
import com.opensymphony.module.propertyset.PropertySet;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceMailService
extends AbstractAtlassianMailService {
    public static final String MAIL = "mail";
    private final MultiQueueTaskManager taskManager;
    private final UserAccessor userAccessor;
    private final SettingsManager settingsManager;

    public ConfluenceMailService(MultiQueueTaskManager taskManager, UserAccessor userAccessor, SettingsManager settingsManager) {
        this.taskManager = Objects.requireNonNull(taskManager, "taskManager");
        this.userAccessor = Objects.requireNonNull(userAccessor, "userAccessor");
        this.settingsManager = Objects.requireNonNull(settingsManager, "settingsManager");
    }

    @Override
    public void sendMail(UpmEmail email) {
        this.taskManager.addTask(MAIL, (Task & Serializable)() -> this.createMailQueueItem(email).send());
    }

    @Override
    public UpmEmail.Format getUserEmailFormatPreference(UserKey userKey) {
        String mimePref;
        PropertySet propertySet = this.userAccessor.getPropertySet(this.userAccessor.getUser(userKey.getStringValue()));
        if (propertySet != null && StringUtils.isNotBlank((CharSequence)(mimePref = propertySet.getString("confluence.prefs.email.mimetype"))) && UpmEmail.Format.TEXT.getMimeType().equals(mimePref)) {
            return UpmEmail.Format.TEXT;
        }
        return UpmEmail.Format.HTML;
    }

    @Override
    public Option<String> getInstanceName() {
        return Option.some(this.settingsManager.getGlobalSettings().getSiteTitle());
    }
}

