/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client.sei;

import com.sun.xml.ws.client.sei.ValueSetter;
import com.sun.xml.ws.model.ParameterImpl;
import javax.xml.ws.WebServiceException;

public abstract class ValueSetterFactory {
    public static final ValueSetterFactory SYNC = new ValueSetterFactory(){

        @Override
        public ValueSetter get(ParameterImpl p) {
            return ValueSetter.getSync(p);
        }
    };
    public static final ValueSetterFactory NONE = new ValueSetterFactory(){

        @Override
        public ValueSetter get(ParameterImpl p) {
            throw new WebServiceException("This shouldn't happen. No response parameters.");
        }
    };
    public static final ValueSetterFactory SINGLE = new ValueSetterFactory(){

        @Override
        public ValueSetter get(ParameterImpl p) {
            return ValueSetter.SINGLE_VALUE;
        }
    };

    public abstract ValueSetter get(ParameterImpl var1);

    public static final class AsyncBeanValueSetterFactory
    extends ValueSetterFactory {
        private Class asyncBean;

        public AsyncBeanValueSetterFactory(Class asyncBean) {
            this.asyncBean = asyncBean;
        }

        @Override
        public ValueSetter get(ParameterImpl p) {
            return new ValueSetter.AsyncBeanValueSetter(p, this.asyncBean);
        }
    }
}

