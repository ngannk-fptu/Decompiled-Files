/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.system;

import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonSystem;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.PrivateIonBinaryWriterBuilder;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.lite.PrivateLiteDomTrampoline;
import software.amazon.ion.system.IonTextWriterBuilder;
import software.amazon.ion.system.SimpleCatalog;

public class IonSystemBuilder {
    private static final IonSystemBuilder STANDARD = new IonSystemBuilder();
    IonCatalog myCatalog;
    boolean myStreamCopyOptimized = false;

    public static IonSystemBuilder standard() {
        return STANDARD;
    }

    private IonSystemBuilder() {
    }

    private IonSystemBuilder(IonSystemBuilder that) {
        this.myCatalog = that.myCatalog;
        this.myStreamCopyOptimized = that.myStreamCopyOptimized;
    }

    public final IonSystemBuilder copy() {
        return new Mutable(this);
    }

    public IonSystemBuilder immutable() {
        return this;
    }

    public IonSystemBuilder mutable() {
        return this.copy();
    }

    void mutationCheck() {
        throw new UnsupportedOperationException("This builder is immutable");
    }

    public final IonCatalog getCatalog() {
        return this.myCatalog;
    }

    public final void setCatalog(IonCatalog catalog) {
        this.mutationCheck();
        this.myCatalog = catalog;
    }

    public final IonSystemBuilder withCatalog(IonCatalog catalog) {
        IonSystemBuilder b = this.mutable();
        b.setCatalog(catalog);
        return b;
    }

    public final boolean isStreamCopyOptimized() {
        return this.myStreamCopyOptimized;
    }

    public final void setStreamCopyOptimized(boolean optimized) {
        this.mutationCheck();
        this.myStreamCopyOptimized = optimized;
    }

    public final IonSystemBuilder withStreamCopyOptimized(boolean optimized) {
        IonSystemBuilder b = this.mutable();
        b.setStreamCopyOptimized(optimized);
        return b;
    }

    public final IonSystem build() {
        IonCatalog catalog = this.myCatalog != null ? this.myCatalog : new SimpleCatalog();
        IonTextWriterBuilder twb = IonTextWriterBuilder.standard().withCharsetAscii();
        twb.setCatalog(catalog);
        PrivateIonBinaryWriterBuilder bwb = PrivateIonBinaryWriterBuilder.standard();
        bwb.setCatalog(catalog);
        bwb.setStreamCopyOptimized(this.myStreamCopyOptimized);
        SymbolTable systemSymtab = PrivateUtils.systemSymtab(1);
        bwb.setInitialSymbolTable(systemSymtab);
        return PrivateLiteDomTrampoline.newLiteSystem(twb, bwb);
    }

    private static final class Mutable
    extends IonSystemBuilder {
        private Mutable(IonSystemBuilder that) {
            super(that);
        }

        public IonSystemBuilder immutable() {
            return new IonSystemBuilder(this);
        }

        public IonSystemBuilder mutable() {
            return this;
        }

        void mutationCheck() {
        }
    }
}

