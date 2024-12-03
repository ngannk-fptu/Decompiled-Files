/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.security.BasicPermission;

public class OgnlInvokePermission
extends BasicPermission {
    public OgnlInvokePermission(String name) {
        super(name);
    }

    public OgnlInvokePermission(String name, String actions) {
        super(name, actions);
    }
}

