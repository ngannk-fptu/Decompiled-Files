/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.OutputStream;
import software.amazon.ion.impl.AppendableFastAppendable;
import software.amazon.ion.impl.OutputStreamFastAppendable;
import software.amazon.ion.util.PrivateFastAppendable;

@Deprecated
public final class PrivateFastAppendableTrampoline {
    public static PrivateFastAppendable forAppendable(Appendable appendable) {
        return new AppendableFastAppendable(appendable);
    }

    public static PrivateFastAppendable forOutputStream(OutputStream outputStream) {
        return new OutputStreamFastAppendable(outputStream);
    }
}

