/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.env;

import java.util.Map;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

public interface ConfigurableEnvironment
extends Environment,
ConfigurablePropertyResolver {
    public void setActiveProfiles(String ... var1);

    public void addActiveProfile(String var1);

    public void setDefaultProfiles(String ... var1);

    public MutablePropertySources getPropertySources();

    public Map<String, Object> getSystemProperties();

    public Map<String, Object> getSystemEnvironment();

    public void merge(ConfigurableEnvironment var1);
}

