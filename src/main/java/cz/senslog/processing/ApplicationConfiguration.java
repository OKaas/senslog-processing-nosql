package cz.senslog.processing;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

/**
 * Created by OK on 02-Nov-18.
 */
@Configuration
public class ApplicationConfiguration {

//    @Bean
//    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
//        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
//        factory.setResources(new Resource[] { new ClassPathResource("data.json")});
//        return factory;
//    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper;
    }
}


