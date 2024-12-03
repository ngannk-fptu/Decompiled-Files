/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import software.amazon.ion.IonBool;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.NullValueException;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonValueLite;

final class IonBoolLite
extends IonValueLite
implements IonBool {
    private static final int HASH_SIGNATURE = IonType.BOOL.toString().hashCode();
    protected static final int TRUE_HASH = HASH_SIGNATURE ^ 16777619 * Boolean.TRUE.hashCode();
    protected static final int FALSE_HASH = HASH_SIGNATURE ^ 16777619 * Boolean.FALSE.hashCode();

    IonBoolLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonBoolLite(IonBoolLite existing, IonContext context) {
        super(existing, context);
    }

    IonBoolLite clone(IonContext context) {
        return new IonBoolLite(this, context);
    }

    public IonBoolLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    public IonType getType() {
        return IonType.BOOL;
    }

    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int result = HASH_SIGNATURE;
        if (!this.isNullValue()) {
            result = this.booleanValue() ? TRUE_HASH : FALSE_HASH;
        }
        return this.hashTypeAnnotations(result, symbolTableProvider);
    }

    public boolean booleanValue() throws NullValueException {
        this.validateThisNotNull();
        return this._isBoolTrue();
    }

    public void setValue(boolean b) {
        this.setValue((Boolean)b);
    }

    public void setValue(Boolean b) {
        this.checkForLock();
        if (b == null) {
            this._isBoolTrue(false);
            this._isNullValue(true);
        } else {
            this._isBoolTrue(b);
            this._isNullValue(false);
        }
    }

    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        if (this.isNullValue()) {
            writer.writeNull(IonType.BOOL);
        } else {
            writer.writeBool(this._isBoolTrue());
        }
    }

    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

