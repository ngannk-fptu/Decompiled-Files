/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.reflect.Method;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotatedElementUtils;

class BeanAnnotationHelper {
    BeanAnnotationHelper() {
    }

    public static boolean isBeanAnnotated(Method method) {
        return AnnotatedElementUtils.hasAnnotation(method, Bean.class);
    }

    public static String determineBeanNameFor(Method beanMethod) {
        String[] names;
        String beanName = beanMethod.getName();
        Bean bean2 = AnnotatedElementUtils.findMergedAnnotation(beanMethod, Bean.class);
        if (bean2 != null && (names = bean2.name()).length > 0) {
            beanName = names[0];
        }
        return beanName;
    }
}

