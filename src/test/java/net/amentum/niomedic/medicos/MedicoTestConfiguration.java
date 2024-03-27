package net.amentum.niomedic.medicos;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc(secure=false)
@TestPropertySource(locations = {"classpath:application-test.properties"})
@SpringBootTest(classes = {MedicosApplication.class})
@ActiveProfiles("test")
public class MedicoTestConfiguration {
	@Autowired
	protected MockMvc mockMvc;

	protected final ObjectMapper MAPPER;

	protected final MediaType JSON = MediaType.APPLICATION_JSON;

	public MedicoTestConfiguration(){
		MAPPER = new ObjectMapper();
	}
}
