/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.symbolTable.Undefined;
import org.apache.axis.wsdl.symbolTable.UndefinedDelegate;

public class UndefinedElement
extends Element
implements Undefined {
    private UndefinedDelegate delegate = null;

    public UndefinedElement(QName pqName) {
        super(pqName, null);
        this.undefined = true;
        this.delegate = new UndefinedDelegate(this);
    }

    public void register(TypeEntry referrant) {
        this.delegate.register(referrant);
    }

    public void update(TypeEntry def) throws IOException {
        this.delegate.update(def);
    }
}

