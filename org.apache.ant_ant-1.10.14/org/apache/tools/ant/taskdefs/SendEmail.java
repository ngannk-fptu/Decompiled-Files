/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.taskdefs.email.EmailTask;

public class SendEmail
extends EmailTask {
    @Deprecated
    public void setMailport(Integer value) {
        this.setMailport((int)value);
    }
}

