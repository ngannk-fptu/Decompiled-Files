/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigValue;
import com.typesafe.config.impl.AbstractConfigValue;

interface Container
extends ConfigValue {
    public AbstractConfigValue replaceChild(AbstractConfigValue var1, AbstractConfigValue var2);

    public boolean hasDescendant(AbstractConfigValue var1);
}

