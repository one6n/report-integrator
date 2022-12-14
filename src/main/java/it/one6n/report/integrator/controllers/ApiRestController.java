package it.one6n.report.integrator.controllers;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.one6n.report.integrator.records.TypedRestResult;
import it.one6n.report.integrator.records.dto.ReportConfigurationDto;
import it.one6n.report.integrator.services.ReportConfigurationService;
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

	@Operation(summary = "Get a Report Configuration by Customer")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Given the customer's configuration", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ReportConfigurationDto.class)) }),
			@ApiResponse(responseCode = "404", description = "Invalid parameter supplied", content = @Content) })
	@GetMapping(path = ApiRestController.GET_CONFIGURATION_PATH, produces = "application/json")
	public ResponseEntity<TypedRestResult<ReportConfigurationDto>> getReportConfiguration(
			@PathVariable String customer) {
		log.info("method=getReportConfiguration, customer={}", customer);
		ResponseEntity<TypedRestResult<ReportConfigurationDto>> responseEntity = null;
		try {
			if (StringUtils.isBlank(customer))
				throw new InvalidParameterException("The customer is null or empty");
			ReportConfigurationDto reportConfigurationDto = getReportConfigurationService().findDtoByCustomer(customer);
			responseEntity = ResponseEntity.status(HttpStatus.OK)
					.body(new TypedRestResult<ReportConfigurationDto>(true, null, reportConfigurationDto));
		} catch (InvalidParameterException e) {
			log.error(e.getMessage(), e);
			responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new TypedRestResult<ReportConfigurationDto>(false, e.getMessage(), null));
		} catch (NoSuchElementException e) {
			log.error(e.getMessage(), e);
			responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new TypedRestResult<ReportConfigurationDto>(false, "No report configuration found", null));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new TypedRestResult<ReportConfigurationDto>(false, e.getMessage(), null));
		}
		return responseEntity;
	}

	@Operation(summary = "Create new Report Configuration")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Configuration created", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ReportConfigurationDto.class)) }),
			@ApiResponse(responseCode = "404", description = "Invalid parameter supplied", content = @Content) })
	@PostMapping(path = ApiRestController.CREATE_CONFIGURATION_PATH, consumes = "application/json", produces = "application/json")
	public ResponseEntity<TypedRestResult<ReportConfigurationDto>> createReportConfiguration(
			@RequestBody ReportConfigurationDto reportConfigurationDto) {
		log.info("method=createReportConfiguration, reportConfigurationDto={}", reportConfigurationDto);
		ResponseEntity<TypedRestResult<ReportConfigurationDto>> responseEntity = null;
		try {
			if (reportConfigurationDto == null || StringUtils.isBlank(reportConfigurationDto.customer())
					|| StringUtils.isNotBlank(reportConfigurationDto.id()))
				throw new InvalidParameterException("The configuration is not valid");
			ReportConfigurationDto configurationCreated = getReportConfigurationService()
					.saveReportConfigurationFromDto(reportConfigurationDto);
			responseEntity = ResponseEntity.status(HttpStatus.CREATED)
					.body(new TypedRestResult<ReportConfigurationDto>(true, null, configurationCreated));
		} catch (InvalidParameterException e) {
			log.error(e.getMessage(), e);
			responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new TypedRestResult<ReportConfigurationDto>(false, e.getMessage(), null));
		} catch (DuplicateKeyException e) {
			log.error(e.getMessage(), e);
			responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new TypedRestResult<ReportConfigurationDto>(false,
							"Another configuration is present for this customer", null));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new TypedRestResult<ReportConfigurationDto>(false, e.getMessage(), null));
		}
		return responseEntity;
	}
}
