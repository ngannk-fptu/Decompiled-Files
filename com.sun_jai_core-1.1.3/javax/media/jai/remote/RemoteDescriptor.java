/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationNode;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.RemoteImagingException;

public interface RemoteDescriptor
extends RegistryElementDescriptor {
    public OperationDescriptor[] getServerSupportedOperationList(String var1) throws RemoteImagingException;

    public NegotiableCapabilitySet getServerCapabilities(String var1) throws RemoteImagingException;

    public URL getServerNameDocs();

    public Object getInvalidRegion(String var1, String var2, ParameterBlock var3, RenderingHints var4, String var5, ParameterBlock var6, RenderingHints var7, OperationNode var8) throws RemoteImagingException;
}

