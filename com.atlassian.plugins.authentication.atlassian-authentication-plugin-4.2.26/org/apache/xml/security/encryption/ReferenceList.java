/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.encryption;

import java.util.Iterator;
import org.apache.xml.security.encryption.Reference;

public interface ReferenceList {
    public static final int DATA_REFERENCE = 1;
    public static final int KEY_REFERENCE = 2;

    public void add(Reference var1);

    public void remove(Reference var1);

    public int size();

    public boolean isEmpty();

    public Iterator<Reference> getReferences();

    public Reference newDataReference(String var1);

    public Reference newKeyReference(String var1);
}

