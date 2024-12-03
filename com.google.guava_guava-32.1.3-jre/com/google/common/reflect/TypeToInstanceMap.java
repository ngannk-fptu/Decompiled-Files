/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.DoNotMock
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.google.common.reflect;

import com.google.common.reflect.ElementTypesAreNonnullByDefault;
import com.google.common.reflect.ParametricNullness;
import com.google.common.reflect.TypeToken;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.NonNull;

@DoNotMock(value="Use ImmutableTypeToInstanceMap or MutableTypeToInstanceMap")
@ElementTypesAreNonnullByDefault
public interface TypeToInstanceMap<B>
extends Map<TypeToken<? extends B>, B> {
    @CheckForNull
    public <T extends B> T getInstance(Class<T> var1);

    @CheckForNull
    public <T extends B> T getInstance(TypeToken<T> var1);

    @CheckForNull
    @CanIgnoreReturnValue
    public <T extends B> T putInstance(Class<@NonNull T> var1, @ParametricNullness T var2);

    @CheckForNull
    @CanIgnoreReturnValue
    public <T extends B> T putInstance(TypeToken<@NonNull T> var1, @ParametricNullness T var2);
}

