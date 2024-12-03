/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.RemoteRIF;
import javax.media.jai.remote.RemoteRenderedImage;

public interface RemoteCRIF
extends RemoteRIF {
    public RenderContext mapRenderContext(String var1, String var2, int var3, RenderContext var4, ParameterBlock var5, RenderableImage var6) throws RemoteImagingException;

    public RemoteRenderedImage create(String var1, String var2, RenderContext var3, ParameterBlock var4) throws RemoteImagingException;

    public Rectangle2D getBounds2D(String var1, String var2, ParameterBlock var3) throws RemoteImagingException;

    public Object getProperty(String var1, String var2, ParameterBlock var3, String var4) throws RemoteImagingException;

    public String[] getPropertyNames(String var1, String var2) throws RemoteImagingException;

    public boolean isDynamic(String var1, String var2) throws RemoteImagingException;
}

