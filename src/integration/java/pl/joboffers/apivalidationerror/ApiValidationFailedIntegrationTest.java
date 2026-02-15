package pl.joboffers.apivalidationerror;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pl.joboffers.BaseIntegrationTest;
import pl.joboffers.JobOffers.infrastructure.apivalidation.ApiValidationErrorDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiValidationFailedIntegrationTest extends BaseIntegrationTest {

    @Test
    public void should_return_bad_request_and_validation_messages_when_client_sends_empty_request_input_data() throws Exception {
        //given
        //when
        ResultActions performPostEmptyData = mockMvc.perform(post("/offers")
                .content("""
                        {
                        "company": "",
                        "position": "",
                        "salary": "",
                        "offerUrl": ""
                        }
                        """.trim()
                ).contentType(MediaType.APPLICATION_JSON)
        );

        //then
        MvcResult performPostEmptyDataResult = performPostEmptyData.andExpect(status().isBadRequest()).andReturn();
        String jsonPerformPostEmptyData = performPostEmptyDataResult.getResponse().getContentAsString();
        ApiValidationErrorDto apiValidationErrorDto = objectMapper.readValue(jsonPerformPostEmptyData, ApiValidationErrorDto.class);
        assertThat(apiValidationErrorDto.errorMessages()).containsExactlyInAnyOrder(
                "offerUrl must not be empty",
                "position must not be empty",
                "company must not be empty",
                "salary must not be empty",
                "position size must be between 1 and 100",
                "company size must be between 1 and 50");
    }

    @Test
    public void should_return_bad_request_and_validation_messages_when_client_sends_no_request_input_data() throws Exception {
        //given
        //when
        ResultActions performPostNoData = mockMvc.perform(post("/offers")
                .content("""
                        {
                        }
                        """.trim()
                ).contentType(MediaType.APPLICATION_JSON)
        );

        //then
        MvcResult performPostNoDataResult = performPostNoData.andExpect(status().isBadRequest()).andReturn();
        String jsonPerformPostNoData = performPostNoDataResult.getResponse().getContentAsString();
        ApiValidationErrorDto apiValidationErrorDto = objectMapper.readValue(jsonPerformPostNoData, ApiValidationErrorDto.class);
        assertThat(apiValidationErrorDto.errorMessages()).containsExactlyInAnyOrder(
                "position must not be null",
                "company must not be empty",
                "salary must not be null",
                "company must not be null",
                "offerUrl must not be empty",
                "position must not be empty",
                "salary must not be empty",
                "offerUrl must not be null");

    }
}
