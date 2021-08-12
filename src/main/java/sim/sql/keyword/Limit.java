package sim.sql.keyword;

/**
 * limit参数
 *
 * @author CodeInDreams
 * @since 2021/8/12 12:45
 */

public class Limit {
    private final Integer offset;
    private final Integer limit;

    private Limit(Integer offset, Integer limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public static Limit none() {
        return new Limit(null, null);
    }

    public static Limit of(int limit) {
        return new Limit(null, limit);
    }

    public static Limit of(int offset, int limit) {
        return new Limit(offset, limit);
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }
}
