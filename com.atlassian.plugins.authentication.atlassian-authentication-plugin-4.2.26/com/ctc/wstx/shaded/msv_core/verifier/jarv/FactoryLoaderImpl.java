/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactory;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactoryLoader;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.DTDFactoryImpl;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.RELAXCoreFactoryImpl;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.RELAXNGFactoryImpl;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.TREXFactoryImpl;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.TheFactoryImpl;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.XSFactoryImpl;

public class FactoryLoaderImpl
implements VerifierFactoryLoader {
    public VerifierFactory createFactory(String language) {
        if (language.equals("http://relaxng.org/ns/structure/1.0")) {
            return new RELAXNGFactoryImpl();
        }
        if (language.equals("http://www.xml.gr.jp/xmlns/relaxCore")) {
            return new RELAXCoreFactoryImpl();
        }
        if (language.equals("http://www.thaiopensource.com/trex")) {
            return new TREXFactoryImpl();
        }
        if (language.equals("http://www.w3.org/2001/XMLSchema") || language.equals("http://www.w3.org/2000/10/XMLSchema")) {
            return new XSFactoryImpl();
        }
        if (language.equals("http://www.xml.gr.jp/xmlns/relaxNamespace")) {
            return new TheFactoryImpl();
        }
        if (language.equals("http://www.w3.org/XML/1998/namespace")) {
            return new DTDFactoryImpl();
        }
        if (language.equals("relax")) {
            return new TheFactoryImpl();
        }
        if (language.toUpperCase().equals("DTD")) {
            return new DTDFactoryImpl();
        }
        return null;
    }
}

