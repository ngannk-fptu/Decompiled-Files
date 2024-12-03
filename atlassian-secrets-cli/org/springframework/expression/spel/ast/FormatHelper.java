/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.ast;

import java.util.List;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class FormatHelper {
    public static String formatMethodForMessage(String name, List<TypeDescriptor> argumentTypes) {
        StringBuilder sb = new StringBuilder(name);
        sb.append("(");
        for (int i = 0; i < argumentTypes.size(); ++i) {
            TypeDescriptor typeDescriptor;
            if (i > 0) {
                sb.append(",");
            }
            if ((typeDescriptor = argumentTypes.get(i)) != null) {
                sb.append(FormatHelper.formatClassNameForMessage(typeDescriptor.getType()));
                continue;
            }
            sb.append(FormatHelper.formatClassNameForMessage(null));
        }
        sb.append(")");
        return sb.toString();
    }

    public static String formatClassNameForMessage(@Nullable Class<?> clazz) {
        return clazz != null ? ClassUtils.getQualifiedName(clazz) : "null";
    }
}

