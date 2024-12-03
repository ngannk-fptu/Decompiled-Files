/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.osgi;

import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

public interface Stax2ValidationSchemaFactoryProvider {
    public static final String OSGI_SVC_PROP_IMPL_NAME = "org.codehaus.stax2.implName";
    public static final String OSGI_SVC_PROP_IMPL_VERSION = "org.codehaus.stax2.implVersion";
    public static final String OSGI_SVC_PROP_SCHEMA_TYPE = "org.codehaus.stax2.validation.schemaType";

    public String getSchemaType();

    public XMLValidationSchemaFactory createValidationSchemaFactory();
}

