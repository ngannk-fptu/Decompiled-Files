/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.ConfigNodeComplexValue;
import java.util.Collection;

final class ConfigNodeArray
extends ConfigNodeComplexValue {
    ConfigNodeArray(Collection<AbstractConfigNode> children) {
        super(children);
    }

    @Override
    protected ConfigNodeArray newNode(Collection<AbstractConfigNode> nodes) {
        return new ConfigNodeArray(nodes);
    }
}

