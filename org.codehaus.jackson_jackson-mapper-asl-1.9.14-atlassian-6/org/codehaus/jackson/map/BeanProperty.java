/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map;

import java.lang.annotation.Annotation;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.util.Annotations;
import org.codehaus.jackson.map.util.Named;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BeanProperty
extends Named {
    @Override
    public String getName();

    public JavaType getType();

    public <A extends Annotation> A getAnnotation(Class<A> var1);

    public <A extends Annotation> A getContextAnnotation(Class<A> var1);

    public AnnotatedMember getMember();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Std
    implements BeanProperty {
        protected final String _name;
        protected final JavaType _type;
        protected final AnnotatedMember _member;
        protected final Annotations _contextAnnotations;

        public Std(String name, JavaType type, Annotations contextAnnotations, AnnotatedMember member) {
            this._name = name;
            this._type = type;
            this._member = member;
            this._contextAnnotations = contextAnnotations;
        }

        public Std withType(JavaType type) {
            return new Std(this._name, type, this._contextAnnotations, this._member);
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> acls) {
            return this._member.getAnnotation(acls);
        }

        @Override
        public <A extends Annotation> A getContextAnnotation(Class<A> acls) {
            return this._contextAnnotations == null ? null : (A)this._contextAnnotations.get(acls);
        }

        @Override
        public String getName() {
            return this._name;
        }

        @Override
        public JavaType getType() {
            return this._type;
        }

        @Override
        public AnnotatedMember getMember() {
            return this._member;
        }
    }
}

