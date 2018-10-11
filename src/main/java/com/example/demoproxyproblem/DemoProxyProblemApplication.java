package com.example.demoproxyproblem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
public class DemoProxyProblemApplication implements CommandLineRunner {

	@Autowired
	private ResultService resultService;

	public static void main(String[] args) {
		SpringApplication.run(DemoProxyProblemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		resultService.average(2017);
		resultService.average(2017);
	}

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
	}
}

@Service
class ResultService {
	private final AverageService averageService;
	private final ResultRepository resultRepository;

	@Autowired
	private ApplicationContext applicationContext;

	ResultService(AverageService averageService, ResultRepository resultRepository) {
		this.averageService = averageService;
		this.resultRepository = resultRepository;
	}

	@Transactional
	public int average(int year) {
		List<Integer> allByYear = resultRepository.findAllByYear(year);
		ResultService resultService = applicationContext.getBean(ResultService.class);
		return resultService.calculateAverage(allByYear);
//		return calculateAverage(allByYear);
//		return averageService.calculateAverage(allByYear);
	}

	@Cacheable("average")
	public int calculateAverage(List<Integer> resultList) {
		System.out.println("calculating average result");
		return 2;
	}
}

@Service
class AverageService {
	@Cacheable("average")
	public int calculateAverage(List<Integer> resultList) {
		System.out.println("calculating average result");
		return 2;
	}
}

@Repository
class ResultRepository {
	public List<Integer> findAllByYear(int year) {
		return Collections.singletonList(10);
	}
}