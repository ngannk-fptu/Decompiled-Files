/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import org.hibernate.validator.internal.xml.mapping.AbstractOneLineStringStaxBuilder;

class DefaultPackageStaxBuilder
extends AbstractOneLineStringStaxBuilder {
    private static final String DEFAULT_PACKAGE_QNAME = "default-package";

    DefaultPackageStaxBuilder() {
    }

    @Override
    protected String getAcceptableQName() {
        return DEFAULT_PACKAGE_QNAME;
    }
}

