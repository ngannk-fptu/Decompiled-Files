/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import java.io.IOException;
import java.util.Locale;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SelectorList;

public interface Parser {
    public void setLocale(Locale var1) throws CSSException;

    public void setDocumentHandler(DocumentHandler var1);

    public void setSelectorFactory(SelectorFactory var1);

    public void setConditionFactory(ConditionFactory var1);

    public void setErrorHandler(ErrorHandler var1);

    public void parseStyleSheet(InputSource var1) throws CSSException, IOException;

    public void parseStyleSheet(String var1) throws CSSException, IOException;

    public void parseStyleDeclaration(InputSource var1) throws CSSException, IOException;

    public void parseRule(InputSource var1) throws CSSException, IOException;

    public String getParserVersion();

    public SelectorList parseSelectors(InputSource var1) throws CSSException, IOException;

    public LexicalUnit parsePropertyValue(InputSource var1) throws CSSException, IOException;

    public boolean parsePriority(InputSource var1) throws CSSException, IOException;
}

