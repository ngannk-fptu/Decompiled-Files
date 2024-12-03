/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.description;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;

public interface ServiceDesc
extends Serializable {
    public Style getStyle();

    public void setStyle(Style var1);

    public Use getUse();

    public void setUse(Use var1);

    public String getWSDLFile();

    public void setWSDLFile(String var1);

    public List getAllowedMethods();

    public void setAllowedMethods(List var1);

    public TypeMapping getTypeMapping();

    public void setTypeMapping(TypeMapping var1);

    public String getName();

    public void setName(String var1);

    public String getDocumentation();

    public void setDocumentation(String var1);

    public void removeOperationDesc(OperationDesc var1);

    public void addOperationDesc(OperationDesc var1);

    public ArrayList getOperations();

    public OperationDesc[] getOperationsByName(String var1);

    public OperationDesc getOperationByName(String var1);

    public OperationDesc getOperationByElementQName(QName var1);

    public OperationDesc[] getOperationsByQName(QName var1);

    public void setNamespaceMappings(List var1);

    public String getDefaultNamespace();

    public void setDefaultNamespace(String var1);

    public void setProperty(String var1, Object var2);

    public Object getProperty(String var1);

    public String getEndpointURL();

    public void setEndpointURL(String var1);

    public TypeMappingRegistry getTypeMappingRegistry();

    public void setTypeMappingRegistry(TypeMappingRegistry var1);

    public boolean isInitialized();

    public boolean isWrapped();

    public List getDisallowedMethods();

    public void setDisallowedMethods(List var1);
}

