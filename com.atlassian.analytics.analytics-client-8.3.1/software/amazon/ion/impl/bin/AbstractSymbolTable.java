/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import java.io.IOException;
import java.util.Iterator;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.impl.bin.Symbols;

abstract class AbstractSymbolTable
implements SymbolTable {
    private final String name;
    private final int version;

    public AbstractSymbolTable(String name, int version) {
        this.name = name;
        this.version = version;
    }

    public final String getName() {
        return this.name;
    }

    public final int getVersion() {
        return this.version;
    }

    public final String getIonVersionId() {
        return "$ion_1_0";
    }

    public final int findSymbol(String name) {
        SymbolToken token = this.find(name);
        if (token == null) {
            return -1;
        }
        return token.getSid();
    }

    public final void writeTo(IonWriter writer) throws IOException {
        SymbolTable[] imports;
        if (this.isSharedTable()) {
            writer.setTypeAnnotationSymbols(Symbols.systemSymbol(9));
        } else if (this.isLocalTable()) {
            writer.setTypeAnnotationSymbols(Symbols.systemSymbol(3));
        } else {
            throw new IllegalStateException("Invalid symbol table, neither shared nor local");
        }
        writer.stepIn(IonType.STRUCT);
        if (this.isSharedTable()) {
            writer.setFieldNameSymbol(Symbols.systemSymbol(4));
            writer.writeString(this.name);
            writer.setFieldNameSymbol(Symbols.systemSymbol(5));
            writer.writeInt(this.version);
        }
        if ((imports = this.getImportedTables()) != null && imports.length > 0) {
            writer.setFieldNameSymbol(Symbols.systemSymbol(6));
            writer.stepIn(IonType.LIST);
            for (SymbolTable st : imports) {
                writer.stepIn(IonType.STRUCT);
                writer.setFieldNameSymbol(Symbols.systemSymbol(4));
                writer.writeString(st.getName());
                writer.setFieldNameSymbol(Symbols.systemSymbol(5));
                writer.writeInt(st.getVersion());
                writer.setFieldNameSymbol(Symbols.systemSymbol(8));
                writer.writeInt(st.getMaxId());
                writer.stepOut();
            }
            writer.stepOut();
        }
        writer.setFieldNameSymbol(Symbols.systemSymbol(7));
        writer.stepIn(IonType.LIST);
        Iterator<String> iter = this.iterateDeclaredSymbolNames();
        while (iter.hasNext()) {
            writer.writeString(iter.next());
        }
        writer.stepOut();
        writer.stepOut();
    }

    public void makeReadOnly() {
    }
}

