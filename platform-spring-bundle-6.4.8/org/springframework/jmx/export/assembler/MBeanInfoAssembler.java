/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.assembler;

import javax.management.JMException;
import javax.management.modelmbean.ModelMBeanInfo;

public interface MBeanInfoAssembler {
    public ModelMBeanInfo getMBeanInfo(Object var1, String var2) throws JMException;
}

