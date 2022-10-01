package it.one6n.report.integrator.controllers;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.records.TypedRestResult;
import it.one6n.report.integrator.records.dto.ReportConfigurationDto;
import it.one6n.report.integrator.services.ReportConfigurationService;
import it.one6n.report.integrator.utils.ReportConfigurationMapperUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Controller
@ResponseBody
@RequestMapping(ApiRestController.API_REST_CONTROLLER_BASE_PATH)
public class ApiRestController {

	public static final String API_REST_CONTROLLER_BASE_PATH = "/api";

	public static final String GET_CONFIGURATION_PATH = "/configuration/{customer}";
	public static final String CREATE_CONFIGURATION_PATH = "/configuration";

	private ReportConfigurationService reportConfigurationService;

	@Autowired
	public ApiRestController(ReportConfigurationService reportConfigurationService) {
		this.reportConfigurationService = reportConfigurationService;
	}

	@GetMapping(path = ApiRestController.GET_CONFIGURATION_PATH, produces = "application/json")
	public ResponseEntity<TypedRestResult<ReportConfigurationDto>> getReportConfiguration(
			@PathVariable String customer) {
		log.info("method=getReportConfiguration, customer={}", customer);
		try {
			ReportConfiguration reportConfiguration = getReportConfigurationService().findByCustomer(customer);
			return ResponseEntity.status(HttpStatus.OK).body(new TypedRestResult<ReportConfigurationDto>(true,
					ReportConfigurationMapperUtils.entityToDto(reportConfiguration)));
		} catch (NoSuchElementException e) {
			log.error(e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new TypedRestResult<ReportConfigurationDto>(false, null));
		}
	}

	@PostMapping(path = ApiRestController.CREATE_CONFIGURATION_PATH, consumes = "application/json", produces = "application/json")
	public ResponseEntity<TypedRestResult<ReportConfigurationDto>> createReportConfiguration(
			@RequestBody ReportConfigurationDto reportConfigurationDto) {
		log.info("method=createReportConfiguration, reportConfigurationDto={}", reportConfigurationDto);
		ReportConfiguration reportConfiguration = getReportConfigurationService()
				.saveReportConfiguration(ReportConfigurationMapperUtils.dtoToEntity(reportConfigurationDto));
		return ResponseEntity.status(HttpStatus.CREATED).body(new TypedRestResult<ReportConfigurationDto>(true,
				ReportConfigurationMapperUtils.entityToDto(reportConfiguration)));
	}
}
