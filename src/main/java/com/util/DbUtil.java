package com.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created by misty on 2018/3/22.
 */
public class DbUtil {

    private static ComboPooledDataSource dataSource = null;

    static {
        try {
            Properties prop = new Properties();
            InputStream in = DbUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
            try{
                prop.load(in);
            }
            catch (IOException e)
            {
                System.out.println("Load jdbc.properties Fail");
            }

            dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass(prop.getProperty("jdbc.driverClassName"));
            dataSource.setJdbcUrl(prop.getProperty("jdbc.url"));
            dataSource.setUser(prop.getProperty("jdbc.username"));
            dataSource.setPassword(prop.getProperty("jdbc.password"));

//            dataSource.setMinPoolSize(20);
//			dataSource.setMaxPoolSize(140);
//			dataSource.setInitialPoolSize(30);
//			dataSource.setMaxIdleTime(60);
//			dataSource.setIdleConnectionTestPeriod(1800);

            dataSource.setAcquireIncrement(5);
            dataSource.setAcquireRetryAttempts(30);
            dataSource.setBreakAfterAcquireFailure(false);
            dataSource.setCheckoutTimeout(1000);
            dataSource.setIdleConnectionTestPeriod(60);
            dataSource.setInitialPoolSize(30);
            dataSource.setMaxIdleTime(60);
            dataSource.setMaxPoolSize(150);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DbUtil() {
    }

    public static synchronized Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

}
