/*
 * Decompiled with CFR 0.152.
 */
package clout.core;

import clojure.lang.AFn;
import clojure.lang.APersistentMap;
import clojure.lang.Counted;
import clojure.lang.IFn;
import clojure.lang.IHashEq;
import clojure.lang.IKeywordLookup;
import clojure.lang.ILookup;
import clojure.lang.ILookupThunk;
import clojure.lang.IMapEntry;
import clojure.lang.IObj;
import clojure.lang.IPersistentCollection;
import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentVector;
import clojure.lang.IRecord;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.MapEntry;
import clojure.lang.Numbers;
import clojure.lang.PersistentArrayMap;
import clojure.lang.PersistentHashSet;
import clojure.lang.RT;
import clojure.lang.RecordIterator;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Util;
import clojure.lang.Var;
import clout.core.CompiledRoute$reify__34697;
import clout.core.CompiledRoute$reify__34699;
import clout.core.CompiledRoute$reify__34701;
import clout.core.CompiledRoute$reify__34703;
import clout.core.Route;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class CompiledRoute
implements Route,
IRecord,
IHashEq,
IObj,
ILookup,
IKeywordLookup,
IPersistentMap,
Map,
Serializable {
    public final Object source;
    public final Object re;
    public final Object keys;
    public final Object absolute_QMARK_;
    public final Object __meta;
    public final Object __extmap;
    int __hash;
    int __hasheq;
    public static final Var const__0 = RT.var("clojure.core", "set");
    public static final Var const__1 = RT.var("clojure.core", "vals");
    public static final Var const__2 = RT.var("clojure.core", "keys");
    public static final Var const__4 = RT.var("clojure.core", "some");
    public static final Var const__7 = RT.var("clojure.core", "contains?");
    public static final Keyword const__8 = RT.keyword(null, "re");
    public static final Keyword const__9 = RT.keyword(null, "source");
    public static final Keyword const__10 = RT.keyword(null, "keys");
    public static final Keyword const__11 = RT.keyword(null, "absolute?");
    public static final AFn const__12 = PersistentHashSet.create(RT.keyword(null, "re"), RT.keyword(null, "source"), RT.keyword(null, "keys"), RT.keyword(null, "absolute?"));
    public static final Var const__13 = RT.var("clojure.core", "dissoc");
    public static final Var const__14 = RT.var("clojure.core", "with-meta");
    public static final Var const__15 = RT.var("clojure.core", "into");
    public static final Var const__16 = RT.var("clojure.core", "not-empty");
    public static final Var const__17 = RT.var("clojure.core", "identical?");
    public static final Var const__18 = RT.var("clojure.core", "assoc");
    public static final AFn const__19 = (AFn)((Object)Tuple.create(RT.keyword(null, "source"), RT.keyword(null, "re"), RT.keyword(null, "keys"), RT.keyword(null, "absolute?")));
    public static final Var const__20 = RT.var("clojure.core", "seq");
    public static final Var const__21 = RT.var("clojure.core", "concat");
    public static final Var const__22 = RT.var("clojure.core", "not");
    public static final Var const__23 = RT.var("clojure.core", "class");
    public static final Var const__24 = RT.var("clojure.core", "imap-cons");
    public static final Var const__25 = RT.var("clojure.core", "str");
    public static final Var const__34 = RT.var("clout.core", "request-url");
    public static final Var const__35 = RT.var("clout.core", "path-info");
    public static final Var const__36 = RT.var("clout.core", "re-match-groups");
    public static final Var const__37 = RT.var("clout.core", "assoc-keys-with-groups");

    public CompiledRoute(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, int n, int n2) {
        this.source = object;
        this.re = object2;
        this.keys = object3;
        this.absolute_QMARK_ = object4;
        this.__meta = object5;
        this.__extmap = object6;
        this.__hash = n;
        this.__hasheq = n2;
    }

    public CompiledRoute(Object object, Object object2, Object object3, Object object4) {
        this(object, object2, object3, object4, null, null, 0, 0);
    }

    public CompiledRoute(Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
        this(object, object2, object3, object4, object5, object6, 0, 0);
    }

    public static IPersistentVector getBasis() {
        return Tuple.create(Symbol.intern(null, "source"), Symbol.intern(null, "re"), Symbol.intern(null, "keys"), Symbol.intern(null, "absolute?"));
    }

    public static CompiledRoute create(IPersistentMap iPersistentMap) {
        Object object = iPersistentMap.valAt(Keyword.intern("source"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("source"));
        Object object2 = iPersistentMap.valAt(Keyword.intern("re"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("re"));
        Object object3 = iPersistentMap.valAt(Keyword.intern("keys"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("keys"));
        Object object4 = iPersistentMap.valAt(Keyword.intern("absolute?"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("absolute?"));
        return new CompiledRoute(object, object2, object3, object4, null, RT.seqOrElse(iPersistentMap), 0, 0);
    }

    public String toString() {
        return (String)this.source;
    }

    @Override
    public Object route_matches(Object request2) {
        Object object;
        Object groups;
        Object path_info2;
        Object object2;
        Object object3 = this_.absolute_QMARK_;
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = request2;
            request2 = null;
            object2 = ((IFn)const__34.getRawRoot()).invoke(object4);
        } else {
            Object object5 = request2;
            request2 = null;
            object2 = ((IFn)const__35.getRawRoot()).invoke(object5);
        }
        Object object6 = path_info2 = object2;
        path_info2 = null;
        Object object7 = groups = ((IFn)const__36.getRawRoot()).invoke(this_.re, object6);
        if (object7 != null && object7 != Boolean.FALSE) {
            Object object8 = groups;
            groups = null;
            CompiledRoute this_ = null;
            object = ((IFn)const__37.getRawRoot()).invoke(object8, this_.keys);
        } else {
            object = null;
        }
        return object;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public int hasheq() {
        void v0;
        int hq__7852__auto__34708 = this.__hasheq;
        if ((long)hq__7852__auto__34708 == 0L) {
            void var2_2;
            int h__7853__auto__34707;
            this.__hasheq = h__7853__auto__34707 = RT.intCast(0xFFFFFFFF9B0E8516L ^ (long)APersistentMap.mapHasheq(this));
            v0 = var2_2;
        } else {
            void var1_1;
            v0 = var1_1;
        }
        return (int)v0;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public int hashCode() {
        void v0;
        int hash__7855__auto__34710 = this.__hash;
        if ((long)hash__7855__auto__34710 == 0L) {
            void var2_2;
            int h__7856__auto__34709;
            this.__hash = h__7856__auto__34709 = APersistentMap.mapHash(this);
            v0 = var2_2;
        } else {
            void var1_1;
            v0 = var1_1;
        }
        return (int)v0;
    }

    @Override
    public boolean equals(Object G__34692) {
        Object object = G__34692;
        G__34692 = null;
        return APersistentMap.mapEquals(this, object);
    }

    @Override
    public IPersistentMap meta() {
        return (IPersistentMap)this.__meta;
    }

    @Override
    public IObj withMeta(IPersistentMap G__34692) {
        IPersistentMap iPersistentMap = G__34692;
        G__34692 = null;
        return new CompiledRoute(this.source, this.re, this.keys, this.absolute_QMARK_, iPersistentMap, this.__extmap, this.__hash, this.__hasheq);
    }

    @Override
    public Object valAt(Object k__7861__auto__) {
        Object object = k__7861__auto__;
        k__7861__auto__ = null;
        return ((ILookup)this).valAt(object, null);
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public Object valAt(Object k__7863__auto__, Object else__7864__auto__) {
        Object object;
        Object G__34705 = k__7863__auto__;
        switch (Util.hash(G__34705) >> 4 & 3) {
            case 0: {
                if (G__34705 != const__11) break;
                object = this_.absolute_QMARK_;
                return object;
            }
            case 1: {
                if (G__34705 != const__9) break;
                object = this_.source;
                return object;
            }
            case 2: {
                if (G__34705 != const__8) break;
                object = this_.re;
                return object;
            }
            case 3: {
                if (G__34705 != const__10) break;
                object = this_.keys;
                return object;
            }
        }
        Object object2 = k__7863__auto__;
        k__7863__auto__ = null;
        Object object3 = else__7864__auto__;
        else__7864__auto__ = null;
        CompiledRoute this_ = null;
        object = RT.get(this_.__extmap, object2, object3);
        return object;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public ILookupThunk getLookupThunk(Keyword k__7866__auto__) {
        IObj iObj;
        Object gclass = ((IFn)const__23.getRawRoot()).invoke(this);
        Keyword keyword2 = k__7866__auto__;
        k__7866__auto__ = null;
        Keyword G__34696 = keyword2;
        switch (Util.hash(G__34696) >> 4 & 3) {
            case 0: {
                if (G__34696 != const__11) break;
                gclass = null;
                iObj = new CompiledRoute$reify__34697(null, gclass);
                return iObj;
            }
            case 1: {
                if (G__34696 != const__9) break;
                gclass = null;
                iObj = new CompiledRoute$reify__34699(null, gclass);
                return iObj;
            }
            case 2: {
                if (G__34696 != const__8) break;
                gclass = null;
                iObj = new CompiledRoute$reify__34701(null, gclass);
                return iObj;
            }
            case 3: {
                if (G__34696 != const__10) break;
                gclass = null;
                iObj = new CompiledRoute$reify__34703(null, gclass);
                return iObj;
            }
        }
        iObj = null;
        return iObj;
    }

    @Override
    public int count() {
        return RT.intCast(Numbers.add(4L, (long)RT.count(this.__extmap)));
    }

    @Override
    public IPersistentCollection empty() {
        throw (Throwable)new UnsupportedOperationException((String)((IFn)const__25.getRawRoot()).invoke("Can't create empty: ", "clout.core.CompiledRoute"));
    }

    @Override
    public IPersistentCollection cons(Object e__7870__auto__) {
        CompiledRoute compiledRoute = this_;
        Object object = e__7870__auto__;
        e__7870__auto__ = null;
        CompiledRoute this_ = null;
        return (IPersistentCollection)((IFn)const__24).invoke(compiledRoute, object);
    }

    @Override
    public boolean equiv(Object G__34692) {
        Boolean bl;
        boolean or__5581__auto__34715 = Util.identical(this, G__34692);
        if (or__5581__auto__34715) {
            bl = or__5581__auto__34715 ? Boolean.TRUE : Boolean.FALSE;
        } else if (Util.identical(((IFn)const__23.getRawRoot()).invoke(this), ((IFn)const__23.getRawRoot()).invoke(G__34692))) {
            Object object = G__34692;
            G__34692 = null;
            Object G__346922 = object;
            boolean and__5579__auto__34714 = Util.equiv(this.source, ((CompiledRoute)G__346922).source);
            if (and__5579__auto__34714) {
                boolean and__5579__auto__34713 = Util.equiv(this.re, ((CompiledRoute)G__346922).re);
                if (and__5579__auto__34713) {
                    boolean and__5579__auto__34712 = Util.equiv(this.keys, ((CompiledRoute)G__346922).keys);
                    if (and__5579__auto__34712) {
                        boolean and__5579__auto__34711 = Util.equiv(this.absolute_QMARK_, ((CompiledRoute)G__346922).absolute_QMARK_);
                        if (and__5579__auto__34711) {
                            Object object2 = G__346922;
                            G__346922 = null;
                            bl = Util.equiv(this.__extmap, ((CompiledRoute)object2).__extmap) ? Boolean.TRUE : Boolean.FALSE;
                        } else {
                            bl = and__5579__auto__34711 ? Boolean.TRUE : Boolean.FALSE;
                        }
                    } else {
                        bl = and__5579__auto__34712 ? Boolean.TRUE : Boolean.FALSE;
                    }
                } else {
                    bl = and__5579__auto__34713 ? Boolean.TRUE : Boolean.FALSE;
                }
            } else {
                bl = and__5579__auto__34714 ? Boolean.TRUE : Boolean.FALSE;
            }
        } else {
            bl = null;
        }
        return RT.booleanCast(bl);
    }

    @Override
    public boolean containsKey(Object k__7873__auto__) {
        Object object = k__7873__auto__;
        k__7873__auto__ = null;
        Boolean bl = Util.identical(this_, ((ILookup)this_).valAt(object, this_)) ? Boolean.TRUE : Boolean.FALSE;
        CompiledRoute this_ = null;
        return (Boolean)((IFn)const__22.getRawRoot()).invoke(bl);
    }

    @Override
    public IMapEntry entryAt(Object k__7875__auto__) {
        MapEntry mapEntry;
        Object v__7876__auto__34716 = ((ILookup)this_).valAt(k__7875__auto__, this_);
        if (Util.identical(this_, v__7876__auto__34716)) {
            mapEntry = null;
        } else {
            Object object = k__7875__auto__;
            k__7875__auto__ = null;
            Object object2 = v__7876__auto__34716;
            v__7876__auto__34716 = null;
            CompiledRoute this_ = null;
            mapEntry = MapEntry.create(object, object2);
        }
        return mapEntry;
    }

    @Override
    public ISeq seq() {
        CompiledRoute this_ = null;
        return (ISeq)((IFn)const__20.getRawRoot()).invoke(((IFn)const__21.getRawRoot()).invoke(Tuple.create(MapEntry.create(const__9, this_.source), MapEntry.create(const__8, this_.re), MapEntry.create(const__10, this_.keys), MapEntry.create(const__11, this_.absolute_QMARK_)), this_.__extmap));
    }

    public Iterator iterator() {
        return new RecordIterator(this, (IPersistentVector)((Object)const__19), RT.iter(this.__extmap));
    }

    @Override
    public IPersistentMap assoc(Object k__7880__auto__, Object G__34692) {
        CompiledRoute compiledRoute;
        Object pred__34694 = const__17.getRawRoot();
        Object expr__34695 = k__7880__auto__;
        Object object = ((IFn)pred__34694).invoke(const__9, expr__34695);
        if (object != null && object != Boolean.FALSE) {
            G__34692 = null;
            compiledRoute = new CompiledRoute(G__34692, this.re, this.keys, this.absolute_QMARK_, this.__meta, this.__extmap);
        } else {
            Object object2 = ((IFn)pred__34694).invoke(const__8, expr__34695);
            if (object2 != null && object2 != Boolean.FALSE) {
                G__34692 = null;
                compiledRoute = new CompiledRoute(this.source, G__34692, this.keys, this.absolute_QMARK_, this.__meta, this.__extmap);
            } else {
                Object object3 = ((IFn)pred__34694).invoke(const__10, expr__34695);
                if (object3 != null && object3 != Boolean.FALSE) {
                    G__34692 = null;
                    compiledRoute = new CompiledRoute(this.source, this.re, G__34692, this.absolute_QMARK_, this.__meta, this.__extmap);
                } else {
                    Object object4 = pred__34694;
                    pred__34694 = null;
                    Object object5 = expr__34695;
                    expr__34695 = null;
                    Object object6 = ((IFn)object4).invoke(const__11, object5);
                    if (object6 != null && object6 != Boolean.FALSE) {
                        G__34692 = null;
                        compiledRoute = new CompiledRoute(this.source, this.re, this.keys, G__34692, this.__meta, this.__extmap);
                    } else {
                        k__7880__auto__ = null;
                        G__34692 = null;
                        compiledRoute = new CompiledRoute(this.source, this.re, this.keys, this.absolute_QMARK_, this.__meta, ((IFn)const__18.getRawRoot()).invoke(this.__extmap, k__7880__auto__, G__34692));
                    }
                }
            }
        }
        return compiledRoute;
    }

    @Override
    public IPersistentMap without(Object k__7882__auto__) {
        Object object;
        Object object2 = ((IFn)const__7.getRawRoot()).invoke(const__12, k__7882__auto__);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = ((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(PersistentArrayMap.EMPTY, this_), this_.__meta);
            Object object4 = k__7882__auto__;
            k__7882__auto__ = null;
            CompiledRoute this_ = null;
            object = ((IFn)const__13.getRawRoot()).invoke(object3, object4);
        } else {
            k__7882__auto__ = null;
            object = new CompiledRoute(this_.source, this_.re, this_.keys, this_.absolute_QMARK_, this_.__meta, ((IFn)const__16.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(this_.__extmap, k__7882__auto__)));
        }
        return (IPersistentMap)object;
    }

    @Override
    public int size() {
        Counted counted = this_;
        CompiledRoute this_ = null;
        return counted.count();
    }

    @Override
    public boolean isEmpty() {
        return Util.equiv(0L, (long)((Counted)this).count());
    }

    @Override
    public boolean containsValue(Object v__7886__auto__) {
        Object[] objectArray = new Object[1];
        Object object = v__7886__auto__;
        v__7886__auto__ = null;
        objectArray[0] = object;
        return RT.booleanCast(((IFn)const__4.getRawRoot()).invoke(RT.set(objectArray), ((IFn)const__1.getRawRoot()).invoke(this)));
    }

    public Object get(Object k__7888__auto__) {
        Object object = k__7888__auto__;
        k__7888__auto__ = null;
        return ((ILookup)this).valAt(object);
    }

    public Object put(Object k__7890__auto__, Object v__7891__auto__) {
        throw (Throwable)new UnsupportedOperationException();
    }

    public Object remove(Object k__7893__auto__) {
        throw (Throwable)new UnsupportedOperationException();
    }

    public void putAll(Map m__7895__auto__) {
        throw (Throwable)new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw (Throwable)new UnsupportedOperationException();
    }

    public Set keySet() {
        Object object = ((IFn)const__2.getRawRoot()).invoke(this_);
        CompiledRoute this_ = null;
        return (Set)((IFn)const__0.getRawRoot()).invoke(object);
    }

    public Collection values() {
        CompiledRoute compiledRoute = this_;
        CompiledRoute this_ = null;
        return (Collection)((IFn)const__1.getRawRoot()).invoke(compiledRoute);
    }

    public Set entrySet() {
        CompiledRoute compiledRoute = this_;
        CompiledRoute this_ = null;
        return (Set)((IFn)const__0.getRawRoot()).invoke(compiledRoute);
    }
}

