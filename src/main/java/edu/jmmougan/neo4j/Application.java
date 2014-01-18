package edu.jmmougan.neo4j;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;

@Configuration
@EnableNeo4jRepositories
public class Application extends Neo4jConfiguration implements
		CommandLineRunner {
	
	private static final String DB = "accessingdataneo4j.db";

	@SuppressWarnings("deprecation")
	@Bean
	EmbeddedGraphDatabase graphDatabaseService() {
		return new EmbeddedGraphDatabase(DB);
	}
	
	@Autowired
	PersonRepository personRepository;
	
	@Autowired
	GraphDatabase graphDatabase;

	@Override
	public void run(String... args) throws Exception {
		
		Person greg = new Person("Greg");
		Person roy = new Person("Roy");
		Person craig = new Person("Craig");
		
		System.out.println("Before linking up with Neo4j...");
		for (Person person : new Person[]{greg, roy, craig}) {
			System.out.println(person);
		}
		
		Transaction tx = graphDatabase.beginTx();
		try {
			personRepository.save(greg);
			personRepository.save(roy);
			personRepository.save(craig);
			
			greg = personRepository.findByName(greg.name);
			greg.worksWith(roy);
			greg.worksWith(craig);
			personRepository.save(greg);
			
			roy = personRepository.findByName(roy.name);
            roy.worksWith(craig);
            // We already know that roy works with greg
            personRepository.save(roy);

            // We already know craig works with roy and greg
            
            tx.success();
		} finally {
			tx.finish();
		}
		
		System.out.println("Lookup each person by name...");
        for (String name: new String[]{greg.name, roy.name, craig.name}) {
            System.out.println(personRepository.findByName(name));
        }

        System.out.println("Looking up who works with Greg...");
        for (Person person : personRepository.findByTeammatesName("Greg")) {
            System.out.println(person.name + " works with Greg.");
        }

	}

	public static void main(String[] args) throws Exception {
		FileUtils.deleteRecursively(new File(DB));
		SpringApplication.run(Application.class, args);
	}

}
