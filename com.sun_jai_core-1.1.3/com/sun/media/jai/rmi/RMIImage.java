/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.ColorModelProxy;
import com.sun.media.jai.rmi.RasterProxy;
import com.sun.media.jai.rmi.RenderContextProxy;
import com.sun.media.jai.rmi.SampleModelProxy;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

public interface RMIImage
extends Remote {
    public static final String RMI_IMAGE_SERVER_NAME = "RemoteImageServer";

    public Long getRemoteID() throws RemoteException;

    public void setSource(Long var1, RenderedImage var2) throws RemoteException;

    public void setSource(Long var1, RenderedOp var2) throws RemoteException;

    public void setSource(Long var1, RenderableOp var2, RenderContextProxy var3) throws RemoteException;

    public void dispose(Long var1) throws RemoteException;

    public Vector getSources(Long var1) throws RemoteException;

    public Object getProperty(Long var1, String var2) throws RemoteException;

    public String[] getPropertyNames(Long var1) throws RemoteException;

    public ColorModelProxy getColorModel(Long var1) throws RemoteException;

    public SampleModelProxy getSampleModel(Long var1) throws RemoteException;

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

    public RasterProxy getTile(Long var1, int var2, int var3) throws RemoteException;

    public RasterProxy getData(Long var1) throws RemoteException;

    public RasterProxy getData(Long var1, Rectangle var2) throws RemoteException;

    public RasterProxy copyData(Long var1, Rectangle var2) throws RemoteException;
}

