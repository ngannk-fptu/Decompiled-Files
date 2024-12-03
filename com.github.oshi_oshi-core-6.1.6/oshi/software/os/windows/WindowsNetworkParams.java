/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.win32.IPHlpAPI
 *  com.sun.jna.platform.win32.IPHlpAPI$FIXED_INFO
 *  com.sun.jna.platform.win32.IPHlpAPI$IP_ADDR_STRING
 *  com.sun.jna.platform.win32.Kernel32
 *  com.sun.jna.platform.win32.Kernel32Util
 *  com.sun.jna.ptr.IntByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.software.os.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.ptr.IntByReference;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractNetworkParams;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class WindowsNetworkParams
extends AbstractNetworkParams {
    private static final Logger LOG = LoggerFactory.getLogger(WindowsNetworkParams.class);
    private static final int COMPUTER_NAME_DNS_DOMAIN_FULLY_QUALIFIED = 3;

    WindowsNetworkParams() {
    }

    @Override
    public String getDomainName() {
        char[] buffer = new char[256];
        IntByReference bufferSize = new IntByReference(buffer.length);
        if (!Kernel32.INSTANCE.GetComputerNameEx(3, buffer, bufferSize)) {
            LOG.error("Failed to get dns domain name. Error code: {}", (Object)Kernel32.INSTANCE.GetLastError());
            return "";
        }
        return Native.toString((char[])buffer);
    }

    @Override
    public String[] getDnsServers() {
        IntByReference bufferSize = new IntByReference();
        int ret = IPHlpAPI.INSTANCE.GetNetworkParams(null, bufferSize);
        if (ret != 111) {
            LOG.error("Failed to get network parameters buffer size. Error code: {}", (Object)ret);
            return new String[0];
        }
        Memory buffer = new Memory((long)bufferSize.getValue());
        ret = IPHlpAPI.INSTANCE.GetNetworkParams((Pointer)buffer, bufferSize);
        if (ret != 0) {
            LOG.error("Failed to get network parameters. Error code: {}", (Object)ret);
            return new String[0];
        }
        IPHlpAPI.FIXED_INFO fixedInfo = new IPHlpAPI.FIXED_INFO((Pointer)buffer);
        ArrayList<String> list = new ArrayList<String>();
        IPHlpAPI.IP_ADDR_STRING dns = fixedInfo.DnsServerList;
        while (dns != null) {
            String addr = Native.toString((byte[])dns.IpAddress.String, (Charset)StandardCharsets.US_ASCII);
            int nullPos = addr.indexOf(0);
            if (nullPos != -1) {
                addr = addr.substring(0, nullPos);
            }
            list.add(addr);
            dns = dns.Next;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public String getHostName() {
        return Kernel32Util.getComputerName();
    }

    @Override
    public String getIpv4DefaultGateway() {
        return WindowsNetworkParams.parseIpv4Route();
    }

    @Override
    public String getIpv6DefaultGateway() {
        return WindowsNetworkParams.parseIpv6Route();
    }

    private static String parseIpv4Route() {
        List<String> lines = ExecutingCommand.runNative("route print -4 0.0.0.0");
        for (String line : lines) {
            String[] fields = ParseUtil.whitespaces.split(line.trim());
            if (fields.length <= 2 || !"0.0.0.0".equals(fields[0])) continue;
            return fields[2];
        }
        return "";
    }

    private static String parseIpv6Route() {
        List<String> lines = ExecutingCommand.runNative("route print -6 ::/0");
        for (String line : lines) {
            String[] fields = ParseUtil.whitespaces.split(line.trim());
            if (fields.length <= 3 || !"::/0".equals(fields[2])) continue;
            return fields[3];
        }
        return "";
    }
}

