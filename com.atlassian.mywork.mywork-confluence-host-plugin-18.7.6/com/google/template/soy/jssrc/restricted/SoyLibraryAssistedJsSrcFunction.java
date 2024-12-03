/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.jssrc.restricted;

import com.google.common.collect.ImmutableSet;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;

public interface SoyLibraryAssistedJsSrcFunction
extends SoyJsSrcFunction {
    public ImmutableSet<String> getRequiredJsLibNames();
}

