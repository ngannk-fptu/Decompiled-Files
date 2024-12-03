/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clout.core.Route;

public final class core$fn__34679$G__34675__34682
extends AFunction {
    @Override
    public Object invoke(Object gf__route__34680, Object gf__request__34681) {
        Object object = gf__route__34680;
        gf__route__34680 = null;
        Object object2 = gf__request__34681;
        gf__request__34681 = null;
        return ((Route)object).route_matches(object2);
    }
}

