/*
 * Decompiled with CFR 0.152.
 */
package ginga.async.haywire;

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
import clojure.lang.Util;
import clojure.lang.Var;
import ginga.async.haywire.Connection$reify__10950;
import ginga.async.haywire.Connection$reify__10952;
import ginga.async.haywire.Connection$reify__10954;
import ginga.async.haywire.Connection$reify__10956;
import ginga.async.haywire.Connection$reify__10958;
import ginga.async.haywire.Connection$reify__10960;
import ginga.async.haywire.Connection$reify__10962;
import ginga.async.haywire.Connection$reify__10964;
import ginga.async.haywire.Connection$reify__10966;
import ginga.async.haywire.Connection$reify__10968;
import ginga.async.haywire.Connection$reify__10970;
import ginga.async.haywire.Connection$reify__10972;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class Connection
implements IRecord,
IHashEq,
IObj,
ILookup,
IKeywordLookup,
IPersistentMap,
Map,
Serializable {
    public final Object alive;
    public final Object close_promise;
    public final Object seq_state;
    public final Object reorder_xf;
    public final Object buffer_stats;
    public final Object win_in_stats;
    public final Object win_out_stats;
    public final Object downstream;
    public final Object upstream;
    public final Object receive_buf;
    public final Object in;
    public final Object out;
    public final Object __meta;
    public final Object __extmap;
    int __hash;
    int __hasheq;
    public static final Var const__0 = RT.var("clojure.core", "set");
    public static final Var const__1 = RT.var("clojure.core", "vals");
    public static final Var const__2 = RT.var("clojure.core", "keys");
    public static final Var const__4 = RT.var("clojure.core", "some");
    public static final Var const__7 = RT.var("clojure.core", "contains?");
    public static final Keyword const__8 = RT.keyword(null, "downstream");
    public static final Keyword const__9 = RT.keyword(null, "seq-state");
    public static final Keyword const__10 = RT.keyword(null, "alive");
    public static final Keyword const__11 = RT.keyword(null, "win-in-stats");
    public static final Keyword const__12 = RT.keyword(null, "close-promise");
    public static final Keyword const__13 = RT.keyword(null, "out");
    public static final Keyword const__14 = RT.keyword(null, "buffer-stats");
    public static final Keyword const__15 = RT.keyword(null, "receive-buf");
    public static final Keyword const__16 = RT.keyword(null, "upstream");
    public static final Keyword const__17 = RT.keyword(null, "win-out-stats");
    public static final Keyword const__18 = RT.keyword(null, "in");
    public static final Keyword const__19 = RT.keyword(null, "reorder-xf");
    public static final AFn const__20 = PersistentHashSet.create(RT.keyword(null, "downstream"), RT.keyword(null, "seq-state"), RT.keyword(null, "alive"), RT.keyword(null, "win-in-stats"), RT.keyword(null, "close-promise"), RT.keyword(null, "out"), RT.keyword(null, "buffer-stats"), RT.keyword(null, "receive-buf"), RT.keyword(null, "upstream"), RT.keyword(null, "win-out-stats"), RT.keyword(null, "in"), RT.keyword(null, "reorder-xf"));
    public static final Var const__21 = RT.var("clojure.core", "dissoc");
    public static final Var const__22 = RT.var("clojure.core", "with-meta");
    public static final Var const__23 = RT.var("clojure.core", "into");
    public static final Var const__24 = RT.var("clojure.core", "not-empty");
    public static final Var const__25 = RT.var("clojure.core", "identical?");
    public static final Var const__26 = RT.var("clojure.core", "assoc");
    public static final AFn const__27 = (AFn)((Object)RT.vector(RT.keyword(null, "alive"), RT.keyword(null, "close-promise"), RT.keyword(null, "seq-state"), RT.keyword(null, "reorder-xf"), RT.keyword(null, "buffer-stats"), RT.keyword(null, "win-in-stats"), RT.keyword(null, "win-out-stats"), RT.keyword(null, "downstream"), RT.keyword(null, "upstream"), RT.keyword(null, "receive-buf"), RT.keyword(null, "in"), RT.keyword(null, "out")));
    public static final Var const__28 = RT.var("clojure.core", "seq");
    public static final Var const__29 = RT.var("clojure.core", "concat");
    public static final Var const__30 = RT.var("clojure.core", "not");
    public static final Var const__31 = RT.var("clojure.core", "class");
    public static final Var const__32 = RT.var("clojure.core", "imap-cons");
    public static final Var const__33 = RT.var("clojure.core", "str");

    public Connection(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9, Object object10, Object object11, Object object12, Object object13, Object object14, int n, int n2) {
        this.alive = object;
        this.close_promise = object2;
        this.seq_state = object3;
        this.reorder_xf = object4;
        this.buffer_stats = object5;
        this.win_in_stats = object6;
        this.win_out_stats = object7;
        this.downstream = object8;
        this.upstream = object9;
        this.receive_buf = object10;
        this.in = object11;
        this.out = object12;
        this.__meta = object13;
        this.__extmap = object14;
        this.__hash = n;
        this.__hasheq = n2;
    }

    public Connection(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9, Object object10, Object object11, Object object12) {
        this(object, object2, object3, object4, object5, object6, object7, object8, object9, object10, object11, object12, null, null, 0, 0);
    }

    public Connection(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9, Object object10, Object object11, Object object12, Object object13, Object object14) {
        this(object, object2, object3, object4, object5, object6, object7, object8, object9, object10, object11, object12, object13, object14, 0, 0);
    }

    public static IPersistentVector getBasis() {
        return RT.vector(Symbol.intern(null, "alive"), Symbol.intern(null, "close-promise"), Symbol.intern(null, "seq-state"), Symbol.intern(null, "reorder-xf"), Symbol.intern(null, "buffer-stats"), Symbol.intern(null, "win-in-stats"), Symbol.intern(null, "win-out-stats"), Symbol.intern(null, "downstream"), Symbol.intern(null, "upstream"), Symbol.intern(null, "receive-buf"), Symbol.intern(null, "in"), Symbol.intern(null, "out"));
    }

    public static Connection create(IPersistentMap iPersistentMap) {
        Object object = iPersistentMap.valAt(Keyword.intern("alive"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("alive"));
        Object object2 = iPersistentMap.valAt(Keyword.intern("close-promise"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("close-promise"));
        Object object3 = iPersistentMap.valAt(Keyword.intern("seq-state"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("seq-state"));
        Object object4 = iPersistentMap.valAt(Keyword.intern("reorder-xf"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("reorder-xf"));
        Object object5 = iPersistentMap.valAt(Keyword.intern("buffer-stats"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("buffer-stats"));
        Object object6 = iPersistentMap.valAt(Keyword.intern("win-in-stats"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("win-in-stats"));
        Object object7 = iPersistentMap.valAt(Keyword.intern("win-out-stats"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("win-out-stats"));
        Object object8 = iPersistentMap.valAt(Keyword.intern("downstream"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("downstream"));
        Object object9 = iPersistentMap.valAt(Keyword.intern("upstream"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("upstream"));
        Object object10 = iPersistentMap.valAt(Keyword.intern("receive-buf"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("receive-buf"));
        Object object11 = iPersistentMap.valAt(Keyword.intern("in"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("in"));
        Object object12 = iPersistentMap.valAt(Keyword.intern("out"), null);
        iPersistentMap = iPersistentMap.without(Keyword.intern("out"));
        return new Connection(object, object2, object3, object4, object5, object6, object7, object8, object9, object10, object11, object12, null, RT.seqOrElse(iPersistentMap), 0, 0);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public int hasheq() {
        void v0;
        int hq__7852__auto__10977 = this.__hasheq;
        if ((long)hq__7852__auto__10977 == 0L) {
            void var2_2;
            int h__7853__auto__10976;
            this.__hasheq = h__7853__auto__10976 = RT.intCast(0x7B5BB9CBL ^ (long)APersistentMap.mapHasheq(this));
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
        int hash__7855__auto__10979 = this.__hash;
        if ((long)hash__7855__auto__10979 == 0L) {
            void var2_2;
            int h__7856__auto__10978;
            this.__hash = h__7856__auto__10978 = APersistentMap.mapHash(this);
            v0 = var2_2;
        } else {
            void var1_1;
            v0 = var1_1;
        }
        return (int)v0;
    }

    @Override
    public boolean equals(Object G__10945) {
        Object object = G__10945;
        G__10945 = null;
        return APersistentMap.mapEquals(this, object);
    }

    @Override
    public IPersistentMap meta() {
        return (IPersistentMap)this.__meta;
    }

    @Override
    public IObj withMeta(IPersistentMap G__10945) {
        IPersistentMap iPersistentMap = G__10945;
        G__10945 = null;
        return new Connection(this.alive, this.close_promise, this.seq_state, this.reorder_xf, this.buffer_stats, this.win_in_stats, this.win_out_stats, this.downstream, this.upstream, this.receive_buf, this.in, this.out, iPersistentMap, this.__extmap, this.__hash, this.__hasheq);
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
        Object G__10974 = k__7863__auto__;
        switch (Util.hash(G__10974) >> 10 & 0x1F) {
            case 1: {
                if (G__10974 != const__10) break;
                object = this_.alive;
                return object;
            }
            case 3: {
                if (G__10974 != const__13) break;
                object = this_.out;
                return object;
            }
            case 4: {
                if (G__10974 != const__15) break;
                object = this_.receive_buf;
                return object;
            }
            case 6: {
                if (G__10974 != const__16) break;
                object = this_.upstream;
                return object;
            }
            case 9: {
                if (G__10974 != const__8) break;
                object = this_.downstream;
                return object;
            }
            case 16: {
                if (G__10974 != const__12) break;
                object = this_.close_promise;
                return object;
            }
            case 17: {
                if (G__10974 != const__18) break;
                object = this_.in;
                return object;
            }
            case 20: {
                if (G__10974 != const__9) break;
                object = this_.seq_state;
                return object;
            }
            case 23: {
                if (G__10974 != const__19) break;
                object = this_.reorder_xf;
                return object;
            }
            case 24: {
                if (G__10974 != const__11) break;
                object = this_.win_in_stats;
                return object;
            }
            case 29: {
                if (G__10974 != const__17) break;
                object = this_.win_out_stats;
                return object;
            }
            case 30: {
                if (G__10974 != const__14) break;
                object = this_.buffer_stats;
                return object;
            }
        }
        Object object2 = k__7863__auto__;
        k__7863__auto__ = null;
        Object object3 = else__7864__auto__;
        else__7864__auto__ = null;
        Connection this_ = null;
        object = RT.get(this_.__extmap, object2, object3);
        return object;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public ILookupThunk getLookupThunk(Keyword k__7866__auto__) {
        IObj iObj;
        Object gclass = ((IFn)const__31.getRawRoot()).invoke(this);
        Keyword keyword2 = k__7866__auto__;
        k__7866__auto__ = null;
        Keyword G__10949 = keyword2;
        switch (Util.hash(G__10949) >> 10 & 0x1F) {
            case 1: {
                if (G__10949 != const__10) break;
                gclass = null;
                iObj = new Connection$reify__10950(null, gclass);
                return iObj;
            }
            case 3: {
                if (G__10949 != const__13) break;
                gclass = null;
                iObj = new Connection$reify__10952(null, gclass);
                return iObj;
            }
            case 4: {
                if (G__10949 != const__15) break;
                gclass = null;
                iObj = new Connection$reify__10954(null, gclass);
                return iObj;
            }
            case 6: {
                if (G__10949 != const__16) break;
                gclass = null;
                iObj = new Connection$reify__10956(null, gclass);
                return iObj;
            }
            case 9: {
                if (G__10949 != const__8) break;
                gclass = null;
                iObj = new Connection$reify__10958(null, gclass);
                return iObj;
            }
            case 16: {
                if (G__10949 != const__12) break;
                gclass = null;
                iObj = new Connection$reify__10960(null, gclass);
                return iObj;
            }
            case 17: {
                if (G__10949 != const__18) break;
                gclass = null;
                iObj = new Connection$reify__10962(null, gclass);
                return iObj;
            }
            case 20: {
                if (G__10949 != const__9) break;
                gclass = null;
                iObj = new Connection$reify__10964(null, gclass);
                return iObj;
            }
            case 23: {
                if (G__10949 != const__19) break;
                gclass = null;
                iObj = new Connection$reify__10966(null, gclass);
                return iObj;
            }
            case 24: {
                if (G__10949 != const__11) break;
                gclass = null;
                iObj = new Connection$reify__10968(null, gclass);
                return iObj;
            }
            case 29: {
                if (G__10949 != const__17) break;
                gclass = null;
                iObj = new Connection$reify__10970(null, gclass);
                return iObj;
            }
            case 30: {
                if (G__10949 != const__14) break;
                gclass = null;
                iObj = new Connection$reify__10972(null, gclass);
                return iObj;
            }
        }
        iObj = null;
        return iObj;
    }

    @Override
    public int count() {
        return RT.intCast(Numbers.add(12L, (long)RT.count(this.__extmap)));
    }

    @Override
    public IPersistentCollection empty() {
        throw (Throwable)new UnsupportedOperationException((String)((IFn)const__33.getRawRoot()).invoke("Can't create empty: ", "ginga.async.haywire.Connection"));
    }

    @Override
    public IPersistentCollection cons(Object e__7870__auto__) {
        Connection connection2 = this_;
        Object object = e__7870__auto__;
        e__7870__auto__ = null;
        Connection this_ = null;
        return (IPersistentCollection)((IFn)const__32).invoke(connection2, object);
    }

    @Override
    public boolean equiv(Object G__10945) {
        Boolean bl;
        boolean or__5581__auto__10992 = Util.identical(this, G__10945);
        if (or__5581__auto__10992) {
            bl = or__5581__auto__10992 ? Boolean.TRUE : Boolean.FALSE;
        } else if (Util.identical(((IFn)const__31.getRawRoot()).invoke(this), ((IFn)const__31.getRawRoot()).invoke(G__10945))) {
            Object object = G__10945;
            G__10945 = null;
            Object G__109452 = object;
            boolean and__5579__auto__10991 = Util.equiv(this.alive, ((Connection)G__109452).alive);
            if (and__5579__auto__10991) {
                boolean and__5579__auto__10990 = Util.equiv(this.close_promise, ((Connection)G__109452).close_promise);
                if (and__5579__auto__10990) {
                    boolean and__5579__auto__10989 = Util.equiv(this.seq_state, ((Connection)G__109452).seq_state);
                    if (and__5579__auto__10989) {
                        boolean and__5579__auto__10988 = Util.equiv(this.reorder_xf, ((Connection)G__109452).reorder_xf);
                        if (and__5579__auto__10988) {
                            boolean and__5579__auto__10987 = Util.equiv(this.buffer_stats, ((Connection)G__109452).buffer_stats);
                            if (and__5579__auto__10987) {
                                boolean and__5579__auto__10986 = Util.equiv(this.win_in_stats, ((Connection)G__109452).win_in_stats);
                                if (and__5579__auto__10986) {
                                    boolean and__5579__auto__10985 = Util.equiv(this.win_out_stats, ((Connection)G__109452).win_out_stats);
                                    if (and__5579__auto__10985) {
                                        boolean and__5579__auto__10984 = Util.equiv(this.downstream, ((Connection)G__109452).downstream);
                                        if (and__5579__auto__10984) {
                                            boolean and__5579__auto__10983 = Util.equiv(this.upstream, ((Connection)G__109452).upstream);
                                            if (and__5579__auto__10983) {
                                                boolean and__5579__auto__10982 = Util.equiv(this.receive_buf, ((Connection)G__109452).receive_buf);
                                                if (and__5579__auto__10982) {
                                                    boolean and__5579__auto__10981 = Util.equiv(this.in, ((Connection)G__109452).in);
                                                    if (and__5579__auto__10981) {
                                                        boolean and__5579__auto__10980 = Util.equiv(this.out, ((Connection)G__109452).out);
                                                        if (and__5579__auto__10980) {
                                                            Object object2 = G__109452;
                                                            G__109452 = null;
                                                            bl = Util.equiv(this.__extmap, ((Connection)object2).__extmap) ? Boolean.TRUE : Boolean.FALSE;
                                                        } else {
                                                            bl = and__5579__auto__10980 ? Boolean.TRUE : Boolean.FALSE;
                                                        }
                                                    } else {
                                                        bl = and__5579__auto__10981 ? Boolean.TRUE : Boolean.FALSE;
                                                    }
                                                } else {
                                                    bl = and__5579__auto__10982 ? Boolean.TRUE : Boolean.FALSE;
                                                }
                                            } else {
                                                bl = and__5579__auto__10983 ? Boolean.TRUE : Boolean.FALSE;
                                            }
                                        } else {
                                            bl = and__5579__auto__10984 ? Boolean.TRUE : Boolean.FALSE;
                                        }
                                    } else {
                                        bl = and__5579__auto__10985 ? Boolean.TRUE : Boolean.FALSE;
                                    }
                                } else {
                                    bl = and__5579__auto__10986 ? Boolean.TRUE : Boolean.FALSE;
                                }
                            } else {
                                bl = and__5579__auto__10987 ? Boolean.TRUE : Boolean.FALSE;
                            }
                        } else {
                            bl = and__5579__auto__10988 ? Boolean.TRUE : Boolean.FALSE;
                        }
                    } else {
                        bl = and__5579__auto__10989 ? Boolean.TRUE : Boolean.FALSE;
                    }
                } else {
                    bl = and__5579__auto__10990 ? Boolean.TRUE : Boolean.FALSE;
                }
            } else {
                bl = and__5579__auto__10991 ? Boolean.TRUE : Boolean.FALSE;
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
        Connection this_ = null;
        return (Boolean)((IFn)const__30.getRawRoot()).invoke(bl);
    }

    @Override
    public IMapEntry entryAt(Object k__7875__auto__) {
        MapEntry mapEntry;
        Object v__7876__auto__10993 = ((ILookup)this_).valAt(k__7875__auto__, this_);
        if (Util.identical(this_, v__7876__auto__10993)) {
            mapEntry = null;
        } else {
            Object object = k__7875__auto__;
            k__7875__auto__ = null;
            Object object2 = v__7876__auto__10993;
            v__7876__auto__10993 = null;
            Connection this_ = null;
            mapEntry = MapEntry.create(object, object2);
        }
        return mapEntry;
    }

    @Override
    public ISeq seq() {
        Connection this_ = null;
        return (ISeq)((IFn)const__28.getRawRoot()).invoke(((IFn)const__29.getRawRoot()).invoke(RT.vector(MapEntry.create(const__10, this_.alive), MapEntry.create(const__12, this_.close_promise), MapEntry.create(const__9, this_.seq_state), MapEntry.create(const__19, this_.reorder_xf), MapEntry.create(const__14, this_.buffer_stats), MapEntry.create(const__11, this_.win_in_stats), MapEntry.create(const__17, this_.win_out_stats), MapEntry.create(const__8, this_.downstream), MapEntry.create(const__16, this_.upstream), MapEntry.create(const__15, this_.receive_buf), MapEntry.create(const__18, this_.in), MapEntry.create(const__13, this_.out)), this_.__extmap));
    }

    public Iterator iterator() {
        return new RecordIterator(this, (IPersistentVector)((Object)const__27), RT.iter(this.__extmap));
    }

    @Override
    public IPersistentMap assoc(Object k__7880__auto__, Object G__10945) {
        Connection connection2;
        Object pred__10947 = const__25.getRawRoot();
        Object expr__10948 = k__7880__auto__;
        Object object = ((IFn)pred__10947).invoke(const__10, expr__10948);
        if (object != null && object != Boolean.FALSE) {
            G__10945 = null;
            connection2 = new Connection(G__10945, this.close_promise, this.seq_state, this.reorder_xf, this.buffer_stats, this.win_in_stats, this.win_out_stats, this.downstream, this.upstream, this.receive_buf, this.in, this.out, this.__meta, this.__extmap);
        } else {
            Object object2 = ((IFn)pred__10947).invoke(const__12, expr__10948);
            if (object2 != null && object2 != Boolean.FALSE) {
                G__10945 = null;
                connection2 = new Connection(this.alive, G__10945, this.seq_state, this.reorder_xf, this.buffer_stats, this.win_in_stats, this.win_out_stats, this.downstream, this.upstream, this.receive_buf, this.in, this.out, this.__meta, this.__extmap);
            } else {
                Object object3 = ((IFn)pred__10947).invoke(const__9, expr__10948);
                if (object3 != null && object3 != Boolean.FALSE) {
                    G__10945 = null;
                    connection2 = new Connection(this.alive, this.close_promise, G__10945, this.reorder_xf, this.buffer_stats, this.win_in_stats, this.win_out_stats, this.downstream, this.upstream, this.receive_buf, this.in, this.out, this.__meta, this.__extmap);
                } else {
                    Object object4 = ((IFn)pred__10947).invoke(const__19, expr__10948);
                    if (object4 != null && object4 != Boolean.FALSE) {
                        G__10945 = null;
                        connection2 = new Connection(this.alive, this.close_promise, this.seq_state, G__10945, this.buffer_stats, this.win_in_stats, this.win_out_stats, this.downstream, this.upstream, this.receive_buf, this.in, this.out, this.__meta, this.__extmap);
                    } else {
                        Object object5 = ((IFn)pred__10947).invoke(const__14, expr__10948);
                        if (object5 != null && object5 != Boolean.FALSE) {
                            G__10945 = null;
                            connection2 = new Connection(this.alive, this.close_promise, this.seq_state, this.reorder_xf, G__10945, this.win_in_stats, this.win_out_stats, this.downstream, this.upstream, this.receive_buf, this.in, this.out, this.__meta, this.__extmap);
                        } else {
                            Object object6 = ((IFn)pred__10947).invoke(const__11, expr__10948);
                            if (object6 != null && object6 != Boolean.FALSE) {
                                G__10945 = null;
                                connection2 = new Connection(this.alive, this.close_promise, this.seq_state, this.reorder_xf, this.buffer_stats, G__10945, this.win_out_stats, this.downstream, this.upstream, this.receive_buf, this.in, this.out, this.__meta, this.__extmap);
                            } else {
                                Object object7 = ((IFn)pred__10947).invoke(const__17, expr__10948);
                                if (object7 != null && object7 != Boolean.FALSE) {
                                    G__10945 = null;
                                    connection2 = new Connection(this.alive, this.close_promise, this.seq_state, this.reorder_xf, this.buffer_stats, this.win_in_stats, G__10945, this.downstream, this.upstream, this.receive_buf, this.in, this.out, this.__meta, this.__extmap);
                                } else {
                                    Object object8 = ((IFn)pred__10947).invoke(const__8, expr__10948);
                                    if (object8 != null && object8 != Boolean.FALSE) {
                                        G__10945 = null;
                                        connection2 = new Connection(this.alive, this.close_promise, this.seq_state, this.reorder_xf, this.buffer_stats, this.win_in_stats, this.win_out_stats, G__10945, this.upstream, this.receive_buf, this.in, this.out, this.__meta, this.__extmap);
                                    } else {
                                        Object object9 = ((IFn)pred__10947).invoke(const__16, expr__10948);
                                        if (object9 != null && object9 != Boolean.FALSE) {
                                            G__10945 = null;
                                            connection2 = new Connection(this.alive, this.close_promise, this.seq_state, this.reorder_xf, this.buffer_stats, this.win_in_stats, this.win_out_stats, this.downstream, G__10945, this.receive_buf, this.in, this.out, this.__meta, this.__extmap);
                                        } else {
                                            Object object10 = ((IFn)pred__10947).invoke(const__15, expr__10948);
                                            if (object10 != null && object10 != Boolean.FALSE) {
                                                G__10945 = null;
                                                connection2 = new Connection(this.alive, this.close_promise, this.seq_state, this.reorder_xf, this.buffer_stats, this.win_in_stats, this.win_out_stats, this.downstream, this.upstream, G__10945, this.in, this.out, this.__meta, this.__extmap);
                                            } else {
                                                Object object11 = ((IFn)pred__10947).invoke(const__18, expr__10948);
                                                if (object11 != null && object11 != Boolean.FALSE) {
                                                    G__10945 = null;
                                                    connection2 = new Connection(this.alive, this.close_promise, this.seq_state, this.reorder_xf, this.buffer_stats, this.win_in_stats, this.win_out_stats, this.downstream, this.upstream, this.receive_buf, G__10945, this.out, this.__meta, this.__extmap);
                                                } else {
                                                    Object object12 = pred__10947;
                                                    pred__10947 = null;
                                                    Object object13 = expr__10948;
                                                    expr__10948 = null;
                                                    Object object14 = ((IFn)object12).invoke(const__13, object13);
                                                    if (object14 != null && object14 != Boolean.FALSE) {
                                                        G__10945 = null;
                                                        connection2 = new Connection(this.alive, this.close_promise, this.seq_state, this.reorder_xf, this.buffer_stats, this.win_in_stats, this.win_out_stats, this.downstream, this.upstream, this.receive_buf, this.in, G__10945, this.__meta, this.__extmap);
                                                    } else {
                                                        k__7880__auto__ = null;
                                                        G__10945 = null;
                                                        connection2 = new Connection(this.alive, this.close_promise, this.seq_state, this.reorder_xf, this.buffer_stats, this.win_in_stats, this.win_out_stats, this.downstream, this.upstream, this.receive_buf, this.in, this.out, this.__meta, ((IFn)const__26.getRawRoot()).invoke(this.__extmap, k__7880__auto__, G__10945));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return connection2;
    }

    @Override
    public IPersistentMap without(Object k__7882__auto__) {
        Object object;
        Object object2 = ((IFn)const__7.getRawRoot()).invoke(const__20, k__7882__auto__);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = ((IFn)const__22.getRawRoot()).invoke(((IFn)const__23.getRawRoot()).invoke(PersistentArrayMap.EMPTY, this_), this_.__meta);
            Object object4 = k__7882__auto__;
            k__7882__auto__ = null;
            Connection this_ = null;
            object = ((IFn)const__21.getRawRoot()).invoke(object3, object4);
        } else {
            k__7882__auto__ = null;
            object = new Connection(this_.alive, this_.close_promise, this_.seq_state, this_.reorder_xf, this_.buffer_stats, this_.win_in_stats, this_.win_out_stats, this_.downstream, this_.upstream, this_.receive_buf, this_.in, this_.out, this_.__meta, ((IFn)const__24.getRawRoot()).invoke(((IFn)const__21.getRawRoot()).invoke(this_.__extmap, k__7882__auto__)));
        }
        return (IPersistentMap)object;
    }

    @Override
    public int size() {
        Counted counted = this_;
        Connection this_ = null;
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
        Connection this_ = null;
        return (Set)((IFn)const__0.getRawRoot()).invoke(object);
    }

    public Collection values() {
        Connection connection2 = this_;
        Connection this_ = null;
        return (Collection)((IFn)const__1.getRawRoot()).invoke(connection2);
    }

    public Set entrySet() {
        Connection connection2 = this_;
        Connection this_ = null;
        return (Set)((IFn)const__0.getRawRoot()).invoke(connection2);
    }
}

