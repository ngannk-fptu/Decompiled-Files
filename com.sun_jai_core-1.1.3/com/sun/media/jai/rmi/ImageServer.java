/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.SerializableRenderableImage;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.SerializableState;

public interface ImageServer
extends Remote {
    public Long getRemoteID() throws RemoteException;

    public void dispose(Long var1) throws RemoteException;

    public void incrementRefCount(Long var1) throws RemoteException;

    public Object getProperty(Long var1, String var2) throws RemoteException;

    public String[] getPropertyNames(Long var1) throws RemoteException;

    public String[] getPropertyNames(String var1) throws RemoteException;

    public SerializableState getColorModel(Long var1) throws RemoteException;

    public SerializableState getSampleModel(Long var1) throws RemoteException;

    public int getWidth(Long var1) throws RemoteException;

    public int getHeight(Long var1) throws RemoteException;

    public int getMinX(Long var1) throws RemoteException;

    public int getMinY(Long var1) throws RemoteException;

    public int getNumXTiles(Long var1) throws RemoteException;

    public int getNumYTiles(Long var1) throws RemoteException;

    public int getMinTileX(Long var1) throws RemoteException;

    public int getMinTileY(Long var1) throws RemoteException;

    public int getTileWidth(Long var1) throws RemoteException;

    public int getTileHeight(Long var1) throws RemoteException;

    public int getTileGridXOffset(Long var1) throws RemoteException;

    public int getTileGridYOffset(Long var1) throws RemoteException;

    public SerializableState getTile(Long var1, int var2, int var3) throws RemoteException;

    public byte[] getCompressedTile(Long var1, int var2, int var3) throws RemoteException;

    public SerializableState getData(Long var1) throws RemoteException;

    public SerializableState getData(Long var1, Rectangle var2) throws RemoteException;

    public SerializableState copyData(Long var1, Rectangle var2) throws RemoteException;

    public void createRenderedOp(Long var1, String var2, ParameterBlock var3, SerializableState var4) throws RemoteException;

    public boolean getRendering(Long var1) throws RemoteException;

    public RenderedOp getNode(Long var1) throws RemoteException;

    public void setRenderedSource(Long var1, RenderedImage var2, int var3) throws RemoteException;

    public void setRenderedSource(Long var1, RenderedOp var2, int var3) throws RemoteException;

    public void setRenderedSource(Long var1, Long var2, int var3) throws RemoteException;

    public void setRenderedSource(Long var1, Long var2, String var3, String var4, int var5) throws RemoteException;

    public float getRenderableMinX(Long var1) throws RemoteException;

    public float getRenderableMinY(Long var1) throws RemoteException;

    public float getRenderableWidth(Long var1) throws RemoteException;

    public float getRenderableHeight(Long var1) throws RemoteException;

    public RenderedImage createScaledRendering(Long var1, int var2, int var3, SerializableState var4) throws RemoteException;

    public RenderedImage createDefaultRendering(Long var1) throws RemoteException;

    public RenderedImage createRendering(Long var1, SerializableState var2) throws RemoteException;

    public void createRenderableOp(Long var1, String var2, ParameterBlock var3) throws RemoteException;

    public Long getRendering(Long var1, SerializableState var2) throws RemoteException;

    public void setRenderableSource(Long var1, Long var2, int var3) throws RemoteException;

    public void setRenderableSource(Long var1, Long var2, String var3, String var4, int var5) throws RemoteException;

    public void setRenderableRMIServerProxyAsSource(Long var1, Long var2, String var3, String var4, int var5) throws RemoteException;

    public void setRenderableSource(Long var1, RenderableOp var2, int var3) throws RemoteException;

    public void setRenderableSource(Long var1, SerializableRenderableImage var2, int var3) throws RemoteException;

    public void setRenderableSource(Long var1, RenderedImage var2, int var3) throws RemoteException;

    public SerializableState mapRenderContext(int var1, Long var2, String var3, SerializableState var4) throws RemoteException;

    public SerializableState getBounds2D(Long var1, String var2) throws RemoteException;

    public boolean isDynamic(String var1) throws RemoteException;

    public boolean isDynamic(Long var1) throws RemoteException;

    public String[] getServerSupportedOperationNames() throws RemoteException;

    public List getOperationDescriptors() throws RemoteException;

    public SerializableState getInvalidRegion(Long var1, ParameterBlock var2, SerializableState var3, ParameterBlock var4, SerializableState var5) throws RemoteException;

    public Rectangle mapSourceRect(Long var1, Rectangle var2, int var3) throws RemoteException;

    public Rectangle mapDestRect(Long var1, Rectangle var2, int var3) throws RemoteException;

    public Long handleEvent(Long var1, String var2, Object var3, Object var4) throws RemoteException;

    public Long handleEvent(Long var1, int var2, SerializableState var3, Object var4) throws RemoteException;

    public NegotiableCapabilitySet getServerCapabilities() throws RemoteException;

    public void setServerNegotiatedValues(Long var1, NegotiableCapabilitySet var2) throws RemoteException;
}

