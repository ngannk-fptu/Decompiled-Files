/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.property;

import java.text.ParsePosition;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.property.ParseNextProperty;

public interface PropertyExpander
extends PropertyHelper.Delegate {
    public String parsePropertyName(String var1, ParsePosition var2, ParseNextProperty var3);
}

