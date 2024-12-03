/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.platform.linux.LibC
 *  com.sun.jna.ptr.PointerByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.software.os.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import com.sun.jna.ptr.PointerByReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.jna.platform.unix.CLibrary;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class LinuxNetworkParams
extends AbstractNetworkParams {
    private static final Logger LOG = LoggerFactory.getLogger(LinuxNetworkParams.class);
    private static final LinuxLibc LIBC = LinuxLibc.INSTANCE;
    private static final String IPV4_DEFAULT_DEST = "0.0.0.0";
    private static final String IPV6_DEFAULT_DEST = "::/0";

    LinuxNetworkParams() {
    }

    @Override
    public String getDomainName() {
        CLibrary.Addrinfo hint = new CLibrary.Addrinfo();
        hint.ai_flags = 2;
        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            LOG.error("Unknown host exception when getting address of local host: {}", (Object)e.getMessage());
            return "";
        }
        PointerByReference ptr = new PointerByReference();
        int res = LIBC.getaddrinfo(hostname, null, hint, ptr);
        if (res > 0) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Failed getaddrinfo(): {}", (Object)LIBC.gai_strerror(res));
            }
            return "";
        }
        CLibrary.Addrinfo info = new CLibrary.Addrinfo(ptr.getValue());
        String canonname = info.ai_canonname.trim();
        LIBC.freeaddrinfo(ptr.getValue());
        return canonname;
    }

    @Override
    public String getHostName() {
        byte[] hostnameBuffer = new byte[256];
        if (0 != LibC.INSTANCE.gethostname(hostnameBuffer, hostnameBuffer.length)) {
            return super.getHostName();
        }
        return Native.toString((byte[])hostnameBuffer);
    }

    @Override
    public String getIpv4DefaultGateway() {
        List<String> routes = ExecutingCommand.runNative("route -A inet -n");
        if (routes.size() <= 2) {
            return "";
        }
        String gateway = "";
        int minMetric = Integer.MAX_VALUE;
        for (int i = 2; i < routes.size(); ++i) {
            String[] fields = ParseUtil.whitespaces.split(routes.get(i));
            if (fields.length <= 4 || !fields[0].equals(IPV4_DEFAULT_DEST)) continue;
            boolean isGateway = fields[3].indexOf(71) != -1;
            int metric = ParseUtil.parseIntOrDefault(fields[4], Integer.MAX_VALUE);
            if (!isGateway || metric >= minMetric) continue;
            minMetric = metric;
            gateway = fields[1];
        }
        return gateway;
    }

    @Override
    public String getIpv6DefaultGateway() {
        List<String> routes = ExecutingCommand.runNative("route -A inet6 -n");
        if (routes.size() <= 2) {
            return "";
        }
        String gateway = "";
        int minMetric = Integer.MAX_VALUE;
        for (int i = 2; i < routes.size(); ++i) {
            String[] fields = ParseUtil.whitespaces.split(routes.get(i));
            if (fields.length <= 3 || !fields[0].equals(IPV6_DEFAULT_DEST)) continue;
            boolean isGateway = fields[2].indexOf(71) != -1;
            int metric = ParseUtil.parseIntOrDefault(fields[3], Integer.MAX_VALUE);
            if (!isGateway || metric >= minMetric) continue;
            minMetric = metric;
            gateway = fields[1];
        }
        return gateway;
    }
}

