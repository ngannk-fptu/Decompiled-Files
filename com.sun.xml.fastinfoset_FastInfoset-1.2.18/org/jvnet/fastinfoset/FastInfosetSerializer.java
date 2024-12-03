/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset;

import java.io.OutputStream;
import java.util.Map;
import org.jvnet.fastinfoset.ExternalVocabulary;
import org.jvnet.fastinfoset.VocabularyApplicationData;

public interface FastInfosetSerializer {
    public static final String IGNORE_DTD_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/DTD";
    public static final String IGNORE_COMMENTS_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/comments";
    public static final String IGNORE_PROCESSING_INSTRUCTIONS_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/processingInstructions";
    public static final String IGNORE_WHITE_SPACE_TEXT_CONTENT_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/whiteSpaceTextContent";
    public static final String BUFFER_SIZE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/buffer-size";
    public static final String REGISTERED_ENCODING_ALGORITHMS_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
    public static final String EXTERNAL_VOCABULARIES_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
    public static final int MIN_CHARACTER_CONTENT_CHUNK_SIZE = 0;
    public static final int MAX_CHARACTER_CONTENT_CHUNK_SIZE = 32;
    public static final int CHARACTER_CONTENT_CHUNK_MAP_MEMORY_CONSTRAINT = Integer.MAX_VALUE;
    public static final int MIN_ATTRIBUTE_VALUE_SIZE = 0;
    public static final int MAX_ATTRIBUTE_VALUE_SIZE = 32;
    public static final int ATTRIBUTE_VALUE_MAP_MEMORY_CONSTRAINT = Integer.MAX_VALUE;
    public static final String UTF_8 = "UTF-8";
    public static final String UTF_16BE = "UTF-16BE";

    public void setIgnoreDTD(boolean var1);

    public boolean getIgnoreDTD();

    public void setIgnoreComments(boolean var1);

    public boolean getIgnoreComments();

    public void setIgnoreProcesingInstructions(boolean var1);

    public boolean getIgnoreProcesingInstructions();

    public void setIgnoreWhiteSpaceTextContent(boolean var1);

    public boolean getIgnoreWhiteSpaceTextContent();

    public void setCharacterEncodingScheme(String var1);

    public String getCharacterEncodingScheme();

    public void setRegisteredEncodingAlgorithms(Map var1);

    public Map getRegisteredEncodingAlgorithms();

    public int getMinCharacterContentChunkSize();

    public void setMinCharacterContentChunkSize(int var1);

    public int getMaxCharacterContentChunkSize();

    public void setMaxCharacterContentChunkSize(int var1);

    public int getCharacterContentChunkMapMemoryLimit();

    public void setCharacterContentChunkMapMemoryLimit(int var1);

    public int getMinAttributeValueSize();

    public void setMinAttributeValueSize(int var1);

    public int getMaxAttributeValueSize();

    public void setMaxAttributeValueSize(int var1);

    public int getAttributeValueMapMemoryLimit();

    public void setAttributeValueMapMemoryLimit(int var1);

    public void setExternalVocabulary(ExternalVocabulary var1);

    public void setVocabularyApplicationData(VocabularyApplicationData var1);

    public VocabularyApplicationData getVocabularyApplicationData();

    public void reset();

    public void setOutputStream(OutputStream var1);
}

