/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.LazySeq;
import ginga.marshal$make_record_unmarshaller_STAR_$iter__20628__20632$fn__20633;

public final class marshal$make_record_unmarshaller_STAR_$iter__20628__20632
extends AFunction {
    Object default_syms;
    Object data_sym;
    Object defaults;
    Object kws;
    Object unmarshaller_sym;

    public marshal$make_record_unmarshaller_STAR_$iter__20628__20632(Object object, Object object2, Object object3, Object object4, Object object5) {
        this.default_syms = object;
        this.data_sym = object2;
        this.defaults = object3;
        this.kws = object4;
        this.unmarshaller_sym = object5;
    }

    @Override
    public Object invoke(Object s__20629) {
        Object object = s__20629;
        s__20629 = null;
        return new LazySeq(new marshal$make_record_unmarshaller_STAR_$iter__20628__20632$fn__20633(this.default_syms, this.data_sym, this.defaults, this.kws, this.unmarshaller_sym, this, object));
    }
}

