/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.tribes.Member
 */
package org.apache.catalina.ha;

import java.io.Serializable;
import org.apache.catalina.tribes.Member;

public interface ClusterMessage
extends Serializable {
    public Member getAddress();

    public void setAddress(Member var1);

    public String getUniqueId();

    public long getTimestamp();

    public void setTimestamp(long var1);
}

