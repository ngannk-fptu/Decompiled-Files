/*
 * Decompiled with CFR 0.152.
 */
package org.checkerframework.common.initializedfields.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.common.initializedfields.qual.InitializedFields;
import org.checkerframework.framework.qual.PolymorphicQualifier;

@PolymorphicQualifier(value=InitializedFields.class)
@Target(value={ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface PolyInitializedFields {
}

