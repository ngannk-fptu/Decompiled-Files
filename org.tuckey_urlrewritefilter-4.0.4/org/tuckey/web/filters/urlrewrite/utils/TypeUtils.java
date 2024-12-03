/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite.utils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TypeUtils {
    public static Class findClass(String param) {
        Class<Object> paramClass = null;
        if ("boolean".equals(param) || "bool".equals(param) || "z".equalsIgnoreCase(param)) {
            paramClass = Boolean.TYPE;
        }
        if ("byte".equals(param) || "b".equalsIgnoreCase(param)) {
            paramClass = Byte.TYPE;
        }
        if ("char".equals(param) || "c".equalsIgnoreCase(param)) {
            paramClass = Character.TYPE;
        }
        if ("short".equals(param) || "s".equalsIgnoreCase(param)) {
            paramClass = Short.TYPE;
        }
        if ("int".equals(param) || "i".equalsIgnoreCase(param)) {
            paramClass = Integer.TYPE;
        }
        if ("long".equals(param) || "l".equalsIgnoreCase(param)) {
            paramClass = Long.TYPE;
        }
        if ("float".equals(param) || "f".equalsIgnoreCase(param)) {
            paramClass = Float.TYPE;
        }
        if ("double".equals(param) || "d".equalsIgnoreCase(param)) {
            paramClass = Double.TYPE;
        }
        if ("Boolean".equals(param) || "Bool".equals(param)) {
            paramClass = Boolean.class;
        }
        if ("Byte".equals(param)) {
            paramClass = Byte.class;
        }
        if ("Character".equalsIgnoreCase(param) || "C".equals(param)) {
            paramClass = Character.class;
        }
        if ("Short".equals(param)) {
            paramClass = Short.class;
        }
        if ("Integer".equals(param)) {
            paramClass = Integer.class;
        }
        if ("Long".equals(param)) {
            paramClass = Long.class;
        }
        if ("Float".equals(param)) {
            paramClass = Float.class;
        }
        if ("Double".equals(param)) {
            paramClass = Double.class;
        }
        if ("Class".equalsIgnoreCase(param)) {
            paramClass = Class.class;
        }
        if ("Number".equalsIgnoreCase(param)) {
            paramClass = Number.class;
        }
        if ("Object".equalsIgnoreCase(param)) {
            paramClass = Object.class;
        }
        if ("String".equalsIgnoreCase(param) || "str".equalsIgnoreCase(param)) {
            paramClass = String.class;
        }
        if ("HttpServletRequest".equalsIgnoreCase(param) || "req".equalsIgnoreCase(param) || "request".equalsIgnoreCase(param)) {
            paramClass = HttpServletRequest.class;
        }
        if ("HttpServletResponse".equalsIgnoreCase(param) || "res".equalsIgnoreCase(param) || "response".equalsIgnoreCase(param)) {
            paramClass = HttpServletResponse.class;
        }
        if ("ServletRequest".equalsIgnoreCase(param)) {
            paramClass = ServletRequest.class;
        }
        if ("ServletResponse".equalsIgnoreCase(param)) {
            paramClass = ServletResponse.class;
        }
        return paramClass;
    }

    public static Object getConvertedParam(Class runMethodParam, Object matchObj) {
        Object param = null;
        if (matchObj == null) {
            if (runMethodParam.isPrimitive()) {
                if (runMethodParam.equals(Boolean.TYPE)) {
                    param = Boolean.FALSE;
                } else if (runMethodParam.equals(Character.TYPE)) {
                    param = new Character('\u0000');
                } else if (runMethodParam.equals(Byte.TYPE)) {
                    param = new Byte(0);
                } else if (runMethodParam.equals(Short.TYPE)) {
                    param = new Short(0);
                } else if (runMethodParam.equals(Integer.TYPE)) {
                    param = new Integer(0);
                } else if (runMethodParam.equals(Long.TYPE)) {
                    param = new Long(0L);
                } else if (runMethodParam.equals(Float.TYPE)) {
                    param = new Float(0.0f);
                } else if (runMethodParam.equals(Double.TYPE)) {
                    param = new Double(0.0);
                }
            }
        } else if (runMethodParam.equals(Boolean.class) || runMethodParam.equals(Boolean.TYPE)) {
            param = Boolean.valueOf((String)matchObj);
        } else if (runMethodParam.equals(Character.class) || runMethodParam.equals(Character.TYPE)) {
            param = new Character(((String)matchObj).charAt(0));
        } else if (runMethodParam.equals(Byte.class) || runMethodParam.equals(Byte.TYPE)) {
            param = Byte.valueOf((String)matchObj);
        } else if (runMethodParam.equals(Short.class) || runMethodParam.equals(Short.TYPE)) {
            param = Short.valueOf((String)matchObj);
        } else if (runMethodParam.equals(Integer.class) || runMethodParam.equals(Integer.TYPE)) {
            param = Integer.valueOf((String)matchObj);
        } else if (runMethodParam.equals(Long.class) || runMethodParam.equals(Long.TYPE)) {
            param = Long.valueOf((String)matchObj);
        } else if (runMethodParam.equals(Float.class) || runMethodParam.equals(Float.TYPE)) {
            param = Float.valueOf((String)matchObj);
        } else if (runMethodParam.equals(Double.class) || runMethodParam.equals(Double.TYPE)) {
            param = Double.valueOf((String)matchObj);
        } else if (matchObj instanceof Throwable && runMethodParam.isAssignableFrom(matchObj.getClass())) {
            param = matchObj;
        } else {
            try {
                param = runMethodParam.cast(matchObj);
            }
            catch (ClassCastException classCastException) {
                // empty catch block
            }
        }
        return param;
    }

    public static String getMethodSignature(String methodStr, Class[] methodParams) {
        if (methodStr == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer(methodStr);
        if (methodParams != null) {
            for (int i = 0; i < methodParams.length; ++i) {
                Class runMethodParam = methodParams[i];
                if (runMethodParam == null) continue;
                if (i == 0) {
                    sb.append("(");
                }
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(runMethodParam.getName());
                if (i + 1 != methodParams.length) continue;
                sb.append(")");
            }
        }
        return sb.toString();
    }
}

