/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ccm;

import java.util.Date;
import org.apache.tools.ant.taskdefs.optional.ccm.CCMCheck;

public class CCMCheckin
extends CCMCheck {
    public CCMCheckin() {
        this.setCcmAction("ci");
        this.setComment("Checkin " + new Date());
    }
}

