/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.PolicyMapKey;

interface PolicyMapKeyHandler {
    public boolean areEqual(PolicyMapKey var1, PolicyMapKey var2);

    public int generateHashCode(PolicyMapKey var1);
}

