/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.util.Base64;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.util.TransientFileFactory;
import org.apache.jackrabbit.value.BinaryValue;

public class ValueHelper {
    private static final Map<Integer, Set<Integer>> SUPPORTED_CONVERSIONS = new HashMap<Integer, Set<Integer>>();

    private ValueHelper() {
    }

    private static Set<Integer> immutableSetOf(int ... types) {
        HashSet<Integer> t = new HashSet<Integer>();
        for (int type : types) {
            t.add(type);
        }
        return Collections.unmodifiableSet(t);
    }

    public static boolean isSupportedConversion(int fromType, int toType) {
        if (fromType == toType) {
            return true;
        }
        if (1 == fromType || 2 == fromType) {
            return true;
        }
        return SUPPORTED_CONVERSIONS.containsKey(fromType) && SUPPORTED_CONVERSIONS.get(fromType).contains(toType);
    }

    public static void checkSupportedConversion(int fromType, int toType) throws ValueFormatException {
        if (!ValueHelper.isSupportedConversion(fromType, toType)) {
            throw new ValueFormatException("Unsupported conversion from '" + PropertyType.nameFromValue(fromType) + "' to '" + PropertyType.nameFromValue(toType) + '\'');
        }
    }

    public static Value convert(String srcValue, int targetType, ValueFactory factory) throws ValueFormatException, IllegalArgumentException {
        if (srcValue == null) {
            return null;
        }
        return factory.createValue(srcValue, targetType);
    }

    public static Value convert(InputStream srcValue, int targetType, ValueFactory factory) throws ValueFormatException, IllegalArgumentException {
        if (srcValue == null) {
            return null;
        }
        return ValueHelper.convert(factory.createValue(srcValue), targetType, factory);
    }

    public static Value[] convert(String[] srcValues, int targetType, ValueFactory factory) throws ValueFormatException, IllegalArgumentException {
        if (srcValues == null) {
            return null;
        }
        Value[] newValues = new Value[srcValues.length];
        for (int i = 0; i < srcValues.length; ++i) {
            newValues[i] = ValueHelper.convert(srcValues[i], targetType, factory);
        }
        return newValues;
    }

    public static Value[] convert(InputStream[] srcValues, int targetType, ValueFactory factory) throws ValueFormatException, IllegalArgumentException {
        if (srcValues == null) {
            return null;
        }
        Value[] newValues = new Value[srcValues.length];
        for (int i = 0; i < srcValues.length; ++i) {
            newValues[i] = ValueHelper.convert(srcValues[i], targetType, factory);
        }
        return newValues;
    }

    public static Value[] convert(Value[] srcValues, int targetType, ValueFactory factory) throws ValueFormatException, IllegalArgumentException {
        if (srcValues == null) {
            return null;
        }
        Value[] newValues = new Value[srcValues.length];
        int srcValueType = 0;
        for (int i = 0; i < srcValues.length; ++i) {
            if (srcValues[i] == null) {
                newValues[i] = null;
                continue;
            }
            if (srcValueType == 0) {
                srcValueType = srcValues[i].getType();
            } else if (srcValueType != srcValues[i].getType()) {
                String msg = "inhomogeneous type of values";
                throw new ValueFormatException(msg);
            }
            newValues[i] = ValueHelper.convert(srcValues[i], targetType, factory);
        }
        return newValues;
    }

