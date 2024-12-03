/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileStruct;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryModule;
import org.eclipse.jdt.internal.compiler.env.IModule;

public class ModuleInfo
extends ClassFileStruct
implements IBinaryModule {
    protected int flags;
    protected int requiresCount;
    protected int exportsCount;
    protected int usesCount;
    protected int providesCount;
    protected int opensCount;
    protected char[] name;
    protected char[] version;
    protected ModuleReferenceInfo[] requires;
    protected PackageExportInfo[] exports;
    protected PackageExportInfo[] opens;
    char[][] uses;
    IModule.IService[] provides;
    protected AnnotationInfo[] annotations;
    private long tagBits;

    @Override
    public boolean isOpen() {
        return (this.flags & 0x20) != 0;
    }

    public int requiresCount() {
        return this.requiresCount;
    }

    public int exportsCount() {
        return this.exportsCount;
    }

    public int usesCount() {
        return this.usesCount;
    }

    public int providesCount() {
        return this.providesCount;
    }

    @Override
    public char[] name() {
        return this.name;
    }

    public void setName(char[] name) {
        this.name = name;
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
    public IBinaryAnnotation[] getAnnotations() {
        return this.annotations;
    }

    @Override
    public long getTagBits() {
        return this.tagBits;
    }

    protected ModuleInfo(byte[] classFileBytes, int[] offsets, int offset) {
        super(classFileBytes, offsets, offset);
    }

    public static ModuleInfo createModule(byte[] classFileBytes, int[] offsets, int offset) {
        ModuleInfo module = new ModuleInfo(classFileBytes, offsets, 0);
        module.readModuleAttribute(offset + 6);
        return module;
    }

    private void readModuleAttribute(int moduleOffset) {
        char[] exportedToName;
        int k;
        int exportedtoCount;
        char[] exported;
        int count;
        int name_index = this.constantPoolOffsets[this.u2At(moduleOffset)];
        int utf8Offset = this.constantPoolOffsets[this.u2At(name_index + 1)];
        this.name = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        CharOperation.replace(this.name, '/', '.');
        this.flags = this.u2At(moduleOffset += 2);
        int version_index = this.u2At(moduleOffset += 2);
        if (version_index > 0) {
            utf8Offset = this.constantPoolOffsets[version_index];
            this.version = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        }
        this.requiresCount = count = this.u2At(moduleOffset += 2);
        this.requires = new ModuleReferenceInfo[count];
        moduleOffset += 2;
        int i = 0;
        while (i < count) {
            int modifiers;
            name_index = this.constantPoolOffsets[this.u2At(moduleOffset)];
            utf8Offset = this.constantPoolOffsets[this.u2At(name_index + 1)];
            char[] requiresNames = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            this.requires[i] = new ModuleReferenceInfo();
            CharOperation.replace(requiresNames, '/', '.');
            this.requires[i].refName = requiresNames;
            this.requires[i].modifiers = modifiers = this.u2At(moduleOffset += 2);
            this.requires[i].isTransitive = (0x20 & modifiers) != 0;
            version_index = this.u2At(moduleOffset += 2);
            if (version_index > 0) {
                utf8Offset = this.constantPoolOffsets[version_index];
                this.requires[i].required_version = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            }
            moduleOffset += 2;
            ++i;
        }
        count = this.u2At(moduleOffset);
        moduleOffset += 2;
        this.exportsCount = count;
        this.exports = new PackageExportInfo[count];
        i = 0;
        while (i < count) {
            PackageExportInfo pack;
            name_index = this.constantPoolOffsets[this.u2At(moduleOffset)];
            utf8Offset = this.constantPoolOffsets[this.u2At(name_index + 1)];
            exported = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            CharOperation.replace(exported, '/', '.');
            this.exports[i] = pack = new PackageExportInfo();
            pack.packageName = exported;
            pack.modifiers = this.u2At(moduleOffset += 2);
            exportedtoCount = this.u2At(moduleOffset += 2);
            moduleOffset += 2;
            if (exportedtoCount > 0) {
                pack.exportedTo = new char[exportedtoCount][];
                pack.exportedToCount = exportedtoCount;
                k = 0;
                while (k < exportedtoCount) {
                    name_index = this.constantPoolOffsets[this.u2At(moduleOffset)];
                    utf8Offset = this.constantPoolOffsets[this.u2At(name_index + 1)];
                    exportedToName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                    CharOperation.replace(exportedToName, '/', '.');
                    pack.exportedTo[k] = exportedToName;
                    moduleOffset += 2;
                    ++k;
                }
            }
            ++i;
        }
        count = this.u2At(moduleOffset);
        moduleOffset += 2;
        this.opensCount = count;
        this.opens = new PackageExportInfo[count];
        i = 0;
        while (i < count) {
            PackageExportInfo pack;
            name_index = this.constantPoolOffsets[this.u2At(moduleOffset)];
            utf8Offset = this.constantPoolOffsets[this.u2At(name_index + 1)];
            exported = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            CharOperation.replace(exported, '/', '.');
            this.opens[i] = pack = new PackageExportInfo();
            pack.packageName = exported;
            pack.modifiers = this.u2At(moduleOffset += 2);
            exportedtoCount = this.u2At(moduleOffset += 2);
            moduleOffset += 2;
            if (exportedtoCount > 0) {
                pack.exportedTo = new char[exportedtoCount][];
                pack.exportedToCount = exportedtoCount;
                k = 0;
                while (k < exportedtoCount) {
                    name_index = this.constantPoolOffsets[this.u2At(moduleOffset)];
                    utf8Offset = this.constantPoolOffsets[this.u2At(name_index + 1)];
                    exportedToName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                    CharOperation.replace(exportedToName, '/', '.');
                    pack.exportedTo[k] = exportedToName;
                    moduleOffset += 2;
                    ++k;
                }
            }
            ++i;
        }
        count = this.u2At(moduleOffset);
        moduleOffset += 2;
        this.usesCount = count;
        this.uses = new char[count][];
        i = 0;
        while (i < count) {
            int classIndex = this.constantPoolOffsets[this.u2At(moduleOffset)];
            utf8Offset = this.constantPoolOffsets[this.u2At(classIndex + 1)];
            char[] inf = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            CharOperation.replace(inf, '/', '.');
            this.uses[i] = inf;
            moduleOffset += 2;
            ++i;
        }
        count = this.u2At(moduleOffset);
        moduleOffset += 2;
        this.providesCount = count;
        this.provides = new ServiceInfo[count];
        i = 0;
        while (i < count) {
            int classIndex = this.constantPoolOffsets[this.u2At(moduleOffset)];
            utf8Offset = this.constantPoolOffsets[this.u2At(classIndex + 1)];
            char[] inf = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            CharOperation.replace(inf, '/', '.');
            ServiceInfo service = new ServiceInfo();
            this.provides[i] = service;
            service.serviceName = inf;
            int implCount = this.u2At(moduleOffset += 2);
            moduleOffset += 2;
            service.with = new char[implCount][];
            if (implCount > 0) {
                service.with = new char[implCount][];
                int k2 = 0;
                while (k2 < implCount) {
                    classIndex = this.constantPoolOffsets[this.u2At(moduleOffset)];
                    utf8Offset = this.constantPoolOffsets[this.u2At(classIndex + 1)];
                    char[] implName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                    CharOperation.replace(implName, '/', '.');
                    service.with[k2] = implName;
                    moduleOffset += 2;
                    ++k2;
                }
            }
            ++i;
        }
    }

    void setAnnotations(AnnotationInfo[] annotationInfos, long tagBits, boolean fullyInitialize) {
        this.annotations = annotationInfos;
        this.tagBits = tagBits;
        if (fullyInitialize) {
            int i = 0;
            int max = annotationInfos.length;
            while (i < max) {
                annotationInfos[i].initialize();
                ++i;
            }
        }
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
        int i;
        buffer.append("\nmodule ");
        buffer.append(this.name).append(' ');
        buffer.append('{').append('\n');
        if (this.requiresCount > 0) {
            i = 0;
            while (i < this.requiresCount) {
                buffer.append("\trequires ");
                if (this.requires[i].isTransitive) {
                    buffer.append(" public ");
                }
                buffer.append(this.requires[i].refName);
                buffer.append(';').append('\n');
                ++i;
            }
        }
        if (this.exportsCount > 0) {
            buffer.append('\n');
            i = 0;
            while (i < this.exportsCount) {
                buffer.append("\texports ");
                buffer.append(this.exports[i].toString());
                ++i;
            }
        }
        buffer.append('\n').append('}').toString();
    }

    class ModuleReferenceInfo
    implements IModule.IModuleReference {
        char[] refName;
        boolean isTransitive = false;
        int modifiers;
        char[] required_version;

        ModuleReferenceInfo() {
        }

        @Override
        public char[] name() {
            return this.refName;
        }

        @Override
        public boolean isTransitive() {
            return this.isTransitive;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IModule.IModuleReference)) {
                return false;
            }
            IModule.IModuleReference mod = (IModule.IModuleReference)o;
            if (this.modifiers != mod.getModifiers()) {
                return false;
            }
            return CharOperation.equals(this.refName, mod.name(), false);
        }

        public int hashCode() {
            return CharOperation.hashCode(this.refName);
        }

        @Override
        public int getModifiers() {
            return this.modifiers;
        }
    }

    class PackageExportInfo
    implements IModule.IPackageExport {
        char[] packageName;
        char[][] exportedTo;
        int exportedToCount;
        int modifiers;

        PackageExportInfo() {
        }

        @Override
        public char[] name() {
            return this.packageName;
        }

        @Override
        public char[][] targets() {
            return this.exportedTo;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            this.toStringContent(buffer);
            return buffer.toString();
        }

        protected void toStringContent(StringBuffer buffer) {
            buffer.append(this.packageName);
            if (this.exportedToCount > 0) {
                buffer.append(" to ");
                int i = 0;
                while (i < this.exportedToCount) {
                    buffer.append(this.exportedTo[i]);
                    buffer.append(',').append(' ');
                    ++i;
                }
            }
            buffer.append(';').append('\n');
        }
    }

    class ServiceInfo
    implements IModule.IService {
        char[] serviceName;
        char[][] with;

        ServiceInfo() {
        }

        @Override
        public char[] name() {
            return this.serviceName;
        }

        @Override
        public char[][] with() {
            return this.with;
        }
    }
}

