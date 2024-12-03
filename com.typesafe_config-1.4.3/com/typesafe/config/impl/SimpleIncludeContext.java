/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigParseable;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.Parseable;
import com.typesafe.config.impl.SimpleIncluder;

class SimpleIncludeContext
implements ConfigIncludeContext {
    private final Parseable parseable;
    private final ConfigParseOptions options;

    SimpleIncludeContext(Parseable parseable) {
        this.parseable = parseable;
        this.options = SimpleIncluder.clearForInclude(parseable.options());
    }

    private SimpleIncludeContext(Parseable parseable, ConfigParseOptions options) {
        this.parseable = parseable;
        this.options = options;
    }

    SimpleIncludeContext withParseable(Parseable parseable) {
        if (parseable == this.parseable) {
            return this;
        }
        return new SimpleIncludeContext(parseable);
    }

    @Override
    public ConfigParseable relativeTo(String filename) {
        if (ConfigImpl.traceLoadsEnabled()) {
            ConfigImpl.trace("Looking for '" + filename + "' relative to " + this.parseable);
        }
        if (this.parseable != null) {
            return this.parseable.relativeTo(filename);
        }
        return null;
    }

    @Override
    public ConfigParseOptions parseOptions() {
        return this.options;
    }

    @Override
    public ConfigIncludeContext setParseOptions(ConfigParseOptions options) {
        return new SimpleIncludeContext(this.parseable, options.setSyntax(null).setOriginDescription(null));
    }
}

