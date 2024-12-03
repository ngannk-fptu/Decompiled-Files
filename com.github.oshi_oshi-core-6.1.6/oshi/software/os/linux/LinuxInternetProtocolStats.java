/*
 * Decompiled with CFR 0.152.
 */
package oshi.software.os.linux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.ProcessStat;
import oshi.driver.unix.NetStat;
import oshi.software.common.AbstractInternetProtocolStats;
import oshi.software.os.InternetProtocolStats;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Pair;

@ThreadSafe
public class LinuxInternetProtocolStats
extends AbstractInternetProtocolStats {
    @Override
    public InternetProtocolStats.TcpStats getTCPv4Stats() {
        return NetStat.queryTcpStats("netstat -st4");
    }

    @Override
    public InternetProtocolStats.UdpStats getUDPv4Stats() {
        return NetStat.queryUdpStats("netstat -su4");
    }

    @Override
    public InternetProtocolStats.UdpStats getUDPv6Stats() {
        return NetStat.queryUdpStats("netstat -su6");
    }

    @Override
    public List<InternetProtocolStats.IPConnection> getConnections() {
        ArrayList<InternetProtocolStats.IPConnection> conns = new ArrayList<InternetProtocolStats.IPConnection>();
        Map<Integer, Integer> pidMap = ProcessStat.querySocketToPidMap();
        conns.addAll(LinuxInternetProtocolStats.queryConnections("tcp", 4, pidMap));
        conns.addAll(LinuxInternetProtocolStats.queryConnections("tcp", 6, pidMap));
        conns.addAll(LinuxInternetProtocolStats.queryConnections("udp", 4, pidMap));
        conns.addAll(LinuxInternetProtocolStats.queryConnections("udp", 6, pidMap));
        return conns;
    }

    private static List<InternetProtocolStats.IPConnection> queryConnections(String protocol, int ipver, Map<Integer, Integer> pidMap) {
        ArrayList<InternetProtocolStats.IPConnection> conns = new ArrayList<InternetProtocolStats.IPConnection>();
        for (String s : FileUtil.readFile(ProcPath.NET + "/" + protocol + (ipver == 6 ? "6" : ""))) {
            String[] split;
            if (s.indexOf(58) < 0 || (split = ParseUtil.whitespaces.split(s.trim())).length <= 9) continue;
            Pair<byte[], Integer> lAddr = LinuxInternetProtocolStats.parseIpAddr(split[1]);
            Pair<byte[], Integer> fAddr = LinuxInternetProtocolStats.parseIpAddr(split[2]);
            InternetProtocolStats.TcpState state = LinuxInternetProtocolStats.stateLookup(ParseUtil.hexStringToInt(split[3], 0));
            Pair<Integer, Integer> txQrxQ = LinuxInternetProtocolStats.parseHexColonHex(split[4]);
            int inode = ParseUtil.parseIntOrDefault(split[9], 0);
            conns.add(new InternetProtocolStats.IPConnection(protocol + ipver, lAddr.getA(), lAddr.getB(), fAddr.getA(), fAddr.getB(), state, txQrxQ.getA(), txQrxQ.getB(), pidMap.getOrDefault(inode, -1)));
        }
        return conns;
    }

    private static Pair<byte[], Integer> parseIpAddr(String s) {
        int colon = s.indexOf(58);
        if (colon > 0 && colon < s.length()) {
            byte[] first = ParseUtil.hexStringToByteArray(s.substring(0, colon));
            int i = 0;
            while (i + 3 < first.length) {
                byte tmp = first[i];
                first[i] = first[i + 3];
                first[i + 3] = tmp;
                tmp = first[i + 1];
                first[i + 1] = first[i + 2];
                first[i + 2] = tmp;
                i += 4;
            }
            int second = ParseUtil.hexStringToInt(s.substring(colon + 1), 0);
            return new Pair<byte[], Integer>(first, second);
        }
        return new Pair<byte[], Integer>(new byte[0], 0);
    }

    private static Pair<Integer, Integer> parseHexColonHex(String s) {
        int colon = s.indexOf(58);
        if (colon > 0 && colon < s.length()) {
            int first = ParseUtil.hexStringToInt(s.substring(0, colon), 0);
            int second = ParseUtil.hexStringToInt(s.substring(colon + 1), 0);
            return new Pair<Integer, Integer>(first, second);
        }
        return new Pair<Integer, Integer>(0, 0);
    }

    private static InternetProtocolStats.TcpState stateLookup(int state) {
        switch (state) {
            case 1: {
                return InternetProtocolStats.TcpState.ESTABLISHED;
            }
            case 2: {
                return InternetProtocolStats.TcpState.SYN_SENT;
            }
            case 3: {
                return InternetProtocolStats.TcpState.SYN_RECV;
            }
            case 4: {
                return InternetProtocolStats.TcpState.FIN_WAIT_1;
            }
            case 5: {
                return InternetProtocolStats.TcpState.FIN_WAIT_2;
            }
            case 6: {
                return InternetProtocolStats.TcpState.TIME_WAIT;
            }
            case 7: {
                return InternetProtocolStats.TcpState.CLOSED;
            }
            case 8: {
                return InternetProtocolStats.TcpState.CLOSE_WAIT;
            }
            case 9: {
                return InternetProtocolStats.TcpState.LAST_ACK;
            }
            case 10: {
                return InternetProtocolStats.TcpState.LISTEN;
            }
            case 11: {
                return InternetProtocolStats.TcpState.CLOSING;
            }
        }
        return InternetProtocolStats.TcpState.UNKNOWN;
    }
}

