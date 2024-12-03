/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import software.amazon.ion.IonClob;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonLobLite;

final class IonClobLite
extends IonLobLite
implements IonClob {
    private static final int HASH_SIGNATURE = IonType.CLOB.toString().hashCode();

    IonClobLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonClobLite(IonClobLite existing, IonContext context) {
        super(existing, context);
    }

    IonClobLite clone(IonContext context) {
        return new IonClobLite(this, context);
    }

    public IonClobLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        return this.lobHashCode(HASH_SIGNATURE, symbolTableProvider);
    }

    public IonType getType() {
        return IonType.CLOB;
    }

    public Reader newReader(Charset cs) {
        InputStream in = this.newInputStream();
        if (in == null) {
            return null;
        }
        return new InputStreamReader(in, cs);
    }

    public String stringValue(Charset cs) {
        byte[] bytes = this.getBytes();
        if (bytes == null) {
            return null;
        }
        return PrivateUtils.decode(bytes, cs);
    }

    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        writer.writeClob(this.getBytesNoCopy());
    }

    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

