package it.one6n.report.integrator.utils;

import it.one6n.report.integrator.models.ReportConfiguration;
import it.one6n.report.integrator.records.dto.ReportConfigurationDto;

public class ReportConfigurationMapperUtils {

	private ReportConfigurationMapperUtils() {
	}

	public static ReportConfiguration dtoToEntity(ReportConfigurationDto dto) {
		return new ReportConfiguration(dto.id(), dto.customer(), dto.exportToCustomer(), dto.exportToSpm(),
				dto.exportToDocumentRoom(), dto.addHeader(), dto.exportOnlyIndex(), dto.reportExtension(),
				dto.reportFieldsSeparator(), dto.customPrefix(), dto.customDateFormat(), dto.fieldsToCustomer(),
				dto.fieldsToSpm(), dto.fieldsToDocumentRoom());
	}

	public static ReportConfigurationDto entityToDto(ReportConfiguration entity) {
		return new ReportConfigurationDto(entity.getId(), entity.getCustomer(), entity.getExportToCustomer(),
				entity.getExportToSpm(), entity.getExportToDocumentRoom(), entity.getAddHeader(),
				entity.getExportOnlyIndex(), entity.getReportExtension(), entity.getReportFieldsSeparator(),
				entity.getCustomPrefix(), entity.getCustomDateFormat(), entity.getFieldsToCustomer(),
				entity.getFieldsToSpm(), entity.getFieldsToDocumentRoom());
	}
}
