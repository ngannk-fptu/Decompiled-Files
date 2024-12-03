/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.BytesMessage
 *  javax.jms.Message
 *  javax.jms.MessageListener
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.jms;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.components.jms.JMSVendorAdapterFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.jms.JMSConnector;
import org.apache.axis.transport.jms.JMSConnectorFactory;
import org.apache.axis.transport.jms.JMSEndpoint;
import org.apache.axis.transport.jms.SimpleJMSWorker;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;

public class SimpleJMSListener
implements MessageListener {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$jms$SimpleJMSListener == null ? (class$org$apache$axis$transport$jms$SimpleJMSListener = SimpleJMSListener.class$("org.apache.axis.transport.jms.SimpleJMSListener")) : class$org$apache$axis$transport$jms$SimpleJMSListener).getName());
    private static boolean doThreads;
    private JMSConnector connector;
    private JMSEndpoint endpoint;
    private AxisServer server;
    private HashMap connectorProps;
    private static AxisServer myAxisServer;
    static /* synthetic */ Class class$org$apache$axis$transport$jms$SimpleJMSListener;

    public SimpleJMSListener(HashMap connectorMap, HashMap cfMap, String destination, String username, String password, boolean doThreads) throws Exception {
        SimpleJMSListener.doThreads = doThreads;
        try {
            JMSVendorAdapter adapter = JMSVendorAdapterFactory.getJMSVendorAdapter();
            this.connector = JMSConnectorFactory.createServerConnector(connectorMap, cfMap, username, password, adapter);
            this.connectorProps = connectorMap;
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            throw e;
        }
        this.endpoint = this.connector.createEndpoint(destination);
    }

    protected static AxisServer getAxisServer() {
        return myAxisServer;
    }

    protected JMSConnector getConnector() {
        return this.connector;
    }

    public void onMessage(Message message) {
        try {
            SimpleJMSWorker worker = new SimpleJMSWorker(this, (BytesMessage)message);
            if (doThreads) {
                Thread t = new Thread(worker);
                t.start();
            } else {
                worker.run();
            }
        }
        catch (ClassCastException cce) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)cce);
            cce.printStackTrace();
            return;
        }
    }

    public void start() throws Exception {
        this.endpoint.registerListener(this, this.connectorProps);
        this.connector.start();
    }

    public void shutdown() throws Exception {
        this.endpoint.unregisterListener(this);
        this.connector.stop();
        this.connector.shutdown();
    }

    public static final HashMap createConnectorMap(Options options) {
        HashMap<String, String> connectorMap = new HashMap<String, String>();
        if (options.isFlagSet('t') > 0) {
            connectorMap.put("transport.jms.domain", "TOPIC");
        }
        return connectorMap;
    }

    public static final HashMap createCFMap(Options options) throws IOException {
        String cfFile = options.isValueSet('c');
        if (cfFile == null) {
            return null;
        }
        Properties cfProps = new Properties();
        cfProps.load(new BufferedInputStream(new FileInputStream(cfFile)));
        HashMap<Object, Object> cfMap = new HashMap<Object, Object>(cfProps);
        return cfMap;
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options(args);
        if (options.isFlagSet('?') > 0 || options.isFlagSet('h') > 0) {
            SimpleJMSListener.printUsage();
        }
        SimpleJMSListener listener = new SimpleJMSListener(SimpleJMSListener.createConnectorMap(options), SimpleJMSListener.createCFMap(options), options.isValueSet('d'), options.getUser(), options.getPassword(), options.isFlagSet('s') > 0);
        listener.start();
    }

    public static void printUsage() {
        System.out.println("Usage: SimpleJMSListener [options]");
        System.out.println(" Opts: -? this message");
        System.out.println();
        System.out.println("       -c connection factory properties filename");
        System.out.println("       -d destination");
        System.out.println("       -t topic [absence of -t indicates queue]");
        System.out.println();
        System.out.println("       -u username");
        System.out.println("       -w password");
        System.out.println();
        System.out.println("       -s single-threaded listener");
        System.out.println("          [absence of option => multithreaded]");
        System.exit(1);
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
        myAxisServer = new AxisServer();
    }
}

