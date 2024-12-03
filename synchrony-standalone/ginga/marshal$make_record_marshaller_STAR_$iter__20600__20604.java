/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.LazySeq;
import ginga.marshal$make_record_marshaller_STAR_$iter__20600__20604$fn__20605;

public final class marshal$make_record_marshaller_STAR_$iter__20600__20604
extends AFunction {
    Object record_sym;
    Object marshaller_sym;

    public marshal$make_record_marshaller_STAR_$iter__20600__20604(Object object, Object object2) {
        this.record_sym = object;
        this.marshaller_sym = object2;
    }

    @Override
    public Object invoke(Object s__20601) {
        Object object = s__20601;
        s__20601 = null;
        return new LazySeq(new marshal$make_record_marshaller_STAR_$iter__20600__20604$fn__20605(object, this.record_sym, this.marshaller_sym, this));
    }
}

