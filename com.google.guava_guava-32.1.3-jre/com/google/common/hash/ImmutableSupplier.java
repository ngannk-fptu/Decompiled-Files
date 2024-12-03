/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 */
package com.google.common.hash;

import com.google.common.base.Supplier;
import com.google.common.hash.ElementTypesAreNonnullByDefault;
import com.google.errorprone.annotations.Immutable;

@Immutable
@ElementTypesAreNonnullByDefault
interface ImmutableSupplier<T>
extends Supplier<T> {
}

