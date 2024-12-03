/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.internal.util.StringHelper;

public class ImplicitNamingStrategyComponentPathImpl
extends ImplicitNamingStrategyJpaCompliantImpl {
    public static final ImplicitNamingStrategyComponentPathImpl INSTANCE = new ImplicitNamingStrategyComponentPathImpl();

    @Override
    protected String transformAttributePath(AttributePath attributePath) {
        StringBuilder sb = new StringBuilder();
        ImplicitNamingStrategyComponentPathImpl.process(attributePath, sb);
        return sb.toString();
    }

    public static void process(AttributePath attributePath, StringBuilder sb) {
        String property = attributePath.getProperty();
        AttributePath parent = attributePath.getParent();
        if (parent != null && StringHelper.isNotEmpty(parent.getProperty())) {
            ImplicitNamingStrategyComponentPathImpl.process(parent, sb);
            sb.append('_');
        } else if ("_identifierMapper".equals(property)) {
            sb.append("id");
            return;
        }
        property = property.replace("<", "");
        property = property.replace(">", "");
        sb.append(property);
    }
}

