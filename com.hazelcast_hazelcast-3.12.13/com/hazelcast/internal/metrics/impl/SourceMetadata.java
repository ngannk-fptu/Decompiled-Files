/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.impl;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.impl.FieldProbe;
import com.hazelcast.internal.metrics.impl.MethodProbe;
import com.hazelcast.internal.metrics.impl.MetricsRegistryImpl;
import com.hazelcast.internal.metrics.impl.ProbeUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final class SourceMetadata {
    private final List<FieldProbe> fields = new ArrayList<FieldProbe>();
    private final List<MethodProbe> methods = new ArrayList<MethodProbe>();

    SourceMetadata(Class clazz) {
        ArrayList classList = new ArrayList();
        ProbeUtils.flatten(clazz, classList);
        for (Class clazz2 : classList) {
            this.scanFields(clazz2);
            this.scanMethods(clazz2);
        }
    }

    void register(MetricsRegistryImpl metricsRegistry, Object source, String namePrefix) {
        for (FieldProbe field : this.fields) {
            field.register(metricsRegistry, source, namePrefix);
        }
        for (MethodProbe method : this.methods) {
            method.register(metricsRegistry, source, namePrefix);
        }
    }

    private void scanFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            Probe probe = field.getAnnotation(Probe.class);
            if (probe == null) continue;
            FieldProbe fieldProbe = FieldProbe.createFieldProbe(field, probe);
            this.fields.add(fieldProbe);
        }
    }

    private void scanMethods(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            Probe probe = method.getAnnotation(Probe.class);
            if (probe == null) continue;
            MethodProbe methodProbe = MethodProbe.createMethodProbe(method, probe);
            this.methods.add(methodProbe);
        }
    }

    public List<FieldProbe> fields() {
        return this.fields;
    }

    public List<MethodProbe> methods() {
        return this.methods;
    }
}

