/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.util.Util;

public class BinaryTypeFormatter {
    public static String annotationToString(IBinaryAnnotation annotation) {
        StringBuffer buffer = new StringBuffer();
        buffer.append('@');
        buffer.append(annotation.getTypeName());
        IBinaryElementValuePair[] valuePairs = annotation.getElementValuePairs();
        if (valuePairs != null) {
            buffer.append('(');
            buffer.append("\n\t");
            int i = 0;
            int len = valuePairs.length;
            while (i < len) {
                if (i > 0) {
                    buffer.append(",\n\t");
                }
                buffer.append(valuePairs[i]);
                ++i;
            }
            buffer.append(')');
        }
        return buffer.toString();
    }

    public static String annotationToString(IBinaryTypeAnnotation typeAnnotation) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(typeAnnotation.getAnnotation());
        buffer.append(' ');
        buffer.append("target_type=").append(typeAnnotation.getTargetType());
        buffer.append(", info=").append(typeAnnotation.getSupertypeIndex());
        buffer.append(", info2=").append(typeAnnotation.getBoundIndex());
        int[] theTypePath = typeAnnotation.getTypePath();
        if (theTypePath != null && theTypePath.length != 0) {
            buffer.append(", location=[");
            int i = 0;
            int max = theTypePath.length;
            while (i < max) {
                if (i > 0) {
                    buffer.append(", ");
                }
                switch (theTypePath[i]) {
                    case 0: {
                        buffer.append("ARRAY");
                        break;
                    }
                    case 1: {
                        buffer.append("INNER_TYPE");
                        break;
                    }
                    case 2: {
                        buffer.append("WILDCARD");
                        break;
                    }
                    case 3: {
                        buffer.append("TYPE_ARGUMENT(").append(theTypePath[i + 1]).append(')');
                    }
                }
                i += 2;
            }
            buffer.append(']');
        }
        return buffer.toString();
    }

    public static String methodToString(IBinaryMethod method) {
        StringBuffer result = new StringBuffer();
        BinaryTypeFormatter.methodToStringContent(result, method);
        return result.toString();
    }

    public static void methodToStringContent(StringBuffer buffer, IBinaryMethod method) {
        int i;
        int modifiers = method.getModifiers();
        char[] desc = method.getGenericSignature();
        if (desc == null) {
            desc = method.getMethodDescriptor();
        }
        buffer.append('{').append(String.valueOf((modifiers & 0x100000) != 0 ? "deprecated " : Util.EMPTY_STRING) + ((modifiers & 1) == 1 ? "public " : Util.EMPTY_STRING) + ((modifiers & 2) == 2 ? "private " : Util.EMPTY_STRING) + ((modifiers & 4) == 4 ? "protected " : Util.EMPTY_STRING) + ((modifiers & 8) == 8 ? "static " : Util.EMPTY_STRING) + ((modifiers & 0x10) == 16 ? "final " : Util.EMPTY_STRING) + ((modifiers & 0x40) == 64 ? "bridge " : Util.EMPTY_STRING) + ((modifiers & 0x80) == 128 ? "varargs " : Util.EMPTY_STRING)).append(method.getSelector()).append(desc).append('}');
        Object defaultValue = method.getDefaultValue();
        if (defaultValue != null) {
            buffer.append(" default ");
            if (defaultValue instanceof Object[]) {
                buffer.append('{');
                Object[] elements = (Object[])defaultValue;
                i = 0;
                int len = elements.length;
                while (i < len) {
                    if (i > 0) {
                        buffer.append(", ");
                    }
                    buffer.append(elements[i]);
                    ++i;
                }
                buffer.append('}');
            } else {
                buffer.append(defaultValue);
            }
            buffer.append('\n');
        }
        IBinaryAnnotation[] annotations = method.getAnnotations();
        i = 0;
        int l = annotations == null ? 0 : annotations.length;
        while (i < l) {
            buffer.append(annotations[i]);
            buffer.append('\n');
            ++i;
        }
        int annotatedParameterCount = method.getAnnotatedParametersCount();
        int i2 = 0;
        while (i2 < annotatedParameterCount) {
            buffer.append("param" + (i2 - 1));
            buffer.append('\n');
            IBinaryAnnotation[] infos = method.getParameterAnnotations(i2, new char[0]);
            int j = 0;
            int k = infos == null ? 0 : infos.length;
            while (j < k) {
                buffer.append(infos[j]);
                buffer.append('\n');
                ++j;
            }
            ++i2;
        }
    }
}

