/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class format$loading__6789__auto____19159
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "refer");
    public static final AFn const__1 = Symbol.intern(null, "clojure.core");
    public static final Keyword const__2 = RT.keyword(null, "exclude");
    public static final AFn const__3 = (AFn)((Object)Tuple.create(Symbol.intern(null, "extend"), Symbol.intern(null, "second")));
    public static final Var const__4 = RT.var("clojure.core", "require");
    public static final AFn const__5 = (AFn)((Object)Tuple.create(Symbol.intern(null, "clj-time.core"), RT.keyword(null, "refer"), RT.keyword(null, "all")));
    public static final AFn const__6 = (AFn)((Object)Tuple.create(Symbol.intern(null, "clojure.set"), RT.keyword(null, "refer"), Tuple.create(Symbol.intern(null, "difference"))));

    @Override
    public Object invoke() {
        Class clazz;
        Var.pushThreadBindings(RT.mapUniqueKeys(Compiler.LOADER, this.getClass().getClassLoader()));
        try {
            ((IFn)const__0.getRawRoot()).invoke(const__1, const__2, const__3);
            ((IFn)const__4.getRawRoot()).invoke(const__5, const__6);
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.util.Locale"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.Chronology"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.DateTime"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.DateTimeZone"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.Interval"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.LocalDateTime"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.Period"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.PeriodType"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.LocalDate"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.LocalTime"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.format.DateTimeFormat"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.format.DateTimeFormatter"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.format.DateTimePrinter"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.format.DateTimeFormatterBuilder"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.format.DateTimeParser"));
            clazz = ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("org.joda.time.format.ISODateTimeFormat"));
        }
        finally {
            Var.popThreadBindings();
        }
        return clazz;
    }
}

