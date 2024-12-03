/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface VisibilityChecker<T extends VisibilityChecker<T>> {
    public T with(JsonAutoDetect var1);

    public T with(JsonAutoDetect.Visibility var1);

    public T withVisibility(JsonMethod var1, JsonAutoDetect.Visibility var2);

    public T withGetterVisibility(JsonAutoDetect.Visibility var1);

    public T withIsGetterVisibility(JsonAutoDetect.Visibility var1);

    public T withSetterVisibility(JsonAutoDetect.Visibility var1);

    public T withCreatorVisibility(JsonAutoDetect.Visibility var1);

    public T withFieldVisibility(JsonAutoDetect.Visibility var1);

    public boolean isGetterVisible(Method var1);

    public boolean isGetterVisible(AnnotatedMethod var1);

    public boolean isIsGetterVisible(Method var1);

    public boolean isIsGetterVisible(AnnotatedMethod var1);

    public boolean isSetterVisible(Method var1);

    public boolean isSetterVisible(AnnotatedMethod var1);

    public boolean isCreatorVisible(Member var1);

    public boolean isCreatorVisible(AnnotatedMember var1);

    public boolean isFieldVisible(Field var1);

    public boolean isFieldVisible(AnnotatedField var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.PUBLIC_ONLY, isGetterVisibility=JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility=JsonAutoDetect.Visibility.ANY, creatorVisibility=JsonAutoDetect.Visibility.ANY, fieldVisibility=JsonAutoDetect.Visibility.PUBLIC_ONLY)
    public static class Std
    implements VisibilityChecker<Std> {
        protected static final Std DEFAULT = new Std(Std.class.getAnnotation(JsonAutoDetect.class));
        protected final JsonAutoDetect.Visibility _getterMinLevel;
        protected final JsonAutoDetect.Visibility _isGetterMinLevel;
        protected final JsonAutoDetect.Visibility _setterMinLevel;
        protected final JsonAutoDetect.Visibility _creatorMinLevel;
        protected final JsonAutoDetect.Visibility _fieldMinLevel;

        public static Std defaultInstance() {
            return DEFAULT;
        }

        public Std(JsonAutoDetect ann) {
            JsonMethod[] incl = ann.value();
            this._getterMinLevel = Std.hasMethod(incl, JsonMethod.GETTER) ? ann.getterVisibility() : JsonAutoDetect.Visibility.NONE;
            this._isGetterMinLevel = Std.hasMethod(incl, JsonMethod.IS_GETTER) ? ann.isGetterVisibility() : JsonAutoDetect.Visibility.NONE;
            this._setterMinLevel = Std.hasMethod(incl, JsonMethod.SETTER) ? ann.setterVisibility() : JsonAutoDetect.Visibility.NONE;
            this._creatorMinLevel = Std.hasMethod(incl, JsonMethod.CREATOR) ? ann.creatorVisibility() : JsonAutoDetect.Visibility.NONE;
            this._fieldMinLevel = Std.hasMethod(incl, JsonMethod.FIELD) ? ann.fieldVisibility() : JsonAutoDetect.Visibility.NONE;
        }

        public Std(JsonAutoDetect.Visibility getter, JsonAutoDetect.Visibility isGetter, JsonAutoDetect.Visibility setter, JsonAutoDetect.Visibility creator, JsonAutoDetect.Visibility field) {
            this._getterMinLevel = getter;
            this._isGetterMinLevel = isGetter;
            this._setterMinLevel = setter;
            this._creatorMinLevel = creator;
            this._fieldMinLevel = field;
        }

        public Std(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                this._getterMinLevel = Std.DEFAULT._getterMinLevel;
                this._isGetterMinLevel = Std.DEFAULT._isGetterMinLevel;
                this._setterMinLevel = Std.DEFAULT._setterMinLevel;
                this._creatorMinLevel = Std.DEFAULT._creatorMinLevel;
                this._fieldMinLevel = Std.DEFAULT._fieldMinLevel;
            } else {
                this._getterMinLevel = v;
                this._isGetterMinLevel = v;
                this._setterMinLevel = v;
                this._creatorMinLevel = v;
                this._fieldMinLevel = v;
            }
        }

        @Override
        public Std with(JsonAutoDetect ann) {
            if (ann == null) {
                return this;
            }
            Std curr = this;
            JsonMethod[] incl = ann.value();
            JsonAutoDetect.Visibility v = Std.hasMethod(incl, JsonMethod.GETTER) ? ann.getterVisibility() : JsonAutoDetect.Visibility.NONE;
            curr = curr.withGetterVisibility(v);
            v = Std.hasMethod(incl, JsonMethod.IS_GETTER) ? ann.isGetterVisibility() : JsonAutoDetect.Visibility.NONE;
            curr = curr.withIsGetterVisibility(v);
            v = Std.hasMethod(incl, JsonMethod.SETTER) ? ann.setterVisibility() : JsonAutoDetect.Visibility.NONE;
            curr = curr.withSetterVisibility(v);
            v = Std.hasMethod(incl, JsonMethod.CREATOR) ? ann.creatorVisibility() : JsonAutoDetect.Visibility.NONE;
            curr = curr.withCreatorVisibility(v);
            v = Std.hasMethod(incl, JsonMethod.FIELD) ? ann.fieldVisibility() : JsonAutoDetect.Visibility.NONE;
            curr = curr.withFieldVisibility(v);
            return curr;
        }

        @Override
        public Std with(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                return DEFAULT;
            }
            return new Std(v);
        }

        @Override
        public Std withVisibility(JsonMethod method, JsonAutoDetect.Visibility v) {
            switch (method) {
                case GETTER: {
                    return this.withGetterVisibility(v);
                }
                case SETTER: {
                    return this.withSetterVisibility(v);
                }
                case CREATOR: {
                    return this.withCreatorVisibility(v);
                }
                case FIELD: {
                    return this.withFieldVisibility(v);
                }
                case IS_GETTER: {
                    return this.withIsGetterVisibility(v);
                }
                case ALL: {
                    return this.with(v);
                }
            }
            return this;
        }

        @Override
        public Std withGetterVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = Std.DEFAULT._getterMinLevel;
            }
            if (this._getterMinLevel == v) {
                return this;
            }
            return new Std(v, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
        }

        @Override
        public Std withIsGetterVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = Std.DEFAULT._isGetterMinLevel;
            }
            if (this._isGetterMinLevel == v) {
                return this;
            }
            return new Std(this._getterMinLevel, v, this._setterMinLevel, this._creatorMinLevel, this._fieldMinLevel);
        }

        @Override
        public Std withSetterVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = Std.DEFAULT._setterMinLevel;
            }
            if (this._setterMinLevel == v) {
                return this;
            }
            return new Std(this._getterMinLevel, this._isGetterMinLevel, v, this._creatorMinLevel, this._fieldMinLevel);
        }

        @Override
        public Std withCreatorVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = Std.DEFAULT._creatorMinLevel;
            }
            if (this._creatorMinLevel == v) {
                return this;
            }
            return new Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, v, this._fieldMinLevel);
        }

        @Override
        public Std withFieldVisibility(JsonAutoDetect.Visibility v) {
            if (v == JsonAutoDetect.Visibility.DEFAULT) {
                v = Std.DEFAULT._fieldMinLevel;
            }
            if (this._fieldMinLevel == v) {
                return this;
            }
            return new Std(this._getterMinLevel, this._isGetterMinLevel, this._setterMinLevel, this._creatorMinLevel, v);
        }

        @Override
        public boolean isCreatorVisible(Member m) {
            return this._creatorMinLevel.isVisible(m);
        }

        @Override
        public boolean isCreatorVisible(AnnotatedMember m) {
            return this.isCreatorVisible(m.getMember());
        }

        @Override
        public boolean isFieldVisible(Field f) {
            return this._fieldMinLevel.isVisible(f);
        }

        @Override
        public boolean isFieldVisible(AnnotatedField f) {
            return this.isFieldVisible(f.getAnnotated());
        }

        @Override
        public boolean isGetterVisible(Method m) {
            return this._getterMinLevel.isVisible(m);
        }

        @Override
        public boolean isGetterVisible(AnnotatedMethod m) {
            return this.isGetterVisible(m.getAnnotated());
        }

        @Override
        public boolean isIsGetterVisible(Method m) {
            return this._isGetterMinLevel.isVisible(m);
        }

        @Override
        public boolean isIsGetterVisible(AnnotatedMethod m) {
            return this.isIsGetterVisible(m.getAnnotated());
        }

        @Override
        public boolean isSetterVisible(Method m) {
            return this._setterMinLevel.isVisible(m);
        }

        @Override
        public boolean isSetterVisible(AnnotatedMethod m) {
            return this.isSetterVisible(m.getAnnotated());
        }

        private static boolean hasMethod(JsonMethod[] methods, JsonMethod method) {
            for (JsonMethod curr : methods) {
                if (curr != method && curr != JsonMethod.ALL) continue;
                return true;
            }
            return false;
        }

        public String toString() {
            return "[Visibility:" + " getter: " + (Object)((Object)this._getterMinLevel) + ", isGetter: " + (Object)((Object)this._isGetterMinLevel) + ", setter: " + (Object)((Object)this._setterMinLevel) + ", creator: " + (Object)((Object)this._creatorMinLevel) + ", field: " + (Object)((Object)this._fieldMinLevel) + "]";
        }
    }
}

