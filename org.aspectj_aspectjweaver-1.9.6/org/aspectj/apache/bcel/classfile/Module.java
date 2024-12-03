/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantModule;
import org.aspectj.apache.bcel.classfile.ConstantPool;

public final class Module
extends Attribute {
    private static final String[] NO_MODULE_NAMES = new String[0];
    private int moduleNameIndex;
    private int moduleFlags;
    private int moduleVersionIndex;
    private Require[] requires;
    private Export[] exports;
    private Open[] opens;
    private Uses[] uses;
    private Provide[] provides;
    private byte[] moduleInfo;
    private int ptr;
    private boolean unpacked = false;

    public Module(Module module) {
        super(module.getTag(), module.getNameIndex(), module.getLength(), module.getConstantPool());
        this.moduleInfo = module.getBytes();
    }

    public Module(int nameIndex, int length, byte[] data, ConstantPool cp) {
        super((byte)23, nameIndex, length, cp);
    }

    Module(int nameIndex, int length, DataInputStream stream, ConstantPool cp) throws IOException {
        this(nameIndex, length, (byte[])null, cp);
        this.moduleInfo = new byte[length];
        stream.read(this.moduleInfo);
        this.unpacked = false;
    }

    private final int readInt() {
        return ((this.moduleInfo[this.ptr++] & 0xFF) << 24) + ((this.moduleInfo[this.ptr++] & 0xFF) << 16) + ((this.moduleInfo[this.ptr++] & 0xFF) << 8) + (this.moduleInfo[this.ptr++] & 0xFF);
    }

    private final int readUnsignedShort() {
        return ((this.moduleInfo[this.ptr++] & 0xFF) << 8) + (this.moduleInfo[this.ptr++] & 0xFF);
    }

    private final int readUnsignedShort(int offset) {
        return ((this.moduleInfo[offset++] & 0xFF) << 8) + (this.moduleInfo[offset] & 0xFF);
    }

    private void ensureUnpacked() {
        if (!this.unpacked) {
            int j;
            int[] to;
            int toCount;
            int flags;
            int index;
            int i;
            this.ptr = 0;
            this.moduleNameIndex = this.readUnsignedShort();
            this.moduleFlags = this.readUnsignedShort();
            this.moduleVersionIndex = this.readUnsignedShort();
            int count = this.readUnsignedShort();
            this.requires = new Require[count];
            for (i = 0; i < count; ++i) {
                this.requires[i] = new Require(this.readUnsignedShort(), this.readUnsignedShort(), this.readUnsignedShort());
            }
            count = this.readUnsignedShort();
            this.exports = new Export[count];
            for (i = 0; i < count; ++i) {
                index = this.readUnsignedShort();
                flags = this.readUnsignedShort();
                toCount = this.readUnsignedShort();
                to = new int[toCount];
                for (j = 0; j < toCount; ++j) {
                    to[j] = this.readUnsignedShort();
                }
                this.exports[i] = new Export(index, flags, to);
            }
            count = this.readUnsignedShort();
            this.opens = new Open[count];
            for (i = 0; i < count; ++i) {
                index = this.readUnsignedShort();
                flags = this.readUnsignedShort();
                toCount = this.readUnsignedShort();
                to = new int[toCount];
                for (j = 0; j < toCount; ++j) {
                    to[j] = this.readUnsignedShort();
                }
                this.opens[i] = new Open(index, flags, to);
            }
            count = this.readUnsignedShort();
            this.uses = new Uses[count];
            for (i = 0; i < count; ++i) {
                this.uses[i] = new Uses(this.readUnsignedShort());
            }
            count = this.readUnsignedShort();
            this.provides = new Provide[count];
            for (i = 0; i < count; ++i) {
                index = this.readUnsignedShort();
                int toCount2 = this.readUnsignedShort();
                int[] to2 = new int[toCount2];
                for (int j2 = 0; j2 < toCount2; ++j2) {
                    to2[j2] = this.readUnsignedShort();
                }
                this.provides[i] = new Provide(index, to2);
            }
            this.unpacked = true;
        }
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        if (!this.unpacked) {
            file.write(this.moduleInfo);
        } else {
            int[] toIndices;
            file.writeShort(this.moduleNameIndex);
            file.writeShort(this.moduleFlags);
            file.writeShort(this.moduleVersionIndex);
            file.writeShort(this.requires.length);
            for (int i = 0; i < this.requires.length; ++i) {
                file.writeShort(this.requires[i].moduleIndex);
                file.writeShort(this.requires[i].flags);
                file.writeShort(this.requires[i].versionIndex);
            }
            file.writeShort(this.exports.length);
            for (Export export : this.exports) {
                file.writeShort(export.packageIndex);
                toIndices = export.toModuleIndices;
                file.writeShort(toIndices.length);
                for (int index : toIndices) {
                    file.writeShort(index);
                }
            }
            file.writeShort(this.opens.length);
            for (Open open : this.opens) {
                file.writeShort(open.packageIndex);
                toIndices = open.toModuleIndices;
                file.writeShort(toIndices.length);
                for (int index : toIndices) {
                    file.writeShort(index);
                }
            }
            file.writeShort(this.uses.length);
            for (Uses use : this.uses) {
                file.writeShort(use.getTypeNameIndex());
            }
            file.writeShort(this.provides.length);
            for (Provide provide : this.provides) {
                file.writeShort(provide.providedTypeIndex);
                toIndices = provide.withTypeIndices;
                file.writeShort(toIndices.length);
                for (int index : toIndices) {
                    file.writeShort(index);
                }
            }
        }
    }

    public String toStringRequires() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(this.requires.length);
        if (this.requires.length > 0) {
            for (Require require : this.requires) {
                s.append(' ');
                s.append(require.moduleIndex).append(':').append(require.flags);
            }
        }
        return s.toString();
    }

    public String toStringExports() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(this.exports.length);
        if (this.exports.length > 0) {
            for (Export export : this.exports) {
                s.append(' ');
                s.append(export.packageIndex).append(":[");
                int[] toIndices = export.toModuleIndices;
                for (int i = 0; i < toIndices.length; ++i) {
                    if (i > 0) {
                        s.append(',');
                    }
                    s.append(toIndices[i]);
                }
                s.append("]");
            }
        }
        return s.toString();
    }

    public String toStringOpens() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(this.opens.length);
        if (this.opens.length > 0) {
            for (Open open : this.opens) {
                s.append(' ');
                s.append(open.packageIndex).append(":[");
                int[] toIndices = open.toModuleIndices;
                for (int i = 0; i < toIndices.length; ++i) {
                    if (i > 0) {
                        s.append(',');
                    }
                    s.append(toIndices[i]);
                }
                s.append("]");
            }
        }
        return s.toString();
    }

    public String toStringUses() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(this.uses.length);
        if (this.uses.length > 0) {
            for (Uses use : this.uses) {
                s.append(' ');
                s.append(use.getTypeName());
            }
        }
        return s.toString();
    }

    public String toStringProvides() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(this.provides.length);
        if (this.provides.length > 0) {
            for (Provide provide : this.provides) {
                s.append(' ');
                s.append(provide.providedTypeIndex).append(":[");
                int[] indices = provide.withTypeIndices;
                for (int i = 0; i < indices.length; ++i) {
                    if (i > 0) {
                        s.append(',');
                    }
                    s.append(indices[i]);
                }
                s.append("]");
            }
        }
        return s.toString();
    }

    @Override
    public final String toString() {
        StringBuilder s = new StringBuilder();
        this.ensureUnpacked();
        s.append("Module(");
        if (this.requires.length != 0) {
            s.append("requires=");
            s.append(this.toStringRequires());
            s.append(" ");
        }
        if (this.exports.length != 0) {
            s.append("exports=");
            s.append(this.toStringExports());
            s.append(" ");
        }
        if (this.opens.length != 0) {
            s.append("opens=");
            s.append(this.toStringOpens());
            s.append(" ");
        }
        if (this.uses.length != 0) {
            s.append("uses=");
            s.append(this.toStringUses());
            s.append(" ");
        }
        if (this.provides.length != 0) {
            s.append("provides=");
            s.append(this.toStringProvides());
            s.append(" ");
        }
        return s.toString().trim() + ")";
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitModule(this);
    }

    public Require[] getRequires() {
        this.ensureUnpacked();
        return this.requires;
    }

    public String[] getRequiredModuleNames() {
        this.ensureUnpacked();
        String[] results = new String[this.requires.length];
        for (int i = 0; i < this.requires.length; ++i) {
            results[i] = this.cpool.getModuleName(this.requires[i].moduleIndex);
        }
        return results;
    }

    public byte[] getBytes() {
        return this.moduleInfo;
    }

    public Export[] getExports() {
        this.ensureUnpacked();
        return this.exports;
    }

    public Open[] getOpens() {
        this.ensureUnpacked();
        return this.opens;
    }

    public Uses[] getUses() {
        this.ensureUnpacked();
        return this.uses;
    }

    public Provide[] getProvides() {
        this.ensureUnpacked();
        return this.provides;
    }

    public String getModuleName() {
        return ((ConstantModule)this.cpool.getConstant(this.moduleNameIndex)).getModuleName(this.cpool);
    }

    public int getModuleFlags() {
        return this.moduleFlags;
    }

    public String getModuleVersion() {
        if (this.moduleVersionIndex == 0) {
            return null;
        }
        return this.cpool.getConstantUtf8(this.moduleVersionIndex).getValue();
    }

    public class Uses {
        private final int typeNameIndex;

        public Uses(int typeNameIndex) {
            this.typeNameIndex = typeNameIndex;
        }

        public String getTypeName() {
            return Module.this.cpool.getConstantString_CONSTANTClass(this.typeNameIndex);
        }

        public int getTypeNameIndex() {
            return this.typeNameIndex;
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("uses ").append(this.getTypeName().replace('/', '.'));
            return s.toString().trim();
        }
    }

    public class Provide {
        private final int providedTypeIndex;
        private final int[] withTypeIndices;

        public Provide(int providedTypeIndex, int[] withTypeIndices) {
            this.providedTypeIndex = providedTypeIndex;
            this.withTypeIndices = withTypeIndices;
        }

        public String getProvidedType() {
            return Module.this.cpool.getConstantString_CONSTANTClass(this.providedTypeIndex);
        }

        public int getProvidedTypeIndex() {
            return this.providedTypeIndex;
        }

        public String[] getWithTypeStrings() {
            String[] result = new String[this.withTypeIndices.length];
            for (int i = 0; i < this.withTypeIndices.length; ++i) {
                result[i] = Module.this.cpool.getConstantString_CONSTANTClass(this.withTypeIndices[i]);
            }
            return result;
        }

        public int[] getWithTypeIndices() {
            return this.withTypeIndices;
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("provides ").append(this.getProvidedType().replace('/', '.'));
            s.append(" with ");
            String[] withtypes = this.getWithTypeStrings();
            for (int i = 0; i < withtypes.length; ++i) {
                if (i > 0) {
                    s.append(",");
                }
                s.append(withtypes[i].replace('/', '.'));
            }
            return s.toString();
        }
    }

    public class Open {
        private final int packageIndex;
        private final int flags;
        private final int[] toModuleIndices;

        public Open(int packageIndex, int flags, int[] toModuleIndices) {
            this.packageIndex = packageIndex;
            this.flags = flags;
            this.toModuleIndices = toModuleIndices;
        }

        public int getPackageIndex() {
            return this.packageIndex;
        }

        public int getFlags() {
            return this.flags;
        }

        public int[] getToModuleIndices() {
            return this.toModuleIndices;
        }

        public String getPackage() {
            return Module.this.cpool.getPackageName(this.packageIndex);
        }

        public String getFlagsAsString() {
            StringBuilder s = new StringBuilder();
            if ((this.flags & 0x1000) != 0) {
                s.append(" synthetic");
            }
            if ((this.flags & 0x8000) != 0) {
                s.append(" synthetic");
            }
            return s.toString();
        }

        public String[] getToModuleNames() {
            if (this.toModuleIndices == null) {
                return NO_MODULE_NAMES;
            }
            String[] toModuleNames = new String[this.toModuleIndices.length];
            for (int i = 0; i < this.toModuleIndices.length; ++i) {
                toModuleNames[i] = Module.this.cpool.getModuleName(this.toModuleIndices[i]);
            }
            return toModuleNames;
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("opens").append(this.getFlagsAsString()).append(" ").append(this.getPackage().replace('/', '.'));
            String[] toModules = this.getToModuleNames();
            if (toModules.length != 0) {
                s.append(" to ");
                for (int i = 0; i < toModules.length; ++i) {
                    if (i > 0) {
                        s.append(", ");
                    }
                    s.append(toModules[i]);
                }
            }
            return s.toString().trim();
        }
    }

    public class Export {
        private final int packageIndex;
        private final int flags;
        private final int[] toModuleIndices;

        public Export(int packageIndex, int flags, int[] toModuleIndices) {
            this.packageIndex = packageIndex;
            this.flags = flags;
            this.toModuleIndices = toModuleIndices;
        }

        public int getPackageIndex() {
            return this.packageIndex;
        }

        public int getFlags() {
            return this.flags;
        }

        public int[] getToModuleIndices() {
            return this.toModuleIndices;
        }

        public String getPackage() {
            return Module.this.cpool.getPackageName(this.packageIndex);
        }

        public String getFlagsAsString() {
            StringBuilder s = new StringBuilder();
            if ((this.flags & 0x1000) != 0) {
                s.append(" synthetic");
            }
            if ((this.flags & 0x8000) != 0) {
                s.append(" synthetic");
            }
            return s.toString();
        }

        public String[] getToModuleNames() {
            if (this.toModuleIndices == null) {
                return NO_MODULE_NAMES;
            }
            String[] toModuleNames = new String[this.toModuleIndices.length];
            for (int i = 0; i < this.toModuleIndices.length; ++i) {
                toModuleNames[i] = Module.this.cpool.getModuleName(this.toModuleIndices[i]);
            }
            return toModuleNames;
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("exports").append(this.getFlagsAsString()).append(" ").append(this.getPackage().replace('/', '.'));
            String[] toModules = this.getToModuleNames();
            if (toModules.length != 0) {
                s.append(" to ");
                for (int i = 0; i < toModules.length; ++i) {
                    if (i > 0) {
                        s.append(", ");
                    }
                    s.append(toModules[i]);
                }
            }
            return s.toString().trim();
        }
    }

    public class Require {
        private final int moduleIndex;
        private final int flags;
        private final int versionIndex;

        public Require(int moduleIndex, int flags, int versionIndex) {
            this.moduleIndex = moduleIndex;
            this.flags = flags;
            this.versionIndex = versionIndex;
        }

        public String getModuleName() {
            return Module.this.cpool.getModuleName(this.moduleIndex);
        }

        public int getFlags() {
            return this.flags;
        }

        public int getVersionIndex() {
            return this.versionIndex;
        }

        public String getVersionString() {
            if (this.versionIndex == 0) {
                return null;
            }
            return Module.this.cpool.getConstantUtf8(this.versionIndex).getValue();
        }

        public String getFlagsAsString() {
            StringBuilder s = new StringBuilder();
            if ((this.flags & 0x20) != 0) {
                s.append(" transitive");
            }
            if ((this.flags & 0x40) != 0) {
                s.append(" static");
            }
            if ((this.flags & 0x1000) != 0) {
                s.append(" synthetic");
            }
            if ((this.flags & 0x8000) != 0) {
                s.append(" mandated");
            }
            return s.toString();
        }

        public String toString() {
            return "requires" + this.getFlagsAsString() + " " + this.getModuleName() + (this.versionIndex == 0 ? "" : " " + this.getVersionString());
        }
    }
}

