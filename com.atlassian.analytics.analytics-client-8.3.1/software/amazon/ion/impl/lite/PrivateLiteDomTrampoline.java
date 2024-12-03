/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import software.amazon.ion.IonSystem;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.PrivateIonBinaryWriterBuilder;
import software.amazon.ion.impl.lite.IonSystemLite;
import software.amazon.ion.impl.lite.ReverseBinaryEncoder;
import software.amazon.ion.system.IonTextWriterBuilder;

@Deprecated
public final class PrivateLiteDomTrampoline {
    public static IonSystem newLiteSystem(IonTextWriterBuilder twb, PrivateIonBinaryWriterBuilder bwb) {
        return new IonSystemLite(twb, bwb);
    }

    public static boolean isLiteSystem(IonSystem system) {
        return system instanceof IonSystemLite;
    }

    public static byte[] reverseEncode(int initialSize, SymbolTable symtab) {
        ReverseBinaryEncoder encoder = new ReverseBinaryEncoder(initialSize);
        encoder.serialize(symtab);
        return encoder.toNewByteArray();
    }
}

