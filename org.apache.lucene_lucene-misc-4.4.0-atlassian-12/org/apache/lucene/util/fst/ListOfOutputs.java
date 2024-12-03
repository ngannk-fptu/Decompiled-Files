/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.store.DataInput
 *  org.apache.lucene.store.DataOutput
 *  org.apache.lucene.util.fst.Outputs
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.fst.Outputs;

public final class ListOfOutputs<T>
extends Outputs<Object> {
    private final Outputs<T> outputs;

    public ListOfOutputs(Outputs<T> outputs) {
        this.outputs = outputs;
    }

    public Object common(Object output1, Object output2) {
        return this.outputs.common(output1, output2);
    }

    public Object subtract(Object object, Object inc) {
        return this.outputs.subtract(object, inc);
    }

    public Object add(Object prefix, Object output) {
        assert (!(prefix instanceof List));
        if (!(output instanceof List)) {
            return this.outputs.add(prefix, output);
        }
        List outputList = (List)output;
        ArrayList<Object> addedList = new ArrayList<Object>(outputList.size());
        for (Object _output : outputList) {
            addedList.add(this.outputs.add(prefix, _output));
        }
        return addedList;
    }

    public void write(Object output, DataOutput out) throws IOException {
        assert (!(output instanceof List));
        this.outputs.write(output, out);
    }

    public void writeFinalOutput(Object output, DataOutput out) throws IOException {
        if (!(output instanceof List)) {
            out.writeVInt(1);
            this.outputs.write(output, out);
        } else {
            List outputList = (List)output;
            out.writeVInt(outputList.size());
            for (Object eachOutput : outputList) {
                this.outputs.write(eachOutput, out);
            }
        }
    }

    public Object read(DataInput in) throws IOException {
        return this.outputs.read(in);
    }

    public Object readFinalOutput(DataInput in) throws IOException {
        int count = in.readVInt();
        if (count == 1) {
            return this.outputs.read(in);
        }
        ArrayList<Object> outputList = new ArrayList<Object>(count);
        for (int i = 0; i < count; ++i) {
            outputList.add(this.outputs.read(in));
        }
        return outputList;
    }

    public Object getNoOutput() {
        return this.outputs.getNoOutput();
    }

    public String outputToString(Object output) {
        if (!(output instanceof List)) {
            return this.outputs.outputToString(output);
        }
        List outputList = (List)output;
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; i < outputList.size(); ++i) {
            if (i > 0) {
                b.append(", ");
            }
            b.append(this.outputs.outputToString(outputList.get(i)));
        }
        b.append(']');
        return b.toString();
    }

    public Object merge(Object first, Object second) {
        ArrayList<Object> outputList = new ArrayList<Object>();
        if (!(first instanceof List)) {
            outputList.add(first);
        } else {
            outputList.addAll((List)first);
        }
        if (!(second instanceof List)) {
            outputList.add(second);
        } else {
            outputList.addAll((List)second);
        }
        return outputList;
    }

    public String toString() {
        return "OneOrMoreOutputs(" + this.outputs + ")";
    }

    public List<T> asList(Object output) {
        if (!(output instanceof List)) {
            ArrayList<Object> result = new ArrayList<Object>(1);
            result.add(output);
            return result;
        }
        return (List)output;
    }
}

