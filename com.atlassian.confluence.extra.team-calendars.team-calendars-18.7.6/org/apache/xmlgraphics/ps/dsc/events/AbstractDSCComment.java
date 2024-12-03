/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.util.ArrayList;
import java.util.List;
import org.apache.xmlgraphics.ps.dsc.events.AbstractEvent;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;

public abstract class AbstractDSCComment
extends AbstractEvent
implements DSCComment {
    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t';
    }

    private int parseNextParam(String value, int pos, List lst) {
        int startPos = pos++;
        while (pos < value.length() && !this.isWhitespace(value.charAt(pos))) {
            ++pos;
        }
        String param = value.substring(startPos, pos);
        lst.add(param);
        return pos;
    }

    private int parseNextParentheseString(String value, int pos, List lst) {
        int nestLevel = 1;
        ++pos;
        StringBuffer sb = new StringBuffer();
        while (pos < value.length() && nestLevel > 0) {
            char c = value.charAt(pos);
            block0 : switch (c) {
                case '(': {
                    if (++nestLevel <= 1) break;
                    sb.append(c);
                    break;
                }
                case ')': {
                    if (nestLevel > 1) {
                        sb.append(c);
                    }
                    --nestLevel;
                    break;
                }
                case '\\': {
                    char cnext = value.charAt(++pos);
                    switch (cnext) {
                        case '\\': {
                            sb.append(cnext);
                            break block0;
                        }
                        case 'n': {
                            sb.append('\n');
                            break block0;
                        }
                        case 'r': {
                            sb.append('\r');
                            break block0;
                        }
                        case 't': {
                            sb.append('\t');
                            break block0;
                        }
                        case 'b': {
                            sb.append('\b');
                            break block0;
                        }
                        case 'f': {
                            sb.append('\f');
                            break block0;
                        }
                        case '(': {
                            sb.append('(');
                            break block0;
                        }
                        case ')': {
                            sb.append(')');
                            break block0;
                        }
                    }
                    int code = Integer.parseInt(value.substring(pos, pos + 3), 8);
                    sb.append((char)code);
                    pos += 2;
                    break;
                }
                default: {
                    sb.append(c);
                }
            }
            ++pos;
        }
        lst.add(sb.toString());
        return ++pos;
    }

    protected List splitParams(String value) {
        ArrayList lst = new ArrayList();
        int pos = 0;
        value = value.trim();
        while (pos < value.length()) {
            if (this.isWhitespace(value.charAt(pos))) {
                ++pos;
                continue;
            }
            if (value.charAt(pos) == '(') {
                pos = this.parseNextParentheseString(value, pos, lst);
                continue;
            }
            pos = this.parseNextParam(value, pos, lst);
        }
        return lst;
    }

    @Override
    public boolean isAtend() {
        return false;
    }

    @Override
    public DSCComment asDSCComment() {
        return this;
    }

    @Override
    public boolean isDSCComment() {
        return true;
    }

    @Override
    public int getEventType() {
        return 1;
    }
}

