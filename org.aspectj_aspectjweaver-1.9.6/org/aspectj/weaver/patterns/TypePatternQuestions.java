/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.util.HashMap;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.patterns.TypePattern;

public class TypePatternQuestions {
    private Map<Question, FuzzyBoolean> questionsAndAnswers = new HashMap<Question, FuzzyBoolean>();

    public FuzzyBoolean askQuestion(TypePattern pattern, ResolvedType type, TypePattern.MatchKind kind) {
        Question question = new Question(pattern, type, kind);
        FuzzyBoolean answer = question.ask();
        this.questionsAndAnswers.put(question, answer);
        return answer;
    }

    public Question anyChanges() {
        for (Map.Entry<Question, FuzzyBoolean> entry : this.questionsAndAnswers.entrySet()) {
            Question question = entry.getKey();
            FuzzyBoolean expectedAnswer = entry.getValue();
            FuzzyBoolean currentAnswer = question.ask();
            if (currentAnswer == expectedAnswer) continue;
            return question;
        }
        return null;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("TypePatternQuestions{");
        for (Map.Entry<Question, FuzzyBoolean> entry : this.questionsAndAnswers.entrySet()) {
            Question question = entry.getKey();
            FuzzyBoolean expectedAnswer = entry.getValue();
            buf.append(question);
            buf.append(":");
            buf.append(expectedAnswer);
            buf.append(", ");
        }
        buf.append("}");
        return buf.toString();
    }

    public class Question {
        TypePattern pattern;
        ResolvedType type;
        TypePattern.MatchKind kind;

        public Question(TypePattern pattern, ResolvedType type, TypePattern.MatchKind kind) {
            this.pattern = pattern;
            this.type = type;
            this.kind = kind;
        }

        public FuzzyBoolean ask() {
            return this.pattern.matches(this.type, this.kind);
        }

        public boolean equals(Object other) {
            if (!(other instanceof Question)) {
                return false;
            }
            Question o = (Question)other;
            return o.pattern.equals(this.pattern) && o.type.equals(this.type) && o.kind == this.kind;
        }

        public int hashCode() {
            int result = 17;
            result = 37 * result + this.kind.hashCode();
            result = 37 * result + this.pattern.hashCode();
            result = 37 * result + this.type.hashCode();
            return result;
        }

        public String toString() {
            return "?(" + this.pattern + ", " + this.type + ", " + this.kind + ")";
        }
    }
}

