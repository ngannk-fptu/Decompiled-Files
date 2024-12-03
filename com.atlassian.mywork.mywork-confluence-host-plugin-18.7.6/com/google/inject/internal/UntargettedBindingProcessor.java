/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binding;
import com.google.inject.internal.AbstractBindingProcessor;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.ProcessedBindingData;
import com.google.inject.spi.UntargettedBinding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class UntargettedBindingProcessor
extends AbstractBindingProcessor {
    UntargettedBindingProcessor(Errors errors, ProcessedBindingData bindingData) {
        super(errors, bindingData);
    }

    @Override
    public <T> Boolean visit(Binding<T> binding) {
        return (Boolean)binding.acceptTargetVisitor(new AbstractBindingProcessor.Processor<T, Boolean>((BindingImpl)binding){

            @Override
            public Boolean visit(UntargettedBinding<? extends T> untargetted) {
                this.prepareBinding();
                if (this.key.getAnnotationType() != null) {
                    UntargettedBindingProcessor.this.errors.missingImplementation(this.key);
                    UntargettedBindingProcessor.this.putBinding(UntargettedBindingProcessor.this.invalidBinding(UntargettedBindingProcessor.this.injector, this.key, this.source));
                    return true;
                }
                try {
                    BindingImpl binding = UntargettedBindingProcessor.this.injector.createUninitializedBinding(this.key, this.scoping, this.source, UntargettedBindingProcessor.this.errors, false);
                    this.scheduleInitialization(binding);
                    UntargettedBindingProcessor.this.putBinding(binding);
                }
                catch (ErrorsException e) {
                    UntargettedBindingProcessor.this.errors.merge(e.getErrors());
                    UntargettedBindingProcessor.this.putBinding(UntargettedBindingProcessor.this.invalidBinding(UntargettedBindingProcessor.this.injector, this.key, this.source));
                }
                return true;
            }

            @Override
            protected Boolean visitOther(Binding<? extends T> binding) {
                return false;
            }
        });
    }
}

