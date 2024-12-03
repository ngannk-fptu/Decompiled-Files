/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.util.Collection;
import software.amazon.ion.ContainedValueException;
import software.amazon.ion.IonList;
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
final class IonListLite
extends IonSequenceLite
implements IonList {
    private static final int HASH_SIGNATURE = IonType.LIST.toString().hashCode();

    IonListLite(ContainerlessContext context, boolean makeNull) {
        super(context, makeNull);
    }

    IonListLite(IonListLite existing, IonContext context) {
        super(existing, context);
    }

    IonListLite(ContainerlessContext context, Collection<? extends IonValue> elements) throws ContainedValueException {
        super(context, elements);
    }

    @Override
    IonListLite clone(IonContext parentContext) {
        return new IonListLite(this, parentContext);
    }

    @Override
    public IonListLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    @Override
    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        return this.sequenceHashCode(HASH_SIGNATURE, symbolTableProvider);
    }

    @Override
    public IonType getType() {
        return IonType.LIST;
    }

    @Override
    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

