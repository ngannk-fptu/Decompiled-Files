/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.ICOSVisitor;

public class COSFloat
extends COSNumber {
    private BigDecimal value;
    private String valueAsString;

    public COSFloat(float aFloat) {
        this.value = new BigDecimal(String.valueOf(aFloat));
        this.valueAsString = this.removeNullDigits(this.value.toPlainString());
    }

    public COSFloat(String aFloat) throws IOException {
        try {
            this.valueAsString = aFloat;
            this.value = new BigDecimal(this.valueAsString);
            this.checkMinMaxValues();
        }
        catch (NumberFormatException e) {
            if (aFloat.startsWith("--")) {
                this.valueAsString = aFloat.substring(1);
            } else if (aFloat.matches("^0\\.0*\\-\\d+")) {
                this.valueAsString = "-" + this.valueAsString.replaceFirst("\\-", "");
            } else {
                throw new IOException("Error expected floating point number actual='" + aFloat + "'", e);
            }
            try {
                this.value = new BigDecimal(this.valueAsString);
                this.checkMinMaxValues();
            }
            catch (NumberFormatException e2) {
                throw new IOException("Error expected floating point number actual='" + aFloat + "'", e2);
            }
        }
    }

    private void checkMinMaxValues() {
        float floatValue = this.value.floatValue();
        double doubleValue = this.value.doubleValue();
        boolean valueReplaced = false;
        if (floatValue == Float.NEGATIVE_INFINITY || floatValue == Float.POSITIVE_INFINITY) {
            if (Math.abs(doubleValue) > 3.4028234663852886E38) {
                floatValue = Float.MAX_VALUE * (float)(floatValue == Float.POSITIVE_INFINITY ? 1 : -1);
                valueReplaced = true;
            }
        } else if (floatValue == 0.0f && doubleValue != 0.0 && Math.abs(doubleValue) < 1.1754943508222875E-38) {
            valueReplaced = true;
        }
        if (valueReplaced) {
            this.value = BigDecimal.valueOf(floatValue);
            this.valueAsString = this.removeNullDigits(this.value.toPlainString());
        }
    }

    private String removeNullDigits(String plainStringValue) {
        if (plainStringValue.indexOf(46) > -1 && !plainStringValue.endsWith(".0")) {
            while (plainStringValue.endsWith("0") && !plainStringValue.endsWith(".0")) {
                plainStringValue = plainStringValue.substring(0, plainStringValue.length() - 1);
            }
        }
        return plainStringValue;
    }

    @Override
    public float floatValue() {
        return this.value.floatValue();
    }

    @Override
    public double doubleValue() {
        return this.value.doubleValue();
    }

    @Override
    public long longValue() {
        return this.value.longValue();
    }

    @Override
    public int intValue() {
        return this.value.intValue();
    }

    public boolean equals(Object o) {
        return o instanceof COSFloat && Float.floatToIntBits(((COSFloat)o).value.floatValue()) == Float.floatToIntBits(this.value.floatValue());
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public String toString() {
        return "COSFloat{" + this.valueAsString + "}";
    }

    @Override
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromFloat(this);
    }

    public void writePDF(OutputStream output) throws IOException {
        output.write(this.valueAsString.getBytes("ISO-8859-1"));
    }
}

