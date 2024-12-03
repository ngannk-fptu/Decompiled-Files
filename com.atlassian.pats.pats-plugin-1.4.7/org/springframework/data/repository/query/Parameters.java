/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.query;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParameterOutOfBoundsException;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

public abstract class Parameters<S extends Parameters<S, T>, T extends Parameter>
implements Streamable<T> {
    public static final List<Class<?>> TYPES = Arrays.asList(Pageable.class, Sort.class);
    private static final String PARAM_ON_SPECIAL = String.format("You must not user @%s on a parameter typed %s or %s", Param.class.getSimpleName(), Pageable.class.getSimpleName(), Sort.class.getSimpleName());
    private static final String ALL_OR_NOTHING = String.format("Either use @%s on all parameters except %s and %s typed once, or none at all!", Param.class.getSimpleName(), Pageable.class.getSimpleName(), Sort.class.getSimpleName());
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    private final int pageableIndex;
    private final int sortIndex;
    private final List<T> parameters;
    private final Lazy<S> bindable;
    private int dynamicProjectionIndex;

    public Parameters(Method method) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        int parameterCount = method.getParameterCount();
        this.parameters = new ArrayList<T>(parameterCount);
        this.dynamicProjectionIndex = -1;
        int pageableIndex = -1;
        int sortIndex = -1;
        for (int i = 0; i < parameterCount; ++i) {
            MethodParameter methodParameter = new MethodParameter(method, i);
            methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
            T parameter = this.createParameter(methodParameter);
            if (((Parameter)parameter).isSpecialParameter() && ((Parameter)parameter).isNamedParameter()) {
                throw new IllegalArgumentException(PARAM_ON_SPECIAL);
            }
            if (((Parameter)parameter).isDynamicProjectionParameter()) {
                this.dynamicProjectionIndex = ((Parameter)parameter).getIndex();
            }
            if (Pageable.class.isAssignableFrom(((Parameter)parameter).getType())) {
                pageableIndex = i;
            }
            if (Sort.class.isAssignableFrom(((Parameter)parameter).getType())) {
                sortIndex = i;
            }
            this.parameters.add(parameter);
        }
        this.pageableIndex = pageableIndex;
        this.sortIndex = sortIndex;
        this.bindable = Lazy.of(this::getBindable);
        this.assertEitherAllParamAnnotatedOrNone();
    }

    protected Parameters(List<T> originals) {
        this.parameters = new ArrayList<T>(originals.size());
        int pageableIndexTemp = -1;
        int sortIndexTemp = -1;
        int dynamicProjectionTemp = -1;
        for (int i = 0; i < originals.size(); ++i) {
            Parameter original = (Parameter)originals.get(i);
            this.parameters.add(original);
            pageableIndexTemp = original.isPageable() ? i : -1;
            sortIndexTemp = original.isSort() ? i : -1;
            dynamicProjectionTemp = original.isDynamicProjectionParameter() ? i : -1;
        }
        this.pageableIndex = pageableIndexTemp;
        this.sortIndex = sortIndexTemp;
        this.dynamicProjectionIndex = dynamicProjectionTemp;
        this.bindable = Lazy.of(() -> this);
    }

    private S getBindable() {
        ArrayList<Parameter> bindables = new ArrayList<Parameter>();
        for (Parameter candidate : this) {
            if (!candidate.isBindable()) continue;
            bindables.add(candidate);
        }
        return this.createFrom(bindables);
    }

    protected abstract T createParameter(MethodParameter var1);

    public boolean hasPageableParameter() {
        return this.pageableIndex != -1;
    }

    public int getPageableIndex() {
        return this.pageableIndex;
    }

    public int getSortIndex() {
        return this.sortIndex;
    }

    public boolean hasSortParameter() {
        return this.sortIndex != -1;
    }

    public int getDynamicProjectionIndex() {
        return this.dynamicProjectionIndex;
    }

    public boolean hasDynamicProjection() {
        return this.dynamicProjectionIndex != -1;
    }

    public boolean potentiallySortsDynamically() {
        return this.hasSortParameter() || this.hasPageableParameter();
    }

    public T getParameter(int index) {
        try {
            return (T)((Parameter)this.parameters.get(index));
        }
        catch (IndexOutOfBoundsException e) {
            throw new ParameterOutOfBoundsException("Invalid parameter index! You seem to have declared too little query method parameters!", e);
        }
    }

    public boolean hasParameterAt(int position) {
        try {
            return null != this.getParameter(position);
        }
        catch (ParameterOutOfBoundsException e) {
            return false;
        }
    }

    public boolean hasSpecialParameter() {
        return this.hasSortParameter() || this.hasPageableParameter();
    }

    public int getNumberOfParameters() {
        return this.parameters.size();
    }

    public S getBindableParameters() {
        return (S)((Parameters)this.bindable.get());
    }

    protected abstract S createFrom(List<T> var1);

    public T getBindableParameter(int bindableIndex) {
        return ((Parameters)this.getBindableParameters()).getParameter(bindableIndex);
    }

    private void assertEitherAllParamAnnotatedOrNone() {
        boolean nameFound = false;
        int index = 0;
        for (Parameter parameter : this.getBindableParameters()) {
            if (parameter.isNamedParameter()) {
                Assert.isTrue((nameFound || index == 0 ? 1 : 0) != 0, (String)ALL_OR_NOTHING);
                nameFound = true;
            } else {
                Assert.isTrue((!nameFound ? 1 : 0) != 0, (String)ALL_OR_NOTHING);
            }
            ++index;
        }
    }

    public static boolean isBindable(Class<?> type) {
        return !TYPES.contains(type);
    }

    @Override
    public Iterator<T> iterator() {
        return this.parameters.iterator();
    }
}

