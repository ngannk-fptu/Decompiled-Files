/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.OperationNode;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.PlanarImageServerProxy;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.RemoteRenderedImage;

public interface RemoteRIF {
    public RemoteRenderedImage create(String var1, String var2, ParameterBlock var3, RenderingHints var4) throws RemoteImagingException;

    public RemoteRenderedImage create(PlanarImageServerProxy var1, OperationNode var2, PropertyChangeEventJAI var3) throws RemoteImagingException;

    public NegotiableCapabilitySet getClientCapabilities();
}

