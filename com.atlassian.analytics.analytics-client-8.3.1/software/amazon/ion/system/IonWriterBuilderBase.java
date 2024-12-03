/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.system;

import software.amazon.ion.IonCatalog;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.system.IonWriterBuilder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class IonWriterBuilderBase<T extends IonWriterBuilderBase>
extends IonWriterBuilder {
    private IonCatalog myCatalog;
    private SymbolTable[] myImports;

    protected IonWriterBuilderBase() {
    }

    protected IonWriterBuilderBase(IonWriterBuilderBase that) {
        this.myCatalog = that.myCatalog;
        this.myImports = that.myImports;
    }

    abstract T copy();

    abstract T immutable();

    abstract T mutable();

    protected void mutationCheck() {
        throw new UnsupportedOperationException("This builder is immutable");
    }

    public final IonCatalog getCatalog() {
        return this.myCatalog;
    }

    public void setCatalog(IonCatalog catalog) {
        this.mutationCheck();
        this.myCatalog = catalog;
    }

    public T withCatalog(IonCatalog catalog) {
        T b = this.mutable();
        ((IonWriterBuilderBase)b).setCatalog(catalog);
        return b;
    }

    private static SymbolTable[] safeCopy(SymbolTable[] imports) {
        if (imports != null && imports.length != 0) {
            imports = (SymbolTable[])imports.clone();
        }
        return imports;
    }

    public final SymbolTable[] getImports() {
        return IonWriterBuilderBase.safeCopy(this.myImports);
    }

    public void setImports(SymbolTable ... imports) {
        this.mutationCheck();
        this.myImports = IonWriterBuilderBase.safeCopy(imports);
    }

    public T withImports(SymbolTable ... imports) {
        T b = this.mutable();
        ((IonWriterBuilderBase)b).setImports(imports);
        return b;
    }
}

