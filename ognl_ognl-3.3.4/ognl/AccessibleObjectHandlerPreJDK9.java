/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.AccessibleObject;
import ognl.AccessibleObjectHandler;

class AccessibleObjectHandlerPreJDK9
implements AccessibleObjectHandler {
    private AccessibleObjectHandlerPreJDK9() {
    }

    static AccessibleObjectHandler createHandler() {
        return new AccessibleObjectHandlerPreJDK9();
    }

    @Override
    public void setAccessible(AccessibleObject accessibleObject, boolean flag) {
        accessibleObject.setAccessible(flag);
    }
}

