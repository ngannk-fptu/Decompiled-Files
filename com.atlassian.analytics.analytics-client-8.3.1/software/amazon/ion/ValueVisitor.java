/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonBlob;
import software.amazon.ion.IonBool;
import software.amazon.ion.IonClob;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonDecimal;
import software.amazon.ion.IonFloat;
import software.amazon.ion.IonInt;
import software.amazon.ion.IonList;
import software.amazon.ion.IonNull;
import software.amazon.ion.IonSexp;
import software.amazon.ion.IonString;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSymbol;
import software.amazon.ion.IonTimestamp;

public interface ValueVisitor {
    public void visit(IonBlob var1) throws Exception;

    public void visit(IonBool var1) throws Exception;

    public void visit(IonClob var1) throws Exception;

    public void visit(IonDatagram var1) throws Exception;

    public void visit(IonDecimal var1) throws Exception;

    public void visit(IonFloat var1) throws Exception;

    public void visit(IonInt var1) throws Exception;

    public void visit(IonList var1) throws Exception;

    public void visit(IonNull var1) throws Exception;

    public void visit(IonSexp var1) throws Exception;

    public void visit(IonString var1) throws Exception;

    public void visit(IonStruct var1) throws Exception;

    public void visit(IonSymbol var1) throws Exception;

    public void visit(IonTimestamp var1) throws Exception;
}

