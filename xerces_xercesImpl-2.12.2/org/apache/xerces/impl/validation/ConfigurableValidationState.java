/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.validation;

import java.util.Iterator;
import org.apache.xerces.impl.validation.ValidationState;

public final class ConfigurableValidationState
extends ValidationState {
    private boolean fIdIdrefChecking = true;
    private boolean fUnparsedEntityChecking = true;

    public void setIdIdrefChecking(boolean bl) {
        this.fIdIdrefChecking = bl;
    }

    public void setUnparsedEntityChecking(boolean bl) {
        this.fUnparsedEntityChecking = bl;
    }

    @Override
    public Iterator checkIDRefID() {
        return this.fIdIdrefChecking ? super.checkIDRefID() : null;
    }

    @Override
    public boolean isIdDeclared(String string) {
        return this.fIdIdrefChecking ? super.isIdDeclared(string) : false;
    }

    @Override
    public boolean isEntityDeclared(String string) {
        return this.fUnparsedEntityChecking ? super.isEntityDeclared(string) : true;
    }

    @Override
    public boolean isEntityUnparsed(String string) {
        return this.fUnparsedEntityChecking ? super.isEntityUnparsed(string) : true;
    }

    @Override
    public void addId(String string) {
        if (this.fIdIdrefChecking) {
            super.addId(string);
        }
    }

    @Override
    public void addIdRef(String string) {
        if (this.fIdIdrefChecking) {
            super.addIdRef(string);
        }
    }
}

