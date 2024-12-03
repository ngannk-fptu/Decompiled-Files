/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.media.jai.OperationRegistry;
import javax.media.jai.remote.SerializableRenderedImage;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoderFactory;
import javax.media.jai.tilecodec.TileEncoderFactory;
import javax.media.jai.util.CaselessStringKey;

public final class SerializableRenderableImage
implements RenderableImage,
Serializable {
    private static final int SERVER_TIMEOUT = 60000;
    private static final String CLOSE_MESSAGE = "CLOSE";
    private transient boolean isServer;
    private transient RenderableImage source;
    private float minX;
    private float minY;
    private float width;
    private float height;
    private transient Vector sources = null;
    private transient Hashtable properties = null;
    private boolean isDynamic;
    private InetAddress host;
    private int port;
    private transient boolean serverOpen = false;
    private transient ServerSocket serverSocket = null;
    private transient Thread serverThread;
    private static transient Hashtable remoteReferenceCount;
    private boolean useTileCodec = false;
    private OperationRegistry registry = null;
    private String formatName = null;
    private TileCodecParameterList encodingParam = null;
    private TileCodecParameterList decodingParam = null;

    private static synchronized void incrementRemoteReferenceCount(Object o) {
        if (remoteReferenceCount == null) {
            remoteReferenceCount = new Hashtable();
            remoteReferenceCount.put(o, new Integer(1));
        } else {
            Integer count = (Integer)remoteReferenceCount.get(o);
            if (count == null) {
                remoteReferenceCount.put(o, new Integer(1));
            } else {
                remoteReferenceCount.put(o, new Integer(count + 1));
            }
        }
    }

    private static synchronized void decrementRemoteReferenceCount(Object o) {
        Integer count;
        if (remoteReferenceCount != null && (count = (Integer)remoteReferenceCount.get(o)) != null) {
            if (count == 1) {
                remoteReferenceCount.remove(o);
            } else {
                remoteReferenceCount.put(o, new Integer(count - 1));
            }
        }
    }

    SerializableRenderableImage() {
    }

    public SerializableRenderableImage(RenderableImage source, OperationRegistry registry, String formatName, TileCodecParameterList encodingParam, TileCodecParameterList decodingParam) {
        this(source);
        this.registry = registry;
        this.formatName = formatName;
        this.encodingParam = encodingParam;
        this.decodingParam = decodingParam;
        if (formatName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderableImage2"));
        }
        if (!formatName.equals(encodingParam.getFormatName())) {
            throw new IllegalArgumentException(JaiI18N.getString("UseTileCodec0"));
        }
        if (!formatName.equals(decodingParam.getFormatName())) {
            throw new IllegalArgumentException(JaiI18N.getString("UseTileCodec1"));
        }
        TileEncoderFactory tileEncoderFactory = (TileEncoderFactory)registry.getFactory("tileEncoder", formatName);
        TileDecoderFactory tileDecoderFactory = (TileDecoderFactory)registry.getFactory("tileDecoder", formatName);
        if (tileEncoderFactory == null || tileDecoderFactory == null) {
            throw new RuntimeException(JaiI18N.getString("UseTileCodec2"));
        }
        this.useTileCodec = true;
    }

    public SerializableRenderableImage(RenderableImage source) {
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderableImage1"));
        }
        this.isServer = true;
        this.source = source;
        this.minX = source.getMinX();
        this.minY = source.getMinY();
        this.width = source.getWidth();
        this.height = source.getHeight();
        this.isDynamic = source.isDynamic();
        this.sources = new Vector();
        this.sources.add(source);
        this.properties = new Hashtable();
        String[] propertyNames = source.getPropertyNames();
        if (propertyNames != null) {
            for (int i = 0; i < propertyNames.length; ++i) {
                String propertyName = propertyNames[i];
                this.properties.put(new CaselessStringKey(propertyName), source.getProperty(propertyName));
            }
        }
        try {
            this.host = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }
        this.serverOpen = false;
    }

    public RenderedImage createDefaultRendering() {
        if (this.isServer) {
            return this.source.createDefaultRendering();
        }
        Socket socket = this.connectToServer();
        OutputStream out = null;
        ObjectOutputStream objectOut = null;
        InputStream in = null;
        ObjectInputStream objectIn = null;
        try {
            out = socket.getOutputStream();
            objectOut = new ObjectOutputStream(out);
            in = socket.getInputStream();
            objectIn = new ObjectInputStream(in);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            objectOut.writeObject("createDefaultRendering");
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        Object object = null;
        try {
            object = objectIn.readObject();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        RenderedImage ri = object instanceof SerializableRenderedImage ? (RenderedImage)object : null;
        try {
            out.close();
            objectOut.close();
            in.close();
            objectIn.close();
            socket.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ri;
    }

    public RenderedImage createRendering(RenderContext renderContext) {
        if (this.isServer) {
            return this.source.createRendering(renderContext);
        }
        Socket socket = this.connectToServer();
        OutputStream out = null;
        ObjectOutputStream objectOut = null;
        InputStream in = null;
        ObjectInputStream objectIn = null;
        try {
            out = socket.getOutputStream();
            objectOut = new ObjectOutputStream(out);
            in = socket.getInputStream();
            objectIn = new ObjectInputStream(in);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            objectOut.writeObject("createRendering");
            objectOut.writeObject(SerializerFactory.getState(renderContext, null));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        Object object = null;
        try {
            object = objectIn.readObject();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        RenderedImage ri = (RenderedImage)object;
        try {
            out.close();
            objectOut.close();
            in.close();
            objectIn.close();
            socket.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ri;
    }

    public RenderedImage createScaledRendering(int w, int h, RenderingHints hints) {
        if (this.isServer) {
            return this.source.createScaledRendering(w, h, hints);
        }
        Socket socket = this.connectToServer();
        OutputStream out = null;
        ObjectOutputStream objectOut = null;
        InputStream in = null;
        ObjectInputStream objectIn = null;
        try {
            out = socket.getOutputStream();
            objectOut = new ObjectOutputStream(out);
            in = socket.getInputStream();
            objectIn = new ObjectInputStream(in);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            objectOut.writeObject("createScaledRendering");
            objectOut.writeObject(new Integer(w));
            objectOut.writeObject(new Integer(h));
            objectOut.writeObject(SerializerFactory.getState(hints, null));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        Object object = null;
        try {
            object = objectIn.readObject();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        RenderedImage ri = (RenderedImage)object;
        try {
            out.close();
            objectOut.close();
            in.close();
            objectIn.close();
            socket.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ri;
    }

    public float getHeight() {
        return this.height;
    }

    public float getMinX() {
        return this.minX;
    }

    public float getMinY() {
        return this.minY;
    }

    public Object getProperty(String name) {
        Object property = this.properties.get(new CaselessStringKey(name));
        return property == null ? Image.UndefinedProperty : property;
    }

    public String[] getPropertyNames() {
        String[] names = null;
        if (!this.properties.isEmpty()) {
            names = new String[this.properties.size()];
            Enumeration keys = this.properties.keys();
            int index = 0;
            while (keys.hasMoreElements()) {
                CaselessStringKey key = (CaselessStringKey)keys.nextElement();
                names[index++] = key.getName();
            }
        }
        return names;
    }

    public Vector getSources() {
        return this.sources;
    }

    public boolean isDynamic() {
        return this.isDynamic;
    }

    public float getWidth() {
        return this.width;
    }

    private synchronized void openServer() throws IOException, SocketException {
        if (!this.serverOpen) {
            this.serverSocket = new ServerSocket(0);
            this.serverSocket.setSoTimeout(60000);
            this.port = this.serverSocket.getLocalPort();
            this.serverOpen = true;
            this.serverThread = new Thread(new RenderingServer());
            this.serverThread.start();
            SerializableRenderableImage.incrementRemoteReferenceCount(this);
        }
    }

    private void closeClient() {
        Socket socket = this.connectToServer();
        OutputStream out = null;
        ObjectOutputStream objectOut = null;
        try {
            out = socket.getOutputStream();
            objectOut = new ObjectOutputStream(out);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            objectOut.writeObject(CLOSE_MESSAGE);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            out.close();
            objectOut.close();
            socket.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Socket connectToServer() {
        Socket socket = null;
        try {
            socket = new Socket(this.host, this.port);
            socket.setSoLinger(true, 1);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return socket;
    }

    protected void finalize() throws Throwable {
        this.dispose();
        super.finalize();
    }

    public void dispose() {
        if (this.isServer) {
            if (this.serverOpen) {
                this.serverOpen = false;
                try {
                    this.serverThread.join(120000L);
                }
                catch (Exception e) {
                    // empty catch block
                }
                try {
                    this.serverSocket.close();
                }
                catch (Exception exception) {}
            }
        } else {
            this.closeClient();
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        try {
            this.openServer();
        }
        catch (Exception e1) {
            if (e1 instanceof SocketException && this.serverSocket != null) {
                try {
                    this.serverSocket.close();
                }
                catch (IOException e2) {
                    // empty catch block
                }
            }
            this.serverOpen = false;
        }
        out.defaultWriteObject();
        Hashtable propertyTable = this.properties;
        boolean propertiesCloned = false;
        Enumeration keys = propertyTable.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (this.properties.get(key) instanceof Serializable) continue;
            if (!propertiesCloned) {
                propertyTable = (Hashtable)this.properties.clone();
                propertiesCloned = true;
            }
            propertyTable.remove(key);
        }
        out.writeObject(propertyTable);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.isServer = false;
        this.source = null;
        this.serverOpen = false;
        this.serverSocket = null;
        this.serverThread = null;
        in.defaultReadObject();
        this.properties = (Hashtable)in.readObject();
    }

    private class RenderingServer
    implements Runnable {
        private RenderingServer() {
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public void run() {
            while (SerializableRenderableImage.this.serverOpen) {
                Socket socket = null;
                try {
                    socket = SerializableRenderableImage.this.serverSocket.accept();
                    socket.setSoLinger(true, 1);
                }
                catch (InterruptedIOException e) {
                    continue;
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                InputStream in = null;
                OutputStream out = null;
                ObjectInputStream objectIn = null;
                ObjectOutputStream objectOut = null;
                try {
                    in = socket.getInputStream();
                    out = socket.getOutputStream();
                    objectIn = new ObjectInputStream(in);
                    objectOut = new ObjectOutputStream(out);
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                Object obj = null;
                try {
                    obj = objectIn.readObject();
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                RenderedImage ri = null;
                if (!(obj instanceof String)) throw new RuntimeException(JaiI18N.getString("SerializableRenderableImage0"));
                String str = (String)obj;
                if (str.equals(SerializableRenderableImage.CLOSE_MESSAGE)) {
                    SerializableRenderableImage.decrementRemoteReferenceCount(this);
                } else {
                    SerializableRenderedImage sri;
                    if (str.equals("createDefaultRendering")) {
                        ri = SerializableRenderableImage.this.source.createDefaultRendering();
                    } else if (str.equals("createRendering")) {
                        obj = null;
                        try {
                            obj = objectIn.readObject();
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        SerializableState ss = (SerializableState)obj;
                        RenderContext rc = (RenderContext)ss.getObject();
                        ri = SerializableRenderableImage.this.source.createRendering(rc);
                    } else if (str.equals("createScaledRendering")) {
                        obj = null;
                        try {
                            obj = objectIn.readObject();
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        int w = (Integer)obj;
                        try {
                            obj = objectIn.readObject();
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        int h = (Integer)obj;
                        try {
                            obj = objectIn.readObject();
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e.getMessage());
                        }
                        SerializableState ss = (SerializableState)obj;
                        RenderingHints rh = (RenderingHints)ss.getObject();
                        ri = SerializableRenderableImage.this.source.createScaledRendering(w, h, rh);
                    }
                    if (SerializableRenderableImage.this.useTileCodec) {
                        try {
                            sri = new SerializableRenderedImage(ri, true, SerializableRenderableImage.this.registry, SerializableRenderableImage.this.formatName, SerializableRenderableImage.this.encodingParam, SerializableRenderableImage.this.decodingParam);
                        }
                        catch (NotSerializableException nse) {
                            throw new RuntimeException(nse.getMessage());
                        }
                    } else {
                        sri = new SerializableRenderedImage(ri, true);
                    }
                    try {
                        objectOut.writeObject(sri);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
                try {
                    objectOut.close();
                    objectIn.close();
                    out.close();
                    in.close();
                    socket.close();
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }
}

