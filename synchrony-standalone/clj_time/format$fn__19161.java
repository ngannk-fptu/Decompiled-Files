/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IObj;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class format$fn__19161
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "commute");
    public static final Var const__1 = RT.var("clojure.core", "deref");
    public static final Var const__2 = RT.var("clojure.core", "*loaded-libs*");
    public static final Var const__3 = RT.var("clojure.core", "conj");
    public static final AFn const__4 = (AFn)((Object)((IObj)Symbol.intern(null, "clj-time.format")).withMeta(RT.map(RT.keyword(null, "doc"), "Utilities for parsing and unparsing DateTimes as Strings.\n\n   Parsing and printing are controlled by formatters. You can either use one\n   of the built in ISO 8601 and a single RFC 822 formatters or define your own, e.g.:\n\n     (def built-in-formatter (formatters :basic-date-time))\n     (def custom-formatter (formatter \"yyyyMMdd\"))\n\n   To see a list of available built-in formatters and an example of a date-time\n   printed in their format:\n\n    (show-formatters)\n\n   Once you have a formatter, parsing and printing are straightforward:\n\n     => (parse custom-formatter \"20100311\")\n     #<DateTime 2010-03-11T00:00:00.000Z>\n\n     => (unparse custom-formatter (date-time 2010 10 3))\n     \"20101003\"\n\n   By default the parse function always returns a DateTime instance with a UTC\n   time zone, and the unparse function always represents a given DateTime\n   instance in UTC. A formatter can be modified to different timezones, locales,\n   etc with the functions with-zone, with-locale, with-chronology,\n   with-default-year and with-pivot-year.")));

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(const__2), const__3.getRawRoot(), const__4);
    }

    @Override
    public Object invoke() {
        return format$fn__19161.invokeStatic();
    }
}

