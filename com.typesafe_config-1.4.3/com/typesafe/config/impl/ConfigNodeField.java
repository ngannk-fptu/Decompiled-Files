/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.AbstractConfigNodeValue;
import com.typesafe.config.impl.ConfigNodeComment;
import com.typesafe.config.impl.ConfigNodePath;
import com.typesafe.config.impl.ConfigNodeSingleToken;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokens;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class ConfigNodeField
extends AbstractConfigNode {
    private final ArrayList<AbstractConfigNode> children;

    public ConfigNodeField(Collection<AbstractConfigNode> children) {
        this.children = new ArrayList<AbstractConfigNode>(children);
    }

    @Override
    protected Collection<Token> tokens() {
        ArrayList<Token> tokens = new ArrayList<Token>();
        for (AbstractConfigNode child : this.children) {
            tokens.addAll(child.tokens());
        }
        return tokens;
    }

    public ConfigNodeField replaceValue(AbstractConfigNodeValue newValue) {
        ArrayList<AbstractConfigNode> childrenCopy = new ArrayList<AbstractConfigNode>(this.children);
        for (int i = 0; i < childrenCopy.size(); ++i) {
            if (!(childrenCopy.get(i) instanceof AbstractConfigNodeValue)) continue;
            childrenCopy.set(i, newValue);
            return new ConfigNodeField(childrenCopy);
        }
        throw new ConfigException.BugOrBroken("Field node doesn't have a value");
    }

    public AbstractConfigNodeValue value() {
        for (int i = 0; i < this.children.size(); ++i) {
            if (!(this.children.get(i) instanceof AbstractConfigNodeValue)) continue;
            return (AbstractConfigNodeValue)this.children.get(i);
        }
        throw new ConfigException.BugOrBroken("Field node doesn't have a value");
    }

    public ConfigNodePath path() {
        for (int i = 0; i < this.children.size(); ++i) {
            if (!(this.children.get(i) instanceof ConfigNodePath)) continue;
            return (ConfigNodePath)this.children.get(i);
        }
        throw new ConfigException.BugOrBroken("Field node doesn't have a path");
    }

    protected Token separator() {
        for (AbstractConfigNode child : this.children) {
            Token t;
            if (!(child instanceof ConfigNodeSingleToken) || (t = ((ConfigNodeSingleToken)child).token()) != Tokens.PLUS_EQUALS && t != Tokens.COLON && t != Tokens.EQUALS) continue;
            return t;
        }
        return null;
    }

    protected List<String> comments() {
        ArrayList<String> comments = new ArrayList<String>();
        for (AbstractConfigNode child : this.children) {
            if (!(child instanceof ConfigNodeComment)) continue;
            comments.add(((ConfigNodeComment)child).commentText());
        }
        return comments;
    }
}

