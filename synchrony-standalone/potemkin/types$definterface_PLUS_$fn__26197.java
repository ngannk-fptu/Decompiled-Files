/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;

public final class types$definterface_PLUS_$fn__26197
extends AFunction {
    Object class_name;

    public types$definterface_PLUS_$fn__26197(Object object) {
        this.class_name = object;
    }

    @Override
    public Object invoke() {
        Boolean bl;
        try {
            this.class_name = null;
            Class.forName((String)this.class_name);
            bl = Boolean.TRUE;
        }
        catch (Exception _2) {
            bl = Boolean.FALSE;
        }
        return bl;
    }
}

