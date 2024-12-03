/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.AALOAD;
import org.apache.bcel.generic.AASTORE;
import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ARETURN;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.AllocationInstruction;
import org.apache.bcel.generic.ArithmeticInstruction;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.BALOAD;
import org.apache.bcel.generic.BASTORE;
import org.apache.bcel.generic.BIPUSH;
import org.apache.bcel.generic.BREAKPOINT;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.CALOAD;
import org.apache.bcel.generic.CASTORE;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.ConversionInstruction;
import org.apache.bcel.generic.D2F;
import org.apache.bcel.generic.D2I;
import org.apache.bcel.generic.D2L;
import org.apache.bcel.generic.DADD;
import org.apache.bcel.generic.DALOAD;
import org.apache.bcel.generic.DASTORE;
import org.apache.bcel.generic.DCMPG;
import org.apache.bcel.generic.DCMPL;
import org.apache.bcel.generic.DCONST;
import org.apache.bcel.generic.DDIV;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DMUL;
import org.apache.bcel.generic.DNEG;
import org.apache.bcel.generic.DREM;
import org.apache.bcel.generic.DRETURN;
import org.apache.bcel.generic.DSTORE;
import org.apache.bcel.generic.DSUB;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.DUP2;
import org.apache.bcel.generic.DUP2_X1;
import org.apache.bcel.generic.DUP2_X2;
import org.apache.bcel.generic.DUP_X1;
import org.apache.bcel.generic.DUP_X2;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.F2D;
import org.apache.bcel.generic.F2I;
import org.apache.bcel.generic.F2L;
import org.apache.bcel.generic.FADD;
import org.apache.bcel.generic.FALOAD;
import org.apache.bcel.generic.FASTORE;
import org.apache.bcel.generic.FCMPG;
import org.apache.bcel.generic.FCMPL;
import org.apache.bcel.generic.FCONST;
import org.apache.bcel.generic.FDIV;
import org.apache.bcel.generic.FLOAD;
import org.apache.bcel.generic.FMUL;
import org.apache.bcel.generic.FNEG;
import org.apache.bcel.generic.FREM;
import org.apache.bcel.generic.FRETURN;
import org.apache.bcel.generic.FSTORE;
import org.apache.bcel.generic.FSUB;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.FieldOrMethod;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.GOTO_W;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.I2B;
import org.apache.bcel.generic.I2C;
import org.apache.bcel.generic.I2D;
import org.apache.bcel.generic.I2F;
import org.apache.bcel.generic.I2L;
import org.apache.bcel.generic.I2S;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.IALOAD;
import org.apache.bcel.generic.IAND;
import org.apache.bcel.generic.IASTORE;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.IDIV;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IFGE;
import org.apache.bcel.generic.IFGT;
import org.apache.bcel.generic.IFLE;
import org.apache.bcel.generic.IFLT;
import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.IFNONNULL;
import org.apache.bcel.generic.IFNULL;
import org.apache.bcel.generic.IF_ACMPEQ;
import org.apache.bcel.generic.IF_ACMPNE;
import org.apache.bcel.generic.IF_ICMPEQ;
import org.apache.bcel.generic.IF_ICMPGE;
import org.apache.bcel.generic.IF_ICMPGT;
import org.apache.bcel.generic.IF_ICMPLE;
import org.apache.bcel.generic.IF_ICMPLT;
import org.apache.bcel.generic.IF_ICMPNE;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.IMPDEP1;
import org.apache.bcel.generic.IMPDEP2;
import org.apache.bcel.generic.IMUL;
import org.apache.bcel.generic.INEG;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.INVOKEDYNAMIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.IOR;
import org.apache.bcel.generic.IREM;
import org.apache.bcel.generic.IRETURN;
import org.apache.bcel.generic.ISHL;
import org.apache.bcel.generic.ISHR;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.ISUB;
import org.apache.bcel.generic.IUSHR;
import org.apache.bcel.generic.IXOR;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.JSR;
import org.apache.bcel.generic.JSR_W;
import org.apache.bcel.generic.JsrInstruction;
import org.apache.bcel.generic.L2D;
import org.apache.bcel.generic.L2F;
import org.apache.bcel.generic.L2I;
import org.apache.bcel.generic.LADD;
import org.apache.bcel.generic.LALOAD;
import org.apache.bcel.generic.LAND;
import org.apache.bcel.generic.LASTORE;
import org.apache.bcel.generic.LCMP;
import org.apache.bcel.generic.LCONST;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.LDIV;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LMUL;
import org.apache.bcel.generic.LNEG;
import org.apache.bcel.generic.LOOKUPSWITCH;
import org.apache.bcel.generic.LOR;
import org.apache.bcel.generic.LREM;
import org.apache.bcel.generic.LRETURN;
import org.apache.bcel.generic.LSHL;
import org.apache.bcel.generic.LSHR;
import org.apache.bcel.generic.LSTORE;
import org.apache.bcel.generic.LSUB;
import org.apache.bcel.generic.LUSHR;
import org.apache.bcel.generic.LXOR;
import org.apache.bcel.generic.LoadClass;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MONITORENTER;
import org.apache.bcel.generic.MONITOREXIT;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.NOP;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.POP2;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.PopInstruction;
import org.apache.bcel.generic.PushInstruction;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.SALOAD;
import org.apache.bcel.generic.SASTORE;
import org.apache.bcel.generic.SIPUSH;
import org.apache.bcel.generic.SWAP;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.StackConsumer;
import org.apache.bcel.generic.StackInstruction;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.StoreInstruction;
import org.apache.bcel.generic.TABLESWITCH;
import org.apache.bcel.generic.TypedInstruction;
import org.apache.bcel.generic.UnconditionalBranch;
import org.apache.bcel.generic.VariableLengthInstruction;
import org.apache.bcel.generic.Visitor;

