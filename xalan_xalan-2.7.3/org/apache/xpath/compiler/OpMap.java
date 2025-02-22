/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.compiler;

import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.ObjectVector;
import org.apache.xpath.compiler.OpMapVector;

public class OpMap {
    protected String m_currentPattern;
    static final int MAXTOKENQUEUESIZE = 500;
    static final int BLOCKTOKENQUEUESIZE = 500;
    ObjectVector m_tokenQueue = new ObjectVector(500, 500);
    OpMapVector m_opMap = null;
    public static final int MAPINDEX_LENGTH = 1;

    public String toString() {
        return this.m_currentPattern;
    }

    public String getPatternString() {
        return this.m_currentPattern;
    }

    public ObjectVector getTokenQueue() {
        return this.m_tokenQueue;
    }

    public Object getToken(int pos) {
        return this.m_tokenQueue.elementAt(pos);
    }

    public int getTokenQueueSize() {
        return this.m_tokenQueue.size();
    }

    public OpMapVector getOpMap() {
        return this.m_opMap;
    }

    void shrink() {
        int n = this.m_opMap.elementAt(1);
        this.m_opMap.setToSize(n + 4);
        this.m_opMap.setElementAt(0, n);
        this.m_opMap.setElementAt(0, n + 1);
        this.m_opMap.setElementAt(0, n + 2);
        n = this.m_tokenQueue.size();
        this.m_tokenQueue.setToSize(n + 4);
        this.m_tokenQueue.setElementAt(null, n);
        this.m_tokenQueue.setElementAt(null, n + 1);
        this.m_tokenQueue.setElementAt(null, n + 2);
    }

    public int getOp(int opPos) {
        return this.m_opMap.elementAt(opPos);
    }

    public void setOp(int opPos, int value) {
        this.m_opMap.setElementAt(value, opPos);
    }

    public int getNextOpPos(int opPos) {
        return opPos + this.m_opMap.elementAt(opPos + 1);
    }

    public int getNextStepPos(int opPos) {
        int stepType = this.getOp(opPos);
        if (stepType >= 37 && stepType <= 53) {
            return this.getNextOpPos(opPos);
        }
        if (stepType >= 22 && stepType <= 25) {
            int newOpPos = this.getNextOpPos(opPos);
            while (29 == this.getOp(newOpPos)) {
                newOpPos = this.getNextOpPos(newOpPos);
            }
            stepType = this.getOp(newOpPos);
            if (stepType < 37 || stepType > 53) {
                return -1;
            }
            return newOpPos;
        }
        throw new RuntimeException(XSLMessages.createXPATHMessage("ER_UNKNOWN_STEP", new Object[]{String.valueOf(stepType)}));
    }

    public static int getNextOpPos(int[] opMap, int opPos) {
        return opPos + opMap[opPos + 1];
    }

    public int getFirstPredicateOpPos(int opPos) throws TransformerException {
        int stepType = this.m_opMap.elementAt(opPos);
        if (stepType >= 37 && stepType <= 53) {
            return opPos + this.m_opMap.elementAt(opPos + 2);
        }
        if (stepType >= 22 && stepType <= 25) {
            return opPos + this.m_opMap.elementAt(opPos + 1);
        }
        if (-2 == stepType) {
            return -2;
        }
        this.error("ER_UNKNOWN_OPCODE", new Object[]{String.valueOf(stepType)});
        return -1;
    }

    public void error(String msg, Object[] args) throws TransformerException {
        String fmsg = XSLMessages.createXPATHMessage(msg, args);
        throw new TransformerException(fmsg);
    }

    public static int getFirstChildPos(int opPos) {
        return opPos + 2;
    }

    public int getArgLength(int opPos) {
        return this.m_opMap.elementAt(opPos + 1);
    }

    public int getArgLengthOfStep(int opPos) {
        return this.m_opMap.elementAt(opPos + 1 + 1) - 3;
    }

    public static int getFirstChildPosOfStep(int opPos) {
        return opPos + 3;
    }

    public int getStepTestType(int opPosOfStep) {
        return this.m_opMap.elementAt(opPosOfStep + 3);
    }

    public String getStepNS(int opPosOfStep) {
        int argLenOfStep = this.getArgLengthOfStep(opPosOfStep);
        if (argLenOfStep == 3) {
            int index = this.m_opMap.elementAt(opPosOfStep + 4);
            if (index >= 0) {
                return (String)this.m_tokenQueue.elementAt(index);
            }
            if (-3 == index) {
                return "*";
            }
            return null;
        }
        return null;
    }

    public String getStepLocalName(int opPosOfStep) {
        int index;
        int argLenOfStep = this.getArgLengthOfStep(opPosOfStep);
        switch (argLenOfStep) {
            case 0: {
                index = -2;
                break;
            }
            case 1: {
                index = -3;
                break;
            }
            case 2: {
                index = this.m_opMap.elementAt(opPosOfStep + 4);
                break;
            }
            case 3: {
                index = this.m_opMap.elementAt(opPosOfStep + 5);
                break;
            }
            default: {
                index = -2;
            }
        }
        if (index >= 0) {
            return this.m_tokenQueue.elementAt(index).toString();
        }
        if (-3 == index) {
            return "*";
        }
        return null;
    }
}

