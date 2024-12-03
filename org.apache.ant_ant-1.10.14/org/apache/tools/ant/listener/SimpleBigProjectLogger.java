/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.listener;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.NoBannerLogger;

public class SimpleBigProjectLogger
extends NoBannerLogger {
    @Override
    protected String extractTargetName(BuildEvent event) {
        String targetName = super.extractTargetName(event);
        String projectName = this.extractProjectName(event);
        if (projectName == null || targetName == null) {
            return targetName;
        }
        return projectName + '.' + targetName;
    }
}

