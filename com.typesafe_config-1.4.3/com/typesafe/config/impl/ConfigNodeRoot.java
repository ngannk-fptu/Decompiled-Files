/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.AbstractConfigNodeValue;
import com.typesafe.config.impl.ConfigNodeArray;
import com.typesafe.config.impl.ConfigNodeComplexValue;
import com.typesafe.config.impl.ConfigNodeObject;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.PathParser;
import java.util.ArrayList;
import java.util.Collection;

final class ConfigNodeRoot
extends ConfigNodeComplexValue {
    private final ConfigOrigin origin;

    ConfigNodeRoot(Collection<AbstractConfigNode> children, ConfigOrigin origin) {
        super(children);
        this.origin = origin;
    }

    @Override
    protected ConfigNodeRoot newNode(Collection<AbstractConfigNode> nodes) {
        throw new ConfigException.BugOrBroken("Tried to indent the root object");
    }

    protected ConfigNodeComplexValue value() {
        for (AbstractConfigNode node : this.children) {
            if (!(node instanceof ConfigNodeComplexValue)) continue;
            return (ConfigNodeComplexValue)node;
        }
        throw new ConfigException.BugOrBroken("ConfigNodeRoot did not contain a value");
    }

    protected ConfigNodeRoot setValue(String desiredPath, AbstractConfigNodeValue value, ConfigSyntax flavor) {
        ArrayList<AbstractConfigNode> childrenCopy = new ArrayList<AbstractConfigNode>(this.children);
        for (int i = 0; i < childrenCopy.size(); ++i) {
            AbstractConfigNode node = (AbstractConfigNode)childrenCopy.get(i);
            if (!(node instanceof ConfigNodeComplexValue)) continue;
            if (node instanceof ConfigNodeArray) {
                throw new ConfigException.WrongType(this.origin, "The ConfigDocument had an array at the root level, and values cannot be modified inside an array.");
            }
            if (!(node instanceof ConfigNodeObject)) continue;
            if (value == null) {
                childrenCopy.set(i, ((ConfigNodeObject)node).removeValueOnPath(desiredPath, flavor));
            } else {
                childrenCopy.set(i, ((ConfigNodeObject)node).setValueOnPath(desiredPath, value, flavor));
            }
            return new ConfigNodeRoot(childrenCopy, this.origin);
        }
        throw new ConfigException.BugOrBroken("ConfigNodeRoot did not contain a value");
    }

    protected boolean hasValue(String desiredPath) {
        Path path = PathParser.parsePath(desiredPath);
        ArrayList childrenCopy = new ArrayList(this.children);
        for (int i = 0; i < childrenCopy.size(); ++i) {
            AbstractConfigNode node = (AbstractConfigNode)childrenCopy.get(i);
            if (!(node instanceof ConfigNodeComplexValue)) continue;
            if (node instanceof ConfigNodeArray) {
                throw new ConfigException.WrongType(this.origin, "The ConfigDocument had an array at the root level, and values cannot be modified inside an array.");
            }
            if (!(node instanceof ConfigNodeObject)) continue;
            return ((ConfigNodeObject)node).hasValue(path);
        }
        throw new ConfigException.BugOrBroken("ConfigNodeRoot did not contain a value");
    }
}

