/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.listener;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;

public class SilentLogger
extends DefaultLogger {
    @Override
    public void buildStarted(BuildEvent event) {
    }

    @Override
    public void buildFinished(BuildEvent event) {
        if (event.getException() != null) {
            super.buildFinished(event);
        }
    }

    @Override
    public void targetStarted(BuildEvent event) {
    }

    @Override
    public void targetFinished(BuildEvent event) {
    }

    @Override
    public void taskStarted(BuildEvent event) {
    }

    @Override
    public void taskFinished(BuildEvent event) {
    }
}

