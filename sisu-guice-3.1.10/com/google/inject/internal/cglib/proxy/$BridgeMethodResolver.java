/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.asm.$ClassReader;
import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.$MethodVisitor;
import com.google.inject.internal.cglib.core.$Signature;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class $BridgeMethodResolver {
    private final Map declToBridge;

    public $BridgeMethodResolver(Map declToBridge) {
        this.declToBridge = declToBridge;
    }

    public Map resolveAll() {
        HashMap resolved = new HashMap();
        Iterator entryIter = this.declToBridge.entrySet().iterator();
        while (entryIter.hasNext()) {
            Map.Entry entry = entryIter.next();
            Class owner = (Class)entry.getKey();
            Set bridges = (Set)entry.getValue();
            try {
                new $ClassReader(owner.getName()).accept(new BridgedFinder(bridges, resolved), 6);
            }
            catch (IOException ignored) {}
        }
        return resolved;
    }

    private static class BridgedFinder
    extends $ClassVisitor {
        private Map resolved;
        private Set eligableMethods;
        private $Signature currentMethod = null;

        BridgedFinder(Set eligableMethods, Map resolved) {
            super(262144);
            this.resolved = resolved;
            this.eligableMethods = eligableMethods;
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        }

        public $MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            $Signature sig = new $Signature(name, desc);
            if (this.eligableMethods.remove(sig)) {
                this.currentMethod = sig;
                return new $MethodVisitor(262144){

                    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                        if (opcode == 183 && BridgedFinder.this.currentMethod != null) {
                            $Signature target = new $Signature(name, desc);
                            if (!target.equals(BridgedFinder.this.currentMethod)) {
                                BridgedFinder.this.resolved.put(BridgedFinder.this.currentMethod, target);
                            }
                            BridgedFinder.this.currentMethod = null;
                        }
                    }
                };
            }
            return null;
        }
    }
}

