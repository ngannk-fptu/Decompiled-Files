/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenQueue;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;

public class TokenBuffer {
    protected TokenStream input;
    int nMarkers = 0;
    int markerOffset = 0;
    int numToConsume = 0;
    TokenQueue queue;

    public TokenBuffer(TokenStream tokenStream) {
        this.input = tokenStream;
        this.queue = new TokenQueue(1);
    }

    public final void reset() {
        this.nMarkers = 0;
        this.markerOffset = 0;
        this.numToConsume = 0;
        this.queue.reset();
    }

    public final void consume() {
        ++this.numToConsume;
    }

    private final void fill(int n) throws TokenStreamException {
        this.syncConsume();
        while (this.queue.nbrEntries < n + this.markerOffset) {
            this.queue.append(this.input.nextToken());
        }
    }

    public TokenStream getInput() {
        return this.input;
    }

    public final int LA(int n) throws TokenStreamException {
        this.fill(n);
        return this.queue.elementAt(this.markerOffset + n - 1).getType();
    }

    public final Token LT(int n) throws TokenStreamException {
        this.fill(n);
        return this.queue.elementAt(this.markerOffset + n - 1);
    }

    public final int mark() {
        this.syncConsume();
        ++this.nMarkers;
        return this.markerOffset;
    }

    public final void rewind(int n) {
        this.syncConsume();
        this.markerOffset = n;
        --this.nMarkers;
    }

    private final void syncConsume() {
        while (this.numToConsume > 0) {
            if (this.nMarkers > 0) {
                ++this.markerOffset;
            } else {
                this.queue.removeFirst();
            }
            --this.numToConsume;
        }
    }
}

