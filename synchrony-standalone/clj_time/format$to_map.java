/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.Keyword;
import clojure.lang.RT;

public final class format$to_map
extends AFunction {
    public static final Keyword const__0 = RT.keyword(null, "years");
    public static final Keyword const__1 = RT.keyword(null, "months");
    public static final Keyword const__2 = RT.keyword(null, "days");
    public static final Keyword const__3 = RT.keyword(null, "hours");
    public static final Keyword const__4 = RT.keyword(null, "minutes");
    public static final Keyword const__5 = RT.keyword(null, "seconds");

    public static Object invokeStatic(Object years2, Object months2, Object days2, Object hours2, Object minutes2, Object seconds2) {
        Object[] objectArray = new Object[12];
        objectArray[0] = const__0;
        Object object = years2;
        years2 = null;
        objectArray[1] = object;
        objectArray[2] = const__1;
        Object object2 = months2;
        months2 = null;
        objectArray[3] = object2;
        objectArray[4] = const__2;
        Object object3 = days2;
        days2 = null;
        objectArray[5] = object3;
        objectArray[6] = const__3;
        Object object4 = hours2;
        hours2 = null;
        objectArray[7] = object4;
        objectArray[8] = const__4;
        Object object5 = minutes2;
        minutes2 = null;
        objectArray[9] = object5;
        objectArray[10] = const__5;
        Object object6 = seconds2;
        seconds2 = null;
        objectArray[11] = object6;
        return RT.mapUniqueKeys(objectArray);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
        Object object7 = object;
        object = null;
        Object object8 = object2;
        object2 = null;
        Object object9 = object3;
        object3 = null;
        Object object10 = object4;
        object4 = null;
        Object object11 = object5;
        object5 = null;
        Object object12 = object6;
        object6 = null;
        return format$to_map.invokeStatic(object7, object8, object9, object10, object11, object12);
    }
}

