/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.net;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.components.net.SocketFactory;
import org.apache.axis.components.net.TransportClientProperties;
import org.apache.axis.components.net.TransportClientPropertiesFactory;
import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class DefaultSocketFactory
implements SocketFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$net$DefaultSocketFactory == null ? (class$org$apache$axis$components$net$DefaultSocketFactory = DefaultSocketFactory.class$("org.apache.axis.components.net.DefaultSocketFactory")) : class$org$apache$axis$components$net$DefaultSocketFactory).getName());
    public static String CONNECT_TIMEOUT = "axis.client.connect.timeout";
    protected Hashtable attributes = null;
    private static boolean plain;
    private static Class inetClass;
    private static Constructor inetConstructor;
    private static Constructor socketConstructor;
    private static Method connect;
    static /* synthetic */ Class class$org$apache$axis$components$net$DefaultSocketFactory;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$net$Socket;

    public DefaultSocketFactory(Hashtable attributes) {
        this.attributes = attributes;
    }

    public Socket create(String host, int port, StringBuffer otherHeaders, BooleanHolder useFullURL) throws Exception {
        int timeout = 0;
        if (this.attributes != null) {
            String value = (String)this.attributes.get(CONNECT_TIMEOUT);
            timeout = value != null ? Integer.parseInt(value) : 0;
        }
        TransportClientProperties tcp = TransportClientPropertiesFactory.create("http");
        Socket sock = null;
        boolean hostInNonProxyList = this.isHostInNonProxyList(host, tcp.getNonProxyHosts());
        if (tcp.getProxyUser().length() != 0) {
            StringBuffer tmpBuf = new StringBuffer();
            tmpBuf.append(tcp.getProxyUser()).append(":").append(tcp.getProxyPassword());
            otherHeaders.append("Proxy-Authorization").append(": Basic ").append(Base64.encode(tmpBuf.toString().getBytes())).append("\r\n");
        }
        if (port == -1) {
            port = 80;
        }
        if (tcp.getProxyHost().length() == 0 || tcp.getProxyPort().length() == 0 || hostInNonProxyList) {
            sock = DefaultSocketFactory.create(host, port, timeout);
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("createdHTTP00"));
            }
        } else {
            sock = DefaultSocketFactory.create(tcp.getProxyHost(), new Integer(tcp.getProxyPort()), timeout);
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("createdHTTP01", tcp.getProxyHost(), tcp.getProxyPort()));
            }
            useFullURL.value = true;
        }
        return sock;
    }

    private static Socket create(String host, int port, int timeout) throws Exception {
        Socket sock = null;
        if (plain || timeout == 0) {
            sock = new Socket(host, port);
        } else {
            Object address = inetConstructor.newInstance(host, new Integer(port));
            sock = (Socket)socketConstructor.newInstance(new Object[0]);
            connect.invoke((Object)sock, address, new Integer(timeout));
        }
        return sock;
    }

    protected boolean isHostInNonProxyList(String host, String nonProxyHosts) {
        if (nonProxyHosts == null || host == null) {
            return false;
        }
        StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|\"");
        while (tokenizer.hasMoreTokens()) {
            String pattern = tokenizer.nextToken();
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("match00", new String[]{"HTTPSender", host, pattern}));
            }
            if (!DefaultSocketFactory.match(pattern, host, false)) continue;
            return true;
        }
        return false;
    }

    protected static boolean match(String pattern, String str, boolean isCaseSensitive) {
        char ch;
        int i;
        char[] patArr = pattern.toCharArray();
        char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        boolean containsStar = false;
        for (i = 0; i < patArr.length; ++i) {
            if (patArr[i] != '*') continue;
            containsStar = true;
            break;
        }
        if (!containsStar) {
            if (patIdxEnd != strIdxEnd) {
                return false;
            }
            for (i = 0; i <= patIdxEnd; ++i) {
                char ch2 = patArr[i];
                if (isCaseSensitive && ch2 != strArr[i]) {
                    return false;
                }
                if (isCaseSensitive || Character.toUpperCase(ch2) == Character.toUpperCase(strArr[i])) continue;
                return false;
            }
            return true;
        }
        if (patIdxEnd == 0) {
            return true;
        }
        while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
            if (isCaseSensitive && ch != strArr[strIdxStart]) {
                return false;
            }
            if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart])) {
                return false;
            }
            ++patIdxStart;
            ++strIdxStart;
        }
        if (strIdxStart > strIdxEnd) {
            for (i = patIdxStart; i <= patIdxEnd; ++i) {
                if (patArr[i] == '*') continue;
                return false;
            }
            return true;
        }
        while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
            if (isCaseSensitive && ch != strArr[strIdxEnd]) {
                return false;
            }
            if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxEnd])) {
                return false;
            }
            --patIdxEnd;
            --strIdxEnd;
        }
        if (strIdxStart > strIdxEnd) {
            for (i = patIdxStart; i <= patIdxEnd; ++i) {
                if (patArr[i] == '*') continue;
                return false;
            }
            return true;
        }
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i2 = patIdxStart + 1; i2 <= patIdxEnd; ++i2) {
                if (patArr[i2] != '*') continue;
                patIdxTmp = i2;
                break;
            }
            if (patIdxTmp == patIdxStart + 1) {
                ++patIdxStart;
                continue;
            }
            int patLength = patIdxTmp - patIdxStart - 1;
            int strLength = strIdxEnd - strIdxStart + 1;
            int foundIdx = -1;
            block8: for (int i3 = 0; i3 <= strLength - patLength; ++i3) {
                for (int j = 0; j < patLength; ++j) {
                    ch = patArr[patIdxStart + j + 1];
                    if (isCaseSensitive && ch != strArr[strIdxStart + i3 + j] || !isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart + i3 + j])) continue block8;
                }
                foundIdx = strIdxStart + i3;
                break;
            }
            if (foundIdx == -1) {
                return false;
            }
            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }
        for (i = patIdxStart; i <= patIdxEnd; ++i) {
            if (patArr[i] == '*') continue;
            return false;
        }
        return true;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        try {
            inetClass = Class.forName("java.net.InetSocketAddress");
            plain = false;
            inetConstructor = inetClass.getConstructor(class$java$lang$String == null ? (class$java$lang$String = DefaultSocketFactory.class$("java.lang.String")) : class$java$lang$String, Integer.TYPE);
            socketConstructor = (class$java$net$Socket == null ? (class$java$net$Socket = DefaultSocketFactory.class$("java.net.Socket")) : class$java$net$Socket).getConstructor(new Class[0]);
            connect = (class$java$net$Socket == null ? (class$java$net$Socket = DefaultSocketFactory.class$("java.net.Socket")) : class$java$net$Socket).getMethod("connect", inetClass.getSuperclass(), Integer.TYPE);
        }
        catch (Exception e) {
            plain = true;
        }
    }
}

