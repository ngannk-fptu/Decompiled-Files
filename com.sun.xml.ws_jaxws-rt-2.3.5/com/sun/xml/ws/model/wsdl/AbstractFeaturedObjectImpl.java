/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.model.wsdl.AbstractExtensibleImpl;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceFeature;

abstract class AbstractFeaturedObjectImpl
extends AbstractExtensibleImpl
implements WSDLFeaturedObject {
    protected WebServiceFeatureList features;

    protected AbstractFeaturedObjectImpl(XMLStreamReader xsr) {
        super(xsr);
    }

    protected AbstractFeaturedObjectImpl(String systemId, int lineNumber) {
        super(systemId, lineNumber);
    }

    @Override
    public final void addFeature(WebServiceFeature feature) {
        if (this.features == null) {
            this.features = new WebServiceFeatureList();
        }
        this.features.add(feature);
    }

    @Override
    @NotNull
    public WebServiceFeatureList getFeatures() {
        if (this.features == null) {
            return new WebServiceFeatureList();
        }
        return this.features;
    }

    public final WebServiceFeature getFeature(String id) {
        if (this.features != null) {
            for (WebServiceFeature f : this.features) {
                if (!f.getID().equals(id)) continue;
                return f;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> featureType) {
        if (this.features == null) {
            return null;
        }
        return this.features.get(featureType);
    }
}

