/*
 * Decompiled with CFR 0.152.
 */
package com.infradna.tool.bridge_method_injector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.TYPE})
public @interface BridgeMethodsAdded {
}

