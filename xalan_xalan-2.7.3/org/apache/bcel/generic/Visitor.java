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

public interface Visitor {
    public void visitAALOAD(AALOAD var1);

    public void visitAASTORE(AASTORE var1);

    public void visitACONST_NULL(ACONST_NULL var1);

    public void visitAllocationInstruction(AllocationInstruction var1);

    public void visitALOAD(ALOAD var1);

    public void visitANEWARRAY(ANEWARRAY var1);

    public void visitARETURN(ARETURN var1);

    public void visitArithmeticInstruction(ArithmeticInstruction var1);

    public void visitArrayInstruction(ArrayInstruction var1);

    public void visitARRAYLENGTH(ARRAYLENGTH var1);

    public void visitASTORE(ASTORE var1);

    public void visitATHROW(ATHROW var1);

    public void visitBALOAD(BALOAD var1);

    public void visitBASTORE(BASTORE var1);

    public void visitBIPUSH(BIPUSH var1);

    public void visitBranchInstruction(BranchInstruction var1);

    public void visitBREAKPOINT(BREAKPOINT var1);

    public void visitCALOAD(CALOAD var1);

    public void visitCASTORE(CASTORE var1);

    public void visitCHECKCAST(CHECKCAST var1);

    public void visitConstantPushInstruction(ConstantPushInstruction var1);

    public void visitConversionInstruction(ConversionInstruction var1);

    public void visitCPInstruction(CPInstruction var1);

    public void visitD2F(D2F var1);

    public void visitD2I(D2I var1);

    public void visitD2L(D2L var1);

    public void visitDADD(DADD var1);

    public void visitDALOAD(DALOAD var1);

    public void visitDASTORE(DASTORE var1);

    public void visitDCMPG(DCMPG var1);

    public void visitDCMPL(DCMPL var1);

    public void visitDCONST(DCONST var1);

    public void visitDDIV(DDIV var1);

    public void visitDLOAD(DLOAD var1);

    public void visitDMUL(DMUL var1);

    public void visitDNEG(DNEG var1);

    public void visitDREM(DREM var1);

    public void visitDRETURN(DRETURN var1);

    public void visitDSTORE(DSTORE var1);

    public void visitDSUB(DSUB var1);

    public void visitDUP(DUP var1);

    public void visitDUP_X1(DUP_X1 var1);

    public void visitDUP_X2(DUP_X2 var1);

    public void visitDUP2(DUP2 var1);

    public void visitDUP2_X1(DUP2_X1 var1);

    public void visitDUP2_X2(DUP2_X2 var1);

    public void visitExceptionThrower(ExceptionThrower var1);

    public void visitF2D(F2D var1);

    public void visitF2I(F2I var1);

    public void visitF2L(F2L var1);

    public void visitFADD(FADD var1);

    public void visitFALOAD(FALOAD var1);

    public void visitFASTORE(FASTORE var1);

    public void visitFCMPG(FCMPG var1);

    public void visitFCMPL(FCMPL var1);

    public void visitFCONST(FCONST var1);

    public void visitFDIV(FDIV var1);

    public void visitFieldInstruction(FieldInstruction var1);

    public void visitFieldOrMethod(FieldOrMethod var1);

    public void visitFLOAD(FLOAD var1);

    public void visitFMUL(FMUL var1);

    public void visitFNEG(FNEG var1);

    public void visitFREM(FREM var1);

    public void visitFRETURN(FRETURN var1);

    public void visitFSTORE(FSTORE var1);

    public void visitFSUB(FSUB var1);

    public void visitGETFIELD(GETFIELD var1);

    public void visitGETSTATIC(GETSTATIC var1);

    public void visitGOTO(GOTO var1);

    public void visitGOTO_W(GOTO_W var1);

    public void visitGotoInstruction(GotoInstruction var1);

    public void visitI2B(I2B var1);

    public void visitI2C(I2C var1);

    public void visitI2D(I2D var1);

    public void visitI2F(I2F var1);

    public void visitI2L(I2L var1);

    public void visitI2S(I2S var1);

    public void visitIADD(IADD var1);

    public void visitIALOAD(IALOAD var1);

    public void visitIAND(IAND var1);

    public void visitIASTORE(IASTORE var1);

    public void visitICONST(ICONST var1);

    public void visitIDIV(IDIV var1);

    public void visitIF_ACMPEQ(IF_ACMPEQ var1);

    public void visitIF_ACMPNE(IF_ACMPNE var1);

    public void visitIF_ICMPEQ(IF_ICMPEQ var1);

    public void visitIF_ICMPGE(IF_ICMPGE var1);

    public void visitIF_ICMPGT(IF_ICMPGT var1);

    public void visitIF_ICMPLE(IF_ICMPLE var1);

