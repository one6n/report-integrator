package it.one6n.report.integrator.utils;

import org.modelmapper.ModelMapper;

import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.records.dto.ReportConfigurationDto;

public class EntityMapperUtils {

	private EntityMapperUtils() {
	}

	public static ReportConfigurationDto convertToDto(ReportConfiguration reportConfiguration) {
		return new ModelMapper().map(reportConfiguration, ReportConfigurationDto.class);
	}

	public static ReportConfiguration convertToEntity(ReportConfigurationDto dto) {
		return new ModelMapper().map(dto, ReportConfiguration.class);
	}
}
