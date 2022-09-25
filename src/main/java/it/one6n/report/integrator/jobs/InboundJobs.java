package it.one6n.report.integrator.jobs;

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
		log.debug("scan dir={}", getInboundDir());
		log.info("job end, duration={}", System.currentTimeMillis() - startMillis);
	}
}
