package it.one6n.report.integrator.services;

import java.io.File;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.repo.ReportConfigurationRepo;
import it.one6n.report.integrator.utils.ReportUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class ReportIntegratorService {

	@Value("${report.spool.dir}")
	private String reportSpoolDir;

	private ReportConfigurationRepo reportConfigurationRepo;

	@Autowired
	public ReportIntegratorService(ReportConfigurationRepo reportConfigurationRepo) {
		this.reportConfigurationRepo = reportConfigurationRepo;
	}

	public void processReportFile(File file) {
		log.info("Start processing report={}", file.getName());
		String customer = ReportUtils.getCustomerFromFilename(file.getName(), "_");
		ReportConfiguration reportConfiguration = getReportConfigurationRepo().findOneByCustomer(customer).orElseThrow(
				() -> new NoSuchElementException(String.format("No configuration found for customer=%s", customer)));
		if (log.isDebugEnabled())
			log.debug("reportConfiguration={}", reportConfiguration);
	}
}
