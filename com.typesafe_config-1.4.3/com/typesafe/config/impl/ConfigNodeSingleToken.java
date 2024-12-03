/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.Token;
import java.util.Collection;
import java.util.Collections;

class ConfigNodeSingleToken
extends AbstractConfigNode {
    final Token token;

    ConfigNodeSingleToken(Token t) {
        this.token = t;
    }

    @Override
    protected Collection<Token> tokens() {
        return Collections.singletonList(this.token);
    }

    protected Token token() {
        return this.token;
    }
}

