/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.lite.IonContainerLite;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonSystemLite;

class ContainerlessContext
implements IonContext {
    private final IonSystemLite _system;
    private final SymbolTable _symbols;

    public static ContainerlessContext wrap(IonSystemLite system) {
        return new ContainerlessContext(system, null);
    }

    public static ContainerlessContext wrap(IonSystemLite system, SymbolTable symbols) {
        return new ContainerlessContext(system, symbols);
    }

    private ContainerlessContext(IonSystemLite system, SymbolTable symbols) {
        this._system = system;
        this._symbols = symbols;
    }

    public IonContainerLite getContextContainer() {
        return null;
    }

    public IonSystemLite getSystem() {
        return this._system;
    }

    public SymbolTable getContextSymbolTable() {
        return this._symbols;
    }
}

