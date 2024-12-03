/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateNumberModel;
import java.io.Serializable;

public final class SimpleNumber
implements TemplateNumberModel,
Serializable {
    private final Number value;

    public SimpleNumber(Number value) {
        this.value = value;
    }

    public SimpleNumber(byte val) {
        this.value = val;
    }

    public SimpleNumber(short val) {
        this.value = val;
    }

    public SimpleNumber(int val) {
        this.value = val;
    }

    public SimpleNumber(long val) {
        this.value = val;
    }

    public SimpleNumber(float val) {
        this.value = Float.valueOf(val);
    }

    public SimpleNumber(double val) {
        this.value = val;
    }

    @Override
    public Number getAsNumber() {
        return this.value;
    }

    public String toString() {
        return this.value.toString();
    }
}

