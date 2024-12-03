/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import java.io.Serializable;
import org.jboss.logging.Logger;

final class SerializedLogger
implements Serializable {
    private static final long serialVersionUID = 508779982439435831L;
    private final String name;

    SerializedLogger(String name) {
        this.name = name;
    }

    protected Object readResolve() {
        return Logger.getLogger(this.name);
    }
}

