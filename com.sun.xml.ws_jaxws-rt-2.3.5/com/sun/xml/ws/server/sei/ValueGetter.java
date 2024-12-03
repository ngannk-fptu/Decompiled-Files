/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebParam$Mode
 *  javax.xml.ws.Holder
 */
package com.sun.xml.ws.server.sei;

import com.sun.xml.ws.model.ParameterImpl;
import javax.jws.WebParam;
import javax.xml.ws.Holder;

public enum ValueGetter {
    PLAIN{

        @Override
        public Object get(Object parameter) {
            return parameter;
        }
    }
    ,
    HOLDER{

        @Override
        public Object get(Object parameter) {
            if (parameter == null) {
                return null;
            }
            return ((Holder)parameter).value;
        }
    };


    public abstract Object get(Object var1);

    public static ValueGetter get(ParameterImpl p) {
        if (p.getMode() == WebParam.Mode.IN || p.getIndex() == -1) {
            return PLAIN;
        }
        return HOLDER;
    }
}

