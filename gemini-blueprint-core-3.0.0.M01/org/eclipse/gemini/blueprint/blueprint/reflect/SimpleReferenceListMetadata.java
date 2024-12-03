/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.factory.config.BeanDefinition
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataUtils;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleServiceReferenceComponentMetadata;
import org.eclipse.gemini.blueprint.service.importer.support.MemberType;
import org.osgi.service.blueprint.reflect.ReferenceListMetadata;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;

class SimpleReferenceListMetadata
extends SimpleServiceReferenceComponentMetadata
implements ReferenceListMetadata {
    private final int memberType;
    private static final String MEMBER_TYPE_PROP = "memberType";

    public SimpleReferenceListMetadata(String name, BeanDefinition definition) {
        super(name, definition);
        MemberType type = MemberType.SERVICE_OBJECT;
        MutablePropertyValues pvs = this.beanDefinition.getPropertyValues();
        if (pvs.contains(MEMBER_TYPE_PROP)) {
            type = (MemberType)((Object)MetadataUtils.getValue((PropertyValues)pvs, MEMBER_TYPE_PROP));
        }
        this.memberType = type.ordinal() + 1;
    }

    @Override
    public int getMemberType() {
        return this.memberType;
    }
}

