/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import java.io.OutputStream;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonException;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SubstituteSymbolTableException;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.LocalSymbolTable;
import software.amazon.ion.impl.bin.PrivateIonManagedBinaryWriterBuilder;
import software.amazon.ion.system.IonBinaryWriterBuilder;
import software.amazon.ion.system.IonSystemBuilder;

@Deprecated
public class PrivateIonBinaryWriterBuilder
extends IonBinaryWriterBuilder {
    private final PrivateIonManagedBinaryWriterBuilder myBinaryWriterBuilder;
    private ValueFactory mySymtabValueFactory;
    private SymbolTable myInitialSymbolTable;

    private PrivateIonBinaryWriterBuilder() {
        this.myBinaryWriterBuilder = PrivateIonManagedBinaryWriterBuilder.create(PrivateIonManagedBinaryWriterBuilder.AllocatorMode.POOLED).withPaddedLengthPreallocation(0);
    }

    private PrivateIonBinaryWriterBuilder(PrivateIonBinaryWriterBuilder that) {
        super(that);
        this.mySymtabValueFactory = that.mySymtabValueFactory;
        this.myInitialSymbolTable = that.myInitialSymbolTable;
        this.myBinaryWriterBuilder = that.myBinaryWriterBuilder.copy();
    }

    public static PrivateIonBinaryWriterBuilder standard() {
        return new Mutable();
    }

    public final PrivateIonBinaryWriterBuilder copy() {
        return new Mutable(this);
    }

    public PrivateIonBinaryWriterBuilder immutable() {
        return this;
    }

    public PrivateIonBinaryWriterBuilder mutable() {
        return this.copy();
    }

    public ValueFactory getSymtabValueFactory() {
        return this.mySymtabValueFactory;
    }

    public void setSymtabValueFactory(ValueFactory factory) {
        this.mutationCheck();
        this.mySymtabValueFactory = factory;
    }

    public PrivateIonBinaryWriterBuilder withSymtabValueFactory(ValueFactory factory) {
        PrivateIonBinaryWriterBuilder b = this.mutable();
        b.setSymtabValueFactory(factory);
        return b;
    }

    public SymbolTable getInitialSymbolTable() {
        return this.myInitialSymbolTable;
    }

    public void setInitialSymbolTable(SymbolTable symtab) {
        this.mutationCheck();
        if (symtab != null) {
            if (symtab.isLocalTable()) {
                SymbolTable[] imports;
                for (SymbolTable imported : imports = ((LocalSymbolTable)symtab).getImportedTablesNoCopy()) {
                    if (!imported.isSubstitute()) continue;
                    String message = "Cannot encode with substitute symbol table: " + imported.getName();
                    throw new SubstituteSymbolTableException(message);
                }
            } else if (!symtab.isSystemTable()) {
                String message = "symtab must be local or system table";
                throw new IllegalArgumentException(message);
            }
        }
        this.myInitialSymbolTable = symtab;
        this.myBinaryWriterBuilder.withInitialSymbolTable(symtab);
    }

    public PrivateIonBinaryWriterBuilder withInitialSymbolTable(SymbolTable symtab) {
        PrivateIonBinaryWriterBuilder b = this.mutable();
        b.setInitialSymbolTable(symtab);
        return b;
    }

    public void setIsFloatBinary32Enabled(boolean enabled) {
        this.mutationCheck();
        if (enabled) {
            this.myBinaryWriterBuilder.withFloatBinary32Enabled();
        } else {
            this.myBinaryWriterBuilder.withFloatBinary32Disabled();
        }
    }

    public PrivateIonBinaryWriterBuilder withFloatBinary32Enabled() {
        PrivateIonBinaryWriterBuilder b = this.mutable();
        b.setIsFloatBinary32Enabled(true);
        return b;
    }

    public PrivateIonBinaryWriterBuilder withFloatBinary32Disabled() {
        PrivateIonBinaryWriterBuilder b = this.mutable();
        b.setIsFloatBinary32Enabled(false);
        return b;
    }

    public void setImports(SymbolTable ... imports) {
        super.setImports(imports);
        this.myBinaryWriterBuilder.withImports(imports);
    }

    public void setCatalog(IonCatalog catalog) {
        super.setCatalog(catalog);
        this.myBinaryWriterBuilder.withCatalog(catalog);
    }

    public void setStreamCopyOptimized(boolean optimized) {
        super.setStreamCopyOptimized(optimized);
        this.myBinaryWriterBuilder.withStreamCopyOptimization(optimized);
    }

    private PrivateIonBinaryWriterBuilder fillDefaults() {
        PrivateIonBinaryWriterBuilder b = this.copy();
        if (b.getSymtabValueFactory() == null) {
            IonSystem system = IonSystemBuilder.standard().build();
            b.setSymtabValueFactory(system);
        }
        return b.immutable();
    }

    SymbolTable buildContextSymbolTable() {
        if (this.myInitialSymbolTable.isReadOnly()) {
            return this.myInitialSymbolTable;
        }
        return ((LocalSymbolTable)this.myInitialSymbolTable).makeCopy();
    }

    public final IonWriter build(OutputStream out) {
        PrivateIonBinaryWriterBuilder b = this.fillDefaults();
        try {
            return b.myBinaryWriterBuilder.newWriter(out);
        }
        catch (IOException e) {
            throw new IonException("I/O Error", e);
        }
    }

    private static final class Mutable
    extends PrivateIonBinaryWriterBuilder {
        private Mutable() {
        }

        private Mutable(PrivateIonBinaryWriterBuilder that) {
            super(that);
        }

        public PrivateIonBinaryWriterBuilder immutable() {
            return new PrivateIonBinaryWriterBuilder(this);
        }

        public PrivateIonBinaryWriterBuilder mutable() {
            return this;
        }

        protected void mutationCheck() {
        }
    }
}

