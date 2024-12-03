/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.win32.IPHlpAPI
 *  com.sun.jna.platform.win32.IPHlpAPI$MIB_TCP6ROW_OWNER_PID
 *  com.sun.jna.platform.win32.IPHlpAPI$MIB_TCP6TABLE_OWNER_PID
 *  com.sun.jna.platform.win32.IPHlpAPI$MIB_TCPROW_OWNER_PID
 *  com.sun.jna.platform.win32.IPHlpAPI$MIB_TCPSTATS
 *  com.sun.jna.platform.win32.IPHlpAPI$MIB_TCPTABLE_OWNER_PID
 *  com.sun.jna.platform.win32.IPHlpAPI$MIB_UDP6ROW_OWNER_PID
 *  com.sun.jna.platform.win32.IPHlpAPI$MIB_UDP6TABLE_OWNER_PID
 *  com.sun.jna.platform.win32.IPHlpAPI$MIB_UDPROW_OWNER_PID
 *  com.sun.jna.platform.win32.IPHlpAPI$MIB_UDPSTATS
 *  com.sun.jna.platform.win32.IPHlpAPI$MIB_UDPTABLE_OWNER_PID
 *  com.sun.jna.platform.win32.VersionHelpers
 *  com.sun.jna.ptr.IntByReference
 */
package oshi.software.os.windows;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractInternetProtocolStats;
import oshi.software.os.InternetProtocolStats;
import oshi.util.ParseUtil;

