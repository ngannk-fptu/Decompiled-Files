/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.i18n.Localizable
 */
package org.apache.batik.parser;

import java.io.Reader;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.parser.ErrorHandler;
import org.apache.batik.parser.ParseException;

public interface Parser
extends Localizable {
    public void parse(Reader var1) throws ParseException;

    public void parse(String var1) throws ParseException;

    public void setErrorHandler(ErrorHandler var1);
}

