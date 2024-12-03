/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayInputStream;

@Internal
public class VariantBool {
    private static final Logger LOG = LogManager.getLogger(VariantBool.class);
    static final int SIZE = 2;
    private boolean _value;

    public void read(LittleEndianByteArrayInputStream lei) {
        short value = lei.readShort();
        switch (value) {
            case 0: {
                this._value = false;
                break;
            }
            case -1: {
                this._value = true;
                break;
            }
            default: {
                LOG.atWarn().log("VARIANT_BOOL value '{}' is incorrect", (Object)Unbox.box(value));
                this._value = true;
            }
        }
    }

    public boolean getValue() {
        return this._value;
    }

    public void setValue(boolean value) {
        this._value = value;
    }
}

