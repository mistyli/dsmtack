import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by misty on 2018/3/22.
 */

@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:applicationContext.xml"}) //加载配置文件
public class JDBCTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @org.junit.Test
    public void testQueryForObject() {
        String sql = "SELECT * from dsm_ld_dev";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        System.out.println(list.toString());

    }

}
