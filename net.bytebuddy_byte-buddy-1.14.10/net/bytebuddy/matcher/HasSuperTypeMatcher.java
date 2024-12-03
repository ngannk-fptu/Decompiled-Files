/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.util.HashSet;
import java.util.Queue;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.QueueFactory;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class HasSuperTypeMatcher<T extends TypeDescription>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final ElementMatcher<? super TypeDescription.Generic> matcher;

    public HasSuperTypeMatcher(ElementMatcher<? super TypeDescription.Generic> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean doMatch(T target) {
        HashSet<TypeDescription> previous = new HashSet<TypeDescription>();
        for (TypeDefinition typeDefinition : target) {
            if (!previous.add(typeDefinition.asErasure())) {
                return false;
            }
            if (this.matcher.matches(typeDefinition.asGenericType())) {
                return true;
            }
            Queue<TypeDescription.Generic> interfaceTypes = QueueFactory.make(typeDefinition.getInterfaces());
            while (!interfaceTypes.isEmpty()) {
                TypeDefinition interfaceType = interfaceTypes.remove();
                if (!previous.add(interfaceType.asErasure())) continue;
                if (this.matcher.matches(interfaceType.asGenericType())) {
                    return true;
                }
                interfaceTypes.addAll(interfaceType.getInterfaces());
            }
        }
        return false;
    }

    public String toString() {
        return "hasSuperType(" + this.matcher + ")";
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
        return this.matcher.equals(((HasSuperTypeMatcher)object).matcher);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.matcher.hashCode();
    }
}

