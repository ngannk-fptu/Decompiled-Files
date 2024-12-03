/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.osgi;

import com.ctc.wstx.api.ValidatorConfig;
import com.ctc.wstx.dtd.DTDSchemaFactory;
import com.ctc.wstx.msv.RelaxNGSchemaFactory;
import com.ctc.wstx.msv.W3CSchemaFactory;
import java.util.Properties;
import org.codehaus.stax2.osgi.Stax2ValidationSchemaFactoryProvider;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

public abstract class ValidationSchemaFactoryProviderImpl
implements Stax2ValidationSchemaFactoryProvider {
    final String mSchemaType;

    protected ValidationSchemaFactoryProviderImpl(String st) {
        this.mSchemaType = st;
    }

    public static ValidationSchemaFactoryProviderImpl[] createAll() {
        return new ValidationSchemaFactoryProviderImpl[]{new DTD(), new RelaxNG(), new W3CSchema()};
    }

    @Override
    public abstract XMLValidationSchemaFactory createValidationSchemaFactory();

    @Override
    public String getSchemaType() {
        return this.mSchemaType;
    }

    public Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("org.codehaus.stax2.implName", ValidatorConfig.getImplName());
        props.setProperty("org.codehaus.stax2.implVersion", ValidatorConfig.getImplVersion());
        props.setProperty("org.codehaus.stax2.validation.schemaType", this.mSchemaType);
        return props;
    }

    static final class W3CSchema
    extends ValidationSchemaFactoryProviderImpl {
        W3CSchema() {
            super("http://www.w3.org/2001/XMLSchema");
        }

        @Override
        public XMLValidationSchemaFactory createValidationSchemaFactory() {
            return new W3CSchemaFactory();
        }
    }

    static final class RelaxNG
    extends ValidationSchemaFactoryProviderImpl {
        RelaxNG() {
            super("http://relaxng.org/ns/structure/0.9");
        }

        @Override
        public XMLValidationSchemaFactory createValidationSchemaFactory() {
            return new RelaxNGSchemaFactory();
        }
    }

    static final class DTD
    extends ValidationSchemaFactoryProviderImpl {
        DTD() {
            super("http://www.w3.org/XML/1998/namespace");
        }

        @Override
        public XMLValidationSchemaFactory createValidationSchemaFactory() {
            return new DTDSchemaFactory();
        }
    }
}

