/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.jsp;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TaglibMethodUtil {
    private static final Pattern FUNCTION_SIGNATURE_PATTERN = Pattern.compile("^([\\w\\.]+(\\s*\\[\\s*\\])?)\\s+(\\w+)\\s*\\((.*)\\)$", 32);
    private static final Pattern FUNCTION_PARAMETER_PATTERN = Pattern.compile("^([\\w\\.]+)(\\s*\\[\\s*\\])?$");

    private TaglibMethodUtil() {
    }

    static Method getMethodByFunctionSignature(Class clazz, String signature) throws SecurityException, NoSuchMethodException, ClassNotFoundException {
        Matcher m1 = FUNCTION_SIGNATURE_PATTERN.matcher(signature);
        if (!m1.matches()) {
            throw new IllegalArgumentException("Invalid function signature (doesn't match this pattern: " + FUNCTION_SIGNATURE_PATTERN + ")");
        }
        String methodName = m1.group(3);
        String params = m1.group(4).trim();
        Class[] paramTypes = null;
        if ("".equals(params)) {
            paramTypes = new Class[]{};
        } else {
            String[] paramsArray = StringUtil.split(params, ',');
            paramTypes = new Class[paramsArray.length];
            String token = null;
            String paramType = null;
            boolean isPrimitive = false;
            boolean isArrayType = false;
            Matcher m2 = null;
            for (int i = 0; i < paramsArray.length; ++i) {
                token = paramsArray[i].trim();
                m2 = FUNCTION_PARAMETER_PATTERN.matcher(token);
                if (!m2.matches()) {
                    throw new IllegalArgumentException("Invalid argument signature (doesn't match pattern " + FUNCTION_PARAMETER_PATTERN + "): " + token);
                }
                paramType = m2.group(1);
                isPrimitive = paramType.indexOf(46) == -1;
                boolean bl = isArrayType = m2.group(2) != null;
                if (isPrimitive) {
                    if ("byte".equals(paramType)) {
                        paramTypes[i] = isArrayType ? byte[].class : Byte.TYPE;
                        continue;
                    }
                    if ("short".equals(paramType)) {
                        paramTypes[i] = isArrayType ? short[].class : Short.TYPE;
                        continue;
                    }
                    if ("int".equals(paramType)) {
                        paramTypes[i] = isArrayType ? int[].class : Integer.TYPE;
                        continue;
                    }
                    if ("long".equals(paramType)) {
                        paramTypes[i] = isArrayType ? long[].class : Long.TYPE;
                        continue;
                    }
                    if ("float".equals(paramType)) {
                        paramTypes[i] = isArrayType ? float[].class : Float.TYPE;
                        continue;
                    }
                    if ("double".equals(paramType)) {
                        paramTypes[i] = isArrayType ? double[].class : Double.TYPE;
                        continue;
                    }
                    if ("boolean".equals(paramType)) {
                        paramTypes[i] = isArrayType ? boolean[].class : Boolean.TYPE;
                        continue;
                    }
                    if ("char".equals(paramType)) {
                        paramTypes[i] = isArrayType ? char[].class : Character.TYPE;
                        continue;
                    }
                    throw new IllegalArgumentException("Invalid primitive type: '" + paramType + "'.");
                }
                paramTypes[i] = isArrayType ? ClassUtil.forName("[L" + paramType + ";") : ClassUtil.forName(paramType);
            }
        }
        return clazz.getMethod(methodName, paramTypes);
    }
}

