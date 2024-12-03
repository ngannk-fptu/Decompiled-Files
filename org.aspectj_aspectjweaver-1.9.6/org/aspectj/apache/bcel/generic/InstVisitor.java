/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.generic.FieldInstruction;
import org.aspectj.apache.bcel.generic.IINC;
import org.aspectj.apache.bcel.generic.INVOKEINTERFACE;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionLV;
import org.aspectj.apache.bcel.generic.InstructionSelect;
import org.aspectj.apache.bcel.generic.InvokeInstruction;
import org.aspectj.apache.bcel.generic.LOOKUPSWITCH;
import org.aspectj.apache.bcel.generic.MULTIANEWARRAY;
import org.aspectj.apache.bcel.generic.RET;
import org.aspectj.apache.bcel.generic.TABLESWITCH;

public interface InstVisitor {
    public void visitStackInstruction(Instruction var1);

    public void visitLocalVariableInstruction(InstructionLV var1);

    public void visitBranchInstruction(InstructionBranch var1);

    public void visitLoadClass(Instruction var1);

    public void visitFieldInstruction(Instruction var1);

    public void visitIfInstruction(Instruction var1);

    public void visitConversionInstruction(Instruction var1);

    public void visitPopInstruction(Instruction var1);

    public void visitStoreInstruction(Instruction var1);

    public void visitTypedInstruction(Instruction var1);

    public void visitSelect(InstructionSelect var1);

    public void visitJsrInstruction(InstructionBranch var1);

    public void visitGotoInstruction(Instruction var1);

    public void visitUnconditionalBranch(Instruction var1);

    public void visitPushInstruction(Instruction var1);

    public void visitArithmeticInstruction(Instruction var1);

    public void visitCPInstruction(Instruction var1);

    public void visitInvokeInstruction(InvokeInstruction var1);

    public void visitArrayInstruction(Instruction var1);

    public void visitAllocationInstruction(Instruction var1);

    public void visitReturnInstruction(Instruction var1);

    public void visitFieldOrMethod(Instruction var1);

    public void visitConstantPushInstruction(Instruction var1);

    public void visitExceptionThrower(Instruction var1);

    public void visitLoadInstruction(Instruction var1);

    public void visitVariableLengthInstruction(Instruction var1);

    public void visitStackProducer(Instruction var1);

    public void visitStackConsumer(Instruction var1);

    public void visitACONST_NULL(Instruction var1);

    public void visitGETSTATIC(FieldInstruction var1);

    public void visitIF_ICMPLT(Instruction var1);

    public void visitMONITOREXIT(Instruction var1);

    public void visitIFLT(Instruction var1);

    public void visitLSTORE(Instruction var1);

    public void visitPOP2(Instruction var1);

    public void visitBASTORE(Instruction var1);

    public void visitISTORE(Instruction var1);

    public void visitCHECKCAST(Instruction var1);

    public void visitFCMPG(Instruction var1);

    public void visitI2F(Instruction var1);

    public void visitATHROW(Instruction var1);

    public void visitDCMPL(Instruction var1);

    public void visitARRAYLENGTH(Instruction var1);

    public void visitDUP(Instruction var1);

    public void visitINVOKESTATIC(InvokeInstruction var1);

    public void visitLCONST(Instruction var1);

    public void visitDREM(Instruction var1);

    public void visitIFGE(Instruction var1);

    public void visitCALOAD(Instruction var1);

    public void visitLASTORE(Instruction var1);

    public void visitI2D(Instruction var1);

    public void visitDADD(Instruction var1);

    public void visitINVOKESPECIAL(InvokeInstruction var1);

    public void visitIAND(Instruction var1);

    public void visitPUTFIELD(FieldInstruction var1);

    public void visitILOAD(Instruction var1);

    public void visitDLOAD(Instruction var1);

    public void visitDCONST(Instruction var1);

    public void visitNEW(Instruction var1);

    public void visitIFNULL(Instruction var1);

    public void visitLSUB(Instruction var1);

    public void visitL2I(Instruction var1);

    public void visitISHR(Instruction var1);

    public void visitTABLESWITCH(TABLESWITCH var1);

    public void visitIINC(IINC var1);

    public void visitDRETURN(Instruction var1);

    public void visitFSTORE(Instruction var1);

    public void visitDASTORE(Instruction var1);

    public void visitIALOAD(Instruction var1);

    public void visitDDIV(Instruction var1);

    public void visitIF_ICMPGE(Instruction var1);

    public void visitLAND(Instruction var1);

    public void visitIDIV(Instruction var1);

    public void visitLOR(Instruction var1);

    public void visitCASTORE(Instruction var1);

    public void visitFREM(Instruction var1);

    public void visitLDC(Instruction var1);

    public void visitBIPUSH(Instruction var1);

    public void visitDSTORE(Instruction var1);

    public void visitF2L(Instruction var1);

