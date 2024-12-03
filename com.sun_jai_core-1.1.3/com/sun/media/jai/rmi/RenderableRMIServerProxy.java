/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.ImageServer;
import com.sun.media.jai.rmi.JAIRMIImageServer;
import com.sun.media.jai.rmi.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.util.ImagingListener;

public class RenderableRMIServerProxy
implements RenderableImage {
    private String serverName;
    private String operationName;
    private ParameterBlock paramBlock;
    private ImageServer imageServer;
    public Long id;
    private static final Class NULL_PROPERTY_CLASS = JAIRMIImageServer.NULL_PROPERTY.getClass();
    private ImagingListener listener;

    public RenderableRMIServerProxy(String serverName, String operationName, ParameterBlock paramBlock, Long opID) {
        this.serverName = serverName;
        this.operationName = operationName;
        this.paramBlock = paramBlock;
        this.imageServer = this.getImageServer(serverName);
        this.id = opID;
        this.listener = ImageUtil.getImagingListener((RenderingHints)null);
    }

    public Vector getSources() {
        return null;
    }

    public Object getProperty(String name) throws RemoteImagingException {
        try {
            Object property = this.imageServer.getProperty(this.id, name);
            if (NULL_PROPERTY_CLASS.isInstance(property)) {
                property = Image.UndefinedProperty;
            }
            return property;
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("JAIRMICRIF7");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return null;
        }
    }

    public String[] getPropertyNames() throws RemoteImagingException {
        try {
            return this.imageServer.getPropertyNames(this.id);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("JAIRMICRIF8");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return null;
        }
    }

    public boolean isDynamic() throws RemoteImagingException {
        try {
            return this.imageServer.isDynamic(this.id);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("JAIRMICRIF9");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return true;
        }
    }

    public float getWidth() throws RemoteImagingException {
        try {
            return this.imageServer.getRenderableWidth(this.id);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RenderableRMIServerProxy0");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return 0.0f;
        }
    }

    public float getHeight() throws RemoteImagingException {
        try {
            return this.imageServer.getRenderableHeight(this.id);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RenderableRMIServerProxy0");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return 0.0f;
        }
    }

    public float getMinX() throws RemoteImagingException {
        try {
            return this.imageServer.getRenderableMinX(this.id);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RenderableRMIServerProxy1");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return 0.0f;
        }
    }

    public float getMinY() throws RemoteImagingException {
        try {
            return this.imageServer.getRenderableMinY(this.id);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RenderableRMIServerProxy1");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return 0.0f;
        }
    }

    public Long getRMIID() {
        return this.id;
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getOperationName() {
        return this.operationName;
    }

    public RenderedImage createScaledRendering(int w, int h, RenderingHints hints) throws RemoteImagingException {
        SerializableState ss = SerializerFactory.getState(hints, null);
        try {
            return this.imageServer.createScaledRendering(this.id, w, h, ss);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RMIServerProxy10");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return null;
        }
    }

    public RenderedImage createDefaultRendering() throws RemoteImagingException {
        try {
            return this.imageServer.createDefaultRendering(this.id);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RMIServerProxy10");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return null;
        }
    }

    public RenderedImage createRendering(RenderContext renderContext) throws RemoteImagingException {
        SerializableState ss = SerializerFactory.getState(renderContext, null);
        try {
            return this.imageServer.createRendering(this.id, ss);
        }
        catch (RemoteException re) {
            String message = JaiI18N.getString("RMIServerProxy10");
            this.listener.errorOccurred(message, new RemoteImagingException(message, re), this, false);
            return null;
        }
    }

    protected synchronized ImageServer getImageServer(String serverName) {
        if (this.imageServer == null) {
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
            this.imageServer = null;
            try {
                this.imageServer = (ImageServer)Naming.lookup(serviceName);
            }
            catch (Exception e) {
                String message = JaiI18N.getString("RMIServerProxy12");
                this.listener.errorOccurred(message, new RemoteImagingException(message, e), this, false);
            }
        }
        return this.imageServer;
    }
}

