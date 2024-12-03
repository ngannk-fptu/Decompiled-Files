/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;

public class NoBannerLogger
extends DefaultLogger {
    protected String targetName;

    @Override
    public synchronized void targetStarted(BuildEvent event) {
        this.targetName = this.extractTargetName(event);
    }

    protected String extractTargetName(BuildEvent event) {
        return event.getTarget().getName();
    }

    @Override
    public synchronized void targetFinished(BuildEvent event) {
        this.targetName = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void messageLogged(BuildEvent event) {
        if (event.getPriority() > this.msgOutputLevel || null == event.getMessage() || event.getMessage().trim().isEmpty()) {
            return;
        }
        NoBannerLogger noBannerLogger = this;
        synchronized (noBannerLogger) {
            if (null != this.targetName) {
                this.out.printf("%n%s:%n", this.targetName);
                this.targetName = null;
            }
        }
        super.messageLogged(event);
    }
}

