/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

import com.sun.xml.txw2.NamespaceResolver;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;

public interface DatatypeWriter<DT> {
    public static final List<DatatypeWriter<?>> BUILTIN = Collections.unmodifiableList(new AbstractList(){
        private DatatypeWriter<?>[] BUILTIN_ARRAY = new DatatypeWriter[]{new DatatypeWriter<String>(){

            @Override
            public Class<String> getType() {
                return String.class;
            }

            @Override
            public void print(String s, NamespaceResolver resolver, StringBuilder buf) {
                buf.append(s);
            }
        }, new DatatypeWriter<Integer>(){

            @Override
            public Class<Integer> getType() {
                return Integer.class;
            }

            @Override
            public void print(Integer i, NamespaceResolver resolver, StringBuilder buf) {
                buf.append(i);
            }
        }, new DatatypeWriter<Float>(){

            @Override
            public Class<Float> getType() {
                return Float.class;
            }

            @Override
            public void print(Float f, NamespaceResolver resolver, StringBuilder buf) {
                buf.append(f);
            }
        }, new DatatypeWriter<Double>(){

            @Override
            public Class<Double> getType() {
                return Double.class;
            }

            @Override
            public void print(Double d, NamespaceResolver resolver, StringBuilder buf) {
                buf.append(d);
            }
        }, new DatatypeWriter<QName>(){

            @Override
            public Class<QName> getType() {
                return QName.class;
            }

            @Override
            public void print(QName qn, NamespaceResolver resolver, StringBuilder buf) {
                String p = resolver.getPrefix(qn.getNamespaceURI());
                if (p.length() != 0) {
                    buf.append(p).append(':');
                }
                buf.append(qn.getLocalPart());
            }
        }};

        @Override
        public DatatypeWriter<?> get(int n) {
            return this.BUILTIN_ARRAY[n];
        }

        @Override
        public int size() {
            return this.BUILTIN_ARRAY.length;
        }
    });

    public Class<DT> getType();

    public void print(DT var1, NamespaceResolver var2, StringBuilder var3);
}

