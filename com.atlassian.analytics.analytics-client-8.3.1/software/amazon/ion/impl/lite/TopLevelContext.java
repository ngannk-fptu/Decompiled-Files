/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonDatagramLite;
import software.amazon.ion.impl.lite.IonSystemLite;

final class TopLevelContext
implements IonContext {
    private final IonDatagramLite _datagram;
    private final SymbolTable _symbols;

    private TopLevelContext(SymbolTable symbols, IonDatagramLite datagram) {
        assert (datagram != null);
        this._symbols = symbols;
        this._datagram = datagram;
    }

    static TopLevelContext wrap(SymbolTable symbols, IonDatagramLite datagram) {
        TopLevelContext context = new TopLevelContext(symbols, datagram);
        return context;
    }

    public IonDatagramLite getContextContainer() {
        return this._datagram;
    }

    public SymbolTable getContextSymbolTable() {
        return this._symbols;
    }

    public IonSystemLite getSystem() {
        return this._datagram.getSystem();
    }
}

