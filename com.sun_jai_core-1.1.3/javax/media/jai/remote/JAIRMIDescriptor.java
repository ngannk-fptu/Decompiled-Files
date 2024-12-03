/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import com.sun.media.jai.rmi.ImageServer;
import com.sun.media.jai.rmi.JAIRMIUtil;
import com.sun.media.jai.rmi.RMIServerProxy;
import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationNode;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.NegotiableCapabilitySet;
import javax.media.jai.remote.RemoteDescriptorImpl;
import javax.media.jai.remote.RemoteImagingException;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.util.CaselessStringKey;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public class JAIRMIDescriptor
extends RemoteDescriptorImpl {
    public static final String IMAGE_SERVER_BIND_NAME = "JAIRMIRemoteServer1.1";
    private MessageFormat formatter = new MessageFormat("");

    public JAIRMIDescriptor() throws MalformedURLException {
        super("jairmi", new URL("http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/remote/JAIRMIDescriptor.html"));
        this.formatter.setLocale(Locale.getDefault());
    }

    public OperationDescriptor[] getServerSupportedOperationList(String serverName) throws RemoteImagingException {
        List odList = null;
        try {
            odList = this.getImageServer(serverName).getOperationDescriptors();
        }
        catch (Exception e) {
            this.sendExceptionToListener(JaiI18N.getString("JAIRMIDescriptor12"), new RemoteImagingException(JaiI18N.getString("JAIRMIDescriptor12"), e));
        }
        OperationDescriptor[] od = new OperationDescriptor[odList.size()];
        int count = 0;
        Iterator i = odList.iterator();
        while (i.hasNext()) {
            od[count++] = (OperationDescriptor)i.next();
        }
        return od;
    }

    private ImageServer getImageServer(String serverName) {
        if (serverName == null) {
            try {
                serverName = InetAddress.getLocalHost().getHostAddress();
            }
            catch (Exception e) {
                this.sendExceptionToListener(JaiI18N.getString("JAIRMIDescriptor13"), new ImagingException(JaiI18N.getString("JAIRMIDescriptor13"), e));
            }
        }
        String serviceName = new String("rmi://" + serverName + "/" + IMAGE_SERVER_BIND_NAME);
        ImageServer imageServer = null;
        try {
            imageServer = (ImageServer)Naming.lookup(serviceName);
        }
        catch (Exception e) {
            this.sendExceptionToListener(JaiI18N.getString("JAIRMIDescriptor14"), new RemoteImagingException(JaiI18N.getString("JAIRMIDescriptor14"), e));
        }
        return imageServer;
    }

    public NegotiableCapabilitySet getServerCapabilities(String serverName) throws RemoteImagingException {
        NegotiableCapabilitySet serverCapabilities = null;
        try {
            serverCapabilities = this.getImageServer(serverName).getServerCapabilities();
        }
        catch (Exception e) {
            this.sendExceptionToListener(JaiI18N.getString("JAIRMIDescriptor15"), new RemoteImagingException(JaiI18N.getString("JAIRMIDescriptor15"), e));
        }
        return serverCapabilities;
    }

    public Object getInvalidRegion(String registryModeName, String oldServerName, ParameterBlock oldParamBlock, RenderingHints oldHints, String newServerName, ParameterBlock newParamBlock, RenderingHints newHints, OperationNode node) throws RemoteImagingException {
        if (registryModeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAIRMIDescriptor11"));
        }
        String operationName = node.getOperationName();
        OperationDescriptor[] oldDescs = this.getServerSupportedOperationList(oldServerName);
        OperationDescriptor oldOD = this.getOperationDescriptor(oldDescs, operationName);
        if (oldOD == null) {
            throw new IllegalArgumentException(JaiI18N.getString("JAIRMIDescriptor1"));
        }
        int numSources = oldOD.getNumSources();
        ParameterListDescriptor oldPLD = null;
        oldPLD = registryModeName.equalsIgnoreCase("remoteRendered") ? oldOD.getParameterListDescriptor("rendered") : (registryModeName.equalsIgnoreCase("remoteRenderable") ? oldOD.getParameterListDescriptor("renderable") : oldOD.getParameterListDescriptor(registryModeName));
        int numParams = oldPLD.getNumParameters();
        if (oldServerName != newServerName) {
            Hashtable newHash;
            Hashtable oldHash;
            String[] newParamNames;
            OperationDescriptor[] newDescs = this.getServerSupportedOperationList(newServerName);
            OperationDescriptor newOD = this.getOperationDescriptor(newDescs, operationName);
            if (newOD == null) {
                throw new IllegalArgumentException(JaiI18N.getString("JAIRMIDescriptor2"));
            }
            if (numSources != newOD.getNumSources()) {
                throw new IllegalArgumentException(JaiI18N.getString("JAIRMIDescriptor3"));
            }
            ParameterListDescriptor newPLD = newOD.getParameterListDescriptor(registryModeName);
            if (numParams != newPLD.getNumParameters()) {
                throw new IllegalArgumentException(JaiI18N.getString("JAIRMIDescriptor4"));
            }
            String[] oldParamNames = oldPLD.getParamNames();
            if (oldParamNames == null) {
                oldParamNames = new String[]{};
            }
            if ((newParamNames = newPLD.getParamNames()) == null) {
                newParamNames = new String[]{};
            }
            if (!this.containsAll(oldHash = this.hashNames(oldParamNames), newHash = this.hashNames(newParamNames))) {
                throw new IllegalArgumentException(JaiI18N.getString("JAIRMIDescriptor8"));
            }
            Class[] thisParamClasses = oldPLD.getParamClasses();
            Class[] otherParamClasses = newPLD.getParamClasses();
            for (int i = 0; i < oldParamNames.length; ++i) {
                if (thisParamClasses[i] == otherParamClasses[this.getIndex(newHash, oldParamNames[i])]) continue;
                throw new IllegalArgumentException(JaiI18N.getString("JAIRMIDescriptor9"));
            }
            return null;
        }
        if (registryModeName == null || (numSources > 0 || numParams > 0) && (oldParamBlock == null || newParamBlock == null)) {
            throw new IllegalArgumentException(JaiI18N.getString("JAIRMIDescriptor5"));
        }
        if (numSources > 0 && (oldParamBlock.getNumSources() != numSources || newParamBlock.getNumSources() != numSources)) {
            Object[] msgArg0 = new Object[]{operationName, new Integer(numParams)};
            this.formatter.applyPattern(JaiI18N.getString("JAIRMIDescriptor6"));
            throw new IllegalArgumentException(this.formatter.format(msgArg0));
        }
        if (numParams > 0 && (oldParamBlock.getNumParameters() != numParams || newParamBlock.getNumParameters() != numParams)) {
            Object[] msgArg0 = new Object[]{operationName, new Integer(numParams)};
            this.formatter.applyPattern(JaiI18N.getString("JAIRMIDescriptor7"));
            throw new IllegalArgumentException(this.formatter.format(msgArg0));
        }
        RenderedOp op = (RenderedOp)node;
        PlanarImage rendering = op.getRendering();
        Long id = null;
        if (!(rendering instanceof RMIServerProxy)) {
            throw new RuntimeException(JaiI18N.getString("JAIRMIDescriptor10"));
        }
        id = ((RMIServerProxy)rendering).getRMIID();
        boolean samePBs = false;
        if (oldParamBlock == newParamBlock) {
            samePBs = true;
        }
        Vector<Object> oldSources = oldParamBlock.getSources();
        oldParamBlock.removeSources();
        JAIRMIUtil.checkClientParameters(oldParamBlock, oldServerName);
        oldParamBlock.setSources(JAIRMIUtil.replaceSourcesWithId(oldSources, oldServerName));
        if (samePBs) {
            newParamBlock = oldParamBlock;
        } else {
            Vector<Object> newSources = newParamBlock.getSources();
            newParamBlock.removeSources();
            JAIRMIUtil.checkClientParameters(newParamBlock, oldServerName);
            newParamBlock.setSources(JAIRMIUtil.replaceSourcesWithId(newSources, oldServerName));
        }
        SerializableState oldRHS = SerializerFactory.getState(oldHints, null);
        SerializableState newRHS = SerializerFactory.getState(newHints, null);
        SerializableState shapeState = null;
        try {
            shapeState = this.getImageServer(oldServerName).getInvalidRegion(id, oldParamBlock, oldRHS, newParamBlock, newRHS);
        }
        catch (Exception e) {
            this.sendExceptionToListener(JaiI18N.getString("JAIRMIDescriptor16"), new RemoteImagingException(JaiI18N.getString("JAIRMIDescriptor16"), e));
        }
        return shapeState.getObject();
    }

    private Hashtable hashNames(String[] paramNames) {
        Hashtable<CaselessStringKey, Integer> h = new Hashtable<CaselessStringKey, Integer>();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; ++i) {
                h.put(new CaselessStringKey(paramNames[i]), new Integer(i));
            }
        }
        return h;
    }

    private int getIndex(Hashtable h, String s) {
        return (Integer)h.get(new CaselessStringKey(s));
    }

    private boolean containsAll(Hashtable thisHash, Hashtable otherHash) {
        Enumeration i = thisHash.keys();
        while (i.hasMoreElements()) {
            CaselessStringKey thisNameKey = (CaselessStringKey)i.nextElement();
            if (otherHash.containsKey(thisNameKey)) continue;
            return false;
        }
        return true;
    }

    private OperationDescriptor getOperationDescriptor(OperationDescriptor[] descriptors, String operationName) {
        for (int i = 0; i < descriptors.length; ++i) {
            OperationDescriptor od = descriptors[i];
            if (!od.getName().equalsIgnoreCase(operationName)) continue;
            return od;
        }
        return null;
    }

    void sendExceptionToListener(String message, Exception e) {
        ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
        listener.errorOccurred(message, e, this, false);
    }
}

