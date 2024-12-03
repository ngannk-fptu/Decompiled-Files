/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.validation;

import java.util.ArrayList;
import org.apache.xerces.impl.validation.EntityState;
import org.apache.xerces.impl.validation.ValidationState;

public class ValidationManager {
    protected final ArrayList fVSs = new ArrayList();
    protected boolean fGrammarFound = false;
    protected boolean fCachedDTD = false;

    public final void addValidationState(ValidationState validationState) {
        this.fVSs.add(validationState);
    }

    public final void setEntityState(EntityState entityState) {
        for (int i = this.fVSs.size() - 1; i >= 0; --i) {
            ((ValidationState)this.fVSs.get(i)).setEntityState(entityState);
        }
    }

    public final void setGrammarFound(boolean bl) {
        this.fGrammarFound = bl;
    }

    public final boolean isGrammarFound() {
        return this.fGrammarFound;
    }

    public final void setCachedDTD(boolean bl) {
        this.fCachedDTD = bl;
    }

    public final boolean isCachedDTD() {
        return this.fCachedDTD;
    }

    public final void reset() {
        this.fVSs.clear();
        this.fGrammarFound = false;
        this.fCachedDTD = false;
    }
}

