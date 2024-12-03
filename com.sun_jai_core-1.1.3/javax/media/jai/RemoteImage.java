/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.rmi.RMIImage;
import com.sun.media.jai.rmi.RMIImageImpl;
import com.sun.media.jai.rmi.RasterProxy;
import com.sun.media.jai.rmi.RenderContextProxy;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.SerializableRenderedImage;

public class RemoteImage
extends PlanarImage {
    static final int DEFAULT_TIMEOUT = 1000;
    static final int DEFAULT_NUM_RETRIES = 5;
    static final int VAR_MIN_X = 0;
    static final int VAR_MIN_Y = 1;
    static final int VAR_WIDTH = 2;
    static final int VAR_HEIGHT = 3;
    static final int VAR_TILE_WIDTH = 4;
    static final int VAR_TILE_HEIGHT = 5;
    static final int VAR_TILE_GRID_X_OFFSET = 6;
    static final int VAR_TILE_GRID_Y_OFFSET = 7;
    static final int VAR_SAMPLE_MODEL = 8;
    static final int VAR_COLOR_MODEL = 9;
    static final int VAR_SOURCES = 10;
    static final int NUM_VARS = 11;
    private static final Class NULL_PROPERTY_CLASS = RMIImageImpl.NULL_PROPERTY.getClass();
    protected RMIImage remoteImage;
    private Long id = null;
    protected boolean[] fieldValid = new boolean[11];
    protected String[] propertyNames = null;
    protected int timeout = 1000;
    protected int numRetries = 5;
    private Rectangle imageBounds = null;

    private static Vector vectorize(RenderedImage image) {
        Vector<RenderedImage> v = new Vector<RenderedImage>(1);
        v.add(image);
        return v;
    }

    public RemoteImage(String serverName, RenderedImage source) {
        super(null, null, null);
        int index;
        boolean remoteChainingHack;
        if (serverName == null) {
            serverName = this.getLocalHostAddress();
        }
        boolean bl = remoteChainingHack = (index = serverName.indexOf("::")) != -1;
        if (!remoteChainingHack && source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteImage1"));
        }
        if (remoteChainingHack) {
            this.id = Long.valueOf(serverName.substring(index + 2));
            serverName = serverName.substring(0, index);
        }
        this.getRMIImage(serverName);
        if (!remoteChainingHack) {
            this.getRMIID();
        }
        this.setRMIProperties(serverName);
        if (source != null) {
            try {
                if (source instanceof Serializable) {
                    this.remoteImage.setSource(this.id, source);
                } else {
                    this.remoteImage.setSource(this.id, new SerializableRenderedImage(source));
                }
            }
            catch (RemoteException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public RemoteImage(String serverName, RenderedOp source) {
        super(null, null, null);
        if (serverName == null) {
            serverName = this.getLocalHostAddress();
        }
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteImage1"));
        }
        this.getRMIImage(serverName);
        this.getRMIID();
        this.setRMIProperties(serverName);
        try {
            this.remoteImage.setSource(this.id, source);
        }
        catch (RemoteException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public RemoteImage(String serverName, RenderableOp source, RenderContext renderContext) {
        super(null, null, null);
        if (serverName == null) {
            serverName = this.getLocalHostAddress();
        }
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteImage1"));
        }
        if (renderContext == null) {
            renderContext = new RenderContext(new AffineTransform());
        }
        this.getRMIImage(serverName);
        this.getRMIID();
        this.setRMIProperties(serverName);
        RenderContextProxy rcp = new RenderContextProxy(renderContext);
        try {
            this.remoteImage.setSource(this.id, source, rcp);
        }
        catch (RemoteException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void getRMIImage(String serverName) {
        if (serverName == null) {
            serverName = this.getLocalHostAddress();
        }
        String serviceName = new String("rmi://" + serverName + "/" + "RemoteImageServer");
        this.remoteImage = null;
        try {
            this.remoteImage = (RMIImage)Naming.lookup(serviceName);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getLocalHostAddress() {
        String serverName;
        try {
            serverName = InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return serverName;
    }

    private void getRMIID() {
        try {
            this.id = this.remoteImage.getRemoteID();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void setRMIProperties(String serverName) {
        this.setProperty(this.getClass().getName() + ".serverName", serverName);
        this.setProperty(this.getClass().getName() + ".id", this.id);
    }

    protected void finalize() {
        try {
            this.remoteImage.dispose(this.id);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void setTimeout(int timeout) {
        if (timeout > 0) {
            this.timeout = timeout;
        }
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setNumRetries(int numRetries) {
        if (numRetries > 0) {
            this.numRetries = numRetries;
        }
    }

    public int getNumRetries() {
        return this.numRetries;
    }

    protected void requestField(int fieldIndex, int retries, int timeout) {
        if (retries < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteImage3"));
        }
        if (timeout < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteImage4"));
        }
        int count = 0;
        if (this.fieldValid[fieldIndex]) {
            return;
        }
        while (count++ < retries) {
            try {
                switch (fieldIndex) {
                    case 0: {
                        this.minX = this.remoteImage.getMinX(this.id);
                        break;
                    }
                    case 1: {
                        this.minY = this.remoteImage.getMinY(this.id);
                        break;
                    }
                    case 2: {
                        this.width = this.remoteImage.getWidth(this.id);
                        break;
                    }
                    case 3: {
                        this.height = this.remoteImage.getHeight(this.id);
                        break;
                    }
                    case 4: {
                        this.tileWidth = this.remoteImage.getTileWidth(this.id);
                        break;
                    }
                    case 5: {
                        this.tileHeight = this.remoteImage.getTileHeight(this.id);
                        break;
                    }
                    case 6: {
                        this.tileGridXOffset = this.remoteImage.getTileGridXOffset(this.id);
                        break;
                    }
                    case 7: {
                        this.tileGridYOffset = this.remoteImage.getTileGridYOffset(this.id);
                        break;
                    }
                    case 8: {
                        this.sampleModel = this.remoteImage.getSampleModel(this.id).getSampleModel();
                        break;
                    }
                    case 9: {
                        this.colorModel = this.remoteImage.getColorModel(this.id).getColorModel();
                        break;
                    }
                    case 10: {
                        Vector localSources = this.remoteImage.getSources(this.id);
                        int numSources = localSources.size();
                        for (int i = 0; i < numSources; ++i) {
                            RenderedImage src = (RenderedImage)localSources.get(i);
                            this.addSource(PlanarImage.wrapRenderedImage(src));
                        }
                        break;
                    }
                }
                this.fieldValid[fieldIndex] = true;
                return;
            }
            catch (RemoteException e) {
                System.err.println(JaiI18N.getString("RemoteImage0"));
                try {
                    Thread.sleep(timeout);
                }
                catch (InterruptedException f) {}
            }
        }
    }

    protected void requestField(int fieldIndex) {
        this.requestField(fieldIndex, this.numRetries, this.timeout);
    }

    public int getMinX() {
        this.requestField(0);
        return this.minX;
    }

    public int getMaxX() {
        this.requestField(0);
        this.requestField(2);
        return this.minX + this.width;
    }

    public int getMinY() {
        this.requestField(1);
        return this.minY;
    }

    public int getMaxY() {
        this.requestField(1);
        this.requestField(3);
        return this.minY + this.height;
    }

    public int getWidth() {
        this.requestField(2);
        return this.width;
    }

    public int getHeight() {
        this.requestField(3);
        return this.height;
    }

    public int getTileWidth() {
        this.requestField(4);
        return this.tileWidth;
    }

    public int getTileHeight() {
        this.requestField(5);
        return this.tileHeight;
    }

    public int getTileGridXOffset() {
        this.requestField(6);
        return this.tileGridXOffset;
    }

    public int getTileGridYOffset() {
        this.requestField(7);
        return this.tileGridYOffset;
    }

    public SampleModel getSampleModel() {
        this.requestField(8);
        return this.sampleModel;
    }

    public ColorModel getColorModel() {
        this.requestField(9);
        return this.colorModel;
    }

    public Vector getSources() {
        this.requestField(10);
        return super.getSources();
    }

    public Object getProperty(String name) {
        Object property = super.getProperty(name);
        if (property == null || property == Image.UndefinedProperty) {
            int count = 0;
            while (count++ < this.numRetries) {
                try {
                    property = this.remoteImage.getProperty(this.id, name);
                    if (!NULL_PROPERTY_CLASS.isInstance(property)) break;
                    property = Image.UndefinedProperty;
                    break;
                }
                catch (RemoteException e) {
                    try {
                        Thread.sleep(this.timeout);
                    }
                    catch (InterruptedException f) {}
                }
            }
            if (property == null) {
                property = Image.UndefinedProperty;
            }
            if (property != Image.UndefinedProperty) {
                this.setProperty(name, property);
            }
        }
        return property;
    }

    public String[] getPropertyNames() {
        String[] localPropertyNames = super.getPropertyNames();
        Vector<String> names = new Vector<String>();
        if (localPropertyNames != null) {
            for (int i = 0; i < localPropertyNames.length; ++i) {
                names.add(localPropertyNames[i]);
            }
        }
        int count = 0;
        String[] remotePropertyNames = null;
        while (count++ < this.numRetries) {
            try {
                remotePropertyNames = this.remoteImage.getPropertyNames(this.id);
                break;
            }
            catch (RemoteException e) {
                try {
                    Thread.sleep(this.timeout);
                }
                catch (InterruptedException f) {}
            }
        }
        if (remotePropertyNames != null) {
            for (int i = 0; i < remotePropertyNames.length; ++i) {
                if (names.contains(remotePropertyNames[i])) continue;
                names.add(remotePropertyNames[i]);
            }
        }
        this.propertyNames = names.size() == 0 ? null : names.toArray(new String[names.size()]);
        return this.propertyNames;
    }

    public Raster getTile(int x, int y) {
        int count = 0;
        while (count++ < this.numRetries) {
            try {
                RasterProxy rp = this.remoteImage.getTile(this.id, x, y);
                return rp.getRaster();
            }
            catch (RemoteException e) {
                try {
                    Thread.sleep(this.timeout);
                }
                catch (InterruptedException f) {}
            }
        }
        return null;
    }

    public Raster getData() {
        int count = 0;
        while (count++ < this.numRetries) {
            try {
                RasterProxy rp = this.remoteImage.getData(this.id);
                return rp.getRaster();
            }
            catch (RemoteException e) {
                try {
                    Thread.sleep(this.timeout);
                }
                catch (InterruptedException interruptedException) {}
            }
        }
        return null;
    }

    public Raster getData(Rectangle rect) {
        if (this.imageBounds == null) {
            this.imageBounds = this.getBounds();
        }
        if (rect == null) {
            rect = this.imageBounds;
        } else if (!rect.intersects(this.imageBounds)) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteImage2"));
        }
        int count = 0;
        while (count++ < this.numRetries) {
            try {
                RasterProxy rp = this.remoteImage.getData(this.id, rect);
                return rp.getRaster();
            }
            catch (RemoteException e) {
                try {
                    Thread.sleep(this.timeout);
                }
                catch (InterruptedException f) {}
            }
        }
        return null;
    }

    public WritableRaster copyData(WritableRaster raster) {
        Rectangle bounds;
        int count = 0;
        Rectangle rectangle = bounds = raster == null ? new Rectangle(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight()) : raster.getBounds();
        while (count++ < this.numRetries) {
            try {
                RasterProxy rp = this.remoteImage.copyData(this.id, bounds);
                try {
                    if (raster == null) {
                        raster = (WritableRaster)rp.getRaster();
                        break;
                    }
                    raster.setDataElements(bounds.x, bounds.y, rp.getRaster());
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    raster = null;
                }
                break;
            }
            catch (RemoteException e) {
                try {
                    Thread.sleep(this.timeout);
                }
                catch (InterruptedException f) {}
            }
        }
        return raster;
    }
}

