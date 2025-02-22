/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm.tree;

import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.tree.AbstractInsnNode;
import groovyjarjarasm.asm.tree.LabelNode;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TypeInsnNode
extends AbstractInsnNode {
    public String desc;

    public TypeInsnNode(int opcode, String desc) {
        super(opcode);
        this.desc = desc;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public void accept(MethodVisitor mv) {
        mv.visitTypeInsn(this.opcode, this.desc);
        this.acceptAnnotations(mv);
    }

    @Override
    public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels) {
        return new TypeInsnNode(this.opcode, this.desc).cloneAnnotations(this);
    }
}

