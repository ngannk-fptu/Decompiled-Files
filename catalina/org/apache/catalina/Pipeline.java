/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.util.Set;
import org.apache.catalina.Contained;
import org.apache.catalina.Valve;

public interface Pipeline
extends Contained {
    public Valve getBasic();

    public void setBasic(Valve var1);

    public void addValve(Valve var1);

    public Valve[] getValves();

    public void removeValve(Valve var1);

    public Valve getFirst();

    public boolean isAsyncSupported();

    public void findNonAsyncValves(Set<String> var1);
}

