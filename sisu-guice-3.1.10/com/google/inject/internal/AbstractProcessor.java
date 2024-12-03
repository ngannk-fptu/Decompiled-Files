/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.Errors;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InjectorShell;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class AbstractProcessor
extends DefaultElementVisitor<Boolean> {
    protected Errors errors;
    protected InjectorImpl injector;

    protected AbstractProcessor(Errors errors) {
        this.errors = errors;
    }

    public void process(Iterable<InjectorShell> isolatedInjectorBuilders) {
        for (InjectorShell injectorShell : isolatedInjectorBuilders) {
            this.process(injectorShell.getInjector(), injectorShell.getElements());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void process(InjectorImpl injector, List<Element> elements) {
        Errors errorsAnyElement = this.errors;
        this.injector = injector;
        try {
            Iterator<Element> i = elements.iterator();
            while (i.hasNext()) {
                Element element = i.next();
                this.errors = errorsAnyElement.withSource(element.getSource());
                Boolean allDone = element.acceptVisitor(this);
                if (!allDone.booleanValue()) continue;
                i.remove();
            }
        }
        finally {
            this.errors = errorsAnyElement;
            this.injector = null;
        }
    }

    @Override
    protected Boolean visitOther(Element element) {
        return false;
    }
}

