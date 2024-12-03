/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.OperationType
 */
package org.apache.axis.wsdl.symbolTable;

import java.util.Map;
import java.util.Vector;
import javax.wsdl.OperationType;
import org.apache.axis.wsdl.symbolTable.Parameter;

public class Parameters {
    public OperationType mep = OperationType.REQUEST_RESPONSE;
    public Vector list = new Vector();
    public Parameter returnParam = null;
    public Map faults = null;
    public String signature = null;
    public int inputs = 0;
    public int inouts = 0;
    public int outputs = 0;

    public String toString() {
        return "\nreturnParam = " + this.returnParam + "\nfaults = " + this.faults + "\nsignature = " + this.signature + "\n(inputs, inouts, outputs) = (" + this.inputs + ", " + this.inouts + ", " + this.outputs + ")" + "\nlist = " + this.list;
    }
}