public abstract class EmptyVisitor
implements Visitor {
    @Override
    public void visitAALOAD(AALOAD obj) {
    }

    @Override
    public void visitAASTORE(AASTORE obj) {
    }

    @Override
    public void visitACONST_NULL(ACONST_NULL obj) {
    }

    @Override
    public void visitAllocationInstruction(AllocationInstruction obj) {
    }

    @Override
    public void visitALOAD(ALOAD obj) {
    }

    @Override
    public void visitANEWARRAY(ANEWARRAY obj) {
    }

    @Override
    public void visitARETURN(ARETURN obj) {
    }

    @Override
    public void visitArithmeticInstruction(ArithmeticInstruction obj) {
    }

    @Override
    public void visitArrayInstruction(ArrayInstruction obj) {
    }

    @Override
    public void visitARRAYLENGTH(ARRAYLENGTH obj) {
    }

    @Override
    public void visitASTORE(ASTORE obj) {
    }

    @Override
    public void visitATHROW(ATHROW obj) {
    }

    @Override
    public void visitBALOAD(BALOAD obj) {
    }

    @Override
    public void visitBASTORE(BASTORE obj) {
    }

    @Override
    public void visitBIPUSH(BIPUSH obj) {
    }

    @Override
    public void visitBranchInstruction(BranchInstruction obj) {
    }

    @Override
    public void visitBREAKPOINT(BREAKPOINT obj) {
    }

    @Override
    public void visitCALOAD(CALOAD obj) {
    }

    @Override
    public void visitCASTORE(CASTORE obj) {
    }

    @Override
    public void visitCHECKCAST(CHECKCAST obj) {
    }

    @Override
    public void visitConstantPushInstruction(ConstantPushInstruction obj) {
    }

    @Override
    public void visitConversionInstruction(ConversionInstruction obj) {
    }

    @Override
    public void visitCPInstruction(CPInstruction obj) {
    }

    @Override
    public void visitD2F(D2F obj) {
    }

    @Override
    public void visitD2I(D2I obj) {
    }

    @Override
    public void visitD2L(D2L obj) {
    }

    @Override
    public void visitDADD(DADD obj) {
    }

    @Override
    public void visitDALOAD(DALOAD obj) {
    }

    @Override
    public void visitDASTORE(DASTORE obj) {
    }

    @Override
    public void visitDCMPG(DCMPG obj) {
    }

    @Override
    public void visitDCMPL(DCMPL obj) {
    }

    @Override
    public void visitDCONST(DCONST obj) {
    }

    @Override
    public void visitDDIV(DDIV obj) {
    }

    @Override
    public void visitDLOAD(DLOAD obj) {
    }

    @Override
    public void visitDMUL(DMUL obj) {
    }

    @Override
    public void visitDNEG(DNEG obj) {
    }

    @Override
    public void visitDREM(DREM obj) {
    }

    @Override
    public void visitDRETURN(DRETURN obj) {
    }

    @Override
    public void visitDSTORE(DSTORE obj) {
    }

    @Override
    public void visitDSUB(DSUB obj) {
    }

    @Override
    public void visitDUP(DUP obj) {
    }

    @Override
    public void visitDUP_X1(DUP_X1 obj) {
    }

    @Override
    public void visitDUP_X2(DUP_X2 obj) {
    }

    @Override
    public void visitDUP2(DUP2 obj) {
    }

    @Override
    public void visitDUP2_X1(DUP2_X1 obj) {
    }

    @Override
    public void visitDUP2_X2(DUP2_X2 obj) {
    }

    @Override
    public void visitExceptionThrower(ExceptionThrower obj) {
    }

    @Override
    public void visitF2D(F2D obj) {
    }

    @Override
    public void visitF2I(F2I obj) {
    }

    @Override
    public void visitF2L(F2L obj) {
    }

    @Override
    public void visitFADD(FADD obj) {
    }

    @Override
    public void visitFALOAD(FALOAD obj) {
    }

    @Override
    public void visitFASTORE(FASTORE obj) {
    }

    @Override
    public void visitFCMPG(FCMPG obj) {
    }

    @Override
    public void visitFCMPL(FCMPL obj) {
    }

    @Override
    public void visitFCONST(FCONST obj) {
    }

    @Override
    public void visitFDIV(FDIV obj) {
    }

    @Override
    public void visitFieldInstruction(FieldInstruction obj) {
    }

    @Override
    public void visitFieldOrMethod(FieldOrMethod obj) {
    }

    @Override
    public void visitFLOAD(FLOAD obj) {
    }

    @Override
    public void visitFMUL(FMUL obj) {
    }

    @Override
    public void visitFNEG(FNEG obj) {
    }

    @Override
    public void visitFREM(FREM obj) {
    }

    @Override
    public void visitFRETURN(FRETURN obj) {
    }

    @Override
    public void visitFSTORE(FSTORE obj) {
    }

    @Override
    public void visitFSUB(FSUB obj) {
    }

    @Override
    public void visitGETFIELD(GETFIELD obj) {
    }

    @Override
    public void visitGETSTATIC(GETSTATIC obj) {
    }

    @Override
    public void visitGOTO(GOTO obj) {
    }

    @Override
    public void visitGOTO_W(GOTO_W obj) {
    }

    @Override
    public void visitGotoInstruction(GotoInstruction obj) {
    }

    @Override
    public void visitI2B(I2B obj) {
    }

    @Override
    public void visitI2C(I2C obj) {
    }

    @Override
    public void visitI2D(I2D obj) {
    }

    @Override
    public void visitI2F(I2F obj) {
    }

    @Override
    public void visitI2L(I2L obj) {
    }

    @Override
    public void visitI2S(I2S obj) {
    }

    @Override
    public void visitIADD(IADD obj) {
    }

    @Override
    public void visitIALOAD(IALOAD obj) {
    }

    @Override
    public void visitIAND(IAND obj) {
    }

    @Override
    public void visitIASTORE(IASTORE obj) {
    }

    @Override
    public void visitICONST(ICONST obj) {
    }

    @Override
    public void visitIDIV(IDIV obj) {
    }

    @Override
    public void visitIF_ACMPEQ(IF_ACMPEQ obj) {
    }

    @Override
    public void visitIF_ACMPNE(IF_ACMPNE obj) {
    }

    @Override
    public void visitIF_ICMPEQ(IF_ICMPEQ obj) {
    }

    @Override
    public void visitIF_ICMPGE(IF_ICMPGE obj) {
    }

    @Override
    public void visitIF_ICMPGT(IF_ICMPGT obj) {
    }

    @Override
    public void visitIF_ICMPLE(IF_ICMPLE obj) {
    }

    @Override
    public void visitIF_ICMPLT(IF_ICMPLT obj) {
    }

    @Override
    public void visitIF_ICMPNE(IF_ICMPNE obj) {
    }

    @Override
    public void visitIFEQ(IFEQ obj) {
    }

    @Override
    public void visitIFGE(IFGE obj) {
    }

    @Override
    public void visitIFGT(IFGT obj) {
    }

    @Override
    public void visitIfInstruction(IfInstruction obj) {
    }

    @Override
    public void visitIFLE(IFLE obj) {
    }

    @Override
    public void visitIFLT(IFLT obj) {
    }

    @Override
    public void visitIFNE(IFNE obj) {
    }

    @Override
    public void visitIFNONNULL(IFNONNULL obj) {
    }

    @Override
    public void visitIFNULL(IFNULL obj) {
    }

    @Override
    public void visitIINC(IINC obj) {
    }

    @Override
    public void visitILOAD(ILOAD obj) {
    }

    @Override
    public void visitIMPDEP1(IMPDEP1 obj) {
    }

    @Override
    public void visitIMPDEP2(IMPDEP2 obj) {
    }

    @Override
    public void visitIMUL(IMUL obj) {
    }

    @Override
    public void visitINEG(INEG obj) {
    }

    @Override
    public void visitINSTANCEOF(INSTANCEOF obj) {
    }

    @Override
    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC obj) {
    }

    @Override
    public void visitInvokeInstruction(InvokeInstruction obj) {
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE obj) {
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL obj) {
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC obj) {
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL obj) {
    }

    @Override
    public void visitIOR(IOR obj) {
    }

    @Override
    public void visitIREM(IREM obj) {
    }

    @Override
    public void visitIRETURN(IRETURN obj) {
    }

    @Override
    public void visitISHL(ISHL obj) {
    }

    @Override
    public void visitISHR(ISHR obj) {
    }

    @Override
    public void visitISTORE(ISTORE obj) {
    }

    @Override
    public void visitISUB(ISUB obj) {
    }

    @Override
    public void visitIUSHR(IUSHR obj) {
    }

    @Override
    public void visitIXOR(IXOR obj) {
    }

    @Override
    public void visitJSR(JSR obj) {
    }

    @Override
    public void visitJSR_W(JSR_W obj) {
    }

    @Override
    public void visitJsrInstruction(JsrInstruction obj) {
    }

    @Override
    public void visitL2D(L2D obj) {
    }

    @Override
    public void visitL2F(L2F obj) {
    }

    @Override
    public void visitL2I(L2I obj) {
    }

    @Override
    public void visitLADD(LADD obj) {
    }

    @Override
    public void visitLALOAD(LALOAD obj) {
    }

    @Override
    public void visitLAND(LAND obj) {
    }

    @Override
    public void visitLASTORE(LASTORE obj) {
    }

    @Override
    public void visitLCMP(LCMP obj) {
    }

    @Override
    public void visitLCONST(LCONST obj) {
    }

    @Override
    public void visitLDC(LDC obj) {
    }

    @Override
    public void visitLDC2_W(LDC2_W obj) {
    }

    @Override
    public void visitLDIV(LDIV obj) {
    }

    @Override
    public void visitLLOAD(LLOAD obj) {
    }

    @Override
    public void visitLMUL(LMUL obj) {
    }

    @Override
    public void visitLNEG(LNEG obj) {
    }

    @Override
    public void visitLoadClass(LoadClass obj) {
    }

    @Override
    public void visitLoadInstruction(LoadInstruction obj) {
    }

    @Override
    public void visitLocalVariableInstruction(LocalVariableInstruction obj) {
    }

    @Override
    public void visitLOOKUPSWITCH(LOOKUPSWITCH obj) {
    }

    @Override
    public void visitLOR(LOR obj) {
    }

    @Override
    public void visitLREM(LREM obj) {
    }

    @Override
    public void visitLRETURN(LRETURN obj) {
    }

    @Override
    public void visitLSHL(LSHL obj) {
    }

    @Override
    public void visitLSHR(LSHR obj) {
    }

    @Override
    public void visitLSTORE(LSTORE obj) {
    }

    @Override
    public void visitLSUB(LSUB obj) {
    }

    @Override
    public void visitLUSHR(LUSHR obj) {
    }

    @Override
    public void visitLXOR(LXOR obj) {
    }

    @Override
    public void visitMONITORENTER(MONITORENTER obj) {
    }

    @Override
    public void visitMONITOREXIT(MONITOREXIT obj) {
    }

    @Override
    public void visitMULTIANEWARRAY(MULTIANEWARRAY obj) {
    }

    @Override
    public void visitNEW(NEW obj) {
    }

    @Override
    public void visitNEWARRAY(NEWARRAY obj) {
    }

    @Override
    public void visitNOP(NOP obj) {
    }

    @Override
    public void visitPOP(POP obj) {
    }

    @Override
    public void visitPOP2(POP2 obj) {
    }

    @Override
    public void visitPopInstruction(PopInstruction obj) {
    }

    @Override
    public void visitPushInstruction(PushInstruction obj) {
    }

    @Override
    public void visitPUTFIELD(PUTFIELD obj) {
    }

    @Override
    public void visitPUTSTATIC(PUTSTATIC obj) {
    }

    @Override
    public void visitRET(RET obj) {
    }

    @Override
    public void visitRETURN(RETURN obj) {
    }

    @Override
    public void visitReturnInstruction(ReturnInstruction obj) {
    }

    @Override
    public void visitSALOAD(SALOAD obj) {
    }

    @Override
    public void visitSASTORE(SASTORE obj) {
    }

    @Override
    public void visitSelect(Select obj) {
    }

    @Override
    public void visitSIPUSH(SIPUSH obj) {
    }

    @Override
    public void visitStackConsumer(StackConsumer obj) {
    }

    @Override
    public void visitStackInstruction(StackInstruction obj) {
    }

    @Override
    public void visitStackProducer(StackProducer obj) {
    }

    @Override
    public void visitStoreInstruction(StoreInstruction obj) {
    }

    @Override
    public void visitSWAP(SWAP obj) {
    }

    @Override
    public void visitTABLESWITCH(TABLESWITCH obj) {
    }

    @Override
    public void visitTypedInstruction(TypedInstruction obj) {
    }

    @Override
    public void visitUnconditionalBranch(UnconditionalBranch obj) {
    }

    @Override
    public void visitVariableLengthInstruction(VariableLengthInstruction obj) {
    }
}

