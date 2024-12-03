/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.DoNotMock
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.NonNull;

@DoNotMock(value="Use ImmutableClassToInstanceMap or MutableClassToInstanceMap")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface ClassToInstanceMap<B>
extends Map<Class<? extends B>, B> {
    @CheckForNull
    public <T extends B> T getInstance(Class<T> var1);

    @CheckForNull
    @CanIgnoreReturnValue
    public <T extends B> T putInstance(Class<@NonNull T> var1, @ParametricNullness T var2);
}

