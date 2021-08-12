package sim.sql.keyword;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * group by参数
 *
 * @author CodeInDreams
 * @since 2021/8/12 12:45
 */

public class GroupBy {
    private final List<String> groupBy;

    private GroupBy(List<String> groupBy) {
        this.groupBy = groupBy;
    }

    public List<String> getGroupBy() {
        return groupBy;
    }

    public static GroupBy none() {
        return new GroupBy(Collections.emptyList());
    }

    public static GroupBy column(String... columns) {
        return new GroupBy(Arrays.asList(columns));
    }
}
