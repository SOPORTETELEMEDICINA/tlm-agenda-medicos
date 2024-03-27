package net.amentum.niomedic.medicos;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.amentum.niomedic.medicos.views.DomicilioView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {MedicosApplication.class})
public class DomicilioTest {
   private final MediaType jsonType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf-8"));
   public MockMvc mockMvc;
   @Autowired
   private WebApplicationContext webApplicationContext;
   @Autowired
   private ObjectMapper objectMapper;

   @Before
   public void setup() {
      mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
   }

   @Test
   public void createDomicilio() throws Exception {

      String idMedico = "66ab932d-c362-482b-af28-0eecc1fd6a6e";

      DomicilioView domicilioView = new DomicilioView();

      domicilioView.setDomicilio("DP-domicilio");
      domicilioView.setLocalidad("DP-colonia");
      domicilioView.setMunicipio("DP-colonia");
      domicilioView.setEstado("DP-estado");
      domicilioView.setPais("DP-pais");
      domicilioView.setCp("DP-codigo postal");
      domicilioView.setTelefonoFijo("DP-telefono-fijo");
      domicilioView.setRegistroSanitario("DP-registro-sanitario");
      domicilioView.setFechaCreacion(new Date());
      domicilioView.setActivo(Boolean.TRUE);
      domicilioView.setTipo("DP-tipo");

      ArrayList<DomicilioView> domicilioViewArrayList = new ArrayList<>();

      domicilioViewArrayList.add(domicilioView);

      System.out.println(objectMapper.writeValueAsString(domicilioViewArrayList));

//      mockMvc.perform(MockMvcRequestBuilders.post("/medicos/" + idMedico + "/domicilio")
//         .contentType(jsonType)
//         .content(objectMapper.writeValueAsString(domicilioViewArrayList)))
//         .andExpect(MockMvcResultMatchers.status().isCreated())
//         .andDo(MockMvcResultHandlers.print());

   }

   @Test
   public void updateDomicilio() throws Exception {

      String idMedico = "c47599d8-71e5-49d0-a287-4b2449c166bd";
      String idDomicilio = "f1862866-09e3-493e-8eb5-7701964fbe2b";

      DomicilioView domicilioView = new DomicilioView();
      domicilioView.setIdDomicilio(idDomicilio);
      domicilioView.setDomicilio("DP-domicilio-U");
      domicilioView.setLocalidad("DP-colonia-U");
      domicilioView.setMunicipio("DP-colonia-U");
      domicilioView.setEstado("DP-estado-U");
      domicilioView.setPais("DP-pais-U");
      domicilioView.setCp("DP-codigo postal-U");
      domicilioView.setTelefonoFijo("DP-telefono-fijo-U");
      domicilioView.setRegistroSanitario("DP-registro-sanitario-U");
      domicilioView.setFechaCreacion(new Date());
      domicilioView.setActivo(Boolean.TRUE);
      domicilioView.setTipo("DP-tipo-U");

      ArrayList<DomicilioView> domicilioViewArrayList = new ArrayList<>();

      domicilioViewArrayList.add(domicilioView);

      System.out.println(objectMapper.writeValueAsString(domicilioView));

      mockMvc.perform(MockMvcRequestBuilders.put("/medicos/" + idMedico + "/domicilio")
         .contentType(jsonType)
         .content(objectMapper.writeValueAsString(domicilioViewArrayList)))
         .andExpect(MockMvcResultMatchers.status().isOk())
         .andDo(MockMvcResultHandlers.print());

   }

   @Test
   public void deleteDomicilio() throws Exception {

      String idMedico = "f0fc99c7-4c93-476d-802f-7dd74eb2bbcc";
      String idDomicilio = "edf249ec-8af3-4dde-92dd-33619c2d62c7";

      ArrayList<DomicilioView> domicilioViewArrayList = new ArrayList<>();

      DomicilioView domicilioView = new DomicilioView();
      domicilioView.setIdDomicilio(idDomicilio);

      domicilioViewArrayList.add(domicilioView);

      mockMvc.perform(MockMvcRequestBuilders.delete("/medicos/" + idMedico + "/domicilio")
         .contentType(jsonType)
         .content(objectMapper.writeValueAsString(domicilioViewArrayList)))
         .andExpect(MockMvcResultMatchers.status().isOk())
         .andDo(MockMvcResultHandlers.print());

   }


}
