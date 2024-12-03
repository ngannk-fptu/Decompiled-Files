/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Handler
extends Serializable {
    public void init();

    public void cleanup();

    public void invoke(MessageContext var1) throws AxisFault;

    public void onFault(MessageContext var1);

    public boolean canHandleBlock(QName var1);

    public List getUnderstoodHeaders();

    public void setOption(String var1, Object var2);

    public Object getOption(String var1);

    public void setName(String var1);

    public String getName();

    public Hashtable getOptions();

    public void setOptions(Hashtable var1);

    public Element getDeploymentData(Document var1);

    public void generateWSDL(MessageContext var1) throws AxisFault;
}

