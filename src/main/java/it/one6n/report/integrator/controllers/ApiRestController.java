package it.one6n.report.integrator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.records.TypedRestResult;
import it.one6n.report.integrator.records.dto.ReportConfigurationDto;
import it.one6n.report.integrator.services.ReportConfigurationService;
import it.one6n.report.integrator.utils.EntityMapperUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Controller
@RequestMapping(ApiRestController.API_REST_CONTROLLER_BASE_PATH)
public class ApiRestController {
	public static final String API_REST_CONTROLLER_BASE_PATH = "/api";

	public static final String GET_CONFIGURATION_PATH = "/configuration/{customer}";

	private ReportConfigurationService reportConfigurationService;

	@Autowired
	public ApiRestController(ReportConfigurationService reportConfigurationService) {
	}

	@GetMapping(ApiRestController.GET_CONFIGURATION_PATH)
	public TypedRestResult<ReportConfigurationDto> getConfiguration(@PathVariable String customer) {
		log.info("method=getConfiguration, customer={}", customer);
		ReportConfiguration reportConfiguration = getReportConfigurationService().findByCustomer(customer);
		return new TypedRestResult<ReportConfigurationDto>(true, EntityMapperUtils.convertToDto(reportConfiguration));
	}
}
