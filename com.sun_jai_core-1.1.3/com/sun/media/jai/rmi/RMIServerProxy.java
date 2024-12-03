/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.ImageServer;
import com.sun.media.jai.rmi.JAIRMIImageServer;
import com.sun.media.jai.rmi.JAIRMIUtil;
import com.sun.media.jai.rmi.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.OperationNode;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.RenderingChangeEvent;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.PlanarImageServerProxy;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.RemoteRenderedOp;
import javax.media.jai.remote.SerializableRenderedImage;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.tilecodec.TileCodecDescriptor;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileDecoderFactory;
import javax.media.jai.util.ImagingListener;

public class RMIServerProxy
extends PlanarImageServerProxy {
    private ImageServer remoteImage = null;
    private Long id;
    private Long renderingID = null;
    private boolean preferencesSet;
    private NegotiableCapabilitySet negPref;
    private static final Class NULL_PROPERTY_CLASS = JAIRMIImageServer.NULL_PROPERTY.getClass();
    private ImagingListener listener;
    static /* synthetic */ Class class$javax$media$jai$tilecodec$TileDecoderFactory;

    public RMIServerProxy(String serverName, String opName, RenderingHints hints) {
        super(serverName, "jairmi", opName, null, hints);
        boolean remoteChaining;
        int index = serverName.indexOf("::");
        boolean bl = remoteChaining = index != -1;
        if (!remoteChaining) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteImage1"));
        }
        if (remoteChaining) {
            this.id = Long.valueOf(serverName.substring(index + 2));
            this.serverName = serverName = serverName.substring(0, index);
        }
        this.listener = ImageUtil.getImagingListener(hints);
        this.remoteImage = this.getImageServer(serverName);
        if (this.preferencesSet) {
            super.setNegotiationPreferences(this.negPref);
        }
        try {
            this.remoteImage.incrementRefCount(this.id);
        }
        catch (RemoteException re) {
            System.err.println(JaiI18N.getString("RMIServerProxy2"));
        }
    }

    public RMIServerProxy(String serverName, ParameterBlock pb, String opName, RenderingHints hints) {
        super(serverName, "jairmi", opName, pb, hints);
        boolean remoteChaining;
        int index = serverName.indexOf("::");
        boolean bl = remoteChaining = index != -1;
        if (!remoteChaining) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteImage1"));
        }
        if (remoteChaining) {
            this.id = Long.valueOf(serverName.substring(index + 2));
            this.serverName = serverName = serverName.substring(0, index);
        }
        this.listener = ImageUtil.getImagingListener(hints);
        this.remoteImage = this.getImageServer(serverName);
        if (this.preferencesSet) {
            super.setNegotiationPreferences(this.negPref);
        }
        try {
            this.remoteImage.incrementRefCount(this.id);
        }
        catch (RemoteException re) {
            System.err.println(JaiI18N.getString("RMIServerProxy2"));
        }
    }

    public RMIServerProxy(String serverName, String operationName, ParameterBlock paramBlock, RenderingHints hints) {
        super(serverName, "jairmi", operationName, paramBlock, hints);
        this.listener = ImageUtil.getImagingListener(hints);
        this.remoteImage = this.getImageServer(serverName);
        this.getRMIID();
        if (this.preferencesSet) {
            super.setNegotiationPreferences(this.negPref);
        }
        ParameterBlock newPB = (ParameterBlock)paramBlock.clone();
        newPB.removeSources();
        JAIRMIUtil.checkClientParameters(newPB, serverName);
        try {
            SerializableState rhs = SerializerFactory.getState(hints, null);
            this.remoteImage.createRenderedOp(this.id, operationName, newPB, rhs);
        }
        catch (RemoteException e) {
            String message = JaiI18N.getString("RMIServerProxy5");
            this.listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
        }
        int size = this.getNumSources();
        for (int i = 0; i < size; ++i) {
            String message;
            PlanarImage rop;
            PlanarImage source = this.getSource(i);
            if (source instanceof RMIServerProxy) {
                try {
                    rop = (RMIServerProxy)source;
                    if (((RMIServerProxy)rop).serverName.equalsIgnoreCase(this.serverName)) {
                        this.remoteImage.setRenderedSource(this.id, ((RMIServerProxy)rop).getRMIID(), i);
                        continue;
                    }
                    this.remoteImage.setRenderedSource(this.id, ((RMIServerProxy)rop).getRMIID(), ((RMIServerProxy)rop).serverName, ((RMIServerProxy)rop).operationName, i);
                }
                catch (RemoteException e) {
                    message = JaiI18N.getString("RMIServerProxy6");
                    this.listener.errorOccurred(message, new RemoteImagingException(e), this, false);
                }
                continue;
            }
            if (source instanceof RenderedOp) {
                rop = (RenderedOp)source;
                RenderedImage rendering = ((RenderedOp)rop).getRendering();
                if (!(rendering instanceof Serializable)) {
                    rendering = new SerializableRenderedImage(rendering);
                }
                try {
                    this.remoteImage.setRenderedSource(this.id, rendering, i);
                }
                catch (RemoteException e) {
                    String message2 = JaiI18N.getString("RMIServerProxy6");
                    this.listener.errorOccurred(message2, new RemoteImagingException(message2, e), this, false);
                }
                continue;
            }
            if (!(source instanceof RenderedImage)) continue;
            try {
                if (source instanceof Serializable) {
                    this.remoteImage.setRenderedSource(this.id, source, i);
                    continue;
                }
                this.remoteImage.setRenderedSource(this.id, new SerializableRenderedImage(source), i);
                continue;
            }
            catch (RemoteException e) {
                message = JaiI18N.getString("RMIServerProxy6");
                this.listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
            }
        }
        try {
            this.remoteImage.incrementRefCount(this.id);
        }
        catch (RemoteException re) {
            System.err.println(JaiI18N.getString("RMIServerProxy2"));
        }
    }

    public RMIServerProxy(PlanarImageServerProxy oldRendering, OperationNode node, String newServerName) {
        this(newServerName, node.getOperationName(), node.getParameterBlock(), node.getRenderingHints());
    }

    public RMIServerProxy(PlanarImageServerProxy oldRendering, OperationNode node, PropertyChangeEventJAI event) {
        super(((RemoteRenderedOp)node).getServerName(), "jairmi", node.getOperationName(), node.getParameterBlock(), node.getRenderingHints());
        this.listener = ImageUtil.getImagingListener(node.getRenderingHints());
        this.remoteImage = this.getImageServer(this.serverName);
        RMIServerProxy oldRMISP = null;
        if (oldRendering instanceof RMIServerProxy) {
            oldRMISP = (RMIServerProxy)oldRendering;
        } else {
            System.err.println(JaiI18N.getString("RMIServerProxy3"));
        }
        Long opID = oldRMISP.getRMIID();
        String propName = event.getPropertyName();
        if (event instanceof RenderingChangeEvent) {
            RenderingChangeEvent rce = (RenderingChangeEvent)event;
            int idx = ((RenderedOp)node).getSources().indexOf(rce.getSource());
            PlanarImage oldSrcRendering = (PlanarImage)event.getOldValue();
            Object oldSrc = null;
            String serverNodeDesc = null;
            if (oldSrcRendering instanceof RMIServerProxy) {
                RMIServerProxy oldSrcRMISP = (RMIServerProxy)oldSrcRendering;
                serverNodeDesc = !oldSrcRMISP.getServerName().equalsIgnoreCase(this.serverName) ? oldSrcRMISP.getServerName() + "::" + oldSrcRMISP.getRMIID() : oldSrcRMISP.getRMIID().toString();
                oldSrc = serverNodeDesc;
            } else {
                oldSrc = oldSrcRendering instanceof Serializable ? oldSrcRendering : new SerializableRenderedImage(oldSrcRendering);
            }
            Shape srcInvalidRegion = rce.getInvalidRegion();
            SerializableState shapeState = SerializerFactory.getState(srcInvalidRegion, null);
            Long oldRenderingID = null;
            try {
                oldRenderingID = this.remoteImage.handleEvent(opID, idx, shapeState, oldSrc);
            }
            catch (RemoteException re) {
                String message = JaiI18N.getString("RMIServerProxy7");
                this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            }
            oldRMISP.id = oldRenderingID;
            this.id = opID;
        } else {
            Object oldValue = null;
            Object newValue = null;
            if (propName.equals("operationname")) {
                oldValue = event.getOldValue();
                newValue = event.getNewValue();
            } else if (propName.equals("parameterblock")) {
                ParameterBlock oldPB = (ParameterBlock)event.getOldValue();
                Vector<Object> oldSrcs = oldPB.getSources();
                oldPB.removeSources();
                ParameterBlock newPB = (ParameterBlock)event.getNewValue();
                Vector<Object> newSrcs = newPB.getSources();
                newPB.removeSources();
                JAIRMIUtil.checkClientParameters(oldPB, this.serverName);
                JAIRMIUtil.checkClientParameters(newPB, this.serverName);
                oldPB.setSources(JAIRMIUtil.replaceSourcesWithId(oldSrcs, this.serverName));
                newPB.setSources(JAIRMIUtil.replaceSourcesWithId(newSrcs, this.serverName));
                oldValue = oldPB;
                newValue = newPB;
            } else if (propName.equals("sources")) {
                Vector oldSrcs = (Vector)event.getOldValue();
                Vector newSrcs = (Vector)event.getNewValue();
                oldValue = JAIRMIUtil.replaceSourcesWithId(oldSrcs, this.serverName);
                newValue = JAIRMIUtil.replaceSourcesWithId(newSrcs, this.serverName);
            } else if (propName.equals("parameters")) {
                Vector oldParameters = (Vector)event.getOldValue();
                Vector newParameters = (Vector)event.getNewValue();
                JAIRMIUtil.checkClientParameters(oldParameters, this.serverName);
                JAIRMIUtil.checkClientParameters(newParameters, this.serverName);
                oldValue = oldParameters;
                newValue = newParameters;
            } else if (propName.equals("renderinghints")) {
                RenderingHints oldRH = (RenderingHints)event.getOldValue();
                RenderingHints newRH = (RenderingHints)event.getNewValue();
                oldValue = SerializerFactory.getState(oldRH, null);
                newValue = SerializerFactory.getState(newRH, null);
            } else {
                throw new RemoteImagingException(JaiI18N.getString("RMIServerProxy4"));
            }
            Long oldRenderingID = null;
            try {
                oldRenderingID = this.remoteImage.handleEvent(opID, propName, oldValue, newValue);
                this.remoteImage.incrementRefCount(oldRenderingID);
            }
            catch (RemoteException re) {
                String message = JaiI18N.getString("RMIServerProxy7");
                this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            }
            oldRMISP.id = oldRenderingID;
            this.id = opID;
        }
        if (this.preferencesSet) {
            super.setNegotiationPreferences(this.negPref);
        }
    }

    public RMIServerProxy(String serverName, String operationName, ParameterBlock pb, RenderingHints hints, Long id) {
        super(serverName, "jairmi", operationName, pb, hints);
        this.listener = ImageUtil.getImagingListener(hints);
        this.remoteImage = this.getImageServer(serverName);
        this.id = id;
    }

    public RMIServerProxy(String serverName, String operationName, ParameterBlock paramBlock, RenderContext rc, boolean isRender) {
        super(serverName, "jairmi", operationName, paramBlock, null);
        String message;
        this.listener = ImageUtil.getImagingListener(rc.getRenderingHints());
        this.remoteImage = this.getImageServer(serverName);
        this.getRMIID();
        if (this.preferencesSet) {
            super.setNegotiationPreferences(this.negPref);
        }
        ParameterBlock newPB = (ParameterBlock)paramBlock.clone();
        newPB.removeSources();
        try {
            this.remoteImage.createRenderableOp(this.id, operationName, newPB);
        }
        catch (RemoteException e) {
            String message2 = JaiI18N.getString("RMIServerProxy8");
            this.listener.errorOccurred(message2, new RemoteImagingException(message2, e), this, false);
        }
        int size = this.getNumSources();
        for (int i = 0; i < size; ++i) {
            String message3;
            Vector<Object> sources = paramBlock.getSources();
            Object source = sources.elementAt(i);
            if (source instanceof RMIServerProxy) {
                try {
                    RMIServerProxy rop = (RMIServerProxy)source;
                    if (rop.serverName.equals(this.serverName)) {
                        this.remoteImage.setRenderableSource(this.id, rop.getRMIID(), i);
                        continue;
                    }
                    this.remoteImage.setRenderableSource(this.id, rop.getRMIID(), rop.serverName, rop.operationName, i);
                }
                catch (RemoteException e) {
                    message3 = JaiI18N.getString("RMIServerProxy6");
                    this.listener.errorOccurred(message3, new RemoteImagingException(message3, e), this, false);
                }
                continue;
            }
            if (source instanceof RenderableOp) {
                try {
                    this.remoteImage.setRenderableSource(this.id, (RenderableOp)source, i);
                }
                catch (RemoteException e) {
                    message3 = JaiI18N.getString("RMIServerProxy6");
                    this.listener.errorOccurred(message3, new RemoteImagingException(message3, e), this, false);
                }
                continue;
            }
            if (!(source instanceof RenderedImage)) continue;
            try {
                this.remoteImage.setRenderableSource(this.id, new SerializableRenderedImage((RenderedImage)source), i);
                continue;
            }
            catch (RemoteException e) {
                message3 = JaiI18N.getString("RMIServerProxy6");
                this.listener.errorOccurred(message3, new RemoteImagingException(message3, e), this, false);
            }
        }
        try {
            this.remoteImage.incrementRefCount(this.id);
        }
        catch (RemoteException e) {
            message = JaiI18N.getString("RMIServerProxy9");
            this.listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
        }
        if (isRender) {
            try {
                this.renderingID = this.remoteImage.getRendering(this.id, SerializerFactory.getState(rc, null));
                this.remoteImage.incrementRefCount(this.renderingID);
            }
            catch (RemoteException e) {
                message = JaiI18N.getString("RMIServerProxy10");
                this.listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
            }
        }
    }

    protected synchronized ImageServer getImageServer(String serverName) {
        if (this.remoteImage == null) {
            if (serverName == null) {
                try {
                    serverName = InetAddress.getLocalHost().getHostAddress();
                }
                catch (Exception e) {
                    String message = JaiI18N.getString("RMIServerProxy11");
                    this.listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
                }
            }
            String serviceName = new String("rmi://" + serverName + "/" + "JAIRMIRemoteServer1.1");
            this.remoteImage = null;
            try {
                this.remoteImage = (ImageServer)Naming.lookup(serviceName);
            }
            catch (Exception e) {
                String message = JaiI18N.getString("RMIServerProxy12");
                this.listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
            }
        }
        return this.remoteImage;
    }

    public synchronized Long getRMIID() {
        if (this.id != null) {
            return this.id;
        }
        try {
            this.id = this.remoteImage.getRemoteID();
            return this.id;
        }
        catch (Exception e) {
            String message = JaiI18N.getString("RMIServerProxy13");
            this.listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
            return this.id;
        }
    }

    public Long getRenderingID() {
        return this.renderingID;
    }

    public boolean canBeRendered() {
        boolean cbr = true;
        this.getImageServer(this.serverName);
        try {
            cbr = this.remoteImage.getRendering(this.getRMIID());
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RMIServerProxy10");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
        }
        return cbr;
    }

    protected void finalize() {
        try {
            this.remoteImage.dispose(this.id);
        }
        catch (Exception exception) {
            // empty catch block
        }
        super.dispose();
    }

    public ImageLayout getImageLayout() throws RemoteImagingException {
        ImageLayout layout = new ImageLayout();
        try {
            layout.setMinX(this.remoteImage.getMinX(this.id));
            layout.setMinY(this.remoteImage.getMinY(this.id));
            layout.setWidth(this.remoteImage.getWidth(this.id));
            layout.setHeight(this.remoteImage.getHeight(this.id));
            layout.setTileWidth(this.remoteImage.getTileWidth(this.id));
            layout.setTileHeight(this.remoteImage.getTileHeight(this.id));
            layout.setTileGridXOffset(this.remoteImage.getTileGridXOffset(this.id));
            layout.setTileGridYOffset(this.remoteImage.getTileGridYOffset(this.id));
            SerializableState smState = this.remoteImage.getSampleModel(this.id);
            layout.setSampleModel((SampleModel)smState.getObject());
            SerializableState cmState = this.remoteImage.getColorModel(this.id);
            layout.setColorModel((ColorModel)cmState.getObject());
            return layout;
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RMIServerProxy14");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return null;
        }
    }

    /*
     * Unable to fully structure code
     */
    public Raster computeTile(int tileX, int tileY) throws RemoteImagingException {
        if (tileX < this.getMinTileX() || tileX > this.getMaxTileX() || tileY < this.getMinTileY() || tileY > this.getMaxTileY()) {
            return null;
        }
        codecCap = this.getNegotiatedValue("tileCodec");
        tdf = null;
        tcpl = null;
        if (codecCap != null) {
            category = codecCap.getCategory();
            capabilityName = codecCap.getCapabilityName();
            generators = codecCap.getGenerators();
            i = generators.iterator();
            while (i.hasNext()) {
                factory = (Class)i.next();
                if (tdf != null || !(RMIServerProxy.class$javax$media$jai$tilecodec$TileDecoderFactory == null ? RMIServerProxy.class$("javax.media.jai.tilecodec.TileDecoderFactory") : RMIServerProxy.class$javax$media$jai$tilecodec$TileDecoderFactory).isAssignableFrom(factory)) continue;
                try {
                    tdf = (TileDecoderFactory)factory.newInstance();
                }
                catch (InstantiationException ie) {
                    throw new RemoteImagingException(ImageUtil.getStackTraceString(ie));
                }
                catch (IllegalAccessException iae) {
                    throw new RemoteImagingException(ImageUtil.getStackTraceString(iae));
                }
            }
            if (tdf == null) {
                throw new RemoteImagingException(JaiI18N.getString("RMIServerProxy0"));
            }
            tcd = (TileCodecDescriptor)this.registry.getDescriptor("tileDecoder", capabilityName);
            if (!tcd.includesSampleModelInfo() || !tcd.includesLocationInfo()) {
                throw new RemoteImagingException(JaiI18N.getString("RMIServerProxy1"));
            }
            pld = tcd.getParameterListDescriptor("tileDecoder");
            tcpl = new TileCodecParameterList(capabilityName, new String[]{"tileDecoder"}, pld);
            ** if (pld == null || (paramNames = pld.getParamNames()) == null) goto lbl39
            for (i = 0; i < paramNames.length; ++i) {
                currParam = paramNames[i];
                try {
                    currValue = codecCap.getNegotiatedValue(currParam);
                }
                catch (IllegalArgumentException iae) {
                    continue;
                }
                tcpl.setParameter(currParam, currValue);
lbl-1000:
                // 2 sources

                {
                    continue;
                }
            }
        }
lbl39:
        // 4 sources

        try {
            if (codecCap != null) {
                ctile = this.remoteImage.getCompressedTile(this.id, tileX, tileY);
                stream = new ByteArrayInputStream(ctile);
                decoder = tdf.createDecoder(stream, tcpl);
                try {
                    return decoder.decode();
                }
                catch (IOException ioe) {
                    throw new RemoteImagingException(ImageUtil.getStackTraceString(ioe));
                }
            }
            rp = this.remoteImage.getTile(this.id, tileX, tileY);
            return (Raster)rp.getObject();
        }
        catch (RemoteException e) {
            message = JaiI18N.getString("RMIServerProxy15");
            this.listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
            return null;
        }
    }

    public Object getRemoteProperty(String name) throws RemoteImagingException {
        try {
            Object property = this.remoteImage.getProperty(this.id, name);
            if (NULL_PROPERTY_CLASS.isInstance(property)) {
                property = Image.UndefinedProperty;
            }
            return property;
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RMIServerProxy16");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return Image.UndefinedProperty;
        }
    }

    public String[] getRemotePropertyNames() throws RemoteImagingException {
        try {
            return this.remoteImage.getPropertyNames(this.id);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RMIServerProxy17");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return null;
        }
    }

    public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) throws RemoteImagingException {
        Rectangle dstRect = null;
        try {
            dstRect = this.remoteImage.mapSourceRect(this.id, sourceRect, sourceIndex);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RMIServerProxy18");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
        }
        return dstRect;
    }

    public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) throws RemoteImagingException {
        Rectangle srcRect = null;
        try {
            srcRect = this.remoteImage.mapDestRect(this.id, destRect, sourceIndex);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RMIServerProxy18");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
        }
        return srcRect;
    }

    public void setNegotiationPreferences(NegotiableCapabilitySet preferences) {
        if (this.remoteImage == null) {
            this.negPref = preferences;
            this.preferencesSet = true;
        } else {
            super.setNegotiationPreferences(preferences);
        }
    }

    public synchronized void setServerNegotiatedValues(NegotiableCapabilitySet negotiatedValues) throws RemoteImagingException {
        try {
            this.remoteImage.setServerNegotiatedValues(this.id, negotiatedValues);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RMIServerProxy19");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
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
}

