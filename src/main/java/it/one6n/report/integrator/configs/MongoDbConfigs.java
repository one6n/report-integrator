package it.one6n.report.integrator.configs;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import it.one6n.report.integrator.models.ReportConfiguration;
import lombok.Getter;

@Getter
@Configuration
@EnableMongoRepositories(basePackages = MongoDbConfigs.MONGO_REPOSITORY_BASE_PACKAGE)
public class MongoDbConfigs {

	public static final String MONGO_REPOSITORY_BASE_PACKAGE = "it.one6n.report.integrator.repo";

	private MongoTemplate mongoTemplate;

	@Autowired
	public MongoDbConfigs(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@PostConstruct
	public void initIndexes() {
		getMongoTemplate().indexOps(ReportConfiguration.class)
				.ensureIndex(new Index().on("customer", Direction.ASC).unique());
	}
}
