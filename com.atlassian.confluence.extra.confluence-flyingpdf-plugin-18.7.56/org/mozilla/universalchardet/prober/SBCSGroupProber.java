/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.mozilla.universalchardet.prober.CharsetProber;
import org.mozilla.universalchardet.prober.HebrewProber;
import org.mozilla.universalchardet.prober.SingleByteCharsetProber;
import org.mozilla.universalchardet.prober.sequence.HebrewModel;
import org.mozilla.universalchardet.prober.sequence.Ibm855Model;
import org.mozilla.universalchardet.prober.sequence.Ibm866Model;
import org.mozilla.universalchardet.prober.sequence.Koi8rModel;
import org.mozilla.universalchardet.prober.sequence.Latin5BulgarianModel;
import org.mozilla.universalchardet.prober.sequence.Latin5Model;
import org.mozilla.universalchardet.prober.sequence.Latin7Model;
import org.mozilla.universalchardet.prober.sequence.MacCyrillicModel;
import org.mozilla.universalchardet.prober.sequence.ThaiModel;
import org.mozilla.universalchardet.prober.sequence.Win1251BulgarianModel;
import org.mozilla.universalchardet.prober.sequence.Win1251Model;
import org.mozilla.universalchardet.prober.sequence.Win1253Model;

public class SBCSGroupProber
extends CharsetProber {
    private CharsetProber.ProbingState state;
    private List<CharsetProber> probers = new ArrayList<CharsetProber>();
    private CharsetProber bestGuess;
    private int activeNum;

    public SBCSGroupProber() {
        this.probers.add(new SingleByteCharsetProber(new Win1251Model()));
        this.probers.add(new SingleByteCharsetProber(new Koi8rModel()));
        this.probers.add(new SingleByteCharsetProber(new Latin5Model()));
        this.probers.add(new SingleByteCharsetProber(new MacCyrillicModel()));
        this.probers.add(new SingleByteCharsetProber(new Ibm866Model()));
        this.probers.add(new SingleByteCharsetProber(new Ibm855Model()));
        this.probers.add(new SingleByteCharsetProber(new Latin7Model()));
        this.probers.add(new SingleByteCharsetProber(new Win1253Model()));
        this.probers.add(new SingleByteCharsetProber(new Latin5BulgarianModel()));
        this.probers.add(new SingleByteCharsetProber(new Win1251BulgarianModel()));
        this.probers.add(new SingleByteCharsetProber(new ThaiModel()));
        HebrewModel hebrewModel = new HebrewModel();
        HebrewProber hebprober = new HebrewProber();
        SingleByteCharsetProber singleByte1 = new SingleByteCharsetProber(hebrewModel, false, hebprober);
        SingleByteCharsetProber singleByte2 = new SingleByteCharsetProber(hebrewModel, true, hebprober);
        hebprober.setModalProbers(singleByte1, singleByte2);
        this.probers.add(hebprober);
        this.probers.add(singleByte1);
        this.probers.add(singleByte2);
        this.reset();
    }

    @Override
    public String getCharSetName() {
        if (this.bestGuess == null) {
            this.getConfidence();
            if (this.bestGuess == null) {
                this.bestGuess = this.probers.get(0);
            }
        }
        return this.bestGuess.getCharSetName();
    }

    @Override
    public float getConfidence() {
        float bestConf = 0.0f;
        if (this.state == CharsetProber.ProbingState.FOUND_IT) {
            return 0.99f;
        }
        if (this.state == CharsetProber.ProbingState.NOT_ME) {
            return 0.01f;
        }
        for (CharsetProber prober : this.probers) {
            float cf;
            if (!prober.isActive() || !(bestConf < (cf = prober.getConfidence()))) continue;
            bestConf = cf;
            this.bestGuess = prober;
        }
        return bestConf;
    }

    @Override
    public CharsetProber.ProbingState getState() {
        return this.state;
    }

    @Override
    public CharsetProber.ProbingState handleData(byte[] buf, int offset, int length) {
        ByteBuffer newbuf = this.filterWithoutEnglishLetters(buf, offset, length);
        if (newbuf.position() != 0) {
            for (CharsetProber prober : this.probers) {
                if (!prober.isActive()) continue;
                CharsetProber.ProbingState st = prober.handleData(newbuf.array(), 0, newbuf.position());
                if (st == CharsetProber.ProbingState.FOUND_IT) {
                    this.bestGuess = prober;
                    this.state = CharsetProber.ProbingState.FOUND_IT;
                    break;
                }
                if (st != CharsetProber.ProbingState.NOT_ME) continue;
                prober.setActive(false);
                --this.activeNum;
                if (this.activeNum > 0) continue;
                this.state = CharsetProber.ProbingState.NOT_ME;
                break;
            }
        }
        return this.state;
    }

    @Override
    public void reset() {
        this.activeNum = 0;
        for (CharsetProber prober : this.probers) {
            prober.reset();
            prober.setActive(true);
            ++this.activeNum;
        }
        this.bestGuess = null;
        this.state = CharsetProber.ProbingState.DETECTING;
    }

    @Override
    public void setOption() {
    }
}

