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

public final class core$fn__18512
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "commute");
    public static final Var const__1 = RT.var("clojure.core", "deref");
    public static final Var const__2 = RT.var("clojure.core", "*loaded-libs*");
    public static final Var const__3 = RT.var("clojure.core", "conj");
    public static final AFn const__4 = (AFn)((Object)((IObj)Symbol.intern(null, "clj-time.core")).withMeta(RT.map(RT.keyword(null, "doc"), "The core namespace for date-time operations in the clj-time library.\n\n   Create a DateTime instance with date-time (or a LocalDateTime instance with local-date-time),\n   specifying the year, month, day, hour, minute, second, and millisecond:\n\n     => (date-time 1986 10 14 4 3 27 456)\n     #<DateTime 1986-10-14T04:03:27.456Z>\n\n     => (local-date-time 1986 10 14 4 3 27 456)\n     #<LocalDateTime 1986-10-14T04:03:27.456>\n\n   Less-significant fields can be omitted:\n\n     => (date-time 1986 10 14)\n     #<DateTime 1986-10-14T00:00:00.000Z>\n\n     => (local-date-time 1986 10 14)\n     #<LocalDateTime 1986-10-14T00:00:00.000>\n\n   Get the current time with (now) and the start of the Unix epoch with (epoch).\n\n   Once you have a date-time, use accessors like hour and second to access the\n   corresponding fields:\n\n     => (hour (date-time 1986 10 14 22))\n     22\n\n     => (hour (local-date-time 1986 10 14 22))\n     22\n\n   The date-time constructor always returns times in the UTC time zone. If you\n   want a time with the specified fields in a different time zone, use\n   from-time-zone:\n\n     => (from-time-zone (date-time 1986 10 22) (time-zone-for-offset -2))\n     #<DateTime 1986-10-22T00:00:00.000-02:00>\n\n   If on the other hand you want a given absolute instant in time in a\n   different time zone, use to-time-zone:\n\n     => (to-time-zone (date-time 1986 10 22) (time-zone-for-offset -2))\n     #<DateTime 1986-10-21T22:00:00.000-02:00>\n\n   In addition to time-zone-for-offset, you can use the time-zone-for-id and\n   default-time-zone functions and the utc Var to construct or get DateTimeZone\n   instances.\n\n   The functions after? and before? determine the relative position of two\n   DateTime instances:\n\n     => (after? (date-time 1986 10) (date-time 1986 9))\n     true\n\n     => (after? (local-date-time 1986 10) (local-date-time 1986 9))\n     true\n\n   Often you will want to find a date some amount of time from a given date. For\n   example, to find the time 1 month and 3 weeks from a given date-time:\n\n     => (plus (date-time 1986 10 14) (months 1) (weeks 3))\n     #<DateTime 1986-12-05T00:00:00.000Z>\n\n     => (plus (local-date-time 1986 10 14) (months 1) (weeks 3))\n     #<LocalDateTime 1986-12-05T00:00:00.000Z>\n\n   An Interval is used to represent the span of time between two DateTime\n   instances. Construct one using interval, then query them using within?,\n   overlaps?, and abuts?\n\n     => (within? (interval (date-time 1986) (date-time 1990))\n                 (date-time 1987))\n     true\n\n   To find the amount of time encompassed by an interval, use in-seconds and\n   in-minutes:\n\n     => (in-minutes (interval (date-time 1986 10 2) (date-time 1986 10 14)))\n     17280\n\n   The overlap function can be used to get an Interval representing the\n   overlap between two intervals:\n\n     => (overlap (t/interval (t/date-time 1986) (t/date-time 1990))\n                             (t/interval (t/date-time 1987) (t/date-time 1991)))\n     #<Interval 1987-01-01T00:00:00.000Z/1990-01-01T00:00:00.000Z>\n\n   Note that all functions in this namespace work with Joda objects or ints. If\n   you need to print or parse date-times, see clj-time.format. If you need to\n   coerce date-times to or from other types, see clj-time.coerce.")));

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(const__2), const__3.getRawRoot(), const__4);
    }

    @Override
    public Object invoke() {
        return core$fn__18512.invokeStatic();
    }
}

