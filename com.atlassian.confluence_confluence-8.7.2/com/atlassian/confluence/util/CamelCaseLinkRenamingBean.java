/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.AbstractLinkRenamingBean;
import java.util.regex.Pattern;

public class CamelCaseLinkRenamingBean
extends AbstractLinkRenamingBean {
    private static final Pattern p = Pattern.compile("([^a-zA-Z0-9!/\\[]|^)([A-Z])([a-z]+([A-Z][a-zA-Z0-9]+)+)(([^a-zA-Z0-9!\\]])|\r?\n|$)");
    private SettingsManager settingsManager;

    public CamelCaseLinkRenamingBean(ContentEntityObject referringContent, SpaceContentEntityObject pageBeingChanged, String newSpaceKey, String newTitle) {
        super(referringContent, pageBeingChanged, newSpaceKey, newTitle);
    }

    @Override
    public void handleText(StringBuffer buffer, String s) {
        if (this.isCamelCaseLink(this.getPageBeingChanged().getTitle()) && this.settingsManager.getGlobalSettings().isAllowCamelCase()) {
            buffer.append(s.replaceAll(this.getPageBeingChanged().getTitle(), this.getNewTitle()));
        } else {
            buffer.append(s);
        }
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    private boolean isCamelCaseLink(String linkText) {
        return p.matcher(linkText).matches();
    }

    private String getNewTitle() {
        if (this.getReferringContentSpaceKey() != null && this.getReferringContentSpaceKey().equalsIgnoreCase(this.newSpaceKey)) {
            return this.newTitle;
        }
        return "[" + this.newSpaceKey + ":" + this.newTitle + "]";
    }
}