    public void visitIF_ICMPLT(IF_ICMPLT var1);

    public void visitIF_ICMPNE(IF_ICMPNE var1);

    public void visitIFEQ(IFEQ var1);

    public void visitIFGE(IFGE var1);

    public void visitIFGT(IFGT var1);

    public void visitIfInstruction(IfInstruction var1);

    public void visitIFLE(IFLE var1);

    public void visitIFLT(IFLT var1);

    public void visitIFNE(IFNE var1);

    public void visitIFNONNULL(IFNONNULL var1);

    public void visitIFNULL(IFNULL var1);

    public void visitIINC(IINC var1);

    public void visitILOAD(ILOAD var1);

    public void visitIMPDEP1(IMPDEP1 var1);

    public void visitIMPDEP2(IMPDEP2 var1);

    public void visitIMUL(IMUL var1);

    public void visitINEG(INEG var1);

    public void visitINSTANCEOF(INSTANCEOF var1);

    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC var1);

    public void visitInvokeInstruction(InvokeInstruction var1);

    public void visitINVOKEINTERFACE(INVOKEINTERFACE var1);

    public void visitINVOKESPECIAL(INVOKESPECIAL var1);

    public void visitINVOKESTATIC(INVOKESTATIC var1);

    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL var1);

    public void visitIOR(IOR var1);

    public void visitIREM(IREM var1);

    public void visitIRETURN(IRETURN var1);

    public void visitISHL(ISHL var1);

    public void visitISHR(ISHR var1);

    public void visitISTORE(ISTORE var1);

    public void visitISUB(ISUB var1);

    public void visitIUSHR(IUSHR var1);

    public void visitIXOR(IXOR var1);

    public void visitJSR(JSR var1);

    public void visitJSR_W(JSR_W var1);

    public void visitJsrInstruction(JsrInstruction var1);

    public void visitL2D(L2D var1);

    public void visitL2F(L2F var1);

    public void visitL2I(L2I var1);

    public void visitLADD(LADD var1);

    public void visitLALOAD(LALOAD var1);

    public void visitLAND(LAND var1);

    public void visitLASTORE(LASTORE var1);

    public void visitLCMP(LCMP var1);

    public void visitLCONST(LCONST var1);

    public void visitLDC(LDC var1);

    public void visitLDC2_W(LDC2_W var1);

    public void visitLDIV(LDIV var1);

    public void visitLLOAD(LLOAD var1);

    public void visitLMUL(LMUL var1);

    public void visitLNEG(LNEG var1);

    public void visitLoadClass(LoadClass var1);

    public void visitLoadInstruction(LoadInstruction var1);

    public void visitLocalVariableInstruction(LocalVariableInstruction var1);

    public void visitLOOKUPSWITCH(LOOKUPSWITCH var1);

    public void visitLOR(LOR var1);

    public void visitLREM(LREM var1);

    public void visitLRETURN(LRETURN var1);

    public void visitLSHL(LSHL var1);

    public void visitLSHR(LSHR var1);

    public void visitLSTORE(LSTORE var1);

    public void visitLSUB(LSUB var1);

    public void visitLUSHR(LUSHR var1);

    public void visitLXOR(LXOR var1);

    public void visitMONITORENTER(MONITORENTER var1);

    public void visitMONITOREXIT(MONITOREXIT var1);

    public void visitMULTIANEWARRAY(MULTIANEWARRAY var1);

    public void visitNEW(NEW var1);

    public void visitNEWARRAY(NEWARRAY var1);

    public void visitNOP(NOP var1);

    public void visitPOP(POP var1);

    public void visitPOP2(POP2 var1);

    public void visitPopInstruction(PopInstruction var1);

    public void visitPushInstruction(PushInstruction var1);

    public void visitPUTFIELD(PUTFIELD var1);

    public void visitPUTSTATIC(PUTSTATIC var1);

    public void visitRET(RET var1);

    public void visitRETURN(RETURN var1);

    public void visitReturnInstruction(ReturnInstruction var1);

    public void visitSALOAD(SALOAD var1);

    public void visitSASTORE(SASTORE var1);

    public void visitSelect(Select var1);

    public void visitSIPUSH(SIPUSH var1);

    public void visitStackConsumer(StackConsumer var1);

    public void visitStackInstruction(StackInstruction var1);

    public void visitStackProducer(StackProducer var1);

    public void visitStoreInstruction(StoreInstruction var1);

    public void visitSWAP(SWAP var1);

    public void visitTABLESWITCH(TABLESWITCH var1);

    public void visitTypedInstruction(TypedInstruction var1);

    public void visitUnconditionalBranch(UnconditionalBranch var1);

    public void visitVariableLengthInstruction(VariableLengthInstruction var1);
}

