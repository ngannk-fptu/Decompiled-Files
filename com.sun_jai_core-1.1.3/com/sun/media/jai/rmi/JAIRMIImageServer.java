/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.remote.JAIServerConfigurationSpi;
import com.sun.media.jai.rmi.ImageServer;
import com.sun.media.jai.rmi.JAIRMIUtil;
import com.sun.media.jai.rmi.JaiI18N;
import com.sun.media.jai.rmi.RMIImageImpl;
import com.sun.media.jai.rmi.RMIServerProxy;
import com.sun.media.jai.rmi.RenderableRMIServerProxy;
import com.sun.media.jai.rmi.SerializableRenderableImage;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.Service;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.CollectionImage;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationRegistry;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertySource;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.RenderingChangeEvent;
import javax.media.jai.registry.CRIFRegistry;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.SerializableRenderedImage;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoder;
import javax.media.jai.tilecodec.TileEncoderFactory;
import javax.media.jai.util.ImagingListener;

public class JAIRMIImageServer
extends UnicastRemoteObject
implements ImageServer {
    private boolean DEBUG = true;
    public static final Object NULL_PROPERTY = RMIImageImpl.NULL_PROPERTY;
    private static long idCounter = 0L;
    private static Hashtable nodes = new Hashtable();
    private static Hashtable negotiated = new Hashtable();
    private static Hashtable refCount = new Hashtable();
    static /* synthetic */ Class class$javax$media$jai$tilecodec$TileEncoderFactory;
    static /* synthetic */ Class class$javax$media$jai$OperationDescriptor;
    static /* synthetic */ Class class$com$sun$media$jai$remote$JAIServerConfigurationSpi;
    static /* synthetic */ Class class$com$sun$media$jai$rmi$JAIRMIImageServer;

    private static PlanarImage getSource(Long id) throws RemoteException {
        Object obj;
        block3: {
            block2: {
                obj = null;
                if (nodes == null) break block2;
                Object v = nodes.get(id);
                obj = v;
                if (v != null) break block3;
            }
            throw new RemoteException(JaiI18N.getString("RMIImageImpl2"));
        }
        return obj;
    }

    private static PropertySource getPropertySource(Long id) throws RemoteException {
        Object obj = nodes.get(id);
        return (PropertySource)obj;
    }

    public JAIRMIImageServer(int serverport) throws RemoteException {
        super(serverport);
    }

    public synchronized Long getRemoteID() throws RemoteException {
        return new Long(++idCounter);
    }

    public synchronized void dispose(Long id) throws RemoteException {
        int count = (Integer)refCount.get(id);
        if (count == 1) {
            if (nodes != null) {
                nodes.remove(id);
                negotiated.remove(id);
            }
            refCount.remove(id);
        } else {
            if (--count == 0) {
                refCount.remove(id);
            }
            refCount.put(id, new Integer(count));
        }
    }

    public void incrementRefCount(Long id) throws RemoteException {
        Integer iCount = (Integer)refCount.get(id);
        int count = 0;
        if (iCount != null) {
            count = iCount;
        }
        refCount.put(id, new Integer(++count));
    }

    public Object getProperty(Long id, String name) throws RemoteException {
        PropertySource ps = JAIRMIImageServer.getPropertySource(id);
        Object property = ps.getProperty(name);
        if (property == null || property.equals(Image.UndefinedProperty)) {
            property = NULL_PROPERTY;
        }
        return property;
    }

    public String[] getPropertyNames(Long id) throws RemoteException {
        PropertySource ps = JAIRMIImageServer.getPropertySource(id);
        return ps.getPropertyNames();
    }

    public String[] getPropertyNames(String opName) throws RemoteException {
        return CRIFRegistry.get(null, opName).getPropertyNames();
    }

    public int getMinX(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getMinX();
    }

    public int getMaxX(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getMaxX();
    }

    public int getMinY(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getMinY();
    }

    public int getMaxY(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getMaxY();
    }

    public int getWidth(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getWidth();
    }

    public int getHeight(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getHeight();
    }

    public int getTileWidth(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getTileWidth();
    }

    public int getTileHeight(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getTileHeight();
    }

    public int getTileGridXOffset(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getTileGridXOffset();
    }

    public int getTileGridYOffset(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getTileGridYOffset();
    }

    public int getMinTileX(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getMinTileX();
    }

    public int getNumXTiles(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getNumXTiles();
    }

    public int getMinTileY(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getMinTileY();
    }

    public int getNumYTiles(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getNumYTiles();
    }

    public int getMaxTileX(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getMaxTileX();
    }

    public int getMaxTileY(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getMaxTileY();
    }

    public SerializableState getSampleModel(Long id) throws RemoteException {
        return SerializerFactory.getState(JAIRMIImageServer.getSource(id).getSampleModel(), null);
    }

    public SerializableState getColorModel(Long id) throws RemoteException {
        return SerializerFactory.getState(JAIRMIImageServer.getSource(id).getColorModel(), null);
    }

    public Rectangle getBounds(Long id) throws RemoteException {
        return JAIRMIImageServer.getSource(id).getBounds();
    }

    public SerializableState getTile(Long id, int tileX, int tileY) throws RemoteException {
        Raster r = JAIRMIImageServer.getSource(id).getTile(tileX, tileY);
        return SerializerFactory.getState(r, null);
    }

    public byte[] getCompressedTile(Long id, int x, int y) throws RemoteException {
        TileCodecParameterList tcpl = null;
        TileEncoderFactory tef = null;
        NegotiableCapability codecCap = null;
        if (negotiated != null) {
            codecCap = ((NegotiableCapabilitySet)negotiated.get(id)).getNegotiatedValue("tileCodec");
        }
        if (codecCap != null) {
            String[] paramNames;
            String category = codecCap.getCategory();
            String capabilityName = codecCap.getCapabilityName();
            List generators = codecCap.getGenerators();
            Iterator i = generators.iterator();
            while (i.hasNext()) {
                Class factory = (Class)i.next();
                if (tef != null || !(class$javax$media$jai$tilecodec$TileEncoderFactory == null ? JAIRMIImageServer.class$("javax.media.jai.tilecodec.TileEncoderFactory") : class$javax$media$jai$tilecodec$TileEncoderFactory).isAssignableFrom(factory)) continue;
                try {
                    tef = (TileEncoderFactory)factory.newInstance();
                }
                catch (InstantiationException ie) {
                    throw new RuntimeException(ie.getMessage());
                }
                catch (IllegalAccessException iae) {
                    throw new RuntimeException(iae.getMessage());
                }
            }
            if (tef == null) {
                throw new RuntimeException(JaiI18N.getString("JAIRMIImageServer0"));
            }
            TileCodecDescriptor tcd = (TileCodecDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor("tileEncoder", capabilityName);
            if (!tcd.includesSampleModelInfo() || !tcd.includesLocationInfo()) {
                throw new RuntimeException(JaiI18N.getString("JAIRMIImageServer1"));
            }
            ParameterListDescriptor pld = tcd.getParameterListDescriptor("tileEncoder");
            tcpl = new TileCodecParameterList(capabilityName, new String[]{"tileEncoder"}, pld);
            if (pld != null && (paramNames = pld.getParamNames()) != null) {
                for (int i2 = 0; i2 < paramNames.length; ++i2) {
                    Object currValue;
                    String currParam = paramNames[i2];
                    try {
                        currValue = codecCap.getNegotiatedValue(currParam);
                    }
                    catch (IllegalArgumentException iae) {
                        continue;
                    }
                    tcpl.setParameter(currParam, currValue);
                }
            }
            Raster r = JAIRMIImageServer.getSource(id).getTile(x, y);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            TileEncoder encoder = tef.createEncoder(stream, tcpl, r.getSampleModel());
            try {
                encoder.encode(r);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe.getMessage());
            }
            return stream.toByteArray();
        }
        throw new RuntimeException(JaiI18N.getString("JAIRMIImageServer2"));
    }

    public SerializableState getData(Long id) throws RemoteException {
        return SerializerFactory.getState(JAIRMIImageServer.getSource(id).getData(), null);
    }

    public SerializableState getData(Long id, Rectangle bounds) throws RemoteException {
        if (bounds == null) {
            return this.getData(id);
        }
        bounds = bounds.intersection(this.getBounds(id));
        return SerializerFactory.getState(JAIRMIImageServer.getSource(id).getData(bounds), null);
    }

    public SerializableState copyData(Long id, Rectangle bounds) throws RemoteException {
        return this.getData(id, bounds);
    }

    public void createRenderedOp(Long id, String opName, ParameterBlock pb, SerializableState hints) throws RemoteException {
        RenderingHints rh = (RenderingHints)hints.getObject();
        JAIRMIUtil.checkServerParameters(pb, nodes);
        RenderedOp node = new RenderedOp(opName, pb, rh);
        node.removeSinks();
        nodes.put(id, node);
    }

    public boolean getRendering(Long id) throws RemoteException {
        RenderedOp op = this.getNode(id);
        return op.getRendering() != null;
    }

    public RenderedOp getNode(Long id) throws RemoteException {
        return (RenderedOp)nodes.get(id);
    }

    public synchronized void setRenderedSource(Long id, RenderedImage source, int index) throws RemoteException {
        PlanarImage pi = PlanarImage.wrapRenderedImage(source);
        Object obj = nodes.get(id);
        if (obj instanceof RenderedOp) {
            RenderedOp op = (RenderedOp)obj;
            op.setSource(pi, index);
            ((PlanarImage)op.getSourceObject(index)).removeSinks();
        } else if (obj instanceof RenderableOp) {
            ((RenderableOp)obj).setSource(pi, index);
        }
    }

    public synchronized void setRenderedSource(Long id, RenderedOp source, int index) throws RemoteException {
        Object obj = nodes.get(id);
        if (obj instanceof RenderedOp) {
            RenderedOp op = (RenderedOp)obj;
            op.setSource(source.getRendering(), index);
            ((PlanarImage)op.getSourceObject(index)).removeSinks();
        } else if (obj instanceof RenderableOp) {
            ((RenderableOp)obj).setSource(source.getRendering(), index);
        }
    }

    public synchronized void setRenderedSource(Long id, Long sourceId, int index) throws RemoteException {
        Object obj = nodes.get(id);
        if (obj instanceof RenderedOp) {
            RenderedOp op = (RenderedOp)obj;
            op.setSource(nodes.get(sourceId), index);
            ((PlanarImage)nodes.get(sourceId)).removeSinks();
        } else if (obj instanceof RenderableOp) {
            ((RenderableOp)obj).setSource(nodes.get(sourceId), index);
        }
    }

    public synchronized void setRenderedSource(Long id, Long sourceId, String serverName, String opName, int index) throws RemoteException {
        Object obj = nodes.get(id);
        if (obj instanceof RenderedOp) {
            RenderedOp node = (RenderedOp)obj;
            node.setSource(new RMIServerProxy(serverName + "::" + sourceId, opName, null), index);
            ((PlanarImage)node.getSourceObject(index)).removeSinks();
        } else if (obj instanceof RenderableOp) {
            ((RenderableOp)obj).setSource(new RMIServerProxy(serverName + "::" + sourceId, opName, null), index);
        }
    }

    public float getRenderableMinX(Long id) throws RemoteException {
        RenderableImage ri = (RenderableImage)nodes.get(id);
        return ri.getMinX();
    }

    public float getRenderableMinY(Long id) throws RemoteException {
        RenderableImage ri = (RenderableImage)nodes.get(id);
        return ri.getMinY();
    }

    public float getRenderableWidth(Long id) throws RemoteException {
        RenderableImage ri = (RenderableImage)nodes.get(id);
        return ri.getWidth();
    }

    public float getRenderableHeight(Long id) throws RemoteException {
        RenderableImage ri = (RenderableImage)nodes.get(id);
        return ri.getHeight();
    }

    public RenderedImage createScaledRendering(Long id, int w, int h, SerializableState hintsState) throws RemoteException {
        RenderingHints hints;
        RenderableImage ri = (RenderableImage)nodes.get(id);
        RenderedImage rendering = ri.createScaledRendering(w, h, hints = (RenderingHints)hintsState.getObject());
        if (rendering instanceof Serializable) {
            return rendering;
        }
        return new SerializableRenderedImage(rendering);
    }

    public RenderedImage createDefaultRendering(Long id) throws RemoteException {
        RenderableImage ri = (RenderableImage)nodes.get(id);
        RenderedImage rendering = ri.createDefaultRendering();
        if (rendering instanceof Serializable) {
            return rendering;
        }
        return new SerializableRenderedImage(rendering);
    }

    public RenderedImage createRendering(Long id, SerializableState renderContextState) throws RemoteException {
        RenderContext renderContext;
        RenderableImage ri = (RenderableImage)nodes.get(id);
        RenderedImage rendering = ri.createRendering(renderContext = (RenderContext)renderContextState.getObject());
        if (rendering instanceof Serializable) {
            return rendering;
        }
        return new SerializableRenderedImage(rendering);
    }

    public synchronized void createRenderableOp(Long id, String opName, ParameterBlock pb) throws RemoteException {
        RenderableOp node = new RenderableOp(opName, pb);
        nodes.put(id, node);
    }

    public synchronized Long getRendering(Long id, SerializableState rcs) throws RemoteException {
        RenderableOp op = (RenderableOp)nodes.get(id);
        PlanarImage pi = PlanarImage.wrapRenderedImage(op.createRendering((RenderContext)rcs.getObject()));
        Long renderingID = this.getRemoteID();
        nodes.put(renderingID, pi);
        this.setServerNegotiatedValues(renderingID, (NegotiableCapabilitySet)negotiated.get(id));
        return renderingID;
    }

    public synchronized void setRenderableSource(Long id, Long sourceId, int index) throws RemoteException {
        RenderableOp node = (RenderableOp)nodes.get(id);
        Object obj = nodes.get(sourceId);
        if (obj instanceof RenderableOp) {
            node.setSource((RenderableOp)obj, index);
        } else if (obj instanceof RenderedImage) {
            node.setSource(PlanarImage.wrapRenderedImage((RenderedImage)obj), index);
        }
    }

    public synchronized void setRenderableSource(Long id, Long sourceId, String serverName, String opName, int index) throws RemoteException {
        RenderableOp node = (RenderableOp)nodes.get(id);
        node.setSource(new RMIServerProxy(serverName + "::" + sourceId, opName, null), index);
    }

    public synchronized void setRenderableRMIServerProxyAsSource(Long id, Long sourceId, String serverName, String opName, int index) throws RemoteException {
        RenderableOp node = (RenderableOp)nodes.get(id);
        node.setSource(new RenderableRMIServerProxy(serverName, opName, null, sourceId), index);
    }

    public synchronized void setRenderableSource(Long id, RenderableOp source, int index) throws RemoteException {
        RenderableOp op = (RenderableOp)nodes.get(id);
        op.setSource(source, index);
    }

    public synchronized void setRenderableSource(Long id, SerializableRenderableImage s, int index) throws RemoteException {
        RenderableOp op = (RenderableOp)nodes.get(id);
        op.setSource(s, index);
    }

    public synchronized void setRenderableSource(Long id, RenderedImage source, int index) throws RemoteException {
        PlanarImage pi = PlanarImage.wrapRenderedImage(source);
        RenderableOp op = (RenderableOp)nodes.get(id);
        op.setSource(pi, index);
    }

    public SerializableState mapRenderContext(int id, Long nodeId, String operationName, SerializableState rcs) throws RemoteException {
        RenderableOp rop = (RenderableOp)nodes.get(nodeId);
        ContextualRenderedImageFactory crif = CRIFRegistry.get(rop.getRegistry(), operationName);
        if (crif == null) {
            throw new RuntimeException(JaiI18N.getString("JAIRMIImageServer3"));
        }
        RenderContext rc = crif.mapRenderContext(id, (RenderContext)rcs.getObject(), rop.getParameterBlock(), rop);
        return SerializerFactory.getState(rc, null);
    }

    public SerializableState getBounds2D(Long nodeId, String operationName) throws RemoteException {
        RenderableOp rop = (RenderableOp)nodes.get(nodeId);
        ContextualRenderedImageFactory crif = CRIFRegistry.get(rop.getRegistry(), operationName);
        if (crif == null) {
            throw new RuntimeException(JaiI18N.getString("JAIRMIImageServer3"));
        }
        Rectangle2D r2D = crif.getBounds2D(rop.getParameterBlock());
        return SerializerFactory.getState(r2D, null);
    }

    public boolean isDynamic(String opName) throws RemoteException {
        return CRIFRegistry.get(null, opName).isDynamic();
    }

    public boolean isDynamic(Long id) throws RemoteException {
        RenderableImage node = (RenderableImage)nodes.get(id);
        return node.isDynamic();
    }

    public String[] getServerSupportedOperationNames() throws RemoteException {
        return JAI.getDefaultInstance().getOperationRegistry().getDescriptorNames(class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = JAIRMIImageServer.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor);
    }

    public List getOperationDescriptors() throws RemoteException {
        return JAI.getDefaultInstance().getOperationRegistry().getDescriptors(class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = JAIRMIImageServer.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor);
    }

    public synchronized SerializableState getInvalidRegion(Long id, ParameterBlock oldParamBlock, SerializableState oldRHints, ParameterBlock newParamBlock, SerializableState newRHints) throws RemoteException {
        RenderingHints oldHints = (RenderingHints)oldRHints.getObject();
        RenderingHints newHints = (RenderingHints)newRHints.getObject();
        RenderedOp op = (RenderedOp)nodes.get(id);
        OperationDescriptor od = (OperationDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor("rendered", op.getOperationName());
        boolean samePBs = false;
        if (oldParamBlock == newParamBlock) {
            samePBs = true;
        }
        Vector<Object> oldSources = oldParamBlock.getSources();
        oldParamBlock.removeSources();
        Vector oldReplacedSources = JAIRMIUtil.replaceIdWithSources(oldSources, nodes, op.getOperationName(), op.getRenderingHints());
        oldParamBlock.setSources(oldReplacedSources);
        if (samePBs) {
            newParamBlock = oldParamBlock;
        } else {
            Vector<Object> newSources = newParamBlock.getSources();
            newParamBlock.removeSources();
            Vector newReplacedSources = JAIRMIUtil.replaceIdWithSources(newSources, nodes, op.getOperationName(), op.getRenderingHints());
            newParamBlock.setSources(newReplacedSources);
        }
        Object invalidRegion = od.getInvalidRegion("rendered", oldParamBlock, oldHints, newParamBlock, newHints, op);
        SerializableState shapeState = SerializerFactory.getState((Shape)invalidRegion, null);
        return shapeState;
    }

    public Rectangle mapSourceRect(Long id, Rectangle sourceRect, int sourceIndex) throws RemoteException {
        RenderedOp op = (RenderedOp)nodes.get(id);
        OpImage rendering = (OpImage)op.getRendering();
        return rendering.mapSourceRect(sourceRect, sourceIndex);
    }

    public Rectangle mapDestRect(Long id, Rectangle destRect, int sourceIndex) throws RemoteException {
        RenderedOp op = (RenderedOp)nodes.get(id);
        OpImage rendering = (OpImage)op.getRendering();
        return rendering.mapDestRect(destRect, sourceIndex);
    }

    public synchronized Long handleEvent(Long renderedOpID, String propName, Object oldValue, Object newValue) throws RemoteException {
        RenderedOp op = (RenderedOp)nodes.get(renderedOpID);
        PlanarImage rendering = op.getRendering();
        Long id = this.getRemoteID();
        nodes.put(id, rendering);
        this.setServerNegotiatedValues(id, (NegotiableCapabilitySet)negotiated.get(renderedOpID));
        if (propName.equals("operationname")) {
            op.setOperationName((String)newValue);
        } else if (propName.equals("parameterblock")) {
            ParameterBlock newPB = (ParameterBlock)newValue;
            Vector<Object> newSrcs = newPB.getSources();
            newPB.removeSources();
            JAIRMIUtil.checkServerParameters(newPB, nodes);
            Vector replacedSources = JAIRMIUtil.replaceIdWithSources(newSrcs, nodes, op.getOperationName(), op.getRenderingHints());
            newPB.setSources(replacedSources);
            op.setParameterBlock(newPB);
            Vector<Object> newSources = newPB.getSources();
            if (newSources != null && newSources.size() > 0) {
                Iterator<Object> it = newSources.iterator();
                while (it.hasNext()) {
                    Object src = it.next();
                    if (src instanceof PlanarImage) {
                        ((PlanarImage)src).removeSinks();
                        continue;
                    }
                    if (!(src instanceof CollectionImage)) continue;
                    ((CollectionImage)src).removeSinks();
                }
            }
        } else if (propName.equals("sources")) {
            Vector replacedSources = JAIRMIUtil.replaceIdWithSources((Vector)newValue, nodes, op.getOperationName(), op.getRenderingHints());
            op.setSources(replacedSources);
            if (replacedSources != null && replacedSources.size() > 0) {
                Iterator it = replacedSources.iterator();
                while (it.hasNext()) {
                    Object src = it.next();
                    if (src instanceof PlanarImage) {
                        ((PlanarImage)src).removeSinks();
                        continue;
                    }
                    if (!(src instanceof CollectionImage)) continue;
                    ((CollectionImage)src).removeSinks();
                }
            }
        } else if (propName.equals("parameters")) {
            Vector parameters = (Vector)newValue;
            JAIRMIUtil.checkServerParameters(parameters, nodes);
            op.setParameters(parameters);
        } else if (propName.equals("renderinghints")) {
            SerializableState newState = (SerializableState)newValue;
            op.setRenderingHints((RenderingHints)newState.getObject());
        }
        return id;
    }

    public synchronized Long handleEvent(Long renderedOpID, int srcIndex, SerializableState srcInvalidRegion, Object oldRendering) throws RemoteException {
        RenderedOp op = (RenderedOp)nodes.get(renderedOpID);
        PlanarImage rendering = op.getRendering();
        Long id = this.getRemoteID();
        nodes.put(id, rendering);
        this.setServerNegotiatedValues(id, (NegotiableCapabilitySet)negotiated.get(renderedOpID));
        PlanarImage oldSrcRendering = null;
        PlanarImage newSrcRendering = null;
        String serverNodeDesc = null;
        Object src = null;
        if (oldRendering instanceof String) {
            boolean diffServer;
            serverNodeDesc = (String)oldRendering;
            int index = serverNodeDesc.indexOf("::");
            boolean bl = diffServer = index != -1;
            oldSrcRendering = diffServer ? new RMIServerProxy(serverNodeDesc, op.getOperationName(), op.getRenderingHints()) : ((src = nodes.get(Long.valueOf(serverNodeDesc))) instanceof RenderedOp ? ((RenderedOp)src).getRendering() : PlanarImage.wrapRenderedImage(src));
        } else {
            oldSrcRendering = PlanarImage.wrapRenderedImage((RenderedImage)oldRendering);
        }
        PlanarImage srcObj = op.getSource(srcIndex);
        if (srcObj instanceof RenderedOp) {
            newSrcRendering = ((RenderedOp)srcObj).getRendering();
        } else if (srcObj instanceof RenderedImage) {
            newSrcRendering = PlanarImage.wrapRenderedImage(srcObj);
        }
        Shape invalidRegion = (Shape)srcInvalidRegion.getObject();
        RenderingChangeEvent rcEvent = new RenderingChangeEvent((RenderedOp)op.getSource(srcIndex), oldSrcRendering, newSrcRendering, invalidRegion);
        op.propertyChange(rcEvent);
        return id;
    }

    public synchronized NegotiableCapabilitySet getServerCapabilities() {
        OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
        String modeName = "tileEncoder";
        String[] descriptorNames = registry.getDescriptorNames(modeName);
        TileEncoderFactory tef = null;
        NegotiableCapabilitySet capabilities = new NegotiableCapabilitySet(false);
        for (int i = 0; i < descriptorNames.length; ++i) {
            Iterator it = registry.getFactoryIterator(modeName, descriptorNames[i]);
            while (it.hasNext()) {
                tef = (TileEncoderFactory)it.next();
                capabilities.add(tef.getEncodeCapability());
            }
        }
        return capabilities;
    }

    public void setServerNegotiatedValues(Long id, NegotiableCapabilitySet negotiatedValues) throws RemoteException {
        if (negotiatedValues != null) {
            negotiated.put(id, negotiatedValues);
        } else {
            negotiated.remove(id);
        }
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        Iterator spiIter = Service.providers(class$com$sun$media$jai$remote$JAIServerConfigurationSpi == null ? (class$com$sun$media$jai$remote$JAIServerConfigurationSpi = JAIRMIImageServer.class$("com.sun.media.jai.remote.JAIServerConfigurationSpi")) : class$com$sun$media$jai$remote$JAIServerConfigurationSpi);
        JAI jai = JAI.getDefaultInstance();
        while (spiIter.hasNext()) {
            JAIServerConfigurationSpi serverSpi = (JAIServerConfigurationSpi)spiIter.next();
            serverSpi.updateServer(jai);
        }
        String host = null;
        int rmiRegistryPort = 1099;
        int serverport = 0;
        if (args.length != 0) {
            for (int i = 0; i < args.length; ++i) {
                int ySize;
                int xSize;
                int xpos;
                String value;
                if (args[i].equalsIgnoreCase("-help")) {
                    System.out.println("Usage: java -Djava.rmi.server.codebase=file:$JAI/lib/jai.jar \\");
                    System.out.println("-Djava.rmi.server.useCodebaseOnly=false \\");
                    System.out.println("-Djava.security.policy=file:`pwd`/policy \\");
                    System.out.println("com.sun.media.jai.rmi.JAIRMIImageServer \\");
                    System.out.println("\nwhere options are:");
                    System.out.println("\t-host <string> The server name or server IP address");
                    System.out.println("\t-port <integer> The port that rmiregistry is running on");
                    System.out.println("\t-rmiRegistryPort <integer> Same as -port option");
                    System.out.println("\t-serverPort <integer> The port that the server should listen on, for connections from clients");
                    System.out.println("\t-cacheMemCapacity <long> The memory capacity in bytes.");
                    System.out.println("\t-cacheMemThreshold <float> The memory threshold, which is the fractional amount of cache memory to retain during tile removal");
                    System.out.println("\t-disableDefaultCache Disable use of default tile cache. Tiles are not stored.");
                    System.out.println("\t-schedulerParallelism <integer> The degree of parallelism of the default TileScheduler");
                    System.out.println("\t-schedulerPrefetchParallelism <integer> The degree of parallelism of the default TileScheduler for tile prefetching");
                    System.out.println("\t-schedulerPriority <integer> The priority of tile scheduling for the default TileScheduler");
                    System.out.println("\t-schedulerPrefetchPriority <integer> The priority of tile prefetch scheduling for the default TileScheduler");
                    System.out.println("\t-defaultTileSize <integer>x<integer> The default tile dimensions in the form <xSize>x<ySize>");
                    System.out.println("\t-defaultRenderingSize <integer>x<integer> The default size to render a RenderableImage to, in the form <xSize>x<ySize>");
                    System.out.println("\t-serializeDeepCopy <boolean> Whether a deep copy of the image data should be used when serializing images");
                    System.out.println("\t-tileCodecFormat <string> The default format to be used for tile serialization via TileCodecs");
                    System.out.println("\t-retryInterval <integer> The retry interval value to be used for dealing with network errors during remote imaging");
                    System.out.println("\t-numRetries <integer> The number of retries to be used for dealing with network errors during remote imaging");
                    continue;
                }
                if (args[i].equalsIgnoreCase("-host")) {
                    host = args[++i];
                    continue;
                }
                if (args[i].equalsIgnoreCase("-port") || args[i].equalsIgnoreCase("-rmiRegistryPort")) {
                    rmiRegistryPort = Integer.parseInt(args[++i]);
                    continue;
                }
                if (args[i].equalsIgnoreCase("-serverport")) {
                    serverport = Integer.parseInt(args[++i]);
                    continue;
                }
                if (args[i].equalsIgnoreCase("-cacheMemCapacity")) {
                    jai.getTileCache().setMemoryCapacity(Long.parseLong(args[++i]));
                    continue;
                }
                if (args[i].equalsIgnoreCase("-cacheMemThreshold")) {
                    jai.getTileCache().setMemoryThreshold(Float.parseFloat(args[++i]));
                    continue;
                }
                if (args[i].equalsIgnoreCase("-disableDefaultCache")) {
                    jai.disableDefaultTileCache();
                    continue;
                }
                if (args[i].equalsIgnoreCase("-schedulerParallelism")) {
                    jai.getTileScheduler().setParallelism(Integer.parseInt(args[++i]));
                    continue;
                }
                if (args[i].equalsIgnoreCase("-schedulerPrefetchParallelism")) {
                    jai.getTileScheduler().setPrefetchParallelism(Integer.parseInt(args[++i]));
                    continue;
                }
                if (args[i].equalsIgnoreCase("-schedulerPriority")) {
                    jai.getTileScheduler().setPriority(Integer.parseInt(args[++i]));
                    continue;
                }
                if (args[i].equalsIgnoreCase("-schedulerPrefetchPriority")) {
                    jai.getTileScheduler().setPrefetchPriority(Integer.parseInt(args[++i]));
                    continue;
                }
                if (args[i].equalsIgnoreCase("-defaultTileSize")) {
                    value = args[++i].toLowerCase();
                    xpos = value.indexOf("x");
                    xSize = Integer.parseInt(value.substring(0, xpos));
                    ySize = Integer.parseInt(value.substring(xpos + 1));
                    JAI.setDefaultTileSize(new Dimension(xSize, ySize));
                    continue;
                }
                if (args[i].equalsIgnoreCase("-defaultRenderingSize")) {
                    value = args[++i].toLowerCase();
                    xpos = value.indexOf("x");
                    xSize = Integer.parseInt(value.substring(0, xpos));
                    ySize = Integer.parseInt(value.substring(xpos + 1));
                    JAI.setDefaultRenderingSize(new Dimension(xSize, ySize));
                    continue;
                }
                if (args[i].equalsIgnoreCase("-serializeDeepCopy")) {
                    jai.setRenderingHint(JAI.KEY_SERIALIZE_DEEP_COPY, Boolean.valueOf(args[++i]));
                    continue;
                }
                if (args[i].equalsIgnoreCase("-tileCodecFormat")) {
                    jai.setRenderingHint(JAI.KEY_TILE_CODEC_FORMAT, args[++i]);
                    continue;
                }
                if (args[i].equalsIgnoreCase("-retryInterval")) {
                    jai.setRenderingHint(JAI.KEY_RETRY_INTERVAL, Integer.valueOf(args[++i]));
                    continue;
                }
                if (!args[i].equalsIgnoreCase("-numRetries")) continue;
                jai.setRenderingHint(JAI.KEY_NUM_RETRIES, Integer.valueOf(args[++i]));
            }
        }
        if (host == null) {
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            }
            catch (UnknownHostException e) {
                String message = JaiI18N.getString("RMIImageImpl1");
                JAIRMIImageServer.sendExceptionToListener(message, new RemoteImagingException(message, e));
            }
        }
        System.out.println(JaiI18N.getString("RMIImageImpl3") + " " + host + ":" + rmiRegistryPort);
        try {
            JAIRMIImageServer im = new JAIRMIImageServer(serverport);
            String serverName = new String("rmi://" + host + ":" + rmiRegistryPort + "/" + "JAIRMIRemoteServer1.1");
            System.out.println(JaiI18N.getString("RMIImageImpl4") + " \"" + serverName + "\".");
            Naming.rebind(serverName, im);
            System.out.println(JaiI18N.getString("RMIImageImpl5"));
        }
        catch (Exception e) {
            String message = JaiI18N.getString("RMIImageImpl1");
            JAIRMIImageServer.sendExceptionToListener(message, new RemoteImagingException(message, e));
        }
    }

    private static void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = ImageUtil.getImagingListener((RenderingHints)null);
        listener.errorOccurred(message, new RemoteImagingException(message, e), class$com$sun$media$jai$rmi$JAIRMIImageServer == null ? (class$com$sun$media$jai$rmi$JAIRMIImageServer = JAIRMIImageServer.class$("com.sun.media.jai.rmi.JAIRMIImageServer")) : class$com$sun$media$jai$rmi$JAIRMIImageServer, false);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

