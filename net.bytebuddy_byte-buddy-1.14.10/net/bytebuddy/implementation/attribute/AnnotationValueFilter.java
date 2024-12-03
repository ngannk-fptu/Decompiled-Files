/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.attribute;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.TypeDescription;

public interface AnnotationValueFilter {
    public boolean isRelevant(AnnotationDescription var1, MethodDescription.InDefinedShape var2);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Default implements AnnotationValueFilter,
    Factory
    {
        SKIP_DEFAULTS{

            public boolean isRelevant(AnnotationDescription annotationDescription, MethodDescription.InDefinedShape methodDescription) {
                AnnotationValue<?, ?> defaultValue = methodDescription.getDefaultValue();
                return defaultValue == null || !defaultValue.equals(annotationDescription.getValue(methodDescription));
            }
        }
        ,
        APPEND_DEFAULTS{

            public boolean isRelevant(AnnotationDescription annotationDescription, MethodDescription.InDefinedShape methodDescription) {
                return true;
            }
        };


        @Override
        public AnnotationValueFilter on(TypeDescription instrumentedType) {
            return this;
        }

        @Override
        public AnnotationValueFilter on(FieldDescription fieldDescription) {
            return this;
        }

        @Override
        public AnnotationValueFilter on(MethodDescription methodDescription) {
            return this;
        }

        @Override
        public AnnotationValueFilter on(RecordComponentDescription recordComponentDescription) {
            return this;
        }
    }

    public static interface Factory {
        public AnnotationValueFilter on(TypeDescription var1);

        public AnnotationValueFilter on(FieldDescription var1);

        public AnnotationValueFilter on(MethodDescription var1);

        public AnnotationValueFilter on(RecordComponentDescription var1);
    }
}

