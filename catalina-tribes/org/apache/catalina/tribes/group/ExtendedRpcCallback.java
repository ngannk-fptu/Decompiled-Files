/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group;

import java.io.Serializable;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.RpcCallback;

public interface ExtendedRpcCallback
extends RpcCallback {
    public void replyFailed(Serializable var1, Serializable var2, Member var3, Exception var4);

    public void replySucceeded(Serializable var1, Serializable var2, Member var3);
}

