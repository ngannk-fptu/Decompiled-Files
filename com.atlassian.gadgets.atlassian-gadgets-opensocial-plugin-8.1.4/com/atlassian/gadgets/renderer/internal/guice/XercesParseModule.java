/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.AbstractModule
 *  com.google.inject.Provider
 *  org.apache.shindig.gadgets.parse.GadgetHtmlParser
 *  org.apache.shindig.gadgets.parse.nekohtml.NekoSimplifiedHtmlParser
 *  org.apache.xerces.dom.DOMImplementationImpl
 */
package com.atlassian.gadgets.renderer.internal.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import org.apache.shindig.gadgets.parse.GadgetHtmlParser;
import org.apache.shindig.gadgets.parse.nekohtml.NekoSimplifiedHtmlParser;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.w3c.dom.DOMImplementation;

public class XercesParseModule
extends AbstractModule {
    protected void configure() {
        this.bind(GadgetHtmlParser.class).to(NekoSimplifiedHtmlParser.class);
        this.bind(DOMImplementation.class).toProvider(XercesDOMImplementationProvider.class);
    }

    public static class XercesDOMImplementationProvider
    implements Provider<DOMImplementation> {
        public DOMImplementation get() {
            return DOMImplementationImpl.getDOMImplementation();
        }
    }
}

