package it.one6n.report.integrator.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.records.dto.ReportConfigurationDto;
import it.one6n.report.integrator.repos.ReportConfigurationDtoRepo;
import it.one6n.report.integrator.repos.ReportConfigurationRepo;
import it.one6n.report.integrator.utils.ReportConfigurationDtoUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class ReportConfigurationService {

	private ReportConfigurationRepo reportConfigurationRepo;
	private ReportConfigurationDtoRepo reportConfigurationDtoRepo;
	private ObjectMapper objectMapper;

	@Autowired
	public ReportConfigurationService(ReportConfigurationRepo reportConfigurationRepo,
			ReportConfigurationDtoRepo reportConfigurationDtoRepo, ObjectMapper objectMapper) {
		this.reportConfigurationRepo = reportConfigurationRepo;
		this.reportConfigurationDtoRepo = reportConfigurationDtoRepo;
		this.objectMapper = objectMapper;
	}

	public ReportConfiguration findByCustomer(String customer) {
		log.debug("findByCustomer={}", customer);
		return getReportConfigurationRepo().findOneByCustomer(StringUtils.trim(customer)).orElseThrow();
	}

	public ReportConfigurationDto findDtoByCustomer(String customer) {
		log.debug("findDtoByCustomer={}", customer);
		return getReportConfigurationDtoRepo().findOneByCustomer(StringUtils.trim(customer)).orElseThrow();
	}

	public ReportConfiguration saveReportConfiguration(ReportConfiguration reportConfiguration) {
		return getReportConfigurationRepo().save(reportConfiguration);
	}

	public ReportConfigurationDto saveReportConfigurationFromDto(ReportConfigurationDto reportConfigurationDto) {
		ReportConfiguration reportConfiguration = getReportConfigurationRepo()
				.save(ReportConfigurationDtoUtils.dtoToEntity(reportConfigurationDto));
		return ReportConfigurationDtoUtils.entityToDto(reportConfiguration);
	}
}
