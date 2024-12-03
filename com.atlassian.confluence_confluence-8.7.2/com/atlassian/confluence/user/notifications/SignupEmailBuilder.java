/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user.notifications;

import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.user.User;

public class SignupEmailBuilder {
    private final I18NBeanFactory i18NBeanFactory;

    public SignupEmailBuilder(I18NBeanFactory i18nBeanFactory) {
        this.i18NBeanFactory = i18nBeanFactory;
    }

    public PreRenderedMailNotificationQueueItem buildFrom(String token, User user) {
        String username = user.getName();
        PreRenderedMailNotificationQueueItem queueItem = (PreRenderedMailNotificationQueueItem)PreRenderedMailNotificationQueueItem.createFromTemplateFileAndLocation(user, "/templates/email/html", "confirm-email.vm", this.getI18nBean().getText("easyuser.confirm.email"));
        queueItem.addVelocityContextParam("user", user);
        queueItem.addVelocityContextParam("confirmationPath", "/confirmemail.action?token=" + token + "&username=" + HtmlUtil.urlEncode(username));
        queueItem.render();
        return queueItem;
    }

    private I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean();
    }
}

