/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.awt.image.RenderedImage;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.RemoteImagingException;

public interface RemoteRenderedImage
extends RenderedImage {
    public String getServerName();

    public String getProtocolName();

    public int getRetryInterval();

    public void setRetryInterval(int var1);

    public int getNumRetries();

    public void setNumRetries(int var1);

    public NegotiableCapabilitySet getNegotiationPreferences();

    public void setNegotiationPreferences(NegotiableCapabilitySet var1);

    public NegotiableCapabilitySet getNegotiatedValues() throws RemoteImagingException;

    public NegotiableCapability getNegotiatedValue(String var1) throws RemoteImagingException;

    public void setServerNegotiatedValues(NegotiableCapabilitySet var1) throws RemoteImagingException;
}

