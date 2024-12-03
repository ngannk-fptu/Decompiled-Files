/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.Bridge
 *  com.sun.xml.bind.api.TypeReference
 *  javax.jws.WebParam$Mode
 *  javax.xml.ws.Holder
 */
package com.sun.xml.ws.model;

import com.sun.xml.bind.api.Bridge;
import com.sun.xml.bind.api.TypeReference;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.Parameter;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.WrapperParameter;
import com.sun.xml.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.WrapperComposite;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.util.List;
import javax.jws.WebParam;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

public class ParameterImpl
implements Parameter {
    private ParameterBinding binding;
    private ParameterBinding outBinding;
    private String partName;
    private final int index;
    private final WebParam.Mode mode;
    private TypeReference typeReference;
    private TypeInfo typeInfo;
    private QName name;
    private final JavaMethodImpl parent;
    WrapperParameter wrapper;
    TypeInfo itemTypeInfo;

    public ParameterImpl(JavaMethodImpl parent, TypeInfo type, WebParam.Mode mode, int index) {
        assert (type != null);
        this.typeInfo = type;
        this.name = type.tagName;
        this.mode = mode;
        this.index = index;
        this.parent = parent;
    }

    @Override
    public AbstractSEIModelImpl getOwner() {
        return this.parent.owner;
    }

    @Override
    public JavaMethod getParent() {
        return this.parent;
    }

    @Override
    public QName getName() {
        return this.name;
    }

    public XMLBridge getXMLBridge() {
        return this.getOwner().getXMLBridge(this.typeInfo);
    }

    public XMLBridge getInlinedRepeatedElementBridge() {
        XMLBridge xb;
        TypeInfo itemType = this.getItemType();
        if (itemType != null && itemType.getWrapperType() == null && (xb = this.getOwner().getXMLBridge(itemType)) != null) {
            return new RepeatedElementBridge(this.typeInfo, xb);
        }
        return null;
    }

    public TypeInfo getItemType() {
        if (this.itemTypeInfo != null) {
            return this.itemTypeInfo;
        }
        if (this.parent.getBinding().isRpcLit() || this.wrapper == null) {
            return null;
        }
        if (!WrapperComposite.class.equals((Object)this.wrapper.getTypeInfo().type)) {
            return null;
        }
        if (!this.getBinding().isBody()) {
            return null;
        }
        this.itemTypeInfo = this.typeInfo.getItemType();
        return this.itemTypeInfo;
    }

    @Override
    public Bridge getBridge() {
        return this.getOwner().getBridge(this.typeReference);
    }

    protected Bridge getBridge(TypeReference typeRef) {
        return this.getOwner().getBridge(typeRef);
    }

    public TypeReference getTypeReference() {
        return this.typeReference;
    }

    public TypeInfo getTypeInfo() {
        return this.typeInfo;
    }

    void setTypeReference(TypeReference type) {
        this.typeReference = type;
        this.name = type.tagName;
    }

    @Override
    public WebParam.Mode getMode() {
        return this.mode;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public boolean isWrapperStyle() {
        return false;
    }

    @Override
    public boolean isReturnValue() {
        return this.index == -1;
    }

    @Override
    public ParameterBinding getBinding() {
        if (this.binding == null) {
            return ParameterBinding.BODY;
        }
        return this.binding;
    }

    public void setBinding(ParameterBinding binding) {
        this.binding = binding;
    }

    public void setInBinding(ParameterBinding binding) {
        this.binding = binding;
    }

    public void setOutBinding(ParameterBinding binding) {
        this.outBinding = binding;
    }

    @Override
    public ParameterBinding getInBinding() {
        return this.binding;
    }

    @Override
    public ParameterBinding getOutBinding() {
        if (this.outBinding == null) {
            return this.binding;
        }
        return this.outBinding;
    }

    @Override
    public boolean isIN() {
        return this.mode == WebParam.Mode.IN;
    }

    @Override
    public boolean isOUT() {
        return this.mode == WebParam.Mode.OUT;
    }

    @Override
    public boolean isINOUT() {
        return this.mode == WebParam.Mode.INOUT;
    }

    @Override
    public boolean isResponse() {
        return this.index == -1;
    }

    @Override
    public Object getHolderValue(Object obj) {
        if (obj != null && obj instanceof Holder) {
            return ((Holder)obj).value;
        }
        return obj;
    }

    @Override
    public String getPartName() {
        if (this.partName == null) {
            return this.name.getLocalPart();
        }
        return this.partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    void fillTypes(List<TypeInfo> types) {
        TypeInfo itemType = this.getItemType();
        if (itemType != null) {
            types.add(itemType);
            if (itemType.getWrapperType() != null) {
                types.add(this.getTypeInfo());
            }
        } else {
            types.add(this.getTypeInfo());
        }
    }
}

