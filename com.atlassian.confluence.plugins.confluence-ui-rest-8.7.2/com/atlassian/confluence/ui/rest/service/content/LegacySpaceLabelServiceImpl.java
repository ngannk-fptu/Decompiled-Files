/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.LabelPermissionEnforcer
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.legacyapi.service.content.SpaceLabelService
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.util.I18NSupport
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ui.rest.service.content;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.legacyapi.service.content.SpaceLabelService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.I18NSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class LegacySpaceLabelServiceImpl
implements SpaceLabelService {
    private final SpaceManager spaceManager;
    private final LabelPermissionEnforcer labelPermissionEnforcer;
    private final LabelManager labelManager;

    @Autowired
    public LegacySpaceLabelServiceImpl(@ComponentImport SpaceManager spaceManager, @ComponentImport LabelPermissionEnforcer labelPermissionEnforcer, @ComponentImport LabelManager labelManager) {
        this.spaceManager = spaceManager;
        this.labelPermissionEnforcer = labelPermissionEnforcer;
        this.labelManager = labelManager;
    }

    public void removeLabel(String spaceKey, long labelId) throws IllegalArgumentException {
        Space space = this.spaceManager.getSpace(spaceKey);
        Label label = this.labelManager.getLabel(labelId);
        if (label != null) {
            if (!this.labelPermissionEnforcer.userCanEditLabel(label, (Labelable)space.getDescription())) {
                throw new IllegalArgumentException(I18NSupport.getText((String)"you.cannot.remove.this.label"));
            }
            this.labelManager.removeLabel((Labelable)space.getDescription(), label);
        }
    }
}

