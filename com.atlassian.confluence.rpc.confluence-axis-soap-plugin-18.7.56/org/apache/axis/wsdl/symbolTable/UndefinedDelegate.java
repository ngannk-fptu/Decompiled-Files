/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import java.io.IOException;
import java.util.Vector;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.symbolTable.Undefined;

public class UndefinedDelegate
implements Undefined {
    private Vector list = new Vector();
    private TypeEntry undefinedType;

    UndefinedDelegate(TypeEntry te) {
        this.undefinedType = te;
    }

    public void register(TypeEntry referrant) {
        this.list.add(referrant);
    }

    public void update(TypeEntry def) throws IOException {
        boolean done = false;
        while (!done) {
            done = true;
            for (int i = 0; i < this.list.size(); ++i) {
                TypeEntry te = (TypeEntry)this.list.elementAt(i);
                if (!te.updateUndefined(this.undefinedType, def)) continue;
                done = false;
            }
        }
        TypeEntry uType = def.getUndefinedTypeRef();
        if (uType != null) {
            for (int i = 0; i < this.list.size(); ++i) {
                TypeEntry te = (TypeEntry)this.list.elementAt(i);
                ((Undefined)((Object)uType)).register(te);
            }
        }
    }
}

