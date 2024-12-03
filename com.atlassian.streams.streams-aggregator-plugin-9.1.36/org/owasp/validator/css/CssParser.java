/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.parser.Parser
 */
package org.owasp.validator.css;

import org.apache.batik.css.parser.Parser;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.LexicalUnit;

public class CssParser
extends Parser {
    protected void parseStyleDeclaration(boolean inSheet) throws CSSException {
        boolean leadingDash = false;
        block9: while (true) {
            switch (this.current) {
                case 0: {
                    if (inSheet) {
                        throw this.createCSSParseException("eof");
                    }
                    return;
                }
                case 2: {
                    if (!inSheet) {
                        throw this.createCSSParseException("eof.expected");
                    }
                    this.nextIgnoreSpaces();
                    return;
                }
                case 8: {
                    this.nextIgnoreSpaces();
                    continue block9;
                }
                case 5: {
                    leadingDash = true;
                    this.next();
                    break;
                }
                default: {
                    throw this.createCSSParseException("identifier");
                }
                case 20: 
            }
            String name = (leadingDash ? "-" : "") + this.scanner.getStringValue();
            leadingDash = false;
            if (this.nextIgnoreSpaces() != 16) {
                throw this.createCSSParseException("colon");
            }
            this.nextIgnoreSpaces();
            LexicalUnit exp = null;
            try {
                exp = this.parseExpression(false);
            }
            catch (CSSParseException e) {
                this.reportError(e);
            }
            if (exp == null) continue;
            boolean important = false;
            if (this.current == 23) {
                important = true;
                this.nextIgnoreSpaces();
            }
            this.documentHandler.property(name, exp, important);
        }
    }
}

