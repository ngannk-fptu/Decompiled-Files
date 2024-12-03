/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.AbstractConfigNode;
import com.typesafe.config.impl.ConfigNodeComplexValue;
import java.util.Collection;

final class ConfigNodeConcatenation
extends ConfigNodeComplexValue {
    ConfigNodeConcatenation(Collection<AbstractConfigNode> children) {
        super(children);
    }

    @Override
    protected ConfigNodeConcatenation newNode(Collection<AbstractConfigNode> nodes) {
        return new ConfigNodeConcatenation(nodes);
    }
}

