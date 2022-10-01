package it.one6n.report.integrator.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Document
public class ReportConfiguration {

	@Id
	private String id;

	private String customer;
	private Boolean exportToCustomer;
	private Boolean exportToSpm;
	private Boolean exportToDocumentRoom;

	private Boolean addHeader;
	private Boolean exportOnlyIndex;
	private String reportExtension;
	private String reportFieldsSeparator;
	private String customPrefix;
	private String customDateFormat;

	private List<String> fieldsToCustomer;
	private List<String> fieldsToSpm;
	private List<String> fieldsToDocumentRoom;
}
