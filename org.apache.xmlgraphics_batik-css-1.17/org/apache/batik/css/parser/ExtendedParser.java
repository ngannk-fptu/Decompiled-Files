/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.CSSException
 *  org.w3c.css.sac.LexicalUnit
 *  org.w3c.css.sac.Parser
 *  org.w3c.css.sac.SACMediaList
 *  org.w3c.css.sac.SelectorList
 */
package org.apache.batik.css.parser;

import java.io.IOException;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

public interface ExtendedParser
extends Parser {
    public void parseStyleDeclaration(String var1) throws CSSException, IOException;

    public void parseRule(String var1) throws CSSException, IOException;

    public SelectorList parseSelectors(String var1) throws CSSException, IOException;

    public LexicalUnit parsePropertyValue(String var1) throws CSSException, IOException;

    public SACMediaList parseMedia(String var1) throws CSSException, IOException;

    public boolean parsePriority(String var1) throws CSSException, IOException;
}

