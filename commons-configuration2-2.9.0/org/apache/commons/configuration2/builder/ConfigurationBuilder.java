/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.event.EventSource;
import org.apache.commons.configuration2.ex.ConfigurationException;

public interface ConfigurationBuilder<T extends ImmutableConfiguration>
extends EventSource {
    public T getConfiguration() throws ConfigurationException;
}

