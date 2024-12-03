/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset;

import java.util.Map;

public interface FastInfosetParser {
    public static final String STRING_INTERNING_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/string-interning";
    public static final String BUFFER_SIZE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/buffer-size";
    public static final String REGISTERED_ENCODING_ALGORITHMS_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
    public static final String EXTERNAL_VOCABULARIES_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
    public static final String FORCE_STREAM_CLOSE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/force-stream-close";

    public void setStringInterning(boolean var1);

    public boolean getStringInterning();

    public void setBufferSize(int var1);

    public int getBufferSize();

    public void setRegisteredEncodingAlgorithms(Map var1);

    public Map getRegisteredEncodingAlgorithms();

    public void setExternalVocabularies(Map var1);

    public Map getExternalVocabularies();

    public void setParseFragments(boolean var1);

    public boolean getParseFragments();

    public void setForceStreamClose(boolean var1);

    public boolean getForceStreamClose();
}

