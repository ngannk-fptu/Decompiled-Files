/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

import java.util.List;
import java.util.Map;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.WSDLElement;

public interface Operation
extends WSDLElement {
    public void setName(String var1);

    public String getName();

    public void setInput(Input var1);

    public Input getInput();

    public void setOutput(Output var1);

    public Output getOutput();

    public void addFault(Fault var1);

    public Fault getFault(String var1);

    public Fault removeFault(String var1);

    public Map getFaults();

    public void setStyle(OperationType var1);

    public OperationType getStyle();

    public void setParameterOrdering(List var1);

    public List getParameterOrdering();

    public void setUndefined(boolean var1);

    public boolean isUndefined();
}

