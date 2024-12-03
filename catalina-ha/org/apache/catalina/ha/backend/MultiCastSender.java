/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.backend;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.ha.backend.HeartbeatListener;
import org.apache.catalina.ha.backend.Sender;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class MultiCastSender
implements Sender {
    private static final Log log = LogFactory.getLog(HeartbeatListener.class);
    private static final StringManager sm = StringManager.getManager(MultiCastSender.class);
    HeartbeatListener config = null;
    MulticastSocket s = null;
    InetAddress group = null;

    @Override
    public void init(HeartbeatListener config) throws Exception {
        this.config = config;
    }

    @Override
    public int send(String mess) throws Exception {
        if (this.s == null) {
            try {
                this.group = InetAddress.getByName(this.config.getGroup());
                if (this.config.getHost() != null) {
                    InetAddress addr = InetAddress.getByName(this.config.getHost());
                    InetSocketAddress addrs = new InetSocketAddress(addr, this.config.getMultiport());
                    this.s = new MulticastSocket(addrs);
                } else {
                    this.s = new MulticastSocket(this.config.getMultiport());
                }
                this.s.setTimeToLive(this.config.getTtl());
                this.s.joinGroup(new InetSocketAddress(this.group, 0), null);
            }
            catch (Exception ex) {
                log.error((Object)sm.getString("multiCastSender.multiCastFailed"), (Throwable)ex);
                this.s = null;
                return -1;
            }
        }
        byte[] buf = mess.getBytes(StandardCharsets.US_ASCII);
        DatagramPacket data = new DatagramPacket(buf, buf.length, this.group, this.config.getMultiport());
        try {
            this.s.send(data);
        }
        catch (Exception ex) {
            log.error((Object)sm.getString("multiCastSender.sendFailed"), (Throwable)ex);
            this.s.close();
            this.s = null;
            return -1;
        }
        return 0;
    }
}

