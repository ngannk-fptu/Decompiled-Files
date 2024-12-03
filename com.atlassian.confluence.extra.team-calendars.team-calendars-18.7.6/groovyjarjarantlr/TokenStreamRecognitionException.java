/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;

public class TokenStreamRecognitionException
extends TokenStreamException {
    public RecognitionException recog;

    public TokenStreamRecognitionException(RecognitionException recognitionException) {
        super(recognitionException.getMessage());
        this.recog = recognitionException;
    }

    public String toString() {
        return this.recog.toString();
    }
}

