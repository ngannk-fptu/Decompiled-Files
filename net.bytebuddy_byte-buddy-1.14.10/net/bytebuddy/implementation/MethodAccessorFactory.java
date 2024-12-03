/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.Implementation;

public interface MethodAccessorFactory {
    public MethodDescription.InDefinedShape registerAccessorFor(Implementation.SpecialMethodInvocation var1, AccessType var2);

    public MethodDescription.InDefinedShape registerGetterFor(FieldDescription var1, AccessType var2);

    public MethodDescription.InDefinedShape registerSetterFor(FieldDescription var1, AccessType var2);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Illegal implements MethodAccessorFactory
    {
        INSTANCE;


        @Override
        public MethodDescription.InDefinedShape registerAccessorFor(Implementation.SpecialMethodInvocation specialMethodInvocation, AccessType accessType) {
            throw new IllegalStateException("It is illegal to register an accessor for this type");
        }

        @Override
        public MethodDescription.InDefinedShape registerGetterFor(FieldDescription fieldDescription, AccessType accessType) {
            throw new IllegalStateException("It is illegal to register a field getter for this type");
        }

        @Override
        public MethodDescription.InDefinedShape registerSetterFor(FieldDescription fieldDescription, AccessType accessType) {
            throw new IllegalStateException("It is illegal to register a field setter for this type");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum AccessType {
        PUBLIC(Visibility.PUBLIC),
        DEFAULT(Visibility.PACKAGE_PRIVATE);

        private final Visibility visibility;

        private AccessType(Visibility visibility) {
            this.visibility = visibility;
        }

        public Visibility getVisibility() {
            return this.visibility;
        }
    }
}

