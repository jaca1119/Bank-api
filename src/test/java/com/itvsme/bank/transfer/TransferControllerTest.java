package com.itvsme.bank.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itvsme.bank.TestUtils;
import com.itvsme.bank.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.sql.Timestamp;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransferController.class)
public class TransferControllerTest
{
    @Autowired
    MockMvc mockMvc;
    @MockBean
    TransferService transferService;
    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @Test
    void testTransferFromTo() throws Exception
    {
        Cookie jwtAuthCookie = TestUtils.createAuthenticationJwtCookie();

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFrom("fromKey");
        transferDTO.setTo("toKey");
        transferDTO.setAmountInHundredScale(100 * 100);
        transferDTO.setTransferDateTime(Timestamp.from(Instant.now()));
        transferDTO.setZone("UTC");

        ObjectMapper objectMapper = new ObjectMapper();
        String transferJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(transferDTO);

        mockMvc.perform(post("/transfer")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF8")
            .content(transferJson)
            .cookie(jwtAuthCookie)
            .header("Origin", "https://affectionate-carson-6417c5.netlify.app")
            )
                .andExpect(status().isOk())
                .andDo(print());

    }
}
