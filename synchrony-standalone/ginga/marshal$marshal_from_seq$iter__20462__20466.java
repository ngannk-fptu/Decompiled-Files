/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.LazySeq;
import ginga.marshal$marshal_from_seq$iter__20462__20466$fn__20467;

public final class marshal$marshal_from_seq$iter__20462__20466
extends AFunction {
    Object marshaller;

    public marshal$marshal_from_seq$iter__20462__20466(Object object) {
        this.marshaller = object;
    }

    @Override
    public Object invoke(Object s__20463) {
        Object object = s__20463;
        s__20463 = null;
        return new LazySeq(new marshal$marshal_from_seq$iter__20462__20466$fn__20467(this, object, this.marshaller));
    }
}

