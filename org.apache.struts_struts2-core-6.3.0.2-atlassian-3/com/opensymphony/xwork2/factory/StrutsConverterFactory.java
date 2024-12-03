/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.factory.ConverterFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrutsConverterFactory
implements ConverterFactory {
    private static final Logger LOG = LogManager.getLogger(StrutsConverterFactory.class);
    private Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public TypeConverter buildConverter(Class<? extends TypeConverter> converterClass, Map<String, Object> extraContext) throws Exception {
        LOG.debug("Creating converter of type [{}]", (Object)converterClass.getCanonicalName());
        return this.container.inject(converterClass);
    }
}

