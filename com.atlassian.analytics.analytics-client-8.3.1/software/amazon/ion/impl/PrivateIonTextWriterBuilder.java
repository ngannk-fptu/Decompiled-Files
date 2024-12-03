/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.OutputStream;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.AppendableFastAppendable;
import software.amazon.ion.impl.IonWriterSystemText;
import software.amazon.ion.impl.IonWriterUser;
import software.amazon.ion.impl.OutputStreamFastAppendable;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.system.IonSystemBuilder;
import software.amazon.ion.system.IonTextWriterBuilder;
import software.amazon.ion.system.SimpleCatalog;
import software.amazon.ion.util.PrivateFastAppendable;

@Deprecated
public class PrivateIonTextWriterBuilder
extends IonTextWriterBuilder {
    private static final CharSequence SPACE_CHARACTER = " ";
    private static final CharSequence LINE_SEPARATOR = System.getProperty("line.separator");
    public static PrivateIonTextWriterBuilder STANDARD = PrivateIonTextWriterBuilder.standard().immutable();
    private boolean _pretty_print;
    public boolean _blob_as_string;
    public boolean _clob_as_string;
    public boolean _decimal_as_float;
    public boolean _sexp_as_list;
    public boolean _skip_annotations;
    public boolean _string_as_json;
    public boolean _symbol_as_string;
    public boolean _timestamp_as_millis;
    public boolean _timestamp_as_string;
    public boolean _untyped_nulls;

    public static PrivateIonTextWriterBuilder standard() {
        return new Mutable();
    }

    private PrivateIonTextWriterBuilder() {
    }

    private PrivateIonTextWriterBuilder(PrivateIonTextWriterBuilder that) {
        super(that);
        this._pretty_print = that._pretty_print;
        this._blob_as_string = that._blob_as_string;
        this._clob_as_string = that._clob_as_string;
        this._decimal_as_float = that._decimal_as_float;
        this._sexp_as_list = that._sexp_as_list;
        this._skip_annotations = that._skip_annotations;
        this._string_as_json = that._string_as_json;
        this._symbol_as_string = that._symbol_as_string;
        this._timestamp_as_millis = that._timestamp_as_millis;
        this._timestamp_as_string = that._timestamp_as_string;
        this._untyped_nulls = that._untyped_nulls;
    }

    public final PrivateIonTextWriterBuilder copy() {
        return new Mutable(this);
    }

    public PrivateIonTextWriterBuilder immutable() {
        return this;
    }

    public PrivateIonTextWriterBuilder mutable() {
        return this.copy();
    }

    public final IonTextWriterBuilder withPrettyPrinting() {
        PrivateIonTextWriterBuilder b = this.mutable();
        b._pretty_print = true;
        return b;
    }

    public final IonTextWriterBuilder withJsonDowngrade() {
        PrivateIonTextWriterBuilder b = this.mutable();
        b.withMinimalSystemData();
        this._blob_as_string = true;
        this._clob_as_string = true;
        this._decimal_as_float = true;
        this._sexp_as_list = true;
        this._skip_annotations = true;
        this._string_as_json = true;
        this._symbol_as_string = true;
        this._timestamp_as_string = true;
        this._timestamp_as_millis = false;
        this._untyped_nulls = true;
        return b;
    }

    final boolean isPrettyPrintOn() {
        return this._pretty_print;
    }

    final CharSequence lineSeparator() {
        if (this._pretty_print) {
            return LINE_SEPARATOR;
        }
        return SPACE_CHARACTER;
    }

    private PrivateIonTextWriterBuilder fillDefaults() {
        PrivateIonTextWriterBuilder b = this.copy();
        if (b.getCatalog() == null) {
            b.setCatalog(new SimpleCatalog());
        }
        if (b.getCharset() == null) {
            b.setCharset(UTF8);
        }
        return (PrivateIonTextWriterBuilder)((IonTextWriterBuilder)b).immutable();
    }

    private IonWriter build(PrivateFastAppendable appender) {
        IonCatalog catalog = this.getCatalog();
        SymbolTable[] imports = this.getImports();
        IonSystem system = IonSystemBuilder.standard().withCatalog(catalog).build();
        SymbolTable defaultSystemSymtab = system.getSystemSymbolTable();
        IonWriterSystemText systemWriter = new IonWriterSystemText(defaultSystemSymtab, this, appender);
        SymbolTable initialSymtab = PrivateUtils.initialSymtab(system, defaultSystemSymtab, imports);
        return new IonWriterUser(catalog, system, systemWriter, initialSymtab);
    }

    public final IonWriter build(Appendable out) {
        PrivateIonTextWriterBuilder b = this.fillDefaults();
        AppendableFastAppendable fast = new AppendableFastAppendable(out);
        return b.build(fast);
    }

    public final IonWriter build(OutputStream out) {
        PrivateIonTextWriterBuilder b = this.fillDefaults();
        OutputStreamFastAppendable fast = new OutputStreamFastAppendable(out);
        return b.build(fast);
    }

    private static final class Mutable
    extends PrivateIonTextWriterBuilder {
        private Mutable() {
        }

        private Mutable(PrivateIonTextWriterBuilder that) {
            super(that);
        }

        public PrivateIonTextWriterBuilder immutable() {
            return new PrivateIonTextWriterBuilder(this);
        }

        public PrivateIonTextWriterBuilder mutable() {
            return this;
        }

        protected void mutationCheck() {
        }
    }
}

