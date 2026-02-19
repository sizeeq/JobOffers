package pl.joboffers.controller.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import pl.joboffers.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DuplicateOfferUrlExceptionIntegrationTest extends BaseIntegrationTest {

    @Test
    public void should_return_conflict_status_when_user_posts_offer_with_duplicated_offer_url() throws Exception {
        //step 1:
        //given
        //when
        ResultActions performPostOffer = mockMvc.perform(post("/offers")
                .content("""
                        {
                        "company": "someCompany",
                        "position": "somePosition",
                        "salary": "7000 PLN",
                        "offerUrl": "https://newoffers.pl/offer/12345"
                        }
                        """.trim()
                ).contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
        );

        //then
        performPostOffer.andExpect(status().isCreated());


        //step 2:
        //given
        //when
        ResultActions performDuplicatePostOfferUrl = mockMvc.perform(post("/offers")
                .content("""
                        {
                        "company": "someCompany",
                        "position": "somePosition",
                        "salary": "7000 PLN",
                        "offerUrl": "https://newoffers.pl/offer/12345"
                        }
                        """.trim()
                ).contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8")
        );

        //then
        performDuplicatePostOfferUrl.andExpect(status().isConflict());
    }
}
