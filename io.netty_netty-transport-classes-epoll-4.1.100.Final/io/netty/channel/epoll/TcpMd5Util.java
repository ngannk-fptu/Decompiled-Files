/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.channel.epoll;

import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.Native;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

final class TcpMd5Util {
    static Collection<InetAddress> newTcpMd5Sigs(AbstractEpollChannel channel, Collection<InetAddress> current, Map<InetAddress, byte[]> newKeys) throws IOException {
        ObjectUtil.checkNotNull((Object)((Object)channel), (String)"channel");
        ObjectUtil.checkNotNull(current, (String)"current");
        ObjectUtil.checkNotNull(newKeys, (String)"newKeys");
        for (Map.Entry<InetAddress, byte[]> e : newKeys.entrySet()) {
            byte[] key = e.getValue();
            ObjectUtil.checkNotNullWithIAE((Object)e.getKey(), (String)"e.getKey");
            ObjectUtil.checkNonEmpty((byte[])key, (String)e.getKey().toString());
            if (key.length <= Native.TCP_MD5SIG_MAXKEYLEN) continue;
            throw new IllegalArgumentException("newKeys[" + e.getKey() + "] has a key with invalid length; should not exceed the maximum length (" + Native.TCP_MD5SIG_MAXKEYLEN + ')');
        }
        for (InetAddress addr : current) {
            if (newKeys.containsKey(addr)) continue;
            channel.socket.setTcpMd5Sig(addr, null);
        }
        if (newKeys.isEmpty()) {
            return Collections.emptySet();
        }
        ArrayList<InetAddress> addresses = new ArrayList<InetAddress>(newKeys.size());
        for (Map.Entry<InetAddress, byte[]> e : newKeys.entrySet()) {
            channel.socket.setTcpMd5Sig(e.getKey(), e.getValue());
            addresses.add(e.getKey());
        }
        return addresses;
    }

    private TcpMd5Util() {
    }
}

