/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import software.amazon.ion.EmptySymbolException;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.UnknownSymbolException;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonSymbol;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonTextLite;
import software.amazon.ion.impl.lite.IonValueLite;

final class IonSymbolLite
extends IonTextLite
implements PrivateIonSymbol {
    private static final int HASH_SIGNATURE = IonType.SYMBOL.toString().hashCode();
    private int _sid = -1;

    IonSymbolLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonSymbolLite(IonSymbolLite existing, IonContext context) throws UnknownSymbolException {
        super(existing, context);
    }

    IonSymbolLite(ContainerlessContext context, SymbolToken sym) {
        super(context, sym == null);
        if (sym != null) {
            String text = sym.getText();
            int sid = sym.getSid();
            assert (text != null || sid > 0);
            if (text != null) {
                if (text.length() == 0) {
                    throw new EmptySymbolException();
                }
                super.setValue(text);
            } else {
                this._sid = sid;
            }
        }
    }

    IonSymbolLite clone(IonContext context) {
        return new IonSymbolLite(this, context);
    }

    public IonSymbolLite clone() throws UnknownSymbolException {
        if (!this.isNullValue() && this._sid != -1 && this._stringValue() == null) {
            throw new UnknownSymbolException(this._sid);
        }
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int sidHashSalt = 127;
        int textHashSalt = 31;
        int result = HASH_SIGNATURE;
        if (!this.isNullValue()) {
            SymbolToken token = this.symbolValue(symbolTableProvider);
            String text = token.getText();
            int tokenHashCode = text == null ? token.getSid() * 127 : text.hashCode() * 31;
            tokenHashCode ^= tokenHashCode << 29 ^ tokenHashCode >> 3;
            result ^= tokenHashCode;
        }
        return this.hashTypeAnnotations(result, symbolTableProvider);
    }

    public IonType getType() {
        return IonType.SYMBOL;
    }

    private String _stringValue() {
        return this._stringValue(new IonValueLite.LazySymbolTableProvider(this));
    }

    private String _stringValue(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        String name = this._get_value();
        if (name == null) {
            assert (this._sid > 0);
            SymbolTable symbols = symbolTableProvider.getSymbolTable();
            name = symbols.findKnownSymbol(this._sid);
            if (name != null && !this._isLocked()) {
                this._set_value(name);
            }
        }
        return name;
    }

    public SymbolToken symbolValue() {
        return this.symbolValue(new IonValueLite.LazySymbolTableProvider(this));
    }

    private int resolveSymbolId() {
        SymbolToken tok;
        this.validateThisNotNull();
        if (this._sid != -1 || this.isReadOnly()) {
            return this._sid;
        }
        SymbolTable symtab = this.getSymbolTable();
        if (symtab == null) {
            symtab = this.getSystem().getSystemSymbolTable();
        }
        assert (symtab != null);
        String name = this._get_value();
        if (!symtab.isLocalTable()) {
            this._sid = symtab.findSymbol(name);
            if (this._sid > 0 || this.isReadOnly()) {
                return this._sid;
            }
        }
        if ((tok = symtab.find(name)) != null) {
            this._sid = tok.getSid();
            this._set_value(tok.getText());
        }
        return this._sid;
    }

    public SymbolToken symbolValue(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        if (this.isNullValue()) {
            return null;
        }
        int sid = this.resolveSymbolId();
        String text = this._stringValue(symbolTableProvider);
        return PrivateUtils.newSymbolToken(text, sid);
    }

    public void setValue(String value) {
        if ("".equals(value)) {
            throw new EmptySymbolException();
        }
        super.setValue(value);
        this._sid = -1;
    }

    protected boolean isIonVersionMarker() {
        return this._isIVM();
    }

    void clearSymbolIDValues() {
        super.clearSymbolIDValues();
        if (!this.isNullValue() && this._stringValue() != null) {
            this._sid = -1;
        }
    }

    protected void setIsIonVersionMarker(boolean isIVM) {
        assert ("$ion_1_0".equals(this._get_value()) == isIVM);
        this._isIVM(isIVM);
        this._isSystemValue(isIVM);
        this._sid = 2;
    }

    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        SymbolToken symbol = this.symbolValue(symbolTableProvider);
        writer.writeSymbolToken(symbol);
    }

    public String stringValue() throws UnknownSymbolException {
        return this.stringValue(new IonValueLite.LazySymbolTableProvider(this));
    }

    private String stringValue(PrivateIonValue.SymbolTableProvider symbolTableProvider) throws UnknownSymbolException {
        if (this.isNullValue()) {
            return null;
        }
        String name = this._stringValue(symbolTableProvider);
        if (name == null) {
            assert (this._sid > 0);
            throw new UnknownSymbolException(this._sid);
        }
        return name;
    }

    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

