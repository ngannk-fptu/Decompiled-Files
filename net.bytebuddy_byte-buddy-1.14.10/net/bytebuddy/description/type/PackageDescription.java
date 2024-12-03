/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.type;

import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface PackageDescription
extends NamedElement.WithRuntimeName,
AnnotationSource {
    public static final PackageDescription DEFAULT = new Simple("");
    public static final String PACKAGE_CLASS_NAME = "package-info";
    public static final int PACKAGE_MODIFIERS = 5632;
    @AlwaysNull
    public static final PackageDescription UNDEFINED = null;

    public boolean contains(TypeDescription var1);

    public boolean isDefault();

    public static class ForLoadedPackage
    extends AbstractBase {
        private final Package aPackage;

        public ForLoadedPackage(Package aPackage) {
            this.aPackage = aPackage;
        }

        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.ForLoadedAnnotations(this.aPackage.getDeclaredAnnotations());
        }

        public String getName() {
            return this.aPackage.getName();
        }
    }

    public static class Simple
    extends AbstractBase {
        private final String name;

        public Simple(String name) {
            this.name = name;
        }

        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Empty();
        }

        public String getName() {
            return this.name;
        }
    }

    public static abstract class AbstractBase
    implements PackageDescription {
        public String getInternalName() {
            return this.getName().replace('.', '/');
        }

        public String getActualName() {
            return this.getName();
        }

        public boolean contains(TypeDescription typeDescription) {
            return this.equals(typeDescription.getPackage());
        }

        public boolean isDefault() {
            return this.getName().equals("");
        }

        public int hashCode() {
            return this.getName().hashCode();
        }

        public boolean equals(@MaybeNull Object other) {
            return this == other || other instanceof PackageDescription && this.getName().equals(((PackageDescription)other).getName());
        }

        public String toString() {
            return "package " + this.getName();
        }
    }
}

