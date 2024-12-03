/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.data;

import aQute.lib.converter.Converter;
import aQute.lib.data.AllowNull;
import aQute.lib.data.Numeric;
import aQute.lib.data.Validator;
import aQute.lib.hex.Hex;
import java.lang.reflect.Field;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Data {
    public static String validate(Object o) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (Formatter formatter = new Formatter(sb);){
            String string;
            Field[] fields;
            for (Field f : fields = o.getClass().getFields()) {
                Pattern p;
                Matcher m;
                Validator patternValidator = f.getAnnotation(Validator.class);
                Numeric numericValidator = f.getAnnotation(Numeric.class);
                AllowNull allowNull = f.getAnnotation(AllowNull.class);
                Object value = f.get(o);
                if (value == null) {
                    if (allowNull != null) continue;
                    formatter.format("Value for %s must not be null%n", f.getName());
                    continue;
                }
                if (patternValidator != null && !(m = (p = Pattern.compile(patternValidator.value())).matcher(value.toString())).matches()) {
                    String reason = patternValidator.reason();
                    if (reason.length() == 0) {
                        formatter.format("Value for %s=%s does not match pattern %s%n", f.getName(), value, patternValidator.value());
                    } else {
                        formatter.format("Value for %s=%s %s%n", f.getName(), value, reason);
                    }
                }
                if (numericValidator == null) continue;
                if (o instanceof String) {
                    try {
                        o = Double.parseDouble((String)o);
                    }
                    catch (Exception e) {
                        formatter.format("Value for %s=%s %s%n", f.getName(), value, "Not a number");
                    }
                }
                try {
                    Number n = (Number)o;
                    long number = n.longValue();
                    if (number < numericValidator.min() || number >= numericValidator.max()) continue;
                    formatter.format("Value for %s=%s not in valid range (%s,%s]%n", f.getName(), value, numericValidator.min(), numericValidator.max());
                }
                catch (ClassCastException e) {
                    formatter.format("Value for %s=%s [%s,%s) is not a number%n", f.getName(), value, numericValidator.min(), numericValidator.max());
                }
            }
            if (sb.length() == 0) {
                string = null;
                return string;
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
            }
            string = sb.toString();
            return string;
        }
    }

    public static void details(Object data, Appendable out) throws Exception {
        Field[] fields = data.getClass().getFields();
        try (Formatter formatter = new Formatter(out);){
            for (Field f : fields) {
                String name = f.getName();
                name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                Object object = f.get(data);
                if (object != null && object.getClass() == byte[].class) {
                    object = Hex.toHexString((byte[])object);
                } else if (object != null && object.getClass().isArray()) {
                    object = Converter.cnv(List.class, object);
                }
                formatter.format("%-40s %s%n", name, object);
            }
        }
    }
}

