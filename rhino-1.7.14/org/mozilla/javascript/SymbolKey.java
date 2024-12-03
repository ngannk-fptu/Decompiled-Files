/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.Serializable;
import org.mozilla.javascript.NativeSymbol;
import org.mozilla.javascript.Symbol;

public class SymbolKey
implements Symbol,
Serializable {
    private static final long serialVersionUID = -6019782713330994754L;
    public static final SymbolKey ITERATOR = new SymbolKey("Symbol.iterator");
    public static final SymbolKey TO_STRING_TAG = new SymbolKey("Symbol.toStringTag");
    public static final SymbolKey SPECIES = new SymbolKey("Symbol.species");
    public static final SymbolKey HAS_INSTANCE = new SymbolKey("Symbol.hasInstance");
    public static final SymbolKey IS_CONCAT_SPREADABLE = new SymbolKey("Symbol.isConcatSpreadable");
    public static final SymbolKey IS_REGEXP = new SymbolKey("Symbol.isRegExp");
    public static final SymbolKey TO_PRIMITIVE = new SymbolKey("Symbol.toPrimitive");
    public static final SymbolKey MATCH = new SymbolKey("Symbol.match");
    public static final SymbolKey REPLACE = new SymbolKey("Symbol.replace");
    public static final SymbolKey SEARCH = new SymbolKey("Symbol.search");
    public static final SymbolKey SPLIT = new SymbolKey("Symbol.split");
    public static final SymbolKey UNSCOPABLES = new SymbolKey("Symbol.unscopables");
    private String name;

    public SymbolKey(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    public boolean equals(Object o) {
        if (o instanceof SymbolKey) {
            return o == this;
        }
        if (o instanceof NativeSymbol) {
            return ((NativeSymbol)o).getKey() == this;
        }
        return false;
    }

    public String toString() {
        if (this.name == null) {
            return "Symbol()";
        }
        return "Symbol(" + this.name + ')';
    }
}

