/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.Collection;
import java.util.Map;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.interpol.Lookup;
import org.apache.commons.configuration2.sync.SynchronizerSupport;

public interface Configuration
extends ImmutableConfiguration,
SynchronizerSupport {
    public Configuration subset(String var1);

    public void addProperty(String var1, Object var2);

    public void setProperty(String var1, Object var2);

    public void clearProperty(String var1);

    public void clear();

    public ConfigurationInterpolator getInterpolator();

    public void setInterpolator(ConfigurationInterpolator var1);

    public void installInterpolator(Map<String, ? extends Lookup> var1, Collection<? extends Lookup> var2);
}

