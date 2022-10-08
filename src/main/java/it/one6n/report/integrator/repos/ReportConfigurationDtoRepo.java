package it.one6n.report.integrator.repos;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.records.dto.ReportConfigurationDto;

public interface ReportConfigurationDtoRepo extends MongoRepository<ReportConfiguration, String> {
	Optional<ReportConfigurationDto> findOneByCustomer(String customer);
}
