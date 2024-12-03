/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import software.amazon.ion.IonString;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonTextLite;

final class IonStringLite
extends IonTextLite
implements IonString {
    private static final int HASH_SIGNATURE = IonType.STRING.toString().hashCode();

    IonStringLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonStringLite(IonStringLite existing, IonContext context) {
        super(existing, context);
    }

    IonStringLite clone(IonContext parentContext) {
        return new IonStringLite(this, parentContext);
    }

    public IonStringLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int result = HASH_SIGNATURE;
        if (!this.isNullValue()) {
            result ^= this.stringValue().hashCode();
        }
        return this.hashTypeAnnotations(result, symbolTableProvider);
    }

    public IonType getType() {
        return IonType.STRING;
    }

    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        writer.writeString(this._get_value());
    }

    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

