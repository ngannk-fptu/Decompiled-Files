/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.emitter;

import java.io.Serializable;
import java.util.Objects;

public class EmitKey
implements Serializable {
    private static final long serialVersionUID = -3861669115439125268L;
    private String emitterName;
    private String emitKey;

    public EmitKey() {
    }

    public EmitKey(String emitterName, String emitKey) {
        this.emitterName = emitterName;
        this.emitKey = emitKey;
    }

    public String getEmitterName() {
        return this.emitterName;
    }

    public String getEmitKey() {
        return this.emitKey;
    }

    public String toString() {
        return "EmitterKey{emitterName='" + this.emitterName + '\'' + ", emitterKey='" + this.emitKey + '\'' + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EmitKey emitKey1 = (EmitKey)o;
        if (!Objects.equals(this.emitterName, emitKey1.emitterName)) {
            return false;
        }
        return Objects.equals(this.emitKey, emitKey1.emitKey);
    }

    public int hashCode() {
        int result = this.emitterName != null ? this.emitterName.hashCode() : 0;
        result = 31 * result + (this.emitKey != null ? this.emitKey.hashCode() : 0);
        return result;
    }
}

