/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;

public interface Acceptor {
    public static final int STRING_PROHIBITED = 0;
    public static final int STRING_IGNORE = 1;
    public static final int STRING_STRICT = 2;

    public Acceptor createChildAcceptor(StartTagInfo var1, StringRef var2);

    public boolean onAttribute2(String var1, String var2, String var3, String var4, IDContextProvider2 var5, StringRef var6, DatatypeRef var7);

    public boolean onAttribute(String var1, String var2, String var3, String var4, IDContextProvider var5, StringRef var6, DatatypeRef var7);

    public boolean onEndAttributes(StartTagInfo var1, StringRef var2);

    public boolean onText2(String var1, IDContextProvider2 var2, StringRef var3, DatatypeRef var4);

    public boolean onText(String var1, IDContextProvider var2, StringRef var3, DatatypeRef var4);

    public boolean stepForward(Acceptor var1, StringRef var2);

    public boolean isAcceptState(StringRef var1);

    public Object getOwnerType();

    public Acceptor createClone();

    public int getStringCareLevel();
}

