/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Iterator;
import org.aspectj.weaver.IWeaveRequestor;
import org.aspectj.weaver.bcel.UnwovenClassFile;

public interface IClassFileProvider {
    public Iterator<UnwovenClassFile> getClassFileIterator();

    public IWeaveRequestor getRequestor();

    public boolean isApplyAtAspectJMungersOnly();
}

