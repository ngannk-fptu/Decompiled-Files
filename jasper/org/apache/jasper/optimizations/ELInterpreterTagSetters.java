/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.jasper.optimizations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.ELInterpreter;
import org.apache.jasper.compiler.JspUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class ELInterpreterTagSetters
implements ELInterpreter {
    private final Log log = LogFactory.getLog(ELInterpreterTagSetters.class);
    private final Pattern PATTERN_BOOLEAN = Pattern.compile("[$][{]([\"']?)(true|false)\\1[}]");
    private final Pattern PATTERN_STRING_CONSTANT = Pattern.compile("[$][{]([\"'])(\\w+)\\1[}]");
    private final Pattern PATTERN_NUMERIC = Pattern.compile("[$][{]([\"'])([+-]?\\d+(\\.\\d+)?)\\1[}]");

    @Override
    public String interpreterCall(JspCompilationContext context, boolean isTagFile, String expression, Class<?> expectedType, String fnmapvar) {
        String result;
        block66: {
            Matcher m;
            result = null;
            if (Boolean.TYPE == expectedType) {
                Matcher m2 = this.PATTERN_BOOLEAN.matcher(expression);
                if (m2.matches()) {
                    result = m2.group(2);
                }
            } else if (Boolean.class == expectedType) {
                Matcher m3 = this.PATTERN_BOOLEAN.matcher(expression);
                if (m3.matches()) {
                    result = "true".equals(m3.group(2)) ? "Boolean.TRUE" : "Boolean.FALSE";
                }
            } else if (Character.TYPE == expectedType) {
                Matcher m4 = this.PATTERN_STRING_CONSTANT.matcher(expression);
                if (m4.matches()) {
                    return "'" + m4.group(2).charAt(0) + "'";
                }
            } else if (Character.class == expectedType) {
                Matcher m5 = this.PATTERN_STRING_CONSTANT.matcher(expression);
                if (m5.matches()) {
                    return "Character.valueOf('" + m5.group(2).charAt(0) + "')";
                }
            } else if (BigDecimal.class == expectedType) {
                Matcher m6 = this.PATTERN_NUMERIC.matcher(expression);
                if (m6.matches()) {
                    try {
                        BigDecimal unused = new BigDecimal(m6.group(2));
                        result = "new java.math.BigDecimal(\"" + m6.group(2) + "\")";
                    }
                    catch (NumberFormatException e) {
                        this.log.debug((Object)("Failed to convert [" + m6.group(2) + "] to BigDecimal"), (Throwable)e);
                    }
                }
            } else if (Long.TYPE == expectedType || Long.class == expectedType) {
                Matcher m7 = this.PATTERN_NUMERIC.matcher(expression);
                if (m7.matches()) {
                    try {
                        Long unused = Long.valueOf(m7.group(2));
                        if (expectedType.isPrimitive()) {
                            result = m7.group(2) + "L";
                            break block66;
                        }
                        result = "Long.valueOf(\"" + m7.group(2) + "\")";
                    }
                    catch (NumberFormatException e) {
                        this.log.debug((Object)("Failed to convert [" + m7.group(2) + "] to Long"), (Throwable)e);
                    }
                }
            } else if (Integer.TYPE == expectedType || Integer.class == expectedType) {
                Matcher m8 = this.PATTERN_NUMERIC.matcher(expression);
                if (m8.matches()) {
                    try {
                        Integer unused = Integer.valueOf(m8.group(2));
                        if (expectedType.isPrimitive()) {
                            result = m8.group(2);
                            break block66;
                        }
                        result = "Integer.valueOf(\"" + m8.group(2) + "\")";
                    }
                    catch (NumberFormatException e) {
                        this.log.debug((Object)("Failed to convert [" + m8.group(2) + "] to Integer"), (Throwable)e);
                    }
                }
            } else if (Short.TYPE == expectedType || Short.class == expectedType) {
                Matcher m9 = this.PATTERN_NUMERIC.matcher(expression);
                if (m9.matches()) {
                    try {
                        Short unused = Short.valueOf(m9.group(2));
                        if (expectedType.isPrimitive()) {
                            result = "(short) " + m9.group(2);
                            break block66;
                        }
                        result = "Short.valueOf(\"" + m9.group(2) + "\")";
                    }
                    catch (NumberFormatException e) {
                        this.log.debug((Object)("Failed to convert [" + m9.group(2) + "] to Short"), (Throwable)e);
                    }
                }
            } else if (Byte.TYPE == expectedType || Byte.class == expectedType) {
                Matcher m10 = this.PATTERN_NUMERIC.matcher(expression);
                if (m10.matches()) {
                    try {
                        Byte unused = Byte.valueOf(m10.group(2));
                        if (expectedType.isPrimitive()) {
                            result = "(byte) " + m10.group(2);
                            break block66;
                        }
                        result = "Byte.valueOf(\"" + m10.group(2) + "\")";
                    }
                    catch (NumberFormatException e) {
                        this.log.debug((Object)("Failed to convert [" + m10.group(2) + "] to Byte"), (Throwable)e);
                    }
                }
            } else if (Double.TYPE == expectedType || Double.class == expectedType) {
                Matcher m11 = this.PATTERN_NUMERIC.matcher(expression);
                if (m11.matches()) {
                    try {
                        Double unused = Double.valueOf(m11.group(2));
                        if (expectedType.isPrimitive()) {
                            result = m11.group(2);
                            break block66;
                        }
                        result = "Double.valueOf(\"" + m11.group(2) + "\")";
                    }
                    catch (NumberFormatException e) {
                        this.log.debug((Object)("Failed to convert [" + m11.group(2) + "] to Double"), (Throwable)e);
                    }
                }
            } else if (Float.TYPE == expectedType || Float.class == expectedType) {
                Matcher m12 = this.PATTERN_NUMERIC.matcher(expression);
                if (m12.matches()) {
                    try {
                        Float unused = Float.valueOf(m12.group(2));
                        if (expectedType.isPrimitive()) {
                            result = m12.group(2) + "f";
                            break block66;
                        }
                        result = "Float.valueOf(\"" + m12.group(2) + "\")";
                    }
                    catch (NumberFormatException e) {
                        this.log.debug((Object)("Failed to convert [" + m12.group(2) + "] to Float"), (Throwable)e);
                    }
                }
            } else if (BigInteger.class == expectedType) {
                Matcher m13 = this.PATTERN_NUMERIC.matcher(expression);
                if (m13.matches()) {
                    try {
                        BigInteger unused = new BigInteger(m13.group(2));
                        result = "new java.math.BigInteger(\"" + m13.group(2) + "\")";
                    }
                    catch (NumberFormatException e) {
                        this.log.debug((Object)("Failed to convert [" + m13.group(2) + "] to BigInteger"), (Throwable)e);
                    }
                }
            } else if (expectedType.isEnum()) {
                Matcher m14 = this.PATTERN_STRING_CONSTANT.matcher(expression);
                if (m14.matches()) {
                    try {
                        Object enumValue = Enum.valueOf(expectedType, m14.group(2));
                        result = expectedType.getName() + "." + ((Enum)enumValue).name();
                    }
                    catch (IllegalArgumentException iae) {
                        this.log.debug((Object)("Failed to convert [" + m14.group(2) + "] to Enum type [" + expectedType.getName() + "]"), (Throwable)iae);
                    }
                }
            } else if (String.class == expectedType && (m = this.PATTERN_STRING_CONSTANT.matcher(expression)).matches()) {
                result = "\"" + m.group(2) + "\"";
            }
        }
        if (result == null) {
            result = JspUtil.interpreterCall(isTagFile, expression, expectedType, fnmapvar);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Expression [" + expression + "], type [" + expectedType.getName() + "], returns [" + result + "]"));
        }
        return result;
    }
}

