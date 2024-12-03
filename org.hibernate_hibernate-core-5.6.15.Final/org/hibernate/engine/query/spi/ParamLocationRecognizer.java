/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.engine.query.ParameterRecognitionException;
import org.hibernate.engine.query.spi.AbstractParameterDescriptor;
import org.hibernate.engine.query.spi.NamedParameterDescriptor;
import org.hibernate.engine.query.spi.OrdinalParameterDescriptor;
import org.hibernate.engine.query.spi.ParameterParser;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;

public class ParamLocationRecognizer
implements ParameterParser.Recognizer {
    private Map<String, NamedParameterDescriptor> namedParameterDescriptors;
    private Map<Integer, OrdinalParameterDescriptor> ordinalParameterDescriptors;
    private Map<String, InFlightNamedParameterState> inFlightNamedStateMap;
    private Map<Integer, InFlightOrdinalParameterState> inFlightOrdinalStateMap;
    private Map<Integer, InFlightJpaOrdinalParameterState> inFlightJpaOrdinalStateMap;
    private final int jdbcStyleOrdinalCountBase;
    private int jdbcStyleOrdinalCount;

    public ParamLocationRecognizer(int jdbcStyleOrdinalCountBase) {
        this.jdbcStyleOrdinalCountBase = jdbcStyleOrdinalCountBase;
        this.jdbcStyleOrdinalCount = jdbcStyleOrdinalCountBase;
    }

    public static ParamLocationRecognizer parseLocations(String query, SessionFactoryImplementor sessionFactory) {
        ParamLocationRecognizer recognizer = new ParamLocationRecognizer(sessionFactory.getSessionFactoryOptions().jdbcStyleParamsZeroBased() ? 0 : 1);
        ParameterParser.parse(query, recognizer);
        return recognizer;
    }

    @Override
    public void complete() {
        HashMap<Object, AbstractParameterDescriptor> tmp;
        if (this.inFlightNamedStateMap != null && (this.inFlightOrdinalStateMap != null || this.inFlightJpaOrdinalStateMap != null)) {
            throw this.mixedParamStrategy();
        }
        if (this.inFlightOrdinalStateMap != null && this.inFlightJpaOrdinalStateMap != null) {
            throw this.mixedParamStrategy();
        }
        if (this.inFlightNamedStateMap != null) {
            tmp = new HashMap<Object, AbstractParameterDescriptor>();
            for (InFlightNamedParameterState inFlightNamedParameterState : this.inFlightNamedStateMap.values()) {
                tmp.put(inFlightNamedParameterState.name, inFlightNamedParameterState.complete());
            }
            this.namedParameterDescriptors = Collections.unmodifiableMap(tmp);
        } else {
            this.namedParameterDescriptors = Collections.emptyMap();
        }
        if (this.inFlightOrdinalStateMap == null && this.inFlightJpaOrdinalStateMap == null) {
            this.ordinalParameterDescriptors = Collections.emptyMap();
        } else {
            tmp = new HashMap();
            if (this.inFlightOrdinalStateMap != null) {
                for (InFlightOrdinalParameterState inFlightOrdinalParameterState : this.inFlightOrdinalStateMap.values()) {
                    tmp.put(inFlightOrdinalParameterState.identifier, inFlightOrdinalParameterState.complete());
                }
            } else {
                for (InFlightJpaOrdinalParameterState inFlightJpaOrdinalParameterState : this.inFlightJpaOrdinalStateMap.values()) {
                    tmp.put(inFlightJpaOrdinalParameterState.identifier, inFlightJpaOrdinalParameterState.complete());
                }
            }
            this.ordinalParameterDescriptors = Collections.unmodifiableMap(tmp);
        }
    }

    private ParameterRecognitionException mixedParamStrategy() {
        throw new ParameterRecognitionException("Mixed parameter strategies - use just one of named, positional or JPA-ordinal strategy");
    }

    public Map<String, NamedParameterDescriptor> getNamedParameterDescriptionMap() {
        return this.namedParameterDescriptors;
    }

    public Map<Integer, OrdinalParameterDescriptor> getOrdinalParameterDescriptionMap() {
        return this.ordinalParameterDescriptors;
    }

    @Override
    public void ordinalParameter(int position) {
        if (this.inFlightOrdinalStateMap == null) {
            this.inFlightOrdinalStateMap = new HashMap<Integer, InFlightOrdinalParameterState>();
        }
        int label = this.jdbcStyleOrdinalCount++;
        this.inFlightOrdinalStateMap.put(label, new InFlightOrdinalParameterState(label, label - this.jdbcStyleOrdinalCountBase, position));
    }

    @Override
    public void namedParameter(String name, int position) {
        this.getOrBuildNamedParameterDescription(name).add(position);
    }

    private InFlightNamedParameterState getOrBuildNamedParameterDescription(String name) {
        InFlightNamedParameterState descriptor;
        if (this.inFlightNamedStateMap == null) {
            this.inFlightNamedStateMap = new HashMap<String, InFlightNamedParameterState>();
        }
        if ((descriptor = this.inFlightNamedStateMap.get(name)) == null) {
            descriptor = new InFlightNamedParameterState(name);
            this.inFlightNamedStateMap.put(name, descriptor);
        }
        return descriptor;
    }

    @Override
    public void jpaPositionalParameter(int name, int position) {
        this.getOrBuildJpaOrdinalParameterDescription(name).add(position);
    }

    private InFlightJpaOrdinalParameterState getOrBuildJpaOrdinalParameterDescription(int name) {
        InFlightJpaOrdinalParameterState descriptor;
        if (this.inFlightJpaOrdinalStateMap == null) {
            this.inFlightJpaOrdinalStateMap = new HashMap<Integer, InFlightJpaOrdinalParameterState>();
        }
        if ((descriptor = this.inFlightJpaOrdinalStateMap.get(name)) == null) {
            descriptor = new InFlightJpaOrdinalParameterState(name);
            this.inFlightJpaOrdinalStateMap.put(name, descriptor);
        }
        return descriptor;
    }

    @Override
    public void other(char character) {
    }

    @Override
    public void outParameter(int position) {
    }

    public static class InFlightJpaOrdinalParameterState {
        private final int identifier;
        private final List<Integer> sourcePositions = new ArrayList<Integer>();

        InFlightJpaOrdinalParameterState(int identifier) {
            this.identifier = identifier;
        }

        private void add(int position) {
            this.sourcePositions.add(position);
        }

        private OrdinalParameterDescriptor complete() {
            return new OrdinalParameterDescriptor(this.identifier, this.identifier - 1, null, ArrayHelper.toIntArray(this.sourcePositions));
        }
    }

    public static class InFlightOrdinalParameterState {
        private final int identifier;
        private final int valuePosition;
        private final int sourcePosition;

        InFlightOrdinalParameterState(int label, int valuePosition, int sourcePosition) {
            this.identifier = label;
            this.valuePosition = valuePosition;
            this.sourcePosition = sourcePosition;
        }

        private OrdinalParameterDescriptor complete() {
            return new OrdinalParameterDescriptor(this.identifier, this.valuePosition, null, new int[]{this.sourcePosition});
        }
    }

    public static class InFlightNamedParameterState {
        private final String name;
        private final List<Integer> sourcePositions = new ArrayList<Integer>();

        InFlightNamedParameterState(String name) {
            this.name = name;
        }

        private void add(int position) {
            this.sourcePositions.add(position);
        }

        private NamedParameterDescriptor complete() {
            return new NamedParameterDescriptor(this.name, null, ArrayHelper.toIntArray(this.sourcePositions));
        }
    }
}

