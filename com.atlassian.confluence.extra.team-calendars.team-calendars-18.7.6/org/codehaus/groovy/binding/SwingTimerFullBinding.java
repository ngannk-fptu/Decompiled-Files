/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.codehaus.groovy.binding.AbstractFullBinding;
import org.codehaus.groovy.binding.ClosureSourceBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;

class SwingTimerFullBinding
extends AbstractFullBinding
implements ActionListener {
    Timer timer;
    long startTime;
    long duration;
    int stepSize;
    boolean reportSteps;
    boolean reportFraction;
    boolean reportElapsed;
    boolean repeat;
    boolean bound;

    SwingTimerFullBinding(ClosureSourceBinding source, TargetBinding target) {
        this(source, target, 50, 1000);
    }

    SwingTimerFullBinding(SourceBinding source, TargetBinding target, int interval, int duration) {
        this.setSourceBinding(source);
        this.setTargetBinding(target);
        this.timer = new Timer(interval, this);
        this.timer.setInitialDelay(0);
        this.timer.setRepeats(true);
        this.duration = duration;
    }

    void resetTimer() {
        this.timer.stop();
        this.startTime = System.currentTimeMillis();
        this.timer.start();
    }

    @Override
    public void bind() {
        if (!this.bound) {
            this.resetTimer();
            this.bound = true;
        }
    }

    @Override
    public void unbind() {
        if (this.bound) {
            this.timer.stop();
            this.bound = false;
        }
    }

    @Override
    public void rebind() {
        if (this.bound) {
            this.resetTimer();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - this.startTime;
        if (elapsed >= this.duration) {
            if (this.repeat) {
                this.startTime = currentTime;
            } else {
                this.timer.stop();
            }
            elapsed = this.duration;
        }
        if (this.reportSteps) {
            ((ClosureSourceBinding)this.sourceBinding).setClosureArgument((int)(elapsed / (long)this.stepSize));
        } else if (this.reportFraction) {
            ((ClosureSourceBinding)this.sourceBinding).setClosureArgument(Float.valueOf((float)elapsed / (float)this.duration));
        } else if (this.reportElapsed) {
            ((ClosureSourceBinding)this.sourceBinding).setClosureArgument(elapsed);
        }
        this.update();
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getInterval() {
        return this.timer.getDelay();
    }

    public void setInterval(int interval) {
        this.timer.setDelay(interval);
    }

    public int getStepSize() {
        return this.stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public boolean isCoalesce() {
        return this.timer.isCoalesce();
    }

    public void setCoalesce(boolean coalesce) {
        this.timer.setCoalesce(coalesce);
    }

    public boolean isReportSteps() {
        return this.reportSteps;
    }

    public void setReportSteps(boolean reportSteps) {
        this.reportSteps = reportSteps;
    }

    public boolean isReportFraction() {
        return this.reportFraction;
    }

    public void setReportFraction(boolean reportFraction) {
        this.reportFraction = reportFraction;
    }

    public boolean isReportElapsed() {
        return this.reportElapsed;
    }

    public void setReportElapsed(boolean reportElapsed) {
        this.reportElapsed = reportElapsed;
    }

    public boolean isRepeat() {
        return this.repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
}

