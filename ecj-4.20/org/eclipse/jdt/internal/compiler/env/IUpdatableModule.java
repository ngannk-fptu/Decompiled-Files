/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.util.SimpleSetOfCharArray;

public interface IUpdatableModule {
    public char[] name();

    public void addReads(char[] var1);

    public void addExports(char[] var1, char[][] var2);

    public void setMainClassName(char[] var1);

    public void setPackageNames(SimpleSetOfCharArray var1);

    public static class AddExports
    implements Consumer<IUpdatableModule> {
        char[] name;
        char[][] targets;

        public AddExports(char[] pkgName, char[][] targets) {
            this.name = pkgName;
            this.targets = targets;
        }

        @Override
        public void accept(IUpdatableModule t) {
            t.addExports(this.name, this.targets);
        }

        public char[] getName() {
            return this.name;
        }

        public char[][] getTargetModules() {
            return this.targets;
        }

        public UpdateKind getKind() {
            return UpdateKind.PACKAGE;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AddExports)) {
                return false;
            }
            AddExports pu = (AddExports)other;
            if (!CharOperation.equals(this.name, pu.name)) {
                return false;
            }
            return CharOperation.equals(this.targets, pu.targets);
        }

        public int hashCode() {
            int hash = CharOperation.hashCode(this.name);
            if (this.targets != null) {
                int i = 0;
                while (i < this.targets.length) {
                    hash += 17 * CharOperation.hashCode(this.targets[i]);
                    ++i;
                }
            }
            return hash;
        }

        public String toString() {
            return "add-exports " + CharOperation.charToString(this.name) + "=" + CharOperation.charToString(CharOperation.concatWith(this.targets, ','));
        }
    }

    public static class AddReads
    implements Consumer<IUpdatableModule> {
        char[] targetModule;

        public AddReads(char[] target) {
            this.targetModule = target;
        }

        @Override
        public void accept(IUpdatableModule t) {
            t.addReads(this.targetModule);
        }

        public char[] getTarget() {
            return this.targetModule;
        }

        public UpdateKind getKind() {
            return UpdateKind.MODULE;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AddReads)) {
                return false;
            }
            AddReads mu = (AddReads)other;
            return CharOperation.equals(this.targetModule, mu.targetModule);
        }

        public int hashCode() {
            return CharOperation.hashCode(this.targetModule);
        }

        public String toString() {
            return "add-read " + CharOperation.charToString(this.targetModule);
        }
    }

    public static enum UpdateKind {
        MODULE,
        PACKAGE;

    }

    public static class UpdatesByKind {
        List<Consumer<IUpdatableModule>> moduleUpdates = Collections.emptyList();
        List<Consumer<IUpdatableModule>> packageUpdates = Collections.emptyList();

        public List<Consumer<IUpdatableModule>> getList(UpdateKind kind, boolean create) {
            switch (kind) {
                case MODULE: {
                    if (this.moduleUpdates == Collections.EMPTY_LIST && create) {
                        this.moduleUpdates = new ArrayList<Consumer<IUpdatableModule>>();
                    }
                    return this.moduleUpdates;
                }
                case PACKAGE: {
                    if (this.packageUpdates == Collections.EMPTY_LIST && create) {
                        this.packageUpdates = new ArrayList<Consumer<IUpdatableModule>>();
                    }
                    return this.packageUpdates;
                }
            }
            throw new IllegalArgumentException("Unknown enum value " + (Object)((Object)kind));
        }

        public String toString() {
            StringBuilder result = new StringBuilder();
            for (Consumer<IUpdatableModule> consumer : this.moduleUpdates) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(consumer);
            }
            for (Consumer<IUpdatableModule> consumer : this.packageUpdates) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(consumer);
            }
            return result.toString();
        }
    }
}

