/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.util.Iterator;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.ChannelReceiver;
import org.apache.catalina.tribes.ChannelSender;
import org.apache.catalina.tribes.MembershipService;

public interface ManagedChannel
extends Channel {
    public void setChannelSender(ChannelSender var1);

    public void setChannelReceiver(ChannelReceiver var1);

    public void setMembershipService(MembershipService var1);

    public ChannelSender getChannelSender();

    public ChannelReceiver getChannelReceiver();

    public MembershipService getMembershipService();

    public Iterator<ChannelInterceptor> getInterceptors();
}

