package github.alittlehuang.sql4j.jdbc.sql;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public
class SqlLogger {
    public static Function<String, String> sqlFormat = null;
    public static BiFunction<String, Object[], String> sqlArgsFormat = null;


    static void traceSql(String sql, List<Object[]> args) {
        if (log.isDebugEnabled()) {
            if (sqlArgsFormat != null) {
                for (Object[] arg : args) {
                    log.debug(sqlArgsFormat.apply(sql, arg));
                }
            } else if (sqlFormat != null) {
                log.debug(sqlFormat.apply(sql));
            } else {
                log.debug(sql);
            }
        }
    }

    static void traceSql(String sql, Object[] args) {
        if (log.isDebugEnabled()) {
            if (sqlArgsFormat != null) {
                log.debug(sqlArgsFormat.apply(sql, args));
            } else if (sqlFormat != null) {
                log.debug(sqlFormat.apply(sql));
            } else {
                log.debug(sql);
            }
        }
    }
}
