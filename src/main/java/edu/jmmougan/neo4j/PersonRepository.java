/**
 * 
 */
package edu.jmmougan.neo4j;

import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * @author juanma
 *
 */
public interface PersonRepository extends GraphRepository<Person> {
	
	Person findByName(String name);
	
	Iterable<Person> findByTeammatesName(String name);

}
