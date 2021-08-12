package sim.sql.condition;

/**
 * 查询条件
 *
 * @author CodeInDreams
 * @since 2021/8/12 12:45
 */

public interface Condition {

    /**
     * 单条数据判定
     *
     * @param obj 单条数据
     * @return 是否满足条件
     */
    boolean match(Object obj);
}
