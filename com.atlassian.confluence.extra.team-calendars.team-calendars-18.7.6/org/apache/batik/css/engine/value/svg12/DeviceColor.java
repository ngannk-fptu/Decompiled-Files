/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value.svg12;

import org.apache.batik.css.engine.value.AbstractValue;
import org.w3c.dom.DOMException;

public class DeviceColor
extends AbstractValue {
    public static final String DEVICE_GRAY_COLOR_FUNCTION = "device-gray";
    public static final String DEVICE_RGB_COLOR_FUNCTION = "device-rgb";
    public static final String DEVICE_CMYK_COLOR_FUNCTION = "device-cmyk";
    public static final String DEVICE_NCHANNEL_COLOR_FUNCTION = "device-nchannel";
    protected boolean nChannel;
    protected int count;
    protected float[] colors = new float[5];

    public DeviceColor(boolean nChannel) {
        this.nChannel = nChannel;
    }

    @Override
    public short getCssValueType() {
        return 3;
    }

    public boolean isNChannel() {
        return this.nChannel;
    }

    public int getNumberOfColors() throws DOMException {
        return this.count;
    }

    public float getColor(int i) throws DOMException {
        return this.colors[i];
    }

    @Override
    public String getCssText() {
        StringBuffer sb = new StringBuffer(this.count * 8);
        if (this.nChannel) {
            sb.append(DEVICE_NCHANNEL_COLOR_FUNCTION);
        } else {
            switch (this.count) {
                case 1: {
                    sb.append(DEVICE_GRAY_COLOR_FUNCTION);
                    break;
                }
                case 3: {
                    sb.append(DEVICE_RGB_COLOR_FUNCTION);
                    break;
                }
                case 4: {
                    sb.append(DEVICE_CMYK_COLOR_FUNCTION);
                    break;
                }
                default: {
                    throw new IllegalStateException("Invalid number of components encountered");
                }
            }
        }
        sb.append('(');
        for (int i = 0; i < this.count; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.colors[i]);
        }
        sb.append(')');
        return sb.toString();
    }

    public void append(float c) {
        if (this.count == this.colors.length) {
            float[] t = new float[this.count * 2];
            System.arraycopy(this.colors, 0, t, 0, this.count);
            this.colors = t;
        }
        this.colors[this.count++] = c;
    }

    public String toString() {
        return this.getCssText();
    }
}

