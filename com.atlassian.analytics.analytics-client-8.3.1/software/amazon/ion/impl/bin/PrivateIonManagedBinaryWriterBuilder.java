/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SubstituteSymbolTableException;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.bin.AbstractIonWriter;
import software.amazon.ion.impl.bin.BlockAllocatorProvider;
import software.amazon.ion.impl.bin.BlockAllocatorProviders;
import software.amazon.ion.impl.bin.IonManagedBinaryWriter;
import software.amazon.ion.impl.bin.IonRawBinaryWriter;
import software.amazon.ion.impl.bin.PooledBlockAllocatorProvider;
import software.amazon.ion.system.SimpleCatalog;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public final class PrivateIonManagedBinaryWriterBuilder {
    public static final int DEFAULT_BLOCK_SIZE = 32768;
    final BlockAllocatorProvider provider;
    volatile int symbolsBlockSize;
    volatile int userBlockSize;
    volatile IonRawBinaryWriter.PreallocationMode preallocationMode;
    volatile IonManagedBinaryWriter.ImportedSymbolContext imports;
    volatile IonCatalog catalog;
    volatile AbstractIonWriter.WriteValueOptimization optimization;
    volatile SymbolTable initialSymbolTable;
    volatile boolean isFloatBinary32Enabled;

    private PrivateIonManagedBinaryWriterBuilder(BlockAllocatorProvider provider) {
        this.provider = provider;
        this.symbolsBlockSize = 32768;
        this.userBlockSize = 32768;
        this.imports = IonManagedBinaryWriter.ONLY_SYSTEM_IMPORTS;
        this.preallocationMode = IonRawBinaryWriter.PreallocationMode.PREALLOCATE_2;
        this.catalog = new SimpleCatalog();
        this.optimization = AbstractIonWriter.WriteValueOptimization.NONE;
        this.isFloatBinary32Enabled = false;
    }

    private PrivateIonManagedBinaryWriterBuilder(PrivateIonManagedBinaryWriterBuilder other) {
        this.provider = other.provider;
        this.symbolsBlockSize = other.symbolsBlockSize;
        this.userBlockSize = other.userBlockSize;
        this.preallocationMode = other.preallocationMode;
        this.imports = other.imports;
        this.catalog = other.catalog;
        this.optimization = other.optimization;
        this.initialSymbolTable = other.initialSymbolTable;
        this.isFloatBinary32Enabled = other.isFloatBinary32Enabled;
    }

    public PrivateIonManagedBinaryWriterBuilder copy() {
        return new PrivateIonManagedBinaryWriterBuilder(this);
    }

    public PrivateIonManagedBinaryWriterBuilder withSymbolsBlockSize(int blockSize) {
        if (blockSize < 1) {
            throw new IllegalArgumentException("Block size cannot be less than 1: " + blockSize);
        }
        this.symbolsBlockSize = blockSize;
        return this;
    }

    public PrivateIonManagedBinaryWriterBuilder withUserBlockSize(int blockSize) {
        if (blockSize < 1) {
            throw new IllegalArgumentException("Block size cannot be less than 1: " + blockSize);
        }
        this.userBlockSize = blockSize;
        return this;
    }

    public PrivateIonManagedBinaryWriterBuilder withImports(SymbolTable ... tables) {
        if (tables != null) {
            return this.withImports(Arrays.asList(tables));
        }
        return this;
    }

    public PrivateIonManagedBinaryWriterBuilder withImports(List<SymbolTable> tables) {
        return this.withImports(IonManagedBinaryWriter.ImportedSymbolResolverMode.DELEGATE, tables);
    }

    public PrivateIonManagedBinaryWriterBuilder withFlatImports(SymbolTable ... tables) {
        if (tables != null) {
            return this.withFlatImports(Arrays.asList(tables));
        }
        return this;
    }

    public PrivateIonManagedBinaryWriterBuilder withFlatImports(List<SymbolTable> tables) {
        return this.withImports(IonManagedBinaryWriter.ImportedSymbolResolverMode.FLAT, tables);
    }

    PrivateIonManagedBinaryWriterBuilder withImports(IonManagedBinaryWriter.ImportedSymbolResolverMode mode, List<SymbolTable> tables) {
        this.imports = new IonManagedBinaryWriter.ImportedSymbolContext(mode, tables);
        return this;
    }

    PrivateIonManagedBinaryWriterBuilder withPreallocationMode(IonRawBinaryWriter.PreallocationMode preallocationMode) {
        this.preallocationMode = preallocationMode;
        return this;
    }

    public PrivateIonManagedBinaryWriterBuilder withPaddedLengthPreallocation(int pad) {
        this.preallocationMode = IonRawBinaryWriter.PreallocationMode.withPadSize(pad);
        return this;
    }

    public PrivateIonManagedBinaryWriterBuilder withCatalog(IonCatalog catalog) {
        this.catalog = catalog;
        return this;
    }

    public PrivateIonManagedBinaryWriterBuilder withStreamCopyOptimization(boolean optimized) {
        this.optimization = optimized ? AbstractIonWriter.WriteValueOptimization.COPY_OPTIMIZED : AbstractIonWriter.WriteValueOptimization.NONE;
        return this;
    }

    public PrivateIonManagedBinaryWriterBuilder withFloatBinary32Enabled() {
        this.isFloatBinary32Enabled = true;
        return this;
    }

    public PrivateIonManagedBinaryWriterBuilder withFloatBinary32Disabled() {
        this.isFloatBinary32Enabled = false;
        return this;
    }

    public PrivateIonManagedBinaryWriterBuilder withInitialSymbolTable(SymbolTable symbolTable) {
        if (symbolTable != null) {
            if (!symbolTable.isLocalTable() && !symbolTable.isSystemTable()) {
                throw new IllegalArgumentException("Initial symbol table must be local or system");
            }
            if (symbolTable.isSystemTable()) {
                if (symbolTable.getMaxId() != 9) {
                    throw new IllegalArgumentException("Unsupported system symbol table");
                }
                symbolTable = null;
            } else {
                for (SymbolTable st : symbolTable.getImportedTables()) {
                    if (!st.isSubstitute()) continue;
                    throw new SubstituteSymbolTableException("Cannot use initial symbol table with imported substitutes");
                }
            }
        }
        this.initialSymbolTable = symbolTable;
        return this;
    }

    public IonWriter newWriter(OutputStream out) throws IOException {
        return new IonManagedBinaryWriter(this, out);
    }

    public static PrivateIonManagedBinaryWriterBuilder create(AllocatorMode allocatorMode) {
        return new PrivateIonManagedBinaryWriterBuilder(allocatorMode.createAllocatorProvider());
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum AllocatorMode {
        POOLED{

            BlockAllocatorProvider createAllocatorProvider() {
                return new PooledBlockAllocatorProvider();
            }
        }
        ,
        BASIC{

            BlockAllocatorProvider createAllocatorProvider() {
                return BlockAllocatorProviders.basicProvider();
            }
        };


        abstract BlockAllocatorProvider createAllocatorProvider();
    }
}