@ThreadSafe
public class WindowsInternetProtocolStats
extends AbstractInternetProtocolStats {
    private static final IPHlpAPI IPHLP = IPHlpAPI.INSTANCE;
    private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();

    @Override
    public InternetProtocolStats.TcpStats getTCPv4Stats() {
        IPHlpAPI.MIB_TCPSTATS stats = new IPHlpAPI.MIB_TCPSTATS();
        IPHLP.GetTcpStatisticsEx(stats, 2);
        return new InternetProtocolStats.TcpStats(stats.dwCurrEstab, stats.dwActiveOpens, stats.dwPassiveOpens, stats.dwAttemptFails, stats.dwEstabResets, stats.dwOutSegs, stats.dwInSegs, stats.dwRetransSegs, stats.dwInErrs, stats.dwOutRsts);
    }

    @Override
    public InternetProtocolStats.TcpStats getTCPv6Stats() {
        IPHlpAPI.MIB_TCPSTATS stats = new IPHlpAPI.MIB_TCPSTATS();
        IPHLP.GetTcpStatisticsEx(stats, 23);
        return new InternetProtocolStats.TcpStats(stats.dwCurrEstab, stats.dwActiveOpens, stats.dwPassiveOpens, stats.dwAttemptFails, stats.dwEstabResets, stats.dwOutSegs, stats.dwInSegs, stats.dwRetransSegs, stats.dwInErrs, stats.dwOutRsts);
    }

    @Override
    public InternetProtocolStats.UdpStats getUDPv4Stats() {
        IPHlpAPI.MIB_UDPSTATS stats = new IPHlpAPI.MIB_UDPSTATS();
        IPHLP.GetUdpStatisticsEx(stats, 2);
        return new InternetProtocolStats.UdpStats(stats.dwOutDatagrams, stats.dwInDatagrams, stats.dwNoPorts, stats.dwInErrors);
    }

    @Override
    public InternetProtocolStats.UdpStats getUDPv6Stats() {
        IPHlpAPI.MIB_UDPSTATS stats = new IPHlpAPI.MIB_UDPSTATS();
        IPHLP.GetUdpStatisticsEx(stats, 23);
        return new InternetProtocolStats.UdpStats(stats.dwOutDatagrams, stats.dwInDatagrams, stats.dwNoPorts, stats.dwInErrors);
    }

    @Override
    public List<InternetProtocolStats.IPConnection> getConnections() {
        if (IS_VISTA_OR_GREATER) {
            ArrayList<InternetProtocolStats.IPConnection> conns = new ArrayList<InternetProtocolStats.IPConnection>();
            conns.addAll(WindowsInternetProtocolStats.queryTCPv4Connections());
            conns.addAll(WindowsInternetProtocolStats.queryTCPv6Connections());
            conns.addAll(WindowsInternetProtocolStats.queryUDPv4Connections());
            conns.addAll(WindowsInternetProtocolStats.queryUDPv6Connections());
            return conns;
        }
        return Collections.emptyList();
    }

    private static List<InternetProtocolStats.IPConnection> queryTCPv4Connections() {
        Memory buf;
        int size;
        ArrayList<InternetProtocolStats.IPConnection> conns = new ArrayList<InternetProtocolStats.IPConnection>();
        IntByReference sizePtr = new IntByReference();
        IPHLP.GetExtendedTcpTable(null, sizePtr, false, 2, 5, 0);
        do {
            size = sizePtr.getValue();
            buf = new Memory((long)size);
            IPHLP.GetExtendedTcpTable((Pointer)buf, sizePtr, false, 2, 5, 0);
        } while (size < sizePtr.getValue());
        IPHlpAPI.MIB_TCPTABLE_OWNER_PID tcpTable = new IPHlpAPI.MIB_TCPTABLE_OWNER_PID((Pointer)buf);
        for (int i = 0; i < tcpTable.dwNumEntries; ++i) {
            IPHlpAPI.MIB_TCPROW_OWNER_PID row = tcpTable.table[i];
            conns.add(new InternetProtocolStats.IPConnection("tcp4", ParseUtil.parseIntToIP(row.dwLocalAddr), ParseUtil.bigEndian16ToLittleEndian(row.dwLocalPort), ParseUtil.parseIntToIP(row.dwRemoteAddr), ParseUtil.bigEndian16ToLittleEndian(row.dwRemotePort), WindowsInternetProtocolStats.stateLookup(row.dwState), 0, 0, row.dwOwningPid));
        }
        return conns;
    }

    private static List<InternetProtocolStats.IPConnection> queryTCPv6Connections() {
        Memory buf;
        int size;
        ArrayList<InternetProtocolStats.IPConnection> conns = new ArrayList<InternetProtocolStats.IPConnection>();
        IntByReference sizePtr = new IntByReference();
        IPHLP.GetExtendedTcpTable(null, sizePtr, false, 23, 5, 0);
        do {
            size = sizePtr.getValue();
            buf = new Memory((long)size);
            IPHLP.GetExtendedTcpTable((Pointer)buf, sizePtr, false, 23, 5, 0);
        } while (size < sizePtr.getValue());
        IPHlpAPI.MIB_TCP6TABLE_OWNER_PID tcpTable = new IPHlpAPI.MIB_TCP6TABLE_OWNER_PID((Pointer)buf);
        for (int i = 0; i < tcpTable.dwNumEntries; ++i) {
            IPHlpAPI.MIB_TCP6ROW_OWNER_PID row = tcpTable.table[i];
            conns.add(new InternetProtocolStats.IPConnection("tcp6", row.LocalAddr, ParseUtil.bigEndian16ToLittleEndian(row.dwLocalPort), row.RemoteAddr, ParseUtil.bigEndian16ToLittleEndian(row.dwRemotePort), WindowsInternetProtocolStats.stateLookup(row.State), 0, 0, row.dwOwningPid));
        }
        return conns;
    }

    private static List<InternetProtocolStats.IPConnection> queryUDPv4Connections() {
        Memory buf;
        int size;
        ArrayList<InternetProtocolStats.IPConnection> conns = new ArrayList<InternetProtocolStats.IPConnection>();
        IntByReference sizePtr = new IntByReference();
        IPHLP.GetExtendedUdpTable(null, sizePtr, false, 2, 1, 0);
        do {
            size = sizePtr.getValue();
            buf = new Memory((long)size);
            IPHLP.GetExtendedUdpTable((Pointer)buf, sizePtr, false, 2, 1, 0);
        } while (size < sizePtr.getValue());
        IPHlpAPI.MIB_UDPTABLE_OWNER_PID udpTable = new IPHlpAPI.MIB_UDPTABLE_OWNER_PID((Pointer)buf);
        for (int i = 0; i < udpTable.dwNumEntries; ++i) {
            IPHlpAPI.MIB_UDPROW_OWNER_PID row = udpTable.table[i];
            conns.add(new InternetProtocolStats.IPConnection("udp4", ParseUtil.parseIntToIP(row.dwLocalAddr), ParseUtil.bigEndian16ToLittleEndian(row.dwLocalPort), new byte[0], 0, InternetProtocolStats.TcpState.NONE, 0, 0, row.dwOwningPid));
        }
        return conns;
    }

    private static List<InternetProtocolStats.IPConnection> queryUDPv6Connections() {
        Memory buf;
        int size;
        ArrayList<InternetProtocolStats.IPConnection> conns = new ArrayList<InternetProtocolStats.IPConnection>();
        IntByReference sizePtr = new IntByReference();
        IPHLP.GetExtendedUdpTable(null, sizePtr, false, 23, 1, 0);
        do {
            size = sizePtr.getValue();
            buf = new Memory((long)size);
            IPHLP.GetExtendedUdpTable((Pointer)buf, sizePtr, false, 23, 1, 0);
        } while (size < sizePtr.getValue());
        IPHlpAPI.MIB_UDP6TABLE_OWNER_PID udpTable = new IPHlpAPI.MIB_UDP6TABLE_OWNER_PID((Pointer)buf);
        for (int i = 0; i < udpTable.dwNumEntries; ++i) {
            IPHlpAPI.MIB_UDP6ROW_OWNER_PID row = udpTable.table[i];
            conns.add(new InternetProtocolStats.IPConnection("udp6", row.ucLocalAddr, ParseUtil.bigEndian16ToLittleEndian(row.dwLocalPort), new byte[0], 0, InternetProtocolStats.TcpState.NONE, 0, 0, row.dwOwningPid));
        }
        return conns;
    }

    private static InternetProtocolStats.TcpState stateLookup(int state) {
        switch (state) {
            case 1: 
            case 12: {
                return InternetProtocolStats.TcpState.CLOSED;
            }
            case 2: {
                return InternetProtocolStats.TcpState.LISTEN;
            }
            case 3: {
                return InternetProtocolStats.TcpState.SYN_SENT;
            }
            case 4: {
                return InternetProtocolStats.TcpState.SYN_RECV;
            }
            case 5: {
                return InternetProtocolStats.TcpState.ESTABLISHED;
            }
            case 6: {
                return InternetProtocolStats.TcpState.FIN_WAIT_1;
            }
            case 7: {
                return InternetProtocolStats.TcpState.FIN_WAIT_2;
            }
            case 8: {
                return InternetProtocolStats.TcpState.CLOSE_WAIT;
            }
            case 9: {
                return InternetProtocolStats.TcpState.CLOSING;
            }
            case 10: {
                return InternetProtocolStats.TcpState.LAST_ACK;
            }
            case 11: {
                return InternetProtocolStats.TcpState.TIME_WAIT;
            }
        }
        return InternetProtocolStats.TcpState.UNKNOWN;
    }
}

