/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import software.amazon.ion.IonContainer;
import software.amazon.ion.IonValue;

@Deprecated
public interface PrivateIonContainer
extends IonContainer {
    public int get_child_count();

    public IonValue get_child(int var1);
}

