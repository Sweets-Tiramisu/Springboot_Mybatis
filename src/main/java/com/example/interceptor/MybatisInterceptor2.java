package com.example.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Locale;
import java.util.Properties;

/**
 * @description:
 * @return:
 * @author misu
 * @date: 2021/8/9 10:11
 */
@Intercepts({
        @Signature(type = Executor.class,method = "update",args = {MappedStatement.class,Object.class}),
        @Signature(type = Executor.class,method = "query",args = {MappedStatement.class,Object.class, RowBounds.class, ResultHandler.class}),
})
public class MybatisInterceptor2 implements Interceptor {

    /**正则匹配 insert、delete、update操作*/
    private static final String REGEX = ".*insert\\\\u0020.*|.*delete\\\\u0020.*|.*update\\\\u0020.*";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("%%%%%%%%%%%%%%%%%%拦截器生效");
        //获取执行参数
        Object[] objects = invocation.getArgs();
        MappedStatement ms = (MappedStatement) objects[0];

        BoundSql boundSql = ms.getSqlSource().getBoundSql(objects[1]);
        String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replace("[\\t\\n\\r]", " ");
        Long startTime = System.currentTimeMillis();
        System.out.println("#######拦截器-打印SQL语句"+sql);
        //如果是insert、delete、update操作 使用主库
        if (sql.matches(REGEX)) {
            System.out.println("#######拦截器-拦截执行数据库的请求：写请求");
        } else {
            //使用从库
            System.out.println("#######拦截器-拦截执行数据库的请求：读请求");
        }
        //通过反射修改sql语句
        String origSql=sql;
        //String newSql = origSql + " limit 2";
        String newSql = origSql;
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), newSql,
                boundSql.getParameterMappings(), boundSql.getParameterObject());

        // 把新的查询放到statement里
        MappedStatement newMs =  copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }

        Object[] queryArgs = invocation.getArgs();
        queryArgs[0] = newMs;
        System.out.println("#######拦截器-改写的SQL: "+newSql);

        //继续执行逻辑
        try {
            return invocation.proceed();
        }catch (Exception e){
           e.printStackTrace();
           throw new RuntimeException("#######SQL拦截器异常，异常原因："+e.getMessage());
        }finally {
            Long endTime = System.currentTimeMillis();
            Long time = endTime - startTime;
            System.out.println("#######拦截器-SQL执行时间："+time+" 毫秒");
        }
    }

    public static class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;
        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }




    @Override
    public Object plugin(Object o) {

        //获取代理权
        if (o instanceof Executor){
            //如果是Executor（执行增删改查操作），则拦截下来
            System.out.println("###########开始获取代理权%%%%%%%%%%%"+o);

            return Plugin.wrap(o,this);
        }else {
            System.out.println("###########没有获取代理权%%%%%%%%%%%"+o.toString());
            return o;
        }


    }

    @Override
    public void setProperties(Properties properties) {
        //读取mybatis配置文件中属性
    }
}
