package sim.sql.keyword;

import org.checkerframework.checker.nullness.qual.NonNull;
import sim.sql.condition.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * where参数
 *
 * @author CodeInDreams
 * @since 2021/8/12 12:45
 */

public class Where implements Condition {
    /**
     * 与，全匹配
     */
    private final List<Condition> addConditions = new ArrayList<>();

    /**
     * 或，空或任一
     */
    private final List<Condition> orConditions = new ArrayList<>();

    public static Where newCondition() {
        return new Where();
    }

    /**
     * 当前condition增加and
     *
     * @param condition 条件
     * @return this
     */
    public Where add(@NonNull Condition condition) {
        addConditions.add(condition);
        return this;
    }

    /**
     * 当前condition增加or
     *
     * @param condition 条件
     * @return this
     */
    public Where or(@NonNull Condition condition) {
        orConditions.add(condition);
        return this;
    }

    @Override
    public boolean match(Object obj) {
        // obj == null时的行为取决于
        return matchAdd(obj) || matchOr(obj);
    }

    /**
     * 与条件判定
     */
    private boolean matchAdd(Object obj) {
        for (Condition condition : addConditions) {
            if (!condition.match(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 或条件判定
     */
    private boolean matchOr(Object obj) {
        if (orConditions.isEmpty()) {
            return true;
        }
        for (Condition condition : orConditions) {
            if (condition.match(obj)) {
                return true;
            }
        }
        return false;
    }
}
