/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.system;

import java.nio.charset.Charset;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.PrivateIonTextWriterBuilder;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.system.IonWriterBuilder;
import software.amazon.ion.system.IonWriterBuilderBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class IonTextWriterBuilder
extends IonWriterBuilderBase<IonTextWriterBuilder> {
    public static final Charset ASCII = PrivateUtils.ASCII_CHARSET;
    public static final Charset UTF8 = PrivateUtils.UTF8_CHARSET;
    private Charset myCharset;
    private IonWriterBuilder.InitialIvmHandling myInitialIvmHandling;
    private IonWriterBuilder.IvmMinimizing myIvmMinimizing;
    private LstMinimizing myLstMinimizing;
    private int myLongStringThreshold;

    public static IonTextWriterBuilder standard() {
        return PrivateIonTextWriterBuilder.standard();
    }

    public static IonTextWriterBuilder minimal() {
        return IonTextWriterBuilder.standard().withMinimalSystemData();
    }

    public static IonTextWriterBuilder pretty() {
        return IonTextWriterBuilder.standard().withPrettyPrinting();
    }

    public static IonTextWriterBuilder json() {
        return IonTextWriterBuilder.standard().withJsonDowngrade();
    }

    protected IonTextWriterBuilder() {
    }

    protected IonTextWriterBuilder(IonTextWriterBuilder that) {
        super(that);
        this.myCharset = that.myCharset;
        this.myInitialIvmHandling = that.myInitialIvmHandling;
        this.myIvmMinimizing = that.myIvmMinimizing;
        this.myLstMinimizing = that.myLstMinimizing;
        this.myLongStringThreshold = that.myLongStringThreshold;
    }

    @Override
    public abstract IonTextWriterBuilder copy();

    @Override
    public abstract IonTextWriterBuilder immutable();

    @Override
    public abstract IonTextWriterBuilder mutable();

    @Override
    public final IonTextWriterBuilder withCatalog(IonCatalog catalog) {
        return (IonTextWriterBuilder)super.withCatalog(catalog);
    }

    @Override
    public final IonTextWriterBuilder withImports(SymbolTable ... imports) {
        return (IonTextWriterBuilder)super.withImports(imports);
    }

    public final Charset getCharset() {
        return this.myCharset;
    }

    public void setCharset(Charset charset) {
        this.mutationCheck();
        if (charset != null && !charset.equals(ASCII) && !charset.equals(UTF8)) {
            throw new IllegalArgumentException("Unsupported Charset " + charset);
        }
        this.myCharset = charset;
    }

    public final IonTextWriterBuilder withCharset(Charset charset) {
        IonTextWriterBuilder b = this.mutable();
        b.setCharset(charset);
        return b;
    }

    public final IonTextWriterBuilder withCharsetAscii() {
        return this.withCharset(ASCII);
    }

    public final IonTextWriterBuilder withMinimalSystemData() {
        IonTextWriterBuilder b = this.mutable();
        b.setInitialIvmHandling(IonWriterBuilder.InitialIvmHandling.SUPPRESS);
        b.setIvmMinimizing(IonWriterBuilder.IvmMinimizing.DISTANT);
        b.setLstMinimizing(LstMinimizing.EVERYTHING);
        return b;
    }

    public abstract IonTextWriterBuilder withPrettyPrinting();

    public abstract IonTextWriterBuilder withJsonDowngrade();

    @Override
    public final IonWriterBuilder.InitialIvmHandling getInitialIvmHandling() {
        return this.myInitialIvmHandling;
    }

    public void setInitialIvmHandling(IonWriterBuilder.InitialIvmHandling handling) {
        this.mutationCheck();
        this.myInitialIvmHandling = handling;
    }

    public final IonTextWriterBuilder withInitialIvmHandling(IonWriterBuilder.InitialIvmHandling handling) {
        IonTextWriterBuilder b = this.mutable();
        b.setInitialIvmHandling(handling);
        return b;
    }

    @Override
    public final IonWriterBuilder.IvmMinimizing getIvmMinimizing() {
        return this.myIvmMinimizing;
    }

    public void setIvmMinimizing(IonWriterBuilder.IvmMinimizing minimizing) {
        this.mutationCheck();
        this.myIvmMinimizing = minimizing;
    }

    public final IonTextWriterBuilder withIvmMinimizing(IonWriterBuilder.IvmMinimizing minimizing) {
        IonTextWriterBuilder b = this.mutable();
        b.setIvmMinimizing(minimizing);
        return b;
    }

    public final LstMinimizing getLstMinimizing() {
        return this.myLstMinimizing;
    }

    public void setLstMinimizing(LstMinimizing minimizing) {
        this.mutationCheck();
        this.myLstMinimizing = minimizing;
    }

    public final IonTextWriterBuilder withLstMinimizing(LstMinimizing minimizing) {
        IonTextWriterBuilder b = this.mutable();
        b.setLstMinimizing(minimizing);
        return b;
    }

    public final int getLongStringThreshold() {
        return this.myLongStringThreshold;
    }

    public void setLongStringThreshold(int threshold) {
        this.mutationCheck();
        this.myLongStringThreshold = threshold;
    }

    public final IonTextWriterBuilder withLongStringThreshold(int threshold) {
        IonTextWriterBuilder b = this.mutable();
        b.setLongStringThreshold(threshold);
        return b;
    }

    public abstract IonWriter build(Appendable var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum LstMinimizing {
        LOCALS,
        EVERYTHING;

    }
}

