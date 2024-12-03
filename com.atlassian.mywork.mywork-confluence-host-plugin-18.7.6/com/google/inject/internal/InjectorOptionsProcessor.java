/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Stage;
import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.Errors;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.DisableCircularProxiesOption;
import com.google.inject.spi.RequireExplicitBindingsOption;

class InjectorOptionsProcessor
extends AbstractProcessor {
    private boolean disableCircularProxies = false;
    private boolean jitDisabled = false;

    InjectorOptionsProcessor(Errors errors) {
        super(errors);
    }

    public Boolean visit(DisableCircularProxiesOption option) {
        this.disableCircularProxies = true;
        return true;
    }

    public Boolean visit(RequireExplicitBindingsOption option) {
        this.jitDisabled = true;
        return true;
    }

    InjectorImpl.InjectorOptions getOptions(Stage stage, InjectorImpl.InjectorOptions parentOptions) {
        $Preconditions.checkNotNull(stage, "stage must be set");
        if (parentOptions == null) {
            return new InjectorImpl.InjectorOptions(stage, this.jitDisabled, this.disableCircularProxies);
        }
        $Preconditions.checkState(stage == parentOptions.stage, "child & parent stage don't match");
        return new InjectorImpl.InjectorOptions(stage, this.jitDisabled || parentOptions.jitDisabled, this.disableCircularProxies || parentOptions.disableCircularProxies);
    }
}