    public void visitFMUL(Instruction var1);

    public void visitLLOAD(Instruction var1);

    public void visitJSR(InstructionBranch var1);

    public void visitFSUB(Instruction var1);

    public void visitSASTORE(Instruction var1);

    public void visitALOAD(Instruction var1);

    public void visitDUP2_X2(Instruction var1);

    public void visitRETURN(Instruction var1);

    public void visitDALOAD(Instruction var1);

    public void visitSIPUSH(Instruction var1);

    public void visitDSUB(Instruction var1);

    public void visitL2F(Instruction var1);

    public void visitIF_ICMPGT(Instruction var1);

    public void visitF2D(Instruction var1);

    public void visitI2L(Instruction var1);

    public void visitIF_ACMPNE(Instruction var1);

    public void visitPOP(Instruction var1);

    public void visitI2S(Instruction var1);

    public void visitIFEQ(Instruction var1);

    public void visitSWAP(Instruction var1);

    public void visitIOR(Instruction var1);

    public void visitIREM(Instruction var1);

    public void visitIASTORE(Instruction var1);

    public void visitNEWARRAY(Instruction var1);

    public void visitINVOKEINTERFACE(INVOKEINTERFACE var1);

    public void visitINEG(Instruction var1);

    public void visitLCMP(Instruction var1);

    public void visitJSR_W(InstructionBranch var1);

    public void visitMULTIANEWARRAY(MULTIANEWARRAY var1);

    public void visitDUP_X2(Instruction var1);

    public void visitSALOAD(Instruction var1);

    public void visitIFNONNULL(Instruction var1);

    public void visitDMUL(Instruction var1);

    public void visitIFNE(Instruction var1);

    public void visitIF_ICMPLE(Instruction var1);

    public void visitLDC2_W(Instruction var1);

    public void visitGETFIELD(FieldInstruction var1);

    public void visitLADD(Instruction var1);

    public void visitNOP(Instruction var1);

    public void visitFALOAD(Instruction var1);

    public void visitINSTANCEOF(Instruction var1);

    public void visitIFLE(Instruction var1);

    public void visitLXOR(Instruction var1);

    public void visitLRETURN(Instruction var1);

    public void visitFCONST(Instruction var1);

    public void visitIUSHR(Instruction var1);

    public void visitBALOAD(Instruction var1);

    public void visitDUP2(Instruction var1);

    public void visitIF_ACMPEQ(Instruction var1);

    public void visitIMPDEP1(Instruction var1);

    public void visitMONITORENTER(Instruction var1);

    public void visitLSHL(Instruction var1);

    public void visitDCMPG(Instruction var1);

    public void visitD2L(Instruction var1);

    public void visitIMPDEP2(Instruction var1);

    public void visitL2D(Instruction var1);

    public void visitRET(RET var1);

    public void visitIFGT(Instruction var1);

    public void visitIXOR(Instruction var1);

    public void visitINVOKEVIRTUAL(InvokeInstruction var1);

    public void visitFASTORE(Instruction var1);

    public void visitIRETURN(Instruction var1);

    public void visitIF_ICMPNE(Instruction var1);

    public void visitFLOAD(Instruction var1);

    public void visitLDIV(Instruction var1);

    public void visitPUTSTATIC(FieldInstruction var1);

    public void visitAALOAD(Instruction var1);

    public void visitD2I(Instruction var1);

    public void visitIF_ICMPEQ(Instruction var1);

    public void visitAASTORE(Instruction var1);

    public void visitARETURN(Instruction var1);

    public void visitDUP2_X1(Instruction var1);

    public void visitFNEG(Instruction var1);

    public void visitGOTO_W(Instruction var1);

    public void visitD2F(Instruction var1);

    public void visitGOTO(Instruction var1);

    public void visitISUB(Instruction var1);

    public void visitF2I(Instruction var1);

    public void visitDNEG(Instruction var1);

    public void visitICONST(Instruction var1);

    public void visitFDIV(Instruction var1);

    public void visitI2B(Instruction var1);

    public void visitLNEG(Instruction var1);

    public void visitLREM(Instruction var1);

    public void visitIMUL(Instruction var1);

    public void visitIADD(Instruction var1);

    public void visitLSHR(Instruction var1);

    public void visitLOOKUPSWITCH(LOOKUPSWITCH var1);

    public void visitDUP_X1(Instruction var1);

    public void visitFCMPL(Instruction var1);

    public void visitI2C(Instruction var1);

    public void visitLMUL(Instruction var1);

    public void visitLUSHR(Instruction var1);

    public void visitISHL(Instruction var1);

    public void visitLALOAD(Instruction var1);

    public void visitASTORE(Instruction var1);

    public void visitANEWARRAY(Instruction var1);

    public void visitFRETURN(Instruction var1);

    public void visitFADD(Instruction var1);

    public void visitBREAKPOINT(Instruction var1);
}

