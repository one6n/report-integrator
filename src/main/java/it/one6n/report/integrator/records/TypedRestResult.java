package it.one6n.report.integrator.records;

public record TypedRestResult<T> (boolean success, String errorMessage, T data) {

}
