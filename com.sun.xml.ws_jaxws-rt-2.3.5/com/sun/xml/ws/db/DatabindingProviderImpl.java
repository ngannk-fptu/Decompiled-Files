/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.db;

import com.oracle.webservices.api.databinding.WSDLGenerator;
import com.oracle.webservices.api.databinding.WSDLResolver;
import com.sun.xml.ws.api.databinding.Databinding;
import com.sun.xml.ws.api.databinding.DatabindingConfig;
import com.sun.xml.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.ws.db.DatabindingImpl;
import com.sun.xml.ws.spi.db.DatabindingProvider;
import java.io.File;
import java.util.Map;

public class DatabindingProviderImpl
implements DatabindingProvider {
    private static final String CachedDatabinding = "com.sun.xml.ws.db.DatabindingProviderImpl";
    Map<String, Object> properties;

    @Override
    public void init(Map<String, Object> p) {
        this.properties = p;
    }

    DatabindingImpl getCachedDatabindingImpl(DatabindingConfig config) {
        Object object = config.properties().get(CachedDatabinding);
        return object != null && object instanceof DatabindingImpl ? (DatabindingImpl)object : null;
    }

    @Override
    public Databinding create(DatabindingConfig config) {
        DatabindingImpl impl = this.getCachedDatabindingImpl(config);
        if (impl == null) {
            impl = new DatabindingImpl(this, config);
            config.properties().put(CachedDatabinding, impl);
        }
        return impl;
    }

    @Override
    public WSDLGenerator wsdlGen(DatabindingConfig config) {
        DatabindingImpl impl = (DatabindingImpl)this.create(config);
        return new JaxwsWsdlGen(impl);
    }

    @Override
    public boolean isFor(String databindingMode) {
        return true;
    }

    public static class JaxwsWsdlGen
    implements WSDLGenerator {
        DatabindingImpl databinding;
        WSDLGenInfo wsdlGenInfo;

        JaxwsWsdlGen(DatabindingImpl impl) {
            this.databinding = impl;
            this.wsdlGenInfo = new WSDLGenInfo();
        }

        @Override
        public WSDLGenerator inlineSchema(boolean inline) {
            this.wsdlGenInfo.setInlineSchemas(inline);
            return this;
        }

        @Override
        public WSDLGenerator property(String name, Object value) {
            return this;
        }

        @Override
        public void generate(WSDLResolver wsdlResolver) {
            this.wsdlGenInfo.setWsdlResolver(wsdlResolver);
            this.databinding.generateWSDL(this.wsdlGenInfo);
        }

        @Override
        public void generate(File outputDir, String name) {
            this.databinding.generateWSDL(this.wsdlGenInfo);
        }
    }
}

