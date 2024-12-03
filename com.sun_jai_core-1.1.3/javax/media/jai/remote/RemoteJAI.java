/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileCache;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.RemoteDescriptor;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.RemoteRIF;
import javax.media.jai.remote.RemoteRenderableOp;
import javax.media.jai.remote.RemoteRenderedOp;
import javax.media.jai.util.CaselessStringKey;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public class RemoteJAI {
    protected String serverName;
    protected String protocolName;
    private OperationRegistry operationRegistry = JAI.getDefaultInstance().getOperationRegistry();
    public static final int DEFAULT_RETRY_INTERVAL = 1000;
    public static final int DEFAULT_NUM_RETRIES = 5;
    private int retryInterval = 1000;
    private int numRetries = 5;
    private transient TileCache cache = JAI.getDefaultInstance().getTileCache();
    private RenderingHints renderingHints;
    private NegotiableCapabilitySet preferences = null;
    private static NegotiableCapabilitySet negotiated;
    private NegotiableCapabilitySet serverCapabilities = null;
    private NegotiableCapabilitySet clientCapabilities = null;
    private Hashtable odHash = null;
    private OperationDescriptor[] descriptors = null;
    private static MessageFormat formatter;
    static /* synthetic */ Class class$java$awt$image$RenderedImage;
    static /* synthetic */ Class class$java$awt$image$renderable$RenderableImage;
    static /* synthetic */ Class class$javax$media$jai$remote$RemoteDescriptor;

    public RemoteJAI(String protocolName, String serverName) {
        this(protocolName, serverName, null, null);
    }

    public RemoteJAI(String protocolName, String serverName, OperationRegistry registry, TileCache tileCache) {
        if (protocolName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        formatter = new MessageFormat("");
        formatter.setLocale(Locale.getDefault());
        this.protocolName = protocolName;
        this.serverName = serverName;
        if (registry != null) {
            this.operationRegistry = registry;
        }
        if (tileCache != null) {
            this.cache = tileCache;
        }
        this.renderingHints = new RenderingHints(null);
        this.renderingHints.put(JAI.KEY_OPERATION_REGISTRY, this.operationRegistry);
        this.renderingHints.put(JAI.KEY_TILE_CACHE, this.cache);
        this.renderingHints.put(JAI.KEY_RETRY_INTERVAL, new Integer(this.retryInterval));
        this.renderingHints.put(JAI.KEY_NUM_RETRIES, new Integer(this.numRetries));
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getProtocolName() {
        return this.protocolName;
    }

    public void setRetryInterval(int retryInterval) {
        if (retryInterval < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
        }
        this.retryInterval = retryInterval;
        this.renderingHints.put(JAI.KEY_RETRY_INTERVAL, new Integer(retryInterval));
    }

    public int getRetryInterval() {
        return this.retryInterval;
    }

    public void setNumRetries(int numRetries) {
        if (numRetries < 0) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
        }
        this.numRetries = numRetries;
        this.renderingHints.put(JAI.KEY_NUM_RETRIES, new Integer(numRetries));
    }

    public int getNumRetries() {
        return this.numRetries;
    }

    public OperationRegistry getOperationRegistry() {
        return this.operationRegistry;
    }

    public void setOperationRegistry(OperationRegistry operationRegistry) {
        if (operationRegistry == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI4"));
        }
        this.operationRegistry = operationRegistry;
        this.renderingHints.put(JAI.KEY_OPERATION_REGISTRY, operationRegistry);
    }

    public void setTileCache(TileCache tileCache) {
        if (tileCache == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI5"));
        }
        this.cache = tileCache;
        this.renderingHints.put(JAI.KEY_TILE_CACHE, this.cache);
    }

    public TileCache getTileCache() {
        return this.cache;
    }

    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    public void setRenderingHints(RenderingHints hints) {
        if (hints == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI6"));
        }
        this.renderingHints = hints;
    }

    public void clearRenderingHints() {
        this.renderingHints = new RenderingHints(null);
    }

    public Object getRenderingHint(RenderingHints.Key key) {
        if (key == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI7"));
        }
        return this.renderingHints.get(key);
    }

    public void setRenderingHint(RenderingHints.Key key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI7"));
        }
        if (value == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI8"));
        }
        try {
            this.renderingHints.put(key, value);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    public void removeRenderingHint(RenderingHints.Key key) {
        this.renderingHints.remove(key);
    }

    public RemoteRenderedOp create(String opName, ParameterBlock args, RenderingHints hints) {
        RenderingHints mergedHints;
        if (opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI9"));
        }
        if (args == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI10"));
        }
        this.getServerSupportedOperationList();
        OperationDescriptor odesc = (OperationDescriptor)this.odHash.get(new CaselessStringKey(opName));
        if (odesc == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI11"));
        }
        if (!odesc.isModeSupported("rendered")) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI12"));
        }
        if (!(class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = RemoteJAI.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage).isAssignableFrom(odesc.getDestClass("rendered"))) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI13"));
        }
        StringBuffer msg = new StringBuffer();
        if (!odesc.validateArguments("rendered", args = (ParameterBlock)args.clone(), msg)) {
            throw new IllegalArgumentException(msg.toString());
        }
        if (hints == null) {
            mergedHints = this.renderingHints;
        } else if (this.renderingHints.isEmpty()) {
            mergedHints = hints;
        } else {
            mergedHints = new RenderingHints(this.renderingHints);
            mergedHints.add(hints);
        }
        RemoteRenderedOp op = new RemoteRenderedOp(this.operationRegistry, this.protocolName, this.serverName, opName, args, mergedHints);
        if (odesc.isImmediate()) {
            PlanarImage im = null;
            im = op.getRendering();
            if (im == null) {
                return null;
            }
        }
        return op;
    }

    public RemoteRenderableOp createRenderable(String opName, ParameterBlock args) {
        if (opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI9"));
        }
        if (args == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI10"));
        }
        this.getServerSupportedOperationList();
        OperationDescriptor odesc = (OperationDescriptor)this.odHash.get(new CaselessStringKey(opName));
        if (odesc == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI11"));
        }
        if (!odesc.isModeSupported("renderable")) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI14"));
        }
        if (!(class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = RemoteJAI.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage).isAssignableFrom(odesc.getDestClass("renderable"))) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI15"));
        }
        StringBuffer msg = new StringBuffer();
        if (!odesc.validateArguments("renderable", args = (ParameterBlock)args.clone(), msg)) {
            throw new IllegalArgumentException(msg.toString());
        }
        RemoteRenderableOp op = new RemoteRenderableOp(this.operationRegistry, this.protocolName, this.serverName, opName, args);
        op.setRenderingHints(this.renderingHints);
        return op;
    }

    public void setNegotiationPreferences(NegotiableCapabilitySet preferences) {
        this.preferences = preferences;
        if (preferences == null) {
            this.renderingHints.remove(JAI.KEY_NEGOTIATION_PREFERENCES);
        } else {
            this.renderingHints.put(JAI.KEY_NEGOTIATION_PREFERENCES, preferences);
        }
        negotiated = null;
        this.getNegotiatedValues();
    }

    public NegotiableCapabilitySet getNegotiatedValues() throws RemoteImagingException {
        if (negotiated == null) {
            if (this.serverCapabilities == null) {
                this.serverCapabilities = this.getServerCapabilities();
            }
            if (this.clientCapabilities == null) {
                this.clientCapabilities = this.getClientCapabilities();
            }
            negotiated = RemoteJAI.negotiate(this.preferences, this.serverCapabilities, this.clientCapabilities);
        }
        return negotiated;
    }

    public NegotiableCapability getNegotiatedValues(String category) throws RemoteImagingException {
        if (negotiated == null) {
            if (this.serverCapabilities == null) {
                this.serverCapabilities = this.getServerCapabilities();
            }
            if (this.clientCapabilities == null) {
                this.clientCapabilities = this.getClientCapabilities();
            }
            return RemoteJAI.negotiate(this.preferences, this.serverCapabilities, this.clientCapabilities, category);
        }
        return negotiated.getNegotiatedValue(category);
    }

    public static NegotiableCapabilitySet negotiate(NegotiableCapabilitySet preferences, NegotiableCapabilitySet serverCapabilities, NegotiableCapabilitySet clientCapabilities) {
        if (serverCapabilities == null || clientCapabilities == null) {
            return null;
        }
        if (serverCapabilities != null && serverCapabilities.isPreference()) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI20"));
        }
        if (clientCapabilities != null && clientCapabilities.isPreference()) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI21"));
        }
        if (preferences == null) {
            return serverCapabilities.negotiate(clientCapabilities);
        }
        if (!preferences.isPreference()) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI19"));
        }
        NegotiableCapabilitySet clientServerCap = serverCapabilities.negotiate(clientCapabilities);
        if (clientServerCap == null) {
            return null;
        }
        return clientServerCap.negotiate(preferences);
    }

    public static NegotiableCapability negotiate(NegotiableCapabilitySet preferences, NegotiableCapabilitySet serverCapabilities, NegotiableCapabilitySet clientCapabilities, String category) {
        NegotiableCapability result;
        if (serverCapabilities == null || clientCapabilities == null) {
            return null;
        }
        if (serverCapabilities != null && serverCapabilities.isPreference()) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI20"));
        }
        if (clientCapabilities != null && clientCapabilities.isPreference()) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI21"));
        }
        if (preferences != null && !preferences.isPreference()) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI19"));
        }
        if (category == null) {
            throw new IllegalArgumentException(JaiI18N.getString("RemoteJAI26"));
        }
        if (preferences == null || preferences.isEmpty()) {
            return serverCapabilities.getNegotiatedValue(clientCapabilities, category);
        }
        List prefList = preferences.get(category);
        List serverList = serverCapabilities.get(category);
        List clientList = clientCapabilities.get(category);
        Iterator p = prefList.iterator();
        NegotiableCapability pref = null;
        pref = !p.hasNext() ? null : (NegotiableCapability)p.next();
        Vector<NegotiableCapability> results = new Vector<NegotiableCapability>();
        Iterator s = serverList.iterator();
        while (s.hasNext()) {
            NegotiableCapability server = (NegotiableCapability)s.next();
            Iterator c = clientList.iterator();
            while (c.hasNext()) {
                NegotiableCapability client = (NegotiableCapability)c.next();
                result = server.negotiate(client);
                if (result == null) continue;
                results.add(result);
                if (pref != null) {
                    result = result.negotiate(pref);
                }
                if (result == null) continue;
                return result;
            }
        }
        while (p.hasNext()) {
            pref = (NegotiableCapability)p.next();
            for (int r = 0; r < results.size(); ++r) {
                result = pref.negotiate((NegotiableCapability)results.elementAt(r));
                if (result == null) continue;
                return result;
            }
        }
        return null;
    }

    public NegotiableCapabilitySet getServerCapabilities() throws RemoteImagingException {
        if (this.serverCapabilities == null) {
            RemoteDescriptor descriptor = (RemoteDescriptor)this.operationRegistry.getDescriptor(class$javax$media$jai$remote$RemoteDescriptor == null ? (class$javax$media$jai$remote$RemoteDescriptor = RemoteJAI.class$("javax.media.jai.remote.RemoteDescriptor")) : class$javax$media$jai$remote$RemoteDescriptor, this.protocolName);
            if (descriptor == null) {
                Object[] msgArg0 = new Object[]{new String(this.protocolName)};
                formatter.applyPattern(JaiI18N.getString("RemoteJAI16"));
                throw new RuntimeException(formatter.format(msgArg0));
            }
            RemoteImagingException rieSave = null;
            int count = 0;
            while (count++ < this.numRetries) {
                try {
                    this.serverCapabilities = descriptor.getServerCapabilities(this.serverName);
                    break;
                }
                catch (RemoteImagingException rie) {
                    System.err.println(JaiI18N.getString("RemoteJAI24"));
                    rieSave = rie;
                    try {
                        Thread.sleep(this.retryInterval);
                    }
                    catch (InterruptedException ie) {
                        this.sendExceptionToListener(JaiI18N.getString("Generic5"), new ImagingException(JaiI18N.getString("Generic5"), ie));
                    }
                }
            }
            if (this.serverCapabilities == null && count > this.numRetries) {
                this.sendExceptionToListener(JaiI18N.getString("RemoteJAI18"), rieSave);
            }
        }
        return this.serverCapabilities;
    }

    public NegotiableCapabilitySet getClientCapabilities() {
        if (this.clientCapabilities == null) {
            RemoteRIF rrif = (RemoteRIF)this.operationRegistry.getFactory("remoteRendered", this.protocolName);
            if (rrif == null) {
                rrif = (RemoteRIF)this.operationRegistry.getFactory("remoteRenderable", this.protocolName);
            }
            if (rrif == null) {
                Object[] msgArg0 = new Object[]{new String(this.protocolName)};
                formatter.applyPattern(JaiI18N.getString("RemoteJAI17"));
                throw new RuntimeException(formatter.format(msgArg0));
            }
            this.clientCapabilities = rrif.getClientCapabilities();
        }
        return this.clientCapabilities;
    }

    public OperationDescriptor[] getServerSupportedOperationList() throws RemoteImagingException {
        if (this.descriptors == null) {
            RemoteDescriptor descriptor = (RemoteDescriptor)this.operationRegistry.getDescriptor(class$javax$media$jai$remote$RemoteDescriptor == null ? (class$javax$media$jai$remote$RemoteDescriptor = RemoteJAI.class$("javax.media.jai.remote.RemoteDescriptor")) : class$javax$media$jai$remote$RemoteDescriptor, this.protocolName);
            if (descriptor == null) {
                Object[] msgArg0 = new Object[]{new String(this.protocolName)};
                formatter.applyPattern(JaiI18N.getString("RemoteJAI16"));
                throw new RuntimeException(formatter.format(msgArg0));
            }
            RemoteImagingException rieSave = null;
            int count = 0;
            while (count++ < this.numRetries) {
                try {
                    this.descriptors = descriptor.getServerSupportedOperationList(this.serverName);
                    break;
                }
                catch (RemoteImagingException rie) {
                    System.err.println(JaiI18N.getString("RemoteJAI25"));
                    rieSave = rie;
                    try {
                        Thread.sleep(this.retryInterval);
                    }
                    catch (InterruptedException ie) {
                        this.sendExceptionToListener(JaiI18N.getString("Generic5"), new ImagingException(JaiI18N.getString("Generic5"), ie));
                    }
                }
            }
            if (this.descriptors == null && count > this.numRetries) {
                this.sendExceptionToListener(JaiI18N.getString("RemoteJAI23"), rieSave);
            }
            this.odHash = new Hashtable();
            for (int i = 0; i < this.descriptors.length; ++i) {
                this.odHash.put(new CaselessStringKey(this.descriptors[i].getName()), this.descriptors[i]);
            }
        }
        return this.descriptors;
    }

    void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
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

