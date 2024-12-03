/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.Binding
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.handler.Handler
 */
package com.sun.xml.ws.api;

import com.oracle.webservices.api.message.MessageContextFactory;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;

public interface WSBinding
extends Binding {
    public SOAPVersion getSOAPVersion();

    public AddressingVersion getAddressingVersion();

    @NotNull
    public BindingID getBindingId();

    @NotNull
    public List<Handler> getHandlerChain();

    public boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> var1);

    public boolean isOperationFeatureEnabled(@NotNull Class<? extends WebServiceFeature> var1, @NotNull QName var2);

    @Nullable
    public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> var1);

    @Nullable
    public <F extends WebServiceFeature> F getOperationFeature(@NotNull Class<F> var1, @NotNull QName var2);

    @NotNull
    public WSFeatureList getFeatures();

    @NotNull
    public WSFeatureList getOperationFeatures(@NotNull QName var1);

    @NotNull
    public WSFeatureList getInputMessageFeatures(@NotNull QName var1);

    @NotNull
    public WSFeatureList getOutputMessageFeatures(@NotNull QName var1);

    @NotNull
    public WSFeatureList getFaultMessageFeatures(@NotNull QName var1, @NotNull QName var2);

    @NotNull
    public Set<QName> getKnownHeaders();

    public boolean addKnownHeader(QName var1);

    @NotNull
    public MessageContextFactory getMessageContextFactory();
}

