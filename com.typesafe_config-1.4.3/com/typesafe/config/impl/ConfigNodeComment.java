/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.impl.ConfigNodeSingleToken;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokens;

final class ConfigNodeComment
extends ConfigNodeSingleToken {
    ConfigNodeComment(Token comment) {
        super(comment);
        if (!Tokens.isComment(this.token)) {
            throw new ConfigException.BugOrBroken("Tried to create a ConfigNodeComment from a non-comment token");
        }
    }

    protected String commentText() {
        return Tokens.getCommentText(this.token);
    }
}

