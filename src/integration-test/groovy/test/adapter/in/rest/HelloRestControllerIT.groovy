package test.adapter.in.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import test.IntegrationTest

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class HelloRestControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc

    def "should return hello world response"() {

        expect:
        mockMvc.perform(get("/api/v1/hello"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"message\": \"Hello World!\"}"))

    }

}
