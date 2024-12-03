/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group;

import java.io.Serializable;
import org.apache.catalina.tribes.Member;

public interface RpcCallback {
    public Serializable replyRequest(Serializable var1, Member var2);

    public void leftOver(Serializable var1, Member var2);
}

