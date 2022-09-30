package it.one6n.report.integrator.records;

public record TypedRestResult<T> (boolean success, T data) {

}
