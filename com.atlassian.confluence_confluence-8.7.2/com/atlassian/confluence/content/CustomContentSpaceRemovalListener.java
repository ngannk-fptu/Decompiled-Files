/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.event.api.EventListener
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.content;

import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.event.events.space.SpaceWillRemoveEvent;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.event.api.EventListener;
import org.springframework.beans.factory.annotation.Qualifier;

public class CustomContentSpaceRemovalListener {
    private final CustomContentManager customContentManager;
    private final I18NBeanFactory i18NBeanFactory;

    public CustomContentSpaceRemovalListener(CustomContentManager customContentManager, @Qualifier(value="userI18NBeanFactory") I18NBeanFactory i18NBeanFactory) {
        this.customContentManager = customContentManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @EventListener
    public void spaceIsBeingRemoved(SpaceWillRemoveEvent event) {
        ProgressMeter progress = event.getProgressMeter();
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        if (!progress.isCompletedSuccessfully()) {
            return;
        }
        try {
            progress.setStatus(i18NBean.getText("progress.remove.space.custom.content"));
            this.customContentManager.removeAllPluginContentInSpace(event.getSpace());
        }
        catch (Error | RuntimeException e) {
            progress.setCompletedSuccessfully(false);
            progress.setStatus(i18NBean.getText("progress.remove.space.failed"));
            throw e;
        }
    }
}

