package it.one6n.report.integrator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.records.dto.ReportConfigurationDto;

public class ReportConfigurationDtoUtils {

	private ReportConfigurationDtoUtils() {
	}

	public static ReportConfiguration dtoToEntity(ReportConfigurationDto dto) {
		return new ObjectMapper().convertValue(dto, ReportConfiguration.class);
	}

	public static ReportConfigurationDto entityToDto(ReportConfiguration entity) {
		return new ObjectMapper().convertValue(entity, ReportConfigurationDto.class);
	}
}
