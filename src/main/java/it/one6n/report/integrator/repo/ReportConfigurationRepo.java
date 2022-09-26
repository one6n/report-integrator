package it.one6n.report.integrator.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.one6n.report.integrator.models.ReportConfiguration;

public interface ReportConfigurationRepo extends MongoRepository<ReportConfiguration, String> {
	Optional<ReportConfiguration> findOneByCustomer(String customer);
}
