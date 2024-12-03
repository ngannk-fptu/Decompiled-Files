/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokens;
import java.util.ArrayList;
import java.util.Collection;

final class ConfigNodePath
extends AbstractConfigNode {
    private final Path path;
    final ArrayList<Token> tokens;

    ConfigNodePath(Path path, Collection<Token> tokens) {
        this.path = path;
        this.tokens = new ArrayList<Token>(tokens);
    }

    @Override
    protected Collection<Token> tokens() {
        return this.tokens;
    }

    protected Path value() {
        return this.path;
    }

    protected ConfigNodePath subPath(int toRemove) {
        int periodCount = 0;
        ArrayList<Token> tokensCopy = new ArrayList<Token>(this.tokens);
        for (int i = 0; i < tokensCopy.size(); ++i) {
            if (Tokens.isUnquotedText(tokensCopy.get(i)) && tokensCopy.get(i).tokenText().equals(".")) {
                ++periodCount;
            }
            if (periodCount != toRemove) continue;
            return new ConfigNodePath(this.path.subPath(toRemove), tokensCopy.subList(i + 1, tokensCopy.size()));
        }
        throw new ConfigException.BugOrBroken("Tried to remove too many elements from a Path node");
    }

    protected ConfigNodePath first() {
        ArrayList<Token> tokensCopy = new ArrayList<Token>(this.tokens);
        for (int i = 0; i < tokensCopy.size(); ++i) {
            if (!Tokens.isUnquotedText(tokensCopy.get(i)) || !tokensCopy.get(i).tokenText().equals(".")) continue;
            return new ConfigNodePath(this.path.subPath(0, 1), tokensCopy.subList(0, i));
        }
        return this;
    }
}

