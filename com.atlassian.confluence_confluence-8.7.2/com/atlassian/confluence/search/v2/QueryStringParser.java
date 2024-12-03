/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.InvalidQueryException;
import com.atlassian.confluence.search.v2.QueryFactory;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class QueryStringParser {
    private final String stringToParse;
    private final QueryFactory queryFactory;
    private Stack parserState = new Stack();

    public QueryStringParser(QueryFactory queryFactory, String stringToParse) {
        this.queryFactory = queryFactory;
        this.stringToParse = stringToParse;
    }

    public SearchQuery parse() throws InvalidQueryException {
        this.pushState(new StartState());
        for (int i = 0; i < this.stringToParse.length(); ++i) {
            this.peekState().parseCharacter(this.stringToParse.charAt(i));
        }
        State endState = this.popState();
        if (endState instanceof StartState) {
            return (SearchQuery)((StartState)endState).parameters.get(0);
        }
        throw new InvalidQueryException("Unexpected end of query");
    }

    private State peekState() {
        return (State)this.parserState.peek();
    }

    private State popState() {
        return (State)this.parserState.pop();
    }

    private void pushState(State state) {
        this.parserState.push(state);
    }

    private class QuotedParameterState
    implements State {
        private StringBuffer paramBuf = new StringBuffer(15);
        private SubQueryKeyState superState;
        private boolean escaped = false;

        public QuotedParameterState(SubQueryKeyState superState) {
            this.superState = superState;
        }

        @Override
        public void parseCharacter(char c) throws InvalidQueryException {
            if (c == '\\' && !this.escaped) {
                this.escaped = true;
            } else if (c == '\"' && !this.escaped) {
                this.superState.parameters.add(this.paramBuf.toString());
                QueryStringParser.this.popState();
            } else {
                this.escaped = false;
                this.paramBuf.append(c);
            }
        }
    }

    private class SubQueryKeyState
    extends QueryState {
        private QueryState superState;
        private StringBuffer keyBuf;

        public SubQueryKeyState(QueryState superState) {
            this.keyBuf = new StringBuffer(15);
            this.superState = superState;
        }

        @Override
        public void parseCharacter(char c) throws InvalidQueryException {
            if (Character.isWhitespace(c)) {
                if (this.keyBuf.length() > 0) {
                    this.parameters.add(this.keyBuf.toString());
                    this.keyBuf = new StringBuffer();
                }
            } else if (c == '\"') {
                if (this.keyBuf.length() > 0) {
                    throw new InvalidQueryException("Quote found in the middle of a parameter");
                }
                QueryStringParser.this.pushState(new QuotedParameterState(this));
            } else if (c == '(') {
                if (this.keyBuf.length() > 0) {
                    throw new InvalidQueryException("Opening bracket found in the middle of a parameter");
                }
                QueryStringParser.this.pushState(new SubQueryKeyState(this));
            } else if (c == ')') {
                if (this.keyBuf.length() > 0) {
                    this.parameters.add(this.keyBuf.toString());
                }
                this.makeQueryFromParameters();
                QueryStringParser.this.popState();
            } else {
                this.keyBuf.append(c);
            }
        }

        private void makeQueryFromParameters() throws InvalidQueryException {
            if (this.parameters.size() == 0) {
                throw new InvalidQueryException("Empty query?");
            }
            if (this.parameters.size() == 1) {
                this.superState.parameters.add(this.makeQuery((String)this.parameters.get(0)));
            } else {
                this.superState.parameters.add(this.makeQuery((String)this.parameters.get(0), this.parameters.subList(1, this.parameters.size())));
            }
        }

        private SearchQuery makeQuery(String key, List parameters) throws InvalidQueryException {
            return QueryStringParser.this.queryFactory.newQuery(key, parameters);
        }

        private SearchQuery makeQuery(String key) throws InvalidQueryException {
            return QueryStringParser.this.queryFactory.newQuery(key);
        }
    }

    private class StartState
    extends QueryState {
        private StartState() {
        }

        @Override
        public void parseCharacter(char c) throws InvalidQueryException {
            if (!Character.isWhitespace(c)) {
                if (c == '(') {
                    QueryStringParser.this.pushState(new SubQueryKeyState(this));
                } else {
                    throw new InvalidQueryException("Expected an opening bracket");
                }
            }
        }
    }

    private abstract class QueryState
    implements State {
        List parameters = new ArrayList();

        private QueryState() {
        }
    }

    private static interface State {
        public void parseCharacter(char var1) throws InvalidQueryException;
    }
}

