/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.util;

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
import software.amazon.ion.IonValue;
import software.amazon.ion.ValueVisitor;

public abstract class AbstractValueVisitor
implements ValueVisitor {
    protected void defaultVisit(IonValue value) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void visit(IonBlob value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonBool value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonClob value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonDatagram value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonDecimal value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonFloat value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonInt value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonList value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonNull value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonSexp value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonString value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonStruct value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonSymbol value) throws Exception {
        this.defaultVisit(value);
    }

    public void visit(IonTimestamp value) throws Exception {
        this.defaultVisit(value);
    }
}

