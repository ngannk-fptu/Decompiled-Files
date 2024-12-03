/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.input;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Vector;
import org.apache.tools.ant.input.InputRequest;

public class MultipleChoiceInputRequest
extends InputRequest {
    private final LinkedHashSet<String> choices;

    @Deprecated
    public MultipleChoiceInputRequest(String prompt, Vector<String> choices) {
        this(prompt, (Collection<String>)choices);
    }

    public MultipleChoiceInputRequest(String prompt, Collection<String> choices) {
        super(prompt);
        if (choices == null) {
            throw new IllegalArgumentException("choices must not be null");
        }
        this.choices = new LinkedHashSet<String>(choices);
    }

    public Vector<String> getChoices() {
        return new Vector<String>(this.choices);
    }

    @Override
    public boolean isInputValid() {
        return this.choices.contains(this.getInput()) || this.getInput().isEmpty() && this.getDefaultValue() != null;
    }
}

