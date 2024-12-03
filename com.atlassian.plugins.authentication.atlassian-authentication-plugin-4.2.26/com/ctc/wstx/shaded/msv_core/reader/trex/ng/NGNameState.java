/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithChildState;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public abstract class NGNameState
extends NameClassWithChildState {
    NGNameState() {
        this.allowNullChild = true;
    }

    protected State createChildState(StartTagInfo tag) {
        if (this.nameClass == null && tag.localName.equals("except")) {
            return ((RELAXNGReader)this.reader).getStateFactory().nsExcept(this, tag);
        }
        return null;
    }

    protected NameClass castNameClass(NameClass halfCastedNameClass, NameClass newChildNameClass) {
        return newChildNameClass;
    }

    protected NameClass annealNameClass(NameClass nameClass) {
        NameClass r = this.getMainNameClass();
        if (nameClass != null) {
            r = new DifferenceNameClass(r, nameClass);
        }
        return r;
    }

    protected abstract NameClass getMainNameClass();

    public static class NsNameState
    extends NGNameState {
        protected NameClass getMainNameClass() {
            return new NamespaceNameClass(this.getPropagatedNamespace());
        }
    }

    public static class AnyNameState
    extends NGNameState {
        protected NameClass getMainNameClass() {
            return NameClass.ALL;
        }
    }
}

