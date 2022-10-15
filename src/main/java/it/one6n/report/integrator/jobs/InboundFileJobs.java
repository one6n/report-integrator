package it.one6n.report.integrator.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.one6n.report.integrator.services.ReportIntegratorService;
import it.one6n.report.integrator.utils.ReportsUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class InboundFileJobs {

	@Value("${inbound.report.dir}")
	private String inboundReportDir;

	private ReportIntegratorService reportIntegratorService;

	@Autowired
	public InboundFileJobs(ReportIntegratorService reportIntegratorService) {
		this.reportIntegratorService = reportIntegratorService;
	}

	@Scheduled(cron = "${job.process.report.file:-}")
	public void processReportFile() {
		log.debug("start job");
		long startMillis = System.currentTimeMillis();

		Path inputPath = Paths.get(getInboundReportDir());
		if (inputPath.toFile().exists() && inputPath.toFile().isDirectory())
			try (Stream<Path> files = Files.list(inputPath)) {
				files.filter(Files::isRegularFile).filter(ReportsUtils::isValidReportFormat).forEach(file -> {
					try {
						lockAndProcessReportFile(file.toFile());
						onDone(file.toFile());
					} catch (Exception e) {
						onError(file.toFile());
						throw new RuntimeException(e);
					}
				});
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		log.info("job end, duration={}", System.currentTimeMillis() - startMillis);
	}

	public void lockAndProcessReportFile(File file) {
		log.debug("try to lock file={}", file.getName());
		// lock file on db
		boolean locked = true;
		if (locked)
			processReportFile(file);

	}

	public void processReportFile(File file) {
		getReportIntegratorService().processReportFile(file);
	}

	public void onDone(File file) {
		try {
			FileUtils.moveFileToDirectory(file, new File(getInboundReportDir(), "_done"), true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void onError(File file) {
		try {
			FileUtils.moveFileToDirectory(file, new File(getInboundReportDir(), "_error"), true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
