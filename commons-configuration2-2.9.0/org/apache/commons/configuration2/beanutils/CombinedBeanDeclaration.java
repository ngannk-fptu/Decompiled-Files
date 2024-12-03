/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.beanutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.configuration2.beanutils.BeanDeclaration;
import org.apache.commons.configuration2.beanutils.ConstructorArg;

public class CombinedBeanDeclaration
implements BeanDeclaration {
    private final ArrayList<BeanDeclaration> childDeclarations;

    public CombinedBeanDeclaration(BeanDeclaration ... decl) {
        this.childDeclarations = new ArrayList<BeanDeclaration>(Arrays.asList(decl));
    }

    @Override
    public String getBeanFactoryName() {
        return this.findFirst(BeanDeclaration::getBeanFactoryName);
    }

    private <T> T findFirst(Function<? super BeanDeclaration, ? extends T> mapper) {
        return this.childDeclarations.stream().map(mapper).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Object getBeanFactoryParameter() {
        return this.findFirst(BeanDeclaration::getBeanFactoryParameter);
    }

    @Override
    public String getBeanClassName() {
        return this.findFirst(BeanDeclaration::getBeanClassName);
    }

    @Override
    public Map<String, Object> getBeanProperties() {
        return this.get(BeanDeclaration::getBeanProperties);
    }

    private Map<String, Object> get(Function<? super BeanDeclaration, ? extends Map<String, Object>> mapper) {
        ArrayList temp = (ArrayList)this.childDeclarations.clone();
        Collections.reverse(temp);
        return temp.stream().map(mapper).filter(Objects::nonNull).collect(HashMap::new, HashMap::putAll, HashMap::putAll);
    }

    @Override
    public Map<String, Object> getNestedBeanDeclarations() {
        return this.get(BeanDeclaration::getNestedBeanDeclarations);
    }

    @Override
    public Collection<ConstructorArg> getConstructorArgs() {
        for (BeanDeclaration d : this.childDeclarations) {
            Collection<ConstructorArg> args = d.getConstructorArgs();
            if (args == null || args.isEmpty()) continue;
            return args;
        }
        return Collections.emptyList();
    }
}

