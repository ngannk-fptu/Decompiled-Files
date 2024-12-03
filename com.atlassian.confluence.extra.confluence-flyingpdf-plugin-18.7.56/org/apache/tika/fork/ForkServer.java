/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import org.apache.tika.exception.TikaException;
import org.apache.tika.fork.ForkObjectInputStream;
import org.apache.tika.fork.ForkProxy;
import org.apache.tika.fork.MemoryURLStreamHandlerFactory;
import org.apache.tika.fork.ParserFactoryFactory;
import org.apache.tika.parser.ParserFactory;
import org.xml.sax.SAXException;

class ForkServer
implements Runnable {
    public static final byte ERROR = -1;
    public static final byte DONE = 0;
    public static final byte CALL = 1;
    public static final byte PING = 2;
    public static final byte RESOURCE = 3;
    public static final byte READY = 4;
    public static final byte FAILED_TO_START = 5;
    public static final byte INIT_PARSER_FACTORY_FACTORY = 6;
    public static final byte INIT_LOADER_PARSER = 7;
    public static final byte INIT_PARSER_FACTORY_FACTORY_LOADER = 8;
    private long serverPulseMillis = 5000L;
    private long serverParserTimeoutMillis = 60000L;
    private long serverWaitTimeoutMillis = 60000L;
    private Object[] lock = new Object[0];
    private final DataInputStream input;
    private final DataOutputStream output;
    private volatile boolean active = true;
    private Object parser;
    private ClassLoader classLoader;
    private boolean parsing = false;
    private long since;

    public static void main(String[] args) throws Exception {
        long serverPulseMillis = Long.parseLong(args[0]);
        long serverParseTimeoutMillis = Long.parseLong(args[1]);
        long serverWaitTimeoutMillis = Long.parseLong(args[2]);
        URL.setURLStreamHandlerFactory(new MemoryURLStreamHandlerFactory());
        ForkServer server = new ForkServer(System.in, System.out, serverPulseMillis, serverParseTimeoutMillis, serverWaitTimeoutMillis);
        System.setIn(new ByteArrayInputStream(new byte[0]));
        System.setOut(System.err);
        Thread watchdog = new Thread((Runnable)server, "Tika Watchdog");
        watchdog.setDaemon(true);
        watchdog.start();
        server.processRequests();
    }

    public ForkServer(InputStream input, OutputStream output, long serverPulseMillis, long serverParserTimeoutMillis, long serverWaitTimeoutMillis) throws IOException {
        this.input = new DataInputStream(input);
        this.output = new DataOutputStream(output);
        this.serverPulseMillis = serverPulseMillis;
        this.serverParserTimeoutMillis = serverParserTimeoutMillis;
        this.serverWaitTimeoutMillis = serverWaitTimeoutMillis;
        this.parsing = false;
        this.since = System.currentTimeMillis();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    @Override
    public void run() {
        try {
            while (true) {
                Object[] objectArray = this.lock;
                // MONITORENTER : this.lock
                long elapsed = System.currentTimeMillis() - this.since;
                if (this.parsing && elapsed > this.serverParserTimeoutMillis) {
                    // MONITOREXIT : objectArray
                    break;
                }
                if (!this.parsing && this.serverWaitTimeoutMillis > 0L && elapsed > this.serverWaitTimeoutMillis) {
                    // MONITOREXIT : objectArray
                    break;
                }
                // MONITOREXIT : objectArray
                Thread.sleep(this.serverPulseMillis);
            }
            System.exit(0);
            return;
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    public void processRequests() {
        try {
            this.initializeParserAndLoader();
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.err.flush();
            try {
                this.output.writeByte(5);
                this.output.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
                System.err.flush();
            }
            return;
        }
        try {
            int request;
            while ((request = this.input.read()) != -1) {
                if (request == 2) {
                    this.output.writeByte(2);
                } else if (request == 1) {
                    this.call(this.classLoader, this.parser);
                } else {
                    throw new IllegalStateException("Unexpected request");
                }
                this.output.flush();
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        System.err.flush();
    }

    private void initializeParserAndLoader() throws IOException, ClassNotFoundException, TikaException, SAXException {
        this.output.writeByte(4);
        this.output.flush();
        int configIndex = this.input.read();
        if (configIndex == -1) {
            throw new TikaException("eof! pipe closed?!");
        }
        Object firstObject = this.readObject(ForkServer.class.getClassLoader());
        switch (configIndex) {
            case 6: {
                if (firstObject instanceof ParserFactoryFactory) {
                    this.classLoader = ForkServer.class.getClassLoader();
                    ParserFactory parserFactory = ((ParserFactoryFactory)firstObject).build();
                    this.parser = parserFactory.build();
                    break;
                }
                throw new IllegalArgumentException("Expecting only one object of class ParserFactoryFactory");
            }
            case 7: {
                if (firstObject instanceof ClassLoader) {
                    this.classLoader = (ClassLoader)firstObject;
                    Thread.currentThread().setContextClassLoader(this.classLoader);
                    this.parser = this.readObject(this.classLoader);
                    break;
                }
                throw new IllegalArgumentException("Expecting ClassLoader followed by a Parser");
            }
            case 8: {
                if (firstObject instanceof ParserFactoryFactory) {
                    ParserFactory parserFactory = ((ParserFactoryFactory)firstObject).build();
                    this.parser = parserFactory.build();
                    this.classLoader = (ClassLoader)this.readObject(ForkServer.class.getClassLoader());
                    Thread.currentThread().setContextClassLoader(this.classLoader);
                    break;
                }
                throw new IllegalStateException("Expecing ParserFactoryFactory followed by a class loader");
            }
        }
        this.output.writeByte(4);
        this.output.flush();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void call(ClassLoader loader, Object object) throws Exception {
        Object[] objectArray = this.lock;
        synchronized (this.lock) {
            this.parsing = true;
            this.since = System.currentTimeMillis();
            // ** MonitorExit[var3_3] (shouldn't be in output)
            try {
                Method method = this.getMethod(object, this.input.readUTF());
                Object[] args = new Object[method.getParameterTypes().length];
                for (int i = 0; i < args.length; ++i) {
                    args[i] = this.readObject(loader);
                }
                try {
                    method.invoke(object, args);
                    this.output.write(0);
                }
                catch (InvocationTargetException e) {
                    this.output.write(-1);
                    Throwable toSend = e.getCause();
                    try {
                        ForkObjectInputStream.sendObject(toSend, this.output);
                    }
                    catch (NotSerializableException nse) {
                        TikaException te = new TikaException(toSend.getMessage());
                        te.setStackTrace(toSend.getStackTrace());
                        ForkObjectInputStream.sendObject(te, this.output);
                    }
                }
            }
            finally {
                objectArray = this.lock;
                synchronized (this.lock) {
                    this.parsing = false;
                    this.since = System.currentTimeMillis();
                    // ** MonitorExit[var3_3] (shouldn't be in output)
                }
            }
            return;
        }
    }

    private Method getMethod(Object object, String name) {
        for (Class<?> klass = object.getClass(); klass != null; klass = klass.getSuperclass()) {
            for (Class<?> iface : klass.getInterfaces()) {
                for (Method method : iface.getMethods()) {
                    if (!name.equals(method.getName())) continue;
                    return method;
                }
            }
        }
        return null;
    }

    private Object readObject(ClassLoader loader) throws IOException, ClassNotFoundException {
        Object object = ForkObjectInputStream.readObject(this.input, loader);
        if (object instanceof ForkProxy) {
            ((ForkProxy)object).init(this.input, this.output);
        }
        this.output.writeByte(0);
        this.output.flush();
        return object;
    }
}

