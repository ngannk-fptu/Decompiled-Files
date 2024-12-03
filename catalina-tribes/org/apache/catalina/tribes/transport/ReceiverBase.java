/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.transport;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.management.ObjectName;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.ChannelReceiver;
import org.apache.catalina.tribes.MessageListener;
import org.apache.catalina.tribes.io.ListenCallback;
import org.apache.catalina.tribes.jmx.JmxRegistry;
import org.apache.catalina.tribes.transport.RxTaskPool;
import org.apache.catalina.tribes.util.ExecutorFactory;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public abstract class ReceiverBase
implements ChannelReceiver,
ListenCallback,
RxTaskPool.TaskCreator {
    public static final int OPTION_DIRECT_BUFFER = 4;
    private static final Log log = LogFactory.getLog(ReceiverBase.class);
    private static final Object bindLock = new Object();
    protected static final StringManager sm = StringManager.getManager("org.apache.catalina.tribes.transport");
    private MessageListener listener;
    private String host = "auto";
    private InetAddress bind;
    private int port = 4000;
    private int udpPort = -1;
    private int securePort = -1;
    private int rxBufSize = 65536;
    private int txBufSize = 25188;
    private int udpRxBufSize = 65536;
    private int udpTxBufSize = 25188;
    private volatile boolean listen = false;
    private RxTaskPool pool;
    private boolean direct = true;
    private long tcpSelectorTimeout = 5000L;
    private int autoBind = 100;
    private int maxThreads = 15;
    private int minThreads = 6;
    private int maxTasks = 100;
    private int minTasks = 10;
    private boolean tcpNoDelay = true;
    private boolean soKeepAlive = false;
    private boolean ooBInline = true;
    private boolean soReuseAddress = true;
    private boolean soLingerOn = true;
    private int soLingerTime = 3;
    private int soTrafficClass = 28;
    private int timeout = 3000;
    private boolean useBufferPool = true;
    private boolean daemon = true;
    private long maxIdleTime = 60000L;
    private ExecutorService executor;
    private Channel channel;
    private ObjectName oname = null;

    @Override
    public void start() throws IOException {
        JmxRegistry jmxRegistry;
        if (this.executor == null) {
            String channelName = "";
            if (this.channel.getName() != null) {
                channelName = "[" + this.channel.getName() + "]";
            }
            TaskThreadFactory tf = new TaskThreadFactory("Tribes-Task-Receiver" + channelName + "-");
            this.executor = ExecutorFactory.newThreadPool(this.minThreads, this.maxThreads, this.maxIdleTime, TimeUnit.MILLISECONDS, tf);
        }
        if ((jmxRegistry = JmxRegistry.getRegistry(this.channel)) != null) {
            this.oname = jmxRegistry.registerJmx(",component=Receiver", this);
        }
    }

    @Override
    public void stop() {
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
        this.executor = null;
        if (this.oname != null) {
            JmxRegistry jmxRegistry = JmxRegistry.getRegistry(this.channel);
            if (jmxRegistry != null) {
                jmxRegistry.unregisterJmx(this.oname);
            }
            this.oname = null;
        }
        this.channel = null;
    }

    @Override
    public MessageListener getMessageListener() {
        return this.listener;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    public int getRxBufSize() {
        return this.rxBufSize;
    }

    public int getTxBufSize() {
        return this.txBufSize;
    }

    @Override
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    public void setRxBufSize(int rxBufSize) {
        this.rxBufSize = rxBufSize;
    }

    public void setTxBufSize(int txBufSize) {
        this.txBufSize = txBufSize;
    }

    public InetAddress getBind() {
        if (this.bind == null) {
            try {
                if ("auto".equals(this.host)) {
                    this.host = InetAddress.getLocalHost().getHostAddress();
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Starting replication listener on address:" + this.host));
                }
                this.bind = InetAddress.getByName(this.host);
            }
            catch (IOException ioe) {
                log.error((Object)sm.getString("receiverBase.bind.failed", this.host), (Throwable)ioe);
            }
        }
        return this.bind;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void bind(ServerSocket socket, int portstart, int retries) throws IOException {
        Object object = bindLock;
        synchronized (object) {
            InetSocketAddress addr = null;
            int port = portstart;
            while (retries > 0) {
                try {
                    addr = new InetSocketAddress(this.getBind(), port);
                    socket.bind(addr);
                    this.setPort(port);
                    log.info((Object)sm.getString("receiverBase.socket.bind", addr));
                    retries = 0;
                }
                catch (IOException x) {
                    if (--retries <= 0) {
                        log.info((Object)sm.getString("receiverBase.unable.bind", addr));
                        throw x;
                    }
                    ++port;
                }
            }
        }
    }

    protected int bindUdp(DatagramSocket socket, int portstart, int retries) throws IOException {
        InetSocketAddress addr = null;
        while (retries > 0) {
            try {
                addr = new InetSocketAddress(this.getBind(), portstart);
                socket.bind(addr);
                this.setUdpPort(portstart);
                log.info((Object)sm.getString("receiverBase.udp.bind", addr));
                return 0;
            }
            catch (IOException x) {
                if (--retries <= 0) {
                    log.info((Object)sm.getString("receiverBase.unable.bind.udp", addr));
                    throw x;
                }
                ++portstart;
                try {
                    Thread.sleep(25L);
                }
                catch (InterruptedException ti) {
                    Thread.currentThread().interrupt();
                }
                retries = this.bindUdp(socket, portstart, retries);
            }
        }
        return retries;
    }

    @Override
    public void messageDataReceived(ChannelMessage data) {
        if (this.listener != null && this.listener.accept(data)) {
            this.listener.messageReceived(data);
        }
    }

    public int getWorkerThreadOptions() {
        int options = 0;
        if (this.getDirect()) {
            options |= 4;
        }
        return options;
    }

    public void setBind(InetAddress bind) {
        this.bind = bind;
    }

    public boolean getDirect() {
        return this.direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    public String getAddress() {
        this.getBind();
        return this.host;
    }

    @Override
    public String getHost() {
        return this.getAddress();
    }

    public long getSelectorTimeout() {
        return this.tcpSelectorTimeout;
    }

    public boolean doListen() {
        return this.listen;
    }

    public MessageListener getListener() {
        return this.listener;
    }

    public RxTaskPool getTaskPool() {
        return this.pool;
    }

    public int getAutoBind() {
        return this.autoBind;
    }

    public int getMaxThreads() {
        return this.maxThreads;
    }

    public int getMinThreads() {
        return this.minThreads;
    }

    public boolean getTcpNoDelay() {
        return this.tcpNoDelay;
    }

    public boolean getSoKeepAlive() {
        return this.soKeepAlive;
    }

    public boolean getOoBInline() {
        return this.ooBInline;
    }

    public boolean getSoLingerOn() {
        return this.soLingerOn;
    }

    public int getSoLingerTime() {
        return this.soLingerTime;
    }

    public boolean getSoReuseAddress() {
        return this.soReuseAddress;
    }

    public int getSoTrafficClass() {
        return this.soTrafficClass;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public boolean getUseBufferPool() {
        return this.useBufferPool;
    }

    @Override
    public int getSecurePort() {
        return this.securePort;
    }

    public int getMinTasks() {
        return this.minTasks;
    }

    public int getMaxTasks() {
        return this.maxTasks;
    }

    public ExecutorService getExecutor() {
        return this.executor;
    }

    public boolean isListening() {
        return this.listen;
    }

    public void setSelectorTimeout(long selTimeout) {
        this.tcpSelectorTimeout = selTimeout;
    }

    public void setListen(boolean doListen) {
        this.listen = doListen;
    }

    public void setAddress(String host) {
        this.host = host;
    }

    public void setHost(String host) {
        this.setAddress(host);
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    public void setPool(RxTaskPool pool) {
        this.pool = pool;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAutoBind(int autoBind) {
        this.autoBind = autoBind;
        if (this.autoBind <= 0) {
            this.autoBind = 1;
        }
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setMinThreads(int minThreads) {
        this.minThreads = minThreads;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public void setSoKeepAlive(boolean soKeepAlive) {
        this.soKeepAlive = soKeepAlive;
    }

    public void setOoBInline(boolean ooBInline) {
        this.ooBInline = ooBInline;
    }

    public void setSoLingerOn(boolean soLingerOn) {
        this.soLingerOn = soLingerOn;
    }

    public void setSoLingerTime(int soLingerTime) {
        this.soLingerTime = soLingerTime;
    }

    public void setSoReuseAddress(boolean soReuseAddress) {
        this.soReuseAddress = soReuseAddress;
    }

    public void setSoTrafficClass(int soTrafficClass) {
        this.soTrafficClass = soTrafficClass;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setUseBufferPool(boolean useBufferPool) {
        this.useBufferPool = useBufferPool;
    }

    public void setSecurePort(int securePort) {
        this.securePort = securePort;
    }

    public void setMinTasks(int minTasks) {
        this.minTasks = minTasks;
    }

    public void setMaxTasks(int maxTasks) {
        this.maxTasks = maxTasks;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void heartbeat() {
    }

    @Override
    public int getUdpPort() {
        return this.udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public int getUdpRxBufSize() {
        return this.udpRxBufSize;
    }

    public void setUdpRxBufSize(int udpRxBufSize) {
        this.udpRxBufSize = udpRxBufSize;
    }

    public int getUdpTxBufSize() {
        return this.udpTxBufSize;
    }

    public void setUdpTxBufSize(int udpTxBufSize) {
        this.udpTxBufSize = udpTxBufSize;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getPoolSize() {
        if (this.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor)this.executor).getPoolSize();
        }
        return -1;
    }

    public int getActiveCount() {
        if (this.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor)this.executor).getActiveCount();
        }
        return -1;
    }

    public long getTaskCount() {
        if (this.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor)this.executor).getTaskCount();
        }
        return -1L;
    }

    public long getCompletedTaskCount() {
        if (this.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor)this.executor).getCompletedTaskCount();
        }
        return -1L;
    }

    public boolean isDaemon() {
        return this.daemon;
    }

    public long getMaxIdleTime() {
        return this.maxIdleTime;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    class TaskThreadFactory
    implements ThreadFactory {
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        TaskThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement());
            t.setDaemon(ReceiverBase.this.daemon);
            t.setPriority(5);
            return t;
        }
    }
}

