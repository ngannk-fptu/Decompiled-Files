/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.commons.ClassRemapper;
import net.bytebuddy.jar.asm.commons.Remapper;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class RenamingPlugin
extends AsmVisitorWrapper.AbstractBase
implements Plugin {
    private final Renaming renaming;
    private final ElementMatcher<? super TypeDescription> matcher;

    public RenamingPlugin(String pattern, String replacement) {
        this(new Renaming.ForPattern(Pattern.compile(pattern), replacement));
    }

    public RenamingPlugin(String pattern, String replacement, String prefix) {
        this(new Renaming.ForPattern(Pattern.compile(pattern), replacement), ElementMatchers.nameStartsWith(prefix));
    }

    public RenamingPlugin(Renaming renaming) {
        this(renaming, ElementMatchers.any());
    }

    public RenamingPlugin(Renaming renaming, ElementMatcher<? super TypeDescription> matcher) {
        this.renaming = renaming;
        this.matcher = matcher;
    }

    @Override
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
        return builder.visit(this);
    }

    @Override
    public boolean matches(TypeDescription target) {
        return this.matcher.matches(target);
    }

    @Override
    public void close() {
    }

    @Override
    public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
        return new ClassRemapper(classVisitor, new RenamingRemapper(this.renaming));
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
        if (!this.renaming.equals(((RenamingPlugin)object).renaming)) {
            return false;
        }
        return this.matcher.equals(((RenamingPlugin)object).matcher);
    }

    public int hashCode() {
        return (this.getClass().hashCode() * 31 + this.renaming.hashCode()) * 31 + this.matcher.hashCode();
    }

    protected static class RenamingRemapper
    extends Remapper {
        private final Renaming renaming;
        private final Map<String, String> cache = new HashMap<String, String>();

        protected RenamingRemapper(Renaming renaming) {
            this.renaming = renaming;
        }

        public String map(String internalName) {
            String renamed = this.cache.get(internalName);
            if (renamed != null) {
                return renamed;
            }
            renamed = this.renaming.apply(internalName.replace('/', '.')).replace('.', '/');
            this.cache.put(internalName, renamed);
            return renamed;
        }
    }

    public static interface Renaming {
        public String apply(String var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Compound
        implements Renaming {
            private final List<Renaming> renamings;

            public Compound(Renaming ... renaming) {
                this(Arrays.asList(renaming));
            }

            public Compound(List<? extends Renaming> renamings) {
                this.renamings = new ArrayList<Renaming>(renamings.size());
                for (Renaming renaming : renamings) {
                    if (renaming instanceof Compound) {
                        this.renamings.addAll(((Compound)renaming).renamings);
                        continue;
                    }
                    if (renaming instanceof NoOp) continue;
                    this.renamings.add(renaming);
                }
            }

            @Override
            public String apply(String name) {
                for (Renaming remapping : this.renamings) {
                    name = remapping.apply(name);
                }
                return name;
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
                return ((Object)this.renamings).equals(((Compound)object).renamings);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.renamings).hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForPattern
        implements Renaming {
            private final Pattern pattern;
            private final String replacement;

            public ForPattern(Pattern pattern, String replacement) {
                this.pattern = pattern;
                this.replacement = replacement;
            }

            public String apply(String name) {
                Matcher matcher = this.pattern.matcher(name);
                if (matcher.find()) {
                    StringBuffer buffer = new StringBuffer();
                    do {
                        matcher.appendReplacement(buffer, this.replacement);
                    } while (matcher.find());
                    return matcher.appendTail(buffer).toString();
                }
                return name;
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
                if (!this.replacement.equals(((ForPattern)object).replacement)) {
                    return false;
                }
                return this.pattern.equals(((ForPattern)object).pattern);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.pattern.hashCode()) * 31 + this.replacement.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements Renaming
        {
            INSTANCE;


            @Override
            public String apply(String name) {
                return name;
            }
        }
    }
}

