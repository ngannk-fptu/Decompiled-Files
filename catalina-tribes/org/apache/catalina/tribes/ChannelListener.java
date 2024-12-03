/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.io.Serializable;
import org.apache.catalina.tribes.Member;

public interface ChannelListener {
    public void messageReceived(Serializable var1, Member var2);

    public boolean accept(Serializable var1, Member var2);

    public boolean equals(Object var1);

    public int hashCode();
}

