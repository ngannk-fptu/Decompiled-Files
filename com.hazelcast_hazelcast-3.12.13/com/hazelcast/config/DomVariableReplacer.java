/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.replacer.spi.ConfigReplacer;
import org.w3c.dom.Node;

interface DomVariableReplacer {
    public void replaceVariables(Node var1, ConfigReplacer var2, boolean var3);
}

