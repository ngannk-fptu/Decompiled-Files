/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

public interface SubBuildListener
extends BuildListener {
    public void subBuildStarted(BuildEvent var1);

    public void subBuildFinished(BuildEvent var1);
}

