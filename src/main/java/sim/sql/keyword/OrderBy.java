package sim.sql.keyword;

import org.apache.commons.collections4.list.UnmodifiableList;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * order by参数
 *
 * @author CodeInDreams
 * @since 2021/8/12 12:45
 */

public class OrderBy {
    private List<SortColumn> orderBy = Collections.emptyList();

    private OrderBy() {
    }

    public static OrderBy none() {
        return new OrderBy();
    }

    public static OrderBy column(String column, Sort sort) {
        final OrderBy orderBy = new OrderBy();
        orderBy.setOrderBy(Collections.singletonList(new SortColumn(column, sort)));
        return orderBy;
    }
    
    public OrderBy thenOrderBy(String column, Sort sort) {
        final List<SortColumn> sortColumns = new LinkedList<>(orderBy);
        sortColumns.add(new SortColumn(column, sort));
        setOrderBy(new UnmodifiableList<>(sortColumns));
        return this;
    }

    private void setOrderBy(List<SortColumn> orderBy) {
        this.orderBy = orderBy;
    }

    public List<SortColumn> getOrderBy() {
        return orderBy;
    }

    /**
     * 排序方式
     */
    public enum Sort {
        // 升序、降序
        ASC, DESC
    }

    public static class SortColumn {
        private final String column;
        private final Sort sort;

        public SortColumn(String column, Sort sort) {
            this.column = column;
            this.sort = sort;
        }

        public String getColumn() {
            return column;
        }

        public Sort getSort() {
            return sort;
        }
    }
}
