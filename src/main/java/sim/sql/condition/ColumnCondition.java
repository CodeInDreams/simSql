package sim.sql.condition;

import sim.sql.util.SimSqlQueryUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 基于列的条件
 *
 * @author CodeInDreams
 * @since 2021/8/12 13:28
 */

public final class ColumnCondition implements Condition {

    private final List<String> columns;
    private final Function<List<Object>, Boolean> expression;

    private ColumnCondition(List<String> columns, Function<List<Object>, Boolean> expression) {
        this.columns = columns;
        this.expression = expression;
    }

    public List<String> getColumns() {
        return columns;
    }

    public Function<List<Object>, Boolean> getExpression() {
        return expression;
    }

    /**
     * @implNote 允许obj为null，但这时不允许查询其字段，否则强制置为不匹配
     */
    @Override
    public boolean match(Object obj) {
        try {
            final Map<String, Field> fieldMap = SimSqlQueryUtil.fieldsOf(obj);
            final List<Object> columnValues = new ArrayList<>(columns.size());
            for (String column : columns) {
                final Field field = fieldMap.get(column);
                if (null == field) {
                    return false;
                }
                columnValues.add(field.get(obj));
            }
            return expression.apply(columnValues);
        } catch (Exception e) {
            // todo log error using slf4j
            return false;
        }
    }

    public static ColumnCondition of(String column, Function<Object, Boolean> expression) {
        final Function<List<Object>, Boolean> function = o -> expression.apply(Collections.singletonList(o));
        return new ColumnCondition(Collections.singletonList(column), function);
    }

    public static ColumnCondition of(List<String> columns, Function<List<Object>, Boolean> expression) {
        return new ColumnCondition(columns, expression);
    }
}
