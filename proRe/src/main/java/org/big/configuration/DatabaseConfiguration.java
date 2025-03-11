package org.big.configuration;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

//classpath가 scr/main/resources를 가리킨데
@Configuration
@PropertySource("classpath:/application.properties")
@EnableTransactionManagement
public class DatabaseConfiguration {
   
   @Autowired
   private ApplicationContext applicationContext;
   //applicationContext를 사용하면 Bean이랑 연결된데

   @Bean
   @ConfigurationProperties(prefix = "spring.datasource.hikari")
   public HikariConfig hikariConfig() {
      return new HikariConfig();
   }
   
   @Bean
   public DataSource dataSource(){
      DataSource dataSource = new HikariDataSource(hikariConfig());
      System.out.println(dataSource.toString());
      return dataSource;
   }
   
   @Bean
   public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
      SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
      sqlSessionFactoryBean.setDataSource(dataSource);
      sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mapper/**/sql-*.xml"));
      sqlSessionFactoryBean.setConfiguration(mybatisConfig());
      return sqlSessionFactoryBean.getObject();
   }//이 빈이랑 연결됨!
   //아랫놈이랑도 이어짐 아무래도 sqlSessionFactory이거 쓰시죠?
   
   @Bean
   public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
      return new SqlSessionTemplate(sqlSessionFactory);
   }
   
   @Bean
   @ConfigurationProperties(prefix = "mybatis.configuration")
   public org.apache.ibatis.session.Configuration mybatisConfig(){
	   return new org.apache.ibatis.session.Configuration();
   }
   
   @Bean
   public PlatformTransactionManager transactionManager() throws Exception {
	   return new DataSourceTransactionManager(dataSource());
   }
}














