/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import java.util.Map;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOutput;
import javax.wsdl.Operation;
import javax.wsdl.WSDLElement;

public interface BindingOperation
extends WSDLElement {
    public void setName(String var1);

    public String getName();

    public void setOperation(Operation var1);

    public Operation getOperation();

    public void setBindingInput(BindingInput var1);

    public BindingInput getBindingInput();

    public void setBindingOutput(BindingOutput var1);

    public BindingOutput getBindingOutput();

    public void addBindingFault(BindingFault var1);

    public BindingFault removeBindingFault(String var1);

    public BindingFault getBindingFault(String var1);

    public Map getBindingFaults();
}

