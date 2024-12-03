/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.ptr.PointerByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.software.os.mac;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.mac.SystemB;
import oshi.jna.platform.unix.CLibrary;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class MacNetworkParams
extends AbstractNetworkParams {
    private static final Logger LOG = LoggerFactory.getLogger(MacNetworkParams.class);
    private static final SystemB SYS = SystemB.INSTANCE;
    private static final String IPV6_ROUTE_HEADER = "Internet6:";
    private static final String DEFAULT_GATEWAY = "default";

    MacNetworkParams() {
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
        int res = SYS.getaddrinfo(hostname, null, hint, ptr);
        if (res > 0) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Failed getaddrinfo(): {}", (Object)SYS.gai_strerror(res));
            }
            return "";
        }
        CLibrary.Addrinfo info = new CLibrary.Addrinfo(ptr.getValue());
        String canonname = info.ai_canonname.trim();
        SYS.freeaddrinfo(ptr.getValue());
        return canonname;
    }

    @Override
    public String getHostName() {
        byte[] hostnameBuffer = new byte[256];
        if (0 != SYS.gethostname(hostnameBuffer, hostnameBuffer.length)) {
            return super.getHostName();
        }
        return Native.toString((byte[])hostnameBuffer);
    }

    @Override
    public String getIpv4DefaultGateway() {
        return MacNetworkParams.searchGateway(ExecutingCommand.runNative("route -n get default"));
    }

    @Override
    public String getIpv6DefaultGateway() {
        List<String> lines = ExecutingCommand.runNative("netstat -nr");
        boolean v6Table = false;
        for (String line : lines) {
            if (v6Table && line.startsWith(DEFAULT_GATEWAY)) {
                String[] fields = ParseUtil.whitespaces.split(line);
                if (fields.length <= 2 || !fields[2].contains("G")) continue;
                return fields[1].split("%")[0];
            }
            if (!line.startsWith(IPV6_ROUTE_HEADER)) continue;
            v6Table = true;
        }
        return "";
    }
}

