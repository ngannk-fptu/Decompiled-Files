/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonText;
import software.amazon.ion.UnknownSymbolException;

public interface IonString
extends IonText {
    public String stringValue();

    public void setValue(String var1);

    public IonString clone() throws UnknownSymbolException;
}

