package sim.sql.keyword;

import org.checkerframework.checker.nullness.qual.NonNull;
import sim.sql.condition.ColumnCondition;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 列
 * 
 * @author CodeInDreams
 * @since 2021/8/12 13:28
 */

public class Column {
    private final String name;

    private Column(String name) {
        this.name = name;
    }

    public static Column of(String name) {
        return new Column(name);
    }

    /**
     * =
     */
    public ColumnCondition isEqualTo(Object exactValue) {
        return ColumnCondition.of(name, o -> Objects.equals(o, exactValue));
    }

    /**
     * >
     */
    public ColumnCondition isGreaterThan(@NonNull Comparable<?> exactValue) {
        return ColumnCondition.of(name, new ObjectCompareFunction<>(exactValue, o -> o > 0));
    }

    /**
     * <
     */
    public ColumnCondition isLessThan(@NonNull Comparable<?> exactValue) {
        return ColumnCondition.of(name, new ObjectCompareFunction<>(exactValue, o -> o < 0));
    }

    /**
     * >=
     */
    public ColumnCondition isGreaterThanOrEqual(@NonNull Comparable<?> exactValue) {
        return ColumnCondition.of(name, new ObjectCompareFunction<>(exactValue, o -> o >= 0));
    }

    /**
     * <=
     */
    public ColumnCondition isLessThanOrEqual(@NonNull Comparable<?> exactValue) {
        return ColumnCondition.of(name, new ObjectCompareFunction<>(exactValue, o -> o <= 0));
    }

    /**
     * in
     */
    public ColumnCondition in(List<Object> range) {
        return ColumnCondition.of(name, range::contains);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class ObjectCompareFunction<T> implements Function<Object, Boolean> {
        private final Comparable<T> exactValue;
        private final Function<Integer, Boolean> resultConverter;

        public ObjectCompareFunction(Comparable<T> exactValue, Function<Integer, Boolean> resultConverter) {
            this.exactValue = exactValue;
            this.resultConverter = resultConverter;
        }

        @Override
        public Boolean apply(Object o) {
            if (!(o instanceof Comparable)) {
                return false;
            }
            // 由于不确定谁是父类，这里都试一下，一般exactValue类型更抽象，所以先试右边
            try {
                final int compareResult = exactValue.compareTo((T) o);
                return resultConverter.apply(negate(compareResult));
            } catch (ClassCastException e) {
                try {
                    final int compareResult = ((Comparable) o).compareTo(exactValue);
                    return resultConverter.apply(compareResult);
                } catch (ClassCastException ignored) {
                    return false;
                }
            }
        }

        /**
         * 对compareTo结果取负
         */
        private static Integer negate(int a) {
            return a == Integer.MIN_VALUE ? 1 : -a;
        }
    }

    /**
     * like %keyword%
     */
    public <T> ColumnCondition likeInclude(@NonNull String keyword) {
        return ColumnCondition.of(name, o -> {
            if (!(o instanceof String)) {
                return false;
            }
            return ((String) o).contains(keyword);
        });
    }

    /**
     * like keyword%
     */
    public <T> ColumnCondition likeLeft(@NonNull String keyword) {
        return ColumnCondition.of(name, o -> {
            if (!(o instanceof String)) {
                return false;
            }
            return ((String) o).startsWith(keyword);
        });
    }

    /**
     * like %keyword
     */
    public <T> ColumnCondition likeRight(@NonNull String keyword) {
        return ColumnCondition.of(name, o -> {
            if (!(o instanceof String)) {
                return false;
            }
            return ((String) o).endsWith(keyword);
        });
    }
    
    // 继续向下扩展，或使用子类扩展...
}
