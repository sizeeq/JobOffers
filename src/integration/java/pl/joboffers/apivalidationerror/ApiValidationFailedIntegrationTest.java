package pl.joboffers.apivalidationerror;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import pl.joboffers.BaseIntegrationTest;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApiValidationFailedIntegrationTest extends BaseIntegrationTest {

    @Test
    public void should_return_bad_request_and_validation_messages_when_client_sends_empty_request_input_data() throws Exception {
        //given
        String postOfferContent = """
                {
                "company": "",
                "position": "",
                "salary": "",
                "offerUrl": ""
                }
                """.trim();

        //when && then
        mockMvc.perform(post("/offers")
                        .content(postOfferContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessages").value(containsInAnyOrder(
                        "offerUrl must not be empty",
                        "position must not be empty",
                        "company must not be empty",
                        "salary must not be empty",
                        "position size must be between 1 and 100",
                        "company size must be between 1 and 50"
                )))
                .andExpect(jsonPath("$.errorMessages.size()").value(6));
    }

    @Test
    public void should_return_bad_request_and_validation_messages_when_client_sends_no_request_input_data() throws Exception {
        //given
        String postOfferContent = """
                {
                }
                """.trim();

        //when && then
        mockMvc.perform(post("/offers")
                        .content(postOfferContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessages").value(containsInAnyOrder(
                        "position must not be null",
                        "company must not be empty",
                        "salary must not be null",
                        "company must not be null",
                        "offerUrl must not be empty",
                        "position must not be empty",
                        "salary must not be empty",
                        "offerUrl must not be null"
                )))
                .andExpect(jsonPath("$.errorMessages.size()").value(8));
    }
}
