/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.hql.internal.ast.NamedParameterInformationImpl;
import org.hibernate.hql.internal.ast.PositionalParameterInformationImpl;
import org.hibernate.hql.spi.NamedParameterInformation;
import org.hibernate.hql.spi.ParameterInformation;
import org.hibernate.hql.spi.ParameterTranslations;
import org.hibernate.hql.spi.PositionalParameterInformation;
import org.hibernate.param.NamedParameterSpecification;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.param.PositionalParameterSpecification;

public class ParameterTranslationsImpl
implements ParameterTranslations {
    private final Map<String, NamedParameterInformationImpl> namedParameters;
    private final Map<Integer, PositionalParameterInformationImpl> ordinalParameters;

    ParameterTranslationsImpl(List<ParameterSpecification> parameterSpecifications) {
        HashMap<String, NamedParameterInformationImpl> namedParameters = null;
        HashMap<Integer, PositionalParameterInformationImpl> ordinalParameters = null;
        int i = 0;
        for (ParameterSpecification specification : parameterSpecifications) {
            NamedParameterSpecification namedSpecification;
            ParameterInformation info;
            if (PositionalParameterSpecification.class.isInstance(specification)) {
                if (ordinalParameters == null) {
                    ordinalParameters = new HashMap<Integer, PositionalParameterInformationImpl>();
                }
                PositionalParameterSpecification ordinalSpecification = (PositionalParameterSpecification)specification;
                info = ParameterTranslationsImpl.getPositionalParameterInfo(ordinalParameters, ordinalSpecification);
                ((PositionalParameterInformationImpl)info).addSourceLocation(i++);
                continue;
            }
            if (!NamedParameterSpecification.class.isInstance(specification)) continue;
            if (namedParameters == null) {
                namedParameters = new HashMap<String, NamedParameterInformationImpl>();
            }
            if (((NamedParameterInformationImpl)(info = this.getNamedParameterInfo(namedParameters, namedSpecification = (NamedParameterSpecification)specification))).getExpectedType() == null && namedSpecification.getExpectedType() != null) {
                ((NamedParameterInformationImpl)info).setExpectedType(namedSpecification.getExpectedType());
            }
            ((NamedParameterInformationImpl)info).addSourceLocation(i++);
        }
        this.namedParameters = namedParameters == null ? Collections.emptyMap() : Collections.unmodifiableMap(namedParameters);
        this.ordinalParameters = ordinalParameters == null ? Collections.emptyMap() : Collections.unmodifiableMap(ordinalParameters);
    }

    private NamedParameterInformationImpl getNamedParameterInfo(Map<String, NamedParameterInformationImpl> namedParameters, NamedParameterSpecification namedSpecification) {
        String name = namedSpecification.getName();
        NamedParameterInformationImpl namedParameterInformation = namedParameters.get(name);
        if (namedParameterInformation == null) {
            namedParameterInformation = new NamedParameterInformationImpl(name, namedSpecification.getExpectedType());
            namedParameters.put(name, namedParameterInformation);
        }
        return namedParameterInformation;
    }

    private static PositionalParameterInformationImpl getPositionalParameterInfo(Map<Integer, PositionalParameterInformationImpl> ordinalParameters, PositionalParameterSpecification ordinalSpecification) {
        Integer label = ordinalSpecification.getLabel();
        PositionalParameterInformationImpl positionalParameterInformation = ordinalParameters.get(label);
        if (positionalParameterInformation == null) {
            positionalParameterInformation = new PositionalParameterInformationImpl(label, ordinalSpecification.getExpectedType());
            ordinalParameters.put(label, positionalParameterInformation);
        }
        return positionalParameterInformation;
    }

    public Map getNamedParameterInformationMap() {
        return this.namedParameters;
    }

    public Map getPositionalParameterInformationMap() {
        return this.ordinalParameters;
    }

    @Override
    public PositionalParameterInformation getPositionalParameterInformation(int position) {
        return this.ordinalParameters.get(position);
    }

    @Override
    public NamedParameterInformation getNamedParameterInformation(String name) {
        return this.namedParameters.get(name);
    }
}

