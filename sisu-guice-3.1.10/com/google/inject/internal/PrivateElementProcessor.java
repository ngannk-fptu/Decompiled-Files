/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.inject.internal;

import com.google.common.collect.Lists;
import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.Errors;
import com.google.inject.internal.InjectorShell;
import com.google.inject.spi.PrivateElements;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class PrivateElementProcessor
extends AbstractProcessor {
    private final List<InjectorShell.Builder> injectorShellBuilders = Lists.newArrayList();

    PrivateElementProcessor(Errors errors) {
        super(errors);
    }

    @Override
    public Boolean visit(PrivateElements privateElements) {
        InjectorShell.Builder builder = new InjectorShell.Builder().parent(this.injector).privateElements(privateElements);
        this.injectorShellBuilders.add(builder);
        return true;
    }

    public List<InjectorShell.Builder> getInjectorShellBuilders() {
        return this.injectorShellBuilders;
    }
}

