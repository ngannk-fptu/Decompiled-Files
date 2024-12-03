/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ProcessingInstruction {
    public void discard();

    public Factory getFactory();

    public <T extends Base> T getParentElement();

    public String getTarget();

    public void setTarget(String var1);

    public String getText();

    public <T extends ProcessingInstruction> T setText(String var1);
}

