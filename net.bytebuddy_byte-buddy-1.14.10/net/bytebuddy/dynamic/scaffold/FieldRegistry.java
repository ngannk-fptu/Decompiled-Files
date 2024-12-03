/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.implementation.attribute.FieldAttributeAppender;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface FieldRegistry {
    public FieldRegistry prepend(LatentMatcher<? super FieldDescription> var1, FieldAttributeAppender.Factory var2, @MaybeNull Object var3, Transformer<FieldDescription> var4);

    public Compiled compile(TypeDescription var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Default
    implements FieldRegistry {
        private final List<Entry> entries;

        public Default() {
            this(Collections.emptyList());
        }

        private Default(List<Entry> entries) {
            this.entries = entries;
        }

        @Override
        public FieldRegistry prepend(LatentMatcher<? super FieldDescription> matcher, FieldAttributeAppender.Factory fieldAttributeAppenderFactory, @MaybeNull Object defaultValue, Transformer<FieldDescription> transformer) {
            ArrayList<Entry> entries = new ArrayList<Entry>(this.entries.size() + 1);
            entries.add(new Entry(matcher, fieldAttributeAppenderFactory, defaultValue, transformer));
            entries.addAll(this.entries);
            return new Default(entries);
        }

        @Override
        public net.bytebuddy.dynamic.scaffold.FieldRegistry$Compiled compile(TypeDescription instrumentedType) {
            ArrayList<Compiled.Entry> entries = new ArrayList<Compiled.Entry>(this.entries.size());
            HashMap<FieldAttributeAppender.Factory, FieldAttributeAppender> fieldAttributeAppenders = new HashMap<FieldAttributeAppender.Factory, FieldAttributeAppender>();
            for (Entry entry : this.entries) {
                FieldAttributeAppender fieldAttributeAppender = (FieldAttributeAppender)fieldAttributeAppenders.get(entry.getFieldAttributeAppenderFactory());
                if (fieldAttributeAppender == null) {
                    fieldAttributeAppender = entry.getFieldAttributeAppenderFactory().make(instrumentedType);
                    fieldAttributeAppenders.put(entry.getFieldAttributeAppenderFactory(), fieldAttributeAppender);
                }
                entries.add(new Compiled.Entry(entry.resolve(instrumentedType), fieldAttributeAppender, entry.getDefaultValue(), entry.getTransformer()));
            }
            return new Compiled(instrumentedType, entries);
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
            return ((Object)this.entries).equals(((Default)object).entries);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.entries).hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Compiled
        implements net.bytebuddy.dynamic.scaffold.FieldRegistry$Compiled {
            private final TypeDescription instrumentedType;
            private final List<Entry> entries;

            protected Compiled(TypeDescription instrumentedType, List<Entry> entries) {
                this.instrumentedType = instrumentedType;
                this.entries = entries;
            }

            @Override
            public TypeWriter.FieldPool.Record target(FieldDescription fieldDescription) {
                for (Entry entry : this.entries) {
                    if (!entry.matches(fieldDescription)) continue;
                    return entry.bind(this.instrumentedType, fieldDescription);
                }
                return new TypeWriter.FieldPool.Record.ForImplicitField(fieldDescription);
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
                if (!this.instrumentedType.equals(((Compiled)object).instrumentedType)) {
                    return false;
                }
                return ((Object)this.entries).equals(((Compiled)object).entries);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + ((Object)this.entries).hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Entry
            implements ElementMatcher<FieldDescription> {
                private final ElementMatcher<? super FieldDescription> matcher;
                private final FieldAttributeAppender fieldAttributeAppender;
                @MaybeNull
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
                private final Object defaultValue;
                private final Transformer<FieldDescription> transformer;

                protected Entry(ElementMatcher<? super FieldDescription> matcher, FieldAttributeAppender fieldAttributeAppender, @MaybeNull Object defaultValue, Transformer<FieldDescription> transformer) {
                    this.matcher = matcher;
                    this.fieldAttributeAppender = fieldAttributeAppender;
                    this.defaultValue = defaultValue;
                    this.transformer = transformer;
                }

                protected TypeWriter.FieldPool.Record bind(TypeDescription instrumentedType, FieldDescription fieldDescription) {
                    return new TypeWriter.FieldPool.Record.ForExplicitField(this.fieldAttributeAppender, this.defaultValue, this.transformer.transform(instrumentedType, fieldDescription));
                }

                @Override
                public boolean matches(@MaybeNull FieldDescription target) {
                    return this.matcher.matches(target);
                }

                public boolean equals(@MaybeNull Object object) {
                    block12: {
                        block11: {
                            Object object2;
                            block10: {
                                Object object3;
                                if (this == object) {
                                    return true;
                                }
                                if (object == null) {
                                    return false;
                                }
                                if (this.getClass() != object.getClass()) {
                                    return false;
                                }
                                if (!this.matcher.equals(((Entry)object).matcher)) {
                                    return false;
                                }
                                if (!this.fieldAttributeAppender.equals(((Entry)object).fieldAttributeAppender)) {
                                    return false;
                                }
                                Object object4 = ((Entry)object).defaultValue;
                                object2 = object3 = this.defaultValue;
                                if (object4 == null) break block10;
                                if (object2 == null) break block11;
                                if (!object3.equals(object4)) {
                                    return false;
                                }
                                break block12;
                            }
                            if (object2 == null) break block12;
                        }
                        return false;
                    }
                    return this.transformer.equals(((Entry)object).transformer);
                }

                public int hashCode() {
                    int n = ((this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.fieldAttributeAppender.hashCode()) * 31;
                    Object object = this.defaultValue;
                    if (object != null) {
                        n = n + object.hashCode();
                    }
                    return n * 31 + this.transformer.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Entry
        implements LatentMatcher<FieldDescription> {
            private final LatentMatcher<? super FieldDescription> matcher;
            private final FieldAttributeAppender.Factory fieldAttributeAppenderFactory;
            @MaybeNull
            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
            private final Object defaultValue;
            private final Transformer<FieldDescription> transformer;

            protected Entry(LatentMatcher<? super FieldDescription> matcher, FieldAttributeAppender.Factory fieldAttributeAppenderFactory, @MaybeNull Object defaultValue, Transformer<FieldDescription> transformer) {
                this.matcher = matcher;
                this.fieldAttributeAppenderFactory = fieldAttributeAppenderFactory;
                this.defaultValue = defaultValue;
                this.transformer = transformer;
            }

            protected FieldAttributeAppender.Factory getFieldAttributeAppenderFactory() {
                return this.fieldAttributeAppenderFactory;
            }

            @MaybeNull
            protected Object getDefaultValue() {
                return this.defaultValue;
            }

            protected Transformer<FieldDescription> getTransformer() {
                return this.transformer;
            }

            @Override
            public ElementMatcher<? super FieldDescription> resolve(TypeDescription typeDescription) {
                return this.matcher.resolve(typeDescription);
            }

            public boolean equals(@MaybeNull Object object) {
                block12: {
                    block11: {
                        Object object2;
                        block10: {
                            Object object3;
                            if (this == object) {
                                return true;
                            }
                            if (object == null) {
                                return false;
                            }
                            if (this.getClass() != object.getClass()) {
                                return false;
                            }
                            if (!this.matcher.equals(((Entry)object).matcher)) {
                                return false;
                            }
                            if (!this.fieldAttributeAppenderFactory.equals(((Entry)object).fieldAttributeAppenderFactory)) {
                                return false;
                            }
                            Object object4 = ((Entry)object).defaultValue;
                            object2 = object3 = this.defaultValue;
                            if (object4 == null) break block10;
                            if (object2 == null) break block11;
                            if (!object3.equals(object4)) {
                                return false;
                            }
                            break block12;
                        }
                        if (object2 == null) break block12;
                    }
                    return false;
                }
                return this.transformer.equals(((Entry)object).transformer);
            }

            public int hashCode() {
                int n = ((this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.fieldAttributeAppenderFactory.hashCode()) * 31;
                Object object = this.defaultValue;
                if (object != null) {
                    n = n + object.hashCode();
                }
                return n * 31 + this.transformer.hashCode();
            }
        }
    }

    public static interface Compiled
    extends TypeWriter.FieldPool {

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements Compiled
        {
            INSTANCE;


            @Override
            public TypeWriter.FieldPool.Record target(FieldDescription fieldDescription) {
                return new TypeWriter.FieldPool.Record.ForImplicitField(fieldDescription);
            }
        }
    }
}

