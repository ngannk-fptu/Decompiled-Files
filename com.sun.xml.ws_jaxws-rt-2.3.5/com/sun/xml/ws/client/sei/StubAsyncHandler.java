/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.soap.SOAPBinding$Style
 */
package com.sun.xml.ws.client.sei;

import com.sun.xml.ws.api.message.MessageContextFactory;
import com.sun.xml.ws.client.sei.StubHandler;
import com.sun.xml.ws.client.sei.ValueGetterFactory;
import com.sun.xml.ws.client.sei.ValueSetterFactory;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import java.util.List;
import javax.jws.soap.SOAPBinding;

public class StubAsyncHandler
extends StubHandler {
    private final Class asyncBeanClass;

    public StubAsyncHandler(JavaMethodImpl jm, JavaMethodImpl sync, MessageContextFactory mcf) {
        super(sync, mcf);
        List<ParameterImpl> rp = sync.getResponseParameters();
        int size = 0;
        for (ParameterImpl param : rp) {
            if (param.isWrapperStyle()) {
                WrapperParameter wrapParam = (WrapperParameter)param;
                size += wrapParam.getWrapperChildren().size();
                if (sync.getBinding().getStyle() != SOAPBinding.Style.DOCUMENT) continue;
                size += 2;
                continue;
            }
            ++size;
        }
        Class tempWrap = null;
        if (size > 1) {
            rp = jm.getResponseParameters();
            for (ParameterImpl param : rp) {
                if (param.isWrapperStyle()) {
                    WrapperParameter wrapParam = (WrapperParameter)param;
                    if (sync.getBinding().getStyle() == SOAPBinding.Style.DOCUMENT) {
                        tempWrap = (Class)wrapParam.getTypeInfo().type;
                        break;
                    }
                    for (ParameterImpl p : wrapParam.getWrapperChildren()) {
                        if (p.getIndex() != -1) continue;
                        tempWrap = (Class)p.getTypeInfo().type;
                        break;
                    }
                    if (tempWrap == null) continue;
                    break;
                }
                if (param.getIndex() != -1) continue;
                tempWrap = (Class)param.getTypeInfo().type;
                break;
            }
        }
        this.asyncBeanClass = tempWrap;
        switch (size) {
            case 0: {
                this.responseBuilder = this.buildResponseBuilder(sync, ValueSetterFactory.NONE);
                break;
            }
            case 1: {
                this.responseBuilder = this.buildResponseBuilder(sync, ValueSetterFactory.SINGLE);
                break;
            }
            default: {
                this.responseBuilder = this.buildResponseBuilder(sync, new ValueSetterFactory.AsyncBeanValueSetterFactory(this.asyncBeanClass));
            }
        }
    }

    @Override
    protected void initArgs(Object[] args) throws Exception {
        if (this.asyncBeanClass != null) {
            args[0] = this.asyncBeanClass.newInstance();
        }
    }

    @Override
    ValueGetterFactory getValueGetterFactory() {
        return ValueGetterFactory.ASYNC;
    }
}

