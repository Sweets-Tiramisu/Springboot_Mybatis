package com.example.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author cong
 * @version 1.0
 * @description: PG更改语句的工具
 * @date 2021/8/6 14:38
 */
public class PgSqlUtil {
    /**
     * @description:  将 mysql 数据库涉及 limit 的数据改为 pg 数据库的语法
     * @param:  * @param sql
     * @return: java.lang.String
     * @author cong
     * @date: 2021/8/6 13:41
     */
    public static String sqlMysqlToPgChangeLimit(String sql ){
        String str="";
        //1.小写
        str=sql.toLowerCase(Locale.ROOT);
        if(str.contains("limit")&&!str.contains("offset")){
            //2.截取Limit之前的，不含Limit
            String topSql= StringUtils.substringBeforeLast(str, "limit");
            //3.截取Limit之后的，不含Limit
            String lastSql = StringUtils.substringAfterLast(str, "limit");
            //4.分开，首先将中文空格替换为英文空格
            lastSql.replaceAll(" "," ");
            String[] bs = lastSql.split(" ");
            List<String> strings = new ArrayList<>();
            for (String f:bs){
                if (f!=null&&!" ".equals(f)&&!"".equals(f)){
                    if(f.contains(",")){
                        String[] splits = f.split(",");
                        for (String sp: splits){
                            if (sp!=null&&!"".equals(sp)&&!" ".equals(sp)){
                                strings.add(sp);
                            }
                        }
                    }else{
                        strings.add(f);
                    }
                }
            }
            //5. sql 拼装
            if(strings.size()==2){
                String chagengsql="";
                chagengsql+=strings.get(1);
                chagengsql+=" offset ";
                chagengsql+=strings.get(0);
                return topSql+ "limit "+chagengsql;
            }else{
                throw new  RuntimeException(" limit 拆解分页异常！");
            }
        }else{
            return str;
        }
    }

    /**
     * @description: 测试PGLimit更改
     * @param:  * @param args
     * @return: void
     * @author cong
     * @date: 2021/8/6 14:41
     */
    public static void main(String[] args) {
        String sql="SELECT * FROM bss_role  q LIMIT 2 10 ";
        sql="SELECT * FROM bss_role b where b.ID = (SELECT e.ID FROM bss_role e LIMIT 1 ) LIMIT 0 , 5 ";
        //sql="SELECT * FROM bss_role LIMIT     2    ,     10 ";
        //String sql="SELECT * FROM bss_role group by id desc";
        sql=sqlMysqlToPgChangeLimit(sql);
        System.out.println("更改后的sql："+sql);
    }
}
