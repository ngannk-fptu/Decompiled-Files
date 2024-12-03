/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.beans.swing;

import java.beans.PropertyEditor;

interface HostBindingInterface {
    public void syncToValue(PropertyEditor var1, Object var2);

    public void addUserModificationListeners();

    public Object fetchUserModification(PropertyEditor var1, Object var2);

    public void alertErroneousInput();
}

