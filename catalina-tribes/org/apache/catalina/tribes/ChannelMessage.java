/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.io.Serializable;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.io.XByteBuffer;

public interface ChannelMessage
extends Serializable,
Cloneable {
    public Member getAddress();

    public void setAddress(Member var1);

    public long getTimestamp();

    public void setTimestamp(long var1);

    public byte[] getUniqueId();

    public void setMessage(XByteBuffer var1);

    public XByteBuffer getMessage();

    public int getOptions();

    public void setOptions(int var1);

    public Object clone();

    public Object deepclone();
}

