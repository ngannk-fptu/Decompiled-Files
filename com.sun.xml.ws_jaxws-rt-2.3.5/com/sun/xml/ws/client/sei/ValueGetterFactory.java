/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebParam$Mode
 */
package com.sun.xml.ws.client.sei;

import com.sun.xml.ws.client.sei.ValueGetter;
import com.sun.xml.ws.model.ParameterImpl;
import javax.jws.WebParam;

abstract class ValueGetterFactory {
    static final ValueGetterFactory SYNC = new ValueGetterFactory(){

        @Override
        ValueGetter get(ParameterImpl p) {
            return p.getMode() == WebParam.Mode.IN || p.getIndex() == -1 ? ValueGetter.PLAIN : ValueGetter.HOLDER;
        }
    };
    static final ValueGetterFactory ASYNC = new ValueGetterFactory(){

        @Override
        ValueGetter get(ParameterImpl p) {
            return ValueGetter.PLAIN;
        }
    };

    ValueGetterFactory() {
    }

    abstract ValueGetter get(ParameterImpl var1);
}

