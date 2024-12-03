/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.tribes.Channel
 *  org.apache.catalina.tribes.ChannelInterceptor
 *  org.apache.catalina.tribes.ChannelReceiver
 *  org.apache.catalina.tribes.ChannelSender
 *  org.apache.catalina.tribes.ManagedChannel
 *  org.apache.catalina.tribes.MembershipService
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import java.util.Iterator;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.ChannelReceiver;
import org.apache.catalina.tribes.ChannelSender;
import org.apache.catalina.tribes.ManagedChannel;
import org.apache.catalina.tribes.MembershipService;

public class ChannelSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aChannel, StoreDescription parentDesc) throws Exception {
        Channel channel;
        if (aChannel instanceof Channel && (channel = (Channel)aChannel) instanceof ManagedChannel) {
            ChannelReceiver receiver;
            ChannelSender sender;
            ManagedChannel managedChannel = (ManagedChannel)channel;
            MembershipService service = managedChannel.getMembershipService();
            if (service != null) {
                this.storeElement(aWriter, indent, service);
            }
            if ((sender = managedChannel.getChannelSender()) != null) {
                this.storeElement(aWriter, indent, sender);
            }
            if ((receiver = managedChannel.getChannelReceiver()) != null) {
                this.storeElement(aWriter, indent, receiver);
            }
            Iterator interceptors = managedChannel.getInterceptors();
            while (interceptors.hasNext()) {
                ChannelInterceptor interceptor = (ChannelInterceptor)interceptors.next();
                this.storeElement(aWriter, indent, interceptor);
            }
        }
    }
}

