/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.activation.CommandInfo
 *  javax.activation.CommandMap
 *  javax.activation.MailcapCommandMap
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.handler.Handler
 *  javax.xml.ws.soap.AddressingFeature
 */
package com.sun.xml.ws.binding;

import com.oracle.webservices.api.EnvelopeStyleFeature;
import com.oracle.webservices.api.message.MessageContextFactory;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.binding.FeatureListUtil;
import com.sun.xml.ws.binding.HTTPBindingImpl;
import com.sun.xml.ws.binding.SOAPBindingImpl;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.client.HandlerConfiguration;
import com.sun.xml.ws.developer.BindingTypeFeature;
import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.AddressingFeature;

public abstract class BindingImpl
implements WSBinding {
    protected static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];
    private HandlerConfiguration handlerConfig;
    private final Set<QName> addedHeaders = new HashSet<QName>();
    private final Set<QName> knownHeaders = Collections.synchronizedSet(new HashSet());
    private final Set<QName> unmodKnownHeaders = Collections.unmodifiableSet(this.knownHeaders);
    private final BindingID bindingId;
    protected final WebServiceFeatureList features;
    protected final Map<QName, WebServiceFeatureList> operationFeatures = new HashMap<QName, WebServiceFeatureList>();
    protected final Map<QName, WebServiceFeatureList> inputMessageFeatures = new HashMap<QName, WebServiceFeatureList>();
    protected final Map<QName, WebServiceFeatureList> outputMessageFeatures = new HashMap<QName, WebServiceFeatureList>();
    protected final Map<MessageKey, WebServiceFeatureList> faultMessageFeatures = new HashMap<MessageKey, WebServiceFeatureList>();
    protected Service.Mode serviceMode = Service.Mode.PAYLOAD;
    protected MessageContextFactory messageContextFactory;

    protected BindingImpl(BindingID bindingId, WebServiceFeature ... features) {
        this.bindingId = bindingId;
        this.handlerConfig = new HandlerConfiguration(Collections.emptySet(), Collections.emptyList());
        if (this.handlerConfig.getHandlerKnownHeaders() != null) {
            this.knownHeaders.addAll(this.handlerConfig.getHandlerKnownHeaders());
        }
        this.features = new WebServiceFeatureList(features);
        this.features.validate();
    }

    @Override
    @NotNull
    public List<Handler> getHandlerChain() {
        return this.handlerConfig.getHandlerChain();
    }

    public HandlerConfiguration getHandlerConfig() {
        return this.handlerConfig;
    }

    protected void setHandlerConfig(HandlerConfiguration handlerConfig) {
        this.handlerConfig = handlerConfig;
        this.knownHeaders.clear();
        this.knownHeaders.addAll(this.addedHeaders);
        if (handlerConfig != null && handlerConfig.getHandlerKnownHeaders() != null) {
            this.knownHeaders.addAll(handlerConfig.getHandlerKnownHeaders());
        }
    }

    public void setMode(@NotNull Service.Mode mode) {
        this.serviceMode = mode;
    }

    @Override
    public Set<QName> getKnownHeaders() {
        return this.unmodKnownHeaders;
    }

    @Override
    public boolean addKnownHeader(QName headerQName) {
        this.addedHeaders.add(headerQName);
        return this.knownHeaders.add(headerQName);
    }

    @Override
    @NotNull
    public BindingID getBindingId() {
        return this.bindingId;
    }

    @Override
    public final SOAPVersion getSOAPVersion() {
        return this.bindingId.getSOAPVersion();
    }

    @Override
    public AddressingVersion getAddressingVersion() {
        AddressingVersion addressingVersion = this.features.isEnabled(AddressingFeature.class) ? AddressingVersion.W3C : (this.features.isEnabled(MemberSubmissionAddressingFeature.class) ? AddressingVersion.MEMBER : null);
        return addressingVersion;
    }

    @NotNull
    public final Codec createCodec() {
        BindingImpl.initializeJavaActivationHandlers();
        return this.bindingId.createEncoder(this);
    }

    public static void initializeJavaActivationHandlers() {
        try {
            MailcapCommandMap mailMap;
            CommandMap map = CommandMap.getDefaultCommandMap();
            if (map instanceof MailcapCommandMap && !BindingImpl.cmdMapInitialized(mailMap = (MailcapCommandMap)map)) {
                mailMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.ws.encoding.XmlDataContentHandler");
                mailMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.ws.encoding.XmlDataContentHandler");
                mailMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.ws.encoding.ImageDataContentHandler");
                mailMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.ws.encoding.StringDataContentHandler");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static boolean cmdMapInitialized(MailcapCommandMap mailMap) {
        CommandInfo[] commands = mailMap.getAllCommands("text/xml");
        if (commands == null || commands.length == 0) {
            return false;
        }
        String saajClassName = "com.sun.xml.messaging.saaj.soap.XmlDataContentHandler";
        String jaxwsClassName = "com.sun.xml.ws.encoding.XmlDataContentHandler";
        for (CommandInfo command : commands) {
            String commandClass = command.getCommandClass();
            if (!saajClassName.equals(commandClass) && !jaxwsClassName.equals(commandClass)) continue;
            return true;
        }
        return false;
    }

    public static BindingImpl create(@NotNull BindingID bindingId) {
        if (bindingId.equals(BindingID.XML_HTTP)) {
            return new HTTPBindingImpl();
        }
        return new SOAPBindingImpl(bindingId);
    }

    public static BindingImpl create(@NotNull BindingID bindingId, WebServiceFeature[] features) {
        for (WebServiceFeature feature : features) {
            if (!(feature instanceof BindingTypeFeature)) continue;
            BindingTypeFeature f = (BindingTypeFeature)feature;
            bindingId = BindingID.parse(f.getBindingId());
        }
        if (bindingId.equals(BindingID.XML_HTTP)) {
            return new HTTPBindingImpl(features);
        }
        return new SOAPBindingImpl(bindingId, features);
    }

    public static WSBinding getDefaultBinding() {
        return new SOAPBindingImpl(BindingID.SOAP11_HTTP);
    }

    public String getBindingID() {
        return this.bindingId.toString();
    }

    @Override
    @Nullable
    public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> featureType) {
        return this.features.get(featureType);
    }

    @Override
    @Nullable
    public <F extends WebServiceFeature> F getOperationFeature(@NotNull Class<F> featureType, @NotNull QName operationName) {
        WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        return FeatureListUtil.mergeFeature(featureType, operationFeatureList, this.features);
    }

    @Override
    public boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> feature) {
        return this.features.isEnabled(feature);
    }

    @Override
    public boolean isOperationFeatureEnabled(@NotNull Class<? extends WebServiceFeature> featureType, @NotNull QName operationName) {
        WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        return FeatureListUtil.isFeatureEnabled(featureType, operationFeatureList, this.features);
    }

    @Override
    @NotNull
    public WebServiceFeatureList getFeatures() {
        if (!this.isFeatureEnabled(EnvelopeStyleFeature.class)) {
            WebServiceFeature[] f = new WebServiceFeature[]{this.getSOAPVersion().toFeature()};
            this.features.mergeFeatures(f, false);
        }
        return this.features;
    }

    @Override
    @NotNull
    public WebServiceFeatureList getOperationFeatures(@NotNull QName operationName) {
        WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        return FeatureListUtil.mergeList(operationFeatureList, this.features);
    }

    @Override
    @NotNull
    public WebServiceFeatureList getInputMessageFeatures(@NotNull QName operationName) {
        WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        WebServiceFeatureList messageFeatureList = this.inputMessageFeatures.get(operationName);
        return FeatureListUtil.mergeList(operationFeatureList, messageFeatureList, this.features);
    }

    @Override
    @NotNull
    public WebServiceFeatureList getOutputMessageFeatures(@NotNull QName operationName) {
        WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        WebServiceFeatureList messageFeatureList = this.outputMessageFeatures.get(operationName);
        return FeatureListUtil.mergeList(operationFeatureList, messageFeatureList, this.features);
    }

    @Override
    @NotNull
    public WebServiceFeatureList getFaultMessageFeatures(@NotNull QName operationName, @NotNull QName messageName) {
        WebServiceFeatureList operationFeatureList = this.operationFeatures.get(operationName);
        WebServiceFeatureList messageFeatureList = this.faultMessageFeatures.get(new MessageKey(operationName, messageName));
        return FeatureListUtil.mergeList(operationFeatureList, messageFeatureList, this.features);
    }

    public void setOperationFeatures(@NotNull QName operationName, WebServiceFeature ... newFeatures) {
        if (newFeatures != null) {
            WebServiceFeatureList featureList = this.operationFeatures.get(operationName);
            if (featureList == null) {
                featureList = new WebServiceFeatureList();
            }
            for (WebServiceFeature f : newFeatures) {
                featureList.add(f);
            }
            this.operationFeatures.put(operationName, featureList);
        }
    }

    public void setInputMessageFeatures(@NotNull QName operationName, WebServiceFeature ... newFeatures) {
        if (newFeatures != null) {
            WebServiceFeatureList featureList = this.inputMessageFeatures.get(operationName);
            if (featureList == null) {
                featureList = new WebServiceFeatureList();
            }
            for (WebServiceFeature f : newFeatures) {
                featureList.add(f);
            }
            this.inputMessageFeatures.put(operationName, featureList);
        }
    }

    public void setOutputMessageFeatures(@NotNull QName operationName, WebServiceFeature ... newFeatures) {
        if (newFeatures != null) {
            WebServiceFeatureList featureList = this.outputMessageFeatures.get(operationName);
            if (featureList == null) {
                featureList = new WebServiceFeatureList();
            }
            for (WebServiceFeature f : newFeatures) {
                featureList.add(f);
            }
            this.outputMessageFeatures.put(operationName, featureList);
        }
    }

    public void setFaultMessageFeatures(@NotNull QName operationName, @NotNull QName messageName, WebServiceFeature ... newFeatures) {
        if (newFeatures != null) {
            MessageKey key = new MessageKey(operationName, messageName);
            WebServiceFeatureList featureList = this.faultMessageFeatures.get(key);
            if (featureList == null) {
                featureList = new WebServiceFeatureList();
            }
            for (WebServiceFeature f : newFeatures) {
                featureList.add(f);
            }
            this.faultMessageFeatures.put(key, featureList);
        }
    }

    @Override
    @NotNull
    public synchronized MessageContextFactory getMessageContextFactory() {
        if (this.messageContextFactory == null) {
            this.messageContextFactory = MessageContextFactory.createFactory(this.getFeatures().toArray());
        }
        return this.messageContextFactory;
    }

    protected static class MessageKey {
        private final QName operationName;
        private final QName messageName;

        public MessageKey(QName operationName, QName messageName) {
            this.operationName = operationName;
            this.messageName = messageName;
        }

        public int hashCode() {
            int hashFirst = this.operationName != null ? this.operationName.hashCode() : 0;
            int hashSecond = this.messageName != null ? this.messageName.hashCode() : 0;
            return (hashFirst + hashSecond) * hashSecond + hashFirst;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            MessageKey other = (MessageKey)obj;
            if (!(this.operationName == other.operationName || this.operationName != null && this.operationName.equals(other.operationName))) {
                return false;
            }
            return this.messageName == other.messageName || this.messageName != null && this.messageName.equals(other.messageName);
        }

        public String toString() {
            return "(" + this.operationName + ", " + this.messageName + ")";
        }
    }
}

