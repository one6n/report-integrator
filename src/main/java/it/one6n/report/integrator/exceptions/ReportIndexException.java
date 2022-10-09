package it.one6n.report.integrator.exceptions;

public class ReportIndexException extends RuntimeException {

	private static final long serialVersionUID = 3872101957419715164L;

	public ReportIndexException() {
		super();
	}

	public ReportIndexException(String message) {
		super(message);
	}

	public ReportIndexException(Throwable cause) {
		super(cause);
	}

	public ReportIndexException(String message, Throwable cause) {
		super(message, cause);
	}
}
