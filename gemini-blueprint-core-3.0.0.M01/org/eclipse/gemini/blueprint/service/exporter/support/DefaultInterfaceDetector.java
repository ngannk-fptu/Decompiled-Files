/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.exporter.support;

import org.eclipse.gemini.blueprint.service.exporter.support.InterfaceDetector;
import org.eclipse.gemini.blueprint.util.internal.ClassUtils;

public enum DefaultInterfaceDetector implements InterfaceDetector
{
    DISABLED{
        private final Class<?>[] clazz = new Class[0];

        @Override
        public Class<?>[] detect(Class<?> targetClass) {
            return this.clazz;
        }
    }
    ,
    INTERFACES{

        @Override
        public Class<?>[] detect(Class<?> targetClass) {
            return ClassUtils.getClassHierarchy(targetClass, ClassUtils.ClassSet.INTERFACES);
        }
    }
    ,
    CLASS_HIERARCHY{

        @Override
        public Class<?>[] detect(Class<?> targetClass) {
            return ClassUtils.getClassHierarchy(targetClass, ClassUtils.ClassSet.CLASS_HIERARCHY);
        }
    }
    ,
    ALL_CLASSES{

        @Override
        public Class<?>[] detect(Class<?> targetClass) {
            return ClassUtils.getClassHierarchy(targetClass, ClassUtils.ClassSet.ALL_CLASSES);
        }
    };

}

