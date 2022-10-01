package it.one6n.report.integrator.repos;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.one6n.report.integrator.models.ReportConfiguration;

public interface ReportConfigurationRepo extends MongoRepository<ReportConfiguration, String> {
	Optional<ReportConfiguration> findOneByCustomer(String customer);
}
