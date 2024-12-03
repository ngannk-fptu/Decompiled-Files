/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.AbstractConfigValue;
import java.util.Collection;

interface Unmergeable {
    public Collection<? extends AbstractConfigValue> unmergedValues();
}

