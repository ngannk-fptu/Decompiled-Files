/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description;

import net.bytebuddy.description.modifier.EnumerationState;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.FieldPersistence;
import net.bytebuddy.description.modifier.MethodManifestation;
import net.bytebuddy.description.modifier.MethodStrictness;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.ParameterManifestation;
import net.bytebuddy.description.modifier.ProvisioningState;
import net.bytebuddy.description.modifier.SynchronizationState;
import net.bytebuddy.description.modifier.SyntheticState;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;

public interface ModifierReviewable {
    public static final int EMPTY_MASK = 0;

    public int getModifiers();

    public boolean isFinal();

    public boolean isSynthetic();

    public SyntheticState getSyntheticState();

    public static abstract class AbstractBase
    implements ForTypeDefinition,
    ForFieldDescription,
    ForMethodDescription,
    ForParameterDescription {
        public boolean isAbstract() {
            return this.matchesMask(1024);
        }

        public boolean isFinal() {
            return this.matchesMask(16);
        }

        public boolean isStatic() {
            return this.matchesMask(8);
        }

        public boolean isPublic() {
            return this.matchesMask(1);
        }

        public boolean isProtected() {
            return this.matchesMask(4);
        }

        public boolean isPackagePrivate() {
            return !this.isPublic() && !this.isProtected() && !this.isPrivate();
        }

        public boolean isPrivate() {
            return this.matchesMask(2);
        }

        public boolean isNative() {
            return this.matchesMask(256);
        }

        public boolean isSynchronized() {
            return this.matchesMask(32);
        }

        public boolean isStrict() {
            return this.matchesMask(2048);
        }

        public boolean isMandated() {
            return this.matchesMask(32768);
        }

        public boolean isSynthetic() {
            return this.matchesMask(4096);
        }

        public boolean isBridge() {
            return this.matchesMask(64);
        }

        public boolean isDeprecated() {
            return this.matchesMask(131072);
        }

        public boolean isAnnotation() {
            return this.matchesMask(8192);
        }

        public boolean isEnum() {
            return this.matchesMask(16384);
        }

        public boolean isInterface() {
            return this.matchesMask(512);
        }

        public boolean isTransient() {
            return this.matchesMask(128);
        }

        public boolean isVolatile() {
            return this.matchesMask(64);
        }

        public boolean isVarArgs() {
            return this.matchesMask(128);
        }

        public SyntheticState getSyntheticState() {
            return this.isSynthetic() ? SyntheticState.SYNTHETIC : SyntheticState.PLAIN;
        }

        public Visibility getVisibility() {
            int modifiers = this.getModifiers();
            switch (modifiers & 7) {
                case 1: {
                    return Visibility.PUBLIC;
                }
                case 4: {
                    return Visibility.PROTECTED;
                }
                case 0: {
                    return Visibility.PACKAGE_PRIVATE;
                }
                case 2: {
                    return Visibility.PRIVATE;
                }
            }
            throw new IllegalStateException("Unexpected modifiers: " + modifiers);
        }

        public Ownership getOwnership() {
            return this.isStatic() ? Ownership.STATIC : Ownership.MEMBER;
        }

        public EnumerationState getEnumerationState() {
            return this.isEnum() ? EnumerationState.ENUMERATION : EnumerationState.PLAIN;
        }

        public TypeManifestation getTypeManifestation() {
            int modifiers = this.getModifiers();
            switch (modifiers & 0x2610) {
                case 16: {
                    return TypeManifestation.FINAL;
                }
                case 1024: {
                    return TypeManifestation.ABSTRACT;
                }
                case 1536: {
                    return TypeManifestation.INTERFACE;
                }
                case 9728: {
                    return TypeManifestation.ANNOTATION;
                }
                case 0: {
                    return TypeManifestation.PLAIN;
                }
            }
            throw new IllegalStateException("Unexpected modifiers: " + modifiers);
        }

        public FieldManifestation getFieldManifestation() {
            int modifiers = this.getModifiers();
            switch (modifiers & 0x50) {
                case 16: {
                    return FieldManifestation.FINAL;
                }
                case 64: {
                    return FieldManifestation.VOLATILE;
                }
                case 0: {
                    return FieldManifestation.PLAIN;
                }
            }
            throw new IllegalStateException("Unexpected modifiers: " + modifiers);
        }

        public FieldPersistence getFieldPersistence() {
            int modifiers = this.getModifiers();
            switch (modifiers & 0x80) {
                case 128: {
                    return FieldPersistence.TRANSIENT;
                }
                case 0: {
                    return FieldPersistence.PLAIN;
                }
            }
            throw new IllegalStateException("Unexpected modifiers: " + modifiers);
        }

        public SynchronizationState getSynchronizationState() {
            return this.isSynchronized() ? SynchronizationState.SYNCHRONIZED : SynchronizationState.PLAIN;
        }

        public MethodManifestation getMethodManifestation() {
            int modifiers = this.getModifiers();
            switch (modifiers & 0x550) {
                case 272: {
                    return MethodManifestation.FINAL_NATIVE;
                }
                case 256: {
                    return MethodManifestation.NATIVE;
                }
                case 16: {
                    return MethodManifestation.FINAL;
                }
                case 64: {
                    return MethodManifestation.BRIDGE;
                }
                case 80: {
                    return MethodManifestation.FINAL_BRIDGE;
                }
                case 1024: {
                    return MethodManifestation.ABSTRACT;
                }
                case 0: {
                    return MethodManifestation.PLAIN;
                }
            }
            throw new IllegalStateException("Unexpected modifiers: " + modifiers);
        }

        public MethodStrictness getMethodStrictness() {
            return this.isStrict() ? MethodStrictness.STRICT : MethodStrictness.PLAIN;
        }

        public ParameterManifestation getParameterManifestation() {
            return this.isFinal() ? ParameterManifestation.FINAL : ParameterManifestation.PLAIN;
        }

        public ProvisioningState getProvisioningState() {
            return this.isMandated() ? ProvisioningState.MANDATED : ProvisioningState.PLAIN;
        }

        private boolean matchesMask(int mask) {
            return (this.getModifiers() & mask) == mask;
        }
    }

