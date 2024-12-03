/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface NamingStrategy {
    public static final String BYTE_BUDDY_RENAME_PACKAGE = "net.bytebuddy.renamed";
    public static final String NO_PREFIX = "";

    public String subclass(TypeDescription.Generic var1);

    public String redefine(TypeDescription var1);

    public String rebase(TypeDescription var1);

    @HashCodeAndEqualsPlugin.Enhance
    public static class PrefixingRandom
    extends AbstractBase {
        private final String prefix;
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
        private final RandomString randomString;

        public PrefixingRandom(String prefix) {
            this.prefix = prefix;
            this.randomString = new RandomString();
        }

        protected String name(TypeDescription superClass) {
            return this.prefix + "." + superClass.getName() + "$" + this.randomString.nextString();
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
            return this.prefix.equals(((PrefixingRandom)object).prefix);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.prefix.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class SuffixingRandom
    extends Suffixing {
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
        private final RandomString randomString;

        public SuffixingRandom(String suffix) {
            this(suffix, Suffixing.BaseNameResolver.ForUnnamedType.INSTANCE);
        }

        public SuffixingRandom(String suffix, String javaLangPackagePrefix) {
            this(suffix, Suffixing.BaseNameResolver.ForUnnamedType.INSTANCE, javaLangPackagePrefix);
        }

        @Deprecated
        public SuffixingRandom(String suffix, BaseNameResolver baseNameResolver) {
            this(suffix, (Suffixing.BaseNameResolver)baseNameResolver);
        }

        public SuffixingRandom(String suffix, Suffixing.BaseNameResolver baseNameResolver) {
            this(suffix, baseNameResolver, NamingStrategy.BYTE_BUDDY_RENAME_PACKAGE);
        }

        @Deprecated
        public SuffixingRandom(String suffix, BaseNameResolver baseNameResolver, String javaLangPackagePrefix) {
            this(suffix, (Suffixing.BaseNameResolver)baseNameResolver, javaLangPackagePrefix);
        }

        public SuffixingRandom(String suffix, Suffixing.BaseNameResolver baseNameResolver, String javaLangPackagePrefix) {
            this(suffix, baseNameResolver, javaLangPackagePrefix, new RandomString());
        }

        @Deprecated
        public SuffixingRandom(String suffix, BaseNameResolver baseNameResolver, String javaLangPackagePrefix, RandomString randomString) {
            this(suffix, (Suffixing.BaseNameResolver)baseNameResolver, javaLangPackagePrefix, randomString);
        }

        public SuffixingRandom(String suffix, Suffixing.BaseNameResolver baseNameResolver, String javaLangPackagePrefix, RandomString randomString) {
            super(suffix, baseNameResolver, javaLangPackagePrefix);
            this.randomString = randomString;
        }

        protected String name(TypeDescription superClass) {
            return super.name(superClass) + "$" + this.randomString.nextString();
        }

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
            return this.getClass() == object.getClass();
        }

        public int hashCode() {
            return super.hashCode();
        }

        @Deprecated
        public static interface BaseNameResolver
        extends Suffixing.BaseNameResolver {

            @Deprecated
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForFixedValue
            extends Suffixing.BaseNameResolver.ForFixedValue
            implements BaseNameResolver {
                public ForFixedValue(String name) {
                    super(name);
                }

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
                    return this.getClass() == object.getClass();
                }

                public int hashCode() {
                    return super.hashCode();
                }
            }

            @Deprecated
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForGivenType
            extends Suffixing.BaseNameResolver.ForGivenType
            implements BaseNameResolver {
                public ForGivenType(TypeDescription typeDescription) {
                    super(typeDescription);
                }

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
                    return this.getClass() == object.getClass();
                }

                public int hashCode() {
                    return super.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @Deprecated
            public static enum ForUnnamedType implements BaseNameResolver
            {
                INSTANCE;


                @Override
                public String resolve(TypeDescription typeDescription) {
                    return typeDescription.getName();
                }
            }
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class Suffixing
    extends AbstractBase {
        private static final String JAVA_PACKAGE = "java.";
        private final String suffix;
        private final String javaLangPackagePrefix;
        private final BaseNameResolver baseNameResolver;

        public Suffixing(String suffix) {
            this(suffix, BaseNameResolver.ForUnnamedType.INSTANCE);
        }

        public Suffixing(String suffix, String javaLangPackagePrefix) {
            this(suffix, BaseNameResolver.ForUnnamedType.INSTANCE, javaLangPackagePrefix);
        }

        public Suffixing(String suffix, BaseNameResolver baseNameResolver) {
            this(suffix, baseNameResolver, NamingStrategy.BYTE_BUDDY_RENAME_PACKAGE);
        }

        public Suffixing(String suffix, BaseNameResolver baseNameResolver, String javaLangPackagePrefix) {
            this.suffix = suffix;
            this.baseNameResolver = baseNameResolver;
            this.javaLangPackagePrefix = javaLangPackagePrefix;
        }

        protected String name(TypeDescription superClass) {
            String baseName = this.baseNameResolver.resolve(superClass);
            if (baseName.startsWith(JAVA_PACKAGE) && !this.javaLangPackagePrefix.equals(NamingStrategy.NO_PREFIX)) {
                baseName = this.javaLangPackagePrefix + "." + baseName;
            }
            return baseName + "$" + this.suffix;
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
            if (!this.suffix.equals(((Suffixing)object).suffix)) {
                return false;
            }
            if (!this.javaLangPackagePrefix.equals(((Suffixing)object).javaLangPackagePrefix)) {
                return false;
            }
            return this.baseNameResolver.equals(((Suffixing)object).baseNameResolver);
        }

        public int hashCode() {
            return ((this.getClass().hashCode() * 31 + this.suffix.hashCode()) * 31 + this.javaLangPackagePrefix.hashCode()) * 31 + this.baseNameResolver.hashCode();
        }

        public static interface BaseNameResolver {
            public String resolve(TypeDescription var1);

            @HashCodeAndEqualsPlugin.Enhance
            public static class WithCallerSuffix
            implements BaseNameResolver {
                private final BaseNameResolver delegate;

                public WithCallerSuffix(BaseNameResolver delegate) {
                    this.delegate = delegate;
                }

                public String resolve(TypeDescription typeDescription) {
                    boolean matched = false;
                    String caller = null;
                    for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
                        if (stackTraceElement.getClassName().equals(ByteBuddy.class.getName())) {
                            matched = true;
                            continue;
                        }
                        if (!matched) continue;
                        caller = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName();
                        break;
                    }
                    if (caller == null) {
                        throw new IllegalStateException("Base name resolver not invoked via " + ByteBuddy.class);
                    }
                    return this.delegate.resolve(typeDescription) + "$" + caller.replace('.', '$');
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
                    return this.delegate.equals(((WithCallerSuffix)object).delegate);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.delegate.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForFixedValue
            implements BaseNameResolver {
                private final String name;

                public ForFixedValue(String name) {
                    this.name = name;
                }

                public String resolve(TypeDescription typeDescription) {
                    return this.name;
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
                    return this.name.equals(((ForFixedValue)object).name);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.name.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForGivenType
            implements BaseNameResolver {
                private final TypeDescription typeDescription;

                public ForGivenType(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                public String resolve(TypeDescription typeDescription) {
                    return this.typeDescription.getName();
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
                    return this.typeDescription.equals(((ForGivenType)object).typeDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForUnnamedType implements BaseNameResolver
            {
                INSTANCE;


                @Override
                public String resolve(TypeDescription typeDescription) {
                    return typeDescription.getName();
                }
            }
        }
    }

    public static abstract class AbstractBase
    implements NamingStrategy {
        public String subclass(TypeDescription.Generic superClass) {
            return this.name(superClass.asErasure());
        }

        protected abstract String name(TypeDescription var1);

        public String redefine(TypeDescription typeDescription) {
            return typeDescription.getName();
        }

        public String rebase(TypeDescription typeDescription) {
            return typeDescription.getName();
        }
    }
}

