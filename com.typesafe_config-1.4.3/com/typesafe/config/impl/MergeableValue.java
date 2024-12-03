/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigValue;

interface MergeableValue
extends ConfigMergeable {
    public ConfigValue toFallbackValue();
}

