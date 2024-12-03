/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm.commons;

import groovyjarjarasm.asm.Attribute;
import groovyjarjarasm.asm.ByteVector;
import groovyjarjarasm.asm.ClassReader;
import groovyjarjarasm.asm.ClassWriter;
import groovyjarjarasm.asm.Label;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ModuleHashesAttribute
extends Attribute {
    public String algorithm;
    public List<String> modules;
    public List<byte[]> hashes;

    public ModuleHashesAttribute(String algorithm, List<String> modules, List<byte[]> hashes) {
        super("ModuleHashes");
        this.algorithm = algorithm;
        this.modules = modules;
        this.hashes = hashes;
    }

    public ModuleHashesAttribute() {
        this(null, null, null);
    }

    @Override
    protected Attribute read(ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
        String hashAlgorithm = cr.readUTF8(off, buf);
        int count = cr.readUnsignedShort(off + 2);
        ArrayList<String> modules = new ArrayList<String>(count);
        ArrayList<byte[]> hashes = new ArrayList<byte[]>(count);
        off += 4;
        for (int i = 0; i < count; ++i) {
            String module = cr.readModule(off, buf);
            int hashLength = cr.readUnsignedShort(off + 2);
            off += 4;
            byte[] hash = new byte[hashLength];
            for (int j = 0; j < hashLength; ++j) {
                hash[j] = (byte)(cr.readByte(off + j) & 0xFF);
            }
            off += hashLength;
            modules.add(module);
            hashes.add(hash);
        }
        return new ModuleHashesAttribute(hashAlgorithm, modules, hashes);
    }

    @Override
    protected ByteVector write(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals) {
        ByteVector v = new ByteVector();
        int index = cw.newUTF8(this.algorithm);
        v.putShort(index);
        int count = this.modules == null ? 0 : this.modules.size();
        v.putShort(count);
        for (int i = 0; i < count; ++i) {
            String module = this.modules.get(i);
            v.putShort(cw.newModule(module));
            byte[] hash = this.hashes.get(i);
            v.putShort(hash.length);
            for (byte b : hash) {
                v.putByte(b);
            }
        }
        return v;
    }
}

