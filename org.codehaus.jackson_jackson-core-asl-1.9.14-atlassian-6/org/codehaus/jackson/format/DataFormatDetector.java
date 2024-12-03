/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.format.DataFormatMatcher;
import org.codehaus.jackson.format.InputAccessor;
import org.codehaus.jackson.format.MatchStrength;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DataFormatDetector {
    public static final int DEFAULT_MAX_INPUT_LOOKAHEAD = 64;
    protected final JsonFactory[] _detectors;
    protected final MatchStrength _optimalMatch;
    protected final MatchStrength _minimalMatch;
    protected final int _maxInputLookahead;

    public DataFormatDetector(JsonFactory ... detectors) {
        this(detectors, MatchStrength.SOLID_MATCH, MatchStrength.WEAK_MATCH, 64);
    }

    public DataFormatDetector(Collection<JsonFactory> detectors) {
        this(detectors.toArray(new JsonFactory[detectors.size()]));
    }

    public DataFormatDetector withOptimalMatch(MatchStrength optMatch) {
        if (optMatch == this._optimalMatch) {
            return this;
        }
        return new DataFormatDetector(this._detectors, optMatch, this._minimalMatch, this._maxInputLookahead);
    }

    public DataFormatDetector withMinimalMatch(MatchStrength minMatch) {
        if (minMatch == this._minimalMatch) {
            return this;
        }
        return new DataFormatDetector(this._detectors, this._optimalMatch, minMatch, this._maxInputLookahead);
    }

    public DataFormatDetector withMaxInputLookahead(int lookaheadBytes) {
        if (lookaheadBytes == this._maxInputLookahead) {
            return this;
        }
        return new DataFormatDetector(this._detectors, this._optimalMatch, this._minimalMatch, lookaheadBytes);
    }

    private DataFormatDetector(JsonFactory[] detectors, MatchStrength optMatch, MatchStrength minMatch, int maxInputLookahead) {
        this._detectors = detectors;
        this._optimalMatch = optMatch;
        this._minimalMatch = minMatch;
        this._maxInputLookahead = maxInputLookahead;
    }

    public DataFormatMatcher findFormat(InputStream in) throws IOException {
        return this._findFormat(new InputAccessor.Std(in, new byte[this._maxInputLookahead]));
    }

    public DataFormatMatcher findFormat(byte[] fullInputData) throws IOException {
        return this._findFormat(new InputAccessor.Std(fullInputData));
    }

    private DataFormatMatcher _findFormat(InputAccessor.Std acc) throws IOException {
        JsonFactory bestMatch = null;
        Enum bestMatchStrength = null;
        for (JsonFactory f : this._detectors) {
            acc.reset();
            MatchStrength strength = f.hasFormat(acc);
            if (strength == null || strength.ordinal() < this._minimalMatch.ordinal() || bestMatch != null && bestMatchStrength.ordinal() >= strength.ordinal()) continue;
            bestMatch = f;
            bestMatchStrength = strength;
            if (strength.ordinal() >= this._optimalMatch.ordinal()) break;
        }
        return acc.createMatcher(bestMatch, (MatchStrength)bestMatchStrength);
    }
}

