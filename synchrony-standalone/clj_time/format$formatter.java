/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$formatter$fn__19164;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimePrinter;

public final class format$formatter
extends RestFn {
    public static final Var const__0 = RT.var("clj-time.format", "formatter");
    public static final Var const__1 = RT.var("clj-time.core", "utc");
    public static final Var const__2 = RT.var("clojure.core", "keyword?");
    public static final Var const__4 = RT.var("clj-time.format", "formatters");
    public static final Var const__5 = RT.var("clojure.core", "string?");
    public static final Keyword const__6 = RT.keyword(null, "else");
    public static final Var const__7 = RT.var("clojure.core", "map");
    public static final Var const__8 = RT.var("clojure.core", "cons");
    public static final Var const__9 = RT.var("clojure.core", "into-array");
    public static final Object const__10 = RT.classForName("org.joda.time.format.DateTimeParser");

    public static Object invokeStatic(Object dtz, Object fmts, ISeq more) {
        DateTimePrinter printer = ((DateTimeFormatter)((IFn)const__0.getRawRoot()).invoke(fmts, dtz)).getPrinter();
        Object object = fmts;
        fmts = null;
        ISeq iSeq = more;
        more = null;
        Object parsers = ((IFn)const__7.getRawRoot()).invoke(new format$formatter$fn__19164(dtz), ((IFn)const__8.getRawRoot()).invoke(object, iSeq));
        DateTimePrinter dateTimePrinter = printer;
        printer = null;
        Object object2 = parsers;
        parsers = null;
        Object object3 = dtz;
        dtz = null;
        return new DateTimeFormatterBuilder().append(dateTimePrinter, (DateTimeParser[])((IFn)const__9.getRawRoot()).invoke(const__10, object2)).toFormatter().withZone((DateTimeZone)object3);
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return format$formatter.invokeStatic(object4, object5, iSeq);
    }

    public static Object invokeStatic(Object fmts, Object dtz) {
        DateTimeFormatter dateTimeFormatter;
        Object object = ((IFn)const__2.getRawRoot()).invoke(fmts);
        if (object != null && object != Boolean.FALSE) {
            Object object2 = fmts;
            fmts = null;
            Object object3 = dtz;
            dtz = null;
            dateTimeFormatter = ((DateTimeFormatter)RT.get(const__4.getRawRoot(), object2)).withZone((DateTimeZone)object3);
        } else {
            Object object4 = ((IFn)const__5.getRawRoot()).invoke(fmts);
            if (object4 != null && object4 != Boolean.FALSE) {
                Object object5 = fmts;
                fmts = null;
                Object object6 = dtz;
                dtz = null;
                dateTimeFormatter = DateTimeFormat.forPattern((String)object5).withZone((DateTimeZone)object6);
            } else {
                Keyword keyword2 = const__6;
                if (keyword2 != null && keyword2 != Boolean.FALSE) {
                    Object object7 = fmts;
                    fmts = null;
                    Object object8 = dtz;
                    dtz = null;
                    dateTimeFormatter = ((DateTimeFormatter)object7).withZone((DateTimeZone)object8);
                } else {
                    dateTimeFormatter = null;
                }
            }
        }
        return dateTimeFormatter;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return format$formatter.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object fmts) {
        Object object = fmts;
        fmts = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, const__1.getRawRoot());
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return format$formatter.invokeStatic(object2);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

