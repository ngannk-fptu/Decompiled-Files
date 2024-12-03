/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.jsontype;

import java.util.Collection;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.jsontype.NamedType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class SubtypeResolver {
    public abstract void registerSubtypes(NamedType ... var1);

    public abstract void registerSubtypes(Class<?> ... var1);

    public abstract Collection<NamedType> collectAndResolveSubtypes(AnnotatedMember var1, MapperConfig<?> var2, AnnotationIntrospector var3);

    public abstract Collection<NamedType> collectAndResolveSubtypes(AnnotatedClass var1, MapperConfig<?> var2, AnnotationIntrospector var3);
}

