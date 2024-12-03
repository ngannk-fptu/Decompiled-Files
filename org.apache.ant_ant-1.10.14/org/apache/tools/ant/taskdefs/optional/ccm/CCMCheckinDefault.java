/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ccm;

import org.apache.tools.ant.taskdefs.optional.ccm.CCMCheck;

public class CCMCheckinDefault
extends CCMCheck {
    public static final String DEFAULT_TASK = "default";

    public CCMCheckinDefault() {
        this.setCcmAction("ci");
        this.setTask(DEFAULT_TASK);
    }
}

