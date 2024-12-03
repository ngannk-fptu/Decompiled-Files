/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.monitor.representations;

import com.atlassian.upm.rest.monitor.representations.PluginStateRep;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class PluginStateCollectionRep {
    @JsonProperty
    public final Collection<PluginStateRep> plugins;

    public PluginStateCollectionRep(Collection<PluginStateRep> plugins) {
        this.plugins = Collections.unmodifiableList(new ArrayList<PluginStateRep>(Objects.requireNonNull(plugins, "plugins")));
    }
}