    public static Value convert(Value srcValue, int targetType, ValueFactory factory) throws ValueFormatException, IllegalStateException, IllegalArgumentException {
        Value val;
        if (srcValue == null) {
            return null;
        }
        int srcType = srcValue.getType();
        if (srcType == targetType) {
            return srcValue;
        }
        block16 : switch (targetType) {
            case 1: {
                try {
                    val = factory.createValue(srcValue.getString());
                    break;
                }
                catch (RepositoryException re) {
                    throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType), re);
                }
            }
            case 2: {
                try {
                    val = factory.createValue(srcValue.getBinary());
                    break;
                }
                catch (RepositoryException re) {
                    throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType), re);
                }
            }
            case 6: {
                try {
                    val = factory.createValue(srcValue.getBoolean());
                    break;
                }
                catch (RepositoryException re) {
                    throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType), re);
                }
            }
            case 5: {
                try {
                    val = factory.createValue(srcValue.getDate());
                    break;
                }
                catch (RepositoryException re) {
                    throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType), re);
                }
            }
            case 4: {
                try {
                    val = factory.createValue(srcValue.getDouble());
                    break;
                }
                catch (RepositoryException re) {
                    throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType), re);
                }
            }
            case 3: {
                try {
                    val = factory.createValue(srcValue.getLong());
                    break;
                }
                catch (RepositoryException re) {
                    throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType), re);
                }
            }
            case 12: {
                try {
                    val = factory.createValue(srcValue.getDecimal());
                    break;
                }
                catch (RepositoryException re) {
                    throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType), re);
                }
            }
            case 8: {
                switch (srcType) {
                    case 8: {
                        return srcValue;
                    }
                    case 1: 
                    case 2: 
                    case 7: {
                        String path;
                        try {
                            path = srcValue.getString();
                        }
                        catch (RepositoryException re) {
                            throw new ValueFormatException("failed to convert source value to PATH value", re);
                        }
                        val = factory.createValue(path, targetType);
                        break block16;
                    }
                    case 11: {
                        URI uri;
                        try {
                            uri = URI.create(srcValue.getString());
                        }
                        catch (RepositoryException re) {
                            throw new ValueFormatException("failed to convert source value to PATH value", re);
                        }
                        if (uri.isAbsolute()) {
                            throw new ValueFormatException("failed to convert URI value to PATH value");
                        }
                        String p = uri.getPath();
                        if (p.startsWith("./")) {
                            p = p.substring(2);
                        }
                        val = factory.createValue(p, targetType);
                        break block16;
                    }
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 9: 
                    case 10: 
                    case 12: {
                        throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType));
                    }
                }
                throw new IllegalArgumentException("not a valid type constant: " + srcType);
            }
            case 7: {
                switch (srcType) {
                    case 7: {
                        return srcValue;
                    }
                    case 1: 
                    case 2: 
                    case 8: {
                        String name;
                        try {
                            name = srcValue.getString();
                        }
                        catch (RepositoryException re) {
                            throw new ValueFormatException("failed to convert source value to NAME value", re);
                        }
                        val = factory.createValue(name, targetType);
                        break block16;
                    }
                    case 11: {
                        URI uri;
                        try {
                            uri = URI.create(srcValue.getString());
                        }
                        catch (RepositoryException re) {
                            throw new ValueFormatException("failed to convert source value to NAME value", re);
                        }
                        if (uri.isAbsolute()) {
                            throw new ValueFormatException("failed to convert URI value to NAME value");
                        }
                        String p = uri.getPath();
                        if (p.startsWith("./")) {
                            p = p.substring(2);
                        }
                        val = factory.createValue(p, targetType);
                        break block16;
                    }
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 9: 
                    case 10: 
                    case 12: {
                        throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType));
                    }
                }
                throw new IllegalArgumentException("not a valid type constant: " + srcType);
            }
            case 9: {
                switch (srcType) {
                    case 9: {
                        return srcValue;
                    }
                    case 1: 
                    case 2: 
                    case 10: {
                        String uuid;
                        try {
                            uuid = srcValue.getString();
                        }
                        catch (RepositoryException re) {
                            throw new ValueFormatException("failed to convert source value to REFERENCE value", re);
                        }
                        val = factory.createValue(uuid, targetType);
                        break block16;
                    }
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 11: 
                    case 12: {
                        throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType));
                    }
                }
                throw new IllegalArgumentException("not a valid type constant: " + srcType);
            }
            case 10: {
                switch (srcType) {
                    case 10: {
                        return srcValue;
                    }
                    case 1: 
                    case 2: 
                    case 9: {
                        String uuid;
                        try {
                            uuid = srcValue.getString();
                        }
                        catch (RepositoryException re) {
                            throw new ValueFormatException("failed to convert source value to WEAKREFERENCE value", re);
                        }
                        val = factory.createValue(uuid, targetType);
                        break block16;
                    }
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 11: 
                    case 12: {
                        throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType));
                    }
                }
                throw new IllegalArgumentException("not a valid type constant: " + srcType);
            }
            case 11: {
                switch (srcType) {
                    case 11: {
                        return srcValue;
                    }
                    case 1: 
                    case 2: {
                        String uuid;
                        try {
                            uuid = srcValue.getString();
                        }
                        catch (RepositoryException re) {
                            throw new ValueFormatException("failed to convert source value to URI value", re);
                        }
                        val = factory.createValue(uuid, targetType);
                        break block16;
                    }
                    case 7: {
                        String name;
                        try {
                            name = srcValue.getString();
                        }
                        catch (RepositoryException re) {
                            throw new ValueFormatException("failed to convert source value to URI value", re);
                        }
                        val = factory.createValue("./" + name, targetType);
                        break block16;
                    }
                    case 8: {
                        String path;
                        try {
                            path = srcValue.getString();
                        }
                        catch (RepositoryException re) {
                            throw new ValueFormatException("failed to convert source value to URI value", re);
                        }
                        if (!path.startsWith("/")) {
                            path = "./" + path;
                        }
                        val = factory.createValue(path, targetType);
                        break block16;
                    }
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 9: 
                    case 10: 
                    case 12: {
                        throw new ValueFormatException("conversion failed: " + PropertyType.nameFromValue(srcType) + " to " + PropertyType.nameFromValue(targetType));
                    }
                }
                throw new IllegalArgumentException("not a valid type constant: " + srcType);
            }
            default: {
                throw new IllegalArgumentException("not a valid type constant: " + targetType);
            }
        }
        return val;
    }

    public static Value copy(Value srcValue, ValueFactory factory) throws IllegalStateException {
        if (srcValue == null) {
            return null;
        }
        Value newVal = null;
        try {
            switch (srcValue.getType()) {
                case 2: {
                    newVal = factory.createValue(srcValue.getStream());
                    break;
                }
                case 6: {
                    newVal = factory.createValue(srcValue.getBoolean());
                    break;
                }
                case 5: {
                    newVal = factory.createValue(srcValue.getDate());
                    break;
                }
                case 4: {
                    newVal = factory.createValue(srcValue.getDouble());
                    break;
                }
                case 3: {
                    newVal = factory.createValue(srcValue.getLong());
                    break;
                }
                case 12: {
                    newVal = factory.createValue(srcValue.getDecimal());
                    break;
                }
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: {
                    newVal = factory.createValue(srcValue.getString(), srcValue.getType());
                    break;
                }
                case 1: {
                    newVal = factory.createValue(srcValue.getString());
                }
            }
        }
        catch (RepositoryException repositoryException) {
            // empty catch block
        }
        return newVal;
    }

    public static Value[] copy(Value[] srcValues, ValueFactory factory) throws IllegalStateException {
        if (srcValues == null) {
            return null;
        }
        Value[] newValues = new Value[srcValues.length];
        for (int i = 0; i < srcValues.length; ++i) {
            newValues[i] = ValueHelper.copy(srcValues[i], factory);
        }
        return newValues;
    }

    public static String serialize(Value value, boolean encodeBlanks) throws IllegalStateException, RepositoryException {
        StringWriter writer = new StringWriter();
        try {
            ValueHelper.serialize(value, encodeBlanks, false, writer);
        }
        catch (IOException ioe) {
            throw new RepositoryException("failed to serialize value", ioe);
        }
        return writer.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void serialize(Value value, boolean encodeBlanks, boolean enforceBase64, Writer writer) throws IllegalStateException, IOException, RepositoryException {
        if (value.getType() == 2) {
            InputStream in = value.getStream();
            try {
                Base64.encode(in, writer);
            }
            finally {
                try {
                    in.close();
                }
                catch (IOException iOException) {}
            }
        }
        String textVal = value.getString();
        if (enforceBase64) {
            byte[] bytes = textVal.getBytes(StandardCharsets.UTF_8);
            Base64.encode(bytes, 0, bytes.length, writer);
        } else {
            if (encodeBlanks) {
                textVal = Text.replace(textVal, " ", "_x0020_");
            }
            writer.write(textVal);
        }
    }

    public static Value deserialize(String value, int type, boolean decodeBlanks, ValueFactory factory) throws ValueFormatException, RepositoryException {
        if (type == 2) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                Base64.decode(value, (OutputStream)baos);
            }
            catch (IOException ioe) {
                throw new RepositoryException("failed to decode binary value", ioe);
            }
            return new BinaryValue(baos.toByteArray());
        }
        if (decodeBlanks) {
            value = Text.replace(value, "_x0020_", " ");
        }
        return ValueHelper.convert(value, type, factory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Value deserialize(Reader reader, int type, boolean decodeBlanks, ValueFactory factory) throws IOException, ValueFormatException, RepositoryException {
        int read;
        if (type == 2) {
            TransientFileFactory fileFactory = TransientFileFactory.getInstance();
            final File tmpFile = fileFactory.createTransientFile("bin", null, null);
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmpFile));){
                Base64.decode(reader, (OutputStream)out);
            }
            return factory.createValue(new FilterInputStream(new FileInputStream(tmpFile)){

                @Override
                public void close() throws IOException {
                    this.in.close();
                    tmpFile.delete();
                }
            });
        }
        char[] chunk = new char[8192];
        StringBuilder buf = new StringBuilder();
        while ((read = reader.read(chunk)) > -1) {
            buf.append(chunk, 0, read);
        }
        String value = buf.toString();
        if (decodeBlanks) {
            value = Text.replace(value, "_x0020_", " ");
        }
        return ValueHelper.convert(value, type, factory);
    }

    public static int getType(Value[] values) throws ValueFormatException {
        int type = 0;
        for (Value value : values) {
            if (value == null) continue;
            if (type == 0) {
                type = value.getType();
                continue;
            }
            if (value.getType() == type) continue;
            throw new ValueFormatException("All values of a multi-valued property must be of the same type");
        }
        return type;
    }

    static {
        SUPPORTED_CONVERSIONS.put(5, ValueHelper.immutableSetOf(1, 2, 4, 12, 3));
        SUPPORTED_CONVERSIONS.put(4, ValueHelper.immutableSetOf(1, 2, 12, 5, 3));
        SUPPORTED_CONVERSIONS.put(12, ValueHelper.immutableSetOf(1, 2, 4, 5, 3));
        SUPPORTED_CONVERSIONS.put(3, ValueHelper.immutableSetOf(1, 2, 12, 5, 4));
        SUPPORTED_CONVERSIONS.put(6, ValueHelper.immutableSetOf(1, 2));
        SUPPORTED_CONVERSIONS.put(7, ValueHelper.immutableSetOf(1, 2, 8, 11));
        SUPPORTED_CONVERSIONS.put(8, ValueHelper.immutableSetOf(1, 2, 7, 11));
        SUPPORTED_CONVERSIONS.put(11, ValueHelper.immutableSetOf(1, 2, 7, 8));
        SUPPORTED_CONVERSIONS.put(9, ValueHelper.immutableSetOf(1, 2, 10));
        SUPPORTED_CONVERSIONS.put(10, ValueHelper.immutableSetOf(1, 2, 9));
    }
}

