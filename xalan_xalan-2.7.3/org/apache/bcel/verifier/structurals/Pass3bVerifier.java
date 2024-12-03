/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.JsrInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.ReturnaddressType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.PassVerifier;
import org.apache.bcel.verifier.VerificationResult;
import org.apache.bcel.verifier.Verifier;
import org.apache.bcel.verifier.exc.AssertionViolatedException;
import org.apache.bcel.verifier.exc.StructuralCodeConstraintException;
import org.apache.bcel.verifier.exc.VerifierConstraintViolatedException;
import org.apache.bcel.verifier.structurals.ControlFlowGraph;
import org.apache.bcel.verifier.structurals.ExceptionHandler;
import org.apache.bcel.verifier.structurals.ExecutionVisitor;
import org.apache.bcel.verifier.structurals.Frame;
import org.apache.bcel.verifier.structurals.InstConstraintVisitor;
import org.apache.bcel.verifier.structurals.InstructionContext;
import org.apache.bcel.verifier.structurals.OperandStack;
import org.apache.bcel.verifier.structurals.UninitializedObjectType;

public final class Pass3bVerifier
extends PassVerifier {
    private static final boolean DEBUG = true;
    private final Verifier myOwner;
    private final int methodNo;

    public Pass3bVerifier(Verifier myOwner, int methodNo) {
        this.myOwner = myOwner;
        this.methodNo = methodNo;
    }

    /*
     * Unable to fully structure code
     */
    private void circulationPump(MethodGen m, ControlFlowGraph cfg, InstructionContext start, Frame vanillaFrame, InstConstraintVisitor icv, ExecutionVisitor ev) {
        random = new Random();
        icq = new InstructionContextQueue();
        start.execute(vanillaFrame, new ArrayList<InstructionContext>(), icv, ev);
        icq.add(start, new ArrayList<InstructionContext>());
        while (!icq.isEmpty()) {
            u = icq.getIC(0);
            ec = icq.getEC(0);
            icq.remove(0);
            oldchain = (ArrayList)ec.clone();
            newchain = (ArrayList)ec.clone();
            newchain.add(u);
            if (u.getInstruction().getInstruction() instanceof RET) {
                ret = (RET)u.getInstruction().getInstruction();
                t = (ReturnaddressType)u.getOutFrame(oldchain).getLocals().get(ret.getIndex());
                theSuccessor = cfg.contextOf(t.getTarget());
                lastJSR = null;
                skipJsr = 0;
                for (ss = oldchain.size() - 1; ss >= 0; --ss) {
                    if (skipJsr < 0) {
                        throw new AssertionViolatedException("More RET than JSR in execution chain?!");
                    }
                    if (((InstructionContext)oldchain.get(ss)).getInstruction().getInstruction() instanceof JsrInstruction) {
                        if (skipJsr == 0) {
                            lastJSR = (InstructionContext)oldchain.get(ss);
                            break;
                        }
                        --skipJsr;
                    }
                    if (!(((InstructionContext)oldchain.get(ss)).getInstruction().getInstruction() instanceof RET)) continue;
                    ++skipJsr;
                }
                if (lastJSR == null) {
                    throw new AssertionViolatedException("RET without a JSR before in ExecutionChain?! EC: '" + oldchain + "'.");
                }
                jsr = (JsrInstruction)lastJSR.getInstruction().getInstruction();
                if (theSuccessor != cfg.contextOf(jsr.physicalSuccessor())) {
                    throw new AssertionViolatedException("RET '" + u.getInstruction() + "' info inconsistent: jump back to '" + theSuccessor + "' or '" + cfg.contextOf(jsr.physicalSuccessor()) + "'?");
                }
                if (theSuccessor.execute(u.getOutFrame(oldchain), newchain, icv, ev)) {
                    newchainClone = (ArrayList)newchain.clone();
                    icq.add(theSuccessor, newchainClone);
                }
            } else {
                succs = u.getSuccessors();
                for (InstructionContext v : succs) {
                    if (!v.execute(u.getOutFrame(oldchain), newchain, icv, ev)) continue;
                    newchainClone = (ArrayList)newchain.clone();
                    icq.add(v, newchainClone);
                }
            }
            excHds = u.getExceptionHandlers();
            for (ExceptionHandler excHd : excHds) {
                v = cfg.contextOf(excHd.getHandlerStart());
                if (!v.execute(new Frame(u.getOutFrame(oldchain).getLocals(), new OperandStack(u.getOutFrame(oldchain).getStack().maxStack(), excHd.getExceptionType() == null ? Type.THROWABLE : excHd.getExceptionType())), new ArrayList<InstructionContext>(), icv, ev)) continue;
                icq.add(v, new ArrayList<InstructionContext>());
            }
        }
        ih = start.getInstruction();
        do {
            if (!(ih.getInstruction() instanceof ReturnInstruction) || cfg.isDead(ih)) continue;
            ic = cfg.contextOf(ih);
            f = ic.getOutFrame(new ArrayList<InstructionContext>());
            lvs = f.getLocals();
            for (i = 0; i < lvs.maxLocals(); ++i) {
                if (!(lvs.get(i) instanceof UninitializedObjectType)) continue;
                this.addMessage("Warning: ReturnInstruction '" + ic + "' may leave method with an uninitialized object in the local variables array '" + lvs + "'.");
            }
            os = f.getStack();
            for (i = 0; i < os.size(); ++i) {
                if (!(os.peek(i) instanceof UninitializedObjectType)) continue;
                this.addMessage("Warning: ReturnInstruction '" + ic + "' may leave method with an uninitialized object on the operand stack '" + os + "'.");
            }
            returnedType = null;
            inStack = ic.getInFrame().getStack();
            returnedType = inStack.size() >= 1 ? inStack.peek() : Type.VOID;
            if (returnedType == null) continue;
            if (returnedType instanceof ReferenceType) {
                try {
                    if (((ReferenceType)returnedType).isCastableTo(m.getReturnType())) ** GOTO lbl81
                    this.invalidReturnTypeError(returnedType, m);
                }
                catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                if (returnedType.equals(m.getReturnType().normalizeForStackOrLocal())) continue;
                this.invalidReturnTypeError(returnedType, m);
            }
lbl81:
            // 6 sources

        } while ((ih = ih.getNext()) != null);
    }

    @Override
    public VerificationResult do_verify() {
        JavaClass jc;
        if (!this.myOwner.doPass3a(this.methodNo).equals(VerificationResult.VR_OK)) {
            return VerificationResult.VR_NOTYET;
        }
        try {
            jc = Repository.lookupClass(this.myOwner.getClassName());
        }
        catch (ClassNotFoundException e) {
            throw new AssertionViolatedException("Missing class: " + e, e);
        }
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(jc.getConstantPool());
        InstConstraintVisitor icv = new InstConstraintVisitor();
        icv.setConstantPoolGen(constantPoolGen);
        ExecutionVisitor ev = new ExecutionVisitor();
        ev.setConstantPoolGen(constantPoolGen);
        Method[] methods = jc.getMethods();
        try {
            MethodGen mg = new MethodGen(methods[this.methodNo], this.myOwner.getClassName(), constantPoolGen);
            icv.setMethodGen(mg);
            if (!mg.isAbstract() && !mg.isNative()) {
                ControlFlowGraph cfg = new ControlFlowGraph(mg);
                Frame f = new Frame(mg.getMaxLocals(), mg.getMaxStack());
                if (!mg.isStatic()) {
                    if (mg.getName().equals("<init>")) {
                        Frame.setThis(new UninitializedObjectType(ObjectType.getInstance(jc.getClassName())));
                        f.getLocals().set(0, Frame.getThis());
                    } else {
                        Frame.setThis(null);
                        f.getLocals().set(0, ObjectType.getInstance(jc.getClassName()));
                    }
                }
                Type[] argtypes = mg.getArgumentTypes();
                int twoslotoffset = 0;
                for (int j = 0; j < argtypes.length; ++j) {
                    if (argtypes[j] == Type.SHORT || argtypes[j] == Type.BYTE || argtypes[j] == Type.CHAR || argtypes[j] == Type.BOOLEAN) {
                        argtypes[j] = Type.INT;
                    }
                    f.getLocals().set(twoslotoffset + j + (mg.isStatic() ? 0 : 1), argtypes[j]);
                    if (argtypes[j].getSize() != 2) continue;
                    f.getLocals().set(++twoslotoffset + j + (mg.isStatic() ? 0 : 1), Type.UNKNOWN);
                }
                this.circulationPump(mg, cfg, cfg.contextOf(mg.getInstructionList().getStart()), f, icv, ev);
            }
        }
        catch (VerifierConstraintViolatedException ce) {
            ce.extendMessage("Constraint violated in method '" + methods[this.methodNo] + "':\n", "");
            return new VerificationResult(2, ce.getMessage());
        }
        catch (RuntimeException re) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            re.printStackTrace(pw);
            throw new AssertionViolatedException("Some RuntimeException occurred while verify()ing class '" + jc.getClassName() + "', method '" + methods[this.methodNo] + "'. Original RuntimeException's stack trace:\n---\n" + sw + "---\n", re);
        }
        return VerificationResult.VR_OK;
    }

    public int getMethodNo() {
        return this.methodNo;
    }

    public void invalidReturnTypeError(Type returnedType, MethodGen m) {
        throw new StructuralCodeConstraintException("Returned type " + returnedType + " does not match Method's return type " + m.getReturnType());
    }

    private static final class InstructionContextQueue {
        private final List<InstructionContext> ics = new Vector<InstructionContext>();
        private final List<ArrayList<InstructionContext>> ecs = new Vector<ArrayList<InstructionContext>>();

        private InstructionContextQueue() {
        }

        public void add(InstructionContext ic, ArrayList<InstructionContext> executionChain) {
            this.ics.add(ic);
            this.ecs.add(executionChain);
        }

        public ArrayList<InstructionContext> getEC(int i) {
            return this.ecs.get(i);
        }

        public InstructionContext getIC(int i) {
            return this.ics.get(i);
        }

        public boolean isEmpty() {
            return this.ics.isEmpty();
        }

        public void remove(int i) {
            this.ics.remove(i);
            this.ecs.remove(i);
        }

        public int size() {
            return this.ics.size();
        }
    }
}

