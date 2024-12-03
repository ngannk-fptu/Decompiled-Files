/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.tiff.TiffElement;
import org.apache.commons.imaging.formats.tiff.TiffTags;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class TiffField {
    private static final Logger LOGGER = Logger.getLogger(TiffField.class.getName());
    private final TagInfo tagInfo;
    private final int tag;
    private final int directoryType;
    private final FieldType fieldType;
    private final long count;
    private final long offset;
    private final byte[] value;
    private final ByteOrder byteOrder;
    private final int sortHint;

    public TiffField(int tag, int directoryType, FieldType fieldType, long count, long offset, byte[] value, ByteOrder byteOrder, int sortHint) {
        this.tag = tag;
        this.directoryType = directoryType;
        this.fieldType = fieldType;
        this.count = count;
        this.offset = offset;
        this.value = value;
        this.byteOrder = byteOrder;
        this.sortHint = sortHint;
        this.tagInfo = TiffTags.getTag(directoryType, tag);
    }

    public int getDirectoryType() {
        return this.directoryType;
    }

    public TagInfo getTagInfo() {
        return this.tagInfo;
    }

    public int getTag() {
        return this.tag;
    }

    public FieldType getFieldType() {
        return this.fieldType;
    }

    public long getCount() {
        return this.count;
    }

    public int getOffset() {
        return (int)this.offset;
    }

    public ByteOrder getByteOrder() {
        return this.byteOrder;
    }

    public int getSortHint() {
        return this.sortHint;
    }

    public boolean isLocalValue() {
        return this.count * (long)this.fieldType.getSize() <= 4L;
    }

    public int getBytesLength() {
        return (int)this.count * this.fieldType.getSize();
    }

    public byte[] getByteArrayValue() {
        return BinaryFunctions.head(this.value, this.getBytesLength());
    }

    public TiffElement getOversizeValueElement() {
        if (this.isLocalValue()) {
            return null;
        }
        return new OversizeValueElement(this.getOffset(), this.value.length);
    }

    public String getValueDescription() {
        try {
            return this.getValueDescription(this.getValue());
        }
        catch (ImageReadException e) {
            return "Invalid value: " + e.getMessage();
        }
    }

    private String getValueDescription(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number) {
            return o.toString();
        }
        if (o instanceof String) {
            return "'" + o.toString().trim() + "'";
        }
        if (o instanceof Date) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
            return df.format((Date)o);
        }
        if (o instanceof Object[]) {
            Object[] objects = (Object[])o;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < objects.length; ++i) {
                Object object = objects[i];
                if (i > 50) {
                    result.append("... (").append(objects.length).append(")");
                    break;
                }
                if (i > 0) {
                    result.append(", ");
                }
                result.append(object.toString());
            }
            return result.toString();
        }
        if (o instanceof short[]) {
            short[] values = (short[])o;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < values.length; ++i) {
                short sval = values[i];
                if (i > 50) {
                    result.append("... (").append(values.length).append(")");
                    break;
                }
                if (i > 0) {
                    result.append(", ");
                }
                result.append(sval);
            }
            return result.toString();
        }
        if (o instanceof int[]) {
            int[] values = (int[])o;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < values.length; ++i) {
                int iVal = values[i];
                if (i > 50) {
                    result.append("... (").append(values.length).append(")");
                    break;
                }
                if (i > 0) {
                    result.append(", ");
                }
                result.append(iVal);
            }
            return result.toString();
        }
        if (o instanceof long[]) {
            long[] values = (long[])o;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < values.length; ++i) {
                long lVal = values[i];
                if (i > 50) {
                    result.append("... (").append(values.length).append(")");
                    break;
                }
                if (i > 0) {
                    result.append(", ");
                }
                result.append(lVal);
            }
            return result.toString();
        }
        if (o instanceof double[]) {
            double[] values = (double[])o;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < values.length; ++i) {
                double dVal = values[i];
                if (i > 50) {
                    result.append("... (").append(values.length).append(")");
                    break;
                }
                if (i > 0) {
                    result.append(", ");
                }
                result.append(dVal);
            }
            return result.toString();
        }
        if (o instanceof byte[]) {
            byte[] values = (byte[])o;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < values.length; ++i) {
                byte bVal = values[i];
                if (i > 50) {
                    result.append("... (").append(values.length).append(")");
                    break;
                }
                if (i > 0) {
                    result.append(", ");
                }
                result.append(bVal);
            }
            return result.toString();
        }
        if (o instanceof char[]) {
            char[] values = (char[])o;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < values.length; ++i) {
                char cVal = values[i];
                if (i > 50) {
                    result.append("... (").append(values.length).append(")");
                    break;
                }
                if (i > 0) {
                    result.append(", ");
                }
                result.append(cVal);
            }
            return result.toString();
        }
        if (o instanceof float[]) {
            float[] values = (float[])o;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < values.length; ++i) {
                float fVal = values[i];
                if (i > 50) {
                    result.append("... (").append(values.length).append(")");
                    break;
                }
                if (i > 0) {
                    result.append(", ");
                }
                result.append(fVal);
            }
            return result.toString();
        }
        return "Unknown: " + o.getClass().getName();
    }

    public void dump() {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw);){
            this.dump(pw);
            pw.flush();
            sw.flush();
            LOGGER.fine(sw.toString());
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void dump(PrintWriter pw) {
        this.dump(pw, null);
    }

    public void dump(PrintWriter pw, String prefix) {
        if (prefix != null) {
            pw.print(prefix + ": ");
        }
        pw.println(this.toString());
        pw.flush();
    }

    public String getDescriptionWithoutValue() {
        return this.getTag() + " (0x" + Integer.toHexString(this.getTag()) + ": " + this.getTagInfo().name + "): ";
    }

    public String toString() {
        return this.getTag() + " (0x" + Integer.toHexString(this.getTag()) + ": " + this.getTagInfo().name + "): " + this.getValueDescription() + " (" + this.getCount() + " " + this.getFieldType().getName() + ")";
    }

    public String getTagName() {
        if (this.getTagInfo() == TiffTagConstants.TIFF_TAG_UNKNOWN) {
            return this.getTagInfo().name + " (0x" + Integer.toHexString(this.getTag()) + ")";
        }
        return this.getTagInfo().name;
    }

    public String getFieldTypeName() {
        return this.getFieldType().getName();
    }

    public Object getValue() throws ImageReadException {
        return this.getTagInfo().getValue(this);
    }

    public String getStringValue() throws ImageReadException {
        Object o = this.getValue();
        if (o == null) {
            return null;
        }
        if (!(o instanceof String)) {
            throw new ImageReadException("Expected String value(" + this.getTagInfo().getDescription() + "): " + o);
        }
        return (String)o;
    }

    public int[] getIntArrayValue() throws ImageReadException {
        Object o = this.getValue();
        if (o instanceof Number) {
            return new int[]{((Number)o).intValue()};
        }
        if (o instanceof Number[]) {
            Number[] numbers = (Number[])o;
            int[] result = new int[numbers.length];
            for (int i = 0; i < numbers.length; ++i) {
                result[i] = numbers[i].intValue();
            }
            return result;
        }
        if (o instanceof short[]) {
            short[] numbers = (short[])o;
            int[] result = new int[numbers.length];
            for (int i = 0; i < numbers.length; ++i) {
                result[i] = 0xFFFF & numbers[i];
            }
            return result;
        }
        if (o instanceof int[]) {
            int[] numbers = (int[])o;
            int[] result = new int[numbers.length];
            System.arraycopy(numbers, 0, result, 0, numbers.length);
            return result;
        }
        throw new ImageReadException("Unknown value: " + o + " for: " + this.getTagInfo().getDescription());
    }

    public double[] getDoubleArrayValue() throws ImageReadException {
        Object o = this.getValue();
        if (o instanceof Number) {
            return new double[]{((Number)o).doubleValue()};
        }
        if (o instanceof Number[]) {
            Number[] numbers = (Number[])o;
            double[] result = new double[numbers.length];
            for (int i = 0; i < numbers.length; ++i) {
                result[i] = numbers[i].doubleValue();
            }
            return result;
        }
        if (o instanceof short[]) {
            short[] numbers = (short[])o;
            double[] result = new double[numbers.length];
            for (int i = 0; i < numbers.length; ++i) {
                result[i] = numbers[i];
            }
            return result;
        }
        if (o instanceof int[]) {
            int[] numbers = (int[])o;
            double[] result = new double[numbers.length];
            for (int i = 0; i < numbers.length; ++i) {
                result[i] = numbers[i];
            }
            return result;
        }
        if (o instanceof float[]) {
            float[] numbers = (float[])o;
            double[] result = new double[numbers.length];
            for (int i = 0; i < numbers.length; ++i) {
                result[i] = numbers[i];
            }
            return result;
        }
        if (o instanceof double[]) {
            double[] numbers = (double[])o;
            double[] result = new double[numbers.length];
            System.arraycopy(numbers, 0, result, 0, numbers.length);
            return result;
        }
        throw new ImageReadException("Unknown value: " + o + " for: " + this.getTagInfo().getDescription());
    }

    public int getIntValueOrArraySum() throws ImageReadException {
        Object o = this.getValue();
        if (o instanceof Number) {
            return ((Number)o).intValue();
        }
        if (o instanceof Number[]) {
            Number[] numbers = (Number[])o;
            int sum = 0;
            for (Number number : numbers) {
                sum += number.intValue();
            }
            return sum;
        }
        if (o instanceof short[]) {
            short[] numbers = (short[])o;
            int sum = 0;
            for (short number : numbers) {
                sum += number;
            }
            return sum;
        }
        if (o instanceof int[]) {
            int[] numbers = (int[])o;
            int sum = 0;
            for (int number : numbers) {
                sum += number;
            }
            return sum;
        }
        throw new ImageReadException("Unknown value: " + o + " for: " + this.getTagInfo().getDescription());
    }

    public int getIntValue() throws ImageReadException {
        Object o = this.getValue();
        if (o == null) {
            throw new ImageReadException("Missing value: " + this.getTagInfo().getDescription());
        }
        return ((Number)o).intValue();
    }

    public double getDoubleValue() throws ImageReadException {
        Object o = this.getValue();
        if (o == null) {
            throw new ImageReadException("Missing value: " + this.getTagInfo().getDescription());
        }
        return ((Number)o).doubleValue();
    }

    public final class OversizeValueElement
    extends TiffElement {
        public OversizeValueElement(int offset, int length) {
            super(offset, length);
        }

        @Override
        public String getElementDescription() {
            return "OversizeValueElement, tag: " + TiffField.this.getTagInfo().name + ", fieldType: " + TiffField.this.getFieldType().getName();
        }
    }
}

