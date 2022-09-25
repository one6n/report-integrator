package it.one6n.report.integrator.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class InboundJobs {

	@Value("${inbound.dir}")
	private String inboundDir;

	@Scheduled(cron = "${job.process.inbound:-}")
	public void processInbound() {
		log.debug("start job");
		long startMillis = System.currentTimeMillis();

		Path inputPath = Paths.get(getInboundDir());
		if (inputPath.toFile().exists() && inputPath.toFile().isDirectory())
			try (Stream<Path> files = Files.list(inputPath)) {
				files.filter(Files::isRegularFile).forEach(file -> {
					try {
						processFile(file.toFile());
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

	public void processFile(File file) {
		log.debug("processFile={}", file.getName());
	}

	public void onDone(File file) {
		try {
			FileUtils.moveFileToDirectory(file, new File(getInboundDir(), "_done"), true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void onError(File file) {
		try {
			FileUtils.moveFileToDirectory(file, new File(getInboundDir(), "_error"), true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
