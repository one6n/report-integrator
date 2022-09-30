package it.one6n.report.integrator.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.repo.ReportConfigurationRepo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class ReportConfigurationService {

	private ReportConfigurationRepo reportConfigurationRepo;

	@Autowired
	public ReportConfigurationService(ReportConfigurationRepo reportConfigurationRepo) {
	}

	public ReportConfiguration findByCustomer(String customer) {
		log.debug("findByCustomer={}", customer);
		return getReportConfigurationRepo().findOneByCustomer(StringUtils.trim(customer)).orElseThrow();
	}
}
