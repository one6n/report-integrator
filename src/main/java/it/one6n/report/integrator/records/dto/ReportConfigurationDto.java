package it.one6n.report.integrator.records.dto;

import java.util.List;

public record ReportConfigurationDto(
		String id, 
		String customer, 
		Boolean exportToCustomer, 
		Boolean exportToSpm,
		Boolean exportToDocumentRoom, 
		Boolean addHeader, 
		Boolean exportOnlyIndex, 
		String reportExtension,
		String reportFieldsSeparator, 
		String customPrefix, 
		String customDateFormat, 
		List<String> fieldsToCustomer,
		List<String> fieldsToSpm,
		List<String> fieldsToDocumentRoom) {}
