/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class chash$loading__6789__auto____14602
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "refer");
    public static final AFn const__1 = Symbol.intern(null, "clojure.core");
    public static final Keyword const__2 = RT.keyword(null, "exclude");
    public static final AFn const__3 = (AFn)((Object)Tuple.create(Symbol.intern(null, "replicate")));
    public static final Var const__4 = RT.var("clojure.core", "require");
    public static final AFn const__5 = (AFn)((Object)Tuple.create(Symbol.intern(null, "ginga.core"), RT.keyword(null, "as"), Symbol.intern(null, "g")));
    public static final AFn const__6 = (AFn)((Object)Tuple.create(Symbol.intern(null, "ginga.hash"), RT.keyword(null, "refer"), Tuple.create(Symbol.intern(null, "hash-bytes"))));
    public static final AFn const__7 = (AFn)((Object)Tuple.create(Symbol.intern(null, "clojure.data.codec.base64"), RT.keyword(null, "as"), Symbol.intern(null, "b64")));

    @Override
    public Object invoke() {
        Object object;
        Var.pushThreadBindings(RT.mapUniqueKeys(Compiler.LOADER, this.getClass().getClassLoader()));
        try {
            ((IFn)const__0.getRawRoot()).invoke(const__1, const__2, const__3);
            object = ((IFn)const__4.getRawRoot()).invoke(const__5, const__6, const__7);
        }
        finally {
            Var.popThreadBindings();
        }
        return object;
    }
}

