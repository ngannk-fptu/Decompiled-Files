/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovyjarjarasm.asm.tree.analysis.Analyzer
 *  groovyjarjarasm.asm.tree.analysis.Interpreter
 *  groovyjarjarasm.asm.tree.analysis.SimpleVerifier
 *  groovyjarjarasm.asm.util.CheckClassAdapter
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.taskdefs.MatchingTask
 */
package org.codehaus.groovy.ant;

import groovyjarjarasm.asm.ClassReader;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.tree.AbstractInsnNode;
import groovyjarjarasm.asm.tree.ClassNode;
import groovyjarjarasm.asm.tree.MethodNode;
import groovyjarjarasm.asm.tree.analysis.Analyzer;
import groovyjarjarasm.asm.tree.analysis.Interpreter;
import groovyjarjarasm.asm.tree.analysis.SimpleVerifier;
import groovyjarjarasm.asm.util.CheckClassAdapter;
import groovyjarjarasm.asm.util.TraceMethodVisitor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class VerifyClass
extends MatchingTask {
    private String topDir = null;
    private boolean verbose = false;

    public void execute() throws BuildException {
        if (this.topDir == null) {
            throw new BuildException("no dir attribute is set");
        }
        File top = new File(this.topDir);
        if (!top.exists()) {
            throw new BuildException("the directory " + top + " does not exist");
        }
        this.log("top dir is " + top);
        int fails = this.execute(top);
        if (fails == 0) {
            this.log("no bytecode problems found");
        } else {
            this.log("found " + fails + " failing classes");
        }
    }

    public void setDir(String dir) throws BuildException {
        this.topDir = dir;
    }

    public void setVerbose(boolean v) {
        this.verbose = v;
    }

    private int execute(File dir) {
        int fails = 0;
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            File f = files[i];
            if (f.isDirectory()) {
                fails += this.execute(f);
                continue;
            }
            if (!f.getName().endsWith(".class")) continue;
            try {
                boolean ok = this.readClass(f.getCanonicalPath());
                if (ok) continue;
                ++fails;
                continue;
            }
            catch (IOException ioe) {
                this.log(ioe.getMessage());
                throw new BuildException((Throwable)ioe);
            }
        }
        return fails;
    }

    private boolean readClass(String clazz) throws IOException {
        ClassReader cr = new ClassReader(new FileInputStream(clazz));
        ClassNode ca = new ClassNode(){

            @Override
            public void visitEnd() {
            }
        };
        cr.accept((ClassVisitor)new CheckClassAdapter((ClassVisitor)ca), 1);
        boolean failed = false;
        List<MethodNode> methods = ca.methods;
        for (int i = 0; i < methods.size(); ++i) {
            MethodNode method = methods.get(i);
            if (method.instructions.size() <= 0) continue;
            Analyzer a = new Analyzer((Interpreter)new SimpleVerifier());
            try {
                a.analyze(ca.name, method);
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
                if (!failed) {
                    failed = true;
                    this.log("verifying of class " + clazz + " failed");
                }
                if (this.verbose) {
                    this.log(method.name + method.desc);
                }
                TraceMethodVisitor mv = new TraceMethodVisitor(null);
                for (int j = 0; j < method.instructions.size(); ++j) {
                    AbstractInsnNode insn = method.instructions.get(j);
                    if (insn instanceof AbstractInsnNode) {
                        insn.accept(mv);
                        continue;
                    }
                    mv.visitLabel((Label)((Object)insn));
                }
                mv.visitMaxs(method.maxStack, method.maxLocals);
            }
        }
        return !failed;
    }
}

