/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.CollectionChangeEvent;
import javax.media.jai.CollectionOp;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.PropertySourceChangeEvent;
import javax.media.jai.RegistryMode;
import javax.media.jai.RenderedOp;
import javax.media.jai.RenderingChangeEvent;
import javax.media.jai.TileCache;
import javax.media.jai.registry.RemoteRIFRegistry;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.PlanarImageServerProxy;
import javax.media.jai.remote.RemoteDescriptor;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.RemoteJAI;
import javax.media.jai.remote.RemoteRIF;
import javax.media.jai.remote.RemoteRenderedImage;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public class RemoteRenderedOp
extends RenderedOp
implements RemoteRenderedImage {
    protected String protocolName;
    protected String serverName;
    private NegotiableCapabilitySet negotiated;
    private transient RenderingHints oldHints;
    private static Set nodeEventNames = null;
    static /* synthetic */ Class class$javax$media$jai$remote$RemoteDescriptor;

    public RemoteRenderedOp(String protocolName, String serverName, String opName, ParameterBlock pb, RenderingHints hints) {
        this(null, protocolName, serverName, opName, pb, hints);
    }

    public RemoteRenderedOp(OperationRegistry registry, String protocolName, String serverName, String opName, ParameterBlock pb, RenderingHints hints) {
        super(registry, opName, pb, hints);
        if (protocolName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        this.protocolName = protocolName;
        this.serverName = serverName;
        this.addPropertyChangeListener("ServerName", this);
        this.addPropertyChangeListener("ProtocolName", this);
        this.addPropertyChangeListener("ProtocolAndServerName", this);
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        if (serverName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic2"));
        }
        if (serverName.equalsIgnoreCase(this.serverName)) {
            return;
        }
        String oldServerName = this.serverName;
        this.serverName = serverName;
        this.fireEvent("ServerName", oldServerName, serverName);
        this.nodeSupport.resetPropertyEnvironment(false);
    }

    public String getProtocolName() {
        return this.protocolName;
    }

    public void setProtocolName(String protocolName) {
        if (protocolName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        if (protocolName.equalsIgnoreCase(this.protocolName)) {
            return;
        }
        String oldProtocolName = this.protocolName;
        this.protocolName = protocolName;
        this.fireEvent("ProtocolName", oldProtocolName, protocolName);
        this.nodeSupport.resetPropertyEnvironment(false);
    }

    public void setProtocolAndServerNames(String protocolName, String serverName) {
        if (serverName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic2"));
        }
        if (protocolName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        boolean protocolNotChanged = protocolName.equalsIgnoreCase(this.protocolName);
        boolean serverNotChanged = serverName.equalsIgnoreCase(this.serverName);
        if (protocolNotChanged) {
            if (serverNotChanged) {
                return;
            }
            this.setServerName(serverName);
            return;
        }
        if (serverNotChanged) {
            this.setProtocolName(protocolName);
            return;
        }
        String oldProtocolName = this.protocolName;
        String oldServerName = this.serverName;
        this.protocolName = protocolName;
        this.serverName = serverName;
        this.fireEvent("ProtocolAndServerName", new String[]{oldProtocolName, oldServerName}, new String[]{protocolName, serverName});
        this.nodeSupport.resetPropertyEnvironment(false);
    }

    public String getRegistryModeName() {
        return RegistryMode.getMode("remoteRendered").getName();
    }

    protected synchronized PlanarImage createInstance(boolean isNodeRendered) {
        ParameterBlock pb = new ParameterBlock();
        pb.setParameters(this.getParameters());
        int numSources = this.getNumSources();
        for (int i = 0; i < numSources; ++i) {
            Object source = this.getNodeSource(i);
            Object ai = null;
            if (source instanceof RenderedOp) {
                RenderedOp src = (RenderedOp)source;
                ai = isNodeRendered ? src.getRendering() : src.createInstance();
            } else {
                ai = source instanceof RenderedImage || source instanceof Collection ? source : (source instanceof CollectionOp ? ((CollectionOp)source).getCollection() : source);
            }
            pb.addSource(ai);
        }
        RemoteRenderedImage instance = RemoteRIFRegistry.create(this.nodeSupport.getRegistry(), this.protocolName, this.serverName, this.nodeSupport.getOperationName(), pb, this.nodeSupport.getRenderingHints());
        if (instance == null) {
            throw new ImagingException(JaiI18N.getString("RemoteRenderedOp2"));
        }
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        this.oldHints = rh == null ? null : (RenderingHints)rh.clone();
        return PlanarImage.wrapRenderedImage(instance);
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        block55: {
            Object evtSrc = evt.getSource();
            Vector<Object> nodeSources = this.nodeSupport.getParameterBlock().getSources();
            String propName = evt.getPropertyName().toLowerCase(Locale.ENGLISH);
            if (this.theImage == null || (!(evt instanceof PropertyChangeEventJAI) || evtSrc != this || evt instanceof PropertySourceChangeEvent || !nodeEventNames.contains(propName)) && (!(evt instanceof RenderingChangeEvent) && !(evt instanceof CollectionChangeEvent) && (!(evt instanceof PropertyChangeEventJAI) || !(evtSrc instanceof RenderedImage) || !propName.equals("invalidregion")) || !nodeSources.contains(evtSrc))) break block55;
            PlanarImage theOldImage = this.theImage;
            boolean shouldFireEvent = false;
            Shape invalidRegion = null;
            if (evtSrc == this && (propName.equals("operationregistry") || propName.equals("protocolname") || propName.equals("protocolandservername"))) {
                shouldFireEvent = true;
                this.theImage = null;
            } else if (evt instanceof RenderingChangeEvent || evtSrc instanceof RenderedImage && propName.equals("invalidregion")) {
                shouldFireEvent = true;
                Shape srcInvalidRegion = null;
                if (evt instanceof RenderingChangeEvent) {
                    RenderingChangeEvent rcEvent = (RenderingChangeEvent)evt;
                    srcInvalidRegion = rcEvent.getInvalidRegion();
                    if (srcInvalidRegion == null) {
                        srcInvalidRegion = ((PlanarImage)rcEvent.getOldValue()).getBounds();
                    }
                } else {
                    srcInvalidRegion = (Shape)evt.getNewValue();
                    if (srcInvalidRegion == null) {
                        RenderedImage rSrc = (RenderedImage)evtSrc;
                        srcInvalidRegion = new Rectangle(rSrc.getMinX(), rSrc.getMinY(), rSrc.getWidth(), rSrc.getHeight());
                    }
                }
                if (!(this.theImage instanceof PlanarImageServerProxy)) {
                    this.theImage = null;
                } else {
                    int i;
                    Rectangle imageBounds;
                    int h;
                    int w;
                    int y;
                    int x;
                    Rectangle tileBounds;
                    PlanarImageServerProxy oldPISP = (PlanarImageServerProxy)this.theImage;
                    Rectangle srcInvalidBounds = srcInvalidRegion.getBounds();
                    if (srcInvalidBounds.isEmpty() && !(tileBounds = new Rectangle(x = oldPISP.tileXToX(oldPISP.getMinTileX()), y = oldPISP.tileYToY(oldPISP.getMinTileY()), w = oldPISP.getNumXTiles() * oldPISP.getTileWidth(), h = oldPISP.getNumYTiles() * oldPISP.getTileHeight())).equals(imageBounds = oldPISP.getBounds())) {
                        Area tmpArea = new Area(tileBounds);
                        tmpArea.subtract(new Area(imageBounds));
                        srcInvalidRegion = tmpArea;
                        srcInvalidBounds = srcInvalidRegion.getBounds();
                    }
                    boolean saveAllTiles = false;
                    ArrayList<Point> validTiles = null;
                    if (srcInvalidBounds.isEmpty()) {
                        invalidRegion = srcInvalidRegion;
                        saveAllTiles = true;
                    } else {
                        Point[] indices;
                        int idx = nodeSources.indexOf(evtSrc);
                        Rectangle dstRegionBounds = oldPISP.mapSourceRect(srcInvalidBounds, idx);
                        if (dstRegionBounds == null) {
                            dstRegionBounds = oldPISP.getBounds();
                        }
                        int numIndices = (indices = this.getTileIndices(dstRegionBounds)) != null ? indices.length : 0;
                        GeneralPath gp = null;
                        for (i = 0; i < numIndices; ++i) {
                            Rectangle dstRect;
                            Rectangle srcRect;
                            if (i % 1000 == 0 && gp != null) {
                                gp = new GeneralPath(new Area(gp));
                            }
                            if ((srcRect = oldPISP.mapDestRect(dstRect = this.getTileRect(indices[i].x, indices[i].y), idx)) == null) {
                                gp = null;
                                break;
                            }
                            if (srcInvalidRegion.intersects(srcRect)) {
                                if (gp == null) {
                                    gp = new GeneralPath(dstRect);
                                    continue;
                                }
                                gp.append(dstRect, false);
                                continue;
                            }
                            if (validTiles == null) {
                                validTiles = new ArrayList<Point>();
                            }
                            validTiles.add(indices[i]);
                        }
                        invalidRegion = gp == null ? null : new Area(gp);
                    }
                    TileCache oldCache = oldPISP.getTileCache();
                    this.theImage = null;
                    if (oldCache != null && (saveAllTiles || validTiles != null)) {
                        this.newEventRendering(this.protocolName, oldPISP, (PropertyChangeEventJAI)evt);
                        if (this.theImage instanceof PlanarImageServerProxy && ((PlanarImageServerProxy)this.theImage).getTileCache() != null) {
                            Raster tile;
                            PlanarImageServerProxy newPISP = (PlanarImageServerProxy)this.theImage;
                            TileCache newCache = newPISP.getTileCache();
                            Object tileCacheMetric = newPISP.getTileCacheMetric();
                            if (saveAllTiles) {
                                Raster[] tiles = oldCache.getTiles(oldPISP);
                                int numTiles = tiles == null ? 0 : tiles.length;
                                for (int i2 = 0; i2 < numTiles; ++i2) {
                                    tile = tiles[i2];
                                    int tx = newPISP.XToTileX(tile.getMinX());
                                    int ty = newPISP.YToTileY(tile.getMinY());
                                    newCache.add(newPISP, tx, ty, tile, tileCacheMetric);
                                }
                            } else {
                                int numValidTiles = validTiles.size();
                                for (i = 0; i < numValidTiles; ++i) {
                                    Point tileIndex = (Point)validTiles.get(i);
                                    tile = oldCache.getTile(oldPISP, tileIndex.x, tileIndex.y);
                                    if (tile == null) continue;
                                    newCache.add(newPISP, tileIndex.x, tileIndex.y, tile, tileCacheMetric);
                                }
                            }
                        }
                    }
                }
            } else {
                ParameterBlock oldPB = null;
                ParameterBlock newPB = null;
                String oldServerName = this.serverName;
                String newServerName = this.serverName;
                boolean checkInvalidRegion = false;
                if (propName.equals("operationname")) {
                    if (this.theImage instanceof PlanarImageServerProxy) {
                        this.newEventRendering(this.protocolName, (PlanarImageServerProxy)this.theImage, (PropertyChangeEventJAI)evt);
                    } else {
                        this.theImage = null;
                        this.createRendering();
                    }
                    shouldFireEvent = true;
                } else if (propName.equals("parameterblock")) {
                    oldPB = (ParameterBlock)evt.getOldValue();
                    newPB = (ParameterBlock)evt.getNewValue();
                    checkInvalidRegion = true;
                } else if (propName.equals("sources")) {
                    Vector<Object> params = this.nodeSupport.getParameterBlock().getParameters();
                    oldPB = new ParameterBlock((Vector)evt.getOldValue(), params);
                    newPB = new ParameterBlock((Vector)evt.getNewValue(), params);
                    checkInvalidRegion = true;
                } else if (propName.equals("parameters")) {
                    oldPB = new ParameterBlock(nodeSources, (Vector)evt.getOldValue());
                    newPB = new ParameterBlock(nodeSources, (Vector)evt.getNewValue());
                    checkInvalidRegion = true;
                } else if (propName.equals("renderinghints")) {
                    oldPB = newPB = this.nodeSupport.getParameterBlock();
                    checkInvalidRegion = true;
                } else if (propName.equals("servername")) {
                    oldPB = newPB = this.nodeSupport.getParameterBlock();
                    oldServerName = (String)evt.getOldValue();
                    newServerName = (String)evt.getNewValue();
                    checkInvalidRegion = true;
                } else if (evt instanceof CollectionChangeEvent) {
                    int collectionIndex = nodeSources.indexOf(evtSrc);
                    Vector oldSources = (Vector)nodeSources.clone();
                    Vector newSources = (Vector)nodeSources.clone();
                    oldSources.set(collectionIndex, evt.getOldValue());
                    newSources.set(collectionIndex, evt.getNewValue());
                    Vector<Object> params = this.nodeSupport.getParameterBlock().getParameters();
                    oldPB = new ParameterBlock(oldSources, params);
                    newPB = new ParameterBlock(newSources, params);
                    checkInvalidRegion = true;
                }
                if (checkInvalidRegion) {
                    shouldFireEvent = true;
                    OperationRegistry registry = this.nodeSupport.getRegistry();
                    RemoteDescriptor odesc = (RemoteDescriptor)registry.getDescriptor(class$javax$media$jai$remote$RemoteDescriptor == null ? (class$javax$media$jai$remote$RemoteDescriptor = RemoteRenderedOp.class$("javax.media.jai.remote.RemoteDescriptor")) : class$javax$media$jai$remote$RemoteDescriptor, this.protocolName);
                    invalidRegion = (Shape)odesc.getInvalidRegion("rendered", oldServerName, oldPB = ImageUtil.evaluateParameters(oldPB), this.oldHints, newServerName, newPB = ImageUtil.evaluateParameters(newPB), this.nodeSupport.getRenderingHints(), this);
                    if (invalidRegion == null || !(this.theImage instanceof PlanarImageServerProxy)) {
                        this.theImage = null;
                    } else {
                        PlanarImageServerProxy oldRendering = (PlanarImageServerProxy)this.theImage;
                        this.newEventRendering(this.protocolName, oldRendering, (PropertyChangeEventJAI)evt);
                        if (this.theImage instanceof PlanarImageServerProxy && oldRendering.getTileCache() != null && ((PlanarImageServerProxy)this.theImage).getTileCache() != null) {
                            int i;
                            int numTiles;
                            Raster[] tiles;
                            Rectangle imageBounds;
                            int h;
                            int w;
                            int y;
                            int x;
                            Rectangle tileBounds;
                            PlanarImageServerProxy newRendering = (PlanarImageServerProxy)this.theImage;
                            TileCache oldCache = oldRendering.getTileCache();
                            TileCache newCache = newRendering.getTileCache();
                            Object tileCacheMetric = newRendering.getTileCacheMetric();
                            if (invalidRegion.getBounds().isEmpty() && !(tileBounds = new Rectangle(x = oldRendering.tileXToX(oldRendering.getMinTileX()), y = oldRendering.tileYToY(oldRendering.getMinTileY()), w = oldRendering.getNumXTiles() * oldRendering.getTileWidth(), h = oldRendering.getNumYTiles() * oldRendering.getTileHeight())).equals(imageBounds = oldRendering.getBounds())) {
                                Area tmpArea = new Area(tileBounds);
                                tmpArea.subtract(new Area(imageBounds));
                                invalidRegion = tmpArea;
                            }
                            if (invalidRegion.getBounds().isEmpty()) {
                                tiles = oldCache.getTiles(oldRendering);
                                numTiles = tiles == null ? 0 : tiles.length;
                                for (i = 0; i < numTiles; ++i) {
                                    Raster tile = tiles[i];
                                    int tx = newRendering.XToTileX(tile.getMinX());
                                    int ty = newRendering.YToTileY(tile.getMinY());
                                    newCache.add(newRendering, tx, ty, tile, tileCacheMetric);
                                }
                            } else {
                                tiles = oldCache.getTiles(oldRendering);
                                numTiles = tiles == null ? 0 : tiles.length;
                                for (i = 0; i < numTiles; ++i) {
                                    Raster tile = tiles[i];
                                    Rectangle bounds = tile.getBounds();
                                    if (invalidRegion.intersects(bounds)) continue;
                                    newCache.add(newRendering, newRendering.XToTileX(bounds.x), newRendering.YToTileY(bounds.y), tile, tileCacheMetric);
                                }
                            }
                        }
                    }
                }
            }
            if (theOldImage instanceof PlanarImageServerProxy && this.theImage == null) {
                this.newEventRendering(this.protocolName, (PlanarImageServerProxy)theOldImage, (PropertyChangeEventJAI)evt);
            } else {
                this.createRendering();
            }
            if (shouldFireEvent) {
                this.resetProperties(true);
                RenderingChangeEvent rcEvent = new RenderingChangeEvent(this, theOldImage, this.theImage, invalidRegion);
                this.eventManager.firePropertyChange(rcEvent);
                Vector sinks = this.getSinks();
                if (sinks != null) {
                    int numSinks = sinks.size();
                    for (int i = 0; i < numSinks; ++i) {
                        Object sink = sinks.get(i);
                        if (!(sink instanceof PropertyChangeListener)) continue;
                        ((PropertyChangeListener)sink).propertyChange(rcEvent);
                    }
                }
            }
        }
    }

    private void newEventRendering(String protocolName, PlanarImageServerProxy oldPISP, PropertyChangeEventJAI event) {
        RemoteRIF rrif = (RemoteRIF)this.nodeSupport.getRegistry().getFactory("remoterendered", protocolName);
        this.theImage = (PlanarImage)((Object)rrif.create(oldPISP, this, event));
    }

    private void fireEvent(String propName, Object oldVal, Object newVal) {
        if (this.eventManager != null) {
            Object eventSource = this.eventManager.getPropertyChangeEventSource();
            PropertyChangeEventJAI evt = new PropertyChangeEventJAI(eventSource, propName, oldVal, newVal);
            this.eventManager.firePropertyChange(evt);
        }
    }

    public int getRetryInterval() {
        if (this.theImage != null) {
            return ((RemoteRenderedImage)((Object)this.theImage)).getRetryInterval();
        }
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        if (rh == null) {
            return 1000;
        }
        Integer i = (Integer)rh.get(JAI.KEY_RETRY_INTERVAL);
        if (i == null) {
            return 1000;
        }
        return i;
    }

    public void setRetryInterval(int retryInterval) {
        RenderingHints rh;
        if (retryInterval < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
        }
        if (this.theImage != null) {
            ((RemoteRenderedImage)((Object)this.theImage)).setRetryInterval(retryInterval);
        }
        if ((rh = this.nodeSupport.getRenderingHints()) == null) {
            this.nodeSupport.setRenderingHints(new RenderingHints(null));
            rh = this.nodeSupport.getRenderingHints();
        }
        rh.put(JAI.KEY_RETRY_INTERVAL, new Integer(retryInterval));
    }

    public int getNumRetries() {
        if (this.theImage != null) {
            return ((RemoteRenderedImage)((Object)this.theImage)).getNumRetries();
        }
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        if (rh == null) {
            return 5;
        }
        Integer i = (Integer)rh.get(JAI.KEY_NUM_RETRIES);
        if (i == null) {
            return 5;
        }
        return i;
    }

    public void setNumRetries(int numRetries) {
        RenderingHints rh;
        if (numRetries < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
        }
        if (this.theImage != null) {
            ((RemoteRenderedImage)((Object)this.theImage)).setNumRetries(numRetries);
        }
        if ((rh = this.nodeSupport.getRenderingHints()) == null) {
            this.nodeSupport.setRenderingHints(new RenderingHints(null));
            rh = this.nodeSupport.getRenderingHints();
        }
        rh.put(JAI.KEY_NUM_RETRIES, new Integer(numRetries));
    }

    public void setNegotiationPreferences(NegotiableCapabilitySet preferences) {
        if (this.theImage != null) {
            ((RemoteRenderedImage)((Object)this.theImage)).setNegotiationPreferences(preferences);
        }
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        if (preferences != null) {
            if (rh == null) {
                this.nodeSupport.setRenderingHints(new RenderingHints(null));
                rh = this.nodeSupport.getRenderingHints();
            }
            rh.put(JAI.KEY_NEGOTIATION_PREFERENCES, preferences);
        } else if (rh != null) {
            rh.remove(JAI.KEY_NEGOTIATION_PREFERENCES);
        }
        this.negotiated = this.negotiate(preferences);
    }

    public NegotiableCapabilitySet getNegotiationPreferences() {
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        return rh == null ? null : (NegotiableCapabilitySet)rh.get(JAI.KEY_NEGOTIATION_PREFERENCES);
    }

    private NegotiableCapabilitySet negotiate(NegotiableCapabilitySet prefs) {
        OperationRegistry registry = this.nodeSupport.getRegistry();
        NegotiableCapabilitySet serverCap = null;
        RemoteDescriptor descriptor = (RemoteDescriptor)registry.getDescriptor(class$javax$media$jai$remote$RemoteDescriptor == null ? (class$javax$media$jai$remote$RemoteDescriptor = RemoteRenderedOp.class$("javax.media.jai.remote.RemoteDescriptor")) : class$javax$media$jai$remote$RemoteDescriptor, this.protocolName);
        if (descriptor == null) {
            Object[] msgArg0 = new Object[]{new String(this.protocolName)};
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(Locale.getDefault());
            formatter.applyPattern(JaiI18N.getString("RemoteJAI16"));
            throw new ImagingException(formatter.format(msgArg0));
        }
        int count = 0;
        int numRetries = this.getNumRetries();
        int retryInterval = this.getRetryInterval();
        RemoteImagingException rieSave = null;
        while (count++ < numRetries) {
            try {
                serverCap = descriptor.getServerCapabilities(this.serverName);
                break;
            }
            catch (RemoteImagingException rie) {
                System.err.println(JaiI18N.getString("RemoteJAI24"));
                rieSave = rie;
                try {
                    Thread.sleep(retryInterval);
                }
                catch (InterruptedException ie) {
                    this.sendExceptionToListener(JaiI18N.getString("Generic5"), new ImagingException(JaiI18N.getString("Generic5"), ie));
                }
            }
        }
        if (serverCap == null && count > numRetries) {
            this.sendExceptionToListener(JaiI18N.getString("RemoteJAI18"), rieSave);
        }
        RemoteRIF rrif = (RemoteRIF)registry.getFactory("remoteRendered", this.protocolName);
        return RemoteJAI.negotiate(prefs, serverCap, rrif.getClientCapabilities());
    }

    public NegotiableCapabilitySet getNegotiatedValues() throws RemoteImagingException {
        if (this.theImage != null) {
            return ((RemoteRenderedImage)((Object)this.theImage)).getNegotiatedValues();
        }
        return this.negotiated;
    }

    public NegotiableCapability getNegotiatedValue(String category) throws RemoteImagingException {
        if (this.theImage != null) {
            return ((RemoteRenderedImage)((Object)this.theImage)).getNegotiatedValue(category);
        }
        return this.negotiated == null ? null : this.negotiated.getNegotiatedValue(category);
    }

    public void setServerNegotiatedValues(NegotiableCapabilitySet negotiatedValues) throws RemoteImagingException {
        if (this.theImage != null) {
            ((RemoteRenderedImage)((Object)this.theImage)).setServerNegotiatedValues(negotiatedValues);
        }
    }

    void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = (ImagingListener)this.getRenderingHints().get(JAI.KEY_IMAGING_LISTENER);
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

    static {
        nodeEventNames = new HashSet();
        nodeEventNames.add("protocolname");
        nodeEventNames.add("servername");
        nodeEventNames.add("protocolandservername");
        nodeEventNames.add("operationname");
        nodeEventNames.add("operationregistry");
        nodeEventNames.add("parameterblock");
        nodeEventNames.add("sources");
        nodeEventNames.add("parameters");
        nodeEventNames.add("renderinghints");
    }
}

