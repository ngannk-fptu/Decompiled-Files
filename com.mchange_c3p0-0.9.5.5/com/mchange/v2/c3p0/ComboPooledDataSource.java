/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.c3p0.AbstractComboPooledDataSource;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.naming.Referenceable;

public final class ComboPooledDataSource
extends AbstractComboPooledDataSource
implements Serializable,
Referenceable {
    private static final long serialVersionUID = 1L;
    private static final short VERSION = 2;

    public ComboPooledDataSource() {
    }

    public ComboPooledDataSource(boolean autoregister) {
        super(autoregister);
    }

    public ComboPooledDataSource(String configName) {
        super(configName);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeShort(2);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        short version = ois.readShort();
        switch (version) {
            case 2: {
                break;
            }
            default: {
                throw new IOException("Unsupported Serialized Version: " + version);
            }
        }
    }
}

