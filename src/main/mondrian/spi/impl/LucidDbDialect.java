/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2008-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.spi.impl;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link mondrian.spi.Dialect} for the LucidDB database.
 *
 * @author jhyde
 * @version $Id$
 * @since Nov 23, 2008
 */
public class LucidDbDialect extends JdbcDialectImpl {

    /**
     * Creates a LucidDbDialect.
     *
     * @param quoteIdentifierString String used to quote identifiers
     * @param productName Product name per JDBC driver
     * @param productVersion Product version per JDBC driver
     * @param supportedResultSetTypes Supported result set types
     * @param readOnly Whether database is read-only
     * @param maxColumnNameLength Maximum column name length
     */
    LucidDbDialect(
        String quoteIdentifierString,
        String productName,
        String productVersion,
        Set<List<Integer>> supportedResultSetTypes,
        boolean readOnly,
        int maxColumnNameLength)
    {
        super(
            quoteIdentifierString,
            productName,
            productVersion,
            supportedResultSetTypes,
            readOnly,
            maxColumnNameLength);
    }

    public boolean allowsMultipleDistinctSqlMeasures() {
        return false;
    }

    public boolean needsExponent(Object value, String valueString) {
        return value instanceof Double && !valueString.contains("E");
    }

    public boolean supportsUnlimitedValueList() {
        return true;
    }

    public boolean supportsMultiValueInExpr() {
        return true;
    }
}

// End LucidDbDialect.java