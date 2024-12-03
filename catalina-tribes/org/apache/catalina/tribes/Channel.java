/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes;

import java.io.Serializable;
import java.util.StringJoiner;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.ErrorHandler;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.UniqueId;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public interface Channel {
    public static final int DEFAULT = 15;
    public static final int SND_RX_SEQ = 1;
    public static final int SND_TX_SEQ = 2;
    public static final int MBR_RX_SEQ = 4;
    public static final int MBR_TX_SEQ = 8;
    public static final int SEND_OPTIONS_BYTE_MESSAGE = 1;
    public static final int SEND_OPTIONS_USE_ACK = 2;
    public static final int SEND_OPTIONS_SYNCHRONIZED_ACK = 4;
    public static final int SEND_OPTIONS_ASYNCHRONOUS = 8;
    public static final int SEND_OPTIONS_SECURE = 16;
    public static final int SEND_OPTIONS_UDP = 32;
    public static final int SEND_OPTIONS_MULTICAST = 64;
    public static final int SEND_OPTIONS_DEFAULT = 2;

    public void addInterceptor(ChannelInterceptor var1);

    public void start(int var1) throws ChannelException;

    public void stop(int var1) throws ChannelException;

    public UniqueId send(Member[] var1, Serializable var2, int var3) throws ChannelException;

    public UniqueId send(Member[] var1, Serializable var2, int var3, ErrorHandler var4) throws ChannelException;

    public void heartbeat();

    public void setHeartbeat(boolean var1);

    public void addMembershipListener(MembershipListener var1);

    public void addChannelListener(ChannelListener var1);

    public void removeMembershipListener(MembershipListener var1);

    public void removeChannelListener(ChannelListener var1);

    public boolean hasMembers();

    public Member[] getMembers();

    public Member getLocalMember(boolean var1);

    public Member getMember(Member var1);

    public String getName();

    public void setName(String var1);

    public ScheduledExecutorService getUtilityExecutor();

    public void setUtilityExecutor(ScheduledExecutorService var1);

    public static int getSendOptionValue(String opt) {
        switch (opt) {
            case "asynchronous": 
            case "async": {
                return 8;
            }
            case "byte_message": 
            case "byte": {
                return 1;
            }
            case "multicast": {
                return 64;
            }
            case "secure": {
                return 16;
            }
            case "synchronized_ack": 
            case "sync": {
                return 4;
            }
            case "udp": {
                return 32;
            }
            case "use_ack": {
                return 2;
            }
        }
        throw new IllegalArgumentException(String.format("[%s] is not a valid option", opt));
    }

    public static int parseSendOptions(String input) {
        try {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException nfe) {
            Log log = LogFactory.getLog(Channel.class);
            log.trace((Object)String.format("Failed to parse [%s] as integer, channelSendOptions possibly set by name(s)", input));
            String[] options = input.split("\\s*,\\s*");
            int result = 0;
            for (String opt : options) {
                result |= Channel.getSendOptionValue(opt);
            }
            return result;
        }
    }

    public static String getSendOptionsAsString(int input) {
        String[] allOptionNames = new String[]{"byte", "use_ack", "sync", "async", "secure", "udp", "multicast"};
        StringJoiner names = new StringJoiner(", ");
        for (int bit = allOptionNames.length - 1; bit >= 0; --bit) {
            if ((1 << bit & input) <= 0) continue;
            names.add(allOptionNames[bit]);
        }
        return names.toString();
    }
}

