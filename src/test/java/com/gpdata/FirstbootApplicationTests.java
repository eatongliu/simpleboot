package com.gpdata;

import com.gpdata.other.controller.OtherController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FirstbootApplicationTests {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private MockMvc mvc;
	@Before
	public void setUp() throws Exception {
		mvc = MockMvcBuilders.standaloneSetup(new OtherController()).build();
	}

	@Test
	public void contextLoads() {
        try {
            MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/remotecall").accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk()).andReturn();
            System.out.println(result.getResponse().getContentAsString());
            logger.debug("result: {}",result.getResponse().getContentAsString());
        } catch (Exception e) {
            logger.error("Exception: {}",e);
        }
    }

}