    public static interface ForParameterDescription
    extends ModifierReviewable {
        public boolean isMandated();

        public ParameterManifestation getParameterManifestation();

        public ProvisioningState getProvisioningState();
    }

    public static interface ForMethodDescription
    extends OfAbstraction {
        public boolean isSynchronized();

        public boolean isVarArgs();

        public boolean isNative();

        public boolean isBridge();

        public boolean isStrict();

        public SynchronizationState getSynchronizationState();

        public MethodStrictness getMethodStrictness();

        public MethodManifestation getMethodManifestation();
    }

    public static interface ForFieldDescription
    extends OfEnumeration {
        public boolean isVolatile();

        public boolean isTransient();

        public FieldManifestation getFieldManifestation();

        public FieldPersistence getFieldPersistence();
    }

    public static interface ForTypeDefinition
    extends OfAbstraction,
    OfEnumeration {
        public boolean isInterface();

        public boolean isAnnotation();

        public TypeManifestation getTypeManifestation();
    }

    public static interface OfEnumeration
    extends OfByteCodeElement {
        public boolean isEnum();

        public EnumerationState getEnumerationState();
    }

    public static interface OfAbstraction
    extends OfByteCodeElement {
        public boolean isAbstract();
    }

    public static interface OfByteCodeElement
    extends ModifierReviewable {
        public boolean isPublic();

        public boolean isProtected();

        public boolean isPackagePrivate();

        public boolean isPrivate();

        public boolean isStatic();

        public boolean isDeprecated();

        public Ownership getOwnership();

        public Visibility getVisibility();
    }
}

