/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Stage;
import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.Errors;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.spi.DisableCircularProxiesOption;
import com.google.inject.spi.RequireAtInjectOnConstructorsOption;
import com.google.inject.spi.RequireExactBindingAnnotationsOption;
import com.google.inject.spi.RequireExplicitBindingsOption;

class InjectorOptionsProcessor
extends AbstractProcessor {
    private boolean disableCircularProxies = false;
    private boolean jitDisabled = false;
    private boolean atInjectRequired = false;
    private boolean exactBindingAnnotationsRequired = false;

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

    public Boolean visit(RequireAtInjectOnConstructorsOption option) {
        this.atInjectRequired = true;
        return true;
    }

    public Boolean visit(RequireExactBindingAnnotationsOption option) {
        this.exactBindingAnnotationsRequired = true;
        return true;
    }

    InjectorImpl.InjectorOptions getOptions(Stage stage, InjectorImpl.InjectorOptions parentOptions) {
        Preconditions.checkNotNull((Object)((Object)stage), (Object)"stage must be set");
        if (parentOptions == null) {
            return new InjectorImpl.InjectorOptions(stage, this.jitDisabled, this.disableCircularProxies, this.atInjectRequired, this.exactBindingAnnotationsRequired);
        }
        Preconditions.checkState((stage == parentOptions.stage ? 1 : 0) != 0, (Object)"child & parent stage don't match");
        return new InjectorImpl.InjectorOptions(stage, this.jitDisabled || parentOptions.jitDisabled, this.disableCircularProxies || parentOptions.disableCircularProxies, this.atInjectRequired || parentOptions.atInjectRequired, this.exactBindingAnnotationsRequired || parentOptions.exactBindingAnnotationsRequired);
    }
}

