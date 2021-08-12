package sim.sql.util;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import sim.sql.keyword.GroupBy;
import sim.sql.keyword.Limit;
import sim.sql.keyword.OrderBy;
import sim.sql.keyword.Where;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 拟SQL查询工具类
 * 
 * @author CodeInDreams
 * @since 2021/8/11 21:04
 */

public final class SimSqlQueryUtil {

    /**
     * 当前查询数据的类型，避免重复解析class
     */
    public static final ThreadLocal<Class<?>> CURRENT_CLASS = new InheritableThreadLocal<>();

    private SimSqlQueryUtil() {
    }

    /**
     * 类SQL查询
     * <p>考虑到不同Object类型的意义不大，为了提升效率，以首个对象的类型作为整个列表的数据类型，因此必须满足列表其余元素类型等于或继承首元素类型
     * <p>如果确实是不同Object类型，那需要改为每个object获取一次Field
     * <p>该方法仅支持属性，不支持按get方法取字段（e.g. interface），如需增加支持，需要将解析Field改为Field+Method，同时缓存改为Function
     *
     * @param data    源数据列表，要求必须是同一POJO（不支持接口类型）
     * @param where   where，支持add or，支持嵌套
     * @param orderBy 排序字段
     * @param groupBy 分组字段
     * @param limit   limit字段
     * @return 查询结果，分组内以第一条为准
     * @see #query(List, Where, OrderBy, GroupBy, Limit, Class)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<Object> query(List<Object> data, @NonNull Where where, @NonNull OrderBy orderBy,
                                     @NonNull GroupBy groupBy, @NonNull Limit limit) {
        if (CollectionUtils.isEmpty(data)) {
            return new ArrayList<>();
        }
        return query((List) data, where, orderBy, groupBy, limit, data.get(0).getClass());
    }

    /**
     * 类SQL查询，参数NotNull
     *
     * @param data    源数据列表，要求必须是同一POJO
     * @param where   where，支持add or，支持嵌套
     * @param orderBy 排序字段
     * @param groupBy 分组字段
     * @param limit   limit字段
     * @param klass   data类型（非接口）
     * @param <T>     data类型（非接口）
     * @return 查询结果，分组内以第一条为准
     */
    public static <T> List<T> query(@NonNull List<T> data, @NonNull Where where, @NonNull OrderBy orderBy,
                                    @NonNull GroupBy groupBy, @NonNull Limit limit, @NonNull Class<T> klass) {
        if (CollectionUtils.isEmpty(data)) {
            return new ArrayList<>();
        }
        CURRENT_CLASS.set(klass);
        try {
            List<T> result = data;
            result = filterBy(where, result);
            result = orderBy(orderBy, result);
            result = groupBy(groupBy, result);
            result = limit(limit, result);
            return result;
        } finally {
            CURRENT_CLASS.remove();
        }
    }

    private static <T> ArrayList<T> filterBy(Where where, List<T> data) {
        return data.stream()
                .filter(where::match)
                .collect(Collectors.toCollection(() -> new ArrayList<>(data.size())));
    }

    private static <T> List<T> orderBy(OrderBy orderBy, List<T> data) {
        final Map<String, Field> fieldMap = FieldCache.fieldsOf(CURRENT_CLASS.get());
        //orderBy.getOrderBy().stream().map(OrderBy.SortColumn::getColumn).map(fieldMap::get).
        try {
            final Comparator<T> comparator = (o, p) -> {
                return 1;
            };
            return data.stream()
                    .sorted(comparator)
                    .collect(Collectors.toCollection(() -> new ArrayList<>(data.size())));
        } catch (Exception e) {
            throw new RuntimeException("排序出错，请检查字段正确性", e);
        }
    }

    private static <T> List<T> groupBy(GroupBy groupBy, List<T> data) {
        final List<String> columnList = groupBy.getGroupBy();
        if (columnList.isEmpty()) {
            return data;
        }
        return null;
    }

    private static <T> List<T> limit(Limit limit, List<T> result) {
        return null;
    }

    /**
     * 查询obj的所有属性，优先用上下文已缓存的class
     *
     * @param obj obj
     * @return class of current context or obj
     */
    public static Map<String, Field> fieldsOf(Object obj) {
        final Class<?> cached = CURRENT_CLASS.get();
        return FieldCache.fieldsOf(null == cached ? obj.getClass() : cached);
    }

    /**
     * Field缓存（含继承）
     */
    private static class FieldCache {

        /**
         * class -> fieldName -> field
         */
        private static final LoadingCache<Class<?>, Map<String, Field>> FIELD_CACHE = Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.DAYS)
                .softValues()
                .build(FieldCache::genFieldCache);

        /**
         * 生成指定class的 fieldName -> field 映射
         * <li>field允许直接存取
         * <li>同名时子类优先
         */
        private static Map<String, Field> genFieldCache(Class<?> klass) {
            final Map<String, Field> generatedFieldMap = new HashMap<>(1 << 7);
            while (klass != null) {
                final Field[] fields = klass.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    generatedFieldMap.putIfAbsent(field.getName(), field);
                }
                klass = klass.getSuperclass();
            }
            return generatedFieldMap;
        }

        /**
         * 查询类的所有属性
         *
         * @param klass class
         * @return name -> field映射，严禁修改
         */
        static Map<String, Field> fieldsOf(@NonNull Class<?> klass) {
            // 如对外提供可考虑用UnmodifiableMap包装一下
            return FIELD_CACHE.get(klass);
        }

    }
}
