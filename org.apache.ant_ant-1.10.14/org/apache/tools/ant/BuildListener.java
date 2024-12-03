/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.util.EventListener;
import org.apache.tools.ant.BuildEvent;

public interface BuildListener
extends EventListener {
    public void buildStarted(BuildEvent var1);

    public void buildFinished(BuildEvent var1);

    public void targetStarted(BuildEvent var1);

    public void targetFinished(BuildEvent var1);

    public void taskStarted(BuildEvent var1);

    public void taskFinished(BuildEvent var1);

    public void messageLogged(BuildEvent var1);
}

