/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileCache;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.RemoteDescriptor;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.RemoteJAI;
import javax.media.jai.remote.RemoteRIF;
import javax.media.jai.remote.RemoteRenderedImage;
import javax.media.jai.util.ImagingListener;

public abstract class PlanarImageServerProxy
extends PlanarImage
implements RemoteRenderedImage {
    protected int retryInterval;
    protected int numRetries;
    protected transient TileCache cache;
    protected Object tileCacheMetric;
    protected transient OperationRegistry registry;
    protected String serverName;
    protected String protocolName;
    protected String operationName;
    protected ParameterBlock paramBlock;
    protected RenderingHints hints;
    private ImageLayout layout = null;
    protected NegotiableCapabilitySet preferences;
    protected NegotiableCapabilitySet negotiated;
    NegotiableCapabilitySet serverCapabilities;
    NegotiableCapabilitySet clientCapabilities;

    private static void checkLayout(ImageLayout layout) {
        if (layout == null) {
            throw new IllegalArgumentException("layout is null.");
        }
        if (layout.getValidMask() != 1023) {
            throw new Error(JaiI18N.getString("PlanarImageServerProxy3"));
        }
    }

    public PlanarImageServerProxy(String serverName, String protocolName, String operationName, ParameterBlock paramBlock, RenderingHints hints) {
        super(null, null, null);
        if (operationName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PlanarImageServerProxy1"));
        }
        this.serverName = serverName;
        this.protocolName = protocolName;
        this.operationName = operationName;
        this.paramBlock = paramBlock;
        this.hints = hints;
        if (hints == null) {
            this.registry = JAI.getDefaultInstance().getOperationRegistry();
            this.cache = JAI.getDefaultInstance().getTileCache();
            this.retryInterval = 1000;
            this.numRetries = 5;
            this.setNegotiationPreferences(null);
        } else {
            Integer integer;
            this.registry = (OperationRegistry)hints.get(JAI.KEY_OPERATION_REGISTRY);
            if (this.registry == null) {
                this.registry = JAI.getDefaultInstance().getOperationRegistry();
            }
            this.cache = (TileCache)hints.get(JAI.KEY_TILE_CACHE);
            if (this.cache == null) {
                this.cache = JAI.getDefaultInstance().getTileCache();
            }
            this.retryInterval = (integer = (Integer)hints.get(JAI.KEY_RETRY_INTERVAL)) == null ? 1000 : integer;
            integer = (Integer)hints.get(JAI.KEY_NUM_RETRIES);
            this.numRetries = integer == null ? 5 : integer;
            this.tileCacheMetric = hints.get(JAI.KEY_TILE_CACHE_METRIC);
            this.setNegotiationPreferences((NegotiableCapabilitySet)hints.get(JAI.KEY_NEGOTIATION_PREFERENCES));
        }
        if (paramBlock != null) {
            this.setSources(paramBlock.getSources());
        }
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getProtocolName() {
        return this.protocolName;
    }

    public String getOperationName() {
        return this.operationName;
    }

    public ParameterBlock getParameterBlock() {
        return this.paramBlock;
    }

    public RenderingHints getRenderingHints() {
        return this.hints;
    }

    public TileCache getTileCache() {
        return this.cache;
    }

    public void setTileCache(TileCache cache) {
        if (this.cache != null) {
            this.cache.removeTiles(this);
        }
        this.cache = cache;
    }

    public Object getTileCacheMetric() {
        return this.tileCacheMetric;
    }

    public abstract ImageLayout getImageLayout() throws RemoteImagingException;

    public abstract Object getRemoteProperty(String var1) throws RemoteImagingException;

    public abstract String[] getRemotePropertyNames() throws RemoteImagingException;

    public abstract Rectangle mapSourceRect(Rectangle var1, int var2) throws RemoteImagingException;

    public abstract Rectangle mapDestRect(Rectangle var1, int var2) throws RemoteImagingException;

    public abstract Raster computeTile(int var1, int var2) throws RemoteImagingException;

    public int getRetryInterval() {
        return this.retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        if (retryInterval < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
        }
        this.retryInterval = retryInterval;
    }

    public int getNumRetries() {
        return this.numRetries;
    }

    public void setNumRetries(int numRetries) {
        if (numRetries < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
        }
        this.numRetries = numRetries;
    }

    public int getMinX() {
        this.requestLayout();
        return this.minX;
    }

    public int getMaxX() {
        this.requestLayout();
        return this.minX + this.width;
    }

    public int getMinY() {
        this.requestLayout();
        return this.minY;
    }

    public int getMaxY() {
        this.requestLayout();
        return this.minY + this.height;
    }

    public int getWidth() {
        this.requestLayout();
        return this.width;
    }

    public int getHeight() {
        this.requestLayout();
        return this.height;
    }

    public int getTileWidth() {
        this.requestLayout();
        return this.tileWidth;
    }

    public int getTileHeight() {
        this.requestLayout();
        return this.tileHeight;
    }

    public int getTileGridXOffset() {
        this.requestLayout();
        return this.tileGridXOffset;
    }

    public int getTileGridYOffset() {
        this.requestLayout();
        return this.tileGridYOffset;
    }

    public SampleModel getSampleModel() {
        this.requestLayout();
        return this.sampleModel;
    }

    public ColorModel getColorModel() {
        this.requestLayout();
        return this.colorModel;
    }

    private ImageLayout requestLayout() {
        if (this.layout != null) {
            return this.layout;
        }
        RemoteImagingException rieSave = null;
        int count = 0;
        while (count++ < this.numRetries) {
            try {
                this.layout = this.getImageLayout();
                PlanarImageServerProxy.checkLayout(this.layout);
                this.minX = this.layout.getMinX(null);
                this.minY = this.layout.getMinY(null);
                this.width = this.layout.getWidth(null);
                this.height = this.layout.getHeight(null);
                this.tileWidth = this.layout.getTileWidth(null);
                this.tileHeight = this.layout.getTileHeight(null);
                this.tileGridXOffset = this.layout.getTileGridXOffset(null);
                this.tileGridYOffset = this.layout.getTileGridYOffset(null);
                this.sampleModel = this.layout.getSampleModel(null);
                this.colorModel = this.layout.getColorModel(null);
                break;
            }
            catch (RemoteImagingException e) {
                System.err.println(JaiI18N.getString("PlanarImageServerProxy0"));
                rieSave = e;
                try {
                    Thread.sleep(this.retryInterval);
                }
                catch (InterruptedException f) {}
            }
        }
        if (this.layout == null) {
            this.sendExceptionToListener(rieSave);
        }
        return this.layout;
    }

    public Object getProperty(String name) {
        Object property = super.getProperty(name);
        if (property == null || property == Image.UndefinedProperty) {
            RemoteImagingException rieSave = null;
            int count = 0;
            while (count++ < this.numRetries) {
                try {
                    property = this.getRemoteProperty(name);
                    if (property != Image.UndefinedProperty) {
                        this.setProperty(name, property);
                    }
                    return property;
                }
                catch (RemoteImagingException rie) {
                    System.err.println(JaiI18N.getString("PlanarImageServerProxy0"));
                    rieSave = rie;
                    try {
                        Thread.sleep(this.retryInterval);
                    }
                    catch (InterruptedException ie) {}
                }
            }
            this.sendExceptionToListener(rieSave);
            return property;
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
        RemoteImagingException rieSave = null;
        while (count++ < this.numRetries) {
            try {
                remotePropertyNames = this.getRemotePropertyNames();
                break;
            }
            catch (RemoteImagingException rie) {
                System.err.println(JaiI18N.getString("PlanarImageServerProxy0"));
                rieSave = rie;
                try {
                    Thread.sleep(this.retryInterval);
                }
                catch (InterruptedException ie) {}
            }
        }
        if (count > this.numRetries) {
            this.sendExceptionToListener(rieSave);
        }
        if (remotePropertyNames != null) {
            for (int i = 0; i < remotePropertyNames.length; ++i) {
                if (names.contains(remotePropertyNames[i])) continue;
                names.add(remotePropertyNames[i]);
            }
        }
        String[] propertyNames = names.size() == 0 ? null : names.toArray(new String[names.size()]);
        return propertyNames;
    }

    public Raster getTile(int tileX, int tileY) {
        Raster tile = null;
        if (tileX >= this.getMinTileX() && tileX <= this.getMaxTileX() && tileY >= this.getMinTileY() && tileY <= this.getMaxTileY()) {
            Raster raster = tile = this.cache != null ? this.cache.getTile(this, tileX, tileY) : null;
            if (tile == null) {
                int count = 0;
                RemoteImagingException rieSave = null;
                while (count++ < this.numRetries) {
                    try {
                        tile = this.computeTile(tileX, tileY);
                        break;
                    }
                    catch (RemoteImagingException rie) {
                        System.err.println(JaiI18N.getString("PlanarImageServerProxy0"));
                        rieSave = rie;
                        try {
                            Thread.sleep(this.retryInterval);
                        }
                        catch (InterruptedException ie) {}
                    }
                }
                if (count > this.numRetries) {
                    this.sendExceptionToListener(rieSave);
                }
                if (this.cache != null) {
                    this.cache.add(this, tileX, tileY, tile, this.tileCacheMetric);
                }
            }
        }
        return tile;
    }

    protected void finalize() throws Throwable {
        if (this.cache != null) {
            this.cache.removeTiles(this);
        }
        super.finalize();
    }

    public NegotiableCapabilitySet getNegotiationPreferences() {
        return this.preferences;
    }

    public void setNegotiationPreferences(NegotiableCapabilitySet preferences) {
        this.preferences = preferences;
        this.negotiated = null;
        this.getNegotiatedValues();
    }

    public synchronized NegotiableCapabilitySet getNegotiatedValues() throws RemoteImagingException {
        if (this.negotiated == null) {
            this.getCapabilities();
            this.negotiated = RemoteJAI.negotiate(this.preferences, this.serverCapabilities, this.clientCapabilities);
            this.setServerNegotiatedValues(this.negotiated);
        }
        return this.negotiated;
    }

    public NegotiableCapability getNegotiatedValue(String category) throws RemoteImagingException {
        if (this.negotiated == null) {
            this.getCapabilities();
            return RemoteJAI.negotiate(this.preferences, this.serverCapabilities, this.clientCapabilities, category);
        }
        return this.negotiated.getNegotiatedValue(category);
    }

    private void getCapabilities() {
        String mode = "remoteRendered";
        if (this.serverCapabilities == null) {
            RemoteDescriptor desc = (RemoteDescriptor)this.registry.getDescriptor(mode, this.protocolName);
            int count = 0;
            RemoteImagingException rieSave = null;
            while (count++ < this.numRetries) {
                try {
                    this.serverCapabilities = desc.getServerCapabilities(this.serverName);
                    break;
                }
                catch (RemoteImagingException rie) {
                    System.err.println(JaiI18N.getString("PlanarImageServerProxy0"));
                    rieSave = rie;
                    try {
                        Thread.sleep(this.retryInterval);
                    }
                    catch (InterruptedException ie) {}
                }
            }
            if (count > this.numRetries) {
                this.sendExceptionToListener(rieSave);
            }
        }
        if (this.clientCapabilities == null) {
            RemoteRIF rrif = (RemoteRIF)this.registry.getFactory(mode, this.protocolName);
            this.clientCapabilities = rrif.getClientCapabilities();
        }
    }

    void sendExceptionToListener(Exception e) {
        ImagingListener listener = null;
        listener = this.hints != null ? (ImagingListener)this.hints.get(JAI.KEY_IMAGING_LISTENER) : JAI.getDefaultInstance().getImagingListener();
        String message = JaiI18N.getString("PlanarImageServerProxy2");
        listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
    }
}

