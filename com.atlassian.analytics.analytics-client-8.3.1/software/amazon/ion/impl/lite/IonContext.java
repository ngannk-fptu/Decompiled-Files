/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.lite.IonContainerLite;
import software.amazon.ion.impl.lite.IonSystemLite;

interface IonContext {
    public IonContainerLite getContextContainer();

    public IonSystemLite getSystem();

    public SymbolTable getContextSymbolTable();
}

