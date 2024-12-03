/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.implementation.attribute.RecordComponentAttributeAppender;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface RecordComponentRegistry {
    public RecordComponentRegistry prepend(LatentMatcher<? super RecordComponentDescription> var1, RecordComponentAttributeAppender.Factory var2, Transformer<RecordComponentDescription> var3);

    public Compiled compile(TypeDescription var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Default
    implements RecordComponentRegistry {
        private final List<Entry> entries;

        public Default() {
            this(Collections.emptyList());
        }

        private Default(List<Entry> entries) {
            this.entries = entries;
        }

        @Override
        public RecordComponentRegistry prepend(LatentMatcher<? super RecordComponentDescription> matcher, RecordComponentAttributeAppender.Factory recordComponentAttributeAppenderFactory, Transformer<RecordComponentDescription> transformer) {
            ArrayList<Entry> entries = new ArrayList<Entry>(this.entries.size() + 1);
            entries.add(new Entry(matcher, recordComponentAttributeAppenderFactory, transformer));
            entries.addAll(this.entries);
            return new Default(entries);
        }

        @Override
        public net.bytebuddy.dynamic.scaffold.RecordComponentRegistry$Compiled compile(TypeDescription instrumentedType) {
            ArrayList<Compiled.Entry> entries = new ArrayList<Compiled.Entry>(this.entries.size());
            HashMap<RecordComponentAttributeAppender.Factory, RecordComponentAttributeAppender> recordComponentAttributeAppenders = new HashMap<RecordComponentAttributeAppender.Factory, RecordComponentAttributeAppender>();
            for (Entry entry : this.entries) {
                RecordComponentAttributeAppender recordComponentAttributeAppender = (RecordComponentAttributeAppender)recordComponentAttributeAppenders.get(entry.getRecordComponentAttributeAppender());
                if (recordComponentAttributeAppender == null) {
                    recordComponentAttributeAppender = entry.getRecordComponentAttributeAppender().make(instrumentedType);
                    recordComponentAttributeAppenders.put(entry.getRecordComponentAttributeAppender(), recordComponentAttributeAppender);
                }
                entries.add(new Compiled.Entry(entry.resolve(instrumentedType), recordComponentAttributeAppender, entry.getTransformer()));
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
        implements net.bytebuddy.dynamic.scaffold.RecordComponentRegistry$Compiled {
            private final TypeDescription instrumentedType;
            private final List<Entry> entries;

            protected Compiled(TypeDescription instrumentedType, List<Entry> entries) {
                this.instrumentedType = instrumentedType;
                this.entries = entries;
            }

            @Override
            public TypeWriter.RecordComponentPool.Record target(RecordComponentDescription recordComponentDescription) {
                for (Entry entry : this.entries) {
                    if (!entry.matches(recordComponentDescription)) continue;
                    return entry.bind(this.instrumentedType, recordComponentDescription);
                }
                return new TypeWriter.RecordComponentPool.Record.ForImplicitRecordComponent(recordComponentDescription);
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
            implements ElementMatcher<RecordComponentDescription> {
                private final ElementMatcher<? super RecordComponentDescription> matcher;
                private final RecordComponentAttributeAppender recordComponentAttributeAppender;
                private final Transformer<RecordComponentDescription> transformer;

                protected Entry(ElementMatcher<? super RecordComponentDescription> matcher, RecordComponentAttributeAppender recordComponentAttributeAppender, Transformer<RecordComponentDescription> transformer) {
                    this.matcher = matcher;
                    this.recordComponentAttributeAppender = recordComponentAttributeAppender;
                    this.transformer = transformer;
                }

                protected TypeWriter.RecordComponentPool.Record bind(TypeDescription instrumentedType, RecordComponentDescription recordComponentDescription) {
                    return new TypeWriter.RecordComponentPool.Record.ForExplicitRecordComponent(this.recordComponentAttributeAppender, this.transformer.transform(instrumentedType, recordComponentDescription));
                }

                @Override
                public boolean matches(@MaybeNull RecordComponentDescription target) {
                    return this.matcher.matches(target);
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
                    if (!this.matcher.equals(((Entry)object).matcher)) {
                        return false;
                    }
                    if (!this.recordComponentAttributeAppender.equals(((Entry)object).recordComponentAttributeAppender)) {
                        return false;
                    }
                    return this.transformer.equals(((Entry)object).transformer);
                }

                public int hashCode() {
                    return ((this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.recordComponentAttributeAppender.hashCode()) * 31 + this.transformer.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Entry
        implements LatentMatcher<RecordComponentDescription> {
            private final LatentMatcher<? super RecordComponentDescription> matcher;
            private final RecordComponentAttributeAppender.Factory recordComponentAttributeAppender;
            private final Transformer<RecordComponentDescription> transformer;

            protected Entry(LatentMatcher<? super RecordComponentDescription> matcher, RecordComponentAttributeAppender.Factory recordComponentAttributeAppender, Transformer<RecordComponentDescription> transformer) {
                this.matcher = matcher;
                this.recordComponentAttributeAppender = recordComponentAttributeAppender;
                this.transformer = transformer;
            }

            protected RecordComponentAttributeAppender.Factory getRecordComponentAttributeAppender() {
                return this.recordComponentAttributeAppender;
            }

            protected Transformer<RecordComponentDescription> getTransformer() {
                return this.transformer;
            }

            @Override
            public ElementMatcher<? super RecordComponentDescription> resolve(TypeDescription typeDescription) {
                return this.matcher.resolve(typeDescription);
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
                if (!this.matcher.equals(((Entry)object).matcher)) {
                    return false;
                }
                if (!this.recordComponentAttributeAppender.equals(((Entry)object).recordComponentAttributeAppender)) {
                    return false;
                }
                return this.transformer.equals(((Entry)object).transformer);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.recordComponentAttributeAppender.hashCode()) * 31 + this.transformer.hashCode();
            }
        }
    }

    public static interface Compiled
    extends TypeWriter.RecordComponentPool {

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements Compiled
        {
            INSTANCE;


            @Override
            public TypeWriter.RecordComponentPool.Record target(RecordComponentDescription recordComponentDescription) {
                return new TypeWriter.RecordComponentPool.Record.ForImplicitRecordComponent(recordComponentDescription);
            }
        }
    }
}

