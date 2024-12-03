/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import software.amazon.ion.IonNull;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonValueLite;

final class IonNullLite
extends IonValueLite
implements IonNull {
    private static final int HASH_SIGNATURE = IonType.NULL.toString().hashCode();

    protected IonNullLite(ContainerlessContext context) {
        super(context, true);
    }

    IonNullLite(IonNullLite existing, IonContext context) {
        super(existing, context);
    }

    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    IonNullLite clone(IonContext context) {
        return new IonNullLite(this, context);
    }

    public IonNullLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    public IonType getType() {
        return IonType.NULL;
    }

    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        writer.writeNull();
    }

    public int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        return this.hashTypeAnnotations(HASH_SIGNATURE, symbolTableProvider);
    }
}

