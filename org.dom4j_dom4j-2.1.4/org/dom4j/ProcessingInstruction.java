/*
 * Decompiled with CFR 0.152.
 */
package org.dom4j;

import java.util.Map;
import org.dom4j.Node;

public interface ProcessingInstruction
extends Node {
    public String getTarget();

    public void setTarget(String var1);

    @Override
    public String getText();

    public String getValue(String var1);

    public Map<String, String> getValues();

    public void setValue(String var1, String var2);

    public void setValues(Map<String, String> var1);

    public boolean removeValue(String var1);
}

