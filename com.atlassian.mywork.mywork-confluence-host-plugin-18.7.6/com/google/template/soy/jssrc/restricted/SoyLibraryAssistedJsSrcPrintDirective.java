/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.jssrc.restricted;

import com.google.common.collect.ImmutableSet;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;

public interface SoyLibraryAssistedJsSrcPrintDirective
extends SoyJsSrcPrintDirective {
    public ImmutableSet<String> getRequiredJsLibNames();
}

