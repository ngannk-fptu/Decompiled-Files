/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.IteratorStatus;

@StrutsTag(name="iterator", tldTagClass="org.apache.struts2.views.jsp.IteratorTag", description="Iterate over a iterable value")
public class IteratorComponent
extends ContextBean {
    private static final Logger LOG = LogManager.getLogger(IteratorComponent.class);
    protected Iterator iterator;
    protected IteratorStatus status;
    protected Object oldStatus;
    protected IteratorStatus.StatusState statusState;
    protected String statusAttr;
    protected String value;
    protected String beginStr;
    protected Integer begin;
    protected String endStr;
    protected Integer end;
    protected String stepStr;
    protected Integer step;

    public IteratorComponent(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean start(Writer writer) {
        if (this.statusAttr != null) {
            this.statusState = new IteratorStatus.StatusState();
            this.status = new IteratorStatus(this.statusState);
        }
        if (this.beginStr != null) {
            this.begin = (Integer)this.findValue(this.beginStr, Integer.class);
        }
        if (this.endStr != null) {
            this.end = (Integer)this.findValue(this.endStr, Integer.class);
        }
        if (this.stepStr != null) {
            this.step = (Integer)this.findValue(this.stepStr, Integer.class);
        }
        ValueStack stack = this.getStack();
        if (this.value == null && this.begin == null && this.end == null) {
            this.value = "top";
        }
        Object iteratorTarget = this.findValue(this.value);
        this.iterator = MakeIterator.convert(iteratorTarget);
        if (this.begin != null) {
            Object values;
            if (this.step == null) {
                this.step = 1;
            }
            if (this.iterator == null) {
                this.iterator = new CounterIterator(this.begin, this.end, this.step, null);
            } else if (iteratorTarget.getClass().isArray()) {
                values = (Object[])iteratorTarget;
                if (this.end == null) {
                    this.end = this.step > 0 ? ((Object[])values).length - 1 : 0;
                }
                this.iterator = new CounterIterator(this.begin, this.end, this.step, Arrays.asList(values));
            } else if (iteratorTarget instanceof List) {
                values = (List)iteratorTarget;
                if (this.end == null) {
                    this.end = this.step > 0 ? values.size() - 1 : 0;
                }
                this.iterator = new CounterIterator(this.begin, this.end, this.step, (List<Object>)values);
            } else {
                LOG.error("Incorrect use of the iterator tag. When 'begin' is set, 'value' must be an Array or a List, or not set at all. 'begin', 'end' and 'step' will be ignored");
            }
        }
        if (this.iterator != null && this.iterator.hasNext()) {
            Object currentValue = this.iterator.next();
            stack.push(currentValue);
            String var = this.getVar();
            if (var != null) {
                this.putInContext(currentValue);
            }
            if (this.statusAttr != null) {
                this.statusState.setLast(!this.iterator.hasNext());
                this.oldStatus = stack.getContext().get(this.statusAttr);
                stack.getContext().put(this.statusAttr, this.status);
            }
            return true;
        }
        super.end(writer, "");
        return false;
    }

    @Override
    public boolean end(Writer writer, String body) {
        ValueStack stack = this.getStack();
        if (this.iterator != null) {
            stack.pop();
        }
        if (this.iterator != null && this.iterator.hasNext()) {
            Object currentValue = this.iterator.next();
            stack.push(currentValue);
            this.putInContext(currentValue);
            if (this.status != null) {
                this.statusState.next();
                this.statusState.setLast(!this.iterator.hasNext());
            }
            return true;
        }
        if (this.status != null) {
            if (this.oldStatus == null) {
                stack.getContext().put(this.statusAttr, null);
            } else {
                stack.getContext().put(this.statusAttr, this.oldStatus);
            }
        }
        super.end(writer, "");
        return false;
    }

    @StrutsTagAttribute(description="If specified, an instanceof IteratorStatus will be pushed into stack upon each iteration", type="Boolean", defaultValue="false")
    public void setStatus(String status) {
        this.statusAttr = status;
    }

    @StrutsTagAttribute(description="the iteratable source to iterate over, else an the object itself will be put into a newly created List")
    public void setValue(String value) {
        this.value = value;
    }

    @StrutsTagAttribute(description="if specified the iteration will start on that index", type="Integer", defaultValue="0")
    public void setBegin(String begin) {
        this.beginStr = begin;
    }

    @StrutsTagAttribute(description="if specified the iteration will end on that index(inclusive)", type="Integer", defaultValue="Size of the 'values' List or array, or 0 if 'step' is negative")
    public void setEnd(String end) {
        this.endStr = end;
    }

    @StrutsTagAttribute(description="if specified the iteration index will be increased by this value on each iteration. It can be a negative value, in which case 'begin' must be greater than 'end'", type="Integer", defaultValue="1")
    public void setStep(String step) {
        this.stepStr = step;
    }

    static class CounterIterator
    implements Iterator<Object> {
        private int step;
        private int end;
        private int currentIndex;
        private List<Object> values;

        CounterIterator(Integer begin, Integer end, Integer step, List<Object> values) {
            this.end = end;
            if (step != null) {
                this.step = step;
            }
            this.currentIndex = begin - this.step;
            this.values = values;
        }

        @Override
        public boolean hasNext() {
            int next = this.peekNextIndex();
            return this.step > 0 ? next <= this.end : next >= this.end;
        }

        @Override
        public Object next() {
            if (this.hasNext()) {
                int nextIndex = this.peekNextIndex();
                this.currentIndex += this.step;
                return this.values != null ? this.values.get(nextIndex) : Integer.valueOf(nextIndex);
            }
            throw new IndexOutOfBoundsException("Index " + (this.currentIndex + this.step) + " must be less than or equal to " + this.end);
        }

        private int peekNextIndex() {
            return this.currentIndex + this.step;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Values cannot be removed from this iterator");
        }
    }
}

