/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.ConfigIncludeKind;
import com.typesafe.config.impl.ConfigNodeSimpleValue;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokens;
import java.util.ArrayList;
import java.util.Collection;

final class ConfigNodeInclude
extends AbstractConfigNode {
    private final ArrayList<AbstractConfigNode> children;
    private final ConfigIncludeKind kind;
    private final boolean isRequired;

    ConfigNodeInclude(Collection<AbstractConfigNode> children, ConfigIncludeKind kind, boolean isRequired) {
        this.children = new ArrayList<AbstractConfigNode>(children);
        this.kind = kind;
        this.isRequired = isRequired;
    }

    public final Collection<AbstractConfigNode> children() {
        return this.children;
    }

    @Override
    protected Collection<Token> tokens() {
        ArrayList<Token> tokens = new ArrayList<Token>();
        for (AbstractConfigNode child : this.children) {
            tokens.addAll(child.tokens());
        }
        return tokens;
    }

    protected ConfigIncludeKind kind() {
        return this.kind;
    }

    protected boolean isRequired() {
        return this.isRequired;
    }

    protected String name() {
        for (AbstractConfigNode n : this.children) {
            if (!(n instanceof ConfigNodeSimpleValue)) continue;
            return (String)Tokens.getValue(((ConfigNodeSimpleValue)n).token()).unwrapped();
        }
        return null;
    }
}

