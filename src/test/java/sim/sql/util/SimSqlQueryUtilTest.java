package sim.sql.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sim.sql.condition.ColumnCondition;
import sim.sql.dto.ExampleDTO;
import sim.sql.keyword.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimSqlQueryUtilTest {

    private static List<ExampleDTO> data;

    @BeforeAll
    public static void setup() {
        final int length = 1_000_000;
        data = new ArrayList<>(length);
        for (int i = length; i > 0; i--) {
            data.add(new ExampleDTO(
                    ("str" + i % 29),
                    i % 13,
                    (long) i % 19
            ));
        }
    }

    @Test
    public void testQuery() {
        // where intValue in (3, 5, 7) or longValue = 5 * intValue - 2
        final Where where = Where.newCondition()
                .add(Column.of("intValue").in(Arrays.asList(3, 5, 7)))
                .or(ColumnCondition.of(Arrays.asList("intValue", "longValue"), list -> ((Long) list.get(1)) == 5 * ((Integer) list.get(0)) - 2));
        final OrderBy orderBy = OrderBy.column("longValue", OrderBy.Sort.ASC)
                .thenOrderBy("strValue", OrderBy.Sort.DESC);
        final GroupBy groupBy = GroupBy.column("strValue");
        final Limit limit = Limit.none();
        final List<ExampleDTO> result = SimSqlQueryUtil.query(data, where, orderBy, groupBy, limit, ExampleDTO.class);
        assertEquals(29, result.size());
        assertTrue(result.stream().allMatch(o -> o.getIntValue() == 1 && o.getLongValue() == 3));
    }

}