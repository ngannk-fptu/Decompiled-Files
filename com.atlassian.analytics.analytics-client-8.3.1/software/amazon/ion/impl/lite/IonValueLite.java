/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import java.io.PrintWriter;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonException;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.IonWriter;
import software.amazon.ion.NullValueException;
import software.amazon.ion.ReadOnlyValueException;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.UnknownSymbolException;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.PrivateIonWriter;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.SymbolTokenImpl;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContainerLite;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonDatagramLite;
import software.amazon.ion.impl.lite.IonStructLite;
import software.amazon.ion.impl.lite.IonSystemLite;
import software.amazon.ion.impl.lite.TopLevelContext;
import software.amazon.ion.system.IonTextWriterBuilder;
import software.amazon.ion.util.Equivalence;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class IonValueLite
implements PrivateIonValue {
    private static final int TYPE_ANNOTATION_HASH_SIGNATURE = "TYPE ANNOTATION".hashCode();
    private static final IonTextWriterBuilder TO_STRING_TEXT_WRITER_BUILDER = IonTextWriterBuilder.standard().withCharsetAscii().immutable();
    protected static final int IS_LOCKED = 1;
    protected static final int IS_SYSTEM_VALUE = 2;
    protected static final int IS_NULL_VALUE = 4;
    protected static final int IS_BOOL_TRUE = 8;
    protected static final int IS_IVM = 16;
    protected static final int IS_AUTO_CREATED = 32;
    protected static final int IS_SYMBOL_PRESENT = 64;
    private static final int ELEMENT_MASK = 255;
    protected static final int ELEMENT_SHIFT = 8;
    private int _flags;
    private int _fieldId = -1;
    protected IonContext _context;
    private String _fieldName;
    private SymbolToken[] _annotations;

    protected final int _getMetadata(int mask, int shift) {
        return (this._flags & mask) >>> shift;
    }

    protected final void _setMetadata(int metadata, int mask, int shift) {
        assert (mask <= 255);
        this._flags &= ~mask;
        this._flags |= metadata << shift & mask;
    }

    protected final void _elementid(int elementid) {
        this._flags &= 0xFF;
        this._flags |= elementid << 8;
        assert (this._elementid() == elementid);
    }

    protected final int _elementid() {
        int elementid = this._flags >>> 8;
        return elementid;
    }

    private final boolean is_true(int flag_bit) {
        return (this._flags & flag_bit) != 0;
    }

    private final void set_flag(int flag_bit) {
        assert (flag_bit != 0);
        this._flags |= flag_bit;
    }

    private final void clear_flag(int flag_bit) {
        assert (flag_bit != 0);
        this._flags &= ~flag_bit;
    }

    protected final boolean _isLocked() {
        return this.is_true(1);
    }

    protected final boolean _isLocked(boolean flag) {
        if (flag) {
            this.set_flag(1);
        } else {
            this.clear_flag(1);
        }
        return flag;
    }

    protected final boolean _isSystemValue() {
        return this.is_true(2);
    }

    protected final boolean _isSystemValue(boolean flag) {
        if (flag) {
            this.set_flag(2);
        } else {
            this.clear_flag(2);
        }
        return flag;
    }

    protected final boolean _isNullValue() {
        return this.is_true(4);
    }

    protected final boolean _isNullValue(boolean flag) {
        if (flag) {
            this.set_flag(4);
        } else {
            this.clear_flag(4);
        }
        return flag;
    }

    protected final boolean _isBoolTrue() {
        return this.is_true(8);
    }

    protected final boolean _isBoolTrue(boolean flag) {
        if (flag) {
            this.set_flag(8);
        } else {
            this.clear_flag(8);
        }
        return flag;
    }

    protected final boolean _isIVM() {
        return this.is_true(16);
    }

    protected final boolean _isIVM(boolean flag) {
        if (flag) {
            this.set_flag(16);
        } else {
            this.clear_flag(16);
        }
        return flag;
    }

    protected final boolean _isAutoCreated() {
        return this.is_true(32);
    }

    protected final boolean _isAutoCreated(boolean flag) {
        if (flag) {
            this.set_flag(32);
        } else {
            this.clear_flag(32);
        }
        return flag;
    }

    protected final boolean _isSymbolPresent() {
        return this.is_true(64);
    }

    protected final boolean _isSymbolPresent(boolean flag) {
        if (flag) {
            this.set_flag(64);
        } else {
            this.clear_flag(64);
        }
        return flag;
    }

    IonValueLite(ContainerlessContext context, boolean isNull) {
        assert (context != null);
        this._context = context;
        if (isNull) {
            this.set_flag(4);
        }
    }

    IonValueLite(IonValueLite existing, IonContext context) {
        if (null == existing._annotations) {
            this._annotations = null;
        } else {
            int size = existing._annotations.length;
            this._annotations = new SymbolToken[size];
            for (int i = 0; i < size; ++i) {
                SymbolToken existingToken = existing._annotations[i];
                if (existingToken == null) continue;
                String text = existingToken.getText();
                this._annotations[i] = text != null ? PrivateUtils.newSymbolToken(text, -1) : existing._annotations[i];
            }
        }
        this._flags = existing._flags;
        this._context = context;
        this.clear_flag(1);
    }

    @Override
    public abstract void accept(ValueVisitor var1) throws Exception;

    @Override
    public void addTypeAnnotation(String annotation) {
        int old_len;
        this.checkForLock();
        if (annotation == null || annotation.length() < 1) {
            throw new IllegalArgumentException("a user type annotation must be a non-empty string");
        }
        if (this.hasTypeAnnotation(annotation)) {
            return;
        }
        SymbolTokenImpl sym = PrivateUtils.newSymbolToken(annotation, -1);
        int n = old_len = this._annotations == null ? 0 : this._annotations.length;
        if (old_len > 0) {
            for (int ii = 0; ii < old_len; ++ii) {
                if (this._annotations[ii] != null) continue;
                this._annotations[ii] = sym;
                return;
            }
        }
        int new_len = old_len == 0 ? 1 : old_len * 2;
        SymbolToken[] temp = new SymbolToken[new_len];
        if (old_len > 0) {
            System.arraycopy(this._annotations, 0, temp, 0, old_len);
        }
        this._annotations = temp;
        this._annotations[old_len] = sym;
    }

    @Override
    public final void clearTypeAnnotations() {
        int old_len;
        this.checkForLock();
        int n = old_len = this._annotations == null ? 0 : this._annotations.length;
        if (old_len > 0) {
            for (int ii = 0; ii < old_len && this._annotations[ii] != null; ++ii) {
                this._annotations[ii] = null;
            }
        }
    }

    @Override
    public abstract IonValue clone();

    abstract IonValueLite clone(IonContext var1);

    @Override
    public int hashCode() {
        return this.hashCode(new LazySymbolTableProvider(this));
    }

    abstract int hashCode(PrivateIonValue.SymbolTableProvider var1);

    @Override
    public IonContainerLite getContainer() {
        return this._context.getContextContainer();
    }

    @Override
    public IonValueLite topLevelValue() {
        IonContainerLite c;
        assert (!(this instanceof IonDatagram));
        IonValueLite value = this;
        while ((c = value._context.getContextContainer()) != null && !(c instanceof IonDatagram)) {
            value = c;
        }
        return value;
    }

    @Override
    public final int getElementId() {
        return this._elementid();
    }

    @Override
    public SymbolToken getFieldNameSymbol() {
        return this.getFieldNameSymbol(new LazySymbolTableProvider(this));
    }

    @Override
    public final SymbolToken getFieldNameSymbol(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int sid = this._fieldId;
        String text = this._fieldName;
        if (text != null) {
            SymbolToken tok;
            if (sid == -1 && (tok = symbolTableProvider.getSymbolTable().find(text)) != null) {
                return tok;
            }
        } else if (sid > 0) {
            text = symbolTableProvider.getSymbolTable().findKnownSymbol(sid);
        } else {
            return null;
        }
        return PrivateUtils.newSymbolToken(text, sid);
    }

    void clearSymbolIDValues() {
        if (this._fieldName != null) {
            this._fieldId = -1;
        }
        if (this._annotations != null) {
            SymbolToken annotation;
            for (int i = 0; i < this._annotations.length && (annotation = this._annotations[i]) != null; ++i) {
                String text = annotation.getText();
                if (text == null || annotation.getSid() == -1) continue;
                this._annotations[i] = PrivateUtils.newSymbolToken(text, -1);
            }
        }
    }

    final void setFieldName(String name) {
        assert (this.getContainer() instanceof IonStructLite);
        assert (this._fieldId == -1 && this._fieldName == null);
        this._fieldName = name;
    }

    final void setFieldNameSymbol(SymbolToken name) {
        assert (this.getContainer() == null);
        assert (this._fieldId == -1 && this._fieldName == null);
        this._fieldName = name.getText();
        this._fieldId = name.getSid();
    }

    @Override
    public final String getFieldName() {
        if (this._fieldName != null) {
            return this._fieldName;
        }
        if (this._fieldId < 0) {
            return null;
        }
        throw new UnknownSymbolException(this._fieldId);
    }

    @Override
    public SymbolTable getSymbolTable() {
        assert (!(this instanceof IonDatagram));
        SymbolTable symbols = this.topLevelValue()._context.getContextSymbolTable();
        if (symbols != null) {
            return symbols;
        }
        return this._context.getSystem().getSystemSymbolTable();
    }

    @Override
    public SymbolTable getAssignedSymbolTable() {
        assert (!(this instanceof IonDatagram));
        SymbolTable symbols = this._context.getContextSymbolTable();
        return symbols;
    }

    @Override
    public IonSystemLite getSystem() {
        return this._context.getSystem();
    }

    @Override
    public IonType getType() {
        throw new UnsupportedOperationException("this type " + this.getClass().getSimpleName() + " should not be instanciated, there is not IonType associated with it");
    }

    @Override
    public SymbolToken[] getTypeAnnotationSymbols() {
        return this.getTypeAnnotationSymbols(new LazySymbolTableProvider(this));
    }

    @Override
    public final SymbolToken[] getTypeAnnotationSymbols(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int count = 0;
        if (this._annotations != null) {
            for (int i = 0; i < this._annotations.length; ++i) {
                if (this._annotations[i] == null) continue;
                ++count;
            }
        }
        if (count == 0) {
            return SymbolToken.EMPTY_ARRAY;
        }
        SymbolToken[] users_copy = new SymbolToken[count];
        for (int i = 0; i < count; ++i) {
            SymbolToken interned;
            SymbolToken token = this._annotations[i];
            String text = token.getText();
            if (text != null && token.getSid() == -1 && (interned = symbolTableProvider.getSymbolTable().find(text)) != null) {
                token = interned;
            }
            users_copy[i] = token;
        }
        return users_copy;
    }

    @Override
    public void setTypeAnnotationSymbols(SymbolToken ... annotations) {
        this.checkForLock();
        if (annotations == null || annotations.length == 0) {
            this._annotations = SymbolToken.EMPTY_ARRAY;
        } else {
            PrivateUtils.ensureNonEmptySymbols(annotations);
            this._annotations = (SymbolToken[])annotations.clone();
        }
    }

    @Override
    public final String[] getTypeAnnotations() {
        int count = 0;
        if (this._annotations != null) {
            int ii = 0;
            while (ii < this._annotations.length && this._annotations[ii] != null) {
                count = ++ii;
            }
        }
        if (count == 0) {
            return PrivateUtils.EMPTY_STRING_ARRAY;
        }
        return PrivateUtils.toStrings(this._annotations, count);
    }

    @Override
    public void setTypeAnnotations(String ... annotations) {
        this.checkForLock();
        this._annotations = PrivateUtils.newSymbolTokens(this.getSymbolTable(), annotations);
    }

    @Override
    public final boolean hasTypeAnnotation(String annotation) {
        int pos;
        return annotation != null && annotation.length() > 0 && (pos = this.find_type_annotation(annotation)) >= 0;
    }

    private final int find_type_annotation(String annotation) {
        assert (annotation != null && annotation.length() > 0);
        if (this._annotations != null) {
            SymbolToken a;
            for (int ii = 0; ii < this._annotations.length && (a = this._annotations[ii]) != null; ++ii) {
                if (!annotation.equals(a.getText())) continue;
                return ii;
            }
        }
        return -1;
    }

    protected int hashTypeAnnotations(int original, PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        SymbolToken[] tokens = this.getTypeAnnotationSymbols(symbolTableProvider);
        if (tokens.length == 0) {
            return original;
        }
        int sidHashSalt = 127;
        int textHashSalt = 31;
        int prime = 8191;
        int result = original ^ TYPE_ANNOTATION_HASH_SIGNATURE;
        result = 8191 * original + tokens.length;
        for (SymbolToken token : tokens) {
            String text = token.getText();
            int tokenHashCode = text == null ? token.getSid() * 127 : text.hashCode() * 31;
            tokenHashCode ^= tokenHashCode << 19 ^ tokenHashCode >> 13;
            result = 8191 * result + tokenHashCode;
            result ^= result << 25 ^ result >> 7;
        }
        return result;
    }

    @Override
    public final boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof IonValue) {
            return Equivalence.ionEquals(this, (IonValue)other);
        }
        return false;
    }

    @Override
    public final boolean isNullValue() {
        return this._isNullValue();
    }

    @Override
    public final boolean isReadOnly() {
        return this._isLocked();
    }

    @Override
    public void makeReadOnly() {
        if (!this._isLocked()) {
            this.makeReadOnlyInternal();
        }
    }

    void makeReadOnlyInternal() {
        this.clearSymbolIDValues();
        this._isLocked(true);
    }

    final void checkForLock() throws ReadOnlyValueException {
        if (this._isLocked()) {
            throw new ReadOnlyValueException();
        }
    }

    @Override
    public boolean removeFromContainer() {
        this.checkForLock();
        boolean removed = false;
        IonContainerLite parent = this._context.getContextContainer();
        if (parent != null) {
            removed = parent.remove(this);
        }
        return removed;
    }

    @Override
    public void removeTypeAnnotation(String annotation) {
        this.checkForLock();
        if (annotation != null && annotation.length() > 0) {
            SymbolToken a;
            int ii;
            int pos = this.find_type_annotation(annotation);
            if (pos < 0) {
                return;
            }
            for (ii = pos; ii < this._annotations.length - 1 && (a = this._annotations[ii + 1]) != null; ++ii) {
                this._annotations[ii] = a;
            }
            if (ii < this._annotations.length) {
                this._annotations[ii] = null;
            }
        }
    }

    @Override
    public String toString() {
        return this.toString(TO_STRING_TEXT_WRITER_BUILDER);
    }

    @Override
    public String toString(IonTextWriterBuilder writerBuilder) {
        StringBuilder buf = new StringBuilder(1024);
        try {
            IonWriter writer = writerBuilder.build(buf);
            this.writeTo(writer);
            writer.finish();
        }
        catch (IOException e) {
            throw new IonException(e);
        }
        return buf.toString();
    }

    @Override
    public String toPrettyString() {
        return this.toString(IonTextWriterBuilder.pretty());
    }

    @Override
    public void writeTo(IonWriter writer) {
        this.writeTo(writer, new LazySymbolTableProvider(this));
    }

    final void writeChildren(IonWriter writer, Iterable<IonValue> container, PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        boolean isDatagram = this instanceof IonDatagram;
        for (IonValue iv : container) {
            IonValueLite vlite = (IonValueLite)iv;
            if (isDatagram) {
                vlite.writeTo(writer);
                continue;
            }
            vlite.writeTo(writer, symbolTableProvider);
        }
    }

    final void writeTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        if (writer.isInStruct() && !((PrivateIonWriter)writer).isFieldNameSet()) {
            SymbolToken tok = this.getFieldNameSymbol(symbolTableProvider);
            if (tok == null) {
                throw new IllegalStateException("Field name not set");
            }
            writer.setFieldNameSymbol(tok);
        }
        SymbolToken[] annotations = this.getTypeAnnotationSymbols(symbolTableProvider);
        writer.setTypeAnnotationSymbols(annotations);
        try {
            this.writeBodyTo(writer, symbolTableProvider);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    abstract void writeBodyTo(IonWriter var1, PrivateIonValue.SymbolTableProvider var2) throws IOException;

    @Override
    public void setSymbolTable(SymbolTable symbols) {
        if (this.getContext() instanceof TopLevelContext) {
            IonDatagramLite datagram = (IonDatagramLite)this.getContainer();
            datagram.setSymbolTableAtIndex(this._elementid(), symbols);
        } else if (this.topLevelValue() == this) {
            this.setContext(ContainerlessContext.wrap(this.getContext().getSystem(), symbols));
        } else {
            throw new UnsupportedOperationException("can't set the symboltable of a child value");
        }
    }

    final void setContext(IonContext context) {
        assert (context != null);
        this.checkForLock();
        this.clearSymbolIDValues();
        this._context = context;
    }

    IonContext getContext() {
        return this._context;
    }

    final void validateThisNotNull() throws NullValueException {
        if (this._isNullValue()) {
            throw new NullValueException();
        }
    }

    final void detachFromContainer() {
        this.checkForLock();
        this.clearSymbolIDValues();
        this._context = ContainerlessContext.wrap(this.getSystem());
        this._fieldName = null;
        this._fieldId = -1;
        this._elementid(0);
    }

    @Override
    public void dump(PrintWriter out) {
        out.println(this);
    }

    @Override
    public String validate() {
        return null;
    }

    static class LazySymbolTableProvider
    implements PrivateIonValue.SymbolTableProvider {
        SymbolTable symtab = null;
        final IonValueLite value;

        LazySymbolTableProvider(IonValueLite value) {
            this.value = value;
        }

        public SymbolTable getSymbolTable() {
            if (this.symtab == null) {
                this.symtab = this.value.getSymbolTable();
            }
            return this.symtab;
        }
    }
}

