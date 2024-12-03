/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ExportsStatement;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.ModuleStatement;
import org.eclipse.jdt.internal.compiler.ast.OpensStatement;
import org.eclipse.jdt.internal.compiler.ast.ProvidesStatement;
import org.eclipse.jdt.internal.compiler.ast.RequiresStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UsesStatement;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.IModulePathEntry;
import org.eclipse.jdt.internal.compiler.env.ISourceModule;
import org.eclipse.jdt.internal.compiler.env.ModuleReferenceImpl;
import org.eclipse.jdt.internal.compiler.env.PackageExportImpl;

public class BasicModule
implements ISourceModule {
    private boolean isOpen = false;
    char[] name;
    IModule.IModuleReference[] requires;
    IModule.IPackageExport[] exports;
    char[][] uses;
    Service[] provides;
    IModule.IPackageExport[] opens;
    private ICompilationUnit compilationUnit;

    private static PackageExportImpl createPackageExport(ExportsStatement[] refs, int i) {
        ExportsStatement ref = refs[i];
        PackageExportImpl exp = new PackageExportImpl();
        exp.pack = ref.pkgName;
        ModuleReference[] imp = ref.targets;
        if (imp != null) {
            exp.exportedTo = new char[imp.length][];
            int j = 0;
            while (j < imp.length) {
                exp.exportedTo = imp[j].tokens;
                ++j;
            }
        }
        return exp;
    }

    private static Service createService(TypeReference service, TypeReference[] with) {
        Service ser = new Service();
        ser.provides = CharOperation.concatWith(service.getTypeName(), '.');
        ser.with = new char[with.length][];
        int i = 0;
        while (i < with.length) {
            ser.with[i] = CharOperation.concatWith(with[i].getTypeName(), '.');
            ++i;
        }
        return ser;
    }

    private static PackageExportImpl createPackageOpen(OpensStatement ref) {
        PackageExportImpl exp = new PackageExportImpl();
        exp.pack = ref.pkgName;
        ModuleReference[] imp = ref.targets;
        if (imp != null) {
            exp.exportedTo = new char[imp.length][];
            int j = 0;
            while (j < imp.length) {
                exp.exportedTo = imp[j].tokens;
                ++j;
            }
        }
        return exp;
    }

    public BasicModule(ModuleDeclaration descriptor, IModulePathEntry root) {
        PackageExportImpl exp;
        int i;
        ModuleStatement[] refs;
        this.compilationUnit = descriptor.compilationResult().compilationUnit;
        this.name = descriptor.moduleName;
        if (descriptor.requiresCount > 0) {
            refs = descriptor.requires;
            this.requires = new ModuleReferenceImpl[refs.length];
            i = 0;
            while (i < refs.length) {
                ModuleReferenceImpl ref = new ModuleReferenceImpl();
                ref.name = CharOperation.concatWith(((RequiresStatement)refs[i]).module.tokens, '.');
                ref.modifiers = ((RequiresStatement)refs[i]).modifiers;
                this.requires[i] = ref;
                ++i;
            }
        } else {
            this.requires = new ModuleReferenceImpl[0];
        }
        if (descriptor.exportsCount > 0) {
            refs = descriptor.exports;
            this.exports = new PackageExportImpl[refs.length];
            i = 0;
            while (i < refs.length) {
                exp = BasicModule.createPackageExport((ExportsStatement[])refs, i);
                this.exports[i] = exp;
                ++i;
            }
        } else {
            this.exports = new PackageExportImpl[0];
        }
        if (descriptor.usesCount > 0) {
            UsesStatement[] u = descriptor.uses;
            this.uses = new char[u.length][];
            i = 0;
            while (i < u.length) {
                this.uses[i] = CharOperation.concatWith(u[i].serviceInterface.getTypeName(), '.');
                ++i;
            }
        }
        if (descriptor.servicesCount > 0) {
            ProvidesStatement[] services = descriptor.services;
            this.provides = new Service[descriptor.servicesCount];
            i = 0;
            while (i < descriptor.servicesCount) {
                this.provides[i] = BasicModule.createService(services[i].serviceInterface, services[i].implementations);
                ++i;
            }
        }
        if (descriptor.opensCount > 0) {
            refs = descriptor.opens;
            this.opens = new PackageExportImpl[refs.length];
            i = 0;
            while (i < refs.length) {
                exp = BasicModule.createPackageOpen((OpensStatement)refs[i]);
                this.opens[i] = exp;
                ++i;
            }
        } else {
            this.opens = new PackageExportImpl[0];
        }
        this.isOpen = descriptor.isOpen();
    }

    @Override
    public ICompilationUnit getCompilationUnit() {
        return this.compilationUnit;
    }

    @Override
    public char[] name() {
        return this.name;
    }

    @Override
    public IModule.IModuleReference[] requires() {
        return this.requires;
    }

    @Override
    public IModule.IPackageExport[] exports() {
        return this.exports;
    }

    @Override
    public char[][] uses() {
        return this.uses;
    }

    @Override
    public IModule.IService[] provides() {
        return this.provides;
    }

    @Override
    public IModule.IPackageExport[] opens() {
        return this.opens;
    }

    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IModule)) {
            return false;
        }
        IModule mod = (IModule)o;
        if (!CharOperation.equals(this.name, mod.name())) {
            return false;
        }
        return Arrays.equals(this.requires, mod.requires());
    }

    public int hashCode() {
        int result = 17;
        int c = CharOperation.hashCode(this.name);
        result = 31 * result + c;
        c = Arrays.hashCode(this.requires);
        result = 31 * result + c;
        return result;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(this.getClass().getName());
        this.toStringContent(buffer);
        return buffer.toString();
    }

    protected void toStringContent(StringBuffer buffer) {
        int n;
        int n2;
        Object object;
        int i;
        buffer.append("\nmodule ");
        buffer.append(this.name).append(' ');
        buffer.append('{').append('\n');
        if (this.requires != null) {
            i = 0;
            while (i < this.requires.length) {
                buffer.append("\trequires ");
                if (this.requires[i].isTransitive()) {
                    buffer.append(" public ");
                }
                buffer.append(this.requires[i].name());
                buffer.append(';').append('\n');
                ++i;
            }
        }
        if (this.exports != null) {
            buffer.append('\n');
            i = 0;
            while (i < this.exports.length) {
                buffer.append("\texports ");
                buffer.append(this.exports[i].toString());
                ++i;
            }
        }
        if (this.uses != null) {
            buffer.append('\n');
            object = this.uses;
            n2 = this.uses.length;
            n = 0;
            while (n < n2) {
                char[] cs = object[n];
                buffer.append(cs);
                buffer.append(';').append('\n');
                ++n;
            }
        }
        if (this.provides != null) {
            buffer.append('\n');
            object = this.provides;
            n2 = this.provides.length;
            n = 0;
            while (n < n2) {
                char[] ser = object[n];
                buffer.append(ser.toString());
                ++n;
            }
        }
        buffer.append('\n').append('}').toString();
    }

    static class Service
    implements IModule.IService {
        char[] provides;
        char[][] with;

        Service() {
        }

        @Override
        public char[] name() {
            return this.provides;
        }

        @Override
        public char[][] with() {
            return this.with;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("provides");
            buffer.append(this.provides);
            buffer.append(" with ");
            buffer.append(this.with);
            buffer.append(';');
            return buffer.toString();
        }
    }
}

