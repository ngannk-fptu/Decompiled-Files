/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebParam$Mode
 */
package com.sun.xml.ws.model;

import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.WrapperComposite;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebParam;

public class WrapperParameter
extends ParameterImpl {
    protected final List<ParameterImpl> wrapperChildren = new ArrayList<ParameterImpl>();

    public WrapperParameter(JavaMethodImpl parent, TypeInfo typeRef, WebParam.Mode mode, int index) {
        super(parent, typeRef, mode, index);
        typeRef.properties().put(WrapperParameter.class.getName(), this);
    }

    @Override
    public boolean isWrapperStyle() {
        return true;
    }

    public List<ParameterImpl> getWrapperChildren() {
        return this.wrapperChildren;
    }

    public void addWrapperChild(ParameterImpl wrapperChild) {
        this.wrapperChildren.add(wrapperChild);
        wrapperChild.wrapper = this;
        assert (wrapperChild.getBinding() == ParameterBinding.BODY);
    }

    public void clear() {
        this.wrapperChildren.clear();
    }

    @Override
    void fillTypes(List<TypeInfo> types) {
        super.fillTypes(types);
        if (WrapperComposite.class.equals((Object)this.getTypeInfo().type)) {
            for (ParameterImpl p : this.wrapperChildren) {
                p.fillTypes(types);
            }
        }
    }
}

