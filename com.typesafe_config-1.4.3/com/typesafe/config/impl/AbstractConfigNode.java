/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.Token;
import com.typesafe.config.parser.ConfigNode;
import java.util.Collection;

abstract class AbstractConfigNode
implements ConfigNode {
    AbstractConfigNode() {
    }

    abstract Collection<Token> tokens();

    @Override
    public final String render() {
        StringBuilder origText = new StringBuilder();
        Collection<Token> tokens = this.tokens();
        for (Token t : tokens) {
            origText.append(t.tokenText());
        }
        return origText.toString();
    }

    public final boolean equals(Object other) {
        return other instanceof AbstractConfigNode && this.render().equals(((AbstractConfigNode)other).render());
    }

    public final int hashCode() {
        return this.render().hashCode();
    }
}

