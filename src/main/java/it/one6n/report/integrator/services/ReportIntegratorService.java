package it.one6n.report.integrator.services;

import java.io.File;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Service
public class ReportIntegratorService {

	public void processReportFile(File file) {
		log.info("Start processing report={}", file.getName());
	}
}
