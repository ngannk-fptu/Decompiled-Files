/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.collections.LRUMap;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.threadpool.ThreadPool;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.management.ServiceAdmin;
import org.apache.axis.server.AxisServer;
import org.apache.axis.session.Session;
import org.apache.axis.session.SimpleSession;
import org.apache.axis.transport.http.SimpleAxisWorker;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.NetworkUtils;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;

public class SimpleAxisServer
implements Runnable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$http$SimpleAxisServer == null ? (class$org$apache$axis$transport$http$SimpleAxisServer = SimpleAxisServer.class$("org.apache.axis.transport.http.SimpleAxisServer")) : class$org$apache$axis$transport$http$SimpleAxisServer).getName());
    private Map sessions;
    private int maxSessions;
    public static final int MAX_SESSIONS_DEFAULT = 100;
    private static ThreadPool pool;
    private static boolean doThreads;
    private static boolean doSessions;
    public static int sessionIndex;
    private static AxisServer myAxisServer;
    private EngineConfiguration myConfig = null;
    private boolean stopped = false;
    private ServerSocket serverSocket;
    static /* synthetic */ Class class$org$apache$axis$transport$http$SimpleAxisServer;

    public static ThreadPool getPool() {
        return pool;
    }

    public SimpleAxisServer() {
        this(100);
    }

    public SimpleAxisServer(int maxPoolSize) {
        this(maxPoolSize, 100);
    }

    public SimpleAxisServer(int maxPoolSize, int maxSessions) {
        this.maxSessions = maxSessions;
        this.sessions = new LRUMap(maxSessions);
        pool = new ThreadPool(maxPoolSize);
    }

    protected void finalize() throws Throwable {
        this.stop();
        super.finalize();
    }

    public int getMaxSessions() {
        return this.maxSessions;
    }

    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
        ((LRUMap)this.sessions).setMaximumSize(maxSessions);
    }

    protected boolean isSessionUsed() {
        return doSessions;
    }

    public void setDoThreads(boolean value) {
        doThreads = value;
    }

    public boolean getDoThreads() {
        return doThreads;
    }

    public EngineConfiguration getMyConfig() {
        return this.myConfig;
    }

    public void setMyConfig(EngineConfiguration myConfig) {
        this.myConfig = myConfig;
    }

    protected Session createSession(String cooky) {
        Session session = null;
        if (this.sessions.containsKey(cooky)) {
            session = (Session)this.sessions.get(cooky);
        } else {
            session = new SimpleSession();
            this.sessions.put(cooky, session);
        }
        return session;
    }

    public synchronized AxisServer getAxisServer() {
        if (myAxisServer == null) {
            if (this.myConfig == null) {
                this.myConfig = EngineConfigurationFactoryFinder.newFactory().getServerEngineConfig();
            }
            myAxisServer = new AxisServer(this.myConfig);
            ServiceAdmin.setEngine(myAxisServer, NetworkUtils.getLocalHostname() + "@" + this.serverSocket.getLocalPort());
        }
        return myAxisServer;
    }

    public void run() {
        log.info((Object)Messages.getMessage("start01", "SimpleAxisServer", new Integer(this.getServerSocket().getLocalPort()).toString(), this.getCurrentDirectory()));
        while (!this.stopped) {
            Socket socket = null;
            try {
                socket = this.serverSocket.accept();
            }
            catch (InterruptedIOException iie) {
            }
            catch (Exception e) {
                log.debug((Object)Messages.getMessage("exception00"), (Throwable)e);
                break;
            }
            if (socket == null) continue;
            SimpleAxisWorker worker = new SimpleAxisWorker(this, socket);
            if (doThreads) {
                pool.addWorker(worker);
                continue;
            }
            worker.run();
        }
        log.info((Object)Messages.getMessage("quit00", "SimpleAxisServer"));
    }

    private String getCurrentDirectory() {
        return System.getProperty("user.dir");
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start(boolean daemon) throws Exception {
        this.stopped = false;
        if (doThreads) {
            Thread thread = new Thread(this);
            thread.setDaemon(daemon);
            thread.start();
        } else {
            this.run();
        }
    }

    public void start() throws Exception {
        this.start(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        if (this.stopped) {
            return;
        }
        this.stopped = true;
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        }
        catch (IOException e) {
            log.info((Object)Messages.getMessage("exception00"), (Throwable)e);
        }
        finally {
            this.serverSocket = null;
        }
        log.info((Object)Messages.getMessage("quit00", "SimpleAxisServer"));
        pool.shutdown();
    }

    public static void main(String[] args) {
        String maxSessions;
        Options opts = null;
        try {
            opts = new Options(args);
        }
        catch (MalformedURLException e) {
            log.error((Object)Messages.getMessage("malformedURLException00"), (Throwable)e);
            return;
        }
        String maxPoolSize = opts.isValueSet('t');
        if (maxPoolSize == null) {
            maxPoolSize = "100";
        }
        if ((maxSessions = opts.isValueSet('m')) == null) {
            maxSessions = "100";
        }
        SimpleAxisServer sas = new SimpleAxisServer(Integer.parseInt(maxPoolSize), Integer.parseInt(maxSessions));
        try {
            doThreads = opts.isFlagSet('t') > 0;
            int port = opts.getPort();
            ServerSocket ss = null;
            int retries = 5;
            for (int i = 0; i < 5; ++i) {
                try {
                    ss = new ServerSocket(port);
                    break;
                }
                catch (BindException be) {
                    log.debug((Object)Messages.getMessage("exception00"), (Throwable)be);
                    if (i >= 4) {
                        throw new Exception(Messages.getMessage("unableToStartServer00", Integer.toString(port)));
                    }
                    Thread.sleep(3000L);
                    continue;
                }
            }
            sas.setServerSocket(ss);
            sas.start();
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            return;
        }
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
        doThreads = true;
        doSessions = true;
        sessionIndex = 0;
        myAxisServer = null;
    }
}

