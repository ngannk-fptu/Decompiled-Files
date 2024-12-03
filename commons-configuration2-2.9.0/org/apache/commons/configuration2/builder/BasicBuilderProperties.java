/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.Collection;
import java.util.Map;
import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.beanutils.BeanHelper;
import org.apache.commons.configuration2.convert.ConversionHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.sync.Synchronizer;

public interface BasicBuilderProperties<T> {
    public T setLogger(ConfigurationLogger var1);

    public T setThrowExceptionOnMissing(boolean var1);

    public T setListDelimiterHandler(ListDelimiterHandler var1);

    public T setInterpolator(ConfigurationInterpolator var1);

    public T setPrefixLookups(Map<String, ? extends Lookup> var1);

    public T setDefaultLookups(Collection<? extends Lookup> var1);

    public T setParentInterpolator(ConfigurationInterpolator var1);

    public T setSynchronizer(Synchronizer var1);

    public T setConversionHandler(ConversionHandler var1);

    public T setConfigurationDecoder(ConfigurationDecoder var1);

    public T setBeanHelper(BeanHelper var1);
}

