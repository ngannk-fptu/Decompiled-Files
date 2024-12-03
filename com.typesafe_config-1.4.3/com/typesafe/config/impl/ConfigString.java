/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.SerializedConfigValue;
import java.io.ObjectStreamException;
import java.io.Serializable;

abstract class ConfigString
extends AbstractConfigValue
implements Serializable {
    private static final long serialVersionUID = 2L;
    protected final String value;

    protected ConfigString(ConfigOrigin origin, String value) {
        super(origin);
        this.value = value;
    }

    boolean wasQuoted() {
        return this instanceof Quoted;
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.STRING;
    }

    @Override
    public String unwrapped() {
        return this.value;
    }

    @Override
    String transformToString() {
        return this.value;
    }

    @Override
    protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
        if (this.hideEnvVariableValue(options)) {
            this.appendHiddenEnvVariableValue(sb);
        } else {
            String rendered = options.getJson() ? ConfigImplUtil.renderJsonString(this.value) : ConfigImplUtil.renderStringUnquotedIfPossible(this.value);
            sb.append(rendered);
        }
    }

    static final class Unquoted
    extends ConfigString {
        Unquoted(ConfigOrigin origin, String value) {
            super(origin, value);
        }

        @Override
        protected Unquoted newCopy(ConfigOrigin origin) {
            return new Unquoted(origin, this.value);
        }

        private Object writeReplace() throws ObjectStreamException {
            return new SerializedConfigValue(this);
        }
    }

    static final class Quoted
    extends ConfigString {
        Quoted(ConfigOrigin origin, String value) {
            super(origin, value);
        }

        @Override
        protected Quoted newCopy(ConfigOrigin origin) {
            return new Quoted(origin, this.value);
        }

        private Object writeReplace() throws ObjectStreamException {
            return new SerializedConfigValue(this);
        }
    }
}

