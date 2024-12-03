/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class MethodSortMatcher<T extends MethodDescription>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final Sort sort;

    public static <T extends MethodDescription> ElementMatcher.Junction<T> of(Sort sort) {
        return sort.getMatcher();
    }

    public MethodSortMatcher(Sort sort) {
        this.sort = sort;
    }

    @Override
    protected boolean doMatch(T target) {
        return this.sort.isSort((MethodDescription)target);
    }

    public String toString() {
        return this.sort.getDescription();
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
        return this.sort.equals((Object)((MethodSortMatcher)object).sort);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.sort.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Sort {
        METHOD("isMethod()"){

            protected boolean isSort(MethodDescription target) {
                return target.isMethod();
            }
        }
        ,
        CONSTRUCTOR("isConstructor()"){

            protected boolean isSort(MethodDescription target) {
                return target.isConstructor();
            }
        }
        ,
        TYPE_INITIALIZER("isTypeInitializer()"){

            protected boolean isSort(MethodDescription target) {
                return target.isTypeInitializer();
            }
        }
        ,
        VIRTUAL("isVirtual()"){

            protected boolean isSort(MethodDescription target) {
                return target.isVirtual();
            }
        }
        ,
        DEFAULT_METHOD("isDefaultMethod()"){

            protected boolean isSort(MethodDescription target) {
                return target.isDefaultMethod();
            }
        };

        private final String description;
        private final MethodSortMatcher<?> matcher;

        private Sort(String description) {
            this.description = description;
            this.matcher = new MethodSortMatcher(this);
        }

        protected abstract boolean isSort(MethodDescription var1);

        protected String getDescription() {
            return this.description;
        }

        protected MethodSortMatcher<?> getMatcher() {
            return this.matcher;
        }
    }
}

