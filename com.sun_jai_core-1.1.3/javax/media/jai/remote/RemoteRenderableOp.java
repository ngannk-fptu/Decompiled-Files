/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.WritablePropertySource;
import javax.media.jai.registry.RemoteCRIFRegistry;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.RemoteCRIF;
import javax.media.jai.remote.RemoteDescriptor;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.RemoteJAI;
import javax.media.jai.remote.RemoteRIF;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public class RemoteRenderableOp
extends RenderableOp {
    protected String protocolName;
    protected String serverName;
    private transient RemoteCRIF remoteCRIF = null;
    private NegotiableCapabilitySet negotiated = null;
    private transient RenderedImage linkToRemoteOp;
    static /* synthetic */ Class class$javax$media$jai$remote$RemoteDescriptor;

    public RemoteRenderableOp(String protocolName, String serverName, String opName, ParameterBlock pb) {
        this(null, protocolName, serverName, opName, pb);
    }

    public RemoteRenderableOp(OperationRegistry registry, String protocolName, String serverName, String opName, ParameterBlock pb) {
        super(registry, opName, pb);
        if (protocolName == null || opName == null) {
            throw new IllegalArgumentException();
        }
        this.protocolName = protocolName;
        this.serverName = serverName;
    }

    public String getRegistryModeName() {
        return RegistryMode.getMode("remoteRenderable").getName();
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        if (serverName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic2"));
        }
        if (serverName.equalsIgnoreCase(this.serverName)) {
            return;
        }
        String oldServerName = this.serverName;
        this.serverName = serverName;
        this.fireEvent("ServerName", oldServerName, serverName);
        this.nodeSupport.resetPropertyEnvironment(false);
    }

    public String getProtocolName() {
        return this.protocolName;
    }

    public void setProtocolName(String protocolName) {
        if (protocolName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        if (protocolName.equalsIgnoreCase(this.protocolName)) {
            return;
        }
        String oldProtocolName = this.protocolName;
        this.protocolName = protocolName;
        this.fireEvent("ProtocolName", oldProtocolName, protocolName);
        this.nodeSupport.resetPropertyEnvironment(false);
    }

    public void setProtocolAndServerNames(String protocolName, String serverName) {
        if (serverName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic2"));
        }
        if (protocolName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        boolean protocolNotChanged = protocolName.equalsIgnoreCase(this.protocolName);
        boolean serverNotChanged = serverName.equalsIgnoreCase(this.serverName);
        if (protocolNotChanged) {
            if (serverNotChanged) {
                return;
            }
            this.setServerName(serverName);
            return;
        }
        if (serverNotChanged) {
            this.setProtocolName(protocolName);
            return;
        }
        String oldProtocolName = this.protocolName;
        String oldServerName = this.serverName;
        this.protocolName = protocolName;
        this.serverName = serverName;
        this.fireEvent("ProtocolAndServerName", new String[]{oldProtocolName, oldServerName}, new String[]{protocolName, serverName});
        this.nodeSupport.resetPropertyEnvironment(false);
    }

    private void fireEvent(String propName, Object oldVal, Object newVal) {
        if (this.eventManager != null) {
            Object eventSource = this.eventManager.getPropertyChangeEventSource();
            PropertyChangeEventJAI evt = new PropertyChangeEventJAI(eventSource, propName, oldVal, newVal);
            this.eventManager.firePropertyChange(evt);
        }
    }

    public float getWidth() {
        this.findRemoteCRIF();
        Rectangle2D boundingBox = this.remoteCRIF.getBounds2D(this.serverName, this.nodeSupport.getOperationName(), this.nodeSupport.getParameterBlock());
        return (float)boundingBox.getWidth();
    }

    public float getHeight() {
        this.findRemoteCRIF();
        Rectangle2D boundingBox = this.remoteCRIF.getBounds2D(this.serverName, this.nodeSupport.getOperationName(), this.nodeSupport.getParameterBlock());
        return (float)boundingBox.getHeight();
    }

    public float getMinX() {
        this.findRemoteCRIF();
        Rectangle2D boundingBox = this.remoteCRIF.getBounds2D(this.serverName, this.nodeSupport.getOperationName(), this.nodeSupport.getParameterBlock());
        return (float)boundingBox.getX();
    }

    public float getMinY() {
        this.findRemoteCRIF();
        Rectangle2D boundingBox = this.remoteCRIF.getBounds2D(this.serverName, this.nodeSupport.getOperationName(), this.nodeSupport.getParameterBlock());
        return (float)boundingBox.getY();
    }

    public RenderedImage createRendering(RenderContext renderContext) {
        this.findRemoteCRIF();
        ParameterBlock renderedPB = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        RenderContext rcIn = renderContext;
        RenderingHints nodeHints = this.nodeSupport.getRenderingHints();
        if (nodeHints != null) {
            RenderingHints mergedHints;
            RenderingHints hints = renderContext.getRenderingHints();
            if (hints == null) {
                mergedHints = nodeHints;
            } else if (nodeHints == null || nodeHints.isEmpty()) {
                mergedHints = hints;
            } else {
                mergedHints = new RenderingHints(nodeHints);
                mergedHints.add(hints);
            }
            if (mergedHints != hints) {
                rcIn = new RenderContext(renderContext.getTransform(), renderContext.getAreaOfInterest(), mergedHints);
            }
        }
        Vector<Object> sources = this.nodeSupport.getParameterBlock().getSources();
        try {
            String[] propertyNames;
            RenderedImage rendering;
            if (sources != null) {
                Vector<Object> renderedSources = new Vector<Object>();
                for (int i = 0; i < sources.size(); ++i) {
                    RenderedImage rdrdImage = null;
                    Object source = sources.elementAt(i);
                    if (source instanceof RenderableImage) {
                        RenderContext rcOut = this.remoteCRIF.mapRenderContext(this.serverName, this.nodeSupport.getOperationName(), i, renderContext, this.nodeSupport.getParameterBlock(), this);
                        RenderableImage src = (RenderableImage)source;
                        rdrdImage = src.createRendering(rcOut);
                    } else if (source instanceof RenderedOp) {
                        rdrdImage = ((RenderedOp)source).getRendering();
                    } else if (source instanceof RenderedImage) {
                        rdrdImage = (RenderedImage)source;
                    }
                    if (rdrdImage == null) {
                        return null;
                    }
                    renderedSources.addElement(rdrdImage);
                }
                if (renderedSources.size() > 0) {
                    renderedPB.setSources(renderedSources);
                }
            }
            if ((rendering = this.remoteCRIF.create(this.serverName, this.nodeSupport.getOperationName(), renderContext, renderedPB)) instanceof RenderedOp) {
                rendering = ((RenderedOp)rendering).getRendering();
            }
            this.linkToRemoteOp = rendering;
            if (rendering != null && rendering instanceof WritablePropertySource && (propertyNames = this.getPropertyNames()) != null) {
                WritablePropertySource wps = (WritablePropertySource)((Object)rendering);
                for (int j = 0; j < propertyNames.length; ++j) {
                    String name = propertyNames[j];
                    Object value = this.getProperty(name);
                    if (value == null || value == Image.UndefinedProperty) continue;
                    wps.setProperty(name, value);
                }
            }
            return rendering;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private RemoteCRIF findRemoteCRIF() {
        if (this.remoteCRIF == null) {
            this.remoteCRIF = RemoteCRIFRegistry.get(this.nodeSupport.getRegistry(), this.protocolName);
            if (this.remoteCRIF == null) {
                throw new ImagingException(JaiI18N.getString("RemoteRenderableOp0"));
            }
        }
        return this.remoteCRIF;
    }

    public int getRetryInterval() {
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        if (rh == null) {
            return 1000;
        }
        Integer i = (Integer)rh.get(JAI.KEY_RETRY_INTERVAL);
        if (i == null) {
            return 1000;
        }
        return i;
    }

    public void setRetryInterval(int retryInterval) {
        if (retryInterval < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
        }
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        if (rh == null) {
            RenderingHints hints = new RenderingHints(null);
            this.nodeSupport.setRenderingHints(hints);
        }
        this.nodeSupport.getRenderingHints().put(JAI.KEY_RETRY_INTERVAL, new Integer(retryInterval));
    }

    public int getNumRetries() {
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        if (rh == null) {
            return 5;
        }
        Integer i = (Integer)rh.get(JAI.KEY_NUM_RETRIES);
        if (i == null) {
            return 5;
        }
        return i;
    }

    public void setNumRetries(int numRetries) {
        if (numRetries < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
        }
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        if (rh == null) {
            RenderingHints hints = new RenderingHints(null);
            this.nodeSupport.setRenderingHints(hints);
        }
        this.nodeSupport.getRenderingHints().put(JAI.KEY_NUM_RETRIES, new Integer(numRetries));
    }

    public NegotiableCapabilitySet getNegotiationPreferences() {
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        NegotiableCapabilitySet ncs = rh == null ? null : (NegotiableCapabilitySet)rh.get(JAI.KEY_NEGOTIATION_PREFERENCES);
        return ncs;
    }

    public void setNegotiationPreferences(NegotiableCapabilitySet preferences) {
        RenderingHints rh = this.nodeSupport.getRenderingHints();
        if (preferences != null) {
            if (rh == null) {
                RenderingHints hints = new RenderingHints(null);
                this.nodeSupport.setRenderingHints(hints);
            }
            this.nodeSupport.getRenderingHints().put(JAI.KEY_NEGOTIATION_PREFERENCES, preferences);
        } else if (rh != null) {
            rh.remove(JAI.KEY_NEGOTIATION_PREFERENCES);
        }
        this.negotiated = this.negotiate(preferences);
    }

    private NegotiableCapabilitySet negotiate(NegotiableCapabilitySet prefs) {
        OperationRegistry registry = this.nodeSupport.getRegistry();
        NegotiableCapabilitySet serverCap = null;
        RemoteDescriptor descriptor = (RemoteDescriptor)registry.getDescriptor(class$javax$media$jai$remote$RemoteDescriptor == null ? (class$javax$media$jai$remote$RemoteDescriptor = RemoteRenderableOp.class$("javax.media.jai.remote.RemoteDescriptor")) : class$javax$media$jai$remote$RemoteDescriptor, this.protocolName);
        if (descriptor == null) {
            Object[] msgArg0 = new Object[]{new String(this.protocolName)};
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(Locale.getDefault());
            formatter.applyPattern(JaiI18N.getString("RemoteJAI16"));
            throw new RuntimeException(formatter.format(msgArg0));
        }
        int count = 0;
        int numRetries = this.getNumRetries();
        int retryInterval = this.getRetryInterval();
        RemoteImagingException rieSave = null;
        while (count++ < numRetries) {
            try {
                serverCap = descriptor.getServerCapabilities(this.serverName);
                break;
            }
            catch (RemoteImagingException rie) {
                System.err.println(JaiI18N.getString("RemoteJAI24"));
                rieSave = rie;
                try {
                    Thread.sleep(retryInterval);
                }
                catch (InterruptedException ie) {
                    this.sendExceptionToListener(JaiI18N.getString("Generic5"), new ImagingException(JaiI18N.getString("Generic5"), ie));
                }
            }
        }
        if (serverCap == null && count > numRetries) {
            this.sendExceptionToListener(JaiI18N.getString("RemoteJAI18"), rieSave);
        }
        RemoteRIF rrif = (RemoteRIF)registry.getFactory("remoteRenderable", this.protocolName);
        return RemoteJAI.negotiate(prefs, serverCap, rrif.getClientCapabilities());
    }

    public NegotiableCapabilitySet getNegotiatedValues() throws RemoteImagingException {
        return this.negotiated;
    }

    public NegotiableCapability getNegotiatedValues(String category) throws RemoteImagingException {
        if (this.negotiated != null) {
            return this.negotiated.getNegotiatedValue(category);
        }
        return null;
    }

    void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = (ImagingListener)this.getRenderingHints().get(JAI.KEY_IMAGING_LISTENER);
        listener.errorOccurred(message, e, this, false);
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

