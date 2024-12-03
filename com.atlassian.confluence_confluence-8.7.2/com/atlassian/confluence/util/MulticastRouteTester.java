/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.IteratorUtils
 *  org.apache.log4j.Category
 *  org.apache.log4j.Priority
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.ClusterUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;

public class MulticastRouteTester
implements Runnable {
    private static final Category logger = Category.getInstance(MulticastRouteTester.class);
    private static final int TEST_TIMEOUT = 5000;
    private static final byte[] TEST_MESSAGE = "CONFLUENCE_MC_TEST".getBytes();
    private static final Map PROBLEM_MESSAGE_MAP;
    private TestReporter reporter = new Log4JReporter(logger, Priority.WARN);
    private InetAddress multicastGroup;
    private NetworkInterface iface;
    private int port = 33333;
    private boolean successful = false;

    public MulticastRouteTester(InetAddress multicastGroup, NetworkInterface iface, int port) {
        if (!multicastGroup.isMulticastAddress()) {
            throw new IllegalArgumentException("Address is not a multicast group");
        }
        this.multicastGroup = multicastGroup;
        this.iface = iface;
        this.port = port;
    }

    public MulticastRouteTester(InetAddress multicastGroup, NetworkInterface iface, int port, TestReporter testReporter) {
        this(multicastGroup, iface, port);
        this.reporter = testReporter;
    }

    @Override
    public void run() {
        try {
            this.successful = this.test(this.multicastGroup, this.iface, this.port);
        }
        catch (IOException ex) {
            logger.error((Object)"Exception while performing multicast route test", (Throwable)ex);
        }
    }

    public boolean test(InetAddress multicastGroup, NetworkInterface iface, int port) throws IOException {
        if (iface == null && (iface = this.getDefaultInterface()) == null) {
            this.reporter.report("no.network.interfaces.found");
            return false;
        }
        if (!ClusterUtils.CLUSTERABLE_INTERFACE_PREDICATE.apply((Object)iface)) {
            this.reporter.report("interface.not.suitable.for.multicast");
            return false;
        }
        return this.runTest(multicastGroup, iface, port);
    }

    private NetworkInterface getDefaultInterface() throws SocketException {
        List interfaces = IteratorUtils.toList(ClusterUtils.getClusterableInterfaces());
        if (interfaces.isEmpty()) {
            return null;
        }
        return (NetworkInterface)interfaces.get(0);
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean runTest(InetAddress multicastGroup, NetworkInterface iface, int port) throws IOException {
        InetSocketAddress testSocketAddr = new InetSocketAddress(multicastGroup, port);
        MulticastSocket testSocket = null;
        try {
            testSocket = new MulticastSocket(port);
            testSocket.joinGroup(testSocketAddr, iface);
            testSocket.setTimeToLive(0);
            DatagramPacket testPacket = new DatagramPacket(TEST_MESSAGE, TEST_MESSAGE.length, testSocketAddr);
            byte[] incomingBytes = new byte[TEST_MESSAGE.length];
            DatagramPacket incomingPacket = new DatagramPacket(incomingBytes, incomingBytes.length);
            testSocket.send(testPacket);
            try {
                testSocket.setSoTimeout(5000);
                testSocket.receive(incomingPacket);
            }
            catch (SocketTimeoutException e) {
                this.reporter.report("multicast.message.receive.timed.out");
                boolean bl = false;
                if (testSocket != null) {
                    if (testSocket.isBound()) {
                        testSocket.leaveGroup(testSocketAddr, iface);
                    }
                    if (!testSocket.isClosed()) {
                        testSocket.close();
                    }
                }
                return bl;
            }
            if (!Arrays.equals(TEST_MESSAGE, incomingBytes)) {
                this.reporter.report("received.unexpected.multicast.message");
                boolean bl = false;
                return bl;
            }
            boolean bl = true;
            return bl;
        }
        finally {
            if (testSocket != null) {
                if (testSocket.isBound()) {
                    testSocket.leaveGroup(testSocketAddr, iface);
                }
                if (!testSocket.isClosed()) {
                    testSocket.close();
                }
            }
        }
    }

    static {
        HashMap<String, String> messageMap = new HashMap<String, String>();
        messageMap.put("no.network.interfaces.found", "No network interfaces were found on this host");
        messageMap.put("interface.not.suitable.for.multicast", "The interface is not suitable for multicast communication");
        messageMap.put("multicast.message.receive.timed.out", "No response from network stack within the configured timeout of 5000ms");
        messageMap.put("received.unexpected.multicast.message", "An unexpected multicast message was received");
        PROBLEM_MESSAGE_MAP = Collections.unmodifiableMap(messageMap);
    }

    public static class Log4JReporter
    implements TestReporter {
        private Category logger;
        private Priority priority;
        private boolean started = false;

        public Log4JReporter(Category logger, Priority priority) {
            this.logger = logger;
            this.priority = priority;
        }

        @Override
        public void report(String problemKey) {
            this.logPreamble();
            this.logger.log(this.priority, PROBLEM_MESSAGE_MAP.get(problemKey));
        }

        private void logPreamble() {
            if (!this.started) {
                this.logger.log(this.priority, (Object)"Problems have been detected with multicast routing on this host.");
                this.started = true;
            }
        }
    }

    public static interface TestReporter {
        public void report(String var1);
    }
}

