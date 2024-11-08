package expression.impl;
import expression.api.Data;
import expression.api.DataType;
import expression.api.Expression;

import java.util.Arrays;

public class Times extends BinaryExpression {

    public Times(Expression left, Expression right) {
        super(left, right);
        setDataType(DataType.NUMERIC);
    }

    @Override
    protected Data dynamicEvaluate(Data left, Data right) {
        return left.getType() == DataType.NUMERIC && right.getType() == DataType.NUMERIC ?
                new DataImpl(DataType.NUMERIC,(double)left.getValue() * (double)right.getValue())
                : new DataImpl(DataType.UNKNOWN,Double.NaN);
    }

    @Override
    public boolean isValidArgs(Object... args) {
        boolean value = Arrays
                .stream(args)
                .map(Expression.class::cast)
                .allMatch(arg -> arg.getType() == DataType.NUMERIC || arg.getType() == DataType.UNKNOWN);

        if (!value) {
            throw new IllegalArgumentException("arguments must be number/numeric function or reference to cell!\n" +
                    "for example: {TIMES,{DIVIDE,4,5},{REF,A3}}");
        }

        return true;
    }
}
