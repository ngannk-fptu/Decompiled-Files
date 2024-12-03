/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.CollectionChangeEvent;
import javax.media.jai.CollectionImage;
import javax.media.jai.CollectionOp;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.OpImage;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationNode;
import javax.media.jai.OperationNodeSupport;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.PropertySource;
import javax.media.jai.PropertySourceChangeEvent;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RegistryMode;
import javax.media.jai.RenderingChangeEvent;
import javax.media.jai.TileCache;
import javax.media.jai.TileComputationListener;
import javax.media.jai.TileRequest;
import javax.media.jai.WritablePropertySourceImpl;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.remote.PlanarImageServerProxy;
import javax.media.jai.util.CaselessStringKey;
import javax.media.jai.util.ImagingListener;

public class RenderedOp
extends PlanarImage
implements OperationNode,
PropertyChangeListener,
Serializable {
    protected OperationNodeSupport nodeSupport;
    protected transient PropertySource thePropertySource;
    protected transient PlanarImage theImage;
    private transient RenderingHints oldHints;
    private static List synthProps;
    private Hashtable synthProperties = null;
    private static Set nodeEventNames;
    private boolean isDisposed = false;
    static /* synthetic */ Class class$javax$media$jai$OperationDescriptor;

    public RenderedOp(OperationRegistry registry, String opName, ParameterBlock pb, RenderingHints hints) {
        super(new ImageLayout(), null, null);
        pb = pb == null ? new ParameterBlock() : (ParameterBlock)pb.clone();
        if (hints != null) {
            hints = (RenderingHints)hints.clone();
        }
        this.nodeSupport = new OperationNodeSupport(this.getRegistryModeName(), opName, registry, pb, hints, this.eventManager);
        this.addPropertyChangeListener("OperationName", this);
        this.addPropertyChangeListener("OperationRegistry", this);
        this.addPropertyChangeListener("ParameterBlock", this);
        this.addPropertyChangeListener("Sources", this);
        this.addPropertyChangeListener("Parameters", this);
        this.addPropertyChangeListener("RenderingHints", this);
        Vector<Object> nodeSources = pb.getSources();
        if (nodeSources != null) {
            Iterator<Object> it = nodeSources.iterator();
            while (it.hasNext()) {
                Object src = it.next();
                if (src instanceof PlanarImage) {
                    ((PlanarImage)src).addSink(this);
                    continue;
                }
                if (!(src instanceof CollectionImage)) continue;
                ((CollectionImage)src).addSink(this);
            }
        }
    }

    public RenderedOp(String opName, ParameterBlock pb, RenderingHints hints) {
        this(null, opName, pb, hints);
    }

    public String getRegistryModeName() {
        return RegistryMode.getMode("rendered").getName();
    }

    public synchronized OperationRegistry getRegistry() {
        return this.nodeSupport.getRegistry();
    }

    public synchronized void setRegistry(OperationRegistry registry) {
        this.nodeSupport.setRegistry(registry);
    }

    public synchronized String getOperationName() {
        return this.nodeSupport.getOperationName();
    }

    public synchronized void setOperationName(String opName) {
        this.nodeSupport.setOperationName(opName);
    }

    public synchronized ParameterBlock getParameterBlock() {
        return (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
    }

    public synchronized void setParameterBlock(ParameterBlock pb) {
        Vector<Object> newSources;
        Vector<Object> nodeSources = this.nodeSupport.getParameterBlock().getSources();
        if (nodeSources != null && nodeSources.size() > 0) {
            Iterator<Object> it = nodeSources.iterator();
            while (it.hasNext()) {
                Object src = it.next();
                if (src instanceof PlanarImage) {
                    ((PlanarImage)src).removeSink(this);
                    continue;
                }
                if (!(src instanceof CollectionImage)) continue;
                ((CollectionImage)src).removeSink(this);
            }
        }
        if (pb != null && (newSources = pb.getSources()) != null && newSources.size() > 0) {
            Iterator<Object> it = newSources.iterator();
            while (it.hasNext()) {
                Object src = it.next();
                if (src instanceof PlanarImage) {
                    ((PlanarImage)src).addSink(this);
                    continue;
                }
                if (!(src instanceof CollectionImage)) continue;
                ((CollectionImage)src).addSink(this);
            }
        }
        this.nodeSupport.setParameterBlock(pb == null ? new ParameterBlock() : (ParameterBlock)pb.clone());
    }

    public RenderingHints getRenderingHints() {
        RenderingHints hints = this.nodeSupport.getRenderingHints();
        return hints == null ? null : (RenderingHints)hints.clone();
    }

    public synchronized void setRenderingHints(RenderingHints hints) {
        if (hints != null) {
            hints = (RenderingHints)hints.clone();
        }
        this.nodeSupport.setRenderingHints(hints);
    }

    public synchronized PlanarImage createInstance() {
        return this.createInstance(false);
    }

    protected synchronized PlanarImage createInstance(boolean isNodeRendered) {
        ParameterBlock pb = new ParameterBlock();
        Vector<Object> parameters = this.nodeSupport.getParameterBlock().getParameters();
        pb.setParameters(ImageUtil.evaluateParameters(parameters));
        int numSources = this.getNumSources();
        for (int i = 0; i < numSources; ++i) {
            Object source = this.getNodeSource(i);
            Object ai = null;
            if (source instanceof RenderedOp) {
                RenderedOp src = (RenderedOp)source;
                ai = isNodeRendered ? src.getRendering() : src.createInstance();
            } else {
                ai = source instanceof CollectionOp ? ((CollectionOp)source).getCollection() : (source instanceof RenderedImage || source instanceof Collection ? source : source);
            }
            pb.addSource(ai);
        }
        RenderedImage rendering = RIFRegistry.create(this.getRegistry(), this.nodeSupport.getOperationName(), pb, this.nodeSupport.getRenderingHints());
        if (rendering == null) {
            throw new RuntimeException(JaiI18N.getString("RenderedOp0"));
        }
        PlanarImage instance = PlanarImage.wrapRenderedImage(rendering);
        this.oldHints = this.nodeSupport.getRenderingHints() == null ? null : (RenderingHints)this.nodeSupport.getRenderingHints().clone();
        return instance;
    }

    protected synchronized void createRendering() {
        if (this.theImage == null) {
            this.theImage = this.createInstance(true);
            this.setImageLayout(new ImageLayout(this.theImage));
            if (this.theImage != null) {
                this.theImage.addTileComputationListener(new TCL(this));
            }
        }
    }

    public PlanarImage getRendering() {
        this.createRendering();
        return this.theImage;
    }

    public PlanarImage getCurrentRendering() {
        return this.theImage;
    }

    public PlanarImage getNewRendering() {
        if (this.theImage == null) {
            return this.getRendering();
        }
        PlanarImage theOldImage = this.theImage;
        this.theImage = null;
        this.createRendering();
        this.resetProperties(true);
        RenderingChangeEvent rcEvent = new RenderingChangeEvent(this, theOldImage, this.theImage, null);
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
        return this.theImage;
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        block47: {
            Object evtSrc = evt.getSource();
            Vector<Object> nodeSources = this.nodeSupport.getParameterBlock().getSources();
            String propName = evt.getPropertyName().toLowerCase(Locale.ENGLISH);
            if (this.theImage == null || (!(evt instanceof PropertyChangeEventJAI) || evtSrc != this || evt instanceof PropertySourceChangeEvent || !nodeEventNames.contains(propName)) && (!(evt instanceof RenderingChangeEvent) && !(evt instanceof CollectionChangeEvent) && (!(evt instanceof PropertyChangeEventJAI) || !(evtSrc instanceof RenderedImage) || !propName.equals("invalidregion")) || !nodeSources.contains(evtSrc))) break block47;
            PlanarImage theOldImage = this.theImage;
            boolean fireEvent = false;
            Shape invalidRegion = null;
            if (evtSrc == this && (propName.equals("operationname") || propName.equals("operationregistry"))) {
                fireEvent = true;
                this.theImage = null;
            } else if (evt instanceof RenderingChangeEvent || evtSrc instanceof RenderedImage && propName.equals("invalidregion")) {
                fireEvent = true;
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
                if (!(this.theImage instanceof OpImage)) {
                    this.theImage = null;
                } else {
                    int i;
                    Rectangle imageBounds;
                    int h;
                    int w;
                    int y;
                    int x;
                    Rectangle tileBounds;
                    OpImage oldOpImage = (OpImage)this.theImage;
                    Rectangle srcInvalidBounds = srcInvalidRegion.getBounds();
                    if (srcInvalidBounds.isEmpty() && !(tileBounds = new Rectangle(x = oldOpImage.tileXToX(oldOpImage.getMinTileX()), y = oldOpImage.tileYToY(oldOpImage.getMinTileY()), w = oldOpImage.getNumXTiles() * oldOpImage.getTileWidth(), h = oldOpImage.getNumYTiles() * oldOpImage.getTileHeight())).equals(imageBounds = oldOpImage.getBounds())) {
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
                        Rectangle dstRegionBounds = oldOpImage.mapSourceRect(srcInvalidBounds, idx);
                        if (dstRegionBounds == null) {
                            dstRegionBounds = oldOpImage.getBounds();
                        }
                        int numIndices = (indices = this.getTileIndices(dstRegionBounds)) != null ? indices.length : 0;
                        GeneralPath gp = null;
                        for (i = 0; i < numIndices; ++i) {
                            Rectangle dstRect;
                            Rectangle srcRect;
                            if (i % 1000 == 0 && gp != null) {
                                gp = new GeneralPath(new Area(gp));
                            }
                            if ((srcRect = oldOpImage.mapDestRect(dstRect = this.getTileRect(indices[i].x, indices[i].y), idx)) == null) {
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
                    this.theImage = null;
                    TileCache oldCache = oldOpImage.getTileCache();
                    if (oldCache != null && (saveAllTiles || validTiles != null)) {
                        this.createRendering();
                        if (this.theImage instanceof OpImage && ((OpImage)this.theImage).getTileCache() != null) {
                            Raster tile;
                            OpImage newOpImage = (OpImage)this.theImage;
                            TileCache newCache = newOpImage.getTileCache();
                            Object tileCacheMetric = newOpImage.getTileCacheMetric();
                            if (saveAllTiles) {
                                Raster[] tiles = oldCache.getTiles(oldOpImage);
                                int numTiles = tiles == null ? 0 : tiles.length;
                                for (int i2 = 0; i2 < numTiles; ++i2) {
                                    tile = tiles[i2];
                                    int tx = newOpImage.XToTileX(tile.getMinX());
                                    int ty = newOpImage.YToTileY(tile.getMinY());
                                    newCache.add(newOpImage, tx, ty, tile, tileCacheMetric);
                                }
                            } else {
                                int numValidTiles = validTiles.size();
                                for (i = 0; i < numValidTiles; ++i) {
                                    Point tileIndex = (Point)validTiles.get(i);
                                    tile = oldCache.getTile(oldOpImage, tileIndex.x, tileIndex.y);
                                    if (tile == null) continue;
                                    newCache.add(newOpImage, tileIndex.x, tileIndex.y, tile, tileCacheMetric);
                                }
                            }
                        }
                    }
                }
            } else {
                ParameterBlock oldPB = null;
                ParameterBlock newPB = null;
                boolean checkInvalidRegion = false;
                if (propName.equals("parameterblock")) {
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
                    fireEvent = true;
                    OperationRegistry registry = this.nodeSupport.getRegistry();
                    OperationDescriptor odesc = (OperationDescriptor)registry.getDescriptor(class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = RenderedOp.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor, this.nodeSupport.getOperationName());
                    invalidRegion = (Shape)odesc.getInvalidRegion("rendered", oldPB = ImageUtil.evaluateParameters(oldPB), this.oldHints, newPB = ImageUtil.evaluateParameters(newPB), this.nodeSupport.getRenderingHints(), this);
                    if (invalidRegion == null || !(this.theImage instanceof OpImage)) {
                        this.theImage = null;
                    } else {
                        OpImage oldRendering = (OpImage)this.theImage;
                        this.theImage = null;
                        this.createRendering();
                        if (this.theImage instanceof OpImage && oldRendering.getTileCache() != null && ((OpImage)this.theImage).getTileCache() != null) {
                            int i;
                            int numTiles;
                            Rectangle imageBounds;
                            int h;
                            int w;
                            int y;
                            int x;
                            Rectangle tileBounds;
                            OpImage newRendering = (OpImage)this.theImage;
                            TileCache oldCache = oldRendering.getTileCache();
                            TileCache newCache = newRendering.getTileCache();
                            Object tileCacheMetric = newRendering.getTileCacheMetric();
                            if (invalidRegion.getBounds().isEmpty() && !(tileBounds = new Rectangle(x = oldRendering.tileXToX(oldRendering.getMinTileX()), y = oldRendering.tileYToY(oldRendering.getMinTileY()), w = oldRendering.getNumXTiles() * oldRendering.getTileWidth(), h = oldRendering.getNumYTiles() * oldRendering.getTileHeight())).equals(imageBounds = oldRendering.getBounds())) {
                                Area tmpArea = new Area(tileBounds);
                                tmpArea.subtract(new Area(imageBounds));
                                invalidRegion = tmpArea;
                            }
                            if (invalidRegion.getBounds().isEmpty()) {
                                Raster[] tiles = oldCache.getTiles(oldRendering);
                                numTiles = tiles == null ? 0 : tiles.length;
                                for (i = 0; i < numTiles; ++i) {
                                    Raster tile = tiles[i];
                                    int tx = newRendering.XToTileX(tile.getMinX());
                                    int ty = newRendering.YToTileY(tile.getMinY());
                                    newCache.add(newRendering, tx, ty, tile, tileCacheMetric);
                                }
                            } else {
                                Raster[] tiles = oldCache.getTiles(oldRendering);
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
            this.createRendering();
            if (fireEvent) {
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

    public synchronized void addNodeSource(Object source) {
        this.addSource(source);
    }

    public synchronized void setNodeSource(Object source, int index) {
        this.setSource(source, index);
    }

    public synchronized Object getNodeSource(int index) {
        return this.nodeSupport.getParameterBlock().getSource(index);
    }

    public synchronized int getNumParameters() {
        return this.nodeSupport.getParameterBlock().getNumParameters();
    }

    public synchronized Vector getParameters() {
        Vector<Object> params = this.nodeSupport.getParameterBlock().getParameters();
        return params == null ? null : (Vector)params.clone();
    }

    public synchronized byte getByteParameter(int index) {
        return this.nodeSupport.getParameterBlock().getByteParameter(index);
    }

    public synchronized char getCharParameter(int index) {
        return this.nodeSupport.getParameterBlock().getCharParameter(index);
    }

    public synchronized short getShortParameter(int index) {
        return this.nodeSupport.getParameterBlock().getShortParameter(index);
    }

    public synchronized int getIntParameter(int index) {
        return this.nodeSupport.getParameterBlock().getIntParameter(index);
    }

    public synchronized long getLongParameter(int index) {
        return this.nodeSupport.getParameterBlock().getLongParameter(index);
    }

    public synchronized float getFloatParameter(int index) {
        return this.nodeSupport.getParameterBlock().getFloatParameter(index);
    }

    public synchronized double getDoubleParameter(int index) {
        return this.nodeSupport.getParameterBlock().getDoubleParameter(index);
    }

    public synchronized Object getObjectParameter(int index) {
        return this.nodeSupport.getParameterBlock().getObjectParameter(index);
    }

    public synchronized void setParameters(Vector parameters) {
        ParameterBlock pb = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        pb.setParameters(parameters);
        this.nodeSupport.setParameterBlock(pb);
    }

    public synchronized void setParameter(byte param, int index) {
        this.setParameter(new Byte(param), index);
    }

    public synchronized void setParameter(char param, int index) {
        this.setParameter(new Character(param), index);
    }

    public synchronized void setParameter(short param, int index) {
        this.setParameter(new Short(param), index);
    }

    public synchronized void setParameter(int param, int index) {
        this.setParameter(new Integer(param), index);
    }

    public synchronized void setParameter(long param, int index) {
        this.setParameter(new Long(param), index);
    }

    public synchronized void setParameter(float param, int index) {
        this.setParameter(new Float(param), index);
    }

    public synchronized void setParameter(double param, int index) {
        this.setParameter(new Double(param), index);
    }

    public synchronized void setParameter(Object param, int index) {
        ParameterBlock pb = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        pb.set(param, index);
        this.nodeSupport.setParameterBlock(pb);
    }

    public synchronized void setRenderingHint(RenderingHints.Key key, Object value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        if (rh == null) {
            this.nodeSupport.setRenderingHints(new RenderingHints(key, value));
        } else {
            rh.put(key, value);
            this.nodeSupport.setRenderingHints(rh);
        }
    }

    public synchronized Object getRenderingHint(RenderingHints.Key key) {
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        return rh == null ? null : rh.get(key);
    }

    private synchronized void createPropertySource() {
        if (this.thePropertySource == null) {
            PropertySource defaultPS = new PropertySource(){

                public String[] getPropertyNames() {
                    return RenderedOp.this.getRendering().getPropertyNames();
                }

                public String[] getPropertyNames(String prefix) {
                    return PropertyUtil.getPropertyNames(this.getPropertyNames(), prefix);
                }

                public Class getPropertyClass(String name) {
                    return null;
                }

                public Object getProperty(String name) {
                    return RenderedOp.this.getRendering().getProperty(name);
                }
            };
            this.thePropertySource = this.nodeSupport.getPropertySource(this, defaultPS);
            this.properties.addProperties(this.thePropertySource);
        }
    }

    protected synchronized void resetProperties(boolean resetPropertySource) {
        this.properties.clearCachedProperties();
        if (resetPropertySource && this.thePropertySource != null) {
            this.synthProperties = null;
            this.properties.removePropertySource(this.thePropertySource);
            this.thePropertySource = null;
        }
    }

    public synchronized String[] getPropertyNames() {
        this.createPropertySource();
        Vector<Object> names = new Vector<Object>(synthProps);
        CaselessStringKey key = new CaselessStringKey("");
        String[] localNames = this.properties.getPropertyNames();
        if (localNames != null) {
            int length = localNames.length;
            for (int i = 0; i < length; ++i) {
                key.setName(localNames[i]);
                if (names.contains(key)) continue;
                names.add(key.clone());
            }
        }
        String[] propertyNames = null;
        int numNames = names.size();
        if (numNames > 0) {
            propertyNames = new String[numNames];
            for (int i = 0; i < numNames; ++i) {
                propertyNames[i] = ((CaselessStringKey)names.get(i)).getName();
            }
        }
        return propertyNames;
    }

    public Class getPropertyClass(String name) {
        this.createPropertySource();
        return this.properties.getPropertyClass(name);
    }

    private synchronized void createSynthProperties() {
        if (this.synthProperties == null) {
            this.synthProperties = new Hashtable();
            this.synthProperties.put(new CaselessStringKey("image_width"), new Integer(this.theImage.getWidth()));
            this.synthProperties.put(new CaselessStringKey("image_height"), new Integer(this.theImage.getHeight()));
            this.synthProperties.put(new CaselessStringKey("image_min_x_coord"), new Integer(this.theImage.getMinX()));
            this.synthProperties.put(new CaselessStringKey("image_min_y_coord"), new Integer(this.theImage.getMinY()));
            if (this.theImage instanceof OpImage) {
                this.synthProperties.put(new CaselessStringKey("tile_cache_key"), this.theImage);
                TileCache tileCache = ((OpImage)this.theImage).getTileCache();
                this.synthProperties.put(new CaselessStringKey("tile_cache"), tileCache == null ? Image.UndefinedProperty : tileCache);
            } else if (this.theImage instanceof PlanarImageServerProxy) {
                this.synthProperties.put(new CaselessStringKey("tile_cache_key"), this.theImage);
                TileCache tileCache = ((PlanarImageServerProxy)this.theImage).getTileCache();
                this.synthProperties.put(new CaselessStringKey("tile_cache"), tileCache == null ? Image.UndefinedProperty : tileCache);
            } else {
                Object tileCacheKey = this.theImage.getProperty("tile_cache_key");
                this.synthProperties.put(new CaselessStringKey("tile_cache_key"), tileCacheKey == null ? Image.UndefinedProperty : tileCacheKey);
                Object tileCache = this.theImage.getProperty("tile_cache");
                this.synthProperties.put(new CaselessStringKey("tile_cache"), tileCache == null ? Image.UndefinedProperty : tileCache);
            }
        }
    }

    public synchronized Object getProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.createPropertySource();
        CaselessStringKey key = new CaselessStringKey(name);
        if (synthProps.contains(key)) {
            this.createRendering();
            this.createSynthProperties();
            return this.synthProperties.get(key);
        }
        Object value = this.properties.getProperty(name);
        if (value == Image.UndefinedProperty) {
            value = this.thePropertySource.getProperty(name);
        }
        if (value != Image.UndefinedProperty && name.equalsIgnoreCase("roi") && value instanceof ROI) {
            ROI roi = (ROI)value;
            Rectangle imageBounds = this.getBounds();
            if (!imageBounds.contains(roi.getBounds())) {
                value = roi.intersect(new ROIShape(imageBounds));
            }
        }
        return value;
    }

    public synchronized void setProperty(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (value == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (synthProps.contains(new CaselessStringKey(name))) {
            throw new RuntimeException(JaiI18N.getString("RenderedOp4"));
        }
        this.createPropertySource();
        super.setProperty(name, value);
    }

    public void removeProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (synthProps.contains(new CaselessStringKey(name))) {
            throw new RuntimeException(JaiI18N.getString("RenderedOp4"));
        }
        this.createPropertySource();
        this.properties.removeProperty(name);
    }

    public synchronized Object getDynamicProperty(String name) {
        this.createPropertySource();
        return this.thePropertySource.getProperty(name);
    }

    public synchronized void addPropertyGenerator(PropertyGenerator pg) {
        this.nodeSupport.addPropertyGenerator(pg);
    }

    public synchronized void copyPropertyFromSource(String propertyName, int sourceIndex) {
        this.nodeSupport.copyPropertyFromSource(propertyName, sourceIndex);
    }

    public synchronized void suppressProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (synthProps.contains(new CaselessStringKey(name))) {
            throw new IllegalArgumentException(JaiI18N.getString("RenderedOp5"));
        }
        this.nodeSupport.suppressProperty(name);
    }

    public int getMinX() {
        this.createRendering();
        return this.theImage.getMinX();
    }

    public int getMinY() {
        this.createRendering();
        return this.theImage.getMinY();
    }

    public int getWidth() {
        this.createRendering();
        return this.theImage.getWidth();
    }

    public int getHeight() {
        this.createRendering();
        return this.theImage.getHeight();
    }

    public int getTileWidth() {
        this.createRendering();
        return this.theImage.getTileWidth();
    }

    public int getTileHeight() {
        this.createRendering();
        return this.theImage.getTileHeight();
    }

    public int getTileGridXOffset() {
        this.createRendering();
        return this.theImage.getTileGridXOffset();
    }

    public int getTileGridYOffset() {
        this.createRendering();
        return this.theImage.getTileGridYOffset();
    }

    public SampleModel getSampleModel() {
        this.createRendering();
        return this.theImage.getSampleModel();
    }

    public ColorModel getColorModel() {
        this.createRendering();
        return this.theImage.getColorModel();
    }

    public Raster getTile(int tileX, int tileY) {
        this.createRendering();
        return this.theImage.getTile(tileX, tileY);
    }

    public Raster getData() {
        this.createRendering();
        return this.theImage.getData();
    }

    public Raster getData(Rectangle rect) {
        this.createRendering();
        return this.theImage.getData(rect);
    }

    public WritableRaster copyData() {
        this.createRendering();
        return this.theImage.copyData();
    }

    public WritableRaster copyData(WritableRaster raster) {
        this.createRendering();
        return this.theImage.copyData(raster);
    }

    public Raster[] getTiles(Point[] tileIndices) {
        this.createRendering();
        return this.theImage.getTiles(tileIndices);
    }

    public TileRequest queueTiles(Point[] tileIndices) {
        this.createRendering();
        return this.theImage.queueTiles(tileIndices);
    }

    public void cancelTiles(TileRequest request, Point[] tileIndices) {
        if (request == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
        }
        this.createRendering();
        this.theImage.cancelTiles(request, tileIndices);
    }

    public void prefetchTiles(Point[] tileIndices) {
        this.createRendering();
        this.theImage.prefetchTiles(tileIndices);
    }

    public synchronized void addSource(PlanarImage source) {
        PlanarImage sourceObject = source;
        this.addSource((Object)sourceObject);
    }

    public synchronized void setSource(PlanarImage source, int index) {
        PlanarImage sourceObject = source;
        this.setSource((Object)sourceObject, index);
    }

    public PlanarImage getSource(int index) {
        return (PlanarImage)this.nodeSupport.getParameterBlock().getSource(index);
    }

    public synchronized boolean removeSource(PlanarImage source) {
        PlanarImage sourceObject = source;
        return this.removeSource((Object)sourceObject);
    }

    public synchronized void addSource(Object source) {
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ParameterBlock pb = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        pb.addSource(source);
        this.nodeSupport.setParameterBlock(pb);
        if (source instanceof PlanarImage) {
            ((PlanarImage)source).addSink(this);
        } else if (source instanceof CollectionImage) {
            ((CollectionImage)source).addSink(this);
        }
    }

    public synchronized void setSource(Object source, int index) {
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ParameterBlock pb = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        if (index < pb.getNumSources()) {
            Object priorSource = pb.getSource(index);
            if (priorSource instanceof PlanarImage) {
                ((PlanarImage)priorSource).removeSink(this);
            } else if (priorSource instanceof CollectionImage) {
                ((CollectionImage)priorSource).removeSink(this);
            }
        }
        pb.setSource(source, index);
        this.nodeSupport.setParameterBlock(pb);
        if (source instanceof PlanarImage) {
            ((PlanarImage)source).addSink(this);
        } else if (source instanceof CollectionImage) {
            ((CollectionImage)source).addSink(this);
        }
    }

    public synchronized boolean removeSource(Object source) {
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ParameterBlock pb = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        Vector<Object> nodeSources = pb.getSources();
        if (nodeSources.contains(source)) {
            if (source instanceof PlanarImage) {
                ((PlanarImage)source).removeSink(this);
            } else if (source instanceof CollectionImage) {
                ((CollectionImage)source).removeSink(this);
            }
        }
        boolean result = nodeSources.remove(source);
        this.nodeSupport.setParameterBlock(pb);
        return result;
    }

    public PlanarImage getSourceImage(int index) {
        return (PlanarImage)this.nodeSupport.getParameterBlock().getSource(index);
    }

    public synchronized Object getSourceObject(int index) {
        return this.nodeSupport.getParameterBlock().getSource(index);
    }

    public int getNumSources() {
        return this.nodeSupport.getParameterBlock().getNumSources();
    }

    public synchronized Vector getSources() {
        Vector<Object> srcs = this.nodeSupport.getParameterBlock().getSources();
        return srcs == null ? null : (Vector)srcs.clone();
    }

    public synchronized void setSources(List sourceList) {
        if (sourceList == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ParameterBlock pb = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        Iterator<Object> it = pb.getSources().iterator();
        while (it.hasNext()) {
            Object priorSource = it.next();
            if (sourceList.contains(priorSource)) continue;
            if (priorSource instanceof PlanarImage) {
                ((PlanarImage)priorSource).removeSink(this);
                continue;
            }
            if (!(priorSource instanceof CollectionImage)) continue;
            ((CollectionImage)priorSource).removeSink(this);
        }
        pb.removeSources();
        int size = sourceList.size();
        for (int i = 0; i < size; ++i) {
            Object src = sourceList.get(i);
            pb.addSource(src);
            if (src instanceof PlanarImage) {
                ((PlanarImage)src).addSink(this);
                continue;
            }
            if (!(src instanceof CollectionImage)) continue;
            ((CollectionImage)src).addSink(this);
        }
        this.nodeSupport.setParameterBlock(pb);
    }

    public synchronized void removeSources() {
        ParameterBlock pb = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        Iterator<Object> it = pb.getSources().iterator();
        while (it.hasNext()) {
            Object priorSource = it.next();
            if (priorSource instanceof PlanarImage) {
                ((PlanarImage)priorSource).removeSink(this);
            } else if (priorSource instanceof CollectionImage) {
                ((CollectionImage)priorSource).removeSink(this);
            }
            it.remove();
        }
        this.nodeSupport.setParameterBlock(pb);
    }

    public synchronized void addSink(PlanarImage sink) {
        if (sink == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        super.addSink(sink);
    }

    public synchronized boolean removeSink(PlanarImage sink) {
        if (sink == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return super.removeSink(sink);
    }

    public void removeSinks() {
        super.removeSinks();
    }

    public boolean addSink(Object sink) {
        if (sink == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return super.addSink(sink);
    }

    public boolean removeSink(Object sink) {
        if (sink == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return super.removeSink(sink);
    }

    public Point2D mapDestPoint(Point2D destPt, int sourceIndex) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
        }
        this.createRendering();
        if (this.theImage != null && this.theImage instanceof OpImage) {
            return ((OpImage)this.theImage).mapDestPoint(destPt, sourceIndex);
        }
        return destPt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
        }
        this.createRendering();
        if (this.theImage != null && this.theImage instanceof OpImage) {
            return ((OpImage)this.theImage).mapSourcePoint(sourcePt, sourceIndex);
        }
        return sourcePt;
    }

    public synchronized void dispose() {
        if (this.isDisposed) {
            return;
        }
        this.isDisposed = true;
        if (this.theImage != null) {
            this.theImage.dispose();
        }
        super.dispose();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.eventManager);
        out.writeObject(this.properties);
    }

    private synchronized void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.eventManager = (PropertyChangeSupportJAI)in.readObject();
        this.properties = (WritablePropertySourceImpl)in.readObject();
        OperationDescriptor odesc = (OperationDescriptor)this.getRegistry().getDescriptor("rendered", this.nodeSupport.getOperationName());
        if (odesc.isImmediate()) {
            this.createRendering();
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
        nodeEventNames = null;
        CaselessStringKey[] propKeys = new CaselessStringKey[]{new CaselessStringKey("image_width"), new CaselessStringKey("image_height"), new CaselessStringKey("image_min_x_coord"), new CaselessStringKey("image_min_y_coord"), new CaselessStringKey("tile_cache"), new CaselessStringKey("tile_cache_key")};
        synthProps = Arrays.asList(propKeys);
        nodeEventNames = new HashSet();
        nodeEventNames.add("operationname");
        nodeEventNames.add("operationregistry");
        nodeEventNames.add("parameterblock");
        nodeEventNames.add("sources");
        nodeEventNames.add("parameters");
        nodeEventNames.add("renderinghints");
    }

    private class TCL
    implements TileComputationListener {
        RenderedOp node;

        private TCL(RenderedOp node) {
            this.node = node;
        }

        public void tileComputed(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY, Raster tile) {
            TileComputationListener[] listeners;
            if (image == RenderedOp.this.theImage && (listeners = RenderedOp.this.getTileComputationListeners()) != null) {
                int numListeners = listeners.length;
                for (int i = 0; i < numListeners; ++i) {
                    listeners[i].tileComputed(this.node, requests, image, tileX, tileY, tile);
                }
            }
        }

        public void tileCancelled(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY) {
            TileComputationListener[] listeners;
            if (image == RenderedOp.this.theImage && (listeners = RenderedOp.this.getTileComputationListeners()) != null) {
                int numListeners = listeners.length;
                for (int i = 0; i < numListeners; ++i) {
                    listeners[i].tileCancelled(this.node, requests, image, tileX, tileY);
                }
            }
        }

        public void tileComputationFailure(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY, Throwable situation) {
            TileComputationListener[] listeners;
            if (image == RenderedOp.this.theImage && (listeners = RenderedOp.this.getTileComputationListeners()) != null) {
                int numListeners = listeners.length;
                for (int i = 0; i < numListeners; ++i) {
                    listeners[i].tileComputationFailure(this.node, requests, image, tileX, tileY, situation);
                }
            }
        }
    }
}

