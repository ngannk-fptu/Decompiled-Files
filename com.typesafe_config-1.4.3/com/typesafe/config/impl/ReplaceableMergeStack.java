/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.Container;
import com.typesafe.config.impl.ResolveContext;

interface ReplaceableMergeStack
extends Container {
    public AbstractConfigValue makeReplacement(ResolveContext var1, int var2);
}

