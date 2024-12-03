/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface FieldLocator {
    public Resolution locate(String var1);

    public Resolution locate(String var1, TypeDescription var2);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForTopLevelType
    extends AbstractBase {
        protected ForTopLevelType(TypeDescription typeDescription) {
            super(typeDescription);
        }

        @Override
        protected FieldList<?> locate(ElementMatcher<? super FieldDescription> matcher) {
            return (FieldList)this.accessingType.getDeclaredFields().filter(matcher);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Factory implements net.bytebuddy.dynamic.scaffold.FieldLocator$Factory
        {
            INSTANCE;


            @Override
            public FieldLocator make(TypeDescription typeDescription) {
                return new ForTopLevelType(typeDescription);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForClassHierarchy
    extends AbstractBase {
        private final TypeDescription typeDescription;

        public ForClassHierarchy(TypeDescription typeDescription) {
            this(typeDescription, typeDescription);
        }

        public ForClassHierarchy(TypeDescription typeDescription, TypeDescription accessingType) {
            super(accessingType);
            this.typeDescription = typeDescription;
        }

        @Override
        protected FieldList<?> locate(ElementMatcher<? super FieldDescription> matcher) {
            for (TypeDefinition typeDefinition : this.typeDescription) {
                FieldList candidates = (FieldList)typeDefinition.getDeclaredFields().filter(matcher);
                if (candidates.isEmpty()) continue;
                return candidates;
            }
            return new FieldList.Empty();
        }

        @Override
        public boolean equals(@MaybeNull Object object) {
            if (!super.equals(object)) {
                return false;
            }
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.typeDescription.equals(((ForClassHierarchy)object).typeDescription);
        }

        @Override
        public int hashCode() {
            return super.hashCode() * 31 + this.typeDescription.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Factory implements net.bytebuddy.dynamic.scaffold.FieldLocator$Factory
        {
            INSTANCE;


            @Override
            public FieldLocator make(TypeDescription typeDescription) {
                return new ForClassHierarchy(typeDescription);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForExactType
    extends AbstractBase {
        private final TypeDescription typeDescription;

        public ForExactType(TypeDescription typeDescription) {
            this(typeDescription, typeDescription);
        }

        public ForExactType(TypeDescription typeDescription, TypeDescription accessingType) {
            super(accessingType);
            this.typeDescription = typeDescription;
        }

        @Override
        protected FieldList<?> locate(ElementMatcher<? super FieldDescription> matcher) {
            return (FieldList)this.typeDescription.getDeclaredFields().filter(matcher);
        }

        @Override
        public boolean equals(@MaybeNull Object object) {
            if (!super.equals(object)) {
                return false;
            }
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.typeDescription.equals(((ForExactType)object).typeDescription);
        }

        @Override
        public int hashCode() {
            return super.hashCode() * 31 + this.typeDescription.hashCode();
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Factory
        implements net.bytebuddy.dynamic.scaffold.FieldLocator$Factory {
            private final TypeDescription typeDescription;

            public Factory(TypeDescription typeDescription) {
                this.typeDescription = typeDescription;
            }

            public FieldLocator make(TypeDescription typeDescription) {
                return new ForExactType(this.typeDescription, typeDescription);
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return this.typeDescription.equals(((Factory)object).typeDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static abstract class AbstractBase
    implements FieldLocator {
        protected final TypeDescription accessingType;

        protected AbstractBase(TypeDescription accessingType) {
            this.accessingType = accessingType;
        }

        @Override
        public Resolution locate(String name) {
            FieldList<?> candidates = this.locate(ElementMatchers.named(name).and(ElementMatchers.isVisibleTo(this.accessingType)));
            return candidates.size() == 1 ? new Resolution.Simple((FieldDescription)candidates.getOnly()) : Resolution.Illegal.INSTANCE;
        }

        @Override
        public Resolution locate(String name, TypeDescription type) {
            FieldList<?> candidates = this.locate(ElementMatchers.named(name).and(ElementMatchers.fieldType(type)).and(ElementMatchers.isVisibleTo(this.accessingType)));
            return candidates.size() == 1 ? new Resolution.Simple((FieldDescription)candidates.getOnly()) : Resolution.Illegal.INSTANCE;
        }

        protected abstract FieldList<?> locate(ElementMatcher<? super FieldDescription> var1);

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.accessingType.equals(((AbstractBase)object).accessingType);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.accessingType.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NoOp implements FieldLocator,
    Factory
    {
        INSTANCE;


        @Override
        public FieldLocator make(TypeDescription typeDescription) {
            return this;
        }

        @Override
        public Resolution locate(String name) {
            return Resolution.Illegal.INSTANCE;
        }

        @Override
        public Resolution locate(String name, TypeDescription type) {
            return Resolution.Illegal.INSTANCE;
        }
    }

    public static interface Factory {
        public FieldLocator make(TypeDescription var1);
    }

    public static interface Resolution {
        public boolean isResolved();

        public FieldDescription getField();

        @HashCodeAndEqualsPlugin.Enhance
        public static class Simple
        implements Resolution {
            private final FieldDescription fieldDescription;

            protected Simple(FieldDescription fieldDescription) {
                this.fieldDescription = fieldDescription;
            }

            public static Resolution ofBeanAccessor(FieldLocator fieldLocator, MethodDescription methodDescription) {
                String name;
                if (ElementMatchers.isSetter().matches(methodDescription)) {
                    name = methodDescription.getInternalName().substring(3);
                } else if (ElementMatchers.isGetter().matches(methodDescription)) {
                    name = methodDescription.getInternalName().substring(methodDescription.getInternalName().startsWith("is") ? 2 : 3);
                } else {
                    return Illegal.INSTANCE;
                }
                Resolution resolution = fieldLocator.locate(Character.toLowerCase(name.charAt(0)) + name.substring(1));
                return resolution.isResolved() ? resolution : fieldLocator.locate(Character.toUpperCase(name.charAt(0)) + name.substring(1));
            }

            public boolean isResolved() {
                return true;
            }

            public FieldDescription getField() {
                return this.fieldDescription;
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return this.fieldDescription.equals(((Simple)object).fieldDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Illegal implements Resolution
        {
            INSTANCE;


            @Override
            public boolean isResolved() {
                return false;
            }

            @Override
            public FieldDescription getField() {
                throw new IllegalStateException("Could not locate field");
            }
        }
    }
}

