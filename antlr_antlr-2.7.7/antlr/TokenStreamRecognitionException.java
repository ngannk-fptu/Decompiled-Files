/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.RecognitionException;
import antlr.TokenStreamException;

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

