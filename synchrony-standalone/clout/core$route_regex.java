/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Var;
import clout.core$route_regex$fn__34740;
import clout.core$route_regex$fn__34742;

public final class core$route_regex
extends AFunction {
    public static final Var const__0 = RT.var("instaparse.core", "transform");
    public static final Keyword const__1 = RT.keyword(null, "route");
    public static final Var const__2 = RT.var("clojure.core", "comp");
    public static final Var const__3 = RT.var("clojure.core", "re-pattern");
    public static final Var const__4 = RT.var("clojure.core", "str");
    public static final Keyword const__5 = RT.keyword(null, "scheme");
    public static final Keyword const__6 = RT.keyword(null, "literal");
    public static final Var const__7 = RT.var("clout.core", "re-escape");
    public static final Keyword const__8 = RT.keyword(null, "escaped");
    public static final Keyword const__9 = RT.keyword(null, "wildcard");
    public static final Var const__10 = RT.var("clojure.core", "constantly");
    public static final Keyword const__11 = RT.keyword(null, "param");
    public static final Var const__12 = RT.var("clojure.core", "partial");
    public static final Var const__13 = RT.var("clout.core", "param-regex");
    public static final Keyword const__14 = RT.keyword(null, "key");
    public static final Var const__15 = RT.var("clojure.core", "keyword");
    public static final Keyword const__16 = RT.keyword(null, "pattern");

    public static Object invokeStatic(Object parse_tree, Object regexs) {
        Object[] objectArray = new Object[16];
        objectArray[0] = const__1;
        objectArray[1] = ((IFn)const__2.getRawRoot()).invoke(const__3.getRawRoot(), const__4.getRawRoot());
        objectArray[2] = const__5;
        objectArray[3] = new core$route_regex$fn__34740();
        objectArray[4] = const__6;
        objectArray[5] = const__7.getRawRoot();
        objectArray[6] = const__8;
        objectArray[7] = new core$route_regex$fn__34742();
        objectArray[8] = const__9;
        objectArray[9] = ((IFn)const__10.getRawRoot()).invoke("(.*?)");
        objectArray[10] = const__11;
        Object object = regexs;
        regexs = null;
        objectArray[11] = ((IFn)const__12.getRawRoot()).invoke(const__13.getRawRoot(), object);
        objectArray[12] = const__14;
        objectArray[13] = const__15.getRawRoot();
        objectArray[14] = const__16;
        objectArray[15] = const__4.getRawRoot();
        Object object2 = parse_tree;
        parse_tree = null;
        return ((IFn)const__0.getRawRoot()).invoke(RT.mapUniqueKeys(objectArray), object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$route_regex.invokeStatic(object3, object4);
    }
}

