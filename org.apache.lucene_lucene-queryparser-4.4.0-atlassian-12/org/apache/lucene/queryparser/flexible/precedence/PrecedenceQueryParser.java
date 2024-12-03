/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 */
package org.apache.lucene.queryparser.flexible.precedence;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.flexible.precedence.processors.PrecedenceQueryNodeProcessorPipeline;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;

public class PrecedenceQueryParser
extends StandardQueryParser {
    public PrecedenceQueryParser() {
        this.setQueryNodeProcessor(new PrecedenceQueryNodeProcessorPipeline(this.getQueryConfigHandler()));
    }

    public PrecedenceQueryParser(Analyzer analyer) {
        super(analyer);
        this.setQueryNodeProcessor(new PrecedenceQueryNodeProcessorPipeline(this.getQueryConfigHandler()));
    }
}

