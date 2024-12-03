/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.io;

import com.google.common.io.ElementTypesAreNonnullByDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@ElementTypesAreNonnullByDefault
@interface IgnoreJRERequirement {
}

