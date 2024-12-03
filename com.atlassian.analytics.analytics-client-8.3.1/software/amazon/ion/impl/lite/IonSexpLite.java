/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.util.Collection;
import software.amazon.ion.ContainedValueException;
import software.amazon.ion.IonSexp;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonSequenceLite;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class IonSexpLite
extends IonSequenceLite
implements IonSexp {
    private static final int HASH_SIGNATURE = IonType.SEXP.toString().hashCode();

    IonSexpLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonSexpLite(IonSexpLite existing, IonContext context) {
        super(existing, context);
    }

    IonSexpLite(ContainerlessContext context, Collection<? extends IonValue> elements) throws ContainedValueException {
        super(context, elements);
    }

    @Override
    IonSexpLite clone(IonContext parentContext) {
        return new IonSexpLite(this, parentContext);
    }

    @Override
    public IonSexpLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    @Override
    public int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        return this.sequenceHashCode(HASH_SIGNATURE, symbolTableProvider);
    }

    @Override
    public IonType getType() {
        return IonType.SEXP;
    }

    @Override
    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

