/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import java.util.function.Predicate;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.IUpdatableModule;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.util.SimpleSetOfCharArray;

public interface IModuleAwareNameEnvironment
extends INameEnvironment {
    @Override
    default public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
        return this.findType(compoundTypeName, ModuleBinding.ANY);
    }

    @Override
    default public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
        return this.findType(typeName, packageName, ModuleBinding.ANY);
    }

    @Override
    default public boolean isPackage(char[][] parentPackageName, char[] packageName) {
        return this.getModulesDeclaringPackage(CharOperation.arrayConcat(parentPackageName, packageName), ModuleBinding.ANY) != null;
    }

    public NameEnvironmentAnswer findType(char[][] var1, char[] var2);

    public NameEnvironmentAnswer findType(char[] var1, char[][] var2, char[] var3);

    public char[][] getModulesDeclaringPackage(char[][] var1, char[] var2);

    default public char[][] getUniqueModulesDeclaringPackage(char[][] packageName, char[] moduleName) {
        Object allNames = this.getModulesDeclaringPackage(packageName, moduleName);
        if (allNames != null && ((char[][])allNames).length > 1) {
            SimpleSetOfCharArray set = new SimpleSetOfCharArray(((char[][])allNames).length);
            char[][] cArray = allNames;
            int n = ((char[][])allNames).length;
            int n2 = 0;
            while (n2 < n) {
                char[] oneName = cArray[n2];
                set.add(oneName);
                ++n2;
            }
            allNames = new char[set.elementSize][];
            set.asArray((Object[])allNames);
        }
        return allNames;
    }

    public boolean hasCompilationUnit(char[][] var1, char[] var2, boolean var3);

    public IModule getModule(char[] var1);

    public char[][] getAllAutomaticModules();

    default public void applyModuleUpdates(IUpdatableModule module, IUpdatableModule.UpdateKind kind) {
    }

    public char[][] listPackages(char[] var1);

    public static abstract class LookupStrategy
    extends Enum<LookupStrategy> {
        public static final /* enum */ LookupStrategy Named = new LookupStrategy(){

            @Override
            public <T> boolean matchesWithName(T elem, Predicate<T> isNamed, Predicate<T> nameMatcher) {
                if (!$assertionsDisabled && nameMatcher == null) {
                    throw new AssertionError((Object)"name match needs a nameMatcher");
                }
                return isNamed.test(elem) && nameMatcher.test(elem);
            }
        };
        public static final /* enum */ LookupStrategy AnyNamed = new LookupStrategy(){

            @Override
            public <T> boolean matchesWithName(T elem, Predicate<T> isNamed, Predicate<T> nameMatcher) {
                return isNamed.test(elem);
            }
        };
        public static final /* enum */ LookupStrategy Any = new LookupStrategy(){

            @Override
            public <T> boolean matchesWithName(T elem, Predicate<T> isNamed, Predicate<T> nameMatcher) {
                return true;
            }
        };
        public static final /* enum */ LookupStrategy Unnamed = new LookupStrategy(){

            @Override
            public <T> boolean matchesWithName(T elem, Predicate<T> isNamed, Predicate<T> nameMatcher) {
                return !isNamed.test(elem);
            }
        };
        private static final /* synthetic */ LookupStrategy[] ENUM$VALUES;

        static {
            ENUM$VALUES = new LookupStrategy[]{Named, AnyNamed, Any, Unnamed};
        }

        public abstract <T> boolean matchesWithName(T var1, Predicate<T> var2, Predicate<T> var3);

        public <T> boolean matches(T elem, Predicate<T> isNamed) {
            return this.matchesWithName(elem, isNamed, t -> true);
        }

        public static LookupStrategy get(char[] moduleName) {
            if (moduleName == ModuleBinding.ANY) {
                return Any;
            }
            if (moduleName == ModuleBinding.ANY_NAMED) {
                return AnyNamed;
            }
            if (moduleName == ModuleBinding.UNNAMED) {
                return Unnamed;
            }
            return Named;
        }

        public static String getStringName(char[] moduleName) {
            switch (LookupStrategy.get(moduleName)) {
                case Named: {
                    return String.valueOf(moduleName);
                }
            }
            return null;
        }

        public static LookupStrategy[] values() {
            LookupStrategy[] lookupStrategyArray = ENUM$VALUES;
            int n = lookupStrategyArray.length;
            LookupStrategy[] lookupStrategyArray2 = new LookupStrategy[n];
            System.arraycopy(ENUM$VALUES, 0, lookupStrategyArray2, 0, n);
            return lookupStrategyArray2;
        }

        public static LookupStrategy valueOf(String string) {
            return Enum.valueOf(LookupStrategy.class, string);
        }
    }
}

