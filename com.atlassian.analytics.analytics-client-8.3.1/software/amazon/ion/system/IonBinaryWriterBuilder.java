/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.system;

import software.amazon.ion.IonCatalog;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.PrivateIonBinaryWriterBuilder;
import software.amazon.ion.system.IonWriterBuilder;
import software.amazon.ion.system.IonWriterBuilderBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class IonBinaryWriterBuilder
extends IonWriterBuilderBase<IonBinaryWriterBuilder> {
    private boolean myStreamCopyOptimized;

    protected IonBinaryWriterBuilder() {
    }

    protected IonBinaryWriterBuilder(IonBinaryWriterBuilder that) {
        super(that);
        this.myStreamCopyOptimized = that.myStreamCopyOptimized;
    }

    public static IonBinaryWriterBuilder standard() {
        return PrivateIonBinaryWriterBuilder.standard();
    }

    @Override
    public abstract IonBinaryWriterBuilder copy();

    @Override
    public abstract IonBinaryWriterBuilder immutable();

    @Override
    public abstract IonBinaryWriterBuilder mutable();

    @Override
    public final IonBinaryWriterBuilder withCatalog(IonCatalog catalog) {
        return (IonBinaryWriterBuilder)super.withCatalog(catalog);
    }

    @Override
    public final IonBinaryWriterBuilder withImports(SymbolTable ... imports) {
        return (IonBinaryWriterBuilder)super.withImports(imports);
    }

    @Override
    public IonWriterBuilder.InitialIvmHandling getInitialIvmHandling() {
        return IonWriterBuilder.InitialIvmHandling.ENSURE;
    }

    @Override
    public IonWriterBuilder.IvmMinimizing getIvmMinimizing() {
        return null;
    }

    public abstract SymbolTable getInitialSymbolTable();

    public abstract void setInitialSymbolTable(SymbolTable var1);

    public abstract IonBinaryWriterBuilder withInitialSymbolTable(SymbolTable var1);

    public abstract void setIsFloatBinary32Enabled(boolean var1);

    public abstract IonBinaryWriterBuilder withFloatBinary32Enabled();

    public abstract IonBinaryWriterBuilder withFloatBinary32Disabled();

    public boolean isStreamCopyOptimized() {
        return this.myStreamCopyOptimized;
    }

    public void setStreamCopyOptimized(boolean optimized) {
        this.mutationCheck();
        this.myStreamCopyOptimized = optimized;
    }

    public final IonBinaryWriterBuilder withStreamCopyOptimized(boolean optimized) {
        IonBinaryWriterBuilder b = this.mutable();
        b.setStreamCopyOptimized(optimized);
        return b;
    }
}

