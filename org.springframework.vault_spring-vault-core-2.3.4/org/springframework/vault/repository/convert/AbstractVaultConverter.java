/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.converter.ConverterRegistry
 *  org.springframework.core.convert.support.GenericConversionService
 *  org.springframework.data.convert.CustomConversions
 *  org.springframework.data.convert.EntityInstantiators
 */
package org.springframework.vault.repository.convert;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.EntityInstantiators;
import org.springframework.vault.repository.convert.VaultConverter;
import org.springframework.vault.repository.convert.VaultCustomConversions;

public abstract class AbstractVaultConverter
implements VaultConverter,
InitializingBean {
    protected final GenericConversionService conversionService;
    protected CustomConversions conversions = new VaultCustomConversions();
    protected EntityInstantiators instantiators = new EntityInstantiators();

    public AbstractVaultConverter(GenericConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public void setCustomConversions(CustomConversions conversions) {
        this.conversions = conversions;
    }

    public void setInstantiators(EntityInstantiators instantiators) {
        this.instantiators = instantiators;
    }

    public ConversionService getConversionService() {
        return this.conversionService;
    }

    public void afterPropertiesSet() {
        this.initializeConverters();
    }

    private void initializeConverters() {
        this.conversions.registerConvertersIn((ConverterRegistry)this.conversionService);
    }
}

