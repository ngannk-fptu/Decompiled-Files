/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.AbstractConfigNodeValue;
import com.typesafe.config.impl.ConfigNodeField;
import com.typesafe.config.impl.ConfigNodeSingleToken;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokens;
import java.util.ArrayList;
import java.util.Collection;

abstract class ConfigNodeComplexValue
extends AbstractConfigNodeValue {
    protected final ArrayList<AbstractConfigNode> children;

    ConfigNodeComplexValue(Collection<AbstractConfigNode> children) {
        this.children = new ArrayList<AbstractConfigNode>(children);
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

    protected ConfigNodeComplexValue indentText(AbstractConfigNode indentation) {
        ArrayList<AbstractConfigNode> childrenCopy = new ArrayList<AbstractConfigNode>(this.children);
        for (int i = 0; i < childrenCopy.size(); ++i) {
            AbstractConfigNode child = childrenCopy.get(i);
            if (child instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)child).token())) {
                childrenCopy.add(i + 1, indentation);
                ++i;
                continue;
            }
            if (child instanceof ConfigNodeField) {
                AbstractConfigNodeValue value = ((ConfigNodeField)child).value();
                if (!(value instanceof ConfigNodeComplexValue)) continue;
                childrenCopy.set(i, ((ConfigNodeField)child).replaceValue(((ConfigNodeComplexValue)value).indentText(indentation)));
                continue;
            }
            if (!(child instanceof ConfigNodeComplexValue)) continue;
            childrenCopy.set(i, ((ConfigNodeComplexValue)child).indentText(indentation));
        }
        return this.newNode(childrenCopy);
    }

    abstract ConfigNodeComplexValue newNode(Collection<AbstractConfigNode> var1);
}

