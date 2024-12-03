/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.types$deftype__GT_deftype_STAR_$find_deftype_STAR___26152;
import potemkin.types$deftype__GT_deftype_STAR_$remove_nil_implements__26154;

public final class types$deftype__GT_deftype_STAR_
extends AFunction {
    public static final Var const__0 = RT.var("riddley.walk", "macroexpand");

    public static Object invokeStatic(Object x) {
        types$deftype__GT_deftype_STAR_$remove_nil_implements__26154 remove_nil_implements;
        Object object = x;
        x = null;
        Object x2 = ((IFn)const__0.getRawRoot()).invoke(object);
        types$deftype__GT_deftype_STAR_$find_deftype_STAR___26152 find_deftype_STAR_ = new types$deftype__GT_deftype_STAR_$find_deftype_STAR___26152();
        types$deftype__GT_deftype_STAR_$remove_nil_implements__26154 types$deftype__GT_deftype_STAR_$remove_nil_implements__26154 = remove_nil_implements = new types$deftype__GT_deftype_STAR_$remove_nil_implements__26154();
        remove_nil_implements = null;
        types$deftype__GT_deftype_STAR_$find_deftype_STAR___26152 types$deftype__GT_deftype_STAR_$find_deftype_STAR___26152 = find_deftype_STAR_;
        find_deftype_STAR_ = null;
        Object object2 = x2;
        x2 = null;
        return ((IFn)types$deftype__GT_deftype_STAR_$remove_nil_implements__26154).invoke(((IFn)types$deftype__GT_deftype_STAR_$find_deftype_STAR___26152).invoke(object2));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$deftype__GT_deftype_STAR_.invokeStatic(object2);
    }
}

