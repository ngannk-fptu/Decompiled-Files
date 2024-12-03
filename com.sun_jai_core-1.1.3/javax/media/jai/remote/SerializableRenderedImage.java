/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.RemoteImage;
import javax.media.jai.TileCache;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoder;
import javax.media.jai.tilecodec.TileDecoderFactory;
import javax.media.jai.tilecodec.TileEncoder;
import javax.media.jai.tilecodec.TileEncoderFactory;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public final class SerializableRenderedImage
implements RenderedImage,
Serializable {
    private static final int SERVER_TIMEOUT = 60000;
    private static final String CLOSE_MESSAGE = "CLOSE";
    private static final String CLOSE_ACK = "CLOSE_ACK";
    private Object UID;
    private transient boolean isServer;
    private boolean isSourceRemote;
    private transient RenderedImage source;
    private int minX;
    private int minY;
    private int width;
    private int height;
    private int minTileX;
    private int minTileY;
    private int numXTiles;
    private int numYTiles;
    private int tileWidth;
    private int tileHeight;
    private int tileGridXOffset;
    private int tileGridYOffset;
    private transient SampleModel sampleModel = null;
    private transient ColorModel colorModel = null;
    private transient Vector sources = null;
    private transient Hashtable properties = null;
    private boolean useDeepCopy;
    private Rectangle imageBounds;
    private transient Raster imageRaster;
    private InetAddress host;
    private int port;
    private transient boolean serverOpen = false;
    private transient ServerSocket serverSocket = null;
    private transient Thread serverThread;
    private String formatName;
    private transient OperationRegistry registry;
    private static transient Hashtable remoteReferenceCount;
    private boolean useTileCodec = false;
    private transient TileDecoderFactory tileDecoderFactory = null;
    private transient TileEncoderFactory tileEncoderFactory = null;
    private TileCodecParameterList encodingParam = null;
    private TileCodecParameterList decodingParam = null;
    static /* synthetic */ Class class$java$awt$image$Raster;

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

    SerializableRenderedImage() {
    }

    public SerializableRenderedImage(RenderedImage source, boolean useDeepCopy, OperationRegistry registry, String formatName, TileCodecParameterList encodingParam, TileCodecParameterList decodingParam) throws NotSerializableException {
        this(source, useDeepCopy, false);
        TileCodecDescriptor tcd;
        if (formatName == null) {
            return;
        }
        this.formatName = formatName;
        if (registry == null) {
            registry = JAI.getDefaultInstance().getOperationRegistry();
        }
        this.registry = registry;
        if (encodingParam == null) {
            tcd = this.getTileCodecDescriptor("tileEncoder", formatName);
            encodingParam = tcd.getDefaultParameters("tileEncoder");
        } else if (!formatName.equals(encodingParam.getFormatName())) {
            throw new IllegalArgumentException(JaiI18N.getString("UseTileCodec0"));
        }
        if (decodingParam == null) {
            tcd = this.getTileCodecDescriptor("tileDecoder", formatName);
            decodingParam = tcd.getDefaultParameters("tileDecoder");
        } else if (!formatName.equals(decodingParam.getFormatName())) {
            throw new IllegalArgumentException(JaiI18N.getString("UseTileCodec1"));
        }
        this.tileEncoderFactory = (TileEncoderFactory)registry.getFactory("tileEncoder", formatName);
        this.tileDecoderFactory = (TileDecoderFactory)registry.getFactory("tileDecoder", formatName);
        if (this.tileEncoderFactory == null || this.tileDecoderFactory == null) {
            throw new RuntimeException(JaiI18N.getString("UseTileCodec2"));
        }
        this.encodingParam = encodingParam;
        this.decodingParam = decodingParam;
        this.useTileCodec = true;
    }

    public SerializableRenderedImage(RenderedImage source, boolean useDeepCopy) {
        this(source, useDeepCopy, true);
    }

    public SerializableRenderedImage(RenderedImage source) {
        this(source, false, true);
    }

    private SerializableRenderedImage(RenderedImage source, boolean useDeepCopy, boolean checkDataBuffer) {
        DataBuffer db;
        Raster ras;
        this.UID = ImageUtil.generateID(this);
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderedImage0"));
        }
        SampleModel sm = source.getSampleModel();
        if (sm != null && SerializerFactory.getSerializer(sm.getClass()) == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderedImage2"));
        }
        ColorModel cm = source.getColorModel();
        if (cm != null && SerializerFactory.getSerializer(cm.getClass()) == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderedImage3"));
        }
        if (checkDataBuffer && (ras = source.getTile(source.getMinTileX(), source.getMinTileY())) != null && (db = ras.getDataBuffer()) != null && SerializerFactory.getSerializer(db.getClass()) == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SerializableRenderedImage4"));
        }
        this.isServer = true;
        this.useDeepCopy = useDeepCopy;
        this.source = source;
        this.isSourceRemote = source instanceof RemoteImage;
        this.minX = source.getMinX();
        this.minY = source.getMinY();
        this.width = source.getWidth();
        this.height = source.getHeight();
        this.minTileX = source.getMinTileX();
        this.minTileY = source.getMinTileY();
        this.numXTiles = source.getNumXTiles();
        this.numYTiles = source.getNumYTiles();
        this.tileWidth = source.getTileWidth();
        this.tileHeight = source.getTileHeight();
        this.tileGridXOffset = source.getTileGridXOffset();
        this.tileGridYOffset = source.getTileGridYOffset();
        this.sampleModel = source.getSampleModel();
        this.colorModel = source.getColorModel();
        this.sources = new Vector();
        this.sources.add(source);
        this.properties = new Hashtable();
        String[] propertyNames = source.getPropertyNames();
        if (propertyNames != null) {
            for (int i = 0; i < propertyNames.length; ++i) {
                this.properties.put(propertyNames[i], source.getProperty(propertyNames[i]));
            }
        }
        this.imageBounds = new Rectangle(this.minX, this.minY, this.width, this.height);
        try {
            this.host = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }
        this.serverOpen = false;
    }

    public WritableRaster copyData(WritableRaster dest) {
        Rectangle region;
        if (this.isServer || this.isSourceRemote) {
            return this.source.copyData(dest);
        }
        if (dest == null) {
            region = this.imageBounds;
            SampleModel destSM = this.getSampleModel().createCompatibleSampleModel(region.width, region.height);
            dest = Raster.createWritableRaster(destSM, new Point(region.x, region.y));
        } else {
            region = dest.getBounds().intersection(this.imageBounds);
        }
        if (!region.isEmpty()) {
            int startTileX = PlanarImage.XToTileX(region.x, this.tileGridXOffset, this.tileWidth);
            int startTileY = PlanarImage.YToTileY(region.y, this.tileGridYOffset, this.tileHeight);
            int endTileX = PlanarImage.XToTileX(region.x + region.width - 1, this.tileGridXOffset, this.tileWidth);
            int endTileY = PlanarImage.YToTileY(region.y + region.height - 1, this.tileGridYOffset, this.tileHeight);
            SampleModel[] sampleModels = new SampleModel[]{this.getSampleModel()};
            int tagID = RasterAccessor.findCompatibleTag(sampleModels, dest.getSampleModel());
            RasterFormatTag srcTag = new RasterFormatTag(this.getSampleModel(), tagID);
            RasterFormatTag dstTag = new RasterFormatTag(dest.getSampleModel(), tagID);
            for (int ty = startTileY; ty <= endTileY; ++ty) {
                for (int tx = startTileX; tx <= endTileX; ++tx) {
                    Raster tile = this.getTile(tx, ty);
                    Rectangle subRegion = region.intersection(tile.getBounds());
                    RasterAccessor s = new RasterAccessor(tile, subRegion, srcTag, this.getColorModel());
                    RasterAccessor d = new RasterAccessor(dest, subRegion, dstTag, null);
                    ImageUtil.copyRaster(s, d);
                }
            }
        }
        return dest;
    }

    public ColorModel getColorModel() {
        return this.colorModel;
    }

    public Raster getData() {
        if (this.isServer || this.isSourceRemote) {
            return this.source.getData();
        }
        return this.getData(this.imageBounds);
    }

    public Raster getData(Rectangle rect) {
        Raster raster = null;
        if (this.isServer || this.isSourceRemote) {
            raster = this.source.getData(rect);
        } else if (this.useDeepCopy) {
            raster = this.imageRaster.createChild(rect.x, rect.y, rect.width, rect.height, rect.x, rect.y, null);
        } else {
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
            catch (IOException e) {
                this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage7"), new ImagingException(JaiI18N.getString("SerializableRenderedImage7"), e));
            }
            try {
                objectOut.writeObject(rect);
            }
            catch (IOException e) {
                this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage10"), new ImagingException(JaiI18N.getString("SerializableRenderedImage10"), e));
            }
            Object object = null;
            try {
                object = objectIn.readObject();
            }
            catch (IOException e) {
                this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage8"), new ImagingException(JaiI18N.getString("SerializableRenderedImage8"), e));
            }
            catch (ClassNotFoundException e) {
                this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage9"), new ImagingException(JaiI18N.getString("SerializableRenderedImage9"), e));
            }
            if (this.useTileCodec) {
                byte[] buf = (byte[])object;
                raster = this.decodeRasterFromByteArray(buf);
            } else {
                if (!(object instanceof SerializableState)) {
                    raster = null;
                }
                SerializableState ss = (SerializableState)object;
                Class c = ss.getObjectClass();
                raster = (class$java$awt$image$Raster == null ? (class$java$awt$image$Raster = SerializableRenderedImage.class$("java.awt.image.Raster")) : class$java$awt$image$Raster).isAssignableFrom(c) ? (Raster)ss.getObject() : null;
            }
            try {
                objectOut.flush();
                socket.shutdownOutput();
                socket.shutdownInput();
                objectOut.close();
                out.close();
                objectIn.close();
                in.close();
                socket.close();
            }
            catch (IOException e) {
                String message = JaiI18N.getString("SerializableRenderedImage11");
                this.sendExceptionToListener(message, new ImagingException(message, e));
            }
            if (this.imageBounds.equals(rect)) {
                this.closeClient();
                this.imageRaster = raster;
                this.useDeepCopy = true;
            }
        }
        return raster;
    }

    public int getHeight() {
        return this.height;
    }

    public int getMinTileX() {
        return this.minTileX;
    }

    public int getMinTileY() {
        return this.minTileY;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getNumXTiles() {
        return this.numXTiles;
    }

    public int getNumYTiles() {
        return this.numYTiles;
    }

    public Object getProperty(String name) {
        Object property = this.properties.get(name);
        return property == null ? Image.UndefinedProperty : property;
    }

    public String[] getPropertyNames() {
        String[] names = null;
        if (!this.properties.isEmpty()) {
            names = new String[this.properties.size()];
            Enumeration keys = this.properties.keys();
            int index = 0;
            while (keys.hasMoreElements()) {
                names[index++] = (String)keys.nextElement();
            }
        }
        return names;
    }

    public SampleModel getSampleModel() {
        return this.sampleModel;
    }

    public Vector getSources() {
        return this.sources;
    }

    public Raster getTile(int tileX, int tileY) {
        Raster tile;
        if (this.isServer || this.isSourceRemote) {
            return this.source.getTile(tileX, tileY);
        }
        TileCache cache = JAI.getDefaultInstance().getTileCache();
        if (cache != null && (tile = cache.getTile(this, tileX, tileY)) != null) {
            return tile;
        }
        Rectangle imageBounds = new Rectangle(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight());
        Rectangle destRect = imageBounds.intersection(new Rectangle(this.tileXToX(tileX), this.tileYToY(tileY), this.getTileWidth(), this.getTileHeight()));
        Raster tile2 = this.getData(destRect);
        if (cache != null) {
            cache.add(this, tileX, tileY, tile2);
        }
        return tile2;
    }

    public Object getImageID() {
        return this.UID;
    }

    private int tileXToX(int tx) {
        return PlanarImage.tileXToX(tx, this.getTileGridXOffset(), this.getTileWidth());
    }

    private int tileYToY(int ty) {
        return PlanarImage.tileYToY(ty, this.getTileGridYOffset(), this.getTileHeight());
    }

    public int getTileGridXOffset() {
        return this.tileGridXOffset;
    }

    public int getTileGridYOffset() {
        return this.tileGridYOffset;
    }

    public int getTileHeight() {
        return this.tileHeight;
    }

    public int getTileWidth() {
        return this.tileWidth;
    }

    public int getWidth() {
        return this.width;
    }

    private synchronized void openServer() throws IOException, SocketException {
        if (!this.serverOpen) {
            this.serverSocket = new ServerSocket(0);
            this.serverSocket.setSoTimeout(60000);
            this.port = this.serverSocket.getLocalPort();
            this.serverOpen = true;
            this.serverThread = new Thread(new TileServer());
            this.serverThread.setDaemon(true);
            this.serverThread.start();
            SerializableRenderedImage.incrementRemoteReferenceCount(this);
        }
    }

    private void closeClient() {
        Socket socket = this.connectToServer();
        OutputStream out = null;
        ObjectOutputStream objectOut = null;
        ObjectInputStream objectIn = null;
        try {
            out = socket.getOutputStream();
            objectOut = new ObjectOutputStream(out);
            objectIn = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage7"), new ImagingException(JaiI18N.getString("SerializableRenderedImage7"), e));
        }
        try {
            objectOut.writeObject(CLOSE_MESSAGE);
        }
        catch (IOException e) {
            this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage13"), new ImagingException(JaiI18N.getString("SerializableRenderedImage13"), e));
        }
        try {
            objectIn.readObject();
        }
        catch (IOException e) {
            this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage8"), new ImagingException(JaiI18N.getString("SerializableRenderedImage8"), e));
        }
        catch (ClassNotFoundException cnfe) {
            this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage9"), new ImagingException(JaiI18N.getString("SerializableRenderedImage9"), cnfe));
        }
        try {
            objectOut.flush();
            socket.shutdownOutput();
            objectOut.close();
            out.close();
            objectIn.close();
            socket.close();
        }
        catch (IOException e) {
            this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage11"), new ImagingException(JaiI18N.getString("SerializableRenderedImage11"), e));
        }
    }

    private Socket connectToServer() {
        Socket socket = null;
        try {
            socket = new Socket(this.host, this.port);
            socket.setSoLinger(true, 1);
        }
        catch (IOException e) {
            this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage14"), new ImagingException(JaiI18N.getString("SerializableRenderedImage14"), e));
        }
        return socket;
    }

    private byte[] encodeRasterToByteArray(Raster raster) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TileEncoder encoder = this.tileEncoderFactory.createEncoder(bos, this.encodingParam, raster.getSampleModel());
        try {
            encoder.encode(raster);
            return bos.toByteArray();
        }
        catch (IOException e) {
            this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage15"), new ImagingException(JaiI18N.getString("SerializableRenderedImage15"), e));
            return null;
        }
    }

    private Raster decodeRasterFromByteArray(byte[] buf) {
        ByteArrayInputStream bis = new ByteArrayInputStream(buf);
        if (this.tileDecoderFactory == null) {
            if (this.registry == null) {
                this.registry = JAI.getDefaultInstance().getOperationRegistry();
            }
            this.tileDecoderFactory = (TileDecoderFactory)this.registry.getFactory("tileDecoder", this.formatName);
            TileCodecParameterList temp = this.decodingParam;
            if (temp != null) {
                TileCodecDescriptor tcd = this.getTileCodecDescriptor("tileDecoder", this.formatName);
                ParameterListDescriptor pld = tcd.getParameterListDescriptor("tileDecoder");
                this.decodingParam = new TileCodecParameterList(this.formatName, new String[]{"tileDecoder"}, pld);
                String[] names = pld.getParamNames();
                if (names != null) {
                    for (int i = 0; i < names.length; ++i) {
                        this.decodingParam.setParameter(names[i], temp.getObjectParameter(names[i]));
                    }
                }
            } else {
                this.decodingParam = this.getTileCodecDescriptor("tileDecoder", this.formatName).getDefaultParameters("tileDecoder");
            }
        }
        TileDecoder decoder = this.tileDecoderFactory.createDecoder(bis, this.decodingParam);
        try {
            return decoder.decode();
        }
        catch (IOException e) {
            this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage16"), new ImagingException(JaiI18N.getString("SerializableRenderedImage16"), e));
            return null;
        }
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
        if (!this.useDeepCopy) {
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
                this.useDeepCopy = true;
            }
        }
        out.defaultWriteObject();
        if (this.isSourceRemote) {
            String remoteClass = this.source.getClass().getName();
            out.writeObject(this.source.getProperty(remoteClass + ".serverName"));
            out.writeObject(this.source.getProperty(remoteClass + ".id"));
        }
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
        out.writeObject(SerializerFactory.getState(this.sampleModel, null));
        out.writeObject(SerializerFactory.getState(this.colorModel, null));
        out.writeObject(propertyTable);
        if (this.useDeepCopy) {
            if (this.useTileCodec) {
                out.writeObject(this.encodeRasterToByteArray(this.source.getData()));
            } else {
                out.writeObject(SerializerFactory.getState(this.source.getData(), null));
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.isServer = false;
        this.source = null;
        this.serverOpen = false;
        this.serverSocket = null;
        this.serverThread = null;
        this.colorModel = null;
        in.defaultReadObject();
        if (this.isSourceRemote) {
            String serverName = (String)in.readObject();
            Long id = (Long)in.readObject();
            this.source = new RemoteImage(serverName + "::" + id, (RenderedImage)null);
        }
        SerializableState smState = (SerializableState)in.readObject();
        this.sampleModel = (SampleModel)smState.getObject();
        SerializableState cmState = (SerializableState)in.readObject();
        this.colorModel = (ColorModel)cmState.getObject();
        this.properties = (Hashtable)in.readObject();
        if (this.useDeepCopy) {
            if (this.useTileCodec) {
                this.imageRaster = this.decodeRasterFromByteArray((byte[])in.readObject());
            } else {
                SerializableState rasState = (SerializableState)in.readObject();
                this.imageRaster = (Raster)rasState.getObject();
            }
        }
    }

    private TileCodecDescriptor getTileCodecDescriptor(String registryMode, String formatName) {
        if (this.registry == null) {
            return (TileCodecDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor(registryMode, formatName);
        }
        return (TileCodecDescriptor)this.registry.getDescriptor(registryMode, formatName);
    }

    void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
        listener.errorOccurred(message, e, this, false);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private class TileServer
    implements Runnable {
        private TileServer() {
        }

        public void run() {
            while (SerializableRenderedImage.this.serverOpen) {
                Socket socket = null;
                try {
                    socket = SerializableRenderedImage.this.serverSocket.accept();
                    socket.setSoLinger(true, 1);
                }
                catch (InterruptedIOException e) {
                    continue;
                }
                catch (SocketException e) {
                    SerializableRenderedImage.this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage5"), new ImagingException(JaiI18N.getString("SerializableRenderedImage5"), e));
                }
                catch (IOException e) {
                    SerializableRenderedImage.this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage6"), new ImagingException(JaiI18N.getString("SerializableRenderedImage6"), e));
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
                catch (IOException e) {
                    SerializableRenderedImage.this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage7"), new ImagingException(JaiI18N.getString("SerializableRenderedImage7"), e));
                }
                Object obj = null;
                try {
                    obj = objectIn.readObject();
                }
                catch (IOException e) {
                    SerializableRenderedImage.this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage8"), new ImagingException(JaiI18N.getString("SerializableRenderedImage8"), e));
                }
                catch (ClassNotFoundException e) {
                    SerializableRenderedImage.this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage9"), new ImagingException(JaiI18N.getString("SerializableRenderedImage9"), e));
                }
                if (obj instanceof String && ((String)obj).equals(SerializableRenderedImage.CLOSE_MESSAGE)) {
                    try {
                        objectOut.writeObject(SerializableRenderedImage.CLOSE_ACK);
                    }
                    catch (IOException e) {
                        SerializableRenderedImage.this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage17"), new ImagingException(JaiI18N.getString("SerializableRenderedImage17"), e));
                    }
                    SerializableRenderedImage.decrementRemoteReferenceCount(this);
                } else if (obj instanceof Rectangle) {
                    Raster raster = SerializableRenderedImage.this.source.getData((Rectangle)obj);
                    if (SerializableRenderedImage.this.useTileCodec) {
                        byte[] buf = SerializableRenderedImage.this.encodeRasterToByteArray(raster);
                        try {
                            objectOut.writeObject(buf);
                        }
                        catch (IOException e) {
                            SerializableRenderedImage.this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage10"), new ImagingException(JaiI18N.getString("SerializableRenderedImage10"), e));
                        }
                    } else {
                        try {
                            objectOut.writeObject(SerializerFactory.getState(raster, null));
                        }
                        catch (IOException e) {
                            SerializableRenderedImage.this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage10"), new ImagingException(JaiI18N.getString("SerializableRenderedImage10"), e));
                        }
                    }
                }
                try {
                    objectOut.flush();
                    socket.shutdownOutput();
                    socket.shutdownInput();
                    objectOut.close();
                    objectIn.close();
                    out.close();
                    in.close();
                    socket.close();
                }
                catch (IOException e) {
                    SerializableRenderedImage.this.sendExceptionToListener(JaiI18N.getString("SerializableRenderedImage10"), new ImagingException(JaiI18N.getString("SerializableRenderedImage10"), e));
                }
            }
        }
    }
}

