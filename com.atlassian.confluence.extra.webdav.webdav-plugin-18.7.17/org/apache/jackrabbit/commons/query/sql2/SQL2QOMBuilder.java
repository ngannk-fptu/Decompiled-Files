/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.query.sql2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import org.apache.jackrabbit.commons.query.QueryObjectModelBuilder;
import org.apache.jackrabbit.commons.query.sql2.Parser;
import org.apache.jackrabbit.commons.query.sql2.QOMFormatter;

public class SQL2QOMBuilder
implements QueryObjectModelBuilder {
    private static final List<String> SUPPORTED = new ArrayList<String>(Arrays.asList("JCR-JQOM", "JCR-SQL2"));

    @Override
    public QueryObjectModel createQueryObjectModel(String statement, QueryObjectModelFactory qf, ValueFactory vf) throws InvalidQueryException, RepositoryException {
        return new Parser(qf, vf).createQueryObjectModel(statement);
    }

    @Override
    public boolean canHandle(String language) {
        return SUPPORTED.contains(language);
    }

    @Override
    public String[] getSupportedLanguages() {
        return SUPPORTED.toArray(new String[SUPPORTED.size()]);
    }

    @Override
    public String toString(QueryObjectModel qom) throws InvalidQueryException {
        try {
            return QOMFormatter.format(qom);
        }
        catch (RepositoryException e) {
            throw new InvalidQueryException(e.getMessage(), e);
        }
    }
}

