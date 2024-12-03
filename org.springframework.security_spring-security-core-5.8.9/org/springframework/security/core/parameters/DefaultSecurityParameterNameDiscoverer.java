/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.core.PrioritizedParameterNameDiscoverer
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.security.core.parameters;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.PrioritizedParameterNameDiscoverer;
import org.springframework.security.core.parameters.AnnotationParameterNameDiscoverer;
import org.springframework.security.core.parameters.P;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class DefaultSecurityParameterNameDiscoverer
extends PrioritizedParameterNameDiscoverer {
    private static final String DATA_PARAM_CLASSNAME = "org.springframework.data.repository.query.Param";
    private static final boolean DATA_PARAM_PRESENT = ClassUtils.isPresent((String)"org.springframework.data.repository.query.Param", (ClassLoader)DefaultSecurityParameterNameDiscoverer.class.getClassLoader());

    public DefaultSecurityParameterNameDiscoverer() {
        this(Collections.emptyList());
    }

    public DefaultSecurityParameterNameDiscoverer(List<? extends ParameterNameDiscoverer> parameterNameDiscovers) {
        Assert.notNull(parameterNameDiscovers, (String)"parameterNameDiscovers cannot be null");
        for (ParameterNameDiscoverer parameterNameDiscoverer : parameterNameDiscovers) {
            this.addDiscoverer(parameterNameDiscoverer);
        }
        HashSet<String> annotationClassesToUse = new HashSet<String>(2);
        annotationClassesToUse.add("org.springframework.security.access.method.P");
        annotationClassesToUse.add(P.class.getName());
        if (DATA_PARAM_PRESENT) {
            annotationClassesToUse.add(DATA_PARAM_CLASSNAME);
        }
        this.addDiscoverer(new AnnotationParameterNameDiscoverer(annotationClassesToUse));
        this.addDiscoverer((ParameterNameDiscoverer)new DefaultParameterNameDiscoverer());
    }
}

