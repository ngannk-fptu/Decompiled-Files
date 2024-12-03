/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.Holder
 */
package com.sun.xml.ws.client.sei;

import javax.xml.ws.Holder;

enum ValueGetter {
    PLAIN{

        @Override
        Object get(Object parameter) {
            return parameter;
        }
    }
    ,
    HOLDER{

        @Override
        Object get(Object parameter) {
            if (parameter == null) {
                return null;
            }
            return ((Holder)parameter).value;
        }
    };


    abstract Object get(Object var1);
}

