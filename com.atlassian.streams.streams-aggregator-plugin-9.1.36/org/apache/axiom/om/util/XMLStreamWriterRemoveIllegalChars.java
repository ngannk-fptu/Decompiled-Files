/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.util;

import org.apache.axiom.om.util.XMLStreamWriterFilterBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XMLStreamWriterRemoveIllegalChars
extends XMLStreamWriterFilterBase {
    private static final Log log = LogFactory.getLog(XMLStreamWriterRemoveIllegalChars.class);
    private static byte[] REMOVE = new byte[32];
    private final int FFFE = 65534;
    private final char FFFF = (char)65535;
    private final char SURROGATE_START = (char)55296;
    private final char SURROGATE_END = (char)57343;

    public XMLStreamWriterRemoveIllegalChars() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Creating XMLStreamWriterRemoveIllegalChars object " + this));
        }
    }

    protected String xmlData(String value) {
        char[] buffer = null;
        int len = value.length();
        int srcI = 0;
        int tgtI = 0;
        int copyLength = 0;
        int i = 0;
        while (i < len) {
            int cp = value.codePointAt(i);
            if (cp > 65535) {
                i += 2;
                copyLength += 2;
                continue;
            }
            if (cp < 32 && REMOVE[cp] > 0 || cp >= 55296 && cp <= 57343 || cp == 65535 || cp == 65534) {
                if (buffer == null) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("One or more illegal characterss found.  Codepoint=" + cp));
                    }
                    buffer = value.toCharArray();
                }
                System.arraycopy(buffer, srcI, buffer, tgtI, copyLength);
                tgtI += copyLength;
                srcI = i + 1;
                copyLength = 0;
            } else {
                ++copyLength;
            }
            ++i;
        }
        if (buffer == null) {
            return value;
        }
        System.arraycopy(buffer, srcI, buffer, tgtI, copyLength);
        String newValue = new String(buffer, 0, tgtI + copyLength);
        return newValue;
    }

    static {
        XMLStreamWriterRemoveIllegalChars.REMOVE[0] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[1] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[2] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[3] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[4] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[5] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[6] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[7] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[8] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[11] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[12] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[14] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[15] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[16] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[17] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[18] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[19] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[20] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[21] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[22] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[23] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[24] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[25] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[26] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[27] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[28] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[29] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[30] = 1;
        XMLStreamWriterRemoveIllegalChars.REMOVE[31] = 1;
    }
}

