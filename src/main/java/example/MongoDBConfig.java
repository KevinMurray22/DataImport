package example;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
@EnableMongoRepositories(basePackages = "example")
public class MongoDBConfig {
    public String getDatabaseName() {
        return "mydatabase";
    }
    @Bean
    public MongoClient mongoClient() {

        ConnectionString address = new ConnectionString("mongodb://localhost:27017");
        MongoClient client = MongoClients.create(address);
        return client;
    }
    @Bean
    public MongoDbFactory mongoDbFactory() {
        MongoDbFactory factory = new SimpleMongoClientDbFactory(mongoClient(), getDatabaseName());
        return factory;
    }
    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate template = new MongoTemplate(mongoDbFactory());
        return template;
    }
}