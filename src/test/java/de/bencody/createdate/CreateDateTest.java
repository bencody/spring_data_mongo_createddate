package de.bencody.createdate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.bencody.createdate.model.AccessibleCreateDateModel;
import de.bencody.createdate.model.NonAccessibleCreateDateModel;
import de.bencody.createdate.repository.AccessibleCreateDateRepository;
import de.bencody.createdate.repository.NonAccessibleCreateDateRepository;
import org.assertj.core.util.introspection.FieldSupport;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by bco on 09.02.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CreateDateTest {

    @Autowired
    private AccessibleCreateDateRepository accessibleCreateDateRepository;
    @Autowired
    private NonAccessibleCreateDateRepository nonAccessibleCreateDateRepository;

    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jackson2HalModule());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        converter.setObjectMapper(mapper);

        restTemplate = new RestTemplate(Collections.singletonList(converter));
    }

    @Test
    public void create_update_accessable_fields_model() {
        Instant startInstant = Instant.now();
        String url = "http://localhost:8080/accessibleCreateDateModels/";

        // Create
        AccessibleCreateDateModel newModel = new AccessibleCreateDateModel();
        ResponseEntity<Resource<AccessibleCreateDateModel>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<AccessibleCreateDateModel>(newModel),
                new ParameterizedTypeReference<Resource<AccessibleCreateDateModel>>() {
                });
        Link idLink = response.getBody().getId();
        ObjectId objectId = getObjectId(idLink);
        AccessibleCreateDateModel createdModel = accessibleCreateDateRepository.findOne(objectId);

        assertThat(createdModel.getCreated()).isGreaterThan(startInstant);
        assertThat(createdModel.getUpdated()).isEqualTo(createdModel.getCreated());

        // Update
        restTemplate.exchange(
                idLink.getHref(),
                HttpMethod.PUT,
                new HttpEntity<AccessibleCreateDateModel>(newModel),
                new ParameterizedTypeReference<Resource<AccessibleCreateDateModel>>() {
                });
        AccessibleCreateDateModel updatedModel = accessibleCreateDateRepository.findOne(objectId);

        // FAILURE HERE! updatedModel.getCreated() gets set to null!

        assertThat(updatedModel.getCreated()).isEqualTo(createdModel.getCreated());
        assertThat(updatedModel.getUpdated()).isGreaterThan(updatedModel.getCreated());
    }

    @Test
    public void create_update_non_accessable_fields_model() {
        Instant startInstant = Instant.now();
        String url = "http://localhost:8080/nonAccessibleCreateDateModels/";

        // Create
        NonAccessibleCreateDateModel newModel = new NonAccessibleCreateDateModel();
        ResponseEntity<Resource<NonAccessibleCreateDateModel>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<NonAccessibleCreateDateModel>(newModel),
                new ParameterizedTypeReference<Resource<NonAccessibleCreateDateModel>>() {
                });
        Link idLink = response.getBody().getId();
        ObjectId objectId = getObjectId(idLink);
        NonAccessibleCreateDateModel createdModel = nonAccessibleCreateDateRepository.findOne(objectId);
        Instant createdModelCreatedDate = FieldSupport.EXTRACTION.fieldValue("created", Instant.class, createdModel);
        Instant createdModelUpdatedDate = FieldSupport.EXTRACTION.fieldValue("updated", Instant.class, createdModel);

        assertThat(createdModelCreatedDate).isGreaterThan(startInstant);
        assertThat(createdModelUpdatedDate).isEqualTo(createdModelCreatedDate);

        // Update
        restTemplate.exchange(
                idLink.getHref(),
                HttpMethod.PUT,
                new HttpEntity<NonAccessibleCreateDateModel>(newModel),
                new ParameterizedTypeReference<Resource<NonAccessibleCreateDateModel>>() {
                });
        NonAccessibleCreateDateModel updatedModel = nonAccessibleCreateDateRepository.findOne(objectId);
        Instant updatedModelCreatedDate = FieldSupport.EXTRACTION.fieldValue("created", Instant.class, updatedModel);
        Instant updatedModelUpdatedDate = FieldSupport.EXTRACTION.fieldValue("updated", Instant.class, updatedModel);
        assertThat(updatedModelCreatedDate).isEqualTo(createdModelCreatedDate);
        assertThat(updatedModelUpdatedDate).isGreaterThan(updatedModelCreatedDate);

        // This works. The @CreatedDate does not get set to null on update.
    }

    private ObjectId getObjectId(Link link) {
        String id = link.getHref().substring(link.getHref().lastIndexOf('/') + 1);
        return new ObjectId(id);
    }
}
