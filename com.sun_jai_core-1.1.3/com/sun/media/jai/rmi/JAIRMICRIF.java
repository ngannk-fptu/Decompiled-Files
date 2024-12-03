/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.ImageServer;
import com.sun.media.jai.rmi.JaiI18N;
import com.sun.media.jai.rmi.RMIServerProxy;
import com.sun.media.jai.rmi.RenderableRMIServerProxy;
import com.sun.media.jai.rmi.SerializableRenderableImage;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.OperationNode;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.PlanarImageServerProxy;
import javax.media.jai.remote.RemoteCRIF;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.RemoteRenderableOp;
import javax.media.jai.remote.RemoteRenderedImage;
import javax.media.jai.remote.RemoteRenderedOp;
import javax.media.jai.remote.SerializableRenderedImage;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.tilecodec.TileDecoderFactory;
import javax.media.jai.util.ImagingListener;

public class JAIRMICRIF
implements RemoteCRIF {
    public RenderContext mapRenderContext(String serverName, String operationName, int i, RenderContext renderContext, ParameterBlock paramBlock, RenderableImage image) throws RemoteImagingException {
        RemoteRenderableOp rrop = (RemoteRenderableOp)image;
        RenderableRMIServerProxy rmisp = this.createProxy(rrop);
        SerializableState rcs = SerializerFactory.getState(renderContext, null);
        try {
            SerializableState rcpOut = rmisp.getImageServer(serverName).mapRenderContext(i, rmisp.getRMIID(), operationName, rcs);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("JAIRMICRIF5");
            this.sendExceptionToListener(renderContext, message, re);
        }
        return (RenderContext)rcs.getObject();
    }

    public Rectangle2D getBounds2D(String serverName, String operationName, ParameterBlock paramBlock) throws RemoteImagingException {
        SerializableState bounds = null;
        RemoteRenderableOp original = new RemoteRenderableOp("jairmi", serverName, operationName, paramBlock);
        RenderableRMIServerProxy rmisp = this.createProxy(original);
        try {
            bounds = rmisp.getImageServer(serverName).getBounds2D(rmisp.getRMIID(), operationName);
        }
        catch (RemoteException e) {
            String message = JaiI18N.getString("JAIRMICRIF6");
            this.sendExceptionToListener(null, message, e);
        }
        return (Rectangle2D.Float)bounds.getObject();
    }

    public Object getProperty(String serverName, String operationName, ParameterBlock paramBlock, String name) throws RemoteImagingException {
        ParameterBlock pb = null;
        pb = paramBlock == null ? new ParameterBlock() : (ParameterBlock)paramBlock.clone();
        RemoteRenderableOp original = new RemoteRenderableOp("jairmi", serverName, operationName, paramBlock);
        RenderableRMIServerProxy rmisp = this.createProxy(original);
        try {
            return rmisp.getProperty(name);
        }
        catch (Exception e) {
            String message = JaiI18N.getString("JAIRMICRIF7");
            this.sendExceptionToListener(null, message, new RemoteImagingException(message, e));
            return null;
        }
    }

    public String[] getPropertyNames(String serverName, String operationName) throws RemoteImagingException {
        ImageServer remoteImage = this.getImageServer(serverName);
        try {
            return remoteImage.getPropertyNames(operationName);
        }
        catch (RemoteException e) {
            String message = JaiI18N.getString("JAIRMICRIF8");
            this.sendExceptionToListener(null, message, new RemoteImagingException(message, e));
            return null;
        }
    }

    private ImageServer getImageServer(String serverName) {
        if (serverName == null) {
            try {
                serverName = InetAddress.getLocalHost().getHostAddress();
            }
            catch (UnknownHostException e) {
                String message = JaiI18N.getString("RMIServerProxy11");
                this.sendExceptionToListener(null, message, new RemoteImagingException(message, e));
            }
        }
        String serviceName = new String("rmi://" + serverName + "/" + "JAIRMIRemoteServer1.1");
        try {
            return (ImageServer)Naming.lookup(serviceName);
        }
        catch (NotBoundException e) {
            String message = JaiI18N.getString("RMIServerProxy12");
            this.sendExceptionToListener(null, message, new RemoteImagingException(message, e));
        }
        catch (MalformedURLException e) {
            String message = JaiI18N.getString("RMIServerProxy12");
            this.sendExceptionToListener(null, message, new RemoteImagingException(message, e));
        }
        catch (RemoteException e) {
            String message = JaiI18N.getString("RMIServerProxy12");
            this.sendExceptionToListener(null, message, new RemoteImagingException(message, e));
        }
        return null;
    }

    public boolean isDynamic(String serverName, String operationName) throws RemoteImagingException {
        ImageServer remoteImage = this.getImageServer(serverName);
        try {
            return remoteImage.isDynamic(operationName);
        }
        catch (RemoteException e) {
            String message = JaiI18N.getString("JAIRMICRIF9");
            this.sendExceptionToListener(null, message, new RemoteImagingException(message, e));
            return true;
        }
    }

    public RemoteRenderedImage create(String serverName, String operationName, ParameterBlock paramBlock, RenderingHints hints) throws RemoteImagingException {
        RMIServerProxy rmisp = new RMIServerProxy(serverName, operationName, paramBlock, hints);
        boolean cbr = rmisp.canBeRendered();
        if (!cbr) {
            return null;
        }
        return rmisp;
    }

    public RemoteRenderedImage create(PlanarImageServerProxy oldRendering, OperationNode node, PropertyChangeEventJAI event) throws RemoteImagingException {
        RMIServerProxy rmisp;
        if (!(node instanceof RemoteRenderedOp)) {
            return null;
        }
        String propName = event.getPropertyName();
        if (propName.equals("servername")) {
            rmisp = new RMIServerProxy(oldRendering, node, (String)event.getNewValue());
        } else {
            if (propName.equals("operationregistry") || propName.equals("protocolname") || propName.equals("protocolandservername")) {
                return this.create(((RemoteRenderedOp)node).getServerName(), node.getOperationName(), node.getParameterBlock(), node.getRenderingHints());
            }
            rmisp = new RMIServerProxy(oldRendering, node, event);
        }
        return rmisp;
    }

    public RemoteRenderedImage create(String serverName, String operationName, RenderContext renderContext, ParameterBlock paramBlock) throws RemoteImagingException {
        RMIServerProxy rmisp = new RMIServerProxy(serverName, operationName, paramBlock, renderContext, true);
        Long renderingID = rmisp.getRenderingID();
        return new RMIServerProxy(serverName + "::" + renderingID, paramBlock, operationName, renderContext.getRenderingHints());
    }

    public NegotiableCapabilitySet getClientCapabilities() {
        OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
        String modeName = "tileDecoder";
        String[] descriptorNames = registry.getDescriptorNames(modeName);
        TileDecoderFactory tdf = null;
        NegotiableCapabilitySet capabilities = new NegotiableCapabilitySet(false);
        for (int i = 0; i < descriptorNames.length; ++i) {
            Iterator it = registry.getFactoryIterator(modeName, descriptorNames[i]);
            while (it.hasNext()) {
                tdf = (TileDecoderFactory)it.next();
                capabilities.add(tdf.getDecodeCapability());
            }
        }
        return capabilities;
    }

    private RenderableRMIServerProxy createProxy(RemoteRenderableOp rop) {
        ParameterBlock oldPB = rop.getParameterBlock();
        ParameterBlock newPB = (ParameterBlock)oldPB.clone();
        Vector<Object> sources = oldPB.getSources();
        newPB.removeSources();
        ImageServer remoteImage = this.getImageServer(rop.getServerName());
        ImagingListener listener = ImageUtil.getImagingListener(rop.getRenderingHints());
        Long opID = new Long(0L);
        try {
            opID = remoteImage.getRemoteID();
            remoteImage.createRenderableOp(opID, rop.getOperationName(), newPB);
        }
        catch (RemoteException e) {
            String message = JaiI18N.getString("RMIServerProxy8");
            listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
        }
        if (sources != null) {
            for (int i = 0; i < sources.size(); ++i) {
                Serializable sri;
                Object ri;
                String message;
                Object source = sources.elementAt(i);
                if (source instanceof RemoteRenderedOp) {
                    RMIServerProxy rmisp = (RMIServerProxy)((RemoteRenderedOp)source).getRendering();
                    try {
                        if (rmisp.getServerName().equalsIgnoreCase(rop.getServerName())) {
                            remoteImage.setRenderedSource(opID, rmisp.getRMIID(), i);
                            newPB.setSource(rmisp, i);
                            continue;
                        }
                        remoteImage.setRenderedSource(opID, rmisp.getRMIID(), rmisp.getServerName(), rmisp.getOperationName(), i);
                        newPB.setSource(rmisp, i);
                    }
                    catch (RemoteException e) {
                        message = JaiI18N.getString("RMIServerProxy6");
                        listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
                    }
                    continue;
                }
                if (source instanceof RenderedOp) {
                    ri = ((RenderedOp)source).getRendering();
                    try {
                        sri = new SerializableRenderedImage((RenderedImage)ri);
                        remoteImage.setRenderedSource(opID, (RenderedImage)((Object)sri), i);
                        newPB.setSource(sri, i);
                    }
                    catch (RemoteException e) {
                        message = JaiI18N.getString("RMIServerProxy6");
                        listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
                    }
                    continue;
                }
                if (source instanceof RenderedImage) {
                    ri = (RenderedImage)source;
                    try {
                        sri = new SerializableRenderedImage((RenderedImage)ri);
                        remoteImage.setRenderedSource(opID, (RenderedImage)((Object)sri), i);
                        newPB.setSource(sri, i);
                    }
                    catch (RemoteException e) {
                        message = JaiI18N.getString("RMIServerProxy6");
                        listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
                    }
                    continue;
                }
                if (source instanceof RemoteRenderableOp) {
                    RenderableRMIServerProxy rrmisp = this.createProxy((RemoteRenderableOp)source);
                    try {
                        if (rrmisp.getServerName().equalsIgnoreCase(rop.getServerName())) {
                            remoteImage.setRenderableSource(opID, rrmisp.getRMIID(), i);
                            newPB.setSource(rrmisp, i);
                            continue;
                        }
                        remoteImage.setRenderableRMIServerProxyAsSource(opID, rrmisp.getRMIID(), rrmisp.getServerName(), rrmisp.getOperationName(), i);
                        newPB.setSource(rrmisp, i);
                    }
                    catch (RemoteException e) {
                        message = JaiI18N.getString("RMIServerProxy6");
                        listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
                    }
                    continue;
                }
                if (!(source instanceof RenderableImage)) continue;
                ri = (RenderableImage)source;
                try {
                    sri = new SerializableRenderableImage((RenderableImage)ri);
                    remoteImage.setRenderableSource(opID, (SerializableRenderableImage)sri, i);
                    newPB.setSource(sri, i);
                    continue;
                }
                catch (RemoteException e) {
                    message = JaiI18N.getString("RMIServerProxy6");
                    listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
                }
            }
        }
        RenderableRMIServerProxy finalRmisp = new RenderableRMIServerProxy(rop.getServerName(), rop.getOperationName(), newPB, opID);
        return finalRmisp;
    }

    private void sendExceptionToListener(RenderContext renderContext, String message, Exception e) {
        ImagingListener listener = ImageUtil.getImagingListener(renderContext);
        listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
    }
}

