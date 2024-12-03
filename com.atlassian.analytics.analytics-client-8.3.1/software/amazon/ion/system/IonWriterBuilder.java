/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.system;

import java.io.OutputStream;
import software.amazon.ion.IonWriter;

public abstract class IonWriterBuilder {
    IonWriterBuilder() {
    }

    public abstract InitialIvmHandling getInitialIvmHandling();

    public abstract IvmMinimizing getIvmMinimizing();

    public abstract IonWriter build(OutputStream var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum IvmMinimizing {
        ADJACENT,
        DISTANT;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum InitialIvmHandling {
        ENSURE,
        SUPPRESS;

    }
}

