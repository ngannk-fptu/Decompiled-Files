/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import java.io.InputStream;
import software.amazon.ion.IonBlob;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonLobLite;

final class IonBlobLite
extends IonLobLite
implements IonBlob {
    private static final int HASH_SIGNATURE = IonType.BLOB.toString().hashCode();

    IonBlobLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonBlobLite(IonBlobLite existing, IonContext context) {
        super(existing, context);
    }

    IonBlobLite clone(IonContext context) {
        return new IonBlobLite(this, context);
    }

    public IonBlobLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        return this.lobHashCode(HASH_SIGNATURE, symbolTableProvider);
    }

    public IonType getType() {
        return IonType.BLOB;
    }

    public void printBase64(Appendable out) throws IOException {
        this.validateThisNotNull();
        InputStream byteStream = this.newInputStream();
        try {
            PrivateUtils.writeAsBase64(byteStream, out);
        }
        finally {
            byteStream.close();
        }
    }

    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        writer.writeBlob(this.getBytesNoCopy());
    }

    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

