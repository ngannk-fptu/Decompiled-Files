/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.Errors;
import com.google.inject.spi.TypeListenerBinding;

final class TypeListenerBindingProcessor
extends AbstractProcessor {
    TypeListenerBindingProcessor(Errors errors) {
        super(errors);
    }

    public Boolean visit(TypeListenerBinding binding) {
        this.injector.state.addTypeListener(binding);
        return true;
    }
}

